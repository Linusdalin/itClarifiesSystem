package analysis;

import actions.Action;
import actions.ActionStatus;
import actions.ActionTable;
import analysis2.AnalysisException;
import analysis2.NewAnalysisOutcome;
import classification.FragmentClassification;
import classification.FragmentClassificationTable;
import classifiers.Classification;
import contractManagement.*;
import crossReference.*;
import dataRepresentation.DBTimeStamp;
import databaseLayer.DBKeyInterface;
import databaseLayer.DatabaseAbstractionFactory;
import document.*;
import featureTypes.FeatureTypeTree;
import fileHandling.BlobRepository;
import fileHandling.RepositoryFileHandler;
import fileHandling.RepositoryInterface;
import language.LanguageCode;
import log.PukkaLogger;
import net.sf.json.JSONObject;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;
import risk.ContractRisk;
import risk.ContractRiskTable;
import risk.RiskClassification;
import risk.RiskClassificationTable;
import search.Keyword;
import search.KeywordTable;
import search.SearchManager2;
import services.DocumentService;
import services.Formatter;
import system.Analyser;
import userManagement.Organization;
import userManagement.PortalUser;
import versioning.Transposer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/********************************************************
 *
 *          Perform the analysis
 *
 *          This is a web hook for the Analysis Queue
 *
 * //TODO: The check:        if(classification.getType().getName().equals(FeatureTypeTree.Reference.getName())){ should be optimized

 */

public class AnalysisServlet extends DocumentService {

    public static final String DataServletName = "Analysis";
    private static final int RELEVANCE_THRESHOLD = 40;          // What is the relevance needed to actually create an entry in the database


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Get not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();


     }


    /***************************************************************************'
     *
     *              Post a request.
     *
     *              Parameters:
     *
     *               - version : The key to the contract version instance
     *               - oldVerson : Optional old version if we are reuploading a document
     *
     *
     *
     * @param req
     * @param resp
     * @throws IOException
     */


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {


        try{
            logRequest(req);
            ContractVersionInstance oldVersion = null;

            if(!validateSession(req, resp, HttpServletResponse.SC_OK))    // Send OK here. A 403 would trigger a retry in the event queue
                return;

            DBKeyInterface _version = getMandatoryKey("version", req);
            ContractVersionInstance versionInstance = new ContractVersionInstance(new LookupByKey(_version));

            if(!mandatoryObjectExists(versionInstance, resp))
                return;

            Contract document = versionInstance.getDocument();

            if(!document.exists()){

                returnError("Document does not exist", ErrorType.DATA, HttpServletResponse.SC_OK, resp );
                return;
            }

            Project project = document.getProject();

            if(!mandatoryObjectExists(document, resp))
                return;

            // Optional old version for transposing

            DBKeyInterface _oldVersion = getOptionalKey("oldVersion", req);
            if(_oldVersion != null){

                oldVersion = new ContractVersionInstance(new LookupByKey(_oldVersion));

                if(!mandatoryObjectExists(oldVersion, resp))
                    return;

                if(!mandatoryObjectExists(oldVersion.getDocument(), resp))
                    return;
            }


            Formatter formatter = getFormatFromParameters(req);

            PukkaLogger.log(PukkaLogger.Level.INFO, "Executing queued task - analysing " + versionInstance.getVersion());

            RepositoryInterface repository = new BlobRepository();
            RepositoryFileHandler fileHandler = new RepositoryFileHandler(document.getFile());

            // Check for the file on the server. It should have been uploaded through the FileUploadServlet.

            if(!repository.existsFile(fileHandler))
                returnError("File " + versionInstance.getVersion() + " does not exist as a file on the server", HttpServletResponse.SC_OK, resp);


            // Now parse the file

            try{

                parseFile(document, versionInstance);

            } catch (BackOfficeException e) {

                PukkaLogger.swallow( e );

                document.setMessage("Failed to parse document: " + e.narration);
                document.setStatus(ContractStatus.getFailed());
                document.update();

                invalidateDocumentCache(document, project);

                returnError(e.narration, HttpServletResponse.SC_OK, resp);
                return;
            }

            // Update the status of the document

            document.setMessage("Completed parsing. Analysing");
            document.setStatus(ContractStatus.getAnalysing());
            document.update();

            invalidateDocumentCache(document, project);

            PukkaLogger.log(PukkaLogger.Level.ACTION, "*****************************\nPhase III: The analysis");


            // Perform the analysis and the transposing
            analyse(versionInstance, oldVersion);

            invalidateDocumentCache(document, project);
            invalidateFragmentCache(versionInstance);

            // Update the status of the document

            document.setMessage("Completed analyzing document.");
            document.setStatus(ContractStatus.getAnalysed());
            document.update();


            // If anyone is interested in the result
            JSONObject json = new JSONObject().put("Analysis", "COMPLETE");
            sendJSONResponse(json, formatter, resp);

        } catch (BackOfficeException e) {

            // Send OK here. An error code would trigger a retry

            PukkaLogger.log( e );
            returnError(e.narration, HttpServletResponse.SC_OK, resp);

        } catch (Exception e) {

            // Send OK here. An error code would trigger a retry

            PukkaLogger.log( e );
            returnError("Internal Error analyzing document", HttpServletResponse.SC_OK, resp);

        }

    }



    /*********************************************************************************
     *
     *      Perform the analysis
     *
     *      This involves the following steps:
     *
     *          - Perform a main pass on the analysis
     *          - Perform the post processing of the analysis (looking for definition usage)
     *
     *          - Create search completion keywords
     *          - Transpose all the annotations, references etc from the old document if needed
     *
     *          - Perform a complete reanalysis of the project to find references to this new document
     *          - Make a pass over the references to see if it is possible to close open references
     *
     *
     *
     * @param newVersion - the new version
     * @param oldVersion - old optional version
     * @throws BackOfficeException
     *
     *          //TODO: Optimization: Definitions are retreived twice.
     *
     *          //TODO: Refactor: analyse and reanalyse are to similar NOT to refactor to one
     *
     */


    public void analyse(ContractVersionInstance newVersion, ContractVersionInstance oldVersion) throws BackOfficeException {

        List<OutcomeMap> outcomeList = new ArrayList<OutcomeMap>();
        DBTimeStamp analysisTime = new DBTimeStamp();
        Contract document = newVersion.getDocument();
        Project project = document.getProject();
        Organization organization = project.getOrganization();
        AbstractProject aProject = project.createAbstractProject();
        AbstractDocument aDocument = newVersion.createAbstractDocumentVersion(aProject);
        List<Definition> definitions = project.getDefinitionsForProject();
        LanguageCode documentLanguage = new LanguageCode(document.getLanguage());
        List<ContractFragment> fragments = newVersion.getFragmentsForVersion();
        PortalUser owner = document.getOwner();
        SearchManager2 searchManager = new SearchManager2(project, owner);

        document.setStatus(ContractStatus.getAnalysing());  // Setting the status for the document
        document.update();

        aProject.addDocument(aDocument);

        // Create an analyser and detect basic principles for the document.

        try {

            Analyser analyser = new Analyser(documentLanguage, MODEL_DIRECTORY);

            if(oldVersion != null){

                PukkaLogger.log(PukkaLogger.Level.INFO, "Deleting old keywords and attributes");

                // Remove existing values in the old version

                deleteKeywords(oldVersion);
                deleteAttributes(oldVersion);

            }


            // Now make a new pass over all the fragments to analyse them. At this point all fragments should have a key

            analyseFragments(fragments, analyser, newVersion, aDocument, project, document, owner, aProject, analysisTime, outcomeList, definitions);

            // Make a second pass over all the fragments
            // This is to handle definition references

            postProcessAnalysis(analyser, outcomeList, aDocument, owner, newVersion, project, aProject, analysisTime, definitions);


            // Retrieve and store all keywords

            storeKeywords(newVersion, analyser, document, project, organization);


            // If there is an old version (update rather than upload new) we need to transpose all old attributes

            if(oldVersion != null){

                // Clone all previous annotations, reference, etc.

                PukkaLogger.log(PukkaLogger.Level.INFO, "Transposing");
                Transposer transposer = new Transposer();
                transposer.clone(oldVersion, newVersion);

            }

            // Now reanalyse project to find existing references to the new document

            reanalyseProjectForReferences(project, newVersion);

            // Finally a last pass over the project to close any open references

            closeReferences(analyser, project);


            // Index all fragments in the search engine

            PukkaLogger.log(PukkaLogger.Level.INFO, "Indexing" + fragments.size() + " fragments for the analysis of the document " + document);
            searchManager.indexFragments(fragments, newVersion, document);



        } catch (AnalysisException e) {

            PukkaLogger.log( e );

        } catch (Exception e) {

            PukkaLogger.log( e );

        }

    }

    /********************************************************************************************
     *
     *          Re-analyse works on an existing document and is only invoked from the back-office
     *
     *          The main purpose is to remove all attributes from previous analysis and redo it.
     *          A typical case is when the analysis is updated.
     *
     *
     * @param documentVersion - document ot analyse
     * @throws BackOfficeException
     */



    public void reAnalyse(ContractVersionInstance documentVersion) throws BackOfficeException {

        List<OutcomeMap> outcomeList = new ArrayList<OutcomeMap>();
        DBTimeStamp analysisTime = new DBTimeStamp();
        Contract document = documentVersion.getDocument();
        Project project = document.getProject();
        Organization organization = project.getOrganization();
        AbstractProject aProject = project.createAbstractProject();
        AbstractDocument aDocument = documentVersion.createAbstractDocumentVersion(aProject);
        List<Definition> definitions = project.getDefinitionsForProject();
        LanguageCode documentLanguage = new LanguageCode(document.getLanguage());
        List<ContractFragment> fragments = documentVersion.getFragmentsForVersion();
        PortalUser owner = document.getOwner();
        SearchManager2 searchManager = new SearchManager2(project, owner);

        document.setStatus(ContractStatus.getAnalysing());  // Setting the status for the document
        document.update();


        // Create an analyser and detect basic principles for the document.

        try {

            Analyser analyser = new Analyser(documentLanguage, MODEL_DIRECTORY);


            PukkaLogger.log(PukkaLogger.Level.INFO, "Deleting old keywords and attributes");

            // Remove existing auto generated values in the old version

            deleteKeywords(documentVersion);
            deleteAttributes(documentVersion);


            // Now make a new pass over all the fragments to analyse them. At this point all fragments should have a key

            analyseFragments(fragments, analyser, documentVersion, aDocument, project, document, owner, aProject, analysisTime, outcomeList, definitions);


            // Make a second pass over all the fragments
            // This is to handle definition references

            postProcessAnalysis(analyser, outcomeList, aDocument, owner, documentVersion, project, aProject, analysisTime, definitions);


            // Retrieve and store all keywords

            storeKeywords(documentVersion, analyser, document, project, organization);


            // Now reanalyse project to find existing references to the new document

            reanalyseProjectForReferences(project, documentVersion);

            // Finally a last pass over the project to close any open references

            closeReferences(analyser, project);

            // Index all fragments in the search engine

            PukkaLogger.log(PukkaLogger.Level.INFO, "Indexing" + fragments.size() + " fragments for the analysis of the document " + document);
            searchManager.indexFragments(fragments, documentVersion, document);


        } catch (AnalysisException e) {

            PukkaLogger.log(PukkaLogger.Level.FATAL, "Error in document analysis");

        }

    }

    private void analyseFragments(List<ContractFragment> fragments,
                                  Analyser analyser,
                                  ContractVersionInstance documentVersion,
                                  AbstractDocument aDocument,
                                  Project project,
                                  Contract document,
                                  PortalUser owner,
                                  AbstractProject aProject,
                                  DBTimeStamp analysisTime,
                                  List<OutcomeMap> outcomeList,
                                  List<Definition> definitionsForProject) throws BackOfficeException {

        StringBuffer errorMessages = new StringBuffer();
        SearchManager2 searchManager = new SearchManager2(project, owner);

        for (AbstractDocument abstractDocument : aProject.documents) {
            System.out.println(" *** Document " + abstractDocument.name + " has " + abstractDocument.definitions.size() + " definitions for the analysis.");

            for (String definition : abstractDocument.definitions) {

                System.out.print( definition + ", ");

            }
            System.out.println(" *** ");

        }

        int risks = 0;
        NewAnalysisOutcome analysisOutcome;

        for(ContractFragment fragment : fragments){

            try{

                StructureItem item = fragment.getStructureItem();
                CellInfo cellInfo = fragment.getCellInfo();
                String headline = "";
                if(item.exists())
                    headline = item.getName();

                System.out.println("**************************************************************");
                System.out.println("* Analysing fragment " + fragment.getName() + "("+ fragment.getKey().toString()+")");
                System.out.println("*  -with headline: " + headline);

                // Old deprecated call. Just kept for the transition

                //AnalysisOutcome analysisOutcome= analyser.analyseFragment(fragment.getText(), headline,
                //        aDocument, aProject);

                String contextText = "";  // TODO: Context text not implemented,
                                          // this is additional text from surrounding parts of the document,
                                          // relevant for the analysis e.g. Lists

                analysisOutcome = analyser.analyseFragment(fragment.getText(), headline, contextText, aDocument, cellInfo, aProject);

                risks += handleResult(analysisOutcome, fragment, project, analysisTime, searchManager, aDocument, definitionsForProject, documentVersion);

                // Store it for the second pass. In that pass we dont want to redo the parsing and analysis

                outcomeList.add(new OutcomeMap(analysisOutcome, fragment));

            }catch(BackOfficeException e){

                String message = "Error analysing fragment " + fragment.getText() + PukkaLogger.getMessage(e);
                errorMessages.append( message );
                PukkaLogger.log( PukkaLogger.Level.WARNING, message );

            }catch(NullPointerException e){

                e.printStackTrace();
                String message = "Internal Error analysing fragment " + fragment.getText() + PukkaLogger.getMessage(e);
                errorMessages.append(message);
                PukkaLogger.log( PukkaLogger.Level.WARNING, message );
            }
        }

        // Create an action to review the potential risks.
        // This may have to be updated when re-uploading documents is implemented and we should only
        // issue an action if there are new unseen risk

        if(risks > 0){

            String actionTitle = "Assess " + risks +" potential risk"+(risks > 1 ? "s" : "")+"  in " + document.getName();
             String actionDescription = "Assess " + risks +" potential risk"+(risks > 1 ? "s" : "")+"  found in the analysis of " + document.getName();

             PortalUser system = PortalUser.getSystemUser();
             ContractFragment fragment = documentVersion.getFirstFragment();
                                     // Also create an implicit action for the risk. All risks should be mitigated or at least acknowledged.


             Action handleRiskAction = new Action(
                     (long)0, // Not supported action id yet. This is a placeholder
                     actionTitle,
                     actionDescription,
                     "",
                     fragment.getKey(),
                     fragment.getVersionId(),
                     fragment.getKey(),             //Target not set
                     project.getKey(),
                     system.getKey(),
                     PortalUser.getNoUser().getKey(),
                     -1,
                     ActionStatus.getOpen(),
                     analysisTime.getISODate(),
                     new DBTimeStamp(DBTimeStamp.NO_DATE, "1900-00-00").getISODate(),
                     new DBTimeStamp(DBTimeStamp.NO_DATE, "1900-00-00").getISODate());

             handleRiskAction.store();

        }

        document.setStatus(ContractStatus.getAnalysed());  // Setting the status for the document
        document.update();

    }


    /***********************************************************************
     *
     *      Go through the entire project and see if there are references to close. This includes:
     *
     *       - Open references
     *       - Definitions
     *
     * @param analyser
     * @param project
     */

    private void closeReferences(Analyser analyser, Project project) throws BackOfficeException{

        AbstractProject aProject = project.createAbstractProject();

        List<Reference> openReferencesForProject = project.getOpenReferences();

        System.out.println("Found " + openReferencesForProject.size() + " open references to analyse and close");

        for(Reference reference : openReferencesForProject){

            NewAnalysisOutcome outcome2 = analyser.analyseOpenReference2(reference.getName(), aProject);

            System.out.println("***** New close delivered " + outcome2.getClassifications().size() + " classifications.");

            for(Classification classification : outcome2.getClassifications()){


                if(classification.getType().getName().equals(FeatureTypeTree.Reference.getName())){

                    // This is a new reference target. The key (which comes from the AbstractStructureItem)
                    // is extracted as semantic extraction

                    DBKeyInterface clauseId = new DatabaseAbstractionFactory().createKey(classification.getExtraction().getSemanticExtraction());

                    PukkaLogger.log(PukkaLogger.Level.INFO, "Closing an open reference(2) " + reference.getName() + ". Found matching clause " + clauseId.toString() );

                    reference.setTo(clauseId);
                    reference.setType(ReferenceType.getExplicit());
                    reference.update();

                    break;
                }



                PukkaLogger.log(PukkaLogger.Level.FATAL, "Action " + classification.getType().getName() + " not supported after analyseOpenReferences()");

            }

        }

    }



    /******************************************************************************************
     *
     *      Go through the entire project to find existing undetected references to the new document
     *
     * @param project
     * @param versionInstance
     */

    public void reanalyseProjectForReferences(Project project, ContractVersionInstance versionInstance) throws BackOfficeException {


        List<Contract> contractsForProject = project.getContractsForProject();
        Contract currentDocument = versionInstance.getDocument();
        String name = currentDocument.getName().toLowerCase();
        String file = currentDocument.getFile().toLowerCase();
        ReferenceType type = ReferenceType.getExplicit();

        ContractFragment firstFragment = new ContractFragment(new LookupItem()
                .addFilter(new ColumnFilter(ContractFragmentTable.Columns.Ordinal.name(), 0))
                .addFilter(new ReferenceFilter(ContractClauseTable.Columns.Version.name(), versionInstance.getKey())));

        if(!firstFragment.exists()){

            List<ContractFragment> fragmentList = versionInstance.getFragmentsForVersion();

            System.out.println("These are the fragments:");
            for(ContractFragment fragment : fragmentList){

                System.out.println("Fragment: " + fragment.getName() + " " + fragment.getOrdinal());
            }

            PukkaLogger.log(PukkaLogger.Level.FATAL, "The document " + currentDocument.getName() + " does not have a first fragment to direct references to");
            return;
        }


        for(Contract contract : contractsForProject){


            //For all other documents we look for references to the title and document name
            if(!contract.equals(currentDocument)){

                ContractVersionInstance latestVersion = contract.getHeadVersion();
                List<ContractFragment> fragmentsForDocument = latestVersion.getFragmentsForVersion();

                for(ContractFragment fragment : fragmentsForDocument){

                    //Now check for occurrences of the name or the file

                    if(fragment.getText().toLowerCase().contains(name) ||
                            fragment.getText().toLowerCase().contains(file)){

                        // Check if there already is a reference

                        List<Reference> existingReferences =  fragment.getReferencesForFragment();
                        boolean foundExistingReference = false;

                        for(Reference reference : existingReferences){

                            if(reference.getTo().equals(firstFragment))
                                foundExistingReference = true;

                            if(reference.getType().equals(ReferenceType.getOpen()) && reference.getName().toLowerCase().contains(name))
                                foundExistingReference = true;

                        }

                        if(!foundExistingReference){

                            // Create a new reference and store it in the fragment
                            // It will point to the first clause in the document

                            Reference reference = new Reference(name, fragment, firstFragment,  versionInstance, project, type, name, 0);
                            reference.store();
                        }

                    }

                }

            }
        }

    }


    private void postProcessAnalysis(Analyser analyser, List<OutcomeMap> outcomeList, AbstractDocument aDocument, PortalUser owner,
                                     ContractVersionInstance version, Project project, AbstractProject aProject, DBTimeStamp analysisTime,
                                     List<Definition> definitionsForProject) throws BackOfficeException{

        PukkaLogger.log(PukkaLogger.Level.INFO, "Second pass with " + outcomeList.size() + " elements.");
        SearchManager2 searchManager = new SearchManager2(project, owner);


        for(OutcomeMap outcome : outcomeList){

            PukkaLogger.log(PukkaLogger.Level.INFO, "Post processing " + outcome.fragment.getText());

            NewAnalysisOutcome postProcessOutcome = analyser.postProcess(outcome.outcome, aProject);

            //handlePostProcessResult(postProcessOutcome, outcome.fragment, project, definitionsForProject);
            handleResult(postProcessOutcome, outcome.fragment, project, analysisTime, searchManager, aDocument, definitionsForProject, version);

        }

    }

    /**************************************************************
     *
     *          Store the extracted keywords in the database
     *
     *
     *
     * @param versionInstance  - the analysed version
     * @param analyser         - list of keywords from the analysis
     * @param organization
     * @throws BackOfficeException
     *
     *          //TODO: This adds #Risk as a keyword. Should be language specific
     *
     */


    private void storeKeywords(ContractVersionInstance versionInstance, Analyser analyser, Contract document, Project project, Organization organization) throws BackOfficeException {

        KeywordTable table = new KeywordTable();
        table.createEmpty();
        List<String> keywords = analyser.getKeywords();

        for(String word : keywords){

            Keyword keyword = new Keyword(word, versionInstance, document, project);
            table.add(keyword);
        }

        // Add risks

        List<ContractRisk> risks = new ContractRiskTable().getAll();

        for(ContractRisk risk : risks){

            Keyword keyword = new Keyword("#" + risk.getName(), versionInstance, document, project);
            table.add(keyword);
        }


        // TODO: Move these to create project


        Keyword keyword = new Keyword("#Risk", versionInstance, document, project);
        table.add(keyword);

        keyword = new Keyword("#Annotation", versionInstance, document, project);
        table.add(keyword);

        keyword = new Keyword("#Classification", versionInstance, document, project);
        table.add(keyword);


        table.store();  // Store back all

    }

    private void deleteKeywords(ContractVersionInstance versionInstance) throws BackOfficeException {

        // First remove all old keywords for the document

        KeywordTable oldKeywords = new KeywordTable(new LookupList().addFilter(new ReferenceFilter(KeywordTable.Columns.Document.name(), versionInstance.getDocumentId())));
        oldKeywords.delete();


    }

    /************************************************************************************************
     *
     *      When re-uploading a document or rerunning the analysis, the original
     *      attributes should be removed to avoid duplicats.
     *
     * @param versionInstance
     * @throws BackOfficeException
     */

    private void deleteAttributes(ContractVersionInstance versionInstance) throws BackOfficeException {

        // First remove all imported annotations

        /*

            Removed this. It should be part of reupload, not reanalyze which is done on existing documents

        ContractAnnotationTable importedAnnotations = new ContractAnnotationTable(new LookupList()
                .addFilter(new ReferenceFilter(ContractAnnotationTable.Columns.Version.name(), versionInstance.getKey()))
                .addFilter(new ReferenceFilter(ContractAnnotationTable.Columns.Creator.name(), PortalUser.getExternalUser().getKey()))

        );

        PukkaLogger.log(PukkaLogger.Level.INFO, "**** Removed " + importedAnnotations.getCount() + " imported annotations");

        importedAnnotations.delete();

         */

        // Also remove internally generated annotations

        ContractAnnotationTable internalAnnotations = new ContractAnnotationTable(new LookupList()
                .addFilter(new ReferenceFilter(ContractAnnotationTable.Columns.Version.name(), versionInstance.getKey()))
                .addFilter(new ReferenceFilter(ContractAnnotationTable.Columns.Creator.name(), PortalUser.getSystemUser().getKey()))

        );

        PukkaLogger.log(PukkaLogger.Level.INFO, "**** Removed " + internalAnnotations.getCount() + " internal annotations");

        internalAnnotations.delete();



        // Remove all old classifications that are created by the system

        FragmentClassificationTable oldClassifications = new FragmentClassificationTable(new LookupList()
                .addFilter(new ReferenceFilter(FragmentClassificationTable.Columns.Version.name(), versionInstance.getKey()))
                .addFilter(new ReferenceFilter(FragmentClassificationTable.Columns.Creator.name(), PortalUser.getSystemUser().getKey()))

        );

        PukkaLogger.log(PukkaLogger.Level.INFO, "**** Removed " + oldClassifications.getCount() + " Classifications");

        oldClassifications.delete();



        // Remove all old actions that are created by the system

        ActionTable oldActions = new ActionTable(new LookupList()
                .addFilter(new ReferenceFilter(ActionTable.Columns.Version.name(), versionInstance.getKey()))
                .addFilter(new ReferenceFilter(ActionTable.Columns.Issuer.name(), PortalUser.getSystemUser().getKey()))
                .addFilter(new ReferenceFilter(ActionTable.Columns.Assignee.name(), PortalUser.getNoUser().getKey()))

        );

        PukkaLogger.log(PukkaLogger.Level.INFO, "**** Removed " + oldActions.getCount() + " Actions");

        oldActions.delete();



        // Now remove all risks
        // TODO: Check how this works with set classifications and "latest"

        RiskClassificationTable oldRiskClassifications = new RiskClassificationTable(new LookupList()
                .addFilter(new ReferenceFilter(RiskClassificationTable.Columns.Version.name(), versionInstance.getKey()))
                .addFilter(new ReferenceFilter(RiskClassificationTable.Columns.Creator.name(), PortalUser.getSystemUser().getKey()))

        );

        PukkaLogger.log(PukkaLogger.Level.INFO, "**** Removed " + oldRiskClassifications.getCount() + " Risk Classifications");

        oldRiskClassifications.delete();

        // Definitions

        DefinitionTable oldDefinitions = new DefinitionTable(new LookupList()
                .addFilter(new ReferenceFilter(DefinitionTable.Columns.Version.name(), versionInstance.getKey()))

        );

        PukkaLogger.log(PukkaLogger.Level.INFO, "**** Removed " + oldDefinitions.getCount() + " Definitions");

        oldDefinitions.delete();


        ReferenceTable oldReferences = new ReferenceTable(new LookupList()
                .addFilter(new ReferenceFilter(ReferenceTable.Columns.Version.name(), versionInstance.getKey()))

        );

        PukkaLogger.log(PukkaLogger.Level.INFO, "**** Removed " + oldReferences.getCount() + " References");

        oldReferences.delete();


    }


    /***************************************************************************
     *
     *      Handle the result from the analysis
     *
     * @param analysisResult - the feature definitions from the analysis
     * @param fragment - the data base fragment to update with classifications and references from analysis
     * @param project
     * @param analysisTime - time for the analysis
     * @param version
     *
     */


    private int handleResult(NewAnalysisOutcome analysisResult, ContractFragment fragment,
                             Project project, DBTimeStamp analysisTime,
                             SearchManager2 searchManager,
                             AbstractDocument aDocument, List<Definition> definitionsForProject,
                             ContractVersionInstance version) throws BackOfficeException{

        boolean updated = false;

        // Counters for updating the fragment

        int classifications = 0;
        int references = 0;
        int annotations = 0;
        int risks = 0;


        FragmentClassification fragmentClassification;

        ContractRisk defaultRisk = ContractRisk.getUnknown();
        PortalUser system = PortalUser.getSystemUser();

        System.out.println("Found " + analysisResult.getClassifications().size() + " classifications in analysis");

        //TODO: Optimization; this batch store is done once per fragment result. Could be done once and for all for the analysis- (Pass this around)

        FragmentClassificationTable classificationsToStore = new FragmentClassificationTable();
        classificationsToStore.createEmpty();


        for(Classification classification : analysisResult.getClassifications()){

            try{

                /**************************************
                        Reference

                 */


                if(classification.getType().getName().equals(FeatureTypeTree.Reference.getName())){

                    // The analysis has classified it as a reference. We create the reference here as open.
                    // In the second phase we go through all the open references and try to close them.
                    // We use the semantic extraction that will be the


                    PukkaLogger.log(PukkaLogger.Level.ACTION, "*** Creating reference for fragment " + fragment.getName() + "(" + classification.getExtraction().getSemanticExtraction() + ")");

                    ReferenceType type = ReferenceType.getOpen();

                    Reference reference = new Reference(
                            classification.getExtraction().getSemanticExtraction(),
                            fragment.getKey(),
                            fragment.getKey(),          // TODO: Points to itself, is this ok?
                            version.getKey(),
                            project.getKey(),
                            type,
                            classification.getExtraction().getSemanticExtraction(),
                            0                          //TODO: Anchor position not implemented
                    );
                    reference.store();
                    references++;
                    updated = true;
                    continue;
                }


                /**************************************
                        Definition Source

                 */


                if(classification.getType().getName().equals(FeatureTypeTree.DefinitionDef.getName())){

                    // The analysis has classified a definition Source.
                    // We create a definition and also add a definition tag. (This may be removed
                    // later when definitions are properly displayed and searchable from the frontend)

                    PukkaLogger.log(PukkaLogger.Level.ACTION, "*** Creating definition for fragment " + fragment.getName() + "(" + classification.getPattern().getText() + ")");

                    Definition definition = new Definition(
                            classification.getPattern().getText(),
                            fragment.getKey(),
                            version.getKey(),
                            project.getKey());
                    definition.store();


                    fragmentClassification = new FragmentClassification(
                            fragment.getKey(),
                            FeatureTypeTree.DefinitionDef.getName(),
                            0,              // requirement level not implemented
                            0,              // applicable phase not implemented
                            "",
                            classification.getKeywords(),
                            system.getKey(),
                            version.getKey(),
                            project.getKey(),
                            classification.getPattern().getText(),
                            classification.getPattern().getPos(),
                            classification.getPattern().getLength(),
                            classification.getSignificance(),
                            "not specified rule",                       //TODO: This should be implemented later
                            analysisTime.getSQLTime().toString());


                    classificationsToStore.add(fragmentClassification);
                    classifications++;

                    updated = true;

                    // Also store the definition in the abstract document to be able to detect it later
                    aDocument.addDefinition(definition.getName());

                    continue;
                }

                /**************************************
                        Risk (or a child to risk)

                 */


                if(classification.getType().getName().equals(FeatureTypeTree.Risk.getName()) ||
                    classification.getType().getParent().getName().equals(FeatureTypeTree.Risk.getName())){

                    // Detecting a risk should result in a risk created in the system.

                    PukkaLogger.log(PukkaLogger.Level.ACTION, "*** Creating risk for fragment " + fragment.getName() + "(" + classification.getPattern().getText() + ")");

                    String riskDescription = "The phrasing " + classification.getPattern().getText() + "(" + classification.getTag()+ ")";

                    RiskClassification risk = new RiskClassification(
                            fragment.getKey(),
                            defaultRisk,
                            riskDescription,
                            "#RISK",
                            system.getKey(),
                            version.getKey(),
                            project.getKey(),
                            classification.getPattern().getText(),
                            classification.getPattern().getPos(),
                            analysisTime.getSQLTime().toString()
                    );
                    risk.store();
                    risks++;
                    fragment.setRisk(defaultRisk);  // Set it in the fragment too

                    // Creating a risk description. This should really be part of displaying the risk, but the

                    ContractAnnotation riskAnnotation = new ContractAnnotation(
                            "Risk Description",
                            fragment.getKey(),
                            0,  // This is the first anyway...
                            riskDescription,
                            system.getKey(),
                            version.getKey(),
                            classification.getPattern().getText(),
                            0,                          //TODO: Anchor position not implemented
                            analysisTime.getSQLTime().toString()

                    );


                    riskAnnotation.store();
                    annotations++;

                    fragment.keywordString =  searchManager.getUpdatedKeywords(fragment, risk);

                    updated = true;
                    continue;

                }

                /**************************************
                        Definition Usage

                 */


                if(classification.getType().getName().equals(FeatureTypeTree.DefinitionUsage.getName())){

                    // The analysis has classified a definition usage
                    // We create a reference and a low priority classificaiton that will not be shown

                    PukkaLogger.log(PukkaLogger.Level.ACTION, "*** Creating definition reference fragment " + fragment.getName() + "(" + classification.getPattern().getText() + ")");

                    ContractFragment definitionFragment = getFragmentForDefinition(fragment, classification.getPattern().getText(), definitionsForProject);

                    if(definitionFragment.exists()){
                        ReferenceType type = ReferenceType.getDefinitionUsage();

                        Reference reference = new Reference(
                                classification.getPattern().getText(),
                                fragment.getKey(),
                                definitionFragment.getKey(),     // Point to the definition
                                version.getKey(),
                                project.getKey(),
                                type,
                                classification.getPattern().getText(),
                                classification.getPattern().getPos()
                                );
                        reference.store();

                        references++;
                        updated = true;
                    }
                    else{
                        PukkaLogger.log(PukkaLogger.Level.WARNING, "Internal error: Definition \""+ classification.getPattern().getText()+
                                "\" identified in analysis but then not found for processing. (Document: " + fragment.getVersion().getDocument().getName() + ")");
                    }

                    System.out.println(" *** Storing Definition Usage classificaiton for definition");

                    fragmentClassification = new FragmentClassification(
                            fragment.getKey(),
                            classification.getType().getName(),
                            0,              // requirement level not implemented
                            0,              // applicable phase not implemented
                            "",
                            classification.getKeywords(),
                            system.getKey(),
                            version.getKey(),
                            project.getKey(),
                            classification.getPattern().getText(),
                            classification.getPattern().getPos(),
                            classification.getPattern().getLength(),
                            Significance.MATCH_SIGNIFICANCE,
                            "not specified rule",
                            analysisTime.getSQLTime().toString());

                    classificationsToStore.add(fragmentClassification);

                    continue;
                }

                /**************************************

                        Default action is to jut add the classification from the analysis

                 */





                if(classification.getRelevance() < RELEVANCE_THRESHOLD){

                    PukkaLogger.log(PukkaLogger.Level.ACTION, "*** Ignoring classification " + fragment.getName() + "( relevance "+ classification.getRelevance()+" below threshold)");
                }
                else{

                    PukkaLogger.log(PukkaLogger.Level.ACTION, "*** Classifying fragment " + fragment.getName() + " with tag "+ classification.getType()+". Pattern(" + classification.getPattern().getText() + ")" + "Relevance: " + classification.getRelevance());

                    //System.out.println(classification.getType().getName() + " " + classification.getTag());

                    System.out.println(" *** Storing pattern " + classification.getPattern().getText() + " for classificaiton");

                    fragmentClassification = new FragmentClassification(
                            fragment.getKey(),
                            classification.getType().getName(),
                            0,              // requirement level not implemented
                            0,              // applicable phase not implemented
                            "",
                            classification.getKeywords(),
                            system.getKey(),
                            version.getKey(),
                            project.getKey(),
                            classification.getPattern().getText(),
                            classification.getPattern().getPos(),
                            classification.getPattern().getLength(),
                            classification.getSignificance(),
                            "not specified rule",
                            analysisTime.getSQLTime().toString());

                    classificationsToStore.add(fragmentClassification);
                    //fragmentClassification.store();

                    fragment.keywordString =  searchManager.getUpdatedKeywords(fragment, fragmentClassification);


                    // Only update the number of classifications if it is above the threshold
                    // for displaying in the front-end

                    if(classification.getSignificance() > Significance.DISPLAY_SIGNIFICANCE){

                        classifications++;
                        updated = true;
                    }


                }

            }catch(BackOfficeException e){


                e.printStackTrace(System.out);
                e.logError("Error in hanndleResult for fragment " + fragment + " and classification " + classification.getType().getName());
            }



        }

        // Store all classifications

        classificationsToStore.store();

        if(classifications != 0){


            fragment.setClassificatonCount(classifications);
            System.out.println("*** Updating classification count to " + classifications + " for fragment " + fragment.getName());
            updated = true;
        }

        if(references != 0){

            fragment.setReferenceCount(references);
            updated = true;
        }

        if(annotations != 0){

            fragment.setAnnotationCount(annotations);
            updated = true;
        }

        if(updated)
            fragment.update();


        return risks;
    }

    /***************************************************************************************************
     *
     *              Locate the definition source in the project
     *
     *
     *
     *
     *
     * @param definition          - the definition usage classification
     * @param fragment            - the fragment where it is used
     * @param projectDefinitions  - the definitions found in the project
     * @return                    - key to the fragment for the definition
     *
     *
     *              If the definition source is not found, we will return null. This indicates that
     *              we create an open reference
     *
     */


    private ContractFragment getDefinitionSource(String definition, ContractFragment fragment, List<Definition> projectDefinitions) {

        for (Definition projectDefinition : projectDefinitions) {

            if(projectDefinition.getName().equals(definition))
                return projectDefinition.getDefinedIn();
        }

        PukkaLogger.log(PukkaLogger.Level.INFO, "Detected a definition usage, but could not find definition source for " + definition);
        return null;
    }


    /********************************************************************************************************
     *
     *
     *
     * @param analysisResult
     * @param fragment
     * @param project
     * @throws BackOfficeException
     */

    private void handlePostProcessResult(NewAnalysisOutcome analysisResult, ContractFragment fragment, Project project, List<Definition> definitionsForProject) throws BackOfficeException{

        boolean updated = false;
        int classifications = 0;
        int references = 0;
        int annotations = 0;
        ContractFragment definitionFragment;

        PukkaLogger.log(PukkaLogger.Level.DEBUG, "Found " + analysisResult.getClassifications().size() + " classifications in post process of " + fragment.getText());

        for(Classification classification : analysisResult.getClassifications()){

            try{

                PukkaLogger.log(PukkaLogger.Level.DEBUG, "Classification is " + classification.getType().getName());


                if(classification.getType().getName().equals(FeatureTypeTree.DefinitionUsage.getName())){

                    // The analysis has classified a definition usage
                    // We create a definition usage tag.

                    PukkaLogger.log(PukkaLogger.Level.ACTION, "*** Creating definition reference fragment " + fragment.getName() + "(" + classification.getPattern().getText() + ")");

                    definitionFragment = getFragmentForDefinition(fragment, classification.getPattern().getText(), definitionsForProject);

                    if(definitionFragment.exists()){
                        ReferenceType type = ReferenceType.getDefinitionUsage();

                        Reference reference = new Reference(
                                classification.getPattern().getText(),
                                fragment.getKey(),
                                definitionFragment.getKey(),     // Point to the definition
                                fragment.getVersionId(),
                                project.getKey(),
                                type,
                                classification.getPattern().getText(),
                                0                          //TODO: Anchor position not implemented
                                );
                        reference.store();

                        references++;
                        updated = true;
                    }
                    else{
                        PukkaLogger.log(PukkaLogger.Level.FATAL, "Internal error: Definition \""+ classification.getPattern().getText()+
                                "\" identified in analysis but then not found for processing. (Document: " + fragment.getVersion().getDocument().getName() + ")");
                    }
                    break;

                }



            }catch(BackOfficeException e){


                e.printStackTrace(System.out);
                e.logError("Error in hanndleResult for fragment " + fragment + " and classification " + classification.getType().getName());
            }

        }



        if(classifications != 0){

            fragment.setClassificatonCount(fragment.getClassificationsForFragment().size());   // Using actual value here to correct any issues
        }

        if(references != 0){

            fragment.setReferenceCount(fragment.getReferencesForFragment().size());   // Using actual value here to correct any issues
        }

        if(annotations != 0){

            fragment.setAnnotationCount(fragment.getAnnotationsForFragment().size());   // Using actual value here to correct any issues
        }


        if(updated)
            fragment.update();
    }


    /*******************************************************************************'
     *
     *          look up the definition for the reference
     *
     * @param fragment
     * @param pattern
     * @return
     *
     *
     */

    private ContractFragment getFragmentForDefinition(ContractFragment fragment, String pattern, List<Definition> definitionsForProject) throws BackOfficeException {


        //System.out.println("Looking through " + definitionsForProject.size() + " definitions in project");

        for(Definition definition : definitionsForProject){

            if(definition.getName().toLowerCase().equals(pattern.toLowerCase())){

                return definition.getDefinedIn();
            }
        }

        return new ContractFragment();

    }


    /******************************************************************************************
     *
     *              Main method for parsing the uploaded document
     *
     *
     *              // TODO: Catch the analysis and IO exceptions and convert them to appropriate BackOffice Exceptions to be passed to the end user
     *
     * @param document
     * @param version
     * @throws BackOfficeException
     * @throws IOException
     */


    public void parseFile(Contract document, ContractVersionInstance version) throws BackOfficeException{

        InputStream stream;
        FragmentSplitterInterface docXManager;

        try{

            RepositoryInterface repository = new BlobRepository();

            //System.out.println("Trying to access document: " + document.getFile() + " locally. ");

            RepositoryFileHandler fileHandler = new RepositoryFileHandler(document.getFile());

            //System.out.println("So far so good");

            stream = repository.getInputStream(fileHandler);

            //System.out.println("Got the stream!");


        }catch(IOException e){

            PukkaLogger.log( e );
            throw new BackOfficeException(BackOfficeException.General, "Could not get document.");
        }

        try{

            PukkaLogger.log(PukkaLogger.Level.ACTION, "*******************Phase I: Parsing document");
            docXManager = new DocumentManager(document.getName(), stream);


        }catch (AnalysisException e){

            // We failed to parse the document. We will return an error and have to delete the version.
            // If this was the only version for the document we remove it too.

            /*
            version.delete();

            int remainingVersions = document.getVersionsForDocument().size();
            if(remainingVersions == 0)
                document.delete();
              */
            throw new BackOfficeException(BackOfficeException.General, "Could not parse document " + document.getFile() + "("+ e.getMessage()+")");
        }

        try{

            LanguageCode languageCode = Analyser.detectLanguage(document.getFile(), docXManager.getBody());
            document.setLanguage(languageCode.code);
            String oldName = document.getName();
            document.setName(docXManager.getDocumentTitle());

            PukkaLogger.log(PukkaLogger.Level.INFO, "Updating name from " + oldName + " to " + docXManager.getDocumentTitle() );
            document.update();

        }catch(AnalysisException e){

            //Fail to detect Language

            throw new BackOfficeException(BackOfficeException.General, "Could not detect language");
        }


        fragmentDocument(document.getFile(), version, docXManager);



    }


    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);

    }



}

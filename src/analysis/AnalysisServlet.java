package analysis;

import actions.Action;
import actions.ActionStatus;
import actions.ActionTable;
import analysis.deferrance.DeferenceHandler;
import analysis2.AnalysisException;
import analysis2.NewAnalysisOutcome;
import classification.FragmentClassificationTable;
import contractManagement.*;
import crossReference.*;
import dataRepresentation.DBTimeStamp;
import databaseLayer.DBKeyInterface;
import document.*;
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

    private String modelDirectory = MODEL_DIRECTORY; // Default value

    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Get not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);

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
            } catch (Exception e) {

                PukkaLogger.log( e );

                document.setMessage("Failed to parse document: Internal Error");
                document.setStatus(ContractStatus.getFailed());
                document.update();

                invalidateDocumentCache(document, project);

                returnError("Internal Error", HttpServletResponse.SC_OK, resp);
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
     *
     */


    public void analyse(ContractVersionInstance newVersion, ContractVersionInstance oldVersion) throws BackOfficeException {

        List<OutcomeMap> outcomeList = new ArrayList<OutcomeMap>();
        DBTimeStamp analysisTime = new DBTimeStamp();
        Contract document = newVersion.getDocument();
        Project project = document.getProject();
        Organization organization = project.getOrganization();
        AbstractProject aProject = project.createAbstractProject();
        AbstractDocument aDocument = newVersion.createAbstractDocumentVersion(aProject, new LanguageCode(document.getLanguage()));
        List<Definition> definitions = project.getDefinitionsForProject();
        LanguageCode documentLanguage = new LanguageCode(document.getLanguage());
        List<ContractFragment> fragments = newVersion.getFragmentsForVersion(
                new LookupList().addSorting(new Sorting(ContractFragmentTable.Columns.Ordinal.name(), Ordering.FIRST))
        );
        PortalUser owner = document.getOwner();
        SearchManager2 searchManager = new SearchManager2(project, owner);

        document.setStatus(ContractStatus.getAnalysing());  // Setting the status for the document
        document.update();

        aProject.addDocument(aDocument);

        // Create an analyser and detect basic principles for the document.

        try {

            Analyser analyser = new Analyser(documentLanguage, modelDirectory);

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

            //postProcessAnalysis(analyser, outcomeList, aDocument, owner, newVersion, project, aProject, analysisTime, definitions);


            // Retrieve and store all keywords

            storeKeywords(newVersion, analyser, document, project, organization);


            // If there is an old version (update rather than upload new) we need to transpose all old attributes

            if(oldVersion != null){

                // Clone all previous annotations, reference, etc.

                PukkaLogger.log(PukkaLogger.Level.INFO, "Transposing classification, risk and annotations");
                Transposer transposer = new Transposer();
                transposer.clone(oldVersion, newVersion);

            }


            // Index all fragments in the search engine

            PukkaLogger.log(PukkaLogger.Level.INFO, "Indexing" + fragments.size() + " fragments for the analysis of the document " + document);
            searchManager.indexFragments(fragments, newVersion, document);



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
     * @param documentVersion - document to analyse
     * @throws BackOfficeException
     */



    public void reAnalyse(ContractVersionInstance documentVersion) throws BackOfficeException {

        PukkaLogger.log(PukkaLogger.Level.INFO, "Deleting old keywords and attributes");

        // Remove existing attributes. These will be regenerated in the analysis

        deleteKeywords(documentVersion);
        deleteAttributes(documentVersion);


        analyse(documentVersion, null);

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
            System.out.println(" *** Document " + abstractDocument.name + " has " + abstractDocument.getDefinitions().size() + " definitions for the analysis.");

            /*
            for (String definition : abstractDocument.definitions) {

                System.out.print( definition + ", ");

            }
            System.out.println(" *** ");
              */
        }

        int risks = 0;
        NewAnalysisOutcome analysisOutcome;
        DeferenceHandler deference = new DeferenceHandler();
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

                analysisOutcome = analyser.analyseFragment(fragment.getText(), (int)fragment.getOrdinal(), headline, contextText, aDocument, cellInfo, aProject);

                NewAnalysisFeedback feedback = handleResult(analysisOutcome, fragment, deference, project, analysisTime, searchManager, aDocument, definitionsForProject, documentVersion);
                risks += feedback.risks; // Count risks for the action message

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

    /*
    private void closeReferences(Analyser analyser, Project project) throws BackOfficeException{

        AbstractProject aProject = project.createAbstractProject();

        List<Reference> openReferencesForProject = project.getOpenReferences();

        System.out.println("Found " + openReferencesForProject.size() + " open references to analyse and close");

        for(Reference reference : openReferencesForProject){

            NewAnalysisOutcome outcome2 = analyser.analyseOpenReferences(reference.getName(), aProject);

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

      */

    /******************************************************************************************
     *
     *      Go through the entire project to find existing undetected references to the new document
     *
     * @param analyser
     * @param project
     * @param versionInstance

     */

    public void reanalyseProjectForReferences(Analyser analyser, Project project, ContractVersionInstance versionInstance, AbstractProject aProject,
                                              AbstractDocument aDocument, PortalUser user) throws BackOfficeException {


        List<Contract> contractsForProject = project.getContractsForProject();
        Contract currentDocument = versionInstance.getDocument();
        String name = currentDocument.getName().toLowerCase();
        String file = currentDocument.getFile().toLowerCase();
        ReferenceType type = ReferenceType.getExplicit();
        DBTimeStamp analysisTime = new DBTimeStamp();
        SearchManager2 searchManager = new SearchManager2(project, user);
        List<Definition> definitionsForProject = project.getDefinitionsForProject();
        PortalUser systemUser = PortalUser.getSystemUser();


        PukkaLogger.log(PukkaLogger.Level.ACTION, "*****************************\nPhase IV: References and Definitions");


        ContractFragment firstFragment = new ContractFragment(new LookupItem()
                .addFilter(new ColumnFilter(ContractFragmentTable.Columns.Ordinal.name(), 0))
                .addFilter(new ReferenceFilter(ContractClauseTable.Columns.Version.name(), versionInstance.getKey())));

        if(!firstFragment.exists()){


            PukkaLogger.log(PukkaLogger.Level.WARNING, "The document " + currentDocument.getName() + " does not have a first fragment to direct references to");
        }


        for(Contract document : contractsForProject){


            //For all documents we look for references to the title and document name
            if(!document.equals(currentDocument)){

                ContractVersionInstance latestVersion = document.getHeadVersion();
                List<ContractFragment> fragmentsForDocument = latestVersion.getFragmentsForVersion(
                        new LookupList().addSorting(new Sorting(ContractFragmentTable.Columns.Ordinal.name(), Ordering.FIRST))
                );

                PukkaLogger.log(PukkaLogger.Level.INFO, "Reanalysing references in document " + document.getName());

                DeferenceHandler deference = new DeferenceHandler();
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

                            Reference reference = new Reference(name, fragment, firstFragment,  latestVersion, project, type, name, 0, systemUser);
                            reference.store();
                        }

                    }



                    NewAnalysisOutcome postProcessOutcome = analyser.postProcess(fragment.getText(), (int)fragment.getOrdinal(), aProject, false);
                    handleResult(postProcessOutcome, fragment, deference, project, analysisTime, searchManager, aDocument, definitionsForProject, latestVersion);


                }

                //TODO: Optimization: Only do this if it is changed
                invalidateFragmentCache(latestVersion);
                invalidateDocumentCache(document, project);

            }
        }

    }

    /*

    private void postProcessAnalysis(Analyser analyser, List<OutcomeMap> outcomeList, AbstractDocument aDocument, PortalUser owner,
                                     ContractVersionInstance version, Project project, AbstractProject aProject, DBTimeStamp analysisTime,
                                     List<Definition> definitionsForProject) throws BackOfficeException{

        PukkaLogger.log(PukkaLogger.Level.INFO, "Second pass with " + outcomeList.size() + " elements.");
        SearchManager2 searchManager = new SearchManager2(project, owner);


        for(OutcomeMap outcome : outcomeList){

            PukkaLogger.log(PukkaLogger.Level.INFO, "Post processing " + outcome.fragment.getText());

            NewAnalysisOutcome postProcessOutcome = analyser.postProcess(outcome.outcome, aProject);

            handleResult(postProcessOutcome, outcome.fragment, project, analysisTime, searchManager, aDocument, definitionsForProject, version);

        }

    }

    */

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
     *      Delete Attributes
     *
     *      When re-uploading a document or rerunning the analysis, the original
     *      attributes should be removed to avoid duplicates.
     *
     * @param versionInstance              - the active document version
     * @throws BackOfficeException
     *
     *
     */

    private void deleteAttributes(ContractVersionInstance versionInstance) throws BackOfficeException {


        // Remove automatically generated annotations

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

    /*

    private ContractFragment getDefinitionSource(String definition, ContractFragment fragment, List<Definition> projectDefinitions) {

        for (Definition projectDefinition : projectDefinitions) {

            if(projectDefinition.getName().equals(definition))
                return projectDefinition.getDefinedIn();
        }

        PukkaLogger.log(PukkaLogger.Level.INFO, "Detected a definition usage, but could not find definition source for " + definition);
        return null;
    }

      */


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
            docXManager = new DocumentManager(document.getFile(), stream);


        }catch (AnalysisException e){

            // We failed to parse the document. We will return an error and have to delete the version.
            // If this was the only version for the document we remove it too.

            /*
            version.delete();

            int remainingVersions = document.getVersionsForDocument().size();
            if(remainingVersions == 0)
                document.delete();
              */
            e.printStackTrace(System.out);
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


    public void setModelDirectory(String modelDirectory){

        this.modelDirectory = modelDirectory;
    }


}

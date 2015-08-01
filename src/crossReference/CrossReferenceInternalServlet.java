package crossReference;

import analysis.NewAnalysisFeedback;
import analysis.ReAnalysisServlet;
import analysis.deferrance.DeferenceHandler;
import analysis2.NewAnalysisOutcome;
import classification.FragmentClassification;
import classification.FragmentClassificationTable;
import classifiers.Classification;
import contractManagement.*;
import dataRepresentation.DBTimeStamp;
import dataRepresentation.DataObjectInterface;
import databaseLayer.DBKeyInterface;
import databaseLayer.DatabaseAbstractionFactory;
import document.AbstractProject;
import featureTypes.FeatureTypeTree;
import language.LanguageCode;
import log.PukkaLogger;
import net.sf.json.JSONObject;
import project.Project;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;
import search.SearchManager2;
import services.DocumentService;
import services.Formatter;
import system.Analyser;
import userManagement.PortalUser;
import userManagement.SessionManagement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**************************************************************************************
 *
 *                  Internal service for performing a reanalysis of cross references
 *
 *
 *                   - The reanalysis should be done every time the content is updated
 *                   - It is triggered by the CrossReference servlet (external)
 *                      -> through the AnynchAnalysis that queues the Web Hook
 *
 *
 */


public class CrossReferenceInternalServlet extends DocumentService {

    public static final String DataServletName = "CrossReferenceInternal";


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {
        try{

            logRequest(req);

            sessionManagement.allowBOAccess();

            if(!validateSession(req, resp, HttpServletResponse.SC_OK))    // Send OK here. A 403 would trigger a retry in the event queue
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);


            Formatter formatter = getFormatFromParameters(req);

            DBKeyInterface _project             = getMandatoryKey("project", req);
            boolean  forceAnalysis              = getOptionalBoolean("forceAnalysis", req, false);


            Project project = new Project(new LookupByKey(_project));

            if(!mandatoryObjectExists(project, resp))
                return;

            // First ensure the analysis is done

            awaitAnalysis( project, forceAnalysis, SessionManagement.MagicKey);

            // Delete all old cross references in the project

            deleteAll( project );

            // Add new analysis

            addCrossReference( project );

            setStatusToAnalysed( project );


            JSONObject response =  new JSONObject().put(DataServletName, "Queued");
            sendJSONResponse(response, formatter, resp);


        } catch (BackOfficeException e) {

            // Send OK here. An error code would trigger a retry

            PukkaLogger.log(e);
            returnError(e.narration, HttpServletResponse.SC_OK, resp);

        } catch (Exception e) {

            // Send OK here. An error code would trigger a retry

            PukkaLogger.log(e);
            returnError("Internal Error analyzing document", HttpServletResponse.SC_OK, resp);

        }

     }

    /***************************************************************
     *
     *          Before the cross referencing, we must
     *          ensure that all the documents are analysed.
     *
     *
     * @param project           - current project
     * @param forceAnalysis     - shall we force the analysis, even if it is analysed. (Used for system updates)
     * @param magicKey          - magic key for internal access
     */


    private void awaitAnalysis(Project project, boolean forceAnalysis, String magicKey) throws BackOfficeException {

        ReAnalysisServlet servlet = new ReAnalysisServlet();

        if(forceAnalysis){

            PukkaLogger.log(PukkaLogger.Level.INFO, "Force analyzing project " + project.getName());
            servlet.analyzeProject(project, magicKey);

        }else if(!isAnalysed(project)){

            PukkaLogger.log(PukkaLogger.Level.INFO, "Project " + project.getName() + " is not fully analyzed");
            servlet.analyzeProject(project, magicKey);

        }

        // Pause for 5 seconds and then check again
        int attempts = 30;

        while(!isAnalysed(project) && attempts > 0){


            try {
                Thread.sleep(5000);

                PukkaLogger.log(PukkaLogger.Level.INFO, "Checking analysis status for project " + project.getName());
                attempts--;

            } catch (InterruptedException e) {
                PukkaLogger.log( e );
                throw new BackOfficeException(BackOfficeException.General, "Analysis interrupted");
            }
        }

        if(attempts == 0){

            PukkaLogger.log(PukkaLogger.Level.INFO, "Project " + project.getName() + " failed cross referencing. Proceeding... ");
        }
        else
            PukkaLogger.log(PukkaLogger.Level.INFO, "Project " + project.getName() + " is analyzed. Proceeding... ");

    }


    /*************************************************************''
     *
     *          isAnalysed chacks a project to see if all the documents involved is analyzed
     *
     *
     * @param project    - the project to analyze
     * @return
     * @throws BackOfficeException
     */

    private boolean isAnalysed(Project project) throws BackOfficeException{


        List<Contract> documentsInProject = project.getContractsForProject();

        for (Contract contract : documentsInProject) {

            if(contract.getStatus().get__Id() == ContractStatus.getAnalysed().get__Id()) {

                PukkaLogger.log(PukkaLogger.Level.INFO, "Document " + contract.getName() + " is not analyzed properly." +
                        "( found " +contract.getStatus().getName() + " expecting "+ ContractStatus.getAnalysed().getName() +"). Reanalyzing the project.");
                continue;
            }

            if(contract.getStatus().get__Id() == ContractStatus.getFailed().get__Id())
                throw new BackOfficeException(BackOfficeException.General, "Analysis Failed");

            return false;
        }

        PukkaLogger.log(PukkaLogger.Level.INFO, "All Documents are analyzed.");
        return true;


    }



    //TODO: Remove this. Should be handled document by document

    private void setStatusToAnalysed(Project project) {

        List<Contract> documentsInProject = project.getContractsForProject();

        for (Contract contract : documentsInProject) {

            try {

                contract.setStatus(ContractStatus.getAnalysed());
                contract.setMessage("Analysed");
                contract.update();

            } catch (BackOfficeException e) {

                PukkaLogger.log( e );
            }
        }


    }


    /***********************************************************************************
     *
     *          Deleting all cross references from the project
     *
     *          This includes:
     *
     *           - all references  (except the manually added)
     *           - all classifications of the type #Definition_USAGE (that are not manually added)
     *           - all classifications of the type #Req_spec (that are not manually added)
     *
     *
     * @param project       - active project
     *
     *        //TODO: Refactor: all post process classifiers should be removed. Here they are hardcoded
     *        //TODO: Improvement: Access database twice. Merge request with or operation in filter
     *
     */


    public void deleteAll(Project project) throws BackOfficeException {

        PortalUser systemUser = PortalUser.getSystemUser();

        PukkaLogger.log(PukkaLogger.Level.INFO, "Delete old cross references for project " + project.getName() );

        // Delete all references

        List<Reference> referencesForProject = project.getReferencesForProject(new LookupList()
                .addFilter(new ReferenceFilter(ReferenceTable.Columns.Creator.name(), systemUser.getKey()))

        );

        PukkaLogger.log(PukkaLogger.Level.INFO, "Deleting "+ referencesForProject.size()+" old automatic reference(s).");

        ReferenceTable t = new ReferenceTable();
        t.createEmpty();
        t.withValues((List<DataObjectInterface>) (List<?>) referencesForProject);

        t.delete();

        List<FragmentClassification> definitionUsagesForProject = project.getFragmentClassificationsForProject(new LookupList()
                .addFilter(new ColumnFilter(FragmentClassificationTable.Columns.ClassTag.name(), FeatureTypeTree.DefinitionUsage.getName()))
                .addFilter(new ReferenceFilter(ReferenceTable.Columns.Creator.name(), systemUser.getKey()))
        );

        List<FragmentClassification> reqSpecForProject = project.getFragmentClassificationsForProject(new LookupList()
                .addFilter(new ColumnFilter(FragmentClassificationTable.Columns.ClassTag.name(), FeatureTypeTree.SolutionReq.getName()))
                .addFilter(new ReferenceFilter(ReferenceTable.Columns.Creator.name(), systemUser.getKey()))
        );

        List<FragmentClassification> classificationsToRemove = definitionUsagesForProject;
        classificationsToRemove.addAll(reqSpecForProject);


        PukkaLogger.log(PukkaLogger.Level.INFO, "Deleting "+ classificationsToRemove.size()+" old automatic #DefinitionUsage tag(s)");

        FragmentClassificationTable t2 = new FragmentClassificationTable();
        t2.createEmpty();
        t2.withValues((List<DataObjectInterface>)(List<?>)classificationsToRemove);

        t2.delete();

        PukkaLogger.log(PukkaLogger.Level.INFO, "Cross references deleted for project " + project.getName());

    }



    /****************************************************************************************
     *
     *      Go through the entire project to find existing undetected references to the new document
     *
     *
     * @param project                   - The current project
     * @throws BackOfficeException
     */

    public void addCrossReference(Project project) throws BackOfficeException{


        List<Contract> contractsForProject = project.getContractsForProject();
        ReferenceType type = ReferenceType.getExplicit();
        DBTimeStamp analysisTime = new DBTimeStamp();
        SearchManager2 searchManager = new SearchManager2(project, project.getCreator());
        List<Definition> definitionsForProject = project.getDefinitionsForProject();
        PortalUser systemUser = PortalUser.getSystemUser();
        AbstractProject abstractProject = project.createAbstractProject();
        Set<ContractVersionInstance> modifiedDocuments = new HashSet<ContractVersionInstance>( 128 );

        PukkaLogger.log(PukkaLogger.Level.ACTION, "*****************************\nCross Referencing");

        for(Contract document : contractsForProject){

            ContractVersionInstance latestVersion = document.getHeadVersion();
            document.setMessage("Cross Referencing Document");
            document.update();

            // We need a separate analyser per document as they can have different languages

            LanguageCode documentLanguage = new LanguageCode(document.getLanguage());
            System.out.println(" *** Language code: " + documentLanguage.code);
            Analyser analyser = new Analyser(documentLanguage, MODEL_DIRECTORY);

            List<ContractFragment> fragmentsInDocument = latestVersion.getFragmentsForVersion();

            DeferenceHandler deference = new DeferenceHandler();
            for (ContractFragment fragment : fragmentsInDocument) {

                try{

                    //Post process for definitions

                    NewAnalysisOutcome postProcessOutcome = analyser.postProcess(fragment.getText(), (int)fragment.getOrdinal(), abstractProject, false);
                    NewAnalysisFeedback feedback = handleResult(postProcessOutcome, fragment, deference, project, analysisTime, searchManager, null, definitionsForProject, latestVersion);

                    /*


                          TODO:  Not implemented looking for chapter names

                    for (AbstractDocument abstractDocument : abstractProject.documents) {

                        for (AbstractStructureItem chapter : abstractDocument.chapters) {

                            String chapterName = chapter.getTopElement().getBody().toLowerCase();

                            // Does the chapter contain a fragment except when it is the same
                            //NOTE: This is a pretty simple match. It has to be improved, but we need to mind execution time

                            if(fragment.getText().toLowerCase().contains(chapterName) &&
                                   ! fragment.getKey().toString().equals(chapter.getKey())){

                                PukkaLogger.log(PukkaLogger.Level.INFO, " Creating a reference from " + fragment.getText() + " to " + chapterName);
                                PukkaLogger.log(PukkaLogger.Level.INFO, " Fragment " + fragment.getKey() + " to chapter " + chapter.getKey());

                                Reference reference = new Reference(
                                        chapterName,
                                        fragment.getKey(),
                                        new DatabaseAbstractionFactory().createKey(chapter.getKey()),
                                        fragment.getVersionId(),
                                        project.getKey(), ReferenceType.getExplicit(),
                                        chapterName,
                                        0,
                                        PortalUser.getSystemUser().getKey());

                                int existingReferences = (int)fragment.getReferenceCount();
                                reference.store();
                                fragment.setReferenceCount(existingReferences + 1);

                            }

                        }


                    }
                    */

                }catch(Exception e){

                    PukkaLogger.log( e );

                }


            }

            // Setting status back to analysed when we are ready

            document.setStatus(ContractStatus.getAnalysed());
            document.setMessage("Analysed!");
            document.update();

            invalidateFragmentCache(latestVersion);
            invalidateDocumentCache(document.getKey(), project.getKey());

        }

        PukkaLogger.log(PukkaLogger.Level.ACTION, "******* Closing references");

        //TODO: Improvement: This should be moved to the main pass above in the reference analysis to try to close it directly

        LanguageCode documentLanguage = new LanguageCode("EN");
        Analyser analyser = new Analyser(documentLanguage, MODEL_DIRECTORY);

        closeReferences(analyser, project);

        PukkaLogger.log(PukkaLogger.Level.ACTION, "*****************************\nDone");


        /*

                // All of this should move to the analysis module


        for(Contract document : contractsForProject){

            // We need a separate analyser per document as they can have different languages

            LanguageCode documentLanguage = new LanguageCode(document.getLanguage());
            Analyser analyser = new Analyser(documentLanguage, MODEL_DIRECTORY);

            //For all documents we look for references to the title and document name

            ContractVersionInstance latestVersion = document.getHeadVersion();
            List<ContractFragment> fragmentsForDocument = latestVersion.getFragmentsForVersion();

            PukkaLogger.log(PukkaLogger.Level.INFO, "Reanalysing references in document " + document.getName());

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



                NewAnalysisOutcome postProcessOutcome = analyser.postProcess(fragment.getText(), aProject);
                handleResult(postProcessOutcome, fragment, project, analysisTime, searchManager, aDocument, definitionsForProject, latestVersion);


            }

            //TODO: Optimization: Only do this if it is changed
            invalidateFragmentCache(latestVersion);
            invalidateDocumentCache(document, project);


        }

            */
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

            NewAnalysisOutcome outcome2 = analyser.analyseOpenReferences(reference.getName(), aProject, false);

            System.out.println("***** analyseOpenReference delivered " + outcome2.getClassifications().size() + " classifications for reference " + reference.getName());

            for(Classification classification : outcome2.getClassifications()){


                if(classification.getType().getName().equals(FeatureTypeTree.Reference.getName())){

                    // This is a new reference target. The key (which comes from the AbstractStructureItem)
                    // is extracted as semantic extraction

                    try{

                        DBKeyInterface clauseId = new DatabaseAbstractionFactory().createKey(classification.getExtraction().getSemanticExtraction());

                        PukkaLogger.log(PukkaLogger.Level.INFO, "Closing an open reference " + reference.getName() + ". Found matching clause " + clauseId.toString() );

                        reference.setTo(clauseId);
                        reference.setType(ReferenceType.getExplicit());
                        reference.update();

                    }catch(Exception e){

                        PukkaLogger.log(PukkaLogger.Level.WARNING, "No key retrieved from Structure Item");


                    }



                    break;
                }



                PukkaLogger.log(PukkaLogger.Level.FATAL, "Action " + classification.getType().getName() + " not supported after analyseOpenReferences()");

            }

        }

    }




}

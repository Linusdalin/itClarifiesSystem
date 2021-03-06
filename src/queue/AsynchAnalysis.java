package queue;

import analysis.AnalysisServlet;
import analysis.ReAnalysisInternalServlet;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import contractManagement.Contract;
import contractManagement.ContractVersionInstance;
import project.Project;
import crossReference.CrossReferenceInternalServlet;
import log.PukkaLogger;
import overviewExport.OverviewExportInternalServlet;
import pukkaBO.exceptions.BackOfficeException;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;

/*************************************************************************'
 *
 *      Wrapping the queue functionality
 *
 */

public class AsynchAnalysis {

    Queue queue;
    private String sessionToken;
    private String magicKey = null;

    private static final boolean USE_SCHEDULING = true;

    public AsynchAnalysis(String sessionToken){

        this.sessionToken = sessionToken;
        queue = QueueFactory.getDefaultQueue();

    }


    public void setMagicKey(String mKey){

        this.magicKey = mKey;
    }

    /***********************************************************
     *
     */


    public void analyseDocument(ContractVersionInstance version, boolean performAnalysis) throws BackOfficeException{

        analyseDocument(version, performAnalysis, null);

    }

    /*****************************************************************
     *
     *              Perform the analysis of a document
     *
     *
     * @param version              - document version to analyse
     * @param performAnalysis      - shall we actually perform classification analysis (or only parse file)
     * @param oldVersion           - optional old version that shall be wiped
     * @throws BackOfficeException
     */


    public void analyseDocument(ContractVersionInstance version, boolean performAnalysis, ContractVersionInstance oldVersion) throws BackOfficeException {

        if(USE_SCHEDULING){

        // Add the task to the default queue.

            TaskOptions call = withUrl("/Analyze")
                    .param("version", version.getKey().toString());

            if(sessionToken != null)
                call.param("session", sessionToken);

            if(magicKey != null)
                call.param("magicKey", magicKey);

            if(oldVersion != null){
                call.param("oldVersion", oldVersion.getKey().toString());
                PukkaLogger.log(PukkaLogger.Level.INFO, "Analysing version of new document");
            }
            else{

                PukkaLogger.log(PukkaLogger.Level.INFO, "Analysing new version of existing document");
            }

                queue.add(call);

        }
        else{

            Contract document = version.getDocument();

                AnalysisServlet servlet = new AnalysisServlet();
                servlet.parseFile(document, version);

            if(performAnalysis)
                servlet.analyse(version, oldVersion);
            else
                PukkaLogger.log(PukkaLogger.Level.INFO, "Suppressing analysis for document " + document.getName() );

        }
    }

    /***************************************************************
     *
     */


    public void crossReference(Project project, boolean forceAnalysis) throws BackOfficeException{

        if(USE_SCHEDULING){


            PukkaLogger.log(PukkaLogger.Level.INFO, "Cross Referencing project");

            TaskOptions call = withUrl("/CrossReferenceInternal")
                    .param("project", project.getKey().toString())
                    .param("forceAnalysis", String.valueOf(forceAnalysis));


            if(sessionToken != null)
                call.param("session", sessionToken);

            if(magicKey != null)
                call.param("magicKey", magicKey);

            queue.add(call);


        }
        else{

                // Force-analysis not implemented for non scheduling

                CrossReferenceInternalServlet servlet = new CrossReferenceInternalServlet();
                servlet.deleteAll(project);
                servlet.addCrossReference(project);

        }

    }



    public void reAnalyse(ContractVersionInstance document) throws BackOfficeException{

        if(USE_SCHEDULING){

            PukkaLogger.log(PukkaLogger.Level.INFO, "Re-analysing project " + " with magicKey: "+ magicKey );

            TaskOptions call = withUrl("/ReAnalyzeInternal")
                    .param("version", document.getKey().toString());

            if(sessionToken != null)
                call.param("session", sessionToken);

            if(magicKey != null)
                call.param("magicKey", magicKey);

            queue.add(call);

        }
        else{

                ReAnalysisInternalServlet servlet = new ReAnalysisInternalServlet();
                servlet.reAnalyse(document);

        }

    }

    /***************************************************************************
     *
     *              Schedule the internal web hook for generating the overview.
     *
     *
     * @param project              - the project key
     * @param comment              - comment (from the user)
     * @param exportTags           - JSON array of the tags to generate on.
     *
     * @throws BackOfficeException
     *
     *          //NOTE: The local execution is not implemented (not in use)
     */


    public void generateOverview(Project project, String comment, String exportTags) throws BackOfficeException{

        if(USE_SCHEDULING){

            PukkaLogger.log(PukkaLogger.Level.INFO, "Generating project overview" + " with magicKey: "+ magicKey );

            TaskOptions call = withUrl("/OverviewExportInternal")
                    .param("project",   project.getKey().toString())
                    .param("comment",   comment)
                    .param("tags",      exportTags);

            if(sessionToken != null)
                call.param("session", sessionToken);

            if(magicKey != null)
                call.param("magicKey", magicKey);

            queue.add(call);

        }
        else{

                OverviewExportInternalServlet servlet = new OverviewExportInternalServlet();
                throw new BackOfficeException(BackOfficeException.General, " Not implemented generateOverview without queueing");

        }
    }
}

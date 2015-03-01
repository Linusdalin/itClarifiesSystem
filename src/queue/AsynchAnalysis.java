package queue;

import analysis.AnalysisServlet;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import contractManagement.ContractVersionInstance;
import contractManagement.Project;
import crossReference.CrossReferenceInternalServlet;
import log.PukkaLogger;
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
    private static final boolean USE_SCHEDULING = true;

    public AsynchAnalysis(String sessionToken){

        this.sessionToken = sessionToken;
        queue = QueueFactory.getDefaultQueue();

    }

    /***********************************************************
     *
     */


    public void analyseDocument(ContractVersionInstance version) throws BackOfficeException{

        analyseDocument(version, null);

    }


    public void analyseDocument(ContractVersionInstance version, ContractVersionInstance oldVersion) throws BackOfficeException {

        if(USE_SCHEDULING){

        // Add the task to the default queue.


            if(oldVersion == null){

                PukkaLogger.log(PukkaLogger.Level.INFO, "Analysing version of new document");

                queue.add(withUrl("/Analyze")
                        .param("session", sessionToken)
                        .param("version", version.getKey().toString()));
            }
            else{

                PukkaLogger.log(PukkaLogger.Level.INFO, "Analysing new version of existing document");

                queue.add(withUrl("/Analyze")
                        .param("session", sessionToken)
                        .param("version", version.getKey().toString())
                        .param("oldVersion", oldVersion.getKey().toString()));
            }

        }
        else{

                AnalysisServlet servlet = new AnalysisServlet();
                servlet.parseFile(version.getDocument(), version);
                servlet.analyse(version, oldVersion);

        }
    }

    /***************************************************************
     *
     */


    public void crossReference(Project project){

        if(USE_SCHEDULING){


                PukkaLogger.log(PukkaLogger.Level.INFO, "Cross Referencing project");

                queue.add(withUrl("/CrossReferenceInternal")
                        .param("session", sessionToken)
                        .param("project", project.getKey().toString()));

        }
        else{

                CrossReferenceInternalServlet servlet = new CrossReferenceInternalServlet();
                servlet.deleteAll(project);
                servlet.addCrossReference(project);

        }

    }

}

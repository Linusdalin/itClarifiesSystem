package queue;

import analysis.AnalysisServlet;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import contractManagement.ContractVersionInstance;
import log.PukkaLogger;
import pukkaBO.exceptions.BackOfficeException;

import java.io.IOException;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;

/**
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


}

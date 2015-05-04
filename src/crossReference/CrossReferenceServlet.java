package crossReference;

import actions.*;
import classification.ClassificationOverviewManager;
import contractManagement.Contract;
import contractManagement.ContractFragment;
import contractManagement.ContractStatus;
import contractManagement.Project;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pukkaBO.condition.LookupByKey;
import pukkaBO.exceptions.BackOfficeException;
import queue.AsynchAnalysis;
import services.DocumentService;
import services.Formatter;
import userManagement.PortalUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/********************************************************
 *
 *          Cross reference service.
 *
 *          This will trigger the queuing of the internal servlet
 *
 */

public class CrossReferenceServlet extends DocumentService {

    public static final String DataServletName = "CrossReference";



    /*************************************************************************
     *
     *          Start a cross reference analysis
     *
     *
     * @throws java.io.IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        try{

            logRequest(req);

            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);


            Formatter formatter = getFormatFromParameters(req);

            DBKeyInterface _project             = getMandatoryKey("project", req);


            Project project = new Project(new LookupByKey(_project));

            if(!mandatoryObjectExists(project, resp))
                return;

            // Queue the event

            AsynchAnalysis queue = new AsynchAnalysis(sessionManagement.getToken());
            queue.crossReference(project);

            // Update the status for all the documents

            setStatusToCrossReferencing(project);


            JSONObject response =  new JSONObject().put(DataServletName, "Queued");
            sendJSONResponse(response, formatter, resp);


        }catch(BackOfficeException e){

            PukkaLogger.log(e);
            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        } catch ( Exception e) {

            PukkaLogger.log(e);
            returnError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        }


    }

    /********************************************************************************
     *
     *          Set status to cross referencing so that it is updated in the document
     *          call in the frontend
     *
     *
     * @param project         - the project to cross reference
     */


    private void setStatusToCrossReferencing(Project project) {

        List<Contract> documentsInProject = project.getContractsForProject();

        for (Contract document : documentsInProject) {

            try {

                document.setStatus(ContractStatus.getAnalysing());
                document.setMessage("Cross Referencing project...");
                document.update();

                invalidateDocumentCache(document.getKey(), project.getKey());

            } catch (BackOfficeException e) {

                PukkaLogger.log( e );
            }
        }




    }


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {


        doGet(req, resp);

     }



    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);

     }



}

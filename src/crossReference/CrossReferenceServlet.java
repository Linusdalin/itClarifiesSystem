package crossReference;

import contractManagement.Contract;
import contractManagement.ContractStatus;
import project.Project;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import net.sf.json.JSONObject;
import pukkaBO.condition.LookupByKey;
import pukkaBO.exceptions.BackOfficeException;
import queue.AsynchAnalysis;
import services.DocumentService;
import services.Formatter;
import userManagement.SessionManagement;

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

            sessionManagement.allowBOAccess();

            if(!validateSession(req, resp))
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

            // Queue the event

            AsynchAnalysis queue = new AsynchAnalysis(sessionManagement.getToken());
            queue.setMagicKey(SessionManagement.MagicKey);
            queue.crossReference(project, forceAnalysis);

            // Update the status for all the documents

            setStatusToCrossReferencing(project);
            project.invalidateExport();

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
                document.setMessage("Awaiting Cross Reference...");
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

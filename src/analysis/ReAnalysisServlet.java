package analysis;

import contractManagement.*;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import net.sf.json.JSONObject;
import pukkaBO.condition.*;
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
 *          Running reanalysis for all documents in a project
 *
 *          This uses the latest version and will not create a
 *          new version of the document in the database
 *
 */

public class ReAnalysisServlet extends DocumentService {

    public static final String DataServletName = "ReAnalysis";


    /***********************************************************************************
     *
     *      Post or Get
     *
     * @param req -
     * @param resp -
     * @throws java.io.IOException -
     *
     *
     */


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        try{

            logRequest(req);

            // This is an admin service. It uses the google authenticate and is then free to access data without a session

            //if(!googleAuthenticate(req, resp))
            //   return;

            DBKeyInterface _project      = getMandatoryKey("project", req);
            String magicKey              = getMandatoryString("magicKey", req);


            Formatter formatter = getFormatFromParameters(req);

            if(magicKey == null || !magicKey.equals(SessionManagement.MagicKey)){

                sendJSONResponse(new ParseFeedbackItem(ParseFeedbackItem.Severity.ERROR,
                        "No Access !", 0).toJSON(), formatter, resp);

            }

            Project project = new Project(new LookupByKey(_project));


            if(!project.exists()){

                sendJSONResponse(new ParseFeedbackItem(ParseFeedbackItem.Severity.ERROR,
                        "Could not find project "+ _project.toString() + ". Aborting!", 0).toJSON(), formatter, resp);
                return;

            }

            List<Contract> documentsForProject = project.getContractsForProject();
            AsynchAnalysis analysisQueue = new AsynchAnalysis(null);
            analysisQueue.setMagicKey(magicKey);

            for (Contract document : documentsForProject) {

                ContractVersionInstance latestVersion = document.getHeadVersion();

                PukkaLogger.log(PukkaLogger.Level.INFO, "Analysing");
                analysisQueue.reAnalyse(latestVersion);

                invalidateFragmentCache(latestVersion);

            }

            JSONObject json = new JSONObject().put("ReAnalysis", "QUEUED");
            sendJSONResponse(json, formatter, resp);



        }catch(BackOfficeException e){

            PukkaLogger.log( e );
            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        } catch ( Exception e) {

            PukkaLogger.log(e);
            returnError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        }

     }



    /*************************************************************************
     *
     *
     * @throws java.io.IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        if(req.getParameter("_method") != null && req.getParameter("_method").equals("DELETE")){
            doDelete(req, resp);
            return;
        }

        doPost(req, resp);


    }


    /************************************************************************
     *
     *
     * @param req -
     * @param resp -
     * @throws java.io.IOException
     */


    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);


    }


}

package overviewExport;

import databaseLayer.DBKeyInterface;
import net.sf.json.JSONObject;
import project.Project;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;
import services.DocumentService;
import services.Formatter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/********************************************************
 *
 *          Prepare an export
 *
 *
 *

 */

public class OverviewExportStatusServlet extends DocumentService {

    public static final String DataServletName = "ExportStatus";

    /**************************************************************************
     *
     *
     *              Get the status fo the background analysis
     *
     * @param req
     * @param resp
     * @throws IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        try{

            if(req.getParameter("_method") != null && req.getParameter("_method").equals("DELETE")){
                doDelete(req, resp);
                return;
            }

            logRequest(req);

            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);

            Formatter formatter = getFormatFromParameters(req);

            DBKeyInterface _project           = getMandatoryKey("project", req);

            Project project = new Project(new LookupByKey(_project));

            if(!mandatoryObjectExists(project, resp))
                return;

            ExtractionStatus status = new ExtractionStatus(new LookupItem().addFilter(new ReferenceFilter(ExtractionStatusTable.Columns.Project.name(), project.getKey())));
            JSONObject response = new JSONObject();

            if(!status.exists()){

                response.put(DataServletName,
                            new JSONObject()
                                    .put("status", ExtractionState.getEmpty().getName())
                            );


            }
            else{

                response.put(DataServletName,
                            new JSONObject()
                                     .put("timeStamp",  status.getDate().getSQLTime().toString())
                                     .put("author",     status.getUserId().toString())
                                     .put("status",     status.getStatus().getName())
                                     .put("key",        status.getKey().toString())
                                     .put("tags",       status.getTags())
                                     .put("message",    status.getDescription())
                            );

            }



            sendJSONResponse(response, formatter, resp);

        }catch(BackOfficeException e){

            e.printStackTrace(System.out);
            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        } catch ( Exception e) {

            e.printStackTrace(System.out);
            returnError(e.getLocalizedMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        }

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
     * @throws java.io.IOException
     */


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Post not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);



    }



    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);

    }



}

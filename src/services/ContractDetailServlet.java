package services;

import contractManagement.*;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import net.sf.json.JSONObject;
import pukkaBO.condition.*;

import pukkaBO.exceptions.BackOfficeException;
import userManagement.AccessGrant;
import userManagement.PortalUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/********************************************************
 *
 *          Contract Servlet returning one contract
 *
 */

public class ContractDetailServlet extends DocumentService{

    public static final String DataServletName = "DocumentDetails";


    /***********************************************************************************
     *
     *      Uploading detail data of a document
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
     *
     *      //TODO: Not implementes
     */


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Post not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

     }



    /*************************************************************************
     *
     *          Get details for a document
     *
     *          Parameters:
     *
     *          &key=<key>      (mandatory)
     *
     *
     * @throws java.io.IOException
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

           PortalUser user = sessionManagement.getUser();

           DBKeyInterface key                = getMandatoryKey("key", req);


           Contract contract = new Contract(new LookupByKey(key));

           //AccessGrant grant = sessionManagement.getGrantForDocument(contract);

           if(!mandatoryObjectExists(contract, resp))
               return;

           JSONObject details = new JSONObject()
                .put("id", contract.getKey().toString())
                .put("name", encodeToJSON(contract.getName()))
                .put("description", encodeToJSON(contract.getDescription()))
                .put("project", contract.getProjectId().toString())
                .put("visibility", "org")                                           //Deprecated
                .put("access", contract.getAccess().getName())
                .put("owner", contract.getOwnerId().toString())
                .put("version", contract.getHeadVersion().getVersion())
                .put("history", "[]")
                .put("creation", contract.getCreation().getISODate())
                .put("modified", "Not implemented yet")
                .put("modified by", "Not implemented yet");


           resp.getWriter().print(new JSONObject().put(DataServletName, details));
           setRespHeaders(formatter , resp);

       }catch(BackOfficeException e){

           PukkaLogger.log(e);
           returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

       } catch ( Exception e) {

           PukkaLogger.log(e);
           returnError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

       }
}


    /************************************************************************
     *
     *          Delete is not supported. Details will be deleted with the actual document
     *
     *            - instances
     *            - clauses
     *            - fragments
     *
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
     */


    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

    }


}

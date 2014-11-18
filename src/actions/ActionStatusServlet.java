package actions;

import actions.ActionStatus;
import actions.ActionStatusTable;
import contractManagement.*;
import dataRepresentation.DataObjectInterface;
import databaseLayer.DBKeyInterface;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;
import services.DocumentService;
import services.Formatter;
import userManagement.Organization;
import userManagement.PortalUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/********************************************************
 *
 *          Risk Flag Servlet
 *
 */

public class ActionStatusServlet extends DocumentService {

    public static final String DataServletName = "ActionStatus";


    /***********************************************************************************
     *
     *      Adding a new classification class
     *
     *
     * @param req -
     * @param resp -
     * @throws java.io.IOException
     *
     */


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        logRequest(req);
        returnError("Post not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);

     }



    /*************************************************************************
     *
     *          Get all classifications available
     *
     *
     * @throws java.io.IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        if(req.getParameter("_method") != null && req.getParameter("_method").equals("DELETE")){
            doDelete(req, resp);
            return;
        }



        try{
            logRequest(req);

            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);


            Formatter formatter = getFormatFromParameters(req);

            JSONArray list = new JSONArray();

            ActionStatusTable allStatuses = new ActionStatusTable(new LookupList()
                    .addSorting(new Sorting(ActionStatusTable.Columns.Ordinal.name(), Ordering.FIRST)));

            for(DataObjectInterface object : allStatuses.getValues()){

                ActionStatus status = (ActionStatus)object;

                JSONObject statusObject = new JSONObject()
                        .put("id", status.getKey().toString())
                        .put("name", status.getName())
                        .put("ordinal", status.getOrdinal());
                list.put(statusObject);

            }

            JSONObject json = new JSONObject().put(DataServletName, list);
            sendJSONResponse(json, formatter, resp);

        }catch(BackOfficeException e){

            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            e.printStackTrace();


        } catch ( Exception e) {

            returnError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            e.printStackTrace();

        }

     }





    /************************************************************************
     *
     *          Delete is not supported.
     *
     *
     * @param req -
     * @param resp -
     * @throws java.io.IOException
     */


    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        logRequest(req);
        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);

    }


}

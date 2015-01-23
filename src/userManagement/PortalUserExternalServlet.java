package userManagement;

import contractManagement.ProjectTable;
import dataRepresentation.DataObjectInterface;
import dataRepresentation.DataTable;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;
import services.Formatter;
import services.ItClarifiesService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/***************************************************
 *
 *         Portal User Servlet is used to crete/delete new users
 *         and get user details.
 *
 */

public class PortalUserExternalServlet extends ItClarifiesService {

    public static final String DataServletName = "PortalUserExternal";



    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        try{
            System.out.println("checkpoint 1");

            logRequest(req);

            System.out.println("checkpoint 2");

            if(!validateSession(req, resp))
                return;

            System.out.println("checkpoint 3");

            if(blockedSmokey(sessionManagement, resp))
                return;

            System.out.println("checkpoint 4");

            DBKeyInterface key                = getOptionalKey("user", req);
            PortalUser requester              = sessionManagement.getUser();
            JSONObject json;

            System.out.println("checkpoint 5");

            ConditionInterface condition = getLookupConditionForOptionalKey(key);
            condition.addFilter(new ReferenceFilter(PortalUserTable.Columns.Organization.name(), requester.getOrganizationId()));

            System.out.println("checkpoint 6");

            if(key == null)
                PukkaLogger.log(PukkaLogger.Level.INFO, "Qualifying list on organization " + requester.getOrganization().getName() + " for user " + requester.getName());
            PortalUserTable userList = new PortalUserTable(condition);
            Formatter formatter = getFormatFromParameters(req);
            JSONArray users = new JSONArray();

            System.out.println("checkpoint 7");

            for(DataObjectInterface o : userList.getValues()){

                PortalUser user = (PortalUser)o;

                // Filter out the "non-user" users. They are not supposed to be sent to the frontend
                //if(user.getUserId() != 0)
                // Moved this check to the frontend

                    users.put(createExternalUserInfoObject(user));

            }
            System.out.println("checkpoint 8");

            json = new JSONObject()
                  .put(DataServletName, users);


            sendJSONResponse(json, formatter, resp);

        }catch(BackOfficeException e){

            returnError("Error getting user details " + e.narration, ErrorType.GENERAL, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, PukkaLogger.Level.FATAL, resp);
            e.printStackTrace();

        }catch ( Exception e) {

            returnError("Error getting user details " + e.getMessage(), ErrorType.GENERAL, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, PukkaLogger.Level.FATAL, resp);
            e.printStackTrace();
        }

    }


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Post not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

    }


    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

    }

    /**************************************************************
     *
     *          Create one object
     *
     *
     * @param user - user to create object from
     * @return     - JSON data object
     */


    private JSONObject createExternalUserInfoObject(PortalUser user){



        JSONObject infoObject = new JSONObject()
             .put("id", user.getKey().toString())
             .put("name", user.getName())
             .put("type", user.getType());


        //infoObject.put("internal", (user.getUserId() == 0 ? "true" : "false"));

        return infoObject;

    }

}

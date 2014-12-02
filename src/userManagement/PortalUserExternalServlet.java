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

            logRequest(req);

            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;


            DBKeyInterface key                = getOptionalKey("user", req);
            PortalUser requester = sessionManagement.getUser();
            JSONObject json;

            ConditionInterface condition = getLookupConditionForOptionalKey(key);
            condition.addFilter(new ReferenceFilter(PortalUserTable.Columns.Organization.name(), requester.getOrganizationId()));

            if(key == null)
                PukkaLogger.log(PukkaLogger.Level.INFO, "Qualifying list on organization " + requester.getOrganization().getName() + " for user " + requester.getName());
            PortalUserTable userList = new PortalUserTable(condition);
            Formatter formatter = getFormatFromParameters(req);
            JSONArray users = new JSONArray();

            for(DataObjectInterface o : userList.getValues()){

                PortalUser user = (PortalUser)o;

                // Filter out the "non-user" users. They are not supposed to be sent to the frontend
                //if(user.getUserId() != 0)
                // Moved this check to the frontend

                    users.put(createExternalUserInfoObject(user));

            }

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


    private JSONObject createExternalUserInfoObject(PortalUser user){



        JSONObject infoObject = new JSONObject()
             .put("id", user.getKey().toString())
             .put("name", user.getName());


        infoObject.put("internal", (user.getUserId() == 0 ? "true" : "false"));

        return infoObject;

    }

}

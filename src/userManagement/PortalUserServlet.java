package userManagement;

import dataRepresentation.DBTimeStamp;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import net.sf.json.JSONObject;
import pukkaBO.password.PasswordManager;
import services.Formatter;
import services.ItClarifiesService;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/***************************************************
 *
 *         Portal User Servlet is used to crete/delete new users
 *         and get user details.
 *
 */

public class PortalUserServlet extends ItClarifiesService {

    public static final String DataServletName = "PortalUser";


    /**********************************************************************'
     *
     *              Create or update a new user
     *
     *
     *
     *
     * @param req
     * @param resp
     * @throws IOException
     */



    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        try{
            logRequest(req);

            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;


            String username         = getMandatoryString("username", req);
            String email            = getMandatoryString("email", req);
            boolean wsAdmin         = getOptionalBoolean("admin", req, true);

            PortalUser parent = sessionManagement.getUser();

            PortalUser user = new PortalUser(new LookupItem().addFilter(new ColumnFilter(PortalUserTable.Columns.Name.name(), username)));

            if(user.exists()){

                returnError("User Already Exists", HttpServletResponse.SC_OK, resp);
                resp.flushBuffer();
                return;

            }


            DBTimeStamp registrationDate = new DBTimeStamp();   // Set now as a registration date
            Organization org = parent.getOrganization();


            // Create the user
            boolean isActive = true;



            PortalUser newUser = new PortalUser(username, 4711, PortalUser.Type.EXTERNAL.name(), email, registrationDate.getISODate(), org, isActive, wsAdmin);
            newUser.store();


            PukkaLogger.log(PukkaLogger.Level.MAJOR_EVENT, "Created a new user " + newUser.getName() + " with id " + newUser.getKey());

            Formatter formatter = getFormatFromParameters(req);

            JSONObject json = createPostResponse(DataServletName, newUser);

            sendJSONResponse(json, formatter, resp);


        }catch(BackOfficeException e){

            returnError("Error creating user: " + e.narration, ErrorType.GENERAL, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, PukkaLogger.Level.FATAL, resp);
            e.logError( "Error in Post in Portal User");

        } catch ( Exception e) {

            returnError("Error creating user: " + e.getMessage(), ErrorType.GENERAL, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, PukkaLogger.Level.FATAL, resp);
            resp.flushBuffer();

            PukkaLogger.log( e );

        }
     }


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


            Formatter formatter = getFormatFromParameters(req);

            PortalUser user = sessionManagement.getUser();

            JSONObject json = new JSONObject()
                    .put(DataServletName, new JSONObject()
                            .put("name", user.getName())
                            .put("email", user.getEmail())
                            .put("wsAdmin", user.getWSAdmin())
                            .put("organization", user.getOrganization().getName()));


            sendJSONResponse(json, formatter, resp);

        }catch(BackOfficeException e){

            PukkaLogger.log( e );
            returnError("Error accessing user details: " + e.narration, ErrorType.GENERAL, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, PukkaLogger.Level.FATAL, resp);


        }catch ( Exception e) {

            PukkaLogger.log( e );
            returnError("Error accessing user details.", ErrorType.GENERAL, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, PukkaLogger.Level.FATAL, resp);
        }

    }

    /************************************************************************************************
     *
     *          Deleting a user also requires deleting the user in the login service
     *
     *
     * @param req
     * @param resp
     * @throws IOException
     */


    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        try{
            logRequest(req);

            if(!validateSession(req, resp))
                return;


            Formatter formatter = getFormatFromParameters(req);

            PortalUser user = sessionManagement.getUser();        // TODO: Add test for the case that the user already is deleted here

            UserManager userManager = new UserManager( user );
            userManager.deleteUser(user);

            JSONObject json = createDeletedResponse(DataServletName, user);

            sendJSONResponse(json, formatter, resp);


        }catch(BackOfficeException e){
        
            returnError("Error deleting user: " + e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            resp.flushBuffer();
            e.printStackTrace();

        }catch ( Exception e) {

            returnError("Error deleting user: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            resp.flushBuffer();
            e.printStackTrace();
        }

    }


}

package userManagement;

import dataRepresentation.DBTimeStamp;
import log.PukkaLogger;
import net.sf.json.JSONObject;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupItem;
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

public class RegistrationServlet extends ItClarifiesService {

    public static final String DataServletName = "Registration";


    /**********************************************************************'
     *
     *              Create a completely new user with a new Orgaisation
     *
     *
     *              //TODO: This should include creating a user in the remote login service
     *
     *              //TODO: User ID is dummy value
     *
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
     */



    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        try{
            logRequest(req);

            // There is no session validation here. This is open for all

            String username         = getMandatoryString("username", req);
            String email            = getMandatoryString("email", req);
            String password         = getMandatoryString("password", req);
            String orgName          = getMandatoryString("organization", req);

            PortalUser user = new PortalUser(new LookupItem().addFilter(new ColumnFilter(PortalUserTable.Columns.Name.name(), username)));

            if(user.exists()){

                returnError("User Already Exists", HttpServletResponse.SC_OK, resp);
                resp.flushBuffer();
                return;

            }


            boolean isAdmin = true;

            UserManager userManager = new UserManager();
            Organization newOrganization = userManager.createOrganization(orgName, "" );  // Empty description. Set it later

            if(!newOrganization.exists()){

                // Failed to create an organization

                returnError("Failed to create organization", HttpServletResponse.SC_OK, resp);
                resp.flushBuffer();
                return;

            }

            boolean isActive = false; // Inactive until we activate the user

            PortalUser newUser = userManager.createUser(username, password, email, newOrganization, isAdmin, isActive);

            PukkaLogger.log(PukkaLogger.Level.MAJOR_EVENT, "Created a new user " + newUser.getName() + " with id " + newUser.getKey());

            Formatter formatter = getFormatFromParameters(req);

            JSONObject json = new JSONObject()
                    .put(DataServletName, new JSONObject()
                                .put("user", newUser.getKey().toString())
                                .put("activation", userManager.getActivationCode())
                    );

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

        logRequest(req);
        returnError("Get not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);

    }


    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        logRequest(req);
        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);

    }


}

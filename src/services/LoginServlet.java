package services;

import backend.ItClarifies;
import databaseLayer.DBKeyInterface;
import databaseLayer.DatabaseAbstractionFactory;
import httpRequest.RequestHandler;
import httpRequest.ServerFactory;
import log.PukkaLogger;
import net.sf.json.JSONObject;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.LookupByKey;
import pukkaBO.exceptions.BackOfficeException;
import userManagement.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class LoginServlet extends ItClarifiesService{

    public static final String DataServletName = "Token";

    public static final BackOfficeInterface backOffice = new ItClarifies();


    /****************************************************************************'
     *
     *          Legacy Servlet, redirecting to the new Login service
     *
     *          This service will do the following:
     *
     *           - Take the old legacy request
     *           - Send the login request to the login server
     *           - Get the response with the session token
     *           - Create a session for the user locally
     *           - Send the response to the client in the old format
     *
     *
     * @param req -
     * @param resp -
     * @throws IOException
     *
     *
     *          NOTE: As the redirect will invalidate the client IP address, we have to pass this to teh login service as a parameter
     *          Once this is removed, that parameter can be removed too.
     *
     */


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        String loginServer;

        logRequest(req);

        try{

            String name           = getMandatoryString    ("user", req);
            String password       = getMandatoryString    ("password", req);

            Formatter formatter = getFormatFromParameters(req);

            loginServer = ServerFactory.getLoginServer();

            String ipAddress = getIPAddress(req);

            RequestHandler requestHandler = new RequestHandler(loginServer + "/Login");
            JSONObject response = new JSONObject(requestHandler.excutePost("user=" + name + "&password=" + password + "&ipaddress=" + ipAddress));

            PukkaLogger.log(PukkaLogger.Level.INFO, "Got response " + response);

            if(response.has("error")){

                String errorMessage = ((JSONObject) response.getJSONArray("error").get(0)).getString("message");
                returnError("Got error from login service " + errorMessage, HttpServletResponse.SC_FORBIDDEN, resp);
                return;

            }

            String token = response.getString("Login");
            int userId = response.getInt("User");


            PortalUser user = sessionManagement.createSessionForUser(token, userId, ipAddress);

            if(!user.exists()){
                returnError("The user " + name + " does not exist", ErrorType.SESSION, HttpServletResponse.SC_BAD_REQUEST, resp);
                return;
            }

            // Create legacy response    { "user" : "<key>" "Token" : "<session token>" }

            JSONObject legacyResponse = new JSONObject()
                    .put("user", user.getKey().toString())
                    .put("Token", token);


            sendJSONResponse(legacyResponse, formatter, resp);

        } catch (BackOfficeException e) {

            e.printStackTrace(System.out);
            returnError(e.narration, HttpServletResponse.SC_BAD_REQUEST, resp);

        } catch (Exception e){

            e.printStackTrace(System.out);
            returnError("Internal Error in Login", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        }

     }


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        doPost(req, resp);

    }

    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in Login", HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

    }


}

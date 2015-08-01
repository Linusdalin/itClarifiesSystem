package services;

import backend.ItClarifies;
import com.google.appengine.api.utils.SystemProperty;
import databaseLayer.DBKeyInterface;
import databaseLayer.DatabaseAbstractionFactory;
import httpRequest.RequestHandler;
import httpRequest.ServerFactory;
import log.PukkaLogger;
import net.sf.json.JSONObject;
import org.apache.log4j.Level;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupByKey;
import pukkaBO.condition.LookupItem;
import pukkaBO.exceptions.BackOfficeException;
import userManagement.Organization;
import userManagement.PortalSessionTable;
import userManagement.PortalUser;
import userManagement.PortalUserTable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class SessionServlet extends ItClarifiesService{

    public static final String DataServletName = "Session";

    public static final BackOfficeInterface backOffice = new ItClarifies();


    /****************************************************************************'
     *
     *
     *              Validate a token is done against the remote login server
     *
     *              We pass the client ip address to ensure sessions are kept
     *
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
     *
     *
     */


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        String loginServer;


        try{

            logRequest(req);

            Formatter formatter = getFormatFromParameters(req);
            String token = getMandatoryString("token", req);
            String ipAddress = getIPAddress(req);

            loginServer = ServerFactory.getLoginServer();

            RequestHandler requestHandler = new RequestHandler(loginServer + "/Validate");

            String responseString = requestHandler.excutePost("token=" + token + "&ipAddress=" + ipAddress);
            PukkaLogger.log(PukkaLogger.Level.INFO, "Got response from Login Service: " + responseString);
            JSONObject response = new JSONObject(responseString);

            if(response.has("error")){

                String errorMessage = ((JSONObject) response.getJSONArray("error").get(0)).getString("message");
                returnError("Got error from login service " + errorMessage, HttpServletResponse.SC_FORBIDDEN, resp);
                return;

            }

            PukkaLogger.log(PukkaLogger.Level.DEBUG, "Got response " + response);


            String outcome = response.getString("Validate");
            int userId = response.getInt("User");

            if(!outcome.equals("OK")){

                returnError("Could not validate session", HttpServletResponse.SC_FORBIDDEN, resp);
                return;
            }

            PortalUser user = sessionManagement.createSessionForUser(token, userId, ipAddress);

            if(!user.exists()){
                returnError("No user found for userid " + userId, HttpServletResponse.SC_BAD_REQUEST, resp);
                return;
            }

            // Create legacy response    { "user" : "<key>" "Token" : "<session token>" }

            JSONObject legacyResponse = new JSONObject()
                    .put("user", user.getKey().toString())
                    .put("Token", token);


            sendJSONResponse(legacyResponse, formatter, resp);

        } catch (BackOfficeException e) {

            PukkaLogger.log(e);
            returnError(e.narration, HttpServletResponse.SC_BAD_REQUEST, resp);

        }

     }


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        doPost(req, resp);

    }

    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in Session", HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

    }


}

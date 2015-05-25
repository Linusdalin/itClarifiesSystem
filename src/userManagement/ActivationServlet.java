package userManagement;

import databaseLayer.DBKeyInterface;
import httpRequest.RequestHandler;
import httpRequest.ServerFactory;
import log.PukkaLogger;
import net.sf.json.JSONObject;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupByKey;
import pukkaBO.condition.LookupItem;
import pukkaBO.exceptions.BackOfficeException;
import services.Formatter;
import services.ItClarifiesService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/***************************************************
 *
 *         Activation Servlet is used to activate a new user

 *
 */

public class ActivationServlet extends ItClarifiesService {

    public static final String DataServletName = "Activation";


    /**********************************************************************'
     *
     *              Change the state to activates
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

            String activationCode   = getMandatoryString("activation", req);
            DBKeyInterface _user    = getMandatoryKey("user", req);

            PortalUser user = new PortalUser(new LookupByKey(_user));

            if(!user.exists()){

                returnError("User Does not Exist", HttpServletResponse.SC_BAD_REQUEST, resp);
                resp.flushBuffer();
                return;

            }

            String loginServer = ServerFactory.getLoginServer();


            RequestHandler requestHandler = new RequestHandler(loginServer + "/Activate");
            JSONObject response = new JSONObject(requestHandler.excutePost("activation=" + activationCode));

            PukkaLogger.log(PukkaLogger.Level.INFO, "Got response " + response);

            if(response.has("error")){

                String errorMessage = ((JSONObject) response.getJSONArray("error").get(0)).getString("message");
                returnError("Got error from login service " + errorMessage, HttpServletResponse.SC_FORBIDDEN, resp);
                return;

            }


            PukkaLogger.log(PukkaLogger.Level.INFO, "Activates user " + user.getName());

            Formatter formatter = getFormatFromParameters(req);

            JSONObject json = createPostResponse(DataServletName, user);

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

package services;

import backend.ItClarifies;
import log.PukkaLogger;
import net.sf.json.JSONObject;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.exceptions.BackOfficeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class LogoutServlet extends ItClarifiesService{

    public static final String DataServletName = "Logout";

    public static final BackOfficeInterface backOffice = new ItClarifies();


    /****************************************************************************'
     *
     *          Post to logout session will close a session given a session token
     *
     *          // TODO: If the session can not be validated, send an OK.
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
     */


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        try{
            logRequest(req);

            // Manually validate session

            String sessionToken  = getMandatoryString("session", req);
            String ipAddress = getIPAddress(req);
            Formatter formatter = getFormatFromParameters(req);

            if(!sessionManagement.validate(sessionToken, ipAddress)){

                JSONObject json = new JSONObject().put("status", "Closed");
                sendJSONResponse(json, formatter, resp);
                return;
            }

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);



            String status = sessionManagement.close(sessionToken);

            JSONObject json = new JSONObject().put("status", status);

            sendJSONResponse(json, formatter, resp);


        } catch (BackOfficeException e) {

            PukkaLogger.log(e);
            returnError("Error in " + DataServletName, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        } catch (Exception e) {

            PukkaLogger.log(e);
            returnError("Error in " + DataServletName, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
        }


     }

    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        doPost(req, resp);
    }

    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in Logout", HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();


    }


}

package services;

import log.PukkaLogger;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import userManagement.SessionCacheKey;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class PingServlet extends ItClarifiesService {

    public static final String DataServletName = "Ping";


    /****************************************************************************'
     *
     *          Post to åing service will fire up server or scale up during load
     *
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
     *
     *
     */


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        try{
            logRequest(req);
            Formatter formatter = getFormatFromParameters(req);

            String sessionToken = getOptionalString("session", req);

            if(sessionToken != null){

                // If there is a session token passed to the ping service, we will
                // update the session to keep it alive.

                String ipAddress = getIPAddress(req);
                if(!sessionManagement.keepAlive(sessionToken, ipAddress)){

                    returnError("Failed to keep session alive", HttpServletResponse.SC_FORBIDDEN, resp);

                }

            }


            JSONObject response = new JSONObject()
                    .put(DataServletName, "Pong");

            sendJSONResponse(response, formatter, resp);


        } catch (JSONException e) {

            PukkaLogger.log(e);
            returnError(e.getMessage(), HttpServletResponse.SC_BAD_REQUEST, resp);

        } catch (Exception e) {

            PukkaLogger.log(e);
            returnError("Internal Error", HttpServletResponse.SC_BAD_REQUEST, resp);

        }

     }


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        doPost(req, resp);

    }

    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

    }


}

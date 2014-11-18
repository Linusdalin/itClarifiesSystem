package services;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/********************************************************
 *
 *          Contract Servlet returning one contract
 *
 */

public class PingServlet extends ItClarifiesService{

    public static final String DataServletName = "Ping";


    /*****************************************************************************
     *
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
     */


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        boolean prettyPrint, htmlEncode;

        returnError("Pong", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
        resp.flushBuffer();
        return;

     }

    /*************************************************************************
     *
     *          GET is not implemented
     *
     * @throws java.io.IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Get not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

    }


    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

    }


}

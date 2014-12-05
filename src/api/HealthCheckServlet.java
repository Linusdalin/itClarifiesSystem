package api;

import adminServices.GenericAdminServlet;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import maintenance.Smokey;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*************************************************************************'''
 *
 *              Admin Servlet
 *
 *              This uses Google accounts for access control
 */

public class HealthCheckServlet extends GenericAdminServlet {



    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {


        resp.getWriter().println("<html>");
        resp.getWriter().println("<body>");
        resp.getWriter().println("<p>Not really checking...</p>");
        resp.getWriter().println("</body>");
        resp.getWriter().println("</html>");

        resp.flushBuffer();

    }


}

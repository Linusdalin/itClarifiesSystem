package api;

import adminServices.GenericAdminServlet;
import cache.ServiceCache;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import maintenance.Smokey;
import pukkaBO.exceptions.BackOfficeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*************************************************************************'''
 *
 *              Admin Servlet
 *
 *              This uses Google accounts for access control
 */

public class ReleaseServlet extends GenericAdminServlet {


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

                // Check access right

        if(!googleAuthenticate(req, resp))
            return;

        String action = req.getParameter("Action");
        boolean isAlreadyInSmokey = Smokey.isInSmokey();
        String message = "";

        if(action.equals("SMOKE")){

            if(isAlreadyInSmokey)
                message = "Already in Smokey. Ignoring";
            else{
                Smokey.enterSmokey();
                message = "Entering Smokey";
            }
        }

        if(action.equals("UNSMOKE")){

            if(!isAlreadyInSmokey)
                message = "NOT in Smokey. Ignoring unsmoke";
            else{
                Smokey.exitSmokey();
                message = "Leaving Smokey";
            }
        }


        if(action.equals("CACHE")){

            try {

                ServiceCache cache = new ServiceCache("All");
                cache.clearAll();
                message = "All caches are cleared";

            } catch (BackOfficeException e) {

                e.printStackTrace();
                message = "Could not access cache...";
            }

        }



        resp.sendRedirect("admin.jsp?message=" + message);

    }

}

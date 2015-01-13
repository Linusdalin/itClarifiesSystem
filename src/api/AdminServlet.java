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

public class AdminServlet extends GenericAdminServlet {

    protected enum Environment {UNKNOWN, LOCAL, STAGE, LIVE}




    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

             // Get the application to be able to set different actions

        Environment environment = getEnvironment();
        resp.setContentType("text/html");


        String bgColour = getColourForEnvironment(environment);
        String heading = getHeadingForEnvironment(environment);


        resp.getWriter().println("<html>");
        resp.getWriter().println("<head><title>"+environment.name()+" Adm</title></head>");
        resp.getWriter().println("<body bgColor=\""+bgColour+"\">");
        resp.getWriter().println("<h1>"+ heading +"</h1>");

        // Check access right

        if(!googleAuthenticate(req, resp))
            return;


        String message = req.getParameter("message");
        if(message != null)
            resp.getWriter().println("<p>"+ message + "</p>");





        if(environment != Environment.LIVE){

            resp.getWriter().println("<a href=\"clearSearchIndex\">Clear Search Index</a><br>");
            resp.getWriter().println("<a href=\"createDB?Action=CREATE\">Create Database</a><br>");
            resp.getWriter().println("<a href=\"createDB?Action=CONSTANTS\">Populate Constants</a>");
            resp.getWriter().println("<a href=\"createDB?Action=TESTDATA\">Constants and Test values</a><br>");

          }else{

            resp.getWriter().println("<p><i>Create Database blocked</i></p>");
            resp.getWriter().println("<p><i>Populate Constants blocked</i></p>");
            resp.getWriter().println("<p><i>Constants and Test values blocked</i></p>");

        }

        if(Smokey.isInSmokey()){

            resp.getWriter().println("<br><b>IN SMOKEY</b>");

            resp.getWriter().println("<a href=\"releaseAdmin?Action=UNSMOKE\">Unsmoke</a><br>");
        }
        else{

            resp.getWriter().println("<a href=\"releaseAdmin?Action=SMOKE\">Put in smokey</a><br>");

        }

        resp.getWriter().println("<a href=\"releaseAdmin?Action=CACHE\">Clear Cache</a><br>");


        resp.getWriter().println("<br>");

        resp.getWriter().println("<a href=\"createConstants?Action=ANALYSE\">Update Constant Analysis</a><br>");
        resp.getWriter().println("<a href=\"bo/table.jsp?section=Home\">To Backoffice</a><br>");
        resp.getWriter().println("<a href=\"_ah/admin/datastore\">Database Viewer</a><br>");

        resp.getWriter().println("<a href=\"customPages/test/apiTest.jsp\">Api Test</a> ( <a href=\"customPages/test/apiTest.jsp?useToken=true\">Real Tokens</a> ) <br/>");
        //resp.getWriter().println("<a href=\"customPages/test/emailTest.jsp\">Test email</a><br/>");
        //resp.getWriter().println("<a href=\"customPages/test/uploadTest.jsp\">Pre-analyse File</a><br/>");


        resp.getWriter().println("<a href=\"generateDemo\">Add demo data</a><br>");



        resp.getWriter().println("<a href=\""+ userService.createLogoutURL(req.getRequestURI()) +"\">Logout</a><br>");

        resp.getWriter().println("</body>");
        resp.getWriter().println("</html>");

        resp.flushBuffer();



    }



    private String getHeadingForEnvironment(Environment environment) {

        switch (environment) {


            case UNKNOWN:

                return("Unknown environment admin....");

            case LOCAL:

                return("Test Admin");

            case STAGE:

                return("Stage/Demo Admin");

            case LIVE:
                return("itClarifies LIVE Admin");
        }

        return("Unknown environment admin....");


    }


    /***********************************************************************
     *
     *              Detecting the environment can be used to indicate this to the user
     *              (or have different access levels on different environments)
     *
     *
     *
     * @return - the environment enum
     */



    protected Environment getEnvironment() {

        String ID = SystemProperty.applicationId.get();
        String serviceAccountName = AppIdentityServiceFactory.getAppIdentityService().getServiceAccountName();


        if(serviceAccountName.contains("localhost")){

            return Environment.LOCAL;

        }
        else{

            if(ID.equals("itclarifiesapidemo") || ID.equals("itclarifiesapistage")){

                return Environment.STAGE;

            }

            if(ID.equals("itclarifiesapi") || ID.equals("itclarifiesapilogin")){

                return Environment.LIVE;

            }

            if(ID.equals("itclarifiesapi2") || ID.equals("itclarifiesapi3")){

                return Environment.LIVE;
            }

        }

        return Environment.UNKNOWN;
    }



    protected String getColourForEnvironment(Environment environment) {

        switch (environment) {


            case UNKNOWN:

                return("#000000");       // pitch black...

            case LOCAL:

                return("#EEEEEE");

            case STAGE:

                return("#AAFFFF");

            case LIVE:
                return("#FFAA00");
        }

        return("#000000");       // pitch black...


    }



}

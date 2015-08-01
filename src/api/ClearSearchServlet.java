package api;

import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import project.Project;
import project.ProjectTable;
import dataRepresentation.DataObjectInterface;
import pukkaBO.condition.LookupList;
import search.SearchManager2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*************************************************************************'''
 *
 *              Admin Servlet
 *
 *              This uses Google accounts for access control
 */

public class ClearSearchServlet extends AdminServlet {


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

             // Get the application to be able to set different actions

        Environment environment = getEnvironment();
        resp.setContentType("text/html");


        String bgColour = getColourForEnvironment(environment);
        String heading = "Deleted all search indexes";


        resp.getWriter().println("<html>");
        resp.getWriter().println("<head><title>"+environment.name()+" Adm</title></head>");
        resp.getWriter().println("<body bgColor=\""+bgColour+"\">");
        resp.getWriter().println("<h1>"+ heading +"</h1>");

        // Check access right

        if(!googleAuthenticate(req, resp))
            return;


        ProjectTable projects = new ProjectTable(new LookupList());

        for (DataObjectInterface object : projects.getValues()) {

            Project project = (Project)object;
            SearchManager2 searchManager = new SearchManager2(project, null);
            searchManager.clear();

        }


        resp.getWriter().println("</body>");
        resp.getWriter().println("</html>");

        resp.flushBuffer();



    }




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

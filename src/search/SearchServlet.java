package search;

import backend.ItClarifies;
import contractManagement.*;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.*;

import pukkaBO.exceptions.BackOfficeException;
import services.Formatter;
import services.ItClarifiesService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/********************************************************
 *
 *          Search Servlet returning a list of fragment id:s that should be listed
 *
 */

public class SearchServlet extends ItClarifiesService {

    public static final String DataServletName = "Search";

    public static final BackOfficeInterface backOffice = new ItClarifies();


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Post not supported in Search", HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

     }


    /*************************************************************************
     *
     *          Get search result
     *
     *          Parameters:
     *
     *          &project=<key>      mandatory project
     *          &text=<text>      mandatory search string
     *
     *
     * @throws java.io.IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        try {

            logRequest(req);

            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);


            Formatter formatter = getFormatFromParameters(req);


            DBKeyInterface _project = getMandatoryKey("project", req);
            Project project = new Project(new LookupByKey(_project));

            if(!mandatoryObjectExists(project, resp))
                return;

            String searchText             = getMandatoryString("text", req);


            PukkaLogger.log(PukkaLogger.Level.INFO, "Got search: " + searchText);

            SearchManager filter = new SearchManager();
            JSONObject json = filter.getMatchJson(searchText, project, sessionManagement);

            sendJSONResponse(json, formatter, resp);


        } catch (BackOfficeException e) {

            e.logError("Error (Get) in Search Servlet");
            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            resp.flushBuffer();

        }


    }

    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in Search", HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

    }


}

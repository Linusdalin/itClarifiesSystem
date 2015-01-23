package search;

import contractManagement.Contract;
import contractManagement.ContractVersionInstance;
import contractManagement.Project;
import databaseLayer.DBKeyInterface;
import fileHandling.*;
import log.PukkaLogger;
import net.sf.json.JSONObject;
import pukkaBO.condition.LookupByKey;
import pukkaBO.exceptions.BackOfficeException;
import services.Formatter;
import services.ItClarifiesService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/********************************************************
 *
 *          Project Servlet will return one or a list of projects.
 *
 */

public class SearchSelectionServlet extends ItClarifiesService{

    public static final String DataServletName = "SearchSelection";


    /*************************************************************************
     *
     *          Get projects matching the request criteria
     *
     *          Parameters:
     *
     *          &key=<key> (if left empty, it will return the entire list)
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
            Set<SearchHit> hits = filter.getAllMatches (searchText, project, sessionManagement);

            String csvFile = getCSVForSelection(hits);

            sendCSVResponse(csvFile, formatter, resp);


        } catch (BackOfficeException e) {

            e.logError("Error (Get) in Search Servlet");
            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            resp.flushBuffer();

        }

    }

    private String getCSVForSelection(Set<SearchHit> hits) {

        StringBuffer csv = new StringBuffer();

        csv.append(SearchHit.getHeader());

        for(SearchHit hit : hits){

            csv.append(hit.toCSV());


        }

        return csv.toString();

    }


    /***********************************************************************
     *
     *          Delete a project with recursively deleting all documents, fragments etc.
     *
     *
     * @param req -
     * @param resp -
     * @throws java.io.IOException
     *
     *          //TODO: Delete project should recursively delete all documents
     *          //TODO: Fix return values correctly
     *
     */

    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Post not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

     }


    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

     }




}

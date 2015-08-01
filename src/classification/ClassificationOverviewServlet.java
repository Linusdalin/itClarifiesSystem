package classification;

import project.Project;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import net.sf.json.JSONObject;
import pukkaBO.condition.LookupByKey;
import pukkaBO.exceptions.BackOfficeException;
import services.DocumentService;
import services.Formatter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/********************************************************
 *
 *          Classification overview servlet returning a tree of classifications
 *
 */

public class ClassificationOverviewServlet extends DocumentService {

    public static final String DataServletName = "ClassificationOverview";



    /*************************************************************************
     *
     *          Get all classifications available
     *
     *
     * @throws java.io.IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        try{

            logRequest(req);

            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);


            Formatter formatter = getFormatFromParameters(req);

            DBKeyInterface _project             = getMandatoryKey("project", req);

            Project project = new Project(new LookupByKey(_project));

            if(!mandatoryObjectExists(project, resp))
                return;

            PukkaLogger.log(PukkaLogger.Level.INFO, "Getting Classification Overview for project " + project.getName());

            ClassificationOverviewManager overview = new ClassificationOverviewManager();
            overview.compileClassificationsForProject(project, sessionManagement);

            JSONObject classificationStatistics =  new JSONObject().put(DataServletName, overview.getStatistics());
            sendJSONResponse(classificationStatistics, formatter, resp);


        }catch(BackOfficeException e){

            PukkaLogger.log(e);
            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        } catch ( Exception e) {

            PukkaLogger.log(e);
            returnError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        }


     }


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Post not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);

     }


    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);

     }



}

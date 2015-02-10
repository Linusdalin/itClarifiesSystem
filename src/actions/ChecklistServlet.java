package actions;

import classification.ClassificationOverviewManager;
import contractManagement.Project;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pukkaBO.condition.LookupByKey;
import pukkaBO.exceptions.BackOfficeException;
import services.DocumentService;
import services.Formatter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/********************************************************
 *
 *          Checklist servlet returning a tree of classifications
 *
 */

public class ChecklistServlet extends DocumentService {

    public static final String DataServletName = "Checklist";



    /*************************************************************************
     *
     *          Get checlists for a project or details given a checklist
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

            DBKeyInterface _key             = getOptionalKey("checkList", req);

            JSONObject response;

            if(_key == null){

                // No key given. Return a list of checklists for a given project

                DBKeyInterface _project             = getMandatoryKey("project", req);

                Project project = new Project(new LookupByKey(_project));

                if(!mandatoryObjectExists(project, resp))
                    return;

                response =  new JSONObject().put(DataServletName, getAllChecklists(project));


            }
            else{

                // Return details of a given checklist

                Checklist checklist = new Checklist(new LookupByKey(_key));

                if(!mandatoryObjectExists(checklist, resp))
                    return;

                Project project = checklist.getProject();

                if(!mandatoryObjectExists(project, resp))
                    return;

                response =  new JSONObject().put(DataServletName, getChecklistDetails(checklist, project));

            }

            sendJSONResponse(response, formatter, resp);


        }catch(BackOfficeException e){

            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            e.printStackTrace();

        } catch ( Exception e) {

            returnError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            e.printStackTrace();

        }


     }

    /**********************************************************************
     *
     *      Get all checklists for a project
     *
     * @param project - active project
     * @return
     */

    JSONArray getAllChecklists(Project project){


        JSONArray checklistsJSON = new JSONArray();
        List<Checklist> checklists = project.getChecklistsForProject();

        for (Checklist checklist : checklists) {

            ChecklistManager checklistManager = new ChecklistManager(checklist, null);
            checklistsJSON.put(checklistManager.getChecklistOverview());
        }

        PukkaLogger.log(PukkaLogger.Level.INFO, "Getting "+ checklists.size() +" checklists for project " + project.getName());

        return checklistsJSON;

    }

    /***************************************************************'
     *
     *          Get checklist details for a specific checklist
     *
     * @param checklist
     * @param project
     * @return
     */


    private JSONObject getChecklistDetails(Checklist checklist, Project project) {


        PukkaLogger.log(PukkaLogger.Level.INFO, "Getting Checklist " + checklist.getName());

        // Go through the classifications for the project

        ClassificationOverviewManager classificationManager = new ClassificationOverviewManager();
        classificationManager.compileClassificationsForProject(project, sessionManagement);

        // Create a checklist

        ChecklistManager checklistManager = new ChecklistManager(checklist, classificationManager.getStatisticsMap());

        return checklistManager.getChecklist();

    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Post not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);

     }


    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);

     }



}

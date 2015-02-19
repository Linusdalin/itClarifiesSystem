package actions;

import classification.ClassificationOverviewManager;
import contractManagement.ContractFragment;
import contractManagement.Project;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pukkaBO.condition.LookupByKey;
import pukkaBO.exceptions.BackOfficeException;
import services.DocumentService;
import services.Formatter;
import userManagement.PortalUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.List;

/********************************************************
 *
 *          Checklist servlet returning a tree of classifications
 *
 */

public class ChecklistItemServlet extends DocumentService {

    public static final String DataServletName = "ChecklistDetails";



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

                        // Go through the classifications for the project


                response =  new JSONObject().put(DataServletName, getDetailsForAllChecklists(project));


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
     *      Get all checklists details for a project
     *
     * @param project - active project
     * @return
     */

    JSONArray getDetailsForAllChecklists(Project project){


        ClassificationOverviewManager classificationManager = new ClassificationOverviewManager();
        classificationManager.compileClassificationsForProject(project, sessionManagement);


        JSONArray checklistsJSON = new JSONArray();
        List<Checklist> checklists = project.getChecklistsForProject();

        for (Checklist checklist : checklists) {

            ChecklistManager checklistManager = new ChecklistManager(checklist, classificationManager.getStatisticsMap());

            checklistsJSON.put(checklistManager.getChecklist());
        }

        PukkaLogger.log(PukkaLogger.Level.INFO, "Getting "+ checklists.size() +" checklists with details for project " + project.getName());

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

        try{

            logRequest(req);

            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);


            Formatter formatter = getFormatFromParameters(req);

            DBKeyInterface _key             = getOptionalKey("item", req);
            String name                     = getOptionalString("name", req, null);
            String text                     = getOptionalString("description", req, null);
            String comment                  = getOptionalString("comment", req, null);
            DBKeyInterface _source          = getOptionalKey("source", req);
            DBKeyInterface _comply          = getOptionalKey("comply", req);
            String tag                      = getOptionalString("comment", req, null);
            long _status                    = getOptionalLong("status", req, -1);

            PortalUser user = sessionManagement.getUser();

            if(_key == null){

                returnError("Create new checklist item not supported in " + DataServletName, HttpServletResponse.SC_BAD_REQUEST, resp);

            }
            else{

                // Updating an existing action

                ChecklistItem item = new ChecklistItem(new LookupByKey(_key));
                ContractFragment complianceFragment = null;

                if(tag != null && !tag.equals(""))
                    item.setTagReference(tag);

                if(name != null && !name.equals(""))
                    item.setName(name);

                if(text != null && !text.equals(""))
                    item.setDescription(text);

                if(comment != null && !comment.equals(""))
                    item.setDescription(comment);

                if(_source != null){

                    ContractFragment sourceFragment = new ContractFragment(new LookupByKey(_source));

                    if(!mandatoryObjectExists(sourceFragment, resp))
                        return;

                    item.setSource(sourceFragment.getKey());
                }

                if(_comply != null){

                    complianceFragment = new ContractFragment(new LookupByKey(_comply));

                    if(!mandatoryObjectExists(complianceFragment, resp))
                        return;

                    item.setCompletion(complianceFragment.getKey());
                }

                if(_status != -1){

                    ActionStatus status = new ActionStatusTable().getValue((int)_status);

                    if(complianceFragment == null &&
                            (status.equals(ActionStatus.getCompleted()) || status.equals(ActionStatus.getCompleted()))){

                        // We are trying to complete a checklist item, without passing a compliance fragment.
                        // This is only ok if we have done that already

                        if(!item.getCompletion().exists()){

                            returnError("Setting completion status not allowed without completion " + DataServletName, HttpServletResponse.SC_BAD_REQUEST, resp);

                        }
                    }

                    item.setStatus(status);
                }

                item.update();
                PukkaLogger.log(PukkaLogger.Level.INFO, "Checklist Item updated by user " + user.getName());


                JSONObject json = createPostResponse(DataServletName, item);
                sendJSONResponse(json, formatter, resp);

            }




        }catch(BackOfficeException e){

            e.printStackTrace(System.out);
            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        } catch ( Exception e) {

            e.printStackTrace(System.out);
            returnError("Internal Error getting actions", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        }

     }



    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);

     }



}

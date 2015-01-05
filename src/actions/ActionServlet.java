package actions;

import contractManagement.*;
import dataRepresentation.DBTimeStamp;
import databaseLayer.DBKeyInterface;
import email.ActionUpdateMail;
import email.NewActionMail;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pukkaBO.condition.LookupByKey;
import pukkaBO.email.MailInterface;
import pukkaBO.exceptions.BackOfficeException;
import services.Formatter;
import services.ItClarifiesService;
import userManagement.PortalUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/********************************************************
 *
 *          Contract Servlet returning one contract
 *
 */

public class ActionServlet extends ItClarifiesService {

    public static final String DataServletName = "Action";

    /*****************************************************************************
     *
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
     *
     *
     */


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        try{

            logRequest(req);

            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);


            Formatter formatter = getFormatFromParameters(req);

            DBKeyInterface _key             = getOptionalKey("action", req);
            String text                     = getOptionalString("description", req, "");
            String pattern                  = getOptionalString("pattern", req, "");
            DBKeyInterface _assignee        = getOptionalKey("assignee", req);
            long priority                   = getOptionalLong("priority", req, -1);
            long _status                    = getOptionalLong("status", req, -1);
            DBTimeStamp dueDate             = getOptionalDate("dueDate", req, new DBTimeStamp(DBTimeStamp.NO_DATE, "1900-00-00"));
            DBTimeStamp completedDate       = getOptionalDate("completedDate", req, new DBTimeStamp(DBTimeStamp.NO_DATE, "1900-00-00"));

            PortalUser creator = sessionManagement.getUser();
            Action action;

            ContractFragment fragment;
            ContractVersionInstance version;
            PortalUser assignee = new PortalUser(new LookupByKey(_assignee));



            if(_key == null){

                // If it is a new action, the assignee is mandatory

                if(!assignee.exists()){

                    returnError("Assignee does not exist", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
                    return;
                }


                // New action

                String name                     = getMandatoryString("name", req);
                DBKeyInterface _fragment        = getMandatoryKey("fragment", req);

                fragment = new ContractFragment(new LookupByKey(_fragment));
                version = fragment.getVersion();
                Contract document = version.getDocument();

                if(_status == -1){

                    returnError("Mandatory status missing", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
                    return;

                }

                ActionStatus status = new ActionStatusTable().getValue((int)_status);

                if(!mandatoryObjectExists(document, resp))
                    return;

                Project project = document.getProject();


                DBTimeStamp creationDate = new DBTimeStamp();


                if(_assignee == null)
                    _assignee = creator.getKey();

                action = new Action(
                        0,      // Global id is not used. Placeholder for future addition
                        name,
                        text,
                        pattern,
                        _fragment,
                        version.getKey(),
                        project.getKey(),
                        creator.getKey(),
                        _assignee,
                        priority,
                        status,
                        creationDate.getISODate(),
                        dueDate.getISODate(),
                        completedDate.getISODate());

                action.store();
                PukkaLogger.log(PukkaLogger.Level.INFO, "New Action created by user " + creator.getName());

                // Notify the assignee

                MailInterface mail = new NewActionMail(project, action, creator);
                mail.sendTo(assignee.getName(), assignee.getEmail());


            }
            else{

                // Updating an existing action

                String name                  = getOptionalString("name", req, "");
                action = new Action(new LookupByKey(_key));
                fragment = action.getFragment();
                version = action.getVersion();
                ActionStatus status = new ActionStatusTable().getValue((int)_status);
                ActionStatus previousStatus = action.getStatus();

                Project project = action.getProject();

                if(!mandatoryObjectExists(project, resp))
                    return;

                if(name != null && !name.equals(""))
                    action.setName(name);
                if(text != null && !text.equals(""))
                    action.setDescription(text);
                if(_assignee != null)
                    action.setAssignee(_assignee);
                if(priority != -1)
                    action.setPriority(priority);
                if(_status != -1)
                    action.setStatus(status);
                if(!dueDate.isEmpty())
                    action.setDue(dueDate);
                if(!completedDate.isEmpty())
                    action.setCompleted(completedDate);

                // Update the action

                action.update();
                PukkaLogger.log(PukkaLogger.Level.INFO, "Action updated by user " + creator.getName());

                if(_status != -1 && !status.equals(previousStatus)){

                    // Status has changed. Send an email to the owner and original creator

                    MailInterface mail = new ActionUpdateMail(project, action, creator);

                    if(assignee.exists())
                        mail.sendTo(assignee.getName(), assignee.getEmail());

                    if(creator.exists())
                        mail.sendTo(creator.getName(), creator.getEmail());



                }

            }

            // Update the action count in the fragment

            if(fragment.exists()){

                fragment.setActionCount(getActionCount(fragment));
                fragment.update();
            }

            //Clear the fragment cache

            if(version.exists()){

                PukkaLogger.log(PukkaLogger.Level.INFO, "Clearing cache for document after adding actions");
                invalidateFragmentCache(version);
            }



            JSONObject json = createPostResponse(DataServletName, action);
            sendJSONResponse(json, formatter, resp);


        }catch(BackOfficeException e){

            e.printStackTrace(System.out);
            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        } catch ( Exception e) {

            e.printStackTrace(System.out);
            returnError("Internal Error getting actions", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        }

     }


    /*************************************************************************
     *
     *          GET is not implemented
     *
     * @throws java.io.IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {


        try{

            if(req.getParameter("_method") != null && req.getParameter("_method").equals("DELETE")){
                 doDelete(req, resp);
                 return;
             }

            logRequest(req);

            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);

            DBKeyInterface _project         = getMandatoryKey("project", req);
            Project project = new Project(new LookupByKey(_project));

            if(!mandatoryObjectExists(project, resp))
                return;

            List<Action> actionsForProject = project.getActionsForProject();

            PukkaLogger.log(PukkaLogger.Level.INFO, "Found " + actionsForProject.size() + " actions for project");

            Formatter formatter = getFormatFromParameters(req);

            JSONObject output = new JSONObject();
            JSONArray actionList = new JSONArray();

            for(Action action: actionsForProject){

                JSONObject annotationJSON = new JSONObject()
                        .put("name",            action.getName() )
                        .put("id", action.getKey().toString())
                        .put("fragment", action.getFragmentId().toString())
                        .put("text", action.getDescription())
                        .put("creator", action.getIssuerId().toString())
                        .put("assignee", action.getAssigneeId().toString())
                        .put("status",          action.getStatus().getId())
                        .put("priority",        action.getPriority() )
                        .put("creationDate",    action.getCreated().getISODate())
                        .put("dueDate",         action.getDue().getISODate())
                        .put("completeDate",    action.getCompleted().getISODate());

                actionList.put(annotationJSON);

            }

            output.put(DataServletName, actionList);


            sendJSONResponse(output, formatter, resp);




        }catch(BackOfficeException e){

            e.printStackTrace(System.out);
            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        } catch ( Exception e) {

            e.printStackTrace(System.out);
            returnError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        }

    }


    /**********************************************************************
     *
     *      Deleting an action
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
     *
     *
     *
     */

    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        try{

            logRequest(req);

            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);

            DBKeyInterface _key         = getMandatoryKey("action", req);
            Action action = new Action(new LookupByKey(_key));

            if(!mandatoryObjectExists(action, resp))
                return;

            ContractFragment fragment = action.getFragment();
            ContractVersionInstance version = action.getVersion();

            if(!mandatoryObjectExists(version, resp))
                return;

            Contract document = version.getDocument();

            if(!deletable(document, resp))
                return;

            Formatter formatter = getFormatFromParameters(req);

            // Update the annotation count in the fragment

            fragment.setActionCount(getActionCount(fragment) - 1);
            fragment.update();

            // Now delete

            int priority = (int)action.getPriority();   // Store the priority

            new ActionTable().deleteItem( action );
            Project project = action.getProject();
            List<Action> remainingActions = project.getActionsForProject();
            adjustPriority(priority, remainingActions);

            PukkaLogger.log(PukkaLogger.Level.INFO, "Clearing cache for document " + document.getName() + " after deleting actions");
            invalidateFragmentCache(version);

            JSONObject result = createDeletedResponse(DataServletName, action);
            sendJSONResponse(result, formatter, resp);


        } catch (BackOfficeException e) {

            PukkaLogger.log( e );
            returnError("Error in " + DataServletName, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        } catch (Exception e) {

            PukkaLogger.log( e );
            returnError("Error in " + DataServletName, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
        }


    }

    /*************************************************************
     *
     *      When deleting an action, all actions with higher priority should be updated.
     *
     * @param priority            - priority of deleted action
     * @param remainingActions    - all actions left
     *
     *      //TODO: Optimize with bach update
     */

    private void adjustPriority(int priority, List<Action> remainingActions) {

        for(Action action : remainingActions){

            if(action.getPriority() > priority){

                try {
                    action.setPriority(action.getPriority() - 1);
                    action.update();

                } catch (BackOfficeException e) {

                    PukkaLogger.log( e );
                }
            }

        }
    }


}

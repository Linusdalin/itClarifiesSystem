package actions;

import contractManagement.*;
import dataRepresentation.DBTimeStamp;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pukkaBO.condition.LookupByKey;
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
 *          Converting an annotation to an action
 *
 *          This is a compound service replacing:
 *
 *           DELETE /Annotation
 *           POST /Action
 *
 */

public class ConvertToActionServlet extends ItClarifiesService {

    public static final String DataServletName = "ConvertToAction";

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

            DBKeyInterface _annotation      = getMandatoryKey("annotation", req);
            DBKeyInterface _assignee        = getMandatoryKey("assignee", req);


            // These are optional parameters. Not part of the main convert use case

            long priority               = getOptionalLong("priority", req, -1);
            long _status                = getOptionalLong("status", req, -1);
            DBTimeStamp dueDate         = getOptionalDate("dueDate", req, new DBTimeStamp(DBTimeStamp.NO_DATE, "1900-00-00"));
            DBTimeStamp completedDate   = getOptionalDate("completedDate", req, new DBTimeStamp(DBTimeStamp.NO_DATE, "1900-00-00"));

            PortalUser creator = sessionManagement.getUser();
            Action action;

            ContractAnnotation annotation = new ContractAnnotation(new LookupByKey(_annotation));
            ContractFragment fragment = annotation.getFragment();
            String text = annotation.getDescription();
            String pattern = annotation.getPattern();
            ContractVersionInstance version = fragment.getVersion();
            DBKeyInterface _project = version.getDocument().getProjectId();

            ActionStatus status = ActionStatus.getOpen();
            if(_status != -1){

                status = new ActionStatusTable().getValue((int)_status);
            }



            //Create the new action

            DBTimeStamp creationDate = new DBTimeStamp();
            String name = creator.getName() + "@" + creationDate.getISODate();

            //Default assignee is self

            if(_assignee == null)
                _assignee = creator.getKey();

            action = new Action(
                    0,      // Global id is not used. Placeholder for future addition
                    name,
                    text,
                    pattern,
                    fragment.getKey(),
                    version.getKey(),
                    fragment.getKey(),
                    _project,
                    creator.getKey(),
                    _assignee,
                    priority,
                    status,
                    creationDate.getISODate(),
                    dueDate.getISODate(),
                    completedDate.getISODate());

            action.store();
            annotation.delete();
            PukkaLogger.log(PukkaLogger.Level.INFO, "New Action converted by user " + creator.getName());


            // Update the action count in the fragment

            fragment.setActionCount(getActionCount(fragment));
            fragment.setAnnotationCount(getAnnotationCount(fragment));
            fragment.update();

            //Clear the fragment cache

            PukkaLogger.log(PukkaLogger.Level.INFO, "Clearing cache for document after converting to actions");
            invalidateFragmentCache(version);


            JSONObject json = createPostResponse(DataServletName, action);
            sendJSONResponse(json, formatter, resp);


        }catch(BackOfficeException e){

            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            e.printStackTrace();

        } catch ( Exception e) {

            returnError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            e.printStackTrace();

        }

     }


    /*************************************************************************
     *
     *          GET is not implemented
     *
     * @throws java.io.IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        doPost(req, resp);


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

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();
    }
}

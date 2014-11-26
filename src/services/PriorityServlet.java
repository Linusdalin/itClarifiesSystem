package services;

import actions.Action;
import contractManagement.Contract;
import contractManagement.Project;
import databaseLayer.DBKeyInterface;
import databaseLayer.DatabaseAbstractionFactory;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import pukkaBO.condition.LookupByKey;
import pukkaBO.exceptions.BackOfficeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

/********************************************************
 *
 *          Ordering Servlet
 *
 */

public class PriorityServlet extends DocumentService{

    public static final String DataServletName = "Priority";


    /***********************************************************************************
     *
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
     *
     *
     *              // TODO: Check rwd access to the project
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

            DBKeyInterface _project         = getMandatoryKey("project", req);
            Project project = new Project(new LookupByKey(_project));

            if(!mandatoryObjectExists(project, resp))
                return;

            String jsonList = getMandatoryString("actions", req);
            jsonList = URLDecoder.decode(jsonList, "UTF-8");


            JSONArray actionArray = new JSONArray(jsonList);

            setActionPriority(project, actionArray);

            JSONObject json = new JSONObject();
            sendJSONResponse(json, formatter, resp);


        }catch(BackOfficeException e){

            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            e.printStackTrace();

        } catch ( JSONException e) {

            returnError(e.getMessage(), HttpServletResponse.SC_BAD_REQUEST, resp);
            e.printStackTrace();

        } catch ( Exception e) {

            returnError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            e.printStackTrace();

        }

     }

    /********************************************************************************
     *
     *          Update all actions with the new order
     *
     *          The array is simply an array of objects {"key":"<key>, "priority":prio}
     *
     *
     * @param project - the project affected
     * @param actionArray - array of action ids in the new order
     * @throws pukkaBO.exceptions.BackOfficeException
     *
     *
     *          //TODO: This could be optimized with loading once and a batch update
     */

    private void setActionPriority(Project project, JSONArray actionArray)  throws BackOfficeException{


        for(int i = 0; i < actionArray.length(); i++){

            JSONObject update = actionArray.getJSONObject( i );
            DBKeyInterface key = new DatabaseAbstractionFactory().createKey(update.getString("key"));
            int newPriority = update.getInt("priority");

            Action action = new Action(new LookupByKey(key));

            action.setPriority( newPriority );
            action.update();

        }

    }


    /*************************************************************************
     *
     *
     * @throws java.io.IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        doPost(req, resp);


     }


    /************************************************************************
     *
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
     */


    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();


    }


}

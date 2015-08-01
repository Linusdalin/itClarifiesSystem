package project;

import contractManagement.DocumentSection;
import dataRepresentation.DBTimeStamp;
import dataRepresentation.DataObjectInterface;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import module.Module;
import module.ModuleProject;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;
import services.Formatter;
import services.ItClarifiesService;
import userManagement.AccessRight;
import userManagement.PortalUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/********************************************************
 *
 *          Project Servlet will return one or a list of projects.
 *
 */

public class ProjectTypeServlet extends ItClarifiesService {

    public static final String DataServletName = "ProjectType";

    /*************************************************************************************'
     *
     *
     * @param req      -
     * @param resp     -
     * @throws java.io.IOException
     *
     *
     */


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName , HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();


     }


    /*************************************************************************
     *
     *          Get all project types
     *
     *          Parameters:
     *
     *
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

           Formatter formatter = getFormatFromParameters(req);

           PortalUser user = sessionManagement.getUser();

           List<DataObjectInterface> allTypes = new ProjectTypeTable().getValues();

           JSONArray projectList = new JSONArray();

           for(DataObjectInterface object : allTypes){

               ProjectType projectType = (ProjectType)object;

               JSONObject projectJSON = new JSONObject()
                    .put("id", "\"" + projectType.get__Id() + "\"")
                    .put("name", projectType.getName())
                    .put("description", projectType.getDescription());

                PukkaLogger.log(PukkaLogger.Level.INFO, "*** Project Type: " + projectType.getName());
                projectList.put(projectJSON);
           }

           JSONObject json = new JSONObject()
                   .put(DataServletName, projectList);

           PukkaLogger.log(PukkaLogger.Level.INFO, "Sent " + projectList.length() + " projects for user " +user.getName() + " (organization " + user.getOrganizationId() + ")" );

           sendJSONResponse(json, formatter, resp);


        }catch(BackOfficeException e){

           PukkaLogger.log( e );
           returnError("Error in " + DataServletName, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        } catch ( Exception e) {

           PukkaLogger.log( e );
           returnError("Error in " + DataServletName, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
        }
    }



    /***********************************************************************
     *
     *
     *
     * @param req -
     * @param resp -
     * @throws java.io.IOException
     *
     *
     */

    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName , HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();


    }




}

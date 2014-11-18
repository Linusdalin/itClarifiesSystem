package services;

import dataRepresentation.DBTimeStamp;
import dataRepresentation.DataObjectInterface;
import databaseLayer.DBKeyInterface;
import contractManagement.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pukkaBO.condition.*;
import pukkaBO.export.ValuePair;
import pukkaBO.exceptions.BackOfficeException;
import userManagement.PortalUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/********************************************************
 *
 *          Project Servlet will return one or a list of projects.
 *
 */

public class ProjectServlet extends ItClarifiesService{

    public static final String DataServletName = "Project";

    /*************************************************************************************'
     *
     *          Create a new project or update an existing project
     *
     *
     * @param req
     * @param resp
     * @throws IOException
     *
     *
     */


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

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

            String name            = getMandatoryString("name", req);
            String description     = getMandatoryString("description", req);

            Project project;

            DBKeyInterface _project = getOptionalKey("key", req);

            PortalUser portalUser = sessionManagement.getUser();

            if(!portalUser.getWSAdmin()){

                returnError("Not sufficient access right to create project", ErrorType.PERMISSION, HttpServletResponse.SC_FORBIDDEN, resp);
                return;

            }


            // Now check if the project exists.

            if(_project != null){

                project = new Project(new LookupByKey(_project));

                if(!mandatoryObjectExists(project, resp))
                    return;

                System.out.print("Name=" + name);

                project.setName(name);
                project.setDescription(description);
                project.setKey(_project);
                project.update();

            }
            else{

                // No project parameter given. We create a new project entry

                DBTimeStamp creationTime = new DBTimeStamp();
                project = new Project(name, description, portalUser.getKey(), portalUser.getOrganizationId(), creationTime.getISODate());
                project.store();
            }

            JSONObject json = createPostResponse(DataServletName, project);

            sendJSONResponse(json, formatter, resp);


        } catch (BackOfficeException e) {

            e.logError("Error (POST) in " + DataServletName);
            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            resp.flushBuffer();
        }


     }

    /*************************************************************************
     *
     *          Get projects matching the request criteria
     *
     *          Parameters:
     *
     *          &key=<key> (if left empty, it will return the entire list)
     *
     *
     * @throws IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

       DBKeyInterface key;

       try{
           logRequest(req);

           if(!validateSession(req, resp))
               return;

           if(blockedSmokey(sessionManagement, resp))
               return;

           setLoggerByParameters(req);

           Formatter formatter = getFormatFromParameters(req);

            key             = getOptionalKey("key", req);

           if(!validateSession(req, resp))
               return;

           PortalUser user = sessionManagement.getUser();

           ConditionInterface condition = getLookupConditionForOptionalKey(key);
           condition.addFilter(new ReferenceFilter(ProjectTable.Columns.Organization.name(), user.getOrganizationId()));

           ProjectTable all = new ProjectTable(condition);

           JSONArray projectList = new JSONArray();

           for(DataObjectInterface object : all.getValues()){

               Project project = (Project)object;

               int documentsForProject = project.getContractsForProject().size();

               JSONObject projectJSON = new JSONObject()
                    .put("id", project.getKey().toString())
                    .put("name", project.getName())
                    .put("description", project.getDescription())
                    .put("organization", project.getOrganizationId().toString())
                    .put("owner", project.getCreatorId().toString())
                    .put("creation", project.getCreationTime().getSQLTime().toString())
                    .put("status", "unknown")                       // TODO: Not implemented passing an analysis status
                    .put("noDocs", documentsForProject);

                projectList.put(projectJSON);
           }

           JSONObject json = new JSONObject()
                   .put(DataServletName, projectList);

           sendJSONResponse(json, formatter, resp);

        }catch(BackOfficeException e){

           returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
           e.printStackTrace(System.out);

        } catch ( Exception e) {

           returnError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
           e.printStackTrace(System.out);
        }
    }



    /***********************************************************************
     *
     *          Delete a project with recursively deleting all documents, fragments etc.
     *
     *
     * @param req -
     * @param resp -
     * @throws IOException
     *
     *          //TODO: Delete project should recursively delete all documents
     *          //TODO: Fix return values correctly
     *
     */

    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        DBKeyInterface key;
        try{
            logRequest(req);

            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);

            Formatter formatter = getFormatFromParameters(req);

            key = getMandatoryKey("Key", req);
            Project project = new Project(new LookupByKey(key));

            // Deletable here means that the user owns the project

            if(!deletable(project, resp))
                return;

            project.recursivelyDelete();
            project.delete();

            JSONObject json = createDeletedResponse(DataServletName, project);
            sendJSONResponse(json, formatter, resp);


        }catch(BackOfficeException e){
        
            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            e.printStackTrace();

        }catch ( Exception e) {

            returnError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            e.printStackTrace();

        }

    }




}

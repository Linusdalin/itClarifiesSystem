package services;

import dataRepresentation.DBTimeStamp;
import dataRepresentation.DataObjectInterface;
import databaseLayer.DBKeyInterface;
import contractManagement.*;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pukkaBO.condition.*;
import pukkaBO.export.ValuePair;
import pukkaBO.exceptions.BackOfficeException;
import userManagement.AccessRight;
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

                Project existingProject = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), name)));

                if(existingProject.exists()){

                    returnError("Project with name " + name + "already exists.", ErrorType.DATA, HttpServletResponse.SC_BAD_REQUEST, resp);
                    return;

                }

                ProjectType projectType = ProjectType.getGeneric();    //TODO: Not implemented different project types. Using default
                AccessRight projectAccess = AccessRight.getrwd();      //TODO: Not implemented access rights to project

                DBTimeStamp creationTime = new DBTimeStamp();

                // No project parameter given. We create a new project entry

                project = new Project(name, description, portalUser.getKey(), portalUser.getOrganizationId(), creationTime.getISODate(), projectType,  projectAccess);
                project.store();

                // Create appropriate sections for the type of project
                createSectionsForProject(project, projectType, portalUser, projectAccess, creationTime);
            }

            JSONObject json = createPostResponse(DataServletName, project);

            sendJSONResponse(json, formatter, resp);


        } catch (BackOfficeException e) {

            PukkaLogger.log( e );
            returnError("Error in " + DataServletName, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        } catch (Exception e) {

            PukkaLogger.log( e );
            returnError("Error in " + DataServletName, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
        }


     }

    /**********************************************************************************************************''
     *
     *              Create appropriate sections for a type of project
     *
     *
     * @param project
     * @param projectType
     * @param owner
     * @param projectAccess
     * @param creationTime
     * @throws BackOfficeException
     *
     *              //TODO: Different sections for different types of projects is not implemented. Always use exactly one section
     *
     */

    private void createSectionsForProject(Project project, ProjectType projectType, PortalUser owner, AccessRight projectAccess, DBTimeStamp creationTime) throws BackOfficeException {

        DocumentSection section = new DocumentSection("Unsorted", (long)0, "Unsorted Documents", project.getKey(), owner.getKey(), projectAccess, null, creationTime.getISODate());
        section.store();

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

           PortalUser user = sessionManagement.getUser();

           ConditionInterface condition = getLookupConditionForOptionalKey(key);
           condition.addFilter(new ReferenceFilter(ProjectTable.Columns.Organization.name(), user.getOrganizationId()));

           ProjectTable all = new ProjectTable(condition);

           JSONArray projectList = new JSONArray();

           for(DataObjectInterface object : all.getValues()){

               Project project = (Project)object;
               String projectTypeName = "Generic";
               ProjectType type = project.getType();
               if(type != null)
                   projectTypeName = type.getName();

               int documentsForProject = project.getContractsForProject().size();

               JSONObject projectJSON = new JSONObject()
                    .put("id", project.getKey().toString())
                    .put("name", project.getName())
                    .put("description", project.getDescription())
                    .put("type", projectTypeName)
                    .put("organization", project.getOrganizationId().toString())
                    .put("owner", project.getCreatorId().toString())
                    .put("creation", project.getCreationTime().getSQLTime().toString())
                    .put("status", "unknown")                       // TODO: Not implemented passing an analysis status
                    .put("noDocs", documentsForProject);

                PukkaLogger.log(PukkaLogger.Level.INFO, "*** Project: " + project.getName());
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
     *          Delete a project with recursively deleting all documents, fragments etc.
     *
     *
     * @param req -
     * @param resp -
     * @throws IOException
     *
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
        
            PukkaLogger.log( e );
            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        }catch ( Exception e) {

            PukkaLogger.log( e );
            returnError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        }

    }




}

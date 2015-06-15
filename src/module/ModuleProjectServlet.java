package module;

import contractManagement.DocumentSection;
import contractManagement.Project;
import contractManagement.ProjectTable;
import contractManagement.ProjectType;
import dataRepresentation.DBTimeStamp;
import dataRepresentation.DataObjectInterface;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
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
 *          Assign or get Modules for a project
 *
 */

public class ModuleProjectServlet extends ItClarifiesService {

    public static final String DataServletName = "ModuleForProject";

    /*************************************************************************************'
     *
     *          Create a new module or update an existing
     *
     *
     * @param req            - request
     * @param resp           -response
     * @throws java.io.IOException
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
     * @throws pukkaBO.exceptions.BackOfficeException
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

           DBKeyInterface _project         = getMandatoryKey("project", req);
           Project project = new Project(new LookupByKey(_project));

           if(!mandatoryObjectExists(project, resp))
               return;

           // Check access right

           if(!user.getOrganizationId().equals(project.getOrganizationId())){

                returnError( _project + " not found", ErrorType.DATA,  HttpServletResponse.SC_BAD_REQUEST, resp);
                return;
           }


           List<Module> allModules = project.getModulesForProject();

           JSONArray moduleList = new JSONArray();

           for (Module module : allModules) {

               JSONObject moduleJSON = new JSONObject()
                       .put("name",         module.getName())
                       .put("description",  module.getDescription())
                       .put("key",          module.getKey().toString());

               moduleList.put(moduleJSON);

           }

           JSONObject json = new JSONObject()
                   .put(DataServletName, moduleList);

           PukkaLogger.log(PukkaLogger.Level.INFO, "Sent " + moduleList.length() + " modules for project " +project.getName()  );

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
     * @throws java.io.IOException
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

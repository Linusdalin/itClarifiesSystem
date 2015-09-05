package project;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import dataRepresentation.DBTimeStamp;
import dataRepresentation.DataObjectInterface;
import databaseLayer.DBKeyInterface;
import contractManagement.*;
import databaseLayer.DatabaseAbstractionFactory;
import log.PukkaLogger;
import module.Module;
import module.ModuleProject;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import project.Project;
import project.ProjectTable;
import project.ProjectType;
import project.ProjectTypeTable;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;
import services.Formatter;
import services.ItClarifiesService;
import userManagement.AccessRight;
import userManagement.PortalUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/********************************************************
 *
 *          Project Servlet will return one or a list of projects.
 *
 */

public class ProjectServlet extends ItClarifiesService {

    public static final String DataServletName = "Project";

    /*************************************************************************************'
     *
     *          Create a new project or update an existing project
     *
     *
     *          The parameter empty=true is sent to suppress the assignment of default
     *          modules to the project. It is mostly for legacy reasons
     *
     *
     * @param req      -
     * @param resp     -
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

            String name            = getOptionalString("name", req);
            String description     = getOptionalString("description", req);
            boolean empty          = getOptionalBoolean("empty", req, false);
            String ordering        = getOptionalString("ordering", req);

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

                if(name != null)
                    project.setName(name);

                if(description != null)
                    project.setDescription(description);

                if(ordering != null)
                    setOrdering(ordering, project);

                project.update();

            }
            else{

                Project existingProject = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), name)));

                long      _projectType = getOptionalLong("type", req, -1);

                if(existingProject.exists()){

                    returnError("Project with name " + name + "already exists.", ErrorType.DATA, HttpServletResponse.SC_BAD_REQUEST, resp);
                    return;

                }

                // Now get the project type

                ProjectType projectType = ProjectType.getContracting();    //TODO: Change this to generic when project type is properly passed from frontend

                if(_projectType != -1)
                    projectType = new ProjectTypeTable().getValue((int)_projectType);

                AccessRight projectAccess = AccessRight.getrwd();      //TODO: Not implemented access rights to project

                DBTimeStamp creationTime = new DBTimeStamp();

                // No project parameter given. We create a new project entry

                project = new Project(name, description, portalUser.getKey(), portalUser.getOrganizationId(), creationTime.getISODate(), projectType,  projectAccess);
                project.store();

                // Create appropriate sections for the type of project
                createSectionsForProject(project, projectType, portalUser, projectAccess, creationTime);
                if(!empty)
                    createModulesForProject(project, projectType, creationTime);
            }

            JSONObject json = createPostResponse(DataServletName, project);

            sendJSONResponse(json, formatter, resp);


        } catch (BackOfficeException e) {

            PukkaLogger.log( e );
            returnError("Error in " + DataServletName + e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        } catch (Exception e) {

            PukkaLogger.log( e );
            returnError("Error in " + DataServletName, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
        }


    }

    /*************************************************************************
     *
     *              Set the ordering in the project given the ordering json array
     *
     *              Example: [  {"key": "<key>", "ordinal": number},
     *                          {"key": "<key>", "ordinal": number} ]
     *
     *
     * @param ordering   - JSON Array
     * @param project    - project to rearrange
     */

    private boolean setOrdering(String ordering, Project project) throws BackOfficeException {

        try{
            DatabaseAbstractionFactory dbFactory = new DatabaseAbstractionFactory();

            JSONArray array = new JSONArray(ordering);
            for(int i = 0; i < array.length(); i++){

                JSONObject doc = array.getJSONObject( i );

                DBKeyInterface _document = dbFactory.createKey(doc.getString("key"));
                int ordinal = doc.getInt("ordinal");

                Contract document = new Contract(new LookupByKey( _document));
                document.setOrdinal( ordinal );
                document.update();

                PukkaLogger.log(PukkaLogger.Level.INFO, "Setting ordinal "+ ordinal + " for document " + document.getName());

                invalidateDocumentCache(document, project);

            }

            return true;

        }catch(Exception e){

            PukkaLogger.log( e );
            return false;

        }

    }

    /*********************************************************************************
     *
     *          Create default modules for the project
     *
     *          The available default modules are:
     *
     *           - itclarifies contracting
     *           - risk
     *           -definition analysis
     *
     *          and they are assigned automatically to a project of type "Contracting"
     *
     *
     * @param project              - the project under creation
     * @param projectType          - the type of the projects to determine which default modules do we need
     * @param creationTime         - time of creation (same as for project)
     *
     *
     */

    private void createModulesForProject(Project project, ProjectType projectType, DBTimeStamp creationTime){


        try {

            if(projectType.get__Id() == ProjectType.getContracting().get__Id()){

                ModuleProject default1 = new ModuleProject("Default Assignment", Module.getContracting().getKey(), project.getKey(), creationTime.getISODate());
                default1.store();
                ModuleProject default2 = new ModuleProject("Default Assignment", Module.getRisk().getKey(), project.getKey(), creationTime.getISODate());
                default2.store();
                ModuleProject default3 = new ModuleProject("Default Assignment", Module.getDefinitions().getKey(), project.getKey(), creationTime.getISODate());
                default3.store();

            }


        } catch (BackOfficeException e) {

            PukkaLogger.swallow( e );        // For now swallow this. Before the modules are deployed to the server, this will fail.

        } catch (Exception e) {

            PukkaLogger.log( e );
        }


    }

    /**********************************************************************************************************''
     *
     *              Create appropriate sections for a type of project
     *
     *              NOTE: Sections are not implemented
     *
     * @param project              - project under creation
     * @param projectType          - type of project (to define which sections are appropriate)
     * @param owner                - owner of the project will also be owner of the sections
     * @param projectAccess        - access will be inherited by the section from the project
     * @param creationTime         - creation time (same as for the project
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

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
import userManagement.Organization;
import userManagement.PortalUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/********************************************************
 *
 *          Assign or get Modules for a project
 *
 */

public class ModuleOrganizationServlet extends ItClarifiesService {

    public static final String DataServletName = "ModuleProject";

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

            DBKeyInterface _organization = getMandatoryKey("key", req);
            DBKeyInterface _module = getMandatoryKey("key", req);

            Module module;
            Organization organization;

            PortalUser portalUser = sessionManagement.getUser();

            if(!portalUser.getWSAdmin()){

                returnError("Not sufficient access right to add modules to organization", ErrorType.PERMISSION, HttpServletResponse.SC_FORBIDDEN, resp);
                return;

            }


            organization = new Organization(new LookupByKey(_organization));

            if(!mandatoryObjectExists(organization, resp))
                return;

            module = new Module(new LookupByKey(_module));

            if(!mandatoryObjectExists(module, resp))
                return;

            DBTimeStamp now = new DBTimeStamp();


            ModuleOrganization access = new ModuleOrganization("" + module.getName() + "->" + organization.getName(), now.toString(), organization, module, portalUser);

            JSONObject json = createPostResponse(DataServletName, access);

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

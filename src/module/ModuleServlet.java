package module;

import contractManagement.DocumentSection;
import project.Project;
import project.ProjectType;
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
import java.util.List;

/********************************************************
 *
 *          Module Servlet will return a list of modules for a given user-organization
 *
 */

public class ModuleServlet extends ItClarifiesService {

    public static final String DataServletName = "Module";

    /*************************************************************************************'
     *
     *          Create a new module or update an existing
     *
     *          (Man) name              - unique name
     *          (Man) description       - free text description
     *          (Opt) public            - should the module be visible (default yes
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
            boolean isPublic       = getOptionalBoolean("public", req, true);

            System.out.println("Public = " + isPublic);

            Module module;

            DBKeyInterface _module = getOptionalKey("key", req);
            DBTimeStamp creationTime = new DBTimeStamp();

            PortalUser portalUser = sessionManagement.getUser();

            if(!portalUser.getWSAdmin()){

                returnError("Not sufficient access right to create module", ErrorType.PERMISSION, HttpServletResponse.SC_FORBIDDEN, resp);
                return;

            }


            // Now check if the project exists.

            if(_module != null){

                module = new Module(new LookupByKey(_module));

                if(!mandatoryObjectExists(module, resp))
                    return;

                System.out.print("Name=" + name);

                module.setName(name);
                module.setDescription(description);
                module.update();

            }
            else{

                Module existingModule = new Module(new LookupItem().addFilter(new ColumnFilter(ModuleTable.Columns.Name.name(), name)));

                if(existingModule.exists()){

                    returnError("Module with name " + name + "already exists.", ErrorType.DATA, HttpServletResponse.SC_BAD_REQUEST, resp);
                    return;

                }

                module = new Module(name, description, isPublic);
                module.store();

                if(!isPublic){

                    // Not public, we need to add access rights specifically for the organization for which it is created

                    String accessName = name + "@" + creationTime.getISODate();


                    ModuleOrganization access = new ModuleOrganization(accessName, creationTime.toString(), portalUser.getOrganizationId(), module.getKey(), portalUser.getKey());
                    access.store();


                }


                PukkaLogger.log(PukkaLogger.Level.MAJOR_EVENT, "Created a new "+(isPublic ? "public" : "")+" nodule \"" + module.getName() + "\"");

            }

            JSONObject json = createPostResponse(DataServletName, module);

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
     *          Get modules for a user
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
           Organization organization = user.getOrganization();

           List<Module> modulesFrOrganization = organization.getModulesForOrganization();
           PukkaLogger.log(PukkaLogger.Level.DEBUG, "There are " + modulesFrOrganization.size() + " modules for the organization");

           ModuleTable allModules = new ModuleTable(new LookupList());  // Get all

           for (DataObjectInterface item : allModules.getValues()) {

               Module module = (Module)item;

               if(module.getisPublic() && !exists(module, modulesFrOrganization)){

                   PukkaLogger.log(PukkaLogger.Level.DEBUG, "Added common module " + module.toString());
                   modulesFrOrganization.add(module);
               }

           }

           JSONArray moduleList = new JSONArray();

           for (Module module : modulesFrOrganization) {

               JSONObject moduleJSON = new JSONObject()
                       .put("name",         module.getName())
                       .put("description", module.getDescription())
                       .put("key",          module.getKey().toString())
                       .put("public",       module.getisPublic());

               moduleList.put(moduleJSON);

           }

           JSONObject json = new JSONObject()
                   .put(DataServletName, moduleList);

           PukkaLogger.log(PukkaLogger.Level.INFO, "Sent " + moduleList.length() + " modules for user " +user.getName() + " (organization " + organization.getName() + ")" );

           sendJSONResponse(json, formatter, resp);


        }catch(BackOfficeException e){

           PukkaLogger.log( e );
           returnError("Error in " + DataServletName, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        } catch ( Exception e) {

           PukkaLogger.log( e );
           returnError("Error in " + DataServletName, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
        }
    }

    private boolean exists(Module module, List<Module> modulesFrOrganization) {

        for (Module existingModule : modulesFrOrganization) {

            if(module.equals(existingModule))
                return true;
        }

        return false;
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

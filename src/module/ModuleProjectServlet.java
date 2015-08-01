package module;

import project.Project;
import dataRepresentation.DBTimeStamp;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pukkaBO.condition.*;
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
 *          Assign or get Modules for a project
 *
 */

public class ModuleProjectServlet extends ItClarifiesService {

    public static final String DataServletName = "ModuleForProject";

    /*************************************************************************************'
     *
     *          Assign a module to a project
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

            DBKeyInterface _project = getOptionalKey("project", req);
            DBKeyInterface _module = getOptionalKey("module", req);


            PortalUser portalUser = sessionManagement.getUser();

            if(!portalUser.getWSAdmin()){

                returnError("Not sufficient access right to add modules to project", ErrorType.PERMISSION, HttpServletResponse.SC_FORBIDDEN, resp);
                return;

            }

            Project project = new Project(new LookupByKey(_project));
            Module module = new Module(new LookupByKey(_module));
            DBTimeStamp creationTime = new DBTimeStamp();

            if(!mandatoryObjectExists(project, resp))
                return;

            if(!mandatoryObjectExists(module, resp))
                return;

            // Create the access object

            ModuleProject existingAccess = new ModuleProject(new LookupItem()
                    .addFilter(new ReferenceFilter(ModuleProjectTable.Columns.Module.name(), _module))
                    .addFilter(new ReferenceFilter(ModuleProjectTable.Columns.Project.name(), _project)));

            if(!existingAccess.exists()){

                String name = portalUser.getName() + "@" + creationTime;
                ModuleProject access = new ModuleProject(name, _module, _project, creationTime.toString());
                access.store();

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


    /*************************************************************************
     *
     *          Get modules for a given project
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
                       .put("name", module.getName())
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
     *          Delete access to a module for a project
     *
     *          As the moduleProject is a secondary data structure, this is not
     *          done through a regular key, but rather by passing
     *
     *           - project
     *           - module
     *
     * @param req -
     * @param resp -
     * @throws java.io.IOException
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

            Formatter formatter = getFormatFromParameters(req);

            DBKeyInterface _project = getOptionalKey("project", req);
            DBKeyInterface _module = getOptionalKey("module", req);


            PortalUser portalUser = sessionManagement.getUser();

            if(!portalUser.getWSAdmin()){

                returnError("Not sufficient access right to delete modules to project", ErrorType.PERMISSION, HttpServletResponse.SC_FORBIDDEN, resp);
                return;

            }

            Project project = new Project(new LookupByKey(_project));
            Module module = new Module(new LookupByKey(_module));

            if(!mandatoryObjectExists(project, resp))
                return;

            if(!mandatoryObjectExists(module, resp))
                return;

            // Get the access object and delete it

            ModuleProject access = new ModuleProject(new LookupItem()
                    .addFilter(new ReferenceFilter(ModuleProjectTable.Columns.Module.name(), _module))
                    .addFilter(new ReferenceFilter(ModuleProjectTable.Columns.Project.name(), _project)));

            if(access.exists())
                access.delete();

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

}

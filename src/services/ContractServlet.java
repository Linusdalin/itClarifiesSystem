package services;

import backend.ItClarifies;
import cache.ServiceCache;
import contractManagement.*;
import dataRepresentation.DataObjectInterface;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;
import userManagement.AccessGrant;
import userManagement.AccessRight;
import userManagement.Organization;
import userManagement.PortalUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/********************************************************
 *
 *          Contract Servlet returning one contract
 *
 */

public class ContractServlet extends DocumentService{

    public static final String DataServletName = "Document";

    public static final BackOfficeInterface backOffice = new ItClarifies();

    /***********************************************************************************
     *
     *      Uploading a document
     *
     * @param req
     * @param resp
     * @throws IOException
     */


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Post not supported in Document", HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

     }



    /*************************************************************************
     *
     *          Get all documents for a project (or matching other request criteria)
     *
     *          Parameters:
     *
     *          &key=<key>      (if left empty, it will return the entire list of contracts)
     *          &project=<key>  return all the documents for a specific project(cant be empty)
     *
     *
     * @throws IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        ConditionInterface condition;

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

           DBKeyInterface key                = getOptionalKey("key", req);
           DBKeyInterface _project           = getOptionalKey("project", req);


           if(key == null)
               condition = new LookupList();
           else
               condition = new LookupByKey(key);

           Project project = null;

           if(_project != null){

               // Add the project as a condition

               project = new Project(new LookupByKey(_project));

               if(!mandatoryObjectExists(project, resp))
                   return;

               PukkaLogger.log(PukkaLogger.Level.INFO, "Accessing Project " + project.getName());

               // Only show documents for the project

               condition.addFilter(new ReferenceFilter(ContractTable.Columns.Project.name(), project.getKey()));
               condition.addOrdering(ContractTable.Columns.Ordinal.name(), Ordering.FIRST);

           }


           //First check the cache

           ServiceCache cache = new ServiceCache(DataServletName, user);   // Qualified on user
           String cacheKey = _project.toString();

           String jsonString= cache.lookup(cacheKey);
           JSONObject jsonObject;

           if(jsonString == null){

                // Nothing in the cache

               ContractTable all = new ContractTable(condition);
               JSONArray documentList = new JSONArray();
               Organization organization = user.getOrganization();

               for(DataObjectInterface object : all.getValues()){

                   Contract contract = (Contract)object;

                   AccessRight access = sessionManagement.getAccess(contract);

                   //AccessGrant grant = sessionManagement.getGrantForDocument(contract);
                   //AccessRight right = grant.getAccessRight();
                   //String accessName = "ro";

                   //if(right.exists())
                   //    accessName = right.getName();

                   if(sessionManagement.getReadAccess(contract)){

                       JSONObject document = new JSONObject()
                            .put("id", contract.getKey().toString())
                            .put("file", encodeToJSON(contract.getFile()))
                            .put("name", encodeToJSON(contract.getName()))
                            .put("project", contract.getProject().getName())
                            .put("status", contract.getStatus().getName())
                            .put("message", contract.getMessage())
                            .put("description", contract.getMessage())               // TODO: This should be a separate analysis feedback
                            .put("owner", contract.getOwnerId().toString())
                            .put("creation", contract.getCreation().getSQLTime().toString())
                            .put("visibility", "org")
                            .put("access", access.getName());

                       documentList.put(document);
                       PukkaLogger.log(PukkaLogger.Level.INFO, "Adding " + contract.getName() + " to document list.");
                   }
                   else{

                       PukkaLogger.log(PukkaLogger.Level.INFO, "Hiding " + contract.getName() + " from " + user.getName() + ". No read access");
                   }

               }

               jsonObject = new JSONObject().put(DataServletName, documentList);

               cache.store(cacheKey, jsonObject.toString(), "Project:" + project);

           }else{

               jsonObject = new JSONObject(jsonString);
           }

           sendJSONResponse(jsonObject, formatter, resp);

       }catch(BackOfficeException e){

           e.printStackTrace(System.out);
           returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

       } catch ( Exception e) {

           e.printStackTrace(System.out);
           returnError(e.getLocalizedMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

       }
    }


    /************************************************************************
     *
     *          Delete document will recursivly remove all
     *
     *            - instances
     *            - clauses
     *            - fragments
     *
     *
     * @param req
     * @param resp
     * @throws IOException
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

           DBKeyInterface key                = getMandatoryKey("key", req);

           Contract contract = new Contract(new LookupByKey(key));

           if(!deletable(contract, resp))
               return;

           DocumentDeleteOutcome outcome = contract.recursivelyDeleteDocument();

           JSONObject output = new JSONObject()
                 .put("deleted", new JSONObject()
                    .put("instances", outcome.versions)
                    .put("clauses", outcome.clauses)
                    .put("fragments", outcome.fragments)
                    .put("annotations", outcome.annotations)
                    .put("flags", outcome.riskFlags)
                    .put("classifications", outcome.classifications)
                    .put("keywords", outcome.keywords)
                    .put("references", outcome.references));

           invalidateDocumentCache(contract, contract.getProject());

           sendJSONResponse(output, formatter, resp);

       }catch(BackOfficeException e){

           e.printStackTrace(System.out);
           returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

       } catch ( Exception e) {

           e.printStackTrace(System.out);
           returnError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

       }

    }


}

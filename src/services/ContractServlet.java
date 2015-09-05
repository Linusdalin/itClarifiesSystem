package services;

import backend.ItClarifies;
import cache.ServiceCache;
import contractManagement.*;
import dataRepresentation.DataObjectInterface;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import project.Project;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;
import userManagement.AccessRight;
import userManagement.Organization;
import userManagement.PortalUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

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
     *      Updating document properties
     *
     *          - ordinal (will put the document on the desired position and shift documents down)
     *
     *      NOTE: This does NOT upload a document. For this there is a separate FileUploadServlet
     *      NOTE: The service will pass the same key back
     *
     * @param req       -
     * @param resp      -
     * @throws IOException
     *
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
            boolean isUpdated = false;

            Formatter formatter = getFormatFromParameters(req);

            PortalUser user = sessionManagement.getUser();

            DBKeyInterface key                = getMandatoryKey("key", req);
            long ordinal                      = getOptionalLong("ordinal", req, -1);

            Contract document = new Contract(new LookupByKey(key));
            Project project = document.getProject();
            List<Contract> allDocuments = project.getContractsForProject();

            if(!mandatoryObjectExists(document, resp))
                return;

            if(!sessionManagement.getRenameDeleteAccess(document)){

                returnError("Not sufficient access to move document", HttpServletResponse.SC_FORBIDDEN, resp);
                return;
            }

            if(ordinal != -1){

                if(ordinal < 0 || ordinal > allDocuments.size()){

                    returnError("Could not set ordinal " + ordinal + " for document " + document.getName() + ". Range( 0.." + allDocuments.size() + ")", HttpServletResponse.SC_FORBIDDEN, resp);
                    return;
                }

                //Update the ordinal for this document and others

                moveDocument(document, ordinal, allDocuments);
                isUpdated = true;

            }

            // Now check if there are any updates done. If not the request may be wrong


            if(!isUpdated){

                returnError("No update parameters given", HttpServletResponse.SC_BAD_REQUEST, resp);
                return;
            }

            JSONObject jsonObject = createPostResponse(DataServletName, document);
            sendJSONResponse(jsonObject, formatter, resp);


    }catch(BackOfficeException e){

        e.printStackTrace(System.out);
        returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

    } catch ( Exception e) {

        e.printStackTrace(System.out);
        returnError(e.getLocalizedMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

    }


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
           DBKeyInterface _project           = getMandatoryKey("project", req);


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

                   Contract document = (Contract)object;

                   AccessRight access = sessionManagement.getAccess(document);
                   ContractVersionInstance version = document.getHeadVersion();

                   //AccessGrant grant = sessionManagement.getGrantForDocument(contract);
                   //AccessRight right = grant.getAccessRight();
                   //String accessName = "ro";

                   //if(right.exists())
                   //    accessName = right.getName();

                   if(sessionManagement.isReadAccess(access)){

                       JSONObject documentJSON = new JSONObject()
                            .put("id", document.getKey().toString())
                            .put("file", encodeToJSON(document.getFile()))
                            .put("name", encodeToJSON(document.getName()))
                            .put("project", document.getProject().getName())
                            .put("status", document.getStatus().getName())
                            .put("message", document.getMessage())
                            .put("analysis", document.getAnalysisDetails())
                            .put("description", document.getMessage())
                            .put("owner", document.getOwnerId().toString())
                            .put("creation", document.getCreation().getSQLTime().toString())
                            .put("updated", version.getCreation().getSQLTime().toString())
                            .put("visibility", "org")
                            .put("ordinal", document.getOrdinal())
                            .put("fingerprint", version.getFingerprint())
                            .put("access", access.getName());

                       documentList.put(documentJSON);
                       PukkaLogger.log(PukkaLogger.Level.INFO, "Adding " + document.getName() + " to document list.");
                   }
                   else{

                       PukkaLogger.log(PukkaLogger.Level.INFO, "Hiding " + document.getName() + " from " + user.getName() + ". No read access");
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

           DocumentDeleteOutcome outcome = contract.recursivelyDeleteDocument( true );

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

    /*********************************************************************************
     *
     *              Updating the ordinal and rippling the update to all documents between the positions
     *
     *              We either shift the documents up or down, depending on if we
     *              are moving documents up or down
     *
     *
     *              Examples
     *
     *                      down from 2 to 5                    up from 5 to 2
     *                      1          1    1                   1        1    1
     *                      2   -> 5   2    5                   2        3    3
     *                      3          2    2                   3        4    4
     *                      4          3    3                   4        5    5
     *                      5          4    4                   5 -> 2   5    2
     *                      6          6    6                   6        6    6
     *
     * @param document          - the document to move
     * @param position           - the new position
     * @param allDocuments      - list of documents
     */


    private void moveDocument(Contract document, long position, List<Contract> allDocuments) {

        try{

            long oldPosition = document.getOrdinal();

            if(oldPosition < position){

                // We are moving the document down. This means that we should shift documents up

                for (Contract aDocument : allDocuments) {

                    if(aDocument.getOrdinal() > oldPosition && aDocument.getOrdinal() <= position)
                        aDocument.setOrdinal( aDocument.getOrdinal() - 1);

                    aDocument.update();
                }

            }
            else{

                // We are moving the document up, shifting other documents up

                for (Contract aDocument : allDocuments) {

                    if(aDocument.getOrdinal() >= position && aDocument.getOrdinal() < oldPosition)
                        aDocument.setOrdinal( aDocument.getOrdinal() + 1);

                    aDocument.update();
                }

            }

            document.setOrdinal(position);
            document.update();

        }catch(BackOfficeException e){

            PukkaLogger.log( e );

        }

    }




}

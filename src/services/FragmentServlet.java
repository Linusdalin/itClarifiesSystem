package services;

import backend.ItClarifies;
import cache.ServiceCache;
import contractManagement.*;
import crossReference.Reference;
import dataRepresentation.DataObjectInterface;
import databaseLayer.DBKeyInterface;
import document.CellInfo;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.*;

import pukkaBO.exceptions.BackOfficeException;
import userManagement.SessionManagement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/********************************************************
 *
 *          Fragment Servlet, retrieving a list of fragments or updating a single fragment
 *
 *          Fragments can be retrieved for a specific version instance or a document (in which
 *          case the latest (HEAD) version is used.
 *
 * //TODO: Add post and delete test cases
 */

public class FragmentServlet extends ItClarifiesService{

    public static final String DataServletName = "Fragment";

    public static final BackOfficeInterface backOffice = new ItClarifies();
    private static final int TAB_WIDTH = 20;  //2x 20  px indentation

    public void init(){

        super.init(DataServletName);


    }

    /*****************************************************************************
     *
     *      Updating an existing fragment
     *
     *      parameters to set for updating (all keys):
     *
     *          - clause
     *          - classification
     *          - type
     *
     * @param req -
     * @param resp -
     * @throws IOException
     *
     *       NOTE: Updating a fragment takes the fragment as a parameter
     */


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {


        try{

            logRequest(req);

            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);


            Formatter formatter = getFormatFromParameters(req);

            DBKeyInterface _fragment = getMandatoryKey("fragment", req);
            ContractFragment fragment= new ContractFragment(new LookupByKey(_fragment));

            ServiceCache cache = new ServiceCache(DataServletName);

            if(!mandatoryObjectExists(fragment, resp))
                return;

            // Parameters for updating

            long structureId                  = getOptionalLong("structure", req, -1);
            String  type                     = getOptionalString("type", req);
            String _indentation              = getOptionalString("indentation", req);
            String _annotations              = getOptionalString("annotations", req);
            String text                      = getOptionalString("text", req);

            PukkaLogger.log(PukkaLogger.Level.INFO, "Annotation = " + _annotations);

            boolean update = false;

            if(structureId != -1){

                // TODO: This is not checked. Lookup the structure id to see that it exists in the document

                fragment.setStructureNo(structureId);
                update = true;
            }

            if(type != null){

                fragment.setType(type);
                update = true;

            }

            if(_indentation != null){

                long indentation;

                try{

                    indentation = new Long(_indentation);
                    fragment.setIndentation(indentation);
                    update = true;

                }catch (Exception e){

                    returnError("Error in getting indentation." +_indentation + " is not a number", HttpServletResponse.SC_BAD_REQUEST, resp);
                    resp.flushBuffer();
                    return;

                }


            }

            if(_annotations != null){

                long annotationCount;

                try{

                    annotationCount = new Long(_annotations);
                    fragment.setAnnotationCount(annotationCount);
                    update = true;

                }catch (Exception e){

                    returnError("Error in getting indentation." +_indentation + " is not a number", HttpServletResponse.SC_BAD_REQUEST, resp);
                    resp.flushBuffer();
                    return;

                }


            }

            if(text != null){

                text = text.replace("\n", " ");  // No newlines
                text = text.trim();
                fragment.setText(text);
                update = true;
            }


            if(update)
                fragment.update();

            // Clear cache for the fragment service for this document

            cache.remove(fragment.getVersion().getDocumentId().toString());

            JSONObject json = createPostResponse(DataServletName, fragment);
            sendJSONResponse(json, formatter, resp);

        }catch(BackOfficeException e){

            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            e.printStackTrace();

        } catch ( Exception e) {

            returnError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            e.printStackTrace();

        }

     }


    /*************************************************************************
     *
     *          Get all fragment for a project (or matching other request criteria)
     *
     *          Parameters:
     *
     *          &key=<key>      (if left empty, it will return the entire list of contracts)
     *          &document=<key>  return all the fragments for the document
     *
     *
     *          This method traverses the data structure, getting all documents fro a project, all clauses
     *          for a document and all fragments for the clauses, finally adding them all.
     *
     *
     * @throws java.io.IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {


       try{

           //PukkaLogger.log(PukkaLogger.Level.INFO, "Starting service");

           logRequest(req);

           if(!validateSession(req, resp))
               return;

           PukkaLogger.log(PukkaLogger.Level.DEBUG, "Validated session");

           if(blockedSmokey(sessionManagement, resp))
               return;

           setLoggerByParameters(req);

           Formatter formatter = getFormatFromParameters(req);


           if(req.getParameter("_method") != null && req.getParameter("_method").equals("DELETE")){
               doDelete(req, resp);
               return;
           }

           JSONObject json;
           ServiceCache cache = new ServiceCache(DataServletName);

           // Get either a document or a version instance

           DBKeyInterface _document = getOptionalKey("document", req);
           ContractVersionInstance activeVersion;

           //PukkaLogger.log(PukkaLogger.Level.INFO, "Looking for document");
           Contract document;

           if(_document != null){

               //PukkaLogger.log(PukkaLogger.Level.INFO, "document 1");
               document = new Contract(new LookupByKey(_document));

               //PukkaLogger.log(PukkaLogger.Level.INFO, "document 2");

               if(!mandatoryObjectExists(document, resp))
                   return;

               //PukkaLogger.log(PukkaLogger.Level.INFO, "document 3");

               activeVersion = document.getHeadVersion();

               //PukkaLogger.log(PukkaLogger.Level.INFO, "document 4");

               if(!mandatoryObjectExists(activeVersion, resp))
                   return;

               //PukkaLogger.log(PukkaLogger.Level.INFO, "document 5");

           }else{

               // No document parameter. Then we look for a version
               DBKeyInterface _version = getMandatoryKey("version", req);
               activeVersion = new ContractVersionInstance(new LookupByKey(_version));

               document = activeVersion.getDocument();

               if(!mandatoryObjectExists(document, resp))
                   return;

               if(!mandatoryObjectExists(activeVersion, resp))
                   return;

           }
           //PukkaLogger.log(PukkaLogger.Level.INFO, "Ready document");

           String cacheJSON = cache.lookup(activeVersion.getKey().toString());

           //PukkaLogger.log(PukkaLogger.Level.INFO, "Done looking in cache");

           if(cacheJSON != null){

               //PukkaLogger.log(PukkaLogger.Level.INFO, "Start creating json");
               json = new JSONObject(cacheJSON);
               //PukkaLogger.log(PukkaLogger.Level.INFO, "Ready creating json");

           }else{

               json = new JSONObject()
                       .put(DataServletName, getFragmentsForDocumentVersion(document, activeVersion, sessionManagement, formatter));
               cache.store(activeVersion.getKey().toString(), json.toString(), "Document " + document.getName());
           }

           sendJSONResponse(json, formatter, resp);

       }catch(BackOfficeException e){

           e.printStackTrace(System.out);
           returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

       } catch ( Exception e) {

           e.printStackTrace(System.out);
           returnError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

       }
    }


    /**************************************************************************************
     *
     *          get all fragments for a document sorted on the ordinal number
     *
     *
     *
     * @param document
     * @param version - the document to search for fragments in
     * @param session
     * @param jsonExport  @return
     * @throws BackOfficeException
     *
     *
     */


    private JSONArray getFragmentsForDocumentVersion(Contract document, ContractVersionInstance version, SessionManagement session, Formatter jsonExport)throws BackOfficeException{

        JSONArray fragmentList = new JSONArray();

        try{

            // Check access

            if(!session.getReadAccess(document))
                return fragmentList;

            // Get all the fragments

            //PukkaLogger.log(PukkaLogger.Level.INFO, "Before db access");
            List<ContractFragment> allFragments = version.getFragmentsForVersion(new LookupList().addOrdering(ContractFragmentTable.Columns.Ordinal.name(), Ordering.FIRST));
            //PukkaLogger.log(PukkaLogger.Level.INFO, "After db access");

            // Get all structure Items and convert it to an array for direct lookup

            //PukkaLogger.log(PukkaLogger.Level.INFO, "Before db access2");
            StructureItem[] structureItems = version.getStructureItemsForVersionAsArray(new LookupList().addOrdering(ContractFragmentTable.Columns.Ordinal.name(), Ordering.FIRST));


            List<Reference> references = version.getReferencesForVersion();

            String contractName = encodeToJSON(document.getName());

            String documentKey = document.getKey().toString();
            String projectKey = document.getProjectId().toString();

            PukkaLogger.log(PukkaLogger.Level.INFO, "Found " + allFragments.size() + " fragments for document " + contractName);

            JSONObject rowObject = null;

            //PukkaLogger.log(PukkaLogger.Level.INFO, "Before json loop");

            for(ContractFragment fragment : allFragments){

                String body = getIndentedText(fragment);

                //PukkaLogger.log(PukkaLogger.Level.INFO, "Got fragment " + fragment.getText().substring(0, (fragment.getText().length() > 40 ? 40 : fragment.getText().length())) + "...");

                //System.out.println("Fragment risk: " + fragment.getRisk());

                JSONObject fragmentJSON = new JSONObject()
                    .put("id",              fragment.getKey().toString())
                    .put("ordinal",         fragment.getOrdinal())
                    .put("text",            encodeToJSON(body))
                    .put("document", documentKey)
                    .put("project",         projectKey)

                    .put("annotations", fragment.getAnnotationCount())
                    .put("actions",         fragment.getActionCount())
                    .put("classifications", fragment.getClassificatonCount())
                    .put("references", getReferencesForFragment(fragment, references))
                    .put("type",            fragment.getType())
                    .put("risk",            "" + fragment.getRisk().getId())
                    .put("display",         getDisplayInfo(fragment))
                ;

                fragmentList.put(fragmentJSON);

            }

            PukkaLogger.log(PukkaLogger.Level.DEBUG, "Created fragment json");
            return fragmentList;

        }catch(JSONException e){

            PukkaLogger.log(PukkaLogger.Level.FATAL, "Error creating fragment list. So far: " + fragmentList.toString());
            throw new BackOfficeException(BackOfficeException.General, "Error encoding to JSON " + e.getMessage());
        }

    }

    private String getIndentedText(ContractFragment fragment) {

        long indentation = fragment.getIndentation();
        if(indentation > 10)
            indentation = 10;


        if(!fragment.getType().equals("LISTITEM") && !fragment.getType().equals("TEXT")){

            System.out.println("Not a list or text fragment ("+fragment.getType() +"), no indentation");
            return fragment.getText();
        }

        if(fragment.getText().equals("TEXT")){

            indentation += 1;   // One more for text to compensate for the bullet symbol
        }


        if(indentation <= 2)
            indentation = 0;
        else
            indentation -= 2;       // Remove two if possible. For the headline and the implicit

        long width = indentation * TAB_WIDTH;

        return (width > 0 ? "<span style=\"display: inline-block; width: "+ width + "px;\">&nbsp;</span>" : "" ) + fragment.getText();
    }

    /************************************************************************************************'
     *
     *          Create a jsonArray for all references for the fragment
     *
     *
     * @param fragment
     * @param references
     * @return
     */

    private JSONArray getReferencesForFragment(ContractFragment fragment, List<Reference> references) {

        JSONArray refrenceJSON = new JSONArray();

        for (Reference reference : references) {

            if(reference.getFromId().equals(fragment.getKey()))
                refrenceJSON.put(new JSONObject()
                        .put("to", reference.getToId().toString())
                        .put("pattern", reference.getPattern())
                        .put("type", reference.getType().getName())

                );
        }


        return refrenceJSON;
    }

    /**************************************************************************************
     *
     *      Merge the fragment information with the compound information of the row.
     *
     *      This is only for as long as there is no display functionality for tables on the frontend.
     *
     * @param rowObject
     * @param fragment
     * @return
     */

    private JSONObject merge(JSONObject rowObject, JSONObject fragment) {

        if(rowObject == null)
            return fragment;


        //TODO: Handle fragment details too. As it is now, the details will be lost for the other fragments in the row

        JSONObject mergedJSON = new JSONObject()
            .put("id",              rowObject.getString("id"))
            .put("ordinal",         rowObject.getInt("ordinal"))
            .put("text",            rowObject.getString("text") + " &nbsp; &nbsp; &nbsp; &nbsp; " + fragment.getString("text"))
            .put("document",        rowObject.getString("document"))
            .put("project",         rowObject.getString("project"))

            .put("annotations",     rowObject.getInt("annotations") + fragment.getInt("annotations"))
            .put("classifications",     rowObject.getInt("classifications") + fragment.getInt("classifications"))
            .put("references",     rowObject.getInt("references") + fragment.getInt("references"))
            .put("type",            rowObject.getString("type"))
            .put("risk",            rowObject.getString("risk"))
            .put("display",         rowObject.getJSONObject("display"))
        ;


        return mergedJSON;
    }


    private boolean isNewRow(ContractFragment fragment) {

        boolean isNew = (fragment.getxPos() == 0);

        if(!isNew)
            System.out.println("Found continuation row for fragment " + fragment.getName());

        return isNew;
    }

    /**************************************************************************************
     *
     *              Create a representation of the display info
     *
     *               - type: the type of the fragment
     *               - structure head: The top fragment for the collapse/expand pivot
     *               - indentation: Indentation for the
     *               - row: for table
     *               - column: for table
     *
     *              //TODO: Optimization: Retrieving structure head could be pre-stored
     *
     *
     * @param fragment
     * @return
     * @throws BackOfficeException
     */

    private JSONObject getDisplayInfo(ContractFragment fragment) throws BackOfficeException {

        JSONObject displayInfo = new JSONObject()
                .put("type", fragment.getType())
                .put("structurehead", fragment.getStructureNo())
                .put("indentation", fragment.getIndentation())
                .put("row", fragment.getyPos())
                .put("col", fragment.getxPos())
                .put("displayInfo", fragment.getdisplay())
        ;


        return displayInfo;

    }


    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        DBKeyInterface key;

        try{
            logRequest(req);

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);

            if(!validateSession(req, resp))
                return;

            Formatter formatter = getFormatFromParameters(req);

            key = getMandatoryKey("key", req);

            ContractFragment fragment = new ContractFragment(new LookupByKey(key));

            fragment.delete( );

            JSONObject json = createDeletedResponse(DataServletName, fragment);
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

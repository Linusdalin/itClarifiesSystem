package services;

import contractManagement.*;
import dataRepresentation.DBTimeStamp;
import dataRepresentation.DataObjectInterface;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import pukkaBO.condition.LookupByKey;
import pukkaBO.condition.LookupList;
import pukkaBO.exceptions.BackOfficeException;
import userManagement.PortalUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/********************************************************
 *
 *          Ordering Servlet
 *
 */

public class OrderingServlet extends DocumentService{

    public static final String DataServletName = "Ordering";


    /***********************************************************************************
     *
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
     *
     *
     *              // TODO: Check rwd access to the project
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

            Formatter formatter = getFormatFromParameters(req);

            DBKeyInterface _project         = getMandatoryKey("project", req);
            Project project = new Project(new LookupByKey(_project));

            if(!mandatoryObjectExists(project, resp))
                return;

            String jsonList = getMandatoryString("documents", req);

            JSONArray documentArray = new JSONArray(jsonList);

            setDocumentOrder(project, documentArray);

            JSONObject json = new JSONObject();
            sendJSONResponse(json, formatter, resp);


        }catch(BackOfficeException e){

            PukkaLogger.log(e);
            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        } catch ( JSONException e) {

            PukkaLogger.log(e);
            returnError(e.getMessage(), HttpServletResponse.SC_BAD_REQUEST, resp);

        } catch ( Exception e) {

            PukkaLogger.log(e);
            returnError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        }

     }

    /********************************************************************************
     *
     *          Update all documents with the new order
     *
     *          The array is simply an array of {"key", "....."}
     *
     *
     * @param project - the project affected
     * @param documentArray - array of document ids in the new order
     * @throws BackOfficeException
     *
     *
     *          //TODO: This could be optimized with a batch update
     */

    private void setDocumentOrder(Project project, JSONArray documentArray)  throws BackOfficeException{


        List<Contract> documents = project.getContractsForProject();

        // Check that the numbers are correct. We might be able to make a work around,
        // but it is better to be stringent and throw an error otherwise

        if(documents.size() != documentArray.length())
            throw new BackOfficeException(BackOfficeException.Usage, "Got " + documentArray.length() + " elements but expected " + documents.size());

        // Set new numbers by looping over the documents. It would be quicker to loo over the elements in the array,
        // but that would not check that all documents actually are updated

        for(Contract document : documents){

            // Look for the document in the array

            boolean found = false;
            for(int i = 0; i < documentArray.length(); i++){

                if(document.getKey().toString().equals(documentArray.getJSONObject(i).getString("key"))){

                    document.setOrdinal( i ); // Set the document number to the position in the json array
                    document.update();
                    found = true;
                    break;
                }
            }

            if(!found)
                throw new BackOfficeException(BackOfficeException.Usage, "Document " + document.getName() + " was not present in the order list");
        }

    }


    /*************************************************************************
     *
     *
     * @throws java.io.IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        doPost(req, resp);


     }


    /************************************************************************
     *
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
     */


    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();


    }


}

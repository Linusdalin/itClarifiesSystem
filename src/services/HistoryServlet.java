package services;

import contractManagement.Contract;
import contractManagement.ContractVersionInstance;
import contractManagement.ContractVersionInstanceTable;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pukkaBO.condition.LookupByKey;
import pukkaBO.condition.LookupList;
import pukkaBO.condition.Ordering;
import pukkaBO.condition.Sorting;
import pukkaBO.exceptions.BackOfficeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/********************************************************
 *
 *          Diff servlet will return a diff between two versions
 *          of the same document
 *
 *          It works on two already uploaded versions
 *
 */

public class HistoryServlet extends ItClarifiesService{

    public static final String DataServletName = "VersionHistory";


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Post not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

     }

    /***********************************************************************************************
     *
     *          GET returns a list of the versions for a specific document
     *
     *          {"VersionHistory":
     *              [
     *                  { "id":"ag9pdGNsYXJpZmllc2xpdmVyHgsSF0NvbnRyYWN0VmVyc2lvbkluc3RhbmNlGOsdDA",
     *                    "creation":"2014-01-01 00:09:00.0",
     *                    "name":"Cannon v1.0" }
     *              ]
     *          }
     *
     *          The list is ordered by creation date
     *
     * @param req
     * @param resp
     * @throws IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        try {

            logRequest(req);

            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);


            Formatter formatter = getFormatFromParameters(req);

            DBKeyInterface _document = getMandatoryKey("document", req);
            Contract document = new Contract(new LookupByKey(_document));

            if(!mandatoryObjectExists(document, resp))
                return;

            PukkaLogger.log(PukkaLogger.Level.INFO, "Getting history for the document " + document.getName());

            List<ContractVersionInstance> versionsForDocument = document.getVersionsForDocument(new LookupList(new Sorting(ContractVersionInstanceTable.Columns.Creation.name(), Ordering.FIRST)));
            JSONArray versionList = new JSONArray();

            for(ContractVersionInstance version : versionsForDocument){

                // Create one object for each

                JSONObject versionJSON = new JSONObject()
                        .put("name", version.getVersion())
                        .put("id", version.getKey().toString())
                        .put("creation", version.getCreation().getSQLTime().toString())
                        .put("creator", version.getCreatorId().toString());

                versionList.put(versionJSON);
            }

            JSONObject json = new JSONObject().put(DataServletName, versionList);
            sendJSONResponse(json, formatter, resp);


        } catch (BackOfficeException e) {

            e.logError("Error (Get) in Search Servlet");
            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            resp.flushBuffer();

        }


    }

    //TODO: Delete not implemented. Probably needed

    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

    }


}

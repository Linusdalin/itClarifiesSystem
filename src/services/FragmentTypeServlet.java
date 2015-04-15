package services;

import contractManagement.*;
import dataRepresentation.DBTimeStamp;
import dataRepresentation.DataObjectInterface;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pukkaBO.condition.LookupByKey;
import pukkaBO.condition.LookupList;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.export.ValuePair;
import userManagement.PortalUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/********************************************************
 *
 *          Search Servlet returning a list of fragment id:s that should be listed
 *
 */

public class FragmentTypeServlet extends ItClarifiesService{

    public static final String DataServletName = "FragmentType";


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        try{

            logRequest(req);

            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);


            Formatter formatter = getFormatFromParameters(req);

            DBKeyInterface key                = getMandatoryKey("fragment", req);
            String type                       = getMandatoryString("type", req);

            ContractFragment fragment = new ContractFragment(new LookupByKey(key));
            if(!mandatoryObjectExists(fragment, resp))
                return;

            ContractVersionInstance version = fragment.getVersion();
            Contract document = version.getDocument();

            if(!modifiable(document, resp))
                return;

            fragment.setType(type);
            fragment.update();

            invalidateFragmentCache(version);


            // Finally create the object

            JSONObject json = createPostResponse(DataServletName, fragment);
            sendJSONResponse(json, formatter, resp);


        }catch(BackOfficeException e){

            PukkaLogger.log(e);
            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        } catch ( Exception e) {

            PukkaLogger.log(e);
            returnError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        }

     }

    /*************************************************************************
     *
     *          Get all fragment types
     *
     *          Parameters:
     *
     *          No parameters, just get all types for constructing dropdown lists

     *
     * @throws java.io.IOException
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

            List <DataObjectInterface> allTypes = new ContractFragmentTypeTable(new LookupList()).getValues();

            JSONArray typeList = new JSONArray();

            for(DataObjectInterface object : allTypes){


                ContractFragmentType type = (ContractFragmentType)object;
                PukkaLogger.log(PukkaLogger.Level.INFO, "type " + type.getName() + " found...");

                JSONObject typeJSON = new JSONObject()
                        .put("id",              type.getKey().toString())
                        .put("name", type.getName())
                        .put("description", type.getDescription());

                typeList.put(typeJSON);

            }

            JSONObject json = new JSONObject().put(DataServletName, typeList);
            sendJSONResponse(json, formatter, resp);

        } catch (BackOfficeException e) {

            PukkaLogger.log( e );
            returnError("Error in " + DataServletName, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        } catch (Exception e) {

            PukkaLogger.log( e );
            returnError("Error in " + DataServletName, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
        }


    }

    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

    }


}

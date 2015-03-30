package risk;

import contractManagement.*;
import dataRepresentation.DBTimeStamp;
import dataRepresentation.DataObjectInterface;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pukkaBO.condition.*;

import pukkaBO.exceptions.BackOfficeException;
import reclassification.Rerisk;
import services.DocumentService;
import services.Formatter;
import userManagement.PortalUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/********************************************************
 *
 *          Risk Flag Servlet
 *
 */

public class RiskFlagServlet extends DocumentService {

    public static final String DataServletName = "Risk";


    /***********************************************************************************
     *
     *      Changing the risk flag
     *
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
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

            DBKeyInterface key               = getMandatoryKey("fragment", req);
            long _risk                       = getMandatorylong("risk", req);

            String comment                   = getOptionalString("comment", req, "");
            String pattern                   = getOptionalString("pattern", req, "");
            long patternPos                  = getOptionalLong("patternPos", req, -1);

            ContractFragment fragment = new ContractFragment(new LookupByKey(key));
            if(!mandatoryObjectExists(fragment, resp))
                return;

            ContractVersionInstance version = fragment.getVersion();
            Contract document = version.getDocument();

            if(!mandatoryObjectExists(document, resp))
                return;

            if(!modifiable(document, resp))
                return;

            if(!mandatoryObjectExists(version, resp))
                return;

            ContractRisk risk = new ContractRiskTable().getValue((int)_risk);
            if(!mandatoryObjectExists(risk, resp))
                return;

             // Calculate the non-submitted data

            PortalUser classifier = sessionManagement.getUser();
            DBTimeStamp now = new DBTimeStamp();

            if(patternPos == -1){

                // If the pattern position is not given, fallback to finding the first occurrence

                patternPos = fragment.getText().indexOf(pattern);
                PukkaLogger.log(PukkaLogger.Level.INFO, "No pattern position give. Using the first occurrence");

            }


            // Create the classification object
            // TODO: add transaction commit here

            RiskClassification classification = new RiskClassification(
                    fragment.getKey(),
                    risk,
                    comment,
                    "#RISK",
                    classifier.getKey(),
                    version.getKey(),
                    document.getProjectId(),
                    pattern,
                    patternPos,
                    now.getISODate());
            classification.store();

            // Now update the fragment

            PukkaLogger.log(PukkaLogger.Level.INFO, "Updating risk for fragment "+ fragment.getName()+" to " + risk.getName() );

            fragment.setRisk(risk);
            fragment.update();

            // And store the change in the log

            Rerisk logEntry = new Rerisk(
                    risk.getName(),
                    now.getISODate(),
                    document.getProject().getName(),
                    document.getName(),
                    fragment.getOrdinal(),
                    fragment.getText(),
                    pattern,
                    patternPos,
                    classifier.getName(),
                    false);

            logEntry.store();


            invalidateFragmentCache(version);

            // Finally create the object

            JSONObject json = createPostResponse(DataServletName, classification);
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
     *
     *
     * @throws java.io.IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        try{
            logRequest(req);

            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);


            Formatter formatter = getFormatFromParameters(req);

            JSONArray list = new JSONArray();

            DBKeyInterface organization = sessionManagement.getUser().getOrganizationId();

            for(DataObjectInterface object : new ContractRiskTable().getValues()){

                ContractRisk risk = (ContractRisk)object;

                JSONObject riskObject = new JSONObject()
                        .put("id", ""+ risk.getId())
                        .put("severity", new Integer(risk.getSeverity()))    //Stored as string
                        .put("name", risk.getName());
                list.put(riskObject);

            }

            JSONObject json = new JSONObject().put(DataServletName, list);
            sendJSONResponse(json, formatter, resp);

        }catch(BackOfficeException e){

            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            e.printStackTrace();


        } catch ( Exception e) {

            returnError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            e.printStackTrace();

        }

     }





    /************************************************************************
     *
     *          Delete is not supported.
     *
     *          //TODO: Not implementes
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
     */


    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);

    }


}

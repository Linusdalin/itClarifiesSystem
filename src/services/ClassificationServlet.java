package services;

import analysis.Significance;
import com.google.appengine.api.datastore.Query;
import contractManagement.*;
import dataRepresentation.DBTimeStamp;
import dataRepresentation.DataObjectInterface;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupByKey;
import pukkaBO.condition.LookupList;

import pukkaBO.condition.ReferenceFilter;
import pukkaBO.exceptions.BackOfficeException;
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

public class ClassificationServlet extends DocumentService{

    public static final String DataServletName = "Classification";


    /***********************************************************************************
     *
     *      Changing the classification
     *
     * @param req -
     * @param resp -
     * @throws java.io.IOException -
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


            DBKeyInterface key                = getMandatoryKey("fragment", req);
            DBKeyInterface fragmentClassKey   = getMandatoryKey("class", req);
            String comment                    = getOptionalString("comment", req, "");
            String pattern                    = getOptionalString("pattern", req, "");
            String keyword                    = getOptionalString("keyword", req, "");
            long pos                          = getOptionalLong("pos", req, 0);
            long length                       = getOptionalLong("length", req, 0);
            long significance                 = getOptionalLong("significance", req, 70);  // Using 70 as default significance. Well over the threshold

            Formatter formatter = getFormatFromParameters(req);

            ContractFragment fragment = new ContractFragment(new LookupByKey(key));

            if(!mandatoryObjectExists(fragment, resp))
                return;


            ContractVersionInstance version = fragment.getVersion();
            if(!mandatoryObjectExists(version, resp))
                return;

            Contract document = version.getDocument();
            if(!mandatoryObjectExists(document, resp))
                return;

            if(!modifiable(document, resp))
                return;

            FragmentClass fragmentClass = new FragmentClass(new LookupByKey(fragmentClassKey));
            if(!mandatoryObjectExists(fragmentClass, resp))
                return;


             // Calculate the noon-submitted data

            PortalUser classifier = sessionManagement.getUser();
            DBTimeStamp now = new DBTimeStamp();

            //Now update the fragment with a new classification

            FragmentClassification classification = new FragmentClassification(
                    fragment.getKey(),
                    fragmentClassKey,
                    fragmentClass.getName(),            // Set the name from the fragment class name
                    comment,
                    keyword,
                    classifier.getKey(),
                    version.getKey(),
                    document.getProjectId(),
                    pattern,
                    pos,
                    length,
                    significance,
                    "no rule specified",
                    now.getISODate());
            classification.store();

            // Update classification count

            int newCount = fragment.getClassificationsForFragment(new LookupList()
                    .addFilter(new ColumnFilter(FragmentClassificationTable.Columns.Significance.name(), Query.FilterOperator.GREATER_THAN, Significance.DISPLAY_SIGNIFICANCE))).size();

            PukkaLogger.log(PukkaLogger.Level.INFO, "new count is " + newCount);

            fragment.setClassificatonCount(newCount);
            fragment.update();

            // Store the reclassification for future analysis and improvement of the rule


            String name = classifier.getName() + "@" + now.getISODate();
            boolean isPositiveClassification = true;
            ContractFragment headline = fragment.getStructureItem().getFragmentForStructureItem();

            Reclassification reclassification = new Reclassification(
                    name,
                    isPositiveClassification,
                    classifier,
                    fragment.getText(),
                    headline.getText(),
                    pattern,
                    comment,
                    fragmentClass,
                    document,
                    now.getISODate(),
                    false);

            reclassification.store();

            // Invalidate the cache. The document overview is changed.

            invalidateFragmentCache(version);

            // Finally create the response

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
     *              Get all classes for the organization
     *
     * @throws java.io.IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        if(req.getParameter("_method") != null && req.getParameter("_method").equals("DELETE")){
            doDelete(req, resp);
            return;
        }

        try{

            logRequest(req);

            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);

            PortalUser user = sessionManagement.getUser();

            Formatter formatter = getFormatFromParameters(req);

            JSONArray list = new JSONArray();

            FragmentClassTable organizationSpecificClasses = new FragmentClassTable(new LookupList()
                    .addFilter(new ReferenceFilter(FragmentClassTable.Columns.Organization.name(), user.getOrganization().getKey())));

            for(DataObjectInterface object : organizationSpecificClasses.getValues()){

                FragmentClass fragmentClass = (FragmentClass)object;

                JSONObject riskObject = new JSONObject()
                        .put("id", fragmentClass.getKey().toString())
                        .put("name", fragmentClass.getName())
                        .put("type", "Organization");
                list.put(riskObject);

            }

            // Now add all generic classes

            FragmentClassTable genericClasses = new FragmentClassTable(new LookupList()
                    .addFilter(new ReferenceFilter(FragmentClassTable.Columns.Organization.name(), Organization.getnone().getKey())));

            for(DataObjectInterface object : genericClasses.getValues()){

                FragmentClass fragmentClass = (FragmentClass)object;

                JSONObject riskObject = new JSONObject()
                        .put("id", fragmentClass.getKey().toString())
                        .put("name", fragmentClass.getName())
                        .put("type", "General");
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
     *
     * @param req -
     * @param resp -
     * @throws java.io.IOException
     */


    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        try{

            logRequest(req);

            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);

            DBKeyInterface key                = getMandatoryKey("key", req);

            Formatter formatter = getFormatFromParameters(req);

            FragmentClassification classification = new FragmentClassification(new LookupByKey(key));
            ContractFragment fragment = classification.getFragment();

            if(!mandatoryObjectExists(classification, resp))
                return;

            DBTimeStamp now = new DBTimeStamp();

            if(classification.getCreatorId().equals(PortalUser.getSystemUser().getKey())){

                // We are removing a system classification. Apparently the user did not agree
                //TODO. Improvement: Add check if there is another similar classification

                ContractFragment headline = fragment.getStructureItem().getFragmentForStructureItem();

                //     public Reclassification(String name, boolean ispositive, DBKeyInterface user, String fragment, String headline, String pattern, String tag, DBKeyInterface classification, DBKeyInterface document, String date) throws BackOfficeException{


                Reclassification reclassification = new Reclassification(
                        "delete classification@" + now.getISODate(),
                        false,
                        sessionManagement.getUser().getKey(),
                        fragment.getText(),
                        headline.getText(),
                        classification.getPattern(),
                        classification.getComment(),
                        classification.getClassificationId(),
                        fragment.getVersion().getDocumentId(),
                        now.getISODate(),
                        false);

                reclassification.store();

            }


            ContractVersionInstance version = fragment.getVersion();
            new FragmentClassificationTable().deleteItem( classification);

            // Nuw update classification count

            int newCount = fragment.getClassificationsForFragment(new LookupList()
                    .addFilter(new ColumnFilter(FragmentClassificationTable.Columns.Significance.name(), Query.FilterOperator.GREATER_THAN, Significance.DISPLAY_SIGNIFICANCE))).size();

            PukkaLogger.log(PukkaLogger.Level.INFO, "new count is " + newCount);

            fragment.setClassificatonCount(newCount);
            fragment.update();

            invalidateFragmentCache(version);

            // Finally create the object

            JSONObject response = createDeletedResponse(DataServletName, classification);
            sendJSONResponse(response, formatter, resp);

        } catch (BackOfficeException e) {

            PukkaLogger.log( e );
            returnError("Error in " + DataServletName, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        } catch (Exception e) {

            PukkaLogger.log( e );
            returnError("Error in " + DataServletName, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
        }



    }


}

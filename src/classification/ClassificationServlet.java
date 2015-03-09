package classification;

import analysis.Significance;
import classification.FragmentClass;
import classification.FragmentClassification;
import classification.FragmentClassificationTable;
import classification.Reclassification;
import classifiers.ClassifierInterface;
import com.google.appengine.api.datastore.Query;
import contractManagement.*;
import dataRepresentation.DBTimeStamp;
import databaseLayer.DBKeyInterface;
import featureTypes.FeatureType;
import featureTypes.FeatureTypeInterface;
import featureTypes.FeatureTypeTree;
import language.Language;
import language.LanguageAnalyser;
import language.LanguageCode;
import language.LanguageInterface;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pukkaBO.condition.*;

import pukkaBO.exceptions.BackOfficeException;
import services.DocumentService;
import services.Formatter;
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

public class ClassificationServlet extends DocumentService {

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
            String classTag                  = getMandatoryString("class", req);
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

             // Calculate the non-submitted data

            PortalUser classifier = sessionManagement.getUser();
            DBTimeStamp now = new DBTimeStamp();

            Organization organization = classifier.getOrganization();


            LanguageCode documentLanguage = new LanguageCode(document.getLanguage());
            LanguageInterface languageForDocument = new LanguageAnalyser().getLanguage(documentLanguage);

            // Lookup the tag. Either in the static tree or custom tags in the database

            String localizedClass = getTag(classTag, organization, languageForDocument);
            String tagName = getTagName(classTag, organization, languageForDocument);
            FeatureTypeInterface featureType = getFeatureType(classTag, languageForDocument);

            if(localizedClass == null){

                returnError("The classification tag " + classTag + " does not exist.", ErrorType.DATA, HttpServletResponse.SC_BAD_REQUEST, resp);
                return;
            }

            if(featureType != null)
                keyword = featureType.createKeywordString(keyword);


            //Now update the fragment with a new classification

            FragmentClassification classification = new FragmentClassification(
                    fragment.getKey(),
                    localizedClass,
                    0,              // requirement level not implemented
                    0,              // applicable phase not implemented
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

            // Store the reclassification for future analysis and improvement of the rule

            String name = classifier.getName() + "@ " + now.getISODate();
            boolean isPositiveClassification = true;
            StructureItem headline = fragment.getStructureItem();
            String headlineText = "";

            if(headline.exists())
                headlineText = headline.getFragmentForStructureItem().getText();

            Reclassification reclassification = new Reclassification(
                    name,
                    isPositiveClassification,
                    classifier.getKey(),
                    fragment.getText(),
                    headlineText,
                    fragment.getOrdinal(),
                    pattern,
                    comment,
                    tagName,                    // Using the name here for code generation
                    0,
                    0,
                    fragment.getRisk(),
                    document.getKey(),
                    now.getISODate(),
                    false);

            reclassification.store();


            // Update classification count

            int oldCount = (int)fragment.getClassificatonCount();

            int newCount = fragment.getClassificationsForFragment(new LookupList()
                    .addFilter(new ColumnFilter(FragmentClassificationTable.Columns.Significance.name(), Query.FilterOperator.GREATER_THAN, Significance.DISPLAY_SIGNIFICANCE))).size();

            fragment.setClassificatonCount(newCount);
            fragment.update();


            if(newCount == oldCount + 1 )
                PukkaLogger.log(PukkaLogger.Level.INFO, "new (real) count of classifications in the fragment is " + newCount);
            else
                PukkaLogger.log(PukkaLogger.Level.WARNING, "Got (real) count of " + newCount + " classifications in the fragment but expected " + (oldCount + 1) );






            // Invalidate the cache. The document overview is changed.

            invalidateFragmentCache(version);

            // Finally create the response

            JSONObject json = createPostResponse(DataServletName, classification);
            sendJSONResponse(json, formatter, resp);


        }catch(BackOfficeException e){

            PukkaLogger.log( e );
            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        } catch ( Exception e) {

            PukkaLogger.log( e );
            returnError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        }

     }



    /*************************************************************************
     *
     *              Get all classes. This includes:
     *
     *               - All defined classes in the Analysis system
     *               - user defined classes for the organization
     *
     * @throws java.io.IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        if(req.getParameter("_method") != null && req.getParameter("_method").equals("DELETE")){
            doDelete(req, resp);
            return;
        }

        logRequest(req);

        getClasses(req, resp, DataServletName);
    }

    public void getClasses(HttpServletRequest req, HttpServletResponse resp, String dataServletName) throws IOException {


        try{

            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);

            PortalUser user = sessionManagement.getUser();

            Formatter formatter = getFormatFromParameters(req);

            JSONArray list = new JSONArray();

            ClassifierInterface[] classifiers = defaultLanguage.getSupportedClassifiers();

            for (ClassifierInterface classifier : classifiers) {

                FeatureTypeInterface featureType = classifier.getType();

                // Only add the classifications that actually have a TYPE

                if(featureType != FeatureTypeTree.None){

                    JSONObject riskObject = new JSONObject()
                                .put("id", classifier.getType().getName())
                                .put("name", classifier.getClassificationName())
                                .put("desc", classifier.getClassificationName())   //TODO: Description not implemented for standard classes
                                .put("type", "General");
                        list.put(riskObject);

                }

            }

            classifiers = defaultLanguage.getPostProcessClassifiers();

            for (ClassifierInterface classifier : classifiers) {

                JSONObject riskObject = new JSONObject()
                            .put("id", classifier.getType().getName())
                            .put("name", classifier.getClassificationName())
                            .put("desc", classifier.getClassificationName())   //TODO: Description not implemented for standard classes
                            .put("type", "General");
                    list.put(riskObject);

            }


            // Get generic classifications stored in the database. First generic

            List<FragmentClass> customClasses = user.getOrganization().getCustomTagsForOrganization();
            List<FragmentClass> genericClasses = Organization.getnone().getCustomTagsForOrganization();

            customClasses.addAll(genericClasses);


            for(FragmentClass fragmentClass : customClasses){

                JSONObject riskObject = new JSONObject()
                        .put("id", fragmentClass.getKey().toString())
                        .put("name", fragmentClass.getName())
                        .put("desc", fragmentClass.getDescription())
                        .put("type", "General");
                list.put(riskObject);

            }


            JSONObject json = new JSONObject().put(dataServletName, list);
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
            ContractVersionInstance version = fragment.getVersion();

            if(!mandatoryObjectExists(classification, resp))
                return;

            DBTimeStamp now = new DBTimeStamp();

            if(classification.getCreatorId().equals(PortalUser.getSystemUser().getKey())){

                // We are removing a system classification. Apparently the user did not agree

                ContractFragment headline = fragment.getStructureItem().getFragmentForStructureItem();

                Reclassification reclassification = new Reclassification(
                        "delete@ " + now.getISODate(),
                        false,
                        sessionManagement.getUser().getKey(),
                        fragment.getText(),
                        headline.getText(),
                        fragment.getOrdinal(),
                        classification.getPattern(),
                        classification.getComment(),
                        classification.getClassTag(),
                        0, 0,
                        fragment.getRisk(),
                        fragment.getVersion().getDocumentId(),
                        now.getISODate(),
                        false);

                reclassification.store();

            }
            else{

                // We are removing a user defined classification. (User changed his/her mind)
                // There should be a reclassification note that we now should remove

                Reclassification reclassificationToDelete = new Reclassification(new LookupItem()
                        .addFilter(new ReferenceFilter(ReclassificationTable.Columns.Document.name(), version.getDocumentId()))
                        .addFilter(new ColumnFilter(ReclassificationTable.Columns.ClassTag.name(), classification.getClassTag()))
                        .addFilter(new ColumnFilter(ReclassificationTable.Columns.FragmentNo.name(), fragment.getOrdinal())));

                if(reclassificationToDelete.exists()){

                    PukkaLogger.log(PukkaLogger.Level.INFO, "Deleting a reclassification note for classification " + classification.getClassTag());
                    reclassificationToDelete.delete();
                }
                else{

                    PukkaLogger.log(PukkaLogger.Level.INFO, "Found no reclassification note to delete for classification " + classification.getClassTag() +
                            "in fragment no " + fragment.getOrdinal() + " in document (version)" + version.getNameColumn());

                }

            }




            new FragmentClassificationTable().deleteItem( classification);

            int oldCount = (int)fragment.getClassificatonCount();

            // Nuw update classification count

            int newCount = fragment.getClassificationsForFragment(new LookupList()
                    .addFilter(new ColumnFilter(FragmentClassificationTable.Columns.Significance.name(), Query.FilterOperator.GREATER_THAN, Significance.DISPLAY_SIGNIFICANCE))).size();


            if(newCount == oldCount - 1 )
                PukkaLogger.log(PukkaLogger.Level.INFO, "new (real) count of classifications in the fragment is " + newCount);
            else
                PukkaLogger.log(PukkaLogger.Level.WARNING, "Got (real) count of " + newCount + " classifications in the fragment but expected " + (oldCount - 1));

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

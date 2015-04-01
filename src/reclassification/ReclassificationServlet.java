package reclassification;

import analysis.ParseFeedbackItem;
import analysis.Significance;
import classification.FragmentClass;
import classification.FragmentClassification;
import classifiers.ClassifierInterface;
import com.google.appengine.api.datastore.Query;
import contractManagement.*;
import dataRepresentation.DBTimeStamp;
import databaseLayer.DBKeyInterface;
import diff.FragmentComparator;
import featureTypes.FeatureTypeInterface;
import featureTypes.FeatureTypeTree;
import language.English;
import language.LanguageAnalyser;
import language.LanguageCode;
import language.LanguageInterface;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;
import reclassification.MTClassification;
import reclassification.MechanicalTurkInterface;
import reclassification.ReclassificationHistory;
import risk.ContractRisk;
import risk.ContractRiskTable;
import risk.RiskClassification;
import services.DocumentService;
import services.Formatter;
import services.ItClarifiesService;
import userManagement.Organization;
import userManagement.PortalUser;
import userManagement.PortalUserTable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

/********************************************************
 *
 *          Contract Servlet returning one contract
 *
 */

public class ReclassificationServlet extends DocumentService {

    public static final String DataServletName = "Reclassification";


    /***********************************************************************************
     *
     *      Shooting in a classification that is stored
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

            // This is an admin service. It uses the google authenticate and is then free to access data without a session

            //if(!googleAuthenticate(req, resp))
            //   return;

            //Type parameter, defining what kind of attribute we will add
            String type                  = getMandatoryString("type", req);
            String magicKey              = getMandatoryString("key", req);

            //Mandatory parameters for all types
            String userName                   = getMandatoryString("user", req);
            String projectName                = getMandatoryString("project", req);
            String documentName               = getMandatoryString("document", req);
            long   ordinal                    = getMandatorylong("ordinal", req);
            String encodedFragment            = getMandatoryString("fragment", req);
            String pattern                    = getMandatoryString("pattern", req);
            long   patternPos                 = getMandatorylong("pos", req);

            // Optional parameters, specific for different type of attributes
            String classTag                   = getOptionalString("class", req, null);
            String riskLevel                  = getOptionalString("risk", req, null);
            String annotationText             = getOptionalString("text", req, null);
            boolean creating                  = getOptionalBoolean("creating", req, true);

            Formatter formatter = getFormatFromParameters(req);

            if(magicKey == null || !magicKey.equals(NewMTProject.MagicKey)){

                sendJSONResponse(new analysis.ParseFeedbackItem(ParseFeedbackItem.Severity.ERROR,
                        "No Access !", 0).toJSON(), formatter, resp);

            }

            DBTimeStamp now = new DBTimeStamp();

            System.out.println("URL-encoded: " + encodedFragment);

            Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), projectName)));


            if(!project.exists()){

                sendJSONResponse(new analysis.ParseFeedbackItem(ParseFeedbackItem.Severity.ERROR,
                        "Could not find project named " + projectName + ". Aborting!", 0).toJSON(), formatter, resp);
                return;

            }

            Contract document = new Contract(new LookupItem()
                    .addFilter(new ColumnFilter(ContractTable.Columns.File.name(), documentName))
                    .addFilter(new ReferenceFilter(ContractTable.Columns.Project.name(), project.getKey())));


            if(!document.exists()){

                sendJSONResponse(new analysis.ParseFeedbackItem(ParseFeedbackItem.Severity.ERROR,
                        "Could not find document named " + documentName + "in project "+ projectName+". Aborting!", 0).toJSON(), formatter, resp);
                return;

            }



            //TODO: Error message if project or document is missing

            PortalUser user = new PortalUser(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.Name.name(), userName)));
            ContractVersionInstance latestVersion = document.getHeadVersion();

            List<ContractFragment> fragmentsForDocument = latestVersion.getFragmentsForVersion(new LookupList().addOrdering(ContractFragmentTable.Columns.Ordinal.name(), Ordering.FIRST));


            //PukkaLogger.setLogLevel(PukkaLogger.Level.DEBUG);

            ParseFeedbackItem feedback = null;
            if(type.equalsIgnoreCase("classification")){

                //String fragmentBody = URLDecoder.decode(encodedFragment, "UTF-8");
                String fragmentBody = encodedFragment;
                Reclassification classification = new Reclassification(classTag, creating, now.getISODate(), projectName, documentName, (int)ordinal, fragmentBody,  pattern, (int)patternPos, userName, false);

                ContractFragment fragment = locateFragment(fragmentBody, (int)ordinal, fragmentsForDocument);

                if(creating)
                    feedback = classification.generate(project, latestVersion, user, fragment);
                else
                    feedback = classification.remove(project, latestVersion, user, fragment);

            }else if(type.equalsIgnoreCase("definition")){

                //String fragmentBody = URLDecoder.decode(encodedFragment, "UTF-8");
                String fragmentBody = encodedFragment;

                Redefinition redefinition = new Redefinition(pattern, true, project.getName(), document.getName(), (int)ordinal, fragmentBody, false);

                ContractFragment fragment = locateFragment(fragmentBody, (int)ordinal, fragmentsForDocument);

                if(creating)
                    feedback = redefinition.generate(project, latestVersion, user, fragment);
                else
                    feedback = redefinition.remove(project, latestVersion, user, fragment);

            }

            else if(type.equalsIgnoreCase("risk")){

                //String fragmentBody = URLDecoder.decode(encodedFragment, "UTF-8");
                String fragmentBody = encodedFragment;

                Rerisk risk = new Rerisk(riskLevel, now.getISODate(), projectName, documentName, (int)ordinal, fragmentBody,  pattern, (int)patternPos, userName, false);

                ContractFragment fragment = locateFragment(fragmentBody, (int)ordinal, fragmentsForDocument);

                feedback = risk.generate(project, latestVersion, user, fragment);

            }

            else if(type.equalsIgnoreCase("annotation")){

                //String fragmentBody = URLDecoder.decode(encodedFragment, "UTF-8");
                //String decodedCommentText = URLDecoder.decode(annotationText, "UTF-8");

                String fragmentBody = encodedFragment;
                String decodedCommentText = annotationText;
                ContractFragment fragment = locateFragment(fragmentBody, (int)ordinal, fragmentsForDocument);

                if(!fragment.exists()){

                    sendJSONResponse(new analysis.ParseFeedbackItem(ParseFeedbackItem.Severity.ERROR,
                            "Could not locate fragment " + fragmentBody + "in project "+ projectName+". Aborting!", 0).toJSON(), formatter, resp);

                }


                Reannotation annotation = new Reannotation(decodedCommentText, creating, now.getISODate(), projectName, documentName, (int)ordinal, fragmentBody,  pattern, (int)patternPos, userName, false);

                if(creating)
                    feedback = annotation.generate(project, latestVersion, user, fragment);
                else
                    feedback = annotation.remove(project, latestVersion, user, fragment);

            }
            else{

                PukkaLogger.log(PukkaLogger.Level.ERROR, "Type "+ type + " not implemented for reclassification");
            }


            invalidateFragmentCache(latestVersion);
            sendJSONResponse(feedback.toJSON(), formatter, resp);



        }catch(BackOfficeException e){

            PukkaLogger.log( e );
            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        } catch ( Exception e) {

            PukkaLogger.log(e);
            returnError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        }

     }



    /*************************************************************************
     *
     *
     * @throws java.io.IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        if(req.getParameter("_method") != null && req.getParameter("_method").equals("DELETE")){
            doDelete(req, resp);
            return;
        }

        returnError("Get not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);


    }


    /************************************************************************
     *
     *
     * @param req -
     * @param resp -
     * @throws java.io.IOException
     */


    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);


    }





    /**************************************************************************************
     *
     *
     *
     * @throws BackOfficeException
     *
     *
     *      TODO: There are a lot of lookup in this service that could be optimized getting once and for all
     */


    /*
    private ParseFeedbackItem generateDemoComment(MTClassification comment) throws BackOfficeException {

        StringBuffer feedback = new StringBuffer();
        feedback.append("<p>Adding comment...");

        PortalUser user = new PortalUser(new LookupItem().addFilter(new ColumnFilter(PortalUserTable.Columns.Name.name(), comment.user)));
        if(!user.exists()){

            feedback.append("<br/> - ! User " + comment.user + " does not exist! </p>");
            return feedback.toString();

        }


        String filename = comment.document;

        try {

            filename = new String (filename.getBytes("UTF-8"));

        } catch (UnsupportedEncodingException e) {

            // Swallow this
        }

        ConditionInterface lookupDocumentCondition = new LookupItem()
                .addFilter(new ColumnFilter(ContractTable.Columns.File.name(), filename))
                .addFilter(new ReferenceFilter(ContractTable.Columns.Project.name(), project.getKey()));

        Contract document = new Contract(lookupDocumentCondition);
        if(!document.exists()){

            feedback.append("<br/> - ! Document " + filename + " does not exist! in target project "+project.getName()+"!</p>");

            List<Contract> availableDocuments = project.getContractsForProject();

            feedback.append("<br/> - Available documents: ");

            for (Contract availableDocument : availableDocuments) {

                feedback.append("<li>" + availableDocument.getFile() + " </li>");

            }


            return feedback.toString();

        }



        Organization organization = document.getProject().getOrganization();
        if(!organization.exists()){

            feedback.append("<br/> - ! No organization for " + document.getProject().getName() + " !</p>");

            return feedback.toString();

        }
        feedback.append("<br/> - Organization: " + organization.getName());


        ContractVersionInstance version = document.getHeadVersion();


        ContractFragment fragment = locateFragment(comment, version);

        if(!fragment.exists()){

            feedback.append("<br/> - ! Fragment id " + comment.ordinal + " does not exist! </p>");
            return feedback.toString();

        }

        LanguageCode documentLanguage = new LanguageCode(document.getLanguage());
        LanguageInterface languageForDocument = new LanguageAnalyser().getLanguage(documentLanguage);

        // Lookup the tag. Either in the static tree or custom tags in the database

        String classTag = getTag(comment.classification, organization, languageForDocument);
        String tagName = getTagName(comment.classification, organization, languageForDocument);
        FeatureTypeInterface featureType = getFeatureType(comment.classification, languageForDocument);

        String keyword = "";

        if(featureType != null)
          keyword = featureType.createKeywordString(keyword);



        int patternPos = fragment.getText().indexOf(comment.pattern);

        // Get all existing classifications for the


        // Now create annotation, classification and risk


        FragmentClassification classification = new FragmentClassification(
                fragment.getKey(),
                comment.classification,
                comment.requirementLevel,
                comment.applicablePhase,
                comment.comment,
                keyword,
                user.getKey(),
                version.getKey(),
                document.getProjectId(),
                comment.pattern,
                patternPos,
                comment.pattern.length(),
                100, //significance
                "",
                analysisTime.getISODate());



        if(comment.classification.equals("#RISK")){

            ContractRisk risk = new ContractRiskTable().getValueForName(comment.riskLevel);

            if(!risk.exists()){

                feedback.append("<br/> - ! Risk level " + comment.riskLevel + " does not exist! </p>");
                return feedback.toString();

            }



            // Add a risk attribute too

            RiskClassification riskClassification = new RiskClassification(
                    fragment.getKey(),
                    risk,
                    "demo",
                    "#RISK",
                    user.getKey(),
                    version.getKey(),
                    document.getProjectId(),
                    comment.pattern,
                    patternPos,
                    analysisTime.getISODate());


            riskClassification.store();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            System.out.println("RiskClassification: " + riskClassification.getKey().toString());
            fragment.setRisk(risk);

            feedback.append("<br/> - Adding risk \""+ risk.getName()+"\" to fragment no "+ fragment.getOrdinal()+" in document " + document.getName());

        }
        int ordinal = (int)fragment.getAnnotationCount() + 1;

        if(comment.comment != null && !comment.comment.equals("")){

        ContractAnnotation annotation = new ContractAnnotation(
                "demo",
                fragment.getKey(),
                ordinal,
                comment.comment,
                user.getKey(),
                version.getKey(),
                comment.pattern,
                0,                          //TODO: Anchor position not implemented
                analysisTime.getISODate());

            annotation.store();
            fragment.setAnnotationCount(ordinal);
            feedback.append("<br/> - Adding annotation \""+ comment.comment + "\" to fragment no "+ fragment.getOrdinal()+" in document " + document.getName());
        }

        classification.store();
        fragment.setClassificatonCount(fragment.getClassificatonCount() + 1);
        feedback.append("<br/> - Adding classification \""+ classification.getClassTag() + "\" to fragment no "+ fragment.getOrdinal()+" in document " + document.getName() + " in project " + project.getName());


        if(fragment.getOrdinal() != comment.ordinal){

            // Fragment is found elsewhere. Prompt the user with a warning

            feedback.append("<br/><b> - !NOTE: The comments are added to fragment " + fragment.getOrdinal() + " but it was listed at fragment " + comment.ordinal + ". Consider revising the stored data.</b>");


        }

        fragment.update();
        feedback.append("</p>");

        // Invalidate the cache. The document overview is changed.

        ItClarifiesService.invalidateFragmentCache(version);


        return feedback.toString();
    }

    /********************************************************************************
     *
     *          Locate fragment will find the closest matching fragment between the ordinal
     *          number in the comment and the fragments in the list
     *
     *
     * @param comment
     * @param version
     * @return
     *
     *          It will look 20 steps up and down in the list
     */

    private ContractFragment locateFragment(String fragmentText, int fragmentNo, List<ContractFragment> fragmentsForDocument)  {

        try{


            FragmentComparator comparator = new FragmentComparator();

            int totalFragments = fragmentsForDocument.size();
            PukkaLogger.log(PukkaLogger.Level.INFO, "Looking for fragment " + fragmentText + " to comment.");
            PukkaLogger.log(PukkaLogger.Level.INFO, "*** Start looking at " + fragmentNo);

            // First check the fragment pointed out by the ordinal value

            ContractFragment fragment  = fragmentsForDocument.get(fragmentNo);

            if(comparator.isSame(fragmentText, fragment.getText(), 500)){

                return fragment;
            }

            for(int offset = 1; offset <= 20; offset++){


                if(fragmentNo + offset < totalFragments){

                    fragment = fragmentsForDocument.get(fragmentNo + offset);

                    //System.out.println("  -> Looking at fragment (" + fragment.getOrdinal() + ")" + fragment.getName());

                    if(comparator.isSame(fragmentText, fragment.getText(), 500)){

                        // Found. Log and return
                        PukkaLogger.log(PukkaLogger.Level.INFO, "Matching fragment " + fragment.getName() + " at " + (fragmentNo + offset) + "(" + offset + " off)");
                        return fragment;
                    }
                }

                if(fragmentNo - offset >= 0){

                    fragment = fragmentsForDocument.get(fragmentNo - offset);

                    //System.out.println(" --> Looking at fragment (" + fragment.getOrdinal() + ")" + fragment.getName());

                    if(comparator.isSame(fragmentText, fragment.getText())){

                        // Found. Log and return
                        PukkaLogger.log(PukkaLogger.Level.INFO, "Matching fragment " + fragment.getName() + " at " + (fragmentNo - offset) + "(" + -offset + " off)");
                        return fragment;
                    }

                }

            }

            PukkaLogger.log(PukkaLogger.Level.WARNING, "Could not find fragment " + fragmentText + " at " + fragmentNo + "( +/- 20)");

            //TOD: Remove this. Only for reenginering
            return fragmentsForDocument.get(fragmentNo);


        }catch(Exception e){

            PukkaLogger.log(e);
        }

        return new ContractFragment();


    }

    protected FeatureTypeInterface getFeatureType(String className, LanguageInterface languageForDocument) {



        ClassifierInterface[] classifiers = languageForDocument.getSupportedClassifiers();

        for (ClassifierInterface classifier : classifiers) {

            if(classifier.getType().getName().equals(className)){

                PukkaLogger.log(PukkaLogger.Level.INFO, "Found static classTag " + className);
                return classifier.getType();
            }
        }

        return null;
    }



}

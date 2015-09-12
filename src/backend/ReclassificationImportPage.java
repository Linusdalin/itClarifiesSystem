package backend;

import classification.FragmentClass;
import classification.FragmentClassification;
import classifiers.ClassifierInterface;
import contractManagement.*;
import dataRepresentation.DBTimeStamp;
import databaseLayer.DBKeyInterface;
import databaseLayer.DatabaseAbstractionFactory;
import diff.FragmentComparator;
import featureTypes.FeatureTypeInterface;
import language.English;
import language.LanguageAnalyser;
import language.LanguageCode;
import language.LanguageInterface;
import log.PukkaLogger;
import project.Project;
import project.ProjectTable;
import pukkaBO.GenericPage.NarrowPage;
import pukkaBO.GenericPage.PageTab;
import pukkaBO.GenericPage.PageTabInterface;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.backOffice.BackOfficeLocation;
import pukkaBO.condition.*;
import pukkaBO.dropdown.DropDownInterface;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.style.Html;
import reclassification.MTClassification;
import reclassification.MTDocument;
import reclassification.MechanicalTurkInterface;
import reclassification.ReclassificationHistory;
import risk.ContractRisk;
import risk.ContractRiskTable;
import risk.RiskClassification;
import services.ItClarifiesService;
import userManagement.Organization;
import userManagement.PortalUser;
import userManagement.PortalUserTable;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;

/****************************************************************************
 *
 *          List reclassifications and allow to reimport them
 *
 */

public class ReclassificationImportPage extends NarrowPage {

    public static final String name = "reclassificationImportPage";

    public ReclassificationImportPage( ){

        super(  name,
                "Import Reclassifications");

        setSection("Documents");

        addTab(new ClassificationTab("Import", "ReClassifications Import for Document"));
        //addTab(new DashboardTab2("Statistics", "Headline for page 2"));
    }

    /***************************************************************************''
     *
     *      Classification Tab
     *
     */


    private class ClassificationTab extends PageTab implements PageTabInterface {

        ClassificationTab(String title, String headline){

            super(title, headline);
        }

        @Override
        public String getBody(String page, int tabId, BackOfficeInterface backOffice, HttpServletRequest req) throws BackOfficeException {

            DropDownInterface projectDropdown = new ProjectTable().getDropDown(null).withName("project").withUnselected("select");

            Project project = null;
            StringBuffer html = new StringBuffer();

            String importMTDoc = req.getParameter("mt");
            String key = req.getParameter("project");


            if(importMTDoc != null){

                try{

                    DBKeyInterface _project = new DatabaseAbstractionFactory().createKey(key);
                    project = new Project(new LookupByKey(_project));

                    if(!project.exists()){

                        html.append(Html.errorBox("No project with key" + key + " exists" ));
                        project = null;

                    }
                }catch(Exception e){

                    html.append(Html.errorBox("No project given!"));
                    project = null;
                }
            }



            if(importMTDoc == null || project == null){

                html.append(Html.paragraph("Historic manual classifications") + Html.newLine() + Html.newLine());

                MTDocument[] history = ReclassificationHistory.documents;

                html.append("<table>");

                for (MTDocument mechanicalTurkDocument : history) {

                    html.append(mechanicalTurkDocument.getForm(projectDropdown, new BackOfficeLocation(backOffice, section, name )));
                }

                html.append("</table>");

            }
            else{


                html.append(Html.paragraph("Executing import of" + importMTDoc + " into project "+ project.getName()) + Html.newLine() + Html.newLine());
                DBTimeStamp analysisTime = new DBTimeStamp();
                html.append(reGenerateMT(importMTDoc, project, analysisTime));
            }



            return html.toString();
        }
    }


    /*********************************************************************
     *
     *          Generate risks, annotations and classifications for all the risks
     *
     *
     * @param importMTDoc
     * @param project
     * @param analysisTime
     * @return
     *
     *          Using english as default language. This should probably change when
     *          languages per user is implemented
     */


    private String reGenerateMT(String importMTDoc, Project project, DBTimeStamp analysisTime) {

        StringBuffer feedback = new StringBuffer();
        MechanicalTurkInterface currentMTDoc = ReclassificationHistory.getDocumentByName(importMTDoc);
        LanguageInterface defaultLanguage = new English();

        try {

            for (MTClassification comment : currentMTDoc.getMTClassifications()) {

                feedback.append(generateDemoComment(comment, analysisTime, project, defaultLanguage));

            }


        } catch (BackOfficeException e) {

            PukkaLogger.log(e);
        }

        return feedback.toString();

    }

    /**************************************************************************************
     *
     *
     *
     * @param comment
     * @param analysisTime
     * @param project
     *@param defaultLanguage @return
     * @throws BackOfficeException
     *
     *
     *      TODO: Improvement Performance: There are a lot of lookup in this service that could be optimized getting once and for all
     */


    private String generateDemoComment(MTClassification comment, DBTimeStamp analysisTime, Project project, LanguageInterface defaultLanguage) throws BackOfficeException {

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
                FragmentClassification.GENERATED,                           //Assuming this is not a negative tag
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
                    analysisTime.getISODate(), FragmentClassification.GENERATED);


            riskClassification.store();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                PukkaLogger.log(e);
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
                project.getKey(),
                comment.pattern,
                0,                          //TODO: Not Implemented: Anchor position not implemented in reclassification import
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

    private ContractFragment locateFragment(MTClassification comment, ContractVersionInstance version)  {

        try{

            FragmentComparator comparator = new FragmentComparator();
            List<ContractFragment> fragmentsForDocument = version.getFragmentsForVersion(new LookupList().addOrdering(ContractFragmentTable.Columns.Ordinal.name(), Ordering.FIRST));
            ContractFragment fragment  = fragmentsForDocument.get(comment.ordinal);
            int totalFragments = fragmentsForDocument.size();
            PukkaLogger.log(PukkaLogger.Level.INFO, "Looking for fragment " + comment.fragment + " to comment.");
            PukkaLogger.log(PukkaLogger.Level.INFO, "*** Start looking at " + comment.ordinal);

            // First check the fragment pointed out by the ordinal value

            if(comparator.isSame(comment.fragment, fragment.getText())){

                return fragment;
            }

            for(int offset = 1; offset <= 20; offset++){


                if(comment.ordinal + offset < totalFragments){

                    fragment = fragmentsForDocument.get(comment.ordinal + offset);

                    //System.out.println("  -> Looking at fragment (" + fragment.getOrdinal() + ")" + fragment.getName());

                    if(comparator.isSame(comment.fragment, fragment.getText())){

                        // Found. Log and return
                        PukkaLogger.log(PukkaLogger.Level.INFO, "Matching fragment " + fragment.getName() + " at " + (comment.ordinal + offset) + "(" + offset + " off)");
                        return fragment;
                    }
                }

                if(comment.ordinal - offset >= 0){

                    fragment = fragmentsForDocument.get(comment.ordinal - offset);

                    //System.out.println(" --> Looking at fragment (" + fragment.getOrdinal() + ")" + fragment.getName());

                    if(comparator.isSame(comment.fragment, fragment.getText())){

                        // Found. Log and return
                        PukkaLogger.log(PukkaLogger.Level.INFO, "Matching fragment " + fragment.getName() + " at " + (comment.ordinal - offset) + "(" + -offset + " off)");
                        return fragment;
                    }

                }

            }

            PukkaLogger.log(PukkaLogger.Level.WARNING, "Could not find fragment " + comment.fragment + " at " + comment.ordinal + "( +/- 20)");

            //TOD: Remove this. Only for reenginering
            return fragmentsForDocument.get(comment.ordinal);


        }catch(Exception e){

            PukkaLogger.log(e);
        }

        return new ContractFragment();


    }

    /***************************************************************************
     *
     *          looking up the tag from both the classification tree and custom tags in the database
     *
     *
     *
     * @param className
     * @param organization
     * @param language         -document language
     * @return
     *
     *
     *              TODO: Improvement Refactor: These are copied from DocumentService due to different inheritance trees. Refactor to reuse
     */

    protected String getTag(String className, Organization organization, LanguageInterface language) {

        //String classTag = languageInterface.getClassificationForName(className);

        ClassifierInterface[] classifiers = language.getSupportedClassifiers();

        for (ClassifierInterface classifier : classifiers) {

            if(classifier.getType().getName().equals(className)){

                PukkaLogger.log(PukkaLogger.Level.INFO, "Found static classTag " + className);
                return className;
            }
        }


            // Look in the database for custom tags

        List<FragmentClass> customClasses = organization.getCustomTagsForOrganization();
        try {
            customClasses.addAll(Organization.getnone().getCustomTagsForOrganization());
        } catch (BackOfficeException e) {

            PukkaLogger.log(PukkaLogger.Level.DEBUG, "Ignoring global classifications");
        }

        for (FragmentClass customClass : customClasses) {

            if(customClass.getType().equals(className)){
                PukkaLogger.log(PukkaLogger.Level.DEBUG, "Found custom classTag " + customClass.getName());
                return customClass.getKey().toString();
            }
        }

        return null;
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



    protected String getTagName(String className, Organization organization, LanguageInterface language) {

        //String classTag = languageInterface.getClassificationForName(className);

        ClassifierInterface[] classifiers = language.getSupportedClassifiers();

        for (ClassifierInterface classifier : classifiers) {

            if(classifier.getType().getName().equals(className)){

                PukkaLogger.log(PukkaLogger.Level.INFO, "Found static classTag " + className);
                return classifier.getClassificationName();
            }
        }


            // Look in the database for custom tags

        List<FragmentClass> customClasses = organization.getCustomTagsForOrganization();
        try {
            customClasses.addAll(Organization.getnone().getCustomTagsForOrganization());
        } catch (BackOfficeException e) {

            PukkaLogger.log(PukkaLogger.Level.DEBUG, "Ignoring global classifications");
        }

        for (FragmentClass customClass : customClasses) {

            if(customClass.getKey().toString().equals(className)){
                PukkaLogger.log(PukkaLogger.Level.DEBUG, "Found custom classTag " + customClass.getName());
                return customClass.getName();
            }
        }

        return null;
    }






}


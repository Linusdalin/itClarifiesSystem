package services;

import actions.CanonicalReferenceParser;
import actions.Checklist;
import actions.ChecklistParser;
import analysis.AnalysisFeedbackItem;
import classification.FragmentClass;
import classifiers.ClassifierInterface;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import contractManagement.*;
import dataRepresentation.DBTimeStamp;
import databaseLayer.DBKeyInterface;
import document.*;
import featureTypes.FeatureTypeInterface;
import language.LanguageInterface;
import log.PukkaLogger;
import net.sf.json.JSONObject;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;
import risk.ContractRisk;
import search.Keyword;
import search.KeywordTable;
import userManagement.Organization;
import userManagement.PortalUser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/***************************************************************************''
 *
 *      Base functionality for a document service
 *
 */

public class DocumentService extends ItClarifiesService{

    public static String MODEL_DIRECTORY = "models";


    /*********************************************************************************
     *
     *          Fragment a document
     *
     * @param fileName - the name of the file. Temporary name of the document
     * @param versionInstance - the instance of the document
     * @param fragmenter - The fragmenter to use
     * @throws pukkaBO.exceptions.BackOfficeException
     *
     * //TODO: Create a special class for document/file name to pass around here allowing for more dynamic display (e.g. truncate extensions)
     * //TODO: Accumulate outcome on the analysis and return all the way to frontend
     */


    protected void fragmentDocument(String fileName, ContractVersionInstance versionInstance, FragmentSplitterInterface fragmenter) throws BackOfficeException {

        Contract document = versionInstance.getDocument();
        Project project = document.getProject();

        //AutoNumberer autoNumberer = new AutoNumberer();
        ContractRisk defaultRisk = ContractRisk.getNotSet();
        DBTimeStamp analysisTime = new DBTimeStamp();

        List<AbstractFragment> fragments = fragmenter.getFragments();

       // Create empty list to store the fragments in the list for batch storing in the database

        ContractFragmentTable fragmentsToStore = new ContractFragmentTable();
        fragmentsToStore.createEmpty();

        int fragmentNo = 0;
        boolean isChecklist = false;
        boolean isCanonicalDefinitionTable = false;
        Set<String> newKeywords = new HashSet<String>();  // To store all new keywords

        PukkaLogger.log(PukkaLogger.Level.ACTION, "*******************Phase II: Fragmenting Document");
        PukkaLogger.log(PukkaLogger.Level.INFO, "Found " + fragmenter.getFragments().size() + " abstract fragments from the parsing");

        String imageServer = getImageServer();

        ChecklistParser checklistParser = new ChecklistParser(fragmenter);
        CanonicalReferenceParser canonicalReferenceParser = new CanonicalReferenceParser(fragmenter, document, project);


        for(AbstractFragment aFragment : fragments){

            try{

                AbstractStructureItem aStructureItem = aFragment.getStructureItem();
                DBKeyInterface structureItemKey = null;
                String fragmentName = createNameFromBody(aFragment);
                //int indentation = aStructureItem.getIndentation();
                int indentation = (int)aFragment.getIndentation();
                int structureNo = aStructureItem.getID();
                boolean newStructureItemCreated = false;

                PukkaLogger.log(PukkaLogger.Level.INFO, "fragment " + fragmentNo + ": ("+ aFragment.getStyle().name()+")" + aFragment.getBody() +"     (" +
                        indentation + ": " + aStructureItem.getStructureType().name() + ":" +
                        (aStructureItem.getTopElement() != null ? aStructureItem.getTopElement().getBody() : "--") +")" );


                CellInfo cellinfo = aFragment.getCellInfo();
                if(cellinfo == null)
                    cellinfo = new CellInfo();


                int row = cellinfo.row;
                int column = cellinfo.col;

                // First check if this is an implicit top level fragment that we have not created yet

                if(aFragment.getStyle() == StructureType.IMPLICIT){


                    PukkaLogger.log(PukkaLogger.Level.INFO, "Creating a new implicit structure item");

                    StructureItem item = new StructureItem(
                            "Implicit Structure Item " + structureNo,
                            fragmentNo,
                            versionInstance,
                            project,
                            structureNo,
                            aStructureItem.getStructureType().name(),
                            indentation);

                    item.store();

                    PukkaLogger.log(PukkaLogger.Level.INFO, "  - Stored");

                    /*
                    structureItemKey = item.getKey();
                    aStructureItem.setKey(structureItemKey.toString());  // Store the key here as all fragments under this structure element will need the key


                    ContractFragment fragment = new ContractFragment(
                            "Implicit",                                     //TODO: This should really be an attribute
                            versionInstance.getKey(),
                            structureNo - 1,                        // Using id as key here for quicker lookup
                            fragmentNo++,
                            "implicit",
                            aFragment.getIndentation(),                //TODO: Remove - Deprecated...
                            StructureType.IMPLICIT.name(),
                            defaultRisk.getKey(),
                            0,     // annotation
                            0,     // reference
                            0,     // classificaton
                            column,
                            row

                    );


                    System.out.println("  -- Adding a fragment " + fragment.getName());
                    fragmentsToStore.add(fragment);

                    */

                    indentation++;     // When creating an implicit element, the indentation automatically increases

                }

                //String bodyText = testAddingHeadlineNumber(aFragment, autoNumberer);
                String bodyText = aFragment.getBody();

                if(aFragment.getStyle() == StructureType.IMAGE){

                    bodyText = "";
                    aFragment.setStyle(StructureType.TEXT); // Set it to text, the image will be represented as a text URL
                    for (AbstractImage abstractImage : aFragment.getImages()) {

                        bodyText += abstractImage.getRetrievalTag(imageServer, versionInstance.getKey().toString());

                    }

                }


                // Now see if this is a new top level item. If that is the case, we create a new structure item for the fragment

                StructureItem item = null;
                ContractFragment fragment;

                if(aStructureItem.getTopElement() == aFragment){

                    PukkaLogger.log(PukkaLogger.Level.DEBUG, "Fragment " + aFragment.getBody() + " is a top item on indentation level " + aFragment.getIndentation());

                    item = new StructureItem(
                            bodyText,
                            fragmentNo,
                            versionInstance,
                            project,
                            structureNo,
                            aStructureItem.getStructureType().name(),
                            aStructureItem.getIndentation());

                    item.store();


                }

                // Handle the parsing of checklists in table

                if(aFragment.getStyle() == StructureType.TABLE){

                    if(cellinfo.row == 0 && cellinfo.col == 0 && bodyText.equals("$_CL")){

                        PukkaLogger.log(PukkaLogger.Level.ACTION, "Detected a Checklist in document. Creating checklist");
                        isChecklist = true;
                        bodyText = "";      // Remove the tag. We do not want it in the uploaded table it ws just to trigger the extraction of the checklist

                    }

                    if(cellinfo.row == 0 && cellinfo.col == 0 && bodyText.equals("$_Canonical")){

                        PukkaLogger.log(PukkaLogger.Level.ACTION, "Detected a Canonical Definition table in document.");
                        isCanonicalDefinitionTable = true;
                        bodyText = "";      // Remove the tag. We do not want it in the uploaded table it ws just to trigger the extraction of the checklist

                        canonicalReferenceParser.startNew();
                    }


                    if(isChecklist && cellinfo.row == 0 && cellinfo.col == 1){

                        String checklistName = bodyText;

                        PukkaLogger.log(PukkaLogger.Level.INFO, "Found checklist name " + checklistName);
                        Checklist newChecklist = new Checklist(checklistName, checklistName, checklistName.substring(0, 1),
                                project.getKey(), document.getOwnerId(), document.getCreation().getISODate());
                        newChecklist.store();

                        checklistParser.startNewChecklist(newChecklist);

                    }

                    if(isChecklist && cellinfo.row > 0){

                        if(!checklistParser.hasOpenCheckist()){

                            PukkaLogger.log(PukkaLogger.Level.INFO, "No name found for checklist in cell (0, 1). Aborting parsing checklist");
                            isChecklist = false;
                        }

                        AnalysisFeedbackItem checklistParsingFeedback = checklistParser.parseChecklistCell(aFragment);

                        if(checklistParsingFeedback != null){
                            PukkaLogger.log(PukkaLogger.Level.INFO, checklistParsingFeedback.severity.name()+ ": " + checklistParsingFeedback.message);

                            if(checklistParsingFeedback.severity == AnalysisFeedbackItem.Severity.ABORT){
                                isChecklist = false;

                            }
                        }


                    }


                    if(isCanonicalDefinitionTable && cellinfo.row > 0){

                        AnalysisFeedbackItem feedback = canonicalReferenceParser.parseCell(aFragment);

                        if(feedback != null){
                            PukkaLogger.log(PukkaLogger.Level.INFO, feedback.severity.name()+ ": " + feedback.message);

                            if(feedback.severity == AnalysisFeedbackItem.Severity.ABORT){
                                isCanonicalDefinitionTable = false;

                            }
                        }


                    }


                }
                else{

                    // No more table. Close the checklist

                    if(isChecklist){

                        isChecklist = false;
                        checklistParser.endCurrentChecklist();
                    }
                }


                // We want to ignore empty fragments. They are not really needed in the presentation

                if(aFragment.getStyle() == StructureType.TEXT && bodyText.equals("")){

                    PukkaLogger.log(PukkaLogger.Level.DEBUG, "Ignoring empty fragment");

                }
                else if(isCanonicalDefinitionTable){

                    PukkaLogger.log(PukkaLogger.Level.DEBUG, "Ignoring item in canonical reference table");

                }
                else{

                    // Finally we create the actual fragment

                    fragment = new ContractFragment(
                            fragmentName,
                            versionInstance.getKey(),
                            project.getKey(),
                            aStructureItem.getID(),
                            fragmentNo++,
                            bodyText,
                            aFragment.getIndentation(),
                            aFragment.getStyle().name(),
                            defaultRisk,
                            0,     // annotation
                            0,     // reference
                            0,     // classificaton
                            0,     // actions
                            column,
                            row,
                            toJSON(cellinfo)

                    );

                    fragmentsToStore.add(fragment);
                    System.out.println("  ***** Adding a fragment " + fragment.getName() + " with ordinal " + (fragmentNo - 1));


                }

            }catch(Exception e){

                e.printStackTrace(System.out);
                PukkaLogger.log(PukkaLogger.Level.FATAL, "Internal Error analysing fragment \"" + aFragment.getBody() + "\" in document "+ document.getName()+ "Error:" + e.getMessage());
            }


            // Store all new keywords from the fragment
            if(aFragment.getKeywords() != null)
                newKeywords.addAll(aFragment.getKeywords());

        }

        // Complete the checklist if it is still open

        if(isChecklist)
            checklistParser.endCurrentChecklist();
        if(isCanonicalDefinitionTable)
            canonicalReferenceParser.endCurrentTable();


        PukkaLogger.log(PukkaLogger.Level.INFO, "Storing " + fragmentsToStore.getCount() + " fragments for the analysis of the document " + document);
        fragmentsToStore.store(); // Save all

        // Now we can map the checklist sources to the correct fragment id:s

        //TODO: This kind of cast should be generated automatically for all tables
        checklistParser.mapItemSources((List<ContractFragment>)(List<?>) fragmentsToStore.getValues());
        canonicalReferenceParser.mapItemSources((List<ContractFragment>)(List<?>) fragmentsToStore.getValues());



        // Store the keywords

        PukkaLogger.log(PukkaLogger.Level.INFO, "Storing keywords for the analysis of the document " + document);
        storeKeyWords(newKeywords, versionInstance, document, project);

        // Handle comments. The comments are extracted by the parsing and stored with a fragment id,
        // so left to do is to find the corresponding fragment.

        PukkaLogger.log(PukkaLogger.Level.INFO, "** Found " + fragmenter.getComments().size() + " comments in the document" );

        for(AbstractComment aComment : fragmenter.getComments()){

            ContractFragment fragment = new ContractFragment(new LookupItem()
                    .addFilter(new ColumnFilter   (ContractFragmentTable.Columns.Ordinal.name(), aComment.getFragmentId() - 1))
                    .addFilter(new ReferenceFilter(ContractFragmentTable.Columns.Version.name(), versionInstance.getKey())));

            if(!fragment.exists()){

                // Debugging only
                for(ContractFragment f : versionInstance.getFragmentsForVersion()){
                    System.out.println("Fragment: " + f.getName() + "id" + f.getOrdinal());
                }

                PukkaLogger.log(PukkaLogger.Level.FATAL, "Could not find fragment for comment. Fragment id: " + aComment.getFragmentId() + "("+ aComment.getComment()+")");

            }
            else{

                ContractAnnotation annotation = new ContractAnnotation(
                        PortalUser.getExternalUser() + "@" + analysisTime.getSQLTime().toString(),
                        fragment,
                        (long)1,
                        aComment.getComment(),
                        PortalUser.getExternalUser(),
                        versionInstance,
                        aComment.getAnchor(),
                        0,                          //TODO: Anchor position not implemented
                        analysisTime.getSQLTime().toString());

                annotation.store();

                fragment.setAnnotationCount(fragment.getAnnotationCount() + 1);
                fragment.update();


            }

        }

    }



    /*****************************************************************************************
     *
     *          Get the server where the images are stored.
     *
     *
     * @return
     */

    private String getImageServer() {

        String ID = SystemProperty.applicationId.get();
        String serviceAccountName = AppIdentityServiceFactory.getAppIdentityService().getServiceAccountName();

        if(serviceAccountName.contains("localhost"))

            return"http://localhost:8080";

        return "https://" + ID + ".appspot.com";

    }


    private static boolean startsWithNumber(String body) {

        return body.matches("^(\\d+[\\s]*\\.)[\\s]*.*");
    }


    private String toJSON(CellInfo cellinfo) {

        JSONObject json = new JSONObject()
                .put("backgroundColour", "#" + cellinfo.colour)
                .put("colSpan", cellinfo.span.cols)
                .put("rowSpan", cellinfo.span.rows)
                .put("width", cellinfo.width)
                .put("wrap", cellinfo.wrap)

        ;
        return json.toString();

    }

    /****************************************************************************************
     *
     *              Store the keywords from the analysis
     *
     *
     * @param newKeywords
     * @param version
     * @param document
     * @param project
     * @throws BackOfficeException
     */

    private void storeKeyWords(Set<String> newKeywords, ContractVersionInstance version, Contract document, Project project) throws BackOfficeException {

        KeywordTable keywordTable = new KeywordTable();
        keywordTable.createEmpty();

        for(String k : newKeywords){

            Keyword keyword = new Keyword(k, version, document,  project);
            keywordTable.add(keyword);
            PukkaLogger.log(PukkaLogger.Level.INFO, "Added a bold keyword " + keyword.getKeyword());

        }

        keywordTable.store();

    }

    private String createNameFromBody(AbstractFragment aFragment) {

        return (aFragment.getBody().length() < 30 ? aFragment.getBody() : aFragment.getBody().substring(0, 29));

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
     *          //TODO: Refactor: Should not need duplicate lookups here
     *
     */

    protected String getTag(String className, Organization organization, LanguageInterface language) {

        //String classTag = languageInterface.getClassificationForName(className);

        ClassifierInterface[] classifiers = language.getSupportedClassifiers();

        for (ClassifierInterface classifier : classifiers) {

            if(classifier.getType().getName().equals(className)){

                PukkaLogger.log(PukkaLogger.Level.INFO, "Found static classTag " + className);
                return classifier.getType().getName();   // This should be the #TAG as this is the key to the frontend
            }
        }

        classifiers = language.getDefinitionUsageClassifiers();

        for (ClassifierInterface classifier : classifiers) {

            if(classifier.getType().getName().equals(className)){

                PukkaLogger.log(PukkaLogger.Level.INFO, "Found static classTag " + className);
                return classifier.getType().getName();   // This should be the #TAG as this is the key to the frontend
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
                return customClass.getKey().toString();
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
                return className;
            }
        }

        classifiers = language.getDefinitionUsageClassifiers();

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

            if(customClass.getKey().toString().equals(className)){
                PukkaLogger.log(PukkaLogger.Level.DEBUG, "Found custom classTag " + customClass.getName());
                return customClass.getName();
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




    public static void main(String[] args){

        String test =  "1. test";
        System.out.println(test +  startsWithNumber(test));

        test =  "7 . \tInformation om uppha";
        System.out.println(test +  startsWithNumber(test));

        test =  "1.2 test";
        System.out.println(test +  startsWithNumber(test));

        test =  "test";
        System.out.println(test +  startsWithNumber(test));

    }



}



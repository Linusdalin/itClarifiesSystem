package services;

import actions.ActionStatus;
import actions.Checklist;
import actions.ChecklistItem;
import analysis.AnalysisFeedback;
import analysis.AnalysisFeedbackItem;
import classification.FragmentClass;
import classifiers.ClassifierInterface;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import contractManagement.*;
import dataRepresentation.DBTimeStamp;
import dataRepresentation.DataObjectInterface;
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
import search.SearchManager2;
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
        Set<String> newKeywords = new HashSet<String>();  // To store all new keywords

        PukkaLogger.log(PukkaLogger.Level.ACTION, "*******************Phase II: Fragmenting Document");
        PukkaLogger.log(PukkaLogger.Level.INFO, "Found " + fragmenter.getFragments().size() + " abstract fragments from the parsing");

        SearchManager2 searchManager = new SearchManager2(project, document.getOwner());

        String imageServer = getImageServer();


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

                // We want to ignore empty fragments. They are not really needed in the presentation

                if(aFragment.getStyle() == StructureType.TEXT && aFragment.getBody().equals("")){

                    PukkaLogger.log(PukkaLogger.Level.DEBUG, "Ignoring empty fragment");

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

        PukkaLogger.log(PukkaLogger.Level.INFO, "Storing " + fragmentsToStore.getCount() + " fragments for the analysis of the document " + document);
        fragmentsToStore.store(); // Save all


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


    private static final String[] checklistHeadlines = {"Id", "Subject", "Compliance Requirement","#Tag", "Comment"};

    /******************************************************************************'
     *
     *      parsing a checklist
     *
     * @param doc
     *
     *      //TODO: Optimization: Add btch store here
     *
     */


    public AnalysisFeedback parseChecklist(FragmentSplitterInterface doc, Checklist checklist) {

        List<AbstractFragment> fragments = doc.getFragments();
        AnalysisFeedback feedback = new AnalysisFeedback();

        try{

            ChecklistItem currentItem = new ChecklistItem();

            for (AbstractFragment fragment : fragments) {

                CellInfo cellInfo = fragment.getCellInfo();

                if(cellInfo == null){

                    PukkaLogger.log(PukkaLogger.Level.INFO, "Ignoring non cell fragment " + fragment.getBody());
                }
                else{

                    PukkaLogger.log(PukkaLogger.Level.INFO, "Handling cell fragment " + fragment.getBody());

                    // Handle table data for the checklist

                    if(cellInfo.row == 0 ){

                        // The expectation is that the first row is a headline

                        if(cellInfo.col < checklistHeadlines.length && !fragment.getBody().equalsIgnoreCase(checklistHeadlines[cellInfo.col])){

                            feedback.add(new AnalysisFeedbackItem(AnalysisFeedbackItem.Severity.ABORT, "Expected to find " + checklistHeadlines[cellInfo.col] + " as title in cell (" + cellInfo.row +", " +  cellInfo.col + "). Found " + fragment.getBody(), cellInfo.row));
                            return feedback;
                        }

                    }


                    if(cellInfo.row > 1 && cellInfo.col == 0){

                        // New row, store the old row

                        PukkaLogger.log(PukkaLogger.Level.INFO, "Storing a checklist item");
                        feedback.add(new AnalysisFeedbackItem(AnalysisFeedbackItem.Severity.INFO, "Created checklist item "+ currentItem.getName()+" with id " + currentItem.getId(), cellInfo.row));
                        currentItem.store();
                    }


                    if(cellInfo.row > 0 && cellInfo.col == 0){

                        if(fragment.getBody().equals("")){

                            feedback.add(new AnalysisFeedbackItem(AnalysisFeedbackItem.Severity.INFO, "Ended checklist ( no more id... )", cellInfo.row));

                        }

                        // New row, create a new item
                        currentItem = new ChecklistItem((long)0, (long)0, "name", "text", "comment", checklist.getKey(),  null, null,
                                checklist.getProjectId(), "", ActionStatus.getOpen(), new DBTimeStamp().getSQLTime().toString());

                        try{

                            int id = new Double(fragment.getBody()).intValue();
                            currentItem.setId(id);

                        }catch(NumberFormatException e){

                            feedback.add(new AnalysisFeedbackItem(AnalysisFeedbackItem.Severity.ABORT, "Expected to find number (id) in cell (" + cellInfo.row +", " +  cellInfo.col + "). Found " + fragment.getBody(), cellInfo.row));
                            return feedback;
                        }
                    }

                    if(cellInfo.row > 0 && cellInfo.col == 1){

                        currentItem.setName(fragment.getBody());
                    }

                    if(cellInfo.row > 0 && cellInfo.col == 2){

                        currentItem.setDescription(fragment.getBody());
                    }

                    if(cellInfo.row > 0 && cellInfo.col == 3){

                        String tag = fragment.getBody();

                        if(tag.startsWith("#"))
                            feedback.add(new AnalysisFeedbackItem(AnalysisFeedbackItem.Severity.ABORT, "Expected to find #TAG in tag column. Found " + tag, cellInfo.row));

                        currentItem.setTagReference(tag.substring(1)); // Remove #
                    }

                    if(cellInfo.row > 0 && cellInfo.col == 4){

                        currentItem.setComment(fragment.getBody());
                    }


                }
            }

        }catch(BackOfficeException e){

            PukkaLogger.log( e );
            feedback.add(new AnalysisFeedbackItem(AnalysisFeedbackItem.Severity.ABORT, "Internal Error:" + e.narration, 0));

        }catch(Exception e){

            PukkaLogger.log( e );
            feedback.add(new AnalysisFeedbackItem(AnalysisFeedbackItem.Severity.ABORT, "Internal Error: " + e.getLocalizedMessage(), 0));

        }


        return feedback;

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



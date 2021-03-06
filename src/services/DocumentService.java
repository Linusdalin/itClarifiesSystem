package services;

import actions.*;
import analysis.*;
import analysis.deferrance.DeferenceHandler;
import analysis.deferrance.NextFragment;
import analysis2.AnalysisException;
import analysis2.NewAnalysisOutcome;
import classification.Classifier;
import classification.FragmentClass;
import classification.FragmentClassification;
import classification.FragmentClassificationTable;
import classifiers.Classification;
import classifiers.ClassifierInterface;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import contractManagement.*;
import crossReference.*;
import dataRepresentation.DBTimeStamp;
import document.*;
import featureTypes.FeatureTypeInterface;
import featureTypes.FeatureTypeTree;
import fileHandling.BlobRepository;
import fileHandling.RepositoryFileHandler;
import fileHandling.RepositoryInterface;
import language.English;
import language.LanguageCode;
import language.LanguageInterface;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import project.Project;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;
import risk.*;
import search.Keyword;
import search.KeywordTable;
import search.SearchManager2;
import system.Analyser;
import userManagement.Organization;
import userManagement.PortalUser;
import versioning.Transposer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
    private static final int RELEVANCE_THRESHOLD = 40;          // What is the relevance needed to actually create an entry in the database

    private String modelDirectory = MODEL_DIRECTORY; // Default value


    /******************************************************************************************
     *
     *          Fragment an uploaded document
     *
     * @param versionInstance   - the instance of the document
     * @param fragmenter        - The fragmenter to use
     * @return                  - Feedback items
     * @throws BackOfficeException
     *
     *       //TODO: Create a special class for document/file name to pass around here allowing for more dynamic display (e.g. truncate extensions)
     *
     */


    protected ParseFeedback fragmentDocument(ContractVersionInstance versionInstance, FragmentSplitterInterface fragmenter) throws BackOfficeException {

        ParseFeedback fragmentingFeedback = new ParseFeedback();

        Contract document = versionInstance.getDocument();
        Project project = document.getProject();

        ContractRisk defaultRisk = ContractRisk.getNotSet();
        DBTimeStamp analysisTime = new DBTimeStamp();

        List<AbstractFragment> fragments = fragmenter.getFragments();

        Classifier classifier = new Classifier(project, versionInstance, analysisTime);

       // Create empty list to store the fragments in the list for batch storing in the database

        ContractFragmentTable fragmentsToStore = new ContractFragmentTable();
        fragmentsToStore.createEmpty();

        int fragmentNo = 0;
        boolean isChecklist = false;
        boolean isCanonicalDefinitionTable = false;
        Set<String> newKeywords = new HashSet<String>();  // To store all new keywords

        PukkaLogger.log(PukkaLogger.Level.ACTION, "*******************Phase II: Fragmenting Document");
        PukkaLogger.log(PukkaLogger.Level.DEBUG, "Found " + fragmenter.getFragments().size() + " abstract fragments from the parsing");

        String imageServer = getImageServer();

        ChecklistParser checklistParser = new ChecklistParser(fragmenter, document.getOwner());
        CanonicalReferenceParser canonicalReferenceParser = new CanonicalReferenceParser(fragmenter, document, project);
        int paragraphCount = 0;  //Count the original aFragmentParagraphs to store in the fragment. Used for merging

        for(AbstractFragment aFragment : fragments){

            try{

                AbstractStructureItem aStructureItem = aFragment.getStructureItem();
                String fragmentName = createNameFromBody(aFragment);
                int indentation = (int)aFragment.getIndentation();
                int structureNo = aStructureItem.getID();

                ParseFeedbackItem feedback = null;

                PukkaLogger.log(PukkaLogger.Level.DEBUG, "fragment " + fragmentNo + ": ("+ aFragment.getStyle().name()+")" + aFragment.getBody() +"     (" +
                        indentation + ": " + aStructureItem.getStructureType().name() + ":" +
                        (aStructureItem.getTopElement() != null ? aStructureItem.getTopElement().getBody() : "--") +")" );


                CellInfo cellinfo = aFragment.getCellInfo();
                if(cellinfo == null)
                    cellinfo = new CellInfo();


                //int row = cellinfo.row;
                //int column = cellinfo.col;

                // First check if this is an implicit top level fragment that we have not created yet

                if(aFragment.getStyle() == StructureType.IMPLICIT){


                    PukkaLogger.log(PukkaLogger.Level.DEBUG, "Creating a new implicit structure item");

                    StructureItem item = new StructureItem(
                            "Implicit Structure Item " + structureNo,
                            fragmentNo,
                            versionInstance,
                            project,
                            structureNo,
                            aStructureItem.getStructureType().name(),
                            indentation);

                    item.store();

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
                JSONArray imageJSON = new JSONArray();

                if(aFragment.getStyle() == StructureType.IMAGE){

                    bodyText = "";
                    //aFragment.setStyle(StructureType.TEXT); // Set it to text, the image will be represented as a text URL
                    for (AbstractImage abstractImage : aFragment.getImages()) {


                        bodyText += abstractImage.getRetrievalTag(imageServer, versionInstance.getKey().toString());
                        imageJSON.put(abstractImage.toJSON());

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
                        bodyText = "";      // Remove the tag. We do not want it in the uploaded table it was just to trigger the extraction of the checklist

                    }

                    if(cellinfo.row == 0 && cellinfo.col == 0 && bodyText.equals("$_Canonical")){

                        PukkaLogger.log(PukkaLogger.Level.ACTION, "Detected a Canonical Definition table in document.");
                        isCanonicalDefinitionTable = true;
                        bodyText = "";      // Remove the tag. We do not want it in the uploaded table it ws just to trigger the extraction of the checklist

                        canonicalReferenceParser.startNew();
                    }


                    if(isChecklist && cellinfo.row == 0 && cellinfo.col == 1){

                        String checklistName = bodyText;

                        PukkaLogger.log(PukkaLogger.Level.INFO, "Found checklist named " + checklistName);

                        Checklist newChecklist = new Checklist(checklistName, checklistName, checklistName.substring(0, 1),
                                project.getKey(), document.getOwnerId(), document.getCreation().getISODate());
                        newChecklist.store();

                        fragmentingFeedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.INFO, "Creating new checklist \"" + checklistName + "\"", 0));

                        feedback = checklistParser.startNewChecklist(newChecklist);

                        if(feedback != null){

                            fragmentingFeedback.add(feedback);
                            PukkaLogger.log(PukkaLogger.Level.INFO, feedback.severity.name()+ ": " + feedback.message);

                            if(feedback.severity == ParseFeedbackItem.Severity.ABORT){
                                isChecklist = false;

                            }
                        }

                    }

                    if(isChecklist && cellinfo.row > 0){

                        if(!checklistParser.hasOpenCheckist()){

                            PukkaLogger.log(PukkaLogger.Level.INFO, "No name found for checklist in cell (0, 1). Aborting parsing checklist");
                            isChecklist = false;
                        }

                        feedback = checklistParser.parseChecklistCell(aFragment);

                        if(feedback != null){

                            fragmentingFeedback.add(feedback);
                            PukkaLogger.log(PukkaLogger.Level.INFO, feedback.severity.name()+ ": " + feedback.message);

                            if(feedback.severity == ParseFeedbackItem.Severity.ABORT){
                                isChecklist = false;

                            }
                        }


                    }


                    if(isCanonicalDefinitionTable && cellinfo.row > 0){

                        feedback = canonicalReferenceParser.parseCell(aFragment);

                        if(feedback != null){

                            fragmentingFeedback.add(feedback);
                            PukkaLogger.log(PukkaLogger.Level.INFO, feedback.severity.name()+ ": " + feedback.message);

                            if(feedback.severity == ParseFeedbackItem.Severity.ABORT){
                                isCanonicalDefinitionTable = false;

                            }
                        }


                    }


                }
                else{

                    // No more table. Close the checklist

                    if(isChecklist){

                        isChecklist = false;
                        feedback = checklistParser.endCurrentChecklist();

                        if(feedback != null){

                            fragmentingFeedback.add(feedback);
                            PukkaLogger.log(PukkaLogger.Level.INFO, feedback.severity.name() + ": " + feedback.message);

                        }

                    }
                }


                // We want to ignore empty fragments. They are not really needed in the presentation

                if(aFragment.getStyle() == StructureType.TEXT && bodyText.equals("")){

                    PukkaLogger.log(PukkaLogger.Level.DEBUG, "Ignoring empty fragment");

                }
                else if(isCanonicalDefinitionTable){

                    PukkaLogger.log(PukkaLogger.Level.DEBUG, "Ignoring item in canonical reference table");

                }
                else if(isChecklist){

                    PukkaLogger.log(PukkaLogger.Level.DEBUG, "Ignoring item in checklist");

                }
                else if(feedback != null && feedback.severity == ParseFeedbackItem.Severity.HIDE){

                    PukkaLogger.log(PukkaLogger.Level.DEBUG, "Ignoring item in canonical reference table");

                }
                else{

                    // Finally we create the actual fragment

                    fragment = new ContractFragment(
                            fragmentName,
                            versionInstance.getKey(),
                            project.getKey(),
                            paragraphCount,
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
                            cellinfo.col,
                            cellinfo.row,
                            cellinfo.rowWidth,
                            cellinfo.toJSON(),
                            imageJSON.toString()

                    );

                    fragmentsToStore.add(fragment);
                    PukkaLogger.log(PukkaLogger.Level.INFO, "  ***** Adding a fragment ( ordinal "+ (fragmentNo - 1)+")" + fragment.getName());


                }

            }catch(Exception e){

                e.printStackTrace(System.out);
                PukkaLogger.log(PukkaLogger.Level.FATAL, "Internal Error analysing fragment \"" + aFragment.getBody() + "\" in document "+ document.getName()+ "Error:" + e.getMessage());
            }


            // Store all new keywords from the fragment
            if(aFragment.getKeywords() != null)
                newKeywords.addAll(aFragment.getKeywords());

            paragraphCount++;
        }

        // Complete the checklist if it is still open

        if(isChecklist){

            ParseFeedbackItem feedback = checklistParser.endCurrentChecklist();

            if(feedback != null){

                fragmentingFeedback.add(feedback);
                PukkaLogger.log(PukkaLogger.Level.INFO, feedback.severity.name() + ": " + feedback.message);

            }


        }

        if(isCanonicalDefinitionTable){

            ParseFeedbackItem feedback =  canonicalReferenceParser.endCurrentTable();

            if(feedback != null){

                fragmentingFeedback.add(feedback);
                PukkaLogger.log(PukkaLogger.Level.INFO, feedback.severity.name() + ": " + feedback.message);

            }


        }


        PukkaLogger.log(PukkaLogger.Level.ACTION, "Storing " + fragmentsToStore.getCount() + " fragments for the analysis of the document " + document);
        fragmentsToStore.store(); // Save all

        // Now we can map the checklist sources to the correct fragment id:s

        PukkaLogger.log(PukkaLogger.Level.INFO, "Mapping sources for canonical references and check list items in the document " + document);

        checklistParser.mapItemSources((List<ContractFragment>)(List<?>) fragmentsToStore.getValues());
        canonicalReferenceParser.mapItemSources(project);



        // Store the keywords

        PukkaLogger.log(PukkaLogger.Level.ACTION, "Storing "+ newKeywords.size()+" keywords from the analysis of the document " + document);
        storeKeyWords(newKeywords, versionInstance, document, project);

        // Handle comments. The comments are extracted by the parsing and stored with a fragment id,
        // so left to do is to find the corresponding fragment.

        PukkaLogger.log(PukkaLogger.Level.INFO, "Handling comments in the document" );
        ParseFeedback feedbackFromComments = handleComments(fragmenter.getComments(), classifier, project, document, versionInstance, analysisTime);

        fragmentingFeedback.add(feedbackFromComments);
        classifier.store();

        System.out.println(" --- Just checking again. Got " + feedbackFromComments.getNo() + " feedback items in handleComments()");


        return fragmentingFeedback;

    }

    /********************************************************************************************
     *
     *
     * @param comments          - list of all comments in the document
     * @param project           - Current project
     * @param document          - the document
     * @param version   - The document version
     * @param analysisTime      - The time of analysis
     *
     */


    /***********************************************************************************************
     *
     *          Handle comments. For a comment in the document there are a number of actions to take:
     *
     *          #TAG            - will result in creating a classification here
     *          #Definition     - will create a definition
     *          #RISK:status    - will set the status of a risk (overriding any existing status
     *          other           - Create an annotation
     *
     *          //TODO: Improvement Module: The tags and keywords should not be looked up statically
     *
     * @param comments              - list of all the comments
     * @param classifier            - object to set classification
     * @param project               - the current project
     * @param document              - the current document
     * @param version               - the current version
     * @param analysisTime          - time of analysis
     * @return                      - feedback to the end user
     * @throws BackOfficeException
     */

    private ParseFeedback handleComments(List<AbstractComment> comments, Classifier classifier, Project project, Contract document, ContractVersionInstance version, DBTimeStamp analysisTime) throws BackOfficeException{

        PortalUser user = PortalUser.getExternalUser();
        LanguageInterface languageForImport = new English();
        Organization organization = project.getOrganization();
        List<Definition> definitionsForProject = project.getDefinitionsForProject();


        PukkaLogger.log(PukkaLogger.Level.INFO, "Handle comments: There are " + comments.size() + " comments in the document " + document.getName());
        ParseFeedback feedback = new ParseFeedback();

        for(AbstractComment aComment : comments){

            try{

                PukkaLogger.log(PukkaLogger.Level.DEBUG, "Lookup Comment: "+ aComment.getComment()+". (Paragraph id: " + aComment.getFragmentId() + ")");

                ContractFragment fragment = new ContractFragment(new LookupItem()
                        .addFilter(new ColumnFilter(ContractFragmentTable.Columns.ParagraphId.name(), aComment.getFragmentId()))
                        .addFilter(new ReferenceFilter(ContractFragmentTable.Columns.Version.name(), version.getKey())));

                if(!fragment.exists()){

                    ParseFeedbackItem notFound = new ParseFeedbackItem(ParseFeedbackItem.Severity.ERROR,
                            "Could not find fragment for comment. Fragment id: " + aComment.getFragmentId() + "("+ aComment.getComment()+")",
                            aComment.getFragmentId());

                    PukkaLogger.log(PukkaLogger.Level.FATAL, notFound.message);
                    feedback.add(notFound);

                    // Debugging only
                    for(ContractFragment f : version.getFragmentsForVersion()){
                        System.out.println(" - Fragment: " + f.getName() + "  (id" + f.getOrdinal() + ")");
                    }

                    continue;
                }

                PukkaLogger.log(PukkaLogger.Level.INFO, "Found fragment "+ fragment.getName() +" for comment " + aComment.getComment() +" Fragment id: " + aComment.getFragmentId());


                // Now see if the comment is really intended as a classification

                if(isClassification(aComment)){

                    boolean isBlocking = getIsNegativeClassification(aComment);

                    String classificationTag = firstWord(aComment.getComment());

                    if(isBlocking){
                        classificationTag = classificationTag.substring( 1 );  // Remove the ! prefix

                        //System.out.println(" --- found blocking of tag " + classificationTag);
                    }


                    // First check if this is a risk

                    if(ImportedRiskClassification.isRisk(aComment.getComment())){

                        ImportedRiskClassification risk = new ImportedRiskClassification(aComment.getComment());
                        PukkaLogger.log(PukkaLogger.Level.INFO, "found a risk classification " + risk.toString());

                        new RiskFlagServlet().classifyRisk(risk.getRisk(), fragment, aComment.getAnchor(), -1, version, document, user, risk.getDescription(), analysisTime, true);
                        continue;
                    }

                    String keywords="", name="";
                    FeatureTypeInterface featureType = getFeatureTypeByName(classificationTag, languageForImport);
                    if(featureType != null){

                        // The classification is a feature type. Get name and keyword from here

                        keywords = featureType.createKeywordString("External");
                        PukkaLogger.log(PukkaLogger.Level.INFO, "Found a "+(isBlocking ? "blocking": "")+" classification " + aComment.getComment() + " in document comment. for text "+
                                aComment.getAnchor()+" in fragment " + fragment.getName());
                        name=featureType.getName();
                    }
                    else{

                        List<FragmentClass> customClasses = organization.getCustomTagsForOrganization();
                        System.out.println(" --- Checking for classification " + classificationTag + " in "+ customClasses.size()+" organization specific tags...");

                        boolean found = false;
                        for (FragmentClass customClass : customClasses) {

                            //System.out.println("      --- '" + customClass.getName() + "'");

                            if(classificationTag.equals(customClass.getName().substring(1))){

                                name = customClass.getName();
                                keywords = "External";
                                found = true;
                            }
                        }

                        if(!found){

                            ParseFeedbackItem notFound = new ParseFeedbackItem(ParseFeedbackItem.Severity.WARNING,
                                    "Could not find classification for " + aComment.getComment(),
                                    aComment.getFragmentId());

                            PukkaLogger.log(PukkaLogger.Level.WARNING, notFound.message);
                            feedback.add(notFound);
                            continue;
                        }

                    }



                    FragmentClassification classification = new FragmentClassification(
                            fragment.getKey(),
                            name,
                            0,              // requirement level not implemented
                            0,              // applicable phase not implemented
                            (isBlocking ? FragmentClassification.BLOCKING : FragmentClassification.IMPORTED),
                            "",
                            keywords,
                            user.getKey(),
                            version.getKey(),
                            project.getKey(),
                            aComment.getAnchor(),
                            fragment.getText().indexOf(aComment.getAnchor()),
                            aComment.getAnchor().length(),
                            Significance.MATCH_SIGNIFICANCE,
                            "external import",
                            analysisTime.getSQLTime().toString());

                    classifier.addClassification(classification, fragment, definitionsForProject);

                    continue;
                }


                //Else it is a regular annotation to import

                PukkaLogger.log(PukkaLogger.Level.INFO, "Found a classification "+ aComment.getComment()+" in document comment.");

                ContractAnnotation annotation = new ContractAnnotation(
                        user + "@" + analysisTime.getSQLTime().toString(),
                        fragment,
                        (long)1,
                        aComment.getComment(),
                        PortalUser.getExternalUser(),
                        version,
                        project,
                        aComment.getAnchor(),
                        0,                          //TODO: Anchor position not implemented
                        analysisTime.getSQLTime().toString());

                annotation.store();

                fragment.setAnnotationCount(fragment.getAnnotationCount() + 1);
                fragment.update();


            }catch(BackOfficeException e){

                PukkaLogger.log( e );
            }


        }

        System.out.println(" --- Just checking. Got " + feedback.getNo() + " feedback items in handleComments()");

        return feedback;

    }

    private boolean getIsNegativeClassification(AbstractComment aComment) {

        return aComment.getComment().trim().startsWith("!#");
    }

    private String firstWord(String comment) {

        comment = comment.trim();
        comment = comment.substring(1);

        int firstSpace = comment.indexOf(" ");
        if(firstSpace < 0)
            return comment;

        return comment.substring(0, firstSpace);

    }

    /**************************************************************************
     *
     *          This is more "looks like classification"
     *
     *          It is used to be able to produce a warning when
     *          referencing an unknown classification
     *
     *          # is a regular tag
     *          !# is a blocking tag
     *
     *
     * @param aComment   - the comment
     * @return           - yes/no
     */

    private boolean isClassification(AbstractComment aComment) {

        return aComment.getComment().startsWith("#") || aComment.getComment().startsWith("!#");

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
     *          #TAG -> #Tag
     *
     *
     * @param className        - the actual Tag
     * @param organization     - the org for lookup in the database
     * @param language         - document language
     *
     * @return                 - the localizad name of the tag
     *
     *
     *
     */

    public static String getLocalizedTag(String className, Organization organization, LanguageInterface language) {

        //String classTag = languageInterface.getClassificationForName(className);

        ClassifierInterface[] classifiers = language.getAllClassifiers();

        for (ClassifierInterface classifier : classifiers) {

            //System.out.println(" --- Comparing classes " + classifier.getType().getName() + " and " + className);

            if(classifier.getType().getName().equals(className)){

                PukkaLogger.log(PukkaLogger.Level.INFO, "Found static classTag " + className);
                return classifier.getClassificationName();
            }
        }


            // Look in the database for custom tags

        if(organization != null){

            List<FragmentClass> customClasses = organization.getCustomTagsForOrganization();

            for (FragmentClass customClass : customClasses) {

                if(customClass.getName().equals(className)){
                    PukkaLogger.log(PukkaLogger.Level.DEBUG, "Found custom classTag " + customClass.getName());
                    return customClass.getName().substring(1);
                }
            }
        }


        return null;
    }

    public static String getTagName(String className, Organization organization, LanguageInterface language) {

        //String classTag = languageInterface.getClassificationForName(className);

        ClassifierInterface[] classifiers = language.getAllClassifiers();

        for (ClassifierInterface classifier : classifiers) {

            if(classifier.getType().getName().equals(className)){

                PukkaLogger.log(PukkaLogger.Level.INFO, "Found static classTag " + className);
                return className;
            }
        }



            // Look in the database for custom tags

        if(organization != null){

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

        }


        return null;
    }


    public static FeatureTypeInterface getFeatureTypeByTag(String className, LanguageInterface languageForDocument) {



        ClassifierInterface[] classifiers = languageForDocument.getAllClassifiers();

        for (ClassifierInterface classifier : classifiers) {

            if(classifier.getType().getName().equals(className)){

                PukkaLogger.log(PukkaLogger.Level.INFO, "Found static classTag " + className);
                return classifier.getType();
            }
        }

        return null;
    }

    public static FeatureTypeInterface getFeatureTypeByName(String className, LanguageInterface languageForDocument) {

        ClassifierInterface[] classifiers = languageForDocument.getAllClassifiers();

        for (ClassifierInterface classifier : classifiers) {

            //System.out.println("  --- matching " + classifier.getClassificationName() + " and " + className);

            if(classifier.getClassificationName().equals(className)){

                PukkaLogger.log(PukkaLogger.Level.DEBUG, "Found static classTag " + className);
                return classifier.getType();
            }
        }

        return null;
    }



    /***************************************************************************
     *
     *              Handle the result from the analysis
     *
     * @param analysisResult - the feature definitions from the analysis
     * @param fragment - the data base fragment to update with classifications and references from analysis
     * @param deference
     * @param project
     * @param analysisTime - time for the analysis
     * @param version
*
*
     *
     */


    protected NewAnalysisFeedback handleResult(NewAnalysisOutcome analysisResult,
                                               Classifier classifier,
                                               ContractFragment fragment,
                                               DeferenceHandler deference, Project project, DBTimeStamp analysisTime,
                                               SearchManager2 searchManager,
                                               AbstractDocument aDocument, List<Definition> definitionsForProject,
                                               ContractVersionInstance version) throws BackOfficeException{

        boolean updated = false;

        // Counters for updating the fragment

        int classifications = 0;
        int references = 0;
        int annotations = 0;
        int risks = 0;

        // First handle any deferred actions from previous fragment

        analysisResult = deference.activateDeferences(analysisResult, fragment);

        FragmentClassification fragmentClassification;


        ContractRisk defaultRisk = ContractRisk.getUnknown();
        PortalUser system = PortalUser.getSystemUser();

        for(Classification classification : analysisResult.getClassifications()){

            try{

                FeatureTypeInterface type = classification.getType();
                FeatureTypeInterface parent = type.getParent();


                /**************************************
                        Reference

                 */


                if(type.getName().equals(FeatureTypeTree.Reference.getName())){

                    // The analysis has classified it as a reference. We create the reference here as open.
                    // In the second phase we go through all the open references and try to close them.
                    // We use the semantic extraction that will be the


                    PukkaLogger.log(PukkaLogger.Level.ACTION, "*** Creating reference for fragment " + fragment.getName() + "(" + classification.getExtraction().getSemanticExtraction() + ")");

                    ReferenceType refType = ReferenceType.getOpen();

                    Reference reference = new Reference(
                            classification.getExtraction().getSyntacticExtraction(),
                            fragment.getKey(),
                            null,
                            version.getKey(),
                            project.getKey(),
                            refType,
                            classification.getExtraction().getSyntacticExtraction(),
                            0,                          //TODO: Anchor position not implemented
                            system.getKey()
                    );
                    reference.store();
                    references++;
                    updated = true;
                    continue;
                }


                /**************************************
                        Definition Source

                 */


                if(type.getName().equals(FeatureTypeTree.DEFINITION.getName())){

                    // The analysis has classified a definition Source.
                    // We create a definition and also add a definition tag. (This may be removed
                    // later when definitions are properly displayed and searchable from the frontend)

                    // First we check for a definition to defer

                    if(classification.getPass() != Analyser.DEFERENCE_PASS && classification.getTag().equals("LeftColumn")){


                            if(isFirstRow(fragment)){

                                PukkaLogger.log(PukkaLogger.Level.INFO, "*** Ignoring deferrence of definition creation for fragment " + fragment.getName() + "(" + fragment.getOrdinal() + ") First Row");

                            }else{

                                PukkaLogger.log(PukkaLogger.Level.ACTION, "*** Deferring definition creation for fragment " + fragment.getName() + "(" + fragment.getOrdinal() + ") Left column");
                                deference.defer( new NextFragment(classification, fragment) );

                            }


                    }
                    else{

                        String definitionText = "";

                        if(classification.getTag().equals("LeftColumn"))
                            definitionText = fragment.getText();

                        PukkaLogger.log(PukkaLogger.Level.ACTION, "*** Creating definition for fragment " + fragment.getName() +
                                "(match: " + classification.getPattern().getText() + " tag: " + classification.getTag() +  ")");

                        /*

                        Definition definition = new Definition(
                                classification.getPattern().getText(),
                                DefinitionType.REGULAR.name(),
                                fragment.getKey(),
                                fragment.getOrdinal(),
                                version.getKey(),
                                project.getKey(),
                                definitionText);
                        definition.store();

                        */


                        fragmentClassification = new FragmentClassification(
                                fragment.getKey(),
                                FeatureTypeTree.DEFINITION.getName(),
                                0,              // requirement level not implemented
                                0,              // applicable phase not implemented
                                0,             // Not blocking
                                "",
                                classification.getKeywords(),
                                system.getKey(),
                                version.getKey(),
                                project.getKey(),
                                classification.getPattern().getText(),
                                classification.getPattern().getPos(),
                                classification.getPattern().getLength(),
                                classification.getSignificance(),
                                "not specified rule",                       //TODO: This should be implemented later
                                analysisTime.getSQLTime().toString());


                        classifier.addClassification(fragmentClassification, fragment, definitionsForProject);
                        classifications++;

                        updated = true;

                        // Also store the definition in the abstract document to be able to detect it later
                        if(aDocument != null)
                            aDocument.addDefinition(new AbstractDefinition(classification.getPattern().getText(), (int)fragment.getOrdinal()));



                    }

                    continue;
                }

                /**************************************
                        Risk (or a child to risk)

                 */


                if(type.getName().equals(FeatureTypeTree.Risk.getName()) ||
                    (parent != null && parent.getName().equals(FeatureTypeTree.Risk.getName()))){

                    // Detecting a risk should result in a risk created in the system.

                    PukkaLogger.log(PukkaLogger.Level.ACTION, "*** Creating risk for fragment " + fragment.getName() + "(" + classification.getPattern().getText() + ")");

                    String riskDescription = "The phrasing " + classification.getPattern().getText() + "(" + classification.getTag()+ ")";

                    RiskClassification risk = new RiskClassification(
                            fragment.getKey(),
                            defaultRisk,
                            riskDescription,
                            "#RISK",
                            system.getKey(),
                            version.getKey(),
                            project.getKey(),
                            classification.getPattern().getText(),
                            classification.getPattern().getPos(),
                            analysisTime.getSQLTime().toString(),
                            FragmentClassification.GENERATED
                    );
                    risk.store();
                    risks++;
                    fragment.setRisk(defaultRisk);  // Set it in the fragment too

                    // Creating a risk description. This should really be part of displaying the risk, but the

                    ContractAnnotation riskAnnotation = new ContractAnnotation(
                            "Risk Description",
                            fragment.getKey(),
                            0,  // This is the first anyway...
                            riskDescription,
                            system.getKey(),
                            version.getKey(),
                            project.getKey(),
                            classification.getPattern().getText(),
                            classification.getPattern().getPos(),
                            analysisTime.getSQLTime().toString()

                    );


                    riskAnnotation.store();
                    annotations++;

                    fragment.keywordString =  searchManager.getUpdatedKeywords(fragment, risk);

                    updated = true;
                    continue;

                }

                /**************************************
                        Definition Usage

                 */


                if(type.getName().equals(FeatureTypeTree.DEFINITION_USAGE.getName())){

                    // The analysis has classified a definition usage
                    // We create a reference and a low priority classificaiton that will not be shown

                    PukkaLogger.log(PukkaLogger.Level.ACTION, "*** Creating definition reference fragment " + fragment.getName() + "(" + classification.getPattern().getText() + ")");

                    ContractFragment definitionFragment = getFragmentForDefinition(fragment, classification.getPattern().getText(), definitionsForProject);

                    if(definitionFragment.exists()){

                        PukkaLogger.log(PukkaLogger.Level.DEBUG,"Check source = usage for " + classification.getPattern().getText());

                        if(definitionFragment.getKey().equals(fragment.getKey())){

                            // We have found da definition and usage in the same fragment. Ignore this

                            PukkaLogger.log(PukkaLogger.Level.INFO, "Ignoring definition usage of " + classification.getPattern() + ". This is the definition.");

                        }
                        else{

                            ReferenceType referenceType = ReferenceType.getDefinitionUsage();

                            Reference reference = new Reference(
                                    classification.getPattern().getText(),
                                    fragment.getKey(),
                                    definitionFragment.getKey(),     // Point to the definition
                                    version.getKey(),
                                    project.getKey(),
                                    referenceType,
                                    classification.getPattern().getText(),
                                    classification.getPattern().getPos(),
                                    system.getKey());
                            reference.store();

                            references++;
                            updated = true;

                        }

                    }
                    else{
                        PukkaLogger.log(PukkaLogger.Level.WARNING, "Internal error: Definition \""+ classification.getPattern().getText()+
                                "\" identified in analysis but then not found for processing. (Document: " + fragment.getVersion().getDocument().getName() + ")");
                    }

                    PukkaLogger.log(PukkaLogger.Level.DEBUG," *** Storing Definition Usage classification for definition");

                    fragmentClassification = new FragmentClassification(
                            fragment.getKey(),
                            classification.getType().getName(),
                            0,              // requirement level not implemented
                            0,              // applicable phase not implemented
                            0,
                            "",
                            classification.getKeywords(),
                            system.getKey(),
                            version.getKey(),
                            project.getKey(),
                            classification.getPattern().getText(),
                            classification.getPattern().getPos(),
                            classification.getPattern().getLength(),
                            Significance.MATCH_SIGNIFICANCE,
                            "not specified rule",
                            analysisTime.getSQLTime().toString());

                    classifier.addClassification(fragmentClassification, fragment, definitionsForProject);

                    continue;
                }

                /**************************************

                        Default action is to jut add the classification from the analysis

                 */



                if(classification.getRelevance() < RELEVANCE_THRESHOLD){

                    PukkaLogger.log(PukkaLogger.Level.ACTION, "*** Ignoring classification " + classification.getTag() + " for " + fragment.getName() + "( relevance "+ classification.getRelevance()+" below threshold)");
                }
                else{

                    PukkaLogger.log(PukkaLogger.Level.ACTION, "*** Classifying fragment " + fragment.getName() + " with tag "+ classification.getType()+". Pattern(" + classification.getPattern().getText() + ")" + "Relevance: " + classification.getRelevance());

                    // If the classification is a legal reference, we add a link
                    //extractLinkForLegalReference(fragment, classification);
                    //extractLinkForURL(fragment, classification);


                    //System.out.println(classification.getType().getName() + " " + classification.getTag());

                    PukkaLogger.log(PukkaLogger.Level.DEBUG, " *** Storing pattern " + classification.getPattern().getText() + " for classificaiton");

                    fragmentClassification = new FragmentClassification(
                            fragment.getKey(),
                            classification.getType().getName(),
                            0,              // requirement level not implemented
                            0,              // applicable phase not implemented
                            0,
                            classification.getTag(),
                            classification.getKeywords(),
                            system.getKey(),
                            version.getKey(),
                            project.getKey(),
                            classification.getPattern().getText(),
                            classification.getPattern().getPos(),
                            classification.getPattern().getLength(),
                            classification.getSignificance(),
                            "not specified rule",
                            analysisTime.getSQLTime().toString());

                    classifier.addClassification(fragmentClassification, fragment, definitionsForProject);

                    fragment.keywordString =  searchManager.getUpdatedKeywords(fragment, fragmentClassification);


                    // Only update the number of classifications if it is above the threshold
                    // for displaying in the front-end

                    if(classification.getSignificance() > Significance.DISPLAY_SIGNIFICANCE){

                        classifications++;
                        updated = true;
                    }


                }

            }catch(BackOfficeException e){


                e.printStackTrace(System.out);
                e.logError("Error in hanndleResult for fragment " + fragment + " and classification " + classification.getType().getName());
            }



        }

        if(classifications != 0){

            fragment.setClassificatonCount(classifications);
            PukkaLogger.log(PukkaLogger.Level.DEBUG, "*** Updating classification count to " + classifications + " for fragment " + fragment.getName());
            updated = true;
        }

        if(references != 0){

            fragment.setReferenceCount(references);
            updated = true;
        }

        if(annotations != 0){

            fragment.setAnnotationCount(annotations);
            updated = true;
        }

        if(updated)
            fragment.update();


        return new NewAnalysisFeedback(classifications, references, annotations, risks);
    }

    private void extractLinkForLegalReference(ContractFragment fragment, Classification classification) {

        if(classification.getType().getName().equals(FeatureTypeTree.LegalReference.getName())){

            if(classification.getTag().equals("SFS-Reference")){

                String pattern = classification.getPattern().getText();
                String fragmentText = fragment.getText();
                String link = "<a href=\"http://lagen.nu/"+pattern+"\" target=\"_blank\">" + pattern + "</a>";


                fragment.setText(fragmentText.replaceAll(pattern, link));

                PukkaLogger.log(PukkaLogger.Level.INFO, "Replacing an SFS reference "+ pattern +"with a link " + link);

            }
        }
    }

    private void extractLinkForURL(ContractFragment fragment, Classification classification) {

        if(classification.getType().getName().equals(FeatureTypeTree.URL.getName())){

            if(classification.getTag().equals("URL")){

                String pattern = classification.getPattern().getText();
                String fragmentText = fragment.getText();
                String link = "<a href=\""+pattern+"\" target=\"_blank\">" + pattern + "</a>";


                fragment.setText(fragmentText.replaceAll(pattern, link));

                PukkaLogger.log(PukkaLogger.Level.INFO, "Replacing an SFS reference "+ pattern +"with a link " + link);

            }
        }
    }


    private boolean isFirstRow(ContractFragment fragment) {

        if(fragment.getCellInfo() == null)
            return false;

        return(fragment.getCellInfo().row == 0);
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


    /*******************************************************************************'
     *
     *          look up the definition for the reference
     *
     * @param fragment
     * @param pattern
     * @return
     *
     *
     */

    private ContractFragment getFragmentForDefinition(ContractFragment fragment, String pattern, List<Definition> definitionsForProject) throws BackOfficeException {


        //System.out.println("Looking through " + definitionsForProject.size() + " definitions in project");

        for(Definition definition : definitionsForProject){

            if(definition.getName().toLowerCase().equals(pattern.toLowerCase())){

                return definition.getDefinedIn();
            }
        }

        return new ContractFragment();

    }




    /*********************************************************************************
     *
     *      Perform the analysis
     *
     *      This involves the following steps:
     *
     *          - Perform a main pass on the analysis
     *          - Perform the post processing of the analysis (looking for definition usage)
     *
     *          - Create search completion keywords
     *          - Transpose all the annotations, references etc from the old document if needed
     *
     *          - Perform a complete reanalysis of the project to find references to this new document
     *          - Make a pass over the references to see if it is possible to close open references
     *
     *
     *
     * @param newVersion - the new version
     * @param oldVersion - old optional version
     * @throws BackOfficeException
     *
     *
     */


    public void analyse(ContractVersionInstance newVersion, ContractVersionInstance oldVersion) throws BackOfficeException {

        List<OutcomeMap> outcomeList = new ArrayList<OutcomeMap>();
        DBTimeStamp analysisTime = new DBTimeStamp();
        Contract document = newVersion.getDocument();
        Project project = document.getProject();
        Organization organization = project.getOrganization();
        AbstractProject aProject = project.createAbstractProject();
        AbstractDocument aDocument = newVersion.createAbstractDocumentVersion(aProject, new LanguageCode(document.getLanguage()));
        List<Definition> definitions = project.getDefinitionsForProject();
        LanguageCode documentLanguage = new LanguageCode(document.getLanguage());
        List<ContractFragment> fragments = newVersion.getFragmentsForVersion(
                new LookupList().addSorting(new Sorting(ContractFragmentTable.Columns.Ordinal.name(), Ordering.FIRST))
        );
        PortalUser owner = document.getOwner();
        SearchManager2 searchManager = new SearchManager2(project, owner);

        document.setStatus(ContractStatus.getAnalysing());  // Setting the status for the document
        document.update();

        aProject.addDocument(aDocument);

        // Create an analyser and detect basic principles for the document.

        try {

            Analyser analyser = new Analyser(documentLanguage, modelDirectory);

            if(oldVersion != null){

                PukkaLogger.log(PukkaLogger.Level.INFO, "Deleting old keywords and attributes");

                // Remove existing values in the old version

                deleteKeywords(oldVersion);
                deleteAttributes(oldVersion);

            }


            // Now make a new pass over all the fragments to analyse them. At this point all fragments should have a key

            analyseFragments(fragments, analyser, newVersion, aDocument, project, document, owner, aProject, analysisTime, outcomeList, definitions);

            // Make a second pass over all the fragments
            // This is to handle definition references

            //postProcessAnalysis(analyser, outcomeList, aDocument, owner, newVersion, project, aProject, analysisTime, definitions);

            PukkaLogger.log(PukkaLogger.Level.INFO, "Completed Analysis");

            // Retrieve and store all keywords

            storeKeywords(newVersion, analyser, document, project, organization);


            // If there is an old version (update rather than upload new) we need to transpose all old attributes

            if(oldVersion != null){

                // Clone all previous annotations, reference, etc.

                PukkaLogger.log(PukkaLogger.Level.INFO, "Transposing classification, risk and annotations");
                Transposer transposer = new Transposer();
                transposer.clone(oldVersion, newVersion);

            }


            // Index all fragments in the search engine

            PukkaLogger.log(PukkaLogger.Level.INFO, "Indexing" + fragments.size() + " fragments for the analysis of the document " + document);

            document.setMessage("Indexing document for search");
            document.update();

            searchManager.indexFragments(fragments, newVersion, document);



        } catch (Exception e) {

            PukkaLogger.log( e );

        }

    }

    /********************************************************************************************
     *
     *          Re-analyse works on an existing document and is only invoked from the back-office
     *
     *          The main purpose is to remove all attributes from previous analysis and redo it.
     *          A typical case is when the analysis is updated.
     *
     *
     * @param documentVersion - document to analyse
     * @throws BackOfficeException
     */



    public void reAnalyse(ContractVersionInstance documentVersion) throws BackOfficeException {

        PukkaLogger.log(PukkaLogger.Level.INFO, "Deleting old keywords and attributes");

        // Remove existing attributes. These will be regenerated in the analysis

        deleteKeywords(documentVersion);
        deleteAttributes(documentVersion);


        analyse(documentVersion, null);

    }

    /********************************************************************************************
     *
     *
     *                  Go through the fragments one by one
     *
     *
     * @param fragments
     * @param analyser
     * @param documentVersion
     * @param aDocument
     * @param project
     * @param document
     * @param owner
     * @param aProject
     * @param analysisTime
     * @param outcomeList
     * @param definitionsForProject
     * @throws BackOfficeException
     *
     *
     *
     *                  //TODO: Return error mssages for all that goes wrong
     */



    private void analyseFragments(List<ContractFragment> fragments,
                                  Analyser analyser,
                                  ContractVersionInstance documentVersion,
                                  AbstractDocument aDocument,
                                  Project project,
                                  Contract document,
                                  PortalUser owner,
                                  AbstractProject aProject,
                                  DBTimeStamp analysisTime,
                                  List<OutcomeMap> outcomeList,
                                  List<Definition> definitionsForProject) throws BackOfficeException {

        StringBuffer errorMessages = new StringBuffer();
        SearchManager2 searchManager = new SearchManager2(project, owner);

        Classifier classifier = new Classifier(project, document.getHeadVersion(), analysisTime);

        int risks = 0;
        NewAnalysisOutcome analysisOutcome;
        DeferenceHandler deference = new DeferenceHandler();
        for(ContractFragment fragment : fragments){

            try{

                StructureItem item = fragment.getStructureItem();
                CellInfo cellInfo = fragment.getCellInfo();
                String headline = "";
                if(item.exists())
                    headline = item.getName();

                PukkaLogger.log(PukkaLogger.Level.DEBUG, "**************************************************************");
                PukkaLogger.log(PukkaLogger.Level.INFO, "* Analysing fragment " + fragment.getName() + "("+ fragment.getKey().toString()+")");
                PukkaLogger.log(PukkaLogger.Level.DEBUG, "*  -with headline: " + headline);

                // Old deprecated call. Just kept for the transition

                //AnalysisOutcome analysisOutcome= analyser.analyseFragment(fragment.getText(), headline,
                //        aDocument, aProject);

                String contextText = "";  // TODO: Context text not implemented,
                                          // this is additional text from surrounding parts of the document,
                                          // relevant for the analysis e.g. Lists

                analysisOutcome = analyser.analyseFragment(fragment.getText(), (int)fragment.getOrdinal(), headline, contextText, aDocument, cellInfo, aProject);
                NewAnalysisFeedback feedback = handleResult(analysisOutcome, classifier, fragment, deference, project, analysisTime, searchManager, aDocument, definitionsForProject, documentVersion);

                risks += feedback.risks; // Count risks for the action message

                // Store it for the second pass. In that pass we dont want to redo the parsing and analysis

                outcomeList.add(new OutcomeMap(analysisOutcome, fragment));

            }catch(BackOfficeException e){

                String message = "Error analysing fragment " + fragment.getText() + PukkaLogger.getMessage(e);
                errorMessages.append( message );
                PukkaLogger.log( PukkaLogger.Level.WARNING, message );

            }catch(NullPointerException e){

                e.printStackTrace(System.out);
                String message = "Internal Error analysing fragment " + fragment.getText() + PukkaLogger.getMessage(e);
                errorMessages.append(message);
                PukkaLogger.log( PukkaLogger.Level.WARNING, message );
            }
        }
        classifier.store();

        // Create an action to review the potential risks.
        // This may have to be updated when re-uploading documents is implemented and we should only
        // issue an action if there are new unseen risk

        if(risks > 0){

            String actionTitle = "Assess " + risks +" potential risk"+(risks > 1 ? "s" : "")+"  in " + document.getName();
             String actionDescription = "Assess " + risks +" potential risk"+(risks > 1 ? "s" : "")+"  found in the analysis of " + document.getName();

             PortalUser system = PortalUser.getSystemUser();
             ContractFragment fragment = documentVersion.getFirstFragment();
                                     // Also create an implicit action for the risk. All risks should be mitigated or at least acknowledged.


             Action handleRiskAction = new Action(
                     (long)0, // Not supported action id yet. This is a placeholder
                     actionTitle,
                     actionDescription,
                     "",
                     fragment.getKey(),
                     fragment.getVersionId(),
                     fragment.getKey(),             //Target not set
                     project.getKey(),
                     system.getKey(),
                     PortalUser.getNoUser().getKey(),
                     -1,
                     ActionStatus.getOpen(),
                     analysisTime.getISODate(),
                     new DBTimeStamp(DBTimeStamp.NO_DATE, "1900-00-00").getISODate(),
                     new DBTimeStamp(DBTimeStamp.NO_DATE, "1900-00-00").getISODate());

             handleRiskAction.store();

        }

        document.setStatus(ContractStatus.getAnalysed());  // Setting the status for the document
        document.update();

    }


    /***********************************************************************
     *
     *      Go through the entire project and see if there are references to close. This includes:
     *
     *       - Open references
     *       - Definitions
     *
     * @param analyser
     * @param project
     */

    /*
    private void closeReferences(Analyser analyser, Project project) throws BackOfficeException{

        AbstractProject aProject = project.createAbstractProject();

        List<Reference> openReferencesForProject = project.getOpenReferences();

        System.out.println("Found " + openReferencesForProject.size() + " open references to analyse and close");

        for(Reference reference : openReferencesForProject){

            NewAnalysisOutcome outcome2 = analyser.analyseOpenReferences(reference.getName(), aProject);

            System.out.println("***** New close delivered " + outcome2.getClassifications().size() + " classifications.");

            for(Classification classification : outcome2.getClassifications()){


                if(classification.getType().getName().equals(FeatureTypeTree.Reference.getName())){

                    // This is a new reference target. The key (which comes from the AbstractStructureItem)
                    // is extracted as semantic extraction

                    DBKeyInterface clauseId = new DatabaseAbstractionFactory().createKey(classification.getExtraction().getSemanticExtraction());

                    PukkaLogger.log(PukkaLogger.Level.INFO, "Closing an open reference(2) " + reference.getName() + ". Found matching clause " + clauseId.toString() );

                    reference.setTo(clauseId);
                    reference.setType(ReferenceType.getExplicit());
                    reference.update();

                    break;
                }



                PukkaLogger.log(PukkaLogger.Level.FATAL, "Action " + classification.getType().getName() + " not supported after analyseOpenReferences()");

            }

        }

    }

      */

    /******************************************************************************************
     *
     *      Go through the entire project to find existing undetected references to the new document
     *
     * @param analyser
     * @param project
     * @param versionInstance

     */

    public void reanalyseProjectForReferences(Analyser analyser, Project project, ContractVersionInstance versionInstance, AbstractProject aProject,
                                              AbstractDocument aDocument, PortalUser user) throws BackOfficeException {


        List<Contract> contractsForProject = project.getContractsForProject();
        Contract currentDocument = versionInstance.getDocument();
        String name = currentDocument.getName().toLowerCase();
        String file = currentDocument.getFile().toLowerCase();
        ReferenceType type = ReferenceType.getExplicit();
        DBTimeStamp analysisTime = new DBTimeStamp();
        SearchManager2 searchManager = new SearchManager2(project, user);
        List<Definition> definitionsForProject = project.getDefinitionsForProject();
        PortalUser systemUser = PortalUser.getSystemUser();


        PukkaLogger.log(PukkaLogger.Level.ACTION, "*****************************\nPhase IV: References and Definitions");


        ContractFragment firstFragment = new ContractFragment(new LookupItem()
                .addFilter(new ColumnFilter(ContractFragmentTable.Columns.Ordinal.name(), 0))
                .addFilter(new ReferenceFilter(ContractClauseTable.Columns.Version.name(), versionInstance.getKey())));

        if(!firstFragment.exists()){


            PukkaLogger.log(PukkaLogger.Level.WARNING, "The document " + currentDocument.getName() + " does not have a first fragment to direct references to");
        }


        for(Contract document : contractsForProject){



            //For all documents we look for references to the title and document name
            if(!document.equals(currentDocument)){

                ContractVersionInstance latestVersion = document.getHeadVersion();
                Classifier classifier = new Classifier(project, latestVersion, analysisTime);

                List<ContractFragment> fragmentsForDocument = latestVersion.getFragmentsForVersion(
                        new LookupList().addSorting(new Sorting(ContractFragmentTable.Columns.Ordinal.name(), Ordering.FIRST))
                );

                PukkaLogger.log(PukkaLogger.Level.INFO, "Reanalysing references in document " + document.getName());

                DeferenceHandler deference = new DeferenceHandler();
                for(ContractFragment fragment : fragmentsForDocument){

                    //Now check for occurrences of the name or the file

                    if(fragment.getText().toLowerCase().contains(name) ||
                            fragment.getText().toLowerCase().contains(file)){

                        // Check if there already is a reference

                        List<Reference> existingReferences =  fragment.getReferencesForFragment();
                        boolean foundExistingReference = false;

                        for(Reference reference : existingReferences){

                            if(reference.getTo().equals(firstFragment))
                                foundExistingReference = true;

                            if(reference.getType().equals(ReferenceType.getOpen()) && reference.getName().toLowerCase().contains(name))
                                foundExistingReference = true;

                        }

                        if(!foundExistingReference){

                            // Create a new reference and store it in the fragment
                            // It will point to the first clause in the document

                            Reference reference = new Reference(name, fragment, firstFragment,  latestVersion, project, type, name, 0, systemUser);
                            reference.store();
                        }

                    }



                    NewAnalysisOutcome postProcessOutcome = analyser.postProcess(fragment.getText(), (int)fragment.getOrdinal(), aProject, false);
                    handleResult(postProcessOutcome, classifier, fragment, deference, project, analysisTime, searchManager, aDocument, definitionsForProject, latestVersion);


                }

                classifier.store();

                //TODO: Optimization: Only do this if it is changed
                invalidateFragmentCache(latestVersion);
                invalidateDocumentCache(document, project);

            }
        }

    }

    /*

    private void postProcessAnalysis(Analyser analyser, List<OutcomeMap> outcomeList, AbstractDocument aDocument, PortalUser owner,
                                     ContractVersionInstance version, Project project, AbstractProject aProject, DBTimeStamp analysisTime,
                                     List<Definition> definitionsForProject) throws BackOfficeException{

        PukkaLogger.log(PukkaLogger.Level.INFO, "Second pass with " + outcomeList.size() + " elements.");
        SearchManager2 searchManager = new SearchManager2(project, owner);


        for(OutcomeMap outcome : outcomeList){

            PukkaLogger.log(PukkaLogger.Level.INFO, "Post processing " + outcome.fragment.getText());

            NewAnalysisOutcome postProcessOutcome = analyser.postProcess(outcome.outcome, aProject);

            handleResult(postProcessOutcome, outcome.fragment, project, analysisTime, searchManager, aDocument, definitionsForProject, version);

        }

    }

    */

    /**************************************************************
     *
     *          Store the extracted keywords in the database
     *
     *
     *
     * @param versionInstance  - the analysed version
     * @param analyser         - list of keywords from the analysis
     * @param organization
     * @throws BackOfficeException
     *
     *          //TODO: This adds #Risk as a keyword. Should be language specific
     *
     */


    private void storeKeywords(ContractVersionInstance versionInstance, Analyser analyser, Contract document, Project project, Organization organization) throws BackOfficeException {

        KeywordTable table = new KeywordTable();
        table.createEmpty();
        List<String> keywords = analyser.getKeywords();

        for(String word : keywords){

            Keyword keyword = new Keyword(word, versionInstance, document, project);
            table.add(keyword);
        }

        // Add risks

        List<ContractRisk> risks = new ContractRiskTable().getAll();

        for(ContractRisk risk : risks){

            Keyword keyword = new Keyword("#" + risk.getName(), versionInstance, document, project);
            table.add(keyword);
        }


        // TODO: Move these to create project


        Keyword keyword = new Keyword("#Risk", versionInstance, document, project);
        table.add(keyword);

        keyword = new Keyword("#Annotation", versionInstance, document, project);
        table.add(keyword);

        keyword = new Keyword("#Classification", versionInstance, document, project);
        table.add(keyword);


        table.store();  // Store back all

    }

    protected void deleteKeywords(ContractVersionInstance versionInstance) throws BackOfficeException {

        // First remove all old keywords for the document

        KeywordTable oldKeywords = new KeywordTable(new LookupList().addFilter(new ReferenceFilter(KeywordTable.Columns.Document.name(), versionInstance.getDocumentId())));
        oldKeywords.delete();


    }

    /************************************************************************************************
     *
     *      Delete Attributes
     *
     *      When re-uploading a document or rerunning the analysis, the original
     *      attributes should be removed to avoid duplicates.
     *
     * @param versionInstance              - the active document version
     * @throws BackOfficeException
     *
     *
     */

    protected void deleteAttributes(ContractVersionInstance versionInstance) throws BackOfficeException {


        // Remove automatically generated annotations

        ContractAnnotationTable internalAnnotations = new ContractAnnotationTable(new LookupList()
                .addFilter(new ReferenceFilter(ContractAnnotationTable.Columns.Version.name(), versionInstance.getKey()))
                .addFilter(new ReferenceFilter(ContractAnnotationTable.Columns.Creator.name(), PortalUser.getSystemUser().getKey()))

        );

        PukkaLogger.log(PukkaLogger.Level.INFO, "**** Removed " + internalAnnotations.getCount() + " internal annotations");

        internalAnnotations.delete();



        // Remove all old classifications that are created by the system

        FragmentClassificationTable oldClassifications = new FragmentClassificationTable(new LookupList()
                .addFilter(new ReferenceFilter(FragmentClassificationTable.Columns.Version.name(), versionInstance.getKey()))
                .addFilter(new ReferenceFilter(FragmentClassificationTable.Columns.Creator.name(), PortalUser.getSystemUser().getKey()))

        );

        PukkaLogger.log(PukkaLogger.Level.INFO, "**** Removed " + oldClassifications.getCount() + " Classifications");

        oldClassifications.delete();



        // Remove all old actions that are created by the system

        ActionTable oldActions = new ActionTable(new LookupList()
                .addFilter(new ReferenceFilter(ActionTable.Columns.Version.name(), versionInstance.getKey()))
                .addFilter(new ReferenceFilter(ActionTable.Columns.Issuer.name(), PortalUser.getSystemUser().getKey()))
                .addFilter(new ReferenceFilter(ActionTable.Columns.Assignee.name(), PortalUser.getNoUser().getKey()))

        );

        PukkaLogger.log(PukkaLogger.Level.INFO, "**** Removed " + oldActions.getCount() + " Actions");

        oldActions.delete();



        // Now remove all risks
        // TODO: Check how this works with set classifications and "latest"

        RiskClassificationTable oldRiskClassifications = new RiskClassificationTable(new LookupList()
                .addFilter(new ReferenceFilter(RiskClassificationTable.Columns.Version.name(), versionInstance.getKey()))
                .addFilter(new ReferenceFilter(RiskClassificationTable.Columns.Creator.name(), PortalUser.getSystemUser().getKey()))

        );

        PukkaLogger.log(PukkaLogger.Level.INFO, "**** Removed " + oldRiskClassifications.getCount() + " Risk Classifications");

        oldRiskClassifications.delete();

        // Definitions

        DefinitionTable oldDefinitions = new DefinitionTable(new LookupList()
                .addFilter(new ReferenceFilter(DefinitionTable.Columns.Version.name(), versionInstance.getKey()))

        );

        PukkaLogger.log(PukkaLogger.Level.INFO, "**** Removed " + oldDefinitions.getCount() + " Definitions");

        oldDefinitions.delete();


        ReferenceTable oldReferences = new ReferenceTable(new LookupList()
                .addFilter(new ReferenceFilter(ReferenceTable.Columns.Version.name(), versionInstance.getKey()))

        );

        PukkaLogger.log(PukkaLogger.Level.INFO, "**** Removed " + oldReferences.getCount() + " References");

        oldReferences.delete();


    }



    /***************************************************************************************************
     *
     *              Locate the definition source in the project
     *
     *
     *
     *
     *
     * @param definition          - the definition usage classification
     * @param fragment            - the fragment where it is used
     * @param projectDefinitions  - the definitions found in the project
     * @return                    - key to the fragment for the definition
     *
     *
     *              If the definition source is not found, we will return null. This indicates that
     *              we create an open reference
     *
     */

    /*

    private ContractFragment getDefinitionSource(String definition, ContractFragment fragment, List<Definition> projectDefinitions) {

        for (Definition projectDefinition : projectDefinitions) {

            if(projectDefinition.getName().equals(definition))
                return projectDefinition.getDefinedIn();
        }

        PukkaLogger.log(PukkaLogger.Level.INFO, "Detected a definition usage, but could not find definition source for " + definition);
        return null;
    }

      */


    /******************************************************************************************
     *
     *              Main method for parsing the uploaded document
     *
     *
     *              // TODO: Catch the analysis and IO exceptions and convert them to appropriate BackOffice Exceptions to be passed to the end user
     *
     * @param document
     * @param version
     * @throws BackOfficeException

     */


    public void parseFile(Contract document, ContractVersionInstance version) throws BackOfficeException{

        InputStream stream;
        FragmentSplitterInterface docXManager;

        try{

            RepositoryInterface repository = new BlobRepository();

            RepositoryFileHandler fileHandler = new RepositoryFileHandler(document.getFile());

            stream = repository.getInputStream(fileHandler);


        }catch(IOException e){

            PukkaLogger.log( e );
            throw new BackOfficeException(BackOfficeException.General, "Could not get document.");
        }

        try{

            PukkaLogger.log(PukkaLogger.Level.ACTION, "*******************Phase I: Parsing document " + document.getFile());
            docXManager = new DocumentManager(document.getFile(), stream);


        }catch (AnalysisException e){

            // We failed to parse the document. We will return an error and have to delete the version.
            // If this was the only version for the document we remove it too.

            /*
            version.delete();

            int remainingVersions = document.getVersionsForDocument().size();
            if(remainingVersions == 0)
                document.delete();
              */
            e.printStackTrace(System.out);
            throw new BackOfficeException(BackOfficeException.General, "Could not parse document " + document.getFile() + "("+ e.getMessage()+")");
        }

        try{

            LanguageCode languageCode = Analyser.detectLanguage(document.getFile(), docXManager.getBody());
            document.setLanguage(languageCode.code);
            String oldName = document.getName();
            document.setName(docXManager.getDocumentTitle());

            PukkaLogger.log(PukkaLogger.Level.INFO, "Updating name from " + oldName + " to " + docXManager.getDocumentTitle() );
            document.update();

        }catch(AnalysisException e){

            //Fail to detect Language

            PukkaLogger.log( e );

            throw new BackOfficeException(BackOfficeException.General, "Could not detect language");
        }


        ParseFeedback feedback = fragmentDocument(version, docXManager);

        if(feedback != null){
            document.setAnalysisDetails(feedback.toJSON().toString());
            document.update();

            System.out.println(" --- Just checking after update: " + feedback.toJSON().toString());
        }
        else{

            System.out.println(" --- Oooops something is null");
        }



    }

    public void setModelDirectory(String modelDirectory){

        this.modelDirectory = modelDirectory;
    }





}



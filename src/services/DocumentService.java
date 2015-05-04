package services;

import actions.CanonicalReferenceParser;
import actions.Checklist;
import actions.ChecklistParser;
import analysis.NewAnalysisFeedback;
import analysis.ParseFeedbackItem;
import analysis.Significance;
import analysis.deferrance.DeferenceHandler;
import analysis.deferrance.NextFragment;
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
import crossReference.Definition;
import crossReference.Reference;
import crossReference.ReferenceType;
import dataRepresentation.DBTimeStamp;
import databaseLayer.DBKeyInterface;
import document.*;
import featureTypes.FeatureTypeInterface;
import featureTypes.FeatureTypeTree;
import language.English;
import language.LanguageInterface;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;
import risk.ContractRisk;
import risk.RiskClassification;
import search.Keyword;
import search.KeywordTable;
import search.SearchManager2;
import system.Analyser;
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
    private static final int RELEVANCE_THRESHOLD = 40;          // What is the relevance needed to actually create an entry in the database


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

        ParseFeedbackItem feedback;

        for(AbstractFragment aFragment : fragments){

            try{

                AbstractStructureItem aStructureItem = aFragment.getStructureItem();
                DBKeyInterface structureItemKey = null;
                String fragmentName = createNameFromBody(aFragment);
                //int indentation = aStructureItem.getIndentation();
                int indentation = (int)aFragment.getIndentation();
                int structureNo = aStructureItem.getID();
                boolean newStructureItemCreated = false;

                feedback = null;

                PukkaLogger.log(PukkaLogger.Level.INFO, "fragment " + fragmentNo + ": ("+ aFragment.getStyle().name()+")" + aFragment.getBody() +"     (" +
                        indentation + ": " + aStructureItem.getStructureType().name() + ":" +
                        (aStructureItem.getTopElement() != null ? aStructureItem.getTopElement().getBody() : "--") +")" );


                CellInfo cellinfo = aFragment.getCellInfo();
                if(cellinfo == null)
                    cellinfo = new CellInfo();


                //int row = cellinfo.row;
                //int column = cellinfo.col;

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

                        feedback = checklistParser.parseChecklistCell(aFragment);

                        if(feedback != null){
                            PukkaLogger.log(PukkaLogger.Level.INFO, feedback.severity.name()+ ": " + feedback.message);

                            if(feedback.severity == ParseFeedbackItem.Severity.ABORT){
                                isChecklist = false;

                            }
                        }


                    }


                    if(isCanonicalDefinitionTable && cellinfo.row > 0){

                        feedback = canonicalReferenceParser.parseCell(aFragment);

                        if(feedback != null){
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
                else if(feedback != null && feedback.severity == ParseFeedbackItem.Severity.HIDE){

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
                            cellinfo.col,
                            cellinfo.row,
                            cellinfo.rowWidth,
                            cellinfo.toJSON(),
                            imageJSON.toString()

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
        if(isCanonicalDefinitionTable){

            feedback =  canonicalReferenceParser.endCurrentTable();
            PukkaLogger.log(PukkaLogger.Level.INFO, feedback.severity.name()+ ": " + feedback.message);

        }


        PukkaLogger.log(PukkaLogger.Level.INFO, "Storing " + fragmentsToStore.getCount() + " fragments for the analysis of the document " + document);
        fragmentsToStore.store(); // Save all

        // Now we can map the checklist sources to the correct fragment id:s

        checklistParser.mapItemSources((List<ContractFragment>)(List<?>) fragmentsToStore.getValues());
        canonicalReferenceParser.mapItemSources(project);



        // Store the keywords

        PukkaLogger.log(PukkaLogger.Level.INFO, "Storing keywords for the analysis of the document " + document);
        storeKeyWords(newKeywords, versionInstance, document, project);

        // Handle comments. The comments are extracted by the parsing and stored with a fragment id,
        // so left to do is to find the corresponding fragment.

        PukkaLogger.log(PukkaLogger.Level.INFO, "** Found " + fragmenter.getComments().size() + " comments in the document" );
        handleComments(fragmenter.getComments(), project, versionInstance, analysisTime);


    }

    /********************************************************************************************
     *
     *          Handle comments. For a comment in the document there are a number of actions to take:
     *
     *          #TAG            - will result in creating an action here
     *          #RISK:status    - will set the status of a risk (overriding any existing status
     *          other           - Create an annotation
     *
     * @param comments          - list of all comments in the document
     * @param project           - Current project
     * @param documentVersion   - The document
     * @param analysisTime      - The time of analysis
     *
     *
     *        //TODO: Future: Allow fro import in different languages
     */


    private void handleComments(List<AbstractComment> comments, Project project, ContractVersionInstance documentVersion, DBTimeStamp analysisTime) throws BackOfficeException{

        PortalUser user = PortalUser.getExternalUser();
        LanguageInterface languageForImport = new English();

        for(AbstractComment aComment : comments){

            try{

                ContractFragment fragment = new ContractFragment(new LookupItem()
                        .addFilter(new ColumnFilter(ContractFragmentTable.Columns.Ordinal.name(), aComment.getFragmentId()))
                        .addFilter(new ReferenceFilter(ContractFragmentTable.Columns.Version.name(), documentVersion.getKey())));

                if(!fragment.exists()){

                    // Debugging only
                    for(ContractFragment f : documentVersion.getFragmentsForVersion()){
                        System.out.println("Fragment: " + f.getName() + "id" + f.getOrdinal());
                    }

                    PukkaLogger.log(PukkaLogger.Level.FATAL, "Could not find fragment for comment. Fragment id: " + aComment.getFragmentId() + "("+ aComment.getComment()+")");
                    continue;
                }


                if(isClassification(aComment)){

                    String classificationTag = firstWord(aComment.getComment());
                    FeatureTypeInterface featureType = getFeatureTypeByName(classificationTag, languageForImport);

                    if(featureType == null){

                        PukkaLogger.log(PukkaLogger.Level.ERROR, "Could not find feature type named " + classificationTag + " for language " + languageForImport.getLanguageCode().code);
                        continue;
                    }

                    String keywords = featureType.getHierarchy();

                    PukkaLogger.log(PukkaLogger.Level.INFO, "Found a classification "+ aComment.getComment()+" in document comment.");


                    FragmentClassification classification = new FragmentClassification(
                            fragment.getKey(),
                            featureType.getName(),
                            0,              // requirement level not implemented
                            0,              // applicable phase not implemented
                            "",
                            keywords,
                            user.getKey(),
                            documentVersion.getKey(),
                            project.getKey(),
                            aComment.getAnchor(),
                            -1,
                            0,
                            Significance.MATCH_SIGNIFICANCE,
                            "external import",
                            analysisTime.getSQLTime().toString());

                    Classifier classifier = new Classifier(project, documentVersion);
                    classifier.addClassification(classification, fragment);
                    classifier.store();



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
                        documentVersion,
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

    }

    private String firstWord(String comment) {

        comment = comment.trim();
        comment = comment.substring(1);

        int firstSpace = comment.indexOf(" ");
        if(firstSpace < 0)
            return comment;

        return comment.substring(0, firstSpace);

    }

    private boolean isClassification(AbstractComment aComment) {

        return aComment.getComment().startsWith("#");

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
     *
     */

    public static String getTag(String className, Organization organization, LanguageInterface language) {

        //String classTag = languageInterface.getClassificationForName(className);

        ClassifierInterface[] classifiers = language.getAllClassifiers();

        for (ClassifierInterface classifier : classifiers) {

            System.out.println(" --- Comparing classes " + classifier.getType().getName() + " and " + className);

            if(classifier.getType().getName().equals(className)){

                PukkaLogger.log(PukkaLogger.Level.INFO, "Found static classTag " + className);
                return classifier.getType().getName();   // This should be the #TAG as this is the key to the frontend
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
                    return customClass.getKey().toString();
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

            System.out.println("  --- matching " + classifier.getClassificationName() + " and " + className);

            if(classifier.getClassificationName().equals(className)){

                PukkaLogger.log(PukkaLogger.Level.INFO, "Found static classTag " + className);
                return classifier.getType();
            }
        }

        return null;
    }


    /***************************************************************************
     *
     *      Handle the result from the analysis
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


    protected NewAnalysisFeedback handleResult(NewAnalysisOutcome analysisResult, ContractFragment fragment,
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

        //System.out.println("Found " + analysisResult.getClassifications().size() + " classifications in analysis");

        //TODO: Optimization; this batch store is done once per fragment result. Could be done once and for all for the analysis- (Pass this around)

        FragmentClassificationTable classificationsToStore = new FragmentClassificationTable();
        classificationsToStore.createEmpty();


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


                if(type.getName().equals(FeatureTypeTree.DefinitionDef.getName())){

                    // The analysis has classified a definition Source.
                    // We create a definition and also add a definition tag. (This may be removed
                    // later when definitions are properly displayed and searchable from the frontend)


                    // First we check for a definition to defer

                    if(classification.getPass() != Analyser.DEFERENCE_PASS &&
                            classification.getTag().equals("LeftColumn")){


                            if(isFirstRow(fragment)){

                                PukkaLogger.log(PukkaLogger.Level.INFO, "*** Ignoring deferrence of definition creation for fragment " + fragment.getName() + "(" + fragment.getOrdinal() + ") First Row");

                            }else{

                                PukkaLogger.log(PukkaLogger.Level.ACTION, "*** Deferring definition creation for fragment " + fragment.getName() + "(" + fragment.getOrdinal() + ") Left column");
                                deference.defer( new NextFragment(classification, fragment) );

                            }


                    }
                    else{

                        // A deferred table definition should hold the definition text to recognize repeat usage.

                        String definitionText = "";
                        if(classification.getTag().equals("LeftColumn"))
                            definitionText = fragment.getText();

                        PukkaLogger.log(PukkaLogger.Level.ACTION, "*** Creating definition for fragment " + fragment.getName() +
                                "(match: " + classification.getPattern().getText() + " tag: " + classification.getTag() +  ")");

                        Definition definition = new Definition(
                                classification.getPattern().getText(),
                                DefinitionType.REGULAR.name(),
                                fragment.getKey(),
                                fragment.getOrdinal(),
                                version.getKey(),
                                project.getKey(),
                                definitionText);
                        definition.store();

                        // Also add the definition to the active list of definitions. This will be needed for subsequent reanalyze of the project

                        definitionsForProject.add(definition);

                        fragmentClassification = new FragmentClassification(
                                fragment.getKey(),
                                FeatureTypeTree.DefinitionDef.getName(),
                                0,              // requirement level not implemented
                                0,              // applicable phase not implemented
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


                        classificationsToStore.add(fragmentClassification);
                        classifications++;

                        updated = true;

                        // Also store the definition in the abstract document to be able to detect it later
                        if(aDocument != null)
                            aDocument.addDefinition(new AbstractDefinition(definition.getName(), (int)fragment.getOrdinal()));


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
                            analysisTime.getSQLTime().toString()
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


                if(type.getName().equals(FeatureTypeTree.DefinitionUsage.getName())){

                    // The analysis has classified a definition usage
                    // We create a reference and a low priority classificaiton that will not be shown

                    PukkaLogger.log(PukkaLogger.Level.ACTION, "*** Creating definition reference fragment " + fragment.getName() + "(" + classification.getPattern().getText() + ")");

                    ContractFragment definitionFragment = getFragmentForDefinition(fragment, classification.getPattern().getText(), definitionsForProject);

                    if(definitionFragment.exists()){

                        System.out.println("Check source = usage for " + classification.getPattern().getText());

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

                    System.out.println(" *** Storing Definition Usage classification for definition");

                    fragmentClassification = new FragmentClassification(
                            fragment.getKey(),
                            classification.getType().getName(),
                            0,              // requirement level not implemented
                            0,              // applicable phase not implemented
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

                    classificationsToStore.add(fragmentClassification);

                    continue;
                }

                /**************************************

                        Default action is to jut add the classification from the analysis

                 */



                if(classification.getRelevance() < RELEVANCE_THRESHOLD){

                    PukkaLogger.log(PukkaLogger.Level.ACTION, "*** Ignoring classification " + fragment.getName() + "( relevance "+ classification.getRelevance()+" below threshold)");
                }
                else{

                    PukkaLogger.log(PukkaLogger.Level.ACTION, "*** Classifying fragment " + fragment.getName() + " with tag "+ classification.getType()+". Pattern(" + classification.getPattern().getText() + ")" + "Relevance: " + classification.getRelevance());

                    // If the classification is a legal reference, we add a link
                    extractLinkForLegalReference(fragment, classification);
                    extractLinkForURL(fragment, classification);


                    //System.out.println(classification.getType().getName() + " " + classification.getTag());

                    System.out.println(" *** Storing pattern " + classification.getPattern().getText() + " for classificaiton");

                    fragmentClassification = new FragmentClassification(
                            fragment.getKey(),
                            classification.getType().getName(),
                            0,              // requirement level not implemented
                            0,              // applicable phase not implemented
                            "",
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

                    classificationsToStore.add(fragmentClassification);
                    //fragmentClassification.store();

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

        // Store all classifications

        classificationsToStore.store();

        if(classifications != 0){


            fragment.setClassificatonCount(classifications);
            System.out.println("*** Updating classification count to " + classifications + " for fragment " + fragment.getName());
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




}



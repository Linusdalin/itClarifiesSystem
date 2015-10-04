package overviewExport;

import actions.ChecklistItem;
import analysis.ParseFeedback;
import analysis.ParseFeedbackItem;
import analysis.Significance;
import classification.ClassificationOverviewManager;
import classification.FragmentClassification;
import contractManagement.*;
import crossReference.Definition;
import dataRepresentation.DBTimeStamp;
import dataRepresentation.DataObjectInterface;
import featureTypes.FeatureTypeInterface;
import featureTypes.FeatureTypeTree;
import language.English;
import language.LanguageInterface;
import log.PukkaLogger;
import module.ContractingModule;
import module.ModuleInterface;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import project.Project;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;
import risk.RiskClassification;
import services.DocumentService;
import userManagement.PortalUser;

import java.util.*;
import java.util.ArrayList;
import java.util.List;

/******************************************************************************************
 *
 *              Generate the project overview from a template
 *
 *              This is done in two passes
 *
 *               - First create extractions and store them in the database
 *               - Second go through the extractions for the project and write to file
 *
 *
 *              // TODO: Add error handling for the template if it is changing
 *              // TODO: External reference links
 *              // TODO: Look info using XSSFRichTextString
 *              // TODO: Store time for extraction
 */


public class OverviewGenerator {

    // Include peers and children
    private static final boolean AddPeers = false;

    // Queues for the substitution in sheets

    private static final String SUBSTITUTE_PROJECT =     "$(PROJECT)";
    private static final String SUBSTITUTE_DATE =        "$(TIME)";
    private static final String SUBSTITUTE_TAG =         "$(TAG)";
    private static final String SUBSTITUTE_DESCRIPTION = "$(DESCRIPTION)";
    private static final String SUBSTITUTE_DEFINITION =  "$(DEFINITION)";


    private ExtractionTagList[] tagExtractions = new ExtractionTagList[0];    // Default is no tag extractions
    private static final int SEARCH_LIMIT = 1000;       // Max number of cells to search before failing replace
    private static final int ComplianceSheetIx = 5;

    private Project project;                    // The actual project we are exporting
    private ExtractionStatus statusEntry;       // The status entry
    private DBTimeStamp exportDate;             // Date of export for all timestamps in the analysis

    // The sheets in the template. we hard code these and get them by name
    // Alternatively we could get them by number, but on the other hand they may change place

    XSSFSheet[] sheets;

    private static final int NoStandardSheets = 6;

    private Extraction emptyLine;      // For adding empty lines in the output

    int extractionOrdinal = 0;            // Global count for all the extractions to order the result


    private static final String BoldFont = "Proxima Nova Bold";
    private static final String RegularFont = "Proxima Nova Regular";
    private static final String LightFont = "Proxima Nova Light";
    private static final String TextFont = "Minion Pro";
    private int[] activeHeadlineLevel;


    /***************************************************************************
     *
     *          Initiate the generator for generating the export
     *
     *
     * @param project       - the project to export
     * @param creator       - user generating the export
     * @param comment       - user initiated comment
     *
     * @throws BackOfficeException
     */


    public OverviewGenerator(Project project, PortalUser creator, String comment, String tagJSON) throws BackOfficeException{

        this.project = project;
        exportDate = new DBTimeStamp();

        // Default values

        String description = "no descr.";
        String name = "Export: " + creator.getName() + "@" + exportDate.getISODate();

        ExtractionStatusTable statusEntriesForProject = new ExtractionStatusTable(new LookupList().addFilter(new ReferenceFilter(ExtractionStatusTable.Columns.Project.name(), project.getKey())));
        statusEntriesForProject.delete();

        PukkaLogger.log(PukkaLogger.Level.ACTION, "Generating overview for project " + project + "(" + comment+ ") with tags " + tagJSON );


        statusEntry = new ExtractionStatus(name, exportDate.getISODate(), creator.getKey(), project.getKey(), comment, ExtractionState.getGenerating(), description, tagJSON);
        statusEntry.store();


    }

    /*************************************************************************************
     *
     *              This is the second pass constructor, used to generate the actual
     *              sheet from the internal representation
     *
     *
     * @param template                              - the workbook to write to
     * @param project                               - project to export (with predefined extractions already genrated
     * @param templateSheetIx                       - The index of the template sheet for the #tag-extractions
     * @throws BackOfficeException
     */



    public OverviewGenerator(XSSFWorkbook template, Project project, int templateSheetIx) throws BackOfficeException{

        this.project = project;
        int sheetIx = 0;

        // Get the stored export status

        statusEntry = new ExtractionStatus(new LookupItem().addFilter(new ReferenceFilter(ExtractionStatusTable.Columns.Project.name(), project.getKey())));
        if(!statusEntry.exists()){

            throw new BackOfficeException(BackOfficeException.Usage, "No export generated for project.");
        }

        // Get the tag extractions stored as a JSON in the generate phase

        this.tagExtractions = getListFromJSONParameter(statusEntry.getTags());

        if(!statusEntry.exists())
            throw new BackOfficeException(BackOfficeException.Usage, "No Extraction generated for project " + project.getName());




        // Allocate space for the sheets and the sheet config

        int noSheets = template.getNumberOfSheets() + tagExtractions.length - 1;  // Remove one for the template sheet
        sheets = new XSSFSheet[noSheets];
        SheetExportStyle[] sheetConfig = getSheetStyles( sheets.length );


        emptyLine = new Extraction("", "", "", "", 0, extractionOrdinal++, "", this.project.getKey(), this.project.getKey(), "", "", "", "", statusEntry.getKey());

        // Get the Standard sheets

        for (int i = 0; i < NoStandardSheets; i++) {

            String name = sheetConfig[ i ].sheetName;

            try{

                XSSFSheet sheet = template.getSheet( name );

                if(sheet == null)
                    throw new BackOfficeException(BackOfficeException.General, "Could not find sheet " + name + " in template.");
                this.sheets[ sheetIx++ ] = sheet;

            }catch(Exception e){

                PukkaLogger.log(PukkaLogger.Level.ERROR, "Error accessing page " + name + " in template.");
            }
        }


        // Loop over the tags to create a sheet

        for(int tagNo = 0; tagNo < tagExtractions.length; tagNo++ ){

            String sheetName =  tagExtractions[tagNo].getMainTag();

            try{

                this.sheets[ tagNo + NoStandardSheets ] = template.cloneSheet( templateSheetIx );
                template.setSheetName(sheetIx + 2, sheetName);
                sheetIx++;


            }catch(Exception e){

                PukkaLogger.log(e, "Failed to create sheet named " + sheetName);
                throw new BackOfficeException(BackOfficeException.Usage, "Failed to create sheet named " + sheetName);

            }

        }

    }



    /************************************************************************'
     *
     *       Pre-calculate all sheets and store it in the database
     *
     *
     *
     */

    public ParseFeedback preCalculate(String tagJSON) throws BackOfficeException{

        // Get basic values for the export

        ParseFeedback feedback = new ParseFeedback();
        emptyLine = new Extraction("", "", "", "", 0, extractionOrdinal++, "", this.project.getKey(), this.project.getKey(), "", "", "", "", statusEntry.getKey());

        // Get all attributes, so that we don't have to look-up once per tag

        List<FragmentClassification> allClassifications = project.getFragmentClassificationsForProject();
        List<ContractAnnotation>     allAnnotations = project.getContractAnnotationsForProject();
        List<RiskClassification>     allRisks = project.getRiskClassificationsForProject();
        List<Definition>             allDefinitions = project.getDefinitionsForProject();
        List<Contract>               allDocuments = project.getContractsForProject(new LookupList().addSorting(new Sorting(ContractTable.Columns.Ordinal.name(), Ordering.FIRST)));

        ExtractionTagList[] tagList = getListFromJSONParameter(tagJSON);

        feedback.add(deleteOldExtractions(project));

        feedback.add(generateExtractions(allDocuments, allClassifications, allAnnotations, allRisks, allDefinitions, tagList));


        try {

            // Update the status with the feedback from the analysis and set the state to ready

            statusEntry.setDescription(feedback.toJSON().toString());
            statusEntry.setStatus(ExtractionState.getReady());
            statusEntry.update();

        } catch (BackOfficeException e) {

            PukkaLogger.log( e );
        }

        return feedback;
    }



    /***************************************************************************************
     *
     *          Delete all existing extractions for the project
     *
     *
     *
     * @param project              - the current project
     * @return                     - feedback for the user
     */

    private ParseFeedbackItem deleteOldExtractions(Project project) {

        ExtractionTable extractionsForProject = new ExtractionTable(new LookupList().addFilter(new ReferenceFilter(ExtractionTable.Columns.Project.name(), project.getKey())));


        PukkaLogger.log(PukkaLogger.Level.INFO, " Deleting " + extractionsForProject.getValues().size() + " old extractions from project " + project.getName());

        extractionsForProject.delete();

        return new ParseFeedbackItem(ParseFeedbackItem.Severity.INFO, " Deleting " + extractionsForProject.getValues().size() + " old extractions from project " + project.getName(), 0);

    }


    /*****************************************************************''
     *
     *          Handle substitutions for the standard sheets and the tag-sheets
     *
     *          (This method is called on the second pass - writing to file
     *
     *
     *
     * @param sheets            - All sheets
     * @param exportTime        - Time of export
     * @param tagExtractions    - list of tags to extract
     * @return                  - user feedback
     */

    private ParseFeedback handleSubstitutions(XSSFSheet[] sheets, String exportTime, ExtractionTagList[] tagExtractions) {

        ParseFeedback feedback = new ParseFeedback();

        for(int i = 0; i < NoStandardSheets; i++){

            feedback.add(substitute(sheets[ i ], "", exportTime));
        }

        System.out.println("No Sheets: " + sheets.length);

        for(int i = 0; i < tagExtractions.length; i++){

            feedback.add(substitute(sheets[i+NoStandardSheets], tagExtractions[i].getMainTag(), exportTime));
        }

        return feedback;
    }

    private ParseFeedback substitute(XSSFSheet sheet, String tag, String exportTime) {

        ParseFeedback feedback = new ParseFeedback();

        String description = "";
        String definition = tag;

        LanguageInterface language = new English();
        FeatureTypeInterface featureType = DocumentService.getFeatureTypeByTag(tag, language);

        if(featureType != null){

            description = featureType.getDescription();
            definition  = featureType.getDefinition();
        }

        System.out.println("Find and replace " + tag + " in sheet " + sheet.getSheetName());

        feedback.add(findAndReplace(sheet, SUBSTITUTE_PROJECT, project.getName()));
        feedback.add(findAndReplace(sheet, SUBSTITUTE_TAG, tag));
        feedback.add(findAndReplace(sheet, SUBSTITUTE_DEFINITION, definition));
        feedback.add(findAndReplace(sheet, SUBSTITUTE_DESCRIPTION, description));
        feedback.add(findAndReplace(sheet, SUBSTITUTE_DATE, exportTime));

        return feedback;
    }


    /***************************************************************************************************
     *
     *       Go through all the fragments of all the documents and store them as
     *       extraction fragments for future reference
     *
     *
     *
     *
     * @param allDocuments                 - complete list from the database
     * @param allClassifications           - complete list from the database
     * @param allAnnotations               - complete list from the database
     * @param allRisks                     - complete list from the database
     * @param allDefinitions               - complete list from the database
     * @param tagExtractions               - the extraction tags for the sheets with their children
     * @return                             - user feedback
     */

    private ParseFeedback generateExtractions(List<Contract> allDocuments,
                                              List<FragmentClassification> allClassifications,
                                              List<ContractAnnotation> allAnnotations,
                                              List<RiskClassification> allRisks,
                                              List<Definition> allDefinitions,
                                              ExtractionTagList[] tagExtractions) {


        ParseFeedback feedback = new ParseFeedback();

        //Create an empty table to store the new extractions

        ExtractionTable table = new ExtractionTable();
        table.createEmpty();

        for (Contract document : allDocuments) {

            try{

                ContractVersionInstance head = document.getHeadVersion();
                List<ContractFragment> fragmentsForDocument = head.getFragmentsForVersion(new LookupList().addSorting(new Sorting(ContractFragmentTable.Columns.Ordinal.name(), Ordering.FIRST)));
                List<StructureItem> allStructureItems = head.getStructureItemsForVersion();
                List<DataObjectInterface> extractionsForDocument = new ArrayList<DataObjectInterface>();

                ContractFragment lastFragment = null;

                for (ContractFragment fragment : fragmentsForDocument) {

                    StructureItem structureItem =  fragment.getStructureItem(allStructureItems);

                    // Skip going through fragments that do not have a classification count

                    if(fragment.getClassificatonCount() != 0){

                        matchClassification(fragment, structureItem, lastFragment, document, tagExtractions, allClassifications, fragmentsForDocument, allRisks, allAnnotations, extractionsForDocument, allStructureItems);

                    }

                    //System.out.println("  -- Matching risk");
                    matchRisk(fragment, document, allRisks, extractionsForDocument);
                    //System.out.println("  -- Matching definition");
                    addDefinitions(fragment, document, allDefinitions, extractionsForDocument);
                    //System.out.println("  -- DOne");

                    lastFragment = fragment;

                }
                feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.INFO, " Creating overview from document "+document.getName() + " with " + extractionsForDocument.size() + " extractions", -1));


                table.add(extractionsForDocument);


            }catch(Exception e){

                PukkaLogger.log( e );
            }
        }

        System.out.println("Generated " + table.getValues().size() + " extractions.");

        int i = 1;
        for (DataObjectInterface tableItem : table.getValues()) {

            Extraction extraction = (Extraction)tableItem;

            if(extraction != null)
                PukkaLogger.log(PukkaLogger.Level.INFO, "Extraction "+ (i++) +": " + extraction.toString());
            else
                PukkaLogger.log(PukkaLogger.Level.INFO, "Extraction "+ (i++) +" is null ");
        }


        try {
            table.store();
        } catch (BackOfficeException e) {
            PukkaLogger.log( e );
        }


        return feedback;
    }


    /**************************************************************************************************
     *
     *                  handle adding classifications for a specific fragment.
     *
     *                  The process is to go through all the classifications to see if there is any that
     *                  has the correct fragmentid
     *
     * @param fragment                                - the fragment
     * @param fragment                                - the preceeding fragment
     * @param document                                - the document
     * @param allClassifications                      - classifictions (prefetched from database)
     * @param fragmentsForDocument                    - fragments (prefetched from database)
     * @param allRisks                                - risks (prefetched from database)
     * @param allAnnotations                          - annotations (prefetched from database)
     * @param extractionsForDocument                  - extractions (prefetched from database)
     * @param allStructureItems                       - headlines etc. (prefetched from database)
     */

    private void matchClassification(ContractFragment fragment, StructureItem structureItem, ContractFragment previousFragment,
                                     Contract document, ExtractionTagList[] tagExtractions,
                                     List<FragmentClassification> allClassifications, List<ContractFragment> fragmentsForDocument,
                                     List<RiskClassification> allRisks, List<ContractAnnotation> allAnnotations, List<DataObjectInterface> extractionsForDocument, List<StructureItem> allStructureItems) {

        String fragmentKey = fragment.getKey().toString();

        for (FragmentClassification classification : allClassifications) {

            try{

                System.out.println(" --- Testing fragment " + fragment.getName());

                if(classification.getFragmentId().toString().equals(fragmentKey)){

                    // We found a classification that match the fragment we look at.

                    for (ExtractionTagList tagExtraction : tagExtractions) {

                        System.out.println("   --- Testing extraction " + tagExtraction.getMainTag());

                        if(structureItem != null && fragment.getType().equals("HEADING") && tagExtraction.activeHeadlineLevel >= (int)structureItem.getIndentation() && fragment.getOrdinal() >  tagExtraction.activeHeadlineStart){
                            tagExtraction.activeHeadlineLevel = -1;
                            System.out.println("  !! Exiting an active segment for fragment " + fragment.getName() + " on indentation level " + structureItem.getIndentation() + " for " + tagExtraction.getMainTag());
                        }



                        if (tagExtraction.isApplicableFor(classification)
                               || isInActiveSection(tagExtraction)
                                ) {

                            // We now have a fragment with a tag that matches the tag(s) we are looking for
                            // We add it to the fragment (and potentially also the heading - if it is not already added)

                            createExtraction(fragment, structureItem, tagExtraction, previousFragment, classification, tagExtraction.getMainTag(), document, allStructureItems, extractionsForDocument, allClassifications, allRisks, allAnnotations, fragmentsForDocument);
                        }
                    }


                }


            }catch(Exception e){

                PukkaLogger.log( e );

            }


        }

    }

    private boolean isInActiveSection(ExtractionTagList tagExtraction) {

        return tagExtraction.activeHeadlineLevel > -1;
    }

    private void createExtraction(ContractFragment fragment, StructureItem structureItem, ExtractionTagList tagExtraction, ContractFragment previousFragment, FragmentClassification classification, String mainTag,
                                  Contract document, List<StructureItem> allStructureItems, List<DataObjectInterface> extractionsForDocument,
                                  List<FragmentClassification> allClassifications, List<RiskClassification> allRisks, List<ContractAnnotation> allAnnotations,
                                  List<ContractFragment> fragmentsForDocument) throws BackOfficeException{

        System.out.println("  --- Found match " + classification.getClassTag() + " in fragment " + fragment.getName());

        potentiallyAddHeading(fragment,  mainTag, document, allStructureItems, extractionsForDocument);

        potentiallyAddPrevious(previousFragment, document, extractionsForDocument, allClassifications, allRisks, allAnnotations, allStructureItems, mainTag);

        // Create a new entry for the classification

        Extraction entry = new Extraction(
                "",
                getClassificationsForFragment(fragment, allClassifications),
                fragment.htmlDecode(),
                fragment.getKey().toString(),
                (int) fragment.getOrdinal(),
                extractionOrdinal++,
                "",
                project.getKey(),
                document.getKey(),
                "",
                getRisksForFragment(fragment, allRisks),
                getAnnotationsForFragment(fragment, allAnnotations),
                mainTag,
                statusEntry.getKey());

        if (fragment.getType().equals("HEADING")) {

            entry.asHeadline((int) fragment.getIndentation());
            //extractionsForDocument.add(emptyLine);

            if(!alreadyExist(entry, extractionsForDocument))
                extractionsForDocument.add(entry);

            //System.out.println(" -- After adding HEADING, we have " + extractionsForDocument.size() + " extractions.");

            // Now check and update the flag for headline to add all children. If we are in an
            // active Headline segment (and we hit a new on the same level we exit. Otherwise we start a new

            if(tagExtraction.activeHeadlineLevel == -1 && structureItem != null){
                tagExtraction.activeHeadlineLevel = (int)structureItem.getIndentation();
                tagExtraction.activeHeadlineStart = (int)fragment.getOrdinal();
                System.out.println("  !! Entering an active segment for fragment " + fragment.getName() + " on indentation level " + structureItem.getIndentation() + " for " + tagExtraction.getMainTag());

            }

            if (AddPeers)
                extractionsForDocument.addAll(findPeers(mainTag, fragment, document, fragmentsForDocument, allAnnotations, allClassifications, allRisks, false));

            //System.out.println(" -- After adding peers, we have " + extractionsForDocument.size() + " extractions");

        } else if (fragment.getType().equals("LIST")) {

            if (AddPeers)
                extractionsForDocument.addAll(findPeers(mainTag, fragment, document, fragmentsForDocument, allAnnotations, allClassifications, allRisks, true));
        } else {

            if(!alreadyExist(entry, extractionsForDocument)){

                PukkaLogger.log(PukkaLogger.Level.INFO, "Adding extraction'" + entry.getText() + "'");
                extractionsForDocument.add(entry);
            }
            else{
                PukkaLogger.log(PukkaLogger.Level.INFO, "Ignoring duplicate extraction'" + entry.getText() + "'");

            }

        }

    }

    private void potentiallyAddPrevious(ContractFragment previousFragment, Contract document, List<DataObjectInterface> extractionsForDocument, List<FragmentClassification> allClassifications, List<RiskClassification> allRisks, List<ContractAnnotation> allAnnotations, List<StructureItem> allStructureItems, String mainTag) {

        try{

            if(previousFragment == null)
                return;

            StructureItem structure = previousFragment.getStructureItem(allStructureItems);
            if(structure == null ||!structure.exists() || previousFragment.getStructureNo() <= 1)
                return;

            if(structure.getType().equals("HEADING"))
                return;

            Extraction entry = new Extraction(
                    "",
                    getClassificationsForFragment(previousFragment, allClassifications),
                    previousFragment.htmlDecode(),
                    previousFragment.getKey().toString(),
                    (int) previousFragment.getOrdinal(),
                    extractionOrdinal++,
                    "",
                    project.getKey(),
                    document.getKey(),
                    "",
                    getRisksForFragment(previousFragment, allRisks),
                    getAnnotationsForFragment(previousFragment, allAnnotations),
                    mainTag,
                    statusEntry.getKey());

            if(!alreadyExist(entry, extractionsForDocument)){

                PukkaLogger.log(PukkaLogger.Level.INFO, "Adding a parent " + previousFragment.getText() + " of type " + structure.getType());
                extractionsForDocument.add(entry);
            }
            else{

                PukkaLogger.log(PukkaLogger.Level.INFO, "Ignoring headline parent " + previousFragment.getText() + " as duplicate.");

            }


        }catch(BackOfficeException e){

        }

    }

    /**************************************************************************************'
     *
     *              All fragments should be exported with their immediate parent for readability
     *
     *               //TODO: Not Implemented: Handle duplicates
     *               //TODO: Improvement Usability: Add ALL chapter levels, not only the parent
     *
     * @param fragment                        - the current fragment
     * @param mainTag                         - the current tag extraction (to put on the correct sheet)
     * @param document                        - the document
     * @param allStructureItems               - all structure items to lookup the parent
     * @param extractionsForDocument          - the resulting list
     *
     */


    private void potentiallyAddHeading(ContractFragment fragment, String mainTag, Contract document, List<StructureItem> allStructureItems, List<DataObjectInterface> extractionsForDocument) throws BackOfficeException{

        try{

            StructureItem structure = fragment.getStructureItem(allStructureItems);
            if(structure == null ||!structure.exists() || fragment.getStructureNo() <= 1)
                return;

            if(!structure.getType().equals("HEADING")){

                // If the parent is NOT a headline we recurse up to find a headline

                ContractFragment parent = structure.getFragmentForStructureItem();
                if(parent.equals(fragment))
                    return;

                PukkaLogger.log(PukkaLogger.Level.INFO, "Recursing with parent fragemnt " + parent.getName() + " (" + parent.getStructureNo()+")" );
                potentiallyAddHeading(parent, mainTag, document, allStructureItems, extractionsForDocument);
            }
            else{

                ContractFragment parent = structure.getFragmentForStructureItem();

                if(parent.equals(fragment))
                    return;

                Extraction entry = new Extraction(
                        "",
                        "",                         // No classifications on the headline
                        parent.htmlDecode(),
                        parent.getKey().toString(),
                        (int) parent.getOrdinal(),
                        extractionOrdinal++,
                        "",
                        project.getKey(),
                        document.getKey(),
                        "",
                        "",   // No risks in headline
                        "",   // No annotations added
                        mainTag,
                        statusEntry.getKey());

                entry.asHeadline( 0 );

                if(!alreadyExist(entry, extractionsForDocument)){

                    PukkaLogger.log(PukkaLogger.Level.INFO, "Adding a parent " + parent.getText() + " of type " + structure.getType());
                    extractionsForDocument.add(entry);
                }
                else{

                    PukkaLogger.log(PukkaLogger.Level.INFO, "Ignoring headline parent " + parent.getText() + " as duplicate.");

                }

            }

        }catch(StackOverflowError e){

            PukkaLogger.log(PukkaLogger.Level.ERROR, "Error getting a potential parent");

        }catch(Exception e){

            PukkaLogger.log(PukkaLogger.Level.ERROR, "Error getting a potential parent");
        }

    }



    private boolean alreadyExist(Extraction entry, List<DataObjectInterface> extractionsForDocument) {

        for (DataObjectInterface object : extractionsForDocument) {

            Extraction existing = (Extraction)object;
            if(existing.getFragmentKey().equals(entry.getFragmentKey()))
                return true;

        }

        return false;

    }


    /*********************************************************************************************************
     *
     *              Go through the risks and add them to the correct tab
     *
     *
     *
     * @param fragment                  - the current fragment in the project
     * @param document                  - current document
     * @param allRisks                  - All risks in the project (prefetched from database)
     * @param extractionsForDocument    - the final list
     *
     */

    private void matchRisk(ContractFragment fragment, Contract document, List<RiskClassification> allRisks, List<DataObjectInterface> extractionsForDocument) {

        String fragmentKey = fragment.getKey().toString();

        for (RiskClassification risk : allRisks) {

            try{

                if(risk.getFragmentId().toString().equals(fragmentKey)){

                    System.out.println("risk1");
                    String riskClass = risk.getRisk().getName();
                    String decodedText = fragment.htmlDecode();

                    Extraction entry = new Extraction(
                            "",
                            "",
                            decodedText,
                            fragment.getKey().toString(),
                            (int)fragment.getOrdinal(),
                            extractionOrdinal++,
                            "",
                            project.getKey(),
                            document.getKey(),
                            riskClass,
                            risk.getComment(),
                            document.getName(),
                            "#Risk",
                            statusEntry.getKey());

                    extractionsForDocument.add(entry);
                    System.out.println("Adding risk '" + entry.getText() + "'");


                }

            }catch(Exception e){

                PukkaLogger.log( e );

            }

        }

    }


    /*******************************************************************************************
     *
     *              Go through all definitions and add them as extractions
     *
     *
     * @param fragment                      - the current fragment
     * @param document                      - the current document
     * @param allDefinitions                - definitions pre-fetched from the database
     * @param extractionsForDocument        - the final list
     */

    private void addDefinitions(ContractFragment fragment, Contract document, List<Definition> allDefinitions, List<DataObjectInterface> extractionsForDocument) {

        String fragmentKey = fragment.getKey().toString();

        for (Definition definition : allDefinitions) {

            try{

                if(definition.getDefinedInId().toString().equals(fragmentKey)){

                    Extraction entry = new Extraction(
                            definition.getName(),
                            "#Definition",
                            fragment.htmlDecode(),
                            fragment.getKey().toString(),
                            (int)fragment.getOrdinal(),
                            extractionOrdinal++,
                            "",
                            project.getKey(),
                            document.getKey(),
                            "",
                            "",
                            document.getName(),
                            "#Definition",
                            statusEntry.getKey());

                        System.out.println("Adding definition " + entry.getName() + " '" + entry.getText() + "'");
                        extractionsForDocument.add(entry);

                    }

            }catch(Exception e){

                PukkaLogger.log( e );

            }

        }
    }



    /***********************************************************************************
     *
     *                  Find peers are ment to add fragments next to the hit. This is used for:
     *
     *                   - Lists
     *
     *
     *
     * @param tag
     * @param fragment
     * @param fragmentsForDocument
     * @param allAnnotations
     * @param allClassifications
     * @param allRisks
     * @param includeSelf
     * @return
     *
     *              //TODO: Recursively go through the structure. This method will skip subsections

     */

    private List<DataObjectInterface> findPeers(String tag, ContractFragment fragment, Contract document,
                                                List<ContractFragment> fragmentsForDocument, List<ContractAnnotation> allAnnotations,
                                                List<FragmentClassification> allClassifications, List<RiskClassification> allRisks, boolean includeSelf) {

        System.out.println("Find peers");

        List<DataObjectInterface> children = new ArrayList<DataObjectInterface>();

        for (ContractFragment otherFragment : fragmentsForDocument) {

            // Find all other fragments with the same structure number but not is the actual text

            //System.out.println(" Checking Peer: fragment " + otherFragment.getName());
            if(otherFragment.getStructureNo() == fragment.getStructureNo()){

                if(includeSelf || otherFragment.getOrdinal() != fragment.getOrdinal()){

                    Extraction child = new Extraction(
                            "",
                            getClassificationsForFragment(otherFragment, allClassifications),
                            otherFragment.htmlDecode(),
                            otherFragment.getKey().toString(),
                            (int)fragment.getOrdinal(),
                            extractionOrdinal++,
                            "",
                            project.getKey(),
                            document.getKey(),
                            "",
                            getRisksForFragment(otherFragment, allRisks),
                            getAnnotationsForFragment(otherFragment, allAnnotations),
                            tag,
                            statusEntry.getKey());

                    children.add(child);

                }
            }

        }

        System.out.println("Found "+ children.size()+"peers");

        return children;


    }

    /***********************************************************************************************
     *
     *                  Generate the Workbook
     *
     *                  This has the side-effect to update the Excel workbook
     *
     *
     * @return         - feedback to the user.
     *
     *
     */


    public ParseFeedback get() throws BackOfficeException{

        ParseFeedback feedback = new ParseFeedback();

        ExtractionStatus latestGeneration = new ExtractionStatus(new LookupItem()
                .addFilter(new ReferenceFilter(ExtractionStatusTable.Columns.Project.name(), project.getKey())));


        if(!latestGeneration.exists()){

            // No export generated. Return error message
            throw new BackOfficeException(BackOfficeException.Usage, "No export generated for project " + project.getName());

        }

        List<Extraction> extractionsForProject = project.getExtractionsForProject(new LookupList().addSorting(new Sorting(ExtractionTable.Columns.ExtractionNumber.name(), Ordering.FIRST)));
        SheetExportStyle[] sheetConfig =  getSheetStyles(sheets.length);

        PukkaLogger.log(PukkaLogger.Level.INFO, " --- Found: " + extractionsForProject.size() + " extractions for project " + project.getName());

        exportDate = latestGeneration.getDate();
        feedback.add(handleSubstitutions(sheets, exportDate.getISODate(), tagExtractions));



        feedback.add(handleDocumentList(project, sheetConfig[1]));

        feedback.add(handleChecklistItems(sheets, tagExtractions, sheetConfig));

        addExtractionHeadline(sheets, sheetConfig);


        Extraction lastExtraction[] = new Extraction[sheets.length];

        for (Extraction extraction : extractionsForProject) {

            PukkaLogger.log(PukkaLogger.Level.INFO, " --- Parsing extraction: " + extraction.toString() + " for sheet " + extraction.getSheet());

            int sheetNo = getSheet(extraction.getSheet(), tagExtractions);

            if(sheetNo < 0){

                PukkaLogger.log(PukkaLogger.Level.ERROR, "Could not find sheet " + extraction.getSheet());
                continue;
            }

            XSSFSheet sheet = sheets[ sheetNo ];
            int currentRow = sheetConfig[sheetNo].currentRow;

            if(sheet == null){
                PukkaLogger.log(PukkaLogger.Level.ERROR, "Sheet " + extraction.getSheet() + " has index "+ sheetNo+" but does not exist!");
                continue;
            }

            if(isNewDocument(extraction, lastExtraction[sheetNo])){

                Extraction title = new Extraction("", "", extraction.getDocument().getName(), "", 0, extractionOrdinal++, "", null, null, "", "", "", "", extraction.getKey());
                title.asTitle();

                currentRow = writeToSheet(emptyLine, sheet, currentRow);
                currentRow = writeToSheet(title, sheet, currentRow);

            }

            PukkaLogger.log(PukkaLogger.Level.DEBUG, "Writing extraction "+ extraction.getClassification()+" to sheet " + sheet.getSheetName() + "("+ sheetNo+")");

            int newRow;

            if(extraction.isDefinition())
                newRow = writeDefinitionToSheet(extraction, sheet, currentRow);
            else
                newRow = writeToSheet(extraction, sheet, currentRow);

            sheetConfig[sheetNo].currentRow = newRow;
            lastExtraction[sheetNo] = extraction;

        }

        // Add overview
        int id = NoStandardSheets + 1;   // Start after static pages

        for (ExtractionTagList tagExtraction : tagExtractions) {

            writeToSheet(tagExtraction.getMainTag(), id, sheets[0], id + sheetConfig[0].currentRow);
            id++;
        }


        return new ParseFeedback(); // TODO: Add some feedback here

    }

    /*********************************************************************'
     *
     *          Set default values for the default sheets
     *
     *
     * @param length       - total lengt of sheets including the generated tag sheets
     * @return             - structure with data for sheets
     */


    private SheetExportStyle[] getSheetStyles(int length) {

        SheetExportStyle[] sheetConfig = new SheetExportStyle[ length ];

        // First set the values for the standard sheets

        sheetConfig[ 0 ] = new SheetExportStyle("Overview",        5);
        sheetConfig[ 1 ] = new SheetExportStyle("Documents",       6);
        sheetConfig[ 2 ] = new SheetExportStyle("Definitions",     7);
        sheetConfig[ 3 ] = new SheetExportStyle("External Ref",    7);
        sheetConfig[ 4 ] = new SheetExportStyle("Risks",           7);
        sheetConfig[ 5 ] = new SheetExportStyle("Compliance",      7);

        // Create style for the dynamic sheets.

        for(int tagNo = 0; tagNo < tagExtractions.length; tagNo++ ){

            sheetConfig[ tagNo + NoStandardSheets ] = new SheetExportStyle( tagExtractions[ tagNo].getMainTag(), 6 );  // Default row for tag extraction is 6

        }


        return sheetConfig;
    }


    /*****************************************************************'
     *
     *          Add headlines to the standard sheets
     *
     *
     * @param sheets     - all sheets
     * @param sheetConfig      - the current row numbers for all sheets
     */

    private void addExtractionHeadline(XSSFSheet[] sheets, SheetExportStyle[] sheetConfig) {

        for (int i = NoStandardSheets; i < sheets.length - 1; i++) {            // remove 1 for the template sheet that is removed

            CellValue[] elements = new CellValue[8];

            elements[0] = new CellValue("Tags").tableHeadline();
            elements[1] = new CellValue("").tableHeadline();
            elements[2] = new CellValue("Paragraph").tableHeadline();
            elements[3] = new CellValue("Comments").tableHeadline();
            elements[4] = new CellValue("Risk").tableHeadline();

            sheetConfig[i].currentRow += 3;   // Create space between the compliance section and the export fragments

            addRow(sheets[i], (sheetConfig[i].currentRow), elements, 1);

            sheetConfig[i].currentRow ++;
        }

    }

    /*****************************************************************************************
     *
     *          Handle checklist items.
     *
     *          All checklist items are added to the tab corresponding to the context tab
     *
     *          And all checklist items are added to the checklist overview sheet
     *
     *
     * @param sheets              - all sheets (to write to)
     * @param tagExtractions      - the list of tags/sheets
     * @param sheetConfig               - current row number
     * @return                    - feedback to the user
     *
     *
     */

    private ParseFeedbackItem handleChecklistItems(XSSFSheet[] sheets, ExtractionTagList[] tagExtractions, SheetExportStyle[] sheetConfig) {

        List<ChecklistItem> checklistItemsForProject = project.getChecklistItemsForProject();
        int checklistRow = sheetConfig[ComplianceSheetIx].currentRow;
        XSSFSheet checklistSheet = sheets[ComplianceSheetIx];

        for (ChecklistItem checklistItem : checklistItemsForProject) {

            try {
                int sheet = getSheet( checklistItem.getContextTag(), tagExtractions);


                if(sheet < 0)
                    PukkaLogger.log(PukkaLogger.Level.INFO, " ! No sheet to write checklistItem " + checklistItem.getName() + " ( tag " + checklistItem.getContextTag() + ")" );
                else{

                    PukkaLogger.log(PukkaLogger.Level.INFO, " Adding " + checklistItem.getName() + " ( to tab  " + checklistItem.getContextTag() + ")" );

                    writeToSheet(checklistItem, sheets[sheet], sheetConfig[sheet].currentRow++);
                    writeToChecklistSheet(checklistItem, checklistSheet, checklistRow++);

                }

            } catch (BackOfficeException e) {

                PukkaLogger.log( e );
            }

        }


        return new ParseFeedbackItem(ParseFeedbackItem.Severity.INFO, "Not implemented checklist items in tabs", 0);
    }


    /**************************************************************************************
     *
     *              Determine if this is a new document for the purpose of writing the
     *              document as a headlin
     *
     *
     * @param extraction                 - this extraction
     * @param lastExtraction             - last extraction written to the sheet
     * @return                           - is it new (and applicable)
     */


    private boolean isNewDocument(Extraction extraction, Extraction lastExtraction) {

        boolean isNew = (lastExtraction == null || !extraction.getDocumentId().equals(lastExtraction.getDocumentId())) && !extraction.getClassification().equals("#Definition");

        PukkaLogger.log(PukkaLogger.Level.DEBUG, " Fragment isNew = " + isNew);

        return isNew;

    }

    /*******************************************************************'
     *
     *              The document list written to the document sheet
     *
     *
     *
     * @param project           - current project
     * @param sheetConfig        - current row for the sheet
     * @return                  - feedback to the user
     */


    private ParseFeedbackItem handleDocumentList(Project project, SheetExportStyle sheetConfig) {

        XSSFSheet sheet = sheets[ 1 ];

        List<Contract> documents = project.getContractsForProject();

        for (Contract document : documents) {

            writeToSheet(document, sheet, sheetConfig.currentRow++);
        }


        return new ParseFeedbackItem(ParseFeedbackItem.Severity.INFO, "Added " + documents.size() + " documents", 0);
    }



    /***************************************************************
     *
     *          Get the id of the given sheet
     *
     *
     * @param sheet                     - name
     * @param tagExtractions            - tags to extract
     * @return                          - the id of the sheet
     * @throws BackOfficeException      - if not found
     */

    private int getSheet(String sheet, ExtractionTagList[] tagExtractions) throws BackOfficeException{

        if(sheet.equals("#Definition"))
            return 2;

        if(sheet.equals("#Risk"))
            return 4;


        for (int i = 0; i < tagExtractions.length; i++) {

            if(tagExtractions[i].getMainTag().equals(sheet))
                return i + NoStandardSheets;
        }
        return -1;

    }




    /************************************************************************'
     *
     *          Write an extraction to a sheet
     *
     *          The extraction is displayed differently for different types (on different sheets)
     *
     *
     * @param extraction         - extraction output
     * @param sheet              - target sheet
     * @param currentRow         - row counter to know where to write it
     * @return                   - new row
     *
     *          //TODO: Not Implemented feedback from write to sheet
     */


    private int writeToSheet(Extraction extraction,  XSSFSheet sheet, int currentRow) {

        ParseFeedback feedback = new ParseFeedback();

        try{

            CellValue[] elements = new CellValue[8];

            elements[0] = new CellValue(extraction.getClassification()).withFont(BoldFont, 10);
            elements[1] = new CellValue("");
            elements[2] = new CellValue(extraction.getText()).withFont(TextFont, 12);
            elements[3] = new CellValue(extraction.getComment()).withFont(RegularFont, 12);
            elements[4] = new CellValue(extraction.getDescription()).withFont(RegularFont, 12);


            if(extraction.getStyle().equals("Title")){

               //System.out.println(" --- Setting bold and italics for style Title");
                elements[2].withFont(22).bold().italics();

            }

            if(extraction.getStyle().equals("Heading")){
                        //System.out.println(" --- Setting bold and italics for style Heading");
                        elements[2].withFont(16).bold().italics();
            }
            if(extraction.getStyle().equals("Text")){
                        //System.out.println(" --- Setting font for text");
                        elements[2].withFont(12);
                        elements[0].withFont(12);
                        elements[3].withFont(12);
                        elements[4].withFont(12);


            }

            addRow(sheet, (currentRow++), elements, 1);
            feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.INFO, "Adding row in sheet " + sheet.getSheetName(), 0));

        }catch(Exception e){

            PukkaLogger.log( e );
            feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.ABORT, "Internal error adding definitions in sheet " + sheet.getSheetName(), 0));

        }

        return currentRow;

    }

    /***********************************************************************************
     *
     *              Write a definition
     *
     *
     * @param extraction              - the current extraction (a definition)
     * @param sheet                   - the sheet
     * @param currentRow              - the current row
     * @return                        - the new rownumber
     *
     *
     *
     *            //TODO: Not Implemented feedback from write to sheet
     */


    private int writeDefinitionToSheet(Extraction extraction,  XSSFSheet sheet, int currentRow) {

        ParseFeedback feedback = new ParseFeedback();
        String documentName = extraction.getDocument().getName();

        try{

            CellValue[] elements = new CellValue[4];

            elements[0] = new CellValue(extraction.getName()).asBox().withFont(RegularFont, 12);
            elements[1] = new CellValue(extraction.getText()).asBox().withFont(RegularFont, 12);
            elements[2] = new CellValue("").asBox().withFont(RegularFont, 12);
            elements[3] = new CellValue(documentName).asBox().withFont(RegularFont, 12);

            addRow(sheet, (currentRow++), elements, 1, 1750/5);
            feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.INFO, "Adding row in sheet " + sheet.getSheetName(), 0));

        }catch(Exception e){

            PukkaLogger.log( e );
            feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.ABORT, "Internal error adding definitions in sheet " + sheet.getSheetName(), 0));

        }

        return currentRow;

    }




    /*************************************************************************************'
     *
     *          Write a document to the sheet (for the docuemnt tab)
     *
     *
     * @param document      - document to list
     * @param sheet         - current sheet
     * @param currentRow    - current row to write to
     *
     *                    // TODO: Not Implemented feedback from write to sheet
     *
     */


    private void writeToSheet(Contract document, XSSFSheet sheet, int currentRow) {

        ParseFeedback feedback = new ParseFeedback();

        try{

            CellValue[] elements = new CellValue[8];

            elements[0] = new CellValue(document.getName()).asBox().withFont(RegularFont, 12);
            elements[1] = new CellValue(document.getFile()).asBox().withFont(RegularFont, 12);
            elements[2] = new CellValue(document.getCreation().getISODate()).asBox().withFont(RegularFont, 12);
            //elements[3] = new CellValue(document.getHeadVersion().getVersion()).asBox().withFont(RegularFont, 12);


            addRow(sheet, (currentRow++), elements, 1, 2550/5);
            feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.INFO, "Adding row in sheet " + sheet.getSheetName(), 0));

        }catch(Exception e){

            PukkaLogger.log( e );
            feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.ABORT, "Internal error adding definitions in sheet " + sheet.getSheetName(), 0));

        }


    }



    private void writeToSheet(String tag, int id, XSSFSheet sheet, int currentRow) {

        ParseFeedback feedback = new ParseFeedback();

        try{

            CellValue[] elements = new CellValue[8];

            elements[0] = new CellValue(id).asBox().withFont(BoldFont, 14);
            elements[1] = new CellValue("").asBox().fill(new byte[]{(byte)0xCC, (byte)0xCC, (byte)0xCC});
            elements[2] = new CellValue(tag).asBox().withFont(RegularFont, 12);
            elements[3] = new CellValue("Extractions from project").asBox().withFont(RegularFont, 12);
            elements[4] = new CellValue("").asBox();
            elements[5] = new CellValue("").asBox();


            addRow(sheet, (currentRow++), elements, 1, 2550 / 5);
            feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.INFO, "Adding row in sheet " + sheet.getSheetName(), 0));

        }catch(Exception e){

            PukkaLogger.log( e );
            feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.ABORT, "Internal error adding definitions in sheet " + sheet.getSheetName(), 0));

        }


    }

    /*********************************************************************************************'
     *
     *              Write a checklist item to a sheet
     *
     * @param item             - checklist item
     * @param sheet            - sheet to write to
     * @param currentRow       - row to write to
     *
     *          //TODO: Not Implemented Instance count not implemented
     *          // TODO: Not Implemented feedback from write to sheet
     */

    private void writeToSheet(ChecklistItem item, XSSFSheet sheet, int currentRow) {

        ParseFeedback feedback = new ParseFeedback();

        try{

            CellValue[] elements = new CellValue[8];

            elements[0] = new CellValue(item.getConformanceTag()).asBox().withFont(RegularFont).bold();
            elements[1] = new CellValue(item.getName()).asBox().withFont(RegularFont);
            elements[2] = new CellValue(item.getDescription()).asBox().withFont(RegularFont);
            elements[3] = new CellValue(item.getContextTag()).asBox().withFont(RegularFont).bold();
            elements[4] = new CellValue(item.getChecklist().getName()).asBox().withFont(RegularFont);
            elements[5] = new CellValue("").asBox();


            addRow(sheet, (currentRow), elements, 1, 2550/5);
            feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.INFO, "Adding row in sheet " + sheet.getSheetName(), 0));

        }catch(Exception e){

            PukkaLogger.log( e );
            feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.ABORT, "Internal error adding definitions in sheet " + sheet.getSheetName(), 0));

        }


    }

    private void writeToChecklistSheet(ChecklistItem item, XSSFSheet sheet, int currentRow) {

        ParseFeedback feedback = new ParseFeedback();

        try{

            CellValue[] elements = new CellValue[6];

            elements[0] = new CellValue(item.getConformanceTag()).asBox().withFont(BoldFont);
            elements[1] = new CellValue(item.getName()).asBox().withFont(RegularFont);
            elements[2] = new CellValue(item.getDescription()).asBox().withFont(RegularFont);
            elements[3] = new CellValue(item.getContextTag()).asBox().withFont(RegularFont);
            elements[4] = new CellValue(item.getChecklist().getName()).asBox().withFont(RegularFont);
            elements[5] = new CellValue("").asBox();


            addRow(sheet, (currentRow), elements, 1, 2550/5);
            feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.INFO, "Adding row in sheet " + sheet.getSheetName(), 0));

        }catch(Exception e){

            PukkaLogger.log( e );
            feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.ABORT, "Internal error adding definitions in sheet " + sheet.getSheetName(), 0));

        }


    }



    /******************************************************************
     *
     *          Add classifications in the classifications tab
     *
     *          It uses the JSON object from the ClassificationOverview manager
     *
     * @param sheet         - the sheet to write to
     * @param project       - the current project
     * @return              - feedback
     */


    private ParseFeedbackItem addClassifications(XSSFSheet sheet, Project project) {

        ClassificationOverviewManager overview = new ClassificationOverviewManager();
        overview.compileClassificationsForProject(project, null);
        JSONObject json = overview.getStatistics();

        return traverseOverviewJson(sheet, json, 3, 5);

    }

    /**************************************************************************
     *
     *          Traverse the JSON and insert it in the sheet, a row at a time
     *          The level is used to create indentation levels in sheet
     *
     * @param sheet                - current sheet
     * @param node                 - The node in the generated tree
     * @param level                - indentation level
     * @param currentRow           - The current row for output
     *
     * @return                     - feedback
     */


    private ParseFeedbackItem traverseOverviewJson(XSSFSheet sheet, JSONObject node, int level, int currentRow){

        String name = node.getString("classification");
        String description = node.getString("comment");
        JSONObject statistics = node.getJSONObject("statistics");
        JSONArray children = node.getJSONArray("subClassifications");
        int hits = statistics.getInt("direct") + statistics.getInt("indirect");

        CellValue elements[] = new CellValue[20];

        elements[ 2] =    new CellValue().asRow();
        elements[ 3] =    new CellValue().asRow();
        elements[ 4] =    new CellValue().asRow();
        elements[ 5] =    new CellValue().asRow();
        elements[ 6] =    new CellValue().asRow();
        elements[ 7] =    new CellValue().asRow();
        elements[ 8] =    new CellValue().asRow();

        elements[level] = new CellValue(name).noWrap().asRow();
        elements[ 9] =    new CellValue(description).asRow();
        elements[10] =   new CellValue().asRow();
        elements[11] =    new CellValue(hits).asBox();

        addRow(sheet, currentRow++, elements, 0);


        for(int i = 0; i < children.length(); i++){

            ParseFeedbackItem feedback = traverseOverviewJson(sheet, children.getJSONObject( i ), level+1, currentRow);
            currentRow = feedback.row;

        }

        return new ParseFeedbackItem(ParseFeedbackItem.Severity.INFO, "Created classification structure", currentRow);

    }



    /******************************************************************************
     *
     *              Add a row for each document on a sheet
     *
     *
     * @param sheet    - sheet to put definitions on
     * @param allDocuments
     *

     */

    private ParseFeedback addDocumentList(XSSFSheet sheet, List<Contract> allDocuments) {

        ParseFeedback feedback = new ParseFeedback();

        try{

            int startingRow = 6;
            int id = 1;

            for (Contract document : allDocuments) {

                CellValue[] elements = new CellValue[4];

                ContractVersionInstance head = document.getHeadVersion();
                String versionName = head.getVersionDescription();


                elements[0] = new CellValue(document.getName()).asBox();                    //title
                elements[1] = new CellValue(document.getFile()).asBox();                    //filename
                elements[2] = new CellValue(document.getCreation().getISODate()).asBox();   //uploaded
                elements[3] = new CellValue(versionName).asBox();                     //version

                addRow(sheet, startingRow++, elements, 1);

            }

            feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.INFO, "Adding " + allDocuments.size() + " documents in sheet " + sheet.getSheetName(), 0));


        }catch(Exception e){

            PukkaLogger.log( e );
            feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.ABORT, "Internal error adding definitions in sheet " + sheet.getSheetName(), 0));

        }



        return feedback;
    }



    /**********************************************************************************
     *
     *              There are a number of external references
     *
     *               - Standards Compliance
     *
     *
     * @param sheet       - the sheet to write into
     *
     *                    //TODO: Add feedback
     */

    private ParseFeedback addReferences(XSSFSheet sheet) {

        ParseFeedback feedback = new ParseFeedback();

        try{

            List<FragmentClassification> classifications = project.getFragmentClassificationsForProject();
            int startingRow = 5;
            int id = 1;

            for (FragmentClassification classification : classifications) {

                if(classification.getClassTag().equals(FeatureTypeTree.STANDARDS_COMPLIANCE.getName())){

                    CellValue[] elements = new CellValue[6];

                    elements[0] = new CellValue(id++).asRow();
                    elements[1] = new CellValue(classification.getPattern()).asRow();
                    elements[2] = new CellValue(classification.getFragment().htmlDecode()).asRow();
                    elements[3] = new CellValue("Standards Compliance").asRow();
                    elements[4] = new CellValue(classification.getComment()).asRow();
                    elements[5] = new CellValue(classification.getVersion().getDocument().getName()).asRow();

                    addRow(sheet, startingRow++, elements, 1);
                }

            }

            feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.INFO, "Adding " + classifications.size() + " references in sheet " + sheet.getSheetName(), 0));

        }catch(Exception e){

            PukkaLogger.log( e );
            feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.ABORT, "Internal error adding definitions in sheet " + sheet.getSheetName(), 0));

        }

        return feedback;

    }



    /************************************************************************************
     *
     *          find and replace a cell content (queue token) with replacement text
     *          by going through all cells. The method will replace multiple occurences
     *
     *
     * @param sheet          - the active sheet to write in
     * @param token          - the token to replace
     * @param replacement    - new text
     *
     *
     */


    private ParseFeedback findAndReplace(XSSFSheet sheet, String token, String replacement) {

        Iterator ite = sheet.rowIterator();
        ParseFeedback feedback = new ParseFeedback();

        try{

            int replacements = 0;
            int searchedCells = 0;

            while(ite.hasNext() && searchedCells++ < SEARCH_LIMIT){

                Row row = (Row)ite.next();
                Iterator<Cell> cite = row.cellIterator();

                while(cite.hasNext()){

                    Cell c = cite.next();
                    //System.out.println("Cell: \"" + c.toString() + "\"");
                    if(c.toString().equals(token)){

                        CellValue replaceValue = new CellValue(replacement).withFont(BoldFont, 16);

                        int rowNo = row.getRowNum();
                        int colNo = c.getColumnIndex();
                        //System.out.println(" -- Found token " + token + "@(" + rowNo +","+ colNo+ ") in "+ sheet.getSheetName()+" to replace with " + replacement);

                        // Update

                        XSSFRow actualRow = sheet.getRow(rowNo);
                        XSSFCell actualCell = actualRow.getCell(colNo);
                        actualCell.setCellValue(replaceValue.getStringValue());
                        actualCell.setCellStyle(replaceValue.getStyle(sheet));

                        feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.INFO, "Replacing " + token + " with " + replacement + " in sheet " + sheet.getSheetName(), rowNo));
                        replacements++;

                    }
                }
            }

            if(replacements == 0){

                feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.ERROR, "Could not find token " + token + " in sheet " + sheet.getSheetName(), 0));

            }

        }catch(Exception e){

            PukkaLogger.log( e );
            feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.ABORT, "Internal error adding definitions in sheet " + sheet.getSheetName(), 0));

        }

        return feedback;
    }

    private void addRow(XSSFSheet sheet, int rowNo, CellValue[] values, int startColumn){

        addRow(sheet, rowNo, values, startColumn, -1);
    }

    /********************************************************************************************'
     *
     *              Add a new row to the sheet
     *
     * @param sheet
     * @param rowNo
     * @param values
     * @param startColumn
     * @param height               - height of the column in pixels
     */

    private void addRow(XSSFSheet sheet, int rowNo, CellValue[] values, int startColumn, int height){

        if(sheet == null){

            System.out.println(" -- Sheet is empty");
            return;

        }


        XSSFRow row = sheet.getRow( rowNo );
        int column = startColumn;

        if(row == null){

            // No row exists in th template. Create a row and try to assign again

            sheet.createRow( rowNo );
            row = sheet.getRow( rowNo );
            PukkaLogger.log(PukkaLogger.Level.DEBUG, "Created new row for Sheet=\"" + sheet.getSheetName() + "\" row = " + rowNo);
        }

        if(height != -1)
            row.setHeight( (short )height );


        for (CellValue value : values) {

            XSSFCell actualCell = row.getCell(column);

            if(value == null){
                column++;
                continue;
            }

            if(actualCell == null){
                actualCell = row.createCell(column);
            }

            // Get the value with the correct type

            switch (value.getType()) {

                case STRING:
                    actualCell.setCellValue(value.getStringValue());
                    break;
                case VALUE:
                    actualCell.setCellValue(value.getValue());
                    break;
                case EMPTY:
                    break;
            }

            actualCell.setCellStyle(value.getStyle(sheet));
            column++;

        }


    }



    /*******************************************************************************'
     *
     *              Lookup all annotations from the pre-fetched set of annotations
     *              and add them as a text.
     *
     *
     *
     * @param fragment         - the current fragment
     * @param annotations      - all annotations for the project
     * @return
     */


    private String getAnnotationsForFragment(ContractFragment fragment, List<ContractAnnotation> annotations) {

        StringBuffer annotationText = new StringBuffer();

        for (ContractAnnotation annotation : annotations) {

            if(annotation.getFragmentId().equals(fragment.getKey())){

                annotationText.append(annotation.getCreator().getName() + ": " +  annotation.getDescription());
            }
        }


        return annotationText.toString();

    }


    private String getRisksForFragment(ContractFragment fragment, List<RiskClassification> risks) {

        StringBuffer riskText = new StringBuffer();

        for (RiskClassification risk : risks) {

            if(risk.getFragmentId().equals(fragment.getKey())){

                riskText.append(risk.getRisk().getName());

                if(risk.getComment() != null && !risk.getComment().equals("")){

                    riskText.append(": " +  risk.getComment());
                }

                riskText.append("\n");
            }
        }


        return riskText.toString();

    }

    private String getClassificationsForFragment(ContractFragment fragment, List<FragmentClassification> classifications) {

        StringBuffer classificationText = new StringBuffer();

        for (FragmentClassification classification : classifications) {

            //System.out.println(" --- Testing: " + classification.getClassTag());

            if(classification.getFragmentId().equals(fragment.getKey())){

                if(classification.getSignificance() >= Significance.DISPLAY_SIGNIFICANCE){

                    if(classification.getblockingState() != FragmentClassification.OVERRIDDEN &&
                        classification.getblockingState() != FragmentClassification.BLOCKING ){

                        classificationText.append(classification.getClassTag());
                        classificationText.append("\n");

                    }

                }
                else{
                    System.out.println("        --- Ignoring because of not significant");
                }

            }
            //System.out.println("        --- Ignoring not applicable to the fragment");

        }


        return classificationText.toString();

    }


    /**************************************************************************
     *
     *          Convert the JSON list to an array of strings
     *
     *
     * @param exportTags              - json array with tags
     * @return                        - array of string
     * @throws BackOfficeException
     *
     *
     */


    private ExtractionTagList[] getListFromJSONParameter(String exportTags) throws BackOfficeException{

        try{

            if(exportTags == null || exportTags.equals(""))
                return null;

            ModuleInterface module = new ContractingModule(); //TODO: Not Implemented: Modules - Lookup which module to use

            JSONArray tagArray = new JSONArray(exportTags);

            ExtractionTagList[] tagList = new ExtractionTagList[tagArray.length()];

            for(int i = 0; i < tagArray.length(); i++){

                // For each given tag we need to lookup all the children as these should also match in the export.

                String tag = tagArray.getString( i );
                tagList[i] = new ExtractionTagList(tag);
                tagList[i].setChildren(module.getChildren(module.getNodeForTag(tag)));
            }


            System.out.println(" -- Got " + tagList.length + " tags from parameter (" + exportTags + ")");
            return tagList;

        }catch(JSONException e){

            throw new BackOfficeException(BackOfficeException.Usage, "Error reading tag parameter in export");
        }


    }


}

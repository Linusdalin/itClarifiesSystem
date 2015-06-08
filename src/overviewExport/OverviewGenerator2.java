package overviewExport;

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
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;
import risk.RiskClassification;
import services.DocumentService;
import userManagement.PortalUser;

import java.util.ArrayList;
import java.util.Iterator;
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


public class OverviewGenerator2 {

    // Include peers and children
    private static final boolean AddPeers = false;

    private static final int headerRows = 7;   // 6 rows in the sheets for header plus one empty. Start export after


    // Queues for the substitution in sheets

    private static final String SUBSTITUTE_PROJECT =     "$(PROJECT)";
    private static final String SUBSTITUTE_DATE =        "$(TIME)";
    private static final String SUBSTITUTE_TAG =         "$(TAG)";
    private static final String SUBSTITUTE_DESCRIPTION = "$(DESCRIPTION)";
    private static final String SUBSTITUTE_DEFINITION =  "$(DEFINITION)";

    // The hardcoded list of tags for the extraction
    // TODO: This should be replaced with parameters in the request

    private static final String[] tagExtractions = {



            "#TERM_AND_TERMINATION",
            "#RIGHTS_AND_OBLIGATIONS",
            "#STANDARDS_COMPLIANCE",
            //"#PARTY",
            //"#BACKGROUND",
            //"#DEADLINE",
            //"#ACCEPTANCE",
            //"#DELIVERY",
            //"#GOVERNANCE",
            //"#DATE",


    };

    private static final int SEARCH_LIMIT = 1000;  // Max number of cells to search before failing replace

    private Project project;                    // The actual project we are exporting
    private ExtractionStatus statusEntry;       // The status entry
    private DBTimeStamp exportDate;             // Date of export for all timestamps in the analysis

    // The sheets in the template. we hard code these and get them by name
    // Alternatively we could get them by number, but on the other hand they may change place

    XSSFSheet[] sheets;


    private Extraction emptyLine;

    /***************************************************************************
     *
     *          Initiate the generator for generating the export
     *
     *
     * @param project       - the project to export
     * @param creator       - user generating the export
     * @param comment       - user initiated comment
     *
     * @throws pukkaBO.exceptions.BackOfficeException
     */


    public OverviewGenerator2(Project project, PortalUser creator, String comment) throws BackOfficeException{

        this.project = project;
        exportDate = new DBTimeStamp();

        String description = "no descr.";
        String name = "Export: " + creator.getName() + "@" + exportDate.getISODate();

        ExtractionStatusTable statusEntriesForProject = new ExtractionStatusTable(new LookupList().addFilter(new ReferenceFilter(ExtractionStatusTable.Columns.Project.name(), project.getKey())));
        statusEntriesForProject.delete();

        boolean isDirty = false; // When created it is not modified


        statusEntry = new ExtractionStatus(name, exportDate.getISODate(), creator.getKey(), project.getKey(), comment, isDirty, description);
        statusEntry.store();

        // Just for test

        List<ExtractionStatus> es = project.getExtractionStatusForProject();
        System.out.println("Now there are " + es.size() + " status entries");


        emptyLine = new Extraction("", "", "", "", 0, "", this.project.getKey(), this.project.getKey(), "", "", "", "", statusEntry.getKey());

    }


    public OverviewGenerator2(XSSFWorkbook template, Project project) {

        this.project = project;
        int noSheets = template.getNumberOfSheets();

        sheets = new XSSFSheet[noSheets];

        // Get the Standard sheets

        this.sheets[ 0] = template.getSheet("Report Overview");
        this.sheets[ 1] = template.getSheet("Documents");
        this.sheets[ 2] = template.getSheet("Definitions");
        this.sheets[ 3] = template.getSheet("Classifications");
        this.sheets[ 4] = template.getSheet("External Ref");
        this.sheets[ 5] = template.getSheet("Risks");

        // Loop over the rest of the sheets to get the predefined sheets

        for(int i = 6; i < template.getNumberOfSheets(); i++){

            this.sheets[ i ] = template.getSheetAt( i );
            PukkaLogger.log(PukkaLogger.Level.DEBUG, " --- Located sheet " + i + " named " +this.sheets[ i ].getSheetName());

        }


    }



    /************************************************************************'
     *
     *       Pre-calculate all sheets and store it in the database
     *
     *
     *
     */

    public ParseFeedback preCalculate() {

        // Get basic values for the export

        ParseFeedback feedback = new ParseFeedback();

        // Get all attributes, so that we don't have to look-up once per tag

        List<FragmentClassification> allClassifications = project.getFragmentClassificationsForProject();
        List<ContractAnnotation>     allAnnotations = project.getContractAnnotationsForProject();
        List<RiskClassification>     allRisks = project.getRiskClassificationsForProject();
        List<Definition>             allDefinitions = project.getDefinitionsForProject();
        List<Contract>               allDocuments = project.getContractsForProject(new LookupList().addSorting(new Sorting(ContractTable.Columns.Ordinal.name(), Ordering.FIRST)));

        feedback.add(deleteOldExtractions(project));

        feedback.add(handleExtraction(allDocuments, allClassifications, allAnnotations, allRisks, allDefinitions));

        // Update the status with the feedback from the analysis

        try {

            statusEntry.setDescription(feedback.toJSON().toString());
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
     * @param sheets            - All sheets
     * @param exportTime        - Time of export
     * @return
     */

    private ParseFeedback handleSubstitutions(XSSFSheet[] sheets, String exportTime) {

        ParseFeedback feedback = new ParseFeedback();

        for(int i = 0; i < 6; i++){

            feedback.add(substitute(sheets[i+1], "", exportTime));
        }


        for(int i = 0; i < tagExtractions.length; i++){

            feedback.add(substitute(sheets[i+7], tagExtractions[i], exportTime));
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

        System.out.println("Find and replace " + tag);

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
     * @param allDocuments                 - complete list from the database
     * @param allClassifications           - complete list from the database
     * @param allAnnotations               - complete list from the database
     * @param allRisks                     - complete list from the database
     * @param allDefinitions               - complete list from the database
     * @return                             - user feedback
     */

    private ParseFeedback handleExtraction(List<Contract> allDocuments,
                                           List<FragmentClassification> allClassifications,
                                           List<ContractAnnotation> allAnnotations,
                                           List<RiskClassification> allRisks,
                                           List<Definition> allDefinitions) {


        ParseFeedback feedback = new ParseFeedback();

        //Create an empty table to store the new extractions

        ExtractionTable table = new ExtractionTable();
        table.createEmpty();

        for (Contract document : allDocuments) {

            try{

                ContractVersionInstance head = document.getHeadVersion();
                List<ContractFragment> fragmentsForDocument = head.getFragmentsForVersion(new LookupList().addSorting(new Sorting(ContractFragmentTable.Columns.Ordinal.name(), Ordering.FIRST)));
                System.out.println("Found " + fragmentsForDocument.size() + " fragments in document " + document.getName());

                List<DataObjectInterface> extractionsForDocument = new ArrayList<DataObjectInterface>();

                for (ContractFragment fragment : fragmentsForDocument) {


                    // Skip going through fragments that do not have a classification count


                    if(fragment.getClassificatonCount() != 0){

                        extractionsForDocument.addAll(matchClassification(fragment, document, allClassifications, fragmentsForDocument, allRisks, allAnnotations));

                    }

                    extractionsForDocument.addAll(matchRisk(fragment, document, allRisks, allAnnotations));
                    extractionsForDocument.addAll(addDefinitions(fragment, document, allDefinitions));

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
            System.out.println("Extraction "+ (i++) +": " + extraction.toString());
        }


        try {
            table.store();
        } catch (BackOfficeException e) {
            PukkaLogger.log( e );
        }


        return feedback;
    }


    /****************************************************************************************************
     *
     *
     * @param fragment
     * @param document
     * @param allClassifications
     * @param fragmentsForDocument
     * @param allRisks
     * @param allAnnotations
     */

    private List<DataObjectInterface> matchClassification(ContractFragment fragment, Contract document, List<FragmentClassification> allClassifications, List<ContractFragment> fragmentsForDocument, List<RiskClassification> allRisks, List<ContractAnnotation> allAnnotations) {

        List<DataObjectInterface> extractionsForDocument = new ArrayList<DataObjectInterface>();

        for (FragmentClassification classification : allClassifications) {

            try{

                if(classification.getFragmentId().equals(fragment.getKey())){

                    // We found a classification that match the fragment we look at.

                    for (String tagExtraction : tagExtractions) {

                        String match = tagExtraction + " ";  // Make sure we only match the full word

                        if (classification.getKeywords().contains(match)) {

                            System.out.println("  --- Found match " + classification.getClassTag() + " in fragment " + fragment.getName());

                            // Create a new entry for the classification

                            Extraction entry = new Extraction(
                                    "",
                                    getClassificationsForFragment(fragment, allClassifications),
                                    fragment.htmlDecode(),
                                    fragment.getKey().toString(),
                                    (int) fragment.getOrdinal(),
                                    "",
                                    project.getKey(),
                                    document.getKey(),
                                    "",
                                    getRisksForFragment(fragment, allRisks),
                                    getAnnotationsForFragment(fragment, allAnnotations),
                                    tagExtraction,
                                    statusEntry.getKey());

                            if (fragment.getType().equals("HEADING")) {

                                entry.asHeadline((int) fragment.getIndentation());
                                extractionsForDocument.add(emptyLine);
                                extractionsForDocument.add(entry);

                                if (AddPeers)
                                    extractionsForDocument.addAll(findPeers(tagExtraction, fragment, document, fragmentsForDocument, allAnnotations, allClassifications, allRisks, false));
                            } else if (fragment.getType().equals("LIST")) {

                                if (AddPeers)
                                    extractionsForDocument.addAll(findPeers(tagExtraction, fragment, document, fragmentsForDocument, allAnnotations, allClassifications, allRisks, true));
                            } else {
                                System.out.println("Adding '" + entry.getText() + "'");
                                extractionsForDocument.add(entry);

                            }
                            System.out.println("Done..");

                        }
                    }

                }


            }catch(Exception e){

                PukkaLogger.log( e );

            }


        }

        return extractionsForDocument;

    }



    /*********************************************************************************************************
     *
     *
     * @param fragment            - the current fragment in the project
     * @param document
     *@param allRisks            - All risks in the project (prefetched from database)
     * @param allAnnotations      - All annotations in the project (prefetched from database)   @return
     */

    private List<Extraction> matchRisk(ContractFragment fragment, Contract document, List<RiskClassification> allRisks, List<ContractAnnotation> allAnnotations) {

        List<Extraction> extractionsForFragment = new ArrayList<Extraction>();

        for (RiskClassification risk : allRisks) {

            try{

                if(risk.getFragmentId().equals(fragment.getKey())){

                    String riskClass = risk.getRisk().getName();

                    Extraction entry = new Extraction(
                            "",
                            "",
                            fragment.htmlDecode(),
                            fragment.getKey().toString(),
                            (int)fragment.getOrdinal(),
                            "",
                            project.getKey(),
                            document.getKey(),
                            riskClass,
                            risk.getComment(),
                            fragment.getVersion().getDocument().getName(),
                            "#Risk",
                            statusEntry.getKey());

                        System.out.println("Adding risk '" + entry.getText() + "'");
                        extractionsForFragment.add(entry);

                    }

            }catch(Exception e){

                PukkaLogger.log( e );

            }


        }
        return extractionsForFragment;

    }


    /*******************************************************************************************
     *
     *
     * @param fragment
     * @param document
     *@param allDefinitions  @return
     */

    private List<Extraction> addDefinitions(ContractFragment fragment, Contract document, List<Definition> allDefinitions) {

        List<Extraction> extractionsForFragment = new ArrayList<Extraction>();

        for (Definition definition : allDefinitions) {

            try{

                if(definition.getDefinedInId().equals(fragment.getKey())){

                    Extraction entry = new Extraction(
                            definition.getName(),
                            "",
                            fragment.htmlDecode(),
                            fragment.getKey().toString(),
                            (int)fragment.getOrdinal(),
                            "",
                            project.getKey(),
                            document.getKey(),
                            "",
                            "",
                            definition.getVersion().getDocument().getName(),
                            "#Definition",
                            statusEntry.getKey());

                        System.out.println("Adding definition " + entry.getName() + " '" + entry.getText() + "'");
                        extractionsForFragment.add(entry);

                    }

            }catch(Exception e){

                PukkaLogger.log( e );

            }


        }
        return extractionsForFragment;

    }



    /***********************************************************************************
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

        List<Extraction> extractionsForProject = project.getExtractionsForProject();
        int[] rowNo = new int[sheets.length];

        exportDate = latestGeneration.getDate();
        feedback.add(handleSubstitutions(sheets, exportDate.getISODate()));

        feedback.add(handleDocumentList(project));
        Extraction lastExtraction = null; // For checking if we shall display a title

        for (Extraction extraction : extractionsForProject) {

            PukkaLogger.log(PukkaLogger.Level.INFO, " --- Parsing extraction: " + extraction.toString());


            int sheetNo = getSheet(extraction.getSheet());

            if(sheetNo < 0){

                PukkaLogger.log(PukkaLogger.Level.ERROR, "Could not find sheet " + extraction.getSheet());
                continue;
            }

            XSSFSheet sheet = sheets[ sheetNo ];
            int currentRow = rowNo[sheetNo];

            if(isNewDocument(extraction, lastExtraction)){

                Extraction headline = new Extraction("", "", extraction.getDocument().getName(), "", 0, "Title", null, null, "", "", "", "", extraction.getKey());

                currentRow = writeToSheet(emptyLine, sheet, currentRow);
                currentRow = writeToSheet(headline, sheet, currentRow);

            }

            int newRow = writeToSheet(extraction, sheet, currentRow);
            rowNo[sheetNo] = newRow;
            lastExtraction = extraction;

        }

        return new ParseFeedback(); // TODO: Add some feedback here

    }

    private boolean isNewDocument(Extraction extraction, Extraction lastExtraction) {

        if(lastExtraction == null)
            return true;

        return !extraction.getDocumentId().equals(lastExtraction.getDocumentId());

    }

    private ParseFeedbackItem handleDocumentList(Project project) {

        XSSFSheet sheet = sheets[ 1 ];
        int currentRow = 1;

        List<Contract> documents = project.getContractsForProject();


        for (Contract document : documents) {

            writeToSheet(document, sheet, currentRow++);
        }


        return new ParseFeedbackItem(ParseFeedbackItem.Severity.INFO, "Added " + (currentRow - 1) + " docuemnts", 0);
    }


    /*************************************************************
     *
     *          Get the id of the given sheet
     *
     *
     * @param sheet
     * @return
     */

    private int getSheet(String sheet) {

        if(sheet.equals("#Definition"))
            return 2;

        if(sheet.equals("#Risk"))
            return 5;


        for (int i = 0; i < tagExtractions.length; i++) {

            if(tagExtractions[i].equals(sheet))
                return i + 7;
        }
        return -1;

    }



    private int writeToSheet(Extraction extraction,  XSSFSheet sheet, int currentRow) {

        ParseFeedback feedback = new ParseFeedback();

        List<String> keys = new ArrayList<String>();  // Store the used keys to avoid duplicates

        try{

            CellValue[] elements = new CellValue[8];

            elements[0] = new CellValue(extraction.getName());
            elements[1] = new CellValue(extraction.getClassification());
            elements[2] = new CellValue("");
            elements[3] = new CellValue(extraction.getText());
            elements[4] = new CellValue(extraction.getRisk());
            elements[5] = new CellValue(extraction.getDescription());
            elements[6] = new CellValue(extraction.getComment());

            if(extraction.getStyle().equals("Title")){

               //System.out.println(" --- Setting bold and italics for style Title");
                elements[3].withFont(22).bold().italics();

            }

            if(extraction.getStyle().equals("Heading")){
                        //System.out.println(" --- Setting bold and italics for style Heading");
                        elements[3].withFont(16).bold().italics();
            }
            if(extraction.getStyle().equals("Text")){
                        //System.out.println(" --- Setting font for text");
                        elements[3].withFont(12);


            }

            addRow(sheet, (headerRows + currentRow++), elements, 1);
            feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.INFO, "Adding row in sheet " + sheet.getSheetName(), 0));

        }catch(Exception e){

            PukkaLogger.log( e );
            feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.ABORT, "Internal error adding definitions in sheet " + sheet.getSheetName(), 0));

        }

        return currentRow;

    }



    private void writeToSheet(Contract document, XSSFSheet sheet, int currentRow) {

        ParseFeedback feedback = new ParseFeedback();

        try{

            CellValue[] elements = new CellValue[8];

            elements[0] = new CellValue(document.getName());
            elements[1] = new CellValue(document.getFile());
            elements[2] = new CellValue(document.getCreation().getISODate());
            elements[3] = new CellValue(document.getHeadVersion().getVersion());


            addRow(sheet, (headerRows + currentRow++), elements, 1);
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

        addRow(sheet, currentRow++, elements);


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

                if(classification.getClassTag().equals(FeatureTypeTree.StandardsCompliance.getName())){

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

                        int rowNo = row.getRowNum();
                        int colNo = c.getColumnIndex();
                        //System.out.println(" -- Found token " + token + "@(" + rowNo +","+ colNo+ ") in "+ sheet.getSheetName()+" to replace with " + replacement);

                        // Update

                        XSSFRow actualRow = sheet.getRow(rowNo);
                        XSSFCell actualCell = actualRow.getCell(colNo);
                        actualCell.setCellValue(replacement);
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

    private void addRow(XSSFSheet sheet, int rowNo, CellValue[] values){

        addRow(sheet, rowNo, values, 0);
    }

    private void addRow(XSSFSheet sheet, int rowNo, CellValue[] values, int startColumn){

        XSSFRow row = sheet.getRow( rowNo );
        int column = startColumn;

        if(row == null){

            // No row exists in th template. Create a row and try to assign again

            sheet.createRow( rowNo );
            row = sheet.getRow( rowNo );
            System.out.println("Created new row for Sheet=\"" + sheet.getSheetName() + "\" row = " + rowNo);
        }

        System.out.println(" ! Adding row " + rowNo + " to sheet " + sheet.getSheetName());


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

            if(classification.getFragmentId().equals(fragment.getKey()) &&
                    classification.getSignificance() > Significance.DISPLAY_SIGNIFICANCE){

                classificationText.append(classification.getClassTag());
                classificationText.append("\n");
            }
        }


        return classificationText.toString();

    }


}

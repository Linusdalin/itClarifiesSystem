package overviewExport;

import analysis.ParseFeedback;
import analysis.ParseFeedbackItem;
import classification.ClassificationOverviewManager;
import classification.FragmentClassification;
import contractManagement.*;
import crossReference.Definition;
import dataRepresentation.DBTimeStamp;
import databaseLayer.DBKeyInterface;
import databaseLayer.DatabaseAbstractionFactory;
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
import pukkaBO.condition.LookupByKey;
import pukkaBO.condition.LookupList;
import pukkaBO.condition.Ordering;
import pukkaBO.condition.Sorting;
import pukkaBO.exceptions.BackOfficeException;
import risk.RiskClassification;
import search.SearchManager2;
import userManagement.PortalUser;

import java.util.*;

/**
 *              Generate the project overview from a template
 *
 *              // TODO: Add error handling for the template if it is changing
 *              // TODO: Write numbers as numbers, not text
 *              // TODO: External reference links
 *              // TODO: add classifications, risk and annotations in search filter output
 *              //TODO: Look info using XSSFRichTextString
 */
public class OverviewGenerator {

    // Queues for the substitution

    private static final String SUBSTITUTE_PROJECT = "$(PROJECT)";
    private static final String SUBSTITUTE_DATE = "$(TIME)";
    private static final String SUBSTITUTE_TAG = "$(TAG)";

    private static final String[] tagExtractions = {

            "#PARTY",
            "#BACKGROUND",
            "#TERMS",
            "#TERMINATION",
            "#DEADLINE",
            "#ACCEPTANCE",
            "#DELIVERY",
            "#RESPONSIBILITY",
            "#DATE",

    };


    private final Project project; // The actual project we are exporting

    // The sheets in the template. we hard code these and get them by name
    // Alternatively we could get them by number, but on the other hand they may change place

    XSSFSheet[] sheets;

    /**********************************************************************'
     *
     *          Initiate the generator
     *
     *
     * @param template      - the excel template file
     * @param project       - the project to export
     */


    public OverviewGenerator(XSSFWorkbook template, Project project) {

        this.project = project;
        int noSheets = template.getNumberOfSheets();

        sheets = new XSSFSheet[noSheets];

        // Get the right sheets (regardless of order in document

        this.sheets[ 0] = template.getSheet("Report Overview");
        this.sheets[ 1] = template.getSheet("Documents");
        this.sheets[ 2] = template.getSheet("Definitions");
        this.sheets[ 3] = template.getSheet("Classifications");
        this.sheets[ 4] = template.getSheet("External Ref");
        this.sheets[ 5] = template.getSheet("Risks");

        for(int i = 6; i < template.getNumberOfSheets(); i++){

            this.sheets[ i ] = template.getSheetAt( i );
            System.out.println(" --- Located sheet " + i + " named " +this.sheets[ i ].getSheetName());

        }


        /*

        this.sheets[ 6] = template.getSheet("Parties");
        this.sheets[ 7] = template.getSheet("Background");
        this.sheets[ 8] = template.getSheet("Terms");
        this.sheets[ 9] = template.getSheet("Termination");
        this.sheets[ 9] = template.getSheet("Deadlines");
        this.sheets[10] = template.getSheet("Acceptance");
        this.sheets[11] = template.getSheet("Delivery");
        this.sheets[12] = template.getSheet("Responsibility");
        this.sheets[13] = template.getSheet("Contract Checklist");
        this.sheets[14] = template.getSheet("Classifications");

        */
    }

    /************************************************************************'
     *
     *          Populate the sheet
     *
     *
     *
     */

    public ParseFeedback populate() {

        ParseFeedback feedback = new ParseFeedback();

        String projectName = project.getName();
        String exportDate = new DBTimeStamp().getISODate();

        // Get all attributes, so that we don't have to look-up once per tag

        List<FragmentClassification> allClassifications = project.getFragmentClassificationsForProject();
        List<ContractAnnotation> allAnnotations = project.getContractAnnotationsForProject();
        List<RiskClassification> allRisks = project.getRiskClassificationsForProject();
        List<Contract> allDocuments = project.getContractsForProject();


        for (int sheetNo = 0; sheetNo < sheets.length; sheetNo++) {

            if(sheets[sheetNo] == null)
                PukkaLogger.log(PukkaLogger.Level.WARNING, "Could not find sheet " + sheetNo );
            else{

                feedback.add(findAndReplace(sheets[sheetNo], SUBSTITUTE_PROJECT, projectName));
                feedback.add(findAndReplace(sheets[sheetNo], SUBSTITUTE_DATE, exportDate));
            }

        }


        feedback.add(addDocumentList(sheets[1]));
        feedback.add(addDefinitions(sheets[2]));
        feedback.add(addClassifications(sheets[3], project));
        feedback.add(addReferences(sheets[4]));
        feedback.add(addRisks(sheets[5]));

        // Go through all the fragments of all the documents and write them to the correct sheet

        SheetExtraction[] extractionPerSheet = new SheetExtraction[tagExtractions.length];

        for (Contract document : allDocuments) {

            try{

                ContractVersionInstance head = document.getHeadVersion();

                List<ContractFragment> fragmentsForDocument = head.getFragmentsForVersion();

                System.out.println("Found " + fragmentsForDocument.size() + " fragments in document " + document.getName());

                for(int i = 0; i < tagExtractions.length; i++){

                    System.out.println("Extracting fragments for classification tag " + tagExtractions[i]);

                    if(extractionPerSheet[i] == null)
                        extractionPerSheet[i] = new SheetExtraction();

                    feedback.add(extractRelevantFragments(tagExtractions[i], document, extractionPerSheet[i], fragmentsForDocument, allClassifications));


                }

            }catch(Exception e){

                PukkaLogger.log( e );
            }
        }
        for(int i = 0; i < tagExtractions.length; i++){

            feedback.add(addToSheet(sheets[i + 7], extractionPerSheet[i], tagExtractions[i], allClassifications, allAnnotations, allRisks));

        }


        return feedback;
    }


    private ParseFeedbackItem extractRelevantFragments(String tagExtraction, Contract document, SheetExtraction sheetExtraction,
                                                       List<ContractFragment> fragmentsForDocument,
                                                       List<FragmentClassification> allClassifications) {


        List<ExtractionFragment> extractionsForThisDocument = new ArrayList<ExtractionFragment>();
        ExtractionFragment emptyLine = new ExtractionFragment("", null, 0);

        for (ContractFragment fragment : fragmentsForDocument) {

            for (FragmentClassification classification : allClassifications) {

                //System.out.println("  -- matching " + classification.getClassTag() + " against " + tagExtraction);

                if(classification.getClassTag().equals(tagExtraction)){

                    if(classification.getFragmentId().equals(fragment.getKey())){

                        ExtractionFragment body = new ExtractionFragment(fragment.htmlDecode(), fragment.getKey().toString(), 0);

                        if(fragment.getType().equals("HEADING")){
                            body.asHeadline((int)fragment.getIndentation());
                            extractionsForThisDocument.add(emptyLine);
                            extractionsForThisDocument.add(body);
                            extractionsForThisDocument.addAll(findPeers(fragment, fragmentsForDocument, false));
                        }
                        else if(fragment.getType().equals("LIST")){

                            extractionsForThisDocument.addAll(findPeers(fragment, fragmentsForDocument, true));
                        }
                        else{
                            extractionsForThisDocument.add(body);

                        }

                    }


                }
            }

        }

        if(extractionsForThisDocument.size() > 0){

            sheetExtraction.fragments.add(emptyLine);
            ExtractionFragment documentHeadline = new ExtractionFragment(document.getName(), null, 0).asTitle();
            sheetExtraction.fragments.add(documentHeadline);

        }

        sheetExtraction.fragments.addAll(extractionsForThisDocument);



        return new ParseFeedbackItem(ParseFeedbackItem.Severity.ERROR, "Not implemented extract", 0);
    }

    //TODO: Recursively go through the structure. This will skip subsections

    private List<ExtractionFragment> findPeers(ContractFragment fragment, List<ContractFragment> fragmentsForDocument, boolean includeSelf) {

        List<ExtractionFragment> children = new ArrayList<ExtractionFragment>();

        for (ContractFragment otherFragment : fragmentsForDocument) {

            // Find all other fragments with the same structure number but not is the actual text

            if(otherFragment.getStructureNo() == fragment.getStructureNo()){

                if(includeSelf || otherFragment.getOrdinal() != fragment.getOrdinal()){

                    children.add(new ExtractionFragment(otherFragment.htmlDecode(), otherFragment.getKey().toString(), 0));

                }
            }

        }

        return children;


    }


    private ParseFeedbackItem addClassifications(XSSFSheet sheet, Project project) {

        ClassificationOverviewManager overview = new ClassificationOverviewManager();
        overview.compileClassificationsForProject(project, null);
        JSONObject json = overview.getStatistics();

        ParseFeedbackItem feedback = traverseOverviewJson(sheet, json, 3, 5);

        return feedback;

    }


    private ParseFeedbackItem traverseOverviewJson(XSSFSheet sheet, JSONObject node, int level, int currentRow){

        String name = node.getString("classification");
        String description = node.getString("comment");
        JSONObject statistics = node.getJSONObject("statistics");
        JSONArray children = node.getJSONArray("subClassifications");
        int hits = statistics.getInt("direct") + statistics.getInt("indirect");

        CellValue elements[] = new CellValue[20];

        elements[level] = new CellValue(name).noWrap();
        elements[ 9] =    new CellValue(description);
        elements[11] =    new CellValue(hits);

        addRow(sheet, currentRow++, elements);


        for(int i = 0; i < children.length(); i++){

            ParseFeedbackItem feedback = traverseOverviewJson(sheet, children.getJSONObject( i ), level+1, currentRow);
            currentRow = feedback.row;

        }

        return new ParseFeedbackItem(ParseFeedbackItem.Severity.INFO, "Created classification structure", currentRow);

    }




    /******************************************************************************
     *
     *              Add a row for each definition on a sheet
     *
     *
     * @param sheet    - sheet to put definitions on
     *
     *                 //TODO: Add feedback
     */

    private ParseFeedback addDefinitions(XSSFSheet sheet) {

        ParseFeedback feedback = new ParseFeedback();

        try{

            List<Definition> definitions = project.getDefinitionsForProject();
            int startingRow = 6;
            int id = 1;

            for (Definition definition : definitions) {

                CellValue[] elements = new CellValue[9];
                String documentName = "unknown";

                String definitionText = "";

                if(definition.getDescription() == null || definition.getDefinition().equals("")){

                    ContractFragment sourceFragment = definition.getDefinedIn();

                    if(sourceFragment.exists()){
                        definitionText = sourceFragment.getText();
                        documentName = sourceFragment.getVersion().getDocument().getName();

                    }
                }
                else
                    definitionText = definition.getDescription();

                elements[0] = new CellValue("" + id++);
                elements[1] = new CellValue(definition.getName());
                elements[2] = new CellValue(definitionText);
                elements[3] = new CellValue("");
                elements[4] = new CellValue("");
                elements[7] = new CellValue(documentName);
                elements[8] = new CellValue("");

                addRow(sheet, startingRow++, elements, 1);

            }

            feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.INFO, "Adding " + definitions.size() + " definitions in sheet " + sheet.getSheetName(), 0));


        }catch(Exception e){

            PukkaLogger.log( e );
            feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.ABORT, "Internal error adding definitions in sheet " + sheet.getSheetName(), 0));

        }



        return feedback;
    }




    /******************************************************************************
     *
     *              Add a row for each document on a sheet
     *
     *
     * @param sheet    - sheet to put definitions on
     *

     */

    private ParseFeedback addDocumentList(XSSFSheet sheet) {

        ParseFeedback feedback = new ParseFeedback();

        try{

            List<Contract> documents = project.getContractsForProject();
            int startingRow = 6;
            int id = 1;

            for (Contract document : documents) {

                CellValue[] elements = new CellValue[4];

                ContractVersionInstance head = document.getHeadVersion();


                elements[0] = new CellValue(document.getName());                    //title
                elements[1] = new CellValue(document.getFile());                    //filename
                elements[2] = new CellValue(document.getCreation().getISODate());   //uploaded
                elements[3] = new CellValue(head.getVersion());                     //version

                addRow(sheet, startingRow++, elements, 1);

            }

            feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.INFO, "Adding " + documents.size() + " documents in sheet " + sheet.getSheetName(), 0));


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
            int startingRow = 6;
            int id = 1;

            for (FragmentClassification classification : classifications) {

                if(classification.getClassTag().equals(FeatureTypeTree.StandardsCompliance.getName())){

                    CellValue[] elements = new CellValue[6];

                    elements[0] = new CellValue("" + id++);
                    elements[1] = new CellValue(classification.getPattern());
                    elements[2] = new CellValue(classification.getFragment().htmlDecode());
                    elements[3] = new CellValue("Standards Compliance");
                    elements[4] = new CellValue(classification.getComment());
                    elements[5] = new CellValue(classification.getVersion().getDocument().getName());

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



    /******************************************************************************
     *
     *              Add a row for each risk on a sheet
     *
     *
     * @param sheet    - sheet to put definitions on
     *
     *             //TODO: Set colour coding and frame of cell
     *             //TODO: Add feedback
     */

    private ParseFeedback addRisks(XSSFSheet sheet) {

        ParseFeedback feedback = new ParseFeedback();

        try{

            List<RiskClassification> risks = project.getRiskClassificationsForProject();
            int startingRow = 6;  // Row 7 in the excel sheet
            int ordinal = 1;

            for (RiskClassification risk : risks) {

                CellValue[] elements = new CellValue[5];
                ContractFragment fragment = risk.getFragment();

                elements[0] = new CellValue("" + ordinal);
                elements[1] = new CellValue(fragment.htmlDecode());
                elements[2] = new CellValue(risk.getRisk().getName());
                elements[3] = new CellValue("");   //Risk description not implemented
                elements[4] = new CellValue(risk.getVersion().getDocument().getName());

                addRow(sheet, startingRow++, elements, 1);

                ordinal++;
            }

            feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.INFO, "Adding " + risks.size() + " risks in sheet " + sheet.getSheetName(), 0));

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
     *          //TODO: Add feedback
     *
     */


    private ParseFeedback findAndReplace(XSSFSheet sheet, String token, String replacement) {

        Iterator ite = sheet.rowIterator();
        ParseFeedback feedback = new ParseFeedback();

        try{

            int replacements = 0;

            while(ite.hasNext()){

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


        for (CellValue value : values) {

            XSSFCell actualCell = row.getCell(column);

            if(value == null){
                column++;
                continue;
            }

            if(actualCell == null){
                actualCell = row.createCell(column);
                //System.out.println("Created new cell for Sheet=\""+sheet.getSheetName()+"\" Column = " + column + " row = " + rowNo);
            }

            actualCell.setCellValue(value.getStringValue());
            actualCell.setCellStyle(value.getStyle(sheet));
            column++;

        }


    }




    /****************************************************************************''
     *
     *          Return the fragments related to a searchString
     *
     *           - including "neighbours"
     *
     *
     * @param project
     * @param searchString
     * @return
     *
     *          //TODO: Add secondary search here too
     */

    private List<ExtractionFragment> getFragmentsForSearch(Project project, String searchString) {

        List<ExtractionFragment> searchResult = new LinkedList<ExtractionFragment>();

        PortalUser user;
        LanguageInterface searchStringLanguage = new English();

        try {
            user = PortalUser.getSystemUser();

        } catch (BackOfficeException e) {

            System.out.println("Error getting system user");
            return searchResult;

        }

        SearchManager2 searchManager = new SearchManager2(project, user );
        JSONArray searchResults = searchManager.search(searchString, searchStringLanguage);

        System.out.println(" --- Got " + searchResults + " results in the search");

        List<ContractFragment> fragmentsForSearch = toFragments(searchResults);


        // Loop over all documents to see which search results belong to which document
        // We want to add the search results document by document anyway

        //TODO: Optimization: This could be optimized by going through the result once and sort the results in different lists

        List<Contract> documentsForProject = project.getContractsForProject(
                new LookupList().addSorting(new Sorting(ContractTable.Columns.Ordinal.name(), Ordering.FIRST)));

        for (Contract contract : documentsForProject) {

            try {

                List<ExtractionFragment> resultForDocument = new LinkedList<ExtractionFragment>();

                PukkaLogger.log(PukkaLogger.Level.INFO, "Looking for hits for document " + contract.getName());

                ContractVersionInstance head = contract.getHeadVersion();

                for (ContractFragment fragment : fragmentsForSearch) {

                    if(fragment.exists()){

                        addToResult(fragment, head, contract.getName(), resultForDocument);

                          //TODO: Add neighbours
                    }
                    else{

                        PukkaLogger.log(PukkaLogger.Level.ERROR, " -- Fragment found in index but not in database");
                    }

                }

                searchResult.addAll(resultForDocument);

            } catch (BackOfficeException e) {

                PukkaLogger.log( e );
            }


        }


        return searchResult;

    }

    private List<ContractFragment> toFragments(JSONArray searchResults) {

        List<ContractFragment> fragments = new ArrayList<ContractFragment>();

        for(int i = 0; i < searchResults.length(); i++){

            JSONObject result = searchResults.getJSONObject(i);

            //Get the real fragment

            //TODO: get these once and for all

            DBKeyInterface _fragment = new DatabaseAbstractionFactory().createKey(result.getString("fragment"));
            ContractFragment fragment = new ContractFragment(new LookupByKey(_fragment));
            fragments.add(fragment);
        }
        return fragments;
    }

    /************************************************************************
     *
     *      Add to result will add the fragment to its correct place in the list
     *      (if it is not a duplicate)
     *
     *
     * @param fragment          - the fragment
     * @param version           - the head version for the document
     * @param documentTitle     - title
     * @param searchResult      - the accumulated result
     */


    private void addToResult(ContractFragment fragment, ContractVersionInstance version, String documentTitle, List<ExtractionFragment> searchResult) {

        if(fragment.getVersionId().equals(version.getKey())){

            if(searchResult.size() == 0){

                // This is the first result. Add a chapter reference first

                PukkaLogger.log(PukkaLogger.Level.INFO, "Adding document headline " + documentTitle);
                ExtractionFragment emptyLine = new ExtractionFragment("", null, 0);
                searchResult.add(emptyLine);
                ExtractionFragment documentHeadline = new ExtractionFragment(documentTitle, null, 0).asTitle();
                searchResult.add(documentHeadline);

            }


            for(int i = 0; i < searchResult.size(); i++){

                if(searchResult.get( i ).getOrdinal() == fragment.getOrdinal()){

                    // Same ordinal in same document. This is a duplicate

                    PukkaLogger.log(PukkaLogger.Level.INFO, "Duplicate fragment " + fragment.getOrdinal());
                    return;

                }

                if(searchResult.get( i ).getOrdinal() > fragment.getOrdinal()){

                    // This is the position. Add fragment to search result

                    PukkaLogger.log(PukkaLogger.Level.INFO, "Inserting new fragment " + fragment.getOrdinal());
                    ExtractionFragment extractionFragment = new ExtractionFragment(fragment.getText(), fragment.getKey().toString(), (int)fragment.getOrdinal());
                    searchResult.add(extractionFragment);
                    return;

                }

            }

            ExtractionFragment extractionFragment = new ExtractionFragment(fragment.getText(), fragment.getKey().toString(), (int)fragment.getOrdinal());

            StructureItem structureItem = fragment.getStructureItem();

            if(fragment.getOrdinal() == structureItem.getTopElement()){

                extractionFragment.asHeadline((int) structureItem.getIndentation());

            }

            searchResult.add(extractionFragment);


        }

    }



    private ParseFeedback addSearchExtraction(XSSFSheet sheet, Project project, String searchString) {

        int startingRow = 7;
        ParseFeedback feedback = new ParseFeedback();

        try{

            System.out.println(" -- In search selection. Search: " + searchString);

            // Write the correct tag

            feedback.add(findAndReplace(sheet, SUBSTITUTE_TAG, searchString));


            List<ExtractionFragment> fragmentsForTags = getFragmentsForSearch(project, searchString);
            List<ContractAnnotation> annotations = project.getContractAnnotationsForProject();
            List<RiskClassification> risks = project.getRiskClassificationsForProject();


            for (ExtractionFragment fragment : fragmentsForTags) {

                System.out.println("Adding fragment: " + fragment.getText());

                CellValue[] elements = new CellValue[8];

                elements[0] = new CellValue("");
                elements[1] = new CellValue("");
                elements[2] = new CellValue("");
                elements[3] = new CellValue("");
                elements[4] = new CellValue(fragment.getText());
                elements[5] = new CellValue("");
                elements[6] = new CellValue(getRisksForFragment(fragment, risks));
                elements[7] = new CellValue(getAnnotationsForFragment(fragment, annotations));

                switch (fragment.getStyle()) {

                    case Title:
                        //System.out.println(" --- Setting bold and italics for style Title");
                        elements[4].withFont(20).bold().italics();
                        break;
                    case Heading:
                        //System.out.println(" --- Setting bold and italics for style Heading");
                        elements[4].withFont(20).bold().italics();
                        break;
                    case Text:
                        //System.out.println(" --- Setting font for text");
                        elements[4].withFont(12);

                        break;
                }

                if(fragment.getStyle() == ExtractionFragment.Style.Heading){

                }


                addRow(sheet, startingRow++, elements, 0);
            }

            feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.INFO, "Adding " + fragmentsForTags.size() + " rows in sheet " + sheet.getSheetName() + " for extraction " + searchString, 0));

        }catch(Exception e){

            PukkaLogger.log( e );
            feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.ABORT, "Internal error adding definitions in sheet " + sheet.getSheetName(), 0));

        }

        return feedback;

    }

    private ParseFeedback addToSheet(XSSFSheet sheet, SheetExtraction extraction, String tag, List<FragmentClassification> allClassifications, List<ContractAnnotation> allAnnotations, List<RiskClassification> allRisks) {

        int startingRow = 7;
        ParseFeedback feedback = new ParseFeedback();

        try{

            System.out.println(" -- In search extraction.");

            feedback.add(findAndReplace(sheet, SUBSTITUTE_TAG, tag));


            for (ExtractionFragment fragment : extraction.fragments) {

                System.out.println("Adding fragment: " + fragment.getText());

                CellValue[] elements = new CellValue[8];

                elements[0] = new CellValue("");
                elements[1] = new CellValue(getClassificationsForFragment(fragment, allClassifications));
                elements[2] = new CellValue("");
                elements[3] = new CellValue(fragment.getText());
                elements[4] = new CellValue("");
                elements[5] = new CellValue(getRisksForFragment(fragment, allRisks));
                elements[6] = new CellValue(getAnnotationsForFragment(fragment, allAnnotations));

                switch (fragment.getStyle()) {

                    case Title:
                        //System.out.println(" --- Setting bold and italics for style Title");
                        elements[3].withFont(22).bold().italics();
                        break;
                    case Heading:
                        //System.out.println(" --- Setting bold and italics for style Heading");
                        elements[3].withFont(16).bold().italics();
                        break;
                    case Text:
                        //System.out.println(" --- Setting font for text");
                        elements[3].withFont(12);

                        break;
                }

                if(fragment.getStyle() == ExtractionFragment.Style.Heading){

                }


                addRow(sheet, startingRow++, elements, 0);
            }

            feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.INFO, "Adding " + extraction.fragments.size() + " rows in sheet " + sheet.getSheetName(), 0));

        }catch(Exception e){

            PukkaLogger.log( e );
            feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.ABORT, "Internal error adding definitions in sheet " + sheet.getSheetName(), 0));

        }

        return feedback;

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


    private String getAnnotationsForFragment(ExtractionFragment fragment, List<ContractAnnotation> annotations) {

        StringBuffer annotationText = new StringBuffer();

        for (ContractAnnotation annotation : annotations) {

            if(annotation.getFragmentId().toString().equals(fragment.getKey())){

                annotationText.append(annotation.getCreator().getName() + ": " +  annotation.getDescription());
            }
        }


        return annotationText.toString();

    }


    private String getRisksForFragment(ExtractionFragment fragment, List<RiskClassification> risks) {

        StringBuffer annotationText = new StringBuffer();

        for (RiskClassification risk : risks) {

            if(risk.getFragmentId().toString().equals(fragment.getKey())){

                annotationText.append(risk.getRisk().getName());

                if(risk.getComment() != null && !risk.getComment().equals("")){

                    annotationText.append(": " +  risk.getComment());
                }

                annotationText.append("\n");
            }
        }


        return annotationText.toString();

    }

    private String getClassificationsForFragment(ExtractionFragment fragment, List<FragmentClassification> classifications) {

        StringBuffer classificationText = new StringBuffer();

        for (FragmentClassification classification : classifications) {

            if(classification.getFragmentId().toString().equals(fragment.getKey())){

                classificationText.append(classification.getClassTag());
                classificationText.append("\n");
            }
        }


        return classificationText.toString();

    }



    private List<ContractFragment> getFragmentsForTags(Project project, String[] tags) {

        List<ContractFragment> searchResult = new ArrayList<ContractFragment>();

        // First extract all data from the database

        List<Contract> documents = this.project.getContractsForProject();
        List<FragmentClassification> classificationsForProject = project.getFragmentClassificationsForProject();
        List<ContractFragment> fragments = this.project.getContractFragmentsForProject();

        for (FragmentClassification classification : classificationsForProject) {

            String tag = classification.getClassTag();

            if(isOneOf(tag, tags)){

                ContractFragment fragment = classification.getFragment();
                System.out.println("Found a fragment " + fragment.getName() + " for tag" + tag);


            }
        }

        return searchResult;

    }

    private boolean isOneOf(String tag, String[] tags) {

        for (String tagInList : tags) {

            if(tag.equals(tagInList)){

                return true;
            }
        }
        return false;


    }


}

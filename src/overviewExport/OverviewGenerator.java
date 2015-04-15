package overviewExport;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *              Generate the project overview from a template
 *
 *              // TODO: Add error handling for the template if it is changing
 *              // TODO: Add feedback and return to the user
 *              // TODO: Add cell styling
 *              // TODO: Write numbers as numbers, not text
 *              // TODO: External reference links
 *              // TODO: Optionally add classifications and annotations in search filter output
 *              // TODO: Add style to the text (font, size, bold run, headlines etc)
 *              // TODO: In search selections, preserve order and add document name as divider
 *
 */
public class OverviewGenerator {

    // Queues for the substitution

    private static final String SUBSTITUTE_PROJECT = "$(PROJECT)";
    private static final String SUBSTITUTE_DATE = "$(TIME)";

    private final Project project; // The actual project we are exporting

    // The sheets in the template. we hard code these and get them by name
    // Alternatively we could get them by number, but on the other hand they may change place

    XSSFSheet[] sheets = new XSSFSheet[14];

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

        // Get the right sheets (regardless of order in document

        this.sheets[ 0] = template.getSheet("Report Overview");
        this.sheets[ 1] = template.getSheet("Documents");
        this.sheets[ 2] = template.getSheet("Definitions");
        this.sheets[ 3] = template.getSheet("Definition Source Graph");
        this.sheets[ 4] = template.getSheet("External Ref");
        this.sheets[ 5] = template.getSheet("Risks");
        this.sheets[ 6] = template.getSheet("Background");
        this.sheets[ 7] = template.getSheet("Terms");
        this.sheets[ 8] = template.getSheet("Schedule");
        this.sheets[ 9] = template.getSheet("Acceptance");
        this.sheets[10] = template.getSheet("Delivery");
        this.sheets[11] = template.getSheet("Responsibility");
        this.sheets[12] = template.getSheet("Contract Checklist");
        this.sheets[13] = template.getSheet("Classifications");

    }

    /************************************************************************'
     *
     *          Populate the sheet
     *
     *
     *
     */

    public void populate() {

        String projectName = project.getName();
        String exportDate = new DBTimeStamp().getISODate();

        for (int sheetNo = 0; sheetNo < sheets.length; sheetNo++) {

            if(sheets[sheetNo] == null)
                PukkaLogger.log(PukkaLogger.Level.ERROR, "Could not find sheet " + sheetNo );
            else{

                findAndReplace(sheets[sheetNo], SUBSTITUTE_PROJECT, projectName);
                findAndReplace(sheets[sheetNo], SUBSTITUTE_DATE, exportDate);
            }

        }


        addDefinitions(sheets[2]);
        addReferences(sheets[4]);
        addRisks(sheets[5]);
        addSearchSelection(sheets[ 6], project, "#Percentage");             //TODO: Change back to background. This is just for testing
        //addSearchSelection(sheets[ 7], project, "#Terms");
        //addSearchSelection(sheets[ 9], project, "#Acceptance");
        //addSearchSelection(sheets[10], project, "#Delivery");
        //addSearchSelection(sheets[11], project, "#Responsibility");

    }


    /******************************************************************************
     *
     *              Add a row for each definition on a sheet
     *
     *
     * @param sheet    - sheet to put definitions on
     */

    private void addDefinitions(XSSFSheet sheet) {

        List<Definition> definitions = project.getDefinitionsForProject();
        int startingRow = 6;
        int id = 1;

        for (Definition definition : definitions) {

            CellValue[] elements = new CellValue[9];

            String definitionText;

            if(definition.getDescription() == null || definition.getDefinition().equals(""))
                definitionText = definition.getDefinedIn().getText();
            else
                definitionText = definition.getDescription();

            elements[0] = new CellValue("" + id++);
            elements[1] = new CellValue(definition.getName());
            elements[2] = new CellValue(definitionText);
            elements[3] = new CellValue("");
            elements[4] = new CellValue("");
            elements[5] = new CellValue("");   //Comment
            elements[6] = new CellValue("All");
            elements[7] = new CellValue(definition.getVersion().getDocument().getName());
            elements[8] = new CellValue("");

            addRow(sheet, startingRow++, elements, 1);

        }

    }

    /**********************************************************************************
     *
     *              There are a number of external references
     *
     *               - Standards Compliance
     *
     *
     * @param sheet       - the sheet to write into
     */

    private void addReferences(XSSFSheet sheet) {

        List<FragmentClassification> classifications = project.getFragmentClassificationsForProject();
        int startingRow = 6;
        int id = 1;

        for (FragmentClassification classification : classifications) {

            if(classification.getClassTag().equals(FeatureTypeTree.StandardsCompliance.getName())){

                CellValue[] elements = new CellValue[6];

                elements[0] = new CellValue("" + id++);
                elements[1] = new CellValue(classification.getPattern());
                elements[2] = new CellValue(classification.getFragment().getText());
                elements[3] = new CellValue("Standards Compliance");
                elements[4] = new CellValue(classification.getComment());
                elements[5] = new CellValue(classification.getVersion().getDocument().getName());

                addRow(sheet, startingRow++, elements, 1);
            }

        }

    }



    /******************************************************************************
     *
     *              Add a row for each risk on a sheet
     *
     *
     * @param sheet    - sheet to put definitions on
     *
     *             //TODO: Set colour coding and frame of cell
     *
     */

    private void addRisks(XSSFSheet sheet) {

        List<RiskClassification> risks = project.getRiskClassificationsForProject();
        int startingRow = 6;  // Row 7 in the excel sheet
        int ordinal = 1;

        for (RiskClassification risk : risks) {

            CellValue[] elements = new CellValue[5];
            ContractFragment fragment = risk.getFragment();

            elements[0] = new CellValue("" + ordinal);
            elements[1] = new CellValue(fragment.getText());
            elements[2] = new CellValue(risk.getRisk().getName());
            elements[3] = new CellValue("");   //Risk description not implemented
            elements[4] = new CellValue(risk.getVersion().getDocument().getName());

            addRow(sheet, startingRow++, elements, 1);

            ordinal++;
        }



    }

    /************************************************************************************
     *
     *          find and replace a cell content (queue token) with replacement text
     *          by going through all cells. The method will replace multiple occurences
     *
     *
     * @param sheet
     * @param token
     * @param replacement
     *
     *
     */


    private void findAndReplace(XSSFSheet sheet, String token, String replacement) {

        Iterator ite = sheet.rowIterator();

        while(ite.hasNext()){

            Row row = (Row)ite.next();
            Iterator<Cell> cite = row.cellIterator();

            while(cite.hasNext()){

                Cell c = cite.next();
                //System.out.println("Cell: \"" + c.toString() + "\"");
                if(c.toString().equals(token)){

                    int rowNo = row.getRowNum();
                    int colNo = c.getColumnIndex();
                    System.out.println(" -- Found token " + token + "@(" + rowNo +","+ colNo+ ") in "+ sheet.getSheetName()+" to replace with " + replacement);

                    // Update

                    XSSFRow actualRow = sheet.getRow(rowNo);
                    XSSFCell actualCell = actualRow.getCell(colNo);
                    actualCell.setCellValue(replacement);

                }
            }
        }

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

            if(actualCell == null){
                actualCell = row.createCell(column);
                System.out.println("Created new cell for Sheet=\""+sheet.getSheetName()+"\" Column = " + column + " row = " + rowNo);
            }

            actualCell.setCellValue(value.getStringValue());
            actualCell.setCellStyle(value.getStyle(sheet));
            column++;

        }


    }



    /****************************************************************************''
     *
     *          Return the fragments related to a tag
     *
     *           - including "neighbours"
     *
     *
     * @param project
     * @param tag
     * @return
     */

    private List<ExtractionFragment> getFragmentsForSearch(Project project, String tag) {

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
        JSONArray searchResults = searchManager.search(tag, searchStringLanguage);

        List<ContractFragment> fragmentsForSearch = toFragments(searchResults);


        // Loop over all documents to see which search results belong to which document
        // We want to add the search results document by document anyway

        //TODO: Add document title for all documents found
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

                        PukkaLogger.log(PukkaLogger.Level.ERROR, "Fragment " + fragment.getName() + " found in index but not in database");
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
                ExtractionFragment emptyLine = new ExtractionFragment("", 0);
                searchResult.add(emptyLine);
                ExtractionFragment documentHeadline = new ExtractionFragment(documentTitle, 0).asTitle();
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
                    ExtractionFragment extractionFragment = new ExtractionFragment(fragment.getText(), (int)fragment.getOrdinal());
                    searchResult.add(extractionFragment);
                    return;

                }

            }

            ExtractionFragment extractionFragment = new ExtractionFragment(fragment.getText(), (int)fragment.getOrdinal());
            searchResult.add(extractionFragment);


        }

    }

    /**********************************************************************
     *
     *          Perform a search and insert the results into the sheet
     *
     * @param sheet                - the sheet to write to
     * @param project              - project to extract
     * @param searchString         - the search to filter on
     *
     *     NOTE: The language is used for the translation of #tags. It has nothing to do with the language(s) of documents
     *
     *     //TODO: There is no access control built into this. It uses the system user with unlimited access
     *
     */



    private void addSearchSelection(XSSFSheet sheet, Project project, String searchString) {

        int startingRow = 7;
        List<ExtractionFragment> fragmentsForTags = getFragmentsForSearch(project, searchString);

        for (ExtractionFragment fragment : fragmentsForTags) {

            System.out.println("Adding fragment: " + fragment.getText());

            CellValue[] elements = new CellValue[8];

            elements[0] = new CellValue("");
            elements[1] = new CellValue("");
            elements[2] = new CellValue("");
            elements[3] = new CellValue("");
            elements[4] = new CellValue(fragment.getText());
            elements[5] = new CellValue("");
            elements[6] = new CellValue("");
            elements[7] = new CellValue("");

            if(fragment.getStyle() == ExtractionFragment.Style.Heading){

                elements[4].withFont(20).bold().italics();
                System.out.println("  -- Setting font size for headline");
            }

            addRow(sheet, startingRow++, elements, 0);
        }


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

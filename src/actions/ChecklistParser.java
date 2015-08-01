package actions;

import analysis.ParseFeedback;
import analysis.ParseFeedbackItem;
import classification.FragmentClass;
import classification.FragmentClassTable;
import classifiers.ClassifierInterface;
import contractManagement.ContractFragment;
import dataRepresentation.DBTimeStamp;
import document.AbstractFragment;
import document.CellInfo;
import document.FragmentSplitterInterface;
import language.English;
import language.LanguageInterface;
import log.PukkaLogger;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupByKey;
import pukkaBO.condition.LookupItem;
import pukkaBO.exceptions.BackOfficeException;
import userManagement.PortalUser;

import java.util.List;

/********************************************************************************************************'
 *
 *          handle parsing checklists either as a separate excel document or
 *          as embedded into a compliance document
 *
 *          The Parser handles all checklists in a document and they are opened and closed by:
 *
 *           - startNewChecklist(checklist) and
 *           - closeCurrentChecklist();
 *
 *          In-between these the call
 *
 *           - parseChecklistCell()
 *
 *           will parse information in the cell and build up the currentChecklistItem. When a new line is detected, the
 *           item will be stored.
 *
 *           NOTE: It is necessary to close the last item manually as there is no new line there
 *
 *           The parser also provides the functionality to map the source text representation to actual
 *           source fragments when the actual fragments are stored (and have a DBKey) with the method
 *
 *           - mapItemSources(List<ContractFragment>)
 *
 *
 */

public class ChecklistParser {

    private static final String[] checklistHeadlines = {"Id", "Name", "Description","#Conformance", "#Context", "Comment", "Source"};

    private FragmentSplitterInterface doc;
    private Checklist currentChecklist = null;
    private ChecklistItem currentItem;  // The current item that we are building up
    private String currentSourceText;   // The source text for the current item

    PortalUser owner;


    // Create a map for the connection from a ChecklistItem to the source fragment.
    // As the source fragments are not yet stored at the point of parsing, we put this in a list and
    // Iterate through it when all the fragments from the document is stored.

    private List<SourceMap> sourceMap = new java.util.ArrayList<SourceMap>();


    /**********************************************************************
     *
     *          Create the parser given a complete document and a checklist item
     *
     *
     * @param doc
     * @param owner
     */

    public ChecklistParser(FragmentSplitterInterface doc, PortalUser owner){

        this.doc = doc;
        this.owner = owner;

    }

    /***********************************************************************
     *
     *          Starting a new checklist (as there can be more than one checklist in the document.
     *
     *          If there is one checklist that is open, we complete it.
     *
     *
     * @param checklist
     */

    public ParseFeedbackItem startNewChecklist(Checklist checklist){

        ParseFeedbackItem feedbackItem = null;

        if(hasOpenCheckist())
            feedbackItem = endCurrentChecklist();

        this.currentChecklist = checklist;
        currentItem = createNewItem();

        return feedbackItem;

    }

    /********************************************************'
     *
     *          Close the current checklist
     *
     *
     * @return
     */

    public ParseFeedbackItem  endCurrentChecklist(){

        ParseFeedbackItem feedback = storeDefinition(0);
        currentChecklist = null;
        return feedback;

    }

    /*********************************************************************
     *
     *          map all found source references to the appropriate fragments found in the list
     *
     *
     * @param documentFragments     - list of fragments (with a proper key)
     */

    public void mapItemSources(List<ContractFragment> documentFragments){

        System.out.println(" *** There are " + sourceMap.size() + " items stored in the source map!");

        for (SourceMap map : sourceMap) {

            if(map.sourceText.length() < 10){

                PukkaLogger.log(PukkaLogger.Level.INFO, "Ignoring source reference \""+ map.sourceText + "\" for item " + map.itemName + " source reference too short");
                break;
            }

            // Look in all fragments

            for (ContractFragment documentFragment : documentFragments) {

                //Very simple match. It could be more lenient
                // Minimum of 10 characters to avoid white space and accidental comments

                if(documentFragment.getText().toLowerCase().contains(map.sourceText.toLowerCase())){

                    try {

                        ChecklistItem item = new ChecklistItem(new LookupByKey(map.fragmentKey));

                        item.setSource(documentFragment.getKey());
                        item.update();
                        PukkaLogger.log(PukkaLogger.Level.INFO, "Setting source fragment on Checklist Item " + map.itemName + " source reference \"" + map.sourceText + "\" found in " + item.getName());
                        PukkaLogger.log(PukkaLogger.Level.DEBUG, "source ref:" + map.sourceText.length()    );
                        break;

                    } catch (BackOfficeException e) {
                        PukkaLogger.log(PukkaLogger.Level.FATAL, "Unable to update source");
                    }
                }


            }

        }

    }


    /******************************************************************************'
     *
     *      parsing a checklist from the entire document
     *
     *
     *      //TODO: Improvement Performance: Add batch store here
     *
     */


    public ParseFeedback parseChecklist() {

        List<AbstractFragment> fragments = doc.getFragments();
        ParseFeedback feedback = new ParseFeedback();

        try{

            for (AbstractFragment fragment : fragments) {

                ParseFeedbackItem cellFeedback = parseChecklistCell(fragment);
                if(cellFeedback != null){

                    feedback.add(cellFeedback);
                    if(cellFeedback.severity == ParseFeedbackItem.Severity.ABORT)
                        return feedback;
                }
            }

        }catch(Exception e){

            PukkaLogger.log( e );
            feedback.add(new ParseFeedbackItem(ParseFeedbackItem.Severity.ABORT, "Internal Error: " + e.getLocalizedMessage(), 0));

        }


        return feedback;

    }


    /************************************************************************************************
     *
     *          Create  new item with standard values
     *
     *
     * @return
     *
     *
     */


    private ChecklistItem createNewItem(){

        currentSourceText = null;

        return new ChecklistItem((long)0, (long)0, "name", "text", "comment", currentChecklist.getKey(),  null, null,
                currentChecklist.getProjectId(), "", "", ActionStatus.getOpen(), new DBTimeStamp().getSQLTime().toString());
    }



    /**********************************************************************'
     *
     *              Update the currentItem with what is found in the cell
     *
     *
     * @param fragment
     * @return
     *
     *
     */


    public ParseFeedbackItem parseChecklistCell(AbstractFragment fragment){

        ParseFeedbackItem feedback = null;

        try{

            CellInfo cellInfo = fragment.getCellInfo();

            if(cellInfo == null){

                PukkaLogger.log(PukkaLogger.Level.DEBUG, "Checklist Parser: Ignoring non cell fragment " + fragment.getBody());
            }
            else{

                PukkaLogger.log(PukkaLogger.Level.DEBUG, "Checklist Parser: Handling cell fragment " + fragment.getBody());

                // Handle table data for the checklist

                if(cellInfo.row == 1 ){

                    // The expectation is that the first row is a headline

                    if(cellInfo.col < checklistHeadlines.length && !fragment.getBody().equalsIgnoreCase(checklistHeadlines[cellInfo.col])){

                        abortCurrentChecklist();
                        return(new ParseFeedbackItem(ParseFeedbackItem.Severity.ABORT, "Checklist Parser: Expected to find " + checklistHeadlines[cellInfo.col] + " as title in cell (" + cellInfo.row +", " +  cellInfo.col + "). Found " + fragment.getBody(), cellInfo.row));
                    }

                    if(cellInfo.col == 5){

                        // Source. This column is parsed and hidden. Remove the title too
                        return(new ParseFeedbackItem(ParseFeedbackItem.Severity.HIDE, "Removing Source column header.", cellInfo.row));


                    }


                }


                if(cellInfo.row > 2 && cellInfo.col == 0){

                    // New row, store the old row

                    feedback = storeDefinition(cellInfo.row);

                }

                if(cellInfo.row > 1 && cellInfo.col == 0){


                    // New row, create a new item
                    currentItem = new ChecklistItem((long)0, (long)0, "name", "text", "comment", currentChecklist.getKey(),  null, null,
                            currentChecklist.getProjectId(), "", "", ActionStatus.getOpen(), new DBTimeStamp().getSQLTime().toString());

                    if(fragment.getBody().equals("")){

                        return(new ParseFeedbackItem(ParseFeedbackItem.Severity.INFO, "Ended checklist ( no more id... )", cellInfo.row));

                    }


                    try{

                        int id = new Double(fragment.getBody()).intValue();
                        //System.out.println("Extracted " + id + " from " + fragment.getBody());
                        currentItem.setIdentifier(id);

                    }catch(NumberFormatException e){

                        abortCurrentChecklist();
                        return(new ParseFeedbackItem(ParseFeedbackItem.Severity.ABORT, "Expected to find number (id) in cell (" + cellInfo.row +", " +  cellInfo.col + "). Found " + fragment.getBody(), cellInfo.row));

                    }
                }

                if(cellInfo.row > 1 && cellInfo.col == 1){

                    currentItem.setName(fragment.getBody());
                }

                if(cellInfo.row > 1 && cellInfo.col == 2){

                    currentItem.setDescription(fragment.getBody());
                }


                // **************************************************
                //  Handle conformance tag



                if(cellInfo.row > 1 && cellInfo.col == 3){

                    String tag = fragment.getBody();

                    if(tag.equals("")){
                        return(new ParseFeedbackItem(ParseFeedbackItem.Severity.WARNING, "Empty #Conformance column, no tag set for checklist item.", cellInfo.row));

                    }
                    if(!tag.startsWith("#")){

                        abortCurrentChecklist();
                        return(new ParseFeedbackItem(ParseFeedbackItem.Severity.ABORT, "Expected to find #TAG in tag column. Found " + tag, cellInfo.row));

                    }
                    String trimmedTag = tag.trim();
                    PukkaLogger.log(PukkaLogger.Level.INFO, "*** Setting Conformance tag:" + trimmedTag);

                    currentItem.setConformanceTag(trimmedTag); // Remove # and potential trailing space

                    FragmentClass classification = new FragmentClass(new LookupItem()
                            .addFilter(new ColumnFilter(FragmentClassTable.Columns.Name.name(), tag)));

                    if(!classification.exists()){

                        FragmentClass newClass = new FragmentClass(tag, tag, "", "Conformance tag for checklist item " + currentItem.getName(), owner.getOrganizationId());
                        newClass.store();
                    }


                }

                //  ************************************''
                //   Handle context tag


                if(cellInfo.row > 1 && cellInfo.col == 4){

                    String tag = fragment.getBody();

                    if(tag.equals("")){
                        return(new ParseFeedbackItem(ParseFeedbackItem.Severity.WARNING, "Empty #Context column, no tag set for checklist item.", cellInfo.row));

                    }
                    if(!tag.startsWith("#")){

                        abortCurrentChecklist();
                        return(new ParseFeedbackItem(ParseFeedbackItem.Severity.ABORT, "Expected to find #TAG in tag column. Found " + tag, cellInfo.row));

                    }


                    LanguageInterface languageForDocument = new English();  // Using english for the tags

                    // Lookup the tag. Either in the static tree or custom tags in the database

                    String trimmedTag = tag.trim();
                    String tagClass = getTagByName(trimmedTag.substring(1), languageForDocument);

                    if(tagClass == null){

                        return(new ParseFeedbackItem(ParseFeedbackItem.Severity.ERROR, "The classification tag " + tag + " does not exist. No checklist item created", cellInfo.row));
                    }


                    PukkaLogger.log(PukkaLogger.Level.INFO, "*** Setting Contact tag:" + tagClass);

                    currentItem.setContextTag(tagClass); // Remove # and potential trailing space
                }


                if(cellInfo.row > 1 && cellInfo.col == 5){

                    currentItem.setComment(fragment.getBody());
                }

                if(cellInfo.row > 1 && cellInfo.col == 6){

                    currentSourceText = fragment.getBody().trim();  //Store this until we create the item.
                    return(new ParseFeedbackItem(ParseFeedbackItem.Severity.HIDE, "Found source reference", cellInfo.row));
                }


            }


        }catch(Exception e){

            PukkaLogger.log( e );
            abortCurrentChecklist();
            return(new ParseFeedbackItem(ParseFeedbackItem.Severity.ABORT, "Internal Error:" + e.getLocalizedMessage(), 0));

        }
        return feedback;
    }


    public ParseFeedbackItem storeDefinition(int row){

        try{
            PukkaLogger.log(PukkaLogger.Level.INFO, "Storing a checklist item");
            currentItem.store();

            if(currentSourceText != null)
                sourceMap.add(new SourceMap(currentItem.getKey(), currentSourceText, currentItem.getName()));

            return new ParseFeedbackItem(ParseFeedbackItem.Severity.INFO, "Created checklist item "+ currentItem.getName()+" with conformance tag " + currentItem.getConformanceTag() + " for tag context " + currentItem.getContextTag(), row);

        }catch(BackOfficeException e){

            abortCurrentChecklist();
            return new ParseFeedbackItem(ParseFeedbackItem.Severity.ABORT, "FAILED to create checklist item "+ currentItem.getName(), row);

        }
    }

    private void abortCurrentChecklist() {
        currentChecklist = null;
    }


    public boolean hasOpenCheckist() {

        return currentChecklist != null;
    }

    /****************************************************************
     *
     *          Lookup the tag by name (in the given language)
     *
     *
     * @param tagName         - name (like Date without the # )
     * @param language        - language to look up in
     * @return                - The name of the tag class (e.g. #DATE)
     */



    public static String getTagByName(String tagName, LanguageInterface language) {

        ClassifierInterface[] classifiers = language.getAllClassifiers();

        for (ClassifierInterface classifier : classifiers) {

            System.out.println(" --- Comparing classes \"" + classifier.getClassificationName() + "\" and \"" + tagName + "\"");

            if(classifier.getClassificationName().equals(tagName)){

                PukkaLogger.log(PukkaLogger.Level.INFO, "Found static classTag " + tagName);
                return classifier.getType().getName();   // This should be the #TAG as this is the key to the frontend
            }
        }

        return null;
    }

}

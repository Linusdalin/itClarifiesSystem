package actions;

import analysis.AnalysisFeedback;
import analysis.AnalysisFeedbackItem;
import contractManagement.ContractFragment;
import dataRepresentation.DBTimeStamp;
import document.AbstractFragment;
import document.CellInfo;
import document.FragmentSplitterInterface;
import log.PukkaLogger;
import pukkaBO.condition.LookupByKey;
import pukkaBO.exceptions.BackOfficeException;

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

    private static final String[] checklistHeadlines = {"Id", "Name", "Description","#Tag", "Comment", "Source"};

    private FragmentSplitterInterface doc;
    private Checklist currentChecklist = null;
    private ChecklistItem currentItem;  // The current item that we are building up
    private String currentSourceText;   // The source text for the current item

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
     */

    public ChecklistParser(FragmentSplitterInterface doc){

        this.doc = doc;
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

    public void startNewChecklist(Checklist checklist){

        if(hasOpenCheckist())
            endCurrentChecklist();

        this.currentChecklist = checklist;
        currentItem = createNewItem();


    }

    public void endCurrentChecklist(){

        storeDefinition(0);
        currentChecklist = null;

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
                        System.out.println("source ref:" + map.sourceText.length()    );
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
     *      //TODO: Optimization: Add batch store here
     *
     */


    public AnalysisFeedback parseChecklist() {

        List<AbstractFragment> fragments = doc.getFragments();
        AnalysisFeedback feedback = new AnalysisFeedback();

        try{

            for (AbstractFragment fragment : fragments) {

                AnalysisFeedbackItem cellFeedback = parseChecklistCell(fragment);
                if(cellFeedback != null){

                    feedback.add(cellFeedback);
                    if(cellFeedback.severity == AnalysisFeedbackItem.Severity.ABORT)
                        return feedback;
                }
            }

        }catch(Exception e){

            PukkaLogger.log( e );
            feedback.add(new AnalysisFeedbackItem(AnalysisFeedbackItem.Severity.ABORT, "Internal Error: " + e.getLocalizedMessage(), 0));

        }


        return feedback;

    }


    private ChecklistItem createNewItem(){

        currentSourceText = null;

        return new ChecklistItem((long)0, (long)0, "name", "text", "comment", currentChecklist.getKey(),  null, null,
                currentChecklist.getProjectId(), "", ActionStatus.getOpen(), new DBTimeStamp().getSQLTime().toString());
    }



    /**********************************************************************'
     *
     *              Update the currentItem with what is found in the cell
     *
     *
     * @param fragment
     * @return
     *
     *              //TODO: Handle source
     *
     */


    public AnalysisFeedbackItem parseChecklistCell(AbstractFragment fragment){

        AnalysisFeedbackItem feedback = null;

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
                        return(new AnalysisFeedbackItem(AnalysisFeedbackItem.Severity.ABORT, "Checklist Parser: Expected to find " + checklistHeadlines[cellInfo.col] + " as title in cell (" + cellInfo.row +", " +  cellInfo.col + "). Found " + fragment.getBody(), cellInfo.row));
                    }

                    if(cellInfo.col == 5){

                        // Source. This column is parsed and hidden. Remove the title too
                        return(new AnalysisFeedbackItem(AnalysisFeedbackItem.Severity.HIDE, "Removing Source column header.", cellInfo.row));


                    }


                }


                if(cellInfo.row > 2 && cellInfo.col == 0){

                    // New row, store the old row

                    feedback = storeDefinition(cellInfo.row);

                }

                if(cellInfo.row > 1 && cellInfo.col == 0){


                    // New row, create a new item
                    currentItem = new ChecklistItem((long)0, (long)0, "name", "text", "comment", currentChecklist.getKey(),  null, null,
                            currentChecklist.getProjectId(), "", ActionStatus.getOpen(), new DBTimeStamp().getSQLTime().toString());

                    if(fragment.getBody().equals("")){

                        return(new AnalysisFeedbackItem(AnalysisFeedbackItem.Severity.INFO, "Ended checklist ( no more id... )", cellInfo.row));

                    }


                    try{

                        int id = new Double(fragment.getBody()).intValue();
                        System.out.println("Extracted " + id + " from " + fragment.getBody());
                        currentItem.setIdentifier(id);

                    }catch(NumberFormatException e){

                        abortCurrentChecklist();
                        return(new AnalysisFeedbackItem(AnalysisFeedbackItem.Severity.ABORT, "Expected to find number (id) in cell (" + cellInfo.row +", " +  cellInfo.col + "). Found " + fragment.getBody(), cellInfo.row));

                    }
                }

                if(cellInfo.row > 1 && cellInfo.col == 1){

                    currentItem.setName(fragment.getBody());
                }

                if(cellInfo.row > 1 && cellInfo.col == 2){

                    currentItem.setDescription(fragment.getBody());
                }

                if(cellInfo.row > 1 && cellInfo.col == 3){

                    String tag = fragment.getBody();

                    if(tag.equals("")){
                        return(new AnalysisFeedbackItem(AnalysisFeedbackItem.Severity.WARNING, "Empty tag column, no tag set for checklist item.", cellInfo.row));

                    }
                    if(!tag.startsWith("#")){

                        abortCurrentChecklist();
                        return(new AnalysisFeedbackItem(AnalysisFeedbackItem.Severity.ABORT, "Expected to find #TAG in tag column. Found " + tag, cellInfo.row));

                    }
                    String trimmedTag = tag.trim();
                    System.out.println("*** Storing tag:" + trimmedTag);

                    currentItem.setTagReference(trimmedTag); // Remove # and potential trailing space
                }

                if(cellInfo.row > 1 && cellInfo.col == 4){

                    currentItem.setComment(fragment.getBody());
                }

                if(cellInfo.row > 1 && cellInfo.col == 5){

                    currentSourceText = fragment.getBody().trim();  //Store this until we create the item.
                    return(new AnalysisFeedbackItem(AnalysisFeedbackItem.Severity.HIDE, "Found source reference", cellInfo.row));
                }


            }


        }catch(Exception e){

            PukkaLogger.log( e );
            abortCurrentChecklist();
            return(new AnalysisFeedbackItem(AnalysisFeedbackItem.Severity.ABORT, "Internal Error:" + e.getLocalizedMessage(), 0));

        }
        return feedback;
    }


    public AnalysisFeedbackItem storeDefinition(int row){

        try{
            PukkaLogger.log(PukkaLogger.Level.INFO, "Storing a checklist item");
            currentItem.store();

            if(currentSourceText != null)
                sourceMap.add(new SourceMap(currentItem.getKey(), currentSourceText, currentItem.getName()));

            return new AnalysisFeedbackItem(AnalysisFeedbackItem.Severity.INFO, "Created checklist item "+ currentItem.getName()+" with id " + currentItem.getIdentifier(), row);

        }catch(BackOfficeException e){

            abortCurrentChecklist();
            return new AnalysisFeedbackItem(AnalysisFeedbackItem.Severity.ABORT, "FAILED to create checklist item "+ currentItem.getName(), row);

        }
    }

    private void abortCurrentChecklist() {
        currentChecklist = null;
    }


    public boolean hasOpenCheckist() {

        return currentChecklist != null;
    }

}

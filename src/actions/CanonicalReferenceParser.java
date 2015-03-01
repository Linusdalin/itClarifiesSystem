package actions;

import analysis.ParseFeedback;
import analysis.ParseFeedbackItem;
import contractManagement.Contract;
import contractManagement.ContractFragment;
import contractManagement.Project;
import crossReference.Definition;
import document.AbstractFragment;
import document.CellInfo;
import document.FragmentSplitterInterface;
import log.PukkaLogger;
import pukkaBO.condition.LookupByKey;
import pukkaBO.exceptions.BackOfficeException;

import java.util.List;

/********************************************************************************************************'
 *
 *          handle parsing a canonical references definition document either as a separate excel document or
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

public class CanonicalReferenceParser {

    private static final String[] checklistHeadlines = {"Name", "Description", "Source",};

    private FragmentSplitterInterface doc;
    private Definition currentItem;     // The current item that we are building up
    private String currentSourceText;   // The source text for the current item

    private boolean isInCanonicalReferenceList = false;

    // Create a map for the connection from a ChecklistItem to the source fragment.
    // As the source fragments are not yet stored at the point of parsing, we put this in a list and
    // Iterate through it when all the fragments from the document is stored.

    private List<SourceMap> sourceMap = new java.util.ArrayList<SourceMap>();
    private final Contract document;
    private final Project project;


    /**********************************************************************
     *
     *          Create the parser given a complete document and a checklist item
     *
     *
     * @param doc
     */

    public CanonicalReferenceParser(FragmentSplitterInterface doc, Contract document, Project project){

        this.document = document;
        this.project = project;
        this.doc = doc;
    }

    public void startNew(){


        isInCanonicalReferenceList = true;
        currentItem = createNewItem();


    }

    public void endCurrentTable(){

        storeDefinition(0);
        isInCanonicalReferenceList = false;
    }

    /*********************************************************************
     *
     *          map all found source references to the appropriate fragments found in the list
     *
     *
     * @param project       - current project
     */

    public void mapItemSources(Project project){


        if(sourceMap.isEmpty())
            return;

        List<ContractFragment> fragments = project.getContractFragmentsForProject();

        for (SourceMap map : sourceMap) {

            Definition definition = new Definition(new LookupByKey(map.fragmentKey));
            boolean hit = false;

            for (ContractFragment documentFragment : fragments) {

                //Very simple match. It could be more lenient

                System.out.println(" * Cpr: " + map.sourceText.toLowerCase() + " with " + documentFragment.getText().toLowerCase());

                if(documentFragment.getText().toLowerCase().contains(map.sourceText.toLowerCase())){

                    try {

                        definition.setDefinedIn(documentFragment.getKey());
                        definition.setVersion(documentFragment.getVersionId());
                        definition.setProject(documentFragment.getProjectId());
                        definition.update();
                        hit = true;
                        PukkaLogger.log(PukkaLogger.Level.INFO, "Setting source fragment on Canonical Definition " + map.itemName + " to " + documentFragment.getKey().toString() + " in " + documentFragment.getVersionId());
                        break;

                    } catch (BackOfficeException e) {
                        PukkaLogger.log(PukkaLogger.Level.FATAL, "Unable to update source");
                    }
                }

            }

            if(!hit)
                PukkaLogger.log(PukkaLogger.Level.INFO, "Could not find " + map.itemName + "(" + map.sourceText + ") in project " + project.getName());
        }

    }

    // innovation norway's tinc (tech incubator)
    // innovation norway's tinc (tech incubator)

    /******************************************************************************'
     *
     *      parsing a checklist from the entire document
     *
     *
     *      //TODO: Optimization: Add batch store here
     *
     */


    public ParseFeedback parseChecklist() {

        List<AbstractFragment> fragments = doc.getFragments();
        ParseFeedback feedback = new ParseFeedback();

        try{

            for (AbstractFragment fragment : fragments) {

                ParseFeedbackItem cellFeedback = parseCell(fragment);
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


    private Definition createNewItem(){

        currentSourceText = null;

        return new Definition("undefined", null,  document.getKey(), project.getKey());

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


    public ParseFeedbackItem parseCell(AbstractFragment fragment){

        ParseFeedbackItem feedback = null;

        try{

            CellInfo cellInfo = fragment.getCellInfo();

            if(cellInfo == null){

                PukkaLogger.log(PukkaLogger.Level.DEBUG, "Canonical Reference Parser: Ignoring non cell fragment " + fragment.getBody());
            }
            else{

                PukkaLogger.log(PukkaLogger.Level.DEBUG, "Canonical Reference Parser: Handling cell fragment " + fragment.getBody());

                // Handle table data for the checklist

                if(cellInfo.row == 1 ){

                    // The expectation is that the first row is a headline

                    if(cellInfo.col < checklistHeadlines.length && !fragment.getBody().equalsIgnoreCase(checklistHeadlines[cellInfo.col])){

                        abortCurrentChecklist();
                        return(new ParseFeedbackItem(ParseFeedbackItem.Severity.ABORT, "Canonical Reference Parser: Expected to find " + checklistHeadlines[cellInfo.col] + " as title in cell (" + cellInfo.row +", " +  cellInfo.col + "). Found " + fragment.getBody(), cellInfo.row));
                    }

                }


                if(cellInfo.row > 2 && cellInfo.col == 0){

                    // New row, store the old row

                    feedback = storeDefinition(cellInfo.row);

                }

                if(cellInfo.row > 1 && cellInfo.col == 0){

                    // New row, create a new item
                    currentItem = createNewItem();

                    if(fragment.getBody().equals("")){

                        return(new ParseFeedbackItem(ParseFeedbackItem.Severity.INFO, "Canonical Reference Parser: Ended canonical reference table ( no more id... )", cellInfo.row));

                    }

                    currentItem.setName(fragment.getBody());

                }

                if(cellInfo.row > 1 && cellInfo.col == 1){

                    if(fragment.getBody().equals(""))
                        return(new ParseFeedbackItem(ParseFeedbackItem.Severity.INFO, "Ignoring empty definition", cellInfo.row));

                    return(new ParseFeedbackItem(ParseFeedbackItem.Severity.ERROR, "Not implemented definition description. Use canonical reference", cellInfo.row));

                }

                if(cellInfo.row > 1 && cellInfo.col == 2){

                    currentSourceText = fragment.getBody().trim();  //Store this until we create teh item.
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
            PukkaLogger.log(PukkaLogger.Level.INFO, "Canonical Reference Parser: Storing a definition " + currentItem.getName());
            currentItem.store();

            if(currentSourceText != null)
                sourceMap.add(new SourceMap(currentItem.getKey(), currentSourceText, currentItem.getName()));

            return new ParseFeedbackItem(ParseFeedbackItem.Severity.INFO, "Created definition "+ currentItem.getName(), row);

        }catch(BackOfficeException e){

            abortCurrentChecklist();
            return new ParseFeedbackItem(ParseFeedbackItem.Severity.ABORT, "FAILED to create definition "+ currentItem.getName(), row);

        }
    }

    private void abortCurrentChecklist() {
        isInCanonicalReferenceList = false;
    }


    public boolean hasOpenCheckist() {

        return isInCanonicalReferenceList;
    }

}

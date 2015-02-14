package backend;

import actions.ChecklistItem;
import actions.ChecklistItemTable;
import contractManagement.*;
import dataRepresentation.DataObjectInterface;
import dataRepresentation.DataTableInterface;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.backOffice.BackOfficeLocation;
import pukkaBO.backOffice.Icon;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.formsPredefined.TableEditForm;
import pukkaBO.list.*;
import pukkaBO.renderer.GroupListRenderer;
import pukkaBO.renderer.ListRendererInterface;
import pukkaBO.style.Html;
import services.ItClarifiesService;
import userManagement.AccessGrant;
import userManagement.AccessGrantTable;
import userManagement.Visibility;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


/*************************************************************************'
 *
 *          Document list
 *
 *          Viewing all documents or a specific document.
 *
 *          The documents are grouped by Project
 *
 */


public class ChecklistList extends GroupByList implements ListInterface{

    // General definitions for the example list

    public static final String Name = "ChecklistList";
    public static final String Title = "Checklists";
    public static final String Description = "Checklist items grouped to checklists";
    public static final int GroupColumn = 6; // Group by Project

    // ids for the callback actions

    public static final int Callback_Action_Edit        = 1;
    public static final int Callback_Action_Delete      = 2;


    public static final ListRendererInterface Renderer = new GroupListRenderer();

    private static final DataTableInterface table = new ChecklistItemTable();


    public ChecklistList(BackOfficeInterface backOffice){

        List<ListColumnInterface> columnStructure = new ArrayList<ListColumnInterface>() {{

            add(new ListTableColumn( 1, table ).withNameFromTableColumn());
            add(new ListTableColumn( 3, table ).withNameFromTableColumn());
            add(new ListTableColumn( 4, table ).withNameFromTableColumn());
            add(new ListTableColumn( 5, table ).withNameFromTableColumn());
            add(new ListTableColumn( 7, table ).withNameFromTableColumn());
            add(new ListTableColumn( 8, table ).withNameFromTableColumn());
            add(new ListTableColumn( 9, table ).withNameFromTableColumn().withConstantMap());
            add(new ListTableColumn( 10, table ).withNameFromTableColumn());
            add(new ListTableColumn( 11, table ).withNameFromTableColumn());
            add(new ListTableColumn( 12, table ).withNameFromTableColumn());

        }};




        init(Name, Title, Description, backOffice, Renderer);

        // Set the table to draw the data from.

        initiateTable( table, columnStructure);

        setGroupColumn(GroupColumn);
        setSorting(new Sorting(ChecklistItemTable.Columns.Identifier.name(), Ordering.FIRST));   //Ordered by the ordinal tag


        // Add actions to each element. In this example we have multiple actions.
        // They may however not all be present in all states.

        actions.add(new ListAction(Callback_Action_Edit,        ActionType.Item, "Edit").setIcon(Icon.Pencil));
        actions.add(new ListAction(Callback_Action_Delete,      ActionType.List, "Delete").setIcon(Icon.Trash));

        // Set the number of elements to display
        displaySize = 20;                                 //TODO: Size not implemented in the Starlight table
    }



    /*************************************************************************'
     *
     *
     *          getHighlight - this is the logic for the list defining how the
     *          item (row) shall be displayed.
     *
     *          The logic is normally defined by the content in the table.
     *
     * @param object - The object on which the selection is done
     * @return - the enum defining the class
     *
     */

    @Override
    public DisplayHighlight getHighlight(DataObjectInterface object){


        return DisplayHighlight.FYI;
    }



    /********************************************************************
     *
     *          Callback implementation.
     *
     *
     *
     *
     * @param action - the callback action as defined by the actions
     * @param key - the object on the selected row
     *
     * @return - text to be displayed. The interpretation will be different depending on the action type.
     */

    @Override
    public String callBack(int action, DBKeyInterface key, String section, HttpServletRequest request){

        ChecklistItem item = new ChecklistItem(new LookupByKey(key));

        try{

            switch(action){



                case Callback_Action_Delete:

                    return "Warning: Delete not implemented";

                case Callback_Action_Edit:

                    // This is a view type, so the return value will be the content of an html page.
                    // Returning null here would be an error

                    return getModifyItemView(item, section);

            }

        }catch(Exception e){

            // Handle exception here
        }

        return "No action performed...";

    }


    private String getModifyItemView(ChecklistItem item, String section) {

        try {

            StringBuffer docView = new StringBuffer();

            docView.append(Html.heading(2, item.getName().toLowerCase()));
            docView.append(Html.paragraph("id: " + item.getKey().toString()));
            docView.append(Html.paragraph("Not implemented modify checklist item"));

            return docView.toString();

        } catch (Exception e) {

            PukkaLogger.log(e);
            return "No document view could be generated";
        }

    }



    @Override
    public boolean hasAction(int action, DataObjectInterface object){


        return true;

    }


    @Override
    public String addForm(String section)throws BackOfficeException{

        DataTableInterface table = new ContractTable();
        BackOfficeLocation location = new BackOfficeLocation(backOffice, section, "");
        String html = new TableEditForm(table, null, TableEditForm.FormType.ADD, location, "&list=" + Name).renderForm();

        return html;
    }

    @Override
    public String submit(HttpServletRequest request){

        return("Warning: Add not implemented");

    }






}

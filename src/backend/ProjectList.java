package backend;

import contractManagement.DocumentDeleteOutcome;
import contractManagement.Project;
import contractManagement.ProjectTable;
import dataRepresentation.DataObjectInterface;
import dataRepresentation.DataTableInterface;
import dataRepresentation.DisplayFormat;
import databaseLayer.DBKeyInterface;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.backOffice.BackOfficeLocation;
import pukkaBO.backOffice.Icon;
import pukkaBO.condition.LookupByKey;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.formsPredefined.TableEditForm;
import pukkaBO.list.*;
import pukkaBO.renderer.GroupListRenderer;
import pukkaBO.renderer.ListRendererInterface;
import userManagement.OrganizationTable;
import userManagement.PortalUser;
import userManagement.PortalUserTable;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


/**
 *
 *      An example list. It shows objects of the type Example1
 *      and allows for actions on them.
 *
 *      There are four actions for a list item:
 *
 *       - Execute() - Typically used to perform a main action on the item
 *       - MarkAsRead() - Typically used to confirm the list item
 *       - MarkAsUnread() - Typically reversing the MarkAsRead() action
 *       - Undo() - Typically reversing the Execute()
 *       - View() - opening a separate view for this specific item
 *
 *
 *       The actions operate on the data in the associated table. In this
 *       example, it uses a field called state.
 *
 */


public class ProjectList extends GroupByList implements ListInterface{

    // General definitions for the example list

    public static final String Name = "ProjectList";
    public static final String Title = "All Projects";
    public static final String Description = "All projects in the system grouped by organization.";
    public static final int GroupColumn = 4; // Group by Organization

    // ids for the callback actions

    public static final int Callback_Action_Do      = 1;
    public static final int Callback_Action_Delete  = 2;
    public static final int Callback_Action_View    = 3;
    public static final int Callback_Action_Add     = 4;

    public static final ListRendererInterface Renderer = new GroupListRenderer();

    private static final DataTableInterface table = new ProjectTable();


    public ProjectList(BackOfficeInterface backOffice){

        List<ListColumnInterface> columnStructure = new ArrayList<ListColumnInterface>() {{

            add(new ListTableColumn( 1, table ).withNameFromTableColumn().withFormat(new DisplayFormat(DisplayFormat.SMALL)));
            add(new ListTableColumn( 2, table ).withNameFromTableColumn());
            add(new ListTableColumn( 3, table ).withNameFromTableColumn().withFormat(new DisplayFormat(DisplayFormat.WIDE)));
            add(new ListTableColumn( 4, table ).withNameFromTableColumn());
            add(new ListTableColumn( 5, table ).withNameFromTableColumn());

        }};




        init(Name, Title, Description, backOffice, Renderer);

        // Set the table to draw the data from.

        initiateTable( table, columnStructure);

        setGroupColumn(GroupColumn);


        // Add actions to each element. In this example we have multiple actions.
        // They may however not all be present in all states.

        //actions.add(new ListAction(Callback_Action_Do,        ActionType.List, "Do").setIcon(Icon.Check));
        actions.add(new ListAction(Callback_Action_Delete,    ActionType.List, "Delete").setIcon(Icon.Trash));
        actions.add(new ListAction(Callback_Action_View,      ActionType.Item, "View").setIcon(Icon.Search));


        setAddAction(new ListAction(Callback_Action_Add, ActionType.Add, "new project"));

        // Set the number of elements to display
        setDisplaySize(20);
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

        Project project = new Project(new LookupByKey(key));

        try{

            switch(action){


                case Callback_Action_Do:


                    return "Error: Not implemented DO";

                case Callback_Action_Delete:

                    DocumentDeleteOutcome outcome = project.recursivelyDelete();

                    return "Success:Deleted project " + project.getName() + " with<br>\n" +
                            " - " + outcome.documents + " documents<br>\n";

                case Callback_Action_View:

                    // This is a view type, so the return value will be the content of an html page.
                    // Returning null here would be an error

                    return "This is a detailed view on a separate page with  " + project.getName().toLowerCase() + "id: " + project.getKey().toString();
            }

        }catch(BackOfficeException e){

            // Handle exception here
        }

        return "Error:No action performed...";

    }


    @Override
    public boolean hasAction(int action, DataObjectInterface object){


        return true;

    }


    @Override
    public String addForm(String section)throws BackOfficeException{

        DataTableInterface table = new ProjectTable();
        BackOfficeLocation location = new BackOfficeLocation(backOffice, section, "");
        String html = new TableEditForm(table, null, TableEditForm.FormType.ADD, location, "&list=" + Name).renderForm();

        return html;
    }

    @Override
    public String submit(HttpServletRequest request){

        try{

            DataObjectInterface object = table.getDataObject(request);
            object.store();
            return ("Success:A new Project with id " + object.getKey() + " was created");

        }
        catch(BackOfficeException e){

            System.out.println("Error: Error adding element " + e.narration);
            e.printStackTrace();
        }

        return "Error: Could not create Item...";
    }







}

package backend;

import contractManagement.FragmentClassification;
import contractManagement.FragmentClassificationTable;
import crossReference.Definition;
import crossReference.DefinitionTable;
import dataRepresentation.DataObjectInterface;
import dataRepresentation.DataTableInterface;
import dataRepresentation.DisplayFormat;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.backOffice.Icon;
import pukkaBO.condition.LookupByKey;
import pukkaBO.condition.Ordering;
import pukkaBO.condition.Sorting;
import pukkaBO.list.*;
import pukkaBO.renderer.GroupListRenderer;
import pukkaBO.renderer.ListRendererInterface;
import pukkaBO.style.Html;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


/*************************************************************************'
 *
 *          Classification list
 *
 *          Viewing all definitions per project
 *
 *
 */


public class ClassificationList extends GroupByList implements ListInterface{


    public static final String Name = "ClassificationList";
    public static final String Title = "All Classifications";
    public static final String Description = "All classifications in the system grouped by project.";
    public static final int GroupColumn = 8; // Group by Project

    // ids for the callback actions

    public static final int Callback_Action_Delete      = 1;

    public static final ListRendererInterface Renderer = new GroupListRenderer();

    private static final DataTableInterface table = new FragmentClassificationTable();


    public ClassificationList(BackOfficeInterface backOffice){

        List<ListColumnInterface> columnStructure = new ArrayList<ListColumnInterface>() {{

            add(new ListTableColumn( 1, table ).withNameFromTableColumn().withFormat(new DisplayFormat(DisplayFormat.SMALL)));
            add(new ListTableColumn( 2, table ).withNameFromTableColumn().withFormat(new DisplayFormat(DisplayFormat.WIDE)));
            add(new ListTableColumn( 5, table ).withNameFromTableColumn());
            add(new ListTableColumn( 6, table ).withNameFromTableColumn());
            add(new ListTableColumn( 7, table ).withNameFromTableColumn());
            add(new ListTableColumn( 9, table ).withNameFromTableColumn());

        }};




        init(Name, Title, Description, backOffice, Renderer);

        // Set the table to draw the data from.

        initiateTable( table, columnStructure);

        setGroupColumn(GroupColumn);
        setSorting(new Sorting(FragmentClassificationTable.Columns.Version.name(), Ordering.FIRST));   //Ordered by the ordinal tag


        // Add actions to each element. In this example we have multiple actions.
        // They may however not all be present in all states.

        //actions.add(new ListAction(Callback_Action_Do,        ActionType.List, "Do").setIcon(Icon.Check));
        actions.add(new ListAction(Callback_Action_Delete,     ActionType.List, "Delete").setIcon(Icon.Trash));

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

        FragmentClassification classification = new FragmentClassification(new LookupByKey(key));

        try{

            switch(action){

                case Callback_Action_Delete:

                    //TODO: Delete both definitions and definition source & usage classifications

                    return ("Warning: Delete not implemented");

                default:

                    return ("Warning: No callback for action " + action + " implemented");

            }

        }catch(Exception e){

            PukkaLogger.log( e );
            return Html.errorBox("Internal error in list action");
        }


    }


}

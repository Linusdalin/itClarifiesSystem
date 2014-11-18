package backend;

import contractManagement.*;
import dataRepresentation.DataObjectInterface;
import dataRepresentation.DataTableInterface;
import dataRepresentation.DisplayFormat;
import databaseLayer.DBKeyInterface;
import language.Swedish;
import log.PukkaLogger;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.backOffice.Icon;
import pukkaBO.condition.LookupByKey;
import pukkaBO.condition.Ordering;
import pukkaBO.condition.Sorting;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.list.*;
import pukkaBO.renderer.GroupListRenderer;
import pukkaBO.renderer.ListRendererInterface;
import pukkaBO.style.Html;
import userManagement.Organization;
import userManagement.OrganizationTable;
import userManagement.PortalUser;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


/**
 *
 *          Reclassification list
 *
 *
 *
 */


public class ReclassificationList extends GroupByList implements ListInterface{

    // General definitions for the example list

    public static final String Name = "ReclassificationList";
    public static final String Title = "Reclassifications";
    public static final String Description = "All reclassifications grouped by class";
    public static final int GroupColumn = 8; // Group by Classification

    // ids for the callback actions

    public static final int Callback_Action_Ignore      = 1;
    public static final int Callback_Action_Reopen      = 2;
    public static final int Callback_Action_View        = 3;

    public static final ListRendererInterface Renderer = new GroupListRenderer();

    private static final DataTableInterface table = new ReclassificationTable();


    public ReclassificationList(BackOfficeInterface backOffice){

        List<ListColumnInterface> columnStructure = new ArrayList<ListColumnInterface>() {{

            add(new ListTableColumn( 1, table ).withNameFromTableColumn( ).withFormat(new DisplayFormat(DisplayFormat.SMALL)));
            add(new ListTableColumn( 2, table ).withNameFromTableColumn( ));
            add(new ListTableColumn( 3, table ).withNameFromTableColumn( ));
            add(new ListTableColumn( 4, table ).withNameFromTableColumn( ).withFormat(new DisplayFormat(DisplayFormat.EXTRA_WIDE)));
            add(new ListTableColumn( 6, table ).withNameFromTableColumn( ));
            add(new ListTableColumn( 7, table ).withNameFromTableColumn( ));
            add(new ListTableColumn( 8, table ).withNameFromTableColumn( ));
            add(new ListTableColumn( 9, table ).withNameFromTableColumn( ));

        }};




        init(Name, Title, Description, backOffice, Renderer);

        // Set the table to draw the data from.

        initiateTable( table, columnStructure);

        setGroupColumn(GroupColumn);
        //setSorting(new Sorting(ContractTable.Columns.Ordinal.name(), Ordering.FIRST));   //Ordered by the ordinal tag


        // Add actions to each element. In this example we have multiple actions.
        // They may however not all be present in all states.

        actions.add(new ListAction(Callback_Action_Ignore,     ActionType.List, "Ignore").setIcon(Icon.Trash));
        actions.add(new ListAction(Callback_Action_View,       ActionType.Item, "View").setIcon(Icon.Search));

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


        Reclassification reclassification = (Reclassification)object;

        if(reclassification.getClosed())
            return DisplayHighlight.FYI;
        else
            return DisplayHighlight.RequireAction;
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

        Reclassification reclassification = new Reclassification(new LookupByKey(key));

        try{

            switch(action){

                case Callback_Action_View:

                    // This is a view type, so the return value will be the content of an html page.
                    // Returning null here would be an error

                    return getItemView(reclassification, section);


                case Callback_Action_Ignore:

                    reclassification.setClosed(true);
                    reclassification.update();

                    return "Closed item " + reclassification.getName();

                case Callback_Action_Reopen:

                    reclassification.setClosed(false);
                    reclassification.update();

                    return "Reopened " + reclassification.getName();

            }

        }catch(BackOfficeException e){

            // Handle exception here
        }

        return "No action performed...";

    }


    private String getItemView(Reclassification reclassification, String section) {

        try {

            StringBuilder docView = new StringBuilder();
            PortalUser submitter = reclassification.getUser();
            Organization organization = submitter.getOrganization();

            docView.append(Html.heading(2, reclassification.getName().toLowerCase()));
            docView.append(Html.paragraph("submitted by: " + reclassification.getUser().getName() + "( " + organization.getName() + ")" ));

            if(reclassification.getClosed())
                docView.append(Html.paragraph(Html.link("?id="+reclassification.getKey().toString()+"&list=" + name + "&action=Item&callbackAction=" + Callback_Action_Reopen + "&section=" + section, "Reopen")));
            else
                docView.append(Html.paragraph(Html.link("?id="+reclassification.getKey().toString()+"&list=" + name + "&action=Item&callbackAction=" + Callback_Action_Ignore + "&section=" + section, "Mark as closed")));

            docView.append(Html.paragraph(getTestcaseForExample(reclassification)));

            return docView.toString();

        } catch (Exception e) {

            e.printStackTrace(System.out);
            PukkaLogger.log(PukkaLogger.Level.FATAL, "Could not generate view of reclassification " + reclassification.getName());
            return "No document view could be generated";
        }

    }

    private String getTestcaseForExample(Reclassification reclassification) {

        StringBuilder html = new StringBuilder();
        String languageCode = reclassification.getDocument().getLanguage();
        String parser = "EnglishParser";
        if(languageCode.equals("SE"))
            parser = "SwedishParser";



        String classificationType = reclassification.getClassification().getType();

        html.append("<pre style=\"font:Courier;\">");

        html.append(Html.heading(3, "Code example for testing this:\n\n"));


        html.append("    @Test\n");
        html.append("    public void test"+ classificationType +"Extractor"+ languageCode+"(){\n");
        html.append("\n");
        html.append("        AnalysisFragment f;\n");
        html.append("        AnalysisOutcome o;\n");
        html.append("        FeatureDefinition d;\n");
        html.append("\n");
        html.append("        f = new AnalysisFragment(\""+ reclassification.getFragment()+"\"\n");
        html.append(        "                , \""+ reclassification.getHeadline()+"\", "+ parser+");\n");
        html.append(        "        o = new " + classificationType+ "Extractor"+ languageCode+"().classify( f );\n");
        html.append(        "\n");
        html.append("        assertThat(\"Expecting one featureDefinition\", o.getDefinitions().size(), is(1));\n");
        html.append("\n");
        html.append("        d= o.getDefinitions().get( 0 ); assertTrue(d.isMatch());\n");
        html.append("        assertThat(d.getType(), is(FeatureType."+ reclassification.getClassification().getType()+"));\n");
        html.append("        assertThat(d.getTag(), is(\""+ reclassification.getTag()+"\"));\n\n");

        html.append("</pre>");
        return html.toString();
    }


    @Override
    public boolean hasAction(int action, DataObjectInterface object){

        Reclassification reclassification = (Reclassification)object;

        if(action == Callback_Action_Ignore && reclassification.getClosed())
            return false;

        return true;

    }


    @Override
    public String addForm(String section)throws BackOfficeException{


        return "no add form";
    }

    @Override
    public String submit(HttpServletRequest request){


        return "Error: No submit action defined...";
    }






}

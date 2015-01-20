package backend;

import classifiers.Classification;
import contractManagement.*;
import dataRepresentation.DataObjectInterface;
import dataRepresentation.DataTableInterface;
import dataRepresentation.DisplayFormat;
import databaseLayer.DBKeyInterface;
import language.Swedish;
import log.PukkaLogger;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.backOffice.Icon;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.list.*;
import pukkaBO.renderer.GroupListRenderer;
import pukkaBO.renderer.ListRendererInterface;
import pukkaBO.style.Html;
import userManagement.Organization;
import userManagement.OrganizationTable;
import userManagement.PortalUser;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
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
    public static final int GroupColumn = 13;   // Group by Document

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
        Contract document = reclassification.getDocument();
        Project project = document.getProject();
        String languageCode = document.getLanguage();

        // Select the appropriate parser. Add more parsers when implemented

        String parser = "englishParser";
        if(languageCode.equals("SE"))
            parser = "swedishParser";



        String classificationType = reclassification.getClassTag().replaceAll("#", "");
        classificationType = classificationType.substring(0, 1) + classificationType.substring(1).toLowerCase();

        html.append("<pre style=\"font:Courier;\">");
        String classificationDate;

        try {
            classificationDate = reclassification.getDate().getISODate();
        } catch (BackOfficeException e) {
            classificationDate = "unknown";
        }

        String fileName = reclassification.getDocument().getFile();
        String body = reclassification.getFragment().replaceAll("\n", "&#92;n").replaceAll("\"", "&#92;\"");
        String headline = reclassification.getHeadline().replaceAll("\n", "&#92;n").replaceAll("\"", "&#92;\"");

        //versionName = new String(version.getVersion().getBytes(), "ISO-8859-1");
        try {

            fileName = new String(fileName.getBytes(), "ISO-8859-1");

        } catch (UnsupportedEncodingException e) {

            PukkaLogger.log( e );
            //Swallow
        }



        html.append(Html.heading(3, "Code example for testing this:\n\n"));

        html.append("    /***********************************************************\n");
        html.append("     *\n");
        html.append("     *      Testing Classification by "+ reclassification.getUser().getName()+" @"+ classificationDate+"\n");
        html.append("     *      Document:   \""+ document.getName()+"\"\n");
        html.append("     *      FragmentNo: "+ reclassification.getFragmentNo()+"\n");
        html.append("     *      Body: \""+ body +"\"\n");
        html.append("     *\n");
        html.append("     */\n");


        html.append("    @Test\n");
        html.append("    public void test"+ classificationType +"Classifier"+ languageCode+"(){\n");
        html.append("        try {\n" +
                "\n" +
                "               new ClassificationTester(\""+body +"\")\n" +
                "                        .withParser("+ parser +")\n" +
                "                        .withHeadline(\""+ headline +"\")\n" +
                "                        .withClassifier(new "+classificationType+ "Classifier"+ languageCode+"())\n");
        if(reclassification.getisPositive()){

            html.append("                        .expectingClassification(new ClassificationAssertion(FeatureTypeTree."+ classificationType+", 1)\n");
            html.append("                            .withTag(\""+ reclassification.getComment()+"\")\n");
        }
        else{

            html.append("                        .expectingClassification(new ClassificationAssertion(FeatureTypeTree."+ classificationType+", 0)\n");

        }
        html.append("                      )\n" +
                "                    .test();\n");
        html.append(
                "           } catch (Exception e) {\n" +
                "                e.printStackTrace();\n" +
                "                assertTrue(false);\n" +
                "           }\n" +
                "        }\n\n");

        html.append("    /***********************************************************\n");
        html.append("     *\n");
        html.append("     *      Regeneration of risk, comment and annotation\n");
        html.append("     *         Document: "+ document.getName() +"\n");
        html.append("     *         Project:  "+ project.getName() +"\n");
        html.append("     *\n");
        html.append("     */\n");

        html.append("    private static final DemoComment[] documentCommentList = {\n\n");


            ReclassificationTable reclassificationForDocument = new ReclassificationTable(new LookupList().addFilter(new ReferenceFilter(ReclassificationTable.Columns.Document.name(), document.getKey())));

            for (DataObjectInterface object : reclassificationForDocument.getValues()) {

                Reclassification reclassificationInDocument = (Reclassification)object;
                String theBody = reclassification.getFragment().replaceAll("\n", "&#92;n").replaceAll("\"", "&#92;\"");

                html.append(        "            new DemoComment(\""+reclassificationInDocument.getClassTag()+"\", 0, 0, \""+fileName+"\", "+ reclassificationInDocument.getFragmentNo()+",\n" +
                                    "                            \""+ theBody + "\",\n" +
                                    "                            \"" + reclassificationInDocument.getRiskLevel().getName()+"\", \""+ reclassificationInDocument.getPattern()+"\", \""+
                                                                 reclassificationInDocument.getComment()+"\", \""+
                                                                 reclassificationInDocument.getUser().getName()+"\"),\n\n");

            }



        html.append("    };\n");
        html.append("\n");

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

package backend;

import reclassification.Reclassification;
import reclassification.ReclassificationTable;
import com.google.appengine.api.datastore.Query;
import contractManagement.*;
import dataRepresentation.DBTimeStamp;
import dataRepresentation.DataObjectInterface;
import dataRepresentation.DataTableInterface;
import dataRepresentation.DisplayFormat;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import org.apache.commons.lang.StringUtils;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.backOffice.Icon;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.form.DateField;
import pukkaBO.list.*;
import pukkaBO.renderer.GroupListRenderer;
import pukkaBO.renderer.ListRendererInterface;
import pukkaBO.style.Html;
import userManagement.Organization;
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
            add(new ListTableColumn( 2, table ).withName("Set"));
            add(new ListTableColumn( 3, table ).withNameFromTableColumn( ).withConstantMap());
            add(new ListTableColumn( 4, table ).withNameFromTableColumn( ).withFormat(new DisplayFormat(DisplayFormat.EXTRA_WIDE)));
            add(new ListTableColumn( 6, table ).withName("Frg"));
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

        // Add a filter for selecting a date

        setFilter(new SelectDateFilter());

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

                    return "Closed classification " + reclassification.getClassification();

                case Callback_Action_Reopen:

                    System.out.println("*** Closing");
                    reclassification.setClosed(false);
                    reclassification.update();
                    System.out.println("*** Closed");

                    return "Reopened " + reclassification.getClassification();

            }

        }catch(BackOfficeException e){

            // Handle exception here
        }

        return "No action performed...";

    }


    private String getItemView(Reclassification reclassification, String section) {

        try {

            StringBuilder docView = new StringBuilder();
            String submitterName = reclassification.getUser();
            String documentName = reclassification.getDocument();

            docView.append(Html.heading(2, reclassification.getClassification().toLowerCase()));
            docView.append(Html.paragraph("submitted by: " + reclassification.getUser() ));

            if(reclassification.getClosed())
                docView.append(Html.paragraph(Html.link("?id="+reclassification.getKey().toString()+"&list=" + name + "&action=Item&callbackAction=" + Callback_Action_Reopen + "&section=" + section, "Reopen")));
            else
                docView.append(Html.paragraph(Html.link("?id="+reclassification.getKey().toString()+"&list=" + name + "&action=Item&callbackAction=" + Callback_Action_Ignore + "&section=" + section, "Mark as closed")));

            // Generate the code. test cases for the document/classification combination and then all the open items for the manual regeneration transposing

            docView.append("<pre style=\"font:Courier;\">");

            docView.append(getTestFile(documentName, reclassification.getClassification()));
            //docView.append(getAllTrasposures(document, document.getProject()));

            docView.append("</pre>");

            return docView.toString();

        } catch (Exception e) {

            e.printStackTrace(System.out);
            PukkaLogger.log(PukkaLogger.Level.FATAL, "Could not generate view of reclassification " + reclassification.getClassification());
            return "No document view could be generated";
        }

    }

    private String getTestFile(String documentName, String tag) {

        StringBuilder html = new StringBuilder();

        html.append(Html.heading(3, "Code example for testing this:\n\n"));

        Contract document = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.File.name(), documentName)));


        html.append("    /***********************************************************\n");
        html.append("     *\n");
        html.append("     *      Testing Classification by examples for tag "+ tag+"\n");
        html.append("     *      Document:   \""+ document.getName()+"\"\n");
        html.append("     *      Language:   \""+ document.getLanguage()+"\"\n");
        html.append("     *\n");
        html.append("     */\n\n\n");
        html.append("    @Test\n");
        html.append("    public void test"+ document.getName() +"Examples(){\n");
        html.append("        try {\n");

        ReclassificationTable reclassificationForDocument = new ReclassificationTable(new LookupList()
                .addFilter(new ReferenceFilter(ReclassificationTable.Columns.Document.name(), document.getKey()))
                .addFilter(new ColumnFilter(ReclassificationTable.Columns.Classification.name(), tag)));

        for (DataObjectInterface object : reclassificationForDocument.getValues()) {

            Reclassification reclassification = (Reclassification)object;
            html.append(getTestcaseForExample(reclassification, document));
        }


        html.append(
                "           } catch (Exception e) {\n" +
                "                e.printStackTrace(System.out);\n" +
                "                assertTrue(false);\n" +
                "           }\n" +
                "        }\n\n");


        return html.toString();
    }



    private String getTestcaseForExample(Reclassification reclassification, Contract document) {

        StringBuilder html = new StringBuilder();
        Project project = document.getProject();
        String languageCode = document.getLanguage();

        // Select the appropriate parser. Add more parsers when implemented

        String parser = "englishParser";
        if(languageCode.equals("SV"))
            parser = "swedishParser";



        String classificationType = reclassification.getClassification().replaceAll("#", "");
        classificationType = classificationType.substring(0, 1) + classificationType.substring(1).toLowerCase();


        String fileName = reclassification.getDocument();
        String body = reclassification.getFragment().replaceAll("\n", "&#92;n").replaceAll("\"", "&#92;\"");
        //String headline = reclassification.getHeadline().replaceAll("\n", "&#92;n").replaceAll("\"", "&#92;\"");

        //versionName = new String(version.getVersion().getBytes(), "ISO-8859-1");
        try {

            fileName = new String(fileName.getBytes(), "ISO-8859-1");

        } catch (UnsupportedEncodingException e) {

            PukkaLogger.log( e );
            //Swallow
        }


        html.append(
                "               new ClassificationTester("+ asSplitString(body, 35, 70 )+")\n" +
                "                        .withParser("+ parser +")\n" +
                "                        .withHeadline(\" add headline...\")\n" +
                "                        .withProject(mockProject, mockDocument)\n" +
                "                        .withClassifier(new NumberClassifier"+ languageCode+"())\n"+
                "                        .withClassifier(new DefinitionUsageClassifier"+ languageCode+"())\n"+
                "                        .withClassifier(new "+classificationType+ "Classifier"+ languageCode+"())\n");
        if(reclassification.getAdd()){

            html.append("                        .expectingClassification(new ClassificationAssertion(FeatureTypeTree."+ classificationType+", 1)\n");
            html.append("                            .withPattern(\"...\")\n");
            html.append("                            .withTag(\"...\")\n");
        }
        else{

            html.append("                        .expectingClassification(new ClassificationAssertion(FeatureTypeTree."+ classificationType+", 0)\n");

        }
        html.append("                      )\n" +
                "                    .test();\n\n\n");


        return html.toString();

    }


    /*******************************************************************************
     *
     *          Splitting a string into code text.
     *
     *          Example:
     *
     *              asSplitString("This is an example string that is too long", 5, 20)  ->
     *
     *                   "This is an example "+
     *                   "string that is too "+
     *                   "long"
     *
     *
     *
     * @param body      - the text
     * @param padding   - indentation for each line
     * @param width     - the line width
     * @return          - string with concatenated text strings
     */

    public static String asSplitString(String body, int padding, int width) {

        StringBuffer completeString = new StringBuffer();
        completeString.append("\"");
        int index = 0;
        int bodyLength = body.length();

        while(bodyLength - index > 0){

            if(index + width > bodyLength){

                completeString.append(body.substring(index));
                index += body.length();

            }
            else{

                completeString.append(body.substring(index, index + width));
                completeString.append("\"+\n"+ StringUtils.leftPad(" ", padding) +"\"");
                index+= width;
            }

        }
        completeString.append("\"");
        return completeString.toString();

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

    /************************************************************
     *
     *      Select a start date. Only show items after this date
     *
     *      //TODO: Automatic index:
     *
     *      <datastore-index kind="Reclassification" ancestor="false" source="manual">
             <property name="Document" direction="asc"/>
             <property name="Date" direction="asc"/>
         </datastore-index>
     *
     */

    private class SelectDateFilter extends ListFilter implements ListFilterInterface {


        private static final String FROM_DATE_PARAMETER = "From";

        SelectDateFilter(){

            formFields.add(new DateField("From", FROM_DATE_PARAMETER));
        }

        public ConditionInterface getFilterCondition(HttpServletRequest request){

            System.out.println("Got "+ request.getParameter(FROM_DATE_PARAMETER)+" in parameter " + FROM_DATE_PARAMETER + " in request. But not implemented filter");

            String date = request.getParameter(FROM_DATE_PARAMETER);
            ConditionInterface filterCondition = new LookupList();

            try{

                if(date != null){
                    PukkaLogger.log(PukkaLogger.Level.INFO, "Got "+ request.getParameter(FROM_DATE_PARAMETER)+" in parameter " + FROM_DATE_PARAMETER + " in request. Adding filter");
                    DBTimeStamp fromTime = new DBTimeStamp(DBTimeStamp.ISO_DATE, date);

                    filterCondition.addFilter(new ColumnFilter(ReclassificationTable.Columns.Date.name(), Query.FilterOperator.GREATER_THAN_OR_EQUAL, fromTime.getISODate()));

                }
            }catch(BackOfficeException e){

                PukkaLogger.log( e );

            }



            return filterCondition;
        }


    }
}

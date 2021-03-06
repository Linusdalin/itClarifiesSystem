package backend;

import analysis.AnalysisServlet;
import contractManagement.*;
import dataRepresentation.DataObjectInterface;
import dataRepresentation.DataTableInterface;
import dataRepresentation.DisplayFormat;
import databaseLayer.DBKeyInterface;
import databaseLayer.DatabaseAbstractionFactory;
import log.PukkaLogger;
import project.Project;
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
import userManagement.SessionManagement;
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


public class DocumentList extends GroupByList implements ListInterface{

    // General definitions for the example list

    public static final String Name = "DocumentList";
    public static final String Title = "All Documents";
    public static final String Description = "All documents in the system grouped by project.";
    public static final int GroupColumn = 9; // Group by Project

    // ids for the callback actions

    public static final int Callback_Action_Do          = 1;
    public static final int Callback_Action_Delete      = 2;
    public static final int Callback_Action_View        = 3;
    public static final int Callback_Action_Add         = 4;
    public static final int Callback_Action_ClearCache  = 5;
    public static final int Callback_Action_Analyse     = 6;
    public static final int Callback_Action_MoveToTop   = 7;
    public static final int Callback_Action_ChangeStyle = 8;

    public static final int Callback_Action_ViewDoc = 9;

    public static final int Callback_Action_SetAccessRO = 10;
    public static final int Callback_Action_SetAccessRC = 11;
    public static final int Callback_Action_SetAccessRWD = 12;


    public static final ListRendererInterface Renderer = new GroupListRenderer();

    private static final DataTableInterface table = new ContractTable();


    public DocumentList(BackOfficeInterface backOffice){

        List<ListColumnInterface> columnStructure = new ArrayList<ListColumnInterface>() {{

            add(new ListTableColumn( 1, table ).withNameFromTableColumn().withFormat(new DisplayFormat(DisplayFormat.SMALL)));
            add(new ListTableColumn( 4, table ).withNameFromTableColumn().withFormat(new DisplayFormat(DisplayFormat.WIDE)));
            add(new ListTableColumn( 5, table ).withNameFromTableColumn());
            add(new ListTableColumn( 6, table ).withNameFromTableColumn());
            add(new ListTableColumn( 7, table ).withNameFromTableColumn().withFormat(new DisplayFormat(DisplayFormat.WIDE)));
            add(new ListTableColumn( 8, table ).withNameFromTableColumn().withFormat(new DisplayFormat(DisplayFormat.WIDE)));
            add(new ListTableColumn( 9, table ).withNameFromTableColumn().withConstantMap());
            add(new ListTableColumn( 10, table ).withNameFromTableColumn());
            add(new ListTableColumn( 11, table ).withNameFromTableColumn());

        }};



        init(Name, Title, Description, backOffice, Renderer);

        // Set the table to draw the data from.

        initiateTable( table, columnStructure);

        setGroupColumn(GroupColumn);
        setSorting(new Sorting(ContractTable.Columns.Ordinal.name(), Ordering.FIRST));   //Ordered by the ordinal tag


        // Add actions to each element. In this example we have multiple actions.
        // They may however not all be present in all states.

        //actions.add(new ListAction(Callback_Action_Do,        ActionType.List, "Do").setIcon(Icon.Check));
        actions.add(new ListAction(Callback_Action_MoveToTop,  ActionType.List, "Top").setIcon(Icon.Sign));
        actions.add(new ListAction(Callback_Action_Delete,     ActionType.List, "Delete").setIcon(Icon.Trash));
        actions.add(new ListAction(Callback_Action_View,       ActionType.Item, "View").setIcon(Icon.Search));

        // Set the number of elements to display
        displaySize = 20;
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

        Contract document = new Contract(new LookupByKey(key));

        try{

            switch(action){

                case Callback_Action_Add:

                    document.store();

                    // This is a add type so the return value will be a confirmation box

                    return "New element added";

                case Callback_Action_Do:


                    return "Not implemented DO";

                case Callback_Action_Delete:

                    DocumentDeleteOutcome outcome = document.recursivelyDeleteDocument(true);

                    return "Deleted document " + document.getName() + " with<br>\n" +
                            " - " + outcome.versions + " versions" +
                            " - " + outcome.clauses + " clauses" +
                            " - " + outcome.fragments + " fragments" +
                            " - " + outcome.annotations + " annotations" +
                            " - " + outcome.riskFlags + " risks" +
                            " - " + outcome.references + " references" +
                            " - " + outcome.keywords + " keywords " +
                            " - " + outcome.indices + " indices and" +
                            " - " + outcome.classifications + " classifications<br>\n";

                case Callback_Action_View:

                    // This is a view type, so the return value will be the content of an html page.
                    // Returning null here would be an error

                    return getDocumentView(document, section);

                case Callback_Action_ClearCache:

                    // This should also regenerate the view. This could be replaced with an AJAX call

                    ItClarifiesService.invalidateDocumentCache(document, null);

                    return Html.successBox("Cleared cache for document " + document.getName()) + getDocumentView(document, section);

                case Callback_Action_Analyse:

                    // This could be replaced with an AJAX call to avoid

                    new AnalysisServlet().reAnalyse(document.getHeadVersion());

                    return Html.successBox("Reanalysed " + document.getName()) +getDocumentView(document, section);

                case Callback_Action_MoveToTop:

                    // Moving an item to the top for reordering projects from the back office

                    moveToTop(document);

                    return "Moved " + document.getName() + " to the top ov the document list";

                case Callback_Action_ChangeStyle:

                    DBKeyInterface _fragment = new DatabaseAbstractionFactory().createKey(request.getParameter("fragment"));
                    ContractFragment fragment = new ContractFragment(new LookupByKey(_fragment));

                    String styleType = request.getParameter("styleType");

                    fragment.setType(styleType);
                    fragment.update();


                    return Html.variableBox("Set style "+ styleType +" for fragment " + fragment.getName()) + getDocumentView(document, section);

                case Callback_Action_SetAccessRC:
                case Callback_Action_SetAccessRO:
                case Callback_Action_SetAccessRWD:

                    return Html.variableBox("Set access Right not implemented");

                case Callback_Action_ViewDoc:

                    return(Html.paragraph(Html.link("?id="+document.getKey().toString()+"&list=" + name + "&action=Item&callbackAction=" + Callback_Action_View + "&section=" + section, "Back")) +
                            document.getInternalView(true));


            }

        }catch(BackOfficeException e){

            // Handle exception here
        }

        return "No action performed...";

    }

    /**********************************************************
     *
     *      Move a document to the top
     *
     * @param thisDocument             - the document to move
     * @throws BackOfficeException
     *
     *          //TODO: Improvement Refactor this should use the POST /Document service
     */

    private void moveToTop(Contract thisDocument) throws BackOfficeException{

        Project project = thisDocument.getProject();
        List<Contract> allDocumentsInProject = project.getContractsForProject(new LookupList().addOrdering(ContractTable.Columns.Ordinal.name(), Ordering.FIRST));

        int ordinal = 1;
        for(Contract documentInList : allDocumentsInProject){

            if(documentInList.equals(thisDocument))
                documentInList.setOrdinal(0);
            else
                documentInList.setOrdinal(ordinal++);


            documentInList.update();
        }

        ItClarifiesService.invalidateDocumentCache(thisDocument, project);

    }

    private String getDocumentView(Contract document, String section) {

        try {

            StringBuffer docView = new StringBuffer();
            ContractVersionInstance headVersion = document.getHeadVersion();

            docView.append(Html.heading(2, document.getName().toLowerCase()));
            docView.append(Html.paragraph("Latest Version: " + headVersion.getVersion() + " Uploaded at:" + headVersion.getCreation().getSQLTime().toString()));
            docView.append(Html.paragraph("id: " + document.getKey().toString()));
            docView.append(Html.paragraph(""));
            docView.append(Html.paragraph(Html.link("?id="+document.getKey().toString()+"&list=" + name + "&action=Item&callbackAction=" + Callback_Action_ClearCache + "&section=" + section, "Clear cache for document")));
            docView.append(Html.paragraph(Html.link("?id="+document.getKey().toString()+"&list=" + name + "&action=Item&callbackAction=" + Callback_Action_Analyse + "&section=" + section, "Re-analyse")));
            docView.append(Html.paragraph(Html.link("/Export?document="+document.getKey().toString()+"&magicKey="+ SessionManagement.MagicKey, "Download Document")));
            docView.append(Html.paragraph(Html.link("/Export?document="+document.getKey().toString()+"&inject=true&magicKey=" + SessionManagement.MagicKey, "Export Document")));
            docView.append(getAccessOptions(document, section));
            docView.append(Html.paragraph(Html.link("?id=" + document.getKey().toString() + "&list=" + name + "&action=Item&callbackAction=" + Callback_Action_ViewDoc + "&section=" + section, "View Content")));

            return docView.toString();

        } catch (BackOfficeException e) {

            PukkaLogger.log(PukkaLogger.Level.FATAL, "Could not generate document view ofr document " + document.getName());
            return "No document view could be generated";
        }

    }

    private Object getAccessOptions(Contract document, String section) throws BackOfficeException {

        AccessGrant grant = new AccessGrant(new LookupItem()
            .addFilter(new ReferenceFilter(AccessGrantTable.Columns.Document.name(), document.getKey()))
            .addFilter(new ReferenceFilter(AccessGrantTable.Columns.Visibility.name(), Visibility.getOrg().getKey())));

        String access = "no";
        if(grant.exists()){

            access = grant.getAccessRight().getName();
        }

        System.out.println("Access: " + access);
        String roOption, rcOption, rwdOption;

        if(access.equals("ro"))
            roOption = Html.bold("RO ");
        else
            roOption = Html.link("?id=" + document.getKey().toString() + "&list=" + name + "&action=Item&callbackAction=" + Callback_Action_SetAccessRO + "&section=" + section, "Set RO ");

        if(access.equals("rc"))
            rcOption = Html.bold("RC ");
        else
            rcOption = Html.link("?id=" + document.getKey().toString() + "&list=" + name + "&action=Item&callbackAction=" + Callback_Action_SetAccessRC + "&section=" + section, "Set RC ");

        if(access.equals("rwd"))
            rwdOption = Html.bold("RWD ");
        else
            rwdOption = Html.link("?id=" + document.getKey().toString() + "&list=" + name + "&action=Item&callbackAction=" + Callback_Action_SetAccessRWD + "&section=" + section, "Set RWD ");

        return (Html.paragraph(roOption + rcOption + rwdOption));


    }


    @Override
    public boolean hasAction(int action, DataObjectInterface object){


        return true;

    }


    @Override
    public String addForm(String section)throws BackOfficeException{

        DataTableInterface table = new ContractTable();
        BackOfficeLocation location = new BackOfficeLocation(backOffice, section, "");
        return new TableEditForm(table, null, TableEditForm.FormType.ADD, location, "&list=" + Name).renderForm();

    }

    @Override
    public String submit(HttpServletRequest request){

        try{

            DataObjectInterface object = table.getDataObject(request);
            object.store();
            return ("Success:A new Document with id " + object.getKey() + " was created");

        }
        catch(BackOfficeException e){

            PukkaLogger.log(e);
            System.out.println("Error: Error adding element " + e.narration);
        }

        return "Error: Could not create Item...";
    }






}

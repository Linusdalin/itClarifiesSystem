<%@ page import="dataRepresentation.DataTableInterface" %>
<%@ page import="pukkaBO.acs.ACS_Brand" %>
<%@ page import="pukkaBO.acs.AccessControlSystem" %>
<%@ page import="pukkaBO.exceptions.BackOfficeException" %>
<%@ page import="pukkaBO.list.ListInterface" %>
<%@ page import="pukkaBO.form.FormInterface" %>
<%@ page import="pukkaBO.pivot.PivotView" %>
<%@ page import="pukkaBO.GenericPage.PageInterface" %>
<%@ page import="pukkaBO.acs.ACS_User" %>
<%@ page import="pukkaBO.acs.ACS_LoginMethod" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="databaseLayer.DBKeyInterface" %>
<%@ page import="databaseLayer.AppEngine.AppEngineKey" %>
<%@ page import="databaseLayer.DatabaseAbstractionFactory" %>
<%@ page import="databaseLayer.DatabaseAbstractionInterface" %>


<%

    ACS_LoginMethod loginMethod = ACS_LoginMethod.getPassword();

    DataTableInterface table = null;
    ListInterface list = null;
    FormInterface form = null;
    PivotView pivotView = null;
    PageInterface pageComponent = null;
    int callbackAction = 0;
    String activeItem = "";
    int tabId = 0;

    // Common parameters for multiple pages. Additional parameters may be read in
    // each and every page.


    String tableParameter   = request.getParameter("table");           // Which table to access
    String pivotParameter   = request.getParameter("pivot");           // Which pivot view to access
    String listParameter    = request.getParameter("list");           // Which table to access
    String action           = request.getParameter("action");          // Action to perform on a table
    String callbackPar      = request.getParameter("callbackAction");          // Action to perform on a list
    String formAction       = request.getParameter("formAction");      // Button for the form  submit
    String formParameter    = request.getParameter("form");            // Form to display
    String idParameter      = request.getParameter("id");              // row of table
    String pageParameter    = request.getParameter("page");             //page number
    String tabParameter    = request.getParameter("tab");             //page number

    String sizeParameter    = request.getParameter("pageSize");         //Items per page


    if(tableParameter != null){
        table = backOffice.getTableByName(tableParameter);
        if(table != null)
            pageTitle = table.getTitle();

        activeItem="table="+tableParameter;

    }

    if(pivotParameter != null){
        pivotView = backOffice.getPivotViewByName(pivotParameter);
        if(pivotView != null)
            pageTitle = pivotView.getTitle();

        activeItem="pivot="+pivotParameter;

    }

    if(pageParameter != null){

        pageComponent = backOffice.getPageByName(pageParameter);

        activeItem="page="+pageParameter;


    }




    if(listParameter != null){
        list = backOffice.getListByName(listParameter);
        if(list != null)
            pageTitle = list.getTitle();

        activeItem="list="+listParameter;

    }


    if(action == null)
        action = "";        // Default action

    DBKeyInterface id = null;

    if(idParameter != null){

        try{

            System.out.println("idParameter: " + idParameter);

            DatabaseAbstractionInterface database = new DatabaseAbstractionFactory().getDatabase();
            id = database.createKey(idParameter);

        }catch(Exception e){
            //e.printStackTrace();

            System.out.println("Warning, illegal id parameter " + idParameter + "- ignored. (" + e.getMessage() + ")");
        }

    }

    if(tabParameter != null){

        try{

            tabId = new Integer(tabParameter);

        }catch(Exception e){
            System.out.println("Warning, illegal tab parameter " + tabParameter + "- ignored");
        }

    }

    if(callbackPar != null){

        try{

            callbackAction = new Integer(callbackPar);

        }catch(Exception e){
            System.out.println("Warning, illegal callback parameter " + callbackPar + "- ignored");
        }

    }


    int pageId = 1; // No pagination implemented
    int pageSize = -1;  //Default no page size. Load all

    if(sizeParameter != null){

        try{

            pageId = new Integer(sizeParameter);

        }catch(Exception e){
            System.out.println("Warning, illegal page parameter " + sizeParameter + "- ignored");
        }

    }



    String[] values = null;         // Parameter values from the data objects

    // Get all the parameters for the form


    if(formParameter != null){

        form = backOffice.getFormByName(formParameter);

        if(form == null){

            new BackOfficeException(BackOfficeException.FormError, "Form is null for form=" + formParameter).logAsMinor("Error in parameter.inc");

        }else{

            String[] formParameterList = form.getParameters();
            values = new String[formParameterList.length];

            int i = 0;
            for(String parameterName : formParameterList){

                values[i++] = request.getParameter(parameterName);
            }

            // Just for testing:

            System.out.print("Got Form Parameters:\n\n");


            for(i = 0; i < values.length; i++){

                System.out.println(" - parameter " + i +"("+ formParameterList[i]+")=" + values[i]);
            }



        }



    }





%>


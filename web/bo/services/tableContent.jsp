<%@ page import="dataRepresentation.DataTableInterface" %>
<%@ page import="pukkaBO.renderer.TableRendererInterface" %>
<%@ page import="dataRepresentation.DataObjectInterface" %>
<%@ page import="pukkaBO.exceptions.BackOfficeException" %>
<%

            // Loading the actual table content.
            // This will be different depending on the renderer used.


%>

<%@ include file="../../bean.inc" %>
<%@page buffer="100kb" %>

<!-- Include scripts for the edit lightbox -->


<%
    boolean showId = true; // TODO: Implement this as parameter to the page

    String tableName = request.getParameter("table");
    System.out.println("Retrieving table content for table = " + tableName);

    DataTableInterface tableObject = backOffice.getTableByName(tableName);
    TableRendererInterface renderer = backOffice.getRenderer();
    try{

        tableObject.loadFromDatabase();
        //out.print(renderer.getTableContent(tableObject, showId));

    }catch(BackOfficeException e){

        out.print("<p>No table</p>");
        System.out.println(e.narration);
        e.printStackTrace();

    }



%>
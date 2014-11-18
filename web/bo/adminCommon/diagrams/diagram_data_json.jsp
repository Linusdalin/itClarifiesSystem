<%@ page import="dataRepresentation.DBTimeStamp" %>
<%@ page import="java.util.List" %>
<%@ page import="pukkaBO.Charts.ChartInterface" %>
<%@ page import="pukkaBO.exceptions.BackOfficeException" %>

<%@ include file="../../../bean.inc" %>
<%@page buffer="100kb" %>


<%

        /*******************************************************************************
         *
         *      generate the correct response for an AJAX request of data for the diagram.
         *
         *      The response is set to text/x-json.
         *
         *      The diagram type is defined by the parameter "diagram" that should match a diagram name in the back office
         *
         *
         */


    // First get all the possible parameters to the diagram data service

    DBTimeStamp from = null;    // Time span in a time series diagram
    DBTimeStamp to = null;      // Time span in a time series diagram

    ChartInterface chart = null;

    String dateFromTmp = request.getParameter("dateFrom");
    String dateToTmp = request.getParameter("dateTo");
    String diagramName = request.getParameter("diagram");

    if(diagramName != null){

        try{

            chart = backOffice.getChartByName(diagramName);

        }catch(Exception e){

            System.out.println("Error reading diagram parameter. (diagram=" + diagramName);
            return;

        }

    }

    if(chart == null){

        System.out.println("Could not find diagram '" + diagramName + "'");
        return;

    }

    if(dateFromTmp != null && dateToTmp != null){

        try{

            from = new DBTimeStamp(DBTimeStamp.ISO_DATE, dateFromTmp);
            to = new DBTimeStamp(DBTimeStamp.ISO_DATE, dateToTmp);

        }catch(Exception e){

            System.out.println("Error reading to and from parameter. (from=" + dateFromTmp + " to=" + dateFromTmp);
            return;

        }

    }


    // Set the correct response content type

    response.setContentType("text/x-json");

    String json = null;
    try {

        json = chart.getJsonEncoding(null);

    } catch (BackOfficeException e) {

        e.logError("Error getting json Data");
    }

    System.out.println("JSON:\n" + json + "\n*****************\n\n");

    out.print(json);

%>
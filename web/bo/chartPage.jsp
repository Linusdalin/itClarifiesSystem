<%@ page import="pukkaBO.Charts.ChartInterface" %>
<%@ page import="dataRepresentation.DBTimeStamp" %>
<%@ page import="pukkaBO.exceptions.BackOfficeException" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN">


<!--

        chartPage.jsp - Generic file for displaying a chart
        This is displayed in an iFrame to solve the issue with
        colliding js directories

-->

<%@ include file="../bean.inc" %>
<%@page buffer="100kb" %>

<!--

        Include the correct functionality for the page.
-->


<html>
<head>


    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title> Chart page</title>

    <script type='text/javascript' src='http://code.jquery.com/jquery-git.js'></script>

    <link rel="stylesheet" type="text/css" href="/css/result-light.css">

    <style type='text/css'>

    </style>

    <%
        String name = request.getParameter("chart");
        String fromParameter = request.getParameter("from");
        String toParameter   = request.getParameter("to");
        String parameter = request.getParameter("parameter");
        ChartInterface chart = backOffice.getChartByName(name);

        try {

            if(chart == null){

                out.print("<p>No chart named "+ name + "</p>");
                return;
            }

            DBTimeStamp from = new DBTimeStamp(DBTimeStamp.ISO_DATE, fromParameter);
            DBTimeStamp to = new DBTimeStamp(DBTimeStamp.ISO_DATE, toParameter);

            System.out.println("In chartPage: From: " + fromParameter + " to: " + toParameter);

            out.print("<script type='text/javascript'>//<![CDATA[ \n");
            out.print(chart.getChartScript(parameter));
            out.print("//]]>  \n</script>\n" +
                "</head>\n" +
                "<body>\n");
                out.print("  <script src=\"http://code.highcharts.com/highcharts.js\"></script>\n" +
"  <script src=\"http://code.highcharts.com/modules/exporting.js\"></script>\n");
            out.print(chart.getChartDiv());

        } catch (BackOfficeException e) {

            e.logError("No Chart defined");
        }


    %>

</body>
</html>
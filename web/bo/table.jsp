<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">


<!--

        table.jsp - Generic file for displaying a table

-->

<%@ include file="../bean.inc" %>
<%@page buffer="100kb" %>
<%



    // Set the common values for the pageTypes

        String selection = request.getParameter("section");  // Get from parameter
        String pageTitle = ""; // Retrieved for the table

        String headline = "Default Headline";
        String message = "Default Text";


%>


<!--

        Include the correct functionality for the page.
-->




<%@ include file="adminCommon/pageTypes/tablePage.inc" %>

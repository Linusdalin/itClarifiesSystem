<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN">


<!--

        pivot.jsp - Generic file for displaying a table

-->

<%@ include file="../bean.inc" %>
<%@page buffer="100kb" %>
<%



    // Set the common values for the pageTypes

        String selection = request.getParameter("section");  // Get from parameter
        String pageTitle = ""; // Retrieved for the table

        String headline = "Pivot View";
        String message = "This is a page displaying a pivot table";


%>


<!--

        Include the correct functionality for the page.
-->




<%@ include file="adminCommon/pageTypes/pivotPage.inc" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN">


<!--

        exampleTable.jsp - Example file for the

-->

<%@ include file="../bean.inc" %>
<%@page buffer="100kb" %>



<%



    // Set the common values for the pageTypes

        String selection = "Home";
        String pageTitle = "Test Page";

        String headline = "Test page";
        String message = "This is the test page displaying the content of the table";


%>


<!--

        Include the correct functionality for the page.
-->




<%@ include file="adminCommon/pageTypes/tablePage.inc" %>
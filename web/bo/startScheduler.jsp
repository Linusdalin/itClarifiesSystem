<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN">


<!--

        startScheduler.jsp - Service test. Add back office tests here

-->

<%@ include file="../bean.inc" %>
<%@page buffer="100kb" %>
<%



   backOffice.startNewJobs();


%>

    <p>New Jobs Started</p>
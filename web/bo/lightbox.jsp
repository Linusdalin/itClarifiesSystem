<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"><meta http-equiv="content-type" content="text/html; charset=UTF-8" />

<%@ page import="pukkaBO.GenericPage.PageTabInterface" %>
<%@ include file="../bean.inc" %>
<%@page buffer="100kb" %>
<html>

<%
    // This is a generic lightbox

    String selection=null;
    String pageTitle=null;
%>

<%@ include file="adminCommon/includes/parameters.inc" %>

<%

    if(pageComponent == null){
        out.print("Page " + pageParameter + "doesn't exist");
        return;
    }
    else{

        try {

            selection = pageComponent.getSection(selection);
            pageTitle = pageComponent.getTitle();

        } catch (BackOfficeException e) {

            e.logError("Error getting page component");
        }
    }

    System.out.println("Action: " + action);

    // Now check callbacks. Both the page and a form could have callbacks defined.

    String callBackMessage = null;

    if(form != null && formAction != null){

        // We have a form with a submit action

        try {
            callBackMessage = form.submitCallBack(values, backOffice);

        } catch (BackOfficeException e) {

            e.logError("Error in submit callback");
        }

    }

    if(action.equals("pageSubmit")){

        try{
            callBackMessage = pageComponent.callBack(tabId, request, backOffice);

        } catch (BackOfficeException e){

            e.logError("Error in callback");
        }

    }

%>



<head>
    <%@ include file="adminCommon/includes/head.inc" %>
</head>


<body class="loggedin">
<%@ include file="adminCommon/includes/verifyLoginSession.inc" %>





            <%@ include file="adminCommon/includes/message.inc" %>

            <%
                // Get the tabs

                out.print(pageComponent.renderTabs(tabId));
                out.print("  <div class=\"content\">");

                PageTabInterface tab = pageComponent.getTabs().get(tabId);

                out.print("<h2>" + tab.getHeadline() + "</h2>");

                //TODO: This should really be a message box

                if(callBackMessage != null)
                    out.print("<p>"+ callBackMessage+"</p>");

                try{
                    out.print(tab.getBody(pageComponent.getName(), tabId, backOffice, request, values, adminUser, acsSystem, loginMethod));

                }catch(BackOfficeException e){

                    e.logError("Error getting page");
                    out.print("<p>No body</p>");
                }

            %>

    </body>
</html>



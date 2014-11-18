<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"><meta http-equiv="content-type" content="text/html; charset=UTF-8" />

<%@ page import="pukkaBO.GenericPage.PageTabInterface" %>
<%@ include file="../bean.inc" %>
<%@page buffer="100kb" %>
<html>

<%
    // This is a generic page that uses no menus or borders

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





    <!-- START OF MAIN CONTENT -->
    <div class="mainwrapper">
        <div class="mainwrapperinner">

            <div class="maincontent">
            	<div class="maincontentinner">

                    <%@ include file="adminCommon/includes/message.inc" %>

                    <%
                        // Get the tabs

                        out.print(pageComponent.renderTabs(tabId));
                        out.print("  <div class=\"content\">");

                        PageTabInterface tab = pageComponent.getTabs().get(tabId);

                        out.print("<h2>" + tab.getHeadline() + "</h2>");
                        out.print("<p>" + tab.getDescription() + "</p></br>");

                        //TODO: This should really be a message box class

                        if(callBackMessage != null){

                            out.print("<div class=\"notification msginfo\">\n" +
                                    "                        <a class=\"close\"></a>\n" +
                                    "                        <p>"+ callBackMessage+"</p>\n" +
                                    "                    </div><!-- notification msginfo -->");


                        }

                        try{
                            out.print(tab.getBody(pageComponent.getName(), tabId, backOffice, request, values, adminUser, acsSystem, loginMethod));

                        }catch(BackOfficeException e){

                            out.print("<div class=\"notification msgerror\">\n" +
                                    "                        <a class=\"close\"></a>\n" +
                                    "                        <p>"+ e.narration+"</p>\n" +
                                    "                    </div><!-- notification -->");

                            e.logError("Error on page");
                        }

                    %>


            </div>
        </div>


            </div>

            </div>
         </div>
    </body>
</html>



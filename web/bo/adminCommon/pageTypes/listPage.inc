<%@ page import="pukkaBO.form.Form" %>
<%@ page import="pukkaBO.backOffice.LightBox" %>
<%@ page import="pukkaBO.renderer.TableRendererInterface" %>
<%@ page import="dataRepresentation.DataObjectInterface" %>
<%@ page import="pukkaBO.style.Html" %>


<html xmlns="http://www.w3.org/1999/xhtml">

<%@ include file="../includes/parameters.inc" %>

<head>
    <%@ include file="../includes/head.inc" %>
</head>


<body class="loggedin">
<%@ include file="../includes/verifyLoginSession.inc" %>

<%@ include file="../includes/header.inc" %>

    <!-- START OF MAIN CONTENT -->
    <div class="mainwrapper">
        <div class="mainwrapperinner">

            <%@ include file="../includes/menu.inc" %>

            <div class="maincontent">
            	<div class="maincontentinner">

                    <%@ include file="../includes/message.inc" %>

                    <%@ include file="../includes/tabs.inc" %>


                    <div class="content">


                <%

                    // Handle actions for the list

                    System.out.println("Actions:  listAction = "+ action+" callbackAction =" + callbackAction);

                    if(list == null){

                        // No list

                        out.print(Html.errorBox("No list"));
                    }
                    else{

                        out.print(list.getRenderer().renderWithAction(list, action, callbackAction, id, values,
                                                adminUser, displayBrand, acsSystem, loginMethod, selection, request));
                    }

                %>

                  <%@ include file="../includes/footer.inc" %>

                    </div>
                        <%@ include file="../includes/rightNav.inc" %>


                </div>
            </div>
    </body>
</html>


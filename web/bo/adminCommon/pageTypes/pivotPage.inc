<%@ page import="pukkaBO.form.Form" %>
<%@ page import="pukkaBO.backOffice.LightBox" %>
<%@ page import="pukkaBO.renderer.TableRendererInterface" %>
<%@ page import="pukkaBO.formsPredefined.TableFilterForm" %>
<%@ page import="static junit.framework.Assert.assertTrue" %>
<%@ page import="pukkaBO.pivot.*" %>

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

    if(pivotView != null){

        out.println("<h2>" + pivotView.getTitle() + "</h2>");
        out.println("<p>" + message + "</p>");

        //pivotView.render();


    }else{

        // No table selected. Print the title and the message instead


        out.print("<h2>" + headline + "</h2>");
        out.print("<p>" + message + "</p>");


    }

        int xCol = -2, yCol = -2, body = -2, function = -2;

        System.out.println("Parameters: Xcol: " + request.getParameter("XCol") +
                "Ycol: " + request.getParameter("YCol") +
                "Body: " + request.getParameter("Body") +
                "Function: " + request.getParameter("Function"));

        try{

            xCol = new Integer(request.getParameter("XCol"));
            yCol = new Integer(request.getParameter("YCol"));
            body = new Integer(request.getParameter("Body")) -1 ;
            function = new Integer(request.getParameter("Function")) -1;

        }catch(Exception e){
            //TODO: Fix this better
        }

        if(table != null){

            PivotTable pivotTable = new PivotTable(table, backOffice);

            FormInterface pivotForm = new pukkaBO.pivot.PivotSelectionForm(pageParameter, pivotTable, selection, request, tabId);
            out.print(pivotForm.renderForm(pageParameter, tabId));


         %>

        <div class="contenttitle radiusbottom0">
         <h2 class="table"><span><% out.print(table.getTitle());%></span></h2>
        </div><!--contenttitle-->



            <%

                    try{


                        if(xCol != -2)
                            pivotTable.setX(new PivotSelection(table, xCol));
                        if(yCol != -2)
                            pivotTable.setY(new PivotSelection(table, yCol));
                        if(body != -2)
                            pivotTable.setBody(new PivotSelection(table, body).setFunction(function));

                        out.print( pivotTable.renderPivotView( backOffice ) );

                    }catch(BackOfficeException e){
                        e.logError("Error in render pivot");

                    }
                }


                %>


                    </div>
                </div>

                <%@ include file="../includes/footer.inc" %>

                </div>
                <%@ include file="../includes/rightNav.inc" %>

            </div>
         </div>
    </body>
</html>


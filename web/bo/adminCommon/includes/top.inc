<%@ page import="pukkaBO.formsPredefined.TableFilterForm" %>
<%@ include file="../../../bean.inc" %>
<%@page buffer="100kb" %>
<%



    // Set the common values for the pageTypes

        String selection = request.getParameter("section");  // Get from parameter

    if(selection == null)
        selection = SECTION;

%>


<%@ include file="../includes/parameters.inc" %>


<head>

    <%@ include file="../includes/head.inc" %>


</head>


<body>
<%@ include file="../includes/verifyLogin.inc" %>

<%@ include file="../includes/menu.inc" %>


    <div class="container-fluid">
        <div class="row">

            <div class="span4">

                <%@ include file="../includes/rightNav.inc" %>

            </div>


            <div class="span14">
              <div class="hero-unit">

    <%




    if(action.equals("list")){

        out.println("<h2>" + table.getTitle() + "</h2>");
        out.println("<p>" + message + "</p>");

        try {

            //Not implemented: Send in a object with predefined values here

            form = new TableFilterForm(table, null, selection, backOffice);

        } catch (BackOfficeException e) {

            e.logError("Error getting table");
        }

        if(form != null){

                out.println("<div class=\"span8\">");
                out.println(form.renderForm());
                out.println("</div>");

        }

    }else{

        // No table selected. Print the title and the message instead


        out.print("<h1>" + headline + "</h1>");
        out.print("<p>" + message + "</p>");

            }

 %>

                  </div>



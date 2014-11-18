<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<!--

        formLightbox.jsp - Generic file for displaying a form

-->



<%@ include file="../bean.inc" %>
<%@page buffer="100kb" %>

<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
<html>

<%
    String pageTitle;
    String selection = "lightBox"; // Used for the login check

%>

<%@ include file="../bo/adminCommon/includes/parameters.inc" %>



<head>
    <link rel="stylesheet" type="text/css" href="adminCommon/styles/<% out.print(backOffice.getStyleConfig().style);%>/css/backOffice.css" />

<title>

    <%  out.print("TODO: Form title"); %>

</title>

</head>

<body>
<style>body {background:#FFFFFF;}</style>

<div class="container-fluid">
    <div class="span10">


            <%
                if(form == null){

                    out.print("<p>No Form with the name " + formParameter + " could be found</p>");
                }
                else{

                    // Render title

                    out.print("<div class=\"hero-unit\">\n" +
                            "            <h2>" + form.getTitle()+ "</h2>\n" +
                            "        </div>");

                    try {

                        if(action == null || action.equals("")){

                            // Render form
                            out.print(form.renderForm("page", 0));

                        }
                        else{

                                out.print(form.submitCallBack(values, backOffice));
                        }

                    } catch (BackOfficeException e) {

                        e.logError("Error in getting form");
                    }
                }

            %>



    </div>

</div>
</body>

</html>


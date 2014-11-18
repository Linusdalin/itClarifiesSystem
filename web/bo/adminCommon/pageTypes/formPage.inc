<%@ page import="pukkaBO.form.Form" %>
<%@ page import="pukkaBO.backOffice.LightBox" %>
<%@ page import="pukkaBO.form.FormInterface" %>

<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
<html>

<%@ include file="../includes/parameters.inc" %>

<head>

    <%@ include file="../includes/head.inc" %>


</head>


<body>
<%@ include file="../includes/verifyLoginSession.inc" %>

<%@ include file="../includes/menu.inc" %>



<div class="container-fluid">
    <div class="row">

        <div class="span4">

            <%@ include file="../includes/rightNav.inc" %>

        </div>


        <div class="span14">
          <div class="hero-unit">

                    <%

                    out.print("<h1>" + headline + "</h1>\n");
                    out.print("<p>" + message + "</p>\n");




                        if(form != null)
                            if(!action.equals("submit"))
                                out.print(form.renderForm("page", 0));
                            else
                                out.print(form.submitCallBack(values, backOffice));
                        else
                            out.print("<p>Error no form "+ formParameter + "</p>");

                    %>


                </div>
            </div>
        </div>
    </div>


</body>

</html>


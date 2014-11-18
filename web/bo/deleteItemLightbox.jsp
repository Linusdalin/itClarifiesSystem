<%@ page import="dataRepresentation.ColumnStructureInterface" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="dataRepresentation.DataObjectInterface" %>
<%@ page import="pukkaBO.exceptions.BackOfficeException" %>
<%@ page import="pukkaBO.formsPredefined.TableEditForm" %>
<%@ page import="pukkaBO.form.FormInterface" %>
<%@ page import="pukkaBO.style.Html" %>

<%@ include file="../bean.inc" %>
<%@page buffer="100kb" %>

<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
<html>

<%
    String pageTitle = "";
    String selection = "lightBox"; // Used for the login check

%>

<%@ include file="adminCommon/includes/head.inc" %>
<%@ include file="adminCommon/includes/parameters.inc" %>
<%@ include file="adminCommon/includes/verifyLoginSession.inc" %>


        <%


            boolean success = false;

            if(id != null){
                DataObjectInterface object = table.getDataObject();
                object.setKey(id);

                success = table.deleteItem(object);

            }
            if(success){

                out.print(Html.successBox("Entry deleted"));
                out.print("<input type=\"button\" value=\"Close\" onClick=\"parent.window.location.reload(true)\">");


            }
            else{

                out.print(Html.errorBox("Entry was NOT deleted"));

            }
        %>



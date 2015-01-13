<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ page import="contractManagement.*" %>
<%@ page import="pukkaBO.condition.*" %>
<%@ page import="databaseLayer.DBKeyInterface" %>
<%@ page import="databaseLayer.DatabaseAbstractionFactory" %>
<html>



<%
    //TODO: This is deprecated. There is a preview service to use

    String token =   request.getParameter("token");
    String _document = request.getParameter("document");

    String hostParameter = request.getParameter("host");
    String host = "";

    if(hostParameter != null)
        host = hostParameter;

    if(_document == null){

        out.print("<p>No document given</p>");
        return;
    }

    DBKeyInterface document = new DatabaseAbstractionFactory().createKey(_document);
    Contract contract = new Contract(new LookupByKey(document));

    ContractVersionInstance lastVersion = contract.getHeadVersion();

    ContractFragmentTable allFragments = new ContractFragmentTable(
            new LookupList(new Sorting(ContractFragmentTable.Columns.Ordinal.name(), Ordering.FIRST))
            .addFilter(new ReferenceFilter(ContractFragmentTable.Columns.Version.name(), lastVersion.getKey())));

%>


    <!--        <a href="Transaction?User=LinusD&Game=slots&Bet=1000&Win=800&TimeStamp=2013-08-25">Create Slot Transaction</a><br> -->

    <head>
        <title>Document View</title>
        <link rel="stylesheet" type="text/css" href="../doc/docstyle.css" title="Style">
    </head>

<body>

<h1>Document Modification View</h1>

<p>Update document </p>

<div style="float:left; width:100%">

    <fieldset>



    </fieldset>



</div>



</body>

<%!
    String getTokenParameter(boolean realToken, String form){

        if(realToken)
            return "<p>\t<label for=\""+ form+"_token\">Token</label>\n" +
                   "        <input type=\"text\"   id=\""+ form+"token\" name=\"session\" size=\"30\"></p>\n";
        else
            return "<input type=\"hidden\" name=\"session\" value=\"DummySessionToken\">\n";
}

%>

</html>
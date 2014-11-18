<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ page import="pukkaBO.exceptions.BackOfficeException" %>
<%@ page import="contractManagement.*" %>
<%@ page import="pukkaBO.condition.*" %>
<%@ page import="databaseLayer.DBKeyInterface" %>
<%@ page import="databaseLayer.DatabaseAbstractionFactory" %>
<%@ page import="dataRepresentation.DropDownTableReference" %>
<%@ page import="dataRepresentation.DataObjectInterface" %>
<%@ page import="log.PukkaLogger" %>
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


        <%

            DropDownTableReference dropDown = new ContractFragmentTypeTable().getDropDown();

            PukkaLogger.log(PukkaLogger.Level.INFO, "Found " + allFragments.getValues().size() + " fragments in document " + contract.getName());

            for(DataObjectInterface object : allFragments.getValues()){

                ContractFragment fragment = (ContractFragment)object;
        %>

        <FORM METHOD=POST action="<% out.print(host); %>/Fragment" name="fragmentForm"  accept-charset="UTF-8">

                <div style="float:left; width:25%">

                    <% out.print(dropDown.generate("type", null, null, "", false)); %>
                    <input type="text" id="indentation" name="indentation" value="<% out.print(fragment.getIndentation());%>">
                    </div>

                <div style="float:left; width:40%">
                    <textarea rows="3" cols="50" name="text">
                        <% out.print(fragment.getText());%>
                    </textarea>
                </div>

                <div style="float:left; width:20%">

                    <input type="text" id="annotations" name="annotations" value="<% out.print(fragment.getAnnotationCount());%>">
                    <input type="hidden" id="session" name="session" value="<%out.print(token);%>">
                    <input type="hidden" id="fragment" name="fragment" value="<%out.print(fragment.getKey().toString());%>">

                    <input type="submit" value="Update" class="btn primary" id="submit_fragment" />
                </div>
        </FORM>

        <div style="float:left; width:5%">

        <FORM METHOD=DELETE action="<% out.print(host); %>/Fragment" name="fragmentForm">
            <input type="hidden" id="session2" name="session" value="<%out.print(token);%>">
            <input type="hidden" id="fragment2" name="key" value="<%out.print(fragment.getKey().toString());%>">
            <input type="hidden" name="_method" value="DELETE" />

            <input type="submit" value="Delete" class="btn primary" id="delete_fragment" />
        </FORM>


            </div>

                <div style="clear:both">&nbsp;</div>


        <%

            }
        %>

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
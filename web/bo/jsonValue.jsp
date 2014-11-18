<%@ page import="dataRepresentation.DataTableInterface" %>
<%@ page import="pukkaBO.exceptions.BackOfficeException" %>
<%@ page import="dataRepresentation.DataObjectInterface" %>
<%@ page import="java.util.List" %>
<%@ page import="pukkaBO.renderer.TableSize" %>


<%
    /*

        jsonValue.jsp - generating json value from a specific column for a table
                        to be used together with the text field autocomplete.


        Example:

            {
                "suggestions": [
                    { "value": "United Arab Emirates", "data": "AE" },
                    { "value": "United Kingdom",       "data": "UK" },
                    { "value": "United States",        "data": "US" }
                ]
            }

            // TODO: Add exception handling on the parameters
            // TODO: Move this to a web service
    */
%>

<%@ include file="../bean.inc" %>
<%@page buffer="100kb" %>

<%

    try{

        String tableName = request.getParameter("table");
        String columnParameter = request.getParameter("column");
        DataTableInterface table = backOffice.getTableByName(tableName);

        int column = table.getNameColumn();

        // Default is name column. But if there is another column, we can use it

        if(columnParameter != null)
            column = new Integer(columnParameter);

        table.loadFromDatabase();

        out.print("{\n");
        out.print("   \"suggestions\":[\n");

        boolean first = true;

        for(DataObjectInterface item : table.getValues()){

            if(!first){
                out.print(", \n");
            }

            first = false;


            String value = item.getColumnValues()[column].getRenderedValue(backOffice, TableSize.SMALL, false);
            out.print("      {" + "\"value\": \"" + value +"\", \"data\": \"" +  value + "\"}");

        }

        out.print("   ]\n");
        out.print("}\n");


    }catch(BackOfficeException e){

    }


%>
<%@ page import="pukkaBO.list.ListInterface" %>
<%@ page import="pukkaBO.exceptions.BackOfficeException" %>
<%@ page import="dataRepresentation.DataTableInterface" %>
<%@ include file="../bean.inc" %>
<%@page buffer="200kb" %><%

    String listName = request.getParameter("list");
    String tableName = request.getParameter("table");

    response.setContentType("application/octet-stream");


    if(listName != null){

        try{

                ListInterface list = backOffice.getListByName(listName);
                response.setHeader("Content-Disposition", "attachment;filename=" + listName+".csv");
                out.print(list.getCsvExport());


        }catch(BackOfficeException e){

            e.logError("Error in rendering csv file for list " + listName);
            out.print("Error generating csv file");
        }


    }
    else if(tableName != null){

        try{

                DataTableInterface table = backOffice.getTableByName(tableName);
                response.setHeader("Content-Disposition", "attachment;filename=" + tableName+".csv");
                out.print(table.getCsvExport());


        }catch(BackOfficeException e){

            e.logError("Error in rendering csv file for table " + tableName);
            out.print("Error generating csv file");
        }

    }

%>
package services;

import backend.ItClarifies;
import contractManagement.*;
import dataRepresentation.DataTableInterface;
import log.PukkaLogger;
import project.ProjectTable;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.exceptions.BackOfficeException;
import search.KeywordTable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ConstantServlet extends ItClarifiesService{

    public static final String DataServletName = "Constant";

    private static final String[] allTables = new String[] {
            "Contract", "Project", "ContractFragment", "ContractClause",
            "ContractAnnotation", "ContractVersionInstance", "ContractReference", "ContractType", "ContractStatus",
    };


    private static final List<String> exportTables = new ArrayList<String>();

    public static final BackOfficeInterface backOffice = new ItClarifies();


    /****************************************************************************'
     *
     *          Get all constants for a table (with symbolic names
     *
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
     *
     *
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

       String tableName;
       String password;

        exportTables.add(ContractTable.TABLE);
        exportTables.add(ContractVersionInstanceTable.TABLE);
        exportTables.add(ContractClauseTable.TABLE);
        exportTables.add(ProjectTable.TABLE);
        exportTables.add(ContractFragmentTable.TABLE);
        exportTables.add(KeywordTable.TABLE);
        exportTables.add(ContractAnnotationTable.TABLE);

        try{

            tableName           = getOptionalString("table", req);
            //password       = getMandatoryString    ("password", req);

            boolean htmlEncode     = true;
            boolean prettyPrint = true;

            StringBuffer html = new StringBuffer();

            Formatter jsonExport = new Formatter()
                    .setFormat(Formatter.OutputFormat.HTML);

            List<String> tables;

            // Decide which tables to use:

            if(tableName != null){

                tables = new ArrayList<String>();
                tables.add(tableName);
            }
            else{
                tables = exportTables;
            }

            html.append("package contractTables;\n\n");
            html.append("import generation.tableMaker.TableConstant;\n\npublic class ITClarifiesConstants implements ConstantInterface{\n");

            html.append("  public TableConstant[] getConstants(String table){\n\n");


            for(String thisTable : tables){

                DataTableInterface table = backOffice.getTableByName(thisTable);

                if(table == null)
                    html.append("\n   // Could not find table " + tableName + "\n");
                else{
                    html.append("  if(table.equals(\"" + table.getTableName() + "\"))\n");
                    html.append("      return\n" );
                    html.append("\n" + table.reverseEngineerConstants("\n", exportTables));
                    html.append("\n");
                }


            }
            html.append("   return null;\n");

            html.append("} //getConstatns\n");

            html.append("} //class\n");

            setRespHeaders(jsonExport, resp);
            resp.getWriter().print(html);
            resp.flushBuffer();


        } catch (BackOfficeException e) {

            PukkaLogger.log(e);
            returnError("Error in " + DataServletName, HttpServletResponse.SC_BAD_REQUEST, resp);

        } catch (Exception e) {

            PukkaLogger.log(e);
            returnError("Error in " + DataServletName, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
        }



     }


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Post not supported in Constant", HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();


    }

    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in Constant", HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();


    }


}

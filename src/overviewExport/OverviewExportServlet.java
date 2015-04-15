package overviewExport;

import contractManagement.Project;
import databaseLayer.DBKeyInterface;
import document.AbstractImage;
import log.PukkaLogger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pukkaBO.condition.LookupByKey;
import pukkaBO.exceptions.BackOfficeException;
import services.Formatter;
import services.ItClarifiesService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/********************************************************
 *
 *          Project Overview servlet will return an excel document overview
 *
 */

public class OverviewExportServlet extends ItClarifiesService{

    public static final String DataServletName = "Overview";


    /*************************************************************************
     *
     *
     *
     * @throws java.io.IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {


       try{
           logRequest(req);

           if(!validateSession(req, resp))
               return;

           if(blockedSmokey(sessionManagement, resp))
               return;


           setLoggerByParameters(req);

           Formatter formatter = getFormatFromParameters(req);
           formatter.setFormat(Formatter.OutputFormat.XLSX);

           DBKeyInterface _project         = getMandatoryKey("project", req);
           Project project = new Project(new LookupByKey(_project));


           if(!mandatoryObjectExists(project, resp))
               return;

           /*

           RepositoryInterface repository = new BlobRepository();
           ContractVersionInstance version = document.getHeadVersion();
           RepositoryFileHandler fileHandler = new RepositoryFileHandler(document.getFile());
           String filename = document.getFile();

           if(!repository.existsFile(fileHandler))
               returnError("File " + version.getVersion() + " does not exist as a file on the server", HttpServletResponse.SC_BAD_REQUEST, resp);

           repository.serveFile(fileHandler, resp);

             */

           XSSFWorkbook overview = getExcelTemplate();
           OverviewGenerator populator = new OverviewGenerator(overview, project);
           populator.populate();

           //OutputStream os = new WriterOutputStream(resp.getWriter());
           //PrintStream ps = new PrintStream(os);

           OutputStream os = resp.getOutputStream();

           resp.setContentType(formatter.getContentType());

           resp.setHeader("Content-Disposition","inline;filename=\"" + project.getName() + "_overview.xlsx\"");
           resp.setHeader("Access-Control-Allow-Origin", "*");
           resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
           resp.setHeader("Access-Control-Max-Age", "" + (0));
           resp.setHeader("Access-Control-Allow-Headers", "x-requested-with");
           resp.setHeader("Access-Control-Allow-Headers", "content-type");

           overview.write(os);
           resp.flushBuffer();
           //resp.setCharacterEncoding("UTF-8");

        } catch (IOException e) {

           PukkaLogger.log( e );

        }catch(BackOfficeException e){

           PukkaLogger.log(e);
           returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        } catch ( Exception e) {

           PukkaLogger.log(e);
           returnError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
        }
    }



    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        doGet(req, resp);

     }


    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

     }

    private XSSFWorkbook getExcelTemplate() throws BackOfficeException {

        File templateFile = new File("exportTemplates/contracting framework template v1.xlsx");
        FileInputStream templateStream = null;
        try{

            templateStream = new FileInputStream(templateFile);
            XSSFWorkbook workbook = new XSSFWorkbook (templateStream);
            return workbook;

        }catch(FileNotFoundException e ){

            throw new BackOfficeException(BackOfficeException.AccessError, " Could not find template file");

        }catch(IOException e ){

            throw new BackOfficeException(BackOfficeException.AccessError, " Could not create workBook");
        }


    }


    private XSSFWorkbook getExcelTemplateTest() {

        //Blank workbook

        XSSFWorkbook workbook = new XSSFWorkbook();

        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet("Employee Data");

        //This data needs to be written (Object[])
        Map<String, Object[]> data = new TreeMap<String, Object[]>();
        data.put("1", new Object[] {"ID", "NAME", "LASTNAME"});
        data.put("2", new Object[] {1, "Amit", "Shukla"});
        data.put("3", new Object[] {2, "Lokesh", "Gupta"});
        data.put("4", new Object[] {3, "John", "Adwards"});
        data.put("5", new Object[] {4, "Brian", "Schultz"});

        //Iterate over data and write to sheet
        Set<String> keyset = data.keySet();
        int rownum = 0;
        for (String key : keyset)
        {
            Row row = sheet.createRow(rownum++);
            Object [] objArr = data.get(key);
            int cellnum = 0;
            for (Object obj : objArr)
            {
               Cell cell = row.createCell(cellnum++);
               if(obj instanceof String)
                    cell.setCellValue((String)obj);
                else if(obj instanceof Integer)
                    cell.setCellValue((Integer)obj);
            }
        }

        return workbook;
    }



}

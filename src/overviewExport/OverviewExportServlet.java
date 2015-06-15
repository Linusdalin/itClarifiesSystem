package overviewExport;

import analysis.ParseFeedback;
import com.google.apphosting.api.DeadlineExceededException;
import contractManagement.Project;
import dataRepresentation.DBTimeStamp;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pukkaBO.condition.LookupByKey;
import pukkaBO.condition.LookupItem;
import pukkaBO.condition.ReferenceFilter;
import pukkaBO.exceptions.BackOfficeException;
import queue.AsynchAnalysis;
import services.Formatter;
import services.ItClarifiesService;
import userManagement.PortalUser;

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

    // The active .xlsx template
    private static final String TemplateFile = "exportTemplates/contracting framework template v2.xlsx";
    private static final int templateSheetIx = 6;


    /*************************************************************************
     *
     *
     *
     * @throws java.io.IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {


        try{
            logRequest(req);

            sessionManagement.allowBOAccess();

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


           XSSFWorkbook overview = getExcelTemplate();
           OverviewGenerator populator = new OverviewGenerator(overview, project, templateSheetIx );
           ParseFeedback feedback = populator.get();
           //ParseFeedback feedback = new ParseFeedback();

           // Hide the template sheet. This should not be used displayed in the final document
           overview.removeSheetAt( templateSheetIx );



           //OutputStream os = new WriterOutputStream(resp.getWriter());
           //PrintStream ps = new PrintStream(os);

           //TODO: This information should be passed to the user. For now we are just logging

           PukkaLogger.log(PukkaLogger.Level.INFO, feedback.toJSON().toString());

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


    /***********************************************************************
     *
     *          Post will trigger the export overview.
     *
     *          It will take all the parameters for the generation
     *
     *
     * @param req
     * @param resp
     * @throws IOException
     */


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        Project project = null;
        PortalUser user = null;
        String exportTags = "[]";
        String comment = "";

        try{
            logRequest(req);

            sessionManagement.allowBOAccess();

            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);

            Formatter formatter = getFormatFromParameters(req);

            DBKeyInterface _project         = getMandatoryKey("project", req);
            comment                         = getOptionalString("comment", req, "");
            exportTags                      = getOptionalString("tags", req, "[]");

            project = new Project(new LookupByKey(_project));

            if(!mandatoryObjectExists(project, resp))
                return;

            user = sessionManagement.getUser();

            // Queue the event

            AsynchAnalysis queue = new AsynchAnalysis(sessionManagement.getToken());
            queue.generateOverview(project);

            JSONObject response =  new JSONObject().put(DataServletName, "Queued");
            sendJSONResponse(response, formatter, resp);

        } catch ( DeadlineExceededException e) {

            PukkaLogger.log(e);
            markAsFail(project);

            returnError("Could not complete the overview generation.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);


        } catch ( BackOfficeException e) {

            PukkaLogger.log(e);
            markAsFail(project);

            returnError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        } catch ( Exception e) {

            PukkaLogger.log(e);
            markAsFail(project);

            returnError("Internal Error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
        }
     }

    private void markAsFail(Project project) {

            // If there is a status for the project, set it to failed

            try {

                ExtractionStatus statusForProject = null;

                if(project != null)
                    statusForProject = new ExtractionStatus(new LookupItem().addFilter(new ReferenceFilter(ExtractionStatusTable.Columns.Project.name(), project.getKey())));

                if(statusForProject != null && statusForProject.exists()){

                    DBTimeStamp failTime = new DBTimeStamp();
                    statusForProject.setStatus(ExtractionState.getFailed());
                    statusForProject.setDate( failTime );
                    statusForProject.update();
                }

            } catch (BackOfficeException e) {

                PukkaLogger.log( e );
            }

    }


    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

     }

    private XSSFWorkbook getExcelTemplate() throws BackOfficeException {

        File templateFile = new File(TemplateFile);
        FileInputStream templateStream ;
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

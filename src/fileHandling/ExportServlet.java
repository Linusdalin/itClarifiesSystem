package fileHandling;

import contractManagement.Contract;
import contractManagement.ContractVersionInstance;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import pukkaBO.condition.LookupByKey;
import pukkaBO.exceptions.BackOfficeException;
import services.Formatter;
import services.ItClarifiesService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/********************************************************
 *
 *                  Exporting one document with or without
 *                  generated annotations
 *
 */

public class ExportServlet extends ItClarifiesService{

    public static final String DataServletName = "Export";


    /*************************************************************************
     *
     *          Get a document matching the request criteria
     *
     *          Parameters:
     *
     *              &document=<key>
     *              &inject=<bool>     inject comments/annotations etc.
     *
     *
     *
     *          //TODO: Pass a list of #tags to filter the annotation
     *
     * @throws java.io.IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

       DBKeyInterface _document;

       try{
           logRequest(req);

           sessionManagement.allowBOAccess();  // For internal access

           if(!validateSession(req, resp))
               return;

           if(blockedSmokey(sessionManagement, resp))
               return;


           setLoggerByParameters(req);

           Formatter formatter = getFormatFromParameters(req);
           formatter.setFormat(Formatter.OutputFormat.DOCX);

           _document         = getMandatoryKey("document", req);
           Contract document = new Contract(new LookupByKey(_document));

           String filter     = getOptionalString("filter", req, "[]");

           if(!mandatoryObjectExists(document, resp))
               return;

           boolean inject         = getOptionalBoolean("inject", req);

           RepositoryInterface repository = new BlobRepository();

           ContractVersionInstance version = document.getHeadVersion();
           RepositoryFileHandler fileHandler = new RepositoryFileHandler(document.getFile());
           String filename = document.getFile();

           if(!repository.existsFile(fileHandler))
               returnError("File " + version.getVersion() + " does not exist as a file on the server", HttpServletResponse.SC_BAD_REQUEST, resp);



           if(inject){

               Exporter exporter = new Exporter();
               DocXExport exportFile = exporter.enhanceFile(version, filter);
               filename = exporter.getOutputName(fileHandler.getFileName());
               fileHandler = exportFile.saveToRepository(filename);

           }

           repository.serveFile(fileHandler, resp);

           resp.setContentType(formatter.getContentType());

           resp.setHeader("Content-Disposition","inline;filename=\"" + filename + "\"");
           resp.setHeader("Access-Control-Allow-Origin", "*");
           resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
           resp.setHeader("Access-Control-Max-Age", "" + (0));
           resp.setHeader("Access-Control-Allow-Headers", "x-requested-with");
           resp.setHeader("Access-Control-Allow-Headers", "content-type");

           //resp.setCharacterEncoding("UTF-8");

        } catch (IOException e) {

           PukkaLogger.log( e );

        }catch(BackOfficeException e){

           returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
           e.printStackTrace();

        } catch ( Exception e) {

           returnError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
           e.printStackTrace();
        }
    }



    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        doGet(req, resp);

     }


    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

     }




}

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
 *          Project Servlet will return one or a list of projects.
 *
 */

public class ExportServlet extends ItClarifiesService{

    public static final String DataServletName = "Export";


    /*************************************************************************
     *
     *          Get projects matching the request criteria
     *
     *          Parameters:
     *
     *          &key=<key> (if left empty, it will return the entire list)
     *
     *
     * @throws java.io.IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

       DBKeyInterface _document;

       try{
           logRequest(req);

           if(!validateSession(req, resp))
               return;

           if(blockedSmokey(sessionManagement, resp))
               return;


           setLoggerByParameters(req);

           Formatter formatter = getFormatFromParameters(req);
           formatter.setFormat(Formatter.OutputFormat.DOCX);

           _document         = getMandatoryKey("document", req);
           Contract document = new Contract(new LookupByKey(_document));

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
               DocXFile exportFile = exporter.getFileToExport(version);
               fileHandler = exportFile.saveToRepository(document.getName());
               filename = exporter.getOutputName(fileHandler.getFileName());

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



    /***********************************************************************
     *
     *          Delete a project with recursively deleting all documents, fragments etc.
     *
     *
     * @param req -
     * @param resp -
     * @throws java.io.IOException
     *
     *          //TODO: Delete project should recursively delete all documents
     *          //TODO: Fix return values correctly
     *
     */

    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Post not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

     }


    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

     }




}

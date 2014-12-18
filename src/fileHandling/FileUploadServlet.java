package fileHandling;

import contractManagement.*;
import dataRepresentation.DBTimeStamp;
import databaseLayer.DatabaseAbstractionFactory;
import language.LanguageCode;
import log.PukkaLogger;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.POIXMLException;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupByKey;
import pukkaBO.condition.LookupItem;
import pukkaBO.exceptions.BackOfficeException;
import queue.AsynchAnalysis;
import services.DocumentService;
import services.Formatter;
import userManagement.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;

/***************************************************************************************
 *
 *          Servlet to upload a document of type .docx
 *
 *          It will be stored in the blob store
 *
 *
 *          // TODO: Add more document types and detect type by extension
 *          // TODO: The order of the parameters should not matter
 *
 */


public class FileUploadServlet extends DocumentService {

    public static final String DataServletName = "Document";

    /******************************************************
     *
     *      Uploading a file and parsing the result
     *
     *      See also uploadTest.jsp
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
     *
     *
     *          //TODO: ACS: Only upload by user in the same organization
     */



    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        int maxFileSize = 5000 * 1024;
        int maxMemSize = 5000 * 1024;

        System.out.println("In file upload");

        try{
            logRequest(req);

            String sessionToken = "";

            Project project = null;
            PortalUser portalUser = null;
            Contract document = null;
            String title = null;

            String _project = null;
            String _document = null;

            String ipAddress = getIPAddress(req);


            JSONObject json = new JSONObject();


            // Default values

            Visibility visibility = Visibility.getOrg();
            AccessRight accessRight = AccessRight.getrwd();

            Formatter formatter = getFormatFromParameters(req);


            // Verify the content type
            String contentType = req.getContentType();

            if((contentType.contains("multipart/form-data"))) {

                DiskFileItemFactory factory = new DiskFileItemFactory();

                // maximum size that will be stored in memory
                factory.setSizeThreshold(maxMemSize);

                // Location to save data that is larger than maxMemSize.
                factory.setRepository(new File("c:\\temp"));

                PukkaLogger.log(PukkaLogger.Level.INFO, "Upload");


                // Create a new file upload handler
                ServletFileUpload upload = new ServletFileUpload(factory);
                // maximum file size to be uploaded.
                upload.setSizeMax( maxFileSize );

                // Parse the request to get file items.
                List fileItems = upload.parseRequest(req);

                // Process the uploaded file items
                Iterator i = fileItems.iterator();

                PukkaLogger.log(PukkaLogger.Level.INFO, "Parsing form ");


                while ( i.hasNext () ){

                    FileItem fi = (FileItem)i.next();

                    //TODO: Make these mandatory

                    if ( fi.isFormField () ){

                        if(fi.getFieldName().equals("session")){

                            sessionToken = new String(fi.get());
                            if(!sessionManagement.validate(sessionToken, ipAddress)){

                                returnError("No Session", ErrorType.SESSION, HttpServletResponse.SC_FORBIDDEN, resp);
                                return;
                            }

                        }

                        if(fi.getFieldName().equals("html")){

                            formatter = formatter.htmlEncode(true);

                            // TODO: Improvement: check on/off here too. now html=off means true
                        }


                        // If we set a visibility, it has to be correct.

                        if(fi.getFieldName().equals("visibility")){

                            visibility = new Visibility(new LookupItem().addFilter(new ColumnFilter(VisibilityTable.Columns.Name.name(), new String(fi.get()))));

                            if(!mandatoryObjectExists(visibility, resp))
                                return;

                            System.out.println("**** Got Parameter Visibility " + visibility.getName() );

                        }

                        if(fi.getFieldName().equals("access")){

                            accessRight = new AccessRight(new LookupItem().addFilter(new ColumnFilter(AccessRightTable.Columns.Name.name(), new String(fi.get()))));

                            if(!mandatoryObjectExists(accessRight, resp))
                                return;

                        }

                        if(fi.getFieldName().equals("project")){

                            _project = new String(fi.get());
                            PukkaLogger.log(PukkaLogger.Level.INFO, "Project key: " + _project);
                            project = new Project(new LookupByKey( new DatabaseAbstractionFactory().createKey(_project)));

                            if(!mandatoryObjectExists(project, resp))
                                return;

                        }

                        if(fi.getFieldName().equals("title")){

                            // Title is sent URL-encoded according to the API spec

                            title = new String(fi.get());
                            //title = URLDecoder.decode(title, "ISO-8859-1");
                            title = URLDecoder.decode(title, "UTF-8");

                            PukkaLogger.log(PukkaLogger.Level.INFO, "Title: " + title);

                        }


                        if(fi.getFieldName().equals("document")){

                            _document = new String(fi.get());
                            PukkaLogger.log(PukkaLogger.Level.INFO, "Document key: " + _document);
                            document = new Contract(new LookupByKey( new DatabaseAbstractionFactory().createKey(_document)));

                            if(!mandatoryObjectExists(document, resp))
                                return;

                        }


                    }
                     else{



                        portalUser = sessionManagement.getUser();

                        //TODO; This should be more relaxed. Within a team more than the owner must be able to contribute. Fix this with team access rights

                        if(!portalUser.isSame(project.getCreator())){

                            returnError("Not sufficient access to upload document. Please contact project owner", ErrorType.PERMISSION, HttpServletResponse.SC_FORBIDDEN, resp);
                            return;
                        }





                        PukkaLogger.log(PukkaLogger.Level.INFO, "Found user " + portalUser.getName());


                        // If there is no explicit title given, we use the document name as title

                        //String fileName = new String (fi.getName().getBytes ("iso-8859-1"), "UTF-8");
                        String fileName = new String (fi.getName().getBytes ("UTF-8"), "UTF-8");

                        if(title == null || title.equals(""))
                            title = fileName;

                        boolean isInMemory = fi.isInMemory();
                        long sizeInBytes = fi.getSize();
                        String fieldName = fi.getFieldName();

                        InputStream stream = fi.getInputStream();

                        ContractVersionInstance oldVersion = null;

                        if(document != null)
                            oldVersion = document.getHeadVersion();

                        DBTimeStamp uploadTime = new DBTimeStamp();

                        RepositoryInterface repository = new BlobRepository();
                        RepositoryFileHandler fileHandler = repository.saveFile(fileName, stream);


                        ContractVersionInstance newVersion = handleUpload(title, fileHandler, document, project, portalUser, accessRight, visibility);

                        // Document is uploaded. Update the status

                        document = newVersion.getDocument();

                        document.setMessage("Uploaded. Parsing Document...");
                        document.setStatus(ContractStatus.getUploaded().getKey());
                        document.update();

                        //System.out.println("Document: " + document.getKey().toString());


                        // Start the asynchronous parsing and analysis

                        AsynchAnalysis analysisQueue = new AsynchAnalysis(sessionToken);
                        analysisQueue.analyseDocument(newVersion, oldVersion);

                        json.put("uploaded", newVersion.getDocument().getKey().toString());

                    }

                 }

                // Clear cache for project and document list

                invalidateDocumentCache(document, project);

                sendJSONResponse(json, formatter, resp);



           }else{

                returnError("Should be multipart/form-data", HttpServletResponse.SC_BAD_REQUEST, resp);

           }


        }catch(POIXMLException ex) {

            //TODO: Detect the document format better and give a better error message here

            returnError("Could not parse document. Document format not supported. (" + ex.getMessage() + ")", HttpServletResponse.SC_BAD_REQUEST, resp);



        }catch(BackOfficeException e) {

            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        } catch (FileUploadException e) {

            returnError("Could not upload document. " + e.getMessage(), HttpServletResponse.SC_BAD_REQUEST, resp);

        } catch (IllegalArgumentException e) {

          returnError("Could not upload document. Project not found " + e.getMessage(), HttpServletResponse.SC_BAD_REQUEST, resp);
      }

    }



    /******************************************************************************************************************
     *
     *
     * @param title
     * @param fileHandler
     * @param document
     * @param project
     * @param portalUser
     * @param accessRight
     * @param visibility
     * @throws pukkaBO.exceptions.BackOfficeException
     */


    public ContractVersionInstance handleUpload(String title, RepositoryFileHandler fileHandler, Contract document, Project project, PortalUser portalUser, AccessRight accessRight, Visibility visibility) throws BackOfficeException{

        ContractVersionInstance version;

        if(document == null){

            PukkaLogger.log(PukkaLogger.Level.INFO, "Creating a new document in db: " + title);

            LanguageCode languageCode = new LanguageCode("unknown");
            version = new ContractTable().addNewDocument(project, title, fileHandler, languageCode, portalUser, accessRight, visibility);



        }
        else{

            ContractVersionInstance oldVersion = document.getHeadVersion();
            project = oldVersion.getDocument().getProject();

            PukkaLogger.log(PukkaLogger.Level.INFO, "Adding new version for existing document " + document.getName());
            version = document.addNewVersion(portalUser, fileHandler);
            PukkaLogger.log(PukkaLogger.Level.INFO, "Fragmenting");

        }

        PukkaLogger.log(PukkaLogger.Level.INFO, "Done creating document and version");
        invalidateDocumentCache(version.getDocument(), project);
        invalidateFragmentCache(version);

        return version;

    }


    /*************************************************************************
     *
     *
     * @throws java.io.IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {


        returnError("Get not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();


    }


    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

    }



}

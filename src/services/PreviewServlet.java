package services;


import backend.ItClarifies;
import com.google.appengine.api.datastore.KeyFactory;
import contractManagement.*;
import databaseLayer.AppEngine.AppEngineKey;
import databaseLayer.DBKeyInterface;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.POIXMLException;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;
import userManagement.PortalUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/***************************************************************************************
 *
 *          Servlet to upload a document of type .docx and preview the outcome
 *
 *          This is mostly an in-house tool for the back office
 *
 */


public class PreviewServlet extends DocumentService{

    public static final String DataServletName = "Preview";

    public static final BackOfficeInterface backOffice = new ItClarifies();


    /******************************************************
     *
     *      Uploading a file and parsing the result
     *
     *      See also uploadTest.jsp
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
     */



    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {


        int maxFileSize = 5000 * 1024;
        int maxMemSize = 5000 * 1024;

        String text = "";

        try{

            logRequest(req);

            /*
            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;



            setLoggerByParameters(req);

            */

            Formatter formatter = getFormatFromParameters(req);

            String sessionToken;
            PortalUser portalUser = null;


            Formatter jsonExport = new Formatter()
                     .setFormat(Formatter.OutputFormat.HTML);


            // Verify the content type
            String contentType = req.getContentType();

            if((contentType.contains("multipart/form-data"))) {

                DiskFileItemFactory factory = new DiskFileItemFactory();

                // maximum size that will be stored in memory
                factory.setSizeThreshold(maxMemSize);

                // Location to save data that is larger than maxMemSize.
                factory.setRepository(new File("c:\\temp"));


                // Create a new file upload handler
                ServletFileUpload upload = new ServletFileUpload(factory);
                // maximum file size to be uploaded.
                upload.setSizeMax( maxFileSize );

                // Parse the request to get file items.
                List fileItems = upload.parseRequest(req);

                // Process the uploaded file items
                Iterator i = fileItems.iterator();


                while ( i.hasNext () ){

                    FileItem fi = (FileItem)i.next();

                    //TODO: Make these mandatory

                    if ( fi.isFormField () ){

                        /*

                        if(fi.getFieldName().equals("session")){

                            sessionToken = new String(fi.get());
                            if(!sessionManagement.validate(sessionToken)){

                                returnError("No Session", ErrorType.SESSION, HttpServletResponse.SC_FORBIDDEN, resp);
                                resp.flushBuffer();
                                return;
                            }
                            portalUser = sessionManagement.getUser();
                            PukkaLogger.log(PukkaLogger.Level.INFO, "Found user " + portalUser.getName());

                        }

                        */

                        if(fi.getFieldName().equals("project")){

                            String _project = new String(fi.get());
                            Project project = new Project(new LookupByKey( new AppEngineKey(KeyFactory.stringToKey(_project))));

                            if(!project.exists()){

                                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                                resp.getWriter().print("Project with id: " + _project + " does not exist");
                                resp.flushBuffer();
                                return;
                            }

                        }

                    }

                 }


                setRespHeaders(jsonExport, resp);
                resp.getWriter().print(text);
                resp.setContentType(jsonExport.getContentType());
                resp.flushBuffer();
                return;


           }else{
               resp.getWriter().print("<html><head><title>Servlet upload</title></head><body><p>No file uploaded</p> </body></html>");

           }

          }catch(POIXMLException ex) {

              resp.getWriter().print("<html><head><title>Servlet upload</title></head><body><p>This was not a docx file!</p> </body></html>");

          }catch(BackOfficeException e) {

                e.logError("In Upload:");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().print(e.narration);
                resp.flushBuffer();
                return;

          } catch (FileUploadException e) {

                e.printStackTrace();
        }
    }


    /*************************************************************************
     *
     *          GET will return a document from the database
     *
     * @throws java.io.IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        try {

            logRequest(req);

            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);


            Formatter formatter = getFormatFromParameters(req);

            Formatter jsonExport = new Formatter()
                     .setFormat(Formatter.OutputFormat.HTML);

            if(!validateSession(req, resp))
                return;


            DBKeyInterface _document = getMandatoryKey("document", req);

            Contract contract = new Contract(new LookupByKey(_document));

            if(!mandatoryObjectExists(contract, resp))
                return;

            String text = contract.getInternalView(true);

            setRespHeaders(jsonExport, resp);
            resp.getWriter().print(text);
            resp.setContentType(jsonExport.getContentType());
            setRespHeaders(jsonExport, resp);
            resp.flushBuffer();
            return;




        } catch (BackOfficeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

            returnError("Error in get Preview", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            resp.flushBuffer();
        }


    }


    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in Preview", HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

    }


}

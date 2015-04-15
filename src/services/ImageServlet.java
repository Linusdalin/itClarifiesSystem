package services;

import contractManagement.*;
import dataRepresentation.DBTimeStamp;
import databaseLayer.DBKeyInterface;
import document.AbstractImage;
import httpRequest.ServerFactory;
import log.PukkaLogger;
import net.sf.json.JSONObject;
import org.apache.xmlgraphics.image.loader.impl.DefaultImageContext;
import pukkaBO.condition.LookupByKey;
import pukkaBO.exceptions.BackOfficeException;
import userManagement.PortalUser;
import userManagement.SessionManagement;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/********************************************************
 *
 *          Image servlet
 *
 */

public class ImageServlet extends ItClarifiesService{

    public static final String DataServletName = "Image";

    public static final String DefaultImage = "default/itclarifies.png";


    /*****************************************************************************
     *
     *
     *      This will work slightly different on localhost and deployed to a server
     *
     *          For localhost, all images are served in the allDocuments directory. (To facilitate uploading static images)
     *          For stage and live images are stored in a directory named after the document key
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
     *
     *
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        if(req.getParameter("_method") != null && req.getParameter("_method").equals("DELETE")){
            doDelete(req, resp);
            return;
        }

        ServletContext sc = getServletContext();

        try{

            logRequest(req);

            String imageFileName;

            // This is a special case of the validate session functionality.
            //   -  If we fail the validation, we still want to serve a default image
            //   -  If there is no read access to the document we also serve a default image.
            //      This is a bit more serious as it could be a sign on hacking

            String sessionToken  = getMandatoryString("session", req);


            if(!sessionManagement.validate(sessionToken, SessionManagement.AnyIP)){

                PukkaLogger.log(PukkaLogger.Level.INFO, "Access Error. Could not validate session "+ sessionToken+" for user. Serving default image");
                imageFileName = DefaultImage;
            }
            else{

                DBKeyInterface _document    = getMandatoryKey("document", req);
                //ContractVersionInstance version = new ContractVersionInstance(new LookupByKey(_document));
                Contract document = new Contract(new LookupByKey(_document));

                //Changed this to document from version. Client side only has the document key


                //if(!version.exists()){
                if(!document.exists()){

                    PukkaLogger.log(PukkaLogger.Level.WARNING, "Access Error. Document version with key "+ _document + " does not exist");
                    imageFileName = DefaultImage;

                }
                else {

                    //Contract document = version.getDocument();
                    if(!sessionManagement.getReadAccess(document)){

                        PukkaLogger.log(PukkaLogger.Level.WARNING, "Access Error. No read access to document "+ document.getName()+" for user. Serving default image");
                        imageFileName = DefaultImage;

                    }else{

                        String imageName            = getMandatoryString("image", req);
                        AbstractImage img = new AbstractImage(imageName);
                        String imageServer = ServerFactory.getLocalSystem();
                        imageFileName = img.getImageFile(imageServer, _document.toString());
                    }
                }

            }

            setLoggerByParameters(req);

            PukkaLogger.log(PukkaLogger.Level.INFO, "Accessing image \"" + imageFileName + "\" from file");


            // Get the MIME type of the image
            String mimeType = getMimeType(imageFileName);

            if (mimeType == null) {

                PukkaLogger.log(PukkaLogger.Level.ERROR, "Could not get MIME type of "+imageFileName);
                imageFileName = DefaultImage;
                mimeType = sc.getMimeType(imageFileName);
            }

            File file = new File(imageFileName);


            // Set content size
            resp.setContentLength((int)file.length());

            FileInputStream inputStream;

            try{

                inputStream = new FileInputStream(file);

            }catch(FileNotFoundException e ){

                PukkaLogger.log(PukkaLogger.Level.ERROR, "Could not find file "+imageFileName + " using default image...");
                imageFileName = AbstractImage.ImageDirectory +  DefaultImage;
                mimeType = sc.getMimeType(imageFileName);
                file = new File(imageFileName);
                inputStream = new FileInputStream(file);


            }

            // Set content type
            resp.setContentType(mimeType);


            serve(inputStream, resp);

            inputStream.close();

            setRespHeaders(resp, 24*60);
            resp.flushBuffer();


        }catch(BackOfficeException e){

            PukkaLogger.log(e);
            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        } catch ( Exception e) {

            PukkaLogger.log(e);
            returnError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        }

     }

    private void serve(FileInputStream in, HttpServletResponse resp) throws IOException{

        //Create an output stream to the HTTP response
        OutputStream out = resp.getOutputStream();

        // Copy the contents of the file to the output stream
        byte[] buf = new byte[1024];
        int count = 0;
        while ((count = in.read(buf)) >= 0) {
            out.write(buf, 0, count);
        }
        out.close();

    }


    /*************************************************************************
     *
     *          GET is not implemented
     *
     * @throws java.io.IOException
     */


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        if(req.getParameter("_method") != null && req.getParameter("_method").equals("DELETE")){
            doDelete(req, resp);
            return;
        }


        returnError("Post not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

    }


    /**********************************************************************
     *
     *      Deleting not implemented
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
     *
     *
     *
     */

    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " +DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

    }


    /*******************************************************************************
     *
     *          Detect the mime type from the file
     *
     *
     * @param fileName          - the file to analyse
     * @return
     *
     *          Either from the ServletContext or overriding with special cases
     */


    private String getMimeType(String fileName){

        ServletContext sc = getServletContext();
        String mimeType = sc.getMimeType(fileName);

        if(mimeType != null)
            return mimeType;

        // Special case, not supported by ServletContext

        if(fileName.endsWith(".emf"))
            return("application/emf");

        return null;

    }

}

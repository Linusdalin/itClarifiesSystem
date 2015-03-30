package services;

import contractManagement.*;
import dataRepresentation.DBTimeStamp;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import net.sf.json.JSONObject;
import pukkaBO.condition.LookupByKey;

import pukkaBO.exceptions.BackOfficeException;
import reclassification.Reannotation;
import userManagement.PortalUser;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/********************************************************
 *
 *          Contract Servlet returning one contract
 *
 */

public class AnnotationServlet extends ItClarifiesService{

    public static final String DataServletName = "Annotation";

    /*****************************************************************************
     *
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
     *
     *
     */


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        try{

            logRequest(req);

            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);


            Formatter formatter = getFormatFromParameters(req);


            DBKeyInterface _annotation       = getOptionalKey("annotation", req);
            String annotationBody            = getMandatoryString("body", req);

            PortalUser creator = sessionManagement.getUser();

            String pattern = getOptionalString("pattern", req, "");

            PukkaLogger.log(PukkaLogger.Level.INFO, "Got pattern: \"" + pattern + "\" + req: " + req.getParameterMap());

            ContractAnnotation annotation;
            Contract document;
            ContractVersionInstance version;

            if(_annotation == null){

                // No annotation given. This is a new annotation

                DBKeyInterface _fragment         = getMandatoryKey("fragment", req);
                ContractFragment fragment = new ContractFragment(new LookupByKey(_fragment));

                version = fragment.getVersion();
                document = version.getDocument();
                DBTimeStamp now = new DBTimeStamp();

                if(!modifiable(document, resp))
                    return;

                // Give it a number that is higher than the count. This will not be ordered
                // when annotations are deleted. Consider getting max value from data set

                long annotationNumber = fragment.getAnnotationCount() + 1;

                // Store the annotation
                // TODO: Add transaction here

                annotation = new ContractAnnotation(
                        creator.getName() + "@" + now.getSQLTime().toString(),
                        fragment,
                        annotationNumber,
                        annotationBody,
                        creator,
                        version,
                        pattern,
                        0,                          //TODO: Anchor position not implemented
                        now.getSQLTime().toString());
                annotation.store();

                // Now update the fragment annotation count

                fragment.setAnnotationCount(getAnnotationCount(fragment));
                fragment.update();

                // Store a log entry

                Reannotation logEntry = new Reannotation(annotationBody, true, now.getISODate(), document.getProject().getName(),
                        document.getName(), fragment.getOrdinal(), fragment.getText(), pattern, 0, creator.getName(),  false);

                logEntry.store();


            }
            else{

                // Annotation is given. Read back from database and update with new description

                annotation = new ContractAnnotation(new LookupByKey((_annotation)));

                if(!mandatoryObjectExists(annotation, resp))
                    return;

                version = annotation.getFragment().getVersion();
                document = version.getDocument();

                if(!modifiable(document, resp))
                    return;


                PukkaLogger.log(PukkaLogger.Level.INFO, "Updating annotation " + _annotation.toString() + " in document " + document.getName());
                annotation.setDescription(annotationBody);
                annotation.update();

            }

            //Clear the fragment cache

            PukkaLogger.log(PukkaLogger.Level.INFO, "Clearing cache for document " + document.getName() + " after adding annotations");
            invalidateFragmentCache(annotation.getVersion());


            JSONObject json = createPostResponse(DataServletName, annotation);
            sendJSONResponse(json, formatter, resp);


        }catch(BackOfficeException e){

            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            e.printStackTrace();

        } catch ( Exception e) {

            returnError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            e.printStackTrace();

        }

     }


    /*************************************************************************
     *
     *          GET is not implemented
     *
     * @throws java.io.IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        if(req.getParameter("_method") != null && req.getParameter("_method").equals("DELETE")){
            doDelete(req, resp);
            return;
        }


        returnError("Get not supported in Annotation", HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

    }


    /**********************************************************************
     *
     *      Deleting an annotation
     *
     * @param req
     * @param resp
     * @throws IOException
     *
     *
     *
     */

    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        try{

            logRequest(req);

            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);

            DBKeyInterface _annotation       = getMandatoryKey("annotation", req);
            ContractAnnotation annotation = new ContractAnnotation(new LookupByKey(_annotation));

            if(!mandatoryObjectExists(annotation, resp))
                return;

            Contract contract = annotation.getFragment().getVersion().getDocument();

            if(!deletable(contract, resp))
                return;

            Formatter formatter = getFormatFromParameters(req);
            DBTimeStamp now = new DBTimeStamp();

            // Update the annotation count in the fragment

            ContractFragment fragment = annotation.getFragment();
            ContractVersionInstance version = annotation.getVersion();
            Contract document = version.getDocument();
            fragment.setAnnotationCount(getAnnotationCount(fragment) - 1);
            fragment.update();

            // Store a log entry

            Reannotation logEntry = new Reannotation(annotation.getDescription(), false, now.getISODate(), document.getProject().getName(),
                    document.getName(), fragment.getOrdinal(), fragment.getText(), annotation.getPattern(), annotation.getPatternPos(), sessionManagement.getUser().getName(),  false);

            logEntry.store();

            // Now delete

            new ContractAnnotationTable().deleteItem( annotation );

            //Clear the fragment cache

            PukkaLogger.log(PukkaLogger.Level.INFO, "Clearing cache for document " + document.getName() + " after deleting annotation");
            invalidateFragmentCache(annotation.getVersion());

            JSONObject result = createDeletedResponse(DataServletName, annotation);
            sendJSONResponse(result, formatter, resp);




        } catch (BackOfficeException e) {

            PukkaLogger.log( e );
            returnError("Error in " + DataServletName, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        } catch (Exception e) {

            PukkaLogger.log( e );
            returnError("Error in " + DataServletName, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
        }


    }


}

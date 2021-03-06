package services;

import actions.ChecklistItem;
import adminServices.GenericAdminServlet;
import cache.ServiceCache;
import contractManagement.*;
import dataRepresentation.DataObjectInterface;
import databaseLayer.DBKeyInterface;
import language.English;
import language.LanguageInterface;
import log.PukkaLogger;
import maintenance.Smokey;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import project.Project;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;
import userManagement.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

/**
 *          Base functionality for the services in the project
 *
 */

public class ItClarifiesService extends GenericAdminServlet {

    protected SessionManagement sessionManagement = new SessionManagement();
    public static String MODEL_DIRECTORY = "models";

    // Use english names of classifications as default
    // When implementing language support, this should be taken from the user settings

    protected static final LanguageInterface defaultLanguage = new English();


    /********************************************************************************
     *
     *          Init is run when the servlet is instantiated.
     *          //TODO: Add cache warm-up here
     *
     *
     * @param dataServletName
     */


    public void init(String dataServletName) {

        PukkaLogger.log(PukkaLogger.Level.INFO, "Warmup in servlet " + dataServletName);

    }


    public static enum ErrorType {

        PERMISSION,            // No permission to perform the operation
        SESSION,               // Session expired
        DATA,                  // Data object missing in the database
        MAINTENANCE, GENERAL                // Something else

    }

    /*************************************************************************************
     *
     *          Setting basic response headers for the services.
     *
     *          //TODO: Make this configurable
     *
     * @param jsonExport
     * @param response - CBV response object to be modified
     */


    protected void setRespHeaders(Formatter jsonExport, HttpServletResponse response) throws BackOfficeException {


        response.setContentType(jsonExport.getContentType());

        setRespHeaders(response, 0);

    }


    protected void setRespHeaders(HttpServletResponse response, int cacheMinutes) {

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "" + (cacheMinutes * 60));
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");

        response.setHeader("Access-Control-Allow-Headers", "content-type");
        response.setCharacterEncoding("UTF-8");

    }



    public static String encodeToJSON(String data){

        if(data == null)
                return null;

        //data = data.replaceAll("\n", "\\n");
        data = data.replace("[", "(");
        data = data.replaceAll("]", ")");

        // This is a sneak variant of tabs, just adding a span for the text.

        data = data.replaceAll("^(.*)?\t\\{(.*?)\\}", "<span style=\"display: inline-block; width: $2px;\">$1</span>");
        //data = data.replaceAll("^(.*)\t", "<span style=\"display: inline-block; width: 160px;\">$1</span>");
        data = data.replaceAll("\t", "&nbsp; &nbsp; ");

        return data;

    }


    /****************************************************************************
     *
     *      Generic options method
     *
     * @param req -
     * @param resp -
     * @throws IOException
     */

    public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        setRespHeaders(resp, 24*60);
        //Tell the browser what requests we allow.

        resp.setHeader("Allow", "GET, HEAD, POST, TRACE, OPTIONS");
    }

    /***********************************************************************
     *
     *          Create an error message and package it correctly
     *
     * @param message   the error message
     * @param httpError error code
     * @param resp      response object to set the correct headers
     * @throws IOException
     */

    protected void returnError(String message, int httpError, HttpServletResponse resp) throws IOException {

        returnError(message, ErrorType.GENERAL, httpError, resp);
    }

    protected void returnError(String message, ErrorType errorCode, int httpError, HttpServletResponse resp) throws IOException {

        returnError(message, errorCode, httpError, PukkaLogger.Level.INFO, resp);

    }

    protected void returnError(String message, ErrorType errorCode, int httpError, PukkaLogger.Level logLevel, HttpServletResponse resp) throws IOException {

        PukkaLogger.log(logLevel, "Sending error(" + httpError + "): " + message);

        resp.getWriter().print(createJSONError(message, errorCode).toString());
        resp.setStatus(httpError);
        resp.setContentType("application/json");   // TODO: Not implemented other types of error formatting
        setRespHeaders(resp, 3600);
        resp.flushBuffer();

    }

    /************************************************************************''
     *
     *      Create an error message JSON
     *
     * @param message - text
     * @return JSON Object
     *
     *      The format allows for a list of errors. Now we are only using one value
     */

    private JSONObject createJSONError(String message, ErrorType type) {




        try {


            JSONObject error = new JSONObject()
                    .put("error", new JSONArray()
                            .put(new JSONObject()
                                    .put("type", type.name())
                                    .put("message", message))
                    );

            return error;

        } catch (JSONException e) {

            PukkaLogger.log(e);
            return new JSONObject();
        }

    }

    /******************************************************************************
     *
     *          Validate the session and block
     *
     * @param req -
     * @param resp -
     * @return  false if the session is not ok and we should return.
     * @throws BackOfficeException
     * @throws IOException
     *
     *          Sets the response message and headers
     *
     *          //TODO: Add finer granularity for the error messages here. (expired, user missing, lofgged out etc.)
     *
     */

    public boolean validateSession(HttpServletRequest req, HttpServletResponse resp) throws BackOfficeException, IOException {

        return validateSession(req, resp, HttpServletResponse.SC_FORBIDDEN);

    }


    /*************************************************************************************'
     *
     *                  Validate the session and get the active session user
     *
     *
     * @param req
     * @param resp
     * @param errorCode
     * @return
     * @throws BackOfficeException
     * @throws IOException
     */

    protected boolean validateSession(HttpServletRequest req, HttpServletResponse resp, int errorCode) throws BackOfficeException, IOException {

        String magicKey  = getOptionalString("magicKey", req);
        String ipAddress = getIPAddress(req);

        if(magicKey != null){

            if(!sessionManagement.validateMagicKey(magicKey, ipAddress)){

                PukkaLogger.log(PukkaLogger.Level.INFO, "Access Error. Could not validate internal session "+ magicKey+".");
                returnError("No session", ErrorType.SESSION, errorCode, resp);
                resp.flushBuffer();
                return false;
            }
            return true;
        }

        String sessionToken  = getMandatoryString("session", req);

        if(!sessionManagement.validate(sessionToken, ipAddress)){

            PukkaLogger.log(PukkaLogger.Level.INFO, "Access Error. Could not validate session "+ sessionToken+" for user");
            returnError("No session", ErrorType.SESSION, errorCode, resp);
            resp.flushBuffer();
            return false;
        }

        return true;

    }



    /****************************************************************************************
     *
     *              Smokey mode is a way to block all users while updating data in the database
     *
     *
     * @param sessionManagement - the session
     * @param resp - the response to send
     * @return
     */

    protected boolean blockedSmokey(SessionManagement sessionManagement, HttpServletResponse resp) throws IOException{


        try {

            String userName = sessionManagement.getUser().getName();

            if(Smokey.isInSmokey(userName)){

                returnError("System is under maintenance", ErrorType.MAINTENANCE, HttpServletResponse.SC_FORBIDDEN, resp);
                return true;
            }

            return Smokey.isInSmokey(userName);

        } catch (BackOfficeException e) {

            PukkaLogger.log(e);
            returnError("Cant access user", ErrorType.GENERAL, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            return true;
        }

    }


    /****************************************************************************************
     *
     * @param req
     *
     *
     */

    public void setLoggerByParameters(HttpServletRequest req) {

        String requestedLevel = req.getParameter("log");

        if(requestedLevel != null){

            if(requestedLevel.equals(PukkaLogger.Level.DEBUG.name())){
                PukkaLogger.setLogLevel(PukkaLogger.Level.DEBUG);
                return;
            }

            if(requestedLevel.equals(PukkaLogger.Level.INFO.name())){
                PukkaLogger.setLogLevel(PukkaLogger.Level.INFO);
                return;
            }

            if(requestedLevel.equals(PukkaLogger.Level.ACTION.name())){
                PukkaLogger.setLogLevel(PukkaLogger.Level.ACTION);
                return;
            }

            PukkaLogger.log(PukkaLogger.Level.WARNING, "Log level " + requestedLevel + " requested but ignored");

        }



    }





    //TODO: Refactor these three
    //TODO: Add a key/name as parameter to be able to print in the error message

    public boolean mandatoryObjectExists(DataObjectInterface object, HttpServletResponse resp) throws IOException {

            if(!object.exists()){

               // A object was given that was not found. This is an error

                returnError( object.getTable().getTableName() + " not found", ErrorType.DATA,  HttpServletResponse.SC_BAD_REQUEST, resp);
                resp.flushBuffer();
                return false;
            }

        try {


            if(object instanceof Contract && !sessionManagement.getReadAccess((Contract) object)){

               // A object was given that has no access

                PukkaLogger.log(PukkaLogger.Level.INFO, "Illegal attempt to access Document " + ((Contract) object).getName());
                returnError(object.getTable().getTableName() + " not found", ErrorType.PERMISSION,  HttpServletResponse.SC_FORBIDDEN, resp);
                resp.flushBuffer();
                return false;
            }

        } catch (BackOfficeException e) {

            PukkaLogger.log(e);
            returnError( "Access error for " + object.getTable().getTableName() + " - " + e.narration , HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            resp.flushBuffer();
            return false;
        }


        return true;
    }

    /**************************************************************************************
     *
     *          Permission checks to be able to perform modifications on the object
     *
     *
     * @param object - data object
     * @param resp   - response object to construct errors
     * @return
     * @throws IOException
     */


    public boolean modifiable(DataObjectInterface object, HttpServletResponse resp) throws IOException {

            if(!object.exists()){

               // A object was given that was not found. This is an error

                returnError( object.getTable().getTableName() + " not found",  ErrorType.DATA, HttpServletResponse.SC_BAD_REQUEST, resp);
                resp.flushBuffer();
                return false;
            }

        try {


            if(object instanceof Contract && !sessionManagement.getCommentAccess((Contract) object)){

               // A object was given that has no access

                PukkaLogger.log(PukkaLogger.Level.INFO, "Illegal attempt to access Document " + ((Contract) object).getName());
                returnError( object.getTable().getTableName() + " read only", ErrorType.PERMISSION, HttpServletResponse.SC_FORBIDDEN, resp);
                resp.flushBuffer();
                return false;
            }

        } catch (BackOfficeException e) {

            // Undefined access error. GENERAL error code

            returnError( "Access error for " + object.getTable().getTableName() , HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            resp.flushBuffer();
            return false;
        }


        return true;
    }

    /**********************************************************************************************
     *
     *          Check the access to a delete request on an object in the system and populate the
     *          response with the appropriate error message if not.
     *
     *
     * @param object      - object to check access to
     * @param resp        - response to the request
     * @return            - true if the request is ok, false to return error
     * @throws IOException
     */



    public boolean deletable(DataObjectInterface object, HttpServletResponse resp) throws IOException {

        if(!object.exists()){

           // A object was given that was not found. This is an error

            returnError( object.getTable().getTableName() + " not found", ErrorType.DATA, HttpServletResponse.SC_BAD_REQUEST, resp);
            resp.flushBuffer();
            return false;
        }

        try {


            if(object instanceof Contract && !sessionManagement.getRenameDeleteAccess((Contract) object)){

                Contract document = (Contract)object;

                PukkaLogger.log(PukkaLogger.Level.INFO, "Illegal attempt to access Document " + document.getName());
                returnError( "You do not have sufficient access right to delete the document " + document.getName() + " please contact the owner of the document.", ErrorType.PERMISSION, HttpServletResponse.SC_FORBIDDEN, resp);
                resp.flushBuffer();
                return false;
            }

            if(object instanceof Project && !sessionManagement.getUser().equals(((Project) object).getCreator())){

                Project project = (Project)object;

                PukkaLogger.log(PukkaLogger.Level.INFO, "Illegal attempt to access Project " + project.getName());
                returnError( "You do not have sufficient access right to delete the project " +project.getName() + " please contact the owner of the project.", ErrorType.PERMISSION, HttpServletResponse.SC_FORBIDDEN, resp);
                resp.flushBuffer();
                return false;
            }


        } catch (BackOfficeException e) {

            returnError( "Access error for " + object.getTable().getTableName() , HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            resp.flushBuffer();
            return false;
        }


        return true;
    }



    /******************************************************************************************
     *
     *          Get the last version of the document.
     *
     *          //TODO: Versions of documents not implemented. Always using the first one found
     *
     *
     * @param contract - versioned contract
     * @return -latest version
     * @throws BackOfficeException
     *

            Depricated. Use document.getHeadVersion()

    protected ContractVersionInstance getLastVersionFor(Contract contract) throws BackOfficeException{

        ContractVersionInstanceTable versions = new ContractVersionInstanceTable(new LookupList(ContractVersionInstanceTable.Columns.Creation.name(), Ordering.LAST)
                .addFilter(new ReferenceFilter(ContractClauseTable.Columns.Contract.name(), contract)));

        ContractVersionInstance lastVersion = (ContractVersionInstance)versions.getValues().get( 0 );

        PukkaLogger.log(PukkaLogger.Level.INFO, " -> \"Last\" version: " + lastVersion.getVersion());

        return lastVersion;

    }

     */


    /**************************************************************************************
     *
     *          Logging the actual raw request to make it easy to review and understand what happened.
     *
     * @param request - http request
     *
     */


    protected void logRequest(HttpServletRequest request) {

        Enumeration pars = request.getParameterNames();
        StringBuffer logString = new StringBuffer();
        while(pars != null && pars.hasMoreElements()){

            String parameterName = (String)pars.nextElement();
            logString.append( parameterName + " - "+(request.getParameter(parameterName)).toString() + " ");
        }

        PukkaLogger.log(PukkaLogger.Level.ACTION, request.getMethod() + "-request " + request.getRequestURI() + " Parameters: " + logString);
    }


    /*********************************************************************************************
     *
     *      Getting the fragments as a textArray
     *
     *
     * @param fragmentsForVersion
     * @return
     *
     *
     *          //TODO: This is copied in two places. Use only this
     */


    protected String[] asTextArray(List<ContractFragment> fragmentsForVersion) {

        String[] fragmentBodyArray = new String[fragmentsForVersion.size()];

        int i = 0;
        for(ContractFragment fragment : fragmentsForVersion){

            fragmentBodyArray[i++] = fragment.getText();
        }

        return fragmentBodyArray;
    }


    /***************************************************************************
     *
     *          Common method to construct and send a JSON response
     *
     * @param json - the JSON object
     * @param formatter - the formatter object to pass formatting information
     * @param resp - the response object
     */

    protected void sendJSONResponse(JSONObject json, Formatter formatter, HttpServletResponse resp) {


        try {
            setRespHeaders(formatter, resp);
            resp.getWriter().print(formatter.formatJSON(json));
            resp.flushBuffer();

            PukkaLogger.log(PukkaLogger.Level.DEBUG, "Sending " + json.toString());

        } catch (BackOfficeException e) {

            e.logError(e.narration);

        } catch (IOException e) {

            PukkaLogger.log( e );
        }

    }

    protected void sendCSVResponse(String csvFile, Formatter formatter, HttpServletResponse resp) {

        try {
            setRespHeaders(formatter, resp);
            resp.getWriter().print(formatter.formatCSV(csvFile));
            resp.flushBuffer();

            PukkaLogger.log(PukkaLogger.Level.INFO, "Sending " + csvFile);

        } catch (BackOfficeException e) {

            e.logError(e.narration);

        } catch (IOException e) {

            PukkaLogger.log( e );
        }

    }


    protected Formatter getFormatFromParameters(HttpServletRequest req) {

        Formatter formatter = new Formatter();

        try {

            formatter.htmlEncode(getOptionalBoolean("html", req));
            formatter.prettyPrint(getOptionalBoolean("pp", req));

        } catch (BackOfficeException e) {

            PukkaLogger.log(PukkaLogger.Level.WARNING, "Could not set formatting for request " + req.getMethod() + "/" + req.getRequestURI());
        }

        return formatter;

    }


    protected JSONObject createDeletedResponse(String service, DataObjectInterface deletedObject) {

        PukkaLogger.log(PukkaLogger.Level.ACTION, "Deleted object " + deletedObject.getKey() + " in " + service );

        return new JSONObject().put(service, "DELETED");

    }



    protected JSONObject createPostResponse(String service, DataObjectInterface updatedObject) {

        PukkaLogger.log(PukkaLogger.Level.ACTION, "Created/Updated object " + updatedObject.getKey() + " in " + service );

        return new JSONObject().put(service, updatedObject.getKey().toString() );
    }




    /*************************************************************************
     *
     *      both the document for project and document itself is affected.
     *
     * @param document
     * @param project
     * @throws BackOfficeException
     */

    public static void invalidateDocumentCache(Contract document, Project project) throws BackOfficeException {

        if(document == null || project == null){

            PukkaLogger.log(PukkaLogger.Level.WARNING, "Trying to clear cache for project/document that is null");
            return;
        }

        invalidateDocumentCache(document.getKey(), project.getKey());
    }


    public static void invalidateDocumentCache(DBKeyInterface _document, DBKeyInterface _project) throws BackOfficeException {

        ServiceCache cache = new ServiceCache(ContractServlet.DataServletName);

        if(_project != null){

            cache.clearKeyForAllQualifiers(_project.toString());
        }


        if(_document != null){

            cache.clearKeyForAllQualifiers(_document.toString());

        }


    }



    /*************************************************************************
     *
     *      Clearing  "fragment for document" cache
     *
     * @param version - the updated version of the contract
     * @throws BackOfficeException
     */

    public static void invalidateFragmentCache(ContractVersionInstance version) throws BackOfficeException {

        if(version == null)
            throw new BackOfficeException(BackOfficeException.General, "Trying to invalidate cache for non-existing version instance");

        if(version.getKey() == null)
            throw new BackOfficeException(BackOfficeException.General, "Trying to invalidate cache for non stored version instance");



        ServiceCache cache = new ServiceCache(FragmentServlet.DataServletName);
        cache.clearKey(version.getKey().toString());          // Fragment is not qualified on user


    }



    protected ConditionInterface getLookupConditionForOptionalKey(DBKeyInterface key) {

        ConditionInterface condition;

        if(key == null)
            condition = new LookupList();

        else
            condition = new LookupByKey(key);

        return condition;
    }


    /********************************************************************'
     *
     *          getActionCount counts the actual actions.
     *
     * @param fragment - the fragment for annotations
     * @return
     */


    protected long getActionCount(ContractFragment fragment) {

        long actionCount = 0;

            actionCount = fragment.getActionsForFragment().size();
        PukkaLogger.log(PukkaLogger.Level.DEBUG,"***** Got " + actionCount + " actions for fragment " + fragment.getName());


        return actionCount;
    }


    /********************************************************************'
     *
     *          getAnnotationCount counts the actual annotations.
     *
     * @param fragment - the fragment for annotations
     * @return
     */


    protected long getAnnotationCount(ContractFragment fragment) {

        long annotationCount = 0;

            annotationCount = fragment.getAnnotationsForFragment().size();
            PukkaLogger.log(PukkaLogger.Level.DEBUG, "Got " + annotationCount + " annotations for fragment " + fragment.getName());

        return annotationCount;
    }


    /**************************************************************************
     *
     *      Create an object for all checklist source and compliance references
     *
     *
     *
     * @param  fragment                 - the fragment
     * @param  checklistItemsInProject - all checklist items (to loop through
     * @return
     */


    protected JSONObject getChecklistReferences(ContractFragment fragment, List<ChecklistItem> checklistItemsInProject) {

        JSONArray sourceReferences = new JSONArray();
        JSONArray complianceReferences = new JSONArray();


        for (ChecklistItem checklistItem : checklistItemsInProject) {

            if(checklistItem.getSourceId() != null && checklistItem.getSourceId().equals(fragment.getKey()))
                sourceReferences.put(checklistItem.getKey().toString());

            if(checklistItem.getCompletionId() != null && checklistItem.getCompletionId().equals(fragment.getKey()))
                complianceReferences.put(checklistItem.getKey().toString());
        }



        JSONObject checkListReference = new JSONObject()
                .put("source", sourceReferences)
                .put("compliance", complianceReferences);

        return checkListReference;

    }


}

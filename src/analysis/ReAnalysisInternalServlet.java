package analysis;

import actions.Action;
import actions.ActionStatus;
import actions.ActionTable;
import analysis.deferrance.DeferenceHandler;
import analysis2.AnalysisException;
import analysis2.NewAnalysisOutcome;
import classification.FragmentClassificationTable;
import contractManagement.*;
import crossReference.*;
import dataRepresentation.DBTimeStamp;
import databaseLayer.DBKeyInterface;
import document.*;
import fileHandling.BlobRepository;
import fileHandling.RepositoryFileHandler;
import fileHandling.RepositoryInterface;
import language.LanguageCode;
import log.PukkaLogger;
import net.sf.json.JSONObject;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;
import risk.ContractRisk;
import risk.ContractRiskTable;
import risk.RiskClassificationTable;
import search.Keyword;
import search.KeywordTable;
import search.SearchManager2;
import services.DocumentService;
import services.Formatter;
import system.Analyser;
import userManagement.Organization;
import userManagement.PortalUser;
import versioning.Transposer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/********************************************************
 *
 *          Perform the analysis
 *
 *          This is a web hook for the Analysis Queue
 *

 */

public class ReAnalysisInternalServlet extends DocumentService {

    public static final String DataServletName = "ReAnalysis";

    private String modelDirectory = MODEL_DIRECTORY; // Default value

    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Get not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);

     }


    /***************************************************************************'
     *
     *              Post a request.
     *
     *              Parameters:
     *
     *               - version : The key to the contract version instance
     *               - oldVerson : Optional old version if we are reuploading a document
     *
     *
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
     */


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {


        try{
            logRequest(req);
            ContractVersionInstance oldVersion = null;

            sessionManagement.allowBOAccess();

            if(!validateSession(req, resp, HttpServletResponse.SC_OK))    // Send OK here. A 403 would trigger a retry in the event queue
                return;

            DBKeyInterface _version = getMandatoryKey("version", req);
            ContractVersionInstance versionInstance = new ContractVersionInstance(new LookupByKey(_version));

            if(!mandatoryObjectExists(versionInstance, resp))
                return;

            Contract document = versionInstance.getDocument();

            if(!document.exists()){

                returnError("Document does not exist", ErrorType.DATA, HttpServletResponse.SC_OK, resp );
                return;
            }

            Project project = document.getProject();

            if(!mandatoryObjectExists(document, resp))
                return;

            // Optional old version for transposing


            Formatter formatter = getFormatFromParameters(req);

            PukkaLogger.log(PukkaLogger.Level.INFO, "Executing queued task - analysing " + versionInstance.getVersion());


            PukkaLogger.log(PukkaLogger.Level.ACTION, "Re - analyse");


            // Perform the analysis and the transposing
            reAnalyse(versionInstance);


            // Update the status of the document

            document.setMessage("Completed analyzing document.");
            document.setStatus(ContractStatus.getAnalysed());
            document.update();

            // Invalidate cache

            invalidateDocumentCache(document, project);
            invalidateFragmentCache(versionInstance);


            // If anyone is interested in the result
            JSONObject json = new JSONObject().put("Analysis", "COMPLETE");
            sendJSONResponse(json, formatter, resp);

        } catch (BackOfficeException e) {

            // Send OK here. An error code would trigger a retry

            PukkaLogger.log( e );
            returnError(e.narration, HttpServletResponse.SC_OK, resp);

        } catch (Exception e) {

            // Send OK here. An error code would trigger a retry

            PukkaLogger.log( e );
            returnError("Internal Error analyzing document", HttpServletResponse.SC_OK, resp);

        }

    }

    public void reAnalyse(ContractVersionInstance documentVersion) throws BackOfficeException {

        PukkaLogger.log(PukkaLogger.Level.INFO, "Deleting old keywords and attributes");

        // Remove existing attributes. These will be regenerated in the analysis

        deleteKeywords(documentVersion);
        deleteAttributes(documentVersion);


        analyse(documentVersion, null);

    }



}

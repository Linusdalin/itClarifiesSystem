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
import userManagement.SessionManagement;
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
 * //TODO: The check:        if(classification.getType().getName().equals(FeatureTypeTree.Reference.getName())){ should be optimized

 */

public class AnalysisServlet extends DocumentService {

    public static final String DataServletName = "Analysis";

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
     * @throws IOException
     */


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {


        try{
            logRequest(req);
            ContractVersionInstance oldVersion = null;

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

            DBKeyInterface _oldVersion = getOptionalKey("oldVersion", req);
            if(_oldVersion != null){

                oldVersion = new ContractVersionInstance(new LookupByKey(_oldVersion));

                if(!mandatoryObjectExists(oldVersion, resp))
                    return;

                if(!mandatoryObjectExists(oldVersion.getDocument(), resp))
                    return;
            }


            Formatter formatter = getFormatFromParameters(req);

            PukkaLogger.log(PukkaLogger.Level.INFO, "Executing queued task - analysing " + versionInstance.getVersion());

            RepositoryInterface repository = new BlobRepository();
            RepositoryFileHandler fileHandler = new RepositoryFileHandler(document.getFile());

            // Check for the file on the server. It should have been uploaded through the FileUploadServlet.

            if(!repository.existsFile(fileHandler))
                returnError("File " + versionInstance.getVersion() + " does not exist as a file on the server", HttpServletResponse.SC_OK, resp);


            // Now parse the file

            try{

                parseFile(document, versionInstance);

            } catch (BackOfficeException e) {

                PukkaLogger.swallow( e );

                document.setMessage("Failed to parse document: " + e.narration);
                document.setStatus(ContractStatus.getFailed());
                document.update();

                invalidateDocumentCache(document, project);

                returnError(e.narration, HttpServletResponse.SC_OK, resp);
                return;
            } catch (Exception e) {

                PukkaLogger.log( e );

                document.setMessage("Failed to parse document: Internal Error");
                document.setStatus(ContractStatus.getFailed());
                document.update();

                invalidateDocumentCache(document, project);

                returnError("Internal Error", HttpServletResponse.SC_OK, resp);
                return;
            }

            // Update the status of the document

            document.setMessage("Completed parsing. Analysing");
            document.setStatus(ContractStatus.getAnalysing());
            document.update();

            invalidateDocumentCache(document, project);

            PukkaLogger.log(PukkaLogger.Level.ACTION, "*****************************\nPhase III: The analysis");


            // Perform the analysis and the transposing
            analyse(versionInstance, oldVersion);

            invalidateDocumentCache(document, project);
            invalidateFragmentCache(versionInstance);

            // Update the status of the document

            document.setMessage("Completed analyzing document.");
            document.setStatus(ContractStatus.getAnalysed());
            document.update();


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



    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);

    }



}

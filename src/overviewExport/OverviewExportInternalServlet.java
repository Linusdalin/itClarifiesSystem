package overviewExport;

import analysis.ParseFeedback;
import project.Project;
import dataRepresentation.DBTimeStamp;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import pukkaBO.condition.LookupByKey;
import pukkaBO.condition.LookupItem;
import pukkaBO.condition.ReferenceFilter;
import pukkaBO.exceptions.BackOfficeException;
import services.Formatter;
import services.ItClarifiesService;
import userManagement.PortalUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/********************************************************
 *
 *          Project Overview servlet will return an excel document overview
 *
 *          This is the internal service
 *
 */

public class OverviewExportInternalServlet extends ItClarifiesService{

    public static final String DataServletName = "OverviewInternal";

    // The active .xlsx template
    private static final String TemplateFile = "exportTemplates/contracting framework template v3.xlsx";
    private static final int templateSheetIx = 6;


    /***********************************************************************
     *
     *          Post will trigger the export overview.
     *
     *          It will take all the parameters for the generation
     *
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
     */


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        Project project = null;
        PortalUser user = null;
        String exportTags = "[]";
        String comment = "";

        try{
            logRequest(req);

            sessionManagement.allowBOAccess();

            if(!validateSession(req, resp, HttpServletResponse.SC_OK))
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

            OverviewGenerator generator = new OverviewGenerator(project, user, comment, exportTags);
            ParseFeedback feedback = generator.preCalculate(exportTags);
            sendJSONResponse(feedback.toJSON(), formatter, resp);


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

    public void markAsFail(Project project) {

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

    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Get not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

     }



}

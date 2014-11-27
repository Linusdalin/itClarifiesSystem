package services;

import contractManagement.*;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import pukkaBO.condition.Sorting;
import versioning.FreezeSnapshot;
import versioning.Snapshot;
import versioning.SnapshotTable;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pukkaBO.condition.LookupByKey;
import pukkaBO.condition.LookupList;
import pukkaBO.condition.Ordering;
import pukkaBO.exceptions.BackOfficeException;
import userManagement.PortalUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/********************************************************
 *
 *          Diff servlet will return a diff between two versions
 *          of the same document
 *
 *          It works on two already uploaded versions
 *
 */

public class SnapshotServlet extends ItClarifiesService{

    public static final String DataServletName = "Snapshot";


    /**************************************************************************
     *
     *          Post will create a new snapshot
     *
     *
     * @param req
     * @param resp
     * @throws IOException
     */

    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        try {

            logRequest(req);

            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);


            Formatter formatter = getFormatFromParameters(req);

            DBKeyInterface _project = getMandatoryKey("project", req);
            Project project = new Project(new LookupByKey(_project));

            if(!mandatoryObjectExists(project, resp))
                return;

            String name = getMandatoryString("name", req);
            String description = getOptionalString("description", req, name); // Using name as description if it is missing

            // Implicit parameters to the creation

            PortalUser user = sessionManagement.getUser();

            PukkaLogger.log(PukkaLogger.Level.INFO, "Creating a new snapshot for the project " + project.getName());

            FreezeSnapshot snapshot = new FreezeSnapshot(name, project, description, user);
            Snapshot freeze = snapshot.freeze();


            JSONObject json = new JSONObject().put(DataServletName, freeze.getKey().toString());

            resp.getWriter().print(json);
            setRespHeaders(resp, 0);
            resp.flushBuffer();


        } catch (BackOfficeException e) {

            PukkaLogger.log( e );
            returnError("Error in " + DataServletName, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        } catch (Exception e) {

            PukkaLogger.log( e );
            returnError("Error in " + DataServletName, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
        }



     }

    /***********************************************************************************************
     *
     *          GET returns a list of snapshots for a project
     *
     *          {"VersionHistory":
     *              [
     *                  { "id":"ag9pdGNsYXJpZmllc2xpdmVyHgsSF0NvbnRyYWN0VmVyc2lvbkluc3RhbmNlGOsdDA",
     *                    "creation":"2014-01-01 00:09:00.0",
     *                    "name":"Cannon v1.0" }
     *              ]
     *          }
     *
     *          The list is ordered by creation date
     *
     * @param req
     * @param resp
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

            DBKeyInterface _project = getMandatoryKey("project", req);
            Project project = new Project(new LookupByKey(_project));

            if(!mandatoryObjectExists(project, resp))
                return;

            PukkaLogger.log(PukkaLogger.Level.INFO, "Getting snapshot history for the project " + project.getName());

            List<Snapshot> snapshotsForProject = project.getSnapshotsForProject(new LookupList(new Sorting(SnapshotTable.Columns.Timestamp.name(), Ordering.FIRST)));
            JSONArray versionList = new JSONArray();

            for(Snapshot snapshot : snapshotsForProject){

                // Create one object for each

                JSONObject versionJSON = new JSONObject()
                        .put("name", snapshot.getName())
                        .put("id", snapshot.getKey().toString())
                        .put("creation", snapshot.getTimestamp().getSQLTime().toString())
                        .put("creator", snapshot.getCreatorId().toString());

                versionList.put(versionJSON);
            }

            JSONObject json = new JSONObject().put(DataServletName, versionList);

            resp.getWriter().print(json);
            setRespHeaders(resp, 0);
            resp.flushBuffer();


        } catch (BackOfficeException e) {

            PukkaLogger.log( e );
            returnError("Error in " + DataServletName, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        } catch (Exception e) {

            PukkaLogger.log( e );
            returnError("Error in " + DataServletName, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
        }



    }

    //TODO: Delete not implemented. Probably needed

    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

    }


}

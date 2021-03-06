package userManagement;

import contractManagement.*;
import databaseLayer.DBKeyInterface;
import net.sf.json.JSONObject;
import project.Project;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupByKey;
import pukkaBO.condition.LookupItem;
import pukkaBO.exceptions.BackOfficeException;
import services.DocumentService;
import services.Formatter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/********************************************************
 *
 *          Access control settings.
 *
 */

public class ACSServlet extends DocumentService {

    public static final String DataServletName = "AccessControl";


    /***********************************************************************************
     *
     *      Setting access rights for a document
     *
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
     *
     *
     *
     */


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        try{

            logRequest(req);


            if(!validateSession(req, resp))
                return;

            DBKeyInterface _document         = getMandatoryKey("document", req);
            String _visibility               = getMandatoryString("visibility", req);
            String _access                    = getMandatoryString("access", req);

            Formatter formatter = getFormatFromParameters(req);

            Contract document = new Contract(new LookupByKey(_document));
            if(!mandatoryObjectExists(document, resp))
                return;

            Visibility visibility = new Visibility(new LookupItem().addFilter(new ColumnFilter(VisibilityTable.Columns.Name.name(), _visibility)));
            if(!mandatoryObjectExists(visibility, resp))
                return;

            AccessRight access = new AccessRightTable().getValueByName(_access);
            if(!mandatoryObjectExists(access, resp))
                return;

            // Check access. To change the permissions the document has to be deletable

            if(!deletable(document, resp))
                return;

            Project project = document.getProject();


            //Updat the project access

            document.setAccess(access);
            document.update();

            // Changing the access right means we will have to invalidate the cache for both the document and the project
            invalidateDocumentCache(document, project);

            JSONObject json = createPostResponse(DataServletName, document);
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
     *              Not implemented. Access rights are passed in the document servlet
     *
     * @throws java.io.IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Get not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

     }





    /************************************************************************
     *
     *          Delete is not supported.
     *
     *          //TODO: Not implementes
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
     */


    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

    }


}

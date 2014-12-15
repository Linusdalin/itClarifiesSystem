package services;

import contractManagement.*;
import dataRepresentation.DataObjectInterface;
import databaseLayer.DBKeyInterface;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;
import userManagement.Organization;
import userManagement.PortalUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/********************************************************
 *
 *          Risk Flag Servlet
 *
 */

public class ClassServlet extends DocumentService{

    public static final String DataServletName = "Class";


    /***********************************************************************************
     *
     *      Adding a new classification class
     *
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
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

            String className                    = getMandatoryString("name", req);
            String keywords                     = getOptionalString("keywords", req, "");
            String description                  = getMandatoryString("description", req);


            FragmentClass classification = new FragmentClass(new LookupItem()
                    .addFilter(new ColumnFilter(FragmentClassTable.Columns.Name.name(), className)));

            if(classification.exists()){

                returnError("Fragment class" + className + " already exists.", HttpServletResponse.SC_BAD_REQUEST, resp );
                return;

            }

            PortalUser user = sessionManagement.getUser();

            FragmentClass newClass = new FragmentClass(className, className, keywords, description, user.getOrganizationId());
            newClass.store();


            JSONObject json = createPostResponse(DataServletName, newClass);
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
     *          Get all classifications available
     *
     *
     * @throws java.io.IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        if(req.getParameter("_method") != null && req.getParameter("_method").equals("DELETE")){
            doDelete(req, resp);
            return;
        }



        try{
            logRequest(req);

            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);


            Formatter formatter = getFormatFromParameters(req);

            JSONArray list = new JSONArray();

            DBKeyInterface organization = sessionManagement.getUser().getOrganizationId();

            FragmentClassTable organizationSpecificClasses = new FragmentClassTable(new LookupList()
                    .addFilter(new ReferenceFilter(FragmentClassTable.Columns.Organization.name(), organization))
                    .addSorting(new Sorting(FragmentClassTable.Columns.Name.name(), Ordering.FIRST)));

            for(DataObjectInterface object : organizationSpecificClasses.getValues()){

                FragmentClass fragmentClass = (FragmentClass)object;

                JSONObject riskObject = new JSONObject()
                        .put("id", fragmentClass.getKey().toString())
                        .put("name", fragmentClass.getName())
                        .put("desc", fragmentClass.getDescription())
                        .put("type", "Organization");
                list.put(riskObject);

            }

            // Now add all generic classes

            FragmentClassTable genericClasses = new FragmentClassTable(new LookupList()
                    .addFilter(new ReferenceFilter(FragmentClassTable.Columns.Organization.name(), Organization.getnone().getKey()))
                    .addSorting(new Sorting(FragmentClassTable.Columns.Name.name(), Ordering.FIRST)));

            for(DataObjectInterface object : genericClasses.getValues()){

                FragmentClass fragmentClass = (FragmentClass)object;

                JSONObject riskObject = new JSONObject()
                        .put("id", fragmentClass.getKey().toString())
                        .put("name", fragmentClass.getName())
                        .put("desc", fragmentClass.getDescription())
                        .put("type", "General");
                list.put(riskObject);

            }


            JSONObject json = new JSONObject().put(DataServletName, list);
            sendJSONResponse(json, formatter, resp);

        }catch(BackOfficeException e){

            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            e.printStackTrace();


        } catch ( Exception e) {

            returnError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            e.printStackTrace();

        }

     }





    /************************************************************************
     *
     *          Delete is not supported.
     *
     *          //TODO: Not implemented
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
     */


    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        try{

            logRequest(req);

            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);


            Formatter formatter = getFormatFromParameters(req);

            DBKeyInterface key                  = getMandatoryKey("key", req);

            FragmentClass classification = new FragmentClass(new LookupByKey(key));

            if(!classification.exists()){

                returnError("Fragment class" + key.toString() + " does not exist.", HttpServletResponse.SC_BAD_REQUEST, resp );
                return;

            }

            classification.delete();

            JSONObject json = createDeletedResponse(DataServletName, classification);
            sendJSONResponse(json, formatter, resp);


        }catch(BackOfficeException e){

            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            e.printStackTrace();

        } catch ( Exception e) {

            returnError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            e.printStackTrace();

        }

    }


}

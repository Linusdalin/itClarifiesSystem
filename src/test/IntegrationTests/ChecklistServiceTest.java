package test.integrationTests;

import actions.*;
import backend.ItClarifies;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import contractManagement.ContractFragment;
import contractManagement.ContractFragmentTable;
import contractManagement.Project;
import contractManagement.ProjectTable;
import databaseLayer.AppEngine.AppEngineKey;
import databaseLayer.DBKeyInterface;
import databaseLayer.DatabaseAbstractionFactory;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupByKey;
import pukkaBO.condition.LookupItem;
import services.AnnotationServlet;
import test.MockWriter;
import test.ServletTests;
import userManagement.PortalUser;
import userManagement.PortalUserTable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/*******************************************************************
 *
 *          Testing the service with mocked request and response messages
 *
 */


public class ChecklistServiceTest extends ServletTests {



    @AfterClass
    public static void tearDown() {

        helper.tearDown();
    }


    @BeforeClass
    public static void preAmble(){


        init();

    }

    /******************************************************************************
     *
     *          First get the list of all checklists in the project.
     *          Look at the first (only) item in the list to verify parameters
     *
     *          get the key to the first checklist
     *
     *          Retrieve the item with the key and verify parameters
     *
     *
     * @throws Exception
     */


    @Test
    public void testBasicAccess() throws Exception {

        try{

            MockWriter mockWriter;


            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("project")).thenReturn(demoProject.getKey().toString());
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ChecklistServlet().doGet(request, response);

            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);

            JSONArray allChecklists = json.getJSONArray("Checklist");
            assertVerbose("There should be one checklist available", allChecklists.length(), is(1));

            // Check the parameters of the item in the list

            JSONObject first = allChecklists.getJSONObject(0);
            assertNotNull(first.getString("id"));
            assertNotNull(first.getString("name"));
            assertNotNull(first.getString("description"));

            DBKeyInterface checklistKey = new DatabaseAbstractionFactory().createKey(first.getString("id"));




            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("project")).thenReturn(null);
            when(request.getParameter("key")).thenReturn(checklistKey.toString());
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ChecklistServlet().doGet(request, response);

            output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject checklist = new JSONObject(output).getJSONObject("Checklist");

            assertVerbose("Expecting an id", checklist.getString("id"), is(checklistKey.toString()));
            assertNotNull("Expecting a name", checklist.getString("name"));
            assertNotNull("Expecting a description", checklist.getString("description"));

            //assertVerbose("Expecting to find 1 item in the checklist", checklist.getJSONArray("items").length(), is(1));


        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }


    @Test
    public void testDetailedAccess() throws Exception {

        try{

            MockWriter mockWriter;

            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("project")).thenReturn(demoProject.getKey().toString());
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ChecklistItemServlet().doGet(request, response);

            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);

            JSONArray allChecklists = json.getJSONArray("ChecklistDetails");
            assertVerbose("There should be one checklist available", allChecklists.length(), is(1));

            // Check the parameters of the item in the list

            JSONObject first = allChecklists.getJSONObject(0);
            assertNotNull(first.getString("id"));
            assertNotNull(first.getString("name"));
            assertNotNull(first.getString("description"));

            DBKeyInterface checklistKey = new DatabaseAbstractionFactory().createKey(first.getString("id"));

            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("project")).thenReturn(null);
            when(request.getParameter("key")).thenReturn(checklistKey.toString());
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ChecklistServlet().doGet(request, response);

            output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject checklist = new JSONObject(output).getJSONObject("Checklist");

            assertVerbose("Expecting an id", checklist.getString("id"), is(checklistKey.toString()));
            assertNotNull("Expecting a name", checklist.getString("name"));
            assertNotNull("Expecting a description", checklist.getString("description"));

            //assertVerbose("Expecting to find 1 item in the checklist", checklist.getJSONArray("items").length(), is(1));


        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }


    @Test
    public void testFailDelete() throws Exception {

        try{

            MockWriter mockWriter;


            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ChecklistServlet().doDelete(request, response);

            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);

            assertVerbose("Not allowed delete", output.contains("Delete not supported"), is(true));



        }catch(Exception e){

            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void testFailNoSession() throws Exception {

        try{

            MockWriter mockWriter;


            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("InvalidToken");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ChecklistServlet().doGet(request, response);

            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);

            assertVerbose("Not allowed without session", output.contains("No session"), is(true));



        }catch(Exception e){

            e.printStackTrace();
            assertTrue(false);
        }
    }




}


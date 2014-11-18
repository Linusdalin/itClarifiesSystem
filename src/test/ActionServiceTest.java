package test;

import actions.Action;
import actions.ActionServlet;
import backend.ItClarifies;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import contractManagement.*;
import databaseLayer.AppEngine.AppEngineKey;
import databaseLayer.DBKeyInterface;
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
import userManagement.PortalUser;
import userManagement.PortalUserTable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/*******************************************************************
 *
 *          Testing the service with mocked request and response messages
 *
 */


public class ActionServiceTest extends ServletTests {


    private static LocalServiceTestHelper helper;
    private static HttpServletRequest request;
    private static HttpServletResponse response;

    private static BackOfficeInterface bo;

    @AfterClass
    public static void tearDown() {

        helper.tearDown();
    }


    @BeforeClass
    public static void preAmble(){

        helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
        helper.setUp();


        PukkaLogger.setLogLevel(PukkaLogger.Level.DEBUG);

        try {

            request = mock(HttpServletRequest.class);
            response = mock(HttpServletResponse.class);

            bo = new ItClarifies();
            bo.createDb();
            bo.populateValues(true);


            PukkaLogger.setLogLevel(PukkaLogger.Level.DEBUG);

        } catch (Exception e) {

            e.printStackTrace();
            assertTrue(false);
        }

    }



        @Test
        public void testCreateUpdateAndDelete() throws Exception {

            try{

                MockWriter mockWriter;

                ContractFragment fragment = new ContractFragment(new LookupItem().addFilter(new ColumnFilter(ContractFragmentTable.Columns.Name.name(), "first fragment")));
                PortalUser assignee = new PortalUser(new LookupItem().addFilter(new ColumnFilter(PortalUserTable.Columns.Name.name(), "demo")));

                long actionCount = fragment.getActionCount();
                long annotationCount = fragment.getAnnotationCount();


                System.out.println("\n\n\n*****************\n Got count. " + actionCount + "/" + annotationCount);

                mockWriter = new MockWriter();

                when(request.getParameter("session")).thenReturn("DummyAdminToken");
                when(request.getParameter("fragment")).thenReturn(fragment.getKey().toString());
                when(request.getParameter("assignee")).thenReturn(assignee.getKey().toString());
                when(request.getParameter("name")).thenReturn("New test action");
                when(request.getParameter("description")).thenReturn("description of New test action");
                when(response.getWriter()).thenReturn(mockWriter.getWriter());

                new ActionServlet().doPost(request, response);

                String output = mockWriter.getOutput();
                PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

                JSONObject json = new JSONObject(output);

                // There should be a key returned

                String action = json.getString("Action");
                isKey(action);

                // Now check that everything is ok in the database

                ContractFragment rereadFragment = new ContractFragment(new LookupItem().addFilter(new ColumnFilter(ContractFragmentTable.Columns.Name.name(), "first fragment")));
                assertThat("One more action", rereadFragment.getActionCount(), is(actionCount + 1));
                assertThat("Same Annotation count", rereadFragment.getAnnotationCount(), is(annotationCount));

                List<Action> actions = fragment.getActionsForFragment();

                assertThat("Same number of actions in the database as the count", (int)rereadFragment.getActionCount(), is(actions.size()));





                // Update

                HttpServletRequest updateRequest = mock(HttpServletRequest.class);
                mockWriter = new MockWriter();

                when(updateRequest.getParameter("session")).thenReturn("DummyAdminToken");
                when(updateRequest.getParameter("action")).thenReturn(action);
                when(updateRequest.getParameter("description")).thenReturn("Updated");
                when(updateRequest.getParameter("dueDate")).thenReturn("2014-10-10");
                when(response.getWriter()).thenReturn(mockWriter.getWriter());

                new ActionServlet().doPost(updateRequest, response);

                output = mockWriter.getOutput();
                PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

                // The action shall be the same but with a new body
                DBKeyInterface key = new AppEngineKey(KeyFactory.stringToKey(action));

                Action rereadAction = new Action(new LookupByKey(key));

                assertThat("Expect to find the same fragment key",rereadAction.getFragment().getKey().toString(), is(fragment.getKey().toString()));
                assertThat("Expect to find an updated description",rereadAction.getDescription(), is("Updated"));
                assertThat("Expect to find the same name",rereadAction.getName(), is("New test action"));
                assertThat("Expect to find a due date", rereadAction.getDue().getISODate(), is("2014-10-10"));

                // Now check that everything is ok in the database

                rereadFragment = new ContractFragment(new LookupItem().addFilter(new ColumnFilter(ContractFragmentTable.Columns.Name.name(), "first fragment")));
                assertThat("One more action", rereadFragment.getActionCount(), is(actionCount + 1));
                assertThat("Same Annotation count", rereadFragment.getAnnotationCount(), is(annotationCount));

                actions = fragment.getActionsForFragment();
                assertThat("Same number of actions in the database as the count", (int)rereadFragment.getActionCount(), is(actions.size()));


                // Now try delete

                HttpServletRequest deleteRequest = mock(HttpServletRequest.class);
                mockWriter = new MockWriter();

                when(deleteRequest.getParameter("session")).thenReturn("DummyAdminToken");
                when(deleteRequest.getParameter("action")).thenReturn(key.toString());
                when(response.getWriter()).thenReturn(mockWriter.getWriter());

                new ActionServlet().doDelete(deleteRequest, response);

                output = mockWriter.getOutput();
                PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

                assertCorrectlyDeleted("Action", new JSONObject(output));

                rereadFragment = new ContractFragment(new LookupItem().addFilter(new ColumnFilter(ContractFragmentTable.Columns.Name.name(), "first fragment")));

                assertThat("Action count back to normal", rereadFragment.getActionCount(), is(actionCount));
                assertThat("Same Annotation count", rereadFragment.getAnnotationCount(), is(annotationCount));

                actions = fragment.getActionsForFragment();
                assertThat("Same number of actions in the database as the count", (int)rereadFragment.getActionCount(), is(actions.size()));




        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }


    @Test
    public void testGetActions() throws Exception {

        Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));
        assertThat("Prerequisite project exists", project.exists(), is(true));


        try{
            MockWriter mockWriter = new MockWriter();

            when(response.getWriter()).thenReturn(mockWriter.getWriter());
            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("project")).thenReturn(project.getKey().toString());

            new ActionServlet().doGet(request, response);


            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            JSONArray actions = json.getJSONArray("Action");

            assertThat("There should be actions", actions.length(), is(not( 0 )));

            JSONObject action = (JSONObject)actions.get(0);


            try{

                // These tests mostly verifies that the fields exists

                assertThat( action.getString("id"), not(is("")));
                assertThat( action.getString("fragment"), is(not("")));
                assertThat( action.getString("text"), is(not("")));
                assertThat( action.getString("creator"), is(not("")));
                assertThat( action.getString("assignee"), is(not("")));
                assertThat( action.getString("status"), is(not("")));
                assertThat( action.getString("creationDate"), is(not("")));
                assertThat( action.getString("dueDate"), is(not("")));
                assertThat( action.getString("completeDate"), is(not("")));

            }catch(JSONException e){

                e.printStackTrace();
                assertThat("Should have found all fields in the reply", false, is(true));
            }


        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void testFailNoSession() throws Exception {

        try{
            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("InvalidSessionToken");
            when(request.getParameter("body")).thenReturn("Updated");

            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new AnnotationServlet().doPost(request, response);


            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            JSONObject error = (JSONObject)json.getJSONArray("error").get(0);

            assertThat(error.getString("type"), is("SESSION"));

        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }





}


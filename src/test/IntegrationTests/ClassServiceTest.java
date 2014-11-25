package test.integrationTests;

import backend.ItClarifies;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import contractManagement.FragmentClass;
import contractManagement.FragmentClass;
import contractManagement.FragmentClassTable;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupItem;
import services.ClassServlet;
import services.ClassificationServlet;
import test.MockWriter;
import test.ServletTests;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/*******************************************************************
 *
 *          Testing the service with mocked request and response messages
 *
 */


public class ClassServiceTest extends ServletTests {


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


        bo = new ItClarifies();
        bo.createDb();
        bo.populateValues(true);

        PukkaLogger.setLogLevel(PukkaLogger.Level.DEBUG);



        try {

            request = mock(HttpServletRequest.class);
            response = mock(HttpServletResponse.class);

        } catch (Exception e) {

            e.printStackTrace();
            assertTrue(false);
        }

    }



    @Test
    public void testVisibility() throws Exception {


        try {

            int classificationCount = new FragmentClassTable().getCount();
            PukkaLogger.log(PukkaLogger.Level.INFO, "There are " + classificationCount + " classifications");
            MockWriter mockWriter;
            String output;

            // Add one more classification


            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());
            when(request.getParameter("name")).thenReturn("new class");
            when(request.getParameter("description")).thenReturn("description fro class");
            when(request.getParameter("keywords")).thenReturn("keywords for class");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");

            new ClassServlet().doPost(request, response);

            output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);


            // Check that the classification is in the database

            FragmentClass contractFragmentClass = new FragmentClass(new LookupItem().addFilter(new ColumnFilter(FragmentClassTable.Columns.Name.name(), "new class")));

            assertThat("New class created", contractFragmentClass.exists(), is(true));


            // Now check the visibility
            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ClassServlet().doGet(request, response);

            output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            JSONArray allClasses = json.getJSONArray("Class");

            assertThat("Assuming that we now get one class more", allClasses.length(), is(classificationCount + 1));



            // Verify that it shows correctly in the service

            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyEveToken");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ClassificationServlet().doGet(request, response);

            output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            json = new JSONObject(output);

            allClasses = json.getJSONArray("Classification");
            assertThat("Asusming that eve only sees the original classes", allClasses.length(), is(classificationCount));


        } catch (Exception e) {

            e.printStackTrace();
            assertTrue(false);
        }


    }



    @Test
    public void testFailNoSession() throws Exception {

        try{
            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("InvalidSessionToken");
            when(request.getParameter("key")).thenReturn("anything");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");

            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ClassServlet().doPost(request, response);


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


    @Test
    public void testFailMissingParameter() throws Exception {

        try{
            MockWriter writer = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("name")).thenReturn(null);
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(writer.getWriter());

            new ClassServlet().doPost(request, response);

            String output = writer.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            JSONObject error = (JSONObject)json.getJSONArray("error").get(0);
            String message = error.getString("message");

            assertThat(message, is("Mandatory Field name is missing in request"));

        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }



    @Test
    public void testGet() throws Exception {

        try{
            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummySessionToken");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ClassServlet().doGet(request, response);


            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            JSONObject list = (JSONObject)json.getJSONArray("Class").get(0);
            String key = list.getString("id");
            String name = list.getString("name");
            String description = list.getString("desc");
            String type = list.getString("type");


        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }





}


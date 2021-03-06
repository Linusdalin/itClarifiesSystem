package test.integrationTests;

import backend.ItClarifies;
import classifiers.ClassifierInterface;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import classification.FragmentClass;
import classification.FragmentClassTable;
import language.English;
import language.LanguageInterface;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupItem;
import classification.ClassServlet;
import classification.ClassificationServlet;
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


    @AfterClass
    public static void tearDown() {

        helper.tearDown();
    }


    @BeforeClass
    public static void preAmble(){

        helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
        helper.setUp();


        init();
    }

    /**********************************************************************************
     *
     *          Adding a new class should return this class together with the machine classes.
     *
     *          It should be visible in the service only for users of the correct organization
     *
     *
     *
     * @throws Exception
     */


    @Test
    public void testVisibility() throws Exception {


        try {

            int classCount = new FragmentClassTable().getCount();
            LanguageInterface defaultLanguage = new English();

            int machineClassifiers = defaultLanguage.getAllClassifiers().length;

            PukkaLogger.log(PukkaLogger.Level.INFO, "There are " + classCount + " user defined classes and " + machineClassifiers + " machine classifiers");

            /*

            for (ClassifierInterface classifierInterface : defaultLanguage.getAllClassifiers()) {

                System.out.println("   >" + classifierInterface.getType().getName());
            }

            */

            MockWriter mockWriter;
            String output;

            // Add one more classification called "#newClass"


            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());
            when(request.getParameter("name")).thenReturn("newClass");
            when(request.getParameter("description")).thenReturn("description for class");
            when(request.getParameter("keywords")).thenReturn("keywords for class");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");


            new ClassServlet().doPost(request, response);

            output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);



            int newClassCount = new FragmentClassTable().getCount();
            PukkaLogger.log(PukkaLogger.Level.INFO, "There are " + newClassCount + " user defined classes and " + machineClassifiers + " machine classifiers");

            // Check that the classification is in the database

            FragmentClass contractFragmentClass = new FragmentClass(new LookupItem().addFilter(new ColumnFilter(FragmentClassTable.Columns.Name.name(), "newClass")));

            assertVerbose("New class created", contractFragmentClass.exists(), is(true));
            assertVerbose("Custom classes in database ", newClassCount, is(classCount + 1));


            // Now check the visibility for the creator
            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ClassServlet().doGet(request, response);

            output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            JSONArray allClasses = json.getJSONArray("Class");

            assertVerbose("Assuming that we get all machine classes and one more class in the service", allClasses.length(), is(classCount + 1 + machineClassifiers));

            // It should also be visible for others in the same organization
            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummySessionToken");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ClassServlet().doGet(request, response);

            output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            json = new JSONObject(output);
            allClasses = json.getJSONArray("Class");

            assertVerbose("Assuming that we get all machine classes and one more class in the service", allClasses.length(), is(classCount + 1 + machineClassifiers));


            // Verify that it DOESN'T show in the service for someone from another organization

            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyEveToken");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ClassificationServlet().doGet(request, response);

            output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            json = new JSONObject(output);

            allClasses = json.getJSONArray("Classification");
            assertThat("Asusming that eve only sees the original classes", allClasses.length(), is(classCount + machineClassifiers));


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


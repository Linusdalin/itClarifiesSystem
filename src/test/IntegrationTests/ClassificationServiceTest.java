package test.integrationTests;

import analysis.FeatureType;
import backend.ItClarifies;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import contractManagement.*;
import databaseLayer.AppEngine.AppEngineKey;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.*;
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


public class ClassificationServiceTest extends ServletTests {


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
    public void testAddAndDeleteClassification() throws Exception {


        try {

            ContractFragment fragment = new ContractFragment(new LookupItem().addFilter(new ColumnFilter(ContractFragmentTable.Columns.Name.name(), "first fragment")));
            String classForFragment = FeatureType.DEFINITION.name();  // Just arbitrary class for test

            long classificationCount = fragment.getClassificatonCount();
            PukkaLogger.log(PukkaLogger.Level.INFO, "There are " + classificationCount + " classifications");

            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("fragment")).thenReturn(fragment.getKey().toString());
            when(request.getParameter("class")).thenReturn(classForFragment);
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ClassificationServlet().doPost(request, response);

            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            String reply = json.getString("Classification");
            isKey(reply);

            // There should now be one more classification for the fragment

            ContractFragment readBack = new ContractFragment(new LookupByKey(fragment.getKey()));

            assertThat(readBack.getClassificatonCount(), is(classificationCount + 1));

            // Adding a classification should have been noted in the reclassification log

            ReclassificationTable classificatons = new ReclassificationTable();

            assertThat("There should be one re-classification note", classificatons.getCount(), is( 1 ));



            // Now try to delete it again

            DBKeyInterface classificationKey = new AppEngineKey(KeyFactory.stringToKey(reply));


            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("key")).thenReturn(classificationKey.toString());
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ClassificationServlet().doDelete(request, response);

            output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            json = new JSONObject(output);
            assertCorrectlyDeleted("Classification", json);

            // There should now be one more classification for the fragment

            readBack = new ContractFragment(new LookupByKey(fragment.getKey()));

            assertThat(readBack.getClassificatonCount(), is(classificationCount));

            // After deleting the classification, the log should still show one entry, as this
            // was not a system classificaton.

            ReclassificationTable classificatonLogAfter = new ReclassificationTable();
            assertThat("There should be one re-classification note", classificatonLogAfter.getCount(), is( 1 ));



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

            new ClassificationServlet().doPost(request, response);


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
            when(request.getParameter("fragment")).thenReturn(null);
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(writer.getWriter());

            new ClassificationServlet().doPost(request, response);

            String output = writer.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            JSONObject error = (JSONObject)json.getJSONArray("error").get(0);
            String message = error.getString("message");

            assertThat(message, is("Mandatory Field fragment is missing in request"));

        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void testFailInvalidKey() throws Exception {

        try{
            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("fragment")).thenReturn("Not A fragment key");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");

            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ClassificationServlet().doPost(request, response);


            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            JSONObject error = (JSONObject)json.getJSONArray("error").get(0);
            String message = error.getString("message");

            assertThat(message, is("Parameter fragment is not a key (Not A fragment key)!"));

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

            new ClassificationServlet().doGet(request, response);


            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            JSONObject list = (JSONObject)json.getJSONArray("Classification").get(0);
            String key = list.getString("id");
            String name = list.getString("name");
            String type = list.getString("type");


        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }





}


package test.integrationTests;

import backend.ItClarifies;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import contractManagement.*;
import databaseLayer.AppEngine.AppEngineKey;
import log.PukkaLogger;
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


public class AnnotationServiceTest extends ServletTests {


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



        @Test
        public void testCreateUpdateAndDelete() throws Exception {

            try{

                BackOfficeInterface bo;

                bo = new ItClarifies();
                bo.createDb();
                bo.populateValues(true);

                MockWriter mockWriter;


                ContractFragment fragment = new ContractFragment(new LookupItem().addFilter(new ColumnFilter(ContractFragmentTable.Columns.Name.name(), "first fragment")));
                long annotationCount = fragment.getAnnotationCount();


                System.out.println("\n\n\n*****************\n Got count = " + annotationCount);

                mockWriter = new MockWriter();

                when(request.getParameter("session")).thenReturn("DummyAdminToken");
                when(request.getParameter("fragment")).thenReturn(fragment.getKey().toString());
                when(request.getParameter("body")).thenReturn("New test annotation");
                when(request.getRemoteAddr()).thenReturn("127.0.0.1");
                when(response.getWriter()).thenReturn(mockWriter.getWriter());

                new AnnotationServlet().doPost(request, response);

                String output = mockWriter.getOutput();
                PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

                JSONObject json = new JSONObject(output);

                // There should be a key returned

                String annotation = json.getString("Annotation");
                isKey(annotation);

                // Now check that everything is ok in the database

                ContractFragment rereadFragment = new ContractFragment(new LookupItem().addFilter(new ColumnFilter(ContractFragmentTable.Columns.Name.name(), "first fragment")));
                assertThat(rereadFragment.getAnnotationCount(), is(annotationCount + 1));

                ContractAnnotation rereadAnnotation = new ContractAnnotation(new LookupByKey(new AppEngineKey(KeyFactory.stringToKey(annotation))));
                assertThat(rereadAnnotation.getFragment().getKey().toString(), is(fragment.getKey().toString()));



                // Update

                HttpServletRequest updateRequest = mock(HttpServletRequest.class);
                mockWriter = new MockWriter();

                when(updateRequest.getParameter("session")).thenReturn("DummyAdminToken");
                when(updateRequest.getParameter("annotation")).thenReturn(annotation);
                when(updateRequest.getParameter("body")).thenReturn("Updated");
                when(updateRequest.getRemoteAddr()).thenReturn("127.0.0.1");
                when(response.getWriter()).thenReturn(mockWriter.getWriter());

                new AnnotationServlet().doPost(updateRequest, response);

                output = mockWriter.getOutput();
                PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

                // The fragment shall still be the same, but a new annotation body

                rereadAnnotation = new ContractAnnotation(new LookupByKey(new AppEngineKey(KeyFactory.stringToKey(annotation))));
                assertThat(rereadAnnotation.getFragment().getKey().toString(), is(fragment.getKey().toString()));
                assertThat(rereadAnnotation.getDescription(), is("Updated"));

                // And still the same annotation count

                rereadFragment     = new ContractFragment(new LookupItem().addFilter(new ColumnFilter(ContractFragmentTable.Columns.Name.name(), "first fragment")));
                assertThat(rereadFragment.getAnnotationCount(), is(annotationCount + 1));

                // Now try delete

                HttpServletRequest deleteRequest = mock(HttpServletRequest.class);
                mockWriter = new MockWriter();

                when(deleteRequest.getParameter("session")).thenReturn("DummyAdminToken");
                when(deleteRequest.getParameter("annotation")).thenReturn(annotation);
                when(deleteRequest.getRemoteAddr()).thenReturn("127.0.0.1");
                when(response.getWriter()).thenReturn(mockWriter.getWriter());

                new AnnotationServlet().doDelete(deleteRequest, response);

                output = mockWriter.getOutput();
                PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

                assertCorrectlyDeleted("Annotation", new JSONObject(output));

                rereadFragment = new ContractFragment(new LookupItem().addFilter(new ColumnFilter(ContractFragmentTable.Columns.Name.name(), "first fragment")));
                assertThat(rereadFragment.getAnnotationCount(), is(annotationCount));




        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }


    @Test
    public void testGetAnnotation() throws Exception {

        try{
            MockWriter mockWriter = new MockWriter();

            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new AnnotationServlet().doGet(request, response);


            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            JSONObject error = (JSONObject)json.getJSONArray("error").get(0);
            String message = error.getString("message");

            assertThat(message, is("Get not supported in Annotation"));

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


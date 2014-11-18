package test;

import backend.ItClarifies;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import contractManagement.*;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupItem;
import search.SearchServlet;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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


public class SearchServiceTest extends ServletTests {


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
        public void testBasicSearch() throws Exception {

            try{

                Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));

                MockWriter mockWriter = new MockWriter();

                when(request.getParameter("session")).thenReturn("DummyAdminToken");
                when(request.getParameter("text")).thenReturn("Google");
                when(request.getParameter("project")).thenReturn(project.getKey().toString());
                when(response.getWriter()).thenReturn(mockWriter.getWriter());

                new SearchServlet().doGet(request, response);


                String output = mockWriter.getOutput();
                PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

                JSONArray hits = new JSONObject(output).getJSONArray("fragments");
                assertThat(hits.length(), is(2* 23));        // 23 hits plus the headlines

                // Now check the structure of the hits in the response
                JSONObject firstHit = (JSONObject)hits.get(0);

                String fragmentKey      = firstHit.getString("fragment");
                String documentKey      = firstHit.getString("document");
                isKey(documentKey);
                int ordinal             = firstHit.getInt("ordinal");
                JSONArray patternList   = firstHit.getJSONArray("patternlist");


                isKey(fragmentKey);
                isKey(documentKey);
                assertThat(ordinal, not(is(0)));
                assertThat(patternList.length(), not(is(0)));

        }catch(Exception e){

            e.printStackTrace();
            assertTrue(false);
        }
    }



    @Test
    public void testAnnotationSearch() throws Exception {

        try{

            Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));

            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("text")).thenReturn("annotation");
            when(request.getParameter("project")).thenReturn(project.getKey().toString());
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new SearchServlet().doGet(request, response);


            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONArray hits = new JSONObject(output).getJSONArray("fragments");
            assertThat(hits.length(), is(1));

            // Now check the structure of the hits in the response
            JSONObject firstHit = (JSONObject)hits.get(0);

            String fragmentKey      = firstHit.getString("fragment");
            String documentKey      = firstHit.getString("document");
            isKey(documentKey);
            int ordinal             = firstHit.getInt("ordinal");
            JSONArray patternList   = firstHit.getJSONArray("patternlist");


            isKey(fragmentKey);
            isKey(documentKey);
            assertThat(ordinal, not(is(0)));
            assertThat(patternList.length(), not(is(0)));

    }catch(Exception e){

        e.printStackTrace();
        assertTrue(false);
    }
}

    @Test
    public void testClassificationSearch() throws Exception {

        try{

            Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));

            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("text")).thenReturn("Datum");
            when(request.getParameter("project")).thenReturn(project.getKey().toString());
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new SearchServlet().doGet(request, response);


            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONArray hits = new JSONObject(output).getJSONArray("fragments");
            assertThat(hits.length(), is(1));

            // Now check the structure of the hits in the response
            JSONObject firstHit = (JSONObject)hits.get(0);

            String fragmentKey      = firstHit.getString("fragment");
            String documentKey      = firstHit.getString("document");
            isKey(documentKey);
            int ordinal             = firstHit.getInt("ordinal");
            JSONArray patternList   = firstHit.getJSONArray("patternlist");


            isKey(fragmentKey);
            isKey(documentKey);
            assertThat(ordinal, not(is(0)));
            assertThat(patternList.length(), not(is(0)));


            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("text")).thenReturn("#Datum");
            when(request.getParameter("project")).thenReturn(project.getKey().toString());
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new SearchServlet().doGet(request, response);


            output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            hits = new JSONObject(output).getJSONArray("fragments");
            assertThat(hits.length(), is(1));

            // Now check the structure of the hits in the response
            firstHit = (JSONObject)hits.get(0);

            fragmentKey      = firstHit.getString("fragment");
            documentKey      = firstHit.getString("document");
            isKey(documentKey);
            ordinal             = firstHit.getInt("ordinal");
            patternList   = firstHit.getJSONArray("patternlist");


            isKey(fragmentKey);
            isKey(documentKey);
            assertThat(ordinal, not(is(0)));
            assertThat(patternList.length(), not(is(0)));


        }catch(Exception e){

            e.printStackTrace();
            assertTrue(false);
        }
}



    /******************************************************************************
     *
     *      make sure we dont get hits from documents we cant see
     *
     *      The phrase "åäö" is found in the Cannon doc which the demo user cant see
     *
     * @throws Exception
     */


    @Test
    public void testRestrictedSearch() throws Exception {

        try{

            Project demo = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));

            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("text")).thenReturn("åäö");
            when(request.getParameter("project")).thenReturn(demo.getKey().toString());
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new SearchServlet().doGet(request, response);


            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONArray hits = new JSONObject(output).getJSONArray("fragments");
            assertThat(hits.length(), is(2));   // A fragment and the headline

            // Now do the same search again, but with a user that cant see the document

            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyEveToken");
            when(request.getParameter("text")).thenReturn("åäö");
            when(request.getParameter("project")).thenReturn(demo.getKey().toString());
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new SearchServlet().doGet(request, response);


            output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            hits = new JSONObject(output).getJSONArray("fragments");
            assertThat(hits.length(), is(0));


        }catch(Exception e){

            e.printStackTrace();
            assertTrue(false);
        }
    }


    @Test
    public void testFailNoSession() throws Exception {

        try{
            request = mock(HttpServletRequest.class);
            response = mock(HttpServletResponse.class);

            Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));

            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("InvalidSessionToken");
            when(request.getParameter("project")).thenReturn(project.getKey().toString());
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new SearchServlet().doGet(request, response);


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
    public void testFailPost() throws Exception {

        try{
            request = mock(HttpServletRequest.class);
            response = mock(HttpServletResponse.class);

            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummySessionToken");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new SearchServlet().doPost(request, response);


            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            JSONObject error = (JSONObject)json.getJSONArray("error").get(0);
            String message = error.getString("message");

            assertThat(message, is("Post not supported in Search"));

        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }


    @Test
    public void testFailDelete() throws Exception {

        try{
            request = mock(HttpServletRequest.class);
            response = mock(HttpServletResponse.class);

            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummySessionToken");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new SearchServlet().doDelete(request, response);


            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            JSONObject error = (JSONObject)json.getJSONArray("error").get(0);
            String message = error.getString("message");

            assertThat(message, is("Delete not supported in Search"));

        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }



}


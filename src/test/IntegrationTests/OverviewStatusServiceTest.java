package test.integrationTests;

import actions.ActionStatusServlet;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import overviewExport.OverviewExportStatusServlet;
import test.MockWriter;
import test.ServletTests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


/*******************************************************************
 *
 *          Testing the service with mocked request and response messages
 *
 */


public class OverviewStatusServiceTest extends ServletTests {


    @AfterClass
    public static void tearDown() {

        helper.tearDown();
    }


    @BeforeClass
    public static void preAmble(){


        init();

    }


    /***********************************************************************************'
     *
     *
     *          Test that the get request returns the complete list of statuses
     *
     *
     *
     * @throws Exception
     */

    private static final int EXPECTED_NO_STATUSES = 6;

        @Test
        public void testGetStatus() throws Exception {

            try{

                MockWriter mockWriter;
                mockWriter = new MockWriter();

                when(request.getParameter("session")).thenReturn("DummyAdminToken");
                when(request.getParameter("project")).thenReturn(demoProject.getKey().toString());
                when(request.getRemoteAddr()).thenReturn("127.0.0.1");
                when(response.getWriter()).thenReturn(mockWriter.getWriter());

                new OverviewExportStatusServlet().doGet(request, response);

                String output = mockWriter.getOutput();
                PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

                JSONObject json = new JSONObject(output);
                JSONObject status = json.getJSONObject("ExportStatus");


        }catch(Exception e){

            e.printStackTrace();
            assertTrue(false);
        }
    }



    @Test
    public void testFailPost() throws Exception {

        try{

            MockWriter mockWriter;


            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new OverviewExportStatusServlet().doPost(request, response);

            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);

            assertVerbose("Not allowed post", output.contains("Post not supported"), is(true));



        }catch(Exception e){

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

            new OverviewExportStatusServlet().doDelete(request, response);

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

            new OverviewExportStatusServlet().doGet(request, response);

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


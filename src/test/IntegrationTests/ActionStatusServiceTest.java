package test.integrationTests;

import actions.ActionStatusServlet;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import test.MockWriter;
import test.ServletTests;

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


public class ActionStatusServiceTest extends ServletTests {


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
     *            - Open
     *            - In Progress
     *            - Completed
     *            - Blocked
     *            - Cancelled
     *            - Accepted
     *
     *
     * @throws Exception
     */

    private static final int EXPECTED_NO_STATUSES = 6;

        @Test
        public void testGetAll() throws Exception {

            try{

                MockWriter mockWriter;


                mockWriter = new MockWriter();

                when(request.getParameter("session")).thenReturn("DummyAdminToken");
                when(request.getRemoteAddr()).thenReturn("127.0.0.1");
                when(response.getWriter()).thenReturn(mockWriter.getWriter());

                new ActionStatusServlet().doGet(request, response);

                String output = mockWriter.getOutput();
                PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

                JSONObject json = new JSONObject(output);
                JSONArray statusList = json.getJSONArray("ActionStatus");

                assertVerbose("There should be "+ EXPECTED_NO_STATUSES+" different action statuses in the system", statusList.length(), is( EXPECTED_NO_STATUSES ));

                // Now try to get an object. We are not testing the content here, just testing that the fields exists

                JSONObject first = statusList.getJSONObject(0);
                first.get("id");
                first.get("name");
                first.get("ordinal");




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

            new ActionStatusServlet().doPost(request, response);

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

            new ActionStatusServlet().doDelete(request, response);

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

            new ActionStatusServlet().doGet(request, response);

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


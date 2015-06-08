package test.integrationTests;

import actions.ActionStatusServlet;
import crossReference.CrossReferenceInternalServlet;
import crossReference.CrossReferenceServlet;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import test.MockWriter;
import test.ServletTests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


/*******************************************************************
 *
 *          Cross reference functionality
 *
 */


public class CrossReferenceTest extends ServletTests {


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
     *
     *
     * @throws Exception
     */


        @Test
        public void testInternal() throws Exception {

            try{

                MockWriter mockWriter;


                mockWriter = new MockWriter();

                when(request.getParameter("session")).thenReturn("DummyAdminToken");
                when(request.getParameter("project")).thenReturn(demoProject.getKey().toString());
                when(request.getRemoteAddr()).thenReturn("127.0.0.1");
                when(response.getWriter()).thenReturn(mockWriter.getWriter());

                new CrossReferenceInternalServlet().doPost(request, response);

                String output = mockWriter.getOutput();
                PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

                JSONObject json = new JSONObject(output);

                //TODO: Add verification of analysis here. This just tested that the process is running


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

            new CrossReferenceServlet().doDelete(request, response);

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

            new CrossReferenceServlet().doGet(request, response);

            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);

            assertVerbose("Not allowed without session", output.contains("No session"), is(true));



        }catch(Exception e){

            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void testFailNoSessionPost() throws Exception {

        try{

            MockWriter mockWriter;


            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("InvalidToken");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new CrossReferenceServlet().doPost(request, response);

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


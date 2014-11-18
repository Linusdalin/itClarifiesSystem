package test;

import backend.ItClarifies;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import contractManagement.*;
import databaseLayer.DBKeyInterface;
import databaseLayer.DatabaseAbstractionFactory;
import log.PukkaLogger;
import net.sf.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.LookupByKey;
import services.ProjectServlet;


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
 *
 *          //TODO: Test get and delete
 */


public class ProjectServiceTest extends ServletTests{


    private static LocalServiceTestHelper helper;
    private static HttpServletRequest request;
    private static HttpServletResponse response;


    @AfterClass
    public static void tearDown() {

        helper.tearDown();
    }


    @BeforeClass
    public static void preAmble(){

        helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
        helper.setUp();



        try {

            request = mock(HttpServletRequest.class);
            response = mock(HttpServletResponse.class);

            PukkaLogger.setLogLevel(PukkaLogger.Level.DEBUG);


        } catch (Exception e) {

            e.printStackTrace();
            assertTrue(false);
        }

    }



    //TODO: Test that the date is correct



    @Test
    public void testCreateAndUpdate() throws Exception {

        try{

            BackOfficeInterface bo;

            bo = new ItClarifies();
            bo.createDb();
            bo.populateValues(true);

            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("name")).thenReturn("Linus Prject");
            when(request.getParameter("description")).thenReturn("A description");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ProjectServlet().doPost(request, response);


            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            // Get the key

            JSONObject json = new JSONObject(output);
            DBKeyInterface key = new DatabaseAbstractionFactory().createKey(json.getString("Project"));

            Project project = new Project(new LookupByKey(key));

            assertTrue(project.exists());
            assertThat(project.getName(), is("Linus Prject"));
            assertThat(project.getDescription(), is("A description"));

            // Now try to update

            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("key")).thenReturn(key.toString());
            when(request.getParameter("name")).thenReturn("Linus Project");
            when(request.getParameter("description")).thenReturn("The correct description");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ProjectServlet().doPost(request, response);


            output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            // Get the key

            json = new JSONObject(output);
            DBKeyInterface newKey = new DatabaseAbstractionFactory().createKey(json.getString("Project"));

            // The update should return a key to the same object

            assertThat(newKey.toString(), is(key.toString()));

            Project rereadProject = new Project(new LookupByKey(key));

            assertTrue(rereadProject.exists());
            assertThat(rereadProject.getName(), is("Linus Project"));
            assertThat(rereadProject.getDescription(), is("The correct description"));






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

            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ProjectServlet().doGet(request, response);


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


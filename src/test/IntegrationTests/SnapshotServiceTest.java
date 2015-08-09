package test.integrationTests;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import project.Project;
import project.ProjectTable;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupItem;
import services.SnapshotServlet;
import test.MockWriter;
import test.ServletTests;


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
 *          //TODO: Test: tests by freezing the project and then change fragments
 */


public class SnapshotServiceTest extends ServletTests {


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
    public void basicTestGet() throws Exception {

        try{

            Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));

            MockWriter writer = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("project")).thenReturn(project.getKey().toString());
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(writer.getWriter());

            new SnapshotServlet().doGet(request, response);

            String output = writer.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);

            // Get the snapshot list

            JSONArray versionList = json.getJSONArray("Snapshot");
            assertThat(versionList.length(), is( 1 ));

            // Check the first element

            JSONObject first = (JSONObject)versionList.get( 0 );

            isKey(first.getString("id"));
            assertThat(first.getString("creation"), is("2014-01-01 00:10:00.0"));
            assertThat(first.getString("name"), is("test freeze"));
            isKey(first.getString("creator"));                       //TODO: Improvement assert that this exists


            // And then create a new one

            String name = "new Freeze";
            int snapshotsBefore = project.getSnapshotsForProject().size();

            writer = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("name")).thenReturn(name);
            when(request.getParameter("project")).thenReturn(project.getKey().toString());
            when(response.getWriter()).thenReturn(writer.getWriter());

            new SnapshotServlet().doPost(request, response);

            output = writer.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            json = new JSONObject(output);
            isKey(json.getString("Snapshot"));


            assertThat(project.getSnapshotsForProject().size(), is(snapshotsBefore + 1));


    }catch(NullPointerException e){

        e.printStackTrace();
        assertTrue(false);
    }
}



    @Test
    public void testFailNoSession() throws Exception {

        try{

            MockWriter writer = new MockWriter();

            when(request.getParameter("session")).thenReturn("InvalidSessionToken");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(writer.getWriter());

            new SnapshotServlet().doGet(request, response);

            String output = writer.getOutput();
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
    public void testFailNoProject() throws Exception {

        try{

            MockWriter writer = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummySessionToken");
            when(request.getParameter("project")).thenReturn(null);
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(writer.getWriter());

            new SnapshotServlet().doGet(request, response);

            String output = writer.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            JSONObject error = (JSONObject)json.getJSONArray("error").get(0);

            assertThat(error.getString("type"), is("GENERAL"));

        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }


}


package test.integrationTests;

import backend.ItClarifies;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import contractManagement.*;
import log.PukkaLogger;
import pukkaBO.condition.*;
import services.AnnotationServlet;
import services.FragmentDetailServlet;
import services.FragmentServlet;
import test.MockWriter;
import test.ServletTests;
import userManagement.PortalUserTable;
import versioning.FreezeSnapshot;
import versioning.Snapshot;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.exceptions.BackOfficeException;
import diff.DiffServlet;

import userManagement.PortalUser;
import versioning.SnapshotTable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/*******************************************************************
 *
 *          Testing the service with mocked request and response messages
 *
 *
 * //TODO: Add more tests by freezing the project and then change fragments
 */


public class DiffServiceTest extends ServletTests {


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
    public void diffWithSameDocument() throws Exception {

        try{

            Contract document = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.Name.name(), "Cannon")));
            ContractVersionInstance latest = document.getHeadVersion();

            MockWriter writer = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("active")).thenReturn(latest.getKey().toString());
            when(request.getParameter("reference")).thenReturn(latest.getKey().toString());
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(writer.getWriter());

            new DiffServlet().doGet(request, response);

            String output = writer.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);

            // Get the version list

            JSONArray versionList = json.getJSONArray("Diff");
            assertThat(versionList.length(), is( 2 ));

            // Check the first element

            JSONObject first = (JSONObject)versionList.get( 0 );

            isKey(first.getString("active"));                             //TODO: Improvement assert that this actually exists
            assertThat(first.getInt("distance"), is(0));
            isKey(first.getString("reference"));                       //TODO: Improvement assert that this exists


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

            new DiffServlet().doGet(request, response);

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

    /***************************************************************'
     *
     *          Not passing mandatory parameters
     *
     *
     * @throws Exception
     */


    @Test
    public void testFailNoVersions() throws Exception {

        try{

            MockWriter writer = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummySessionToken");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(writer.getWriter());
            when(request.getParameter("active")).thenReturn(null);

            new DiffServlet().doGet(request, response);

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


    /*********************************************************************
     *
     *          Basic test for the freeze and diff service
     *
     *           - perform a diff on a document between head and the default snapshot (should be the same)
     *           - modify a fragment
     *           - Retreive fragments based on the snapshot (should now be the unmodified fragment)
     *           - Get the diff between HEAD and the default snapshot (should now be different)
     *           - Create a new freeze fragment
     *           - Get the diff between HEAD and the new freeze snapshot (should be the same)
     *           - Change an annotation and a risk
     *           - Retreive fragments based on the new snapshot (should now include the first modification, not the annotation and risk)
     *
     *
     * @throws Exception
     */

    @Test
    public void freezeAndDiff() throws Exception {

        try{

            Contract document = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.Name.name(), "Cannon")));
            Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));
            PortalUser user = new PortalUser(new LookupItem().addFilter(new ColumnFilter(PortalUserTable.Columns.Name.name(), "admin")));

            Snapshot defaultFreeze = new Snapshot(new LookupItem().addFilter(new ColumnFilter(SnapshotTable.Columns.Name.name(), "test freeze")));

            assertTrue(defaultFreeze.exists());

            ContractVersionInstance head =  document.getHeadVersion();
            ContractVersionInstance frozen = document.getVersionForSnapshot(defaultFreeze);

            assertTrue(head.isSame(frozen));

            MockWriter writer = new MockWriter();

            // perform a diff on a document between head and the default snapshot (should be the same)

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("active")).thenReturn(head.getKey().toString());
            when(request.getParameter("reference")).thenReturn(frozen.getKey().toString());
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(writer.getWriter());

            new DiffServlet().doGet(request, response);

            String output = writer.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);

            // Get the version list

            JSONArray versionList = json.getJSONArray("Diff");
            assertThat(versionList.length(), is( 2 ));

            // Check all the elements


            assertThat(((JSONObject)versionList.get( 0 )).getInt("distance"), is( 0 ));
            assertThat(((JSONObject) versionList.get(1)).getInt("distance"), is(0));

            List<ContractVersionInstance> allVersions = document.getVersionsForDocument();
            for(ContractVersionInstance instance : allVersions){

                PukkaLogger.log(PukkaLogger.Level.INFO, "  -> " + instance.getVersion() + " @ " + instance.getCreation().getSQLTime().toString());
            }


            // Create a new Freeze Snapshot
            //TODO: Replace this with a service call

            FreezeSnapshot freezeSnapshot = new FreezeSnapshot("new Snapshot", project, "desc", user);
            Snapshot newSnapshot = freezeSnapshot.freeze();

            // Retreive fragments based on the snapshot (They should now look the same but be different fragments)

            PukkaLogger.log(PukkaLogger.Level.INFO, " - Created new freeze       (@ " + newSnapshot.getTimestamp().getSQLTime().toString() + ") " + newSnapshot.getName());
            PukkaLogger.log(PukkaLogger.Level.INFO, " - Created freeze version   (@ " + document.getVersionForSnapshot(newSnapshot).getCreation().getSQLTime().toString() + ") " + document.getVersionForSnapshot(newSnapshot).getVersion());
            PukkaLogger.log(PukkaLogger.Level.INFO, " - Created new head version (@ " + document.getHeadVersion().getCreation().getSQLTime().toString() + ") " + document.getHeadVersion().getVersion());


            // Now update head and frozen

            head = document.getHeadVersion();
            frozen = document.getVersionForSnapshot(newSnapshot);

            // Check the fragments. They should have been updates

            assertThat(head.getFragmentsForVersion().size(), is( 2 ));
            assertThat(frozen.getFragmentsForVersion().size(), is( 2 ));


            // Get the first fragment in both versions and compare them

            ContractFragment firstInHead = head.getFragmentsForVersion(new LookupList(new Sorting(ContractFragmentTable.Columns.Ordinal.name(), Ordering.FIRST))).get(0);
            ContractFragment firstSnapshot = frozen.getFragmentsForVersion(new LookupList(new Sorting(ContractFragmentTable.Columns.Ordinal.name(), Ordering.FIRST))).get( 0 );

            PukkaLogger.log(PukkaLogger.Level.INFO, "head  : " + head.getVersion() + " /" + head.getKey().toString());
            PukkaLogger.log(PukkaLogger.Level.INFO, "frozen: " + frozen.getVersion() + " /" + frozen.getKey().toString());

            // Same text. Same type, but they are different instances.

            assertThat(firstInHead.getText(), is(firstSnapshot.getText()));
            assertThat(firstInHead.getType(), is(firstSnapshot.getType()));
            assertFalse(head.isSame(frozen));

            // The annotation should have been copied

            assertThat(firstInHead.getAnnotationsForFragment().size(), is( 1 ));
            assertThat(firstSnapshot.getAnnotationsForFragment().size(), is( 1 ));
            assertThat(firstInHead.getAnnotationCount(), is( (long) 1 ));
            assertThat(firstSnapshot.getAnnotationCount(), is( (long) 1 ));


            // perform a new diff on a document between head and the default snapshot (should still be the same)

            writer = new MockWriter();
            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("active")).thenReturn(head.getKey().toString());
            when(request.getParameter("reference")).thenReturn(frozen.getKey().toString());
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(writer.getWriter());

            new DiffServlet().doGet(request, response);

            output = writer.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            json = new JSONObject(output);

            // Get the version list

            versionList = json.getJSONArray("Diff");
            assertThat(versionList.length(), is( 2 ));

            // Check all the elements

            assertThat(((JSONObject) versionList.get( 0 )).getInt("distance"), is( 0 ));
            assertThat(((JSONObject) versionList.get( 1 )).getInt("distance"), is( 0 ));


            // Now modify the fragment just a bit

            firstInHead.setText(firstInHead.getText() + "!");
            firstInHead.update();

            PukkaLogger.log(PukkaLogger.Level.INFO, "First fragment: " + firstInHead.getText());
            PukkaLogger.log(PukkaLogger.Level.INFO, "First fragment: " + firstSnapshot.getText());

            writer = new MockWriter();
            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("active")).thenReturn(head.getKey().toString());
            when(request.getParameter("reference")).thenReturn(frozen.getKey().toString());
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(writer.getWriter());

            new DiffServlet().doGet(request, response);

            output = writer.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            json = new JSONObject(output);

            // Get the version list

            versionList = json.getJSONArray("Diff");
            assertThat(versionList.length(), is( 2 ));

            // Check all the elements

            assertThat(((JSONObject) versionList.get( 0 )).getInt("distance"), is( 1 ));
            assertThat(((JSONObject) versionList.get( 1 )).getInt("distance"), is( 0 ));

            // Annotate

            writer = new MockWriter();
            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("fragment")).thenReturn(firstInHead.getKey().toString());
            when(request.getParameter("body")).thenReturn("New test annotation in head");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(writer.getWriter());

            new AnnotationServlet().doPost(request, response);

            output = writer.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);


            // Check the annotation in head and in snapshot.
            // There should be one annotation in the freeze snapshot and two in head

            writer = new MockWriter();
            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("fragment")).thenReturn(firstSnapshot.getKey().toString());
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(writer.getWriter());

            new FragmentDetailServlet().doGet(request, response);


            output = writer.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);
            json = new JSONObject(output);
            JSONObject details = json.getJSONObject("FragmentDetail");
            JSONArray annotations = details.getJSONArray("annotations");
            assertThat(annotations.length(), is( 1));


            writer = new MockWriter();
            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("fragment")).thenReturn(firstInHead.getKey().toString());
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(writer.getWriter());

            new FragmentDetailServlet().doGet(request, response);


            output = writer.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);
            json = new JSONObject(output);
            details = json.getJSONObject("FragmentDetail");
            annotations = details.getJSONArray("annotations");
            assertThat(annotations.length(), is(2));

            // Retrieve and check fragments in snapshot based on version

            writer = new MockWriter();
            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("version")).thenReturn(frozen.getKey().toString());
            when(request.getParameter("document")).thenReturn(null);
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(writer.getWriter());

            new FragmentServlet().doGet(request, response);


            output = writer.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);
            json = new JSONObject(output);
            JSONArray fragmentList = json.getJSONArray("Fragment");
            JSONObject firstFragment = (JSONObject)fragmentList.get(0);
            assertThat(firstFragment.getString("text").substring(0, 20), is(firstSnapshot.getText().substring(0, 20)));

            // Retrieve and check fragments in head based on version

            writer = new MockWriter();
            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("version")).thenReturn(head.getKey().toString());
            when(request.getParameter("document")).thenReturn(null);
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(writer.getWriter());

            new FragmentServlet().doGet(request, response);


            output = writer.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);
            json = new JSONObject(output);
            fragmentList = json.getJSONArray("Fragment");
            firstFragment = (JSONObject)fragmentList.get(0);
            assertThat(firstFragment.getString("text").substring(0, 20), is(firstInHead.getText().substring(0, 20)));

            // Retrieve and check fragments in head based on version

            writer = new MockWriter();
            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("version")).thenReturn(null);
            when(request.getParameter("document")).thenReturn(document.getKey().toString());
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(writer.getWriter());

            new FragmentServlet().doGet(request, response);


            output = writer.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);
            json = new JSONObject(output);
            fragmentList = json.getJSONArray("Fragment");
            firstFragment = (JSONObject)fragmentList.get(0);
            assertThat(firstFragment.getString("text").substring(0, 20), is(firstInHead.getText().substring(0, 20)));



        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);

        }catch(BackOfficeException e){

            e.logError("Error in test");
            assertTrue(false);
        }
    }




}


package test.integrationTests;

import backend.ItClarifies;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import contractManagement.*;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import module.ModuleProjectServlet;
import module.ModuleServlet;
import module.ModuleTable;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupItem;
import pukkaBO.condition.LookupList;
import pukkaBO.condition.ReferenceFilter;
import services.ContractServlet;
import test.MockWriter;
import test.ServletTests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


/*******************************************************************
 *
 *          Testing the services in the module package:
 *
 *           /Module
 *           /ModuleOrganization
 *           /ModuleProject
 *
 */


public class ModuleServiceTest extends ServletTests {


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


    /**************************************************************************************
     *
     *              Getting modules:
     *
     *               - Get all modules for the user. This should return all the modules
     *               - Create a new module. Now there should be one more module but it is not accessible for the user.
     *               - Assign the module to the organization
     *               - Now the user will see the module
     *               - A user from another organization will not see the module
     *
     *
     *
     *
     */


    @Test
    public void testGetModules(){

        try{

            assertTrue(demoProject.exists());
            assertTrue(demoModule.exists());

            MockWriter mockWriter;
            String output;


            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ModuleServlet().doGet(request, response);

            output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject jsonResponse = new JSONObject(output);
            JSONArray modules = jsonResponse.getJSONArray("Module");

            assertVerbose("Expecting 2 modules in project " + demoProject.getName(), modules.length(), is( 2 ) );

            // Check that the arguments have the correct names

            JSONObject first = modules.getJSONObject( 0 );
            first.getString("name");
            first.getString("description");
            first.getString("key");


            // Now create a new module and assign it to the organization
            // We expect to find it in the database but not in the module list as it is not public


            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("name")).thenReturn("Custom Module");
            when(request.getParameter("description")).thenReturn("This is the description");
            when(request.getParameter("public")).thenReturn("false");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ModuleServlet().doPost(request, response);

            output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            jsonResponse = new JSONObject(output);

            String module = jsonResponse.getString("Module");

            assertVerbose("Now there should be 3 modules ", new ModuleTable(new LookupList()).getValues().size(), is( (3 )));

            // Accessing the module service should still only return two modules
            // The new module is hidden

            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ModuleServlet().doGet(request, response);

            output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            jsonResponse = new JSONObject(output);
            modules = jsonResponse.getJSONArray("Module");

            assertVerbose("Expecting 2 modules in project " + demoProject.getName(), modules.length(), is( 2 ) );


            // This time we create a new module without hiding it. (Omitting the public flag sets default value false)
            // Now there should be four values in the database and the service should return three.


            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("name")).thenReturn("Public Module");
            when(request.getParameter("description")).thenReturn("This is the description");

            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ModuleServlet().doPost(request, response);

            output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            jsonResponse = new JSONObject(output);

            assertVerbose("Now there should be 4 modules ", new ModuleTable(new LookupList()).getValues().size(), is( ( 4 )));

            // NOw try to get them
            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ModuleServlet().doGet(request, response);

            output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            jsonResponse = new JSONObject(output);
            modules = jsonResponse.getJSONArray("Module");

            assertVerbose("Expecting 3 modules in project " + demoProject.getName(), modules.length(), is( 3 ) );



    }catch(Exception e){

        e.printStackTrace();
        assertTrue(false);
    }
}


    @Test
    public void testUniqueName(){

            try{

                assertTrue(demoProject.exists());
                assertTrue(demoModule.exists());

                MockWriter mockWriter;
                String output;


                mockWriter = new MockWriter();

                when(request.getParameter("session")).thenReturn("DummyAdminToken");
                when(request.getRemoteAddr()).thenReturn("127.0.0.1");
                when(response.getWriter()).thenReturn(mockWriter.getWriter());

                new ModuleServlet().doGet(request, response);

                output = mockWriter.getOutput();
                PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

                JSONObject jsonResponse = new JSONObject(output);
                JSONArray modules = jsonResponse.getJSONArray("Module");

                JSONObject first = modules.getJSONObject( 0 );
                String existingName = first.getString("name");


                // Now create a new module with the same name as the last one

                mockWriter = new MockWriter();

                when(request.getParameter("session")).thenReturn("DummyAdminToken");
                when(request.getParameter("name")).thenReturn( existingName );
                when(request.getParameter("description")).thenReturn("This is the description");
                when(request.getParameter("public")).thenReturn("false");
                when(request.getRemoteAddr()).thenReturn("127.0.0.1");
                when(response.getWriter()).thenReturn(mockWriter.getWriter());

                new ModuleServlet().doPost(request, response);

                output = mockWriter.getOutput();
                PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

                JSONObject json = new JSONObject(output);
                JSONObject error = (JSONObject)json.getJSONArray("error").get(0);

                assertThat(error.getString("type"), is("DATA"));

            }catch(Exception e){

                e.printStackTrace();
                assertTrue(false);
            }


    }


    @Test
    public void testFailNoSession() throws Exception {

        try{
            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("InvalidSessionToken");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ModuleServlet().doGet(request, response);

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

    /*******************************************************************************
     *
     *          No access to the modules for a user of another organization
     *
     *
     * @throws Exception
     */


    @Test
    public void testNoAccess() throws Exception {

        try{
            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyEveToken");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ModuleServlet().doGet(request, response);

            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject jsonResponse = new JSONObject(output);
            JSONArray modules = jsonResponse.getJSONArray("Module");

            assertVerbose("Expecting No modules for user eve " + demoProject.getName(), modules.length(), is( 0 ) );

        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }



    @Test
    public void testGetModulesForProject(){

        try{

            assertTrue(demoProject.exists());
            assertTrue(demoModule.exists());

            MockWriter mockWriter;
            String output;


            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("project")).thenReturn(demoProject.getKey().toString());
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ModuleProjectServlet().doGet(request, response);

            output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject jsonResponse = new JSONObject(output);
            JSONArray modules = jsonResponse.getJSONArray("ModuleForProject");

            assertVerbose("Expecting 2 modules in project " + demoProject.getName(), modules.length(), is( 2 ) );

            // Check that the arguments have the correct names

            JSONObject first = modules.getJSONObject(0);
            first.getString("name");
            first.getString("description");
            first.getString("key");


    }catch(Exception e){

        e.printStackTrace();
        assertTrue(false);
    }
}


    @Test
    public void testFailNoSession2() throws Exception {

        try{
            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("InvalidSessionToken");
            when(request.getParameter("project")).thenReturn(demoProject.getKey().toString());
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ModuleProjectServlet().doGet(request, response);

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

    /*******************************************************************************
     *
     *          No access to the modules for a user of another organization
     *
     *          This should detect that there is no access to the project,
     *          not return an empty list
     *
     *
     * @throws Exception
     */


    @Test
    public void testNoAccess2() throws Exception {

        try{
            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyEveToken");
            when(request.getParameter("project")).thenReturn(demoProject.getKey().toString());
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ModuleProjectServlet().doGet(request, response);

            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            JSONObject error = (JSONObject)json.getJSONArray("error").get(0);

            assertThat(error.getString("type"), is("DATA"));

        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }




}


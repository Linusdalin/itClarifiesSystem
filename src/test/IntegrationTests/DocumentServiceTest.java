package test.integrationTests;

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
import pukkaBO.condition.ReferenceFilter;
import services.ContractServlet;
import test.MockWriter;
import test.ServletTests;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/*******************************************************************
 *
 *          Testing the service with mocked request and response messages
 *
 */


public class DocumentServiceTest extends ServletTests {


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

            PukkaLogger.setLogLevel(PukkaLogger.Level.DEBUG);

            request = mock(HttpServletRequest.class);
            response = mock(HttpServletResponse.class);

        } catch (Exception e) {

            e.printStackTrace();
            assertTrue(false);
        }

    }



        @Test
        public void testGetDocumentsForProject(){

            try{

                BackOfficeInterface bo;

                bo = new ItClarifies();
                bo.createDb();
                bo.populateValues(true);

                Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));
                assertTrue(project.exists());

                assertDocumentsInProject(project);
                MockWriter mockWriter;
                String output;

                // Now test to delete


            Contract contract = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.Name.name(), "Cannon")));
            assertTrue(contract.exists());

            ContractVersionInstanceTable instances = new ContractVersionInstanceTable(new LookupItem().addFilter(new ReferenceFilter(ContractVersionInstanceTable.Columns.Document.name(), contract.getKey())));
            assertThat(instances.getValues().size(), is(1));


            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("key")).thenReturn(contract.getKey().toString());
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ContractServlet().doDelete(request, response);


            output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject jsonResponse = new JSONObject(output);
            JSONObject info = jsonResponse.getJSONObject("deleted");

            // Delete should have deleted 1 instance with 2 clauses and 2 fragments. This should have been reported

            assertThat((Integer)info.get("instances"),  is( 1 ));
            assertThat((Integer)info.get("clauses"),    is( 2 ));
            assertThat((Integer)info.get("fragments"),  is( 2 ));

            ContractVersionInstanceTable instancesAfterDelete = new ContractVersionInstanceTable(new LookupItem().addFilter(new ReferenceFilter(ContractVersionInstanceTable.Columns.Version.name(), contract.getKey())));
            assertThat(instancesAfterDelete.getValues().size(), is( 0 ));



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
            when(request.getParameter("body")).thenReturn("Updated");

            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ContractServlet().doGet(request, response);


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
    public void testPost() throws Exception {

        try{
            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummySessionToken");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ContractServlet().doPost(request, response);


            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            JSONObject error = (JSONObject)json.getJSONArray("error").get(0);
            String message = error.getString("message");

            assertThat(message, is("Post not supported in Document"));

        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }





}


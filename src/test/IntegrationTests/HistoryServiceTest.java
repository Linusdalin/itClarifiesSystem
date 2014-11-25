package test.integrationTests;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import contractManagement.Contract;
import contractManagement.ContractTable;
import backend.ItClarifies;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupItem;
import services.HistoryServlet;
import test.MockWriter;
import test.ServletTests;


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


public class HistoryServiceTest extends ServletTests {


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
    public void basicTest() throws Exception {

        try{

            Contract document = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.Name.name(), "Cannon")));

            MockWriter writer = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("document")).thenReturn(document.getKey().toString());
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(writer.getWriter());

            new HistoryServlet().doGet(request, response);

            String output = writer.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);

            // Get the version list

            JSONArray versionList = json.getJSONArray("VersionHistory");
            assertThat(versionList.length(), is( 1 ));

            // Check the first element

            JSONObject first = (JSONObject)versionList.get( 0 );

            isKey(first.getString("id"));                             //TODO: Improvement assert that this actually exists
            assertThat(first.getString("creation"), is("2014-01-01 00:09:00.0"));
            assertThat(first.getString("name"), is("Cannon v1.0"));
            isKey(first.getString("creator"));                       //TODO: Improvement assert that this exists


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

            new HistoryServlet().doGet(request, response);

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
    public void testFailNoDocument() throws Exception {

        try{

            MockWriter writer = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummySessionToken");
            when(request.getParameter("document")).thenReturn(null);
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(writer.getWriter());

            new HistoryServlet().doGet(request, response);

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


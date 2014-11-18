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
import services.FragmentDetailServlet;
import services.FragmentServlet;


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
 */


public class FragmentDetailServiceTest extends ServletTests {


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



        } catch (Exception e) {

            e.printStackTrace();
            assertTrue(false);
        }

    }


    /**************************************************************************
     *
     *      Getting all the details for a fragments of a document
     *
     *
     * @throws Exception
     */


    @Test
    public void testGetFragmentDetails() throws Exception {

        try{

            BackOfficeInterface bo;

            bo = new ItClarifies();
            bo.createDb();
            bo.populateValues(true);

            Contract document = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.Name.name(), "Cannon")));

            ContractFragment fragment = new ContractFragment(new LookupItem().addFilter(new ColumnFilter(ContractFragmentTable.Columns.Name.name(), "first fragment")));

            request = mock(HttpServletRequest.class);
            response = mock(HttpServletResponse.class);

            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("fragment")).thenReturn(fragment.getKey().toString());
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new FragmentDetailServlet().doGet(request, response);


            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject details = new JSONObject(output).getJSONObject("FragmentDetail");
            JSONArray annotations       = details.getJSONArray("annotations");
            JSONArray classifications   = details.getJSONArray("classifications");
            JSONArray references        = details.getJSONArray("references");


            assertThat(annotations.length(), is( 1 ));
            assertThat(classifications.length(), is( 1 ));
            assertThat(references.length(), is( 1 ));



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

            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("InvalidSessionToken");
            when(request.getParameter("body")).thenReturn("Updated");

            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new FragmentServlet().doPost(request, response);


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


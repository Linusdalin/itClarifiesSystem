package test.integrationTests;

import backend.ItClarifies;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import contractManagement.*;
import log.PukkaLogger;
import net.sf.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.*;
import risk.ContractRisk;
import risk.RiskClassification;
import risk.RiskClassificationTable;
import risk.RiskFlagServlet;
import test.MockWriter;
import test.ServletTests;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static junit.framework.Assert.assertNotNull;
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


public class RiskServiceTest extends ServletTests {


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
    public void testSetRisk() throws Exception {


        try {

            ContractFragment fragment = new ContractFragment(new LookupItem().addFilter(new ColumnFilter(ContractFragmentTable.Columns.Name.name(), "first fragment")));
            ContractRisk newRisk = ContractRisk.getBlack();

            PukkaLogger.log(PukkaLogger.Level.INFO, "Risk is " + fragment.getRisk().getName() + " for fragment " + fragment.getName());

            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("fragment")).thenReturn(fragment.getKey().toString());
            when(request.getParameter("comment")).thenReturn("some comment");
            when(request.getParameter("pattern")).thenReturn("some pattern");
            when(request.getParameter("risk")).thenReturn(newRisk.getKey().toString());
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new RiskFlagServlet().doPost(request, response);


            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            String reply = json.getString("Risk");
            isKey(reply);

            // Risk is stored both in the fragment and in the risk classification table


            ContractFragment readBack = new ContractFragment(new LookupByKey(fragment.getKey()));
            assertThat(readBack.getRisk().isSame(newRisk), is(true));

            RiskClassification newClassification = readBack.getRiskClassificationsForFragment(new LookupList().addOrdering(RiskClassificationTable.Columns.Time.name(), Ordering.LAST)).get( 0 );

            assertTrue(newClassification.exists());
            assertTrue(newClassification.getFragment().isSame(fragment));
            assertThat(newClassification.getComment(), is("some comment"));
            assertThat(newClassification.getPattern(), is("some pattern"));

            assertThat("Should be the same version", newClassification.getVersionId().toString(), is(fragment.getVersionId().toString()));

            PukkaLogger.log(PukkaLogger.Level.INFO, "Risk is now " + readBack.getRisk().getName() + " for fragment " + fragment.getName() + "( set " + newClassification.getTime().getISODate() + ")");


        } catch (Exception e) {

            e.printStackTrace();
            assertTrue(false);
        }


    }






    @Test
    public void testFailNoSession() throws Exception {

        try{
            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("InvalidSessionToken");
            when(request.getParameter("key")).thenReturn("anything");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");

            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new RiskFlagServlet().doPost(request, response);


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
    public void testDelete() throws Exception {

        try{
            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummySessionToken");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new RiskFlagServlet().doDelete(request, response);


            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            JSONObject error = (JSONObject)json.getJSONArray("error").get(0);
            String message = error.getString("message");

            assertThat(message, is("Delete not supported in Risk"));

        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }




    @Test
    public void testGet() throws Exception {

        try{
            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummySessionToken");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new RiskFlagServlet().doGet(request, response);


            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            JSONObject risk1 = (JSONObject)json.getJSONArray("Risk").get(0);
            assertThat(risk1.getString("name"), is("Blocker"));
            assertNotNull(risk1.getString("id"));


        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }





}


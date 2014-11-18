package test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import backend.ItClarifies;
import databaseLayer.DBKeyInterface;
import databaseLayer.DatabaseAbstractionFactory;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pukkaBO.backOffice.BackOfficeInterface;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.servlet.http.*;

import pukkaBO.condition.LookupByKey;
import services.ItClarifiesService;
import services.LoginServlet;
import services.LogoutServlet;

import userManagement.PortalUserExternalServlet;
import userManagement.PortalUserServlet;
import userManagement.PortalUser;
import userManagement.SessionManagement;


/*******************************************************************
 *
 *          Testing the service with mocked request and response messages
 *
 */


public class LoginServiceTest extends ServletTests {


    private static BackOfficeInterface bo;
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

        bo = new ItClarifies();
        bo.createDb();
        bo.populateValues(true);
        bo.populateSpecificValues();

        PukkaLogger.setLogLevel(PukkaLogger.Level.DEBUG);

        try {

            request = mock(HttpServletRequest.class);
            response = mock(HttpServletResponse.class);

            PukkaLogger.setLogLevel(PukkaLogger.Level.DEBUG);


        } catch (Exception e) {

            e.printStackTrace();
            assertTrue(false);
        }

    }

    /****************************************************************************************
     *
     *          Main test use case
     *
     *           - Login
     *           - Verify that the session validates
     *           - Verifu that the login response returned the correct user
     *           - Get user details
     *           - Logout
     *           - Verify that the session does not validate
     *
     *
     * @throws Exception
     */


    @Test
    public void testLogin() throws Exception {

        try{

            String ipAddress = "127.0.0.1";

            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("user")).thenReturn("demo");
            when(request.getParameter("password")).thenReturn("demodemo");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new LoginServlet().doPost(request, response);

            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            String token = json.getString("Token");
            String id = json.getString("user");

            DBKeyInterface userKey = new DatabaseAbstractionFactory().createKey(id);

            PortalUser portalUser = new PortalUser(new LookupByKey(userKey));

            assertThat(token, is("DummySessionToken"));
            assertTrue(portalUser.exists());
            assertThat(portalUser.getName(), is("demo"));

            // Check the session

            SessionManagement sessionManagement = new SessionManagement();
            assertTrue(sessionManagement.validate(token, ipAddress));

            // Check User details

            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn(token);
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new PortalUserServlet().doGet(request, response);

            output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            json = new JSONObject(output);
            JSONObject user = json.getJSONObject("PortalUser");

            assertThat(user.getString("name"),          is("demo"));
            assertThat(user.getString("email"),         is("demo@dev.null"));
            assertThat(user.getString("organization"),  is("demo.org"));


            // Now log out

            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn(token);
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new LogoutServlet().doPost(request, response);

            output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            json = new JSONObject(output);
            assertThat(json.getString("status"), is("closed"));

            assertFalse(sessionManagement.validate(token, ipAddress));


            // Fail getting User details

            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn(token);
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new PortalUserServlet().doGet(request, response);

            output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            json = new JSONObject(output);
            assertError(json, ItClarifiesService.ErrorType.SESSION);

            // Still possible to get the external user details
            // Using another user here

            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("user")).thenReturn(portalUser.getKey().toString());
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new PortalUserExternalServlet().doGet(request, response);

            output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            json = new JSONObject(output);
            JSONArray users = json.getJSONArray("PortalUserExternal");
            JSONObject first = (JSONObject)users.get(0);
            assertThat(first.getString("name"), is("demo"));


        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }


    @Test
    public void testDeleteLogin() throws Exception {

        try{
            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("user")).thenReturn("demo");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new LoginServlet().doDelete(request, response);


            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            JSONObject error = (JSONObject)json.getJSONArray("error").get(0);
            String message = error.getString("message");

            assertThat(message, is("Delete not supported in Login"));

        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }



    @Test
    public void testFailPassword() throws Exception {

        try{
            MockWriter mockWriter;

            mockWriter = new MockWriter();

            when(request.getParameter("user")).thenReturn("demo");
            when(request.getParameter("password")).thenReturn("wrong pwd");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new LoginServlet().doPost(request, response);

            String output = mockWriter.getOutput();

            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);

            assertError(json, ItClarifiesService.ErrorType.SESSION);


        }catch(Exception e){

            e.printStackTrace();
            assertTrue(false);

        }
    }


    @Test
    public void testFailUser() throws Exception {

        try{
            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("user")).thenReturn("unknown user");
            when(request.getParameter("password")).thenReturn("demo");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new LoginServlet().doPost(request, response);


            String output = mockWriter.getOutput();

            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);

            assertError(json, ItClarifiesService.ErrorType.SESSION);


        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);

        }catch(JSONException e){

            e.printStackTrace();
            assertTrue(false);
        }


    }

    @Test
    public void userList() throws Exception {

        try{

            MockWriter mockWriter;
            String output;
            JSONObject json;

            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("user")).thenReturn(null);
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new PortalUserExternalServlet().doGet(request, response);

            output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            json = new JSONObject(output);
            JSONArray userList = json.getJSONArray("PortalUserExternal");
            assertThat(userList.length(), is( 3 ));  // Three users have the same org

        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }



}


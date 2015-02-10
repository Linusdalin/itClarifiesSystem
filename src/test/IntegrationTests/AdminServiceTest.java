package test.integrationTests;

import actions.ChecklistServlet;
import api.AdminServlet;
import backend.ItClarifies;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.sun.security.auth.UserPrincipal;
import contractManagement.Project;
import contractManagement.ProjectTable;
import databaseLayer.DBKeyInterface;
import databaseLayer.DatabaseAbstractionFactory;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupItem;
import test.MockWriter;
import test.ServletTests;
import userManagement.PortalUser;
import userManagement.PortalUserTable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/*******************************************************************
 *
 *          Testing the service with mocked request and response messages
 *
 */


public class AdminServiceTest extends ServletTests {


    private static LocalServiceTestHelper helper;
    private static HttpServletRequest request;
    private static HttpServletResponse response;
    private static UserServiceFactory userServiceFactory;
    private static UserService userService;

    private static BackOfficeInterface bo;

    @AfterClass
    public static void tearDown() {

        helper.tearDown();
    }


    @BeforeClass
    public static void preAmble(){

        helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig())
                .setEnvIsAdmin(true)
                .setEnvIsLoggedIn(true)
                .setEnvAuthDomain("example.com")
                .setEnvEmail("test@example.com");


        helper.setUp();


        PukkaLogger.setLogLevel(PukkaLogger.Level.DEBUG);

        try {

            request = mock(HttpServletRequest.class);
            response = mock(HttpServletResponse.class);

            bo = new ItClarifies();
            bo.createDb();
            bo.populateValues(true);


            PukkaLogger.setLogLevel(PukkaLogger.Level.DEBUG);

        } catch (Exception e) {

            e.printStackTrace();
            assertTrue(false);
        }

    }

    /******************************************************************************
     *
     *
     *
     * @throws Exception
     */


    @Test
    public void testBasicAccess() throws Exception {

        try{

            MockWriter mockWriter;


            mockWriter = new MockWriter();

            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());
            when(request.getUserPrincipal()).thenReturn(new UserPrincipal("linus.dalin@itclarifies.com"));

            new AdminServlet().doGet(request, response);

            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "HTML: " + output);


        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }


    @Test
    public void failAccess() throws Exception {

        try{

            MockWriter mockWriter;


            mockWriter = new MockWriter();

            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());
            when(request.getUserPrincipal()).thenReturn(new UserPrincipal("badie@uglycorp.com"));

            new AdminServlet().doGet(request, response);

            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "HTML: " + output);
            assertThat(output.contains("Service not available"), is(true));


        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }



}


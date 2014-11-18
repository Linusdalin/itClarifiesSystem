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
import services.*;

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


public class AccessRightOrganizationTest extends ServletTests{


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

            BackOfficeInterface bo;

            bo = new ItClarifies();
            bo.createDb();
            bo.populateValues(true);

            PukkaLogger.setLogLevel(PukkaLogger.Level.DEBUG);

            request = mock(HttpServletRequest.class);
            response = mock(HttpServletResponse.class);

        } catch (Exception e) {

            e.printStackTrace();
            assertTrue(false);
        }

    }

    /*******************************************************************
     *
     *
     *          eve belongs to another organization and should not be able to do anything
     *
     * @throws Exception
     */


        @Test
        public void testDocumentsForProjectOutsideOrg() throws Exception {


            try{


                Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));
                assertTrue(project.exists());

                MockWriter mockWriter = new MockWriter();

                when(request.getParameter("session")).thenReturn("DummyEveToken");
                when(request.getParameter("project")).thenReturn(project.getKey().toString());
                when(response.getWriter()).thenReturn(mockWriter.getWriter());

                new ContractServlet().doGet(request, response);


                String output = mockWriter.getOutput();
                PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

                JSONObject json = new JSONObject(output);
                JSONArray documents = json.getJSONArray("Document");

                assertThat(documents.length(), is(0));



        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }

    /***************************************************************************************
     *
     *      The fact that the Google document is shared in the organization
     *      does not mean eve should see it.
     *
     *
     * @throws Exception
     */

    @Test
    public void testFailFragmentsForSharedDocument() throws Exception {

        try{

            Contract document = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.Name.name(), "Google Analytics")));

            request = mock(HttpServletRequest.class);
            response = mock(HttpServletResponse.class);

            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyEveToken");
            when(request.getParameter("document")).thenReturn(document.getKey().toString());
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new FragmentServlet().doGet(request, response);


            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            JSONArray errors = json.getJSONArray("error");

            assertThat(errors.length(), is(1));

            JSONObject error = (JSONObject)errors.get( 0 );

            assertThat(error.getString("message"), is( "Contract not found" ));
            assertThat(error.getString("type"), is( "PERMISSION" ));


        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }



    @Test
    public void testFailDeleteProject() throws Exception {

        try{

            Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));

            request = mock(HttpServletRequest.class);
            response = mock(HttpServletResponse.class);

            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyEveToken");
            when(request.getParameter("Key")).thenReturn(project.getKey().toString());
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ProjectServlet().doDelete(request, response);


            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            JSONArray errors = json.getJSONArray("error");

            assertThat(errors.length(), is(1));

            JSONObject error = (JSONObject)errors.get( 0 );

            assertThat(error.getString("message"), is( "Project read only" ));
            assertThat(error.getString("type"), is( "PERMISSION" ));


        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }




}


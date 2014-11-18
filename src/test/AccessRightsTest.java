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
import pukkaBO.condition.ReferenceFilter;
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


public class AccessRightsTest extends ServletTests{


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
     *          demo user will only be able to access one of the documents
     *
     * @throws Exception
     */


        @Test
        public void testDocumentsForProjectWithRestrictedAccess() throws Exception {

            try{


                Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));
                assertTrue(project.exists());

                MockWriter mockWriter = new MockWriter();

                when(request.getParameter("session")).thenReturn("DummySessionToken");
                when(request.getParameter("project")).thenReturn(project.getKey().toString());
                when(response.getWriter()).thenReturn(mockWriter.getWriter());

                new ContractServlet().doGet(request, response);


                String output = mockWriter.getOutput();
                PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

                JSONObject json = new JSONObject(output);
                JSONArray documents = json.getJSONArray("Document");

                assertThat(documents.length(), is(1));

                JSONObject doc1 = (JSONObject)documents.get( 0 );

                assertThat(doc1.getString("name"), is("Google Analytics"));
                isKey(doc1.getString("id"));




        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }


    @Test
    public void testFailFragmentsForRestrictedDocument() throws Exception {

        try{

            Contract document = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.Name.name(), "Cannon")));

            request = mock(HttpServletRequest.class);
            response = mock(HttpServletResponse.class);

            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummySessionToken");
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


        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }


    /****************************************************************************'
     *
     *          The demo user does not have access to the Cannon document
     *          Accessing a fragment in the document should fail
     *
     * @throws Exception
     *
     */

    @Test
    public void testFailFragmentDetailsForDocument() throws Exception {

        try{

            Contract document = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.Name.name(), "Cannon")));
            ContractFragment fragment = new ContractFragment(new LookupItem().addFilter(new ColumnFilter(ContractFragmentTable.Columns.Name.name(), "first fragment")));



            request = mock(HttpServletRequest.class);
            response = mock(HttpServletResponse.class);

            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummySessionToken");
            when(request.getParameter("fragment")).thenReturn(fragment.getKey().toString());
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new FragmentDetailServlet().doGet(request, response);


            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            JSONArray errors = json.getJSONArray("error");

            assertThat(errors.length(), is(1));

            JSONObject error = (JSONObject)errors.get( 0 );

            assertThat(error.getString("message"), is( "Contract not found" ));


        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }



    /****************************************************************************'
     *
     *          The demo user does not have access to the Cannon document
     *          Accessing a fragment in the document should fail
     *
     * @throws Exception
     *
     */

    @Test
    public void testFailAnnotate() throws Exception {

        try{

            Contract document = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.Name.name(), "Cannon")));
            ContractFragment fragment = new ContractFragment(new LookupItem().addFilter(new ColumnFilter(ContractFragmentTable.Columns.Name.name(), "first fragment")));

            request = mock(HttpServletRequest.class);
            response = mock(HttpServletResponse.class);

            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummySessionToken");
            when(request.getParameter("fragment")).thenReturn(fragment.getKey().toString());
            when(request.getParameter("body")).thenReturn("Testing to annotate this");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new AnnotationServlet().doPost(request, response);


            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            JSONArray errors = json.getJSONArray("error");

            assertThat(errors.length(), is(1));

            JSONObject error = (JSONObject)errors.get( 0 );

            assertThat(error.getString("message"), is( "Contract read only" ));


        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }

    /****************************************************************************'
     *
     *          The demo user does not have access to the Cannon document
     *          Accessing a fragment in the document should fail
     *
     * @throws Exception
     *
     */

    @Test
    public void testFailModifyAnnotation() throws Exception {

        try{

            Contract document = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.Name.name(), "Cannon")));
            ContractFragment fragment = new ContractFragment(new LookupItem().addFilter(new ColumnFilter(ContractFragmentTable.Columns.Name.name(), "first fragment")));

            ContractAnnotation annotation = new ContractAnnotation(new LookupItem().addFilter(new ReferenceFilter(ContractAnnotationTable.Columns.Fragment.name(), fragment.getKey())));
            request = mock(HttpServletRequest.class);
            response = mock(HttpServletResponse.class);

            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummySessionToken");
            when(request.getParameter("annotation")).thenReturn(annotation.getKey().toString());
            when(request.getParameter("body")).thenReturn("Testing to annotate this");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new AnnotationServlet().doPost(request, response);


            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            JSONArray errors = json.getJSONArray("error");

            assertThat(errors.length(), is(1));

            JSONObject error = (JSONObject)errors.get( 0 );

            assertThat(error.getString("message"), is( "Contract read only" ));


        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }


    /****************************************************************************'
     *
     *          The demo user does not have access to the Cannon document
     *          Accessing a fragment in the document should fail
     *
     * @throws Exception
     *
     */

    @Test
    public void testFailDelete() throws Exception {

        try{

            Contract document = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.Name.name(), "Cannon")));
            ContractFragment fragment = new ContractFragment(new LookupItem().addFilter(new ColumnFilter(ContractFragmentTable.Columns.Name.name(), "first fragment")));
            ContractAnnotation annotation = new ContractAnnotation(new LookupItem().addFilter(new ReferenceFilter(ContractAnnotationTable.Columns.Fragment.name(), fragment.getKey())));
            request = mock(HttpServletRequest.class);
            response = mock(HttpServletResponse.class);

            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummySessionToken");
            when(request.getParameter("annotation")).thenReturn(annotation.getKey().toString());
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new AnnotationServlet().doDelete(request, response);


            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            JSONArray errors = json.getJSONArray("error");

            assertThat(errors.length(), is(1));

            JSONObject error = (JSONObject)errors.get( 0 );

            assertThat(error.getString("message"), is( "Contract read only" ));


        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }


    /****************************************************************************'
     *
     *          The demo user does not have access to the Cannon document
     *          Accessing a fragment in the document should fail
     *
     * @throws Exception
     *
     */

    @Test
    public void testFailDeleteProject() throws Exception {

        try{

            Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));

            request = mock(HttpServletRequest.class);
            response = mock(HttpServletResponse.class);

            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummySessionToken");
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


        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }




}


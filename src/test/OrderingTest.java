package test;

import backend.ItClarifies;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import contractManagement.*;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupItem;
import pukkaBO.exceptions.BackOfficeException;
import services.ContractServlet;
import services.OrderingServlet;
import services.ProjectServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2014-09-07
 * Time: 09:16
 * To change this template use File | Settings | File Templates.
 */
public class OrderingTest extends ServletTests{


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
        public void testBasicReorder() throws Exception {

            try{

                Contract google = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.Name.name(), "Google Analytics")));
                Contract cannon = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.Name.name(), "Cannon")));
                Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));

                MockWriter mockWriter;
                String output;

                assertThat("Precondition: Test document \"Google Analytics\" exists", google.exists(), is(true));
                assertThat("Precondition: Test document \"Cannon\" exists", cannon.exists(), is(true));

                assertDocumentsInProject(project);

                JSONArray newOrder = new JSONArray()
                        .put(new JSONObject().put("key", google.getKey().toString()))
                        .put(new JSONObject().put("key", cannon.getKey().toString()))
                ;


                mockWriter = new MockWriter();

                when(request.getParameter("session")).thenReturn("DummyAdminToken");
                when(request.getParameter("project")).thenReturn(project.getKey().toString());
                when(request.getParameter("documents")).thenReturn(newOrder.toString());
                when(response.getWriter()).thenReturn(mockWriter.getWriter());

                new OrderingServlet().doPost(request, response);


                output = mockWriter.getOutput();
                PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

                // Get the key

                JSONObject json = new JSONObject(output);

                // Now verify that the documents come in the other order

                    mockWriter = new MockWriter();

                    when(request.getParameter("session")).thenReturn("DummyAdminToken");
                    when(request.getParameter("project")).thenReturn(project.getKey().toString());
                    when(response.getWriter()).thenReturn(mockWriter.getWriter());

                    new ContractServlet().doGet(request, response);


                    output = mockWriter.getOutput();
                    PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

                    json = new JSONObject(output);
                    JSONArray documents = json.getJSONArray("Document");

                    assertThat(documents.length(), CoreMatchers.is(2));

                    JSONObject doc1 = (JSONObject)documents.get( 0 );
                    JSONObject doc2 = (JSONObject)documents.get( 1 );

                    assertThat(doc1.getString("name"), CoreMatchers.is("Cannon"));
                    isKey(doc1.getString("id"));
                    assertThat(doc2.getString("name"), CoreMatchers.is("Google Analytics"));


        }catch(Exception e){

            e.printStackTrace();
            assertTrue(false);
        }
    }


    @Test
    public void testFailReorder() throws Exception {

        try{

            Contract google = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.Name.name(), "Google Analytics")));
            Contract cannon = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.Name.name(), "Cannon")));
            Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));

            MockWriter mockWriter;
            String output;

            assertThat("Precondition: Test document \"Google Analytics\" exists", google.exists(), is(true));
            assertThat("Precondition: Test document \"Cannon\" exists", cannon.exists(), is(true));

            assertDocumentsInProject(project);

            JSONArray newOrderMissingElement = new JSONArray()
                    .put(new JSONObject().put("key", google.getKey().toString()))
            ;


            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("project")).thenReturn(project.getKey().toString());
            when(request.getParameter("documents")).thenReturn(newOrderMissingElement.toString());
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new OrderingServlet().doPost(request, response);


            output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            // Get the error message

            JSONObject json = new JSONObject(output);
            JSONObject first = (JSONObject)json.getJSONArray("error").get(0);

            assertThat("Should be a general error", first.getString("type"), is("GENERAL"));

            assertDocumentsInProject(project);


    }catch(NullPointerException e){

        e.printStackTrace();
        assertTrue(false);
    }
}



}

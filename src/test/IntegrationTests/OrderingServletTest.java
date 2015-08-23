package test.integrationTests;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import contractManagement.*;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hamcrest.CoreMatchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import project.Project;
import project.ProjectTable;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupItem;
import services.ContractServlet;
import services.OrderingServlet;
import test.MockWriter;
import test.ServletTests;

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
public class OrderingServletTest extends ServletTests {


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


    /***************************************************************************************
     *
     *      Reordering is done by sending an array with document keys, indicating
     *      the new ordering of documents
     *
     *
     * @throws Exception
     */


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
                when(request.getRemoteAddr()).thenReturn("127.0.0.1");
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
                    when(request.getRemoteAddr()).thenReturn("127.0.0.1");
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

    /********************************************************************************
     *
     *          Gracefully failing to reorder with an array of indexes that is not
     *          the same length as the number of documents
     *
     *
     * @throws Exception
     */


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
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
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

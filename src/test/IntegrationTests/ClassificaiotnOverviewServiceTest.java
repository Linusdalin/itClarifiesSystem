package test.integrationTests;

import actions.ChecklistServlet;
import backend.ItClarifies;
import classification.ClassificationOverviewServlet;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.when;


/*******************************************************************
 *
 *          Testing the service with mocked request and response messages
 *
 */


public class ClassificaiotnOverviewServiceTest extends ServletTests {


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
     *          First get the list of all checklists in the project.
     *          Look at the first (only) item in the list to verify parameters
     *
     *          get the key to the first checklist
     *
     *          Retrieve the item with the key and verify parameters
     *
     *
     * @throws Exception
     */


    @Test
    public void testBasicAccess() throws Exception {

        try{

            MockWriter mockWriter;

            Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));

            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("project")).thenReturn(project.getKey().toString());
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ClassificationOverviewServlet().doGet(request, response);

            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);

            JSONObject topNode = json
                    .getJSONObject("ClassificationOverview");

            validateClassification(topNode);

        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }

    private void validateClassification(JSONObject node) {

        System.out.println("Validating classification node " + node.getString("classification"));
        JSONArray children = node.getJSONArray("subClassifications");

        if(!children.isEmpty()){

            for(int i = 0; i < children.length(); i++){

                validateClassification(children.getJSONObject( i ));
            }

        }

        assertNotNull(node.getString("classification"));
        assertNotNull(node.getString("statistics"));
        assertNotNull(node.getString("comment"));


    }


}


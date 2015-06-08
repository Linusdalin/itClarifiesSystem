package test.functionalTests;

import backend.ItClarifies;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import contractManagement.*;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupItem;

import test.MockWriter;
import test.ServletTests;
import userManagement.ACSServlet;
import services.ContractServlet;

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


public class ChangAccessRightsTest extends ServletTests {


    @AfterClass
    public static void tearDown() {

        helper.tearDown();
    }


    @BeforeClass
    public static void preAmble(){

        init();


    }

    /*******************************************************************
     *
     *
     *          The owner of a document should be able to change the permissions
     *
     * @throws Exception
     */


        @Test
        public void testChangeRights() throws Exception {

            try{

                Contract restrictedDocument = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.Name.name(), "Cannon")));
                Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));

                assertVerbose("Prerequisite: Document exists.", restrictedDocument.exists(), is(true));

                // First just check. We can only get one document

                MockWriter mockWriter = new MockWriter();

                when(request.getParameter("session")).thenReturn("DummySessionToken");
                when(request.getParameter("project")).thenReturn(project.getKey().toString());
                when(request.getRemoteAddr()).thenReturn("127.0.0.1");
                when(response.getWriter()).thenReturn(mockWriter.getWriter());

                new ContractServlet().doGet(request, response);


                String output = mockWriter.getOutput();
                PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

                JSONObject json = new JSONObject(output);
                JSONArray documents = json.getJSONArray("Document");

                assertVerbose("We can only access one document", documents.length(), is(1));

                // Now change the access right. (Admin user can do that)

                mockWriter = new MockWriter();

                when(request.getParameter("session")).thenReturn("DummyAdminToken");
                when(request.getParameter("document")).thenReturn(restrictedDocument.getKey().toString());
                when(request.getParameter("visibility")).thenReturn("Organization");
                when(request.getParameter("access")).thenReturn("rwd");
                when(response.getWriter()).thenReturn(mockWriter.getWriter());

                new ACSServlet().doPost(request, response);


                output = mockWriter.getOutput();
                PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

                json = new JSONObject(output);
                System.out.println("Got reply:" + json.toString());

                // After changing the access rights the demo user can see both documents

                mockWriter = new MockWriter();

                when(request.getParameter("session")).thenReturn("DummySessionToken");
                when(request.getParameter("project")).thenReturn(project.getKey().toString());
                when(response.getWriter()).thenReturn(mockWriter.getWriter());

                new ContractServlet().doGet(request, response);


                output = mockWriter.getOutput();
                PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

                json = new JSONObject(output);
                documents = json.getJSONArray("Document");

                assertThat(documents.length(), is(2));


        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }

    /***************************************************************************************
     *
     *          A non owner will not be able to change the rights
     *
     * @throws Exception
     */

    @Test
    public void testFailChangeAccess() throws Exception {

    }



}


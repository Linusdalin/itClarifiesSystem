package test.functionalTests;

import analysis.AnalysisServlet;
import backend.ItClarifies;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import contractManagement.*;
import crossReference.Reference;
import crossReference.ReferenceType;
import dataRepresentation.DBTimeStamp;
import log.PukkaLogger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupItem;
import risk.ContractRisk;
import services.ItClarifiesService;
import test.MockWriter;
import test.ServletTests;
import userManagement.PortalUser;
import userManagement.PortalUserTable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/*******************************************************************
 *
 *          Testing object existance tests
 *
 *
 *          //Add tests for reference servlet
 */


public class ObjectCheckTest extends ServletTests {


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

    /*******************************************************************************
     *
     *          When adding a new document, the system should go through and find
     *          references to this document that now can be closed.
     *
     *          This test will see if the reference to "test document.docx" is present in the existing documents in the project
     *
     * @throws Exception
     */


        @Test
        public void testMandatoryObject() throws Exception {

            try{

                // First create a new document for the project "Demo"

                Contract cannon = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.Name.name(), "Cannon")));

                ItClarifiesService service = new ItClarifiesService();

                MockWriter mockWriter;
                mockWriter = new MockWriter();

                when(request.getParameter("session")).thenReturn("DummyAdminToken");
                when(request.getRemoteAddr()).thenReturn("127.0.0.1");
                when(response.getWriter()).thenReturn(mockWriter.getWriter());

                service.validateSession(request, response);

                service.mandatoryObjectExists(cannon, response);
                System.out.println("Buffer: " + response.getBufferSize());

                cannon.delete();

                assertVerbose("After deleting the object does not exist", service.mandatoryObjectExists(cannon, response), is(false));
                System.out.println("Buffer: " + response.getBufferSize());


        }catch(Exception e){

            e.printStackTrace();
            assertTrue(false);
        }
    }


    @Test
    public void testDocumentAccessControl() throws Exception {

        try{

            // First create a new document for the project "Demo"

            Contract google = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.Name.name(), "Cannon")));

            ItClarifiesService service = new ItClarifiesService();

            MockWriter mockWriter;
            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummySessionToken");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            service.validateSession(request, response);

            assertVerbose("Demo user shall not see the Cannon Document", service.mandatoryObjectExists(google, response), is(false));


    }catch(Exception e){

        e.printStackTrace();
        assertTrue(false);
    }
}

    @Test
    public void notValidatedSession() throws Exception {

        try{

            // First create a new document for the project "Demo"

            Contract google = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.Name.name(), "Google Analytics")));

            ItClarifiesService service = new ItClarifiesService();

            MockWriter mockWriter;
            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            assertVerbose("Fail validation as the session is not validated",
                    service.mandatoryObjectExists(google, response), is(false));

            System.out.println("Status:" + response.getStatus());


        }catch(Exception e){

            e.printStackTrace();
            assertTrue(false);
        }
    }


}


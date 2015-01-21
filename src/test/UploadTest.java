package test;

import analysis.AnalysisServlet;
import backend.ItClarifies;
import classification.FragmentClassification;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import contractManagement.*;
import fileHandling.BlobRepository;
import log.PukkaLogger;
import net.sf.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupItem;
import analysis.UploadServlet;

import risk.RiskClassification;
import userManagement.AccessRight;
import userManagement.PortalUser;
import userManagement.PortalUserTable;
import userManagement.Visibility;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.List;

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


public class UploadTest extends ServletTests {


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

            PukkaLogger.setLogLevel(PukkaLogger.Level.DEBUG);


            bo = new ItClarifies();
            bo.createDb();
            bo.populateValues(true);

        } catch (Exception e) {

            e.printStackTrace();
            assertTrue(false);
        }

    }



        @Test
        public void testUploadAndAnalyse() throws Exception {

            String testDocumentTitle = "test Document.docx";

            try{
                MockWriter mockWriter;

                String testDoc = "Test document.docx";
                Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));
                PortalUser user = new PortalUser(new LookupItem().addFilter(new ColumnFilter(PortalUserTable.Columns.Name.name(), "admin")));
                AccessRight mockAccessRight = AccessRight.getro();
                Visibility mockVisibility = Visibility.getOrg();


                FileInputStream in = new FileInputStream(testDoc);

                // TODO: Store document in blob store here

                ContractVersionInstance newVersion = new UploadServlet().handleUpload(testDocumentTitle, new BlobRepository().getEmptyFileHandler(),
                        null, project, user, mockAccessRight, mockVisibility, "DummyAdminToken");

                assertThat("Version should exist", newVersion.exists(), is(true));
                assertThat("Document name from uploaded file",  newVersion.getDocument().getName(), is(testDocumentTitle));
                assertThat("Version name from uploaded file",  newVersion.getVersion(), is(testDocumentTitle + "_v1"));

                //TODO: create more assertions to verify the outcome of the parsing

                // Now analyse document


                mockWriter = new MockWriter();
                JSONObject json;
                Contract newDocument;
                String output;

                when(request.getParameter("session")).thenReturn("DummyAdminToken");
                when(request.getParameter("version")).thenReturn(newVersion.getKey().toString());
                when(request.getRemoteAddr()).thenReturn("127.0.0.1");
                when(response.getWriter()).thenReturn(mockWriter.getWriter());

                AnalysisServlet.MODEL_DIRECTORY = "web/models";
                new AnalysisServlet().doPost(request, response);

                output = mockWriter.getOutput();
                PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

                json = new JSONObject(output);


                newDocument = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.Name.name(), testDocumentTitle)));
                assertThat("The document is stored", newDocument.exists(), is(true));

                List<FragmentClassification> classifications = newDocument.getHeadVersion().getFragmentClassificationsForVersion();
                int numberOfClassifications = classifications.size();
                assertThat("There should be classifications after the analysis", numberOfClassifications, is( 2 ));

                List<RiskClassification> risks = newDocument.getHeadVersion().getRiskClassificationsForVersion();
                int numberOfRisks = risks.size();
                assertThat("There should be risks after the analysis", numberOfRisks, is( 1 ));

                System.out.println(classifications);


                // Now we should try to run the analysis again. This should generate exactly the same state again


                mockWriter = new MockWriter();

                when(request.getParameter("session")).thenReturn("DummyAdminToken");
                when(request.getParameter("version")).thenReturn(newVersion.getKey().toString());
                when(request.getRemoteAddr()).thenReturn("127.0.0.1");
                when(response.getWriter()).thenReturn(mockWriter.getWriter());

                AnalysisServlet.MODEL_DIRECTORY = "web/models";
                new AnalysisServlet().doPost(request, response);

                output = mockWriter.getOutput();
                PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

                json = new JSONObject(output);


                newDocument = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.Name.name(), testDocumentTitle)));
                assertThat("The document is stored", newDocument.exists(), is(true));

                classifications = newDocument.getHeadVersion().getFragmentClassificationsForVersion();
                assertThat("There should be classifications after the analysis", classifications.size(), is( numberOfClassifications ));

                risks = newDocument.getHeadVersion().getRiskClassificationsForVersion();
                assertThat("There should be risks after the analysis", risks.size(), is( numberOfRisks ));

                System.out.println(classifications);


            }catch(NullPointerException e){

                e.printStackTrace();
                assertTrue(false);
            }
        }




}


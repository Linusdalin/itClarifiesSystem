package test.integrationTests;

import analysis.AnalysisServlet;
import classification.FragmentClassification;
import contractManagement.Contract;
import contractManagement.ContractFragment;
import contractManagement.ContractVersionInstance;
import contractManagement.DocumentSection;
import fileHandling.BlobRepository;
import fileHandling.FileUploadServlet;
import fileHandling.RepositoryFileHandler;
import fileHandling.RepositoryInterface;
import junit.framework.Assert;
import log.PukkaLogger;
import module.ModuleServlet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import overviewExport.OverviewExportInternalServlet;
import overviewExport.OverviewExportServlet;
import overviewExport.OverviewExportStatusServlet;
import risk.RiskClassification;
import test.MockWriter;
import test.ServletTests;
import userManagement.AccessRight;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;


/*******************************************************************
 *
 *          Testing the service with mocked request and response messages
 *
 */


public class UploadAalyseExportTest extends ServletTests {



    @AfterClass
    public static void tearDown() {

        helper.tearDown();
    }


    @BeforeClass
    public static void preAmble(){

        init();

    }



        @Test
        public void testUploadAndAnalyse() throws Exception {


            try{

                String filename = "Functional Test Test Document.docx";             // located in the project root
                FileInputStream stream = new FileInputStream(new File(filename));

                AccessRight accessRight = AccessRight.getrwd();

                // Create new document
                Contract document = null;
                ContractVersionInstance oldVersion = null;


                RepositoryInterface repository = new BlobRepository();
                RepositoryFileHandler fileHandler = repository.saveFile(filename, stream);

                FileUploadServlet uploadServlet = new FileUploadServlet();
                DocumentSection section = demoProject.getDefaultSection();

                ContractVersionInstance newVersion = uploadServlet.handleUploadDocument(filename, fileHandler, document, demoProject, adminUser, accessRight, section, "dummyFingerprint");


                AnalysisServlet servlet = new AnalysisServlet();
                servlet.setModelDirectory("web/models");
                servlet.parseFile(newVersion.getDocument(), newVersion);
                servlet.analyse(newVersion, oldVersion);

                // Now perform tests

                List<ContractFragment> fragments =  newVersion.getFragmentsForVersion();
                List<FragmentClassification> classifications =  newVersion.getFragmentClassificationsForVersion();
                List<RiskClassification> risks =  newVersion.getRiskClassificationsForVersion();

                assertVerbose("Found fragments in the test document", fragments.size(), is(66));
                assertVerbose("Found classifications in the test document", classifications.size(), is(4));
                assertVerbose("Found risk classifications in the test document", risks.size(), is(0));


                MockWriter mockWriter;
                String output;


                mockWriter = new MockWriter();

                when(request.getParameter("session")).thenReturn("DummyAdminToken");
                when(request.getRemoteAddr()).thenReturn("127.0.0.1");
                when(request.getParameter("project")).thenReturn(demoProject.getKey().toString());
                when(request.getParameter("tags")).thenReturn("[\"#DATE\"]");
                when(response.getWriter()).thenReturn(mockWriter.getWriter());

                new OverviewExportInternalServlet().doPost(request, response);

                output = mockWriter.getOutput();
                PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

                JSONObject feedback = new JSONObject(output);
                JSONArray messages = feedback.getJSONArray("feedback");

                assertVerbose("Expecting to get messages back", messages.length() > 0, is(true));


                // Check the status

                mockWriter = new MockWriter();

                when(request.getParameter("session")).thenReturn("DummyAdminToken");
                when(request.getRemoteAddr()).thenReturn("127.0.0.1");
                when(request.getParameter("project")).thenReturn(demoProject.getKey().toString());
                when(response.getWriter()).thenReturn(mockWriter.getWriter());

                new OverviewExportStatusServlet().doGet(request, response);

                output = mockWriter.getOutput();
                PukkaLogger.log(PukkaLogger.Level.INFO, "JSON2: " + output);

                JSONObject status = new JSONObject(output).getJSONObject("ExportStatus");

                JSONArray tags = status.getJSONArray("tags");

                assertVerbose("Expecting to get 1 tag back", tags.length(), is( 1 ));




            }catch(Exception e){

                e.printStackTrace();
                Assert.assertTrue(false);
            }
        }


}


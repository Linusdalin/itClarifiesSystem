package test;

import analysis.AnalysisServlet;
import backend.ItClarifies;
import classification.FragmentClassification;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import contractManagement.*;
import fileHandling.BlobRepository;
import fileHandling.FileUploadServlet;
import fileHandling.RepositoryFileHandler;
import fileHandling.RepositoryInterface;
import junit.framework.Assert;
import log.PukkaLogger;
import net.sf.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupItem;

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

            assertVerbose("Found fragments in the test document", fragments.size(), is(79));
            assertVerbose("Found classifications in the test document", classifications.size(), is(4));
            assertVerbose("Found risk classifications in the test document", risks.size(), is(0));


        }catch(Exception e){

            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }


}


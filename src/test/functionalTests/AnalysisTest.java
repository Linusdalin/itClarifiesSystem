package test.functionalTests;

import analysis.AnalysisServlet;
import analysis2.AnalysisException;
import classification.FragmentClassification;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import contractManagement.Contract;
import contractManagement.ContractFragment;
import contractManagement.ContractVersionInstance;
import contractManagement.DocumentSection;
import fileHandling.BlobRepository;
import fileHandling.FileUploadServlet;
import fileHandling.RepositoryFileHandler;
import fileHandling.RepositoryInterface;
import log.PukkaLogger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.asm.tree.analysis.AnalyzerException;
import risk.RiskClassification;
import test.ServletTests;
import userManagement.AccessRight;
import userManagement.Visibility;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;

/*******************************************************************************
 *
 *          Testing to analyse the test document
 */

public class AnalysisTest  extends ServletTests {

    @AfterClass
    public static void tearDown() {

        helper.tearDown();
    }


    @BeforeClass
    public static void preAmble(){

        // Setup test env for database

        helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
        helper.setUp();

        // Init database, sessions and test values

        init();

    }

    /*******************************************************************
     *
     *      Read document from disc, parse and analyse it
     *
     *
     * @throws Exception
     */



    @Test
    public void testAnalyseDocument() throws Exception {

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
            assertTrue(false);
        }
    }


}

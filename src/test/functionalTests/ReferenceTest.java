package test.functionalTests;

import analysis.AnalysisServlet;
import backend.ItClarifies;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import contractManagement.*;
import crossReference.Definition;
import crossReference.Reference;
import crossReference.ReferenceType;
import dataRepresentation.DBTimeStamp;
import document.AbstractDocument;
import document.AbstractProject;
import language.LanguageCode;
import log.PukkaLogger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupItem;
import risk.ContractRisk;
import system.Analyser;
import test.ServletTests;
import userManagement.AccessRight;
import userManagement.PortalUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;


/*******************************************************************
 *
 *          Testing the references
 *
 *
 *          //Add tests for reference servlet
 */


public class ReferenceTest extends ServletTests {


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
        public void testReanalyseProject() throws Exception {

            try{

                Analyser analyser = new Analyser(new LanguageCode("EN"), "" );

                // First create a new document for the project "Demo"

                Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));
                Contract cannon = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.Name.name(), "Cannon")));

                // Create a new document and put it in the project

                ContractFragment firstFragmentInCannonDoc = new ContractFragment(new LookupItem().addFilter(new ColumnFilter(ContractFragmentTable.Columns.Name.name(), "first fragment")));
                ContractType documentType = ContractType.getUnknown();
                ContractStatus status = ContractStatus.getAnalysing();
                PortalUser user = PortalUser.getSystemUser();
                DBTimeStamp creationTime = new DBTimeStamp();
                AbstractProject aProject = project.createAbstractProject();
                AbstractDocument aDocument = null;
                DocumentSection section = project.getDefaultSection();


                Contract newDocument = new Contract("pricelist", "Test Document.docx", 3, documentType, status,  "no message", "test document", project, user, creationTime.toString(), "EN", section, AccessRight.getrwd());
                newDocument.store();

                // Create a new version for the document

                ContractVersionInstance version = new ContractVersionInstance("v0.9", newDocument, "no file", user, creationTime.toString());
                version.store();

                // Add a new fragment to the document. This is what the document reference will point to.

                ContractFragment aFirstFragment = new ContractFragment("Test Document", version, project, 0, 0, "text", 0, "Headline", ContractRisk.getNone(), 0, 0, 0, 0, 0, 0, 0, "{}", "{}");
                aFirstFragment.store();

                List<Reference> referencesForFragment;
                int referencesCount = firstFragmentInCannonDoc.getReferencesForFragment().size();
                PukkaLogger.log(PukkaLogger.Level.INFO, "Number of references in the first fragment before analysis is " + referencesCount);

                // Now perform the reanalysis

                new AnalysisServlet().reanalyseProjectForReferences(analyser, project, version, aProject, aDocument, user);

                referencesForFragment = firstFragmentInCannonDoc.getReferencesForFragment();
                assertVerbose("One more reference (over the " + referencesCount + " before) is expected from the analysis", referencesForFragment.size(), is(referencesCount + 1));

                Reference newReference = referencesForFragment.get(referencesCount);  // Gat the latest

                assertTrue(newReference.getTo().equals(aFirstFragment));


        }catch(Exception e){

            e.printStackTrace();
            assertTrue(false);
        }
    }


    @Test
    public void testGetOpenReferences() throws Exception {

        try{



            Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));
            Contract cannon = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.Name.name(), "Cannon")));
            ContractFragment firstFragmentInCannonDoc = new ContractFragment(new LookupItem().addFilter(new ColumnFilter(ContractFragmentTable.Columns.Name.name(), "first fragment")));
            PortalUser user = PortalUser.getSystemUser();

            int referencesBefore =  project.getOpenReferences().size();

            Reference newOpen = new Reference(
                    "open ref",
                    firstFragmentInCannonDoc.getKey(),
                    firstFragmentInCannonDoc.getKey(),          //TODO: This is pointing back to self
                    cannon.getHeadVersion().getKey(),
                    project.getKey(),
                    ReferenceType.getOpen(),
                    "pattern",
                    0,
                    user.getKey()
            );
            newOpen.store();

            Reference newExplicit = new Reference(
                    "explicit and closed ref",
                    firstFragmentInCannonDoc.getKey(),
                    firstFragmentInCannonDoc.getKey(),          //TODO: This is pointing back to self
                    cannon.getHeadVersion().getKey(),
                    project.getKey(),
                    ReferenceType.getExplicit(),
                    "pattern",
                    0,
                    user.getKey()
                    );

            newExplicit.store();

            List<Reference> openReferencesForProjectAfter = project.getOpenReferences();



            assertThat("Expecting to find one new open reference", openReferencesForProjectAfter.size(), is( referencesBefore + 1));


    }catch(Exception e){

        e.printStackTrace();
        assertTrue(false);
    }
}

}


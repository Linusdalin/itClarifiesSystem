package test;

import actions.Action;
import actions.ActionStatus;
import backend.ItClarifies;
import classification.FragmentClassification;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import contractManagement.*;
import dataRepresentation.DBTimeStamp;
import featureTypes.FeatureTypeTree;
import fileHandling.BlobRepository;
import language.LanguageCode;
import log.PukkaLogger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.*;

import pukkaBO.exceptions.BackOfficeException;
import risk.ContractRisk;
import userManagement.AccessRight;
import userManagement.PortalUser;
import userManagement.PortalUserTable;
import userManagement.Visibility;
import versioning.Transposer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;


/*******************************************************************
 *
 *          Testing the service with mocked request and response messages
 *
 */


public class ReplaceDocumentTest extends ServletTests{


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
     *          Replacing System Use Case:
     *
     *           - Upload a first version   (crating the document with an initial _v1)
     *           - Upload again             (this is now a temporary doc with _v2)
     *
     *          // TODO add annotation to the check
     *
     * @throws Exception
     */


    @Test
    public void replacingDocument() throws Exception {


        try {

            ContractTable contractTable = new ContractTable();

            // Create some dummy values

            Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));
            PortalUser creator = new PortalUser(new LookupItem().addFilter(new ColumnFilter(PortalUserTable.Columns.Name.name(), "demo")));
            AccessRight accessRight = AccessRight.getrwd();
            Visibility visibility = Visibility.getPrivate();
            String name = "TestDoc";  // Replacing the existing document

            ContractVersionInstance initialVersion = contractTable.addNewDocument(project, name, new BlobRepository().getEmptyFileHandler(), new LanguageCode("EN"), creator, accessRight, visibility);

            assertThat(initialVersion.getVersion(), is("TestDoc_v1"));

            Contract readBack = new Contract(new LookupByKey(initialVersion.getDocumentId()));
            assertThat(readBack.getName(), is("TestDoc"));

            // Now create some fragments, classifications and risk for the initial version

            mockParseDocument(project, initialVersion);
            mockAnnotateAndClassify(project, initialVersion, creator);


            // Check so there ara classifications and annotations on the first fragment

            ContractFragment first = new ContractFragment(new LookupItem()
                    .addFilter(new ReferenceFilter(ContractFragmentTable.Columns.Version.name(), initialVersion.getKey()))
                    .addFilter(new ColumnFilter(ContractFragmentTable.Columns.Ordinal.name(), 1)));

            List<FragmentClassification> classifications = first.getClassificationsForFragment();
            assertThat(classifications.size(), not(is(0)));

            List<ContractAnnotation> annotations = first.getAnnotationsForFragment();
            assertThat(annotations.size(), not(is(0)));

            List<Action> actions= first.getActionsForFragment();
            assertThat(actions.size(), not(is(0)));

            String classification = classifications.get(0).getClassTag();
            ContractAnnotation      annotation     = annotations.get(0);
            Action                  action         = actions.get(0);
            ContractRisk            risk            = first.getRisk();

            PukkaLogger.log(PukkaLogger.Level.INFO, "Class: " + classification + " Risk: " + risk.getName() + " Annotation: " + annotation.getDescription());

            assertThat(classification, is("Definition"));
            assertThat(risk.getName(), is("Black"));
            assertThat(annotation.getDescription(), is("a comment"));
            assertThat(action.getDescription(), is("action description"));


            // Now upload a new version of the document

            ContractVersionInstance newVersion = readBack.addNewVersion(creator, new BlobRepository().getEmptyFileHandler());
            assertThat(newVersion.getVersion(), is("TestDoc_v2"));

            // This second time we upload the same document, but clone the classifications, risks and annotations.

            mockParseDocument(project, newVersion);
            Transposer transposer = new Transposer();
            transposer.clone(initialVersion, newVersion);

            // Now check again so that the fragments exists in the new document

            first = new ContractFragment(new LookupItem()
                    .addFilter(new ReferenceFilter(ContractFragmentTable.Columns.Version.name(), newVersion.getKey()))
                    .addFilter(new ColumnFilter(ContractFragmentTable.Columns.Ordinal.name(), 1)));

            assertThat(first.exists(), is(true));

            annotations = first.getAnnotationsForFragment();
            assertThat("Expect to find a transferred annotation", annotations.size(), not(is(0)));

            classifications = first.getClassificationsForFragment();
            assertThat("Expect to find a transferred classification", classifications.size(), not(is(0)));

            actions = first.getActionsForFragment();
            assertThat("Expect to find a transferred action", actions.size(), not(is(0)));

            classification  = classifications.get(0).getClassTag();
            annotation      = annotations.get(0);
            action         = actions.get(0);
            risk            = first.getRisk();

            PukkaLogger.log(PukkaLogger.Level.INFO, "Class: " + classification + " Risk: " + risk.getName());

            // The classification and risk should have been transposed to the new version of the document

            assertThat(classification, is("Definition"));
            assertThat(risk.getName(), is("Black"));
            assertThat(annotation.getDescription(), is("a comment"));
            assertThat(action.getDescription(), is("action description"));

            // The count should also be set correctly

            assertThat(first.getAnnotationCount(),      is( (long) 1 ));
            assertThat(first.getClassificatonCount(),   is( (long) 1 ));
            assertThat(first.getActionCount(),          is( (long) 1 ));



        } catch (BackOfficeException e) {

            e.logError("Error in test");
            assertTrue(false);

        } catch (Exception e) {

            e.printStackTrace();
            assertTrue(false);
        }


    }




    private void mockAnnotateAndClassify(Project project, ContractVersionInstance version, PortalUser user) throws BackOfficeException{

        DBTimeStamp now = new DBTimeStamp();

        PortalUser adminUser = new PortalUser(new LookupItem().addFilter(new ColumnFilter(PortalUserTable.Columns.Name.name(), "admin")));

        ContractFragment first = new ContractFragment(new LookupItem()
                .addFilter(new ReferenceFilter(ContractFragmentTable.Columns.Version.name(), version.getKey()))
                .addFilter(new ColumnFilter(ContractFragmentTable.Columns.Ordinal.name(), 1)));

        first.setRisk(ContractRisk.getBlack());
        first.setAnnotationCount(1);
        first.update();

        new ContractAnnotation("", first, 1, "a comment", user, version, "", 0, now.getISODate()).store();

        new FragmentClassification(first, FeatureTypeTree.Definition.getName(), 0, 0, "", "", user, version, project, "", 0, 0, 70, "no rule specified", now.getISODate()).store();

        new Action(0, "name", "action description", "pattern", first, version, first, first.getVersion().getDocument().getProject(),
                adminUser, adminUser, (long)4711, ActionStatus.getInProgress(), new DBTimeStamp().getISODate(), new DBTimeStamp().getISODate(), new DBTimeStamp().getISODate()).store();

    }

    private void mockParseDocument(Project project, ContractVersionInstance version) throws BackOfficeException {

        int annotationcount = 0;
        int referencecount = 0;
        int classificationCount = 0;
        int actionCount = 0;

        ContractClause c = new ContractClause("A clause", version.getKey(), 1);
        c.store();


        new ContractFragment("F1", version.getKey(), project.getKey(), c.getNumber(), 1, "The First fragment",
                0, "Text", ContractRisk.getUnknown(), annotationcount, referencecount, classificationCount, actionCount, 0, 0, "{ }").store();
        new ContractFragment("F2", version.getKey(), project.getKey(), c.getNumber(), 2, "Some other fragment",
                0, "Text", ContractRisk.getUnknown(), annotationcount, referencecount, classificationCount, actionCount, 0, 0, "{ }").store();
    }


}


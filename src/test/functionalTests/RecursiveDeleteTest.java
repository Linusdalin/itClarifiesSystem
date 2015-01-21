package test.functionalTests;

import backend.ItClarifies;
import classification.FragmentClassification;
import classification.FragmentClassificationTable;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import contractManagement.*;
import log.PukkaLogger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupByKey;
import pukkaBO.condition.LookupItem;
import pukkaBO.condition.ReferenceFilter;
import pukkaBO.exceptions.BackOfficeException;
import test.ServletTests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Simple tests for the time stamp datatype.
 */



public class RecursiveDeleteTest extends ServletTests{

    private static LocalServiceTestHelper helper;


    @AfterClass
    public static void tearDown() {

        helper.tearDown();
    }


    @BeforeClass
    public static void preAmble(){

        helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
        helper.setUp();

        PukkaLogger.setLogLevel(PukkaLogger.Level.DEBUG);


    }


    @Test
    public void delete(){

        deleteContract();
        deleteProject();
    }



    /******************************************************************
     *
     *
     *          Test recursively deleting the project
     *
     *
     *          //TODO: Add riskflagging too
     */


    public void deleteContract(){

        BackOfficeInterface bo = new ItClarifies();
        bo.createDb();
        bo.populateValues(true);


        try {

            Contract cannon = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.Name.name(), "Cannon")));

            ContractFragment testFragment = new ContractFragment(new LookupItem().addFilter(new ColumnFilter(ContractFragmentTable.Columns.Name.name(), "first fragment")));
            ContractAnnotation testAnnotation       = new ContractAnnotation(new LookupItem().addFilter(new ReferenceFilter(ContractAnnotationTable.Columns.Fragment.name(), testFragment.getKey())));
            FragmentClassification testClassification
                    = new FragmentClassification(new LookupItem().addFilter(new ReferenceFilter(FragmentClassificationTable.Columns.Fragment.name(), testFragment.getKey())));

            DocumentDeleteOutcome outcome =  cannon.recursivelyDeleteDocument();

            assertThat(outcome.documents,       is( 1 ));
            assertThat(outcome.versions,        is( 1 ));
            assertThat(outcome.clauses,         is( 2 ));
            assertThat(outcome.fragments,       is( 2 ));
            assertThat(outcome.annotations,     is( 1 ));
            assertThat(outcome.riskFlags,       is( 1 ));
            assertThat(outcome.classifications, is( 1 ));

            // Now verify this in the database. These should now be gone

            ContractFragment readBackFragment                = new ContractFragment (new LookupByKey(testFragment.getKey()));
            ContractAnnotation readBackAnnotation            = new ContractAnnotation(new LookupByKey(testAnnotation.getKey()));
            FragmentClassification readBackClassification    = new FragmentClassification(new LookupByKey(testClassification.getKey()));

            assertFalse(readBackFragment.exists());
            assertFalse(readBackAnnotation.exists());
            assertFalse(readBackClassification.exists());

        } catch (BackOfficeException e) {
            e.logError("error in Project delete test");
            assertTrue(false);
        }


    }


    public void deleteProject(){

        BackOfficeInterface bo = new ItClarifies();
        bo.createDb();
        bo.populateValues(true);


        try {

            Project demo = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));

            ContractFragment   testFragment = new ContractFragment(new LookupItem().addFilter(new ColumnFilter(ContractFragmentTable.Columns.Name.name(), "first fragment")));
            ContractAnnotation testAnnotation       = new ContractAnnotation(new LookupItem().addFilter(new ReferenceFilter(ContractAnnotationTable.Columns.Fragment.name(), testFragment.getKey())));

            DocumentDeleteOutcome outcome =  demo.recursivelyDelete();

            assertThat("Deleted documents", outcome.documents,       is(  2 ));
            assertThat("Deleted versions", outcome.versions,        is(  2 ));
            assertThat("Deleted clauses", outcome.clauses,         is( 11 ));
            assertThat("Deleted fragments", outcome.fragments,       is( 61 ));
            assertThat("Deleted annotations", outcome.annotations,     is( 1 ));
            assertThat("Deleted risks", outcome.riskFlags,       is( 1 ));
            assertThat("Deleted classifications", outcome.classifications, is( 1 ));

            // Now verify this in the database. These should now be gone

            ContractFragment readBackFragment                = new ContractFragment(new LookupByKey(testFragment.getKey()));
            ContractAnnotation readBackAnnotation            = new ContractAnnotation(new LookupByKey(testAnnotation.getKey()));
            //FragmentClassification readBackClassification    = new FragmentClassification(new LookupByKey(testClassification.getKey()));

            assertFalse(readBackFragment.exists());
            assertFalse(readBackAnnotation.exists());
            //assertFalse(readBackClassification.exists());

        } catch (BackOfficeException e) {
            e.logError("error in Project delete test");
            assertTrue(false);
        }


    }


}


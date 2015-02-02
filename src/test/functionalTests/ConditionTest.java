package test.functionalTests;

import backend.*;
import classification.Reclassification;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import log.PukkaLogger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupItem;
import pukkaBO.condition.LookupList;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.form.FormInterface;
import test.ServletTests;
import userManagement.PortalUser;
import userManagement.PortalUserTable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;


/******************************************************
 *
 *     //TODO: Move this test to demoBackOffice(and expand it)
 */


public class ConditionTest extends ServletTests{

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



            bo = new ItClarifies();
            bo.createDb();
            bo.populateValues(true);


            PukkaLogger.setLogLevel(PukkaLogger.Level.DEBUG);

        } catch (Exception e) {

            e.printStackTrace();
            assertTrue(false);
        }

    }





    /******************************************************************
     *
     *
     *
     */



    @Test
    public void filterTest(){

        try {

            PortalUserTable all = new PortalUserTable(new LookupList());
            PortalUserTable wsAdmin = new PortalUserTable(new LookupList().addFilter(new ColumnFilter(PortalUserTable.Columns.WSAdmin.name(), true)));
            PortalUserTable nonWSAdmin = new PortalUserTable(new LookupList().addFilter(new ColumnFilter(PortalUserTable.Columns.WSAdmin.name(), false)));

            assertVerbose("Precondition: Found " + all.getCount() + " users",       all.getCount(), is(14));
            assertVerbose("Found " + wsAdmin.getCount() +           " wsAdmin",     wsAdmin.getCount(), is(13));
            assertVerbose("Found " + nonWSAdmin.getCount() +        " non wsAdmin", nonWSAdmin.getCount(), is(1));



        } catch (BackOfficeException e) {

            e.printStackTrace();
            assertTrue(false);
        }


    }

}
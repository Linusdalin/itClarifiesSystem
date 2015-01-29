package test.functionalTests;

import backend.*;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import document.AbstractFragment;
import document.AbstractStructureItem;
import document.SimpleStyle;
import document.StructureType;
import log.PukkaLogger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupItem;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.form.FormInterface;
import test.ServletTests;
import userManagement.PortalUser;
import userManagement.PortalUserTable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 *
 */


public class FormTest extends ServletTests{

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
    public void editUserForm(){

        PortalUser user = new PortalUser(new LookupItem().addFilter(new ColumnFilter(PortalUserTable.Columns.Name.name(), "demo")));


        try {
            BackOfficeInterface backOffice = null;

            FormInterface editUserForm = new EditUserForm(backOffice, "section", "list", user);
            String form = editUserForm.renderForm();

            System.out.println("Form:" + form);

            assertVerbose("Find field ", form.contains("<label for=\"Email\">Email</label>"), is(true));
            assertVerbose("Find field ", form.contains("<label for=\"wsAdmin\">wsAdmin</label>"), is(true));
            assertVerbose("Find field ", form.contains("<label for=\"Name\">Name</label>"), is(true));


        } catch (BackOfficeException e) {

            e.printStackTrace();
            assertTrue(false);
        }


    }



    @Test
    public void ChangePasswordForm(){

        PortalUser user = new PortalUser(new LookupItem().addFilter(new ColumnFilter(PortalUserTable.Columns.Name.name(), "demo")));


        try {
            BackOfficeInterface backOffice = null;

            FormInterface form = new ChangePasswordForm(backOffice, "section", "list", user);
            String html = form.renderForm();

            System.out.println("Form:" + html);

            assertVerbose("Find field ", html.contains("<label for=\"Email\">Email</label>"), is(true));
            assertVerbose("Find field ", html.contains("<label for=\"Name\">Name</label>"), is(true));


        } catch (BackOfficeException e) {

            e.printStackTrace();
            assertTrue(false);
        }


    }


    @Test
    public void newOrganizationForm(){

        PortalUser user = new PortalUser(new LookupItem().addFilter(new ColumnFilter(PortalUserTable.Columns.Name.name(), "demo")));


        try {
            BackOfficeInterface backOffice = null;

            FormInterface form = new NewOrganizationForm(backOffice, "section", "list");
            String html = form.renderForm();

            System.out.println("Form:" + html);

            assertVerbose("Find field ", html.contains("<label for=\"Description\">Description</label>"), is(true));
            assertVerbose("Find field ", html.contains("<label for=\"Name\">Name</label>"), is(true));


        } catch (BackOfficeException e) {

            e.printStackTrace();
            assertTrue(false);
        }


    }

    @Test
    public void newUserForm(){

        PortalUser user = new PortalUser(new LookupItem().addFilter(new ColumnFilter(PortalUserTable.Columns.Name.name(), "demo")));


        try {
            BackOfficeInterface backOffice = null;

            FormInterface form = new NewUserForm(backOffice, "section", "list");
            String html = form.renderForm();

            System.out.println("Form:" + html);

            assertVerbose("Find field ", html.contains("<label for=\"Email\">Email</label>"), is(true));
            assertVerbose("Find field ", html.contains("<label for=\"Name\">Name</label>"), is(true));


        } catch (BackOfficeException e) {

            e.printStackTrace();
            assertTrue(false);
        }


    }


}


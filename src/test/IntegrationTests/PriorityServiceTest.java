package test.integrationTests;

import actions.Action;
import actions.ActionStatus;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import contractManagement.*;
import dataRepresentation.DBTimeStamp;
import log.PukkaLogger;
import net.sf.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import project.Project;
import project.ProjectTable;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupByKey;
import pukkaBO.condition.LookupItem;
import services.PriorityServlet;
import test.MockWriter;
import test.ServletTests;
import userManagement.PortalUser;
import userManagement.PortalUserTable;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/*******************************************************************
 *
 *          Setting the priority of all actions
 *
 */


public class PriorityServiceTest extends ServletTests {


    @AfterClass
    public static void tearDown() {

        helper.tearDown();
    }


    @BeforeClass
    public static void preAmble(){

        helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
        helper.setUp();

        init();

    }



        @Test
        public void rearrange() throws Exception {

            try{

                MockWriter mockWriter;

                Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));
                Contract document = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.Name.name(), "Cannon")));
                ContractFragment fragment = new ContractFragment(new LookupItem().addFilter(new ColumnFilter(ContractFragmentTable.Columns.Name.name(), "first fragment")));
                PortalUser user = new PortalUser(new LookupItem().addFilter(new ColumnFilter(PortalUserTable.Columns.Name.name(), "admin")));
                DBTimeStamp t = new DBTimeStamp();


                assertTrue(project.exists());
                assertTrue(document.exists());


                //Create two actions

                Action first = new Action(1, "Action1", "Desc", "Pattern", fragment, document.getHeadVersion(), fragment, project, user, user, -1, ActionStatus.getOpen(), t.getISODate(), t.getISODate(), t.getISODate());
                Action second = new Action(2, "Action2", "Desc", "Pattern", fragment, document.getHeadVersion(), fragment, project, user, user, -1, ActionStatus.getOpen(), t.getISODate(), t.getISODate(), t.getISODate());

                first.store();
                second.store();

                String arrangeJSON = "[{\"key\":\""+ first.getKey().toString()+"\", \"priority\":\"1\"}, {\"key\":\""+ second.getKey().toString()+"\", \"priority\":\"2\"}]";

                mockWriter = new MockWriter();

                when(request.getParameter("session")).thenReturn("DummyAdminToken");
                when(request.getParameter("project")).thenReturn(project.getKey().toString());
                when(request.getParameter("actions")).thenReturn(arrangeJSON);
                when(request.getRemoteAddr()).thenReturn("127.0.0.1");
                when(response.getWriter()).thenReturn(mockWriter.getWriter());

                new PriorityServlet().doPost(request, response);

                String output = mockWriter.getOutput();
                PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

                JSONObject json = new JSONObject(output);

                Action rereadFirst = new Action(new LookupByKey(first.getKey()));
                Action rereadSecond = new Action(new LookupByKey(second.getKey()));

                assertVerbose("Priority in fragment 1 updated", (int)rereadFirst.getPriority(), is( 1 ));
                assertVerbose("Priority in fragment 2 updated", (int)rereadSecond.getPriority(), is( 2 ));


        }catch(NullPointerException e){

            e.printStackTrace();
            assertTrue(false);
        }
    }





}


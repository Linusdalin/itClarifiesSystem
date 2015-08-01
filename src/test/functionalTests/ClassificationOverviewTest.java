package test.functionalTests;

import classification.ClassificationOverviewManager;
import contractManagement.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;
import project.Project;
import project.ProjectTable;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupItem;
import test.ServletTests;
import userManagement.PortalUser;
import userManagement.PortalUserTable;
import userManagement.SessionManagement;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/*******************************************************************
 *
 *          Testing the classification overview functionality
 *
 */


public class ClassificationOverviewTest extends ServletTests {


    //private static LocalServiceTestHelper helper;


    @AfterClass
    public static void tearDown() {

        helper.tearDown();
    }


    @BeforeClass
    public static void preAmble(){

        init();



    }

    /*******************************************************************
     *
     *
     */


     @Test
     public void basicTest() throws Exception {


         Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));
         PortalUser user = new PortalUser(new LookupItem().addFilter(new ColumnFilter(PortalUserTable.Columns.Name.name(), "admin")));

         assertTrue(user.exists());
         assertTrue(project.exists());

         SessionManagement session = mock(SessionManagement.class);
         when(session.getReadAccess(Matchers.any(Contract.class))).thenReturn(true);



         ClassificationOverviewManager overview = new ClassificationOverviewManager();
         overview.compileClassificationsForProject( project, session );

         JSONObject statistics = overview.getStatistics();

         assertVerbose("Got a structure back", ((JSONArray)statistics.get("subClassifications")).length(), is(1));

         System.out.println("Got statistics from project:\n" + statistics.toString());


    }


}
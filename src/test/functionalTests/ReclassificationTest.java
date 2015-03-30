package test.functionalTests;

import classification.ClassificationOverviewManager;
import classification.ClassificationServlet;
import com.google.appengine.api.datastore.KeyFactory;
import contractManagement.*;
import databaseLayer.AppEngine.AppEngineKey;
import databaseLayer.DBKeyInterface;
import featureTypes.FeatureTypeTree;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupByKey;
import pukkaBO.condition.LookupItem;
import reclassification.ReclassificationTable;
import test.MockWriter;
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
 *          Testing the re-classification export
 *
 */


public class ReclassificationTest extends ServletTests {


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
    public void testLiftSweepReplaceClassification() throws Exception {


        try {

            ContractFragment fragment = new ContractFragment(new LookupItem().addFilter(new ColumnFilter(ContractFragmentTable.Columns.Name.name(), "first fragment")));
            String classForFragment = FeatureTypeTree.DefinitionDef.getName();  // Just arbitrary class for test

            long classificationCount = fragment.getClassificatonCount();
            PukkaLogger.log(PukkaLogger.Level.INFO, "There are " + classificationCount + " classifications");

            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("fragment")).thenReturn(fragment.getKey().toString());
            when(request.getParameter("class")).thenReturn(classForFragment);
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ClassificationServlet().doPost(request, response);

            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            String reply = json.getString("Classification");
            isKey(reply);

            // There should now be one more classification for the fragment

            ContractFragment readBack = new ContractFragment(new LookupByKey(fragment.getKey()));

            assertThat(readBack.getClassificatonCount(), is(classificationCount + 1));

            // Adding a classification should have been noted in the reclassification log

            ReclassificationTable classificatons = new ReclassificationTable();

            assertThat("There should be one re-classification note", classificatons.getCount(), is( 1 ));

            // Now try to delete it again

            DBKeyInterface classificationKey = new AppEngineKey(KeyFactory.stringToKey(reply));

            mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("key")).thenReturn(classificationKey.toString());
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ClassificationServlet().doDelete(request, response);

            output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            json = new JSONObject(output);
            assertCorrectlyDeleted("Classification", json);

            // There should now be one more classification for the fragment

            readBack = new ContractFragment(new LookupByKey(fragment.getKey()));

            assertThat(readBack.getClassificatonCount(), is(classificationCount));

            // After deleting the classification, the log should not show any entries,
            // as the classification was revoked.

            ReclassificationTable classificatonLogAfter = new ReclassificationTable();
            assertThat("There should still be one re-classification note", classificatonLogAfter.getCount(), is( 0 ));


            // TODO: Put it back with the reclassification servlet here

            //TODO: Do the same with risk and annotation




        } catch (Exception e) {

            e.printStackTrace();
            assertTrue(false);
        }


    }



}
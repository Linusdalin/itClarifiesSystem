package test.functionalTests;

import backend.ItClarifies;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import contractManagement.*;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupItem;
import search.IndexManager;
import search.KeywordFieldHandler;
import search.SearchManager2;
import test.ServletTests;
import userManagement.PortalUser;
import userManagement.PortalUserTable;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;


/*******************************************************************
 *
 *          Testing the service with mocked request and response messages
 *
 */


public class SearchTest extends ServletTests {


    private static LocalServiceTestHelper helper;


    @AfterClass
    public static void tearDown() {

        helper.tearDown();
    }


    @BeforeClass
    public static void preAmble(){

        helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
        helper.setUp();



        try {

            BackOfficeInterface bo;

            bo = new ItClarifies();
            bo.createDb();
            bo.populateValues(true);

            PukkaLogger.setLogLevel(PukkaLogger.Level.DEBUG);



        } catch (Exception e) {

            e.printStackTrace();
            assertTrue(false);
        }

    }

    /***********************************************************************************
     *
     *              This test shows the usage of the index manager
     *
     *
     * @throws Exception
     */


    @Test
    public void indexManagerTest() throws Exception {

        try{

            String key = "theKey";
            String text = "The rain in Spain";
            String project = "projectA";
            String document = "documentX";
            String keyWord = "#TAG1, #TAG2";
            String owner = "UserX";

            // Create an index manager for the project key

            IndexManager indexManager = new IndexManager(project);

            // Create and store document

            Document doc = indexManager.createDocument(text, key, document, owner, 0, keyWord, IndexManager.PUBLIC, 1);
            indexManager.indexDocument(doc);


            // Now look it up

            Results<ScoredDocument> results = indexManager.search("rain");

            assertVerbose("Got key back :" + results.toString(),    results.toString().contains("theKey"), is(true));
            assertVerbose("Got document key back :" + results.toString(),    results.toString().contains("documentX"), is(true));
            assertVerbose("Got string "+ text+"back", results.toString().contains(text), is(true));
            assertVerbose("No Hits" + results.toString() ,    results.toString().contains("numberFound=1"), is(true));


            // With the wrong namespace there should be no results

            results = indexManager.search("que?");
            assertVerbose("No Hits" + results.toString() ,    results.toString().contains("numberFound=0"), is(true));

            //We should find #TAG1

            results = indexManager.search("#TAG1");
            assertVerbose("Got key back :" + results.toString(),    results.toString().contains("theKey"), is(true));
            assertVerbose("Got string "+ text+"back", results.toString().contains(text), is(true));
            assertVerbose("No Hits" + results.toString() ,    results.toString().contains("numberFound=1"), is(true));


            // Not find an unknown tag

            results = indexManager.search("#TAG3");
            assertVerbose("No Hits" + results.toString() ,    results.toString().contains("numberFound=0"), is(true));


        }catch(Exception e){

            e.printStackTrace();
            assertTrue(false);
        }
    }

    /********************************************************************************
     *
     *          Test the index search manager
     *
     *
     */


    @Test
    public void SearchManagerTest(){

        ContractFragment fragment = new ContractFragment(new LookupItem().addFilter(new ColumnFilter(ContractFragmentTable.Columns.Name.name(), "first fragment")));
        Contract document = fragment.getVersion().getDocument();
        PortalUser user  = new PortalUser(new LookupItem().addFilter(new ColumnFilter(PortalUserTable.Columns.Name.name(), "demo")));
        Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));

        SearchManager2 searchManager = new SearchManager2(project, user);

        searchManager.indexFragment(fragment, document);

        JSONArray results = searchManager.search("2014-07-01");
        assertVerbose("Found the one and only fragment", results.length(), is( 1 ));
        JSONObject theHit = results.getJSONObject(0);
        assertVerbose("Got the correct fragment key back",        theHit.getString("fragment"), is( fragment.getKey().toString() ));

    }

    /*************************************************************************
     *
     *          If the fragment is stored with private access it should
     *          only be accessed if the owner is retrieving it
     *
     *
     */

    @Test
    public void PrivacyTest(){

        ContractFragment fragment = new ContractFragment(new LookupItem().addFilter(new ColumnFilter(ContractFragmentTable.Columns.Name.name(), "first fragment")));
        Contract document = fragment.getVersion().getDocument();
        document.setAccess("no");   // Setting hidden access for the document

        PortalUser documentOwner = document.getOwner();
        PortalUser otherUser  = new PortalUser(new LookupItem().addFilter(new ColumnFilter(PortalUserTable.Columns.Name.name(), "demo")));

        assertVerbose("Precondition, first user exists ", documentOwner.exists(), is(true));
        assertVerbose("Precondition, second user exists ", otherUser.exists(), is(true));

        Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));

        SearchManager2 originalSearchManager = new SearchManager2(project, documentOwner);
        SearchManager2 otherSearchManager    = new SearchManager2(project, otherUser);

        originalSearchManager.indexFragment(fragment, document);

        // As before it should be possible to get the result back

        JSONArray results;

        results = originalSearchManager.search("2014-07-01");
        assertVerbose("Found the one and only fragment", results.length(), is( 1 ));
        JSONObject theHit = results.getJSONObject( 0 );
        assertVerbose("Got the correct fragment key back",        theHit.getString("fragment"), is( fragment.getKey().toString() ));

        // For the otherSearchManager, there should be no results back, as the user is different and should not see the document

        results = otherSearchManager.search("2014-07-01");
        assertVerbose("Should get zero hidden fragment back", results.length(), is( 0 ));


    }


    @Test
    public void KeywordFieldTest(){

        String tag = "#tag";
        String pattern = "the pattern";

        KeywordFieldHandler keywordFieldHandler = new KeywordFieldHandler();
        keywordFieldHandler.addTag(tag, pattern);
        assertVerbose("Got the pattern " + pattern + " back from tag",  keywordFieldHandler.getPatternForTag(tag), is(pattern));

        // Now try to create keyword field handler from a string


    }

    @Test
    public void KeywordFieldTest2(){

        String tag = "#tag";
        String pattern = "the pattern";

        KeywordFieldHandler keywordFieldHandler = new KeywordFieldHandler(tag + "{" + pattern + "}");
        assertVerbose("Got the pattern " + pattern + " back from tag",  keywordFieldHandler.getPatternForTag(tag), is(pattern));

        // Now try to create keyword field handler from a string


    }



}

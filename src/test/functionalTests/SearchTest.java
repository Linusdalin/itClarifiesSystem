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
import pukkaBO.exceptions.BackOfficeException;
import search.IndexManager;
import search.KeywordFieldHandler;
import search.SearchManager2;
import test.ServletTests;
import userManagement.AccessRight;
import userManagement.PortalUser;
import userManagement.PortalUserTable;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;


/*******************************************************************
 *
 *          Testing the service with mocked request and response messages
 *
 *
 *          //TODO: add test for deleting document and version in a project
 */


public class SearchTest extends ServletTests {


    private static LocalServiceTestHelper helper;


    @AfterClass
    public static void tearDown() {

        helper.tearDown();
    }


    @BeforeClass
    public static void preAmble(){

        init();

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
            String version = "version x.1";
            String keyWord = "#TAG1, #TAG2";
            String owner = "UserX";

            // Create an index manager for the project key

            IndexManager indexManager = new IndexManager(project);

            // Create and store document

            Document doc = indexManager.createDocument(text, key, version, document, owner, 0, keyWord, IndexManager.PUBLIC, 1);
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

        try{

            ContractFragment fragment = new ContractFragment(new LookupItem().addFilter(new ColumnFilter(ContractFragmentTable.Columns.Name.name(), "first fragment")));
            Contract document = fragment.getVersion().getDocument();
            ContractVersionInstance head = document.getHeadVersion();
            PortalUser user  = new PortalUser(new LookupItem().addFilter(new ColumnFilter(PortalUserTable.Columns.Name.name(), "demo")));
            Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));

            SearchManager2 searchManager = new SearchManager2(project, user);

            searchManager.indexFragment(fragment, head, document);

            JSONArray results = searchManager.search("2014-07-01", English);
            assertVerbose("Found the one and only fragment", results.length(), is( 1 ));
            //JSONObject theHit = results.getJSONObject(0);
            //assertVerbose("Got the correct fragment key back",        theHit.getString("fragment"), is( fragment.getKey().toString() ));

        }catch(BackOfficeException e){

            assertTrue(false);
        }

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

        try{

            ContractFragment fragment = new ContractFragment(new LookupItem().addFilter(new ColumnFilter(ContractFragmentTable.Columns.Name.name(), "first fragment")));
            Contract document = fragment.getVersion().getDocument();
            ContractVersionInstance head = document.getHeadVersion();

            // Setting hidden access for the document
            document.setAccess(AccessRight.getno() );

            PortalUser documentOwner = document.getOwner();
            PortalUser otherUser  = new PortalUser(new LookupItem().addFilter(new ColumnFilter(PortalUserTable.Columns.Name.name(), "demo")));

            assertVerbose("Precondition, first user exists ", documentOwner.exists(), is(true));
            assertVerbose("Precondition, second user exists ", otherUser.exists(), is(true));
            assertVerbose("Precondition, Demo is not the owner ", documentOwner.getName().equals(otherUser.getName()), is(false));

            Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));

            SearchManager2 originalSearchManager = new SearchManager2(project, documentOwner);
            SearchManager2 otherSearchManager    = new SearchManager2(project, otherUser);

            originalSearchManager.indexFragment(fragment, head, document);

            // As before it should be possible to get the result back

            JSONArray results;

            results = originalSearchManager.search("2014-07-01", Swedish);

            System.out.println("Found result: " + results.toString());

            assertVerbose("Found the one and only fragment", results.length(), is( 2 ));
            JSONObject theHit = results.getJSONObject( 0 );
            assertVerbose("Got the correct fragment key back",        theHit.getString("fragment"), is( fragment.getKey().toString() ));

            // For the otherSearchManager, there should be no results back, as the user is different and should not see the document

            results = otherSearchManager.search("2014-07-01", Swedish);

            System.out.println("Found result: " + results.toString());

            assertVerbose("Should get zero hidden fragment back", results.length(), is( 1 ));

        }catch(BackOfficeException e){

            assertTrue(false);
        }


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


    @Test
    public void GetPatternMultipleTags(){

        String tag = "#one#two#three";
        String pattern = "the pattern";

        KeywordFieldHandler keywordFieldHandler = new KeywordFieldHandler();
        keywordFieldHandler.addTag(tag, pattern);
        assertVerbose("Got the pattern " + pattern + " back from tag #one",  keywordFieldHandler.getPatternForTag("#one"), is(pattern));
        assertVerbose("Got the pattern " + pattern + " back from tag #three",  keywordFieldHandler.getPatternForTag("#three"), is(pattern));


    }

    @Test
    public void GetPatternFromMultipleKeywords(){

        String tag1 = "#one#two#three";
        String pattern1 = "the pattern";

        String tag2 = "#moja#mbili#tatu";
        String pattern2 = "second pattern";

        KeywordFieldHandler keywordFieldHandler = new KeywordFieldHandler();
        keywordFieldHandler.addTag(tag1, pattern1);
        keywordFieldHandler.addTag(tag2, pattern2);
        assertVerbose("Got the pattern " + pattern1 + " back from tag #two",  keywordFieldHandler.getPatternForTag("#two"), is(pattern1));
        assertVerbose("Got the pattern " + pattern2 + " back from tag #mbili",  keywordFieldHandler.getPatternForTag("#mbili"), is(pattern2));


    }

    /*********************************************************************
     *
     *      If the tag is not found, we expect to get the pattern back. This may
     *      occure in the text and we want to use it as tag
     *
     *
     */

    @Test
    public void TestNotFoundTag(){

        String tag = "#tag";
        String pattern = "the pattern";
        String someOtherTag = "#somethingElse";


        KeywordFieldHandler keywordFieldHandler = new KeywordFieldHandler();
        keywordFieldHandler.addTag(tag, pattern);
        assertVerbose("Got the tag " + someOtherTag + " back from not found",  keywordFieldHandler.getPatternForTag(someOtherTag), is(someOtherTag.substring( 1 ).toLowerCase()));

    }



}


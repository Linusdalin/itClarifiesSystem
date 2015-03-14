package test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import contractManagement.Contract;
import backend.ItClarifies;
import contractManagement.Project;
import contractManagement.ProjectTable;
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
import search.SearchManager;
import search.SearchManager2;
import system.TextMatcher;
import userManagement.PortalUser;
import userManagement.PortalUserTable;
import userManagement.SessionManagement;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

/**
 *
 */


public class SearchTest extends ServletTests {

    /******************************************************************
     *
     *
     *
     */


    private static BackOfficeInterface bo;
    private static LocalServiceTestHelper helper;
    private static SessionManagement mockedSession;
    private static TextMatcher textMatcher;

    @AfterClass
    public static void tearDown() {

        helper.tearDown();
    }


    @BeforeClass
    public static void preAmble() throws BackOfficeException{

        helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
        helper.setUp();


        bo = new ItClarifies();
        bo.createDb();
        bo.populateValues(true);

        PukkaLogger.setLogLevel(PukkaLogger.Level.DEBUG);

        init();

        mockedSession = mock(SessionManagement.class);
        when(mockedSession.getReadAccess(any(Contract.class))).thenReturn(true);
        when(mockedSession.getUser()).thenReturn( adminUser );

        textMatcher = new TextMatcher();



    }



    /*****************************************************************************'
     *
     *          Test to search in the demo project. This may be a bit instable if the demo project changes.
     *
     *
     */


    @Test
    public void testDemoProject(){

        try {

            JSONObject json;

            json = new SearchManager().getMatchJson("Google", demoProject, mockedSession);
            expectMatches(json, 43);

            json = new SearchManager().getMatchJson("Google Analytics", demoProject, mockedSession);
            expectMatches(json, 13);

            json = new SearchManager().getMatchJson("åäö", demoProject, mockedSession);
            expectMatches(json, 1);


        } catch (BackOfficeException e) {
            e.printStackTrace();
        }


    }


    /*********************************************************************************
     *
     *
     *          Matching an annotation should also return the fragment
     *
     */


    @Test
    public void testAnnotationMatches(){

        try {
            JSONObject json;

            json = new SearchManager().getMatchJson("annotation!", demoProject, mockedSession);
            expectMatches(json, 1);


        } catch (BackOfficeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    @Test
    public void testClassificationMatches(){

        try {
            JSONObject json;

            json = new SearchManager().getMatchJson("#Date", demoProject, mockedSession);
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + json);

            expectMatches(json, 2);    // A bit volatile. If there is changes to the text we may find more "date"

            // Now check the pattern.

            JSONObject res = new JSONObject(json);
            JSONArray hits = res.getJSONArray("fragments");
            JSONObject theHit = (JSONObject)hits.get( 0 );
            int ordinal = theHit.getInt("ordinal");
            JSONArray patternList = theHit.getJSONArray("patternlist");
            JSONObject patternObject = (JSONObject)patternList.get(0);
            String pattern = patternObject.getString("pattern");


            assertThat(ordinal, is(( 1 )));
            assertThat(pattern, is("2014-07-01"));


        } catch (BackOfficeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
    @Test
    public void testRiskMatches(){

        try {
            JSONObject json;

            json = new SearchManager().getMatchJson("Blocker", demoProject, mockedSession);
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + json);

            expectMatches(json, 1);    // A bit volatile. If there is changes to the text we may find more "risk"

            // Now check the pattern.

            JSONObject res = new JSONObject(json);
            JSONArray hits = res.getJSONArray("fragments");
            JSONObject theHit = (JSONObject)hits.get( 0 );
            int ordinal = theHit.getInt("ordinal");
            JSONArray patternList = theHit.getJSONArray("patternlist");
            JSONObject patternObject = (JSONObject)patternList.get(0);
            String pattern = patternObject.getString("pattern");


            assertThat(ordinal, is(( 1 )));
            assertThat(pattern, is("2014-07-01"));


            json = new SearchManager().getMatchJson("#Blocker", demoProject, mockedSession);
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + json);

            expectMatches(json, 1);    // A bit volatile. If there is changes to the text we may find more "risk"


        } catch (BackOfficeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }



    }

    @Test
    public void testRiskMatches2(){

        try {
            JSONObject json;

            json = new SearchManager().getMatchJson("#Risk", demoProject, mockedSession);
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + json);

            expectMatches(json, 1);    // A bit volatile. If there is changes to the text we may find more "risk"

            // Now check the pattern.

            JSONObject res = new JSONObject(json);
            JSONArray hits = res.getJSONArray("fragments");
            JSONObject theHit = (JSONObject)hits.get( 0 );
            int ordinal = theHit.getInt("ordinal");
            JSONArray patternList = theHit.getJSONArray("patternlist");
            JSONObject patternObject = (JSONObject)patternList.get(0);
            String pattern = patternObject.getString("pattern");


            assertThat(ordinal, is(( 1 )));
            assertThat(pattern, is("2014-07-01"));


        } catch (BackOfficeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }



    }


    @Test
    public void testRiskMatchesGenericTag(){

        try {
            JSONObject json;

            json = new SearchManager().getMatchJson("#Risk", demoProject, mockedSession);
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + json);

            expectMatches(json, 1);    // A bit volatile. If there is changes to the text we may find more "risk"

            // Now check the pattern.

            JSONObject res = new JSONObject(json);
            JSONArray hits = res.getJSONArray("fragments");
            JSONObject theHit = (JSONObject)hits.get( 0 );
            int ordinal = theHit.getInt("ordinal");
            JSONArray patternList = theHit.getJSONArray("patternlist");
            JSONObject patternObject = (JSONObject)patternList.get(0);
            String pattern = patternObject.getString("pattern");


            assertThat(ordinal, is(( 1 )));
            assertThat(pattern, is("2014-07-01"));


        } catch (BackOfficeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }



    }



    /*********************************************************************************
     *
     *
     *          Matching an headline should return all fragments fo the clause
     *
     *          //TODO: Test case missing
     *
     */


    @Test
    public void testHeadlineMatches(){

        try {
            JSONObject json;

            //json = new SearchManager().getMatchJson("Definitions", demoProject, mockedSession);
            //expectMatches(json, 29);


        } catch (Exception e) {

            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            assertTrue(false);
        }


    }





}


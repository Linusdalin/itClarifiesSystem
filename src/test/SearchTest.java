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
import system.TextMatcher;
import userManagement.PortalUser;
import userManagement.PortalUserTable;
import userManagement.SessionManagement;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

/**
 *
 */


public class SearchTest {

    /******************************************************************
     *
     *
     *
     */


    private static BackOfficeInterface bo;
    private static LocalServiceTestHelper helper;
    private static Project demo;
    private static PortalUser adminUser;
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

        demo = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));
        adminUser = new PortalUser(new LookupItem().addFilter(new ColumnFilter(PortalUserTable.Columns.Name.name(), "admin")));

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

            json = new SearchManager().getMatchJson("Google", demo, mockedSession);
            expectMatches(json, 46);

            json = new SearchManager().getMatchJson("Google Analytics", demo, mockedSession);
            expectMatches(json, 14);

            json = new SearchManager().getMatchJson("åäö", demo, mockedSession);
            expectMatches(json, 2);


        } catch (BackOfficeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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

            json = new SearchManager().getMatchJson("annotation!", demo, mockedSession);
            expectMatches(json, 1);


        } catch (BackOfficeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    @Test
    public void testClassificationMatches(){

        try {
            JSONObject json;

            json = new SearchManager().getMatchJson("Datum", demo, mockedSession);      // TODO: The tags are not language independent. We have to fix
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + json);

            expectMatches(json, 1);    // A bit volatile. If there is changes to the text we may find more "date"

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

            json = new SearchManager().getMatchJson("Blocker", demo, mockedSession);
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

            json = new SearchManager().getMatchJson("#Risk", demo, mockedSession);
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
     */


    @Test
    public void testHeadlineMatches(){

        try {
            Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));
            JSONObject json;

            json = new SearchManager().getMatchJson("Definitions", project, mockedSession);
            expectMatches(json, 29);


        } catch (BackOfficeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }




    private void expectMatches(JSONObject json, int expected) {

        JSONArray matches = json.getJSONArray("fragments");

        if(matches.length() != expected){

            PukkaLogger.log(PukkaLogger.Level.INFO, "Expected " + expected + " matches but found " + matches + " in JSON:\n" + json);
        }

        assertThat(matches.length(), is(expected));

    }


}


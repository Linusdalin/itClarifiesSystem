package test;

import backend.ItClarifies;
import cache.ServiceCache;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import project.Project;
import project.ProjectTable;
import language.English;
import language.LanguageInterface;
import language.Swedish;
import log.PukkaLogger;
import module.Module;
import module.ModuleTable;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hamcrest.CoreMatchers;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupItem;
import pukkaBO.exceptions.BackOfficeException;
import services.ContractServlet;
import services.ItClarifiesService;
import userManagement.PortalUser;
import userManagement.PortalUserTable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2014-07-03
 * Time: 15:42
 * To change this template use File | Settings | File Templates.
 */
public class ServletTests extends PukkaTest{

    protected static final LanguageInterface English = new English();
    protected static final LanguageInterface Swedish = new Swedish();

    protected static Project demoProject;
    protected static Module demoModule;
    protected static PortalUser adminUser, demoUser;

    protected static LocalServiceTestHelper helper;
    protected static HttpServletRequest request;
    protected static HttpServletResponse response;


    protected void isKey(String key){

        assertNotNull( key );
        assertTrue(key.length() > 10);    // This is just a small test. Google App Engine keys are longer

        assertFalse(key.contains("{"));
        assertFalse(key.contains("}"));
        assertFalse(key.contains("["));
        assertFalse(key.contains("}"));
    }

    protected static void init(){

        helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
        helper.setUp();


        // Setup database

        try {

            BackOfficeInterface bo;

            bo = new ItClarifies();
            bo.createDb();
            bo.populateValues(true);

            PukkaLogger.setLogLevel(PukkaLogger.Level.DEBUG);

            // Demo values from the database

            demoProject = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));
            demoModule = new Module(new LookupItem().addFilter(new ColumnFilter(ModuleTable.Columns.Name.name(), "Test")));
            adminUser = new PortalUser(new LookupItem().addFilter(new ColumnFilter(PortalUserTable.Columns.Name.name(), "admin")));
            demoUser = new PortalUser(new LookupItem().addFilter(new ColumnFilter(PortalUserTable.Columns.Name.name(), "demo")));

            // Request mock items

            request = mock(HttpServletRequest.class);
            response = mock(HttpServletResponse.class);

            ServiceCache cache = new ServiceCache("Token");

            cache.store("DummyAdminToken",      "admin@2020-01-01 00:00:00#127.0.0.1", "");
            cache.store("DummySessionToken",    "demo@2020-01-01 00:00:00#127.0.0.1", "");
            cache.store("DummyEveToken",        "eve@2020-01-01 00:00:00#127.0.0.1", "");




        } catch (BackOfficeException e) {

            e.printStackTrace();
            assertTrue(false);
        }



    }


    protected void assertCorrectlyDeleted(String service, JSONObject json) {

        assertThat(json.getString(service), is("DELETED"));
    }


    protected void assertError(JSONObject json, ItClarifiesService.ErrorType session) {

        JSONArray errorData = json.getJSONArray("error");
        JSONObject first = (JSONObject)errorData.get(0);
        assertThat(first.getString("type"), is(session.name()));

    }


    protected void assertDocumentsInProject(Project project) {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        try{

            MockWriter mockWriter = new MockWriter();

            when(request.getParameter("session")).thenReturn("DummyAdminToken");
            when(request.getParameter("project")).thenReturn(project.getKey().toString());
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getWriter()).thenReturn(mockWriter.getWriter());

            new ContractServlet().doGet(request, response);

            String output = mockWriter.getOutput();
            PukkaLogger.log(PukkaLogger.Level.INFO, "JSON: " + output);

            JSONObject json = new JSONObject(output);
            JSONArray documents = json.getJSONArray("Document");

            assertVerbose("Expecting to find 2 documents", documents.length(), is(2));

            JSONObject doc1 = (JSONObject)documents.get( 0 );
            JSONObject doc2 = (JSONObject)documents.get( 1 );

            assertThat(doc1.getString("name"), CoreMatchers.is("Cannon"));
            isKey(doc1.getString("id"));
            assertThat(doc2.getString("name"), CoreMatchers.is("Google Analytics"));

        }catch(Exception e){

            e.printStackTrace();
            assertThat("Error getting documents in project", false, is(true));
        }

    }

    protected void expectMatches(JSONObject json, int expected) {

        JSONArray matches = json.getJSONArray("fragments");

        assertVerbose("Expected " + expected + " matches but found " + matches + " in JSON:\n" + json, matches.length(), CoreMatchers.is(expected));

    }









}

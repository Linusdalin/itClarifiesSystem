package test;

import contractManagement.Project;
import language.English;
import language.LanguageInterface;
import language.Swedish;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import services.ContractServlet;
import services.ItClarifiesService;

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

    protected void isKey(String key){

        assertNotNull( key );
        assertTrue(key.length() > 10);    // This is just a small test. Google App Engine keys are longer

        assertFalse(key.contains("{"));
        assertFalse(key.contains("}"));
        assertFalse(key.contains("["));
        assertFalse(key.contains("}"));
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

            assertThat(documents.length(), CoreMatchers.is(2));

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

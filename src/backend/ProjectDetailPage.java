package backend;

import classification.ClassificationOverviewManager;
import classification.ClassificationOverviewServlet;
import contractManagement.Project;
import crossReference.Definition;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import pukkaAnalysis.TransactionTable;
import pukkaAnalysis.UserTable;
import pukkaBO.GenericPage.NarrowPage;
import pukkaBO.GenericPage.PageTab;
import pukkaBO.GenericPage.PageTabInterface;
import pukkaBO.accordion.Accordion;
import pukkaBO.acs.ACS_LoginMethod;
import pukkaBO.acs.ACS_User;
import pukkaBO.acs.AccessControlSystem;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.LookupList;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.statisticsBox.StatisticsBox;
import pukkaBO.style.Html;
import pukkaBO.style.StyleWidth;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-01-21
 * Time: 15:04
 * To change this template use File | Settings | File Templates.
 */

public class ProjectDetailPage extends NarrowPage {

    public ProjectDetailPage(Project project){

        super(  "projectDetailPage",
                "Project Details");


        setSection("Organizations and Projects");
        setList("ProjectList");

        addTab(new ClassificationTab ("Classifications", "Classifications for Project", project));
        addTab(new DefinitionsTab    ("Definitions",     "Definitions for Project",     project));

    }

    /***************************************************************************''
     *
     *      Classification Tab
     *
     */


    private class ClassificationTab extends PageTab implements PageTabInterface {

        private Project project;

        ClassificationTab(String title, String headline, Project project){

            super(title, headline);
            this.project = project;
        }

        @Override
        public String getBody(String page, int tabId, BackOfficeInterface backOffice, HttpServletRequest req) throws BackOfficeException {

            StringBuffer html = new StringBuffer();

            html.append(Html.paragraph("Classification overview of the project " + project.getName()) + Html.newLine() + Html.newLine());

            ClassificationOverviewManager overview = new ClassificationOverviewManager();
            overview.compileClassificationsForProject(project, null);
            JSONObject json = overview.getStatistics();

            html.append("<table class=\"minimalist\" id=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
            html.append("<thead><th colspan=\"6\">Classification Tag</th> <th>Occurences</th></thead>");
            html.append("<tbody>");
            html.append("<tr><td width=\"30\"></td><td width=\"30\"></td><td width=\"30\"></td><td width=\"30\"></td><td width=\"30\"></td><td width=\"200\"></td><td></td></tr>");

            if(json != null)
                html.append(traverseOverviewJson(json, 0));
            html.append("</tbody>");
            html.append("</table>" + Html.newLine()  + Html.newLine() + Html.newLine());

            /*

            html.append("<pre>");

            if(json != null)
                html.append(json.toString( 4 ));
            html.append("</pre>");

            */

            return html.toString();
        }


        private String traverseOverviewJson(JSONObject node, int level){

            String name = node.getString("classification");
            JSONObject statistics = node.getJSONObject("statistics");
            JSONArray children = node.getJSONArray("subClassifications");
            int hits = statistics.getInt("direct") + statistics.getInt("indirect");

            StringBuffer row = new StringBuffer();
            row.append("<tr>");
            String emptyTab = "<td></td>";
            row.append(StringUtils.repeat(emptyTab, level));
            row.append("<td colspan=\""+(6 - level) +"\">"+ name+"</td><td>"+hits+"</td></tr>");

            for(int i = 0; i < children.length(); i++){

                row.append(traverseOverviewJson(children.getJSONObject( i ), level+1));

            }

            return row.toString();

        }

    }


    private class DefinitionsTab extends PageTab implements PageTabInterface {

        private Project project;

        DefinitionsTab(String title, String headline, Project project){

            super(title, headline);
            this.project = project;
        }

        @Override
        public String getBody(String page, int tabId, BackOfficeInterface backOffice, HttpServletRequest req) throws BackOfficeException {

            StringBuffer html = new StringBuffer();

            html.append(Html.paragraph("Classification overview of the project " + project.getName()) + Html.newLine() + Html.newLine());

            ClassificationOverviewManager overview = new ClassificationOverviewManager();
            overview.compileClassificationsForProject(project, null);
            JSONObject json = overview.getStatistics();

            html.append("<table class=\"minimalist\" id=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
            html.append("<thead><th width=\"100px\">Definition</th> <th width=\"300px\">Fragment Text</th> <th width=\"100px\">Document</th> <th width=\"100px\">Source</th></thead>");
            html.append("<tbody>");

            List<Definition> definitionsForProject = project.getDefinitionsForProject();

            for (Definition definition : definitionsForProject) {

                html.append("<tr>");
                html.append("<td>"+ definition.getName()+"</td><td>"+ definition.getDefinedIn().getText()+"</td><td>"+definition.getVersion().getDocument().getName()+"</td><td>"+ definition.getDefinedInId()+"</td>");
                html.append("</tr>");
            }


            html.append("</tbody>");
            html.append("</table>" + Html.newLine()  + Html.newLine() + Html.newLine());

            return html.toString();
        }

    }

    private class DashboardTab2 extends PageTab implements PageTabInterface {

        DashboardTab2(String title, String headline){

            super(title, headline);
        }

        public String getBody(String page, int tabId, BackOfficeInterface backOffice, String[] parameters, ACS_User adminUser, AccessControlSystem acs, ACS_LoginMethod loginMethod) throws BackOfficeException {

            return "Body for test tab "+(tabId+1)+" on page "+ page+"... Add information here.";
        }

    }



}


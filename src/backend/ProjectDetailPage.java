package backend;

import classification.ClassificationOverviewManager;
import classification.ClassificationOverviewServlet;
import contractManagement.Project;
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

/**
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

        addTab(new ClassificationTab("Classifications", "Classifications for Project", project));
        //addTab(new DashboardTab2("Statistics", "Headline for page 2"));
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

/*
            html.append("<table class=\"minimalist\" id=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                    "<colgroup>\n" +
                    "   <col class=\"con0\">\n" +
                    "   <col class=\"con1\">\n" +
                    "   <col class=\"con0\">\n" +
                    "   <col class=\"con1\">\n" +
                    "   <col class=\"con0\">\n" +
                    "   <col class=\"con1\">\n" +
                    "   <col class=\"con0\">\n" +
                    "   <col class=\"con1\">\n" +
                    "</colgroup>\n" +
                    "<thead><tr>\n" +
                    "<th> Name</th>\n" +
                    "<th> Description</th>\n" +
                    "<th> Creator</th>\n" +
                    "<th> Organization</th>\n" +
                    "<th> CreationTime</th>\n" +
                    "<th></th><th></th><th></th>\n" +
                    "</tr></thead>\n" +
                    "<tbody>\n" +
                    "<tr class=\"odd narrow\"><td class=\" width40 state-normal\">Demo</td><td class=\" width120 state-normal\">Test project</td><td class=\" width240 state-normal\">admin</td><td class=\" width120 state-normal\">demo.org</td><td class=\" width120 state-normal\">2014-02-01</td><td></td>\n" +
                    "<td class=\" width45 norightborder\"><a class=\"btn btn3_small btn_trash\" href=\"?action=List&amp;callbackAction=2&amp;id=ahNpdGNsYXJpZmllc2FwaXN0YWdlchQLEgdQcm9qZWN0GICAgICAtpoJDA&amp;list=ProjectList&amp;section=Organizations and Projects\"></a></td><td class=\" width45 norightborder\"><a class=\"btn btn3_small btn_search\" href=\"?action=Item&amp;callbackAction=3&amp;id=ahNpdGNsYXJpZmllc2FwaXN0YWdlchQLEgdQcm9qZWN0GICAgICAtpoJDA&amp;list=ProjectList&amp;section=Organizations and Projects\"></a></td></tr>\n" +
                    "<tr></tr>\n" +
                    "</tbody>\n" +
                    "</table>");
  */
            html.append("<table class=\"minimalist\" id=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
            html.append("<thead><th colspan=\"6\">Classification Tag</th> <th>Occurences</th></thead>");
            html.append("<tbody>");
            html.append("<tr><td width=\"30\"></td><td width=\"30\"></td><td width=\"30\"></td><td width=\"30\"></td><td width=\"30\"></td><td width=\"200\"></td><td></td></tr>");
            html.append(traverseOverviewJson(json, 0));
            html.append("</tbody>");
            html.append("</table>");

            //html.append("<pre>");
            //html.append(json.toString( 4 ));
            //html.append("</pre>");


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



    private class DashboardTab2 extends PageTab implements PageTabInterface {

        DashboardTab2(String title, String headline){

            super(title, headline);
        }

        public String getBody(String page, int tabId, BackOfficeInterface backOffice, String[] parameters, ACS_User adminUser, AccessControlSystem acs, ACS_LoginMethod loginMethod) throws BackOfficeException {

            return "Body for test tab "+(tabId+1)+" on page "+ page+"... Add information here.";
        }

    }



}


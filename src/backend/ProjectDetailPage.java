package backend;

import actions.Checklist;
import actions.ChecklistItem;
import actions.ChecklistItemTable;
import classification.ClassificationOverviewManager;
import contractManagement.ContractFragment;
import contractManagement.ContractVersionInstance;
import contractManagement.Project;
import crossReference.Definition;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import pukkaBO.GenericPage.NarrowPage;
import pukkaBO.GenericPage.PageTab;
import pukkaBO.GenericPage.PageTabInterface;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.LookupList;
import pukkaBO.condition.Ordering;
import pukkaBO.condition.Sorting;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.style.Html;
import risk.RiskClassification;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**********************************************************************************
 *
 *
 *              Back-office page for information on a project
 *
 *
 *              The page has different tabs for different information on the project
 *
 *
 */

public class ProjectDetailPage extends NarrowPage {

    public ProjectDetailPage(Project project){

        super(  "projectDetailPage",
                "Project Details");


        setSection("Organizations and Projects");
        setList("ProjectList");

        addTab(new ClassificationTab ("Classifications","Classifications for Project", project));
        addTab(new DefinitionsTab    ("Definitions",    "Definitions for Project",     project));
        addTab(new RisksTab          ("Risks",          "Risks for Project",           project));
        addTab(new ChecklistTab      ("Checklists",     "Checklists for Project",      project));

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

                String documentName = "- deleted -";
                String fragmentBody = "- no fragment -";
                ContractFragment definedIn = definition.getDefinedIn();

                if(definedIn.exists()){

                    fragmentBody = definedIn.getText();
                    ContractVersionInstance documentVersion = definedIn.getVersion();
                    if(documentVersion.exists()){

                        documentName = documentVersion.getDocument().getName();
                    }
                }

                html.append("<tr>");
                html.append("<td>"+ definition.getName()+"</td><td>"+ fragmentBody +"</td><td>" + documentName+"</td><td>"+ definition.getDefinedInId()+"</td>");
                html.append("</tr>");
            }


            html.append("</tbody>");
            html.append("</table>" + Html.newLine()  + Html.newLine() + Html.newLine());

            return html.toString();
        }

    }

    private class RisksTab extends PageTab implements PageTabInterface {

        private Project project;

        RisksTab(String title, String headline, Project project){

            super(title, headline);
            this.project = project;
        }

        @Override
        public String getBody(String page, int tabId, BackOfficeInterface backOffice, HttpServletRequest req) throws BackOfficeException {

            StringBuffer html = new StringBuffer();

            html.append(Html.paragraph("Risk overview of the project " + project.getName()) + Html.newLine() + Html.newLine());

            List<RiskClassification> risksForProject = project.getRiskClassificationsForProject();

            html.append("<table class=\"minimalist\" id=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
            html.append("<thead><th width=\"100px\">Risk</th> <th width=\"300px\">Fragment Text</th> <th width=\"100px\">Document</th> <th width=\"100px\">Pattern</th></thead>");
            html.append("<tbody>");

            for (RiskClassification riskClassification : risksForProject) {


                html.append("<tr>");
                html.append("<td>"+ riskClassification.getRisk().getName()+"</td><td>"+ riskClassification.getFragment().getText()
                        +"</td><td>" + riskClassification.getVersion().getDocument().getName()+"</td><td>"+ riskClassification.getPattern()+"</td>");
                html.append("</tr>");
            }


            html.append("</tbody>");
            html.append("</table>" + Html.newLine()  + Html.newLine() + Html.newLine());

            return html.toString();
        }

    }


    private class ChecklistTab extends PageTab implements PageTabInterface {

        private Project project;

        ChecklistTab(String title, String headline, Project project){

            super(title, headline);
            this.project = project;
        }

        @Override
        public String getBody(String page, int tabId, BackOfficeInterface backOffice, HttpServletRequest req) throws BackOfficeException {

            StringBuffer html = new StringBuffer();

            html.append(Html.paragraph("Checklist overview of the project " + project.getName()) + Html.newLine() + Html.newLine());

            List<Checklist> checklistListsForProject = project.getChecklistsForProject();

            html.append("<table class=\"minimalist\" id=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
            html.append("<thead><th width=\"100px\">Checklist</th> <th width=\"30px\"></th><th width=\"500px\">Items</th> <th width=\"100px\"></th> <th width=\"100px\">Tag</th></thead>");
            html.append("<tbody>");

            for (Checklist checklist : checklistListsForProject) {

                html.append("<tr>");
                html.append("<td>"+ checklist.getName()+"</td><td>" +"</td><td>"
                        +"</td><td>" +"</td><td>"+ "</td>");
                html.append("</tr>");

                List<ChecklistItem> items = checklist.getChecklistItemsForChecklist(new LookupList().addSorting(new Sorting(ChecklistItemTable.Columns.Identifier.name(), Ordering.FIRST)));

                for (ChecklistItem item : items) {

                    html.append("<tr>");
                    html.append("<td>"+"</td><td>"+ item.getIdentifier()+ "</td><td>" + item.getDescription()
                            +"</td><td>" +item.getTagReference() +"</td><td>"+ "</td>");
                    html.append("</tr>");

                }

            }


            html.append("</tbody>");
            html.append("</table>" + Html.newLine()  + Html.newLine() + Html.newLine());

            return html.toString();
        }

    }



}


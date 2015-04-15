package backend;

import actions.Checklist;
import actions.ChecklistItem;
import actions.ChecklistItemTable;
import classification.ClassificationOverviewManager;
import httpRequest.ServerFactory;
import log.PukkaLogger;
import reclassification.*;
import contractManagement.Contract;
import contractManagement.ContractFragment;
import contractManagement.ContractVersionInstance;
import contractManagement.Project;
import crossReference.Definition;
import dataRepresentation.DataObjectInterface;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import pukkaBO.GenericPage.NarrowPage;
import pukkaBO.GenericPage.PageTab;
import pukkaBO.GenericPage.PageTabInterface;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.*;
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

        addTab(new ClassificationTab  ("Classifications",   "Classifications for Project",      project));
        addTab(new DefinitionsTab     ("Definitions",       "Definitions for Project",          project));
        addTab(new RisksTab           ("Risks",             "Risks for Project",                project));
        addTab(new ChecklistTab       ("Checklists",        "Checklists for Project",           project));
        addTab(new ReclassificationTab("Reclassification",  "Code for reclassifying a project", project));

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
            html.append("<thead><th width=\"100px\">Definition</th> <th width=\"300px\">Fragment Text</th> <th width=\"100px\">Document</th> <th width=\"100px\">Source</th><th width=\"100px\">Definition</th></thead>");
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
                html.append("<td>"+ definition.getName()+"</td><td>"+ fragmentBody +"</td><td>" + documentName+"</td><td>"+ definition.getDefinedInId()+"</td><td>"+ definition.getDefinition()+"</td>");
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


    private class ReclassificationTab extends PageTab implements PageTabInterface {

        private Project project;

        ReclassificationTab(String title, String headline, Project project){

            super(title, headline);
            this.project = project;
        }

        @Override
        public String getBody(String page, int tabId, BackOfficeInterface backOffice, HttpServletRequest req) throws BackOfficeException {

            StringBuffer html = new StringBuffer();

            html.append(Html.paragraph("Reclassifications for the project " + project.getName() + " copy this code and use it to execute the <i>replace</i> step in the lift/sweep/replace process.") + Html.newLine() + Html.newLine());

            List<Contract> documentsInProject = project.getContractsForProject();

            String serverName = ServerFactory.getLocalSystem();

            html.append("<pre>\n");
            html.append(getConfigurationExport(project, serverName));

            for (Contract document : documentsInProject) {

                html.append(getAllExports(project, document));

            }
            html.append("</pre>\n");

            return html.toString();

        }

        private String getConfigurationExport(Project project, String serverName){

            StringBuilder html = new StringBuilder();

            html.append("    /***********************************************************\n");
            html.append("     *\n");
            html.append("     *      Generate the correct name and target server\n");
            html.append("     */\n");

            html.append("         DBTimestamp now = new DBTimestamp();\n");
            html.append("         setProjectName(\""+project.getName()+"\");\n");
            html.append("         setTargetServer(\""+ serverName+"\");\n\n\n");

            return html.toString();
        }



        /*******************************************************************************************
         *
         *              Generate code for transposing classifications
         *
         *
         *
         * @param project               - the project to run it in
         * @param document              - the current document
         * @return
         */


        private String getAllExports(Project project, Contract document){

            StringBuilder html = new StringBuilder();
            String fileName = document.getFile();


            html.append("    /***********************************************************\n");
            html.append("     *\n");
            html.append("     *      Regeneration of classification, action, risk, and annotation\n");
            html.append("     *      Project:  "+ project.getName() +"\n");
            html.append("     *      Document: "+ document.getName() +"\n");
            html.append("     *\n");
            html.append("     */\n");

            int totalCount = 0;


                // First handle classifications

            ReclassificationTable reclassificationForDocument = new ReclassificationTable(new LookupList()
                    .addFilter(new ColumnFilter(ReclassificationTable.Columns.Project.name(), project.getName()))
                    .addFilter(new ColumnFilter(ReclassificationTable.Columns.Document.name(), document.getName())));

            for (DataObjectInterface object : reclassificationForDocument.getValues()) {

                Reclassification reclassification = (Reclassification)object;
                String theBody = reclassification.getFragment()
                        .replaceAll("\n", " ")
                        .replaceAll("%", "")
                        .replaceAll("\"", "&#92;\"");

                // Cap the size to optimize the communication. 500 chars should be sufficient to detect virtually all texts

                if(theBody.length() > 500){
                    theBody = theBody.substring(0, 500);
                }

                try {
                    html.append(        "            addClassification(new Reclassification("+"" +
                                                            "\""+reclassification.getClassification()+"\", "+ reclassification.getAdd()+", \""+ reclassification.getDate().getISODate()+"\", \""+project.getName()+"\", \""+fileName+"\", "+ reclassification.getFragmentNo()+",\n" +
                                        "                            \""+ theBody + "\",\n" +
                                        "                            \"" +reclassification.getPattern()+"\", "+
                                                                     reclassification.getPatternPos()+", \""+
                                                                     reclassification.getUser()+"\", false, now.getISODate()));\n\n");
                } catch (BackOfficeException e) {
                    PukkaLogger.log(e);
                }

            }
            totalCount += reclassificationForDocument.getValues().size();


            // Handle Risk

            ReriskTable reRiskForDocument = new ReriskTable(new LookupList()
                    .addFilter(new ColumnFilter(ReriskTable.Columns.Project.name(), project.getName()))
                    .addFilter(new ColumnFilter(ReriskTable.Columns.Document.name(), document.getName())));

            for (DataObjectInterface object : reRiskForDocument.getValues()) {

                Rerisk rerisk = (Rerisk)object;
                String theBody = rerisk.getFragment().replaceAll("\n", " ").replaceAll("\"", "&#92;\"");

                // Cap the size to optimize the communication. 500 chars should be sufficient to detect virtually all texts

                if(theBody.length() > 500){
                    theBody = theBody.substring(0, 500);
                }

                try {
                    html.append(        "            addRisk(new Rerisk("+"" +
                                                            "\""+rerisk.getRiskLevel()+"\", \""+ rerisk.getDate().getISODate()+"\", \""+project.getName()+"\", \""+fileName+"\", "+ rerisk.getFragmentNo()+",\n" +
                                        "                            \""+ theBody + "\",\n" +
                                        "                            \"" +rerisk.getPattern()+"\", "+
                                                                     rerisk.getPatternPos()+", \""+
                                                                     rerisk.getUser()+"\", false));\n\n");
                } catch (BackOfficeException e) {
                    PukkaLogger.log(e);
                }

            }

            totalCount += reRiskForDocument.getValues().size();

            // Handle Annotations

            ReannotationTable reAnnotationsForDocument = new ReannotationTable(new LookupList()
                    .addFilter(new ColumnFilter(ReannotationTable.Columns.Project.name(), project.getName()))
                    .addFilter(new ColumnFilter(ReannotationTable.Columns.Document.name(), document.getName())));

            for (DataObjectInterface object : reAnnotationsForDocument.getValues()) {

                Reannotation reAnnotation = (Reannotation)object;
                String theBody = reAnnotation.getFragment().replaceAll("\n", " ").replaceAll("\"", "&#92;\"");

                // Cap the size to optimize the communication. 500 chars should be sufficient to detect virtually all texts

                if(theBody.length() > 500){
                    theBody = theBody.substring(0, 500);
                }

                try {
                    html.append(        "            addAnnotation(new Reannotation("+"" +
                                                            "\""+reAnnotation.getText()+"\", "+ reAnnotation.getAdd() +", \""+ reAnnotation.getDate().getISODate()+"\", \""+project.getName()+"\", \""+fileName+"\", "+ reAnnotation.getFragmentNo()+",\n" +
                                        "                            \""+ theBody + "\",\n" +
                                        "                            \"" +reAnnotation.getPattern()+"\", "+
                                                                     reAnnotation.getPatternPos()+", \""+
                                                                     reAnnotation.getUser()+"\", false));\n\n");
                } catch (BackOfficeException e) {
                    PukkaLogger.log(e);
                }

            }
            totalCount += reAnnotationsForDocument.getValues().size();



            // Handle Definitions

            RedefinitionTable reDefinitionsForDocument = new RedefinitionTable(new LookupList()
                    .addFilter(new ColumnFilter(RedefinitionTable.Columns.Project.name(), project.getName()))
                    .addFilter(new ColumnFilter(RedefinitionTable.Columns.Document.name(), document.getName())));


            try {
                System.out.println("Found " + reDefinitionsForDocument.getCount() + " definitions.");
            } catch (BackOfficeException e) {
                PukkaLogger.log(e);
            }

            for (DataObjectInterface object : reDefinitionsForDocument.getValues()) {

                Redefinition reDefinition = (Redefinition)object;
                String theBody = reDefinition.getFragment().replaceAll("\n", " ").replaceAll("\"", "&#92;\"");

                // Cap the size to optimize the communication. 500 chars should be sufficient to detect virtually all texts

                if(theBody.length() > 500){
                    theBody = theBody.substring(0, 500);
                }

                try {
                    html.append(        "            addDefinition(new Redefinition("+"" +
                                                            "\""+reDefinition.getName()+"\", "+ reDefinition.getAdd() +", \""+project.getName()+"\", \""+fileName+"\", "+ reDefinition.getFragmentNo()+",\n" +
                                        "                            \""+ theBody + "\", false));\n\n");
                } catch (Exception e) {
                    PukkaLogger.log(e);
                }

            }
            totalCount += reDefinitionsForDocument.getValues().size();



            if(totalCount == 0){

                // There were no reclassification for the document. Just add a comment

                html.append("            // No manual classifications, risks or annotations for the document " + fileName + "\n\n");

            }



            html.append("\n\n");


            return html.toString();
        }



    }





}


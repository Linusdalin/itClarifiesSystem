<%@ page import="pukkaBO.backOffice.Section" %>
<%@ page import="pukkaBO.links.LinkElement" %>
<%@ page import="pukkaBO.backOffice.SectionGroups" %>
<%@ page import="pukkaBO.backOffice.SectionGroup" %>
<%@ page import="pukkaBO.pivot.PivotView" %>

        <!-- Old logo:

        <div class="well">

            <img src="../bo/test/pukkaLogo.png" alt="Brand Logo">

            </div>
        <div class="well">

            -->

<div class="mainright">
 <div class="mainrightinner">

    <%

        Section section = backOffice.getSection(selection);
        LinkElement[] actions;
        ListInterface[] tools;
        PivotView[] pivotViews;

        if(section == null){

            actions = new LinkElement[] {};
            tools = new ListInterface[] {};
            pivotViews = new PivotView[] {};
        }
        else{

            actions  = section.getActions();
            tools = section.getLists();
            pivotViews = section.getPivotViews();

        }
        if(actions.length + tools.length + pivotViews.length > 0){

            out.print("      <div class=\"widgetbox\">\n" +
                    "                \t<div class=\"title\"><h2 class=\"calendar\"><span>Actions</span></h2></div>\n" +
                    "                    <div class=\"widgetcontent padding10\">\n" +
                    "\t\t\t\t\t<div id=\"tabs-1\">                    \n" +
                    "                        <ul class=\"listthumb\">\n");



            for(LinkElement sectionAction : actions){

                try{

                    out.print(sectionAction.render(adminUser, acsSystem, loginMethod));

                }catch(BackOfficeException e){

                    System.out.println("Failing render action:" + e.narration);
                    e.printStackTrace();
                }


            }

            for(ListInterface tool : tools){

                try{

                    out.print(tool.getLink(section.menuTitle, adminUser, acsSystem, loginMethod));

                }catch(BackOfficeException e){

                    e.logError("Failing render tool link:");

                }


            }

            for(PivotView view : pivotViews){

                try{

                    out.print(view.getLink(section.menuTitle, adminUser, acsSystem, loginMethod));

                }catch(BackOfficeException e){

                    e.logError("Failing render pivot link:");

                }


            }


            out.print("                        </ul>\n" +
                    "                     </div>\n" +
                    "                    </div><!--widgetcontent-->\n" +
                    "                </div><!--widgetbox-->            \n");


        }

        // Get tables

        SectionGroups groups;

        if(section != null)
            groups = section.getTablesGroups();
        else
            groups = new SectionGroups(new SectionGroup[]{});


        for(SectionGroup group: groups.tableGroups){

            StringBuffer linkBuffer = new StringBuffer();

            for(DataTableInterface theTable : group.getTables()){

                try{

                    theTable.setBackOffice(backOffice);
                    linkBuffer.append(backOffice.getTableLink(theTable, adminUser, false, selection, acsSystem, loginMethod));

                }catch(BackOfficeException e){

                    System.out.println("Failing render action:" + e.narration);
                    e.printStackTrace();
                }
            }

            // Only print non empty lists

            if(linkBuffer.length() > 0){


                out.print("      <div class=\"widgetbox\">\n" +
                        "                \t<div class=\"title\"><h2 class=\"calendar\"><span>"+group.getName()+"</span></h2></div>\n" +
                        "                    <div class=\"widgetcontent padding10\">\n" +
                        "\t\t\t\t\t<div id=\"tabs-1\">                    \n" +
                        "                        <ul class=\"listthumb\">\n");



                out.print(linkBuffer.toString());

                out.print("             </ul>\n");
                out.print("           </div>\n");
                out.print("         </div>\n");

            }

        }


    %>
            </div>
    </div>


    <!-- Right nav end -->



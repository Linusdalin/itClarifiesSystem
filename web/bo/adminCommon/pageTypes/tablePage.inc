<%@ page import="pukkaBO.form.Form" %>
<%@ page import="pukkaBO.backOffice.LightBox" %>
<%@ page import="pukkaBO.renderer.TableRendererInterface" %>
<%@ page import="pukkaBO.formsPredefined.TableFilterForm" %>
<%@ page import="dataRepresentation.ColumnStructureInterface" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="pukkaBO.formsPredefined.TableEditForm" %>
<%@ page import="dataRepresentation.DataObjectInterface" %>
<%@ page import="pukkaBO.style.Html" %>
<%@ page import="pukkaBO.condition.*" %>
<html xmlns="http://www.w3.org/1999/xhtml">

<%@ include file="../includes/parameters.inc" %>

<head>
    <%@ include file="../includes/head.inc" %>
</head>

<body class="loggedin">
<%@ include file="../includes/verifyLoginSession.inc" %>

<%@ include file="../includes/header.inc" %>

    <!-- START OF MAIN CONTENT -->
    <div class="mainwrapper">
        <div class="mainwrapperinner">

            <%@ include file="../includes/menu.inc" %>

            <div class="maincontent">
            	<div class="maincontentinner">

                    <%@ include file="../includes/message.inc" %>

                    <%@ include file="../includes/tabs.inc" %>


                    <div class="content">
    <%

        TableRendererInterface renderer = backOffice.getRenderer();
        List<String> parameterList = new ArrayList<String>();


        if(action.equals("add") || (formAction != null && formAction.equals("add"))){

            System.out.println("Adding...");

            ColumnStructureInterface[] columns =  table.getColumns();

            //Get parameter values for all the form fields

            for(ColumnStructureInterface column : columns){

                parameterList.add(request.getParameter(column.getName()));
            }


            DBKeyInterface newId; // Value if the lookup fail

            try{

                DataObjectInterface object = table.getDataObject(parameterList);
                newId  = table.store(object);
                //form = new TableEditForm(table, object, TableEditForm.FormType.ADD, backOffice, selection);

                out.print("<p>A new entry with id " + newId + " was created</p>");
                //out.print("<input type=\"button\" value=\"Close\" onClick=\"parent.window.location.reload(true)\">");

            }
            catch(BackOfficeException e){

                System.out.println("Error adding element " + e.narration);
                e.printStackTrace();
            }
        }



        if(formAction != null && formAction.equals("update")){

            System.out.print("Updating...");

                ColumnStructureInterface[] columns =  table.getColumns();

                for(ColumnStructureInterface column : columns){

                    System.out.println("Trying to find parameter named " + column.getName());
                    parameterList.add(request.getParameter(column.getName()));
                }


                int newId = -1;

                try{

                    DataObjectInterface object = table.getDataObject(parameterList);
                    boolean success = (table.store(object) != null);

                    //form = new TableEditForm(table, object, TableEditForm.FormType.UPDATE, backOffice, selection);

                    if(success){

                        out.print(Html.successBox("Updated Entry"));

                        //out.print("Updated entry with id " + id + ".</p>");
                    }
                    else{
                        out.print(Html.errorBox("Failed to update Entry"));
                        //out.print("failed updating id " + id + "</p>");
                    }


                }
                catch(BackOfficeException e){

                    out.print(Html.errorBox("Failed to update Entry: " + e.narration));
                    //System.out.println("Error adding element " + e.narration);
                    //e.printStackTrace();
                }
            }


        if(table != null && action.equals("list")){

            out.println("<p>" + table.getDescription() + "</p><br/>");

            //TODO: Add help here

            out.print(renderer.getFilter());

        }

        // The actual table

        if(table != null && (action.equals("list") || action.equals("update") || action.equals("add"))){


            %>

                         <div class="contenttitle radiusbottom0">
         	                <h2 class="table"><span><% out.print(table.getTitle());%></span></h2>
                         </div><!--contenttitle-->

                    <%

                        //Link

                        out.print(renderer.getAddLink(table));
                        try{

                            out.print(renderer.getTableScript(table));

                            // Load from database with default sorting
                            table.loadFromDatabase(new LookupList().addSorting(new Sorting(table.getColumns()[ table.getNameColumn() ].getName(), Ordering.FIRST )));
                            int totalItems = table.getCount();
                            if(renderer.getTableAccess(adminUser, table, displayBrand, acsSystem, loginMethod)){

                                //TODO: Not implemented Read Only. Enforce ReadOnly access to tables by using "hasEdit"

                                out.print(renderer.getTableContent(table, id, selection, false, true, table.hasEdit(), request.getCookies()));
                                out.print(renderer.getPagination(selection, tableParameter, pageId, pageSize, totalItems));
                            }else{

                                out.print("<p>You have not sufficient access to vew this table</p>");
                            }

                        }catch(BackOfficeException e){


                            out.print("<p>No table</p>");
                            System.out.println(e.narration);
                            e.printStackTrace();

                        }
                        %>

                </div>
            <%

                    }else{

                    // No table selected. Print the title and the message instead


                    out.println("<h2>" + headline + "</h2>");
                    out.print("<p>" + message + "</p>");



                }
             %>


                </div>
                <%@ include file="../includes/footer.inc" %>


            </div>
                <%@ include file="../includes/rightNav.inc" %>

            </div>
         </div>
    </body>
</html>

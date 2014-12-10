<%@ page import="dataRepresentation.ColumnStructureInterface" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="dataRepresentation.DataObjectInterface" %>
<%@ page import="pukkaBO.exceptions.BackOfficeException" %>
<%@ page import="pukkaBO.formsPredefined.TableEditForm" %>
<%@ page import="pukkaBO.condition.LookupByKey" %>
<%@ page import="pukkaBO.style.Html" %>
<%@ page import="pukkaBO.backOffice.BackOfficeLocation" %>

<%@ include file="../bean.inc" %>
<%
    String selection = request.getParameter("section");
    String pageTitle = "";
%>

<%@ include file="adminCommon/includes/head.inc" %>
<%@ include file="adminCommon/includes/parameters.inc" %>
<%@ include file="adminCommon/includes/verifyLoginSession.inc" %>

        <%

            List<String> parameterList = new ArrayList<String>();
            //out.print("<h1>Add value</h1>");
            DataObjectInterface object = null;

            BackOfficeLocation location = new BackOfficeLocation(backOffice, selection, pageParameter, tabId);

            // Start by reading the parameters

            System.out.println("!!The action is: " + action);
            System.out.println("!!The FORM action is: " + formAction);
            if(formAction == null){
                formAction = "No Form Action";
            }


            if(formAction.equalsIgnoreCase("add")){

                // Form action is add. This means parse the parameters and add an object


                System.out.println("Adding object!");

                ColumnStructureInterface[] columns =  table.getColumns();

                //Get parameter values for all the form fields

                for(ColumnStructureInterface column : columns){

                    parameterList.add(request.getParameter(column.getName()));
                }


                DBKeyInterface newId = null; // Value if the lookup fail

                try{

                    object = table.getDataObject(parameterList);
                    newId  = table.store(object);
                    form = new TableEditForm(table, object, TableEditForm.FormType.ADD, location);

                    out.print(Html.successBox("Added new entry"));

                    out.print("<input type=\"button\" value=\"Close\" onClick=\"parent.window.location.reload(true)\">");

                }
                catch(BackOfficeException e){

                    System.out.println("Error adding element " + e.narration);
                    e.printStackTrace();
                }
            }
            else if(formAction.equalsIgnoreCase("update")){

                // Update an existing object


                System.out.println("Updating object!");

                ColumnStructureInterface[] columns =  table.getColumns();

                for(ColumnStructureInterface column : columns){

                    System.out.println("Trying to find parameter named " + column.getName());
                    parameterList.add(request.getParameter(column.getName()));
                }


                int newId = -1;

                try{

                    object = table.getDataObject(parameterList);
                    object.setKey(id);

                    boolean success = (table.update(object) != null);

                    form = new TableEditForm(table, object, TableEditForm.FormType.UPDATE, location);

                    if(success){

                        out.print(Html.successBox("Updated Entry"));
                        //out.print("<p>Updated entry with id " + id + ".</p>");
                        //out.print("<a href=\"#\" onClick=\"history.back(); return false;\"> Close</a>");
                        //out.print("<input type=\"button\" value=\"Close\" onClick=\"history.go(-1)\">");
                        out.print("<input type=\"button\" value=\"Close\" onClick=\"parent.window.location.reload(true)\">");
                    }


                }
                catch(BackOfficeException e){

                    System.out.println("Error updating element " + e.narration);
                    e.printStackTrace();
                }
            }
            else if(formAction.equalsIgnoreCase("DELETE")){

                try{


                    boolean success = false;

                    if(id != null){
                        object = table.getDataObject();
                        object.setKey(id);

                        success = table.deleteItem(object);

                    }
                    if(success){

                        out.print(Html.successBox("Entry deleted"));
                        out.print("<input type=\"button\" value=\"Close\" onClick=\"parent.window.location.reload(true)\">");


                    }
                    else{

                        out.print(Html.errorBox("Entry was NOT deleted"));
                        out.print("<input type=\"button\" value=\"Close\" onClick=\"parent.window.location.reload(true)\">");

                    }


                    out.print("<p>Item deleted</p>");
                    return;

                }catch(BackOfficeException e){

                    out.print(Html.errorBox("Delete entry FAILED"));
                    out.print("<input type=\"button\" value=\"Close\" onClick=\"parent.window.location.reload(true)\">");

                }
            }
            else{

                System.out.println("Action = \""+ action +"\" Lookup data!");


                //Not add. But if there is a row given, we need to read all the values to fill the form

                //out.print("id=" + id);

                if(id != null){

                    try{

                        //table.loadFromDatabase();
                        object = table.getDataObject(new LookupByKey(id));
                        form = new TableEditForm(table, object, TableEditForm.FormType.UPDATE, location);
                    }
                    catch(BackOfficeException e){

                        System.out.println("Error loading element " + id + e.narration);
                        e.printStackTrace();
                    }
                }
                else{

                    try{

                        object = table.getDataObject(parameterList);
                        form = new TableEditForm(table, object, TableEditForm.FormType.ADD, location);

                    }
                    catch(BackOfficeException e){

                        System.out.println("Error rendering form " + e.narration);
                        e.printStackTrace();
                    }

                }
            }

                if(form != null)
                    out.print(form.renderForm());
                else
                    out.print("<p>Error no form "+ formParameter + "</p>");


        %>


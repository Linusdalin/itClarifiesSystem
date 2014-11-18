<%@ page import="pukkaBO.condition.LookupList" %>

<!---
            This is the actual displaying of a list.
            It assumes a table width of 14 units.


  -->

    <%
        if(list == null)
            out.print("<p>No list</p>");
        else{

            try{

                if(list.getListAccess(adminUser, displayBrand, acsSystem, loginMethod)){

                    //out.print("<a href=\"csv.jsp?list="+ list.getName()+ "\"> export to csv</a>");
                    out.println(list.getRenderer().getTableScript("dyntable", "", ""));
                    out.println(list.getRenderer().getTableContent(list, new LookupList(), selection, false, backOffice));   //TODO: Make show id parameter in the list
                }
            }catch(BackOfficeException e){

                e.logError("Render list");
            }
        }


    %>


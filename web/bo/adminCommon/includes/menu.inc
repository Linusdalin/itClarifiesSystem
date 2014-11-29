<%@ page import="pukkaBO.exceptions.BackOfficeException" %>
<%@ page import="pukkaBO.backOffice.Menu" %>


<div class="mainleft">
    <div class="mainleftinner">

        <div class="leftmenu">
            <ul>

                <%    Menu menu = backOffice.getMenu();
                        out.print(menu.render(selection, displayBrand, acsSystem));
                  %>


            </ul>
        </div><!--leftmenu-->
    	<div id="togglemenuleft"><a></a></div>
    </div><!--mainleftinner-->
</div><!--mainleft-->



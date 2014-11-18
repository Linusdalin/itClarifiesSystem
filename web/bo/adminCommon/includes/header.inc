<%@ page import="pukkaBO.backOffice.EventHandlerInterface" %>
<!-- START OF HEADER -->
<div class="header radius5">
   	<div class="headerinner">

           <a href=""><h1 class="logotext"><% out.print(backOffice.getStyleConfig().caption); %></h1></a>


           <div class="headright">
           	<div class="headercolumn">&nbsp;</div>
           	<div id="searchPanel" class="headercolumn">
               	<div class="searchbox">
                       <form action="" method="post">

                           <!--
                           <input type="text" id="keyword" name="keyword" class="radius2" value="Search..." />
                           -->
                           <span class="stdform">

						<select id="selection2" name="selection" style="min-width:25%; background:#222; border-color:#222;">

                            <!--
                               <option value="1">All</option>
                               <option value="2">Ragga</option>
                               <option value="3">Pukka</option>

                             -->
                                <option value="2">Master</option>

                           </select>
                           </span>
                       </form>
                   </div><!--searchbox-->
               </div><!--headercolumn-->

               <%

                   // Add the event functionality

                   EventHandlerInterface eventHandler = backOffice.getEventHandler();

                   if(eventHandler != null){

                       // There is an event handler defined for the backoffice


               %>

           	<div id="notiPanel" class="headercolumn">
                   <div class="notiwrapper">
                       <a href="services/popupList.jsp?type=event" class="notialert radius2"><% out.print(eventHandler.getUnhandledEvents()); %></a>
                       <div class="notibox">
                           <ul class="tabmenu">
                               <!--
                               <li class="current"><a href="services/messages.jsp" class="msg">Messages (2)</a></li>
                               <li><a href="services/activities.jsp" class="act">Messages (3)</a></li>
                               -->
                               <li><a href="services/popupList.jsp?type=event" class="act">Events (<% out.print(eventHandler.getUnhandledEvents()); %>)</a></li>

                           </ul>
                           <br clear="all" />
                           <div class="loader"><img src="adminCommon/styles/bootstrap/images/loaders/loader3.gif" alt="Loading Icon" /> Loading...</div>
                           <div class="noticontent"></div><!--noticontent-->
                       </div><!--notibox-->
                   </div><!--notiwrapper-->
               </div><!--headercolumn-->

               <%
                   } // end eventHandler == null

                   System.out.println("Admin User: " + adminUser);
               %>



               <div id="userPanel" class="headercolumn">
                   <a href="" class="userinfo radius2">
                       <img src="adminCommon/styles/bootstrap/images/avatar.png" alt="" class="radius2" />
                       <span><strong><% out.print(adminUser.getName());%></strong></span>
                   </a>
                   <div class="userdrop">
                       <ul>
                           <li><a href="table.jsp?section=ACS&action=list&table=ACS_User">Account</a></li>
                           <li><a href="table.jsp?logout=true&section=<% out.print(selection); %>">Logout</a></li>
                       </ul>
                   </div><!--userdrop-->
               </div><!--headercolumn-->
           </div><!--headright-->

       </div><!--headerinner-->
</div><!--header-->
   <!-- END OF HEADER -->

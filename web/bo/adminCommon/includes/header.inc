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

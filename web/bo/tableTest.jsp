<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">


<!--

        table.jsp - Generic file for displaying a table

-->






<!--

        Include the correct functionality for the page.
-->

























<head>
    
<!-- Old head functionality


<link rel="stylesheet" type="text/css" href="../bo/adminCommon/styles/bootstrap/css/backOffice.css" />

<title></title>

<script type="text/html" src="../bo/adminCommon/styles/bootstrap/scripts.inc"></script>

< %@ include file="../scripts/scripts.inc" %>

    //TODO: Fix the style based on config parameter

-->

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title></title>
<link rel="stylesheet" type="text/css" href="../bo/adminCommon/styles/bootstrap/css/bootstrap.css"/>
<link rel="stylesheet" type="text/css" href="../bo/adminCommon/styles/bootstrap/css/backOffice.css"/>
<!--[if IE 9]>
    <link rel="stylesheet" media="screen" href="../bo/adminCommon/styles/bootstrap/css/ie9.css"/>
<![endif]-->

<!--[if IE 8]>
    <link rel="stylesheet" media="screen" href="../bo/adminCommon/styles/bootstrap/css/ie8.css"/>
<![endif]-->

<!--[if IE 7]>
    <link rel="stylesheet" media="screen" href="../bo/adminCommon/styles/bootstrap/css/ie7.css"/>
<![endif]-->
<script type="text/javascript" src="../bo/adminCommon/scripts/js/plugins/jquery-1.7.min.js"></script>
<script type="text/javascript" src="../bo/adminCommon/scripts/js/plugins/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="../bo/adminCommon/scripts/js/plugins/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../bo/adminCommon/scripts/js/custom/tables.js"></script>

<script type="text/javascript" src="../bo/adminCommon/scripts/js/plugins/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../bo/adminCommon/scripts/js/plugins/jquery.effects.core.min.js"></script>
<script type="text/javascript" src="../bo/adminCommon/scripts/js/plugins/jquery.effects.explode.min.js"></script>
<script type="text/javascript" src="../bo/adminCommon/scripts/js/plugins/jquery.colorbox-min.js"></script>
<script type="text/javascript" src="../bo/adminCommon/scripts/js/custom/media.js"></script>

<script type="text/javascript" src="../bo/adminCommon/scripts/js/plugins/colorpicker.js"></script>
<script type="text/javascript" src="../bo/adminCommon/scripts/js/plugins/jquery.alerts.js"></script>
<script type="text/javascript" src="../bo/adminCommon/scripts/js/custom/elements.js"></script>
<script type="text/javascript" src="../bo/adminCommon/scripts/js/custom/general.js"></script>
<script type="text/javascript" src="../bo/adminCommon/scripts/js/plugins/jquery.jgrowl.js"></script>
<script type="text/javascript" src="../bo/adminCommon/scripts/js/custom/form.js"></script>
<!--[if lt IE 9]>
	<script src="http://css3-mediaqueries-js.googlecode.com/svn/trunk/css3-mediaqueries.js"></script>
<![endif]-->

</head>


<body class="loggedin">
<!-- < %@ include file="../includes/verifyLogin.inc" %>  -->

<!-- START OF HEADER -->
<div class="header radius3">
   	<div class="headerinner">

           <a href=""><h1 class="logotext">Pukka Tool Back Office</h1></a>


           <div class="headright">
           	<div class="headercolumn">&nbsp;</div>
           	<div id="searchPanel" class="headercolumn">
               	<div class="searchbox">
                       <form action="" method="post">
                           <input type="text" id="keyword" name="keyword" class="radius2" value="Search here" />
                           <span class="stdform">
						<select id="selection2" name="selection" style="min-width:25%; background:#222; border-color:#222;">
                               <option value="1">All</option>
                               <option value="2">Ragga</option>
                               <option value="3">Pukka</option>
                           </select>
                           </span>
                       </form>
                   </div><!--searchbox-->
               </div><!--headercolumn-->
           	<div id="notiPanel" class="headercolumn">
                   <div class="notiwrapper">
                       <a href="ajax/messages.php" class="notialert radius2">5</a>
                       <div class="notibox">
                           <ul class="tabmenu">
                               <li class="current"><a href="ajax/messages.php" class="msg">Messages (2)</a></li>
                               <li><a href="ajax/activities.php" class="act">Recent Activity (3)</a></li>
                           </ul>
                           <br clear="all" />
                           <div class="loader"><img src="adminCommon/styles/bootstrap/images/loaders/loader3.gif" alt="Loading Icon" /> Loading...</div>
                           <div class="noticontent"></div><!--noticontent-->
                       </div><!--notibox-->
                   </div><!--notiwrapper-->
               </div><!--headercolumn-->
               <div id="userPanel" class="headercolumn">
                   <a href="" class="userinfo radius2">
                       <img src="adminCommon/styles/bootstrap/images/avatar.png" alt="" class="radius2" />
                       <span><strong>Justin Meller</strong></span>
                   </a>
                   <div class="userdrop">
                       <ul>
                           <li><a href="">Profile</a></li>
                           <li><a href="">Account Settings</a></li>
                           <li><a href="index.html">Logout</a></li>
                       </ul>
                   </div><!--userdrop-->
               </div><!--headercolumn-->
           </div><!--headright-->

       </div><!--headerinner-->
</div><!--header-->
   <!-- END OF HEADER -->


    <!-- START OF MAIN CONTENT -->
    <div class="mainwrapper">
        <div class="mainwrapperinner">

            





<div class="mainleft">
  	<div class="mainleftinner">

      	<div class="leftmenu">
            <ul>
            <!--
                    Menu list
                    Each list item below represents a tab in the menu. Set the selection
                    of any list item to make it, active.
            <li ><a href="../bo/page.jsp?page=dashboardPage&section=Home" class="btn_home"><span>Home</span></a></li>
            <li class="active"><a href="../bo/table.jsp?section=Reports" class="btn_pie"><span>Reports</span></a></li>
            <li ><a href="../bo/table.jsp?section=Users" class="btn_users"><span>Users</span></a></li>
            <li ><a href="../bo/table.jsp?section=Campaigns" class="btn_flag"><span>Campaigns</span></a></li>
            <li ><a href="../bo/table.jsp?section=Watchdog" class="btn_video"><span>Watchdog</span></a></li>
            <li ><a href="../bo/table.jsp?section=ACS" class="btn_note"><span>ACS</span></a></li>
            </ul>
            -->

                  <li ><a href="../bo/table.jsp?section=Home">Home</a></li>
<li ><a href="../bo/table.jsp?section=System">System</a></li>
<li ><a href="../bo/table.jsp?section=ACS">ACS</a></li>



        </ul>
        </div><!--leftmenu-->
    	<div id="togglemenuleft"><a></a></div>
    </div><!--mainleftinner-->
</div><!--mainleft-->

<!-- //TODO: Add logout                 out.print("<a href=\"?logout=true&section="+ selection+"\"> Logout </a></p>"); ->




            <div class="maincontent">
            	<div class="maincontentinner">


    					<div class="notifyMessage notification msgsuccess" style="padding:0;">
                            <a class="close"></a>
                        </div><!-- notification msginfo -->

                <!-- TODO: Fix tabs -->

                <ul class="maintabmenu">
                	<li class="current"><a href="dashboard.html">Tables</a></li>
                </ul><!--maintabmenu-->


    <h2>Default Headline</h2><p>Default Text</p>




        


                </div>
                <div class="footer">

    <!-- TODO: Make this a config parameter -->

    <span>&copy; 2011 - 2013 Pukka Gaming Solutions</span>
</div>


            </div>
                





        <!-- TODO: Add logo somewhere and load it from config

        <div class="well">

            <img src="../bo/test/pukkaLogo.png" alt="Brand Logo">

            </div>
        <div class="well">

            -->

<div class="mainright">
 <div class="mainrightinner">

    
            </div>
    </div>


    <!-- Right nav end -->




            </div>
         </div>
</body>


</html>
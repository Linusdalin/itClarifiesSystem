<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN">


<!--

        list.jsp - Generic file for displaying a list

-->







<!--

        Include the correct functionality for the page.
-->










<html xmlns="http://www.w3.org/1999/xhtml">



















<head>
    
<!-- Old head functionality


<link rel="stylesheet" type="text/css" href="../bo/adminCommon/styles/bootstrap/css/backOffice.css" />

<title>Test List</title>

<script type="text/html" src="../bo/adminCommon/styles/bootstrap/scripts.inc"></script>

< %@ include file="../scripts/scripts.inc" %>

-->

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>Test List</title>
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

<!-- Test added shadowbox -->

<link rel="stylesheet" type="text/css" href="../bo/adminCommon/scripts/shadowbox-303-base/shadowbox.css"/>
<script type="text/javascript" src="../bo/adminCommon/scripts/shadowbox-303-base/shadowbox.js"></script>
<script type="text/javascript">
Shadowbox.init({
    handleOversize: "resize",
    modal: true
});
</script>

<!-- End test shadowbox -->

<!--[if lt IE 9]>
	<script src="http://css3-mediaqueries-js.googlecode.com/svn/trunk/css3-mediaqueries.js"></script>
<![endif]-->

</head>


<body class="loggedin">














<!-- START OF HEADER -->
<div class="header radius3">
   	<div class="headerinner">

           <a href=""><h1 class="logotext">Pukka Test Back Office</h1></a>


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
           	<div id="notiPanel" class="headercolumn">
                   <div class="notiwrapper">
                       <a href="services/events.jsp" class="notialert radius2">0</a>
                       <div class="notibox">
                           <ul class="tabmenu">
                               <!--
                               <li class="current"><a href="services/messages.jsp" class="msg">Messages (2)</a></li>
                               <li><a href="services/activities.jsp" class="act">Messages (3)</a></li>
                               -->
                               <li><a href="services/events.jsp" class="act">Events (0)</a></li>
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
                       <span><strong>admin</strong></span>
                   </a>
                   <div class="userdrop">
                       <ul>
                           <li><a href="table.jsp?section=ACS&action=list&table=ACS_User">Account</a></li>
                           <li><a href="?logout=true&section=Home&list=TestList">Logout</a></li>
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

                  <li class="current"><a href="../bo/table.jsp?section=Home" class="btn_home"> <span>Home</span></a></li>
<li ><a href="../bo/table.jsp?section=System" class="btn_folder"> <span>System</span></a></li>
<li ><a href="../bo/table.jsp?section=ACS" class="btn_users"> <span>ACS</span></a></li>



            </ul>
        </div><!--leftmenu-->
    	<div id="togglemenuleft"><a></a></div>
    </div><!--mainleftinner-->
</div><!--mainleft-->




            <div class="maincontent">
            	<div class="maincontentinner">

                    <div class="notifyMessage notification msgsuccess" style="padding:0;">

    <a class="close"></a>

</div><!-- notification msginfo -->


                    <!-- Tabs should be implemented here -->

<ul class="maintabmenu">
	<li class="current"><a href="#">Home</a></li>
</ul><!--maintabmenu-->



                    <div class="content">



        <div class="contenttitle radiusbottom0">
            <h2 class="table"><span>Test List</span></h2>
        </div><!--contenttitle-->

        <!-- The actual list -->

            

<!---
            This is the actual displaying of a list.
            It assumes a table width of 14 units.


  -->

    
<table cellpadding="0" cellspacing="0" border="0" class="stdtable" id="">
<colgroup>
   <col class="con0" />
   <col class="con1" />
   <col class="con0" />
   <col class="con1" />
   <col class="con0" />
   <col class="con1" />
   <col class="con0" />
   <col class="con1" />
   <col class="con0" />
</colgroup>
<thead><tr>
<th class="head1" width="50px"> Id </th>
<th> Name</th>
<th>Occ:</th><th>Extra Element</th><th>Birth</th>
</tr></thead>
<tbody>
<tr class=odd><td class="state-grey">aglwdWtrYXRvb2xyDgsSCGV4YW1wbGUxGBkM</td>
<td width="20px" class="state-grey"> Player1 </td>
<td width="20px" class="state-grey">N/A</td><td width="20px" class="state-grey">calculate val for id; aglwdWtrYXRvb2xyDgsSCGV4YW1wbGUxGBkM</td><td width="20px" class="state-grey">N/A</td>
    <td class="padding0 width40 norightborder"></td>
    <td class="padding0 width40 norightborder"></td>
    <td class="padding0 width40 norightborder"></td>
    <td class="padding0 width20 norightborder"><a class="btn btn3 btn_refresh" href="?action=Undo&id=aglwdWtrYXRvb2xyDgsSCGV4YW1wbGUxGBkM&list=TestList&section=Home"></a></td>
    <td class="padding0 width20 norightborder"><a class="btn btn3 btn_search" href="itemView.jsp?id=aglwdWtrYXRvb2xyDgsSCGV4YW1wbGUxGBkM&list=TestList&section=Home"></a></td>
</tr>

<tr ><td class="state-grey">aglwdWtrYXRvb2xyDgsSCGV4YW1wbGUxGBoM</td>
<td width="20px" class="state-grey"> Player2 </td>
<td width="20px" class="state-grey">N/A</td><td width="20px" class="state-grey">calculate val for id; aglwdWtrYXRvb2xyDgsSCGV4YW1wbGUxGBoM</td><td width="20px" class="state-grey">N/A</td><td class="padding20 width45 norightborder"></td><td class="padding20 width45 norightborder"></td><td class="padding20 width45 norightborder"><a class="btn btn3 btn_refresh" href="?action=MarkAsUnRead&id=aglwdWtrYXRvb2xyDgsSCGV4YW1wbGUxGBoM&list=TestList&section=Home"></a></td><td class="padding20 width45 norightborder"></td><td class="padding20 width45 norightborder"><a class="btn btn3 btn_search" href="itemView.jsp?id=aglwdWtrYXRvb2xyDgsSCGV4YW1wbGUxGBoM&list=TestList&section=Home"></a></td></tr>
</tbody>
</table>







                        
                  <div class="footer">

    <span>&copy; 2011 Pukka Gaming Solutions</span>
</div>

                    </div>
                        





        <!-- Old logo:

        <div class="well">

            <img src="../bo/test/pukkaLogo.png" alt="Brand Logo">

            </div>
        <div class="well">

            -->

<div class="mainright">
 <div class="mainrightinner">

          <div class="widgetbox">
                	<div class="title"><h2 class="calendar"><span>Actions</span></h2></div>
                    <div class="widgetcontent padding10">
					<div id="tabs-1">                    
                        <ul class="listthumb">
<li><a href="page.jsp?page=testPage&section=Home" >Test Page</a><div style="float:right;"><img src="/bo/adminCommon/styles/bootstrap/images/i.gif" width="12" height="12" class="tooltipimage" title="This is a page title"></div></li><li><a href="lightbox.jsp?page=testLightbox" rel="shadowbox;width=700;height=700">Test Light box</a></li><li><a href="list.jsp?section=Home&list=TestList">Test List</a></li><li><a href="list.jsp?section=Home&list=GroupList">An example GroupBy List</a></li><li><a href="pivot.jsp?section=Home&table=ExamplePivotObject&XCol=1&YCol=2&Body=4&Function=2">An example pivot table</a></li>                        </ul>
                     </div>
                    </div><!--widgetcontent-->
                </div><!--widgetbox-->            
      <div class="widgetbox">
                	<div class="title"><h2 class="calendar"><span>Example Tables</span></h2></div>
                    <div class="widgetcontent padding10">
					<div id="tabs-1">                    
                        <ul class="listthumb">
<li><a href="table.jsp?section=Home&action=list&table=example1">Example1</a><div style="float:right;"><a href="#" rel='twipsy' title='This is an example data object to be stored'><img src="/bo/adminCommon/styles/bootstrap/images/i.gif"></a></div>
</li>
<li><a href="table.jsp?section=Home&action=list&table=example2">Example2</a><div style="float:right;"><a href="#" rel='twipsy' title='This is a more complex example data with referencing another table'><img src="/bo/adminCommon/styles/bootstrap/images/i.gif"></a></div>
</li>
<li><a href="table.jsp?section=Home&action=list&table=ExamplePivotObject">Pivot</a><div style="float:right;"><a href="#" rel='twipsy' title='this is the description of the pivot table'><img src="/bo/adminCommon/styles/bootstrap/images/i.gif"></a></div>
</li>
             </ul>
           </div>
         </div>

            </div>
    </div>


    <!-- Right nav end -->





                </div>
            </div>
    </body>
</html>


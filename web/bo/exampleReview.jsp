<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"><meta http-equiv="content-type" content="text/html; charset=UTF-8" />

<html>

<head>
    
<!-- Old head functionality


<link rel="stylesheet" type="text/css" href="../bo/adminCommon/styles/bootstrap/css/backOffice.css" />

<title>This is a page title</title>

<script type="text/html" src="../bo/adminCommon/styles/bootstrap/scripts.inc"></script>

< %@ include file="../scripts/scripts.inc" %>

-->

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>This is a page title</title>
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


<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>

    <link rel="stylesheet" href="http://code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
    <script src="http://code.jquery.com/ui/1.10.4/jquery-ui.js"></script>


    <script type="text/javascript" src="../bo/adminCommon/scripts/js/plugins/jquery.dataTables.min.js"></script>





<script type="text/javascript" src="../bo/adminCommon/scripts/js/plugins/jquery.effects.core.min.js"></script>
<script type="text/javascript" src="../bo/adminCommon/scripts/js/plugins/jquery.effects.explode.min.js"></script>




    <script type="text/javascript" src="../bo/adminCommon/scripts/js/plugins/colorpicker.js"></script>
    <script type="text/javascript" src="../bo/adminCommon/scripts/js/plugins/jquery.alerts.js"></script>


    <script type="text/javascript" src="../bo/adminCommon/scripts/js/custom/general.js"></script>
    <script type="text/javascript" src="../bo/adminCommon/scripts/js/plugins/jquery.jgrowl.js"></script>


    <script type="text/javascript" src="../bo/adminCommon/scripts/js/custom/tables.js"></script>

<!--
    <script type="text/javascript" src="../bo/adminCommon/scripts/js/plugins/jquery.colorbox-min.js"></script>
    <script type="text/javascript" src="../bo/adminCommon/scripts/js/custom/elements.js"></script>
    <script type="text/javascript" src="../bo/adminCommon/scripts/js/custom/media.js"></script>

-->



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

















    <!-- START OF MAIN CONTENT -->
    <div class="mainwrapper">
        <div class="mainwrapperinner">

            <div class="maincontent">
            	<div class="maincontentinner">

                    <div class="notifyMessage notification msgsuccess" style="padding:0;">

    <a class="close"></a>

</div><!-- notification msginfo -->


                    <ul class="maintabmenu">
	<li ><a href="?page=reviewPage&section=Home&tab=0">Overview</a></li>
	<li class="current"><a href="?page=reviewPage&section=Home&tab=1">Review</a></li>
	<li ><a href="?page=reviewPage&section=Home&tab=2">Statistics</a></li>
	<li ><a href="?page=reviewPage&section=Home&tab=3">Dates and Renewals</a></li>
	<li ><a href="?page=reviewPage&section=Home&tab=4">Financials</a></li>
</ul><!--maintabmenu-->  <div class="content"><h2></h2><p></p></br><h1>Google Analytics Service Agreement</h1>    <div style="width:500px;" id="xody">
<FORM METHOD=POST action="?page=reviewPage&tab=1&section=Home&formAction=search&form=FilterForm" id="FilterForm" name="FilterForm" class=" stdform quickform">
<fieldset>
<h3>Filtering</h3>

<p>	<script type="text/javascript">

    var countries = [
       { value: 'Andorra', data: 'AD' },
        { value: 'Sweden', data: 'AD' },
       { value: 'Zimbabwe', data: 'ZZ' }
    ];

    jQuery(document).ready(function(){

       // jQuery methods go here...

        jQuery('#SearchXX').autocomplete({
            source: countries,
            //serviceUrl: '../jsonValue.jsp?table=acs_user',
            onSelect: function (suggestion) {
            alert('You selected: ' + suggestion.value + ', ' + suggestion.data);
            }
        });


    });

</script>


	<label for="SearchXX">Search2</label>
	<input id="SearchXX" name="Search" placeholder = "filter terms..." type="text" size="20" maxlength="20" />

</p><p>	<label for="View">View As</label>
<div class="input"><select id="View" class="" name="View" 
>
<option name="all" value="0">Please Select</option>
<option name="Table" value="1" >Table</option>
<option name="Text" value="2" selected >Text</option>
</select>

</div></p><p>		<input type="submit" name="Search" value="Search" class="btn primary" id="submit_form" /> 
</p><p>		<input type="submit" name="Clear" value="Clear" class="btn primary" id="submit_form" /> 
</p></fieldset>

</FORM>
</div>
<div class="ContractView"><style media="screen" type="text/css">
.ContractView {
	margin:0px;padding:0px;
	width:100%;
	box-shadow: 10px 10px 5px #888888;
	border:1px solid #007f3f;
	
	-moz-border-radius-bottomleft:1px;
	-webkit-border-bottom-left-radius:1px;
	border-bottom-left-radius:1px;
	
	-moz-border-radius-bottomright:1px;
	-webkit-border-bottom-right-radius:1px;
	border-bottom-right-radius:1px;
	
	-moz-border-radius-topright:1px;
	-webkit-border-top-right-radius:1px;
	border-top-right-radius:1px;
	
	-moz-border-radius-topleft:1px;
	-webkit-border-top-left-radius:1px;
	border-top-left-radius:1px;
}.ContractView table{
    border-collapse: collapse;
        border-spacing: 0;
	width:100%;
	height:100%;
	margin:0px;padding:0px;
}.ContractView tr:last-child td:last-child {
	-moz-border-radius-bottomright:1px;
	-webkit-border-bottom-right-radius:1px;
	border-bottom-right-radius:1px;
}
.ContractView table tr:first-child td:first-child {
	-moz-border-radius-topleft:1px;
	-webkit-border-top-left-radius:1px;
	border-top-left-radius:1px;
}
.ContractView table tr:first-child td:last-child {
	-moz-border-radius-topright:1px;
	-webkit-border-top-right-radius:1px;
	border-top-right-radius:1px;
}.ContractView tr:last-child td:first-child{
	-moz-border-radius-bottomleft:1px;
	-webkit-border-bottom-left-radius:1px;
	border-bottom-left-radius:1px;
}.ContractView tr:hover td{
	
}
.ContractView tr:first-child td{
	font-family:Arial;
	font-weight:bold;
	color:#000000;
	font-size:14px;
}
.ContractView td{
	vertical-align:bottom;
	
	
	border:0px;
	border-width:0px 0px 0px 0px;
	text-align:left;
	padding:4px;
	font-size:10px;
	font-family:Verdana;
	font-weight:normal;
	color:#000000;
}.ContractView tr:last-child td{
	border-width:0px 0px 0px 0px;
}.ContractView tr td:last-child{
	border-width:0px 0px 0px 0px;
}.ContractView tr:last-child td:last-child{
	border-width:0px 0px 0px 0px;
}

	border:0px solid #007f3f;
	text-align:center;
	border-width:0px 0px 0px 0px;
	font-size:14px;
	font-family:Arial;
	font-weight:bold;
	color:#ffffff;
}
.ContractView tr:first-child td:first-child{
	border-width:0px 0px 0px 0px;
}
.ContractView tr:first-child td:last-child{
	border-width:0px 0px 0px 0px;
}
</style><table><table><tr><td><td>INTRODUCTION</td><td> Classification</td><td> Annotation</td></tr><tr><td width="100"></td><td>These Google Analytics Terms of Service (this "Agreement") are entered into by Google Inc. ("Google") and the entity executing this Agreement ("You").</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>This Agreement governs Your use of the standard Google Analytics (the "Service").</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>BY CLICKING THE "I ACCEPT" BUTTON, COMPLETING THE REGISTRATION PROCESS, OR USING THE SERVICE, YOU ACKNOWLEDGE THAT YOU HAVE REVIEWED AND ACCEPT THIS AGREEMENT AND ARE AUTHORIZED TO ACT ON BEHALF OF, AND BIND TO THIS AGREEMENT, THE OWNER OF THIS ACCOUNT.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>In consideration of the foregoing, the parties agree as follows:</td><td width="60"></td><td width="200"></td></tr></table><p> &nbsp; </p><table><tr><td><td>Definitions</td><td> Classification</td><td> Annotation</td></tr><tr><td width="100"></td><td>"Account" refers to the billing account for the Service.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>All Profiles linked to a single Property will have their Hits aggregated before determining the charge for the Service for that Property.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>"Confidential Information" includes any proprietary data and any other information disclosed by one party to the other in writing and marked "confidential" or disclosed orally and, within five business days, reduced to writing and marked "confidential".</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>However, Confidential Information will not include any information that is or becomes known to the general public, which is already in the receiving party's possession prior to disclosure by a party or which is independently developed by the receiving party without the use of Confidential Information.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>"Customer Data" means the data concerning the characteristics and activities of Visitors that is collected through use of the GATC and then forwarded to the Servers and analyzed by the Processing Software.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>"Documentation" means any accompanying documentation made available to You by Google for use with the Processing Software, including any documentation available online.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>"GATC" means the Google Analytics Tracking Code, which is installed on a Property for the purpose of collecting Customer Data, together with any fixes, updates and upgrades provided to You.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>"Hit" means the base unit that the Google Analytics system processes.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>A Hit may be a call to the Google Analytics system by various libraries, including, Javascript (ga.js, urchin.js), Silverlight, Flash, and Mobile.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>A Hit may currently be a page view, a transaction, item, or event.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>Hits may also be delivered to the Google Analytics system without using one of the various libraries by other Google Analytics-supported protocols and mechanisms the Service makes available to You.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>"Processing Software" means the Google Analytics server-side software and any upgrades, which analyzes the Customer Data and generates the Reports.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>"Profile" means the collection of settings that together determine the information to be included in, or excluded from, a particular Report.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>For example, a Profile could be established to view a small portion of a web site as a unique Report.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>There can be multiple Profiles established under a single Property.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>"Property" means a group of web pages or apps that are linked to an Account and use the same GATC.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>Each Property includes a default Profile that measures all pages within the Property.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>"Privacy Policy" means the privacy policy on a Property.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>"Report" means the resulting analysis shown at http://www.google.com/analytics for a Profile.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>"Servers" means the servers controlled by Google (or its wholly owned subsidiaries) on which the Processing Software and Customer Data are stored.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>"Software" means the GATC and the Processing Software.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>"Third Party" means any third party (i) to which You provide access to Your Account or (i) for which You use the Service to collect information on the third party's behalf.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>"Visitors" means visitors to Your Properties.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>The words "include" and "including" mean "including but not limited to."</td><td width="60"></td><td width="200"></td></tr></table><p> &nbsp; </p><table><tr><td><td>Fees and Service</td><td> Classification</td><td> Annotation</td></tr><tr><td width="100"></td><td>Subject to Section 15, the Service is provided without charge to You for up to 10 million Hits per month per account.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>Google may change its fees and payment policies for the Service from time to time including the addition of costs for geographic data, the importing of cost data from search engines, or other fees charged to Google or its wholly-owned subsidiaries by third party vendors for the inclusion of data in the Service reports.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>The changes to the fees or payment policies are effective upon Your acceptance of those changes which will be posted at http://www.google.com/analytics.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>Unless otherwise stated, all fees are quoted in U.S. Dollars.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>Any outstanding balance becomes immediately due and payable upon termination of this Agreement and any collection expenses (including attorneys' fees) incurred by Google will be included in the amount owed, and may be charged to the credit card or other billing mechanism associated with Your AdWords account.</td><td width="60"></td><td width="200"></td></tr></table><p> &nbsp; </p><table><tr><td><td>Member Account, Password, and Security</td><td> Classification</td><td> Annotation</td></tr><tr><td width="100"></td><td>To register for the Service, You must complete the registration process by providing Google with current, complete and accurate information as prompted by the registration form, including Your e-mail address (username) and password.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>You will protect Your passwords and take full responsibility for Your own, and third party, use of Your accounts.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>You are solely responsible for any and all activities that occur under Your Account.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>You will notify Google immediately upon learning of any unauthorized use of Your Account or any other breach of security.</td><td width="60"></td><td width="200"></td></tr><tr><td width="100"></td><td>Google's (or its wholly-owned subsidiaries') support staff may, from time to time, log in to the Service under Your customer password in order to maintain or improve service, including to provide You assistance with technical or billing issues.</td><td width="60"></td><td width="200"></td></tr></table><p> &nbsp; </p></table></div><br/><br/>


            </div>
        </div>


            </div>

            </div>
         </div>
    </body>
</html>



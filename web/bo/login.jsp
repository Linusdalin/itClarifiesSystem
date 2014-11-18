<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ include file="../bean.inc" %>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>Login Page | Pukka Tool Backoffice</title>
<link rel="stylesheet" type="text/css" href="adminCommon/styles/bootstrap/css/bootstrap.css"/>
<script type="text/javascript" src="adminCommon/scripts/js/plugins/jquery-1.7.min.js"></script>
<script type="text/javascript" src="adminCommon/scripts/js/plugins/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	$('.loginform button').hover(function(){
		$(this).stop().switchClass('default','hover');
	},function(){
		$(this).stop().switchClass('hover','default');
	});
	
	$('#LoginForm').submit(function(){
		var u = jQuery(this).find('#loginUsername');
		if(u.val() == '') {
			jQuery('.loginerror').slideDown();
			u.focus();
			return false;	
		}
	});
	
	$('#loginUsername').keypress(function(){
		jQuery('.loginerror').slideUp();
	});
});
</script>
<!--[if lt IE 9]>
	<script src="http://css3-mediaqueries-js.googlecode.com/svn/trunk/css3-mediaqueries.js"></script>
<![endif]-->
</head>

<body class="login">

<%
      //System.out.println("Msg: " + _msg);

%>

<div class="loginbox radius3">
	<div class="loginboxinner radius3">
    	<div class="loginheader">
    		<h1 class="bebas">Sign In to <% out.print(backOffice.getStyleConfig().caption); %></h1>
        	<div class="logo"></div>
    	</div><!--loginheader-->
        
        <div class="loginform">
        	<div class="loginerror"><p>Invalid username or password</p></div>
        	<FORM METHOD=POST action="?form=LoginForm&action=" id="LoginForm" onSubmit="if (notLoggedInTest()) { return true } else { return false }">
            	<p>
                	<label for="username" class="bebas">Username</label>
                    <input type="text" id="loginUsername" name="loginUsername" placeholder = "username..." maxlength="20" class="xlarge" />
                </p>
                <p>
                	<label for="password" class="bebas">Password</label>
                    <input id="loginPassword" name="loginPassword" value="" class="xlarge" type="password" maxlength="20" />
                </p>
                <p>
                	<button class="radius3 bebas">Sign in</button>
                </p>

                <%
                    // Add bypass login

                    if(backOffice.getProperties().use_pi_login){

                        out.print("<a href=\"?loginUsername=superadmin&loginPassword=admin&section=Home\" class=\"whitelink small\">pi</a>");
                    }

                %>


                <!--<p><a href="" class="whitelink small">Can't access your account?</a></p> -->
            </form>
        </div><!--loginform-->
    </div><!--loginboxinner-->
</div><!--loginbox-->

</body>
</html>
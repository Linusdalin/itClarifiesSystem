<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>mouseover image position</title>
<meta http-equiv="Content-Type" content="application/xhtml+xml; charset=utf-8" />

<style type="text/css">
/*<![CDATA[ */
body
   {
    background-color:#aaaaff;
   }
#one
   {
    position:absolute;
    left:50%;
    top:50%;
    margin:-150px 0 0 -250px;
   }
object
   {
    width:500px;
    height:400px;
    border:solid 1px #000000;
   }
 /*//]]>*/
</style>

<script type="text/javascript" src=test.js>
</script>

</head>
<body>

<div id="one">
<object id="foo" name="foo" type="text/html" data="http://www.w3schools.com/"></object>
</div>
<div>
<a href="http://mobil.aftonbladet.se" onclick="updateObjectIframe(this); return false;">this is an object test not an iframe test</a>
</div>

</body>
</html>
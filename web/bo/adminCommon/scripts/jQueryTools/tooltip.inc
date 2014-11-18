 <script src="http://cdn.jquerytools.org/1.2.5/tiny/jquery.tools.min.js"></script>
 <script type="text/javascript">
    $(function() {
        $("#tooltip img[title]").tooltip();
        $("img.tooltipimage").tooltip();
    });
</script>

 <style>
/* tooltip styling. by default the element to be styled is .tooltip  */
.tooltip {
	display:none;
	background:transparent url(/adminCommon/images/tooltip/white_arrow.png);
	font-size:12px;
	height:70px;
	width:160px;
	padding:25px;
	color:#000;
}

/* style the trigger elements */
#tooltip img {
	border:0;
	cursor:pointer;
	margin:0 8px;
}
</style>
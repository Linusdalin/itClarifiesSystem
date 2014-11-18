<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>Highcharts Example</title>
		
		
		<!-- 1. Add these JavaScript inclusions in the head of your page -->
		<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
		<script type="text/javascript" src="../js/highcharts.js"></script>
		
		<!-- 1a) Optional: the exporting module -->
		<script type="text/javascript" src="../js/modules/exporting.js"></script>
		
		
		<%
		
			List<String> categories = new ArrayList<String>();
            categories.add("Tue");
            categories.add("Wed");
            categories.add("Thu");
            categories.add("Fri");
            categories.add("Sat");
            categories.add("Sun");
            categories.add("Mon");

		
		%>
		
		
		<!-- 2. Add the JavaScript to initialize the chart on document ready -->
		<script type="text/javascript">
		
			var chart;
			$(document).ready(function() {
			
				// define the options
				var options = {
					chart: {
						renderTo: 'container',
						defaultSeriesType: 'spline'
					},
					title: {
						text: 'Rake'
					},
					subtitle: {
						text: 'Last week'
					},
					xAxis: {
						categories: <%
						    out.print("[");
						    boolean firstEntry = true;
                            for(String cat : categories){
                                if(!firstEntry)
                                    out.print(",");
		                        out.print("'" + cat + "'");
                                if(firstEntry)
                                    firstEntry = false;
                            }

                            
						    out.print("]");
						%>
					},
					yAxis: {
						title: {
							text: 'Rake ($)'
						}
					},
					legend: {
						enabled: false
					},
					tooltip: {
						formatter: function() {
				                return '<b>'+ this.series.name +'</b><br/>'+
								this.x +': '+ '$' + this.y;
						}
					},
					plotOptions: {
						spline: {
							cursor: 'pointer',
							point: {
								events: {
									click: function() {
										hs.htmlExpand(null, {
											pageOrigin: {
												x: this.pageX, 
												y: this.pageY
											},
											headingText: this.series.name,
											maincontentText: 'this.category: '+ this.category +
												'<br/>this.y: '+ this.y,
											width: 200
										});
									}
								}
							}
						}
					},
					series: []
				}
				
				// Load data asynchronously using jQuery. On success, add the data
				// to the options and initiate the chart.
				// http://api.jquery.com/jQuery.getJSON/
				jQuery.getJSON('tokyo.json', null, function(data) {
					options.series.push({
						name: 'Daily rake',
						data: data
					});
					
					chart = new Highcharts.Chart(options);
				});
				
			});
				
		</script>
		
		<!-- Additional files for the Highslide popup effect -->
		<script type="text/javascript" src="http://www.highcharts.com/highslide/highslide-full.min.js"></script>
		<script type="text/javascript" src="http://www.highcharts.com/highslide/highslide.config.js" charset="utf-8"></script>
		<link rel="stylesheet" type="text/css" href="http://www.highcharts.com/highslide/highslide.css" />
	</head>
	<body>
		<% out.print("HEY!!!");%>
		<!-- 3. Add the container -->
		<div id="container" style="width: 400px; height: 400px; margin: 0 auto"></div>        
		fasfs
				
	</body>
</html>

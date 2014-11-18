<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="sun.io.CharToByteASCII" %>
<%@ page import="pukkaBO.Charts.BackOfficeChart" %>
<%@ page import="pukkaBO.Charts.ChartInterface" %>

<%

    // Create a generic script to initiate a chart

    String from = "2012-01-01";
    String to   = "2012-01-03";

    ChartInterface chart = backoffice.getChart("testChart");

%>
<!-- 2. Add the JavaScript to initialize the chart on document ready -->

		<script type="text/javascript">

			var chart;
			$(document).ready(function() {

				// define the options
				var options = {
					chart: {
						renderTo: '<% out.print(chart.getName());%>',
						defaultSeriesType: '<% out.print(chart.getDefaultSeriesType());%>',
                        zoomType: '<% out.print(chart.getZoomType());%>',
                        backgroundColor: '<% out.print(chart.getBackgroundColor());%>',
                        borderRadius: '<% out.print(chart.getBorderRadius());%>',
                        borderColor: '<% out.print(chart.getBorderColor());%>',
                        borderWidth: '<% out.print(chart.getBorderWidth());%>'
					},
					title: {
						text: '<% out.print(chart.getTitle()); %>'
					},
					subtitle: {
						text: '<%out.print(from + " - " + to);%>'
					},
					xAxis: {
                        type: 'datetime',
                        maxZoom: 3600 * 1000 * 24 * 5,  //5 days are always shown, preventing to zoom in more than to day level
						dateTimeLabelFormats: {
                            day: '%e of %b',
                            week: '%e. %b',
                            month: '%b \'%y',
                            year: '%Y'
                        }
					},
					yAxis: {
						title: {
							text: 'Rake ($)'
						}
					},
					legend: {
						enabled: true
					},
					tooltip: {
						formatter: function() {
				                return '<b>'+ this.series.name +'</b><br/>'+
								 Highcharts.dateFormat('%A %B %e %Y', this.x) +': '+ '$' + this.y;
						}
                        //formatter: function() {
                        //  var el = $('.highcharts-tooltip', this.series.chart.container).find('tspan');
                        //  if (el.length == 0) //ie
                         //   el = $('.highcharts-tooltip', this.series.chart.container).find('span');

                        //  el.text('something');

                       // }
					},
					plotOptions: {
						column: {

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
											maincontentText: this.category +
												'<br/>$'+ this.y,
											width: 200
										});
									}
								}
							}
						}
					},
					series: []
				};

				// Load data asynchronously using jQuery. On success, add the data
				// to the options and initiate the chart.
				// http://api.jquery.com/jQuery.getJSON/
				jQuery.getJSON('diagram/ppc_rake_fee_hold_json.jsp<%out.print("?dateFrom=" + from + "&dateTo=" + to);%>', null, function(data) {
                    //options.series.push(eval(data));
                    $(data).each(
                        function(){
                            options.series.push(eval(this));
                        });


					//options.series.push({
					//	name: 'Daily rake',
					//	data: data
					//});

					chart = new Highcharts.Chart(options);
				});

			});

		</script>

		<!-- Additional files for the Highslide popup effect -->
		<script type="text/javascript" src="http://www.highcharts.com/highslide/highslide-full.min.js"></script>
		<script type="text/javascript" src="http://www.highcharts.com/highslide/highslide.config.js" charset="utf-8"></script>
		<link rel="stylesheet" type="text/css" href="http://www.highcharts.com/highslide/highslide.css" />
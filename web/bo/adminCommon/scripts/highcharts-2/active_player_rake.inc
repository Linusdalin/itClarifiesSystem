<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Calendar" %>
<%
            /**
            String from = "";
            String to = "";



            Calendar calTo = Calendar.getInstance();
            Calendar calFrom = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                calTo.add(Calendar.DATE, -1);
                to = dateFormat.format(calTo.getTime());

                calFrom.add(Calendar.DATE, -7);
                from = dateFormat.format(calFrom.getTime());

                calFrom.set(2010, 8, 25);
                from = "2010-09-25";

            } catch (Exception e) {
                e.printStackTrace();
            }  **/

		%>


<!-- 2. Add the JavaScript to initialize the chart on document ready -->
		<script type="text/javascript">

			var chart;
			$(document).ready(function() {
//Net deposit (in/ut bar + trendgraf) & win/loss (trendgraf).
				// define the options
				var options = {
					chart: {
						renderTo: 'active_player_rake',
                        zoomType: 'x',
                        backgroundColor: '#FAFAFA',
                        borderRadius: '10',
                        borderColor: '#A0A0A0',
                        borderWidth: '1'
					},
					title: {
						text: 'Rake & Active players'
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
					yAxis: [{
						title: {
							text: 'Rake ($)'          
						},
                        min: 0
					},{
						title: {
							text: 'Active players'
						},
                        opposite: true,
                        min: 0
					}
                    ],
					legend: {
						enabled: true
					},
					tooltip: {
						formatter: function() {
                            var dollar = '';
                            if (this.series.name == 'Rake'){
                                dollar = '$';
                            }
                            return '<b>'+ this.series.name +'</b><br/>'+
								 Highcharts.dateFormat('%A %B %e %Y', this.x) +': ' + dollar + this.y;
						}
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
				jQuery.getJSON('diagram/active_player_rake_json.jsp<%out.print("?dateFrom=" + from + "&dateTo=" + to);%>', null, function(data) {
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
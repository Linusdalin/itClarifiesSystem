<%@ page import="database.SiteUserTable" %>
<%@ page import="PlayerManagementSystem.SiteUser" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%
            String from = "";
            String to = "";


            Calendar calTo = Calendar.getInstance();
            Calendar calFrom = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {

                to = dateFormat.format(calTo.getTime());

                calFrom.add(Calendar.DATE, -6);
                from = dateFormat.format(calFrom.getTime());

            } catch (Exception e) {
                e.printStackTrace();
            }


			List<String> categories = new ArrayList<String>();
            while(calFrom.before(calTo)){
                categories.add("" + calFrom.get(Calendar.DATE));
                calFrom.add(Calendar.DATE, 1);
            }
            categories.add("" + calTo.get(Calendar.DATE));


		%>


<!-- 2. Add the JavaScript to initialize the chart on document ready -->
		<script type="text/javascript">

			var chart;
			$(document).ready(function() {

				// define the options
				var options = {
					chart: {
						renderTo: 'player_regions',
                        margin: [50, 0, 0, 0],
                        plotBackgroundColor: 'none',
                        plotBorderWidth: 0,
                        plotShadow: false,
                        backgroundColor: '#FAFAFA',
                        borderRadius: '10',
                        borderColor: '#A0A0A0',
                        borderWidth: '1'
					},
                    title: {
						text: 'Total players per region'
					},
					subtitle: {
						text: 'Outer circle: Now. Inner circle: One month ago.'
					},
					 plotArea: {
                         shadow: null,
                         borderWidth: null,
                         backgroundColor: null
                    },
                    plotOptions: {
                        pie: {
                            dataLabels: {
                                formatter: function() {
                                    return this.y +' %';
                                }
                            }
                        }
                    },
					tooltip: {
						formatter: function() {
				                return '<b>'+ this.series.name +'</b><br/>'+
								this.point.name + ': ' + this.y + '%';
						}
					},
						series: []
				};

				// Load data asynchronously using jQuery. On success, add the data
				// to the options and initiate the chart.
				// http://api.jquery.com/jQuery.getJSON/
				jQuery.getJSON('diagram/player_regions.jsp<%out.print("?dateFrom=" + from + "&dateTo=" + to + "&limit=" + 10);%>', null, function(data) {
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
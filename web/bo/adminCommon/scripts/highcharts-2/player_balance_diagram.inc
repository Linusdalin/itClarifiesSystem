<%@ page import="database.SiteUserTable" %>
<%@ page import="PlayerManagementSystem.SiteUser" %>
<%
            //String from = "";
            //String to = "";

    SiteUserTable siteUserTable = new SiteUserTable(backOffice);
    SiteUser user = siteUserTable.getUser(Integer.parseInt(row));


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

            if(user != null){
                calFrom = user.registrationDate.getCalendar();
                from = dateFormat.format(calFrom.getTime());
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
						renderTo: 'player_balance',
						        zoomType: 'x'
                        //plotBackgroundImage : 'http://www.tripwiremagazine.com/wp-content/uploads/images/stories/Articles/jqueryui/newtitle.jpg'
					},
					title: {
						text: 'Balance'
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
                        labels: {
                        formatter: function(){
                             return this.value
                            
                          }
                    },
					yAxis: {
						title: {
							text: '$ in account'
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
					},
						series: []
				};

				// Load data asynchronously using jQuery. On success, add the data
				// to the options and initiate the chart.
				// http://api.jquery.com/jQuery.getJSON/
				jQuery.getJSON('diagram/player_balance.jsp<%out.print("?dateFrom=" + from + "&dateTo=" + to + "&userId=" + row);%>', null, function(data) {
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
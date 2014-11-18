<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN">
<html>

<%@ page import="java.util.*" %>
<%@ page import="PlayerManagementSystem.*" %>
<%@ page import="tcopSystem.tenCommandments.TimeSpan" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="DBAccess.*" %>
<%@ page import="PlayerManagementSystem.Segmentation.*" %>
<%@ page import="Generic.PukkaGamingGeneral" %>


<%


%>

<%@ include file="../../includes/header.inc" %>

<%

    Cookie[] cookieList;

    //Check login

    String userName = null;
    UserManagementInterface usermanager = acsBean;
    

%>

<%@ include file="../../includes/verifyLogin.inc" %>


<head>
<title>

    <%@ include file="../../includes/title.inc" %>

</title>

<meta http-equiv="content-type" content="text/html; charset=UTF-8" />

     <%@ include file="/adminCommon/includes/commonJS.inc" %>


<script type="text/javascript">
    <%@ include file="../../includes/highcharts-2/js/highcharts.js" %>
</script>

    <!-- 1a) Optional: the exporting module -->
<script type="text/javascript">
    <%@ include file="../../includes/highcharts-2/js/modules/exporting.js" %>
</script>



    <script>
    $(function() {
        $("#dateFrom").datepicker({dateFormat: 'yy-mm-dd', firstDay: 1, changeYear:true});
        $("#dateTo").datepicker({dateFormat: 'yy-mm-dd', firstDay: 1, changeYear:true});
    });
    </script>

</head>
<body>

<div class="wrapper">

<%@ include file="../../../adminCommon/includes/menu.inc" %>


    <div class="page-body">
        <div id="container">
	        <div id="primarycontainer">
		        <div id="primarycontent">

			<!-- Primary content start -->


                    <h1>Stakes/Frequency Diagram</h1>

<%
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
						renderTo: 'frequency_stakes_scatter',
						defaultSeriesType: 'scatter',
                        plotBackgroundImage : '../img/frequency_scatter_background.jpg',
                        backgroundColor: '#FFFFFF',
                        borderRadius: '10',
                        borderColor: '#A0A0A0',
                        borderWidth: '1'
					},
					title: {
						text: 'Stakes/Frequency scatter'
					},
					subtitle: {
						text: '<%out.print(to);%>'
					},
				xAxis: {
					title: {
						enabled: true,
						text: 'Stakes'
					},
					startOnTick: true,
					endOnTick: true,
					showLastLabel: true,
                    gridLineWidth: 0,
                    min: 0
				},
				yAxis: {
                    gridLineWidth: 0,
					title: {
						text: 'Frequency'
					},
                    min: 0
				},
                legend: {
						enabled: true
					},
                tooltip: {
						formatter: function() {
				                return this.point.tip;
						}
					},
                plotOptions: {
					scatter: {
						marker: {
							radius: 5,
							states: {
								hover: {
									enabled: true,
									lineColor: 'rgb(100,100,100)'
								}
							}
						},
						states: {
							hover: {
								marker: {
									enabled: false
								}
							}
						},
                        cursor: 'pointer',
                            point: {
                                events: {
                                    click: function() {
                                        location.href = this.options.url;
                                    }
                                }
                            }
					}
				},
					series: []
				};

				// Load data asynchronously using jQuery. On success, add the data
				// to the options and initiate the chart.
				// http://api.jquery.com/jQuery.ÎgetJSON/
				jQuery.getJSON('diagram/ppc_frequency_stakes_scatter_json.jsp<%out.print("?dateFrom=" + from + "&dateTo=" + to);%>', null, function(data) {
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


        <div id="frequency_stakes_scatter" style="width: 500px; height: 500px; margin: 0 auto"></div>                                
                

                <!-- Primary content end -->

                    <div id="empty" style="color:#FFFFFF;">

                        <table height="800px"><tr><td></td></tr></table>

                    </div>

                    

                </div>
            </div>

        <!-- right nav -->


        <%@ include file="../../includes/menu_subsections.inc" %>

        <div class="clearit"></div>

         </div>
     </div>

    <%@ include file="../../includes/footer.inc" %>


    </body>
    </html>



<!-- 2. Add the JavaScript to initialize the chart on document ready -->
		<script type="text/javascript">

			var chart;
			$(document).ready(function() {

				// define the options
				var options = {
					chart: {
						renderTo: 'favourite_day_site',
                        backgroundColor: '#FAFAFA'
					},
					title: {
						text: 'Favourite day'
					},
					subtitle: {
						text: 'Relative frequency of active players per day for all users'
					},
					xAxis: {
                        categories: [
                                'Sun',
                                'Mon',
                                'Tue',
                                'Wed',
                                'Thu',
                                'Fri',
                                'Sat'
                            ]
					},
					yAxis: {
						title: {
							text: ''
						},
                        min: 0,
                        max: 100
					},
					legend: {
						enabled: true
					},
					tooltip: {
						formatter: function() {
				                return '<b>'+ this.series.name +'</b><br/>'+
								 this.y + '%';
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
				jQuery.getJSON('../pukkatool/diagram/favourite_day_site.jsp', null, function(data) {
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
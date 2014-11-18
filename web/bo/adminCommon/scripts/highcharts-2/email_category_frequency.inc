<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="tcopSystem.pukkapokah.PukkaGeneral" %>
<%@ page import="PlayerManagementSystem.Segmentation.CategoryOld" %>
<%@ page import="PlayerManagementSystem.Segmentation.Category" %>
<%@ page import="tcopSystem.tenCommandments.TimeSpan" %>
<%

try{

    /**
			List<String> categories = new ArrayList<String>();
            while(calFrom.before(calTo)){
                categories.add("" + calFrom.get(Calendar.DATE));
                calFrom.add(Calendar.DATE, 1);
            }
            categories.add("" + calTo.get(Calendar.DATE));
    **/
    List<CategoryOpenFrequencies> frequencies = mailBean.getOpenFrequenciesForCategories(mailing, acsBean.getDisplayBrand(), new DBTimeStamp(DBTimeStamp.NOW), show);
    CategoryOpenFrequencies allPlayers = mailBean.getOpenFrequenciesForCampaign(mailing, acsBean.getDisplayBrand(), new DBTimeStamp(DBTimeStamp.NOW), show);

    if(frequencies != null)
        Collections.sort(frequencies, Collections.reverseOrder());

    int count = 0;
    CategoryManager manager = new CategoryManager();
    StringBuffer categoryListing = new StringBuffer();

    for( CategoryOpenFrequencies frequency: frequencies){
        Category cat = manager.getCategory(frequency.categoryId);

        //out.println("<li>Category: "+ cat.name+" Frequency: "+ frequency.getOpenFrequency()+
          //      "% (" + frequency.countOpen + "/" + (frequency.countOpen + frequency.countNot) + ")</li>");
        if(count > 0)
            categoryListing.append(",");
        categoryListing.append("'");
        categoryListing.append(cat.name);
        categoryListing.append("'");
        count++;

    }

		%>


<!-- 2. Add the JavaScript to initialize the chart on document ready -->
		<script type="text/javascript">

			var chart;
			$(document).ready(function() {


				chart = new Highcharts.Chart({
					chart: {
						renderTo: 'email_category_frequency',
						defaultSeriesType: 'bar',
                        marginLeft: 175
					},
					title: {
						text: 'Campaign Response Frequency by Category',
                        y: 13
					},
					subtitle: {
						text: '<%
						    out.print("Campaign Activity: ");

                            switch(show){
                                case MAILING:
						            out.print(mailing.name + " (of campaign " + mailing.getCampaign().name +")");
                                    break;

                                case CLASS:
						            out.print("All activities of type " + mailing.getCampaign().getCampaignClass().name +" ");
                                    break;

                                default:
						            out.print(mailing.getCampaign().name + " (of type " + mailing.getCampaign().getCampaignClass().name +")");

                            }



						    out.print("<br><strong>Opened</strong> " + allPlayers.open.hit + "/" + allPlayers.getAllOpen() + " (" +
						    PukkaGeneral.getDisplayPercentage(allPlayers.open.hit, allPlayers.getAllOpen()) + ")");
						    out.print(", <strong>Action</strong> " + allPlayers.click.hit + "/" + allPlayers.getAllClick() + " (" +
						    PukkaGeneral.getDisplayPercentage(allPlayers.click.hit, allPlayers.getAllClick()) + ")");
						    out.print(", <strong>Target</strong> " + allPlayers.target.hit + "/" + allPlayers.getAllTarget() + " (" +
						    PukkaGeneral.getDisplayPercentage(allPlayers.target.hit, allPlayers.getAllTarget()) + ")");%>',

                        floating: true,
                        y: 27
					},
					xAxis: {
                        categories: [<%out.print(categoryListing.toString());%>],
                        title: {
                            text: null
                        }
					},
					yAxis: {
                        min: 0,
                        max: 100,
						title: {
							text: 'Percent opened'
						}
					},
                  legend: {
                     layout: 'vertical',
                     //align: 'right',
                     verticalAlign: 'top',
                     x: -275 ,
                     y: 0,
                     floating: true,
                     borderWidth: 1,
                     shadow: true
                  },
                    credits: {
                         enabled: false
                  },
					tooltip: {
						formatter: function() {
				                return '<b>' + this.x + ':</b> ' + this.y + '%';
						}
					},
					plotOptions: {
						bar: {
                            dataLabels: {
                               enabled: true,
                                formatter: function() {
	                                <%
	                                    out.println("var openFrequencies = Array();");
	                                    out.println("var actionFrequencies = Array();");
	                                    out.println("var targetFrequencies = Array();");
	                                    int i = 0;
	                                    for(CategoryOpenFrequencies freq : frequencies){
	                                        int totOpen = freq.getAllOpen();
	                                        out.println("openFrequencies[" + i + "]=\"" + freq.open.hit + " / " + totOpen + "\";");
	                                        int totAction = freq.getAllClick();
	                                        out.println("actionFrequencies[" + i + "]=\"" + freq.click.hit + " / " + totAction + "\";");
	                                        int totTarget = freq.getAllTarget();
	                                        out.println("targetFrequencies[" + i + "]=\"" + freq.target.hit + " / " + totTarget + "\";");
	                                        i++;
	                                    }


	                                %>
                                    if(this.series.name == "Open"){
                                        return openFrequencies[this.x];
                                    }else if(this.series.name == "Action"){
                                        return actionFrequencies[this.x];
                                    } if(this.series.name == "Target"){
                                        return targetFrequencies[this.x];
                                    }


                                }
                            }
                         }
					},
					series: [
                        <%
                        if(displayTarget){
                        %>
                        {name:'Target', data:[ <%
                        count = 0;
                        for(CategoryOpenFrequencies frequency: frequencies){
                            //Category cat = manager.createCategory(frequency.categoryId);

                            //out.println("<li>Category: "+ cat.name+" Frequency: "+ frequency.getOpenFrequency()+
                              //      "% (" + frequency.countOpen + "/" + (frequency.countOpen + frequency.countNot) + ")</li>");
                            if(count > 0)
                                out.print(",");
                            out.print(frequency.getTargetFrequency());
                            count++;

                        }


					%>   ]},
                    <%
                    }
                    if(displayAction){
                    %>

                        { name:'Action', data:[ <%
					    count = 0;
                        for(CategoryOpenFrequencies frequency: frequencies){
                            //Category cat = manager.createCategory(frequency.categoryId);

                            //out.println("<li>Category: "+ cat.name+" Frequency: "+ frequency.getOpenFrequency()+
                              //      "% (" + frequency.countOpen + "/" + (frequency.countOpen + frequency.countNot) + ")</li>");
                            if(count > 0)
                                out.print(",");
                            out.print(frequency.getClickFrequency());
                            count++;

                        }

                    %>   ]},
                     <%
                     }
                     if(displayOpen){
                     %>

                        { name:'Open', data:[ <%
                        count = 0;
                        for(CategoryOpenFrequencies frequency: frequencies){
                            //Category cat = manager.createCategory(frequency.categoryId);

                            //out.println("<li>Category: "+ cat.name+" Frequency: "+ frequency.getOpenFrequency()+
                              //      "% (" + frequency.countOpen + "/" + (frequency.countOpen + frequency.countNot) + ")</li>");
                            if(count > 0)
                                out.print(",");
                            out.print(frequency.getOpenFrequency());
                            count++;

                        }

                     %>


                    ]}
                    <%
                    }
                    %>
                    ]
                });


				// Load data asynchronously using jQuery. On success, add the data
				// to the options and initiate the chart.
				// http://api.jquery.com/jQuery.getJSON/
//				jQuery.getJSON('../pukkatool/diagram/email_category_frequency_json.jsp<%out.print("?mailing=" + mailingId + "&show=" + show.name());%>', null, function(data) {

  //                  $(data).each(
    //                    function(){
      //                      options.series.push(eval(this));
        //                });
		//			chart = new Highcharts.Chart(options);
	//			});
			});
		</script>

		<!-- Additional files for the Highslide popup effect -->
		<script type="text/javascript" src="http://www.highcharts.com/highslide/highslide-full.min.js"></script>
		<script type="text/javascript" src="http://www.highcharts.com/highslide/highslide.config.js" charset="utf-8"></script>
		<link rel="stylesheet" type="text/css" href="http://www.highcharts.com/highslide/highslide.css" />

<%
    }catch(Exception e){
        e.printStackTrace(System.out);
    }
%>
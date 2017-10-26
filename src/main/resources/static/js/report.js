// Load google charts
google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawChart);
google.charts.setOnLoadCallback(weekchart);
// Draw the chart and set the chart values
function weekchart(){
  var dataweek =google.visualization.arrayToDataTable([
    ['calls','no. of calls'],
    ['Successfull',1000],
    ['failed',200]
  ]);


  var options = {'title':'weekly report', 'width':500, 'height':400};
  // Display the chart inside the <div> element with id="piechart"

  var chart = new google.visualization.PieChart(document.getElementById('weeklyReport'));
  chart.draw(dataweek, options);


}
function drawChart() {
  var data = google.visualization.arrayToDataTable([
  ['calls', 'no. of calls'],
  ['Successfull', 200],
  ['failed', 20]
]);



  // Optional; add a title and set the width and height of the chart
  var options = {'title':'Daily report', 'width':500, 'height':400};

  // Display the chart inside the <div> element with id="piechart"
  var chart = new google.visualization.PieChart(document.getElementById('dailyReport'));
  chart.draw(data, options);


}

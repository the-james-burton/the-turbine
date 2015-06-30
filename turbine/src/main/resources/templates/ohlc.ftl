<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <link href="css/nv.d3.css" rel="stylesheet" type="text/css">
    <script src="js/d3.js" charset="utf-8"></script>
    <script src="js/nv.d3.js"></script>

    <style>
        text {
            font: 12px sans-serif;
        }
        svg {
            display: block;
        }
        html, body, #chart1, svg {
            margin: 0px;
            padding: 0px;
            height: 96%;
            width: 100%;
        }
    </style>
</head>
<body>

<p>${time}</p>

<div id="chart1">
    <svg></svg>
</div>

<script>

    var data = [{values: [
        // {"date": 15707, "open": 145.11, "high": 146.15, "low": 144.73, "close": 146.06, "volume": 192059000, "adjusted": 144.65},
        <#list ticks as tick>
          ${tick}<#if tick_has_next>,</#if>
		</#list>  
        ]}];

    var chart = nv.addGraph(function() {
        var chart = nv.models.ohlcBarChart()
            .x(function(d) { return d['date'] })
            .y(function(d) { return d['close'] })
            .duration(250)
            .margin({left: 75, bottom: 50});

        // chart sub-models (ie. xAxis, yAxis, etc) when accessed directly, return themselves, not the parent chart, so need to chain separately
        chart.xAxis
                .axisLabel("Dates")
                .tickFormat(function(d) {
                    // https://github.com/mbostock/d3/wiki/Time-Formatting
                    return d3.time.format('%X')(new Date(d));
                });

        chart.yAxis
                .axisLabel('Stock Price')
                .tickFormat(function(d,i){ return '$' + d3.format(',.1f')(d); });

        d3.select("#chart1 svg")
                .datum(data)
                .transition().duration(500)
                .call(chart);

        nv.utils.windowResize(chart.update);
        return chart;
    });


</script>
</body>
</html>
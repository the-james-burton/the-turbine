<!DOCTYPE html>
<html>
<head>
    <title>OHLC WebSocket</title>
    <script src="js/sockjs-0.3.4.js"></script>
    <script src="js/stomp.js"></script>
    <link href="css/nv.d3.1.8.1.css" rel="stylesheet" type="text/css">
    <script src="js/d3.3.5.5.js" charset="utf-8"></script>
    <script src="js/nv.d3.1.8.1.js"></script>

    <!--
    <script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.0.2/sockjs.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/nvd3/1.8.1-alpha/nv.d3.min.css" rel="stylesheet" type="text/css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/d3/3.5.6/d3.min.js" charset="utf-8"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/nvd3/1.8.1-alpha/nv.d3.min.js"></script>
	-->
	
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
            height: 93%;
            width: 100%;
        }
    </style>

    <script type="text/javascript">
    
    var data = [{values: [
        // {"date": 15707, "open": 145.11, "high": 146.15, "low": 144.73, "close": 146.06, "volume": 192059000, "adjusted": 144.65},
        <#list ticks as tick>
          ${tick}<#if tick_has_next>,</#if>
		</#list>  
        ]}];

        var stompClient = null;

        function setConnected(connected) {
            document.getElementById('connect').disabled = connected;
            document.getElementById('disconnect').disabled = !connected;
            document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
            document.getElementById('response').innerHTML = '';
        }

        function connect() {
            var socket = new SockJS('/ticks');
            stompClient = Stomp.over(socket);
            stompClient.connect({}, function(frame) {
                setConnected(true);
                console.log('Connected: ' + frame);
                stompClient.subscribe('/topic/ticks', function(tick){
                    onTick(tick.body);
                });
                stompClient.subscribe('/topic/ping', function(msg){
                    onPing(msg.body);
                });
            });
        }

        function disconnect() {
            if (stompClient != null) {
                stompClient.disconnect();
            }
            setConnected(false);
            console.log("Disconnected");
        }

        function pingServer() {
            stompClient.send("/app/ping", {});
        }

        function onPing(message) {
        	alert(message);
		}
		
        function onTick(message) {
            // var response = document.getElementById('response');
            // response.innerHTML = JSON.stringify(data[0]);
            data[0].values.push(JSON.parse(message));
            chart.update();
            // drawGraph();
        }
    	
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

    nv.addGraph(drawGraph);

	function drawGraph() {
	  d3.select('#chart1 svg')
	    .datum(data)
	    .transition().duration(500)
	    .call(chart);
	  nv.utils.windowResize(chart.update);
	  return chart;
	};
    
    </script>
</head>
<body onload="disconnect()">
<noscript><h2 style="color: #ff0000">Seems your browser doesn't support Javascript! Websocket relies on Javascript being enabled. Please enableJavascript and reload this page!</h2></noscript>

<p>${time}</p>

<div>
    <div>
        <button id="connect" onclick="connect();">Connect</button>
        <button id="disconnect" disabled="disabled" onclick="disconnect();">Disconnect</button>
    </div>
    <div id="conversationDiv">
        <button id="pingServer" onclick="pingServer();">Ping Server</button>
        <p id="response"></p>
    </div>
</div>

<div id="chart1">
    <svg></svg>
</div>

</body>
</html>
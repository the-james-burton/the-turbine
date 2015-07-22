<!DOCTYPE html>
<html>
<head>
    <title>Hello WebSocket</title>
    <script src="js/sockjs-1.0.0.js"></script>
    <script src="js/stomp.js"></script>
    <script type="text/javascript">
        var stompClient = null;

        function onLoad() {
            var socket = new SockJS('/reply');
            stompClient = Stomp.over(socket);
            stompClient.connect({}, function(frame) {
                console.log('Connected: ' + frame);
                stompClient.subscribe('/topic/reply', function(message){
                	receiveReply(message);
                });
   	            sendMessage();
            });
        }
        
        function sendMessage() {
            stompClient.send("/app/reply", {}, JSON.stringify({ 'message': 'websockets test' }));
        }
        
        function receiveReply(message) {
            var response = document.getElementById('reply');
            response.innerHTML = JSON.parse(message.body).message;
            stompClient.disconnect();
        }
        
    </script>
</head>

<body onload="onLoad()">
	
<noscript><h2 style="color: #ff0000">Seems your browser doesn't support Javascript! Websocket relies on Javascript being enabled. Please enable Javascript and reload this page!</h2></noscript>
    
Date: ${time?date}
<br>
Time: ${time?time}
<br>
Message: ${message}

<p id="reply">testing websockets</p>

</body>

</html>
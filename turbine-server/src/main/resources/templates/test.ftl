<!DOCTYPE html>
<html>
<head>
    <title>Hello WebSocket</title>
    <script src="js/sockjs-1.0.0.js"></script>
    <script src="js/stomp.js"></script>

    <!--
    <script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.0.2/sockjs.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
    -->

    <script type="text/javascript">
        var stompClient = null;

        function onLoad() {
            var socket = new SockJS('http://localhost:15674/stomp');
            stompClient = Stomp.over(socket);
            stompClient.connect({login : "guest", passcode : "guest"}, function(frame) {
                console.log('Connected: ' + frame);
                stompClient.subscribe('/topic/reply', function(message){
                	receiveReply(message);
                });
   	            sendMessage();
            });
        }
        
        function sendMessage() {
            stompClient.send("/amq/queue/request", {}, JSON.stringify({ 'message': 'websockets test' }));
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
var ws = new WebSocket("ws://localhost:8081/websocket");

ws.onopen = function() {
}

ws.onmessage = function(e) {
    document.getElementById("dump").innerHTML = e.data; 
}

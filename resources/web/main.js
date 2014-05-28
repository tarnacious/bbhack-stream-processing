var ws = new WebSocket("ws://localhost:8081/websocket");

ws.onopen = function() {
}


var leaderboard = function(json, key, selector) {
    var rows = json[key];
    var el = $(selector);
    el.empty();
    rows.forEach(function(e) {
       var name = $("<span>").addClass("name").html(e["item"]);
       var score = $("<span>").addClass("score").html("(" + e["count"] + ")");
       el.append($("<div>").append(name).append(score));
    })
};


ws.onmessage = function(e) {
    var json = JSON.parse(e.data);
    document.getElementById("date").innerHTML = json["last-date"]; 
    document.getElementById("unique").innerHTML = json["total-unique"]; 
    document.getElementById("tweets").innerHTML = json["total-tweets"]; 

    console.log();
    
    var zscores = json["zscores"];
    var scoresEl = $("#zscores");
    scoresEl.empty();
    zscores.forEach(function(e) {
       var name = $("<span>").addClass("name").html(e["item"]);
       var score = $("<span>").addClass("score").html("(" + e["score"] + ")");
       scoresEl.append($("<div>").append(name).append(score));
    })


    leaderboard(json, "total-top-k", "#topseen");
    leaderboard(json, "sample-top-k", "#topsample");

}

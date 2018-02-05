;

var heartflag = false;

function parseObj(strData) { 
	return (new Function("return " + strData))();
};

function GetPercent(num, total) { 
	num = parseFloat(num); 
	total = parseFloat(total); 
	if (isNaN(num) || isNaN(total)) { 
		return "-"; 
	} 
	return total <= 0 ? "0%" : (Math.round(num / total * 10000) / 100.00 + "%"); 
} 

function heart() {
    if (heartflag){
    	console.log('发送心跳')
    	ws.send("marketdata:");
    }
    setTimeout("heart()", 10*60*1000);

}

var url = common.websocket_marketdata
var ws = new WebSocket(url);

//用于自由地 send socket topic
this.send = function (message, callback) {
    this.waitForConnection(function () {
        ws.send(message);
        if (typeof callback !== 'undefined') {
          callback();
        }
    }, 1000);
};

this.waitForConnection = function (callback, interval) {
    if (ws.readyState === 1) {
        callback();
    } else {
        var that = this;
        // optional: implement backoff for interval here
        setTimeout(function () {
            that.waitForConnection(callback, interval);
        }, interval);
    }
};

ws.onopen = function()
{
	heartflag = true;
	console.log("open");
	
	this.send("marketdata:");
};

ws.onmessage = function(evt)
{
	var data = parseObj(evt.data).text
	data = parseObj(data)
	
//	console.log(data)
	
	var oldDataNow = $('.' + data.stockcode + '-now').text()
	
	if (data.now > oldDataNow) {
		$('.' + data.stockcode + '-now').html("<font color='red'>" + data.now + "</font>")
	} else if (data.now < oldDataNow) {
		$('.' + data.stockcode + '-now').html("<font color='green'>" + data.now + "</font>")
	}
};
ws.onclose = function(evt)
{
  console.log("WebSocketClosed!");
};
ws.onerror = function(evt)
{
  console.log("WebSocketError!");
};
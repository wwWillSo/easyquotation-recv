;

var heartflag = false;

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
	var data = common.parseObj(evt.data).text
	data = common.parseObj(data)
	$('.'+data.stockcode+'-order-tr').each(function() {
		if ($(this).find('.status').text() == '已成交') {
			var orderPrice = $(this).find('.orderPrice').text()
			var orderHand = $(this).find('.orderHand').text()
			var winLoss = common.floatMul(common.floatSub(data.now, orderPrice), orderHand)
			if (data.now > orderPrice) {
				$(this).find('.winLoss').html("<font color='red'>" + winLoss + "</font>")
			} else {
				$(this).find('.winLoss').html("<font color='green'>" + winLoss + "</font>")
			}
		}
	})
};
ws.onclose = function(evt)
{
  console.log("WebSocketClosed!");
};
ws.onerror = function(evt)
{
  console.log("WebSocketError!");
};
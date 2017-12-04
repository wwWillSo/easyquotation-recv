;
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

var url = 'ws://39.108.179.2:8080/optionalDeepSocketServer'
var ws = new WebSocket(url);
ws.onopen = function()
{  console.log("open");
	ws.send("marketdata:");
};
ws.onmessage = function(evt)
{
	var data = parseObj(evt.data).text
	data = parseObj(data)
	console.log(data)
	
	$('.' + data.stockcode + '-now').text(data.now)

};
ws.onclose = function(evt)
{
  console.log("WebSocketClosed!");
};
ws.onerror = function(evt)
{
  console.log("WebSocketError!");
};
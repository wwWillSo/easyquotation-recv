;

var heartflag = false;

function heart() {
    if (heartflag){
    	
    	console.log('发送心跳')
    	
    	var stockcode = $('.stock-code').text()

    	ws.send("marketdata:" + stockcode);
    }
    setTimeout("heart()", 10*60*1000);

}

var url = common.websocket_marketdata
var ws = new WebSocket(url);
ws.onopen = function()
{  
	heartflag = true;
	console.log("open");

	var stockcode = $('.stock-code').text()

   ws.send("marketdata:" + stockcode);
};
ws.onmessage = function(evt)
{
	var data = common.parseObj(evt.data).text
	data = common.parseObj(data)
	console.log(data)

	$('#sell5 td').eq(1).text(data.ask5)
	$('#sell5 td').eq(2).text(data.ask5_volume)
	
	$('#sell4 td').eq(1).text(data.ask4)
	$('#sell4 td').eq(2).text(data.ask4_volume)
	
	$('#sell3 td').eq(1).text(data.ask3)
	$('#sell3 td').eq(2).text(data.ask3_volume)
	
	$('#sell2 td').eq(1).text(data.ask2)
	$('#sell2 td').eq(2).text(data.ask2_volume)
	
	$('#sell1 td').eq(1).text(data.ask1)
	$('#sell1 td').eq(2).text(data.ask1_volume)
	
	$('#buy1 td').eq(1).text(data.bid1)
	$('#buy1 td').eq(2).text(data.bid1_volume)
	
	$('#buy2 td').eq(1).text(data.bid2)
	$('#buy2 td').eq(2).text(data.bid2_volume)
	
	$('#buy3 td').eq(1).text(data.bid3)
	$('#buy3 td').eq(2).text(data.bid3_volume)
	
	$('#buy4 td').eq(1).text(data.bid4)
	$('#buy4 td').eq(2).text(data.bid4_volume)
	
	$('#buy5 td').eq(1).text(data.bid5)
	$('#buy5 td').eq(2).text(data.bid5_volume)
	
	$('#now td').eq(1).text(data.now)
	
	var percent = 0
	
	if (data.open != 0) {
		percent = common.GetPercent(data.now - data.close, data.close)
	} else {
		percent = "停牌中";
	}
		
	$('#percent td').eq(1).text(percent)
	
	$('#turnover td').eq(1).text(data.turnover)
	$('#volume td').eq(1).text(data.volume)
	$('#high td').eq(1).text(data.high)
	$('#low td').eq(1).text(data.low)
	$('#open td').eq(1).text(data.open)
	$('#close td').eq(1).text(data.close)
};
ws.onclose = function(evt)
{
  console.log("WebSocketClosed!");
};
ws.onerror = function(evt)
{
  console.log("WebSocketError!");
};
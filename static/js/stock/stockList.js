;
$(document).ready(function(){

	var stockList = retrieveStockList()
	
	for (var i = 0 ; i < stockList.length ; i ++) {
		var stock = stockList[i]
		
		var trHtml="<tr>" +
				"<td>" + stock.stockcode + "</td>" +
				"<td>" + stock.name + "</td>" +
				"<td>" + stock.open + "</td>" +
				"<td>" + stock.close + "</td>" +
				"<td>" + stock.low + "</td>" +
				"<td>" + stock.high + "</td>" +
				"<td>" + stock.now + "</td>" +
				"</tr>"
		
		addTr("stock-list-table", i, trHtml)
	}
})

function retrieveStockList() {
	var data = null 
	$.ajax({
		url:'http://39.108.179.2:8080/getAllMarketdata',
		dataType:'json',
		async:false,
		success:function (result) {
			data = result
		},
		error:function(e) {
			console.log(e)
		}
	})
	
	return data 
}

function addTr(tab, row, trHtml){
    //获取table最后一行 $("#tab tr:last")
    //获取table第一行 $("#tab tr").eq(0)
    //获取table倒数第二行 $("#tab tr").eq(-2)
    var $tr=$("#"+tab+" tr").eq(row);
    if($tr.size()==0){
       alert("指定的table id或行数不存在！");
       return;
    }
    $tr.after(trHtml);
 }

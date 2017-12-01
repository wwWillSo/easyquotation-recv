;
$(document).ready(function(){
	
	var table = 'stock-list-table'
	
	addStockTr(table) 
	
	$('.search-btn').click(function (e) {
		e.preventDefault()
		emptyTable(table)
		addStockTr(table)
	})
	
	$('.first-page-btn').click(function (e) {
		e.preventDefault()
		
		setPageNo(1)
		
		emptyTable(table)
		addStockTr(table)
	})
	
	$('.last-page-btn').click(function (e) {
		e.preventDefault()
		
		var lastPageNo = $('.lastPageNo').val()
		
		setPageNo(lastPageNo)
		
		emptyTable(table)
		addStockTr(table)
	})
	
	$('.prev-page-btn').click(function (e) {
		e.preventDefault()
		
		if (getPageNo() == 1) {
			setPageNo(1)
		}
		else {
			setPageNo(getPageNo() - 1)
		}
		
		emptyTable(table)
		addStockTr(table)
	})
	
	$('.next-page-btn').click(function (e) {
		e.preventDefault()
		
		var lastPageNo = $('.lastPageNo').val()
		
		if (getPageNo() == parseInt(lastPageNo)) {
			setPageNo(lastPageNo)
		} else {
			setPageNo(getPageNo() + 1)
		}

		emptyTable(table)
		addStockTr(table)
	})
})

function setPageNo(i) {
	
	$('.pageNo').val(i)
}

function getPageNo() {
	var pageNo = $('.pageNo').val()
	
	return parseInt(pageNo)
}

function retrieveStockList(pageNo, pageSize) {
	var req = {
		"pageNo" : pageNo-1,
		"pageSize" : pageSize
	}
	var resp = null 
	$.ajax({
		url:'http://39.108.179.2:8080/getAllMarketdata',
		contentType:"application/json",
		type:'POST',
		data:JSON.stringify(req),
		dataType:'json',
		async:false,
		success:function (result) {
			resp = {
				"list" : result.list,
				"lastPageNo" : result.lastPageNo
			}
		},
		error:function(e) {
			console.log(e)
		}
	})
	
	return resp 
}

function addStockTr(tab) {
	pageNo = $('.pageNo').val() 
	pageSize = $('.pageSize').val() 
	
	var data = retrieveStockList(pageNo, pageSize)

	var stockList = data.list
	
	var lastPageNo = data.lastPageNo
	
	$('.lastPageNo').val(lastPageNo)
	
	for (var i = 0 ; i < stockList.length ; i ++) {
		var stock = stockList[i]
		
		var trHtml="<tr onclick='goto_chart("+ "\"" + stock.stockcode + "\"" +")'>" +
				"<td>" + stock.stockcode + "</td>" +
				"<td>" + stock.name + "</td>" +
				"<td>" + stock.open + "</td>" +
				"<td>" + stock.close + "</td>" +
				"<td>" + stock.low + "</td>" +
				"<td>" + stock.high + "</td>" +
				"<td>" + stock.now + "</td>" +
				"</tr>"
		
		addTr(tab, i, trHtml)
	}
}

function emptyTable(tab) {
	$('#' + tab + " tr").not(':eq(0)').empty()
}

function addTr(tab, row, trHtml){
    //获取table最后一行 $("#tab tr:last")
    //获取table第一行 $("#tab tr").eq(0)
    //获取table倒数第二行 $("#tab tr").eq(-2)
    var $tr=$("#"+tab+" tr").eq(row);
    if($tr.size()==0){
       return;
    }
    $tr.after(trHtml);
}

function goto_chart(stockcode) {
	window.location.href = 'DailyKLineChart.html?stockcode=' + stockcode
}

;
$(document).ready(function(){
	var table = 'order-list-table'
		
	console.log(table)
		
	if (!common.isLogin()) {
		layer.alert('未登录！')
		window.location.href = '/'
	} else {
		addStockTr(table)
	}
	
	$('.search-btn').click(function (e) {
		e.preventDefault()
		
		setPageNo(1)
		
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
		} else if (lastPageNo == 0) {
			setPageNo(1)
		} else {
			setPageNo(getPageNo() + 1)
		}

		emptyTable(table)
		addStockTr(table)
	})
})

function sellBtn(tr) {
	var order_tr = $(tr).parent().parent()
	var stockCode = $(order_tr).find('.stockCode').text()
	var orderHand = $(order_tr).find('.orderHand').text()
	var orderType = 0
	var orderSide = 1
	var offsetOrderNo = $(order_tr).find('.orderNo').text()
	
	var req = {
		"stockCode" : stockCode,
		"orderHand" : orderHand,
		"orderType" : orderType,
		"orderSide" : orderSide,
		"offsetOrderNo" : offsetOrderNo
	}
	
	console.log(req)
	
	var resp = null 
	$.ajax({
		url:common.http_trade_api + ";jsessionid=" + locache.get("sessionId"),
		contentType:"application/json",
		type:'POST',
		data:JSON.stringify(req),
		dataType:'json',
		async:false,
		success:function (result) {
			console.log(result)
			if (result._ReturnCode == '0000') {
				layer.msg('下单请求已提交！', {
					   time: 0
					   ,btn: ['查看订单', '查看股票']
					   ,btn1: function(index, layero){
						   window.location.href = 'orderList.html'
					   }
					   ,btn2: function(index, layero){
						  window.location.href = 'stockList.html'
					  }
					});
			} else {
				layer.alert('下单请求提交失败！')
			}
		},
		error:function(e) {
			console.log(e)
		}
	})
}

function cancelBtn(tr) {
	var order_tr = $(tr).parent().parent()
	var cancelOrderNo = $(order_tr).find('.orderNo').text()
	
	var req = {
		"cancelOrderNo" : cancelOrderNo
	}
	
	console.log(req)
	
	var resp = null 
	$.ajax({
		url:common.http_cancel_api + ";jsessionid=" + locache.get("sessionId"),
		contentType:"application/json",
		type:'POST',
		data:JSON.stringify(req),
		dataType:'json',
		async:false,
		success:function (result) {
			console.log(result)
			if (result._ReturnCode == '0000') {
				layer.msg('取消单请求已提交！', {
					   time: 0
					   ,btn: ['查看订单', '查看股票']
					   ,btn1: function(index, layero){
						   window.location.href = 'orderList.html'
					   }
					   ,btn2: function(index, layero){
						  window.location.href = 'stockList.html'
					  }
					});
			} else {
				layer.alert('取消单请求提交失败！')
			}
		},
		error:function(e) {
			console.log(e)
		}
	})
}

function setPageNo(i) {
	
	$('.pageNo').val(i)
}

function getPageNo() {
	var pageNo = $('.pageNo').val()
	
	return parseInt(pageNo)
}

function retrieveOrderList(pageNo, pageSize, keyword) {
	console.log('sessionId = ' + locache.get("sessionId"))
	var req = {
		"pageNo" : pageNo,
		"pageSize" : pageSize,
		"keyword" : keyword
	}
	var resp = null 
	$.ajax({
		url:common.http_orderList_all + ";jsessionid=" + locache.get("sessionId"),
		contentType:"application/json",
		type:'POST',
		data:JSON.stringify(req),
		dataType:'json',
		async:false,
		success:function (result, status, xhr) {
			resp = {
				"list" : result.data.list,
				"lastPageNo" : result.data.pages
			}
		},
		error:function(xhr, errorType, error) {
			console.log(error)
			if (xhr.status == '401' || xhr.status == '') {
//				window.location.href = '/'
			}
		}
	})
	return resp 
}

function addStockTr(tab) {
	
	pageNo = $('.pageNo').val() 
	pageSize = $('.pageSize').val() 
	keyword = $('.keyword').val()
	
	var data = retrieveOrderList(pageNo, pageSize, keyword)

	var orderList = data.list
	
	var lastPageNo = data.lastPageNo
	
	$('.lastPageNo').val(lastPageNo)
	
	for (var i = 0 ; i < orderList.length ; i ++) {
		var order = orderList[i]
		order = common.convertToOrder(order)
		var trHtml="<tr class='"+order.stockCode+"-order-tr'>" +
				"<td class='orderNo'>" + order.orderNo + "</td>" +
				"<td class='stockCode'>" + order.stockCode + "</td>" +
				"<td class='orderPrice'>" + order.orderPrice + "</td>" +
				"<td class='orderHand'>" + order.orderHand + "</td>" +
				"<td>" + order.orderType + "</td>" +
				"<td>" + order.orderSide + "</td>" +
				"<td>" + order.orderAmount + "</td>" +
				"<td>" + order.serviceAmount + "</td>" +
				"<td>" + order.createTime + "</td>" +
				"<td>" + order.updateTime + "</td>" +
				"<td class='status'>" + order.status + "</td>" +
				"<td class='winLoss'>" + order.winLoss + "</td>" +
				"<td><a class='sell' onclick='sellBtn(this)'>卖出</a>&nbsp;&nbsp;<a class='cancel' onclick='cancelBtn(this)'>取消</a></td>" + 
				"</tr>"
		
		addTr(tab, i, trHtml)
		
//		console.log(i)
//		console.log(trHtml)
	}
	
	$('#page-span').text(pageNo + '/' + lastPageNo)
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
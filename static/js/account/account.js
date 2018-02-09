;
$(document).ready(function(){
	var table = 'investment-summary'
		
	var table_account = "account-table"
		
	if (!common.isLogin()) {
		layer.alert('未登录！')
		window.location.href = '/'
	} else {
		addInvestmentSummaryTr(table)
		addAccountTr(table_account)
	}
	
	$('.search-btn').click(function (e) {
		e.preventDefault()
		
		setPageNo(1)
		
		emptyTable(table)
		addInvestmentSummaryTr(table)
	})
	
	$('.first-page-btn').click(function (e) {
		e.preventDefault()
		
		setPageNo(1)
		
		emptyTable(table)
		addInvestmentSummaryTr(table)
	})
	
	$('.last-page-btn').click(function (e) {
		e.preventDefault()
		
		var lastPageNo = $('.lastPageNo').val()
		
		setPageNo(lastPageNo)
		
		emptyTable(table)
		addInvestmentSummaryTr(table)
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
		addInvestmentSummaryTr(table)
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
		addInvestmentSummaryTr(table)
	})
});

function setPageNo(i) {
	
	$('.pageNo').val(i)
}

function getPageNo() {
	var pageNo = $('.pageNo').val()
	
	return parseInt(pageNo)
}

function retrieveInvestmentSummaryList(pageNo, pageSize, keyword) {
	console.log('sessionId = ' + locache.get("sessionId"))
	var req = {
		"pageNo" : pageNo,
		"pageSize" : pageSize,
		"keyword" : keyword
	}
	var resp = null 
	$.ajax({
		url:common.http_investmentSummary_api + ";jsessionid=" + locache.get("sessionId"),
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

function addInvestmentSummaryTr(tab) {
	
	pageNo = $('.pageNo').val() 
	pageSize = $('.pageSize').val() 
	keyword = $('.keyword').val()
	
	var data = retrieveInvestmentSummaryList(pageNo, pageSize, keyword)

	var investmentSummaryList = data.list
	
	var lastPageNo = data.lastPageNo
	
	$('.lastPageNo').val(lastPageNo)
	
	for (var i = 0 ; i < investmentSummaryList.length ; i ++) {
		var investmentSummary = investmentSummaryList[i]
		var trHtml="<tr class='"+investmentSummary.stockCode+"-investmentSummary-tr'>" +
				"<td class='stockCode'>" + investmentSummary.stockCode + "</td>" +
				"<td class='deposit'>" + investmentSummary.deposit + "</td>" +
				"<td class='hand'>" + investmentSummary.hand + "</td>" +
				"<td class='floatingWinloss'>" + investmentSummary.floatingWinloss + "</td>" +
				"</tr>"
		addTr(tab, i, trHtml)
	}
	
	$('#page-span').text(pageNo + '/' + lastPageNo)
}

function retrieveAccount() {
	console.log('sessionId = ' + locache.get("sessionId"))
	var resp = null 
	$.ajax({
		url:common.http_account_api + ";jsessionid=" + locache.get("sessionId"),
		contentType:"application/json",
		type:'POST',
		dataType:'json',
		async:false,
		success:function (result, status, xhr) {
			resp = {
				"account" : result.data
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

function addAccountTr(tab) {
	
	var data = retrieveAccount()

	var account = data.account
	
	var trHtml="<tr class='account-tr'>" +
			"<td class='depositAmount'>" + account.depositAmount + "</td>" +
			"<td class='usableAmount'>" + account.usableAmount + "</td>" +
			"<td class='totalAmount'>" + common.floatAdd(account.depositAmount, account.usableAmount) + "</td>" +
			"</tr>"
	addTr(tab, 0, trHtml)
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
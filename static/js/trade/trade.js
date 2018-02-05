;
$(document).ready(function(){
	//初始化值
	$('#stockcode').val(common.GetQueryString())
	$('#orderHand').val(1)
	
	$('input[name="orderType"]').click(function() {
		if ($(this).val() == 1) {
			$('#orderPrice').removeAttr('readonly')
		} else {
			$('#orderPrice').attr('readonly', 'readonly')
		}
	})
	
	$('#trade_confirm').click(function(e) {
		//阻止原来的事件执行结果
		e.preventDefault() ;
		
		var stockcode = $('#stockcode').val() ;
		var orderType = $('input[name="orderType"]:checked').val() ;
		var orderSide = $('input[name="orderSide"]:checked').val() ;
		var orderHand = $('#orderHand').val() ;
		var orderPrice = $('#orderPrice').val() ;
		
		if ('' == stockcode) {
			layer.alert('股票代码不能为空！') ;
			return ;
		}
		if (null == orderType) {
			layer.alert('订单类型不能为空！') ;
			return ;
		}
		if (null == orderSide) {
			layer.alert('订单方向不能为空！') ;
			return ;
		}
		if ('' == orderHand) {
			layer.alert('数量不能为空！') ;
			return ;
		}
		if (orderType == 1 && '' == orderPrice) {
			layer.alert('限价单需要输入价格！')
		}
		
		console.log(stockcode + "-" + orderType + "-" + orderSide + "-" + orderHand + "-" + orderPrice)
		
		var req = {
			"stockCode" : stockcode,
			"orderType" : orderType,
			"orderSide" : orderSide,
			"orderHand" : orderHand,
			"orderPrice" : orderPrice
		}
		
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
	})
})
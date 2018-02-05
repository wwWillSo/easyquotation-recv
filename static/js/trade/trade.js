;
$(document).ready(function(){
	$('#stockcode').val(common.GetQueryString())
	$('#orderHand').val(1)
	$('#trade_confirm').click(function(e) {
		//阻止原来的事件执行结果
		e.preventDefault() ;
		
		var stockcode = $('#stockcode').val() ;
		var orderType = $('input[name="orderType"]:checked').val() ;
		var orderSide = $('input[name="orderSide"]:checked').val() ;
		var orderHand = $('#orderHand').val() ;
		
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
		
		console.log(stockcode + "-" + orderType + "-" + orderSide + "-" + orderHand)
	})
})
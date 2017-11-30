;
$(document).ready(function(){

	$('.choose-stockcode-btn').click(function () {
		var stockcode = $('.stockcode-textfield').val() ;
		if (stockcode == '') {
			stockcode = '000001'
		}
		
		genKLineChart(stockcode, 1440)
	})
})


function genKLineChart(stockcode, chartType) {
	
	//取得股票名称
	$.ajax({
		url:'http://39.108.179.2:8080/getMarketdataByCode/' + stockcode ,
		async:false ,
		dataType:'json',
		success:function (result) {
			$('.stock-code').text(result.stockcode)
			$('.stock-name').text(result.name)
			$('.chart-type').text('日k')
		},
		error:function(e) {
			console.log(e)
		}
	})
	
	url = 'http://39.108.179.2:8080/retrieveKChart/'+stockcode+'/'+chartType
	console.log(url)
	genChart(url)
}

function strToJson(str){ 
	return JSON.parse(str); 
} 
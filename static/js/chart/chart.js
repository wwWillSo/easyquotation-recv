;
$(document).ready(function(){
		
	var stockcode = GetQueryString()
	
	$('.chart-type').text('日k')

	genKLineChart(stockcode, 1440)
	
	$('.kLine-type').click(function (e) {
		e.preventDefault()
		
		//重置选中样式
		$('.active').removeClass('active')
		$(this).parent().addClass('active')
		
		$('.chart-type').text($(this).text() + 'k')
		
		var chartType = $(this).data('flag')
		genKLineChart(stockcode, chartType)
	})
})

/**
 * 获取页面参数
 * @param name
 * @returns
 */
function GetQueryString(name)
{
    var url = window.location.href
    
    var pos = url.indexOf("?" + name + "=")
    
    var param = url.substr(pos-5)

    return param
}

/**
 * 补零方法
 * @param num
 * @param length
 * @returns
 */
function PrefixInteger(num, length) {
	return (Array(length).join('0') + num).slice(-length);
}

function genKLineChart(stockcode, chartType) {
	
	//取得股票名称
	$.ajax({
		url:'http://39.108.179.2:8080/getMarketdataByCode/' + stockcode ,
		async:false ,
		dataType:'json',
		success:function (result) {
			$('.stock-code').text(result.stockcode)
			$('.stock-name').text(result.name)
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
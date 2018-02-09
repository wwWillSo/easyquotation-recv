window.common = (function () {
	var common = {};
	
	/** 行情websocket地址*/
	common.websocket_marketdata = 'ws://39.108.179.2:8080/optionalDeepSocketServer' ;
	/** 获取所有实时行情*/
	common.http_marketdata_all = 'http://39.108.179.2:8080/getAllMarketdata' ;
	/** 根据代码获取实时行情*/
	common.http_marketdata_code = 'http://39.108.179.2:8080/getMarketdataByCode/' ;
	/** 根据代码和k线图类型获取k线数据*/
	common.http_kChart_code_chartType = 'http://39.108.179.2:8080/retrieveKChart/' ;
	/** 用户登录接口*/
	common.http_userLogin = 'http://localhost:8080/userLogin' ;
	/** 用户密码加密接口*/
	common.http_passwordTransformer = 'http://localhost:8080/api/public/passwordTransformer/' ;
	/** 查询订单列表接口*/
	common.http_orderList_all = 'http://localhost:8080/api/customer/queryOrder' ;
	/** 下单接口*/
	common.http_trade_api = 'http://localhost:8080/api/customer/createOrder' ;
	/** 取消单接口*/
	common.http_cancel_api = "http://localhost:8080/api/customer/cancelOrder" ;
	/** 查询持仓接口*/
	common.http_investmentSummary_api = "http://localhost:8080/api/customer/queryInvestmentSummary" ;
	/** 查询账户接口*/
	common.http_account_api = "http://localhost:8080/api/customer/queryAccount" ;
	
	/**
	 * 加 
	 */ 
	common.floatAdd = function floatAdd(arg1,arg2){    
	     var r1,r2,m;    
	     try{r1=arg1.toString().split(".")[1].length}catch(e){r1=0}    
	     try{r2=arg2.toString().split(".")[1].length}catch(e){r2=0}    
	     m=Math.pow(10,Math.max(r1,r2));    
	     return (arg1*m+arg2*m)/m;    
	}    

	/**
	 * 减
	 */
	common.floatSub = function floatSub(arg1,arg2){    
	    var r1,r2,m,n;    
	    try{r1=arg1.toString().split(".")[1].length}catch(e){r1=0}    
	    try{r2=arg2.toString().split(".")[1].length}catch(e){r2=0}    
	    m=Math.pow(10,Math.max(r1,r2));    
	    //动态控制精度长度    
	    n=(r1>=r2)?r1:r2;    
	    return ((arg1*m-arg2*m)/m).toFixed(n);    
	}    
	    
	/**
	 * 乘
	 */
	common.floatMul = function floatMul(arg1,arg2)   {     
	    var m=0,s1=arg1.toString(),s2=arg2.toString();     
	    try{m+=s1.split(".")[1].length}catch(e){}     
	    try{m+=s2.split(".")[1].length}catch(e){}     
	    return Number(s1.replace(".",""))*Number(s2.replace(".",""))/Math.pow(10,m);     
	}     
	      
   /**
    * 除 
    */  
	common.floatDiv = function floatDiv(arg1,arg2){     
	      var t1=0,t2=0,r1,r2;     
	      try{t1=arg1.toString().split(".")[1].length}catch(e){}     
	      try{t2=arg2.toString().split(".")[1].length}catch(e){}     
	        
	      r1=Number(arg1.toString().replace(".",""));  
	   
	      r2=Number(arg2.toString().replace(".",""));     
	      return (r1/r2)*Math.pow(10,t2-t1);     
	}  
	
	/**
	 * 计算百分比
	 */
	common.GetPercent = function GetPercent(num, total) { 
		num = parseFloat(num); 
		total = parseFloat(total); 
		if (isNaN(num) || isNaN(total)) { 
			return "-"; 
		} 
		return total <= 0 ? "0%" : (Math.round(num / total * 10000) / 100.00 + "%"); 
	} 
	
	/**
	 * 格式转换
	 */
	common.parseObj = function parseObj(strData) { 
		return (new Function("return " + strData))();
	};
	
	/**
	 * 订单转换类
	 */
	common.convertToOrder = function convertToOrder(order) {
		order.orderType = (order.orderType == 0 ? '市价单' : '限价单')
		order.orderSide = (order.orderSide == 0 ? '买入' : '卖出')
		order.createTime = common.timestampToTime(order.createTime)
		order.updateTime = common.timestampToTime(order.updateTime)
		order.winLoss = (order.winLoss == null ? 0 : order.winLoss)
		order.orderAmount = (order.orderAmount == null ? 0 : order.orderAmount)
		order.serviceAmount = (order.serviceAmount == null ? 0 : order.serviceAmount)
		switch (order.status) {
		case 0 : order.status = '订单待交易'; break ;
		case 1 : order.status = '订单待交易'; break ;
		case 2 : order.status = '已成交' ; break ;
		case 3 : order.status = '交易失败' ; break ;
		case 4 : order.status = '已平仓' ; break ;
		case 5 : order.status = '已取消' ; break ;
		}
		return order ;
	}
	
	/**
	 * 时间戳转换成日期
	 */
	common.timestampToTime = function timestampToTime(timestamp) {
        var date = null ;
        if (timestamp.lengh == 10) {
        	date = new Date(timestamp * 1000);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
        } else {
        	date = new Date(timestamp);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
        }
        Y = date.getFullYear() + '-';
        M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
        D = date.getDate() + ' ';
        
        h = ((''+date.getHours()).length == 1 ? '0'+(date.getHours()) : date.getHours() ) + ':';
        m = ((''+date.getMinutes()).length == 1 ? '0'+(date.getMinutes()) : date.getMinutes()) + ':';
        s = ((''+date.getSeconds()).length == 1 ? '0'+(date.getSeconds()) : date.getSeconds());
        
        return Y+M+D+h+m+s;
    }
	
	/**
	 * 日期转换成时间戳
	 */
	common.timeTotimestamp = function timestampToTime(time) {
		var date = new Date(time);
	    // 有三种方式获取
	    var time1 = date.getTime();		//精确到毫秒
//	    var time2 = date.valueOf();		//精确到毫秒
//	    var time3 = Date.parse(date);	//只精确到秒，后面以0代替
	    return time1 / 1000 ;   //UNIX时间戳需要在此结果除以1000
    }
	
	/**
	 * 获取页面参数（传入单个参数）
	 * @param name
	 * @returns
	 */
	common.GetQueryString = function GetQueryString(name)
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
	common.PrefixInteger = function PrefixInteger(num, length) {
		return (Array(length).join('0') + num).slice(-length);
	}
	
	common.strToJson = function strToJson(str){ 
		return JSON.parse(str); 
	} 
	
	//判断是否已登录
    common.isLogin = function isLogin() {
    	if (locache.get("sessionId") == null) {
    		return false ;
    	} else {
    		return true ;
    	}
    }
	
	common.isPc = function IsPC() {
	    var userAgentInfo = navigator.userAgent;
	    var Agents = ["Android", "iPhone",
	                "SymbianOS", "Windows Phone",
	                "iPad", "iPod"];
	    var flag = true;
	    for (var v = 0; v < Agents.length; v++) {
	        if (userAgentInfo.indexOf(Agents[v]) > 0) {
	            flag = false;
	            break;
	        }
	    }
	    return flag;
	}
	
	return common ;
})();
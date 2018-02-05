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
	common.http_userLogin = 'http://127.0.0.1:8080/userLogin' ;
	/** 用户密码加密接口*/
	common.http_passwordTransformer = 'http://127.0.0.1:8080/api/public/passwordTransformer/' ;
	
	
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
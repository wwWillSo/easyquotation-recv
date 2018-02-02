window.common = (function () {
	var common = {};
	
	/** 行情websocket地址*/
	common.websocket_marketdata = 'ws://39.108.179.2:8080/optionalDeepSocketServer' ;
	/** 获取所有实时行情*/
	common.http_marketdata_all = 'http://39.108.179.2:8080/getAllMarketdata' ;
	/** 根据代码获取实时行情*/
	common.http_marketdata_code = 'http://39.108.179.2:8080/getMarketdataByCode/' ;
	/**根据代码和k线图类型获取k线数据*/
	common.http_kChart_code_chartType = 'http://39.108.179.2:8080/retrieveKChart/' ;
	
	return common ;
})();
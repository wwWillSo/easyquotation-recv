package com.szw.easyquotation;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.szw.easyquotation.entity.RealTimeMarketdata;
import com.szw.easyquotation.util.HttpClientUtils;
import com.szw.easyquotation.util.ListUtil;


public class TestService {

	public static void main(String[] args) {
		String entity = HttpClientUtils.doGet("http://127.0.0.1:8081/getAllMarketData");
		// System.out.println(entity);
		JSONObject jsonObj = JSON.parseObject(entity);
		JSONArray result = jsonObj.getJSONArray("marketdata");
		List<RealTimeMarketdata> dataList = JSON.parseArray(result.toJSONString(), RealTimeMarketdata.class);
		List<List<RealTimeMarketdata>> list = ListUtil.averageAssign(dataList, 11);

		for (List<RealTimeMarketdata> l : list) {
			System.out.println(l.size());
		}
	}
}

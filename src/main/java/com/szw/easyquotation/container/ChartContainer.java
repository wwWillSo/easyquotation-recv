package com.szw.easyquotation.container;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.szw.easyquotation.bean.DailyKLineBean;
import com.szw.easyquotation.entity.MarketDataCandleChart;
import com.szw.easyquotation.entity.RealTimeMarketdata;
import com.szw.easyquotation.util.HttpClientUtils;
import com.szw.easyquotation.util.JdbcUtil;
import com.szw.easyquotation.util.SpringUtil;


public class ChartContainer {

	public final static String[] chartTypeArr = { "1", "3", "5", "10", "30", "60" };

	public static ConcurrentMap<String, Map<String, MarketDataCandleChart>> timeMap = new ConcurrentHashMap<String, Map<String, MarketDataCandleChart>>();

	public final static ConcurrentMap<String, RealTimeMarketdata> marketdataMap = new ConcurrentHashMap<String, RealTimeMarketdata>();

	public static List<RealTimeMarketdata> dataList = null;

	public static List<RealTimeMarketdata> getAllMarketdata(String marketdataUrl) {

		String entity = HttpClientUtils.doGet(marketdataUrl);
		JSONObject jsonObj = JSON.parseObject(entity);
		JSONArray result = jsonObj.getJSONArray("marketdata");
		ChartContainer.dataList = JSON.parseArray(result.toJSONString(), RealTimeMarketdata.class);

		return ChartContainer.dataList;
	}

	/**
	 * 获取日k数据
	 * 
	 * @return
	 * @author 苏镇威 2017年11月29日 上午11:02:14
	 */
	public static List<DailyKLineBean> getDailyKLine(String kLineUrl, String stockcode) {

		try {
			String entity = HttpClientUtils.doGet(kLineUrl + stockcode);
			JSONObject jsonObj = JSON.parseObject(entity);
			JSONArray result = jsonObj.getJSONArray(stockcode);
			List<DailyKLineBean> list = JSON.parseArray(result.toJSONString(), DailyKLineBean.class);

			if (list.isEmpty())
				return null;

			return list;
		} catch (Exception e) {
			// e.printStackTrace();
		}

		return null;

	}

	public static int persisChartList(List<MarketDataCandleChart> list) {
		SpringUtil.getBean("jdbcUtil", JdbcUtil.class).insertBatchFlush(list);

		return list.size();
	}

	public static boolean clearTimeMap() {
		timeMap = new ConcurrentHashMap<String, Map<String, MarketDataCandleChart>>();
		return true;
	}
}

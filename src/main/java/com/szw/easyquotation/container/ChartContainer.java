package com.szw.easyquotation.container;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.szw.easyquotation.entity.MarketDataCandleChart;
import com.szw.easyquotation.entity.RealTimeMarketdata;
import com.szw.easyquotation.repository.MarketdataCandleChartRepository;
import com.szw.easyquotation.util.DateUtil;
import com.szw.easyquotation.util.HttpClientUtils;


public class ChartContainer {

	public final static int[] chartTypeArr = { 1, 3, 5, 10, 30, 60, 1440 };

	public final static ConcurrentMap<String, Map<String, MarketDataCandleChart>> chartMap = new ConcurrentHashMap<String, Map<String, MarketDataCandleChart>>();

	public static void genDataMap(List<MarketDataCandleChart> list) {

		for (MarketDataCandleChart chart : list) {
			// 第一种情况：此code未曾初始化过
			if (null == chartMap.get(chart.getStockcode())) {
				Map<String, MarketDataCandleChart> map = new HashMap<String, MarketDataCandleChart>();
				map.put(chart.getChartType() + "", chart);
				chartMap.put(chart.getStockcode(), map);
			} else {
				// 第二种情况：此code已经初始化过，下面操作更新此code下的data
				Map<String, MarketDataCandleChart> map = chartMap.get(chart.getStockcode());
				map.put(chart.getChartType() + "", chart);
				chartMap.put(chart.getStockcode(), map);
			}
		}

		System.out.println("chartMap初始化完成，data数量为：" + chartMap.size());
	}

	public static boolean initDataMap(MarketdataCandleChartRepository marketDataCandleChartRepository, List<RealTimeMarketdata> dataList) {
		Date now = DateUtil.resetZeroSeconds(new Date());
		List<MarketDataCandleChart> list = new ArrayList<MarketDataCandleChart>();
		for (RealTimeMarketdata marketdata : dataList) {
			// long start = new Date().getTime();
			for (int min : ChartContainer.chartTypeArr) {
				MarketDataCandleChart chart = null;
				// 当chartMap为空时，很大可能是因为程序重启了，为了避免一天生成两条日k的情况，需检查数据库
				if (ChartContainer.chartMap.size() == 0 || null == ChartContainer.chartMap.get(marketdata.getStockcode())) {
					chart = marketDataCandleChartRepository.findTopByStockcodeAndChartTypeOrderByCreateTimeDesc(marketdata.getStockcode(), min);
				} else {
					chart = ChartContainer.chartMap.get(marketdata.getStockcode()).get(min + "");
				}

				if (null == chart) {
					return false;
				}

				if (null == chart || DateUtil.countMinutes(now, chart.getCreateTime()) >= min) {
					MarketDataCandleChart newChart = new MarketDataCandleChart();
					BeanUtils.copyProperties(marketdata, newChart);
					list.add(newChart);
				}
			}
			// long end = new Date().getTime();
			// System.out.println((end - start));
		}
		ChartContainer.genDataMap(list);

		return true;
	}

	public static List<RealTimeMarketdata> getAllMarketdata(String marketdataUrl) {
		String entity = HttpClientUtils.doGet(marketdataUrl);
		// System.out.println(entity);
		JSONObject jsonObj = JSON.parseObject(entity);
		JSONArray result = jsonObj.getJSONArray("marketdata");
		List<RealTimeMarketdata> list = JSON.parseArray(result.toJSONString(), RealTimeMarketdata.class);
		return list;
	}
}

package com.szw.easyquotation.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;

import com.szw.easyquotation.entity.MarketDataCandleChart;
import com.szw.easyquotation.util.SpringUtil;


public class NewChartContainer {
	public final static int[] chartTypeArr = { 1, 3, 5, 10, 30, 60, 1440 };

	public final static Map<String, Map<String, List<MarketDataCandleChart>>> codeMap = new HashMap<String, Map<String, List<MarketDataCandleChart>>>();

	public static Map<String, Map<String, List<MarketDataCandleChart>>> initCodeMap() {
		System.out.println("正在创建分时图...");

		for (String code : retrieveStockCode()) {
			Map<String, List<MarketDataCandleChart>> timeMap = new HashMap<String, List<MarketDataCandleChart>>();
			for (int min : chartTypeArr) {
				timeMap.put(min + "", new ArrayList<MarketDataCandleChart>());
			}
			codeMap.put(code, timeMap);
			System.out.println("分时图创建成功...");
		}

		return codeMap;
	}

	public static String[] retrieveStockCode() {

		System.out.println("从redis中获取股票代码集...");

		try {
			String all_codes = SpringUtil.getBean("stringRedisTemplate", StringRedisTemplate.class).opsForValue().get("stockCodes");
			String[] code_arr = all_codes.split(",");
			return code_arr;
		} catch (Exception e) {
			System.out.println("redis中不存在股票代码集...");
		}

		return null;
	}

	public static Map<String, List<MarketDataCandleChart>> getCandleChartByCode(String code) {
		try {

			if (codeMap.size() == 0) {
				initCodeMap();
			}

			Map<String, List<MarketDataCandleChart>> timeMap = codeMap.get(code);

			return timeMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void saveTimeMap(String code, Map<String, List<MarketDataCandleChart>> timeMap) {
		codeMap.put(code, timeMap);
	}
}

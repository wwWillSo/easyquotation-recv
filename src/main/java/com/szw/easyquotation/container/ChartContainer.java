package com.szw.easyquotation.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.szw.easyquotation.entity.MarketDataCandleChart;


public class ChartContainer {

	public final static int[] chartTypeArr = { 1, 3, 5, 10, 30, 60, 1440 };

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	public Map<String, Map<String, List<MarketDataCandleChart>>> initCodeMap() {
		System.out.println("正在创建分时图redis...");
		Map<String, Map<String, List<MarketDataCandleChart>>> codeMap = new HashMap<String, Map<String, List<MarketDataCandleChart>>>();

		for (String code : retrieveStockCode()) {
			Map<String, List<MarketDataCandleChart>> timeMap = new HashMap<String, List<MarketDataCandleChart>>();
			for (int min : chartTypeArr) {
				timeMap.put(min + "", new ArrayList<MarketDataCandleChart>());
			}
			codeMap.put(code, timeMap);
		}
		redisTemplate.opsForValue().set("chart", codeMap);
		System.out.println("分时图redis创建成功...");
		return codeMap;
	}

	public String[] retrieveStockCode() {
		String all_codes = stringRedisTemplate.opsForValue().get("stockCodes");
		String[] code_arr = all_codes.split(",");
		return code_arr;
	}

	public Map<String, List<MarketDataCandleChart>> getCandleChartByCode(String code) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Map<String, List<MarketDataCandleChart>>> codeMap = (HashMap<String, Map<String, List<MarketDataCandleChart>>>) redisTemplate
					.opsForValue().get("chart");

			if (null == codeMap) {
				codeMap = initCodeMap();
			}

			Map<String, List<MarketDataCandleChart>> timeMap = codeMap.get(code);

			return timeMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void saveTimeMap(String code, Map<String, List<MarketDataCandleChart>> timeMap) {
		Map<String, Map<String, List<MarketDataCandleChart>>> codeMap = (HashMap<String, Map<String, List<MarketDataCandleChart>>>) redisTemplate.opsForValue()
				.get("chart");
		codeMap.put(code, timeMap);
		redisTemplate.opsForValue().set("chart", codeMap);
	}

}

package com.szw.easyquotation.container;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.szw.easyquotation.entity.MarketDataCandleChart;
import com.szw.easyquotation.entity.RealTimeMarketdata;


public class ChartContainer {

	public final static ConcurrentMap<String, Map<String, MarketDataCandleChart>> chart = new ConcurrentHashMap<String, Map<String, MarketDataCandleChart>>();

	public final static ConcurrentMap<String, RealTimeMarketdata> market = new ConcurrentHashMap<String, RealTimeMarketdata>();

	public final static ConcurrentMap<String, RealTimeMarketdata> market_temp = new ConcurrentHashMap<String, RealTimeMarketdata>();

	public final static int[] chatTypeArr = { 1, 3, 5, 10, 30, 60, 1440 };

}

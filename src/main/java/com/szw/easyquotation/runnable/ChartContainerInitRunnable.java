package com.szw.easyquotation.runnable;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import com.szw.easyquotation.container.ChartContainer;
import com.szw.easyquotation.entity.RealTimeMarketdata;
import com.szw.easyquotation.repository.MarketdataCandleChartRepository;
import com.szw.easyquotation.util.DateUtil;


public class ChartContainerInitRunnable implements Callable<ChartContainerInitRunnable> {

	private MarketdataCandleChartRepository marketDataCandleChartRepository = null;

	private List<RealTimeMarketdata> dataList = null;

	public ChartContainerInitRunnable(MarketdataCandleChartRepository marketDataCandleChartRepository, List<RealTimeMarketdata> dataList) {
		this.marketDataCandleChartRepository = marketDataCandleChartRepository;
		this.dataList = dataList;
	}

	@Override
	public ChartContainerInitRunnable call() {
		System.out.println("chartContainer初始化..." + DateUtil.format_yyyyMMddHHmmss(new Date()));
		// 初始化ChartContainer(改为容器启动时自动初始化)
		ChartContainer.initDataMap(marketDataCandleChartRepository, dataList);
		System.out.println("chartContainer初始化结束..." + DateUtil.format_yyyyMMddHHmmss(new Date()));

		return null;
	}

}

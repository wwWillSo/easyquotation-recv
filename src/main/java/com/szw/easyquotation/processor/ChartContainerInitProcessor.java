package com.szw.easyquotation.processor;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.szw.easyquotation.container.ChartContainer;
import com.szw.easyquotation.entity.RealTimeMarketdata;
import com.szw.easyquotation.repository.MarketdataCandleChartRepository;
import com.szw.easyquotation.runnable.ChartContainerInitRunnable;
import com.szw.easyquotation.util.ListUtil;


@Service
public class ChartContainerInitProcessor {

	@Autowired
	private MarketdataCandleChartRepository marketDataCandleChartRepository;

	private int poolSize = 8;
	private ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
	// private ExecutorService threadPool = Executors.newSingleThreadExecutor();

	@Value("${marketdata.webservice.host}")
	private String marketdataUrl;

	public boolean execute() {

		try {
			System.out.println("chartContainer-init任务开始...");

			List<RealTimeMarketdata> dataList = ChartContainer.getAllMarketdata(marketdataUrl);

			// 切分行情列表
			List<List<RealTimeMarketdata>> list = ListUtil.averageAssign(dataList, 20);

			for (List<RealTimeMarketdata> l : list) {

				threadPool.submit(new ChartContainerInitRunnable(marketDataCandleChartRepository, l));
			}
			threadPool.shutdown();
			threadPool.awaitTermination(1, TimeUnit.HOURS);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			threadPool.shutdown();
		}
		return threadPool.isTerminated();
	}

}

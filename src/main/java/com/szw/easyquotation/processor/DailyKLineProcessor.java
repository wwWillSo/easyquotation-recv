package com.szw.easyquotation.processor;

import java.util.Date;
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
import com.szw.easyquotation.runnable.DailyKLineRunnable;
import com.szw.easyquotation.util.DateUtil;
import com.szw.easyquotation.util.ListUtil;


@Service
public class DailyKLineProcessor {

	@Autowired
	private MarketdataCandleChartRepository marketDataCandleChartRepository;

	private int poolSize = 8;
	private ExecutorService threadPool = null;
	// private ExecutorService threadPool = Executors.newSingleThreadExecutor();

	@Value("${marketdata.webservice.host}")
	private String marketdataUrl;

	@Value("${marketdata.kLine.host}")
	private String kLineUrl;

	public boolean execute() {

		try {
			System.out.println("dailyKLine任务开始..." + DateUtil.format_yyyyMMddHHmmss(new Date()));

			threadPool = Executors.newFixedThreadPool(poolSize);

			List<RealTimeMarketdata> dataList = ChartContainer.getAllMarketdata(marketdataUrl);

			// 切分行情列表
			List<List<RealTimeMarketdata>> list = ListUtil.averageAssign(dataList, poolSize);

			for (List<RealTimeMarketdata> l : list) {

				threadPool.submit(new DailyKLineRunnable(marketDataCandleChartRepository, l, kLineUrl));
			}
			threadPool.shutdown();
			threadPool.awaitTermination(1, TimeUnit.DAYS);
			System.out.println("dailyKLine任务结束..." + DateUtil.format_yyyyMMddHHmmss(new Date()));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			threadPool.shutdown();
		}
		return threadPool.isTerminated();
	}

}

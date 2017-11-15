package com.szw.easyquotation.processor;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.szw.easyquotation.container.ChartContainer;
import com.szw.easyquotation.entity.RealTimeMarketdata;
import com.szw.easyquotation.repository.MarketdataCandleChartRepository;
import com.szw.easyquotation.runnable.EasyQuotationChartRunnable;
import com.szw.easyquotation.util.DateUtil;
import com.szw.easyquotation.util.ListUtil;


@Service
public class EasyQuotationChartProcessor {

	@Autowired
	private MarketdataCandleChartRepository marketDataCandleChartRepository;

	private int poolSize = 8;
	private ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
	// private ExecutorService threadPool = Executors.newSingleThreadExecutor();

	@Value("${marketdata.webservice.host}")
	private String marketdataUrl;

	public void execute() {

		try {

			Date now = DateUtil.resetZeroSeconds(new Date());

			System.out.println("分时图生成任务开始..." + DateUtil.format_yyyyMMddHHmmss(now) + "...线程池对象：" + threadPool.toString());

			List<RealTimeMarketdata> dataList = ChartContainer.getAllMarketdata(marketdataUrl);

			// 切分行情列表
			List<List<RealTimeMarketdata>> list = ListUtil.averageAssign(dataList, 8);

			// 初始化ChartContainer(改为容器启动时自动初始化)
			// ChartContainer.initDataMap(marketDataCandleChartRepository, dataList);

			for (List<RealTimeMarketdata> l : list) {

				threadPool.submit(new EasyQuotationChartRunnable(l, marketDataCandleChartRepository, now));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// threadPool.shutdown();
		}
	}

}
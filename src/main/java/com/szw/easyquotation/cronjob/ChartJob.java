package com.szw.easyquotation.cronjob;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.szw.easyquotation.container.ChartContainer;
import com.szw.easyquotation.entity.RealTimeMarketdata;
import com.szw.easyquotation.processor.NewEasyQuotationChartProcessor;
import com.szw.easyquotation.repository.MarketdataCandleChartRepository;
import com.szw.easyquotation.util.DateUtil;


/**
 * 定时任务生成k线 <P>TODO</P>
 * 
 * @author 苏镇威 2017年11月6日 下午4:47:36
 */
@Component
public class ChartJob {
	public final static long ONE_Minute = 60 * 1000;

	@Value("${marketdata.webservice.host}")
	private String marketdataUrl;

	@Autowired
	private NewEasyQuotationChartProcessor newEasyQuotationChartProcessor;

	@Autowired
	private MarketdataCandleChartRepository marketDataCandleChartRepository;

	private boolean flag = false;

	@Scheduled(fixedRate = ONE_Minute)
	public void fixedDelayJob() {

		if (!flag) {
			System.out.println("chartContainer初始化..." + DateUtil.format_yyyyMMddHHmmss(new Date()));
			List<RealTimeMarketdata> dataList = ChartContainer.getAllMarketdata(marketdataUrl);
			// 初始化ChartContainer(改为容器启动时自动初始化)
			ChartContainer.initDataMap(marketDataCandleChartRepository, dataList);
			System.out.println("chartContainer初始化结束..." + DateUtil.format_yyyyMMddHHmmss(new Date()));

			flag = true;
		}

		System.out.println("定时任务fixedDelayJob启动...");
		newEasyQuotationChartProcessor.execute();
	}

}
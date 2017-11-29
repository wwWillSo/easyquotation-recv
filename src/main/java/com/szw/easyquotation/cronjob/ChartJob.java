package com.szw.easyquotation.cronjob;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.szw.easyquotation.container.ChartContainer;
import com.szw.easyquotation.processor.ChartContainerInitProcessor;
import com.szw.easyquotation.processor.DailyKLineProcessor;
import com.szw.easyquotation.processor.EasyQuotationChartProcessor;
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
	private EasyQuotationChartProcessor newEasyQuotationChartProcessor;

	@Autowired
	private ChartContainerInitProcessor chartContainerInitProcessor;

	@Autowired
	private DailyKLineProcessor dailyKLineProcessor;

	@Autowired
	private Environment env ;
	
	@Scheduled(cron = "0 0/1 9-11 ? * MON-FRI ")
	public void openMarketMorning() {
		
		if (!env.getProperty("chart.job.switch").equals("Y")) {
			return ;
		}

		if (DateUtil.isBefore(new Date(), DateUtil.getTime(9, 30, 0))) {
			return;
		}

		if (DateUtil.isAfter(new Date(), DateUtil.getTime(11, 30, 0))) {
			return;
		}

		if (ChartContainer.hasbeenInit) {
			System.out.println("早上定时任务启动...");
			newEasyQuotationChartProcessor.execute();
		} else {
			System.out.println("chartContainer初始化任务未完成...");
			chartContainerInitProcessor.execute();
		}

	}

	// @Scheduled(cron = "0 30 11 ? * MON-FRI ")
	// public void closeMarketMorning() {
	// System.out.println("早上定时任务关闭...");
	// if (newEasyQuotationChartProcessor.shutdown()) {
	// System.out.println("关闭成功...");
	// }
	// }

	@Scheduled(cron = "0 0/1 13-15 ? * MON-FRI ")
	public void openMarketAfternoon() {
		
		if (!env.getProperty("chart.job.switch").equals("Y")) {
			return ;
		}

		if (DateUtil.isAfter(new Date(), DateUtil.getTime(15, 0, 0))) {
			return;
		}

		if (ChartContainer.hasbeenInit) {
			System.out.println("下午定时任务启动...");
			newEasyQuotationChartProcessor.execute();
		} else {
			System.out.println("chartContainer初始化任务未完成...");
			chartContainerInitProcessor.execute();
		}

	}

	@Scheduled(cron = "0 30 16 ? * MON-FRI ")
	public void genDailyKLine() {
		
		if (!env.getProperty("kLine.job.switch").equals("Y")) {
			return ;
		}
		
		System.out.println("日K生成任务启动...");
		dailyKLineProcessor.execute();
	}

	// @Scheduled(cron = "0 0 15 ? * MON-FRI ")
	// public void closeMarketAfternoon() {
	// System.out.println("下午定时任务关闭...");
	// if (newEasyQuotationChartProcessor.shutdown()) {
	// System.out.println("关闭成功...");
	// }
	// }

}
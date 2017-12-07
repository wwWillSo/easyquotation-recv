package com.szw.easyquotation.cronjob;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.szw.easyquotation.container.ChartContainer;
import com.szw.easyquotation.entity.RealTimeMarketdata;
import com.szw.easyquotation.processor.ChartContainerInitProcessor;
import com.szw.easyquotation.processor.DailyKLineProcessor;
import com.szw.easyquotation.processor.EasyQuotationChartProcessor;
import com.szw.easyquotation.util.DateUtil;
import com.szw.easyquotation.util.HttpClientUtils;
import com.szw.easyquotation.util.RedisCacheUtil;


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

	@Value("${createNewTableJob.host}")
	private String createNewTableJobUrl;

	@Value("${createNewTableJob.interval}")
	private String createNewTableJobInterval;

	@Autowired
	private EasyQuotationChartProcessor newEasyQuotationChartProcessor;

	@Autowired
	private ChartContainerInitProcessor chartContainerInitProcessor;

	@Autowired
	private DailyKLineProcessor dailyKLineProcessor;

	@Autowired
	private RedisCacheUtil<RealTimeMarketdata> redisCacheUtil;

	@Autowired
	private Environment env;

	@Scheduled(cron = "${openMarketMorning}")
	public void openMarketMorning() {

		if (!env.getProperty("chart.job.switch").equals("Y")) {
			return;
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

	@Scheduled(cron = "${openMarketAfternoon}")
	public void openMarketAfternoon() {

		if (!env.getProperty("chart.job.switch").equals("Y")) {
			return;
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

	@Scheduled(cron = "${genDailyKLine}")
	public void genDailyKLine() {

		if (!env.getProperty("kLine.job.switch").equals("Y")) {
			return;
		}

		System.out.println("日K生成任务启动...");
		dailyKLineProcessor.execute();
	}

	@Scheduled(cron = "${genRealMarketdataMap}")
	public void genRealMarketdataMap() {
		if (!env.getProperty("zmqRecv.job.switch").equals("Y")) {
			return;
		}
		redisCacheUtil.setCacheMap("marketdata", ChartContainer.marketdataMap);
	}

	@Scheduled(cron = "${createNewTableJob}")
	public void createNewTableJob() {

		if (!env.getProperty("createNewTableJob.switch").equals("Y")) {
			return;
		}

		System.out.println("分时数据转移到新表任务启动...");
		HttpClientUtils.doGet(createNewTableJobUrl + createNewTableJobInterval);
	}

}
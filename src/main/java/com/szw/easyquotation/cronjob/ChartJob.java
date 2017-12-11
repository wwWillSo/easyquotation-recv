package com.szw.easyquotation.cronjob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.szw.easyquotation.container.ChartContainer;
import com.szw.easyquotation.entity.RealTimeMarketdata;
import com.szw.easyquotation.processor.DailyKLineProcessor;
import com.szw.easyquotation.processor.ZmqEasyQuotationChartProcessor;
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
	private DailyKLineProcessor dailyKLineProcessor;

	@Autowired
	private RedisCacheUtil<RealTimeMarketdata> redisCacheUtil;

	@Autowired
	private Environment env;

	@Autowired
	private ZmqEasyQuotationChartProcessor zmqEasyQuotationChartProcessor;

	@Scheduled(cron = "${openMarketMorning}")
	public void openMarketMorning() {
		System.out.println("早上开市...");
		zmqEasyQuotationChartProcessor.execute();
	}

	@Scheduled(cron = "${openMarketAfternoon}")
	public void openMarketAfternoon() {
		System.out.println("下午开市...");
		zmqEasyQuotationChartProcessor.execute();
	}

	@Scheduled(cron = "${closeMarketMorning}")
	public void closeMarketMorning() {
		System.out.println("早上收市...");
		zmqEasyQuotationChartProcessor.shutdown();
	}

	@Scheduled(cron = "${closeMarketAfternoon}")
	public void closeMarketAfternoon() {
		System.out.println("下午收市...");
		zmqEasyQuotationChartProcessor.shutdown();
	}

	@Scheduled(cron = "${genDailyKLine}")
	public void genDailyKLine() {

		if (!env.getProperty("dailyKLine.job.switch").equals("Y")) {
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
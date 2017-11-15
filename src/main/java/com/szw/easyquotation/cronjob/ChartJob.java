package com.szw.easyquotation.cronjob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.szw.easyquotation.container.ChartContainer;
import com.szw.easyquotation.processor.ChartContainerInitProcessor;
import com.szw.easyquotation.processor.EasyQuotationChartProcessor;


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

	@Scheduled(fixedRate = ONE_Minute)
	public void fixedDelayJob() {

		if (ChartContainer.hasbeenInit) {
			System.out.println("定时任务fixedDelayJob启动...");
			newEasyQuotationChartProcessor.execute();
		} else {
			System.out.println("chartContainer初始化任务未完成...");
			chartContainerInitProcessor.execute();
		}

	}

}
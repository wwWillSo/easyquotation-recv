package com.szw.easyquotation.cronjob;

import org.springframework.stereotype.Component;


/**
 * 定时任务生成k线 <P>TODO</P>
 * 
 * @author 苏镇威 2017年11月6日 下午4:47:36
 */
@Component
public class ChartJob {
	public final static long ONE_Minute = 60 * 1000;

	// @Scheduled(fixedRate = ONE_Minute)
	public void fixedDelayJob() {
		System.out.println("开始查询一分钟线...");
	}

}
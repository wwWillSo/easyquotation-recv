package com.szw.easyquotation.cronjob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.szw.easyquotation.repository.MarketdataCandleChartRepository;


/**
 * 定时任务生成k线 <P>TODO</P>
 * 
 * @author 苏镇威 2017年11月6日 下午4:47:36
 */
@Component
public class ChartJob {
	public final static long ONE_Minute = 60 * 1000;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private MarketdataCandleChartRepository marketDataCandleChartRepository;

	// @Scheduled(fixedRate = ONE_Minute)
	public void fixedDelayJob() {
		System.out.println("定时任务fixedDelayJob启动...");
		new Thread(() -> {

		}).start();
	}
}
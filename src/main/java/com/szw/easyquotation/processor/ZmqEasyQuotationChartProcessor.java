package com.szw.easyquotation.processor;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.szw.easyquotation.entity.RealTimeMarketdata;
import com.szw.easyquotation.repository.MarketdataCandleChartRepository;
import com.szw.easyquotation.runnable.ZmqEasyQuotationChartRunnable;
import com.szw.easyquotation.util.DateUtil;
import com.szw.easyquotation.util.RedisCacheUtil;


@Service
public class ZmqEasyQuotationChartProcessor {

	private ExecutorService threadPool = null;

	@Autowired
	private RedisCacheUtil<RealTimeMarketdata> redisCacheUtil;

	@Value("${marketdata.zeromq.host}")
	private String zmqUrl;

	private String title = "marketdata:";

	@Autowired
	private MarketdataCandleChartRepository marketdataCandleChartRepository;

	public void execute() {

		try {
			threadPool = Executors.newSingleThreadExecutor();
			threadPool.submit(new ZmqEasyQuotationChartRunnable(marketdataCandleChartRepository, redisCacheUtil, zmqUrl, title));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			threadPool.shutdown();
		}
	}

	public boolean shutdown() {
		System.out.println("调用ZmqEasyQuotationChartProcessor.shutdown()开始..." + DateUtil.format_yyyyMMddHHmmss(new Date()));
		// threadPool.shutdownNow();
		threadPool.shutdown();
		try {
			threadPool.awaitTermination(1, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			threadPool.isTerminated();
		}

		System.out.println("调用ZmqEasyQuotationChartProcessor.shutdown()完成..." + DateUtil.format_yyyyMMddHHmmss(new Date()));
		return threadPool.isShutdown();
	}

}

package com.szw.easyquotation.processor;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
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

	private ExecutorService threadPool = Executors.newSingleThreadExecutor();;

	@Autowired
	private RedisCacheUtil<RealTimeMarketdata> redisCacheUtil;

	@Value("${marketdata.zeromq.host}")
	private String zmqUrl;

	private String title = "marketdata:";

	@Autowired
	private MarketdataCandleChartRepository marketdataCandleChartRepository;

	private final Logger log = Logger.getLogger(ZmqEasyQuotationChartProcessor.class);

	private ZmqEasyQuotationChartRunnable task = null;

	public void execute() {
		task = new ZmqEasyQuotationChartRunnable(marketdataCandleChartRepository, redisCacheUtil, zmqUrl, title);
		log.info("【线程池】" + threadPool.toString() + "提交任务...thread = " + task.toString());
		try {
			threadPool.submit(task);
		} catch (Exception e) {
			log.error("【线程池】" + threadPool.toString() + "提交任务出现异常", e);
		}
	}

	public boolean shutdown() {
		log.info("调用ZmqEasyQuotationChartProcessor.shutdown()开始..." + DateUtil.format_yyyyMMddHHmmss(new Date()));
		// threadPool.shutdownNow();
		task.isOpen = false;
		// threadPool.shutdown();
		// try {
		// threadPool.awaitTermination(1, TimeUnit.HOURS);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } finally {
		// threadPool.isTerminated();
		// }

		log.info("调用ZmqEasyQuotationChartProcessor.shutdown()完成..." + DateUtil.format_yyyyMMddHHmmss(new Date()));
		// return threadPool.isShutdown();
		return true;
	}

}

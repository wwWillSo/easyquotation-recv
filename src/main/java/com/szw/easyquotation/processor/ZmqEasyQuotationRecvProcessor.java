package com.szw.easyquotation.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.szw.easyquotation.entity.RealTimeMarketdata;
import com.szw.easyquotation.runnable.ZmqEasyQuotationRecvRunnable;
import com.szw.easyquotation.util.RedisCacheUtil;


@Service
public class ZmqEasyQuotationRecvProcessor {

	private ExecutorService threadPool = Executors.newSingleThreadExecutor();

	@Autowired
	private RedisCacheUtil<RealTimeMarketdata> redisCacheUtil;

	@Value("${marketdata.zeromq.host}")
	private String zmqUrl;

	private String title = "marketdata";

	private final Logger log = Logger.getLogger(ZmqEasyQuotationRecvProcessor.class);

	public void execute() {

		try {

			threadPool.submit(new ZmqEasyQuotationRecvRunnable(redisCacheUtil, zmqUrl, title));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// threadPool.shutdown();
		}
	}
}

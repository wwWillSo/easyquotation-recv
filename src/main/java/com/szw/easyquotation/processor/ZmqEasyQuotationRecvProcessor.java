package com.szw.easyquotation.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.szw.easyquotation.repository.RealTimeMarketdataRepository;
import com.szw.easyquotation.runnable.ZmqEasyQuotationRecvRunnable;


@Service
public class ZmqEasyQuotationRecvProcessor {

	@Autowired
	private RealTimeMarketdataRepository realTimeMarketdataRepository;

	private ExecutorService threadPool = Executors.newSingleThreadExecutor();

	@Autowired
	private RedisTemplate redisTemplate;

	@Value("${marketdata.zeromq.host}")
	private String zmqUrl;

	private String title = "marketdata";

	public void execute() {

		try {

			threadPool.submit(new ZmqEasyQuotationRecvRunnable(redisTemplate, zmqUrl, title));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			threadPool.shutdown();
		}
	}
}

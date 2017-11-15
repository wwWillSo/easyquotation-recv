package com.szw.easyquotation.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.szw.easyquotation.rabbitmq.RabbitMQRecv;
import com.szw.easyquotation.repository.RealTimeMarketdataRepository;
import com.szw.easyquotation.runnable.EasyQuotationRecvRunnable;


@Service
public class EasyQuotationRecvProcessor {

	@Autowired
	private RealTimeMarketdataRepository realTimeMarketdataRepository;

	private int poolSize = 11;
	private ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private RabbitMQRecv rabbitMQRecv;

	public void execute() {

		try {

			for (int i = 1; i < poolSize + 1; i++) {
				String queueName = "mq-" + i;
				threadPool.submit(new EasyQuotationRecvRunnable(rabbitMQRecv, redisTemplate, realTimeMarketdataRepository, queueName));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			threadPool.shutdown();
		}
	}
}

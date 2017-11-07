package com.szw.easyquotation.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.szw.easyquotation.container.NewChartContainer;
import com.szw.easyquotation.rabbitmq.RabbitMQRecv;
import com.szw.easyquotation.repository.MarketdataCandleChartRepository;
import com.szw.easyquotation.runnable.FinalEasyQuotationChartRunnable;


@Service
public class NewEasyQuotationChartProcessor {

	@Autowired
	private MarketdataCandleChartRepository MarketDataCandleChartRepository;

	private ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private RabbitMQRecv rabbitMQRecv;

	public void execute() {

		try {

			String queueName = "mq-all";

			String[] codes = NewChartContainer.retrieveStockCode();

			singleThreadPool.submit(new FinalEasyQuotationChartRunnable(MarketDataCandleChartRepository, rabbitMQRecv, queueName, codes));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// singleThreadPool.shutdown();
		}
	}
}

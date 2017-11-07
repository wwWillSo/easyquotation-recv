package com.szw.easyquotation.runnable;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;
import com.szw.easyquotation.entity.RealTimeMarketdata;
import com.szw.easyquotation.rabbitmq.RabbitMQRecv;
import com.szw.easyquotation.repository.MarketdataCandleChartRepository;


public class NewEasyQuotationChartRunnable implements Callable<NewEasyQuotationChartRunnable> {

	// 队列名称
	private String QUEUE_NAME = "cc";

	private MarketdataCandleChartRepository marketDataCandleChartRepository = null;

	private Channel channel = null;

	private RedisTemplate redisTemplate = null;

	private RabbitMQRecv rabbitMQRecv = null;

	public NewEasyQuotationChartRunnable(RabbitMQRecv rabbitMQRecv, RedisTemplate redisTemplate,
			MarketdataCandleChartRepository marketDataCandleChartRepository, String queueName) {
		this.rabbitMQRecv = rabbitMQRecv;
		this.marketDataCandleChartRepository = marketDataCandleChartRepository;
		this.QUEUE_NAME = queueName;
		this.redisTemplate = redisTemplate;
	}

	@Override
	public NewEasyQuotationChartRunnable call()
			throws ShutdownSignalException, ConsumerCancelledException, IOException, InterruptedException, TimeoutException {

		System.out.println("分时图线程启动...订阅" + QUEUE_NAME);
		while (true) {
			String message = rabbitMQRecv.getMessage(QUEUE_NAME);
			if (null == message) {
				System.out.println("null");
				continue;
			}
			JSONObject obj = JSONObject.parseObject(message);
			RealTimeMarketdata marketdata = obj.toJavaObject(RealTimeMarketdata.class);

		}
	}

}

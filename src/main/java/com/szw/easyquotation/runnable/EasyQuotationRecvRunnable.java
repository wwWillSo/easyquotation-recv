package com.szw.easyquotation.runnable;

import java.util.Date;
import java.util.concurrent.Callable;

import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson.JSONObject;
import com.szw.easyquotation.entity.RealTimeMarketdata;
import com.szw.easyquotation.rabbitmq.RabbitMQRecv;
import com.szw.easyquotation.repository.RealTimeMarketdataRepository;


public class EasyQuotationRecvRunnable implements Callable<EasyQuotationRecvRunnable> {

	// 队列名称
	private String QUEUE_NAME = "cc";

	private RealTimeMarketdataRepository realTimeMarketdataRepository = null;

	private RedisTemplate redisTemplate = null;

	private RabbitMQRecv rabbitMQRecv = null;

	public EasyQuotationRecvRunnable(RabbitMQRecv rabbitMQRecv, RedisTemplate redisTemplate, RealTimeMarketdataRepository realTimeMarketdataRepository,
			String queueName) {
		this.rabbitMQRecv = rabbitMQRecv;
		this.realTimeMarketdataRepository = realTimeMarketdataRepository;
		this.QUEUE_NAME = queueName;
		this.redisTemplate = redisTemplate;
	}

	@Override
	public EasyQuotationRecvRunnable call() {

		try {

			System.out.println(" [线程" + Thread.currentThread().getId() + "] for " + QUEUE_NAME + " 接收数据中...");

			while (true) {
				String message = rabbitMQRecv.getMessage(QUEUE_NAME);

				if (null == message)
					continue;

				JSONObject obj = JSONObject.parseObject(message);
				RealTimeMarketdata marketdata = obj.toJavaObject(RealTimeMarketdata.class);
				marketdata.setUpdateTime(new Date());
				redisTemplate.opsForValue().set(marketdata.getStockcode(), marketdata);
			}
		} catch (Exception e) {
			System.out.println(" [线程" + Thread.currentThread().getId() + "] for " + QUEUE_NAME + " 报错...");
			e.printStackTrace();
		}

		return null;
	}

}

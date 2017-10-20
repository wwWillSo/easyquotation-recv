package com.szw.easyquotation.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.szw.easyquotation.repository.MarketdataCandleChartRepository;
import com.szw.easyquotation.runnable.EasyQuotationChartRunnable;


@Service
public class EasyQuotationChartProcessor {

	@Autowired
	private MarketdataCandleChartRepository MarketDataCandleChartRepository;

	private ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

	@Autowired
	private RedisTemplate redisTemplate;

	public void execute() {

		try {
			// 打开连接和创建频道，与发送端一样
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");

			Connection connection = factory.newConnection();

			String queueName = "mq-all";

			Channel channel = connection.createChannel();
			// channel.basicQos(0,1,false); //RabbitMQ客户端接受消息最大数量
			// 声明队列，主要为了防止消息接收者先运行此程序，队列还不存在时创建队列。
			channel.exchangeDeclare("Clogs-" + queueName, "fanout");
			channel.queueDeclare(queueName, false, false, false, null);
			channel.queueBind(queueName, "Clogs-" + queueName, "");

			singleThreadPool.submit(new EasyQuotationChartRunnable(redisTemplate, MarketDataCandleChartRepository, channel, queueName));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			singleThreadPool.shutdown();
		}
	}
}

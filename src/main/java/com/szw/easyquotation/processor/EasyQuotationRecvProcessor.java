package com.szw.easyquotation.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
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

	public void execute() {

		try {
			// 打开连接和创建频道，与发送端一样
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");

			for (int i = 1; i < poolSize + 1; i++) {

				Connection connection = factory.newConnection();

				String queueName = "mq-" + i;

				Channel channel = connection.createChannel();
				// channel.basicQos(0,1,false); //RabbitMQ客户端接受消息最大数量
				// 声明队列，主要为了防止消息接收者先运行此程序，队列还不存在时创建队列。
				channel.exchangeDeclare("Clogs-" + queueName, "fanout");
				channel.queueDeclare(queueName, false, false, false, null);
				channel.queueBind(queueName, "Clogs-" + queueName, "");

				threadPool.submit(new EasyQuotationRecvRunnable(redisTemplate, realTimeMarketdataRepository, channel, queueName));
			}

			// threadPool.submit(new EasyQuotationRecvRunnable(realTimeMarketdataRepository,
			// connection.createChannel(), "mq-" + 1)) ;
			// threadPool.submit(new EasyQuotationRecvRunnable(realTimeMarketdataRepository,
			// connection.createChannel(), "mq-" + 2)) ;
			// threadPool.submit(new EasyQuotationRecvRunnable(realTimeMarketdataRepository,
			// connection.createChannel(), "mq-" + 3)) ;
			// threadPool.submit(new EasyQuotationRecvRunnable(realTimeMarketdataRepository,
			// connection.createChannel(), "mq-" + 4)) ;
			// threadPool.submit(new EasyQuotationRecvRunnable(realTimeMarketdataRepository,
			// connection.createChannel(), "mq-" + 5)) ;
			// threadPool.submit(new EasyQuotationRecvRunnable(realTimeMarketdataRepository,
			// connection.createChannel(), "mq-" + 6)) ;
			// threadPool.submit(new EasyQuotationRecvRunnable(realTimeMarketdataRepository,
			// connection.createChannel(), "mq-" + 7)) ;
			// threadPool.submit(new EasyQuotationRecvRunnable(realTimeMarketdataRepository,
			// connection.createChannel(), "mq-" + 8)) ;
			// threadPool.submit(new EasyQuotationRecvRunnable(realTimeMarketdataRepository,
			// connection.createChannel(), "mq-" + 9)) ;
			// threadPool.submit(new EasyQuotationRecvRunnable(realTimeMarketdataRepository,
			// connection.createChannel(), "mq-" + 10)) ;
			// threadPool.submit(new EasyQuotationRecvRunnable(realTimeMarketdataRepository,
			// connection.createChannel(), "mq-" + 11)) ;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			threadPool.shutdown();
		}
	}
}

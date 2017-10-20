package com.szw.easyquotation.container;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import com.szw.easyquotation.entity.MarketDataCandleChart;
import com.szw.easyquotation.entity.RealTimeMarketdata;


public class ChartContainer {
	public final static ConcurrentMap<String, Map<String, MarketDataCandleChart>> chart = new ConcurrentHashMap<String, Map<String, MarketDataCandleChart>>();

	public final static int[] chatTypeArr = { 1, 3, 5, 10, 30, 60, 1440 };

	public void init() throws IOException, TimeoutException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
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
		// 创建队列消费者
		QueueingConsumer consumer = new QueueingConsumer(channel);
		// 指定消费队列
		channel.basicConsume("mq-all", true, consumer);

		// nextDelivery是一个阻塞方法（内部实现其实是阻塞队列的take方法）
		QueueingConsumer.Delivery delivery = consumer.nextDelivery();
		String message = new String(delivery.getBody());

		JSONObject obj = JSONObject.parseObject(message);
		RealTimeMarketdata marketdata = obj.toJavaObject(RealTimeMarketdata.class);

		// 1、检查内存中有无此stockcode的图集,有则继续生成流程，无则创建新图集
		if (ChartContainer.chart.get(marketdata.getStockcode()) == null) {
			Map<String, MarketDataCandleChart> map = new HashMap<String, MarketDataCandleChart>();
			// 初始化所有类型的图表
			for (int i : ChartContainer.chatTypeArr) {
				MarketDataCandleChart chart = new MarketDataCandleChart();
				BeanUtils.copyProperties(marketdata, chart);
				chart.setCreateTime(new Date());
				chart.setUpdateTime(chart.getUpdateTime());
				map.put(i + "", chart);
				ChartContainer.chart.put(marketdata.getStockcode(), map);
			}
		}
	}
}

package com.szw.easyquotation.rabbitmq;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
import com.rabbitmq.client.ShutdownSignalException;


@Component
public class RabbitMQRecv {

	private final static ConcurrentMap<String, Object> map = new ConcurrentHashMap<String, Object>();

	@Value("${marketdata.rabbitmq.host}")
	private String MQHost;

	private ConnectionFactory factory = new ConnectionFactory();
	private Connection connection = null;
	private Channel channel = null;

	public void init() {

	}

	@SuppressWarnings("deprecation")
	public synchronized QueueingConsumer getConsume(String queueName)
			throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException, TimeoutException {
		factory.setHost(MQHost);
		connection = factory.newConnection();
		channel = connection.createChannel();
		channel.exchangeDeclare("Clogs-" + queueName, "fanout");
		channel.queueDeclare(queueName, false, false, false, null);
		channel.queueBind(queueName, "Clogs-" + queueName, "");

		// 创建队列消费者
		QueueingConsumer consumer = new QueueingConsumer(channel);
		// 指定消费队列
		channel.basicConsume(queueName, true, consumer);
		// Delivery delivery = consumer.nextDelivery();

		return consumer;
	}

	@SuppressWarnings("deprecation")
	public String getMessage(String queueName) throws ShutdownSignalException, ConsumerCancelledException, IOException, InterruptedException, TimeoutException {
		QueueingConsumer consumer = (QueueingConsumer) map.get(queueName);
		if (null == consumer) {
			consumer = getConsume(queueName);
			map.put(queueName, consumer);
		}
		String message = null;
		Delivery delivery = consumer.nextDelivery();
		message = new String(delivery.getBody());

		return message;
	}
}

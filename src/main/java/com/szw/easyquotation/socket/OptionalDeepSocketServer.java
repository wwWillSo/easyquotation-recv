package com.szw.easyquotation.socket;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;


@ServerEndpoint(value = "/optionalDeepSocketServer")
@Component
public class OptionalDeepSocketServer {

	private Logger logger = Logger.getLogger(OptionalDeepSocketServer.class);

	private static Map<String, Session> sessionMap = new ConcurrentHashMap<String, Session>();

	private static Map<String, String> topicMap = new ConcurrentHashMap<String, String>();

	private static ExecutorService exec = Executors.newCachedThreadPool();

	private static String QUEUE_NAME = "mq-all";

	// 打开连接和创建频道，与发送端一样
	private ConnectionFactory factory = null;
	private Connection connection = null;

	public OptionalDeepSocketServer() {
		System.out.println("初始化socketServer。。。");
		factory = new ConnectionFactory();
		factory.setHost("localhost");
	}

	@OnOpen
	public void onOpen(Session session) {
		try {
			logger.info("onOpen sessionId:" + session.getId());

			connection = factory.newConnection();

			Channel channel = connection.createChannel();
			channel.exchangeDeclare("Clogs-" + QUEUE_NAME, "fanout");
			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			channel.queueBind(QUEUE_NAME, "Clogs-" + QUEUE_NAME, "");

			sessionMap.put(session.getId(), session);
			topicMap.put(session.getId(), "init");
			if (((ThreadPoolExecutor) exec).getActiveCount() <= 0) {
				getContentAndSetToStatic(QUEUE_NAME, connection, channel);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@OnMessage
	public void onMessage(String topic, Session session) {
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		if (StringUtils.isEmpty(topic)) {
			topic = "all";
		}
		topicMap.put(session.getId(), topic);
		logger.info("onMessage sessionId:" + session.getId() + " topic:" + topic);
		System.out.println("onMessage sessionId:" + session.getId() + " topic:" + topic);
	}

	public static void getContentAndSetToStatic(String host, Connection connection, Channel channel) {

		exec.execute(new Runnable() {
			public void run() {

				try {
					// 创建队列消费者
					QueueingConsumer consumer = new QueueingConsumer(channel);
					// 指定消费队列
					channel.basicConsume(QUEUE_NAME, true, consumer);

					while (!Thread.currentThread().isInterrupted()) {
						// nextDelivery是一个阻塞方法（内部实现其实是阻塞队列的take方法）
						QueueingConsumer.Delivery delivery = consumer.nextDelivery();
						String contents = new String(delivery.getBody());

						// System.out.println(contents);

						JSONObject obj = JSONObject.parseObject(contents);

						String topics = obj.getString("stockcode");

						synchronized (OptionalDeepSocketServer.class) {
							actionMethod(topics, contents);
						}
					}

					channel.close();
					connection.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.info("onClose sessionId:" + session.getId());
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		synchronized (OptionalDeepSocketServer.class) {
			delMethod(session);
		}
		// sessionMap.remove(session.getId());
		// logger.info(String.format("Session %s closed because of %s", session.getId(),
		// closeReason));
	}

	@OnError
	public void error(Session session, java.lang.Throwable throwable) {
		// sessionMap.remove(session.getId());
		// System.err.println("session "+session.getId()+" error:"+throwable);
		logger.info("OnError sessionId:" + session.getId());
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		synchronized (OptionalDeepSocketServer.class) {
			delMethod(session);
		}
	}

	public synchronized static void actionMethod(String topic, String content) {
		if (StringUtils.isEmpty(topic) || StringUtils.isEmpty(content))
			return;

		Set<Map.Entry<String, Session>> set = sessionMap.entrySet();
		for (Map.Entry<String, Session> i : set) {
			try {
				// System.out.println(topicMap.get(i.getValue().getId()));// 000001
				// System.out.println(topic);// 遍历到的stockCode

				if (topic.startsWith(topicMap.get(i.getValue().getId())) || "all".equals(topicMap.get(i.getValue().getId()))) {
					i.getValue().getBasicRemote().sendText("{type:'" + "" + "',text:'" + content + "'}");
				} else {
					// Response resp = new Response();
					// resp.setCode("E_000001");
					// resp.setDesc("错误产品代码");
					// i.getValue().getBasicRemote().sendText("{type:'" + "" + "',text:'" +
					// JSONObject.toJSONString(resp) + "'}");
					// return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized static void delMethod(Session session) {
		if (session != null) {
			if (sessionMap.containsKey(session.getId()))
				sessionMap.remove(session.getId());
			if (topicMap.containsKey(session.getId()))
				topicMap.remove(session.getId());
		}
	}
}
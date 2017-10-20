package com.szw.easyquotation;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;
import com.szw.easyquotation.rabbitmq.RabbitMQRecv;


public class TestMQ {

	static RabbitMQRecv rabbitMQRecv = new RabbitMQRecv();

	@PostConstruct
	public void init(String args[]) {

	}

	public static void main(String args[]) {
		rabbitMQRecv.init();
		new Thread(() -> {
			while (true) {
				try {
					String m = rabbitMQRecv.getMessage("mq-1");
					if (m.indexOf("000001") != -1) {
						System.out.println(m);
					}

				} catch (ShutdownSignalException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ConsumerCancelledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
}

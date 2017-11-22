package com.szw.easyquotation.zeromq;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;


public class ZMQPub {
	public static void main(String[] args) throws InterruptedException {
		Context context = ZMQ.context(1);
		Socket publisher = context.socket(ZMQ.PUB);
		publisher.bind("tcp://*:5561");
		// zmq发送速度太快，在订阅者尚未与发布者建立联系时，已经开始了数据发布
		Thread.sleep(1000);

		int update_nbr;

		for (update_nbr = 20; update_nbr < 40; update_nbr++) {
			String a = "{\"magicNum\":\"CHINSOFT\",\"varName\":\"ZJ_YD_1\",\"varType\":\"5\",\"varValue\":" + update_nbr
					+ ",\"varQuality\":\"1111\",\"varTime\":" + System.currentTimeMillis() / 1000 + "}";
			publisher.send(a.getBytes(), ZMQ.NOBLOCK);
			System.out.println(update_nbr);
			Thread.sleep(1000);
		}

		publisher.close();
		context.term();
	}
}

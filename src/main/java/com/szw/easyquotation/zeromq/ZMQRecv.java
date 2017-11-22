package com.szw.easyquotation.zeromq;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;


public class ZMQRecv {

	// public final static ConcurrentMap<String, Socket> subMap = new ConcurrentHashMap<String,
	// Socket>();

	public static void main(String[] args) {
		Context context = ZMQ.context(1);
		Socket subscriber = context.socket(ZMQ.SUB);
		subscriber.connect("tcp://localhost:5561");

		String title = "marketdata";

		subscriber.subscribe(title.getBytes());
		while (true) {

			String msg = subscriber.recvStr();

			String data = msg.substring(msg.lastIndexOf("{"));

			System.out.println(data);
		}
	}

	// public static Socket getSubscriber(String title) {
	// Socket subscriber = subMap.get(title) ;
	// }
}

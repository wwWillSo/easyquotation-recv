package com.szw.easyquotation;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;


public class TestZMQ {
	public static void main(String[] args) {
		Context context = ZMQ.context(1);
		Socket subscriber = context.socket(ZMQ.SUB);
		subscriber.connect("tcp://39.128.179.2:5561");
		subscriber.subscribe("".getBytes());

		while (true) {
			String msg = subscriber.recvStr();
			String message = msg.substring(msg.lastIndexOf("{"));

			System.out.println(message);
		}
	}
}

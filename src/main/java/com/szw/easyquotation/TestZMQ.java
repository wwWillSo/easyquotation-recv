package com.szw.easyquotation;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import com.alibaba.fastjson.JSONObject;
import com.szw.easyquotation.entity.RealTimeMarketdata;


public class TestZMQ {
	public static void main(String[] args) {
		Context context = ZMQ.context(1);
		Socket subscriber = context.socket(ZMQ.SUB);
		subscriber.connect("tcp://localhost:5561");
		subscriber.subscribe("marketdata:000001".getBytes());

		while (true) {
			String msg = subscriber.recvStr();
			String message = msg.substring(msg.lastIndexOf("{"));
			
			JSONObject obj = JSONObject.parseObject(message);
			RealTimeMarketdata marketdata = obj.toJavaObject(RealTimeMarketdata.class);
			
			System.out.println(marketdata.getDate());
		}
	}
}

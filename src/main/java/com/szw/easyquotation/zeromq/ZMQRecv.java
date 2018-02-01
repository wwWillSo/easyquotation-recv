package com.szw.easyquotation.zeromq;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;


public class ZMQRecv {

	public final static ConcurrentMap<String, Socket> socketMap = new ConcurrentHashMap<String, Socket>();

	public static Context context = ZMQ.context(1);

	public static Socket getZMQRecver(String zmqUrl, String title) {
		if (null == socketMap.get(zmqUrl + "-" + title)) {
			Socket subscriber = context.socket(ZMQ.SUB);
			subscriber.connect(zmqUrl);
			subscriber.subscribe(title.getBytes());
			socketMap.put(zmqUrl + "-" + title, subscriber);
		}

		return socketMap.get(zmqUrl + "-" + title);

	}
}

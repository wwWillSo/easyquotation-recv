package com.szw.easyquotation.runnable;

import java.util.concurrent.Callable;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import com.alibaba.fastjson.JSONObject;
import com.szw.easyquotation.container.ChartContainer;
import com.szw.easyquotation.entity.RealTimeMarketdata;
import com.szw.easyquotation.util.RedisCacheUtil;


public class ZmqEasyQuotationRecvRunnable implements Callable<ZmqEasyQuotationRecvRunnable> {

	private RedisCacheUtil redisCacheUtil;

	private String zmqUrl;

	private String title;

	public ZmqEasyQuotationRecvRunnable(RedisCacheUtil redisCacheUtil, String zmqUrl, String title) {
		this.redisCacheUtil = redisCacheUtil;
		this.zmqUrl = zmqUrl;
		this.title = title;
	}

	@Override
	public ZmqEasyQuotationRecvRunnable call() {

		Context context = ZMQ.context(1);
		Socket subscriber = context.socket(ZMQ.SUB);
		subscriber.connect(zmqUrl);
		subscriber.subscribe(title.getBytes());

		try {

			System.out.println(" [实时行情线程" + Thread.currentThread().getId() + "] for " + title + " 接收数据中...");

			while (true) {

				String msg = subscriber.recvStr();
				String message = msg.substring(msg.lastIndexOf("{"));

				if (null == message)
					continue;

				JSONObject obj = JSONObject.parseObject(message);
				RealTimeMarketdata marketdata = obj.toJavaObject(RealTimeMarketdata.class);
				marketdata.setUpdateTime(marketdata.getDate());

				// if (marketdata.getStockcode().equals("000001")) {
				// System.out.println(" [线程" + Thread.currentThread().getId() + "]" + message);
				// }

				redisCacheUtil.setCacheObject(marketdata.getStockcode(), marketdata);

				ChartContainer.marketdataMap.put(marketdata.getStockcode(), marketdata);
			}
		} catch (Exception e) {
			System.out.println(" [线程" + Thread.currentThread().getId() + "] for " + title + " 报错...");
			e.printStackTrace();
		} finally {
			subscriber.close();
			context.close();
		}

		return null;
	}

}

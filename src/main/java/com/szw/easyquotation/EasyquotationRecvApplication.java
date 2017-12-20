package com.szw.easyquotation;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import com.alibaba.fastjson.JSONObject;
import com.szw.easyquotation.entity.RealTimeMarketdata;
import com.szw.easyquotation.processor.ZmqEasyQuotationChartProcessor;
import com.szw.easyquotation.processor.ZmqEasyQuotationRecvProcessor;
import com.szw.easyquotation.util.DateUtil;


@SpringBootApplication
@EnableScheduling
public class EasyquotationRecvApplication {

	@Autowired
	private ZmqEasyQuotationRecvProcessor zmqEasyQuotationRecvProcessor;

	@Autowired
	private ZmqEasyQuotationChartProcessor zmqEasyQuotationChartProcessor;

	@Autowired
	private Environment env;

	@PostConstruct
	public void init() {
		if (env.getProperty("zmqRecv.job.switch").equals("Y")) {
			zmqEasyQuotationRecvProcessor.execute();
		}

		String[] openMarketMorning = env.getProperty("openMarketMorning").split(" ");

		String[] closeMarketMorning = env.getProperty("closeMarketMorning").split(" ");

		String[] openMarketAfternoon = env.getProperty("openMarketAfternoon").split(" ");

		String[] closeMarketAfternoon = env.getProperty("closeMarketAfternoon").split(" ");

		Context context = ZMQ.context(1);
		Socket subscriber = context.socket(ZMQ.SUB);
		subscriber.connect(env.getProperty("marketdata.zeromq.host"));
		subscriber.subscribe("marketdata:000001");

		String marketdata_now = subscriber.recvStr();
		String tempMessage = marketdata_now.substring(marketdata_now.lastIndexOf("{"));

		JSONObject tempObj = JSONObject.parseObject(tempMessage);
		RealTimeMarketdata tempMarketdata = tempObj.toJavaObject(RealTimeMarketdata.class);

		Date now = tempMarketdata.getDate();

		System.out.println("当前行情时间：" + now);

		// 检查是否在开市时间内
		if (DateUtil.getWeek(now) >= 2 && DateUtil.getWeek(now) <= 6) {
			if (DateUtil.isAfter(now,
					DateUtil.getTime(Integer.valueOf(openMarketMorning[2]), Integer.valueOf(openMarketMorning[1]), Integer.valueOf(openMarketMorning[0])))
					&& DateUtil.isBefore(now, DateUtil.getTime(Integer.valueOf(closeMarketMorning[2]), Integer.valueOf(closeMarketMorning[1]),
							Integer.valueOf(closeMarketMorning[0])))) {

				new Thread(() -> {
					System.out.println("程序重启，当前行情时间为" + DateUtil.format_yyyyMMddHHmmss(now) + ", 准备早上开市...");
					while (true) {

						String msg = subscriber.recvStr();
						String message = msg.substring(msg.lastIndexOf("{"));

						if (null == message)
							continue;

						JSONObject obj = JSONObject.parseObject(message);
						RealTimeMarketdata marketdata = obj.toJavaObject(RealTimeMarketdata.class);

						if (DateUtil.getSecond(marketdata.getDate()) < 10) {
							System.out.println("早上开市线程启动..." + DateUtil.format_yyyyMMddHHmmss(marketdata.getDate()));
							zmqEasyQuotationChartProcessor.execute();
							break;
						}
					}
				}).start();
			} else if (DateUtil.isAfter(now,
					DateUtil.getTime(Integer.valueOf(openMarketAfternoon[2]), Integer.valueOf(openMarketAfternoon[1]), Integer.valueOf(openMarketAfternoon[0])))
					&& DateUtil.isBefore(now, DateUtil.getTime(Integer.valueOf(closeMarketAfternoon[2]), Integer.valueOf(closeMarketAfternoon[1]),
							Integer.valueOf(closeMarketAfternoon[0])))) {

				new Thread(() -> {
					System.out.println("程序重启，当前行情时间为" + DateUtil.format_yyyyMMddHHmmss(now) + ", 准备下午开市...");
					while (true) {

						String msg = subscriber.recvStr();
						String message = msg.substring(msg.lastIndexOf("{"));

						if (null == message)
							continue;

						JSONObject obj = JSONObject.parseObject(message);
						RealTimeMarketdata marketdata = obj.toJavaObject(RealTimeMarketdata.class);

						if (DateUtil.getSecond(marketdata.getDate()) < 10) {
							System.out.println("下午开市线程启动..." + DateUtil.format_yyyyMMddHHmmss(marketdata.getDate()));
							zmqEasyQuotationChartProcessor.execute();
							break;
						}
					}
				}).start();
			}
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(EasyquotationRecvApplication.class, args);
	}

}

package com.szw.easyquotation;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

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

		Date now = new Date();

		String[] openMarketMorning = env.getProperty("openMarketMorning").split(" ");

		String[] closeMarketMorning = env.getProperty("closeMarketMorning").split(" ");

		String[] openMarketAfternoon = env.getProperty("openMarketAfternoon").split(" ");

		String[] closeMarketAfternoon = env.getProperty("closeMarketAfternoon").split(" ");

		// 检查是否在开市时间内
		if (DateUtil.getWeek(now) >= 2 && DateUtil.getWeek(now) <= 6) {
			if (DateUtil.isAfter(now,
					DateUtil.getTime(Integer.valueOf(openMarketMorning[2]), Integer.valueOf(openMarketMorning[1]), Integer.valueOf(openMarketMorning[0])))
					&& DateUtil.isBefore(now, DateUtil.getTime(Integer.valueOf(closeMarketMorning[2]), Integer.valueOf(closeMarketMorning[1]),
							Integer.valueOf(closeMarketMorning[0])))) {
				System.out.println("程序启动时间为" + DateUtil.format_yyyyMMddHHmmss(now) + ", 早上开市...");
				zmqEasyQuotationChartProcessor.execute();
			} else if (DateUtil.isAfter(now,
					DateUtil.getTime(Integer.valueOf(openMarketAfternoon[2]), Integer.valueOf(openMarketAfternoon[1]), Integer.valueOf(openMarketAfternoon[0])))
					&& DateUtil.isBefore(now, DateUtil.getTime(Integer.valueOf(closeMarketAfternoon[2]), Integer.valueOf(closeMarketAfternoon[1]),
							Integer.valueOf(closeMarketAfternoon[0])))) {
				System.out.println("程序启动时间为" + DateUtil.format_yyyyMMddHHmmss(now) + ", 下午开市...");
				zmqEasyQuotationChartProcessor.execute();
			}
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(EasyquotationRecvApplication.class, args);
	}

}

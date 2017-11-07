package com.szw.easyquotation.runnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;
import com.szw.easyquotation.container.NewChartContainer;
import com.szw.easyquotation.entity.MarketDataCandleChart;
import com.szw.easyquotation.entity.RealTimeMarketdata;
import com.szw.easyquotation.rabbitmq.RabbitMQRecv;
import com.szw.easyquotation.repository.MarketdataCandleChartRepository;
import com.szw.easyquotation.util.DateUtil;
import com.szw.easyquotation.util.JdbcUtil;


public class FinalEasyQuotationChartRunnable implements Callable<FinalEasyQuotationChartRunnable> {

	// 队列名称
	private String QUEUE_NAME = "cc";

	private RabbitMQRecv rabbitMQRecv = null;

	private String[] codes = null;

	private Map<String, Object> set = new HashMap<String, Object>();

	private MarketdataCandleChartRepository marketdataCandleChartRepository = null;

	public FinalEasyQuotationChartRunnable(MarketdataCandleChartRepository marketdataCandleChartRepository, RabbitMQRecv rabbitMQRecv, String queueName,
			String[] codes) {
		this.marketdataCandleChartRepository = marketdataCandleChartRepository;
		this.rabbitMQRecv = rabbitMQRecv;
		this.QUEUE_NAME = queueName;
		this.codes = codes;
	}

	@Override
	public FinalEasyQuotationChartRunnable call() {
		Date now = new Date();
		List<MarketDataCandleChart> list = new ArrayList<MarketDataCandleChart>();
		System.out.println("分时图线程启动...订阅" + QUEUE_NAME);
		while (true) {
			String message = null;
			try {
				message = rabbitMQRecv.getMessage(QUEUE_NAME);
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
			if (null == message) {
				System.out.println("null");
				continue;
			}
			JSONObject obj = JSONObject.parseObject(message);
			RealTimeMarketdata marketdata = obj.toJavaObject(RealTimeMarketdata.class);

			if (null != set.get(marketdata.getStockcode())) {
				continue;
			}

			for (int min : NewChartContainer.chartTypeArr) {
				MarketDataCandleChart chart = marketdataCandleChartRepository.findTopByStockcodeAndChartTypeOrderByCreateTimeDesc(marketdata.getStockcode(),
						min);
				if (null == chart || DateUtil.countMinutes(now, chart.getCreateTime()) >= min) {
					MarketDataCandleChart newChart = new MarketDataCandleChart();
					BeanUtils.copyProperties(marketdata, newChart);
					newChart.setChartType(min);
					newChart.setCreateTime(new Date());
					newChart.setUpdateTime(newChart.getCreateTime());
					// 不能这么频繁，程序处理速度与数据库IO速度不匹配
					// marketdataCandleChartRepository.save(newChart);
					// 改成批处理
					list.add(newChart);
				}
			}

			set.put(marketdata.getStockcode(), null);
			if (set.size() % 100 == 0)
				System.out.println(set.size() + "/" + codes.length);
			if (set.size() == codes.length) {
				System.out.println("分时图线程结束...订阅" + QUEUE_NAME);
				break;
			}
		}

		JdbcUtil.batchUpdate(list);
		return null;

	}

}

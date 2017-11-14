package com.szw.easyquotation.runnable;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;
import com.szw.easyquotation.container.NewChartContainer;
import com.szw.easyquotation.entity.MarketDataCandleChart;
import com.szw.easyquotation.entity.RealTimeMarketdata;
import com.szw.easyquotation.rabbitmq.RabbitMQRecv;
import com.szw.easyquotation.util.DateUtil;


@Deprecated
public class NewEasyQuotationChartRunnable implements Callable<NewEasyQuotationChartRunnable> {

	// 队列名称
	private String QUEUE_NAME = "cc";

	private Channel channel = null;

	private RabbitMQRecv rabbitMQRecv = null;

	private String[] codes = null;

	private int count = 0;

	public NewEasyQuotationChartRunnable(RabbitMQRecv rabbitMQRecv, String queueName, String[] codes) {
		this.rabbitMQRecv = rabbitMQRecv;
		this.QUEUE_NAME = queueName;
		this.codes = codes;
	}

	@Override
	public NewEasyQuotationChartRunnable call() {

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

			Map<String, List<MarketDataCandleChart>> timeMap = null;
			try {

				timeMap = NewChartContainer.getCandleChartByCode(marketdata.getStockcode());
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("创建分时图redis失败...");
				continue;
			}

			Iterator<Map.Entry<String, List<MarketDataCandleChart>>> iterator = timeMap.entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry<String, List<MarketDataCandleChart>> entry = iterator.next();
				int min = Integer.parseInt(entry.getKey());
				List<MarketDataCandleChart> list = entry.getValue();

				if (0 == list.size()) {
					MarketDataCandleChart chart = new MarketDataCandleChart();
					BeanUtils.copyProperties(marketdata, chart);
					chart.setChartType(min);
					chart.setCreateTime(new Date());
					chart.setUpdateTime(chart.getCreateTime());
					list.add(chart);
				} else {
					// 获得最新记录
					MarketDataCandleChart chart = list.get(list.size() - 1);
					// 判断当前时间与最新记录的创建时间是否满足新增坐标条件
					long mins = DateUtil.countMinutes(new Date(), chart.getCreateTime());
					if (mins >= min) {
						chart = new MarketDataCandleChart();
						BeanUtils.copyProperties(marketdata, chart);
						chart.setChartType(min);
						chart.setCreateTime(new Date());
						chart.setUpdateTime(chart.getCreateTime());
						list.add(chart);
					}
				}

				timeMap.put(min + "", list);
			}

			NewChartContainer.saveTimeMap(marketdata.getStockcode(), timeMap);
			count += 1;

			if (count == codes.length) {
				System.out.println("分时图线程结束...订阅" + QUEUE_NAME);
				break;
			}
		}
		return null;

	}

}

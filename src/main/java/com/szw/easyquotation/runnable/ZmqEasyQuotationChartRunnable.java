package com.szw.easyquotation.runnable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import com.alibaba.fastjson.JSONObject;
import com.szw.easyquotation.container.ChartContainer;
import com.szw.easyquotation.entity.MarketDataCandleChart;
import com.szw.easyquotation.entity.RealTimeMarketdata;
import com.szw.easyquotation.repository.MarketdataCandleChartRepository;
import com.szw.easyquotation.util.DateUtil;
import com.szw.easyquotation.util.RedisCacheUtil;


public class ZmqEasyQuotationChartRunnable implements Callable<ZmqEasyQuotationChartRunnable> {

	private RedisCacheUtil redisCacheUtil;

	private String zmqUrl;

	private String title;

	private MarketdataCandleChartRepository marketdataCandleChartRepository;

	public ZmqEasyQuotationChartRunnable(MarketdataCandleChartRepository marketdataCandleChartRepository, RedisCacheUtil redisCacheUtil, String zmqUrl,
			String title) {
		this.marketdataCandleChartRepository = marketdataCandleChartRepository;
		this.redisCacheUtil = redisCacheUtil;
		this.zmqUrl = zmqUrl;
		this.title = title;
	}

	@Override
	public ZmqEasyQuotationChartRunnable call() {

		Context context = ZMQ.context(1);
		Socket subscriber = context.socket(ZMQ.SUB);
		subscriber.connect(zmqUrl);
		subscriber.subscribe(title.getBytes());

		try {

			System.out.println(" [k线图线程" + Thread.currentThread().getId() + "] for " + title + " 接收数据中...");

			while (true) {

				String msg = subscriber.recvStr();
				String message = msg.substring(msg.lastIndexOf("{"));

				if (null == message)
					continue;

				JSONObject obj = JSONObject.parseObject(message);
				RealTimeMarketdata marketdata = obj.toJavaObject(RealTimeMarketdata.class);
				marketdata.setUpdateTime(marketdata.getDate());

				// k线图与分时图逻辑
				for (String min : ChartContainer.chartTypeArr) {
					// map不存在此分钟的集合，新建一个
					if (null == ChartContainer.timeMap.get(min)) {
						Map<String, MarketDataCandleChart> chartMap = new HashMap<String, MarketDataCandleChart>();
						ChartContainer.timeMap.put(min, chartMap);
					}
					// 该分钟集合不存在此code的对象，新建一个
					if (null == ChartContainer.timeMap.get(min).get(marketdata.getStockcode())) {

						MarketDataCandleChart chart = new MarketDataCandleChart();
						chart.setChartType(Integer.parseInt(min));
						chart.setClose(marketdata.getNow());
						chart.setCreateTime(DateUtil.resetZeroSeconds(marketdata.getDate()));
						chart.setHigh(marketdata.getNow());
						chart.setLow(marketdata.getNow());
						chart.setName(marketdata.getName());
						chart.setNow(marketdata.getNow());
						chart.setOpen(marketdata.getNow());
						chart.setStockcode(marketdata.getStockcode());
						chart.setUpdateTime(DateUtil.resetZeroSeconds(marketdata.getDate()));

						// 成交量
						chart.setTurnover(marketdata.getTurnover());
						// 成交额
						chart.setVolume(marketdata.getVolume());

						// 此分钟累计成交量
						chart.setRealTimeTurnover(BigDecimal.ZERO);
						chart.setRealTimeVolume(BigDecimal.ZERO);

						ChartContainer.timeMap.get(min).put(marketdata.getStockcode(), chart);

						// if (marketdata.getStockcode().equals("000001") && min == "1") {
						// System.out.println("turnover:" + chart.getTurnover());
						// }

					} else {
						// 该分钟集合存在此code的对象，进行计算
						MarketDataCandleChart chart = ChartContainer.timeMap.get(min).get(marketdata.getStockcode());

						if (chart.getHigh().compareTo(marketdata.getNow()) == -1) {
							chart.setHigh(marketdata.getNow());
						}
						if (chart.getLow().compareTo(marketdata.getNow()) == 1) {
							chart.setLow(marketdata.getNow());
						}
						chart.setClose(marketdata.getNow());
						chart.setNow(marketdata.getNow());
						chart.setUpdateTime(marketdata.getDate());

						// 此分钟累计成交量
						chart.setRealTimeTurnover(marketdata.getTurnover().subtract(chart.getTurnover()));
						chart.setRealTimeVolume(marketdata.getVolume().subtract(chart.getVolume()));

						ChartContainer.timeMap.get(min).put(marketdata.getStockcode(), chart);

						// if (marketdata.getStockcode().equals("000001") && min == "1") {
						// System.out
						// .println(DateUtil.format_yyyyMMddHHmmss(chart.getCreateTime()) + "-" +
						// DateUtil.format_yyyyMMddHHmmss(chart.getUpdateTime())
						// + "=" + DateUtil.countSeconds(chart.getUpdateTime(),
						// chart.getCreateTime()) + ", turnover:" + chart.getTurnover());
						// }

						// 判断是否需要持久化
						if (DateUtil.countMinutes(chart.getUpdateTime(), chart.getCreateTime()) == Integer.parseInt(min)) {
							chart.setTurnover(marketdata.getTurnover().subtract(chart.getTurnover()));
							chart.setVolume(marketdata.getVolume().subtract(chart.getVolume()));

							if (min != "1") {
								chart.setCreateTime(DateUtil.resetZeroSeconds(chart.getUpdateTime()));
							} else {
								chart.setUpdateTime(chart.getCreateTime());
							}

							marketdataCandleChartRepository.save(chart);

							if (chart.getStockcode().endsWith("000001"))
								System.out.println(
										chart.getStockcode() + "-" + chart.getChartType() + "持久化完成..." + DateUtil.format_yyyyMMddHHmmss(chart.getCreateTime()));

							ChartContainer.timeMap.get(min).remove(marketdata.getStockcode());
						}
					}
				}
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

// package com.szw.easyquotation.runnable;
//
// import java.util.Date;
// import java.util.HashMap;
// import java.util.Iterator;
// import java.util.Map;
// import java.util.concurrent.Callable;
//
// import org.springframework.beans.BeanUtils;
// import org.springframework.data.redis.core.RedisTemplate;
//
// import com.alibaba.fastjson.JSONObject;
// import com.rabbitmq.client.Channel;
// import com.rabbitmq.client.QueueingConsumer;
// import com.szw.easyquotation.container.ChartContainer;
// import com.szw.easyquotation.entity.MarketDataCandleChart;
// import com.szw.easyquotation.entity.RealTimeMarketdata;
// import com.szw.easyquotation.repository.MarketdataCandleChartRepository;
// import com.szw.easyquotation.util.DateUtil;
//
//
// @Deprecated
// public class EasyQuotationChartRunnable implements Callable<EasyQuotationChartRunnable> {
//
// // 队列名称
// private String QUEUE_NAME = "cc";
//
// private MarketdataCandleChartRepository marketDataCandleChartRepository = null;
//
// private Channel channel = null;
//
// private RedisTemplate redisTemplate = null;
//
// public EasyQuotationChartRunnable(RedisTemplate redisTemplate, MarketdataCandleChartRepository
// marketDataCandleChartRepository, Channel channel,
// String queueName) {
// this.marketDataCandleChartRepository = marketDataCandleChartRepository;
// this.channel = channel;
// this.QUEUE_NAME = queueName;
// this.redisTemplate = redisTemplate;
// }
//
// @Override
// public EasyQuotationChartRunnable call() {
//
// try {
//
// System.out.println(" [线程" + Thread.currentThread().getId() + "] for " + QUEUE_NAME + " Waiting
// for messages. To exit press CTRL+C");
//
// // 创建队列消费者
// QueueingConsumer consumer = new QueueingConsumer(channel);
// // 指定消费队列
// channel.basicConsume(QUEUE_NAME, true, consumer);
//
// System.out.println(" [线程" + Thread.currentThread().getId() + "] for " + QUEUE_NAME + "
// 接收数据中...");
//
// while (true) {
// // nextDelivery是一个阻塞方法（内部实现其实是阻塞队列的take方法）
// QueueingConsumer.Delivery delivery = consumer.nextDelivery();
// String message = new String(delivery.getBody());
//
// JSONObject obj = JSONObject.parseObject(message);
// RealTimeMarketdata marketdata = obj.toJavaObject(RealTimeMarketdata.class);
//
// // 1、检查内存中有无此stockcode的图集,有则继续生成流程，无则创建新图集
// if (ChartContainer.chart.get(marketdata.getStockcode()) == null) {
// Map<String, MarketDataCandleChart> map = new HashMap<String, MarketDataCandleChart>();
// // 初始化所有类型的图表
// for (int i : ChartContainer.chartTypeArr) {
// MarketDataCandleChart chart = new MarketDataCandleChart();
// BeanUtils.copyProperties(marketdata, chart);
// chart.setCreateTime(new Date());
// chart.setUpdateTime(chart.getUpdateTime());
// map.put(i + "", chart);
// ChartContainer.chart.put(marketdata.getStockcode(), map);
// }
// continue;
// }
//
// // System.out.println(marketdata.getStockcode() + "-A");
//
// // 2、如果内存中已经有此stockcode的图集，则进行计算，符合条件的将会被持久化
// Map<String, MarketDataCandleChart> map = ChartContainer.chart.get(marketdata.getStockcode());
// Iterator<Map.Entry<String, MarketDataCandleChart>> iterator = map.entrySet().iterator();
// while (iterator.hasNext()) {
// Map.Entry<String, MarketDataCandleChart> entry = iterator.next();
// int i = Integer.parseInt(entry.getKey());
// MarketDataCandleChart chart = entry.getValue();
// BeanUtils.copyProperties(marketdata, chart, "createTime");
// chart.setUpdateTime(new Date());
// map.put(entry.getKey(), chart);
//
// if (DateUtil.countMinutes(chart.getUpdateTime(), chart.getCreateTime()) >= i) {
// chart.setCreateTime(chart.getUpdateTime());
// map.put(entry.getKey(), chart);
// chart.setChartType(i);
// marketDataCandleChartRepository.save(chart);
//
// // System.out.println(marketdata.getStockcode() + "-B");
// continue;
// }
// }
// // BeanUtils.copyProperties(marketdata, chart);
// // chart.setCreateTime(new Date());
// // chart.setUpdateTime(new Date());
//
// // marketdata.setUpdateTime(new Date());
// // redisTemplate.opsForValue().set("container", ChartContainer.chart);
//
// }
// } catch (Exception e) {
// System.out.println(" [线程" + Thread.currentThread().getId() + "] for " + QUEUE_NAME + " 报错...");
// e.printStackTrace();
// }
//
// return null;
// }
//
// }

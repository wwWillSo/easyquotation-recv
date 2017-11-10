package com.szw.easyquotation.processor;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.szw.easyquotation.entity.RealTimeMarketdata;
import com.szw.easyquotation.rabbitmq.RabbitMQRecv;
import com.szw.easyquotation.repository.MarketdataCandleChartRepository;
import com.szw.easyquotation.runnable.FinalEasyQuotationChartRunnable;
import com.szw.easyquotation.util.DateUtil;
import com.szw.easyquotation.util.HttpClientUtils;
import com.szw.easyquotation.util.ListUtil;


@Service
public class NewEasyQuotationChartProcessor {

	@Autowired
	private MarketdataCandleChartRepository MarketDataCandleChartRepository;

	private int poolSize = 11;
	private ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private RabbitMQRecv rabbitMQRecv;

	@Value("${marketdata.webservice.host}")
	private String marketdataUrl;

	public void execute() {

		try {
			System.out.println("分时图生成任务开始..." + DateUtil.format_yyyyMMddHHmmss(new Date()));
			List<RealTimeMarketdata> dataList = getAllMarketdata();

			List<List<RealTimeMarketdata>> list = ListUtil.averageAssign(dataList, 11);

			for (List<RealTimeMarketdata> l : list) {

				threadPool.submit(new FinalEasyQuotationChartRunnable(l, MarketDataCandleChartRepository));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// threadPool.shutdown();
		}
	}

	public List<RealTimeMarketdata> getAllMarketdata() {
		String entity = HttpClientUtils.doGet(marketdataUrl);
		// System.out.println(entity);
		JSONObject jsonObj = JSON.parseObject(entity);
		JSONArray result = jsonObj.getJSONArray("marketdata");
		List<RealTimeMarketdata> list = JSON.parseArray(result.toJSONString(), RealTimeMarketdata.class);
		return list;
	}

}

package com.szw.easyquotation.runnable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.springframework.beans.BeanUtils;

import com.szw.easyquotation.container.NewChartContainer;
import com.szw.easyquotation.entity.MarketDataCandleChart;
import com.szw.easyquotation.entity.RealTimeMarketdata;
import com.szw.easyquotation.repository.MarketdataCandleChartRepository;
import com.szw.easyquotation.util.DateUtil;
import com.szw.easyquotation.util.JdbcUtil;
import com.szw.easyquotation.util.SpringUtil;


public class FinalEasyQuotationChartRunnable implements Callable<FinalEasyQuotationChartRunnable> {

	private Map<String, Object> set = new HashMap<String, Object>();

	private MarketdataCandleChartRepository marketdataCandleChartRepository = null;

	private List<RealTimeMarketdata> dataList = null;

	public FinalEasyQuotationChartRunnable(List<RealTimeMarketdata> dataList, MarketdataCandleChartRepository marketdataCandleChartRepository) {
		this.dataList = dataList;
		this.marketdataCandleChartRepository = marketdataCandleChartRepository;
	}

	@Override
	public FinalEasyQuotationChartRunnable call() {

		Date now = new Date();
		List<MarketDataCandleChart> list = new ArrayList<MarketDataCandleChart>();

		for (RealTimeMarketdata marketdata : dataList) {

			// long start = new Date().getTime();

			if (null != set.get(marketdata.getStockcode())) {
				System.out.println(marketdata.getStockcode() + "重复了");
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

			// long end = new Date().getTime();
			// System.out.println((end - start));
		}
		// System.out.println("执行完毕...");
		SpringUtil.getBean("jdbcUtil", JdbcUtil.class).insertBatchFlush(list);
		return null;

	}

}

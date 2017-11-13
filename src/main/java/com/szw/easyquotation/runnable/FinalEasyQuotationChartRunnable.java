package com.szw.easyquotation.runnable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.springframework.beans.BeanUtils;

import com.szw.easyquotation.container.ChartContainer;
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

	private Date now = null;

	public FinalEasyQuotationChartRunnable(List<RealTimeMarketdata> dataList, MarketdataCandleChartRepository marketdataCandleChartRepository, Date now) {
		this.dataList = dataList;
		this.marketdataCandleChartRepository = marketdataCandleChartRepository;
		this.now = now;
	}

	@Override
	public FinalEasyQuotationChartRunnable call() {
		try {
			List<MarketDataCandleChart> list = new ArrayList<MarketDataCandleChart>();

			for (RealTimeMarketdata marketdata : dataList) {

				// long start = new Date().getTime();

				if (null != set.get(marketdata.getStockcode())) {
					System.out.println(marketdata.getStockcode() + "重复了");
					continue;
				}

				for (int min : ChartContainer.chartTypeArr) {

					MarketDataCandleChart chart = null;
					// 当chartMap为空时，很大可能是因为程序重启了，为了避免一天生成两条日k的情况，需检查数据库
					if (ChartContainer.chartMap.size() == 0 || null == ChartContainer.chartMap.get(marketdata.getStockcode())) {
						chart = marketdataCandleChartRepository.findTopByStockcodeAndChartTypeOrderByCreateTimeDesc(marketdata.getStockcode(), min);
					} else {
						chart = ChartContainer.chartMap.get(marketdata.getStockcode()).get(min + "");
					}

					if (null == chart) {
						// System.out.println("异常....chart为null...");
					}

					if (null == chart || DateUtil.countMinutes(now, chart.getCreateTime()) >= min) {
						MarketDataCandleChart newChart = new MarketDataCandleChart();
						BeanUtils.copyProperties(marketdata, newChart);
						newChart.setChartType(min);
						newChart.setCreateTime(now);
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
			ChartContainer.genDataMap(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
package com.szw.easyquotation.runnable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.springframework.beans.BeanUtils;

import com.szw.easyquotation.container.ChartContainer;
import com.szw.easyquotation.entity.MarketDataCandleChart;
import com.szw.easyquotation.entity.RealTimeMarketdata;
import com.szw.easyquotation.repository.MarketdataCandleChartRepository;
import com.szw.easyquotation.util.DateUtil;


public class EasyQuotationChartRunnable implements Callable<EasyQuotationChartRunnable> {

	private MarketdataCandleChartRepository marketdataCandleChartRepository = null;

	private List<RealTimeMarketdata> dataList = null;

	private Date now = null;

	public EasyQuotationChartRunnable(List<RealTimeMarketdata> dataList, MarketdataCandleChartRepository marketdataCandleChartRepository, Date now) {
		this.dataList = dataList;
		this.marketdataCandleChartRepository = marketdataCandleChartRepository;
		this.now = now;
	}

	@Override
	public EasyQuotationChartRunnable call() {
		try {
			List<MarketDataCandleChart> list = new ArrayList<MarketDataCandleChart>();

			for (RealTimeMarketdata marketdata : dataList) {

				// long start = new Date().getTime();

				for (int min : ChartContainer.chartTypeArr) {

					// 当chartMap中此code为空时，应该给他生成一个
					Map<String, MarketDataCandleChart> codeMap = ChartContainer.chartMap.get(marketdata.getStockcode());
					if (null == codeMap || null == codeMap.get(min + "")) {
						MarketDataCandleChart newChart = new MarketDataCandleChart();
						BeanUtils.copyProperties(marketdata, newChart);
						newChart.setChartType(min);
						newChart.setCreateTime(now);
						newChart.setUpdateTime(newChart.getCreateTime());

						ChartContainer.genDataMap(newChart);

						continue;
					}

					MarketDataCandleChart chart = ChartContainer.chartMap.get(marketdata.getStockcode()).get(min + "");

					if (null == chart) {
						System.out.println("异常....chart为null...");
					}

					if (DateUtil.countMinutes(now, chart.getCreateTime()) >= min) {
						MarketDataCandleChart newChart = new MarketDataCandleChart();
						BeanUtils.copyProperties(marketdata, newChart);
						newChart.setChartType(min);
						newChart.setCreateTime(now);
						newChart.setUpdateTime(newChart.getCreateTime());
						list.add(newChart);
					}
				}

				// long end = new Date().getTime();
				// System.out.println((end - start));
			}
			ChartContainer.genDataMap(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}

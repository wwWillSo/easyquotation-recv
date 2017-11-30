package com.szw.easyquotation.runnable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.beans.BeanUtils;

import com.szw.easyquotation.bean.DailyKLineBean;
import com.szw.easyquotation.container.ChartContainer;
import com.szw.easyquotation.entity.MarketDataCandleChart;
import com.szw.easyquotation.entity.RealTimeMarketdata;
import com.szw.easyquotation.repository.MarketdataCandleChartRepository;
import com.szw.easyquotation.util.DateUtil;


public class DailyKLineRunnable implements Callable<DailyKLineRunnable> {

	private MarketdataCandleChartRepository marketDataCandleChartRepository = null;

	private List<RealTimeMarketdata> dataList = null;

	private String kLineUrl = null;

	public DailyKLineRunnable(MarketdataCandleChartRepository marketDataCandleChartRepository, List<RealTimeMarketdata> dataList, String kLineUrl) {
		this.marketDataCandleChartRepository = marketDataCandleChartRepository;
		this.dataList = dataList;
		this.kLineUrl = kLineUrl;
	}

	@Override
	public DailyKLineRunnable call() {

		try {
			for (RealTimeMarketdata stock : dataList) {
				String stockCode = stock.getStockcode();

				List<DailyKLineBean> kLineBeanList = ChartContainer.getDailyKLine(kLineUrl, stockCode);
				
				if (null == kLineBeanList || kLineBeanList.isEmpty()) {
					System.out.println(stockCode + "的日k数据为空");
					continue ;
				}

				List<MarketDataCandleChart> list = new ArrayList<MarketDataCandleChart>();
				for (DailyKLineBean bean : kLineBeanList) {
					Date date = DateUtil.resetZeroSeconds(bean.getDate());
					MarketDataCandleChart temp = marketDataCandleChartRepository.findByStockcodeAndChartTypeAndCreateTime(stockCode, 1440, date);
					if (null == temp) {
						MarketDataCandleChart chart = new MarketDataCandleChart();
						BeanUtils.copyProperties(bean, chart);
						chart.setChartType(1440);
						chart.setStockcode(stockCode);
						chart.setCreateTime(DateUtil.resetZeroSeconds(bean.getDate()));
						chart.setUpdateTime(chart.getCreateTime());
						if (null == chart.getVolume())
							chart.setVolume(BigDecimal.ZERO);
						if (null == chart.getTurnover())
							chart.setTurnover(BigDecimal.ZERO);
						list.add(chart);
					}
				}
				ChartContainer.persisChartList(list);
//				System.out.println(stockCode + "日k数据持久化完毕，总共" + list.size() + "条");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}

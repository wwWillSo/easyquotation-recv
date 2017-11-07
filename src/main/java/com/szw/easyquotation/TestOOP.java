package com.szw.easyquotation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.szw.easyquotation.entity.MarketDataCandleChart;


public class TestOOP {

	public static void main(String[] args) {
		MarketDataCandleChart chart1 = new MarketDataCandleChart();
		MarketDataCandleChart chart2 = new MarketDataCandleChart();
		MarketDataCandleChart chart3 = new MarketDataCandleChart();
		// chart.setCreateTime(new Date());
		// TODO Auto-generated method stub
		List<MarketDataCandleChart> list = new ArrayList<MarketDataCandleChart>();
		list.add(chart1);
		list.add(chart2);
		list.add(chart3);

		// for (MarketDataCandleChart a : list) {
		// System.out.println(a.getCreateTime());
		// }

		// list.get(0).setUpdateTime(new Date());

		MarketDataCandleChart chart = list.get(0);
		chart.setUpdateTime(new Date());

		for (MarketDataCandleChart a : list) {
			System.out.println(a.getUpdateTime());
		}
	}

}

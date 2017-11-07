package com.szw.easyquotation.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.szw.easyquotation.entity.MarketDataCandleChart;


public class JdbcUtil {

	public static <T> void batchUpdate(List<T> list) {
		try {

			for (T t : list) {
				Field[] fields = t.getClass().getDeclaredFields();
				for (Field m : fields) {
					if (!m.isAccessible())
						m.setAccessible(true);
					System.out.println(m.getName() + m.get(t));
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String args[]) {
		List<MarketDataCandleChart> list = new ArrayList<MarketDataCandleChart>();
		MarketDataCandleChart chart1 = new MarketDataCandleChart();
		chart1.setId("1");
		MarketDataCandleChart chart2 = new MarketDataCandleChart();
		chart1.setId("2");
		MarketDataCandleChart chart3 = new MarketDataCandleChart();
		chart1.setId("3");

		list.add(chart1);
		list.add(chart2);
		list.add(chart3);

		batchUpdate(list);
	}
}

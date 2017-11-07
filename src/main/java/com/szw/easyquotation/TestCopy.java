package com.szw.easyquotation;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import com.szw.easyquotation.entity.MarketDataCandleChart;


public class TestCopy {

	public static void main(String[] args) {
		MarketDataCandleChart c1 = new MarketDataCandleChart();
		MarketDataCandleChart c2 = new MarketDataCandleChart();

		c1.setStockcode("0001");
		c1.setCreateTime(new Date());

		BeanUtils.copyProperties(c1, c2);

		System.out.println(c2.getCreateTime());
	}

}

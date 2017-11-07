package com.szw.easyquotation.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.szw.easyquotation.container.ChartContainer;
import com.szw.easyquotation.entity.MarketDataCandleChart;


@Controller
public class MapContainerController {

	@RequestMapping("/getMarketDataCandleChart")
	@ResponseBody
	public Map<String, MarketDataCandleChart> getMarketDataCandleChart(String stockcode) {
		return ChartContainer.chart.get(stockcode);
	}
}

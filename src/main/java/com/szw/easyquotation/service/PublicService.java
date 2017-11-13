package com.szw.easyquotation.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.szw.easyquotation.entity.MarketDataCandleChart;
import com.szw.easyquotation.repository.MarketdataCandleChartRepository;


@Service
public class PublicService {

	@Autowired
	private MarketdataCandleChartRepository marketdataCandleChartRepository;

	public List<MarketDataCandleChart> retrieveMarketDataCandleChart(String stockcode, int chartType) {
		return marketdataCandleChartRepository.findByStockcodeAndChartType(stockcode, chartType);
	}

}

package com.szw.easyquotation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.szw.easyquotation.entity.MarketDataCandleChart;


@Repository
public interface MarketdataCandleChartRepository extends JpaRepository<MarketDataCandleChart, Long> {

	public MarketDataCandleChart findTopByStockcodeAndChartTypeOrderByCreateTimeDesc(String stockcode, int chartType);
}

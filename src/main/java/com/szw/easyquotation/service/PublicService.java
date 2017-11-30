package com.szw.easyquotation.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.szw.easyquotation.bean.AllMarketDataJsonResp;
import com.szw.easyquotation.bean.PageRequest;
import com.szw.easyquotation.entity.MarketDataCandleChart;
import com.szw.easyquotation.entity.RealTimeMarketdata;
import com.szw.easyquotation.repository.MarketdataCandleChartRepository;
import com.szw.easyquotation.util.DateUtil;
import com.szw.easyquotation.util.RedisCacheUtil;


@Service
public class PublicService {

	@Autowired
	private MarketdataCandleChartRepository marketdataCandleChartRepository;

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Value("${marketdata.webservice.host}")
	private String marketdataUrl;

	public List<MarketDataCandleChart> retrieveMarketDataCandleChart(String stockcode, int chartType) {
		return marketdataCandleChartRepository.findByStockcodeAndChartType(stockcode, chartType);
	}

	public List<Object[]> retrieveKChart(String stockcode, int chartType) {

		List<Object[]> charts = new ArrayList<Object[]>();

		List<MarketDataCandleChart> list = marketdataCandleChartRepository.findByStockcodeAndChartType(stockcode, chartType);

		for (MarketDataCandleChart o : list) {
			Object[] chart = new Object[6];
			String date = DateUtil.format_yyyyMMdd(o.getCreateTime());
			BigDecimal open = o.getOpen();
			BigDecimal close = o.getClose();
			BigDecimal low = o.getLow();
			BigDecimal high = o.getHigh();
			BigDecimal vol = o.getVolume();

			chart[0] = date;
			chart[1] = open;
			chart[2] = close;
			chart[3] = low;
			chart[4] = high;
			chart[5] = vol;
			charts.add(chart);
		}

		return charts;
	}

	public RealTimeMarketdata getMarketdataByCode(String stockcode) {
		return (RealTimeMarketdata) redisCacheUtil.getCacheObject(stockcode);
	}

	public AllMarketDataJsonResp getAllMarketData(PageRequest request) {

		AllMarketDataJsonResp resp = new AllMarketDataJsonResp();

		int pageNo = request.getPageNo();
		int pageSize = request.getPageSize() == 0 ? 1 : request.getPageSize();

		List<RealTimeMarketdata> list = new ArrayList<RealTimeMarketdata>();

		Map<String, RealTimeMarketdata> map = redisCacheUtil.getCacheMap("marketdata");

		String stockCodes = (String) redisCacheUtil.getCacheObject("stockCodes");

		String[] code_arr = stockCodes.split(",");

		for (String code : code_arr) {
			if (null == map.get(code)) {
				list.add((RealTimeMarketdata) redisCacheUtil.getCacheObject(code));
			} else {
				list.add(map.get(code));
			}
		}

		int fromIndex = pageNo * pageSize;
		int toIndex = fromIndex + pageSize;
		int lastPageNo = list.size() % pageSize == 0 ? list.size() / pageSize : list.size() / pageSize + 1;

		try {

			resp.setList(list.subList(fromIndex, toIndex));
		} catch (IndexOutOfBoundsException e) {

			resp.setList(list.subList(fromIndex, list.size()));
		}

		resp.setLastPageNo(lastPageNo);

		return resp;
	}

}

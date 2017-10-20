package com.szw.easyquotation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.szw.easyquotation.entity.RealTimeMarketdata;
import com.szw.easyquotation.repository.RealTimeMarketdataRepository;


@Controller
public class MarketdataController {

	@Autowired
	private RealTimeMarketdataRepository realTimeMarketdataRepository;

	@RequestMapping("/getMarketdataByCode/{stockCode}")
	@ResponseBody
	public RealTimeMarketdata getMarketdataByCode(@PathVariable String stockCode) {
		return realTimeMarketdataRepository.findOne(stockCode);
	}
}

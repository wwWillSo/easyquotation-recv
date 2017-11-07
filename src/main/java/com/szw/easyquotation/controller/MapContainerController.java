package com.szw.easyquotation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.szw.easyquotation.bean.Response;
import com.szw.easyquotation.container.NewChartContainer;


@Controller
public class MapContainerController {

	@RequestMapping("/getCodeMap/{stockcode}/{type}")
	@ResponseBody
	public Response getMarketDataCandleChart(@PathVariable("stockcode") String stockcode, @PathVariable("type") String type) {
		Response resp = new Response();

		if ("all".equals(stockcode)) {
			resp.setData(NewChartContainer.codeMap);
		} else {
			if ("all".equals(type)) {
				resp.setData(NewChartContainer.codeMap.get(stockcode));
			} else {
				resp.setData(NewChartContainer.codeMap.get(stockcode).get(type));
			}
		}

		resp.setCode("SUCCESS");
		resp.setDesc("查询成功， 参数：" + stockcode + ", " + type);

		return resp;
	}

}

package com.szw.easyquotation.bean;

import java.util.ArrayList;
import java.util.List;


public class DailyKLineResponse {
	private List<DailyKLineBean> record = new ArrayList<DailyKLineBean>();

	public List<DailyKLineBean> getRecord() {
		return record;
	}

	public void setRecord(List<DailyKLineBean> record) {
		this.record = record;
	}

}

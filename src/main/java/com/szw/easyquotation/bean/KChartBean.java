package com.szw.easyquotation.bean;

import java.io.Serializable;
import java.util.List;


public class KChartBean implements Serializable {

	/**
	 * TODO
	 */
	private static final long serialVersionUID = -5007233091799229280L;

	private List<List<String>> charts;

	public List<List<String>> getCharts() {
		return charts;
	}

	public void setCharts(List<List<String>> charts) {
		this.charts = charts;
	}

}

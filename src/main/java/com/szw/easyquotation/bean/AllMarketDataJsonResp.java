package com.szw.easyquotation.bean;

import java.io.Serializable;
import java.util.List;

import com.szw.easyquotation.entity.RealTimeMarketdata;


public class AllMarketDataJsonResp implements Serializable {
	/**
	 * TODO
	 */
	private static final long serialVersionUID = 8227395878334403905L;
	private List<RealTimeMarketdata> list;

	private int lastPageNo;

	public int getLastPageNo() {
		return lastPageNo;
	}

	public void setLastPageNo(int lastPageNo) {
		this.lastPageNo = lastPageNo;
	}

	public List<RealTimeMarketdata> getList() {
		return list;
	}

	public void setList(List<RealTimeMarketdata> list) {
		this.list = list;
	}

}

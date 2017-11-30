package com.szw.easyquotation.bean;

import java.io.Serializable;


public class PageRequest implements Serializable {
	/**
	 * TODO
	 */
	private static final long serialVersionUID = 1918560552649774353L;
	private int pageNo;
	private int pageSize;

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

}

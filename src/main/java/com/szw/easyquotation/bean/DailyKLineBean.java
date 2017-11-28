package com.szw.easyquotation.bean;

import java.math.BigDecimal;
import java.util.Date;


public class DailyKLineBean {
	// ['date', 'open', 'high', 'close', 'low', 'volume','chg', '%chg', 'ma5', 'ma10',
	// 'ma20','vma5', 'vma10', 'vma20', 'turnover']

	private Date date;
	private BigDecimal open;
	private BigDecimal high;
	private BigDecimal close;
	private BigDecimal low;
	private BigDecimal volume;
	private BigDecimal turnover;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public BigDecimal getOpen() {
		return open;
	}

	public void setOpen(BigDecimal open) {
		this.open = open;
	}

	public BigDecimal getHigh() {
		return high;
	}

	public void setHigh(BigDecimal high) {
		this.high = high;
	}

	public BigDecimal getClose() {
		return close;
	}

	public void setClose(BigDecimal close) {
		this.close = close;
	}

	public BigDecimal getLow() {
		return low;
	}

	public void setLow(BigDecimal low) {
		this.low = low;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public BigDecimal getTurnover() {
		return turnover;
	}

	public void setTurnover(BigDecimal turnover) {
		this.turnover = turnover;
	}

}

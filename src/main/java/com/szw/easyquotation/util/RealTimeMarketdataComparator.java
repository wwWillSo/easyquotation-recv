package com.szw.easyquotation.util;

import java.util.Comparator;

import com.szw.easyquotation.entity.RealTimeMarketdata;


public class RealTimeMarketdataComparator implements Comparator<RealTimeMarketdata> {

	@Override
	public int compare(RealTimeMarketdata o1, RealTimeMarketdata o2) {
		if (Integer.valueOf(o1.getStockcode()) < Integer.valueOf(o2.getStockcode()))
			return -1;
		else if (Integer.valueOf(o1.getStockcode()) > Integer.valueOf(o2.getStockcode()))
			return 1;
		else
			return 0;
	}

}

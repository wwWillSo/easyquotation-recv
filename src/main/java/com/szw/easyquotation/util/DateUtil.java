package com.szw.easyquotation.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateUtil {

	public final static long MINUTE = 60 * 1000;

	public static Date resetLastSeconds(Date date) {
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.set(Calendar.SECOND, 59);
		now.set(Calendar.MILLISECOND, 0);
		return now.getTime();
	}

	public static Date resetZeroSeconds(Date date) {
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		return now.getTime();
	}

	public static String format_yyyyMMddHHmmss(Date date) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		return sdf.format(date);

	}

	public static String format_yyyyMMdd(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		return sdf.format(date);
	}

	public static long countMinutes(Date end, Date start) {

		long minute = (end.getTime() - start.getTime()) / MINUTE;

		return minute;
	}

	public static void main(String args[]) throws Exception {
		// Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2017-11-06 13:00:44");
		// Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2017-11-06 13:01:00");
		// System.out.println(countMinutes(date2, date1));

		System.out.println(resetZeroSeconds(new Date()));
	}
}

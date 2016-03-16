package com.datasage.oi.common.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datasage.oi.dao.chat.ChatOracleDAO;

public class ObjectTransition {

	private static Logger logger = LoggerFactory.getLogger(ChatOracleDAO.class);

	private static DateFormat DEFAULT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static DateFormat FORMAT_1 = new SimpleDateFormat("yyyyMMddHHmmss");

	public static Date string2Date(String datStr) throws ParseException {
		return DEFAULT_FORMAT.parse(datStr);
	}

	public static Date string2Date1(String datStr) throws ParseException {
		return FORMAT_1.parse(datStr);
	}

	public static String dateToSting(Date date) {
		if (date == null)
			return "";
		return DEFAULT_FORMAT.format(date);
	}

	public static java.sql.Date utilDate2SqlDate(java.util.Date utilDate) {
		if (utilDate == null)
			return null;

		return new java.sql.Date(utilDate.getTime());
	}
	
	public static Timestamp date2Timestamp (Date date) {
		if (date == null)
			return null;
		return new Timestamp(date.getTime());
	}

	public static Long object2Long(Object obj) {
		if (obj == null)
			return 0l;

		return Long.parseLong(obj.toString());
	}

	public static String object2String(Object obj) {
		if (obj == null)
			return "";
		return obj.toString();
	}

	public static String strOracleTransMeaning(String str) {
		if (str == null || str.isEmpty())
			return str;

		StringBuffer strBuffer = new StringBuffer();

		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			strBuffer.append(ch);

			if (ch == '\'') {
				strBuffer.append("'");
			}
		}

		return strBuffer.toString();
	}

	public static Date timeAddMinutes(Date time, int minusTime) {
		if (time == null)
			return null;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		cal.add(Calendar.MINUTE, minusTime);
		return cal.getTime();

	}
	
	public static Date timeAddSeconds (Date time, int seconds) {
		if (time == null)
			return null;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		cal.add(Calendar.SECOND, seconds);
		return cal.getTime();
	}

	public static void main(String[] args) {
		int i = 30;
		System.out.println(timeAddMinutes(new Date(), -i));
	}
}

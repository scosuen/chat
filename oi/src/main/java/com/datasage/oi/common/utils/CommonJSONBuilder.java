package com.datasage.oi.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CommonJSONBuilder {
	
	public static String getResponseJSON() {
		return getResponseJSON(0, null);
	}

	public static String getResponseJSON(int errorCode, String errorMsg) {
		StringBuffer json = new StringBuffer("{\"success\": ");

		if (errorCode > 0)
			json.append("false");
		else
			json.append("true");

		json.append(", \"payload\": { }");

		if (errorCode > 0) {
			json.append(", \"error\": { \"code\": ").append(errorCode)
					.append(", ");
			json.append("\"message\": \"").append(errorMsg).append("\"} ");
		}

		json.append("}");
		return json.toString();
	}
	

	public static void main(String[] args) {
		System.out.println(CommonJSONBuilder
				.getResponseJSON(0, "This is an error!"));
	}

}

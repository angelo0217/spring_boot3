package com.example.demo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StockUtils {
	public static boolean isEStock(String stockCode) {
		String twseRegex = "\\d{4}$";
		Pattern pattern = Pattern.compile(twseRegex);
		Matcher matcher = pattern.matcher(stockCode);
		return matcher.matches();
	}
}

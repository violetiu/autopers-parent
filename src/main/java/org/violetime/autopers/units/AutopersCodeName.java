package org.violetime.autopers.units;

import java.util.HashMap;
import java.util.Map;

public class AutopersCodeName {
	
	public static void main(String[] args) {
		
		System.out.println(AutopersCodeName.className("market_code"));
	}
	private  static Map<String,String> sourceTableMap;
	public static String className(String value,String baseSource) {
		if (value == null || value.length() == 0)
			return null;
		if(sourceTableMap==null){
			sourceTableMap=new HashMap<>();
		}
		if(sourceTableMap.containsKey(value)){
			if(baseSource!=sourceTableMap.get(value))
				value=value+"_"+baseSource;
		}
		sourceTableMap.put(value,baseSource);
		value=value.toLowerCase();
		if (value.contains("_")) {
			String result = "";
			String[] valueArray = value.split("_");
			for (String val : valueArray) {
				if (val == null || value == "")
					continue;
				else if (val.length() == 1) {
					result += val.toUpperCase();
				} else {
					result += val.substring(0, 1).toUpperCase() + val.substring(1);
				}
			}
			return result;
		} else {
			return value.substring(0, 1).toUpperCase() + value.substring(1);
		}
	}
	public static String className(String value) {
		if (value == null || value.length() == 0)
			return null;
		value=value.toLowerCase();
		if (value.contains("_")) {
			String result = "";
			String[] valueArray = value.split("_");
			for (String val : valueArray) {
				if (val == null || value == "")
					continue;
				else if (val.length() == 1) {
					result += val.toUpperCase();
				} else {
					result += val.substring(0, 1).toUpperCase() + val.substring(1);
				}
			}
			return result;

		} else {
			return value.substring(0, 1).toUpperCase() + value.substring(1);
		}

	}
	public static String attributeGetSetCaseName(String value) {
		if (value == null || value.length() == 0)
			return null;
		if (value.contains("_")) {
			String result = "";
			String[] valueArray = value.split("_");
			for (String val : valueArray) {
				if (val == null || value == "")
					continue;
				else if (val.length() == 1) {
					result += val.toUpperCase();
				} else {
					result += val.substring(0, 1).toUpperCase() + val.substring(1);
				}
			}
			return result;

		} else {
			return value.substring(0, 1).toUpperCase() + value.substring(1);
		}

	}
	public static String attributeGetSetName(String value) {
		if (value == null || value.length() == 0)
			return null;
		value=value.toLowerCase();
		if (value.contains("_")) {
			String result = "";
			String[] valueArray = value.split("_");
			for (String val : valueArray) {
				if (val == null || value == "")
					continue;
				else if (val.length() == 1) {
					result += val.toUpperCase();
				} else {
					result += val.substring(0, 1).toUpperCase() + val.substring(1);
				}
			}
			return result;

		} else {
			return value.substring(0, 1).toUpperCase() + value.substring(1);
		}

	}
	public static String attributeName(String value) {
		if (value == null || value.length() == 0)
			return null;
		value=value.toLowerCase();
		if (value.contains("_")) {
			String result = "";
			String[] valueArray = value.split("_");
			for (String val : valueArray) {
				if (val == null || value == "")
					continue;
				else if (val.length() == 1) {
					result += val.toUpperCase();
				} else {
					result += val.substring(0, 1).toUpperCase() + val.substring(1);
				}
			}
			return result.substring(0, 1).toLowerCase()+ result.substring(1);

		} else {
			return value.substring(0, 1)+ value.substring(1);
		}

	}
}

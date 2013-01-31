package com.storageroomapp.client.util;

import java.util.Calendar;

import org.json.simple.JSONObject;

/**
 * Parsing helpers for json-simple
 */
public class JsonSimpleUtil {
	
	/**
	 * Convenience method for pulling a String value off of a JSONObject
	 * @param obj the JSONObject received from the server
	 * @param key the String key of the value we want
	 * @return the String value, or null if not found
	 */
	static public String parseJsonStringValue(JSONObject obj, String key) {
		if ((obj == null) || (key == null)) {
			return null;
		}
		String value = null;
		Object valueObj = obj.get(key);
		if (valueObj != null) {
			value = valueObj.toString();
		}
		return value;
		
	}

	/**
	 * Convenience method for pulling a Integer value off of a JSONObject
	 * @param obj the JSONObject received from the server
	 * @param key the String key of the value we want
	 * @param defaultValue the Integer to return if a value is not found (can be null)
	 * @return the Integer value, or null if not found or not an int
	 */
	static public Integer parseJsonIntValue(JSONObject obj, String key, Integer defaultValue) {
		if ((obj == null) || (key == null)) {
			return defaultValue;
		}
		Integer value = defaultValue;
		Object valueObj = obj.get(key);
		if (valueObj != null) {
			String valueStr = valueObj.toString();
			try {
				value = Integer.parseInt(valueStr);
			} catch (NumberFormatException nfe) {
				// TODO log
				value = defaultValue;
			}
		}
		return value;
	}

	/**
	 * Convenience method for pulling a Float value off of a JSONObject
	 * @param obj the JSONObject received from the server
	 * @param key the String key of the value we want
	 * @param defaultValue the Float to return if a value is not found (can be null)
	 * @return the Float value, or null if not found or not a float
	 */
	static public Float parseJsonFloatValue(JSONObject obj, String key, Float defaultValue) {
		if ((obj == null) || (key == null)) {
			return defaultValue;
		}
		Float value = defaultValue;
		Object valueObj = obj.get(key);
		if (valueObj != null) {
			String valueStr = valueObj.toString();
			try {
				value = Float.parseFloat(valueStr);
			} catch (NumberFormatException nfe) {
				// TODO log
				value = defaultValue;
			}
		}
		return value;
		
	}
	
	/**
	 * Convenience method for pulling a Boolean value off of a JSONObject
	 * @param obj the JSONObject received from the server
	 * @param key the String key of the value we want
	 * @param defaultValue the Boolean to return if a value is not found (can be null)
	 * @return the Boolean value, or null if not found or not an boolean
	 */
	static public Boolean parseJsonBooleanValue(JSONObject obj, String key, Boolean defaultValue) {
		if ((obj == null) || (key == null)) {
			return defaultValue;
		}
		String value = null;
		Object valueObj = obj.get(key);
		if (valueObj != null) {
			value = valueObj.toString();
		}
		return "true".equals(value);
		
	}

	/**
	 * Convenience method for pulling a Calendar value off of a JSONObject
	 * @param obj the JSONObject received from the server
	 * @param key the String key of the value we want
	 * @return the Calendar value, or null if not found or not a proper timestamp
	 */
	static public Calendar parseJsonTimeValue(JSONObject obj, String key) {
		if ((obj == null) || (key == null)) {
			return null;
		}
		Calendar value = null;
		Object valueObj = obj.get(key);
		if (valueObj != null) {
			String valueStr = valueObj.toString();
			value = StorageRoomUtil.storageRoomTimeStringToCalendar(valueStr);
		}
		return value;
		
	}
	
	/**
	 * Convenience method for pulling a Calendar value off of a JSONObject
	 * @param obj the JSONObject received from the server
	 * @param key the String key of the value we want
	 * @return the Calendar value, or null if not found or not a proper timestamp
	 */
	static public Calendar parseJsonDateValue(JSONObject obj, String key) {
		if ((obj == null) || (key == null)) {
			return null;
		}
		Calendar value = null;
		Object valueObj = obj.get(key);
		if (valueObj != null) {
			String valueStr = valueObj.toString();
			value = StorageRoomUtil.storageRoomDateStringToCalendar(valueStr);
		}
		return value;
		
	}

}

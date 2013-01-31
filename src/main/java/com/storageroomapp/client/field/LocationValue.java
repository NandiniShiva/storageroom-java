package com.storageroomapp.client.field;

import org.json.simple.JSONObject;

import com.storageroomapp.client.util.JsonSimpleUtil;

public class LocationValue extends GenericValue<String>{
	protected LocationField parentField = null;
	public float latitude = 0;
	public float longitude = 0;

	public LocationValue() {
		this.parentField = null;
	}
	
	public LocationValue(LocationField parentField) {
		this.parentField = parentField;
	}

	public LocationValue(LocationField parentField, float lat, float lng) {
		this.parentField = parentField;
		this.latitude = lat;
		this.longitude = lng;
	}
	
	/*
	 * GET
	 {
  			"@type": "Location",
  			"lat": 12.3333,
  			"lng": -33.123
	 }
	 */
	static public LocationValue parseJSONFileObject(LocationField parentField, JSONObject jsonObj) {
		if (jsonObj == null) {
			return null;
		}
		Float lat = JsonSimpleUtil.parseJsonFloatValue(jsonObj, "lat", null);
		if (lat == null) {
			return null;
		}
		Float lng = JsonSimpleUtil.parseJsonFloatValue(jsonObj, "lng", null);
		if (lng == null) {
			return null;
		}
		
		LocationValue value = new LocationValue(parentField);
		value.latitude = lat;
		value.longitude = lng;
		return value;
	}

	public String toJSONString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{ \"lat\": \"");
		sb.append(latitude);
		sb.append("\", \"lng\": \"");
		sb.append(longitude);
		sb.append("\" }");
		return innerValue.toString();
	}
	
}

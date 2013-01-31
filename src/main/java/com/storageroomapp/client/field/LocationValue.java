/*
Copyright 2013 Peter Laird

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
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

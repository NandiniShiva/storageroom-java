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

public class LocationField extends GenericField<LocationValue> {

	static public final String STORAGEROOM_TYPE_NAME = "LocationField";

	public LocationField() {
		super(STORAGEROOM_TYPE_NAME);
		
		// all Fields must have an instantiated Value container
		this.value = new LocationValue();
		
		// Location objects are complex, labeled in SR as 'compound'
		this.isCompoundFieldType = true;
	}
	
	@Override
	protected LocationValue deserializeJsonValue(Object jsonValue) 	{
		if (jsonValue == null) {
			return new LocationValue();
		}
		
		JSONObject jsonObject = (JSONObject)jsonValue;
		LocationValue value = LocationValue.parseJSONFileObject(this, jsonObject); 
		return value;
	}

}

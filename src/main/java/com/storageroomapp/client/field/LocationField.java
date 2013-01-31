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

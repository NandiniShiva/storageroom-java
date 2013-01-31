package com.storageroomapp.client.field;

import org.json.simple.JSONObject;

public class FileField extends GenericField<FileValue> {

	static public final String STORAGEROOM_TYPE_NAME = "FileField";

	public FileField() {
		super(STORAGEROOM_TYPE_NAME);
		
		// all Fields must have an instantiated Value container
		this.value = new FileValue();
		
		// File objects are complex, labeled in SR as 'compound'
		this.isCompoundFieldType = true;
	}
	
	@Override
	protected FileValue deserializeJsonValue(Object jsonValue) 	{
		if (jsonValue == null) {
			return new FileValue();
		}
		
		JSONObject jsonObject = (JSONObject)jsonValue;
		FileValue value = FileValue.parseJSONObject(this, jsonObject); 
		return value;
	}

}

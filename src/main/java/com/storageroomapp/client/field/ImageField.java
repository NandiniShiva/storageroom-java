package com.storageroomapp.client.field;

import org.json.simple.JSONObject;


public class ImageField extends GenericField<ImageValue> {

	static public final String STORAGEROOM_TYPE_NAME = "ImageField";
	
	public ImageField() {
		super(STORAGEROOM_TYPE_NAME);

		// all Fields must have an instantiated Value container
		this.value = new ImageValue();
		
		// Image objects are complex, labeled in SR as 'compound'
		this.isCompoundFieldType = true;
	}
	
	@Override
	protected ImageValue deserializeJsonValue(Object jsonValue) 	{
		if (jsonValue == null) {
			return new ImageValue();
		}
		
		JSONObject jsonObject = (JSONObject)jsonValue;
		ImageValue value = ImageValue.parseJSONObject(this, jsonObject); 
		return value;
	}
}

package com.storageroomapp.client.field;

public class StringField extends GenericField<StringValue> {

	static public final String STORAGEROOM_TYPE_NAME = "StringField";

	public StringField() {
		super(STORAGEROOM_TYPE_NAME);
		
		// all Fields must have an instantiated Value container
		this.value = new StringValue();
	}
	
	@Override
	protected StringValue deserializeJsonValue(Object jsonValue) 	{
		if (jsonValue == null) {
			return new StringValue();
		}
		String valueStr = jsonValue.toString();
		return new StringValue(valueStr);
	}

}

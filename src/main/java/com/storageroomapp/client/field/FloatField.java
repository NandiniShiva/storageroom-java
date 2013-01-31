package com.storageroomapp.client.field;

public class FloatField extends GenericField<FloatValue> {

	static public final String STORAGEROOM_TYPE_NAME = "FloatField";

	public FloatField() {
		super(STORAGEROOM_TYPE_NAME);
		
		// all Fields must have an instantiated Value container
		this.value = new FloatValue();
	}
	
	@Override
	protected FloatValue deserializeJsonValue(Object jsonValue) 	{
		if (jsonValue == null) {
			return new FloatValue();
		}
		Number valueNum = (Number)jsonValue;
		Float valueFloat = valueNum.floatValue();
		return new FloatValue(valueFloat);
	}

}

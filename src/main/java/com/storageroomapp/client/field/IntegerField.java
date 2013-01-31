package com.storageroomapp.client.field;

public class IntegerField extends GenericField<IntegerValue> {

	static public final String STORAGEROOM_TYPE_NAME = "IntegerField";

	public IntegerField() {
		super(STORAGEROOM_TYPE_NAME);
		
		// all Fields must have an instantiated Value container
		this.value = new IntegerValue();
	}
	
	@Override
	protected IntegerValue deserializeJsonValue(Object jsonValue) 	{
		if (jsonValue == null) {
			return new IntegerValue();
		}
		Number valueNum = (Number)jsonValue;
		Integer valueInt = valueNum.intValue();
		return new IntegerValue(valueInt);
	}

}

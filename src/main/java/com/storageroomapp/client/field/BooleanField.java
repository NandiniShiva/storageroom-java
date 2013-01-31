package com.storageroomapp.client.field;

public class BooleanField extends GenericField<GenericValue<Boolean>> {

	static public final String STORAGEROOM_TYPE_NAME = "BooleanField";
	
	public BooleanField() {
		super(STORAGEROOM_TYPE_NAME);		
		
		// all Fields must have an instantiated Value container
		this.value = new BooleanValue();
	}
	
	@Override
	protected BooleanValue deserializeJsonValue(Object jsonValue) 	{
		if (jsonValue == null) {
			return new BooleanValue();
		}
		Boolean valueBool = (Boolean)jsonValue;
		return new BooleanValue(valueBool);
	}

}

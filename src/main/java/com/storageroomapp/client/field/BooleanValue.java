package com.storageroomapp.client.field;

public class BooleanValue extends GenericValue<Boolean>{

	public BooleanValue() {
		this.innerValue = null;
	}
	
	public BooleanValue(Boolean value) {
		this.innerValue = value;
	}
}

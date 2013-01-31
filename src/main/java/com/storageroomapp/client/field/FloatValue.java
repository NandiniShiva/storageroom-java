package com.storageroomapp.client.field;

public class FloatValue extends GenericValue<Float>{

	public FloatValue() {
		this.innerValue = null;
	}
	
	public FloatValue(Float value) {
		this.innerValue = value;
	}
}

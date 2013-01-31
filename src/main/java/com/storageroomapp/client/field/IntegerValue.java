package com.storageroomapp.client.field;

public class IntegerValue extends GenericValue<Integer>{

	public IntegerValue() {
		this.innerValue = null;
	}
	
	public IntegerValue(Integer value) {
		this.innerValue = value;
	}
}

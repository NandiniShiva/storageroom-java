package com.storageroomapp.client.field;

public class StringValue extends GenericValue<String>{

	public StringValue() {
		this.innerValue = null;
	}
	
	public StringValue(String value) {
		this.innerValue = value;
	}
}

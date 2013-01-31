package com.storageroomapp.client.field;

public class GenericValue<V> {
	public V innerValue = null;
	
	public V getInnerValue() {
		return innerValue;
	}
	
	public String toString() {
		if (innerValue == null) {
			return null;
		}
		return innerValue.toString();
	}
	
	/**
	 * Writes the field value into a json payload. This method is
	 * critical in the code paths that involve inserting or 
	 * updating Entry objects. This is the code that correctly
	 * inserts each field value into the JSON.
	 * <p>
	 * Note, the compound field types must override this method to write
	 * their unique structures into the json (file, image, location).
	 * @return
	 */
	public String toJSONString() {
		// some subclasses override this default behavior which
		// is to just output the innerValue
		return "\""+this.toString()+"\"";
	}
}

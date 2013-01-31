/*
Copyright 2013 Peter Laird

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
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

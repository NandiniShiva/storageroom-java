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

import java.util.Calendar;

import com.storageroomapp.client.util.StorageRoomUtil;

public class DateValue extends GenericValue<Calendar>{

	public DateValue() {
		this.innerValue = null;
	}

	public DateValue(Calendar value) {
		this.innerValue = value;
	}
	
	public String toString() {
		if (innerValue == null) {
			return null;
		}
		return StorageRoomUtil.calendarToStorageRoomDateString(innerValue);
	}
	
}

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

public class TimeField extends GenericField<TimeValue> {

	static public final String STORAGEROOM_TYPE_NAME = "TimeField";

	public TimeField() {
		super(STORAGEROOM_TYPE_NAME);
		
		// all Fields must have an instantiated Value container
		this.value = new TimeValue();
	}
	
	@Override
	protected TimeValue deserializeJsonValue(Object jsonValue) 	{
		if (jsonValue == null) {
			return new TimeValue();
		}
		String valueStr = jsonValue.toString();
		Calendar valueCal = StorageRoomUtil.storageRoomTimeStringToCalendar(valueStr);
		return new TimeValue(valueCal);
	}

	@Override
	public String toString() {
		if (value == null) {
			return null;
		}
		Calendar calValue = value.innerValue;
		if (calValue == null) {
			return null;
		}
		String calString = StorageRoomUtil.calendarToStorageRoomTimeString(calValue);
		return this.identifier+" ["+calString+"] ";
	}
}

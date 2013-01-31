package com.storageroomapp.client.field;

import java.util.Calendar;

import com.storageroomapp.client.util.StorageRoomUtil;

public class TimeValue extends GenericValue<Calendar>{

	public TimeValue() {
		this.innerValue = null;
	}
	
	public TimeValue(Calendar value) {
		this.innerValue = value;
	}
	
	public String toString() {
		if (innerValue == null) {
			return null;
		}
		return StorageRoomUtil.calendarToStorageRoomTimeString(innerValue);
	}
	
}

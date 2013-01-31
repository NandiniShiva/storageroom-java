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

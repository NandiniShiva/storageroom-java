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

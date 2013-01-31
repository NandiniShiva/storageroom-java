package com.storageroomapp.client.field;

import java.util.Calendar;

import com.storageroomapp.client.util.StorageRoomUtil;

public class DateField extends GenericField<DateValue> {

	static public final String STORAGEROOM_TYPE_NAME = "DateField";

	public DateField() {
		super(STORAGEROOM_TYPE_NAME);

		// all Fields must have an instantiated Value container
		this.value = new DateValue();
	}
	
	@Override
	protected DateValue deserializeJsonValue(Object jsonValue) 	{
		if (jsonValue == null) {
			return new DateValue();
		}
		String valueStr = jsonValue.toString();
		Calendar valueCal = StorageRoomUtil.storageRoomDateStringToCalendar(valueStr);
		if (valueCal == null) {
			return new DateValue();
		}
		DateValue dateValue = new DateValue(valueCal);
		return dateValue;
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
		String calString = StorageRoomUtil.calendarToStorageRoomDateString(calValue);
		return this.identifier+" ["+calString+"] ";
	}
}

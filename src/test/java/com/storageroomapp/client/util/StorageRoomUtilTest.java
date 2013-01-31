package com.storageroomapp.client.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Calendar;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Test;

import com.storageroomapp.client.util.StorageRoomUtil;

public class StorageRoomUtilTest {

	@Test
	public void testDecorateUrl() {
		String authToken = "myauthtoken888888";
		String baseUrl = "http://google.com/test";
		
		String decoratedUrl = StorageRoomUtil.decorateUrl(baseUrl, authToken, true, null);
		String expected = baseUrl+".json"+"?auth_token="+authToken;
		assertEquals("URL decoration for StorageRoom API calls has changed. Please update this test.", 
				expected, decoratedUrl);
	}

	@Test
	public void testTimeParsingUtils() {
		String timeStr = "2013-01-10T21:17:00Z";
		Calendar srCal = StorageRoomUtil.storageRoomTimeStringToCalendar(timeStr);
		String roundtripStr = StorageRoomUtil.calendarToStorageRoomTimeString(srCal);
		assertEquals("Something is broken with StorageRoom time parsing. Round tripping of dates into and out of the String format is not correct."
				,timeStr, roundtripStr);
	}

	@Test
	public void testTimeParsingFromJson() {
		String timeStr = "2013-01-10T21:17:00Z";
		String jsonStr = "{ \"mytime\": \""+timeStr+"\" }";
		JSONObject jsonObj = (JSONObject)JSONValue.parse(jsonStr);
		Calendar srCal = JsonSimpleUtil.parseJsonTimeValue(jsonObj, "mytime");
		String roundtripStr = StorageRoomUtil.calendarToStorageRoomTimeString(srCal);
		assertEquals("Something is broken with StorageRoom time parsing from JSON. Round tripping of dates into and out of the String format is not correct.",
				timeStr, roundtripStr);
	}

	@Test
	public void testDateParsingUtils() {
		String dateStr = "2013-01-10";
		Calendar srCal = StorageRoomUtil.storageRoomDateStringToCalendar(dateStr);
		String roundtripStr = StorageRoomUtil.calendarToStorageRoomDateString(srCal);
		assertEquals("Something is broken with StorageRoom date parsing. Round tripping of dates into and out of the String format is not correct."
				,dateStr, roundtripStr);
	}

	@Test
	public void testDateParsingFromJson() {
		String dateStr = "2013-01-10";
		String jsonStr = "{ \"mydate\": \""+dateStr+"\" }";
		JSONObject jsonObj = (JSONObject)JSONValue.parse(jsonStr);
		Calendar srCal = JsonSimpleUtil.parseJsonDateValue(jsonObj, "mydate");
		String roundtripStr = StorageRoomUtil.calendarToStorageRoomDateString(srCal);
		assertEquals("Something is broken with StorageRoom date parsing from JSON. Round tripping of dates into and out of the String format is not correct.",
				dateStr, roundtripStr);
	}
	
	@Test
	public void testStringParsingFromJson() {
		String testStr = "We all love StorageRoom!";
		String jsonStr = "{ \"mystring\": \""+testStr+"\" }";
		JSONObject jsonObj = (JSONObject)JSONValue.parse(jsonStr);
		String roundtripStr = JsonSimpleUtil.parseJsonStringValue(jsonObj, "mystring");
		assertEquals("Something is broken with StorageRoom string parsing from JSON. Round tripping of string into and out of the String format is not correct.",
				testStr, roundtripStr);
	}

	@Test
	public void testMissingStringParsingFromJson() {
		String testStr = "We all love StorageRoom!";
		String jsonStr = "{ \"mystring\": \""+testStr+"\" }";
		JSONObject jsonObj = (JSONObject)JSONValue.parse(jsonStr);
		String roundtripStr = JsonSimpleUtil.parseJsonStringValue(jsonObj, "missingkey");
		assertNull("We asked for a missing key in the JSON payload, we should have gotten null when asking for the value",
				roundtripStr);
	}
}

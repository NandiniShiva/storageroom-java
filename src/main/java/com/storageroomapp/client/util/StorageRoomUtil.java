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
package com.storageroomapp.client.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Class provides various utilities and constants useful when interacting with
 * the StorageRoom REST API
 */
public class StorageRoomUtil {
	static private Log log = LogFactory.getLog(StorageRoomUtil.class);

	// API UTILITIES
	
	/**
	 * Core path from which all API paths are based. This should only be used
	 * during the initial request, and hypermedia used thereafter
	 */
	static public final String URL_BASE = "api.storageroomapp.com/accounts/";
	
	/**
	 * Marks up a url with necessary query parameters
	 * 
	 * @param url	the String unadorned API url
	 * @param authToken the String authToken for this 'application'
	 * @param jsonRepresentation a boolean, true if JSON is desired; should be 'true' unless you know otherwise
	 * @param extraQueryParams a String concatenation of extra query params to tack onto the url ; do not prepend with &
	 * @return the adorned url
	 */
	static public String decorateUrl(String url, String authToken, boolean jsonRepresentation,
			String extraQueryParams) {
		if ((url == null) || (authToken == null)) {
			return null;
		}
		if (jsonRepresentation) {
			url = url+".json";
		}
		url = url+"?auth_token="+authToken;
		
		if (extraQueryParams != null) {
			url = url + "&" + extraQueryParams;
		}
		 
		return url;
	}

	
	// TIME AND DATE
	
	/**
	 * Serializes a Calendar as a StorageRoom time String
	 * @param cal the Calendar
	 * @return the serialized String
	 */
	static public String calendarToStorageRoomTimeString(Calendar cal) {
		initFormatters();
		String date = timeFieldFormatter.format(cal.getTime());
		return date;
	}
	
	/**
	 * Converts a StorageRoom time String into a Java Calendar
	 * @param str the String time field
	 * @return the Calendar, or null if not a valid time String
	 */
	static public Calendar storageRoomTimeStringToCalendar(String str) {
		initFormatters();
		Calendar cal=Calendar.getInstance(gmt);
		try {
			Date date = timeFieldFormatter.parse(str);
			cal.setTime(date);
		} catch (Exception e) {
			log.error("Error deserializing a StorageRoom time field", e);
		}
		return cal;
	}

	/**
	 * Serializes a Calendar as a StorageRoom date String
	 * @param cal the Calendar
	 * @return the serialized String
	 */
	static public String calendarToStorageRoomDateString(Calendar cal) {
		initFormatters();
		String date = dateFieldFormatter.format(cal.getTime());
		return date;
	}
	
	/**
	 * Converts a StorageRoom date String into a Java Calendar
	 * @param str the String date field
	 * @return the Calendar, or null if not a valid date String
	 */
	static public Calendar storageRoomDateStringToCalendar(String str) {
		initFormatters();
		Calendar cal=Calendar.getInstance(gmt);
		try {
			Date date = dateFieldFormatter.parse(str);
			cal.setTime(date);
		} catch (Exception e) {
			log.error("Error deserializing a StorageRoom date field", e);
		}
		return cal;
	}
	
	static private SimpleDateFormat timeFieldFormatter = null;
	static private SimpleDateFormat dateFieldFormatter = null;
	static private TimeZone gmt = null;
	static private boolean initialized = false;
	
	static private void initFormatters() {
		if (!initialized) {
			gmt = TimeZone.getTimeZone("GMT");
			timeFieldFormatter = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ssX");
			timeFieldFormatter.setTimeZone(gmt);
			dateFieldFormatter = new SimpleDateFormat("yyyy-MM-dd");
			dateFieldFormatter.setTimeZone(gmt);
			
			initialized = true;
		}
	}
}

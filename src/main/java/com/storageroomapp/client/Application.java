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
package com.storageroomapp.client;

import java.util.HashMap;
import java.util.Map;
import com.storageroomapp.client.util.Http;
import com.storageroomapp.client.util.StorageRoomUtil;

/**
 * Application is the parent object to all interaction within the Java StorageRoom API.
 * <p>
 * To start working with StorageRoom, you must call getInstance() with your application
 * name. 
 */
public class Application {

	// Identity
	private String name = null;
	private String accountId = null; // e.g. "50ef2fcc0f6602017a000787";
	private String authToken = null; // e.g. "9ixzsJb77axXhRdgL3Tk";
	static protected Map<String, Application> apps = new HashMap<String, Application>();

	// Children
	protected AccountInfo accountInfo = null;
	protected Collections collections = null;
	
	private Application(String name) {
		this.name = name;
	}
	
	/**
	 * Obtains an instance of Application to start working with the API. The
	 * passed name should match the name you gave your application in the 
	 * StorageRoom UI.
	 * <p>
	 * It is fully supported to use two different StorageRoom application
	 * keys at the same time using this library. Simply use distinct names
	 * when calling getInstance().
	 * <p>
	 * Note, if this application has been retrieved previously, and connected,
	 * the returned object here will be in the connected state.
	 *  
	 * @param name the String name of the application
	 * @return an Application object which may or may not be connected depending
	 * if the Application has been used before in this JVM
	 */
	static public Application getInstance(String name) {
		if (name == null) {
			return null;
		}
		Application app = apps.get(name);
		if (app == null) {
			app = new Application(name);
			apps.put(name, app);
		}
		return app;
	}
	
	/**
	 * Returns the name of this application as reported by the StorageRoom
	 * API.
	 * @return the String name, or null if this Application has never connected
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the accountId of this application.
	 * 
	 * @return the String id, or null if this Application has never connected
	 */
	public String getAccountId() {
		return accountId;
	}
	
	/**
	 * Returns the authToken used by this application.
	 * 
	 * @return the String token, or null if this Application has never connected
	 */
	public String getAuthToken() {
		return authToken;
	}
	
	/**
	 * Returns the AccountInfo returned during connect()
	 * 
	 * @param useCache if false, the call will always trigger a connect() call
	 * @return the AccountInfo object, or null if it cannot be obtained
	 */
	public AccountInfo getAccountInfo(boolean useCache) {
		if (useCache && (accountInfo != null)) {
			return accountInfo;
		}
		if ((accountId == null) || (authToken == null)) {
			return null;
		}
		
		connect(accountId, authToken, useCache);
		return accountInfo;
	}
	
	/**
	 * Makes a live call to the StorageRoom API. This will populate the 
	 * AccountInfo object associated with this Application, but only if 
	 * the passed accountId+authToken pair are valid, and only if the
	 * StorageRoom API is network accessible.
	 * <p>
	 * SERVER ROUND TRIP: this is a live API call
	 * 
	 * @param accountId the String accountId as provided by the SR GUI
	 * @param authToken the String accountToken as provided by the SR GUI
	 * @param useCache if false, the SR API call will be made even if already
	 * connected
	 * @return true if the connect() attempt succeeded, false if not
	 */
	public boolean connect(String accountId, String authToken, boolean useCache) {
		if (useCache && (accountInfo != null)) {
			// we are already connected
			return true;
		}
		
		// this is the root request, so we need to manually build up the URL
		// the rest of the URLs come via hypermedia
		String url = "http://"+StorageRoomUtil.URL_BASE+accountId;
		url = StorageRoomUtil.decorateUrl(url, authToken, true, null);
		
		// make the request, it will return null for all bad reasons (no internet, bad credentials, etc)
		String accountInfoJson = Http.getAsString(url);
		if (accountInfoJson == null) {
			
			// we will leave the cached account info alone, if one exists
			// accountInfo = null;
			
			return false;
		}
		AccountInfo newAccountInfo = AccountInfo.parseJson(accountInfoJson);
		if (newAccountInfo == null) {
			return false;
		}
		accountInfo = newAccountInfo;
		
		// ok, we made it through successfully, cache the credentials as they are valid
		this.accountId = accountId;
		this.authToken = authToken;
		
		return true;
	}	
	
	/**
	 * Retrieves the list of Collections visible to this
	 * Application for this Account.
	 * 
	 * @param useCache if false, the SR API call will be made even if the collections
	 * are already cached
	 * @return the Collections object or null if a failure occurred
	 */
	public Collections getCollections(boolean useCache) {
		if (useCache && (collections != null)) {
			return collections;
		}
		
		getAccountInfo(useCache);
		if (accountInfo != null) {
			String url = StorageRoomUtil.decorateUrl(accountInfo.collectionsUrl, authToken, true, null);
			String collectionsJson = Http.getAsString(url);
			collections = Collections.parseJson(this, collectionsJson);
		}
		
		return collections;
	}
		
}

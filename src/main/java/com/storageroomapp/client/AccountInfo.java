package com.storageroomapp.client;

import java.util.Calendar;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.storageroomapp.client.util.JsonSimpleUtil;

/**
 * Represents the Account object in StorageRoom. See the wiki on how
 * the Account and Application objects relate, as it may not be obvious.
 * 
 * @author plaird
 */
public class AccountInfo {
	
	// NOTE there is a bidirectional relationship between Account and Application in the live API. 
	// Accounts own one or more Applications. However, an Account is not accessible outside of the
	// context of an Application. We have chosen to make Application the master in the data model, 
	// with an Account being subordinate.
	//
	
	// Looking for AccountId? 
	// Another thing that may not be obvious is accountId is owned by Application 
	// for programming convenience.

	protected  String name = "undefined";
	protected  String subdomain = "undefined";
	protected  Calendar created = null;
	protected  Calendar updated = null;
	
	// Hypermedia
	protected String collectionsUrl = null; // e.g. "http://api.storageroomapp.com/accounts/50ef2fcc0f6602017a000787/collections"
	protected String deletedEntriesUrl = null; // e.g. "http://api.storageroomapp.com/accounts/50ef2fcc0f6602017a000787/collections"
	
	/**
	 * Constructor. Since StorageRoom does not support creating Accounts through the API,
	 * this is protected and can only be created via deserializing json.
	 */
	protected AccountInfo() {
	}
	
	/*
	{
	  "account":
	    {
	      "@type":"Account",
	      "@url":"http://api.storageroomapp.com/accounts/50ef2fcc0f6602017a000787",
	      "@collections_url":"http://api.storageroomapp.com/accounts/50ef2fcc0f6602017a000787/collections",
	      "@deleted_entries_url":"http://api.storageroomapp.com/accounts/50ef2fcc0f6602017a000787/deleted_entries",
	      "@created_at":"2013-01-10T21:17:00Z",
	      "@updated_at":"2013-01-10T21:17:00Z",
	      "@version":1,
	      "name":"storageroomclient",
	      "subdomain":"storageroomclient"
	    }
	}
    */

	/**
	 * Parses a String of json text and returns an AccountInfo object.
	 * It will correctly parse the AccountInfo object if it is toplevel,
	 * or also if nested in an 'account' key-value pair.
	 * 
	 * @param json the String with the json text
	 * @return an AccountInfo object, or null if the parsing failed
	 */
	static public AccountInfo parseJson(String json) {
		JSONObject root = (JSONObject)JSONValue.parse(json);
		if (root == null) {
			return null;
		}
		JSONObject account = (JSONObject)root.get("account");
		if (account == null) {
			return null;
		}
		return parseJsonObject(account);
	}

	/**
	 * Unmarshalls an AccountInfo object from a JSONObject. This method
	 * assumes the name-values are immediately attached to the passed object
	 * and not nested under a key (e.g. 'account')
	 * @param jsonObj the JSONObject
	 * @return an AccountInfo object, or null if the unpacking failed
	 */
	static public AccountInfo parseJsonObject(JSONObject jsonObj) {
		AccountInfo accountInfo = new AccountInfo();
		
		// REQUIRED PROPERTIES
		accountInfo.name = JsonSimpleUtil.parseJsonStringValue(jsonObj, "name");
		if (accountInfo.name == null) {
			return null;
		}
		accountInfo.collectionsUrl = JsonSimpleUtil.parseJsonStringValue(jsonObj, "@collections_url");
		if (accountInfo.collectionsUrl == null) {
			return null;
		}
	
		// OPTIONAL PROPERTIES (BUT SHOULD BE THERE)
		accountInfo.subdomain = JsonSimpleUtil.parseJsonStringValue(jsonObj, "subdomain");
		accountInfo.deletedEntriesUrl = JsonSimpleUtil.parseJsonStringValue(jsonObj, "@deleted_entries_url");
		accountInfo.created = JsonSimpleUtil.parseJsonTimeValue(jsonObj, "@created_at");
		accountInfo.updated = JsonSimpleUtil.parseJsonTimeValue(jsonObj, "@updated_at");
		
		return accountInfo;
	}
	
	// GETTERS
	
	/**
	 * Returns the application name, as defined by the admin in the 
	 * Application editor.
	 * @return the String name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the assigned subdomain for the application, which appears
	 * to be roughly the lowercasing (and url encoding?) of the application
	 * name. It is used to prefix all UI urls.
	 * 
	 * @return the String subdomain
	 */
	public String getSubdomain() {
		return subdomain;
	}
	
	/**
	 * Returns the created time and date of the account.
	 * @return the Calendar date and time
	 */
	public Calendar getCreated() {
		return created;
	}
	
	/**
	 * Returns the last updated time and date of the account.
	 * @return the Calendar date and time
	 */
	public Calendar getUpdated() {
		return updated;
	}
	
	
}


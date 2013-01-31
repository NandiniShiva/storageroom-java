package com.storageroomapp.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.storageroomapp.client.util.Http;
import com.storageroomapp.client.util.JsonSimpleUtil;
import com.storageroomapp.client.util.StorageRoomUtil;

/**
 * Encapsulates a page of live results from a query to the StorageRoom
 * API. It is initially obtained via the query methods on the CollectionEntries
 * object. Navigating to other pages of the results is done with the 
 * jumpPage() method on this object.
 *
 */
public class PageOfEntries  {
	
	// INPUT into server
	protected Collection parentCollection = null;
	protected CollectionQuery queryOptions = null;
	protected int currentPage = 0;

	// OUTPUT from server
	protected int numResultsPages = 0;
	protected int currentPageSize = 0;
	protected List<Entry> currentPageEntries = null;
	
	// ITERATOR
	protected int iteratorIndex = 0;

	// CONSTRUCTION

	/**
	 * Parses a String of json text and returns an PageOfEntries object.
	 * It will correctly parse the PageOfEntries object if it is toplevel,
	 * or also if nested in an 'array' key-value pair.
	 * 
	 * @param json the String with the json text
	 * @return an PageOfEntries object, or null if the parsing failed
	 */
	static public PageOfEntries parseJSON(Collection parent, String json) {
		if (json == null) {
			return null;
		}
		JSONObject jsonObj = (JSONObject)JSONValue.parse(json);
		if (jsonObj == null) {
			return null;
		}
		JSONObject jsonArrayObj = (JSONObject)jsonObj.get("array");
		if (jsonArrayObj != null) {
			jsonObj = jsonArrayObj;
		}
		
		return parseJSONObject(parent, jsonObj);
	}

	/**
	 * Unmarshalls an PageOfEntries object from a JSONObject. This method
	 * assumes the name-values are immediately attached to the passed object
	 * and not nested under a key (e.g. 'array')
	 * @param jsonObj the JSONObject
	 * @return an PageOfEntries object, or null if the unpacking failed
	 */
	static public PageOfEntries parseJSONObject(Collection parent, JSONObject jsonObject) {
		if (jsonObject == null) {
			return null;
		}
		JSONArray jsonArray = (JSONArray)jsonObject.get("resources");
		if (jsonArray == null) {
			return null;
		}
		
		PageOfEntries el = new PageOfEntries();
		el.currentPage = JsonSimpleUtil.parseJsonIntValue(jsonObject, "@page", 0);
		el.currentPageSize = JsonSimpleUtil.parseJsonIntValue(jsonObject, "@total_resources", 20);
		el.numResultsPages = JsonSimpleUtil.parseJsonIntValue(jsonObject, "@pages", 1);
		
		el.currentPageEntries = new ArrayList<Entry>();
		@SuppressWarnings("unchecked")
		Iterator<JSONObject> iter = jsonArray.iterator();
		while (iter.hasNext()) {
			JSONObject entryObj = iter.next();
			Entry entry = Entry.parseJSONObject(parent, entryObj);
			if (entry != null) {
				el.currentPageEntries.add(entry);
			}
		}
		
		return el;
	}
	
	// OPERATIONS
	
	/**
	 * Performs the actual query against the SR API. This is a protected method,
	 * it is accessed via jumpPage() on this object or via the CollectionEntries
	 * query methods.
	 * <p>
	 * SERVER ROUND TRIP: this is a live API call
	 * 
	 * @param parent the Collection
	 * @param query the query definition
	 * @param page the page to retrieve, 0 for the first page
	 * @return the PageOfEntries result, or null if a problem occurred
	 */
	static protected PageOfEntries doLiveQuery(Collection parent, CollectionQuery query, int page) {
		
		if (parent == null) {
			return null;
		}
		
		Application pa = parent.getParentApplication();
		String entriesUrl = parent.getEntriesUrl();
		
		String extraQueryParams = null;
		if (query != null) {
			extraQueryParams = query.generateQueryString(page);
		}
		
		String queryUrl = StorageRoomUtil.decorateUrl(entriesUrl, pa.getAuthToken(), 
				true, extraQueryParams);
		
		String results = Http.getAsString(queryUrl);
		if (results == null) {
			return null;
		}
		PageOfEntries el = PageOfEntries.parseJSON(parent, results);
		if (el == null) {
			return null;
		}
		el.parentCollection = parent;
		el.queryOptions = query;
		el.currentPage = page;
		
		return el;
	}

	/**
	 * Jump to a new page in the results, using the same query
	 * <p>
	 * SERVER ROUND TRIP: this is a live API call
	 * 
	 * @param newPage the new page in the results to fetch
	 * @return the PageOfEntries result, or null if a problem occurred
	 */
	public PageOfEntries jumpPage(int newPage) {
		return doLiveQuery(this.parentCollection, this.queryOptions, newPage);
	}
	
	// GETTERS
	
	public Collection getParentCollection() {
		return parentCollection;
	}

	public CollectionQuery getQueryOptions() {
		return queryOptions;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getNumResultsPages() {
		return numResultsPages;
	}

	public int getCurrentPageSize() {
		return currentPageSize;
	}
	
	/**
	 * Returns the list of Entry objects as an Iterable
	 * @return the Iterable, or null if no entries exist
	 */
	public Iterable<Entry> asIterable() {
		if (currentPageEntries == null) {
			return null;
		}
		return java.util.Collections.unmodifiableList(currentPageEntries);
	}
	
	/**
	 * Returns the list of Entry objects as a List
	 * @return the List, or null if no entries exist
	 */
	public List<Entry> asList() {
		if (currentPageEntries == null) {
			return null;
		}
		return java.util.Collections.unmodifiableList(currentPageEntries);
	}
}

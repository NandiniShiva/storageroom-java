package com.storageroomapp.client;

import com.storageroomapp.client.field.Fields;
import com.storageroomapp.client.util.Http;
import com.storageroomapp.client.util.StorageRoomUtil;

/**
 * CollectionEntries encapsulate the entries associated with a Collection.
 * This object provides useful operations across the entries in a Collection,
 * such as querying.
 */
public class CollectionEntries {
	protected Collection parentCollection = null;
	
	protected CollectionEntries(Collection parent) {
		this.parentCollection = parent;
	}

	/**
	 * Returns a page of results for this Collection using
	 * no filtering. Paging through the results is done
	 * on the returned PageOfEntries object.
	 * <p>
	 * SERVER ROUND TRIP: this is a live API call
	 * 
	 * @return the PageOfEntries first page of the results, or null
	 *  if there was a problem.
	 */
	public PageOfEntries queryAll() {
		if (parentCollection == null) {
			return null;
		}
		PageOfEntries el = PageOfEntries.doLiveQuery(parentCollection, null, 0);
		return el;
	}
	
	/**
	 * Returns a page of results for this Collection using
	 * the specified filtering. Paging through the results is done
	 * on the returned PageOfEntries object.
	 * <p>
	 * SERVER ROUND TRIP: this is a live API call
	 * 
	 * @param queryDef defines the filtering for the query
	 * @return the PageOfEntries first page of the results, or null
	 *  if there was a problem.
	 */
	public PageOfEntries query(CollectionQuery queryDef) {
		if (parentCollection == null) {
			return null;
		}
		PageOfEntries el = PageOfEntries.doLiveQuery(parentCollection, queryDef, 0);
		return el;
	}
	
	/**
	 * Adds an entry to the Collection on the server. The passed
	 * Entry object should have been first retrieved from the 
	 * createNewEntryTemplateObject() method on this CollectionEntries
	 * object.
	 * <p>
	 * SERVER ROUND TRIP: this is a live API call
	 * 
	 * @param newEntry the populated Entry object
	 * @return
	 */
	public boolean insertNewEntry(Entry newEntry) {
		boolean success = false;
		
		// we will only accept new entries associated with this collection
		if (!newEntry.parentCollection.equals(this.parentCollection)) {
			return false;
		}

		Application application = parentCollection.getParentApplication();
		String insertUrl = parentCollection.getEntriesUrl();
		insertUrl = StorageRoomUtil.decorateUrl(insertUrl, application.getAuthToken(), true, null);
		
		String postBody = newEntry.toJSONString(true);
		if (postBody != null) {
			success = Http.post(insertUrl, postBody);
		}
		return success;
	}

	/**
	 * Returns a template Entry for this Collection that has
	 * blank Field metadata objects ready for definition by your code.
	 * <p>
	 * Once the Entry is ready for publishing, invoke the 
	 * insertNewEntry() method on this CollectionEntries object.
	 * 
	 * @return the blank Entry object with the proper metadata
	 */
	public Entry createNewEntryTemplateObject() {
		Entry newEntry = new Entry(parentCollection);
		
		Fields fields = parentCollection.getFields();
		fields.addMissingDataFieldsToEntry(newEntry);
		
		return newEntry;
	}
	
}

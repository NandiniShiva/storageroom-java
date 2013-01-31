package com.storageroomapp.client;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.storageroomapp.client.util.JsonSimpleUtil;

public class Collections extends ArrayList<Collection> {
	static private final long serialVersionUID = 1L;

	protected Application parentApplication = null;
	protected String url = null;

	// where are the children Collection objects?
	// remember, this class extends List of Collection items, so e.g. get(i) will get a child Collection
	// List<Collection> children; <-- this is unnecessary
	
	
	/**
	 * An Application/Account only has one set of Collections, therefore this
	 * object is always created as a result of parsing returning json upon
	 * connection. Therefore this method is protected.
	 * 
	 * @param parent
	 */
	protected Collections(Application parent) {
		this.parentApplication = parent;
	}

	/*
	{"array":
	 {
	  "@url":"http://api.storageroomapp.com/accounts/50ef2fcc0f6602017a000787/collections",
	  "@type":"Array",
	  "resources":
	  [
	   {  
	  	  SEE COLLECTION CLASS JSON
   	   },
	   {
	  	  SEE COLLECTION CLASS JSON
	   }
	  ]
	 }
   }
*/
	
	/**
	 * Parses a String of json text and returns a Collections object.
	 * It will correctly parse the Collections object if it is toplevel,
	 * or also if nested in an 'array' key-value pair.
	 * 
	 * @param parent the Application object associated with the json
	 * @param json the String with the json text
	 * @return an Collections object, or null if the parsing failed
	 */
	static public Collections parseJson(Application parent, String json) {
		JSONObject root = (JSONObject)JSONValue.parse(json);
		if (root == null) {
			return null;
		}
		
		// unwrap the collections object if it is a value on key 'array'
		JSONObject rootArray = (JSONObject)root.get("array");
		if (rootArray != null) {
			root = rootArray;
		}
		return parseJsonObject(parent, root);
	}
	
	/**
	 * Unmarshalls an Collections object from a JSONObject. This method
	 * assumes the name-values are immediately attached to the passed object
	 * and not nested under a key (e.g. 'array')
	 *
	 * @param parent the Collections object associated with the json
	 * @param jsonObj the JSONObject
	 * @return an Collections object, or null if the unpacking failed
	 */
	static public Collections parseJsonObject(Application parent, JSONObject jsonObj) {
		Collections colls = new Collections(parent);
		colls.url = JsonSimpleUtil.parseJsonStringValue(jsonObj, "@url");
		if (colls.url == null) {
			return null;
		}
		
		JSONArray collsArray = (JSONArray)jsonObj.get("resources");
		@SuppressWarnings("unchecked")
		Iterator<JSONObject> collsIter = (Iterator<JSONObject>)collsArray.listIterator();
		while (collsIter.hasNext()) {
			JSONObject collJson = collsIter.next();
			Collection collection = Collection.parseJsonObject(parent, collJson);
			if (collection != null) {
				colls.add(collection);
			}
		}
		return colls;
	}

	// GETTERS
	
	/**
	 * Gets the hypermedia url associated with this collection.
	 * @return the String url, which should always exist
	 */
	public String getUrl() {
		return url;
	}
	
	// OPERATIONS
	
	/**
	 * A convenience method for finding a child Collection by name for
	 * this Application/Account
	 * @param name the String name of the collection as given in the SR UI
	 * @return the Collection object, or null if not found
	 */
	public Collection findCollection(String name) {
		if (name == null) {
			return null;
		}
		for (Collection col : this) {
			if (name.equals(col.getName())) {
				return col;
			}
		}
		return null;
	}
	
}

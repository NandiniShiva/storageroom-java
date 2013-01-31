package com.storageroomapp.client;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.storageroomapp.client.field.Fields;
import com.storageroomapp.client.util.JsonSimpleUtil;

/**
 * Represents the Collection concept in StorageRoom.
 * <p>
 * The usual way of obtaining a reference to a Collection is
 * from a Collections object (which in turn is retrieved from
 * an Application object).
 * <p> 
 * In the rare case that you want to create a new collection via the 
 * API, there is a public constructor that you need to use.
 * <p>
 * If you need to inspect the metadata of the fields on this
 * Collection, consult the Fields child object.
 * <p>
 * GET: If you need a way to query for the entries in this Collection,
 * see the CollectionEntries child object.
 * <p>
 * CREATE: To add a new Entry to this Collection, see the insert operation
 * on the child CollectionEntries object. 
 * <p>
 * UPDATE, DELETE: look for the respective methods on the Entry object
 * to update or delete individual entries
 */
public class Collection {

	protected Application parentApplication = null;
	
	protected String name;
	protected String url;
	protected String entriesUrl;
	protected String entryType;
	protected String pk; // TODO move this to Fields
	protected Fields fields = null;
	protected CollectionEntries entries = new CollectionEntries(this);
	
	protected boolean existsOnServer = true;
	
	/**
	 * Creates a new Collection object for the account.
	 * <p>
	 * Note, this method is only used if you are intending to 
	 * create a new collection in your StorageRoom account. Otherwise,
	 * the framework will create the Collection objects for you
	 * (see Collections).
	 * 
	 * @param parent the parent Application/Account for which
	 * this collection is associated
	 * @param name the String name of the collection as seen in the SR UI
	 * @param entryType the String entry type as seen in the SR UI
	 */
	public Collection(Application parent, String name, String entryType) {
		this.parentApplication = parent;
		this.name = name;
		this.entryType = entryType;
		
		this.existsOnServer = false; // somebody is creating a new collection
	}

	/*
	 NOTE the parse method can handle either a raw collection object or the case where the collection object is a value on key 'collection'
		{"collection":
		  {
		     "@type":"Collection",
		     "@url":"http://api.storageroomapp.com/accounts/50ef2fcc0f6602017a000787/collections/50ef4b390f66021eca001847",
		     "@account_url":"http://api.storageroomapp.com/accounts/50ef2fcc0f6602017a000787",
		     "@entries_url":"http://api.storageroomapp.com/accounts/50ef2fcc0f6602017a000787/collections/50ef4b390f66021eca001847/entries",
		     "@deleted_entries_url":"http://api.storageroomapp.com/accounts/50ef2fcc0f6602017a000787/deleted_entries?collection_url=http%3A%2F%2Fapi.storageroomapp.com%2Faccounts%2F50ef2fcc0f6602017a000787%2Fcollections%2F50ef4b390f66021eca001847",
		     "@version":1,
		     "@created_at":"2013-01-10T23:14:01Z",
		     "@updated_at":"2013-01-10T23:14:01Z",
		     "name":"ReadOnlyCollection",
		     "note":"",
		     "entry_type":"CatalogItemRO",
		     "primary_field_identifier":"sku",
		     "fields":[
		     {
		       "@type":"StringField",
		       "name":"SKU",
		       "identifier":"sku",
		       "show_in_interface":true,
		       "edit_in_interface":true,
		       "input_type":"text_field",
		       "required":true,
		       "unique":true,
		       "maximum_length":16,
		       "minimum_length":4,
		       "include_blank_choice":false
		     },
		     {
		       "@type":"IntegerField",
		       "name":"InStock",
		       "identifier":"in_stock",
		       "show_in_interface":true,
		       "edit_in_interface":true,
		       "input_type":"text_field",
		       "minimum_number":0.0,
		       "include_blank_choice":false
		     }
		     ],
		     "webhook_definitions":[]
		  }
		}
	 */
	
	/**
	 * Parses a String of json text and returns an Collection object.
	 * It will correctly parse the Collection object if it is toplevel,
	 * or also if nested in an 'collection' key-value pair.
	 * 
	 * @param json the String with the json text
	 * @return an Collection object, or null if the parsing failed
	 */
	static public Collection parseJson(Application parent, String json) {
		JSONObject collectionObj = (JSONObject)JSONValue.parse(json);
		if (collectionObj == null) {
			return null;
		}
		
		// unwrap the Collection object if it is a value with key 'collection;
		JSONObject collectionInnerObj = (JSONObject)collectionObj.get("collection");
		if (collectionInnerObj != null) {
			collectionObj = collectionInnerObj;
		}
		
		return parseJsonObject(parent, collectionObj);
	}

	/**
	 * Unmarshalls an Collection object from a JSONObject. This method
	 * assumes the name-values are immediately attached to the passed object
	 * and not nested under a key (e.g. 'collection')
	 * @param jsonObj the JSONObject
	 * @return an Collection object, or null if the unpacking failed
	 */
	static public Collection parseJsonObject(Application parent, JSONObject jsonObj) {
		String name = JsonSimpleUtil.parseJsonStringValue(jsonObj, "name");
		String entryType = JsonSimpleUtil.parseJsonStringValue(jsonObj, "entry_type");

		Collection col = new Collection(parent, name, entryType);
		col.existsOnServer = true;
		
		col.pk = JsonSimpleUtil.parseJsonStringValue(jsonObj, "primary_field_identifier");
		col.url = JsonSimpleUtil.parseJsonStringValue(jsonObj, "@url");
		col.entriesUrl = JsonSimpleUtil.parseJsonStringValue(jsonObj, "@entries_url");
		
		// validation, some properties are required
		if ((col.name == null) || (col.entriesUrl == null) || (col.url == null) || (col.entryType == null)) {
			return null;
		}

		// parse list of fields
		JSONArray fieldArray = (JSONArray)jsonObj.get("fields");
		if (fieldArray != null) {
			col.fields = Fields.parseJsonFieldArray(col, fieldArray); 
		}
		
		return col;
	}
	
	// GETTERS
	
	public Application getParentApplication() {
		return parentApplication;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public String getEntriesUrl() {
		return entriesUrl;
	}

	public String getEntryType() {
		return entryType;
	}

	public String getPk() {
		return pk;
	}

	/**
	 * Returns a live copy of the child Fields object. When creating
	 * or updating a collection, you may directly modify the returned
	 * object.
	 * 
	 * @return the Fields object, which will never be null
	 */
	public Fields getFields() {
		if (this.fields == null) {
			this.fields = new Fields(this);
		}
		return this.fields;
	}
	
	/**
	 * Returns a live copy of the child Entry objects.
	 * 
	 * @return the CollectionEntries object, which will never be null
	 */
	public CollectionEntries getEntries() {
		if (this.entries == null) {
			this.entries = new CollectionEntries(this);
		}
		return this.entries;
	}
	
	// OPERATIONS
	
	/**
	 * Creates a new Collection on the server for this Application/Account. Be sure
	 * to populate the Fields child object.
	 * <p>
	 * This operation will only succeed if this Collection was created via the 
	 * public constructor.
	 * <p>
	 * @return true if the Collection was created on the server, false if not
	 */
	public boolean create() {
		if (existsOnServer) {
			return false;
		}
		// TODO
		throw new UnsupportedOperationException("POST of a new Collection is not yet supported in this client.");
	}
	
	/**
	 * Updates the Collection definition on the server for this Application/Account.
	 * <p>
	 * This operation will only succeed if this Collection was retrieved via the 
	 * API (parsed JSON).
	 * <p>
	 * @return true if the Collection was created on the server, false if not
	 */
	public boolean update(Fields fields) {
		if (!existsOnServer) {
			return false;
		}
		// TODO
		throw new UnsupportedOperationException("PUT of a new Collection is not yet supported in this client.");
	}
	
	/**
	 * Note, emptying the trash does not currently work. StorageRoom is aware of the issue:
	 * {@link http://help.storageroomapp.com/discussions/questions/977-api-to-empty-the-trash}
	 * @return true if successful, false if not
	 */
	public boolean emptyTrash() {
		throw new UnsupportedOperationException("Emptying of Collection trash is not supported by StorageRoom API.");

//		 It would look something like this:
//		 boolean success = true;
//		 String trashUrl = url+"/trash/empty";
//		 success = Http.delete(trashUrl);
//		 return success;
	}
	
	// SERIALIZATION
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("URL [");
		sb.append(url);
		sb.append("] entriesURL [");
		sb.append(entriesUrl);
		sb.append("] name [");
		sb.append(name);
		sb.append("] entry_type [");
		sb.append(entryType);
		sb.append("]");
		
		if (pk != null) {
			sb.append(" primary_field_identifier [");
			sb.append(pk);		
			sb.append("]");
		}
		
		if (fields != null) {
			sb.append(" fields ");
			sb.append(fields.toString());
		}
		
		return sb.toString();
	}

	public String toJSONString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("{\"@type\":\"Collection\",\"@url\":\"");
		sb.append(url);
		sb.append("\", \"@entries_url\":\"");
		sb.append(entriesUrl);
		sb.append("\", \"name\":\"");
		sb.append(name);
		sb.append("\", \"entry_type\":\"");
		sb.append(entryType);
		sb.append("\"");
		
		if (pk != null) {
			sb.append(", \"primary_field_identifier\":\"");
			sb.append(pk);		
			sb.append("\"");
		}
		
		if (fields != null) {
			sb.append(", \"fields\":");
			sb.append(fields.toJSONString());
		}
		
		sb.append("}");
		
		return sb.toString();
	}
}

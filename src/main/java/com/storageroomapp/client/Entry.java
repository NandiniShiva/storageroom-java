package com.storageroomapp.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;

import com.storageroomapp.client.field.Fields;
import com.storageroomapp.client.field.GenericField;
import com.storageroomapp.client.field.StringField;
import com.storageroomapp.client.util.FileUtil;
import com.storageroomapp.client.util.Http;
import com.storageroomapp.client.util.StorageRoomUtil;

/**
 * The Entry object represents an entry in a collection. Entry objects
 * are normally obtained via querys to collections (see how in 
 * the CollectionEntries class).
 * <p>
 * When you wish to GET or CREATE an Entry, see the CollectionEntries class
 * for methods to enable just that.
 * <p>
 * To UPDATE or DELETE a particular entry, this object offers methods to 
 * do that.
 */
public class Entry extends HashMap<String, GenericField<?>> {
	static private final long serialVersionUID = 1L;

	protected Collection parentCollection = null;
	
	// Strange not to see any more members, but an Entry in 
	// StorageRoom really is just a bag of Field values. This
	// Entry class extends HashMap for that purpose
	
	/**
	 * Public callers should not be creating raw Entry objects. When creating
	 * new entries, callers should instead retrieve an Entry object populated
	 * with the Field metadata for the associated Collection. The CollectionEntries
	 * object does just that.
	 * 
	 * @param parent
	 */
	protected Entry(Collection parent) {
		this.parentCollection = parent;
	}
	
	// DESERIALIZATION
	
	/* 
		{
		  "@type":"Inputdata",
		  "@url":"http://api.storageroomapp.com/accounts/50eb30ec0f66024ee5001733/collections/50edda060f66020e0c000126/entries/50ede25b0f660224af0000d3",
		  "@collection_url":"http://api.storageroomapp.com/accounts/50eb30ec0f66024ee5001733/collections/50edda060f66020e0c000126",
		  "@version":2,
		  "@trash":false,
		  "@created_at":"2013-01-09T21:34:19Z",
		  "@updated_at":"2013-01-14T21:19:53Z",
		  "field_1":"string field value",
		  "field_2":79.2,
		  "datafile":null
		}	 
     */
	
	/**
	 * Unmarshalls an Entry object from a JSONObject. This method
	 * assumes the name-values are immediately attached to the passed object
	 * and not nested under a key (e.g. 'entry')
	 * @param jsonObj the JSONObject
	 * @return an Entry object, or null if the unpacking failed
	 */
	static public Entry parseJSONObject(Collection parent, JSONObject jsonObj) {
		if (jsonObj == null) {
			return null;
		}
		Entry entry = new Entry(parent);
		
		// an Entry is just a bag of field values, nothing to parse
		// except the fields themselves...
		
		Fields fields = parent.getFields();
		fields.parseFieldsInEntryJsonObject(jsonObj, entry);
		
		return entry;
	}
	
	// OPERATIONS
	
	/**
	 * This method makes a live request to the StorageRoom API to 
	 * delete this Entry. 
	 * <p>
	 * After a successful delete, it may be okay to continue using this
	 * object for offline purposes. It is not recommended to subsequently
	 * reuse this same object in an insert call. Residual state within this
	 * object may cause that operation to fail.
	 * <p>
	 * SERVER ROUND TRIP: this is a live API call
	 * 
	 * @return true if the delete succeeded, false if not
	 */
	public boolean delete() {
		boolean success = false;
		
		StringField itemUrlValue = (StringField)this.get("@url");
		if (itemUrlValue == null) {
			return false;
		}
		String itemUrl = itemUrlValue.getValueWrapper().getInnerValue();
		if (itemUrl == null) {
			return false;
		}
		Application application = parentCollection.getParentApplication();
		itemUrl = StorageRoomUtil.decorateUrl(itemUrl, application.getAuthToken(), true, null);
		
		success = Http.delete(itemUrl);
		
		return success;
	}

	public boolean update() {
		boolean success = false;
		
		StringField itemUrlValue = (StringField)this.get("@url");
		if (itemUrlValue == null) {
			return false;
		}
		String itemUrl = itemUrlValue.getValueWrapper().getInnerValue();
		if (itemUrl == null) {
			return false;
		}
		Application application = parentCollection.getParentApplication();
		itemUrl = StorageRoomUtil.decorateUrl(itemUrl, application.getAuthToken(), true, null);
		
		String putBody = toJSONString(true);
		if (putBody != null) {
			success = Http.put(itemUrl, putBody);
		}
				
		return success;
	}
	
	// CONVENIENCE METHODS
	
	/**
	 * A convenience method for retrieving the value of one of 
	 * this Entry's fields, as a String. 
	 * 
	 * @param fieldIdentifier the String name of the field
	 * @return the value as a String, or null if the value is not set
	 */
	public String fieldValueToString(String fieldIdentifier) {
		String value = null;
		
		GenericField<?> field = get(fieldIdentifier);
		if (field != null) {
			value = field.getValueAsString();
		}
		
		return value;
	}
	
	public List<GenericField<?>> asListOfDataFieldsOnly() {
		Fields allFieldDefinitions = parentCollection.getFields();
		List<GenericField<?>> dataFieldDefinitions = allFieldDefinitions.withOnlyDataFields();
		List<GenericField<?>> dataFieldInstances = new ArrayList<GenericField<?>>();
		
		for (GenericField<?> dataFieldDef : dataFieldDefinitions) {
			GenericField<?> dataFieldInstance = this.get(dataFieldDef.getIdentifier());
			if (dataFieldInstance != null) {
				dataFieldInstances.add(dataFieldInstance);
			}
		}
		return dataFieldInstances;
	}
	
	/**
	 * Convenience method to get the 'name' field
	 * @return the String name, or null if it does not have one
	 */
	public String getName() {
		StringField nameField = (StringField)get("name");
		if (nameField == null) {
			return null;
		}
		return nameField.getValueWrapper().getInnerValue();
	}
	
	public String getInternalUniqueIdentifier() {
		StringField urlField = (StringField)get("@url");
		if (urlField == null) {
			return null;
		}
		String url = urlField.getValueWrapper().getInnerValue();
		String lastPath = FileUtil.getLastUrlPath(url);
		return lastPath;
	}

	// SERIALIZATION
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Entry collection_url [");
		sb.append(parentCollection.url);
		sb.append("] type [");
		sb.append(parentCollection.entryType);
		sb.append("] ");
		
		for (GenericField<?> field : this.values()) {
			String fieldStr = field.toString();
			sb.append(fieldStr);
		}
		
		return sb.toString();
	}
	
	public String toJSONString(boolean dataFieldsOnly) {
		java.util.Collection<GenericField<?>> fieldList = this.values();
		if (dataFieldsOnly) {
			fieldList = this.asListOfDataFieldsOnly();
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("{ \"entry\": {");
		boolean isFirst = true;
		for (GenericField<?> dataField : fieldList) {
			String fieldStr = dataField.toJSONString();
			if (fieldStr == null) {
				continue;
			}
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(", ");
			}
			sb.append(fieldStr);
		}
		sb.append("} }");
		return sb.toString();
	}
	
}

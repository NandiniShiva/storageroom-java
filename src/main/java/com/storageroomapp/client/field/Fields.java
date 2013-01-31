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
package com.storageroomapp.client.field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.storageroomapp.client.Collection;
import com.storageroomapp.client.Entry;
import com.storageroomapp.client.util.JsonSimpleUtil;

public class Fields extends ArrayList<GenericField<?>> {
	static private final long serialVersionUID = 1L;
		
	protected Collection parentCollection = null;
	private ArrayList<GenericField<?>> dataFieldsOnlyList = new ArrayList<GenericField<?>>();

	
	public Fields(Collection parentCollection) {
		this.parentCollection = parentCollection;
	}
	
	// FIELD METADATA PARSING
	
	/*
	 * A property named fields exists in the Collection JSON, this class can parse that property

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
	 */
	
	static public Fields parseJsonFieldArray(Collection parent, JSONArray jsonObj) {
		if ((parent == null) || (jsonObj == null)) {
			return null;
		}
		Fields fields = new Fields(parent);
		
		// parse and build the DATA fields (the ones unique to this Collection)
		@SuppressWarnings("unchecked")
		Iterator<JSONObject> iter = jsonObj.iterator();
		while (iter.hasNext()) {
			JSONObject fieldObj = iter.next();
			GenericField<?> field = parseJsonFieldObject(parent, fieldObj);
			if (field != null) {
				fields.add(field);
				fields.dataFieldsOnlyList.add(field);
			}
		}

		// build the METADATA fields (common to all Collections)
		for (GenericField<?> field : metadataFields) {
			fields.add(field.cloneAsMetadataField(parent));
		}
		
		return fields;
	}
	
	static public GenericField<?> parseJsonFieldObject(Collection parent, JSONObject fieldObj) {
		String fieldType = JsonSimpleUtil.parseJsonStringValue(fieldObj, "@type");
		if (fieldType == null) {
			return null;
		}
		GenericField<?> field = null;
		
		// Factory for fields
		field = fieldPrototypes.get(fieldType);
		if (field == null) {
			field = defaultField;
		}
		field = field.cloneAsMetadataField(parent, fieldObj);
		
		return field;
	}
	
	// ENTRY FIELD INSTANCE PARSING
	
	public void parseFieldsInEntryJsonObject(JSONObject jsonObject, Entry hydrate) {
		for (GenericField<?> field : this) {
			String identifier = field.getIdentifier();
			GenericField<?> instance = field.parseValueJsonForField(jsonObject, identifier);
			if (instance != null) {
				hydrate.put(identifier, instance);
			}
		}
	}
	
	public void addMissingDataFieldsToEntry(Entry hydrate) {
		for (GenericField<?> dataField : dataFieldsOnlyList) {
			String identifier = dataField.getIdentifier();
			GenericField<?> entryField = hydrate.get(identifier);
			if (entryField == null) {
				// Entry does not have this field yet, add one with a null value
				hydrate.put(identifier, dataField.cloneAsDataField(null));
			}
		}
	}
	
	// CONVENIENCE METHODS
	
	public List<GenericField<?>> withOnlyDataFields() {
		return dataFieldsOnlyList;
	}
	
	
	// FIELD FACTORY INITIALIZATION

	static protected GenericField<?> defaultField = null;
	static protected Map<String, GenericField<?>> fieldPrototypes = null;
	static protected ArrayList<GenericField<?>> metadataFields = new ArrayList<GenericField<?>>();
	
	static {
		fieldPrototypes = new HashMap<String, GenericField<?>>();
		
		// Register the Field prototype objects (one per type of supported Field on StorageRoom)
		StringField stringField = new StringField();
		registerFieldImplementation(stringField);
		defaultField = stringField;

		BooleanField booleanField = new BooleanField();
		registerFieldImplementation(booleanField);
		
		DateField dateField = new DateField();
		registerFieldImplementation(dateField);
		
		TimeField timeField = new TimeField();
		registerFieldImplementation(timeField);
		
		FileField fileField = new FileField();
		registerFieldImplementation(fileField);

		ImageField imageField = new ImageField();
		registerFieldImplementation(imageField);
		
		FloatField floatField = new FloatField();
		registerFieldImplementation(floatField);
		
		IntegerField integerField = new IntegerField();
		registerFieldImplementation(integerField);
		
		// Create the Field prototype objects that every entry has
		GenericField<?> field = stringField.cloneAsMetadataField(null, "@type", "@type");
		metadataFields.add(field);
		field = stringField.cloneAsMetadataField(null, "@url", "@url");
		metadataFields.add(field);
		field = integerField.cloneAsMetadataField(null, "@version", "@version");
		metadataFields.add(field);
		field = booleanField.cloneAsMetadataField(null, "@trash", "@trash");
		metadataFields.add(field);
		field = dateField.cloneAsMetadataField(null, "@created_at", "@created_at");
		metadataFields.add(field);
		field = dateField.cloneAsMetadataField(null, "@updated_at", "@updated_at");
		metadataFields.add(field);
	}
	
	static private void registerFieldImplementation(GenericField<?> f) {
		String type = f.getType();
		if (type != null) {
			fieldPrototypes.put(type, f);
		}
	}
	
	// SERIALIZATION

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("[ ");
		boolean first = true;
		for (GenericField<?> field : this) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append(field.toString());
		}
		sb.append(" ]");
		
		return sb.toString();
	}
	
	public String toJSONString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("[ ");
		boolean first = true;
		for (GenericField<?> field : this) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append(field.toJSONString());
		}
		sb.append(" ]");
		
		return sb.toString();
	}
	
}

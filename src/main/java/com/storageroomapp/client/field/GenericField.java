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

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.storageroomapp.client.Collection;
import com.storageroomapp.client.util.JsonSimpleUtil;

abstract public class GenericField<T extends GenericValue<?>> implements Cloneable {

	/*
	 * MY FRIEND:
	 * 
	 * I was hoping you wouldn't have to come down in here, as the higher level
	 * classes (com.storageroomapp.client.*) are fairly nice and neat. They match
	 * well with the SR concepts, and more or less are light and airy.
	 * 
	 * When it comes to Fields, I am sorry but here is where we get nasty. 
	 * This class here, GenericField, is the epicenter of some decent complexity.
	 * 
	 * 1. I used Java generics
	 * 
	 * Most folks frankly just aren't that familiar with generics outside their use
	 * with Java collections, so I was not planning to use them. But, the Field
	 * solution was made much better to the public API user because of them, so I
	 * yielded and used them. Sorry. Generics kinda suck from a readability point
	 * of view.
	 * 
	 * 2. I unified the metadata and data fields
	 * 
	 * A controversial choice perhaps, but I used the same class hierarchy for metadata
	 * fields (those that describe what fields are defined for a collection, that can
	 * have a default value) and data fields (the fields associated with an Entry, 
	 * and can have an actual value). This saved a ton of code, but makes working at this
	 * level a little harder as you have to always keep in mind what flavor of field you
	 * are working with. The 'isFieldDefinition' member will remind you, but still.
	 * 
	 * 3. Some StorageRoom fields are Chimeras
	 * 
	 * Field types like File and Image have entirely different json structures depending
	 * on whether they are being downloaded from SR in a GET, or uploaded to SR in 
	 * a PUT or POST. Ouch.
	 * 
	 * 4. My use case is solved, I am losing interest in continuing the javadoc
	 * 
	 * Sadly, I am about ready to be done here. I did a decent job of documenting the
	 * higher classes, but I need to move on. Providing good docs for the Fields package
	 * would take a ton of time, so I leave you on your own...
	 */
	
	
	protected Collection parentCollection = null;
	
	// this is used by subclasses to differentiate
	protected String type = "all";

	// the core properties of a Field
	protected String name = "undefined";
	protected String identifier = "undefined";
	// the rest of the properties
	protected Map<String, String> additionalProperties = new HashMap<String, String>();

	// is this a compound field? File, Image, Location, Association
	protected boolean isCompoundFieldType = false;

	// If isFieldDefinition is true, this object is a metadata field, meaning it
	// is associated with a Collection, not an Entry
	protected boolean isFieldDefinition = true;
	// Field 'value' should only be used if isFieldDefinition is false
	protected T value = null;
	
	protected GenericField(String type) {
		this.type = type;
	}
	
	
	// METADATA FIELD CONSTRUCTION (a field definition for a collection; no value is allowed)
	
	// we are using the prototype pattern here
	// each Field should know how to create Field instances for specific Collections on demand
	
	@SuppressWarnings("unchecked")
	private GenericField<T> build() {
		GenericField<T> clone = null;
		try {
			clone = (GenericField<T>)this.clone();
		} catch (CloneNotSupportedException cns) {
			// nothing
		}
		return clone;
	}

	/**
	 * Copies a metadata field from one collection to another. This is 
	 * used when copying over the standard fields that occur in all collections
	 * into a newly created collection.
	 */
	protected GenericField<T> cloneAsMetadataField(Collection otherCollection) {
		GenericField<T> clone = build();
				
		clone.parentCollection = otherCollection;

		return clone;
	}
	
	protected GenericField<T> cloneAsMetadataField(Collection otherCollection, 
			String name, String identifier) {
		GenericField<T> clone = build();
				
		clone.parentCollection = otherCollection;
		clone.type = type;
		clone.name = name;
		clone.identifier = identifier;
		
		return clone;
	}
	
	protected GenericField<T> cloneAsMetadataField(Collection otherCollection, JSONObject jsonObj) {
		GenericField<T> clone = build();
				
		clone.parentCollection = otherCollection;
		clone.type = JsonSimpleUtil.parseJsonStringValue(jsonObj, "@type");
		clone.name = JsonSimpleUtil.parseJsonStringValue(jsonObj, "name");
		clone.identifier = JsonSimpleUtil.parseJsonStringValue(jsonObj, "identifier");
		
		return clone;
	}
	
	// DATA FIELD CONSTRUCTION (a field associated with an entry in the collection)
	
	protected GenericField<T> cloneAsDataField(T value) {
		GenericField<T> clone = build();
		
		clone.isFieldDefinition = false;
		clone.value = value;
		
		return clone;
	}
	
	/*
    "@type":"IntegerField",
    "name":"InStock",
    "identifier":"in_stock",
    "show_in_interface":true,
    "edit_in_interface":true,
    "input_type":"text_field",
	 */
	
	protected GenericField<T> parseValueJsonForField(JSONObject entry, String identifier) {
		Object valueJson = entry.get(identifier);
		if (valueJson == null) {
			return null;
		}
		T valueJava = deserializeJsonValue(valueJson);
		GenericField<T> instance = null;
		if (valueJava != null) {
			instance = cloneAsDataField(valueJava);
		}

		return instance;
	}
	
	abstract protected T deserializeJsonValue(Object jsonValue);

	
	// GETTERS and SETTERS
	
	/**
	 * Gets the StorageRoom type label for this field. Examples include
	 * StringField, TimeField, IntegerField, etc.
	 * @return the String type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Gets the name of this field, provided by the admin user when creating
	 * the field in the SR UI.
	 * @return the String name
	 */
	public String getName() {
		return name;
	}

	/**
	 * The internal identifier used for this field in API calls. Generally,
	 * it is the lowercase version of the name, with spaces replaced with
	 * underscores.
	 * 
	 * @return the String id
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Returns whether this field represents a compound type. This is useful
	 * for StorageRoom UI apps to know when to show a complicated value editor.
	 * Compound types are File, Image, Location and others.
	 * @return true if it is a compound type, false if not.
	 */
	public boolean isCompoundFieldType() {
		return isCompoundFieldType;
	}
	
	/**
	 * Returns the wrapper object that carrys the value of this field.
	 * This will never return null, even if the underlying value is
	 * not set.
	 * <p>
	 * This bears some more explanation.
	 * <p>
	 * Because this Java client is largely a json serialization/deserialization
	 * tool (at least when we are down to the Field level), there is a lot of
	 * machinery that is responsible for converting objects into StorageRoom
	 * json and back. The value wrapper returned by this method is critical
	 * to that capability.
	 * <p>
	 * If this Field object is a metadata field (i.e. associated with a Collection
	 * not an Entry), then calling this method will result in an exception being 
	 * thrown. This radical approach is meant to root out serious coding bugs that
	 * can result by mistaking Collection as Entry fields.
	 * 
	 * @return the wrapper GenericField subclass appropriate for the field type
	 */
	public T getValueWrapper() {
		if (isFieldDefinition) {
			throw new IllegalArgumentException();
		}
		return value;
	}

	/**
	 * A convenience method to quickly get the value of this field as a String.
	 * 
	 * @return the String value
	 */
	public String getValueAsString() {
		if (isFieldDefinition) {
			throw new IllegalArgumentException();
		}
		String valueStr = null;
		if (value != null) {
			valueStr = value.toString();
		}
		return valueStr;
	}
	
	/**
	 * When planning to update an Entry, this method allows you to set the
	 * Field value to a new value.
	 * <p>
	 * Note, if you wish to nullify a field value, do not pass null to this method.
	 * Instead, instantiate the proper GenericValue subclass (e.g. StringValue) using
	 * the default constructor, which sets the inner value to null. Then, pass that
	 * constructed GenericValue into this method. 
	 * 
	 * @param newValue the new value for the field. It should never be null.
	 */
	public void setValue(T newValue) {
		if (isFieldDefinition) {
			throw new IllegalArgumentException();
		}
		value = newValue;
	}
	
	/**
	 * Determines if two fields refer to the same Field in the same Collection.
	 * It does NOT check for value equality, entry equality, or anything else.
	 * @param match the field to match
	 * @return true if they are the same field in the collection
	 */
	public boolean isSameField(GenericField<?> match) {
		if (match == null) {
			return false;
		}
		if (!this.parentCollection.equals(match.parentCollection)) {
			return false;
		}
		if (!this.identifier.equals(match.identifier)) {
			return false;
		}
		return true;
	}

	// Serialization
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		if (isFieldDefinition) {
			sb.append("{ type [");
			sb.append(type);
			sb.append("] name [");
			sb.append(name);
			sb.append("] id [");
			sb.append(identifier);
			sb.append("] value [");
			sb.append(value);
			sb.append("]");
			sb.append("}");
		} else {
			String valueStr = "null";
			if (value != null) {
				valueStr = value.toString();
			}
			sb.append(identifier);
			sb.append(" [");
			sb.append(valueStr);
			sb.append("] ");
		}
		return sb.toString();
	}

	public String toJSONString() {
		StringBuilder sb = new StringBuilder();
		
		if (isFieldDefinition) {
			sb.append("{ \"@type\": \"");
			sb.append(type);
			sb.append("\", \"name\": \"");
			sb.append(name);
			sb.append("\", \"identifier\": \"");
			sb.append(identifier);
			sb.append("\"");
			sb.append(" }");
		} else {
			if (value == null) {
				return null;
			}
			sb.append("\"");
			sb.append(identifier);
			sb.append("\": ");
			sb.append(value.toJSONString());
		}
		
		return sb.toString();
	}
}

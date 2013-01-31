package com.storageroomapp.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class CollectionStaticTest {
	
	protected Application fakeApplication = Application.getInstance("fake");
	
	static private String collectionsJson = 
		"{ \"array\": { \"@url\":\"COLLECTIONS_URL\", \"@type\":\"Array\", \"resources\":["+
				"{ \"name\":\"c1\", \"@url\": \"URL\", \"@entries_url\": \"ENTRIES\", \"entry_type\": \"ENTRY_TYPE\" },"+ 
				"{ \"name\":\"c2\", \"@url\": \"URL2\", \"@entries_url\": \"ENTRIES2\", \"entry_type\": \"ENTRY_TYPE\" }"+ 
		"]}}";
	@Test
	public void testParseCollectionsJson() {
		Collections colls = Collections.parseJson(fakeApplication, collectionsJson);
		
		assertTrue("There should be two collections detected in the parsed json.", colls.size() == 2);
		assertEquals("We failed to read the Collections URL out of the parsed json", "COLLECTIONS_URL", colls.getUrl());
		
		Collection c1 = colls.get(0);
		// test that the first collection is filled out, and the parsing maintained the order of elements
		assertEquals("Name from the Collection was not parsed correctly.", "c1", c1.getName());
		assertEquals("URL from the Collection was not parsed correctly.", "URL", c1.getUrl());
		assertEquals("EntriesURL from the Collection was not parsed correctly.", "ENTRIES", c1.getEntriesUrl());
	}

	static private String collectionJson = 
			"{ \"collection\": "+
					"{ \"name\":\"c1\", \"@url\": \"URL\", \"@entries_url\": \"ENTRIES\", \"entry_type\": \"ENTRY_TYPE\", "+
					    "\"fields\": ["+
					    	"{\"@type\":\"IntegerField\", \"name\":\"InStock\", \"identifier\":\"in_stock\", \"show_in_interface\":true, \"edit_in_interface\":true, \"input_type\":\"text_field\"}"+
					    "]"+
					"}"+
			"}";
	@Test
	public void testParseCollectionJson() {
		Collection c1 = Collection.parseJson(fakeApplication, collectionJson);
		
		assertNotNull("We did not parse the Collection, as it returned null.", c1);
		assertEquals("Name from the Collection was not parsed correctly.", "c1", c1.getName());
		assertEquals("URL from the Collection was not parsed correctly.", "URL", c1.getUrl());
		assertEquals("EntriesURL from the Collection was not parsed correctly.", "ENTRIES", c1.getEntriesUrl());
	}
	
	@Test
	public void testToString() {
		// we aren't going to test for particular return value here, but at least make sure it does not NPE
		Collection c1 = Collection.parseJson(fakeApplication, collectionJson);
		c1.toString();
	}

	@Test
	public void testToJSONString() {
		Collection c1 = Collection.parseJson(fakeApplication, collectionJson);
		String json = c1.toJSONString();
		Collection c2 = Collection.parseJson(fakeApplication, json);
		assertNotNull("We were not able to roundtrip the Collection from JSON->Obj->JSON->Obj", c2);
	}
	
	// NEGATIVE TESTS

	static private String missingUrlCollectionsJson = 
			"{ \"array\": { \"NOT_@url\":\"COLLECTIONS_URL\", \"@type\":\"Array\", \"resources\":["+
					"{ \"name\":\"c1\", \"@url\": \"URL\", \"@entries_url\": \"ENTRIES\", \"entry_type\": \"ENTRY_TYPE\" },"+ 
					"{ \"name\":\"c2\", \"@url\": \"URL2\", \"@entries_url\": \"ENTRIES2\", \"entry_type\": \"ENTRY_TYPE\" }"+ 
			"]}}";
	@Test
	public void testParseMissingURLCollectionsJson() {
		// Collections
		Collections colls = Collections.parseJson(fakeApplication, missingUrlCollectionsJson);
		
		assertNull("The parsing should have failed, as there is no @url property for the Collections item in the json.", colls);
	}
	
	static private String missingNameCollectionJson = 
			"{ \"array\": { \"@url\":\"COLLECTIONS_URL\", \"@type\":\"Array\", \"resources\":["+
					"{ \"NOT_name\":\"c1\", \"@url\": \"URL\", \"@entries_url\": \"ENTRIES\", \"entry_type\": \"ENTRY_TYPE\" },"+ 
					"{ \"name\":\"c2\", \"@url\": \"URL2\", \"@entries_url\": \"ENTRIES2\", \"entry_type\": \"ENTRY_TYPE\" }"+ 
			"]}}";	
	@Test
	public void testParseMissingNameCollectionJson() {
		// Collections
		Collections colls = Collections.parseJson(fakeApplication, missingNameCollectionJson);
		
		assertTrue("The parsing should have failed, as there is no name property for the Collection [c1] in the json.", colls.size() == 1);
	}

	static private String missingUrlCollectionJson = 
			"{ \"array\": { \"@url\":\"COLLECTIONS_URL\", \"@type\":\"Array\", \"resources\":["+
					"{ \"name\":\"c1\", \"@NOT_url\": \"URL\", \"@entries_url\": \"ENTRIES\", \"entry_type\": \"ENTRY_TYPE\" },"+ 
					"{ \"name\":\"c2\", \"@url\": \"URL2\", \"@entries_url\": \"ENTRIES2\", \"entry_type\": \"ENTRY_TYPE\" }"+ 
			"]}}";
	@Test
	public void testParseMissingURLCollectionJson() {
		// Collections
		Collections colls = Collections.parseJson(fakeApplication, missingUrlCollectionJson);
		
		assertTrue("The parsing should have failed, as there is no @url property for the Collection [c1] in the json.", colls.size() == 1);
	}
	
	static private String missingEntriesUrlCollectionJson = 
			"{ \"array\": { \"@url\":\"COLLECTIONS_URL\", \"@type\":\"Array\", \"resources\":["+
					"{ \"name\":\"c1\", \"@url\": \"URL\", \"NOT_@entries_url\": \"ENTRIES\", \"entry_type\": \"ENTRY_TYPE\" },"+ 
					"{ \"name\":\"c2\", \"@url\": \"URL2\", \"@entries_url\": \"ENTRIES2\", \"entry_type\": \"ENTRY_TYPE\" }"+ 
			"]}}";
	@Test
	public void testParseMissingEntriesURLCollectionJson() {
		// Collections
		Collections colls = Collections.parseJson(fakeApplication, missingEntriesUrlCollectionJson);
		
		assertTrue("The parsing should have failed, as there is no @entries_url property for the Collection [c1] in the json.", colls.size() == 1);
	}
}

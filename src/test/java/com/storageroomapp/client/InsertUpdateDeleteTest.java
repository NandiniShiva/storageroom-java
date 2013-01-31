package com.storageroomapp.client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.storageroomapp.client.field.IntegerField;
import com.storageroomapp.client.field.IntegerValue;
import com.storageroomapp.client.field.StringField;
import com.storageroomapp.client.field.StringValue;

public class InsertUpdateDeleteTest {
	
	static protected String insertedSKU = null;
	
	@Test
	public void testInsertQueryUpdateDeleteCycle() {
		Application app = Application.getInstance("JavaClient");
		boolean success = app.connect(StorageRoomTestEnv.JavaClientReadWrite_AccountId, StorageRoomTestEnv.JavaClientReadWrite_AuthToken, true);
		assertTrue("Could not connect to StorageRoom, perhaps service is down or account "+StorageRoomTestEnv.JavaClientReadWrite_AccountId+" is no longer valid.", success);
		Collections colls = app.getCollections(true);
		Collection col = colls.findCollection("ReadWriteCollection");
		CollectionEntries entries = col.getEntries();
		
		String insertedSKU = "JUNIT"+System.currentTimeMillis();
	
		// INSERT
		Entry newEntry = entries.createNewEntryTemplateObject();
		StringField sku = (StringField)newEntry.get("sku");
		sku.setValue(new StringValue(insertedSKU));
		IntegerField inStock = (IntegerField)newEntry.get("in_stock");
		inStock.setValue(new IntegerValue(888));
		success = entries.insertNewEntry(newEntry);
		assertTrue("Could not insert an entry into the ReadWriteCollection", success);

		// QUERY
		CollectionQuery queryDef = new CollectionQuery();
		queryDef.filterOptions = "sku="+insertedSKU;
		PageOfEntries results = entries.query(queryDef);
		assertFalse("Could not locate inserted item upon query.", results.getCurrentPageSize() == 0);
		assertTrue("Query should have only matched one SKU", results.getCurrentPageSize() == 1);

		// UPDATE
		Entry result = results.asList().get(0);
		inStock = (IntegerField)result.get("in_stock");
		inStock.setValue(new IntegerValue(67890));
		success = result.update();
		assertTrue("Could not update an entry in the ReadWriteCollection", success);
		
		// QUERY
		queryDef.filterOptions = "in_stock=67890";
		results = entries.query(queryDef);
		assertFalse("Could not locate updated item upon query.", results.getCurrentPageSize() == 0);
		assertTrue("Query should have only matched one row", results.getCurrentPageSize() == 1);

		// DELETE
		result = results.asList().get(0);
		success  = result.delete();
		assertTrue("Could not delete an entry from the ReadWriteCollection", success);

		// QUERY
		results = entries.query(queryDef);
		assertTrue("Deleted item is still in Collection.", results.getCurrentPageSize() == 0);
	}

}

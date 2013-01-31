package com.storageroomapp.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.storageroomapp.client.field.GenericField;

public class CollectionQueryDynamicTest {

	@Test
	public void testQueryAll() {
		Application app = Application.getInstance("JavaClient");
		boolean success = app.connect(StorageRoomTestEnv.JavaClientReadWrite_AccountId, StorageRoomTestEnv.JavaClientReadWrite_AuthToken, true);
		assertTrue("Could not connect to StorageRoom, perhaps service is down or account "+StorageRoomTestEnv.JavaClientReadWrite_AccountId+" is no longer valid.", success);
		Collections colls = app.getCollections(true);
		Collection col = colls.findCollection("ReadOnlyCollection");
		
		CollectionEntries entries = col.getEntries();
		PageOfEntries results = entries.queryAll();
		assertNotNull("Query for all items in ReadOnlyCollection failed.", results);
		assertTrue("Query for all items in ReadOnlyCollection returned incorrect number of results.", results.getCurrentPageSize() == 1);
		Entry entry = results.asList().get(0);
		for (GenericField<?> field : entry.values()) {
			String id = field.getIdentifier();
			if ("sku".equals(id)) {
				assertEquals("The SKU did not match.", "534534TRTE", field.getValueWrapper().getInnerValue());
			} else if ("@created_at".equals(id)) {
				// TODO: StorageRoom broke something (Jan25 2013), the times are coming back 24:00:00
				//Calendar cal = (Calendar)field.getValueWrapper().getInnerValue();
				//String createDateStr = StorageRoomUtil.calendarToStorageRoomTimeString(cal);
				//assertEquals("The Date did not match.", "2013-01-15T06:47:57Z", createDateStr);
			}
		}
	}

	@Test
	public void testQuery() {
		//fail("Not yet implemented");
	}

}

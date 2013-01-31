package com.storageroomapp.client;

import static org.junit.Assert.*;

import org.junit.Test;

public class SerializationTest {

	@Test
	public void testToString() {
		Application app = Application.getInstance("JavaClient");
		boolean success = app.connect(StorageRoomTestEnv.JavaClientReadWrite_AccountId, StorageRoomTestEnv.JavaClientReadWrite_AuthToken, true);
		assertTrue("Could not connect to StorageRoom, perhaps service is down or account "+StorageRoomTestEnv.JavaClientReadWrite_AccountId+" is no longer valid.", success);
		Collections colls = app.getCollections(true);
		Collection col = colls.findCollection("ReadWriteCollection");
		
		CollectionEntries entries = col.getEntries();
		PageOfEntries results = entries.queryAll();
		for (Entry entry : results.asIterable()) {
			System.out.println("TOSTRING: "+entry.toString());
		}
	}

	@Test
	public void testToJSON() {
		Application app = Application.getInstance("JavaClient");
		boolean success = app.connect(StorageRoomTestEnv.JavaClientReadWrite_AccountId, StorageRoomTestEnv.JavaClientReadWrite_AuthToken, true);
		assertTrue("Could not connect to StorageRoom, perhaps service is down or account "+StorageRoomTestEnv.JavaClientReadWrite_AccountId+" is no longer valid.", success);
		Collections colls = app.getCollections(true);
		Collection col = colls.findCollection("ReadWriteCollection");
		
		CollectionEntries entries = col.getEntries();
		PageOfEntries results = entries.queryAll();
		for (Entry entry : results.asIterable()) {
			System.out.println("JSON: "+entry.toJSONString(true));
		}
	}
}

package com.storageroomapp.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

public class CollectionDynamicTest {

	
	static private final String collectionsUrl = "http://api.storageroomapp.com/accounts/50ef2fcc0f6602017a000787/collections";
	@Test
	public void testGetCollections() {
		Application app = Application.getInstance("JavaClient");
		boolean success = app.connect(StorageRoomTestEnv.JavaClientReadWrite_AccountId, StorageRoomTestEnv.JavaClientReadWrite_AuthToken, true);
		assertTrue("Could not connect to StorageRoom, perhaps service is down or account "+StorageRoomTestEnv.JavaClientReadWrite_AccountId+" is no longer valid.", success);
		Collections colls = app.getCollections(false);
		
		assertNotNull("Could not retrieve Collections from StorageRoom, perhaps service is down or account "+StorageRoomTestEnv.JavaClientReadWrite_AccountId+" is no longer valid.", colls);
		assertTrue("There should be two collections detected in the parsed json. Or the online StorageRoom account is not setup as expected.", colls.size() > 1);
		assertEquals("We failed to properly read the Collections URL out of the parsed json. Or the online StorageRoom account is not setup as expected.", 
				collectionsUrl, colls.getUrl());
	}

	@Test
	public void testGetReadOnlyCollection() {
		Application app = Application.getInstance("JavaClient");
		boolean success = app.connect(StorageRoomTestEnv.JavaClientReadWrite_AccountId, StorageRoomTestEnv.JavaClientReadWrite_AuthToken, true);
		assertTrue("Could not connect to StorageRoom, perhaps service is down or account "+StorageRoomTestEnv.JavaClientReadWrite_AccountId+" is no longer valid.", success);
		Collections colls = app.getCollections(false);
		
		assertNotNull("Could not retrieve Collections from StorageRoom, perhaps service is down or account "+StorageRoomTestEnv.JavaClientReadWrite_AccountId+" is no longer valid.", colls);
		assertTrue("There should be two collections detected in the parsed json. Or the online StorageRoom account is not setup as expected.", colls.size() > 1);
		assertEquals("We failed to properly read the Collections URL out of the parsed json. Or the online StorageRoom account is not setup as expected.", 
				collectionsUrl, colls.getUrl());
	}

	@Test
	@Ignore
	// See method note, this feature is not implemented yet on StorageRoom
	public void testCollectionEmptyTrash() {
		Application app = Application.getInstance("JavaClient");
		boolean success = app.connect(StorageRoomTestEnv.JavaClientReadWrite_AccountId, StorageRoomTestEnv.JavaClientReadWrite_AuthToken, true);
		assertTrue("Could not connect to StorageRoom, perhaps service is down or account "+StorageRoomTestEnv.JavaClientReadWrite_AccountId+" is no longer valid.", success);
		Collections colls = app.getCollections(false);
		
		assertNotNull("Could not retrieve Collections from StorageRoom, perhaps service is down or account "+StorageRoomTestEnv.JavaClientReadWrite_AccountId+" is no longer valid.", colls);
		assertEquals("We failed to properly read the Collections URL out of the parsed json. Or the online StorageRoom account is not setup as expected.", 
				collectionsUrl, colls.getUrl());
		Collection col = colls.findCollection("ReadWriteCollection");
		assertNotNull("Could not locate the ReadWriteCollection, perhaps it has been deleted?", col);

		success = col.emptyTrash();
		assertTrue("Unsuccessful deleting the trash.", success);
	}
}

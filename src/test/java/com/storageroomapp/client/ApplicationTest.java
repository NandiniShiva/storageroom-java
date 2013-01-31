package com.storageroomapp.client;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class ApplicationTest {

	@Test
	public void testGetInstance() {
		Application app = Application.getInstance("myTestApp");
		Application app2 = Application.getInstance("myTestApp");
		assertTrue("Application.getInstance() should be handing out the same instance for the same app name.", app == app2);
	}

	@Test
	@Ignore // we don't test this since it is covered in so many other places
	public void testConnectSingleApplication() {
		//Application app = Application.getInstance("JavaClient");
		//AccountInfo ai = app.connect(StorageRoomEnv.accountId, StorageRoomEnv.authToken);		
	}

	@Test
	public void testConnectMultipleApplications() {
		Application app = Application.getInstance("JavaClientRW");
		app.connect(StorageRoomTestEnv.JavaClientReadWrite_AccountId, StorageRoomTestEnv.JavaClientReadWrite_AuthToken, false);
		AccountInfo ai = app.getAccountInfo(true);
		assertNotNull("Could not connect to StorageRoom, perhaps service is down or account "+StorageRoomTestEnv.JavaClientReadWrite_AccountId+" is no longer valid.", ai);

		Application app2 = Application.getInstance("JavaClientReadOnly");
		app2.connect(StorageRoomTestEnv.JavaClientReadOnly_AccountId, StorageRoomTestEnv.JavaClientReadOnly_AuthToken, false);
		AccountInfo ai2 = app.getAccountInfo(true);
		assertNotNull("Could not connect to StorageRoom, perhaps service is down or account "+StorageRoomTestEnv.JavaClientReadOnly_AccountId+" is no longer valid.", ai2);

		// the two apps should be storing independent metadata
		assertEquals("Multiple concurrent connected Applications may not be working (or StorageRoom account/API is inactive).", "storageroomclient", ai.getName());
	}
}

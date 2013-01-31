package com.storageroomapp.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.storageroomapp.client.util.StorageRoomUtil;

public class AccountInfoTest {

	@Test
	public void testConnect() {
		Application app = Application.getInstance("JavaClient");
		app.connect(StorageRoomTestEnv.JavaClientReadWrite_AccountId, StorageRoomTestEnv.JavaClientReadWrite_AuthToken, true);
		AccountInfo ai = app.getAccountInfo(true);
		
		assertNotNull("Could not connect to StorageRoom, perhaps service is down or account "+StorageRoomTestEnv.JavaClientReadWrite_AccountId+" is no longer valid.", ai);
		assertEquals("The name of the Storage Room account is not as expected.", "storageroomclient", ai.getName());
		assertEquals("The subdomain of the Storage Room account is not as expected.", "storageroomclient", ai.getSubdomain());
		
		String createdStr = StorageRoomUtil.calendarToStorageRoomTimeString(ai.getCreated());
		assertEquals("The created date of the Storage Room account is not as expected. This might be ok - did you change the account JUnit is using?", "2013-01-10T21:17:00Z", createdStr);
	}

	@Test
	public void testCachedConnect() {
		Application app = Application.getInstance("JavaClient");
		app.connect(StorageRoomTestEnv.JavaClientReadWrite_AccountId, StorageRoomTestEnv.JavaClientReadWrite_AuthToken, true);
		AccountInfo ai = app.getAccountInfo(true);
		assertNotNull("Could not connect to StorageRoom, perhaps service is down or account "+StorageRoomTestEnv.JavaClientReadWrite_AccountId+" is no longer valid.", ai);
		
		// now connect again, with a bad auth token, but with Application caching turned on. this should succeed as the Application details are already cached
		app.connect(StorageRoomTestEnv.JavaClientReadWrite_AccountId, StorageRoomTestEnv.JavaClientReadWrite_AuthToken, true);
		ai = app.getAccountInfo(true);
		assertNotNull("Application object is not properly caching the ", ai);
	}

	@Test
	public void testNotCachedConnect() {
		Application app = Application.getInstance("JavaClient");
		app.connect(StorageRoomTestEnv.JavaClientReadWrite_AccountId, StorageRoomTestEnv.JavaClientReadWrite_AuthToken, true);
		AccountInfo ai = app.getAccountInfo(true);
		assertNotNull("Could not connect to StorageRoom, perhaps service is down or account "+StorageRoomTestEnv.JavaClientReadWrite_AccountId+" is no longer valid.", ai);
		
		// now connect again, with a bad auth token, and Application caching turned off. this should still retain the old info
		app.connect(StorageRoomTestEnv.JavaClientReadWrite_AccountId, StorageRoomTestEnv.JavaClientReadWrite_AuthToken, true);
		ai = app.getAccountInfo(true);
		assertNotNull("Application object is not properly caching the ", ai);
	}
}

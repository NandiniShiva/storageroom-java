package com.storageroomapp.client.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.storageroomapp.client.Application;
import com.storageroomapp.client.Collection;
import com.storageroomapp.client.CollectionEntries;
import com.storageroomapp.client.Collections;
import com.storageroomapp.client.Entry;
import com.storageroomapp.client.StorageRoomTestEnv;
import com.storageroomapp.client.field.IntegerField;
import com.storageroomapp.client.field.IntegerValue;
import com.storageroomapp.client.field.StringField;
import com.storageroomapp.client.field.StringValue;
import com.storageroomapp.client.util.FileUtil;

public class CommandLineTest {
	
	static private final String ACCT = StorageRoomTestEnv.JavaClientReadWrite_AccountId;
	static private final String AUTH = StorageRoomTestEnv.JavaClientReadWrite_AuthToken;
	static private final String COL_RW = "ReadWriteCollection";
	
	static private final String LIST = StorageRoomClient.OP_LISTALLINCOLLECTION;
	static private final String GETANDDEL = StorageRoomClient.OP_GETANDDELETECOLLECTION;
	

	@Test
	public void testParseGoodCommandLine() {
		String[] args = {AUTH, ACCT, LIST, COL_RW};
		
		StorageRoomClient src = StorageRoomClient.parseCommandLine(args);
		assertNotNull("A good command line did not parse correctly.", src);
		assertEquals("Account was not set propertly.", src.accountId, ACCT);
		assertEquals("AuthToken was not set propertly.", src.authToken, AUTH);
		assertEquals("Operation was not set propertly.", src.operation, LIST);
		assertEquals("Account was not set propertly.", src.collectionName, COL_RW);
	}

	@Test
	public void testParseBadCommandLine() {
		String[] args0 = {};
		
		StorageRoomClient src = StorageRoomClient.parseCommandLine(args0);
		assertNull("A bad command line did not fail to parse.", src);

		String[] args1 = {AUTH};
		src = StorageRoomClient.parseCommandLine(args1);
		assertNull("A bad command line did not fail to parse.", src);

		String[] args2 = {ACCT, AUTH};
		src = StorageRoomClient.parseCommandLine(args2);
		assertNull("A bad command line did not fail to parse.", src);
	}
	
	@Test
	public void testListRun() {
		String[] args = {AUTH, ACCT, LIST, COL_RW};
		StorageRoomClient src = StorageRoomClient.parseCommandLine(args);

		boolean success = src.run();
		assertTrue("The StorageRoomClient listAll command did not work.", success);
	}

	@Test
	public void testListAndDeleteRun() {
		System.out.println("testListAndDeleteRun");

		Application app = Application.getInstance("JavaClient");
		boolean success = app.connect(StorageRoomTestEnv.JavaClientReadWrite_AccountId, StorageRoomTestEnv.JavaClientReadWrite_AuthToken, true);
		assertTrue("Could not connect to StorageRoom, perhaps service is down or account "+StorageRoomTestEnv.JavaClientReadWrite_AccountId+" is no longer valid.", success);
		Collections colls = app.getCollections(true);
		Collection col = colls.findCollection("ReadWriteCollection");
		CollectionEntries entries = col.getEntries();
		
		// INSERT a bunch of stuff
		System.out.println("inserting a bunch of records");
		String insertedSKU = "JUNIT"+System.currentTimeMillis();
		for (int i =0; i<40; i++) {
			Entry newEntry = entries.createNewEntryTemplateObject();
			StringField sku = (StringField)newEntry.get("sku");
			sku.setValue(new StringValue(insertedSKU+"_"+i));
			IntegerField inStock = (IntegerField)newEntry.get("in_stock");
			inStock.setValue(new IntegerValue(i));
			success = entries.insertNewEntry(newEntry);
			assertTrue("Could not insert an entry into the ReadWriteCollection", success);
		}		
		
		String outputFolderPath = "/tmp/JUNIT_STORAGE_ROOM";
		File outputFolder = new File(outputFolderPath);
		if (outputFolder.exists()) {
			FileUtil.deleteFileTree(outputFolder);
		}
		outputFolder.mkdir();
		
		String[] args = {AUTH, ACCT, GETANDDEL, COL_RW, outputFolderPath};
		StorageRoomClient src = StorageRoomClient.parseCommandLine(args);

		success = src.run();
		assertTrue("The StorageRoomClient GetAndDelete command did not work.", success);
	}
}

package com.storageroomapp.client.field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.storageroomapp.client.Application;
import com.storageroomapp.client.Collection;
import com.storageroomapp.client.CollectionEntries;
import com.storageroomapp.client.CollectionQuery;
import com.storageroomapp.client.Collections;
import com.storageroomapp.client.Entry;
import com.storageroomapp.client.PageOfEntries;
import com.storageroomapp.client.StorageRoomTestEnv;

public class FileFieldTest {

	@Test
	public void testGetFileAsStream() {
		Application app = Application.getInstance("JavaClient");
		boolean success = app.connect(StorageRoomTestEnv.JavaClientReadWrite_AccountId, StorageRoomTestEnv.JavaClientReadWrite_AuthToken, true);
		assertTrue("Could not connect to StorageRoom, perhaps service is down or account "+StorageRoomTestEnv.JavaClientReadWrite_AccountId+" is no longer valid.", success);
		Collections colls = app.getCollections(true);
		Collection col = colls.findCollection("ReadWriteCollection");
		CollectionEntries entries = col.getEntries();
		
		CollectionQuery queryDef = new CollectionQuery();
		queryDef.filterOptions = "sku=JUNIT_DONOTDELETE";
		PageOfEntries results = entries.query(queryDef);
		Entry result = results.asList().get(0);
		
		FileField fileField = (FileField)result.get("product_specs");
		FileValue fileValue = (FileValue)fileField.getValueWrapper();
		String text = fileValue.getFileAsString();
		assertNotNull("Text did not come back", text);
	}

	@Test
	public void testGetFileAsFile() {
		Application app = Application.getInstance("JavaClient");
		boolean success = app.connect(StorageRoomTestEnv.JavaClientReadWrite_AccountId, StorageRoomTestEnv.JavaClientReadWrite_AuthToken, true);
		assertTrue("Could not connect to StorageRoom, perhaps service is down or account "+StorageRoomTestEnv.JavaClientReadWrite_AccountId+" is no longer valid.", success);
		Collections colls = app.getCollections(true);
		Collection col = colls.findCollection("ReadWriteCollection");
		CollectionEntries entries = col.getEntries();
		
		CollectionQuery queryDef = new CollectionQuery();
		queryDef.filterOptions = "sku=JUNIT_DONOTDELETE";
		PageOfEntries results = entries.query(queryDef);
		Entry result = results.asList().get(0);
		
		FileField fileField = (FileField)result.get("product_specs");
		FileValue fileValue = (FileValue)fileField.getValueWrapper();
		File outfile = new File("/tmp/SRtest_outfile.txt");
		success = fileValue.getFileAsFile(outfile);
		assertTrue("Text did not come back", success);
	}
	
	@Test
	public void testFileExtension() {
		FileValue value = new FileValue(null);
		value.innerValue = "asdfasdfasdfsadf.png";
		assertEquals("'png' not returned for field extension", "png", value.getFileExtension());
	}
}

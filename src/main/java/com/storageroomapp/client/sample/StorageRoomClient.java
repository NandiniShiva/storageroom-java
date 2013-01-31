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
package com.storageroomapp.client.sample;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.storageroomapp.client.Application;
import com.storageroomapp.client.Collection;
import com.storageroomapp.client.CollectionEntries;
import com.storageroomapp.client.Collections;
import com.storageroomapp.client.Entry;
import com.storageroomapp.client.PageOfEntries;
import com.storageroomapp.client.field.BooleanField;
import com.storageroomapp.client.field.FileField;
import com.storageroomapp.client.field.FileValue;
import com.storageroomapp.client.field.GenericField;
import com.storageroomapp.client.field.ImageField;
import com.storageroomapp.client.field.ImageValue;
import com.storageroomapp.client.field.ImageVersion;
import com.storageroomapp.client.util.FileUtil;

/**
 * This is a command line client that operates scripted use cases against
 * the StorageRoom API using the Java StorageRoom framework.
 */
public class StorageRoomClient {
	static private Log log = LogFactory.getLog(StorageRoomClient.class);

	protected String[] args = null;
	protected String applicationName = "JavaClient";
	protected String authToken = null;
	protected String accountId = null;
	protected String operation = null;
	protected String collectionName = null;
	
	// Operations
	
	static public final String OP_VERIFYCONNECT = "verifyConnection";
	static public final String OP_LISTALLINCOLLECTION = "listAll";
	static public final String OP_GETANDDELETECOLLECTION = "getAndDelete";

	static private boolean doesOpRequireCollectionArg(String operation) {
		boolean needsCol = false;
		
		if (OP_LISTALLINCOLLECTION.equals(operation)) {
			needsCol = true;
		} else if (OP_GETANDDELETECOLLECTION.equals(operation)) {
			needsCol = true;
		}
		return needsCol;
	}
	
	/**
	 * @param args
	 */
	static public void main(String[] args) {
		StorageRoomClient src = StorageRoomClient.parseCommandLine(args);
		if (src == null) {
			log.error("Error with command line.\nParameters: authtoken accountid "+OP_VERIFYCONNECT+"|"+OP_LISTALLINCOLLECTION+
					"|"+OP_GETANDDELETECOLLECTION+" [collection-name]");
			System.exit(1);
		}
		src.run();
	}

	public boolean run() {
		boolean success = true;
		
		Application app = Application.getInstance("JavaClient");
		success = app.connect(accountId, authToken, false);

		if (OP_VERIFYCONNECT.equals(operation)) {
			if (!success) {
				return false;
			}
		} else {
			Collections colls = app.getCollections(true);
			Collection col = colls.findCollection(collectionName);
			if (col == null) {
				return false;
			}
			if (OP_LISTALLINCOLLECTION.equals(operation)) {
				success = listCollection(col);
			} else if (OP_GETANDDELETECOLLECTION.equals(operation)) {
				success = getAndDeleteCollection(col, args);
			}
		}		
		return success;
	}
	
	private boolean listCollection(Collection col) {
		CollectionEntries entries = col.getEntries();
		PageOfEntries results = entries.queryAll();

		int currentPage = 0;
		boolean done = false;
		while (!done) {
			for (Entry entry : results.asIterable()) {
				log.info(entry.toJSONString(true));
			}
			currentPage++;
			if (currentPage < results.getNumResultsPages()) {
				results = results.jumpPage(currentPage);
				if (results == null) {
					done = true;
				}
			} else {
				done = true;
			}
		}
		return true;
	}
	
	private boolean getAndDeleteCollection(Collection col, String[] args) {
		boolean success = true;
		
		if (args.length < 5) {
			log.error("Directory name on command line does not exist.");
			return false;
		}
		String folderToWriteDataPath = args[4];
		
		File folderToWriteData = new File(folderToWriteDataPath);
		if (!folderToWriteData.exists()) {
			log.error("Directory ["+folderToWriteDataPath+"] does not exist.");
			return false;
		}
		if (!folderToWriteData.canWrite()) {
			log.error("Directory ["+folderToWriteDataPath+"] is not writable.");
			return false;
		}
		
		String pk = col.getPk();
		CollectionEntries entries = col.getEntries();
		PageOfEntries results = entries.queryAll();
		
		boolean done = false;
		while (!done) {
			for (Entry entry : results.asIterable()) {
				
				String entryPK = null;
				if (pk != null) {
					GenericField<?> entryPKField = entry.get(pk);
					entryPK = entryPKField.getValueWrapper().toString();
				} 
				if (entryPK == null) {
					entryPK = entry.getInternalUniqueIdentifier();
				}
				
				// write out the json data
				String entryAsString = entry.toJSONString(true);
				File entryFile = new File(folderToWriteData, entryPK+".json");
				FileUtil.writeStringToFile(entryFile, entryAsString);
				
				// write out any File/Image fields as separate files
				for (GenericField<?> field : entry.asListOfDataFieldsOnly()) {
					if (field.isCompoundFieldType()) {
						if (field instanceof FileField) {
							FileField fileField = (FileField)field;
							StorageRoomClient.writeFileFieldToFile(entryPK, fileField, folderToWriteData);
						} else if (field instanceof ImageField) {
							ImageField imageField = (ImageField)field;
							StorageRoomClient.writeImageFieldToFiles(entryPK, imageField, folderToWriteData);
						}
					}
				}
				
				BooleanField protectFromAutoDelete = (BooleanField)entry.get("do_not_auto_delete");
				if ((protectFromAutoDelete == null) || !protectFromAutoDelete.getValueWrapper().getInnerValue()) {
					log.info("Deleting Item ["+entry.getName()+"]");
					entry.delete();
				}
			}
			if (results.getNumResultsPages() == 1) {
				// done = true; // the natural way
				try { Thread.sleep(15*60*1000); } catch (InterruptedException ie) {}
			} else {
				results = results.jumpPage(0);
				if (results == null) {
					// nothing left
					try { Thread.sleep(15*60*1000); } catch (InterruptedException ie) {}
				}
			}
		}
		return success;
	}
	
	static public StorageRoomClient parseCommandLine(String[] args) {

		if (args.length < 3) {
			return null;
		}
		
		StorageRoomClient src = new StorageRoomClient();
		src.args = args;
		src.authToken = args[0];
		src.accountId = args[1];
		src.operation = args[2];
		boolean needsCol = doesOpRequireCollectionArg(src.operation);
		if (needsCol) {
			if (args.length > 3) {
				src.collectionName = args[3];
			} else {
				return null;
			}
		}
		return src;
	}
	
	
	static private void writeFileFieldToFile(String entryPK, FileField fileField, File folderToWriteData) {
		FileValue fileFieldValue = fileField.getValueWrapper();
		String fileExtension = fileFieldValue.getFileExtension();
		String fieldFilename = entryPK+"_"+fileField.getIdentifier()+"."+fileExtension;
		File fieldFile = new File(folderToWriteData, fieldFilename);
		fileFieldValue.getFileAsFile(fieldFile);
	}
	
	static private void writeImageFieldToFiles(String entryPK, ImageField imageField, File folderToWriteData) {
		ImageValue imageFieldValue = imageField.getValueWrapper();
		
		// write the original image to file
		String fileExtension = imageFieldValue.getFileExtension();
		String fieldFilename = entryPK+"_"+imageField.getIdentifier()+"."+fileExtension;
		File fieldFile = new File(folderToWriteData, fieldFilename);
		imageFieldValue.getImageAsFile(fieldFile);
		
		// now write the versions to file
		// this is not really necessary, but it is a nice example
		List<ImageVersion> versions = imageFieldValue.getVersions();
		for (ImageVersion version : versions) {
			fileExtension = version.getFileExtension();
			fieldFilename = entryPK+"_"+imageField.getIdentifier()+"_"+version.identifier+"."+fileExtension;
			fieldFile = new File(folderToWriteData, fieldFilename);
			imageFieldValue.getImageAsFile(fieldFile);
		}
	}
	
	private StorageRoomClient() {
	}
	
}

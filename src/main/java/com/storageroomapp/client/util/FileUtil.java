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
package com.storageroomapp.client.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Mundane file and url utilities.
 * 
 * @author plaird
 *
 */
public class FileUtil {
	static private Log log = LogFactory.getLog(FileUtil.class);
	
	static public boolean writeStringToFile(File fileToWrite, String text) {
		InputStream is = new ByteArrayInputStream(text.getBytes());
		return writeStreamToFile(fileToWrite, is, true);
	}
	
	static public boolean writeStreamToFile(File fileToWrite, InputStream is, boolean closeIS) {
		boolean success = true;
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fileToWrite);
			if (is != null) {
				byte[] buffer = new byte[16384];
				int len;
				while ((len = is.read(buffer)) != -1) {
				    fos.write(buffer, 0, len);
				}				
			}
		} catch (IOException ioe) {
			log.error("Error writing a stream to a file", ioe);
			success = false;
		} finally {
			if (closeIS && (is != null)) {
				try { is.close(); } catch (IOException ioe) {}
			}
			if (fos != null) {
				try { fos.close(); } catch (IOException ioe) {}
			}
		}
		
		return success;
	}
	
	static public void deleteFileTree(File file) {
    	if(file.isDirectory()) {
    		if(file.list().length==0){
    		   file.delete();
    		} else {
        	   String files[] = file.list();
        	   for (String cur : files) {
        	      File fileDelete = new File(file, cur);
        	      deleteFileTree(fileDelete);
        	   }
        	   if(file.list().length==0) {
        		   file.delete();
        	   }
    		}
    	} else {
    		file.delete();
    	}
	}
	
	static public String getDotExtension(String fileOrUrl) {
		if (fileOrUrl == null) {
			return null;
		}
		int lastDot = fileOrUrl.lastIndexOf(".");
		String ext = null;
		if (lastDot > 0) {
			ext = fileOrUrl.substring(lastDot+1);
		}
		return ext;
	}
	
	static public String getLastUrlPath(String url) {
		if (url == null) {
			return null;
		}
		int lastDot = url.lastIndexOf("/");
		String ext = null;
		if (lastDot > 0) {
			ext = url.substring(lastDot+1);
		}
		return ext;
	}

}

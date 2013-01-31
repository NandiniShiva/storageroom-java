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
package com.storageroomapp.client.field;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.simple.JSONObject;

import com.storageroomapp.client.util.FileUtil;
import com.storageroomapp.client.util.Http;
import com.storageroomapp.client.util.JsonSimpleUtil;

public class FileValue extends GenericValue<String> {
	
	protected FileField parentField = null;
	
	// url is stored in the superclass innerValue slot
	
	// Unfortunately, File fields look entirely different on GET and POST/PUT
	protected boolean isObjectConfiguredForUpload = false;	
	// PUT/POST
	protected String contentType = null;
	protected String filename = null;
	protected String dataString = null;
	protected InputStream dataStream = null;
	
	// CTORS

	public FileValue() {
		this.parentField = null;
	}
	
	protected FileValue(FileField parentField) {
		this.parentField = parentField;
	}
	
	public FileValue(FileField parentField, String contentType, String filename, 
			String dataString) {
		this.parentField = parentField;
		this.contentType = contentType;
		this.filename = filename;
		this.dataString = dataString;
	}
	
	
	// DESERIALIZATION
	
	/*
	 * GET
	 {
	   "@type":"File",
	   "@url":"http://files.storageroomapp.com/accounts/50eb30ec0f66024ee5001733/collection/50edda060f66020e0c000126/entries/50ede60e0f6602564800105b/fields/k50edda060f66020e0c000128/file.r"
	 }
	 */
	static public FileValue parseJSONObject(FileField parentField, JSONObject jsonObj) {
		if (jsonObj == null) {
			return null;
		}
		String url = JsonSimpleUtil.parseJsonStringValue(jsonObj, "@url");
		if (url == null) {
			return null;
		}
		
		FileValue value = new FileValue(parentField);
		value.isObjectConfiguredForUpload = false;
		value.innerValue = url;
		return value;
	}
	
	// client closes stream
	public InputStream getFileAsStream() {
		if (isObjectConfiguredForUpload) {
			throw new IllegalStateException();
		}
		InputStream istream = Http.get(this.innerValue);
		return istream;
	}

	public boolean getFileAsFile(File fileToWrite) {
		if (isObjectConfiguredForUpload) {
			throw new IllegalStateException();
		}
		
		InputStream is = getFileAsStream();
		boolean success = FileUtil.writeStreamToFile(fileToWrite, is, true);
		
		return success;
	}
	
	public String getFileAsString() {
		if (isObjectConfiguredForUpload) {
			throw new IllegalStateException();
		}
		String result = null;
		InputStream is = null;
		try {
			is = getFileAsStream();
			result = deserializeStream(is);
		} catch (Exception e) {
			// TODO log
		} finally {
			if (is != null) {
				try { is.close(); } catch (IOException ioe) {}
			}
		}
		return result;
	}

	// SERIALIZATION
	
	public String toString() {
		String result = null;
		if (isObjectConfiguredForUpload) {
			// TODO
		} else {
			result = "File url ["+innerValue+"]";
		}
		return result;
	}
	
	@Override
	public String toJSONString() {
		StringBuilder sb = new StringBuilder();
		if (isObjectConfiguredForUpload) {
			// TODO base64 encode the payload
		} else {
			sb.append("{ \"@type\":\"File\", \"@url\": \"");
			sb.append(innerValue);
			sb.append("\" }");
		}
		return sb.toString();
	}
	
	// GETTERS AND SETTERS
	
	
	public boolean isObjectConfiguredForUpload() {
		return isObjectConfiguredForUpload;
	}

	public String getUrl() {
		if (isObjectConfiguredForUpload) {
			return null;
		}
		return innerValue;
	}

	public String getFileExtension() {
		if (isObjectConfiguredForUpload || (innerValue == null)) {
			return null;
		}
		return FileUtil.getDotExtension(innerValue);
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setDataString(String dataString) {
		this.dataString = dataString;
		this.dataStream = null;
	}

	public void setDataStream(InputStream dataStream) {
		this.dataStream = dataStream;
		this.dataString = null;
	}	

	// INTERNAL

	static private String deserializeStream(InputStream is) {
		if (is == null) {
			return null;
		}
	   	StringBuilder sb = new StringBuilder();
	    BufferedReader br = null;
		try {
		   	br	= new BufferedReader(new InputStreamReader(is));
 		   	String line;
	    	while ((line = br.readLine()) != null) {
	    		sb.append(line);
	    	}
		} catch (Exception e) {
			// TODO log
			e.printStackTrace();
	    } 
	
		return sb.toString();
	}
	
}

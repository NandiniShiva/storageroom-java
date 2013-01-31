package com.storageroomapp.client.field;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.json.simple.JSONObject;

import com.storageroomapp.client.util.FileUtil;
import com.storageroomapp.client.util.Http;
import com.storageroomapp.client.util.JsonSimpleUtil;

public class ImageValue extends GenericValue<String> {

	protected ImageField parentField = null;

	// Unfortunately, Image fields look entirely different on GET and POST/PUT
	protected boolean isObjectConfiguredForUpload = false;
	
	protected boolean isProcessing = false;
	protected List<ImageVersion> versions = null;
	
	public ImageValue() {
		this.parentField = null;
	}
	
	public ImageValue(ImageField parentField, String url) {
		this.parentField = parentField;
		this.innerValue = url;
		this.isObjectConfiguredForUpload = false;
	}


	// DESERIALIZATION
	
	/*
	 * GET
	 {
	   "@type":"File",
	   "@url":"http://files.storageroomapp.com/accounts/50eb30ec0f66024ee5001733/collection/50edda060f66020e0c000126/entries/50ede60e0f6602564800105b/fields/k50edda060f66020e0c000128/file.r"
	 }
	 */
	static public ImageValue parseJSONObject(ImageField parentField, JSONObject jsonObj) {
		if (jsonObj == null) {
			return null;
		}
		String url = JsonSimpleUtil.parseJsonStringValue(jsonObj, "@url");
		if (url == null) {
			return null;
		}
		
		ImageValue value = new ImageValue(parentField, url);
		value.isProcessing = JsonSimpleUtil.parseJsonBooleanValue(jsonObj, "@processing", false);
		
		JSONObject versionObject = (JSONObject)jsonObj.get("@versions");
		if (versionObject != null) {
			value.versions = ImageVersion.parseJSONListObject(versionObject);
		}
		return value;
	}
	
	// client closes stream
	public InputStream getImageAsStream() {
		if (isObjectConfiguredForUpload) {
			throw new IllegalStateException();
		}
		InputStream istream = Http.get(this.innerValue);
		return istream;
	}

	public boolean getImageAsFile(File fileToWrite) {
		if (isObjectConfiguredForUpload) {
			throw new IllegalStateException();
		}
		
		InputStream is = getImageAsStream();
		boolean success = FileUtil.writeStreamToFile(fileToWrite, is, true);
		
		return success;
	}
	
	// SERIALIZATION
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" imageUrl [");
		sb.append(innerValue);
		sb.append("] ");
		
		sb.append(" versions [");
		for (ImageVersion version : versions) {
			sb.append(version.toString());
		}
		sb.append("] ");
				
		return sb.toString();
	}
	
	public String toJSONString() {
		return "FIXME ImageFieldValue";
	}

	
	// GETTERS
	
	public String getUrl() {
		return innerValue;
	}

	public String getFileExtension() {
		if (isObjectConfiguredForUpload || (innerValue == null)) {
			return null;
		}
		return FileUtil.getDotExtension(innerValue);
	}

	public boolean isProcessing() {
		return isProcessing;
	}

	public List<ImageVersion> getVersions() {
		return versions;
	}

	
}

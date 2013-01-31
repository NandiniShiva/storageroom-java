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

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;

import com.storageroomapp.client.util.FileUtil;
import com.storageroomapp.client.util.Http;

public class ImageVersion {
	public String identifier = null;
	public String url = null;
	
	public ImageVersion(String identifier, String url) {
		this.identifier = identifier;
		this.url = url;
	}
	
	static public List<ImageVersion> parseJSONListObject(JSONObject jsonObj) {
		@SuppressWarnings("unchecked")
		Set<String> identifiers = (Set<String>)jsonObj.keySet();
		
		List<ImageVersion> versions = new ArrayList<ImageVersion>();
		for (String identifier : identifiers) {
			JSONObject versionObject = (JSONObject)jsonObj.get(identifier);
			String url = (String)versionObject.get("@url");
			if (url != null) {
				ImageVersion newVersion = new ImageVersion(identifier, url);
				versions.add(newVersion);
			}
		}
		return versions;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" ");
		sb.append(identifier);
		sb.append(" [");
		sb.append(url);
		sb.append("]");
		
		return sb.toString();
	}
	
	public String toJSONString() {
		return "FIXME ImageVersion";
	}
	
	// client closes stream
	public InputStream getImageAsStream() {
		InputStream istream = Http.get(this.url);
		return istream;
	}

	public boolean getImageAsFile(File fileToWrite) {
		InputStream is = getImageAsStream();
		boolean success = FileUtil.writeStreamToFile(fileToWrite, is, true);
		
		return success;
	}
	
	public String getFileExtension() {
		if (url == null) {
			return null;
		}
		return FileUtil.getDotExtension(url);
	}
}

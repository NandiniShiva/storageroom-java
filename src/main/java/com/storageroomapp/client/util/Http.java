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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;

/**
 * Abstraction class for accessing HTTP endpoints using verbs.
 * 
 * The default implementation uses org.apache.http.* as a dependency
 * but feel free to replace it with your own.
 */
public class Http {
	static private Log log = LogFactory.getLog(Http.class);

	/**
	 * GET the payload for the given URL as a String
	 * 
	 * @param url a String url
	 * @return a String if successful, null otherwise
	 */
	static public String getAsString(String url) {
		String body = null;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			int code = response.getStatusLine().getStatusCode();
			String reason = response.getStatusLine().getReasonPhrase();
			if (entity != null) {
				InputStream instream = entity.getContent();
				body = deserializeBody(instream);
				if (log.isDebugEnabled()) {
					log.debug("Http.getAsString url ["+url+"] response code ["+code+"] reason ["+reason+"] body ["+body+"]");
				}
			} else {
				if (log.isDebugEnabled()) {
					log.debug("Http.getAsString url ["+url+"] response code ["+code+"] reason ["+reason+"] body [nothing returned]");
				}
			}
		} catch (Exception e) {
			log.error("Http.getAsString failed, with url ["+url+"]", e);
		}
		return body;
	}

	/**
	 * GET the payload for the given URL as an Inputstream
	 * 
	 * NOTE: caller must close the stream
	 * @param url a String url
	 * @return an Inputstream if successful, null otherwise
	 */
	static public InputStream get(String url) {
		InputStream instream = null;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			int code = response.getStatusLine().getStatusCode();
			String reason = response.getStatusLine().getReasonPhrase();
			if (entity != null) {
				instream = entity.getContent();
				if (log.isDebugEnabled()) {
					log.debug("Http.get url ["+url+"] response code ["+code+"] reason ["+reason+"] body [input stream]");
				}
			} else {
				if (log.isDebugEnabled()) {
					log.debug("Http.get url ["+url+"] response code ["+code+"] reason ["+reason+"] body [nothing returned]");
				}
			}	
		} catch (Exception e) {
			log.error("Http.get failed, with url ["+url+"]", e);
		}
		return instream;
	}

	
	/**
	 * POST to the url with the provided body.
	 * @param url a String url
	 * @param body a String with text for the request body
	 * @return true if successful (response code < 400), false otherwise
	 */
	static public boolean post(String url, String body) {
		boolean success = false;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			HttpEntity entity = new StringEntity(body);
			httppost.setEntity(entity);

			// Be sure to add these headers or the StRoom API will NOT work! 
			Header contentType = new BasicHeader("Content-Type",
					"application/json");
			httppost.setHeader(contentType);
			Header accept = new BasicHeader("Accept", "*/*");
			httppost.setHeader(accept);
			
			HttpResponse response = httpclient.execute(httppost);
			int code = response.getStatusLine().getStatusCode();
			String reason = response.getStatusLine().getReasonPhrase();
			success = code < 400;

			if (log.isDebugEnabled()) {
				log.debug("Http.post url ["+url+"] body ["+body+"] response code ["+code+"] reason ["+reason+"]");
			}
			
		} catch (Exception e) {
			log.error("Http.post failed, with url ["+url+"]", e);
		}
		return success;
	}

	/**
	 * PUT to the url with the body provided
	 * @param url a String url
	 * @param body a String with text for the request body
	 * @return true if successful (response code < 400), false otherwise
	 */
	static public boolean put(String url, String body) {
		boolean success = false;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPut httpPut = new HttpPut(url);
			HttpEntity entity = new StringEntity(body);
			httpPut.setEntity(entity);
			
			// Be sure to add these headers or the StRoom API will NOT work! 
			Header contentType = new BasicHeader("Content-Type",
					"application/json");
			httpPut.setHeader(contentType);
			Header accept = new BasicHeader("Accept", "*/*");
			httpPut.setHeader(accept);
			
			HttpResponse response = httpclient.execute(httpPut);
			int code = response.getStatusLine().getStatusCode();
			success = code < 400;

			if (log.isDebugEnabled()) {
				String reason = response.getStatusLine().getReasonPhrase();
				log.debug("Http.post url ["+url+"] body ["+body+"] response code ["+code+"] reason ["+reason+"]");
			}
	
		} catch (Exception e) {
			log.error("Http.put failed, with url ["+url+"]", e);
		}
		return success;
	}

	/**
	 * DELETE the url
	 * @param url a String url
	 * @return true if successful (response code < 400), false otherwise
	 */
	static public boolean delete(String url) {
		boolean success = false;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpDelete httpdelete = new HttpDelete(url);
			HttpResponse response = httpclient.execute(httpdelete);
			int code = response.getStatusLine().getStatusCode();
			success = code < 400;

			if (log.isDebugEnabled()) {
				String reason = response.getStatusLine().getReasonPhrase();
				log.debug("Http.delete url ["+url+"] response code ["+code+"] reason ["+reason+"]");
			}
		
		} catch (Exception e) {
			log.error("Http.delete failed, with url ["+url+"]", e);
		}
		return success;
	}

	// INTERNAL

	static private String deserializeBody(InputStream is) {
		if (is == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			log.error("Http response deserialization error", e);
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (Exception a) {
			}
			try {
				is.close();
			} catch (Exception a) {
			}
		}
		return sb.toString();
	}

}

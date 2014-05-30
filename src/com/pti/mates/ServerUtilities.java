/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pti.mates;
/*CLASE DEL CHAT BORRAR SI NO FUNCIONA*/
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.facebook.Session;
import com.pti.mates.Common;
import com.pti.mates.DataProvider;
import com.pti.mates.DataProvider.MessageType;

/**
 * Helper class used to communicate with the demo server.
 */
public final class ServerUtilities {

	private static final String TAG = "ServerUtilities";
	private static final int MAX_ATTEMPTS = 5;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final Random random = new Random();
	
	public static String s;
	

	/**
	 * Register this account/device pair within the server.
	 */
	public static void register(String fbid, String regId, Context ctx) {
		Utils utils = new Utils(ctx);
		DefaultHttpClient client = new MyHttpClient(ctx);
		HttpPost post = new HttpPost("https://54.194.14.115:443/register");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	      nameValuePairs.add(new BasicNameValuePair("matesid", fbid));
	      Log.d("REGISTERBACK","matesId = " + fbid);
	      nameValuePairs.add(new BasicNameValuePair("gcmid", regId));
	      
	      Log.d("regIdBACK", regId);
	      try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Execute the Post call and obtain the response
		HttpResponse postResponse;
		try {
			Log.d("REGISTERBACK","ENTRO TRY");
		    postResponse = client.execute(post);
		    HttpEntity responseEntity = postResponse.getEntity();
		    InputStream is = responseEntity.getContent();
		    s = utils.convertStreamToString(is);
		    Log.d("S REGISTER", s);
		    
		} catch (ClientProtocolException e) {
			Log.e("ERROR",e.toString());
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		    Log.e("ERROR",e.toString());
		    s = "ERROR: " + e.toString() + " :(";
		}
		Log.d("CHIVATO registe","FIN THREAD");
		
		//doRegister = new DoRegister(fbid,regId,ctx);
		//doRegister.execute();
		 Intent intent = new Intent(ctx, LogOk.class);
			ctx.startActivity(intent); 
		
	}

	public static void send2(String msg,String id, Context ctx) throws IOException {
		Log.v(LogConstants.LOG_SERVERUTILITIES,"Comencem send2");
		String serverUrl = Common.getServerUrl() + "/send";
		Log.v(LogConstants.LOG_SERVERUTILITIES,"URL = " + serverUrl);
		//Map<String, String> params = new HashMap<String, String>();
		//params.put("data", msg);
		Log.v(LogConstants.LOG_SERVERUTILITIES,"Missatge = "+ msg);
		//params.put("id", id);       
		Log.v(LogConstants.LOG_SERVERUTILITIES,"ID = " + id);
		post2(serverUrl, msg, id, ctx);
	}
	
	private static void post2(String endpoint,final String msg, final String id, final Context ctx) {
		DefaultHttpClient client = new MyHttpClient(ctx);
		HttpPost post = new HttpPost("https://54.194.14.115:443/send");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	      nameValuePairs.add(new BasicNameValuePair("receiver",id));
	      nameValuePairs.add(new BasicNameValuePair("sender",Common.getFBID()));
	      nameValuePairs.add(new BasicNameValuePair("data",msg));
	      try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Execute the Post call and obtain the response
		HttpResponse postResponse;
		s = "s No inicialitzada";
		try {
		    postResponse = client.execute(post);
		    HttpEntity responseEntity = postResponse.getEntity();
		    InputStream is = responseEntity.getContent();
		    s = convertStreamToString(is);
		    
		} catch (ClientProtocolException e) {
			Log.e("ERROR",e.toString());
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		    Log.e("ERROR",e.toString());
		    s = "ERROR: " + e.toString() + " :(";
		}
		Log.d("CHIVATO","FIN THREAD");
		//return s;
	}
	
	private static String convertStreamToString(InputStream is) {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}
	
	private static void updatePreferences(JSONObject user, Context ctx, String fbid) {
		DefaultHttpClient client = new MyHttpClient(ctx);
		HttpPost post = new HttpPost("https://54.194.14.115:443/upload");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	      nameValuePairs.add(new BasicNameValuePair("user",user.toString()));
	      try {
	    	post.setHeader("Content-type", "application/json");
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    HttpResponse postResponse;
		try {
		    postResponse = client.execute(post);
		    HttpEntity responseEntity = postResponse.getEntity();
		    InputStream is = responseEntity.getContent();
		} catch (ClientProtocolException e) {
			Log.e("ERROR",e.toString());
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		    Log.e("ERROR",e.toString());
		    s = "ERROR: " + e.toString() + " :(";
		}
		Log.d("CHIVATO","FIN THREAD");
	}
	
	public static void sendPreferences(SharedPreferences prefs, Context ctx) {
		//enviar preferencies al server
		Log.v(LogConstants.LOG_SERVERUTILITIES, "radius = " + prefs.getString(Common.RADIUS_KEY, "Empty"));
		Log.v(LogConstants.LOG_SERVERUTILITIES,"gender = " + prefs.getString(Common.GENDER_KEY, "Empty"));
		Log.v(LogConstants.LOG_SERVERUTILITIES, "interested in = " + prefs.getString(Common.INTERESTED_IN_KEY, "Empty"));	
		String fbid = Common.getFBID();
		DefaultHttpClient client = new MyHttpClient(ctx);
		HttpPost post = new HttpPost("https://54.194.14.115:443/user");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	      nameValuePairs.add(new BasicNameValuePair("id",fbid));
	      try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Execute the Post call and obtain the response
		HttpResponse postResponse;
		JSONObject user;
		try {
		    postResponse = client.execute(post);
		    HttpEntity responseEntity = postResponse.getEntity();
		    InputStream is = responseEntity.getContent();
		    try {
				user = new JSONObject(convertStreamToString(is));
				user.put("gender", prefs.getString(Common.GENDER_KEY, "Empty"));
				user.put("interested_in", prefs.getString(Common.INTERESTED_IN_KEY, "Empty"));
				user.put("distance", prefs.getString(Common.RADIUS_KEY, "Empty"));
				updatePreferences(user, ctx, fbid);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		} catch (ClientProtocolException e) {
			Log.e("ERROR",e.toString());
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		    Log.e("ERROR",e.toString());
		    s = "ERROR: " + e.toString() + " :(";
		}
		Log.d("CHIVATO","FIN THREAD");
		
	}
	
	/**
	 * Unregister this account/device pair within the server.
	 */
	//unregister no utilitzat
	/*public static void unregister(final String fbid) {
		//Log.i(TAG, "unregistering device (email = " + email + ")");
		String serverUrl = Common.getServerUrl() + "/unregister";
		Map<String, String> params = new HashMap<String, String>();
		params.put(DataProvider.SENDERFBID, fbid);
		try {
			post(serverUrl, params, MAX_ATTEMPTS);
		} catch (IOException e) {
			// At this point the device is unregistered from GCM, but still
			// registered in the server.
			// We could try to unregister again, but it is not necessary:
			// if the server tries to send a message to the device, it will get
			// a "NotRegistered" error message and should unregister the device.
		}
	}*/

	/**
	 * Send a message.
	 */
	/*public static void send(String msg, String to) throws IOException {
		//Log.i(TAG, "sending message (msg = " + msg + ")");
		String serverUrl = Common.getServerUrl() + "/send";
		Map<String, String> params = new HashMap<String, String>();
		params.put(DataProvider.MESSAGE, msg);
		params.put(DataProvider.SENDERFBID, Common.getFBID());
		params.put(DataProvider.RECEIVERFBID, to);        
		post(serverUrl, params, MAX_ATTEMPTS);
	}*/

	

	/** Issue a POST with exponential backoff */
	/*private static void post(String endpoint, Map<String, String> params, int maxAttempts) throws IOException {
		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
		for (int i = 1; i <= maxAttempts; i++) {
			//Log.d(TAG, "Attempt #" + i);
			try {
				post(endpoint, params);
				return;
			} catch (IOException e) {
				//Log.e(TAG, "Failed on attempt " + i + ":" + e);
				if (i == maxAttempts) {
					throw e;
				}
				try {
					Thread.sleep(backoff);
				} catch (InterruptedException e1) {
					Thread.currentThread().interrupt();
					return;
				}
				backoff *= 2;    			
			} catch (IllegalArgumentException e) {
				throw new IOException(e.getMessage(), e);
			}
		}
	}
	*/
	/**
	 * Issue a POST request to the server.
	 *
	 * @param endpoint POST address.
	 * @param params request parameters.
	 *
	 * @throws IOException propagated from POST.
	 */
	/*private static void post(String endpoint, Map<String, String> params) throws IOException {
		URL url;
		try {
			url = new URL(endpoint);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("invalid url: " + endpoint);
		}
		StringBuilder bodyBuilder = new StringBuilder();
		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
		// constructs the POST body using the parameters
		while (iterator.hasNext()) {
			Entry<String, String> param = iterator.next();
			bodyBuilder.append(param.getKey()).append('=').append(param.getValue());
			if (iterator.hasNext()) {
				bodyBuilder.append('&');
			}
		}
		String body = bodyBuilder.toString();
		//Log.v(TAG, "Posting '" + body + "' to " + url);
		//canviar a postTry
		byte[] bytes = body.getBytes();
		HttpURLConnection conn = null;
		
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setFixedLengthStreamingMode(bytes.length);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			// post the request
			OutputStream out = conn.getOutputStream();
			out.write(bytes);
			out.close();
			// handle the response
			int status = conn.getResponseCode();
			if (status != 200) {
				throw new IOException("Post failed with error code " + status);
			}
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}*/
}
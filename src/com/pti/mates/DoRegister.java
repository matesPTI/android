package com.pti.mates;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

public class DoRegister extends AsyncTask<Void, Integer, Boolean> {
	 
	Context ctx;
	public SharedPreferences prefs;
	private String fbid;
	private String regId;
	private Utils utils;
	private String s;
	
	public DoRegister(String FBID, String REGID, Context mContext)
    {
        ctx=mContext;
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        utils = new Utils(mContext);
        s = new String("s no inicialitzada");
    } 
	
    @Override
    protected Boolean doInBackground(Void... params) {
 
    	DefaultHttpClient client = new MyHttpClient(ctx);
		HttpPost post = new HttpPost("https://54.194.14.115:443/register");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	      nameValuePairs.add(new BasicNameValuePair("matesid", fbid));
	      nameValuePairs.add(new BasicNameValuePair("gcmid", regId));
	      try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Execute the Post call and obtain the response
		HttpResponse postResponse;
		try {
		    postResponse = client.execute(post);
		    HttpEntity responseEntity = postResponse.getEntity();
		    InputStream is = responseEntity.getContent();
		    s = utils.convertStreamToString(is);
		    
		} catch (ClientProtocolException e) {
			Log.e("ERROR",e.toString());
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		    Log.e("ERROR",e.toString());
		    s = "ERROR: " + e.toString() + " :(";
		}
		Log.d("CHIVATO","FIN THREAD");
		//Toast.makeText(ctx, "Register serverutilities: " + s, Toast.LENGTH_LONG).show();
    	return true;
    }
 
    @Override
    protected void onProgressUpdate(Integer... values) {
    	
    }
 
    @Override
    protected void onPreExecute() {
    }
 
    @Override
    protected void onPostExecute(Boolean result) {
    }
 
    @Override
    protected void onCancelled() {
    }
}

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

public class DoUser extends AsyncTask<Void, Integer, Boolean> {
	 
	Context mContext;
	public SharedPreferences prefs;
	private Utils utils;
	private String matesId;
	
	public DoUser(Context context, String id)
    {
        mContext=context;
        utils = new Utils(mContext);
        matesId=id;
        
    } 
	
    @Override
    protected Boolean doInBackground(Void... params) {
 
    	 //Aqu’ ejecutamos nuestras tareas costosas
		DefaultHttpClient client = new MyHttpClient(mContext);
		//url
		HttpPost post = new HttpPost("https://54.194.14.115:443/user");
		
		
		//Incloure Valors al post (nom del valor, valor)
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	      nameValuePairs.add(new BasicNameValuePair("id",
	          matesId));
	      
	      try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		// Execute the Post call and obtain the response
		HttpResponse postResponse;
		try {
			prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		    postResponse = client.execute(post);
		    HttpEntity responseEntity = postResponse.getEntity();
		    InputStream is = responseEntity.getContent();
		    String JsonString = utils.convertStreamToString(is);
		    Log.d("douser1", JsonString);
		    
		    SharedPreferences.Editor editor = prefs.edit();
		    editor.putString("profile_picture", utils.getInfoFromJson(JsonString, "picture"));
		    editor.putString("profile_birthday", utils.getInfoFromJson(JsonString, "birthday"));
		    editor.putString("profile_name", utils.getInfoFromJson(JsonString, "name"));
		    Log.d("douser2", utils.getInfoFromJson(JsonString, "picture"));
		    	
		    editor.commit();
		    
		} catch (ClientProtocolException e) {
			Log.e("ERROR",e.toString());
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		    Log.e("ERROR",e.toString());
		}
		Log.d("CHIVATO","FIN THREAD"); 
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
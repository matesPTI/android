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

import com.facebook.Session;

public class DoSignup extends AsyncTask<Void, Integer, Boolean> {
	 
	Context mContext;
	public SharedPreferences prefs;
	private Utils utils;
	
	public DoSignup(Context context)
    {
        mContext=context;
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        utils = new Utils(mContext);
    } 
	
    @Override
    protected Boolean doInBackground(Void... params) {
 
    	 //Aqu’ ejecutamos nuestras tareas costosas
		DefaultHttpClient client = new MyHttpClient(mContext);
		//url
		HttpPost post = new HttpPost("https://54.194.14.115:443/signup");
		
		//Obtener sesion de facebook
		Session session = Session.getActiveSession();
		Log.d("ACCESSTOKEN=",session.getAccessToken().toString());
		
		//Incloure Valors al post (nom del valor, valor)
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	      nameValuePairs.add(new BasicNameValuePair("accessToken",
	          session.getAccessToken().toString()));
	      
	      try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		// Execute the Post call and obtain the response
		HttpResponse postResponse;
		try {
		    postResponse = client.execute(post);
		    HttpEntity responseEntity = postResponse.getEntity();
		    InputStream is = responseEntity.getContent();
		    String JsonString = utils.convertStreamToString(is);
		    Log.d("REBO", JsonString);
		    
		    SharedPreferences.Editor editor = prefs.edit();
		    editor.putString("display_fbid", utils.getInfoFromJson(JsonString, "id"));
		    editor.putString("display_birthday", utils.getInfoFromJson(JsonString, "birthday"));
		    editor.putString("display_picture", utils.getInfoFromJson(JsonString, "picture"));
		    editor.putString("display_name", utils.getInfoFromJson(JsonString, "name"));
		    	
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
    	boolean isRegistering = false;
        if (prefs.getAll().containsKey("isRegistering")) {
        	isRegistering = prefs.getBoolean("isRegistering", false);
        }
        if (!isRegistering) {
        	Log.d("REGISTER", "ENTRO");
        	Log.d("idFbPrefs", prefs.getString("display_fbid", "ERROR"));
        	//if (checkPlayServices()) {
    			//registerReceiver(registrationStatusReceiver, new IntentFilter(Common.ACTION_REGISTER));
    			GcmUtil gcmUtil = new GcmUtil(mContext, prefs.getString("display_fbid", "ERROR"));
    		//}
    		/*else {
                Log.v("ON CREATE CHAT ACTIVITY", "No valid Google Play Services APK found.");
            }*/
        }
        
       
    }
 
    /*private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.v("CHECK PLAY SERVICES", "This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}*/
	
    
    @Override
    protected void onCancelled() {
    }
}
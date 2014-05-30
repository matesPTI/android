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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

public class DoLocate extends AsyncTask<Void, Integer, Boolean> {
	
	Utils utils;
	public GPSTracker gps;
	public double latitude;
	public double longitude;
	ProgressDialog ppDialog;
	Context mContext;
	public SharedPreferences prefs;
	
	
	public DoLocate(Context context)
    {
		mContext=context;
		utils = new Utils(mContext);
		 prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		 gps = new GPSTracker(mContext);
		    
		    // check if GPS enabled     
		    if(!gps.canGetLocation()){
		    	gps.showSettingsAlert();
		    }
		    latitude = gps.getLatitude();
	        longitude = gps.getLongitude();
    } 
	
    @Override
    protected Boolean doInBackground(Void... params) {
 
    	//Aqu’ ejecutamos nuestras tareas costosas
    	DefaultHttpClient client = new MyHttpClient(mContext);
    	//url
    	HttpPost post = new HttpPost("https://54.194.14.115:443/locate");
    	
        Log.d("LATITUDE", "" + latitude);
        Log.d("Longitude", "" + longitude);
    	//Incloure Valors al post (nom del valor, valor)
          List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
          nameValuePairs.add(new BasicNameValuePair("lat", String.valueOf(latitude)));
          //List<NameValuePair> nameValuePairs2 = new ArrayList<NameValuePair>(1);
          nameValuePairs.add(new BasicNameValuePair("lon", String.valueOf(longitude)));
          //List<NameValuePair> nameValuePairs3 = new ArrayList<NameValuePair>(1);
          nameValuePairs.add(new BasicNameValuePair("id", prefs.getString("display_fbid", "ERROR")));
          
          try {
    		post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    		//post.setEntity(new UrlEncodedFormEntity(nameValuePairs2));
    		//post.setEntity(new UrlEncodedFormEntity(nameValuePairs3));
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
    	    String JsonString = utils.convertStreamToString(is);
			Log.d("REBOLocate", JsonString);
			if (utils.getInfoFromJson(JsonString, "found").contentEquals("true")) {
				SharedPreferences.Editor editor = prefs.edit();
					editor.putString("mate_birthday",utils.getInfoFromJson(JsonString, "birthday"));
					editor.putString("mate_picture", utils.getInfoFromJson(JsonString, "picture"));
					editor.putString("mate_distance", utils.getInfoFromJson(JsonString, "distance"));
					editor.putString("mate_name", utils.getInfoFromJson(JsonString, "name"));
					editor.putString("mate_id", utils.getInfoFromJson(JsonString, "id"));
					editor.putString("found", "true");
				editor.commit();
			}
			else {
				SharedPreferences.Editor editor = prefs.edit();
					editor.putString("found", "false");
				editor.commit();
			}
    	    
    	} catch (ClientProtocolException e) {
    		Log.e("ERROR",e.toString());
    	    e.printStackTrace();
    	} catch (IOException e) {
    	    e.printStackTrace();
    	    Log.e("ERROR",e.toString());
    	}
    	Log.d("LOCATE", "ACABO LOCATE");
    	return true;
    }
    
    @Override
    protected void onProgressUpdate(Integer... values) {
    	int progreso = values[0].intValue();
		
		ppDialog.setProgress(progreso);
    }
 
    @Override
    protected void onPreExecute() {
    	ppDialog = new ProgressDialog(mContext);
		ppDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		ppDialog.setMessage("Searching people near...");
		ppDialog.setCancelable(false);
		ppDialog.setMax(100);
    	ppDialog.setMax(100);
        ppDialog.setProgress(0);
        ppDialog.show();
    }
 
    @Override
    protected void onPostExecute(Boolean result) {
    	ppDialog.dismiss();
       
    }
 
    @Override
    protected void onCancelled() {
        
    }
}

package com.pti.mates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;


public class Utils {

	Context mContext; 
	public GPSTracker gps;
	public DoSignup doSignup;
	public DoNext doNext;
	public DoMate doMate;
	public DoUser doUser;
	public double latitude;
	public double longitude;
	
	/**
	 * 
	 * @param context Current context
	 */
	
    public Utils(Context context)
    {
        mContext=context;
    } 
	
    /**
     * 
     * @param is ImputStream to convert
     * @return String converted
     * 
     * Converts ImputString to String
     */
    
	public String convertStreamToString(InputStream is) {
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

	/**
	 * 
	 * @param JsonString
	 * @param key
	 * @return String from de Json with the key "key"
	 */
	
	public String getInfoFromJson(String JsonString, String key) {
	
		try {
			JSONObject jsonObject = new JSONObject(JsonString);
			return jsonObject.getString(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "ERROR";
	}

	/**
	 *
	 * Get the current latitude and longitude
	 * 
	 */
	
	public void getGPS() {
		gps = new GPSTracker(mContext);
	    
	    // check if GPS enabled     
	    if(!gps.canGetLocation()){
	    	gps.showSettingsAlert();
	    }
	    latitude = gps.getLatitude();
        longitude = gps.getLongitude();
        Log.d("LOG y LAT" , "Lat = " + latitude + " long " + longitude);
	}

	
	
	
	public void signup() {
		doSignup = new DoSignup(mContext);
		doSignup.execute();
		/*try {
			doSignup.get(100000, TimeUnit.MILLISECONDS);
			Log.d("SIGNUP", "ACABO");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
	public void next() {
		doNext = new DoNext(mContext);
		doNext.execute();
		
	}
	
	public void mate() {
		doMate = new DoMate(mContext);
		Log.d("MATE","INICIO MATE");
		doMate.execute();
		
		
	}

	public String getImageFromMate() {
		SharedPreferences prefs;
		prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		return prefs.getString("mate_picture", "ERROR");
	}
	
	public String getNameFromMate() {
		SharedPreferences prefs;
		prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		Log.d("LOCNUMBER", prefs.getString("number","ERROR"));
		return prefs.getString("mate_name", "ERROR");
	}
	
	public String getAgeFromMate() {
		SharedPreferences prefs;
		prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		return prefs.getString("mate_birthday", "ERROR");
	}
	
	public String getIdFromMate() {
		SharedPreferences prefs;
		prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		return prefs.getString("mate_id", "ERROR");
	}
	
	public String getMyId() {
		SharedPreferences prefs;
		prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		return prefs.getString("display_fbid", "ERROR");
	}
	
	public void getImageFromProfile(String id) {
		doUser = new DoUser(mContext, id);
		doUser.execute();
		/*try {
			doUser.get(100000, TimeUnit.MILLISECONDS);
			Log.d("MATE", "ACABO");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
}


package com.pti.mates;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
/*CLASE DEL CHAT BORRAR SI NO FUNCIONA*/
public class GcmUtil {
	
	private static final String TAG = "GcmUtil";
	
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final String PROPERTY_ON_SERVER_EXPIRATION_TIME = "onServerExpirationTimeMs";
	
    /**
     * Default lifespan (7 days) of a reservation until it is considered expired.
     */
    public static final long REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 7;
    
    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();    

    private Context ctx;
	private SharedPreferences prefs;
	private GoogleCloudMessaging gcm;
	private AsyncTask registrationTask;
	public MakeSignUp makeSignUp;

	public GcmUtil(Context applicationContext, String idDeFb) {
		super();
		ctx = applicationContext;
		prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		gcm = GoogleCloudMessaging.getInstance(ctx);
		String regid = getRegistrationId();
		if (!regid.isEmpty()) Toast.makeText(ctx, "REGID NOT EMPTY: " + regid, Toast.LENGTH_LONG).show();
		if (regid.isEmpty()) {
			Editor edt = prefs.edit();
    		edt.putBoolean("isRegistering", true);
    		edt.commit();
            registerBackground(idDeFb);
        } else {
        	broadcastStatus(true);
        }		
	}
	
	/**
	 * Gets the current registration id for application on GCM service.
	 * <p>
	 * If result is empty, the registration has failed.
	 *
	 * @return registration id, or empty string if the registration is not
	 *         complete.
	 */
	private String getRegistrationId() {
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.length() == 0) {
	        //Log.v(TAG, "Registration not found.");
	        return "";
	    }
	    // check if app was updated; if so, it must clear registration id to
	    // avoid a race condition if GCM sends a message
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion();
	    /*if (registeredVersion != currentVersion || isRegistrationExpired()) {
	        //Log.v(TAG, "App version changed or registration expired.");
	        return "";
	    }*/
	    if (registeredVersion != currentVersion) {
	        //Log.v(TAG, "App version changed or registration expired.");
	        return "";
	    }
	    return registrationId;
	}
	
	/**
	 * Stores the registration id, app versionCode, and expiration time in the
	 * application's {@code SharedPreferences}.
	 *
	 * @param regId registration id
	 */
	private void setRegistrationId(String regId) {
	    int appVersion = getAppVersion();
	    //Log.v(TAG, "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    long expirationTime = System.currentTimeMillis() + REGISTRATION_EXPIRY_TIME_MS;

	    //Log.v(TAG, "Setting registration expiry time to " + new Timestamp(expirationTime));
	    editor.putLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, expirationTime);
	    editor.commit();
	}	
	
	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private int getAppVersion() {
	    try {
	        PackageInfo packageInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	
	/**
	 * Checks if the registration has expired.
	 *
	 * <p>To avoid the scenario where the device sends the registration to the
	 * server but the server loses it, the app developer may choose to re-register
	 * after REGISTRATION_EXPIRY_TIME_MS.
	 *
	 * @return true if the registration has expired.
	 */
	private boolean isRegistrationExpired() {
	    // checks if the information is not stale
	    long expirationTime = prefs.getLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, -1);
	    return System.currentTimeMillis() > expirationTime;
	}	
	
	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration id, app versionCode, and expiration time in the 
	 * application's shared preferences.
	 */
	private void registerBackground(final String idDeFb) {
		registrationTask = new AsyncTask<Void, Integer, Boolean>() {
	        @Override
	        protected Boolean doInBackground(Void... params) {
	        	Log.d("REGISTER","ENTRO REGISTERBACKGROUND");
	            long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
	            for (int i = 1; i <= MAX_ATTEMPTS; i++) {
	            	//Log.d(TAG, "Attempt #" + i + " to register");
		            try {
		                if (gcm == null) {
		                    gcm = GoogleCloudMessaging.getInstance(ctx);
		                }
		                String regid = gcm.register(Common.getSenderId());
		                // You should send the registration ID to your server over HTTP,
		                // so it can use GCM/HTTP or CCS to send messages to your app.
		                Log.d("REGISTERBACKGROUND","fb id = " + idDeFb);
		                ServerUtilities.register(idDeFb, regid, ctx);
		               
		                // Save the regid - no need to register again.
		                setRegistrationId(regid);
		                return Boolean.TRUE;
		                
		            } catch (IOException ex) {
		                //Log.e(TAG, "Failed to register on attempt " + i + ":" + ex);
		                if (i == MAX_ATTEMPTS) {
		                    break;
		                }
		                try {
		                    //Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
		                    Thread.sleep(backoff);
		                } catch (InterruptedException e1) {
		                    // Activity finished before we complete - exit.
		                    //Log.d(TAG, "Thread interrupted: abort remaining retries!");
		                    Thread.currentThread().interrupt();
		                }
		                // increase backoff exponentially
		                backoff *= 2;		                
		            }
	            }
	            return Boolean.FALSE;
	        }

	        @Override
	        protected void onPostExecute(Boolean status) {
	        	Log.d("REGISTER","ACABO");
	        	Editor edt = prefs.edit();
	        	edt.putBoolean("isRegistering", false);
	        	edt.commit();
	        	String regid = getRegistrationId();
	        	Toast.makeText(ctx, "REGID WAS EMPTY. Finished registering with regid =  " + regid, Toast.LENGTH_LONG).show();
	        	broadcastStatus(status);
	        	MakeSignUp makeSignUp = new MakeSignUp(ctx);
	            makeSignUp.execute();
	        	//DoSignup doSignup = new DoSignup(ctx);
	        	//doSignup.execute();
	        }   
		 
	    }.execute(null, null, null);
	   
	}
	
	private void broadcastStatus(boolean status) {
    	Intent intent = new Intent(Common.ACTION_REGISTER);
        intent.putExtra(Common.EXTRA_STATUS, status ? Common.STATUS_SUCCESS : Common.STATUS_FAILED);
        ctx.sendBroadcast(intent);		
	}
	
	public void cleanup() {
		if (registrationTask != null) {
			registrationTask.cancel(true);
		}
		if (gcm != null) {
			gcm.close();
		}
	}	
	
}

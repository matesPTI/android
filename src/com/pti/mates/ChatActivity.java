package com.pti.mates;

import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.pti.mates.DataProvider.MessageType;
import com.pti.mates.GcmUtil;
import com.pti.mates.ServerUtilities;
import com.pti.mates.R;
/*CLASE DEL CHAT BORRAR SI NO FUNCIONA*/
/*
 * 	implements MessagesFragment.OnFragmentInteractionListener, 
	EditContactDialog.OnFragmentInteractionListener, OnClickListener
 * 
 */
public class ChatActivity extends ActionBarActivity implements MessagesFragment.OnFragmentInteractionListener, 
EditContactDialog.OnFragmentInteractionListener, OnClickListener {
	
	//private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private EditText msgEdit;
	private Button sendBtn;
	private String profileId;
	private String profileName;
	private String profileFBID;
	//private GcmUtil gcmUtil;
	private SharedPreferences prefs;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_activity);
		profileId = getIntent().getStringExtra(Common.PROFILE_ID);
		msgEdit = (EditText) findViewById(R.id.msg_edit);
		sendBtn = (Button) findViewById(R.id.send_btn);
		sendBtn.setOnClickListener(this);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		Cursor c = getContentResolver().query(Uri.withAppendedPath(DataProvider.CONTENT_URI_PROFILE, profileId), null, null, null, null);
		if (c.moveToFirst()) {
			profileName = c.getString(c.getColumnIndex(DataProvider.COL_NAME));
			profileFBID = c.getString(c.getColumnIndex(DataProvider.COL_FBID));
			actionBar.setTitle(profileName);
		}
		/*actionBar.setSubtitle("connecting ...");
		if (checkPlayServices()) {
			registerReceiver(registrationStatusReceiver, new IntentFilter(Common.ACTION_REGISTER));
			gcmUtil = new GcmUtil(getApplicationContext());
		}
		else {
            Log.v("ON CREATE CHAT ACTIVITY", "No valid Google Play Services APK found.");
        }*/
	}

	/*private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		/*case R.id.action_edit:
			EditContactDialog dialog = new EditContactDialog();
			Bundle args = new Bundle();
			args.putString(Common.PROFILE_ID, profileId);
			args.putString(DataProvider.COL_NAME, profileName);
			dialog.setArguments(args);
			dialog.show(getSupportFragmentManager(), "EditContactDialog");
			return true;*/
		case R.id.Profile:
			Intent intent2 = new Intent(this, ProfileActivity.class); 
			intent2.putExtra(Common.PROFILE_ID, profileId);
			intent2.putExtra("FaceBookID",profileFBID);
			startActivity(intent2);
			return true;

		case android.R.id.home:
			Intent intent = new Intent(this, LogOk.class);
			//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			//onBackPressed();
			return true;			
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.send_btn:
			send(msgEdit.getText().toString());
			msgEdit.setText(null);
			break;
		}
	}

	@Override
	public void onEditContact(String name) {
		getSupportActionBar().setTitle(name);
	}	

	@Override
	public String getFBID() {
		return profileFBID;
	}	
	/*@Override
	public String getProfileEmail() {
		return profileEmail;
	}*/	

	private void send(final String txt) {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					/*ServerUtilities.send(txt, profileEmail);
					ContentValues values = new ContentValues(2);
					values.put(DataProvider.COL_TYPE,  MessageType.OUTGOING.ordinal());
					values.put(DataProvider.COL_MESSAGE, txt);
					values.put(DataProvider.COL_RECEIVER_EMAIL, profileEmail);
					values.put(DataProvider.COL_SENDER_EMAIL, Common.getPreferredEmail());		
					getContentResolver().insert(DataProvider.CONTENT_URI_MESSAGES, values);*/
					prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					Log.v(LogConstants.LOG_CHATACTIVITY, "Fem send2 del missatge");
					ServerUtilities.send2(txt, profileFBID, getApplicationContext());
					////BORRAR A PARTIR D'AQUI
					//Bundle data = new Bundle();
					//data.putString("data", txt);
					//GcmUtil.gcm.send(Constants.SENDER_ID, prefs.getString(GcmUtil.PROPERTY_REG_ID, ""), data);
					////FINS AQUI (tornar a fer gcm private en comptes de public static) 
					Log.v("PUTA", prefs.getString("registration_id", "")); //no surt per pantalla
					ContentValues values = new ContentValues(2);
					values.put(DataProvider.COL_TYPE,  MessageType.OUTGOING.ordinal());
					values.put(DataProvider.COL_MESSAGE, txt);
					values.put(DataProvider.COL_RECEIVERFBID, profileFBID);
					values.put(DataProvider.COL_SENDERFBID, Common.getFBID());		
					getContentResolver().insert(DataProvider.CONTENT_URI_MESSAGES, values);
					msg = ServerUtilities.s;

				} catch (IOException ex) {
					msg = "Message could not be sent";
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				if (!TextUtils.isEmpty(msg)) {
					Toast.makeText(getApplicationContext(), "On post execute send(chat) " + msg, Toast.LENGTH_LONG).show();
				}
				else {
					Toast.makeText(getApplicationContext(), "On post execute send(chat) else " + msg, Toast.LENGTH_LONG).show();
				}
			}
		}.execute(null, null, null);		
	}	

	@Override
	protected void onPause() {
		ContentValues values = new ContentValues(1);
		values.put(DataProvider.COL_COUNT, 0);
		getContentResolver().update(Uri.withAppendedPath(DataProvider.CONTENT_URI_PROFILE, profileId), values, null, null);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		//unregisterReceiver(registrationStatusReceiver);
		//gcmUtil.cleanup();
		super.onDestroy();
	}

	/*private BroadcastReceiver registrationStatusReceiver = new  BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			Toast.makeText(getApplicationContext(),sp.getString(GcmUtil.PROPERTY_REG_ID, "No tenim ID") , Toast.LENGTH_LONG).show();
			if (intent != null && Common.ACTION_REGISTER.equals(intent.getAction())) {
				switch (intent.getIntExtra(Common.EXTRA_STATUS, 100)) {
				case Common.STATUS_SUCCESS:
					getSupportActionBar().setSubtitle("online");
					sendBtn.setEnabled(true);
					break;

				case Common.STATUS_FAILED:
					getSupportActionBar().setSubtitle("offline");					
					break;					
				}
			}
			//les dues linies de a baix no serveixen el gcmbroadcastreceiver es registra en el manifest
			//unregisterReceiver(this);
			//registerReceiver(new GcmBroadcastReceiver(getApplicationContext()), new IntentFilter(Common.ACTION_REGISTER));
		}
	};*/
}

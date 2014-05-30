package com.pti.mates;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProfileActivity extends ActionBarActivity {
	private String profileId;
	private String fbid;
	private TextView tvName;
	private ImageView iv;
	private Utils utils;
	public  SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		profileId = getIntent().getStringExtra(Common.PROFILE_ID);
		fbid = getIntent().getStringExtra("profileFBID");
		iv = (ImageView)findViewById(R.id.profilePhotoProfile);
		tvName = (TextView)findViewById(R.id.NameProfile);
		utils = new Utils(this);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		utils.getImageFromProfile(fbid);
		Log.d("profile_picture", prefs.getString("profile_picture", "ERROR"));
        new DownloadImageTask(iv, this).execute(prefs.getString("profile_picture", "ERROR"));
        
        
        
        tvName.setText(prefs.getString("profile_name","ERROR") + ", "+ prefs.getString("profile_birthday", "ERROR"));
		
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		else if (id == android.R.id.home) {
			Intent intent = new Intent(this, ChatActivity.class);
			intent.putExtra(Common.PROFILE_ID, profileId);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

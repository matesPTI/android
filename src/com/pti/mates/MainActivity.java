package com.pti.mates;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.SessionDefaultAudience;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.Permission.Type;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;

public class MainActivity extends ActionBarActivity {

	private com.facebook.widget.LoginButton auth;
	private SimpleFacebook mSimpleFacebook;
	public SharedPreferences prefs;
	private Utils utils;
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private GcmUtil gcmUtil;
	DoSignup doSignup;
	
	
	
	public DoLocate doLocate;
	public MakeSignUp makeSignUp;

	//GCM REGISTER GLOBAL DATA
    
    @Override
	public void onResume() {
	    super.onResume();
	    mSimpleFacebook = SimpleFacebook.getInstance(this);
	}

	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }*/
        utils = new Utils(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        doSignup = new DoSignup(this);
        
        
        if (prefs.getAll().containsKey("display_fbid") ) {
        	Log.d("miId",prefs.getString("display_fbid", "ERROR"));
        	Intent intent = new Intent(MainActivity.this, LogOk.class);
    		startActivity(intent);
        	//locate();
        }
        hideActionBar();
		clickFacebookButton();
		 
	        
    }
	
	void locate() {
        doLocate = new DoLocate(this);
		doLocate.execute();
		Intent intent = new Intent(MainActivity.this, LogOk.class);
		startActivity(intent);
	}
	
	 @Override
	    protected void onDestroy() {
	    	// TODO Auto-generated method stub
	    	if (gcmUtil != null) gcmUtil.cleanup();
	    	super.onDestroy();
	    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        	Intent intent = new Intent(MainActivity.this, LogOk.class);
			startActivity(intent);
			return true;
        }
        return super.onOptionsItemSelected(item);
    }
    

	 @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data); 
	        super.onActivityResult(requestCode, resultCode, data);
	    }

    /**
     * Start animation
     */
    private void animationStart() {
    	ImageView i = (ImageView) findViewById(R.id.imagePrincipal);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.heart_beat_animation);
		i.startAnimation(animation);
    }
    
    /**
     * Hide action bar
     */
    
    private void hideActionBar() {

        ActionBar bar = getActionBar();
        bar.hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
    }
    
    /**
     * Click Facebook button
     */
    private void clickFacebookButton() {

        
        auth = (com.facebook.widget.LoginButton) findViewById(R.id.authButton);
        auth.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				new Thread(new Runnable() {
					public void run() {
						//Aqu’ ejecutamos nuestras tareas costosas
						
						Permission[] permissions = new Permission[] { 
								Permission.BASIC_INFO,  
								Permission.USER_LIKES, 
								Permission.USER_PHOTOS,
								Permission.USER_BIRTHDAY
						};

						SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
							.setAppId("693428864054184")
							.setNamespace("matesandroid")
							.setPermissions(permissions)
							.setDefaultAudience(SessionDefaultAudience.FRIENDS)
							.setAskForAllPermissionsAtOnce(false)
							.build();

						SimpleFacebook.setConfiguration(configuration);
						if (!mSimpleFacebook.isLogin())
							mSimpleFacebook.login(mOnLoginListener);
						
						else
							mSimpleFacebook.logout(mOnLogoutListener);
					}
			    }).start();
			}
		});
    }
    
    /* FACEBOOK THINGS */
	// Logout listener
	private OnLogoutListener mOnLogoutListener = new OnLogoutListener() {

		@Override
		public void onFail(String reason) {
		}

		@Override
		public void onException(Throwable throwable) {
		}

		@Override
		public void onThinking() {
			// show progress bar or something to the user while login is
			// happening
		}

		@Override
		public void onLogout() {
			// change the state of the button or do whatever you want
		}

	};


	private OnLoginListener mOnLoginListener = new OnLoginListener() {
	
		@Override
		public void onThinking() {
			// TODO Auto-generated method stub
			auth.setClickable(false);
			auth.setVisibility(View.GONE);
			Log.v("MainActivity", "FB onThinking");
			animationStart();
	
		}
	
		@Override
		public void onException(Throwable throwable) {
			// TODO Auto-generated method stub
			Log.v("MainActivity", "FB onException");
			Log.v("MainActivity", "FB onException: " + throwable.getMessage());
		}
	
		@Override
		public void onFail(String reason) {
			// TODO Auto-generated method stub
			Log.v("MainActivity", "FB onFail");
		}
	
		@Override
		public void onLogin() {
			Log.v("MainActivity", "On Login FB");
			Toast.makeText(getApplicationContext(), "OnLogin fb", Toast.LENGTH_LONG).show();
			doSignup.execute();
			/*boolean isRegistering = false;
	        if (prefs.getAll().containsKey("isRegistering")) {
	        	isRegistering = prefs.getBoolean("isRegistering", false);
	        }
	        if (!isRegistering) {
	        	Log.d("REGISTER", "ENTRO");
	        	Log.d("idFbPrefs", prefs.getString("display_fbid", "ERROR"));
	        	register(prefs.getString("display_fbid", "ERROR"));
	        }*/
			//singup();
			
		}
	
		@Override
		public void onNotAcceptingPermissions(Type type) {
			// TODO Auto-generated method stub
			Log.v("MainActivity", "FB onNotAcceptingPermissions");
			
		}
	 
	};
   
    public void singup() {
    	
        makeSignUp = new MakeSignUp(this);
        makeSignUp.execute();
    }
	
    private void register(String fbid) {
    	if (checkPlayServices()) {
			//registerReceiver(registrationStatusReceiver, new IntentFilter(Common.ACTION_REGISTER));
			gcmUtil = new GcmUtil(getApplicationContext(), fbid);
		}
		else {
            Log.v("ON CREATE CHAT ACTIVITY", "No valid Google Play Services APK found.");
        }
    }
	
	private boolean checkPlayServices() {
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
	}
	
	
	
	/**
	 * 
	 *  CLASES QUE NO PUEDEN ESTAR FUERA
	 * 
	 *
	 */
	
	
	
	
}

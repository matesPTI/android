package com.pti.mates;


import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;


public class LogOk extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        NavigationDrawerFragmentRight.NavigationDrawerCallbacks,
        LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private NavigationDrawerFragmentRight mNavigationDrawerFragmentRight;
    private ContactCursorAdapter ContactCursorAdapter;
	public static PhotoCache photoCache;
	ListView listView;
	private ActionBar actionBar;
	public static  SharedPreferences prefs;
	Utils util;
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private GcmUtil gcmUtil;
	private DrawerLayout dl;
	
	
	
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_ok);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        mNavigationDrawerFragmentRight = (NavigationDrawerFragmentRight)
        		getFragmentManager().findFragmentById(R.id.navigation_drawer_right);
        mNavigationDrawerFragmentRight.setUp(
        		R.id.navigation_drawer_right, 
        		(DrawerLayout)findViewById(R.id.drawer_layout));
        ContactCursorAdapter = new ContactCursorAdapter(this, null);
        photoCache = new PhotoCache(this);
        listView = mNavigationDrawerFragmentRight.getList();
        listView.setOnItemClickListener(this);
        listView.setAdapter(ContactCursorAdapter);
        /*actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.show();*/
		//actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME, ActionBar.DISPLAY_SHOW_CUSTOM);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        dl = (DrawerLayout) findViewById(R.id.drawer_layout);

		SharedPreferences.Editor editor = prefs.edit();
			editor.putString("refresh","YES");
		editor.commit();
        
     
        
        getSupportLoaderManager().initLoader(0, null, this);
        try {
        	ViewConfiguration config = ViewConfiguration.get(this);
        	Field menuKeyField = ViewConfiguration.class
        	.getDeclaredField("sHasPermanentMenuKey");
        	if (menuKeyField != null) {
        	menuKeyField.setAccessible(true);
        	menuKeyField.setBoolean(config, false);
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        
		dl.closeDrawer(Gravity.END);
		
		dl.closeDrawer(Gravity.START);
		
		ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
 
        View mCustomView = mInflater.inflate(R.layout.action_bar_layout, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
        mTitleTextView.setText("MATES");
 
        ImageButton settings = (ImageButton) mCustomView
                .findViewById(R.id.openSettings);
        settings.setOnClickListener(new OnClickListener() {
 
            @Override
            public void onClick(View view) {
            	if (dl.isDrawerOpen(Gravity.START))
                	dl.closeDrawer(Gravity.START);
            	else dl.openDrawer(Gravity.START);
            }
        });
        
        ImageButton chat  = (ImageButton) mCustomView
                .findViewById(R.id.openChats);
        chat.setOnClickListener(new OnClickListener() {
 
            @Override
            public void onClick(View view) {
            	if (dl.isDrawerOpen(Gravity.END))
                	dl.closeDrawer(Gravity.END);
            	else dl.openDrawer(Gravity.END);
            }
        });
 
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
        
		
    }
        
    @Override
   	public void onResume() {
   	    super.onResume();
   	    util = new Utils(getApplicationContext());
   	 prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor editor = prefs.edit();
			editor.putString("refresh","YES");
		editor.commit();
   	}
    
    
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    /*public void restoreActionBar() {
        /*ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);*/
    	/*actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.show();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME, ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setTitle(mTitle);
    }*/
 
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	if (gcmUtil != null) gcmUtil.cleanup();
    	super.onDestroy();
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements OnRefreshListener {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private ImageView iv;
        private ImageView ivAnimate;
        private ProgressBar pb;
        private Utils utils;
        private TextView tvName;
        private Button next;
        private Button mate;
        View rootView;
        public GPSTracker gps;
    	public double latitude;
    	public double longitude;
    	private TextView swipe;
    	
    	public DoLocatePrivate doLocate;
    	public DoMatePrivate doMate;
    	public DoNextPrivate doNext;
    	
    	private SwipeRefreshLayout swipeLayout;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_log_ok, container, false);
            utils = new Utils(rootView.getContext());
            iv = (ImageView)rootView.findViewById(R.id.profilePhoto);
            ivAnimate = (ImageView)rootView.findViewById(R.id.animation_loading_image);
            swipe = (TextView)rootView.findViewById(R.id.swipe_for_refresh);
            
            tvName = (TextView)rootView.findViewById(R.id.NameLogOk);
            next = (Button)rootView.findViewById(R.id.next);
            mate = (Button)rootView.findViewById(R.id.mate);
            doLocate = new DoLocatePrivate(rootView.getContext());
            tvName.setVisibility(View.INVISIBLE);
        	iv.setVisibility(View.INVISIBLE);
        	next.setVisibility(View.INVISIBLE);
        	mate.setVisibility(View.INVISIBLE);
        	swipe.setVisibility(View.VISIBLE);
        	ivAnimate.setVisibility(View.VISIBLE);
    		Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.heart_beat_animation);
    		ivAnimate.startAnimation(animation);
            //doLocate.execute();
            
    		swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
    	    swipeLayout.setOnRefreshListener(this);
    	    swipeLayout.setColorScheme(android.R.color.holo_blue_bright, 
    	            android.R.color.holo_green_light, 
    	            android.R.color.holo_orange_light, 
    	            android.R.color.holo_red_light);
    	    
		
			next.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Log.d("LogOk","Click next");
					doNext = new DoNextPrivate(rootView.getContext());
					doNext.execute();
					//doLocate.execute();
					
				}
			});
			
			mate.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Log.d("LogOk","Click mate");
					doMate = new DoMatePrivate(rootView.getContext());
					doMate.execute();
					//doLocate.execute();
				}
			});
			
			
            return rootView;
        }
        
        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((LogOk) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
        
        void afterLocate(Context ctx) {
        	Log.d("AFTERLOCATE","ENTRO AFTERLOCATE");
        	if (prefs.getString("found","false").contentEquals("true")) {
				
        		/*SET PHOTO*/
            	next.setVisibility(View.VISIBLE);
            	mate.setVisibility(View.VISIBLE);
            	iv.setImageResource(R.drawable.mates_logo_150p);
            	
                new DownloadImageTask(iv, ctx).execute(utils.getImageFromMate());
                Log.d("IF", "ENTRO AL IF");
	    		
	    		/*SET NAME*/
                //String name = new String (utils.getNameFromMate().substring(0, utils.getNameFromMate().indexOf(' ')));
                
                //String edad = new String (calcularEdad(utils.getAgeFromMate()));
                
	    		tvName.setText(utils.getNameFromMate() + ", " + utils.getAgeFromMate());
	    		Log.d("NAME + BIRTHDAY", (utils.getNameFromMate() + ", " + utils.getAgeFromMate()));
	    		Log.d("ELSE", "ENTRO AL ELSE");
	    		ivAnimate.clearAnimation();
	    		ivAnimate.setVisibility(View.INVISIBLE);
	        	tvName.setVisibility(View.VISIBLE);
	        	next.setVisibility(View.VISIBLE);
	        	mate.setVisibility(View.VISIBLE);
	        	swipe.setVisibility(View.INVISIBLE);
	        	SharedPreferences.Editor editor = prefs.edit();
				editor.putString("refresh","NO");
			editor.commit();
	        	
			}
            else {
            	tvName.setVisibility(View.INVISIBLE);
            	iv.setVisibility(View.INVISIBLE);
            	next.setVisibility(View.INVISIBLE);
            	mate.setVisibility(View.INVISIBLE);
            	swipe.setVisibility(View.VISIBLE);
            	ivAnimate.setVisibility(View.VISIBLE);
        		Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.heart_beat_animation);
        		ivAnimate.startAnimation(animation);
        		SharedPreferences.Editor editor = prefs.edit();
					editor.putString("refresh","YES");
				editor.commit();
				
				
			}
        	swipe.setText("Swipe down for refreshing");
        	swipeLayout.setRefreshing(false);
        	Log.d("LOCATE","END AFTERLOCATE");
        }
        
        String calcularEdad(String fecha) {
        	String anyo = fecha.substring(2, utils.getNameFromMate().indexOf('/'));
        	String mes = fecha.substring(1, utils.getNameFromMate().indexOf('/'));
        	String dia = fecha.substring(0, utils.getNameFromMate().indexOf('/'));
        	Calendar today = Calendar.getInstance();  
        	int age = today.get(Calendar.YEAR) - Integer.parseInt(anyo);  
        	if (today.get(Calendar.MONTH) < Integer.parseInt(mes)) {
        	  age--;  
        	} else if (today.get(Calendar.MONTH) == Integer.parseInt(mes)
        	    && today.get(Calendar.DAY_OF_MONTH) < Integer.parseInt(dia)) {
        	  age--;  
        	}
			return Integer.toString(age);
        	
        }
        
        public class DoLocatePrivate extends AsyncTask<Void, Integer, Boolean> {
        	
        	Utils utils;
        	public GPSTracker gps;
        	public double latitude;
        	public double longitude;
        	Context mContext;
        	public SharedPreferences prefs;
        	
        	
        	public DoLocatePrivate(Context context)
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
            }
         
            @Override
            protected void onPreExecute() {
            }
         
            @Override
            protected void onPostExecute(Boolean result) {
            	Log.d("ONPOSTEXECUTE","PASO");
            	afterLocate(mContext);
               
            }
         
            @Override
            protected void onCancelled() {
                
            }
        }
        
        
        public class DoMatePrivate extends AsyncTask<Void, Integer, Boolean> {
       	 
        	Context mContext;
        	public SharedPreferences prefs;
        	private Utils utils;
        	
        	public DoMatePrivate(Context context)
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
        		HttpPost post = new HttpPost("https://54.194.14.115:443/mate");
        		
        		
        		//Incloure Valors al post (nom del valor, valor)
        		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        	      nameValuePairs.add(new BasicNameValuePair("receiver",
        	          utils.getIdFromMate()));
        	      Log.d("MATE", "Receiver = " + utils.getIdFromMate());
        	      nameValuePairs.add(new BasicNameValuePair("sender", utils.getMyId()));
        	      Log.d("MATE", "MyId = " + utils.getMyId());
        	      nameValuePairs.add(new BasicNameValuePair("mate", "1"));
        	      
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
        		    String msg = utils.convertStreamToString(is);
        			Log.d("DoMate", msg);
        		    
        		} catch (ClientProtocolException e) {
        			Log.e("ERROR",e.toString());
        		    e.printStackTrace();
        		} catch (IOException e) {
        		    e.printStackTrace();
        		    Log.e("ERROR",e.toString());
        		}
        		Log.d("MATE","ACABO MATE"); 
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
            	DoLocatePrivate ddoLocate = new DoLocatePrivate(mContext);
            	ddoLocate.execute();
            	
            	
            }
         
            @Override
            protected void onCancelled() {
            }
        }
        
        public class DoNextPrivate extends AsyncTask<Void, Integer, Boolean> {
       	 
        	Context mContext;
        	public SharedPreferences prefs;
        	private Utils utils;
        	
        	public DoNextPrivate(Context context)
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
        		HttpPost post = new HttpPost("https://54.194.14.115:443/mate");
        		
        		
        		//Incloure Valors al post (nom del valor, valor)
        		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        	      nameValuePairs.add(new BasicNameValuePair("receiver",
        	          utils.getIdFromMate()));
        	      nameValuePairs.add(new BasicNameValuePair("sender", utils.getMyId()));
        	      nameValuePairs.add(new BasicNameValuePair("mate", "0"));
        	      
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
        		    String msg = utils.convertStreamToString(is);
        			Log.d("DoMate", msg);
        		    
        		} catch (ClientProtocolException e) {
        			Log.e("ERROR",e.toString());
        		    e.printStackTrace();
        		} catch (IOException e) {
        		    e.printStackTrace();
        		    Log.e("ERROR",e.toString());
        		}
        		Log.d("NEXT","ACABO NEXT"); 
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
            	DoLocatePrivate ddoLocate = new DoLocatePrivate(mContext);
            	ddoLocate.execute();
            }
         
            @Override
            protected void onCancelled() {
            }
        }

		@Override
		public void onRefresh() {
			tvName.setVisibility(View.INVISIBLE);
        	iv.setVisibility(View.INVISIBLE);
        	next.setVisibility(View.INVISIBLE);
        	mate.setVisibility(View.INVISIBLE);
        	swipe.setVisibility(View.VISIBLE);
        	ivAnimate.setVisibility(View.VISIBLE);
    		Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.heart_beat_animation);
    		ivAnimate.startAnimation(animation);
			swipeLayout.setRefreshing(true);
			swipe.setText("Refreshing...");
			SharedPreferences.Editor editor = prefs.edit();
				editor.putString("refresh","NO");
			editor.commit();
			Log.d("ONREFRESH","PASO");
			DoLocatePrivate ddoLocate = new DoLocatePrivate(getActivity());
	    	ddoLocate.execute();
			//swipeLayout.setRefreshing(false);
			
			
		}
    }

    @Override
	public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
		Intent intent = new Intent(this, ChatActivity.class);
		intent.putExtra(Common.PROFILE_ID, String.valueOf(arg3));
		startActivity(intent);
	}

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader loader = new CursorLoader(this, 
				DataProvider.CONTENT_URI_PROFILE, 
				new String[]{DataProvider.COL_ID, DataProvider.COL_NAME, DataProvider.COL_FBID, DataProvider.COL_COUNT}, 
				null, 
				null, 
				DataProvider.COL_ID + " DESC"); 
		return loader;
	}

    
	@Override
	public void onLoadFinished(android.support.v4.content.Loader<Cursor> arg0, Cursor arg1) {
		ContactCursorAdapter.swapCursor(arg1);
	}

	@Override
	public void onLoaderReset(android.support.v4.content.Loader<Cursor> arg0) {
		ContactCursorAdapter.swapCursor(null);
	}
	
	public class ContactCursorAdapter extends CursorAdapter {

		private LayoutInflater mInflater;

		public ContactCursorAdapter(Context context, Cursor c) {
			super(context, c, 0);
			this.mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override public int getCount() {
			return getCursor() == null ? 0 : super.getCount();
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View itemLayout = mInflater.inflate(R.layout.main_list_item, parent, false);
			ViewHolder holder = new ViewHolder();
			itemLayout.setTag(holder);
			holder.text1 = (TextView) itemLayout.findViewById(R.id.text1);
			holder.text2 = (TextView) itemLayout.findViewById(R.id.text2);
			//holder.textName = (TextView) itemLayout.findViewById(R.id.textEmail);
			//holder.avatar = (ImageView) itemLayout.findViewById(R.id.avatar);
			return itemLayout;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ViewHolder holder = (ViewHolder) view.getTag();
			holder.text1.setText(cursor.getString(cursor.getColumnIndex(DataProvider.COL_NAME)));
			//holder.textName.setText(cursor.getString(cursor.getColumnIndex(DataProvider.COL_NAME)));
			int count = cursor.getInt(cursor.getColumnIndex(DataProvider.COL_COUNT));
			if (count > 0){
				holder.text2.setVisibility(View.VISIBLE);
				holder.text2.setText(String.format("%d new message%s", count, count==1 ? "" : "s"));
			}else
				holder.text2.setVisibility(View.GONE);

			//photoCache.DisplayBitmap(requestPhoto(cursor.getString(cursor.getColumnIndex(DataProvider.COL_EMAIL))), holder.avatar);

		}
	}

	private static class ViewHolder {
		TextView text1;
		TextView text2;
		//TextView textName;
		//ImageView avatar;
	}

	
	@SuppressLint("InlinedApi")
	private Uri requestPhoto(String email){
		Cursor emailCur = null;
		Uri uri = null;
		try{
			int SDK_INT = android.os.Build.VERSION.SDK_INT;
			if(SDK_INT >= 11){
				String[] projection = { ContactsContract.CommonDataKinds.Email.PHOTO_URI };
				ContentResolver cr = getContentResolver();
				emailCur = cr.query(
						ContactsContract.CommonDataKinds.Email.CONTENT_URI, projection,
						ContactsContract.CommonDataKinds.Email.ADDRESS + " = ?", 
								new String[]{email}, null);
				if (emailCur != null && emailCur.getCount() > 0) {	
					if (emailCur.moveToNext()) {
						String photoUri = emailCur.getString( emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.PHOTO_URI));
						if(photoUri != null)
							uri = Uri.parse(photoUri);
					}
				}
			}else if(SDK_INT < 11) {
				String[] projection = { ContactsContract.CommonDataKinds.Photo.CONTACT_ID };
				ContentResolver cr = getContentResolver();
				emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
						projection,
						ContactsContract.CommonDataKinds.Email.ADDRESS + " = ?",
						new String[]{email}, null);
				if (emailCur.moveToNext()) {
					int columnIndex = emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Photo.CONTACT_ID);
					long contactId = emailCur.getLong(columnIndex);
					uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,	contactId);
					uri = Uri.withAppendedPath(uri,	ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
				}
			}	
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(emailCur != null)
					emailCur.close();
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return uri;
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
	
	@Override
	public void onBackPressed() {
	    moveTaskToBack(true);
	}
	
}
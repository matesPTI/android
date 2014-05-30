package com.pti.mates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Session;

public class PostTry extends Activity {
	private TextView tv;
	private Button bt;
	public String s = "Encara NO";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_try);


		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		tv = (TextView) findViewById(R.id.textView1Proba);
		bt = (Button) findViewById(R.id.buttonEscriu);
		bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tv.setText(s);
			}
		});
		new Thread(new Runnable() {
		    public void run() {
		        //Aqu’ ejecutamos nuestras tareas costosas
		    	s = "Espera desesperat";
				DefaultHttpClient client = new MyHttpClient(getApplicationContext());
				//url
				HttpPost post = new HttpPost("https://54.194.14.115:443/signup");
				
				//Obtener sesion de facebook
				Session session = Session.getActiveSession();
				
				//Incloure Valors al post (nom del valor, valor)
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			      nameValuePairs.add(new BasicNameValuePair("accesToken",
			          session.getAccessToken().toString()));
			      
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
				    s = convertStreamToString(is);
				    
				} catch (ClientProtocolException e) {
					Log.e("ERROR",e.toString());
				    e.printStackTrace();
				} catch (IOException e) {
				    e.printStackTrace();
				    Log.e("ERROR",e.toString());
				    s = "ERROR: " + e.toString() + " :(";
				}
				Log.d("CHIVATO","FIN THREAD");

		    	
		    }
		}).start();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.post_try, menu);
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
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_post_try,
					container, false);
			return rootView;
		}
	}

	private String convertStreamToString(InputStream is) {
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
	
	
	
}

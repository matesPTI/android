package com.pti.mates;

import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Patterns;

import com.pti.mates.Constants;

/*CLASE DEL CHAT BORRAR SI NO FUNCIONA*/
public class Common extends Application{
	public static final String PROFILE_ID = "profile_id";
	public static final String ACTION_REGISTER = "com.pti.mates.REGISTER";
	public static final String EXTRA_STATUS = "status";
	public static final int STATUS_SUCCESS = 1;
	public static final int STATUS_FAILED = 0;
	public static String[] email_arr;
    private static SharedPreferences prefs;
    
    //settings
    public static final String GENDER_KEY = "gender";
    public static final String INTERESTED_IN_KEY = "interested_in";
    public static final String RADIUS_KEY = "radius";
    
    @Override
    public void onCreate() {
        super.onCreate();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
         
        //List<String> emailList = getEmailList();
        //email_arr = emailList.toArray(new String[emailList.size()]);
    }
     
    /*private List<String> getEmailList() {
        List<String> lst = new ArrayList<String>();
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            if (Patterns.EMAIL_ADDRESS.matcher(account.name).matches()) {
                lst.add(account.name);
            }
        }
        return lst;
    } */
    
    /*public static String getPreferredEmail() {
		return prefs.getString("chat_email_id", email_arr.length==0 ? "" : email_arr[0]);
	}*/
	public static String getFBID() {
		return prefs.getString("display_fbid", "");
	}
	public static String getDisplayName() {
		return prefs.getString("display_name", "unnamed");
	}
	
	public static boolean isNotify() {
		return prefs.getBoolean("notifications_new_message", true);
	}
	
	public static String getRingtone() {
		return prefs.getString("notifications_new_message_ringtone", android.provider.Settings.System.DEFAULT_NOTIFICATION_URI.toString());
	}
	
	public static String getServerUrl() {
		return prefs.getString("server_url_pref", Constants.SERVER_URL);
	}
	
	public static String getSenderId() {
		return prefs.getString("sender_id_pref", Constants.SENDER_ID);
	}
}

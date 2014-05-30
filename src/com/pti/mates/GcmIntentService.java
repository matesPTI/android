/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pti.mates;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.pti.mates.DataProvider;
import com.pti.mates.DataProvider.MessageType;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }
    public static final String TAG = "GCM Mates";
    //function to obatin parametres by key from a json string 
    private String getInfoFromJson(String JsonString, String key) {
        
    	try {
			JSONObject jsonObject = new JSONObject(JsonString);
			return jsonObject.getString(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "ERROR";
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
        //sendNotification("Estem fent HandleIntent");
        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                //sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                //sendNotification("Deleted messages on server: " + extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            	//sendNotification("Message type correcte");
                // This loop represents the service doing some work.
            	String type = intent.getStringExtra("type");
            	if (type.equals("message")) {
            		String msg = intent.getStringExtra("data");
                	String senderid = intent.getStringExtra("sender");
                	String receiverid = intent.getStringExtra("receiver");
    				//String senderEmail = intent.getStringExtra(DataProvider.COL_SENDER_EMAIL);
    				//String receiverEmail = intent.getStringExtra(DataProvider.COL_RECEIVER_EMAIL);
    				ContentValues values = new ContentValues(2);
    				values.put(DataProvider.COL_TYPE,  MessageType.INCOMING.ordinal());				
    				values.put(DataProvider.COL_MESSAGE, msg);
    				values.put(DataProvider.COL_SENDERFBID, senderid);
    				values.put(DataProvider.COL_RECEIVERFBID, receiverid);
    				getApplicationContext().getContentResolver().insert(DataProvider.CONTENT_URI_MESSAGES, values);
    				if (Common.isNotify()) {
    					/*Cursor c = getApplicationContext().getContentResolver().query(DataProvider.CONTENT_URI_PROFILE, 
    							null, DataProvider.COL_FBID + "=" + senderid, null, null);
    					int index = c.getColumnIndex(DataProvider.COL_NAME);
    					String sendername;
    					if (index != -1) {
    						sendername = c.getString(index);
    					}
    					else {
    						sendername = "name not available";
    					}
    					sendNotificationChat("New message from " + sendername,senderid);*/
    					sendNotificationChat("New message", senderid);
    				}
            	}
            	else if (type.equals("mate")) {
            		//extreure info de la persona necessaria per crear el xat (insertar a la base de dades un nou perfil)
            		//si fa falta cridar la activity per mostrar la info. (perfilActivity??)
            		String userdatajson = intent.getStringExtra("data");
            		String fbid = getInfoFromJson(userdatajson,"id");
            		String name = getInfoFromJson(userdatajson, "name");
            		Log.d("Mate intent service", "fbid = " + fbid + " name = " + name);
        			ContentValues values = new ContentValues(2);
					values.put(DataProvider.COL_NAME, name); 
					values.put(DataProvider.COL_FBID, fbid);
        			getApplication().getContentResolver().insert(DataProvider.CONTENT_URI_PROFILE, values);
        			sendNotificationMate("New Mate with " + name);
        			
            	}
            }
            /*else {
            	String msg = intent.getExtras().toString();
				//String senderEmail = intent.getStringExtra(DataProvider.COL_SENDER_EMAIL);
				//String receiverEmail = intent.getStringExtra(DataProvider.COL_RECEIVER_EMAIL);
            	String senderid = intent.getStringExtra("sender");
            	String receiverid = intent.getStringExtra("receiver");
				//String senderEmail = intent.getStringExtra(DataProvider.COL_SENDER_EMAIL);
				//String receiverEmail = intent.getStringExtra(DataProvider.COL_RECEIVER_EMAIL);
				ContentValues values = new ContentValues(2);
				values.put(DataProvider.COL_TYPE,  MessageType.INCOMING.ordinal());				
				values.put(DataProvider.COL_MESSAGE, msg);
				values.put(DataProvider.COL_SENDERFBID, senderid);
				values.put(DataProvider.COL_RECEIVERFBID, receiverid);
				getApplicationContext().getContentResolver().insert(DataProvider.CONTENT_URI_MESSAGES, values);
				if (Common.isNotify()) {
					sendNotification("New message");
				}
            }*/
        }
        else {
        	//sendNotification("Extras empty");
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotificationChat(String msg, String senderid) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        /*Cursor caux;
        caux = getApplicationContext().getContentResolver().query(DataProvider.CONTENT_URI_PROFILE, new String[] {DataProvider.COL_ID}, DataProvider.COL_FBID + "=" + senderid, null, null);
        Intent intent = new Intent(this, ChatActivity.class);
		intent.putExtra(Common.PROFILE_ID, caux.getString(caux.getColumnIndex(DataProvider.COL_ID)));*/
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(getApplicationContext(), MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle("New chat message")
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
    private void sendNotificationMate(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        /*Cursor caux;
        caux = getApplicationContext().getContentResolver().query(DataProvider.CONTENT_URI_PROFILE, new String[] {DataProvider.COL_ID}, DataProvider.COL_FBID + "=" + senderid, null, null);
        Intent intent = new Intent(this, ChatActivity.class);
		intent.putExtra(Common.PROFILE_ID, caux.getString(caux.getColumnIndex(DataProvider.COL_ID)));*/
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(getApplicationContext(), MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle("Congratulations!")
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
    
}

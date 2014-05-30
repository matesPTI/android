package com.pti.mates;
/*CLASE DEL CHAT BORRAR SI NO FUNCIONA*/
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.content.ComponentName;

import com.pti.mates.Common;
import com.pti.mates.DataProvider;
import com.pti.mates.DataProvider.MessageType;
import com.pti.mates.InitialChatActivity;
import com.pti.mates.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Explicitly specify that GcmIntentService will handle the intent.
    	Log.v("GcmBroadcastReceiver","ENTREM AL BROADCAST RECEIVER DE GCM!!!");
    	//Toast.makeText(context, "Estem en el onReceive GcmB", Toast.LENGTH_LONG).show();
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmIntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}

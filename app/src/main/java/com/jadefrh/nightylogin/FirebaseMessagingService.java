package com.jadefrh.nightylogin;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static com.facebook.GraphRequest.TAG;

/**
 * Created by astronaught on 05/06/2017.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Integer quickblox_id = 0;
        Integer user_id = 0;
        Integer call_max_duration = 0;
        Integer call_ring_duration = 0;
        Integer call_delay_interaction = 0 ;
        JSONObject detailsObject = null;

        try {
            detailsObject = new JSONObject(remoteMessage.getData().get("details"));
            quickblox_id = detailsObject.getInt("quickblox_id");
            user_id = detailsObject.getInt("user_id");
            call_max_duration = detailsObject.getInt("call_max_duration");
            call_ring_duration = detailsObject.getInt("call_ring_duration");
            call_delay_interaction = detailsObject.getInt("call_delay_interaction");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.equals("tags") && value.equals("true")) {
                System.out.println("non on la montre pas");
            } else if (key.equals("mute") && value.equals("false")) {
                System.out.println("oui on la monnnntreeee");
                showNotification(remoteMessage.getData().get("message"), quickblox_id, user_id, call_max_duration, call_ring_duration, call_delay_interaction);
            }
            Log.d(TAG, "key, " + key + " value " + value);
            //
        }


    }

    private void showNotification(String message, Integer quickblox_id, Integer user_id, Integer call_max_duration, Integer call_ring_duration, Integer call_delay_interaction  ) {

        Intent i = new Intent(this, VideoActivity.class);
        i.putExtra("quickblox_id", quickblox_id);
        i.putExtra("user_id", user_id);
        i.putExtra("call_max_duration", call_max_duration);
        i.putExtra("call_ring_duration", call_ring_duration);
        i.putExtra("call_delay_interaction", call_delay_interaction);

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("FCM test")
                .setContentText(message)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0,builder.build());


    }
}

package com.jadefrh.nightylogin;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.jivesoftware.smackx.carbons.packet.CarbonExtension;

import java.io.IOException;
import java.util.UUID;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.jadefrh.nightylogin.InitActivity.MY_PREFS_NAME;

/**
 * Created by astronaught on 05/06/2017.
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    String uniqueId = null;
    String token;

    @Override
    public void onTokenRefresh() {

        String fbToken = FirebaseInstanceId.getInstance().getToken();

        registerToken(fbToken);
    }

    private void registerToken(String fbToken) {

        uniqueId = UUID.randomUUID().toString();

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("fb_token", fbToken);
        editor.putString("identifier", uniqueId);
        editor.apply();

        System.out.println("test première instance : " + fbToken + " et " + uniqueId);

        // On récupère le Nighty Access Token des Shared Preferences
        //SharedPreferences prefs = this.getSharedPreferences(MY_PREFS_NAME, this.MODE_PRIVATE);
        //token = prefs.getString("nighty_access_token", null);
        //System.out.println("le petit token recherché ! : " + token);

        //System.out.println("fb token : " + fbToken);


//        OkHttpClient client = new OkHttpClient();
//
//        System.out.println("UUID : " + uniqueId);
//
//        RequestBody body = new FormBody.Builder()
//                .add("token", fbToken)
//                .add("platform","android")
//                .add("identifier", uniqueId)
//                .build();
//
//        Request request = new Request.Builder()
//                .url("http://nighty-develop.ivvp7jqj5r.eu-west-1.elasticbeanstalk.com/api/device")
//                .post(body)
//                .addHeader("Authorization", "Bearer " + token)
//                .build();
//
//        System.out.println("EST CE QUE JE RENTRE ICI MEME ????? : " + token);
//
//        try {
//            client.newCall(request).execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}

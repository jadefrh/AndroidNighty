package com.jadefrh.nightylogin.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.jadefrh.nightylogin.InitActivity;
import com.jadefrh.nightylogin.MoodActivity;
import com.jadefrh.nightylogin.PatienceActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.jadefrh.nightylogin.InitActivity.MY_PREFS_NAME;
import static com.jadefrh.nightylogin.InitActivity.mLastLocation;

/**
 * Created by astronaught on 28/05/2017.
 */

public abstract class SunTimeHelper extends AsyncTask<Void, Void, long[]> {

    private String token;

    public SunTimeHelper(Context context) {

        // On récupère le Nighty Access Token des Shared Preferences
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, context.MODE_PRIVATE);
        token = prefs.getString("nighty_access_token", null);
        execute();
    }

    // ce qu'on va faire en fond
    @Override
    public long[] doInBackground(Void... params) {

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, "{\"latitude\": " + mLastLocation.getLatitude() + ", \"longitude\": " + mLastLocation.getLongitude() + "}");
        System.out.println("latitude : " + mLastLocation.getLatitude() + ", long : " + mLastLocation.getLongitude());
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://nighty-develop.ivvp7jqj5r.eu-west-1.elasticbeanstalk.com/api/user/current")
                .put(body) //PUT
                .addHeader("Authorization", "Bearer " + token)
                .build();
        Response response = null;
        try {

            response = client.newCall(request).execute();
            JSONObject parentObject = new JSONObject(response.body().string());

            long service_start = parentObject.getLong("service_start");
            long service_end = parentObject.getLong("service_end");

            long currentTime = System.currentTimeMillis() / 1000L;

            return new long[]{service_start, service_end, currentTime};
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected abstract void onPostExecute(long[] longs);
}

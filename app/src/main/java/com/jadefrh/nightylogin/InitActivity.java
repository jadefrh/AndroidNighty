package com.jadefrh.nightylogin;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.jadefrh.nightylogin.helpers.ApiFetch;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class InitActivity extends AppCompatActivity {

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    TextView fbaccesstoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        fbaccesstoken = (TextView) findViewById(R.id.fbaccesstoken);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                if (!hasToken()) {
                    String nightyTokenAccess = authenticate();
                    storeToken(nightyTokenAccess);
                }
                getLocation();
                isNightTime();

                return null;
            }
        }.execute();
    }

    // get Facebook access token, sends to server to get Nighty Access Token
    private String authenticate() {

        AccessToken token = AccessToken.getCurrentAccessToken();

        // get Facebook Access Token
        fbaccesstoken.setText(token.getToken());
        //System.out.println(token.getToken());

        // POST request to API to get Nighty Access Token
        String url = ApiFetch.API_URL + "/oauth/token";
        String charset = "UTF-8";
        String clientId = "1_5g8ptf7pfeskcssssc8skok44kwcc0kwgsg8so8g4cwwgk0c0c";
        String clientSecret = "5t56m2ydfokkg8sowwkgksww00kkwwcoo0s4osw40ggs8s4s40";
        String grantType = "http://nighty-develop.ivvp7jqj5r.eu-west-1.elasticbeanstalk.com/oauth/auth/connect/facebook";
        String accessToken = token.getToken();

        try {
            String query = String.format("client_id=%s&client_secret=%s&grant_type=%s&access_token=%s",
                    URLEncoder.encode(clientId, charset),
                    URLEncoder.encode(clientSecret, charset),
                    URLEncoder.encode(grantType, charset),
                    URLEncoder.encode(accessToken, charset));

            URLConnection connection = new URL(url).openConnection();
            connection.setDoOutput(true); // Triggers POST.
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            try (OutputStream output = connection.getOutputStream()) {
                output.write(query.getBytes(charset));
            }

            InputStream response = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(response));

            StringBuffer buffer = new StringBuffer();

            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            String finalJson = buffer.toString();
            JSONObject parentObject = new JSONObject(finalJson);
            String nightyAccessToken = parentObject.getString("access_token");
            //System.out.println("testtttttttt : " + nightyAccessToken);

            return nightyAccessToken;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    // store Nighty access token on device
    private void storeToken(String token) {
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("nighty_access_token", token);
        editor.apply();
    }

    private boolean hasToken() {
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString("nighty_access_token", null);

        return token != null;
    }

//    private String[] getLocation() {
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
//                    PERMISSION_ACCESS_COARSE_LOCATION);
//        }
//
//        return null;
//    }

    // checks suntime to know if user can use the application
    private boolean isNightTime() {
        // GET
        new ApiFetch("/api/suntime", this) {
            @Override
            protected void onResult(JSONObject json) {
                if (json != null) {
                    System.out.println("oki suntime: " + json.toString());
                }
            }
        };

        return true;

    }
}

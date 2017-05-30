package com.jadefrh.nightylogin.helpers;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by astronaught on 29/05/2017.
 */

public abstract class ApiFetch {

    public static final String API_URL = "http://nighty-develop.ivvp7jqj5r.eu-west-1.elasticbeanstalk.com";

    public ApiFetch(String apiUrl, Context context) {
        String fullUrl = API_URL + apiUrl;

        try {
            URL url = new URL(fullUrl);
            URLConnection connection = url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer buffer = new StringBuffer();

            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            String finalJson = buffer.toString();
            JSONObject object = new JSONObject(finalJson);

            onResult(object);
            return;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        onResult(null);
    }

    protected abstract void onResult(JSONObject json);
}

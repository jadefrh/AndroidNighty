package com.jadefrh.nightylogin.helpers;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by astronaught on 28/05/2017.
 */

public abstract class SunTimeHelper extends AsyncTask<String, String, String[]> {

    public SunTimeHelper() {
        execute("https://api.sunrise-sunset.org/json?lat=36.7201600&lng=-4.4203400&date=today");
    }

    // ce qu'on va faire en fond
    @Override
    protected String[] doInBackground(String... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();

            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            String finalJson = buffer.toString();
            JSONObject parentObject = new JSONObject(finalJson);
            JSONObject finalObject = parentObject.getJSONObject("results");


            String sunriseTime = finalObject.getString("sunrise");
            String sunsetTime = finalObject.getString("sunset");

            return new String[] { sunriseTime, sunsetTime };

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return null;
    }

    @Override
    protected abstract void onPostExecute(String[] result);
}

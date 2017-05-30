package com.jadefrh.nightylogin;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class GetSunsetActivity extends AppCompatActivity {

    private TextView sunrise;
    private TextView sunset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_get_sunset);

        Intent intent = getIntent();

        sunset = (TextView) findViewById(R.id.sunset);
        sunrise = (TextView) findViewById(R.id.sunrise);

        new JSONTask().execute("https://api.sunrise-sunset.org/json?lat=48.866667&lng=2.333333&date=today");


//        LoginManager.getInstance().logOut();

    }

    public class JSONTask extends AsyncTask<String, String, String[]> {

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

                String sunsetTime = finalObject.getString("sunset");
                String sunriseTime = finalObject.getString("sunrise");

                return new String[] { sunsetTime, sunriseTime };

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
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            sunrise.setText(result[1]);
            sunset.setText(result[0]);
        }
    }
}




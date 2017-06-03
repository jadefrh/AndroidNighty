package com.jadefrh.nightylogin;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private com.android.volley.RequestQueue queue;

    private Button logout;
    private SeekBar distanceBar;
    private TextView distanceText;

    private int distanceValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        distanceText = (TextView)findViewById(R.id.distanceText);

        distanceBar = (SeekBar)findViewById(R.id.distanceBar);
        distanceText.setText("2");
        distanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distanceText.setText("Distance : "+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                distanceValue = distanceBar.getProgress();
            }


        });
        logout = (Button)findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                Intent i;
                i = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        onStopTrackingTouch();

        queue = Volley.newRequestQueue(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            updatePreferences();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void updatePreferences() {
        System.out.println("hihi : " + distanceValue);
//        StringRequest putRequest = new StringRequest(Request.Method.PUT, "http://nighty-develop.ivvp7jqj5r.eu-west-1.elasticbeanstalk.com/api/user/current",
//                new Response.Listener<String>()
//                {
//                    @Override
//                    public void onResponse(String response) {
//                        // response
//                        try {
//                            JSONObject parentObject = new JSONObject(response);
//                            userStatus = parentObject.getBoolean("is_online");
//                            updateView();
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        Log.d("Response", response);
//
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // error
//                        Log.d("Error.Response", error.getMessage());
//                    }
//                }
//        ) {
//
//            @Override
//            protected Map<String, String> getParams()
//            {
//                Map<String, String>  params = new HashMap<String, String>();
//                if (userStatus) {
//                    System.out.println("is online = false");
//                    params.put("is_online", "0");
//                } else {
//                    System.out.println("is online = true");
//                    params.put("is_online", "1");
//                }
//
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String,String> params = new HashMap<String, String>();
//                params.put("Authorization","Bearer " + token);
//                return params;
//            }
//
//        };
//
//        queue.add(putRequest);
    }

    public void onStopTrackingTouch() {

    }


}

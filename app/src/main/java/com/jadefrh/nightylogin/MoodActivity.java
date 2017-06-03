package com.jadefrh.nightylogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.RequestQueue;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jadefrh.nightylogin.helpers.ApiFetch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import static com.jadefrh.nightylogin.InitActivity.MY_PREFS_NAME;
import static com.jadefrh.nightylogin.InitActivity.mLastLocation;

public class MoodActivity extends AppCompatActivity {


    com.android.volley.RequestQueue queue;
    private String token;

    TextView mainTitleMood;
    Button submitMoods;

    int selectedFeeling;
    int selectedVibe;
    int selectedLookingFor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);

        mainTitleMood = (TextView)findViewById(R.id.mainTitleMood);
        submitMoods = (Button)findViewById(R.id.submitMoods);

        submitMoods.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                checkMoods();
            }
        });


        SharedPreferences prefs = this.getSharedPreferences(MY_PREFS_NAME, this.MODE_PRIVATE);
        token = prefs.getString("nighty_access_token", null);

        queue = Volley.newRequestQueue(this);

        getMoods();

    }

    private void getMoods() {

        final String url = ApiFetch.API_URL + "/api/mood";

        StringRequest sr = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        displayMoods(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("HttpClient", "error: " + error.toString());
                    }
                })
        {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Authorization","Bearer " + token);
                return params;
            }
        };
        queue.add(sr);
    }

    private void displayMoods(String moods) {

        try {


            LinearLayout ll_vibe = (LinearLayout) findViewById(R.id.vibe_scroll);
            LinearLayout ll_feeling = (LinearLayout) findViewById(R.id.feeling_scroll);
            LinearLayout ll_lookingfor = (LinearLayout) findViewById(R.id.lookingfor_scroll);
            //LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            JSONArray parentArray = new JSONArray(moods);
            for (int i = 0; i < parentArray.length(); i++) {

                    final JSONObject mood = parentArray.getJSONObject(i);
                    String mood_type = mood.getString("type");

                if ( mood_type.equals("vibe") ) {
                    String mood_name = mood.getString("name");
                    String mood_id = mood.getString("id");

                    final Button moodButtonVibe = new Button(this);
                    moodButtonVibe.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    moodButtonVibe.setId(Integer.parseInt(mood_id));
                    moodButtonVibe.setText(mood_name);
                    ll_vibe.addView(moodButtonVibe);

                    moodButtonVibe.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            selectedVibe = moodButtonVibe.getId();
                        }
                    });


                } else if ( mood_type.equals("feeling") ) {
                    String mood_name = mood.getString("name");
                    String mood_id = mood.getString("id");

                    final Button moodButtonFeeling = new Button(this);
                    moodButtonFeeling.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    moodButtonFeeling.setId(Integer.parseInt(mood_id));
                    moodButtonFeeling.setText(mood_name);
                    ll_feeling.addView(moodButtonFeeling);

                    moodButtonFeeling.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            selectedFeeling = moodButtonFeeling.getId();
                        }
                    });

                } else if ( mood_type.equals("lookingFor") ) {
                    String mood_name = mood.getString("name");
                    String mood_id = mood.getString("id");

                    final Button moodButtonLookingFor = new Button(this);
                    moodButtonLookingFor.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    moodButtonLookingFor.setId(Integer.parseInt(mood_id));
                    moodButtonLookingFor.setText(mood_name);
                    ll_lookingfor.addView(moodButtonLookingFor);

                    moodButtonLookingFor.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            selectedLookingFor = moodButtonLookingFor.getId();
                        }
                    });
                }


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }



    }



    private void checkMoods() {

// check si tous les selected != 0.
        if (selectedVibe == 0 || selectedFeeling == 0 || selectedLookingFor == 0 ) {
            System.out.println("heeeeeeee");
            return;
        } else {

            StringRequest putRequest = new StringRequest(Request.Method.PUT, "http://nighty-develop.ivvp7jqj5r.eu-west-1.elasticbeanstalk.com/api/user/current",
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            // response
                            //Log.d("Response", response);
                            Intent i;
                            i = new Intent(MoodActivity.this, OnlineActivity.class);
                            startActivity(i);
                            finish();
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("Error.Response", error.getMessage());
                        }
                    }
            ) {

                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String>  params = new HashMap<String, String> ();
                    params.put("mood_ids[0]", String.valueOf(selectedFeeling));
                    params.put("mood_ids[1]", String.valueOf(selectedVibe));
                    params.put("mood_ids[2]", String.valueOf(selectedLookingFor));
                    params.put("is_online", "true");

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("Authorization","Bearer " + token);
                    return params;
                }

            };

            queue.add(putRequest);

        }
        //

    }


}

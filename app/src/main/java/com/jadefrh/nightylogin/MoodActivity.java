package com.jadefrh.nightylogin;

import android.content.SharedPreferences;
import android.net.http.RequestQueue;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jadefrh.nightylogin.InitActivity.MY_PREFS_NAME;

public class MoodActivity extends AppCompatActivity {

    private String token;
    TextView mainTitleMood;
    com.android.volley.RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);

        mainTitleMood = (TextView)findViewById(R.id.mainTitleMood);



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
                        Log.e("HttpClient", "success! response: " + response);
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
            String selectedFeeling = null;
            String selectedVibe = null;
            String selectedLookingFor = null;

            LinearLayout ll_vibe = (LinearLayout) findViewById(R.id.vibe_scroll);
            LinearLayout ll_feeling = (LinearLayout) findViewById(R.id.feeling_scroll);
            LinearLayout ll_lookingfor = (LinearLayout) findViewById(R.id.lookingfor_scroll);
            //LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            JSONArray parentArray = new JSONArray(moods);
            for (int i = 0; i < parentArray.length(); i++) {

                    JSONObject mood = parentArray.getJSONObject(i);
                    String mood_type = mood.getString("type");

                if ( mood_type.equals("vibe") ) {
                    String mood_name = mood.getString("name");
                    String mood_id = mood.getString("id");

                    Button moodButton = new Button(this);
                    moodButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    moodButton.setId(Integer.parseInt(mood_id));
                    moodButton.setText(mood_name);
                    ll_vibe.addView(moodButton);

                    Button moodButton2 = new Button(this);
                    moodButton2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    moodButton2.setId(Integer.parseInt(mood_id));
                    moodButton2.setText(mood_name);
                    ll_vibe.addView(moodButton2);

                    //System.out.println("test de l'id chosen : " + selectedVibe);


                } else if ( mood_type.equals("feeling") ) {
                    String mood_name = mood.getString("name");
                    String mood_id = mood.getString("id");

                    Button moodButton = new Button(this);
                    moodButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    moodButton.setId(Integer.parseInt(mood_id));
                    moodButton.setText(mood_name);
                    ll_feeling.addView(moodButton);

                    Button moodButton2 = new Button(this);
                    moodButton2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    moodButton2.setId(Integer.parseInt(mood_id));
                    moodButton2.setText(mood_name);
                    ll_feeling.addView(moodButton2);

                } else if ( mood_type.equals("lookingFor") ) {
                    String mood_name = mood.getString("name");
                    String mood_id = mood.getString("id");

                    Button moodButton = new Button(this);
                    moodButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    moodButton.setId(Integer.parseInt(mood_id));
                    moodButton.setText(mood_name);
                    ll_lookingfor.addView(moodButton);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }


}

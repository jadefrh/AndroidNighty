package com.jadefrh.nightylogin;

import android.content.SharedPreferences;
import android.net.http.RequestQueue;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jadefrh.nightylogin.helpers.ApiFetch;

import org.json.JSONObject;

import java.util.HashMap;
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

        System.out.println("teeeesttttt : " + token);

        queue = Volley.newRequestQueue(this);

        getMoods("feeling");
    }

    private String[] getMoods(final String filter) {
        final String url = ApiFetch.API_URL + "/api/mood?type=" + filter;

        StringRequest sr = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("HttpClient", "success! response: " + response);
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
        System.out.println("liennnn : " + sr);
        return new String[]{};
    }


}

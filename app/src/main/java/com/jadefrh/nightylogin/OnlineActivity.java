package com.jadefrh.nightylogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.jadefrh.nightylogin.InitActivity.MY_PREFS_NAME;

public class OnlineActivity extends AppCompatActivity {

    private com.android.volley.RequestQueue queue;
    private String token;
    private Button disconnect;
    private Button settingsButton;
    private boolean userStatus = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);

        System.out.println("alors?" + userStatus);

        SharedPreferences prefs = this.getSharedPreferences(MY_PREFS_NAME, this.MODE_PRIVATE);
        token = prefs.getString("nighty_access_token", null);

        settingsButton = (Button)findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i;
                i = new Intent(OnlineActivity.this, SettingsActivity.class);
                startActivity(i);

            }
        });

        disconnect = (Button)findViewById(R.id.disconnect);
        disconnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                user_is_offline();
            }
        });

        queue = Volley.newRequestQueue(this);

    }



    // Bouton pour se déconnecter
    private void user_is_offline(){

        StringRequest putRequest = new StringRequest(Request.Method.PUT, "http://nighty-develop.ivvp7jqj5r.eu-west-1.elasticbeanstalk.com/api/user/current",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        try {
                            JSONObject parentObject = new JSONObject(response);
                            userStatus = parentObject.getBoolean("is_online");
                            updateView();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("Response", response);

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
                Map<String, String>  params = new HashMap<String, String>();
                if (userStatus) {
                    System.out.println("is online = false");
                    params.put("is_online", "0");
                } else {
                    System.out.println("is online = true");
                    params.put("is_online", "1");
                }

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


    private void updateView(){
        if (userStatus) {

            disconnect.setText("deconnexion");
        } else {
            disconnect.setText("connexion");
        }
    }
}

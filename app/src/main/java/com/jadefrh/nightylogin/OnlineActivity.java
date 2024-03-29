package com.jadefrh.nightylogin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.quickblox.auth.QBAuth;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import static com.jadefrh.nightylogin.InitActivity.MY_PREFS_NAME;

public class OnlineActivity extends AppCompatActivity {

    TextView tv1, tv2;

    Typeface tf1, tf2;

    private com.android.volley.RequestQueue queue;
    private String token;
    private Button disconnect;
    private TextView titleConnect;
    private TextView subtitleConnect;
    private ImageView imageConnect;
    private ImageButton settingsButton;
    private boolean userStatus = true;
    private String uniqueId;
    private String fbToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);

        SharedPreferences prefs = this.getSharedPreferences(MY_PREFS_NAME, this.MODE_PRIVATE);
        token = prefs.getString("nighty_access_token", null);
        uniqueId = prefs.getString("identifier", null);
        fbToken = prefs.getString("fb_token", null);
        System.out.println("ici fbtoken : " + fbToken + " ettttttt : " + uniqueId);

        postDevice();

        settingsButton = (ImageButton) findViewById(R.id.settingsButton);
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

        titleConnect = (TextView)findViewById(R.id.titleConnect);
        subtitleConnect = (TextView)findViewById(R.id.subtitleConnect);
        imageConnect = (ImageView)findViewById(R.id.imageConnect);


        queue = Volley.newRequestQueue(this);
        System.out.println("alors?" + userStatus);

        tv1 = (TextView) findViewById(R.id.titleConnect);
        tv2 = (TextView) findViewById(R.id.subtitleConnect);

        tf1 = Typeface.createFromAsset(getAssets(),"fonts/Gotham Rounded Bold.otf");
        tv1.setTypeface(tf1);

        tf2 = Typeface.createFromAsset(getAssets(),"fonts/SFDisplay-Regular.otf");
        tv2.setTypeface(tf2);

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

    private void postDevice() {

        StringRequest postRequest = new StringRequest(Request.Method.POST, "http://nighty-develop.ivvp7jqj5r.eu-west-1.elasticbeanstalk.com/api/device",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("c bon", response);

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
                params.put("token", fbToken);
                params.put("platform", "android");
                params.put("identifier", uniqueId);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Authorization","Bearer " + token);
                return params;
            }

        };


        Volley.newRequestQueue(this).add(postRequest);
    }

    private void updateView(){
        //si l'user est connecté, afficher ça
        if (userStatus) {
            titleConnect.setText("Tu es connecté \uD83C\uDF89");
            subtitleConnect.setText("Tu peux maintenant quitter l’application, tu seras notifé dès que quelqu’un qui te correspond sera connecté !");
            imageConnect.setImageResource(R.drawable.online);
            disconnect.setText("Je ne suis plus disponible");
            //s'il est deconnecté afficher çaaaa
        } else {
            titleConnect.setText("Tu es déconnecté \uD83D\uDE22");
            subtitleConnect.setText("L’application ne te propose désormais plus personne. Tu peux quitter l’application avec l’assurance de ne plus rien recevoir !");
            imageConnect.setImageResource(R.drawable.offline);
            disconnect.setText("Se reconnecter");

        }
    }



}
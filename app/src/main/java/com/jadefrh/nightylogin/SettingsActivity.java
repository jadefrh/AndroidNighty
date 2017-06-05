package com.jadefrh.nightylogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.jadefrh.nightylogin.InitActivity.MY_PREFS_NAME;

public class SettingsActivity extends AppCompatActivity {



    TextView tv1, tv2, tv3;

    Typeface tf1, tf2, tf3;

    private com.android.volley.RequestQueue queue;
    private String token;

    private Button logout;
    private ImageButton female;
    private ImageButton male;
    private ImageButton both;
    private SeekBar distanceBar;
    private CrystalRangeSeekbar ageBar;
    private TextView distanceText;

    private int selectedDistance = 50;
    private String selectedGender = "both";
    private int[] selectedAge = {18, 60};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences prefs = this.getSharedPreferences(MY_PREFS_NAME, this.MODE_PRIVATE);
        token = prefs.getString("nighty_access_token", null);

        female = (ImageButton) findViewById(R.id.female);
        male = (ImageButton) findViewById(R.id.male);
        both = (ImageButton)findViewById(R.id.both);

        ageBar = (CrystalRangeSeekbar) findViewById(R.id.ageBar);



        ///////////////////// AGE BAR //////////////////////
        final TextView minAge = (TextView)findViewById(R.id.minAge);
        final TextView maxAge = (TextView)findViewById(R.id.maxAge);
        ageBar.setMinValue(18);
        ageBar.setMaxValue(60);
        // set listener
        ageBar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                minAge.setText(String.valueOf(minValue));
                maxAge.setText(String.valueOf(maxValue));
            }
        });

        // set final value listener
        ageBar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                selectedAge[0] = minValue.intValue();
                selectedAge[1] = maxValue.intValue();
                System.out.println("ageeee = " +selectedAge[0] + " - " + selectedAge[1]);
            }
        });

        ///////////////////// DISTANCE BAR //////////////////////
        distanceText = (TextView)findViewById(R.id.distanceText);
        distanceBar = (SeekBar)findViewById(R.id.distanceBar);
        distanceText.setText("MAXIMUM DISTANCE");
        distanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distanceText.setText("Maximum distance : "+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                selectedDistance = distanceBar.getProgress();
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

        female.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectedGender = "female";
            }
        });

        male.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectedGender = "male";
            }
        });

        both.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectedGender = "both";
            }
        });



        queue = Volley.newRequestQueue(this);





        tv1 = (TextView) findViewById(R.id.age);
        tv2 = (TextView) findViewById(R.id.distanceText);
        tv3 = (TextView) findViewById(R.id.interest);

        tf1 = Typeface.createFromAsset(getAssets(),"fonts/SFDisplay-Regular.otf");
        tv1.setTypeface(tf1);

        tf2 = Typeface.createFromAsset(getAssets(),"fonts/SFDisplay-Regular.otf");
        tv2.setTypeface(tf2);

        tf3 = Typeface.createFromAsset(getAssets(),"fonts/SFDisplay-Regular.otf");
        tv3.setTypeface(tf3);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            updatePreferences();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void updatePreferences() {
        System.out.println("selected distance : " + selectedDistance + " and selected gender : " + selectedGender);
        System.out.println("selected ages = " + selectedAge[0] + " ------ " + selectedAge[1]);
        StringRequest putRequest = new StringRequest(Request.Method.PUT, "http://nighty-develop.ivvp7jqj5r.eu-west-1.elasticbeanstalk.com/api/user/current",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response

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
                    params.put("distance", String.valueOf(selectedDistance));
                    params.put("interest", selectedGender);
                    params.put("ages[0]", String.valueOf(selectedAge[0]));
                    params.put("ages[1]", String.valueOf(selectedAge[1]));


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


}

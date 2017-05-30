package com.jadefrh.nightylogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import static com.jadefrh.nightylogin.InitActivity.MY_PREFS_NAME;

public class LauncherActivity extends AppCompatActivity {
    private static int TIME_OUT = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //LoginManager.getInstance().logOut();
                boolean loggedIn = AccessToken.getCurrentAccessToken() != null;
                Intent i;
                if (loggedIn) {
                    i = new Intent(LauncherActivity.this, InitActivity.class);

                } else {
                    i = new Intent(LauncherActivity.this, MainActivity.class);

                }
                startActivity(i);
                finish();
            }
        }, TIME_OUT);
    }
}

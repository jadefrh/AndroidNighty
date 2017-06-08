package com.jadefrh.nightylogin;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;

import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.jadefrh.nightylogin.helpers.SunTimeHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    TextView tv1, tv2, tv3;

    Typeface tf1, tf2, tf3;

    TextView txtStatus;
    LoginButton login_button;
    CallbackManager callbackManager;
    Button custom_login_button;
    ImageButton imageButton2;
    com.facebook.login.LoginManager fbLoginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);



        initializeControls();
        loginWithFB();
        //login_button.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));

        imageButton2 = (ImageButton) findViewById(R.id.imageButton2);

        tv1 = (TextView) findViewById(R.id.txtstatus);
        tv2 = (TextView) findViewById(R.id.subtitle);
        tv3 = (TextView) findViewById(R.id.fbwarning);

        tf1 = Typeface.createFromAsset(getAssets(),"fonts/Gotham Rounded Bold.otf");
        tv1.setTypeface(tf1);

        tf2 = Typeface.createFromAsset(getAssets(),"fonts/SFDisplay-Regular.otf");
        tv2.setTypeface(tf2);

        tf3 = Typeface.createFromAsset(getAssets(),"fonts/SFDisplay-Regular.otf");
        tv3.setTypeface(tf3);

        setTitle("MainActivity");

        fbLoginManager = com.facebook.login.LoginManager.getInstance();
        CallbackManager callbackManager = CallbackManager.Factory.create();

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbLoginManager.logInWithReadPermissions(MainActivity.this, Arrays.asList("email", "public_profile", "user_birthday"));
            }
        });


    }

    @Override public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }

    private void initializeControls() {
        callbackManager = CallbackManager.Factory.create();
        txtStatus = (TextView)findViewById(R.id.txtstatus);
    }

    private void loginWithFB() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Intent i = new Intent(MainActivity.this, InitActivity.class);
                System.out.println("TOKEN : "+loginResult.getAccessToken().getToken());
                startActivity(i);
                finish();
            }

            @Override
            public void onCancel() {
                txtStatus.setText("Login cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                txtStatus.setText("Login Error: "+error.getMessage());
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}

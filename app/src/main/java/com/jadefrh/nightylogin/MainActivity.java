package com.jadefrh.nightylogin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

    TextView txtStatus;
    LoginButton login_button;
    CallbackManager callbackManager;
    Button custom_login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);




        initializeControls();
        loginWithFB();
        login_button.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));

    }

    private void afterLogin() {
        System.out.println("hellooooooooo");

        //startActivity(new Intent(this, schedule_act.class));
//        new SunTimeHelper() {
//            @Override
//            protected void onPostExecute(String[] result) {
//                String sunrise = result[0];
//                String sunset = result[1];
//
//                try {
//                    SimpleDateFormat formatter = new SimpleDateFormat("h:mm:ss a", Locale.US);
//
//                    Date time1 = formatter.parse(sunset);
//                    Calendar calendar1 = Calendar.getInstance();
//                    calendar1.setTime(time1);
//                    int hour1 = calendar1.get(Calendar.HOUR);
//                    int minute1 = calendar1.get(Calendar.MINUTE);
//                    int second1 = calendar1.get(Calendar.SECOND);
//                    int sunsetTime = hour1 * 3600 + minute1 * 60 + second1;
//
//                    Date time2 = formatter.parse(sunrise);
//                    Calendar calendar2 = Calendar.getInstance();
//                    calendar2.setTime(time2);
//                    calendar2.add(Calendar.DATE, 1);
//                    int hour2 = calendar2.get(Calendar.HOUR);
//                    int minute2 = calendar2.get(Calendar.MINUTE);
//                    int second2 = calendar2.get(Calendar.SECOND);
//                    int sunriseTime = hour2 * 3600 + minute2 * 60 + second2;
//
//                    Calendar now = Calendar.getInstance();
//                    int hour3 = now.get(Calendar.HOUR);
//                    int minute3 = now.get(Calendar.MINUTE);
//                    int second3 = now.get(Calendar.SECOND);
//                    int currentTime = hour3 * 3600 + minute3 * 60 + second3;
//
//                    System.out.println(sunsetTime);
//                    System.out.println(currentTime);
//                    System.out.println(sunriseTime);
//
////                    Date x = calendar3.getTime();
////                    if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
////                        //checks whether the current time is between 14:49:00 and 20:11:13.
////                        startActivity(new Intent(MainActivity.this, MoodActivity.class));
////                    } else {
////                    }
//
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
    }


    private void initializeControls() {
        callbackManager = CallbackManager.Factory.create();

        txtStatus = (TextView)findViewById(R.id.txtStatus);
        login_button = (LoginButton)findViewById(R.id.login_button);
        //custom_login_button = (Button)findViewById(R.id.custom_login_button);
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

        //custom_login_button.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "email"));
        //    }
        //});
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}

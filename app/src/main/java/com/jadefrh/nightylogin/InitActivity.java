package com.jadefrh.nightylogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.jadefrh.nightylogin.helpers.ApiFetch;
import com.jadefrh.nightylogin.helpers.SunTimeHelper;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class InitActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    // Identifiant de la requete de permission
    public static final int RC_LOCATION = 1;

    private GoogleApiClient mGoogleApiClient;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    TextView fbaccesstoken;
    public static Location mLastLocation;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private LocationRequest mLocationRequest;

    private boolean checkTimeWaiting = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        fbaccesstoken = (TextView) findViewById(R.id.fbaccesstoken);
//       LoginManager.getInstance().logOut();



        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // On commence l'authentification en background
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                if (!hasToken()) {

                    String nightyTokenAccess = authenticate();
                    storeToken(nightyTokenAccess);
                }

                checkNightTime();

                return null;
            }
        }.execute();

        // On check si on a la permission ou non
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // On demande la permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    RC_LOCATION);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_LOCATION)
    private void requirePermissions() {
        String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "Please enable localisation", RC_LOCATION, perms);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Quand GoogleAPI est ready
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        // On a reçu une location
        if (checkTimeWaiting) {
            checkNightTime();
        }

        if (mLastLocation == null) {
            // Si on a pas de derniere localisation; on la request
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(UPDATE_INTERVAL)
                    .setFastestInterval(FASTEST_INTERVAL);

            // Request location updates
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    public void onLocationChanged(Location location) {
        // On a reçu une location
        mLastLocation = location;
        if (checkTimeWaiting) {
            checkNightTime();
        }
    }


    // get Facebook access token, sends to server to get Nighty Access Token
    private String authenticate() {

        AccessToken token = AccessToken.getCurrentAccessToken();

        // get Facebook Access Token
//        fbaccesstoken.setText(token.getToken());
        //System.out.println("FACEBOOK TOKEN: " + token.getToken());

        // POST request to API to get Nighty Access Token
        String url = ApiFetch.API_URL + "/oauth/token";
        String charset = "UTF-8";
        String clientId = "1_5g8ptf7pfeskcssssc8skok44kwcc0kwgsg8so8g4cwwgk0c0c";
        String clientSecret = "5t56m2ydfokkg8sowwkgksww00kkwwcoo0s4osw40ggs8s4s40";
        String grantType = "http://nighty-develop.ivvp7jqj5r.eu-west-1.elasticbeanstalk.com/oauth/auth/connect/facebook";
        String accessToken = token.getToken();

        try {
            String query = String.format("client_id=%s&client_secret=%s&grant_type=%s&access_token=%s",
                    URLEncoder.encode(clientId, charset),
                    URLEncoder.encode(clientSecret, charset),
                    URLEncoder.encode(grantType, charset),
                    URLEncoder.encode(accessToken, charset));

            URLConnection connection = new URL(url).openConnection();
            connection.setDoOutput(true); // Triggers POST.
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            try (OutputStream output = connection.getOutputStream()) {
                output.write(query.getBytes(charset));
            }

            InputStream response = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(response));

            StringBuffer buffer = new StringBuffer();

            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            String finalJson = buffer.toString();
            JSONObject parentObject = new JSONObject(finalJson);
            String nightyAccessToken = parentObject.getString("access_token");
            System.out.println("Access Token : " + nightyAccessToken);

            return nightyAccessToken;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    // store Nighty access token on device
    private void storeToken(String token) {
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("nighty_access_token", token);
        editor.apply();
    }

    private boolean hasToken() {
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString("nighty_access_token", null);

        return token != null;
    }

    // checks suntime to know if user can use the application
    private void checkNightTime() {
        String result = "";

        // On attends la location
        if (mLastLocation == null) {
            checkTimeWaiting = true;
            return;
        } else {
            checkTimeWaiting = false;
        }

        new SunTimeHelper(this) {
            @Override
            protected void onPostExecute(long[] longs) {
                System.out.println("resultats : " + longs[0]);
                long serviceStart = longs[0];
                long serviceEnd = longs[1];
                long currentTime = longs[2];

              //  boolean serviceIsOnline = currentTime > serviceStart && currentTime < serviceEnd;
                boolean serviceIsOnline = true;
                Intent i;
                if (serviceIsOnline){
                    System.out.println("TEEEEEEST");
                    i = new Intent(InitActivity.this, MoodActivity.class);
                } else {
                    i = new Intent(InitActivity.this, PatienceActivity.class);
                }
                startActivity(i);
                finish();
            }
        };


    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

}

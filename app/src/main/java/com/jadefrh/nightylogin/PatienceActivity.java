package com.jadefrh.nightylogin;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class PatienceActivity extends AppCompatActivity {

    TextView tv1, tv2, tv3 ;

    Typeface tf1, tf2, tf3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patience);

        tv1 = (TextView) findViewById(R.id.titre);
        tv2 = (TextView) findViewById(R.id.subtitle);
        tv3 = (TextView) findViewById(R.id.chrono);

        tf1 = Typeface.createFromAsset(getAssets(),"fonts/Gotham Rounded Bold.otf");
        tv1.setTypeface(tf1);

        tf2 = Typeface.createFromAsset(getAssets(),"fonts/SFDisplay-Regular.otf");
        tv2.setTypeface(tf2);

        tf3 = Typeface.createFromAsset(getAssets(),"fonts/SFDisplay-Regular.otf");
        tv3.setTypeface(tf3);

    }
}

package com.jadefrh.nightylogin;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.audiofx.BassBoost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jadefrh.nightylogin.helpers.SunTimeHelper;




public class PatienceActivity extends AppCompatActivity {

    TextView tv1, tv2, tv3;

    Typeface tf1, tf2, tf3;

    TextView countdown;

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


        countdown = (TextView) findViewById(R.id.countdown);
        displayCounter();
    }

    private void displayCounter() {

        new SunTimeHelper(this) {
            @Override
            protected void onPostExecute(long[] longs) {
                long serviceStart = longs[0];
                long currentTime = longs[2];
                long timeLeft = serviceStart - currentTime;

                final long SEC_PER_DAY = 24 * 60 * 60;
                final long SEC_PER_HOUR = 60 * 60;
                final long SEC_PER_MIN = 60;

                new CountDownTimer(timeLeft*1000, 1000) {
//
                    public void onTick(long timeLeft) {
                        long tot_sec   = timeLeft/1000;
                        long rem_days  = tot_sec / SEC_PER_DAY;
                        long rem_hours = (tot_sec % SEC_PER_DAY) / SEC_PER_HOUR;
                        long rem_mins  = ((tot_sec % SEC_PER_DAY) % SEC_PER_HOUR) / SEC_PER_MIN;
                        long rem_secs  = ((tot_sec % SEC_PER_DAY) % SEC_PER_HOUR) % SEC_PER_MIN;
                        StringBuilder sb = new StringBuilder();
                        if (rem_hours > 0) {
                            sb.append(rem_hours);
                            if (rem_hours == 1) {
                                sb.append(" hour, ");
                            } else sb.append(" hours, ");
                        }
                        if (rem_mins > 0 || rem_hours > 0) {
                            sb.append(rem_mins);
                            if (rem_mins == 1) {
                                sb.append(" minute and ");
                            } else sb.append(" minutes and ");
                        }
                        sb.append(rem_secs);
                        if (rem_secs == 1) {
                            sb.append(" second remaining.");
                        } else sb.append(" seconds remaining.");

                        countdown.setText(sb.toString());

                }

                    public void onFinish() {
                        Intent i;
                        i = new Intent(PatienceActivity.this, MoodActivity.class);
                    }
                }.start();
            }
        };
    }

}




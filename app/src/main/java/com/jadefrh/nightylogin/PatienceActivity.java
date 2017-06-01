package com.jadefrh.nightylogin;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jadefrh.nightylogin.helpers.SunTimeHelper;

public class PatienceActivity extends AppCompatActivity {

    TextView countdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patience);
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

                System.out.println("service start : " + serviceStart +", current time : " + currentTime);

                new CountDownTimer(timeLeft*1000, 1000) {
//
                    public void onTick(long timeLeft) {
                        long tot_sec   = timeLeft/1000;
                        long rem_days  = tot_sec / SEC_PER_DAY;
                        long rem_hours = (tot_sec % SEC_PER_DAY) / SEC_PER_HOUR;
                        long rem_mins  = ((tot_sec % SEC_PER_DAY) % SEC_PER_HOUR) / SEC_PER_MIN;
                        long rem_secs  = ((tot_sec % SEC_PER_DAY) % SEC_PER_HOUR) % SEC_PER_MIN;
                        countdown.setText(rem_hours + "hours, " + rem_mins + " minutes, " + rem_secs + " seconds remaining.");

                }
//
                    public void onFinish() {
                        countdown.setText("done!");
                    }
                }.start();
            }
        };
    }

}

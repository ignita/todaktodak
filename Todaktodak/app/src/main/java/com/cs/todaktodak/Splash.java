package com.cs.todaktodak;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yjchoi on 2017. 7. 30..
 */

public class Splash extends AppCompatActivity {
    private Timer timer;
    private ProgressWheel progressWheel;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        progressWheel.setSpinSpeed(0.333f);

        progressWheel.spin();
        final long period = 50;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //this repeats every 50 ms
                if (i < 50) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressWheel.spin();
                        }
                    });
                    i++;
                } else {
                    //closing the timer
                    timer.cancel();
                    Intent intent = new Intent(Splash.this, MainActivity.class);
                    startActivity(intent);
                    // close this activity
                    finish();
                }
            }
        }, 0, period);
    }
}

package com.cs.todaktodak;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by admin on 2017-07-18.
 */
public class LoadingActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_activity);

        startLoading();
    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }
}

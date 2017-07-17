package com.cookandroid.bigdata;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.LinearLayout;

/**
 * Created by admin on 2017-07-14.
 */
public class InfoActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab2_info);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout);
    }
}

package com.cookandroid.bigdata;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity {
    LocalActivityManager mLocalActivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, LodingActivity.class);
        startActivity(intent);

        mLocalActivityManager = new LocalActivityManager(this, false);
        mLocalActivityManager.dispatchCreate(savedInstanceState);


        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup(mLocalActivityManager);

        TabHost.TabSpec tabSpecmap = tabHost.newTabSpec("MAP").setIndicator("지도보기");
        tabSpecmap.setContent(new Intent(this, MapActivity.class));
        tabHost.addTab(tabSpecmap);

        TabHost.TabSpec tabSpecinfo = tabHost.newTabSpec("INFO").setIndicator("정보");
        tabSpecinfo.setContent(new Intent(this, InfoActivity.class));
        tabHost.addTab(tabSpecinfo);

        TabHost.TabSpec tabSpeclist = tabHost.newTabSpec("LIST").setIndicator("목록보기");
        tabSpeclist.setContent(new Intent(this, ListViewActivity.class));
        tabHost.addTab(tabSpeclist);
    }
}

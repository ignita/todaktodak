package com.cs.todaktodak;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by yjchoi on 2017. 7. 17..
 */

public class TwoFragment extends Fragment{
    public TwoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("11","Aa");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_two, container, false);
    }
}

package com.example.cs.materialtest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cs.materialtest.R;

/**
 * Created by CS on 2017-07-17.
 */
public class ThreeFragment extends Fragment{
    public ThreeFragment(){
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_three, container, false);
    }
}

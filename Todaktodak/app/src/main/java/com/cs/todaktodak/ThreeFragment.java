package com.cs.todaktodak;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by yjchoi on 2017. 7. 17..
 */

public class ThreeFragment extends Fragment{
    public ThreeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_three, container, false);

        final String[] mid = {"A", "B", "C", "D", "F"};

        ListView listview = (ListView) view.findViewById(R.id.list_item);

        ArrayAdapter<String> Adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mid);
        listview.setAdapter(Adapter);

        return view;
    }
}

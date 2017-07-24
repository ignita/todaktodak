package com.cs.todaktodak;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by yjchoi on 2017. 7. 20..
 */

public class MarkerItem implements ClusterItem {

    private LatLng location;
    private String address;
    private String title;
    private String snippet;
    BitmapDescriptor icon;

    public MarkerItem(BitmapDescriptor icon, LatLng location, String address, String title, String snippet) {
        this.icon = icon;
        this.location = location;
        this.address = address;
        this.title = title;
        this.snippet = snippet;
    }

    public BitmapDescriptor getIcon() { return icon; }
    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getTitle() {
        return title;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public LatLng getPosition() {
        return location;
    }

}

package com.cs.todaktodak;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by yjchoi on 2017. 7. 25..
 */

public class PetHospital {

    private String name;
    private String address;
    private String phone;
    private int num;
    private String time;
    private String night;
    private String homepage;
    private LatLng latLng;

    public PetHospital (String name, String address, String phone, int num, String time, String night, String homepage, LatLng latLng) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.num = num;
        this.time = time;
        this.night = night;
        this.homepage = homepage;
        this.latLng = latLng;
    }

    public String getName() { return name; }

    public String getAddress() { return address; }

    public String getPhone() { return phone; }

    public Integer getNum() { return num; }

    public String getTime() { return time; }

    public String getNight() { return night; }

    public String getHomepage() { return homepage; }

    public LatLng getLatLng() { return latLng; }
}

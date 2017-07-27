package com.cs.todaktodak;

/**
 * Created by yjchoi on 2017. 7. 25..
 */

public class PetHospital {

    private String name;
    private String address;
    private String phone;
    private int num;

    public PetHospital (String name, String address, String phone, int num) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.num = num;
    }

    public String getName() { return name; }

    public String getAddress() { return address; }

    public String getPhone() { return phone; }

    public Integer getNum() { return num; }

}

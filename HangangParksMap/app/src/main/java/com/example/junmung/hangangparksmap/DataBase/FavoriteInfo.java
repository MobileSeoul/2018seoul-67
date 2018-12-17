package com.example.junmung.hangangparksmap.DataBase;

import com.example.junmung.hangangparksmap.FavoritePoint;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FavoriteInfo extends RealmObject {
    @PrimaryKey
    private int index;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String url;
    private Date date;

    public FavoriteInfo() {
    }

    public FavoriteInfo(int index, String name, String address, double latitude, double longitude, Date date) {
        this.index = index;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getUrl() {
        return url;
    }

    public Date getDate() {
        return date;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

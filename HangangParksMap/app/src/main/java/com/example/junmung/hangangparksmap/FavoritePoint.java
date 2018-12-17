package com.example.junmung.hangangparksmap;

import com.example.junmung.hangangparksmap.ARGuide.Point;

import java.util.Date;

public class FavoritePoint extends Point{
    private String address;
    private String url;
    private Date date;

    public FavoritePoint(String name, double latitude, double longitude, String address, Date date) {
        super(name, latitude, longitude);
        this.address = address;
        this.date = date;
    }

    public FavoritePoint(CommonPoint point){
        super(point.getName(), point.latitude, point.longitude);
        address = point.getAddress();
        date = new Date();
        if(point.hasUrl())
            url = point.getUrl();
    }

    public FavoritePoint(CulturePoint point){
        super(point.getName(), point.latitude, point.longitude);
        address = point.getAddress();
        date = new Date();

        url = point.getUrl();
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean hasUrl(){
        if(url == null)
            return false;
        else
            return true;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date registerDate) {
        this.date = registerDate;
    }
}

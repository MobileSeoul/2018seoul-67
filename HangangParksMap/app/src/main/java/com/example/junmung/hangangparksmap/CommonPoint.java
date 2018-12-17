package com.example.junmung.hangangparksmap;

import android.util.Log;

import com.example.junmung.hangangparksmap.ARGuide.Point;
import com.example.junmung.hangangparksmap.SearchPointPOJO.Document;

public class CommonPoint extends Point {
    private String address;
    private int distance;
    private String url;

    public CommonPoint(String name, double latitude, double longitude, String address) {
        super(name, latitude, longitude);
        this.address = address;
    }

    public CommonPoint(Document document){
        super(document.getPlaceName(), Double.parseDouble(document.getLatitude()), Double.parseDouble(document.getLongitude()), 0);
        Log.d("Documents Name", document.getPlaceName());

        address = document.getAddressName();
        distance = Integer.parseInt(document.getDistance());
        url = document.getPlaceUrl();
    }

    public CommonPoint(FavoritePoint point) {
        super(point.getName(), point.latitude, point.longitude);
        this.address = point.getAddress();
        this.url = point.getUrl();
    }


    public String getAddress() {
        return address;
    }

    public int getDistance() {
        return distance;
    }

    public String getUrl() {
        return url;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean hasUrl(){
        if(this.url == null)
            return false;
        else
            return true;
    }
}

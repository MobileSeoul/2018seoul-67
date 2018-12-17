package com.example.junmung.hangangparksmap;

import com.example.junmung.hangangparksmap.ARGuide.Point;

public class CulturePoint extends Point{
    private String address;
    private String parkName;
    private String eventDate;
    private String eventTime;
    private String pointType;
    private int distance;
    private String url;

    public CulturePoint(String name, double latitude, double longitude, String address, String parkName, String eventDate, String eventTime, String pointType) {
        super(name, latitude, longitude);
        this.address = address;
        this.parkName = parkName;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.pointType = pointType;
    }

    public CulturePoint(FavoritePoint point) {
        super(point.getName(), point.latitude, point.longitude);
        this.address = point.getAddress();
        this.url = point.getUrl();
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

    public String getParkName() {
        return parkName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public String getPointType() {
        return pointType;
    }

    public int getDistance() {
        return distance;
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

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public void setPointType(String pointType) {
        this.pointType = pointType;
    }

    public void setDistance(int distance) {
        this.distance = distance;
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
}

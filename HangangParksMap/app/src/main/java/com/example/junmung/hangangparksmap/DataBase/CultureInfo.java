package com.example.junmung.hangangparksmap.DataBase;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CultureInfo extends RealmObject{
    @PrimaryKey
    private int index;
    private String contentsName;
    private String address;
    private double latitude;
    private double longitude;
    private String parkName;
    private String eventDate;
    private String eventTime;
    private String pointType;

    public CultureInfo() {
    }

    public CultureInfo(int index, String contentsName, String address, double latitude, double longitude, String parkName, String eventDate, String eventTime, String pointType) {
        this.index = index;
        this.contentsName = contentsName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.parkName = parkName;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.pointType = pointType;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getContentsName() {
        return contentsName;
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

    public void setContentsName(String contentsName) {
        this.contentsName = contentsName;
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
}

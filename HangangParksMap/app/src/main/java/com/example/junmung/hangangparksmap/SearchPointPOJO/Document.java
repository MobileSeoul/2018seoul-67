package com.example.junmung.hangangparksmap.SearchPointPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Document {

    @SerializedName("place_name")
    @Expose
    private String placeName;

    @SerializedName("distance")
    @Expose
    private String distance;

    @SerializedName("place_url")
    @Expose
    private String placeUrl;

    @SerializedName("address_name")
    @Expose
    private String addressName;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("x")
    @Expose
    private String longitude;

    @SerializedName("y")
    @Expose
    private String latitude;

    public Document(String placeName, String addressName, String distance, String latitude, String longitude){
        this.placeName = placeName;
        this.addressName = addressName;
        this.distance = distance;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPlaceUrl() {
        return placeUrl;
    }

    public void setPlaceUrl(String placeUrl) {
        this.placeUrl = placeUrl;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

}
package com.example.junmung.hangangparksmap.CulturePointPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Row {
    @SerializedName("COT_ADDR_FULL_OLD")
    @Expose
    private String address;

    @SerializedName("COT_COORD_X")
    @Expose
    private Double longitude;

    @SerializedName("COT_COORD_Y")
    @Expose
    private Double latitude;


    @SerializedName("COT_CONTS_NAME")
    @Expose
    private String contentsName;

    @SerializedName("COT_TEL_NO")
    @Expose
    private String telNumber;

    @SerializedName("COT_VALUE_01")
    @Expose
    private String eventPark;

    @SerializedName("COT_VALUE_02")
    @Expose
    private String eventDate;

    @SerializedName("COT_VALUE_03")
    @Expose
    private String eventTime;


    @SerializedName("COT_COORD_TYPE")
    @Expose
    private String coordinateType;
    // 좌표 타입 (1:Point 3:Polyline 4:MultiPolyline 5:Polygon 6:MultiPolygon 8:multiPoint)




    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getContentsName() {
        return contentsName;
    }

    public void setContentsName(String contentsName) {
        this.contentsName = contentsName;
    }


    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    public String getEventPark() {
        return eventPark;
    }

    public void setEventPark(String eventPark) {
        this.eventPark = eventPark;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }




    public String getCoordinateType() {
        return coordinateType;
    }

    public void setCoordinateType(String coordinateType) {
        this.coordinateType = coordinateType;
    }



}
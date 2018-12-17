package com.example.junmung.hangangparksmap.ARGuide;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

public class Point {
    private String name;
    private Location location;
    public double latitude;
    public double longitude;


    public Point(String name, double latitude, double longitude, double altitude) {
        this.name = name;
        location = new Location("Point");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setAltitude(altitude);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Point(Location location){
        name = " ";
        this.location = location;
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    public Point(String name, double latitude, double longitude){
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public void updateLocation(Location updatedLocation){
        location = updatedLocation;
        latitude = updatedLocation.getLatitude();
        longitude = updatedLocation.getLongitude();
    }

    public void updateLatLon(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int distanceTo(Point point){
        return (int)SphericalUtil.computeDistanceBetween(
                new LatLng(latitude, longitude),
                new LatLng(point.latitude, point.longitude)
        );
    }

    static public int distance(LatLng point_1, LatLng point_2){
        return (int)SphericalUtil.computeDistanceBetween(point_1, point_2);
    }


    public double bearingTo(Point dest_Point) {
        // 현재 위치 : 위도나 경도는 지구 중심을 기반으로 하는 각도이기 때문에 라디안 각도로 변환한다.
        double Cur_Lat_radian = latitude * (Math.PI / 180);
        double Cur_Lon_radian = longitude * (Math.PI / 180);

        // 목표 위치 : 위도나 경도는 지구 중심을 기반으로 하는 각도이기 때문에 라디안 각도로 변환한다.
        double Dest_Lat_radian = dest_Point.latitude * (Math.PI / 180);
        double Dest_Lon_radian = dest_Point.longitude * (Math.PI / 180);

        // radian distance
        double radian_distance = (Math.acos(Math.sin(Cur_Lat_radian) * Math.sin(Dest_Lat_radian)
                + Math.cos(Cur_Lat_radian) * Math.cos(Dest_Lat_radian) * Math.cos(Cur_Lon_radian - Dest_Lon_radian)));


        // 목적지 이동 방향을 구한다.(현재 좌표에서 다음 좌표로 이동하기 위해서는 방향을 설정해야 한다. 라디안값이다.
        // acos의 인수로 주어지는 x는 360분법의 각도가 아닌 radian(호도)값이다.
        double radian_bearing =
                (Math.acos((Math.sin(Dest_Lat_radian) - Math.sin(Cur_Lat_radian) * Math.cos(radian_distance))
                        / (Math.cos(Cur_Lat_radian) * Math.sin(radian_distance))));


        double true_bearing;

        if (Math.sin(Dest_Lon_radian - Cur_Lon_radian) < 0) {
            true_bearing = radian_bearing * (180 / Math.PI);
            true_bearing = 360 - true_bearing;
        }
        else {
            true_bearing = radian_bearing * (180 / Math.PI);
        }

        return true_bearing;
    }

}

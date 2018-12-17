package com.example.junmung.hangangparksmap.Culture;

import com.example.junmung.hangangparksmap.CulturePoint;

public class CultureItem {
    private String name;
    private String date;
    private String area;
    private boolean favorite;

    public CultureItem(CulturePoint point){
        name = point.getName();
        date = point.getEventDate();
        area = point.getParkName();
        favorite = false;
    }

    public CultureItem(CulturePoint point, boolean isFavorite){
        name = point.getName();
        date = point.getEventDate();
        area = point.getParkName();
        favorite = isFavorite;
    }
    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getArea() {
        return area;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public boolean isFavorite(){
        if(favorite)
            return true;
        else
            return false;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}

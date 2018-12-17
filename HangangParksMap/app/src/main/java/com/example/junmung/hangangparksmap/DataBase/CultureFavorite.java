package com.example.junmung.hangangparksmap.DataBase;

import io.realm.RealmObject;

public class CultureFavorite extends RealmObject{
    private String contentName;

    public CultureFavorite(){

    }

    public CultureFavorite(String name){
        contentName = name;
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }
}

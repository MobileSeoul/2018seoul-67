package com.example.junmung.hangangparksmap.Map.Dialog;

import android.graphics.Bitmap;

public class FilterItem {
    private String name;
    private int imgRes;

    public FilterItem(String name, int res) {
        this.name = name;
        this.imgRes = res;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public int getImgRes() {
        return imgRes;
    }

    public void setImgRes(int imgRes) {
        this.imgRes = imgRes;
    }
}

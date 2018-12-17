package com.example.junmung.hangangparksmap.CulturePointPOJO;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CulturePojo {

    @SerializedName("Mgishangang")
    @Expose
    private Mgishangang mgishangang;

    public Mgishangang getMgishangang() {
        return mgishangang;
    }

    public void setMgishangang(Mgishangang mgishangang) {
        this.mgishangang = mgishangang;
    }

}








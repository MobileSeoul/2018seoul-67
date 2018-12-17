package com.example.junmung.hangangparksmap.CulturePointPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RESULT {

    @SerializedName("CODE")
    @Expose
    private String code;
    @SerializedName("MESSAGE")
    @Expose
    private String message;

    public String getCODE() {
        return code;
    }

    public void setCODE(String code) {
        this.code = code;
    }

    public String getMESSAGE() {
        return message;
    }

    public void setMESSAGE(String message) {
        this.message = message;
    }

}

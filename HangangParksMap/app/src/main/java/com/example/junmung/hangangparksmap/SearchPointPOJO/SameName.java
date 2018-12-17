package com.example.junmung.hangangparksmap.SearchPointPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SameName {

    @SerializedName("region")
    @Expose
    private List<String> region = null;
    @SerializedName("keyword")
    @Expose
    private String keyword;
    @SerializedName("selected_region")
    @Expose
    private String selectedRegion;

    public List<String> getRegion() {
        return region;
    }

    public void setRegion(List<String> region) {
        this.region = region;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getSelectedRegion() {
        return selectedRegion;
    }

    public void setSelectedRegion(String selectedRegion) {
        this.selectedRegion = selectedRegion;
    }

}

package com.example.junmung.hangangparksmap.CulturePointPOJO;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Mgishangang {

    @SerializedName("list_total_count")
    @Expose
    private Integer listTotalCount;

    @SerializedName("RESULT")
    @Expose
    private RESULT result;

    @SerializedName("row")
    @Expose
    private List<Row> rows = null;

    public Integer getListTotalCount() {
        return listTotalCount;
    }

    public void setListTotalCount(Integer listTotalCount) {
        this.listTotalCount = listTotalCount;
    }

    public RESULT getRESULT() {
        return result;
    }

    public void setRESULT(RESULT result) {
        this.result = result;
    }

    public List<Row> getRow() {
        return rows;
    }

    public void setRow(List<Row> row) {
        this.rows = row;
    }

}
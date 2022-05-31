package com.codebrew.clikat.modal;

/*
 * Created by cbl80 on 30/5/16.
 */

import java.util.ArrayList;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class BarcodeData {

    @SerializedName("list")
    @Expose
    private java.util.List<BarcodeList> list = new ArrayList<BarcodeList>();

    /**
     *
     * @return
     * The list
     */
    public java.util.List<BarcodeList> getList() {
        return list;
    }

    /**
     *
     * @param list
     * The list
     */
    public void setList(java.util.List<BarcodeList> list) {
        this.list = list;
    }

}
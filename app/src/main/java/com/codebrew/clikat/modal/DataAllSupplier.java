package com.codebrew.clikat.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class DataAllSupplier {

    @SerializedName("list"/*"supplierList"*/)
    @Expose
    private List<SupplierList> supplierList = new ArrayList<SupplierList>();

    @SerializedName("sponser")
    @Expose
    private SupplierList sponser;

    /**
     * @return The supplierList
     */
    public List<SupplierList> getSupplierList() {
        return supplierList;
    }

    /**
     * @param supplierList The supplierList
     */
    public void setSupplierList(List<SupplierList> supplierList) {
        this.supplierList = supplierList;
    }


    /**
     * @return The sponser
     */
    public SupplierList getSponser() {
        return sponser;
    }

    /**
     * @param sponser The sponser
     */
    public void setSponser(SupplierList sponser) {
        this.sponser = sponser;
    }




}
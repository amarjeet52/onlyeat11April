package com.codebrew.clikat.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by cbl80 on 8/6/16.
 */
public class LoyalityCartServer {
    @SerializedName("supplierBranchId")
    @Expose
    private int supplierBranchId;//

    @SerializedName("deliveryType")
    @Expose
    private int deliveryType;//

    @SerializedName("deliveryAddressId")
    @Expose
    private int deliveryAddressId;//

    @SerializedName("totalPoints")
    @Expose
    private int totalPoints;//
    @SerializedName("deliveryDate")
    @Expose
    private String deliveryDate;//
    @SerializedName("isPostponed")
    @Expose
    private int is_postponed;//
    @SerializedName("urgent")
    @Expose
    private int urgent;//
    @SerializedName("urgentPrice")
    @Expose
    private float urgentPrice;//
    @SerializedName("remarks")
    @Expose
    private String remarks="0";
    @SerializedName("accessToken")
    @Expose
    private String accessToken;
    @SerializedName("languageId")
    @Expose
    private int languageId;

    @SerializedName("productList")
    @Expose
    private List<Integer> productList=new ArrayList<>();
    //


    public List<Integer> getProductList() {
        return productList;
    }

    public void setProductList(List<Integer> productList) {
        this.productList = productList;
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }



    public int getSupplierBranchId() {
        return supplierBranchId;
    }

    public void setSupplierBranchId(int supplierBranchId) {
        this.supplierBranchId = supplierBranchId;
    }

    public int getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(int deliveryType) {
        this.deliveryType = deliveryType;
    }

    public int getDeliveryAddressId() {
        return deliveryAddressId;
    }

    public void setDeliveryAddressId(int deliveryAddressId) {
        this.deliveryAddressId = deliveryAddressId;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public int getIs_postponed() {
        return is_postponed;
    }

    public void setIs_postponed(int is_postponed) {
        this.is_postponed = is_postponed;
    }

    public int getUrgent() {
        return urgent;
    }

    public void setUrgent(int urgent) {
        this.urgent = urgent;
    }

    public float getUrgentPrice() {
        return urgentPrice;
    }

    public void setUrgentPrice(float urgentPrice) {
        this.urgentPrice = urgentPrice;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

}

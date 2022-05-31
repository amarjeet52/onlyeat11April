package com.codebrew.clikat.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Supplier {

    @SerializedName("supplier_branch_id")
    @Expose
    private Integer supplierBranchId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("delivery_max_time")
    @Expose
    private Integer deliveryMaxTime;
    @SerializedName("delivery_min_time")
    @Expose
    private Integer deliveryMinTime;
    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("total_reviews")
    @Expose
    private Integer totalReviews;
    @SerializedName("supplier_id")
    @Expose
    private Integer supplierId;
    @SerializedName("rating")
    @Expose
    private float rating;
    @SerializedName("payment_method")
    @Expose
    private Integer paymentMethod;
    @SerializedName("business_start_date")
    @Expose
    private Integer businessStartDate;


    @SerializedName("category")
    @Expose
    private List<CategoryFavourites> category = new ArrayList<CategoryFavourites>();

    /**
     * @return The category
     */
    public List<CategoryFavourites> getCategory() {
        return category;
    }

    /**
     * @param category The category
     */
    public void setCategory(List<CategoryFavourites> category) {
        this.category = category;
    }

    /**
     * @return The supplierBranchId
     */
    public Integer getSupplierBranchId() {
        return supplierBranchId;
    }

    /**
     * @param supplierBranchId The supplier_branch_id
     */
    public void setSupplierBranchId(Integer supplierBranchId) {
        this.supplierBranchId = supplierBranchId;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address The address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return The deliveryMaxTime
     */
    public Integer getDeliveryMaxTime() {
        return deliveryMaxTime;
    }

    /**
     * @param deliveryMaxTime The delivery_max_time
     */
    public void setDeliveryMaxTime(Integer deliveryMaxTime) {
        this.deliveryMaxTime = deliveryMaxTime;
    }

    /**
     * @return The deliveryMinTime
     */
    public Integer getDeliveryMinTime() {
        return deliveryMinTime;
    }

    /**
     * @param deliveryMinTime The delivery_min_time
     */
    public void setDeliveryMinTime(Integer deliveryMinTime) {
        this.deliveryMinTime = deliveryMinTime;
    }

    /**
     * @return The logo
     */
    public String getLogo() {
        return logo;
    }

    /**
     * @param logo The logo
     */
    public void setLogo(String logo) {
        this.logo = logo;
    }

    /**
     * @return The status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return The totalReviews
     */
    public Integer getTotalReviews() {
        return totalReviews;
    }

    /**
     * @param totalReviews The total_reviews
     */
    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }

    /**
     * @return The supplierId
     */
    public Integer getSupplierId() {
        return supplierId;
    }

    /**
     * @param supplierId The supplier_id
     */
    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    /**
     * @return The rating
     */
    public float getRating() {
        return rating;
    }

    /**
     * @param rating The rating
     */
    public void setRating(Integer rating) {
        this.rating = rating;
    }

    /**
     * @return The paymentMethod
     */
    public Integer getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * @param paymentMethod The payment_method
     */
    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * @return The businessStartDate
     */
    public Integer getBusinessStartDate() {
        return businessStartDate;
    }

    /**
     * @param businessStartDate The business_start_date
     */
    public void setBusinessStartDate(Integer businessStartDate) {
        this.businessStartDate = businessStartDate;
    }

}
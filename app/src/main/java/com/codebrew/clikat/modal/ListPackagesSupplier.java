package com.codebrew.clikat.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class ListPackagesSupplier {

    @SerializedName("icon")
    @Expose
    private String icon;

    @SerializedName("supplier_name")
    @Expose
    private String supplierName;

    @SerializedName("logo")
    @Expose
    private String logo;

    @SerializedName("status")
    @Expose
    private Integer status;

    @SerializedName("payment_method")
    @Expose
    private Integer paymentMethod;

    @SerializedName("delivery_min_time")
    @Expose
    private Integer deliveryMinTime;

    @SerializedName("delivery_max_time")
    @Expose
    private Integer deliveryMaxTime;

    @SerializedName("total_reviews")
    @Expose
    private Integer totalReviews;

    @SerializedName("rating")
    @Expose
    private Float rating;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("category_id")
    @Expose
    private Integer categoryId;
    public Integer order;

    @SerializedName("min_order")
    @Expose
    private float minOrder=0;

    public float getMinOrder() {
        return minOrder;
    }

    public void setMinOrder(Integer minOrder) {
        this.minOrder = minOrder;
    }

    public void setCommissionPackage(int commissionPackage) {
        this.commissionPackage = commissionPackage;
    }


    @SerializedName("commission_package")
    @Expose
    private Integer commissionPackage=3;

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }



    @SerializedName("supplier_id")
    @Expose
    private Integer supplierId;

    @SerializedName("supplier_branch_id")
    @Expose
    private Integer supplierBranchId;
    @SerializedName("category")
    @Expose
    private java.util.List<CategoryPackages> category = new ArrayList<CategoryPackages>();

    /**
     * @return The icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @param icon The icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * @return The supplierName
     */
    public String getSupplierName() {
        return supplierName;
    }

    /**
     * @param supplierName The supplier_name
     */
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    /**
     * @return The ic_splash
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
     * @return The rating
     */
    public Float getRating() {
        return rating;
    }

    /**
     * @param rating The rating
     */
    public void setRating(Float rating) {
        this.rating = rating;
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
     * @return The categoryId
     */
    public Integer getCategoryId() {
        return categoryId;
    }

    /**
     * @param categoryId The category_id
     */
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
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
     * @return The category
     */
    public java.util.List<CategoryPackages> getCategory() {
        return category;
    }

    /**
     * @param category The category
     */
    public void setCategory(java.util.List<CategoryPackages> category) {
        this.category = category;
    }


    public int getCommissionPackage() {
        return commissionPackage;
    }
}
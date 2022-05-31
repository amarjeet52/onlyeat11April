package com.codebrew.clikat.modal;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Favourite {

    @SerializedName("min_order")
    @Expose
    private float min_order;
    @SerializedName("supplier_branch_id")
    @Expose
    private Integer supplier_branch_id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("delivery_max_time")
    @Expose
    private Integer delivery_max_time;
    @SerializedName("delivery_min_time")
    @Expose
    private Integer delivery_min_time;
    @SerializedName("supplier_image")
    @Expose
    private String supplier_image;
    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("total_reviews")
    @Expose
    private Integer total_reviews;
    @SerializedName("supplier_id")
    @Expose
    private Integer supplier_id;
    @SerializedName("rating")
    @Expose
    private Float rating;
    @SerializedName("payment_method")
    @Expose
    private Integer payment_method;
    @SerializedName("business_start_date")
    @Expose
    private String business_start_date;
    @SerializedName("category")
    @Expose
    private List<CategoryFavourites> category = new ArrayList<CategoryFavourites>();

    @SerializedName("commission_package")
    @Expose
    private Integer commissionPackage=3;

    public Integer getCommissionPackage() {
        return commissionPackage;
    }

    /**
     *
     * @return
     * The min_order
     */
    public float getMin_order() {
        return min_order;
    }

    /**
     *
     * @param min_order
     * The min_order
     */
    public void setMin_order(Integer min_order) {
        this.min_order = min_order;
    }

    /**
     *
     * @return
     * The supplier_branch_id
     */
    public Integer getSupplier_branch_id() {
        return supplier_branch_id;
    }

    /**
     *
     * @param supplier_branch_id
     * The supplier_branch_id
     */
    public void setSupplier_branch_id(Integer supplier_branch_id) {
        this.supplier_branch_id = supplier_branch_id;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The address
     */
    public String getAddress() {
        return address;
    }

    /**
     *
     * @param address
     * The address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     *
     * @return
     * The delivery_max_time
     */
    public Integer getDelivery_max_time() {
        return delivery_max_time;
    }

    /**
     *
     * @param delivery_max_time
     * The delivery_max_time
     */
    public void setDelivery_max_time(Integer delivery_max_time) {
        this.delivery_max_time = delivery_max_time;
    }

    /**
     *
     * @return
     * The delivery_min_time
     */
    public Integer getDelivery_min_time() {
        return delivery_min_time;
    }

    /**
     *
     * @param delivery_min_time
     * The delivery_min_time
     */
    public void setDelivery_min_time(Integer delivery_min_time) {
        this.delivery_min_time = delivery_min_time;
    }

    /**
     *
     * @return
     * The supplier_image
     */
    public String getSupplier_image() {
        return supplier_image;
    }

    /**
     *
     * @param supplier_image
     * The supplier_image
     */
    public void setSupplier_image(String supplier_image) {
        this.supplier_image = supplier_image;
    }

    /**
     *
     * @return
     * The ic_splash
     */
    public String getLogo() {
        return logo;
    }

    /**
     *
     * @param logo
     * The ic_splash
     */
    public void setLogo(String logo) {
        this.logo = logo;
    }

    /**
     *
     * @return
     * The status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The total_reviews
     */
    public Integer getTotal_reviews() {
        return total_reviews;
    }

    /**
     *
     * @param total_reviews
     * The total_reviews
     */
    public void setTotal_reviews(Integer total_reviews) {
        this.total_reviews = total_reviews;
    }

    /**
     *
     * @return
     * The supplier_id
     */
    public Integer getSupplier_id() {
        return supplier_id;
    }

    /**
     *
     * @param supplier_id
     * The supplier_id
     */
    public void setSupplier_id(Integer supplier_id) {
        this.supplier_id = supplier_id;
    }

    /**
     *
     * @return
     * The rating
     */
    public Float getRating() {
        return rating;
    }

    /**
     *
     * @param rating
     * The rating
     */
    public void setRating(Float rating) {
        this.rating = rating;
    }

    /**
     *
     * @return
     * The payment_method
     */
    public Integer getPayment_method() {
        return payment_method;
    }

    /**
     *
     * @param payment_method
     * The payment_method
     */
    public void setPayment_method(Integer payment_method) {
        this.payment_method = payment_method;
    }

    /**
     *
     * @return
     * The business_start_date
     */
    public String getBusiness_start_date() {
        return business_start_date;
    }

    /**
     *
     * @param business_start_date
     * The business_start_date
     */
    public void setBusiness_start_date(String business_start_date) {
        this.business_start_date = business_start_date;
    }

    /**
     *
     * @return
     * The category
     */
    public List<CategoryFavourites> getCategory() {
        return category;
    }

    /**
     *
     * @param category
     * The category
     */
    public void setCategory(List<CategoryFavourites> category) {
        this.category = category;
    }

}

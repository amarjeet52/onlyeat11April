package com.codebrew.clikat.modal;

/*
 * Created by cbl80 on 8/7/16.
 */
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class CompareResultDetail {

    @SerializedName("start_time")
    @Expose
    private String start_time;
    @SerializedName("end_time")
    @Expose
    private String end_time;
    @SerializedName("total_reviews")
    @Expose
    private Integer total_reviews;
    @SerializedName("delivery_min_time")
    @Expose
    private Integer delivery_min_time;
    @SerializedName("delivery_max_time")
    @Expose
    private Integer delivery_max_time;
    @SerializedName("rating")
    @Expose
    private float rating;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("payment_method")
    @Expose
    private Integer payment_method;
    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("fixed_price")
    @Expose
    private String fixed_price;
    @SerializedName("handling_supplier")
    @Expose
    private Integer handling_supplier;
    @SerializedName("handling_admin")
    @Expose
    private Integer handling_admin;
    @SerializedName("can_urgent")
    @Expose
    private Integer can_urgent;
    @SerializedName("category_id")
    @Expose
    private Integer category_id;
    @SerializedName("sub_category_id")
    @Expose
    private Integer sub_category_id;
    @SerializedName("supplier_id")
    @Expose
    private Integer supplier_id;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("offer_name")
    @Expose
    private String offer_name;
    @SerializedName("display_price")
    @Expose
    private String display_price;
    @SerializedName("measuring_unit")
    @Expose
    private String measuring_unit;
    @SerializedName("product_desc")
    @Expose
    private String product_desc;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("product_image")
    @Expose
    private String product_image;
    @SerializedName("supplier_image")
    @Expose
    private String supplier_image;
    @SerializedName("product_id")
    @Expose
    private Integer product_id;
    @SerializedName("supplier_branch_id")
    @Expose
    private Integer supplier_branch_id;
    @SerializedName("delivery_charges")
    @Expose
    private Integer delivery_charges;
    @SerializedName("min_order")
    @Expose
    private Integer min_order;
    @SerializedName("charges_below_min_order")
    @Expose
    private Integer charges_below_min_order;
    @SerializedName("supplier_name")
    @Expose
    private String supplier_name;

    @SerializedName("commission_package")
    @Expose
    private int commissionPackage;

    public int getCommissionPackage() {
        return commissionPackage;
    }

    @SerializedName("price_type")
    @Expose
    private Integer price_type;



    private float netPrice;

    public float getNetPrice() {
        return netPrice;
    }

    @SerializedName("hourly_price")
    @Expose
    private List<HourlyPrice> hourly_price = new ArrayList<HourlyPrice>();

    /**
     *
     * @return
     * The start_time
     */
    public String getStart_time() {
        return start_time;
    }

    /**
     *
     * @param start_time
     * The start_time
     */
    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    /**
     *
     * @return
     * The end_time
     */
    public String getEnd_time() {
        return end_time;
    }

    /**
     *
     * @param end_time
     * The end_time
     */
    public void setEnd_time(String end_time) {
        this.end_time = end_time;
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
     * The rating
     */
    public float getRating() {
        return rating;
    }

    /**
     *
     * @param rating
     * The rating
     */
    public void setRating(Integer rating) {
        this.rating = rating;
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
     * The fixed_price
     */
    public String getFixed_price() {
        return fixed_price;
    }

    /**
     *
     * @param fixed_price
     * The fixed_price
     */
    public void setFixed_price(String fixed_price) {
        this.fixed_price = fixed_price;
    }

    /**
     *
     * @return
     * The handling_supplier
     */
    public Integer getHandling_supplier() {
        return handling_supplier;
    }

    /**
     *
     * @param handling_supplier
     * The handling_supplier
     */
    public void setHandling_supplier(Integer handling_supplier) {
        this.handling_supplier = handling_supplier;
    }

    /**
     *
     * @return
     * The handling_admin
     */
    public Integer getHandling_admin() {
        return handling_admin;
    }

    /**
     *
     * @param handling_admin
     * The handling_admin
     */
    public void setHandling_admin(Integer handling_admin) {
        this.handling_admin = handling_admin;
    }

    /**
     *
     * @return
     * The can_urgent
     */
    public Integer getCan_urgent() {
        return can_urgent;
    }

    /**
     *
     * @param can_urgent
     * The can_urgent
     */
    public void setCan_urgent(Integer can_urgent) {
        this.can_urgent = can_urgent;
    }

    /**
     *
     * @return
     * The category_id
     */
    public Integer getCategory_id() {
        return category_id;
    }

    /**
     *
     * @param category_id
     * The category_id
     */
    public void setCategory_id(Integer category_id) {
        this.category_id = category_id;
    }

    /**
     *
     * @return
     * The sub_category_id
     */
    public Integer getSub_category_id() {
        return sub_category_id;
    }

    /**
     *
     * @param sub_category_id
     * The sub_category_id
     */
    public void setSub_category_id(Integer sub_category_id) {
        this.sub_category_id = sub_category_id;
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
     * The price
     */
    public String getPrice() {
        return price;
    }

    /**
     *
     * @param price
     * The price
     */
    public void setPrice(String price) {
        this.price = price;
    }

    /**
     *
     * @return
     * The offer_name
     */
    public String getOffer_name() {
        return offer_name;
    }

    /**
     *
     * @param offer_name
     * The offer_name
     */
    public void setOffer_name(String offer_name) {
        this.offer_name = offer_name;
    }

    /**
     *
     * @return
     * The display_price
     */
    public String getDisplay_price() {
        return display_price;
    }

    /**
     *
     * @param display_price
     * The display_price
     */
    public void setDisplay_price(String display_price) {
        this.display_price = display_price;
    }

    /**
     *
     * @return
     * The measuring_unit
     */
    public String getMeasuring_unit() {
        return measuring_unit;
    }

    /**
     *
     * @param measuring_unit
     * The measuring_unit
     */
    public void setMeasuring_unit(String measuring_unit) {
        this.measuring_unit = measuring_unit;
    }

    /**
     *
     * @return
     * The product_desc
     */
    public String getProduct_desc() {
        return product_desc;
    }

    /**
     *
     * @param product_desc
     * The product_desc
     */
    public void setProduct_desc(String product_desc) {
        this.product_desc = product_desc;
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
     * The product_image
     */
    public String getProduct_image() {
        return product_image;
    }

    /**
     *
     * @param product_image
     * The product_image
     */
    public void setProduct_image(String product_image) {
        this.product_image = product_image;
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
     * The product_id
     */
    public Integer getProduct_id() {
        return product_id;
    }

    /**
     *
     * @param product_id
     * The product_id
     */
    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
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
     * The delivery_charges
     */
    public Integer getDelivery_charges() {
        return delivery_charges;
    }

    /**
     *
     * @param delivery_charges
     * The delivery_charges
     */
    public void setDelivery_charges(Integer delivery_charges) {
        this.delivery_charges = delivery_charges;
    }

    /**
     *
     * @return
     * The min_order
     */
    public Integer getMin_order() {
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
     * The charges_below_min_order
     */
    public Integer getCharges_below_min_order() {
        return charges_below_min_order;
    }

    /**
     *
     * @param charges_below_min_order
     * The charges_below_min_order
     */
    public void setCharges_below_min_order(Integer charges_below_min_order) {
        this.charges_below_min_order = charges_below_min_order;
    }

    /**
     *
     * @return
     * The supplier_name
     */
    public String getSupplier_name() {
        return supplier_name;
    }

    /**
     *
     * @param supplier_name
     * The supplier_name
     */
    public void setSupplier_name(String supplier_name) {
        this.supplier_name = supplier_name;
    }

    /**
     *
     * @return
     * The price_type
     */
    public Integer getPrice_type() {
        return price_type;
    }

    /**
     *
     * @param price_type
     * The price_type
     */
    public void setPrice_type(Integer price_type) {
        this.price_type = price_type;
    }

    /**
     *
     * @return
     * The hourly_price
     */
    public List<HourlyPrice> getHourly_price() {
        return hourly_price;
    }

    /**
     *
     * @param hourly_price
     * The hourly_price
     */
    public void setHourly_price(List<HourlyPrice> hourly_price) {
        this.hourly_price = hourly_price;
    }

    public void setNetPrice(float netPrice) {
        this.netPrice = netPrice;
    }
}

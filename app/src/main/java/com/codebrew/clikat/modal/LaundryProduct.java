package com.codebrew.clikat.modal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")

public class LaundryProduct implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("supplier_name")
    @Expose
    private String supplier_name;

    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("handling_admin")
    @Expose
    private Float handling;
    @SerializedName("handling_supplier")
    @Expose
    private Float handling_supplier;
    @SerializedName("image_path")
    @Expose
    private String image_path;
    @SerializedName("measuring_unit")
    @Expose
    private String measuring_unit;
    @SerializedName("product_desc")
    @Expose
    private String product_desc;
    @SerializedName("sub_category_id")
    @Expose
    private Integer sub_category_id;
    @SerializedName("bar_code")
    @Expose
    private String bar_code;
    @SerializedName("delivery_charges")
    @Expose
    private Float delivery_charges;
    @SerializedName("min_order")
    @Expose
    private Integer min_order;
    @SerializedName("charges_below_min_order")
    @Expose
    private Float charges_below_min_order;
    @SerializedName("product_id")
    @Expose
    private Integer product_id;
    @SerializedName("supplier_branch_id")
    @Expose
    private Integer supplier_branch_id;

    private Integer quantity = 0;
    private Float netPrice = 0f;

    private String subCategoryName = "";


    @SerializedName("can_urgent")
    @Expose
    private int canUrgent;

    @SerializedName("urgent_value")
    @Expose
    private Float urgent_value;

    @SerializedName("urgent_type")
    @Expose
    int urgentType = 0;

    @SerializedName("price_type")
    @Expose
    int price_type=0;

    public int getUrgentType() {
        return urgentType;
    }

    public void setUrgentType(int urgentType) {
        this.urgentType = urgentType;
    }

    public int getCanUrgent() {
        return canUrgent;
    }

    public void setCanUrgent(int canUrgent) {
        this.canUrgent = canUrgent;
    }

    public Float getUrgent_value() {
        return urgent_value;
    }

    public void setUrgent_value(Float urgent_value) {
        this.urgent_value = urgent_value;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public Float getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(Float netPrice) {
        this.netPrice = netPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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
     * @return The price
     */
    public String  getPrice() {
        return price;
    }

    /**
     * @param price The price
     */
    public void setPrice(String  price) {
        this.price = price;
    }

    /**
     * @return The handling
     */
    public float getHandling() {
        return handling;
    }

    /**
     * @param handling The handling
     */
    public void setHandling(Float handling) {
        this.handling = handling;
    }

    /**
     * @return The handling_supplier
     */
    public float getHandling_supplier() {
        return handling_supplier;
    }

    /**
     * @param handling_supplier The handling_supplier
     */
    public void setHandling_supplier(Float handling_supplier) {
        this.handling_supplier = handling_supplier;
    }

    /**
     * @return The image_path
     */
    public String getImage_path() {
        return image_path;
    }

    /**
     * @param image_path The image_path
     */
    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    /**
     * @return The measuring_unit
     */
    public String getMeasuring_unit() {
        return measuring_unit;
    }

    /**
     * @param measuring_unit The measuring_unit
     */
    public void setMeasuring_unit(String measuring_unit) {
        this.measuring_unit = measuring_unit;
    }

    /**
     * @return The product_desc
     */
    public String getProduct_desc() {
        return product_desc;
    }

    /**
     * @param product_desc The product_desc
     */
    public void setProduct_desc(String product_desc) {
        this.product_desc = product_desc;
    }

    /**
     * @return The sub_category_id
     */
    public Integer getSub_category_id() {
        return sub_category_id;
    }

    /**
     * @param sub_category_id The sub_category_id
     */
    public void setSub_category_id(Integer sub_category_id) {
        this.sub_category_id = sub_category_id;
    }

    /**
     * @return The bar_code
     */
    public String getBar_code() {
        return bar_code;
    }

    /**
     * @param bar_code The bar_code
     */
    public void setBar_code(String bar_code) {
        this.bar_code = bar_code;
    }

    /**
     * @return The delivery_charges
     */
    public float getDelivery_charges() {
        return delivery_charges;
    }

    /**
     * @param delivery_charges The delivery_charges
     */
    public void setDelivery_charges(Float delivery_charges) {
        this.delivery_charges = delivery_charges;
    }

    /**
     * @return The min_order
     */
    public Integer getMin_order() {
        return min_order;
    }

    /**
     * @param min_order The min_order
     */
    public void setMin_order(Integer min_order) {
        this.min_order = min_order;
    }

    /**
     * @return The charges_below_min_order
     */
    public float getCharges_below_min_order() {
        return charges_below_min_order;
    }

    /**
     * @param charges_below_min_order The charges_below_min_order
     */
    public void setCharges_below_min_order(Float charges_below_min_order) {
        this.charges_below_min_order = charges_below_min_order;
    }

    /**
     * @return The product_id
     */
    public Integer getProduct_id() {
        return product_id;
    }

    /**
     * @param product_id The product_id
     */
    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
    }

    /**
     * @return The supplier_branch_id
     */
    public Integer getSupplier_branch_id() {
        return supplier_branch_id;
    }

    /**
     * @param supplier_branch_id The supplier_branch_id
     */
    public void setSupplier_branch_id(Integer supplier_branch_id) {
        this.supplier_branch_id = supplier_branch_id;
    }


    public int getPrice_type() {
        return price_type;
    }

    public void setPrice_type(int price_type) {
        this.price_type = price_type;
    }



    public String getSupplier_name() {
        return supplier_name;
    }

    public void setSupplier_name(String supplier_name) {
        this.supplier_name = supplier_name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
package com.codebrew.clikat.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HistoryProduct {

    @SerializedName("measuring_unit")
    @Expose
    private String measuringUnit;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("image_path")
    @Expose
    private String imagePath;
    @SerializedName("product_desc")
    @Expose
    private String productDesc;


    @SerializedName("handling_admin")
    @Expose
    private Float handling_admin;
    @SerializedName("handling_supplier")
    @Expose
    private Float handling_supplier;
    @SerializedName("fixed_quantity")
    @Expose
    private Integer quantity;

    @SerializedName("actual_price")
    @Expose
    private Float actualPrice;

    @SerializedName("product_id")
    @Expose
    private Integer productId;

    @SerializedName("urgent_value")
    @Expose
    private float urgent_value;

    public float getUrgent_value() {
        return urgent_value;
    }

    public int getUrgent_type() {
        return urgent_type;
    }

    @SerializedName("delivery_charges")
    @Expose
    private float deliveryCharges;

    @SerializedName("urgent_type")
    @Expose
    private int urgent_type;

    @SerializedName("can_urgent")
    @Expose
    private int can_urgent;


    @SerializedName("supplier_id")
    @Expose
    private int supplier_id;

    public int getCan_urgent() {
        return can_urgent;
    }


    public float getDeliveryCharges() {
        return deliveryCharges;
    }

    public int getSupplierBranchId() {
        return supplierBranchId;
    }

    @SerializedName("order")

    @Expose
    private int order;

    @SerializedName("category_id")
    @Expose
    private int category_id;

    public int getCategory_id() {
        return category_id;
    }

    @SerializedName("supplier_branch_id")
    @Expose
    private int supplierBranchId;

    public int getOrder() {
        return order;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Float getHandling_admin() {
        return handling_admin;
    }

    public void setHandling_admin(Float handling_admin) {
        this.handling_admin = handling_admin;
    }

    public Float getHandling_supplier() {
        return handling_supplier;
    }

    public void setHandling_supplier(Float handling_supplier) {
        this.handling_supplier = handling_supplier;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Float getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(Float actualPrice) {
        this.actualPrice = actualPrice;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * @return The measuringUnit
     */
    public String getMeasuringUnit() {
        return measuringUnit;
    }

    /**
     * @param measuringUnit The measuring_unit
     */
    public void setMeasuringUnit(String measuringUnit) {
        this.measuringUnit = measuringUnit;
    }

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The price
     */
    public String getPrice() {
        return price;
    }

    /**
     * @param price The price
     */
    public void setPrice(String price) {
        this.price = price;
    }

    /**
     * @return The productName
     */
    public String getProductName() {
        return productName;
    }

    /**
     * @param productName The product_name
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * @return The imagePath
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * @param imagePath The image_path
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * @return The productDesc
     */
    public String getProductDesc() {
        return productDesc;
    }

    /**
     * @param productDesc The product_desc
     */
    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public int getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(int supplier_id) {
        this.supplier_id = supplier_id;
    }
}
package com.codebrew.clikat.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class ProductLoyalityPoints {

    @SerializedName("image_path")
    @Expose
    private String imagePath;
    @SerializedName("product_id")
    @Expose
    private Integer productId;



    @SerializedName("supplier_name")
    @Expose
    private String supplierName="Prince";



    @SerializedName("product_desc")
    @Expose
    private String productDesc;
    @SerializedName("measuring_unit")
    @Expose
    private String measuringUnit;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("supplier_id")
    @Expose
    private Integer supplierId;
    @SerializedName("loyalty_points")
    @Expose
    private Integer loyaltyPoints;
    @SerializedName("supplier_branch_id")
    @Expose
    private int supplierBranchId;
    private Boolean tick;
    @SerializedName("deliver_charges")
    @Expose
    private float deliveryCharges=0f;
    @SerializedName("handling_charges")
    @Expose
    private float handlingCharges=0f;


    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }


    /**
     *
     * @return
     * The imagePath
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     *
     * @param imagePath
     * The image_path
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     *
     * @return
     * The productId
     */
    public Integer getProductId() {
        return productId;
    }

    /**
     *
     * @param productId
     * The product_id
     */
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    /**
     *
     * @return
     * The productDesc
     */
    public String getProductDesc() {
        return productDesc;
    }

    /**
     *
     * @param productDesc
     * The product_desc
     */
    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    /**
     *
     * @return
     * The measuringUnit
     */
    public String getMeasuringUnit() {
        return measuringUnit;
    }

    /**
     *
     * @param measuringUnit
     * The measuring_unit
     */
    public void setMeasuringUnit(String measuringUnit) {
        this.measuringUnit = measuringUnit;
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
     * The supplierId
     */
    public Integer getSupplierId() {
        return supplierId;
    }

    /**
     *
     * @param supplierId
     * The supplier_id
     */
    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    /**
     *
     * @return
     * The loyaltyPoints
     */
    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    /**
     *
     * @param loyaltyPoints
     * The loyalty_points
     */
    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    /**
     *
     * @return
     * The supplierBranchId
     */
    public int getSupplierBranchId() {
        return supplierBranchId;
    }

    /**
     *
     * @param supplierBranchId
     * The supplier_branch_id
     */
    public void setSupplierBranchId(Integer supplierBranchId) {
        this.supplierBranchId = supplierBranchId;
    }

    public void setTick(Boolean tick) {
        this.tick = tick;
    }

    public Boolean getTick() {
        return tick;
    }

    public float getDeliveryCharges() {
        return deliveryCharges;
    }


    public float getHandlingCharges() {
        return handlingCharges;
    }
}
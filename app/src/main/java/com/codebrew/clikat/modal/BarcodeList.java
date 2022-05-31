package com.codebrew.clikat.modal;

/*
 * Created by cbl80 on 30/5/16.
 */
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class BarcodeList {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("measuring_unit")
    @Expose
    private String measuring_unit;
    @SerializedName("product_desc")
    @Expose
    private String product_desc;
    @SerializedName("image_path")
    @Expose
    private String image_path;
    @SerializedName("supplier_branch_id")
    @Expose
    private Integer supplier_branch_id;
    @SerializedName("product_id")
    @Expose
    private Integer product_id;
    @SerializedName("delivery_charges")
    @Expose
    private float delivery_charges;
    @SerializedName("min_order")
    @Expose
    private float min_order;
    @SerializedName("charges_below_min_order")
    @Expose
    private float charges_below_min_order;

    @SerializedName("sku")
    @Expose
    private String sku;

    public String getSku() {
        return sku;
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
     * The image_path
     */
    public String getImage_path() {
        return image_path;
    }

    /**
     *
     * @param image_path
     * The image_path
     */
    public void setImage_path(String image_path) {
        this.image_path = image_path;
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
     * The delivery_charges
     */
    public float getDelivery_charges() {
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
     * The charges_below_min_order
     */
    public float getCharges_below_min_order() {
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

}
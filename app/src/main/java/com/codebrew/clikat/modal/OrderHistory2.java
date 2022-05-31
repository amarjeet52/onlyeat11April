package com.codebrew.clikat.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class OrderHistory2 {

    @SerializedName("net_amount")
    @Expose
    private Float netAmount;
    @SerializedName("order_id")
    @Expose
    private ArrayList<Integer> orderId;
    @SerializedName("service_date")
    @Expose
    private String serviceDate;
    @SerializedName("delivered_on")
    @Expose
    private String deliveredOn;

    @SerializedName("status")
    @Expose
    private Integer status;

    @SerializedName("near_on")
    @Expose
    private String nearOn;
    @SerializedName("shipped_on")
    @Expose
    private String shippedOn;
    @SerializedName("payment_type")
    @Expose
    private Integer paymentType;
    @SerializedName("created_on")
    @Expose
    private String createdOn;
    @SerializedName("product")
    @Expose
    private List<HistoryProduct> product = new ArrayList<HistoryProduct>();
    @SerializedName("product_count")
    @Expose
    private Integer productCount;
    @SerializedName("user_delivery_address")
    @Expose
    private Integer userDeliveryAddress;
    @SerializedName("delivery_address")
    @Expose
    private Address deliveryAddress;

    @SerializedName("schedule_order")
    @Expose
    private Integer schedlueOrder;
    @SerializedName("supplier_branch_id")
    @Expose
    private Integer supplierbranchid;

    @SerializedName("supplier_id")
    @Expose
    private int supplierId;

    @SerializedName("schedule_date")
    @Expose
    private String scheduleDate="";


    public float promo_discount;
    public String promo_code;
    public Integer apply_promo;


    @SerializedName("categoryId")
    @Expose
    private int categoryId=0;

    public int getCategoryId() {
        return categoryId;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public int getSupplierId() {
        return supplierId;
    }

    @SerializedName("logo")
    @Expose
    private String logo;

    @SerializedName("area_id")
    @Expose
    private int area_id;

    public int getArea_id() {
        return area_id;
    }

    public int getIsFrom() {

        return isFrom;
    }

    public void setIsFrom(int isFrom) {
        this.isFrom = isFrom;
    }

    @SerializedName("isFrom")
    @Expose
    private int isFrom=0;

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getLogo() {
        return logo;
    }

    public Integer getSupplierbranchid() {
        return supplierbranchid;
    }

    public Integer getSupplier_branch_id() {
        return supplierbranchid;
    }

    public void setSupplier_branch_id(Integer supplier_branch_id) {
        this.supplierbranchid = supplier_branch_id;
    }



    public Integer getSchedlueOrder() {
        return schedlueOrder;
    }

    public void setSchedlueOrder(Integer schedlueOrder) {
        this.schedlueOrder = schedlueOrder;
    }




    /**
     * @return The netAmount
     */
    public Float getNetAmount() {
        return netAmount;
    }

    /**
     * @param netAmount The net_amount
     */
    public void setNetAmount(Float netAmount) {
        this.netAmount = netAmount;
    }

    /**
     * @return The orderId
     */
    public ArrayList<Integer> getOrderId() {
        return orderId;
    }

    /**
     * @param orderId The order_id
     */
    public void setOrderId(ArrayList<Integer> orderId) {
        this.orderId = orderId;
    }

    /**
     * @return The serviceDate
     */
    public String getServiceDate() {
        return serviceDate;
    }

    /**
     * @param serviceDate The service_date
     */
    public void setServiceDate(String serviceDate) {
        this.serviceDate = serviceDate;
    }

    /**
     * @return The deliveredOn
     */
    public String getDeliveredOn() {
        return deliveredOn;
    }

    /**
     * @param deliveredOn The delivered_on
     */
    public void setDeliveredOn(String deliveredOn) {
        this.deliveredOn = deliveredOn;
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
     * @return The nearOn
     */
    public String getNearOn() {
        return nearOn;
    }

    /**
     * @param nearOn The near_on
     */
    public void setNearOn(String nearOn) {
        this.nearOn = nearOn;
    }

    /**
     * @return The shippedOn
     */
    public String getShippedOn() {
        return shippedOn;
    }

    /**
     * @param shippedOn The shipped_on
     */
    public void setShippedOn(String shippedOn) {
        this.shippedOn = shippedOn;
    }

    /**
     * @return The paymentType
     */
    public Integer getPaymentType() {
        return paymentType;
    }

    /**
     * @param paymentType The payment_type
     */
    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    /**
     * @return The createdOn
     */
    public String getCreatedOn() {
        return createdOn;
    }

    /**
     * @param createdOn The created_on
     */
    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    /**
     * @return The product
     */
    public List<HistoryProduct> getProduct() {
        return product;
    }

    /**
     * @param product The product
     */
    public void setProduct(List<HistoryProduct> product) {
        this.product = product;
    }

    /**
     * @return The productCount
     */
    public Integer getProductCount() {
        return productCount;
    }

    /**
     * @param productCount The product_count
     */
    public void setProductCount(Integer productCount) {
        this.productCount = productCount;
    }

    /**
     * @return The userDeliveryAddress
     */
    public Integer getUserDeliveryAddress() {
        return userDeliveryAddress;
    }

    /**
     * @param userDeliveryAddress The user_delivery_address
     */
    public void setUserDeliveryAddress(Integer userDeliveryAddress) {
        this.userDeliveryAddress = userDeliveryAddress;
    }

    /**
     * @return The deliveryAddress
     */
    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    /**
     * @param deliveryAddress The delivery_address
     */
    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public void setSupplierbranchid(Integer supplierbranchid) {
        this.supplierbranchid = supplierbranchid;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }
}
package com.codebrew.clikat.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Address {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("address_line_1")
    @Expose
    private String addressLine1;
    @SerializedName("address_line_2")
    @Expose
    private String addressLine2;
    @SerializedName("pincode")
    @Expose
    private String pincode;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("landmark")
    @Expose
    private String landmark;
    @SerializedName("directions_for_delivery")
    @Expose
    private String directionsForDelivery;
    @SerializedName("is_deleted")
    @Expose
    private Integer isDeleted;
    @SerializedName("area_id")
    @Expose
    private Integer areaId;

    @SerializedName("name")
    @Expose
    private String name="";

    @SerializedName("address_link")
    @Expose
    private String addressLink;

    @SerializedName("customer_address")
    @Expose
    private String customerAddress="";

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getAddressLink() {
        return addressLink;
    }

    public void setAddressLink(String addressLink) {
        this.addressLink = addressLink;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {

        return name;
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
     * @return The userId
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * @param userId The user_id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * @return The addressLine1
     */
    public String getAddressLine1() {
        return addressLine1;
    }

    /**
     * @param addressLine1 The address_line_1
     */
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    /**
     * @return The addressLine2
     */
    public String getAddressLine2() {
        return addressLine2;
    }

    /**
     * @param addressLine2 The address_line_2
     */
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    /**
     * @return The pincode
     */
    public String getPincode() {
        return pincode;
    }

    /**
     * @param pincode The pincode
     */
    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    /**
     * @return The city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city The city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return The landmark
     */
    public String getLandmark() {
        return landmark;
    }

    /**
     * @param landmark The landmark
     */
    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    /**
     * @return The directionsForDelivery
     */
    public String getDirectionsForDelivery() {
        return directionsForDelivery;
    }

    /**
     * @param directionsForDelivery The directions_for_delivery
     */
    public void setDirectionsForDelivery(String directionsForDelivery) {
        this.directionsForDelivery = directionsForDelivery;
    }

    /**
     * @return The isDeleted
     */
    public Integer getIsDeleted() {
        return isDeleted;
    }

    /**
     * @param isDeleted The is_deleted
     */
    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * @return The areaId
     */
    public Integer getAreaId() {
        return areaId;
    }

    /**
     * @param areaId The area_id
     */
    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

}
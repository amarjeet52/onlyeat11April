package com.codebrew.clikat.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class DataLoyalityPoints {

    @SerializedName("loyalty_points")
    @Expose
    private Integer loyaltyPoints;
    @SerializedName("product")
    @Expose
    private List<ProductLoyalityPoints> product = new ArrayList<ProductLoyalityPoints>();
    @SerializedName("orders")
    @Expose
    private List<LoyalityOrders> orders = new ArrayList<LoyalityOrders>();

    public List<LoyalityOrders> getOrders() {
        return orders;
    }

    /**
     * @return The loyaltyPoints
     */
    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    /**
     * @param loyaltyPoints The loyalty_points
     */
    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    /**
     * @return The product
     */
    public List<ProductLoyalityPoints> getProduct() {
        return product;
    }

    /**
     * @param product The product
     */
    public void setProduct(List<ProductLoyalityPoints> product) {
        this.product = product;
    }

}
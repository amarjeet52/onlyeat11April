package com.codebrew.clikat.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DataCommon {
    @SerializedName("cartId")
    @Expose
    private Integer cartId;

    @SerializedName("id")
    @Expose
    private Integer id;


    @SerializedName("orderId")
    private int orderId;

    @SerializedName("image")
    @Expose
    private String image;

    public String getImage() {
        return image;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    @SerializedName("favourites")
    @Expose
    private List<Favourite> favourites = new ArrayList<>();

    /**
     *
     * @return
     * The favourites
     */
    public List<Favourite> getFavourites() {
        return favourites;
    }

    /**
     *
     * @param favourites
     * The favourites
     */
    public void setFavourites(List<Favourite> favourites) {
        this.favourites = favourites;
    }

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The cartId
     */
    public Integer getCartId() {
        return cartId;
    }

    /**
     *
     * @param cartId
     * The cartId
     */
    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

}

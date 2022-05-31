package com.codebrew.clikat.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DataOrderHistory {

    @SerializedName("orderHistory")
    @Expose
    private List<OrderHistory2> orderHistory = new ArrayList<OrderHistory2>();

    /**
     * @return The orderHistory
     */
    public List<OrderHistory2> getOrderHistory() {
        return orderHistory;
    }

    /**
     * @param orderHistory The orderHistory
     */
    public void setOrderHistory(List<OrderHistory2> orderHistory) {
        this.orderHistory = orderHistory;
    }


    @SerializedName("orderList")
    @Expose
    private List<OrderHistory2> orderList = new ArrayList<OrderHistory2>();

    /**
     * @return The orderList
     */
    public List<OrderHistory2> getOrderList() {
        return orderList;
    }

    /**
     * @param orderList The orderList
     */
    public void setOrderList(List<OrderHistory2> orderList) {
        this.orderList = orderList;
    }


}
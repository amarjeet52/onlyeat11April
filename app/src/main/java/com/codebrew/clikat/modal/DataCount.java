package com.codebrew.clikat.modal;

/*
 * Created by cbl80 on 8/9/16.
 */

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class DataCount {

    @SerializedName("scheduleOrders")
    @Expose
    private Integer scheduleOrders;
    @SerializedName("pendingOrder")
    @Expose
    private Integer pendingOrder;

    /**
     * @return The scheduleOrders
     */
    public Integer getScheduleOrders() {
        return scheduleOrders;
    }

    /**
     * @param scheduleOrders The scheduleOrders
     */
    public void setScheduleOrders(Integer scheduleOrders) {
        this.scheduleOrders = scheduleOrders;
    }

    /**
     * @return The pendingOrder
     */
    public Integer getPendingOrder() {
        return pendingOrder;
    }

    /**
     * @param pendingOrder The pendingOrder
     */
    public void setPendingOrder(Integer pendingOrder) {
        this.pendingOrder = pendingOrder;
    }

}

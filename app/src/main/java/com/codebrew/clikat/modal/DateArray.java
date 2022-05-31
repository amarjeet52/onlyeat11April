package com.codebrew.clikat.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/*
 * Created by cbl80 on 11/8/16.
 */
public class DateArray {

    @SerializedName("pickupDate")
    @Expose
    private String pickUPDate = "";

    @SerializedName("pickupTime")
    @Expose
    private String pickupTime;

    public String getDelivery_time() {
        return delivery_time;
    }

    public void setDelivery_time(String delivery_time) {
        this.delivery_time = delivery_time;
    }

    @SerializedName("delivery_time")
    @Expose
    private String delivery_time;

    public String getPickUPDate() {
        return pickUPDate;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    @SerializedName("deliveryDate")
    @Expose
    private String deliveryDate = "";



    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }



    public void setPickUPDate(String pickUPDate) {
        this.pickUPDate = pickUPDate;
    }
}

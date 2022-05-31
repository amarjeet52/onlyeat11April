package com.codebrew.clikat.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by cbl45 on 7/5/16.
 */
public class SchedulerSent {




    @SerializedName("orderId")
    @Expose
    ArrayList<Integer> orderId;

    @SerializedName("orderDates")
    @Expose
    private List<DateArray> monthlyArr = new ArrayList<>();

    @SerializedName("accessToken")
    @Expose
    String accessToken;


    /**
     *
     * @return
     * The monthlyArr
     */
    public List<DateArray> getMonthlyArr() {
        return monthlyArr;
    }

    /**
     *
     * @param monthlyArr
     * The monthly_arr
     */
    public void setMonthlyArr(List<DateArray> monthlyArr) {
        this.monthlyArr = monthlyArr;
    }


    public ArrayList<Integer> getOrderId() {
        return orderId;
    }

    public void setOrderId(ArrayList<Integer> orderId) {
        this.orderId = orderId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}

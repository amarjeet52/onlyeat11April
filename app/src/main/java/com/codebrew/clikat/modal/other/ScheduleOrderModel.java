package com.codebrew.clikat.modal.other;

import java.util.List;

public class ScheduleOrderModel {

    /**
     * status : 200
     * message : Success
     * data : [329]
     */

    private int status;
    private String message;
    private List<Integer> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Integer> getData() {
        return data;
    }

    public void setData(List<Integer> data) {
        this.data = data;
    }
}

package com.codebrew.clikat.modal.agent;

import java.util.List;

public class AgentSlotsModel {


    /**
     * statusCode : 200
     * success : 1
     * message : Success
     * data : ["17:38:00","18:08:00","18:38:00","19:08:00","19:38:00","20:08:00","20:38:00"]
     */

    private int statusCode;
    private int status;
    private int success;
    private String message;
    private List<String> data;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

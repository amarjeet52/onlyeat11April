package com.codebrew.clikat.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExampleOrderHistory {

@SerializedName("status")
@Expose
private Integer status;
@SerializedName("message")
@Expose
private String message;
@SerializedName("data")
@Expose
private DataOrderHistory data;

/**
* 
* @return
* The status
*/
public Integer getStatus() {
return status;
}

/**
* 
* @param status
* The status
*/
public void setStatus(Integer status) {
this.status = status;
}

/**
* 
* @return
* The message
*/
public String getMessage() {
return message;
}

/**
* 
* @param message
* The message
*/
public void setMessage(String message) {
this.message = message;
}

/**
* 
* @return
* The data
*/
public DataOrderHistory getData() {
return data;
}

/**
* 
* @param data
* The data
*/
public void setData(DataOrderHistory data) {
this.data = data;
}

}
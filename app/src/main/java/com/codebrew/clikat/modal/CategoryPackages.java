package com.codebrew.clikat.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class CategoryPackages {

@SerializedName("category_name")
@Expose
private String categoryName;
@SerializedName("category_id")
@Expose
private Integer categoryId;

/**
* 
* @return
* The categoryName
*/
public String getCategoryName() {
return categoryName;
}

/**
* 
* @param categoryName
* The category_name
*/
public void setCategoryName(String categoryName) {
this.categoryName = categoryName;
}

/**
* 
* @return
* The categoryId
*/
public Integer getCategoryId() {
return categoryId;
}

/**
* 
* @param categoryId
* The category_id
*/
public void setCategoryId(Integer categoryId) {
this.categoryId = categoryId;
}

}
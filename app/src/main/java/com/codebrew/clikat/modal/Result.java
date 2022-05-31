package com.codebrew.clikat.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

/*
 * Created by cbl80 on 11/5/16.
 */

@Generated("org.jsonschema2pojo")
public class Result {

    @SerializedName("can_urgent")
    @Expose
    public Integer can_urgent;
    @SerializedName("urgent_value")
    @Expose
    public float urgent_value;
    @SerializedName("supplier_branch_id")
    @Expose
    public Integer supplier_branch_id;
    @SerializedName("pricing_type")
    @Expose
    public Integer pricing_type;
    @SerializedName("delivery_charges")
    @Expose
    public float delivery_charges;
    @SerializedName("handling_supplier")
    @Expose
    public float handling_supplier;
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("bar_code")
    @Expose
    public String bar_code;
    @SerializedName("sku")
    @Expose
    public String sku;
    @SerializedName("image_path")
    @Expose
    public String image_path;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("price")
    @Expose
    public String price;
    @SerializedName("product_desc")
    @Expose
    public String product_desc;
    @SerializedName("hourly_price")
    @Expose
    public List<HourlyPrice> hourly_price = new ArrayList<HourlyPrice>();
    @SerializedName("fixed_price")
    @Expose
    public Float fixed_price;
    @SerializedName("price_type")
    @Expose
    public Integer price_type;

    public int quantity = 0;
    public float netPrice = 0f;
    @SerializedName("handling_admin")
    @Expose
    public float handling_admin = 0;
    @SerializedName("urgent_type")
    @Expose
    public int urgent_type=0;
    @SerializedName("measuring_unit")
    @Expose
    public String measuring_unit="";

    @SerializedName("supplier_name")
    @Expose
    public String supplier_name="";

}

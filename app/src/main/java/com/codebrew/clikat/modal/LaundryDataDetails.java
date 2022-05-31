package com.codebrew.clikat.modal;

/*
 * Created by cbl80 on 1/6/16.
 */
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class LaundryDataDetails  {

    @SerializedName("sub_category_id")
    @Expose
    private Integer sub_category_id;
    @SerializedName("sub_category_image")
    @Expose
    private String sub_category_image;
    @SerializedName("sub_category_icon")
    @Expose
    private String sub_category_icon;
    @SerializedName("name")
    @Expose
    private String sub_category_name;
    @SerializedName("sub_category_description")
    @Expose
    private String sub_category_description;
    @SerializedName("product")
    @Expose
    private List<LaundryProduct> product = new ArrayList<LaundryProduct>();

    /**
     *
     * @return
     * The sub_category_id
     */
    public Integer getSub_category_id() {
        return sub_category_id;
    }

    /**
     *
     * @param sub_category_id
     * The sub_category_id
     */
    public void setSub_category_id(Integer sub_category_id) {
        this.sub_category_id = sub_category_id;
    }

    /**
     *
     * @return
     * The sub_category_image
     */
    public String getSub_category_image() {
        return sub_category_image;
    }

    /**
     *
     * @param sub_category_image
     * The sub_category_image
     */
    public void setSub_category_image(String sub_category_image) {
        this.sub_category_image = sub_category_image;
    }

    /**
     *
     * @return
     * The sub_category_icon
     */
    public String getSub_category_icon() {
        return sub_category_icon;
    }

    /**
     *
     * @param sub_category_icon
     * The sub_category_icon
     */
    public void setSub_category_icon(String sub_category_icon) {
        this.sub_category_icon = sub_category_icon;
    }

    /**
     *
     * @return
     * The sub_category_name
     */
    public String getSub_category_name() {
        return sub_category_name;
    }

    /**
     *
     * @param sub_category_name
     * The sub_category_name
     */
    public void setSub_category_name(String sub_category_name) {
        this.sub_category_name = sub_category_name;
    }

    /**
     *
     * @return
     * The sub_category_description
     */
    public String getSub_category_description() {
        return sub_category_description;
    }

    /**
     *
     * @param sub_category_description
     * The sub_category_description
     */
    public void setSub_category_description(String sub_category_description) {
        this.sub_category_description = sub_category_description;
    }

    /**
     *
     * @return
     * The product
     */
    public List<LaundryProduct> getProduct() {
        return product;
    }

    /**
     *
     * @param product
     * The product
     */
    public void setProduct(List<LaundryProduct> product) {
        this.product = product;
    }

}
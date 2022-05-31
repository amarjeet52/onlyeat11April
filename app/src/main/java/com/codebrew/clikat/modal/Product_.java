package com.codebrew.clikat.modal;

/*
 * Created by cbl80 on 2/9/16.
 */
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Product_ {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("product_id")
    @Expose
    private Integer product_id;
    @SerializedName("image_path")
    @Expose
    private String image_path;
    @SerializedName("product_desc")
    @Expose
    private String product_desc;

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The product_id
     */
    public Integer getProduct_id() {
        return product_id;
    }

    /**
     *
     * @param product_id
     * The product_id
     */
    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
    }

    /**
     *
     * @return
     * The image_path
     */
    public String getImage_path() {
        return image_path;
    }

    /**
     *
     * @param image_path
     * The image_path
     */
    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    /**
     *
     * @return
     * The product_desc
     */
    public String getProduct_desc() {
        return product_desc;
    }

    /**
     *
     * @param product_desc
     * The product_desc
     */
    public void setProduct_desc(String product_desc) {
        this.product_desc = product_desc;
    }

}

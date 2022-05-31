package com.codebrew.clikat.modal;

/*
 * Created by cbl80 on 2/9/16.
 */
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class LoyalityOrders {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("supplier_branch_id")
    @Expose
    private Integer supplier_branch_id;
    @SerializedName("delivery_address_id")
    @Expose
    private Integer delivery_address_id;
    @SerializedName("created_on")
    @Expose
    private String created_on;
    @SerializedName("status")
    @Expose
    private double status;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("rating")
    @Expose
    private Integer rating;
    @SerializedName("near_on")
    @Expose
    private String near_on;
    @SerializedName("shipped_on")
    @Expose
    private String shipped_on;
    @SerializedName("total_points")
    @Expose
    private Integer total_points;
    @SerializedName("order_id")
    @Expose
    private Integer order_id;
    @SerializedName("service_date")
    @Expose
    private String service_date;
    @SerializedName("remarks")
    @Expose
    private String remarks;
    @SerializedName("delivered_on")
    @Expose
    private String delivered_on;
    @SerializedName("product_name")
    @Expose
    private String product_name;
    @SerializedName("product_desc")
    @Expose
    private String product_desc;
    @SerializedName("image_path")
    @Expose
    private String image_path;
    @SerializedName("product")
    @Expose
    private List<Product_> product = new ArrayList<Product_>();

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The logo
     */
    public String getLogo() {
        return logo;
    }

    /**
     *
     * @param logo
     * The logo
     */
    public void setLogo(String logo) {
        this.logo = logo;
    }

    /**
     *
     * @return
     * The supplier_branch_id
     */
    public Integer getSupplier_branch_id() {
        return supplier_branch_id;
    }

    /**
     *
     * @param supplier_branch_id
     * The supplier_branch_id
     */
    public void setSupplier_branch_id(Integer supplier_branch_id) {
        this.supplier_branch_id = supplier_branch_id;
    }

    /**
     *
     * @return
     * The delivery_address_id
     */
    public Integer getDelivery_address_id() {
        return delivery_address_id;
    }

    /**
     *
     * @param delivery_address_id
     * The delivery_address_id
     */
    public void setDelivery_address_id(Integer delivery_address_id) {
        this.delivery_address_id = delivery_address_id;
    }

    /**
     *
     * @return
     * The created_on
     */
    public String getCreated_on() {
        return created_on;
    }

    /**
     *
     * @param created_on
     * The created_on
     */
    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    /**
     *
     * @return
     * The status
     */
    public double getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(double status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The comment
     */
    public String getComment() {
        return comment;
    }

    /**
     *
     * @param comment
     * The comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     *
     * @return
     * The rating
     */
    public Integer getRating() {
        return rating;
    }

    /**
     *
     * @param rating
     * The rating
     */
    public void setRating(Integer rating) {
        this.rating = rating;
    }

    /**
     *
     * @return
     * The near_on
     */
    public String getNear_on() {
        return near_on;
    }

    /**
     *
     * @param near_on
     * The near_on
     */
    public void setNear_on(String near_on) {
        this.near_on = near_on;
    }

    /**
     *
     * @return
     * The shipped_on
     */
    public String getShipped_on() {
        return shipped_on;
    }

    /**
     *
     * @param shipped_on
     * The shipped_on
     */
    public void setShipped_on(String shipped_on) {
        this.shipped_on = shipped_on;
    }

    /**
     *
     * @return
     * The total_points
     */
    public Integer getTotal_points() {
        return total_points;
    }

    /**
     *
     * @param total_points
     * The total_points
     */
    public void setTotal_points(Integer total_points) {
        this.total_points = total_points;
    }

    /**
     *
     * @return
     * The order_id
     */
    public Integer getOrder_id() {
        return order_id;
    }

    /**
     *
     * @param order_id
     * The order_id
     */
    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    /**
     *
     * @return
     * The service_date
     */
    public String getService_date() {
        return service_date;
    }

    /**
     *
     * @param service_date
     * The service_date
     */
    public void setService_date(String service_date) {
        this.service_date = service_date;
    }

    /**
     *
     * @return
     * The remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     *
     * @param remarks
     * The remarks
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     *
     * @return
     * The delivered_on
     */
    public String getDelivered_on() {
        return delivered_on;
    }

    /**
     *
     * @param delivered_on
     * The delivered_on
     */
    public void setDelivered_on(String delivered_on) {
        this.delivered_on = delivered_on;
    }

    /**
     *
     * @return
     * The product_name
     */
    public String getProduct_name() {
        return product_name;
    }

    /**
     *
     * @param product_name
     * The product_name
     */
    public void setProduct_name(String product_name) {
        this.product_name = product_name;
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
     * The product
     */
    public List<Product_> getProduct() {
        return product;
    }

    /**
     *
     * @param product
     * The product
     */
    public void setProduct(List<Product_> product) {
        this.product = product;
    }

}

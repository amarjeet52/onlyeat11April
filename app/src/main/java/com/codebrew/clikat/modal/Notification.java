package com.codebrew.clikat.modal;

/*
 * Created by cbl80 on 8/6/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Notification {

    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("notification_status")
    @Expose
    private Integer notification_status;
    @SerializedName("is_read")
    @Expose
    private Integer is_read;
    @SerializedName("order_id")
    @Expose
    private Integer order_id;
    @SerializedName("notification_message")
    @Expose
    private String notification_message;
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("notification_type")
    @Expose
    private String notificationType;



    @SerializedName("created_on")
    @Expose
    private String created_on;

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
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
     * The notification_status
     */
    public Integer getNotification_status() {
        return notification_status;
    }

    /**
     *
     * @param notification_status
     * The notification_status
     */
    public void setNotification_status(Integer notification_status) {
        this.notification_status = notification_status;
    }

    /**
     *
     * @return
     * The is_read
     */
    public Integer getIs_read() {
        return is_read;
    }

    /**
     *
     * @param is_read
     * The is_read
     */
    public void setIs_read(Integer is_read) {
        this.is_read = is_read;
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
     * The notification_message
     */
    public String getNotification_message() {
        return notification_message;
    }

    /**
     *
     * @param notification_message
     * The notification_message
     */
    public void setNotification_message(String notification_message) {
        this.notification_message = notification_message;
    }

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

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }
}



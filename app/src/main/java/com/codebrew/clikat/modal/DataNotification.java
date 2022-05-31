package com.codebrew.clikat.modal;

/*
 * Created by cbl80 on 8/6/16.
 */
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class DataNotification {

    @SerializedName("notification")
    @Expose
    private ArrayList<Notification> notification = new ArrayList<Notification>();

    /**
     *
     * @return
     * The notification
     */
    public ArrayList<Notification> getNotification() {
        return notification;
    }

    /**
     *
     * @param notification
     * The notification
     */
    public void setNotification(ArrayList<Notification> notification) {
        this.notification = notification;
    }

}

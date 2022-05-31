package com.codebrew.clikat.modal.other;

public class SupplierServiceModel {

    public SupplierServiceModel(Integer image_id, String service_name, String service_value) {
        this.image_id = image_id;
        this.service_name = service_name;
        this.service_value = service_value;
    }

    private Integer image_id;
    private String service_name;
    private String service_value;

    public Integer getImage_id() {
        return image_id;
    }

    public void setImage_id(Integer image_id) {
        this.image_id = image_id;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getService_value() {
        return service_value;
    }

    public void setService_value(String service_value) {
        this.service_value = service_value;
    }
}

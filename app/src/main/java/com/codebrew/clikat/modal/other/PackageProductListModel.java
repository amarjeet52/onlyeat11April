package com.codebrew.clikat.modal.other;

import java.util.List;

public class PackageProductListModel {

    /**
     * status : 200
     * message : Success
     * data : {"list":[{"supplier_branch_id":10,"image_path":"http://45.232.252.46:8081/clikat-buckettest/winter-workshop-7-1EIGkP4.jpg","price":123,"fixed_price":123,"name":"test1","measuring_unit":"qw","product_desc":"test1","bar_code":"0","id":234,"package_id":234,"product_id":184,"handling":0,"handling_supplier":0,"delivery_charges":0,"price_type":0}]}
     */

    private int status;
    private String message;
    private DataBean data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<ListBean> list;

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * supplier_branch_id : 10
             * image_path : http://45.232.252.46:8081/clikat-buckettest/winter-workshop-7-1EIGkP4.jpg
             * price : 123
             * fixed_price : 123
             * name : test1
             * measuring_unit : qw
             * product_desc : test1
             * bar_code : 0
             * id : 234
             * package_id : 234
             * product_id : 184
             * handling : 0
             * handling_supplier : 0
             * delivery_charges : 0
             * price_type : 0
             */

            private int supplier_branch_id;
            private String image_path;
            private int price;
            private int fixed_price;
            private String name;
            private String measuring_unit;
            private String product_desc;
            private String bar_code;
            private int id;
            private int package_id;
            private int product_id;
            private int handling;
            private int handling_supplier;
            private int delivery_charges;
            private int price_type;
            private int quantity;
            private int handlingAdmin=0;
            private float netPrice=0;

            public int getSupplier_branch_id() {
                return supplier_branch_id;
            }

            public void setSupplier_branch_id(int supplier_branch_id) {
                this.supplier_branch_id = supplier_branch_id;
            }

            public String getImage_path() {
                return image_path;
            }

            public void setImage_path(String image_path) {
                this.image_path = image_path;
            }

            public int getPrice() {
                return price;
            }

            public void setPrice(int price) {
                this.price = price;
            }

            public int getFixed_price() {
                return fixed_price;
            }

            public void setFixed_price(int fixed_price) {
                this.fixed_price = fixed_price;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getMeasuring_unit() {
                return measuring_unit;
            }

            public void setMeasuring_unit(String measuring_unit) {
                this.measuring_unit = measuring_unit;
            }

            public String getProduct_desc() {
                return product_desc;
            }

            public void setProduct_desc(String product_desc) {
                this.product_desc = product_desc;
            }

            public String getBar_code() {
                return bar_code;
            }

            public void setBar_code(String bar_code) {
                this.bar_code = bar_code;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getPackage_id() {
                return package_id;
            }

            public void setPackage_id(int package_id) {
                this.package_id = package_id;
            }

            public int getProduct_id() {
                return product_id;
            }

            public void setProduct_id(int product_id) {
                this.product_id = product_id;
            }

            public int getHandling() {
                return handling;
            }

            public void setHandling(int handling) {
                this.handling = handling;
            }

            public int getHandling_supplier() {
                return handling_supplier;
            }

            public void setHandling_supplier(int handling_supplier) {
                this.handling_supplier = handling_supplier;
            }

            public int getDelivery_charges() {
                return delivery_charges;
            }

            public void setDelivery_charges(int delivery_charges) {
                this.delivery_charges = delivery_charges;
            }

            public int getPrice_type() {
                return price_type;
            }

            public void setPrice_type(int price_type) {
                this.price_type = price_type;
            }

            public int getQuantity() {
                return quantity;
            }

            public void setQuantity(int quantity) {
                this.quantity = quantity;
            }

            public int getHandlingAdmin() {
                return handlingAdmin;
            }

            public void setHandlingAdmin(int handlingAdmin) {
                this.handlingAdmin = handlingAdmin;
            }

            public float getNetPrice() {
                return netPrice;
            }

            public void setNetPrice(float netPrice) {
                this.netPrice = netPrice;
            }
        }
    }
}

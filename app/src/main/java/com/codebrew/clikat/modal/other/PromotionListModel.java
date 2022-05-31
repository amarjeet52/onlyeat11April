package com.codebrew.clikat.modal.other;

import java.util.List;

public class PromotionListModel {


    /**
     * status : 200
     * message : Success
     * data : {"list":[{"ic_splash":"http://45.232.252.46:8081/clikat-buckettest/mi1hJFp4C.png","offer_product_value":184,"promotion_price":"160","category_name":"Mobiles, Computer","supplier_name":"Mi","supplier_id":11,"category_id":1084,"supplier_branch_id":10,"promotion_description":"test2","promotion_name":"test2","id":8,"promotion_image":"http://45.232.252.46:8081/clikat-buckettest/winter-workshop-7-1PHxVtD.jpg","promotion_type":0,"min_order":0,"delivery_charges":0,"handling_admin":1,"handling_supplier":1},{"ic_splash":"http://45.232.252.46:8081/clikat-buckettest/mi1hJFp4C.png","offer_product_value":184,"promotion_price":"160","category_name":"Mobiles, Computer","supplier_name":"Mi","supplier_id":11,"category_id":1084,"supplier_branch_id":10,"promotion_description":"test3","promotion_name":"test3","id":9,"promotion_image":"http://45.232.252.46:8081/clikat-buckettest/west-iceland-2Z8ptjP.jpg","promotion_type":1,"min_order":0,"delivery_charges":0,"handling_admin":1,"handling_supplier":1}]}
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
             * ic_splash : http://45.232.252.46:8081/clikat-buckettest/mi1hJFp4C.png
             * offer_product_value : 184
             * promotion_price : 160
             * category_name : Mobiles, Computer
             * supplier_name : Mi
             * supplier_id : 11
             * category_id : 1084
             * supplier_branch_id : 10
             * promotion_description : test2
             * promotion_name : test2
             * id : 8
             * promotion_image : http://45.232.252.46:8081/clikat-buckettest/winter-workshop-7-1PHxVtD.jpg
             * promotion_type : 0
             * min_order : 0
             * delivery_charges : 0
             * handling_admin : 1
             * handling_supplier : 1
             */

            private String logo;
            private int offer_product_value;
            private String promotion_price;
            private String category_name;
            private String supplier_name;
            private int supplier_id;
            private int category_id;
            private int supplier_branch_id;
            private String promotion_description;
            private String promotion_name;
            private int id;
            private String promotion_image;
            private int promotion_type;
            private int min_order;
            private int delivery_charges;
            private int handling_admin;
            private int handling_supplier;

            public String getLogo() {
                return logo;
            }

            public void setLogo(String logo) {
                this.logo = logo;
            }

            public int getOffer_product_value() {
                return offer_product_value;
            }

            public void setOffer_product_value(int offer_product_value) {
                this.offer_product_value = offer_product_value;
            }

            public String getPromotion_price() {
                return promotion_price;
            }

            public void setPromotion_price(String promotion_price) {
                this.promotion_price = promotion_price;
            }

            public String getCategory_name() {
                return category_name;
            }

            public void setCategory_name(String category_name) {
                this.category_name = category_name;
            }

            public String getSupplier_name() {
                return supplier_name;
            }

            public void setSupplier_name(String supplier_name) {
                this.supplier_name = supplier_name;
            }

            public int getSupplier_id() {
                return supplier_id;
            }

            public void setSupplier_id(int supplier_id) {
                this.supplier_id = supplier_id;
            }

            public int getCategory_id() {
                return category_id;
            }

            public void setCategory_id(int category_id) {
                this.category_id = category_id;
            }

            public int getSupplier_branch_id() {
                return supplier_branch_id;
            }

            public void setSupplier_branch_id(int supplier_branch_id) {
                this.supplier_branch_id = supplier_branch_id;
            }

            public String getPromotion_description() {
                return promotion_description;
            }

            public void setPromotion_description(String promotion_description) {
                this.promotion_description = promotion_description;
            }

            public String getPromotion_name() {
                return promotion_name;
            }

            public void setPromotion_name(String promotion_name) {
                this.promotion_name = promotion_name;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getPromotion_image() {
                return promotion_image;
            }

            public void setPromotion_image(String promotion_image) {
                this.promotion_image = promotion_image;
            }

            public int getPromotion_type() {
                return promotion_type;
            }

            public void setPromotion_type(int promotion_type) {
                this.promotion_type = promotion_type;
            }

            public int getMin_order() {
                return min_order;
            }

            public void setMin_order(int min_order) {
                this.min_order = min_order;
            }

            public int getDelivery_charges() {
                return delivery_charges;
            }

            public void setDelivery_charges(int delivery_charges) {
                this.delivery_charges = delivery_charges;
            }

            public int getHandling_admin() {
                return handling_admin;
            }

            public void setHandling_admin(int handling_admin) {
                this.handling_admin = handling_admin;
            }

            public int getHandling_supplier() {
                return handling_supplier;
            }

            public void setHandling_supplier(int handling_supplier) {
                this.handling_supplier = handling_supplier;
            }
        }
    }
}

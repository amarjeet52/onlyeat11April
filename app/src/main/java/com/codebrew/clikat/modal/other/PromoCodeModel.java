package com.codebrew.clikat.modal.other;

import java.util.List;

public class PromoCodeModel {

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


        private int discountType;
        private float discountPrice;
        private String promoCode;
        private int id;
        private int minOrder;
        private List<Integer> categoryIds;
        private List<Integer> supplierIds;
        private float max_discount_value;

        public int getDiscountType() {
            return discountType;
        }

        public void setDiscountType(int discountType) {
            this.discountType = discountType;
        }

        public float getDiscountPrice() {
            return discountPrice;
        }

        public void setDiscountPrice(float discountPrice) {
            this.discountPrice = discountPrice;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public List<Integer> getCategoryIds() {
            return categoryIds;
        }

        public void setCategoryIds(List<Integer> categoryIds) {
            this.categoryIds = categoryIds;
        }

        public List<Integer> getSupplierIds() {
            return supplierIds;
        }

        public void setSupplierIds(List<Integer> supplierIds) {
            this.supplierIds = supplierIds;
        }


        public String getPromoCode() {
            return promoCode;
        }

        public void setPromoCode(String promoCode) {
            this.promoCode = promoCode;
        }

        public int getMinOrder() {
            return minOrder;
        }

        public void setMinOrder(int minOrder) {
            this.minOrder = minOrder;
        }

        public float getMax_discount_value() {
            return max_discount_value;
        }

        public void setMax_discount_value(float max_discount_value) {
            this.max_discount_value = max_discount_value;
        }
    }
}

package com.codebrew.clikat.modal.other;

import java.util.List;

public class ScheduleListModel {




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
        private List<OrderHistoryBean> orderHistory;

        public List<OrderHistoryBean> getOrderHistory() {
            return orderHistory;
        }

        public void setOrderHistory(List<OrderHistoryBean> orderHistory) {
            this.orderHistory = orderHistory;
        }

        public static class OrderHistoryBean {

            private float tip_agent;
            private float net_amount;
            private float delivery_charges;
            private float referral_amount;
            private float discountAmount;
            private float handling_admin;
            private float handling_supplier;
            private int order_id;
            private int supplier_branch_id;
            private int supplier_id;
            private String logo;
            private String service_date;
            private double status;
            private String near_on;
            private String shipped_on;
            private int schedule_order;
            private int payment_type;
            private int self_pickup;
            private String created_on;
            private int product_count;
            private String terminology;
            private String delivered_on;
            private int user_delivery_address;
            private DeliveryAddressBean delivery_address;
            private List<ProductDataBean> product;
            private int Area_id;
            private String delivery_min_time;
            private String delivery_max_time;

            public String getDelivery_min_time() {
                return delivery_min_time;
            }

            public void setDelivery_min_time(String delivery_min_time) {
                this.delivery_min_time = delivery_min_time;
            }

            public String getDelivery_max_time() {
                return delivery_max_time;
            }

            public void setDelivery_max_time(String delivery_max_time) {
                this.delivery_max_time = delivery_max_time;
            }

            public float getTip_agent() {
                return tip_agent;
            }

            public void setTip_agent(float tip_agent) {
                this.tip_agent = tip_agent;
            }

            public float getNet_amount() {
                return net_amount;
            }

            public void setNet_amount(float net_amount) {
                this.net_amount = net_amount;
            }

            public int getOrder_id() {
                return order_id;
            }

            public void setOrder_id(int order_id) {
                this.order_id = order_id;
            }

            public int getSupplier_branch_id() {
                return supplier_branch_id;
            }

            public void setSupplier_branch_id(int supplier_branch_id) {
                this.supplier_branch_id = supplier_branch_id;
            }

            public int getSupplier_id() {
                return supplier_id;
            }

            public void setSupplier_id(int supplier_id) {
                this.supplier_id = supplier_id;
            }

            public String getLogo() {
                return logo;
            }

            public void setLogo(String logo) {
                this.logo = logo;
            }

            public String getService_date() {
                return service_date;
            }

            public void setService_date(String service_date) {
                this.service_date = service_date;
            }

            public double getStatus() {
                return status;
            }

            public void setStatus(double status) {
                this.status = status;
            }

            public String getNear_on() {
                return near_on;
            }

            public void setNear_on(String near_on) {
                this.near_on = near_on;
            }

            public String getShipped_on() {
                return shipped_on;
            }

            public void setShipped_on(String shipped_on) {
                this.shipped_on = shipped_on;
            }

            public int getSchedule_order() {
                return schedule_order;
            }

            public void setSchedule_order(int schedule_order) {
                this.schedule_order = schedule_order;
            }

            public int getPayment_type() {
                return payment_type;
            }

            public void setPayment_type(int payment_type) {
                this.payment_type = payment_type;
            }

            public String getCreated_on() {
                return created_on;
            }

            public void setCreated_on(String created_on) {
                this.created_on = created_on;
            }

            public int getProduct_count() {
                return product_count;
            }

            public void setProduct_count(int product_count) {
                this.product_count = product_count;
            }

            public int getUser_delivery_address() {
                return user_delivery_address;
            }

            public void setUser_delivery_address(int user_delivery_address) {
                this.user_delivery_address = user_delivery_address;
            }

            public DeliveryAddressBean getDelivery_address() {
                return delivery_address;
            }

            public void setDelivery_address(DeliveryAddressBean delivery_address) {
                this.delivery_address = delivery_address;
            }

            public List<ProductDataBean> getProduct() {
                return product;
            }

            public void setProduct(List<ProductDataBean> product) {
                this.product = product;
            }

            public String getDeliveredOn() {
                return delivered_on;
            }

            public void setDeliveredOn(String deliveredOn) {
                delivered_on = deliveredOn;
            }

            public int getArea_id() {
                return Area_id;
            }

            public void setArea_id(int area_id) {
                Area_id = area_id;
            }

            public float getDelivery_charges() {
                return delivery_charges;
            }

            public void setDelivery_charges(float delivery_charges) {
                this.delivery_charges = delivery_charges;
            }

            public float getHandling_admin() {
                return handling_admin;
            }

            public void setHandling_admin(float handling_admin) {
                this.handling_admin = handling_admin;
            }

            public float getHandling_supplier() {
                return handling_supplier;
            }

            public void setHandling_supplier(float handling_supplier) {
                this.handling_supplier = handling_supplier;
            }

            public float getDiscountAmount() {
                return discountAmount;
            }

            public void setDiscountAmount(float discountAmount) {
                this.discountAmount = discountAmount;
            }

            public float getReferral_amount() {
                return referral_amount;
            }

            public void setReferral_amount(float referral_amount) {
                this.referral_amount = referral_amount;
            }

            public int getSelf_pickup() {
                return self_pickup;
            }

            public void setSelf_pickup(int self_pickup) {
                this.self_pickup = self_pickup;
            }

            public static class DeliveryAddressBean {
                /**
                 * address_line_1 : Al Barsha
                 * address_line_2 : Dubai,@#United Arab Emirates
                 * pincode : hfghgfhgfhf,@#hgfhfghf
                 * city :
                 * landmark : hfhfghfg
                 */

                private String address_line_1;
                private String address_line_2;
                private String pincode;
                private String city;
                private String landmark;

                public String getAddress_line_1() {
                    return address_line_1;
                }

                public void setAddress_line_1(String address_line_1) {
                    this.address_line_1 = address_line_1;
                }

                public String getAddress_line_2() {
                    return address_line_2;
                }

                public void setAddress_line_2(String address_line_2) {
                    this.address_line_2 = address_line_2;
                }

                public String getPincode() {
                    return pincode;
                }

                public void setPincode(String pincode) {
                    this.pincode = pincode;
                }

                public String getCity() {
                    return city;
                }

                public void setCity(String city) {
                    this.city = city;
                }

                public String getLandmark() {
                    return landmark;
                }

                public void setLandmark(String landmark) {
                    this.landmark = landmark;
                }
            }

            public String getTerminology() {
                return terminology;
            }

            public void setTerminology(String terminology) {
                this.terminology = terminology;
            }

            public String getDelivered_on() {
                return delivered_on;
            }

            public void setDelivered_on(String delivered_on) {
                this.delivered_on = delivered_on;
            }
        }
    }
}

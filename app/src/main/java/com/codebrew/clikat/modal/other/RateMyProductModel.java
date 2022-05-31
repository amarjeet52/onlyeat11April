package com.codebrew.clikat.modal.other;

import java.util.ArrayList;
import java.util.List;

public class RateMyProductModel {


    /**
     * status : 200
     * message : Success
     * data : {"orderList":[{"net_amount":102,"order_id":294,"supplier_branch_id":2,"supplier_id":16,"ic_splash":"http://45.232.252.46:8082/clikat-buckettest/metroaNrdb6.jpg","service_date":"2019-03-29T12:49:04+00:00","delivered_on":"2019-03-29T10:22:39+00:00","status":5,"near_on":"2019-03-29T10:22:39+00:00","shipped_on":"2019-03-29T10:22:14+00:00","payment_type":0,"created_on":"2019-03-29T14:20:14+00:00","product":[{"category_id":4,"supplier_id":16,"phone":"NaN","supplier_image":null,"email":"support@metro.com","can_urgent":1,"urgent_type":0,"urgent_value":2,"order":0,"category_flow":"Category>SubCategory>Pl","supplier_branch_id":2,"handling_admin":1,"handling_supplier":1,"fixed_quantity":1,"name":"Adidas Originals Men's Stan Smith Sneakers","measuring_unit":"1","product_id":31,"price":"100","fixed_price":"100","product_name":"Adidas Originals Men's Stan Smith Sneakers","image_path":"http://45.232.252.46:8082/clikat-buckettest/1zq5ajV.jpg","product_desc":"<div>Material Type: Leather<\/div><div>Lifestyle: C","delivery_charges":0}],"product_count":1,"user_delivery_address":25,"schedule_order":0,"delivery_address":{"address_line_1":"Al Barsha","address_line_2":"Dubai,@#United Arab Emirates","pincode":"ljl,@#oj","city":"","landmark":"ass"}}]}
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
        private List<OrderListBean> orderList;

        public List<OrderListBean> getOrderList() {
            return orderList;
        }

        public void setOrderList(List<OrderListBean> orderList) {
            this.orderList = orderList;
        }

        public static class OrderListBean {
            /**
             * net_amount : 102
             * order_id : 294
             * supplier_branch_id : 2
             * supplier_id : 16
             * ic_splash : http://45.232.252.46:8082/clikat-buckettest/metroaNrdb6.jpg
             * service_date : 2019-03-29T12:49:04+00:00
             * delivered_on : 2019-03-29T10:22:39+00:00
             * status : 5
             * near_on : 2019-03-29T10:22:39+00:00
             * shipped_on : 2019-03-29T10:22:14+00:00
             * payment_type : 0
             * created_on : 2019-03-29T14:20:14+00:00
             * product : [{"category_id":4,"supplier_id":16,"phone":"NaN","supplier_image":null,"email":"support@metro.com","can_urgent":1,"urgent_type":0,"urgent_value":2,"order":0,"category_flow":"Category>SubCategory>Pl","supplier_branch_id":2,"handling_admin":1,"handling_supplier":1,"fixed_quantity":1,"name":"Adidas Originals Men's Stan Smith Sneakers","measuring_unit":"1","product_id":31,"price":"100","fixed_price":"100","product_name":"Adidas Originals Men's Stan Smith Sneakers","image_path":"http://45.232.252.46:8082/clikat-buckettest/1zq5ajV.jpg","product_desc":"<div>Material Type: Leather<\/div><div>Lifestyle: C","delivery_charges":0}]
             * product_count : 1
             * user_delivery_address : 25
             * schedule_order : 0
             * delivery_address : {"address_line_1":"Al Barsha","address_line_2":"Dubai,@#United Arab Emirates","pincode":"ljl,@#oj","city":"","landmark":"ass"}
             */

            private float net_amount;
            private float delivery_charges;
            private float handling_admin;
            private float handling_supplier;
            private int order_id;
            private int supplier_branch_id;
            private int supplier_id;
            private String logo;
            private String service_date;
            private String delivered_on;
            private double status;
            private String near_on;
            private String shipped_on;
            private int payment_type;
            private String created_on;
            private int product_count;
            private int user_delivery_address;
            private int schedule_order;
            private String DeliveredOn;
            private DeliveryAddressBean delivery_address;
            private ArrayList<ProductBean> product;

            public float getNet_amount() {
                return net_amount;
            }

            public void setNet_amount(float net_amount) {
                this.net_amount = net_amount;
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

            public String getDelivered_on() {
                return delivered_on;
            }

            public void setDelivered_on(String delivered_on) {
                this.delivered_on = delivered_on;
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

            public int getSchedule_order() {
                return schedule_order;
            }

            public void setSchedule_order(int schedule_order) {
                this.schedule_order = schedule_order;
            }

            public DeliveryAddressBean getDelivery_address() {
                return delivery_address;
            }

            public void setDelivery_address(DeliveryAddressBean delivery_address) {
                this.delivery_address = delivery_address;
            }

            public ArrayList<ProductBean> getProduct() {
                return product;
            }

            public void setProduct(ArrayList<ProductBean> product) {
                this.product = product;
            }

            public String getDeliveredOn() {
                return DeliveredOn;
            }

            public void setDeliveredOn(String deliveredOn) {
                DeliveredOn = deliveredOn;
            }

            public static class DeliveryAddressBean {
                /**
                 * address_line_1 : Al Barsha
                 * address_line_2 : Dubai,@#United Arab Emirates
                 * pincode : ljl,@#oj
                 * city :
                 * landmark : ass
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

            public static class ProductBean {
                /**
                 * category_id : 4
                 * supplier_id : 16
                 * phone : NaN
                 * supplier_image : null
                 * email : support@metro.com
                 * can_urgent : 1
                 * urgent_type : 0
                 * urgent_value : 2
                 * order : 0
                 * category_flow : Category>SubCategory>Pl
                 * supplier_branch_id : 2
                 * handling_admin : 1
                 * handling_supplier : 1
                 * fixed_quantity : 1
                 * name : Adidas Originals Men's Stan Smith Sneakers
                 * measuring_unit : 1
                 * product_id : 31
                 * price : 100
                 * fixed_price : 100
                 * product_name : Adidas Originals Men's Stan Smith Sneakers
                 * image_path : http://45.232.252.46:8082/clikat-buckettest/1zq5ajV.jpg
                 * product_desc : <div>Material Type: Leather</div><div>Lifestyle: C
                 * delivery_charges : 0
                 */

                private int category_id;
                private int supplier_id;
                private String phone;
                private Object supplier_image;
                private String email;
                private int can_urgent;
                private int urgent_type;
                private int urgent_value;
                private int order;
                private String category_flow;
                private int supplier_branch_id;
                private int handling_admin;
                private int handling_supplier;
                private int quantity;
                private String name;
                private String measuring_unit;
                private int product_id;
                private String price;
                private String fixed_price;
                private String product_name;
                private String image_path;
                private String product_desc;
                private int delivery_charges;

                public int getCategory_id() {
                    return category_id;
                }

                public void setCategory_id(int category_id) {
                    this.category_id = category_id;
                }

                public int getSupplier_id() {
                    return supplier_id;
                }

                public void setSupplier_id(int supplier_id) {
                    this.supplier_id = supplier_id;
                }

                public String getPhone() {
                    return phone;
                }

                public void setPhone(String phone) {
                    this.phone = phone;
                }

                public Object getSupplier_image() {
                    return supplier_image;
                }

                public void setSupplier_image(Object supplier_image) {
                    this.supplier_image = supplier_image;
                }

                public String getEmail() {
                    return email;
                }

                public void setEmail(String email) {
                    this.email = email;
                }

                public int getCan_urgent() {
                    return can_urgent;
                }

                public void setCan_urgent(int can_urgent) {
                    this.can_urgent = can_urgent;
                }

                public int getUrgent_type() {
                    return urgent_type;
                }

                public void setUrgent_type(int urgent_type) {
                    this.urgent_type = urgent_type;
                }

                public int getUrgent_value() {
                    return urgent_value;
                }

                public void setUrgent_value(int urgent_value) {
                    this.urgent_value = urgent_value;
                }

                public int getOrder() {
                    return order;
                }

                public void setOrder(int order) {
                    this.order = order;
                }

                public String getCategory_flow() {
                    return category_flow;
                }

                public void setCategory_flow(String category_flow) {
                    this.category_flow = category_flow;
                }

                public int getSupplier_branch_id() {
                    return supplier_branch_id;
                }

                public void setSupplier_branch_id(int supplier_branch_id) {
                    this.supplier_branch_id = supplier_branch_id;
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

                public int getQuantity() {
                    return quantity;
                }

                public void setQuantity(int quantity) {
                    this.quantity = quantity;
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

                public int getProduct_id() {
                    return product_id;
                }

                public void setProduct_id(int product_id) {
                    this.product_id = product_id;
                }

                public String getPrice() {
                    return price;
                }

                public void setPrice(String price) {
                    this.price = price;
                }

                public String getFixed_price() {
                    return fixed_price;
                }

                public void setFixed_price(String fixed_price) {
                    this.fixed_price = fixed_price;
                }

                public String getProduct_name() {
                    return product_name;
                }

                public void setProduct_name(String product_name) {
                    this.product_name = product_name;
                }

                public String getImage_path() {
                    return image_path;
                }

                public void setImage_path(String image_path) {
                    this.image_path = image_path;
                }

                public String getProduct_desc() {
                    return product_desc;
                }

                public void setProduct_desc(String product_desc) {
                    this.product_desc = product_desc;
                }

                public int getDelivery_charges() {
                    return delivery_charges;
                }

                public void setDelivery_charges(int delivery_charges) {
                    this.delivery_charges = delivery_charges;
                }
            }
        }
    }
}

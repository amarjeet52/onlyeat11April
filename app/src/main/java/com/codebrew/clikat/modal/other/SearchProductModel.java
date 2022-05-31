package com.codebrew.clikat.modal.other;


import java.util.List;

public class SearchProductModel {

    /**
     * status : 200
     * message : Success
     * data : {"details":{"products":[{"supplier_id":1,"supplier_name":"apurva","supplier_logo":"http://45.232.252.46:8081/clikat-buckettest/winter-workshop-9v8qbMs.jpg","branchId":1,"id":18,"display_price":"10","name":"New Chai","sku":"1","product_desc":"ssss","measuring_unit":"gram","image_path":"http://192.168.102.52:8081/clikat-buckettest/download(3)GmJA5e.jpg","price":"9.8","price_type":1},{"supplier_id":2,"supplier_name":"Mac","supplier_logo":"http://45.232.252.46:8081/clikat-buckettest/ic_backwhite@3xOGRmut.png","branchId":2,"id":15,"display_price":"10","name":"new bread","sku":"12","product_desc":"sdfsdfsd","measuring_unit":"gran","image_path":"http://192.168.102.52:8081/clikat-buckettest/Fresh-VegetablesX2bdat.jpg","price":"10","price_type":0}],"count":2}}
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
        /**
         * details : {"products":[{"supplier_id":1,"supplier_name":"apurva","supplier_logo":"http://45.232.252.46:8081/clikat-buckettest/winter-workshop-9v8qbMs.jpg","branchId":1,"id":18,"display_price":"10","name":"New Chai","sku":"1","product_desc":"ssss","measuring_unit":"gram","image_path":"http://192.168.102.52:8081/clikat-buckettest/download(3)GmJA5e.jpg","price":"9.8","price_type":1},{"supplier_id":2,"supplier_name":"Mac","supplier_logo":"http://45.232.252.46:8081/clikat-buckettest/ic_backwhite@3xOGRmut.png","branchId":2,"id":15,"display_price":"10","name":"new bread","sku":"12","product_desc":"sdfsdfsd","measuring_unit":"gran","image_path":"http://192.168.102.52:8081/clikat-buckettest/Fresh-VegetablesX2bdat.jpg","price":"10","price_type":0}],"count":2}
         */

        private DetailsBean details;

        public DetailsBean getDetails() {
            return details;
        }

        public void setDetails(DetailsBean details) {
            this.details = details;
        }

        public static class DetailsBean {
            /**
             * products : [{"supplier_id":1,"supplier_name":"apurva","supplier_logo":"http://45.232.252.46:8081/clikat-buckettest/winter-workshop-9v8qbMs.jpg","branchId":1,"id":18,"display_price":"10","name":"New Chai","sku":"1","product_desc":"ssss","measuring_unit":"gram","image_path":"http://192.168.102.52:8081/clikat-buckettest/download(3)GmJA5e.jpg","price":"9.8","price_type":1},{"supplier_id":2,"supplier_name":"Mac","supplier_logo":"http://45.232.252.46:8081/clikat-buckettest/ic_backwhite@3xOGRmut.png","branchId":2,"id":15,"display_price":"10","name":"new bread","sku":"12","product_desc":"sdfsdfsd","measuring_unit":"gran","image_path":"http://192.168.102.52:8081/clikat-buckettest/Fresh-VegetablesX2bdat.jpg","price":"10","price_type":0}]
             * count : 2
             */

            private int count;
            private List<ProductDataBean> products;


            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }

            public List<ProductDataBean> getProducts() {
                return products;
            }

            public void setProducts(List<ProductDataBean> products) {
                this.products = products;
            }


        }
    }
}

package com.codebrew.clikat.modal.other;

import java.util.Comparator;
import java.util.List;

public class FilterVarientCatModel {


    /**
     * status : 200
     * message : Success
     * data : [{"variant_name":"Size","id":2,"variant_values":[{"value":"S","id":14},{"value":"M","id":15},{"value":"L","id":16},{"value":"XL","id":17},{"value":"XXL","id":18}]},{"variant_name":"Color","id":3,"variant_values":[{"value":"#000000","id":19},{"value":"#ff4a00","id":20},{"value":"#0098ff","id":21},{"value":"#00d9ff","id":22},{"value":"#ffd332","id":23},{"value":"#872213","id":24},{"value":"#29395d","id":25},{"value":"#0a6202","id":26},{"value":"#6b1669","id":27},{"value":"#ffffff","id":28},{"value":"#5f6399","id":29},{"value":"#470210","id":54}]}]
     * brands : [{"id":23,"description":null,"image":"http://45.232.252.46:8082/clikat-buckettest/s-l300andkPZ.jpg","name":"Tommy Hilfiger"},{"id":31,"description":null,"image":"http://45.232.252.46:8082/clikat-buckettest/Gas19hGec.jpg","name":"GAS"},{"id":32,"description":null,"image":"http://45.232.252.46:8082/clikat-buckettest/download(2)LMNhNm.png","name":"Calvin Klein"},{"id":33,"description":null,"image":"http://45.232.252.46:8082/clikat-buckettest/download(3)ZnlqeJ.png","name":"Levis"},{"id":30,"description":null,"image":"http://45.232.252.46:8082/clikat-buckettest/gapIGBczr.jpg","name":"GAP"},{"id":29,"description":null,"image":"http://45.232.252.46:8082/clikat-buckettest/indian-terrain-org-logo-1618wQ2Nry.png","name":"Indian Terrain"},{"id":28,"description":null,"image":"http://45.232.252.46:8082/clikat-buckettest/d21b1271e5e6073b7db3db56317da2f5LJwARl.jpg","name":"Arrow Sport"},{"id":27,"description":null,"image":"http://45.232.252.46:8082/clikat-buckettest/louis-philippe-awarded-comfort-vote-quality-labelRwVP7Y.jpg","name":"Louis Philippe"},{"id":24,"description":null,"image":"http://45.232.252.46:8082/clikat-buckettest/download(1)Xe2i9A.png","name":"Lacoste"},{"id":25,"description":null,"image":"http://45.232.252.46:8082/clikat-buckettest/download(8)pyU1pb.jpeg","name":"Van Heusen"},{"id":26,"description":null,"image":"http://45.232.252.46:8082/clikat-buckettest/blackbarrys-1-63853Ly8U.jpg","name":"Blackberrys"},{"id":22,"description":null,"image":"http://45.232.252.46:8082/clikat-buckettest/220px-USPoloAssn_logoLp0sPY.jpg","name":"U.S. Polo Assn."},{"id":21,"description":null,"image":"http://45.232.252.46:8082/clikat-buckettest/download4VtrPo.png","name":"United Colors of Benetton"},{"id":20,"description":null,"image":"http://45.232.252.46:8082/clikat-buckettest/images(6)MNrdd1.jpeg","name":"Arrow New York"},{"id":19,"description":null,"image":"http://45.232.252.46:8082/clikat-buckettest/download(7)mc6IFR.jpeg","name":"Free Authority"},{"id":34,"description":null,"image":"http://45.232.252.46:8082/clikat-buckettest/download(9)06YCFw.jpeg","name":"WROGN"}]
     */

    private int status;
    private String message;
    private List<DataBean> data;
    private List<BrandsBean> brands;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public List<BrandsBean> getBrands() {
        return brands;
    }

    public void setBrands(List<BrandsBean> brands) {
        this.brands = brands;
    }

    public static class DataBean {
        /**
         * variant_name : Size
         * id : 2
         * variant_values : [{"value":"S","id":14},{"value":"M","id":15},{"value":"L","id":16},{"value":"XL","id":17},{"value":"XXL","id":18}]
         */

        private String variant_name;
        private int id;
        private int variant_type;
        private List<VariantValuesBean> variant_values;
        private List<BrandsBean> brands_values;

        public String getVariant_name() {
            return variant_name;
        }

        public void setVariant_name(String variant_name) {
            this.variant_name = variant_name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public List<VariantValuesBean> getVariant_values() {
            return variant_values;
        }

        public void setVariant_values(List<VariantValuesBean> variant_values) {
            this.variant_values = variant_values;
        }

        public List<BrandsBean> getBrands_values() {
            return brands_values;
        }

        public void setBrands_values(List<BrandsBean> brands_values) {
            this.brands_values = brands_values;
        }

        public int getVariant_type() {
            return variant_type;
        }

        public void setVariant_type(int variant_type) {
            this.variant_type = variant_type;
        }

        public static class VariantValuesBean {
            /**
             * value : S
             * id : 14
             */

            private String value;
            private int id;
            private int variant_type;
            private boolean status;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public boolean isStatus() {
                return status;
            }

            public void setStatus(boolean status) {
                this.status = status;
            }


            public int getVariant_type() {
                return variant_type;
            }

            public void setVariant_type(int variant_type) {
                this.variant_type = variant_type;
            }
        }
    }

    public static class BrandsBean implements Comparator<BrandsBean> {
        /**
         * id : 23
         * description : null
         * image : http://45.232.252.46:8082/clikat-buckettest/s-l300andkPZ.jpg
         * name : Tommy Hilfiger
         */

        private int id;
        private Object description;
        private String image;
        private String name;
        private boolean status;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Object getDescription() {
            return description;
        }

        public void setDescription(Object description) {
            this.description = description;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        @Override
        public int compare(BrandsBean o1, BrandsBean o2) {
            return o1.getName().toLowerCase().compareToIgnoreCase(o2.getName().toLowerCase());
        }
    }
}

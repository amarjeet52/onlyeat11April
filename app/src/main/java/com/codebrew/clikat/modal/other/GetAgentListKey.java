package com.codebrew.clikat.modal.other;

import java.util.List;

public class GetAgentListKey {

    /**
     * status : 200
     * message : Success
     * data : [{"key":"api_key","value":"964UTvzJKCblds1J&&^Saas&x8gIbTXcEEJSUilGqpxCcmnx"},{"key":"db_secret_key","value":"a0e767bdeb37fd7a02b9cbc6d6b142c6"}]
     */

    private int status;
    private String message;
    private List<DataBean> data;

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

    public static class DataBean {
        /**
         * key : api_key
         * value : 964UTvzJKCblds1J&&^Saas&x8gIbTXcEEJSUilGqpxCcmnx
         */

        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

package com.codebrew.clikat.modal.other;

import java.util.List;

public class CheckPromoCodeParam {


    private String promoCode;
    private String accessToken;
    private String totalBill;
    private String langId;
    private List<String> supplierId;
    private List<String> categoryId;
 private String self_pickup;

    public CheckPromoCodeParam(String promoCode, String accessToken, String totalBill, String langId, List<String> supplierId, List<String> categoryId,String self_pick) {
        this.promoCode = promoCode;
        this.accessToken = accessToken;
        this.totalBill = totalBill;
        this.langId = langId;
        this.supplierId = supplierId;
        this.categoryId = categoryId;
        this.self_pickup=self_pick;
    }

    public String getSelf_pickup() {
        return self_pickup;
    }

    public void setSelf_pickup(String self_pickup) {
        this.self_pickup = self_pickup;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTotalBill() {
        return totalBill;
    }

    public void setTotalBill(String totalBill) {
        this.totalBill = totalBill;
    }

    public String getLangId() {
        return langId;
    }

    public void setLangId(String langId) {
        this.langId = langId;
    }

    public List<String> getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(List<String> supplierId) {
        this.supplierId = supplierId;
    }

    public List<String> getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(List<String> categoryId) {
        this.categoryId = categoryId;
    }
}

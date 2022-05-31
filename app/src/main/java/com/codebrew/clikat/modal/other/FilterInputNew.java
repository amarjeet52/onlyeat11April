package com.codebrew.clikat.modal.other;

import java.util.ArrayList;
import java.util.List;

public class FilterInputNew {

    private String languageId;
    private List<Integer> subCategoryId =new ArrayList<>();
    private String low_to_high;
    private int is_popularity;
    private String is_availability;
    private String is_discount;
    private String product_name;
    private String max_price_range;
    private String min_price_range;
    private String latitude;
    private String longitude;
    private String booking_from_date;
    private String  booking_to_date;
    private int need_agent;
    private String zone_offset;
    private List<Integer> variant_ids =new ArrayList<>();
    private List<String> supplier_ids=new ArrayList<>();
    private List<Integer> brand_ids=new ArrayList<>();

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public List<Integer> getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(List<Integer> subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getLow_to_high() {
        return low_to_high;
    }

    public void setLow_to_high(String low_to_high) {
        this.low_to_high = low_to_high;
    }

    public int getIs_popularity() {
        return is_popularity;
    }

    public void setIs_popularity(int is_popularity) {
        this.is_popularity = is_popularity;
    }

    public String getIs_availability() {
        return is_availability;
    }

    public void setIs_availability(String is_availability) {
        this.is_availability = is_availability;
    }

    public String getIs_discount() {
        return is_discount;
    }

    public void setIs_discount(String is_discount) {
        this.is_discount = is_discount;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getMax_price_range() {
        return max_price_range;
    }

    public void setMax_price_range(String max_price_range) {
        this.max_price_range = max_price_range;
    }

    public String getMin_price_range() {
        return min_price_range;
    }

    public void setMin_price_range(String min_price_range) {
        this.min_price_range = min_price_range;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public List<Integer> getVariant_ids() {
        return variant_ids;
    }

    public void setVariant_ids(List<Integer> variant_ids) {
        this.variant_ids = variant_ids;
    }

    public List<String> getSupplier_ids() {
        return supplier_ids;
    }

    public void setSupplier_ids(List<String> supplier_ids) {
        this.supplier_ids = supplier_ids;
    }

    public List<Integer> getBrand_ids() {
        return brand_ids;
    }

    public void setBrand_ids(List<Integer> brand_ids) {
        this.brand_ids = brand_ids;
    }

    public String getBooking_from_date() {
        return booking_from_date;
    }

    public void setBooking_from_date(String booking_from_date) {
        this.booking_from_date = booking_from_date;
    }

    public String getBooking_to_date() {
        return booking_to_date;
    }

    public void setBooking_to_date(String booking_to_date) {
        this.booking_to_date = booking_to_date;
    }

    public int getNeed_agent() {
        return need_agent;
    }

    public void setNeed_agent(int need_agent) {
        this.need_agent = need_agent;
    }

    public String getZone_offset() {
        return zone_offset;
    }

    public void setZone_offset(String zone_offset) {
        this.zone_offset = zone_offset;
    }
}

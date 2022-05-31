package com.codebrew.clikat.modal.other;

import android.os.Build;

import com.codebrew.clikat.data.model.api.QuestionList;

import java.util.ArrayList;
import java.util.List;

public class PlaceOrderInput {

    private String promoCode;
    private int promoId;
    private float discountAmount;
    private String accessToken;
    private String offset;
    private String cartId;
    private int paymentType;
    private int languageId;
    private int isPackage;
    private String date_time;
    private int duration;
    private List<Integer> agentIds = new ArrayList<>();
    private String from_address;
    private String to_address;
    private String booking_from_date;
    private String booking_to_date;
    private Double from_latitude;
    private Double to_latitude;
    private Double from_longitude;
    private Double to_longitude;
    private int self_pickup;
    private String order_day;
    private String order_time;
    private String gateway_unique_id;
    private String payment_token;
    private String pres_image1;
    private String pres_image2;
    private String pres_image3;
    private String pres_image4;
    private String pres_image5;
    private Integer use_refferal;
    private String pres_description;
    private Float tip_agent;
    private Double user_service_charge;
    private List<QuestionList> questions;
    private Float addOn;
    private int type;
    private String customer_payment_id;
    private String card_id;
    private int payment_after_confirmation;
    private int donate_to_someone;
    private Integer is_schedule;
    private String schedule_date;

    public String getSchedule_type() {
        return schedule_type;
    }

    public void setSchedule_type(String schedule_type) {
        this.schedule_type = schedule_type;
    }

    public String getIs_dine_in_with_food() {
        return is_dine_in_with_food;
    }

    public void setIs_dine_in_with_food(String is_dine_in_with_food) {
        this.is_dine_in_with_food = is_dine_in_with_food;
    }

    public String getIs_dine_in() {
        return is_dine_in;
    }

    public void setIs_dine_in(String is_dine_in) {
        this.is_dine_in = is_dine_in;
    }

    public String getTable_id() {
        return table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public String getIs_dine_in_only() {
        return is_dine_in_only;
    }

    public void setIs_dine_in_only(String is_dine_in_only) {
        this.is_dine_in_only = is_dine_in_only;
    }

    public int getOrder_source() {
        return order_source;
    }

    public void setOrder_source(int order_source) {
        this.order_source = order_source;
    }

    public String getAndroid_version() {
        return android_version;
    }

    public void setAndroid_version(String android_version) {
        this.android_version = android_version;
    }

    private String schedule_type;
    private String schedule_end_date;
    private String currency;
    private String service_provider;
    private String click_at_no;
    private String ip_address;

    //tidycoop

    public int have_pet;
    public int cleaner_in;
    public String parking_instructions;
    public String area_to_focus;
    private String mobile_no;
    private String slot_price;
    private String use_loyality_point;
    private String user_car_id;
    public String is_dine_in_with_food;
    public String is_dine_in;
    public String table_id;
    public String is_dine_in_only;
    public int order_source = 0;
    public String android_version = Build.VERSION.RELEASE;
    public String authnet_payment_profile_id;
    public String authnet_profile_id;
    private String cvt;
    private String cp;
    private String expMonth;
    private String expYear;
    private String drop_off_date;
    private Integer order_delivery_type;
    private String vehicle_number;
    private String is_cutlery_required;
    private String cvc;
    private String cardHolderName;
    private String delivery_company_id;

    private String from_signature_plate;
    private String from_signature_menu;
    private String signature_menu_id;
    private String signature_plate_id ;

    public String getFrom_signature_plate() {
        return from_signature_plate;
    }

    public void setFrom_signature_plate(String from_signature_plate) {
        this.from_signature_plate = from_signature_plate;
    }

    public String getFrom_signature_menu() {
        return from_signature_menu;
    }

    public void setFrom_signature_menu(String from_signature_menu) {
        this.from_signature_menu = from_signature_menu;
    }

    public String getSignature_menu_id() {
        return signature_menu_id;
    }

    public void setSignature_menu_id(String signature_menu_id) {
        this.signature_menu_id = signature_menu_id;
    }

    public String getSignature_plate_id() {
        return signature_plate_id;
    }

    public void setSignature_plate_id(String signature_plate_id) {
        this.signature_plate_id = signature_plate_id;
    }


    public String getDelivery_company_id() {
        return delivery_company_id;
    }

    public void setDelivery_company_id(String delivery_company_id) {
        this.delivery_company_id = delivery_company_id;
    }


    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }



    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }


    public String getUser_car_id() {
        return user_car_id;
    }

    public void setUser_car_id(String user_car_id) {
        this.user_car_id = user_car_id;
    }



    public String getIs_cutlery_required() {
        return is_cutlery_required;
    }

    public void setIs_cutlery_required(String is_cutlery_required) {
        this.is_cutlery_required = is_cutlery_required;
    }


    public String getVehicle_number() {
        return vehicle_number;
    }

    public void setVehicle_number(String vehicle_number) {
        this.vehicle_number = vehicle_number;
    }



    public String getAuthnet_profile_id() {
        return authnet_profile_id;
    }

    public void setAuthnet_profile_id(String authnet_profile_id) {
        this.authnet_profile_id = authnet_profile_id;
    }


    public String getAuthnet_payment_profile_id() {
        return authnet_payment_profile_id;
    }

    public void setAuthnet_payment_profile_id(String authnet_payment_profile_id) {
        this.authnet_payment_profile_id = authnet_payment_profile_id;
    }


    public String getHave_coin_change() {
        return have_coin_change;
    }

    public void setHave_coin_change(String have_coin_change) {
        this.have_coin_change = have_coin_change;
    }

    private String have_coin_change;

    public String getUse_loyality_point() {
        return use_loyality_point;
    }

    public void setUse_loyality_point(String use_loyality_point) {
        this.use_loyality_point = use_loyality_point;
    }


    public String getSlot_id() {
        return slot_id;
    }

    public void setSlot_id(String slot_id) {
        this.slot_id = slot_id;
    }

    private String slot_id;

    public String getSlot_price() {
        return slot_price;
    }

    public void setSlot_price(String slot_price) {
        this.slot_price = slot_price;
    }


    public Double getWallet_discount_amount() {
        return wallet_discount_amount;
    }

    public void setWallet_discount_amount(Double wallet_discount_amount) {
        this.wallet_discount_amount = wallet_discount_amount;
    }

    private Double wallet_discount_amount;


    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }


    public Float getTip_agent() {
        return tip_agent;
    }

    public void setTip_agent(Float tip_agent) {
        this.tip_agent = tip_agent;
    }


    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public int getPromoId() {
        return promoId;
    }

    public void setPromoId(int promoId) {
        this.promoId = promoId;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public float getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(float discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public int getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public int getIsPackage() {
        return isPackage;
    }

    public void setIsPackage(int isPackage) {
        this.isPackage = isPackage;
    }

    public List<Integer> getAgentIds() {
        return agentIds;
    }

    public void setAgentIds(List<Integer> agentIds) {
        this.agentIds = agentIds;
    }

    public String getBooking_date_time() {
        return date_time;
    }

    public void setBooking_date_time(String booking_date_time) {
        this.date_time = booking_date_time;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }


    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getFrom_address() {
        return from_address;
    }

    public void setFrom_address(String from_address) {
        this.from_address = from_address;
    }

    public String getTo_address() {
        return to_address;
    }

    public void setTo_address(String to_address) {
        this.to_address = to_address;
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

    public Double getFrom_latitude() {
        return from_latitude;
    }

    public void setFrom_latitude(Double from_latitude) {
        this.from_latitude = from_latitude;
    }

    public Double getTo_latitude() {
        return to_latitude;
    }

    public void setTo_latitude(Double to_latitude) {
        this.to_latitude = to_latitude;
    }

    public Double getFrom_longitude() {
        return from_longitude;
    }

    public void setFrom_longitude(Double from_longitude) {
        this.from_longitude = from_longitude;
    }

    public Double getTo_longitude() {
        return to_longitude;
    }

    public void setTo_longitude(Double to_longitude) {
        this.to_longitude = to_longitude;
    }

    public int getSelf_pickup() {
        return self_pickup;
    }

    public void setSelf_pickup(int self_pickup) {
        this.self_pickup = self_pickup;
    }

    public String getOrder_day() {
        return order_day;
    }

    public void setOrder_day(String order_day) {
        this.order_day = order_day;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }


    public String getGateway_unique_id() {
        return gateway_unique_id;
    }

    public void setGateway_unique_id(String gateway_unique_id) {
        this.gateway_unique_id = gateway_unique_id;
    }

    public String getPayment_token() {
        return payment_token;
    }

    public void setPayment_token(String payment_token) {
        this.payment_token = payment_token;
    }

    public Integer getUse_refferal() {
        return use_refferal;
    }

    public void setUse_refferal(Integer use_refferal) {
        this.use_refferal = use_refferal;
    }

    public String getPres_image1() {
        return pres_image1;
    }

    public void setPres_image1(String pres_image1) {
        this.pres_image1 = pres_image1;
    }

    public String getPres_image2() {
        return pres_image2;
    }

    public void setPres_image2(String pres_image2) {
        this.pres_image2 = pres_image2;
    }

    public String getPres_image3() {
        return pres_image3;
    }

    public void setPres_image3(String pres_image3) {
        this.pres_image3 = pres_image3;
    }

    public String getPres_image4() {
        return pres_image4;
    }

    public void setPres_image4(String pres_image4) {
        this.pres_image4 = pres_image4;
    }

    public String getPres_image5() {
        return pres_image5;
    }

    public void setPres_image5(String pres_image5) {
        this.pres_image5 = pres_image5;
    }

    public String getPres_description() {
        return pres_description;
    }

    public void setPres_description(String pres_description) {
        this.pres_description = pres_description;
    }

    public Double getUser_service_charge() {
        return user_service_charge;
    }

    public void setUser_service_charge(Double user_service_charge) {
        this.user_service_charge = user_service_charge;
    }

    public int getHave_pet() {
        return have_pet;
    }

    public void setHave_pet(int have_pet) {
        this.have_pet = have_pet;
    }

    public int getCleaner_in() {
        return cleaner_in;
    }

    public void setCleaner_in(int cleaner_in) {
        this.cleaner_in = cleaner_in;
    }

    public String getParking_instructions() {
        return parking_instructions;
    }

    public void setParking_instructions(String parking_instructions) {
        this.parking_instructions = parking_instructions;
    }

    public String getArea_to_focus() {
        return area_to_focus;
    }

    public void setArea_to_focus(String area_to_focus) {
        this.area_to_focus = area_to_focus;
    }

    public List<QuestionList> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionList> questions) {
        this.questions = questions;
    }

    public Float getAddOn() {
        return addOn;
    }

    public void setAddOn(Float addOn) {
        this.addOn = addOn;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCustomer_payment_id() {
        return customer_payment_id;
    }

    public void setCustomer_payment_id(String customer_payment_id) {
        this.customer_payment_id = customer_payment_id;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public int getPayment_after_confirmation() {
        return payment_after_confirmation;
    }

    public void setPayment_after_confirmation(int payment_after_confirmation) {
        this.payment_after_confirmation = payment_after_confirmation;
    }

    public int getDonate_to_someone() {
        return donate_to_someone;
    }

    public void setDonate_to_someone(int donate_to_someone) {
        this.donate_to_someone = donate_to_someone;
    }

    public Integer getIs_schedule() {
        return is_schedule;
    }

    public void setIs_schedule(Integer is_schedule) {
        this.is_schedule = is_schedule;
    }

    public String getSchedule_date() {
        return schedule_date;
    }

    public void setSchedule_date(String schedule_date) {
        this.schedule_date = schedule_date;
    }

    public String getSchedule_end_date() {
        return schedule_end_date;
    }

    public void setSchedule_end_date(String schedule_end_date) {
        this.schedule_end_date = schedule_end_date;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getService_provider() {
        return service_provider;
    }

    public void setService_provider(String service_provider) {
        this.service_provider = service_provider;
    }

    public String getClick_at_no() {
        return click_at_no;
    }

    public void setClick_at_no(String click_at_no) {
        this.click_at_no = click_at_no;
    }


    public String getCvt() {
        return cvt;
    }

    public void setCvt(String cvt) {
        this.cvt = cvt;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(String expMonth) {
        this.expMonth = expMonth;
    }

    public String getExpYear() {
        return expYear;
    }

    public void setExpYear(String expYear) {
        this.expYear = expYear;
    }

    public String getDrop_off_date() {
        return drop_off_date;
    }

    public void setDrop_off_date(String drop_off_date) {
        this.drop_off_date = drop_off_date;
    }

    public Integer getOrder_delivery_type() {
        return order_delivery_type;
    }

    public void setOrder_delivery_type(Integer order_delivery_type) {
        this.order_delivery_type = order_delivery_type;
    }
}

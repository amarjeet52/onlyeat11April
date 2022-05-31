package com.codebrew.clikat.data.model.api

import com.codebrew.clikat.modal.other.SettingModel
import com.google.gson.annotations.SerializedName

data class GetDbKeyModel(
        val data: DBData,
        val message: String,
        val statusCode: Int,
        val success: Int
)

data class DBData(
        val data: List<DBKeyData>,
        val currency: List<Currency>,
        val settingsData: SettingModel.DataBean.SettingData? = null,
        val featureData:List<SettingModel.DataBean.FeatureData>?=null
)


data class DBKeyData(
        val app_type: Int,
        val business_name: String,
        val name: String,
        val cbl_customer_domains: List<CblCustomerDomain>,
        val uniqueId: String,
        val whatsapp_country_code:String?=null,
        val whatsapp_phone_number:String?=null

)




data class CblCustomerDomain(
        val admin_domain: String,
        val agent_db_secret_key: String,
        val bn_image: String,
        val bn_thumb: String,
        val db_secret_key: String,
        val logo_image: String,
        val logo_thumb: String,
        val site_domain: String,
        val supplier_domain: String
)

data class Currency
(
    val id:Int?=null,
    val conversion_rate:Float?=null,
    val currency_name:String?=null,
    val currency_symbol:String?=null,
    val currency_description:String?=null,
    var is_default:Int?=null,
    var country_name:String?=null
){
    override fun toString(): String {
        return country_name?:""
    }
}



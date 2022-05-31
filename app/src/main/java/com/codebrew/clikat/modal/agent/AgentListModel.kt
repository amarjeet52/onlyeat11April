package com.codebrew.clikat.modal.agent

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class AgentListModel(
        /**
         * statusCode : 200
         * success : 1
         * message : Success
         * data : [{"service_id":13,"cbl_user":{"email":"sahil@code-brew.com","id":10,"image":null,"city":"342342","phone_number":"2342342","experience":null,"occupation":null}}]
         */
        var statusCode: Int? = null,
        var success: Int? = null,
        var message: String? = null,
        var data: List<DataBean>? = null)

@Parcelize
class DataBean(
        /**
         * service_id : 13
         * cbl_user : {"email":"sahil@code-brew.com","id":10,"image":null,"city":"342342","phone_number":"2342342","experience":null,"occupation":null}
         */
        var service_id: Int? = null,
        var cbl_user: CblUserBean? = null,
        var cbl_user_service_pricing:ArrayList<PriceItem>?=null,
var is_instant_available:String?=null) : Parcelable

@Parcelize
class CblUserBean(
        /**
         * email : sahil@code-brew.com
         * id : 10
         * image : null
         * city : 342342
         * phone_number : 2342342
         * experience : null
         * occupation : null
         */
        var email: String? = null,
        var id: Int? = null,
        var name: String? = null,
        var image: String? = null,
        var city: String? = null,
        var phone_number: String? = null,
        var experience: Int? = null,
        var avg_rating: Double? = null,
        var occupation: String? = null,
        var agent_bio_url:String?=null,
        var is_instant_available:String?=null
) : Parcelable

@Parcelize
data class PriceItem(

        @field:SerializedName("pricing_type")
        var pricingType: Int?=null,

        @field:SerializedName("end_date")
        var endDate: String?=null,

        @field:SerializedName("handling_supplier")
        var handlingSupplier: Float?=null,

        @field:SerializedName("display_price")
        var displayPrice: String?=null,

        @field:SerializedName("delivery_charges")
        var deliveryCharges: Float?=null,

        @field:SerializedName("price")
        var price: String?=null,

        @field:SerializedName("price_type")
        var priceType: Int?=null,

        @field:SerializedName("user_type_id")
        var userTypeId: Int?=null,

        @field:SerializedName("handling")
        var handling: Float?=null,

        @field:SerializedName("id")
        var id: Int?=null,

        @field:SerializedName("start_date")
        var startDate: String?=null,

        @field:SerializedName("agentBufferPrice")
        var agentBufferPrice:Double?=null,

        @field:SerializedName("description")
        var description:String?=null,




        var netPrice: Float? = null
):Parcelable

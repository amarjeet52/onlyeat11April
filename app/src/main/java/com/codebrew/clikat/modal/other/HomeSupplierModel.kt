package com.codebrew.clikat.modal.other

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


data class HomeSupplierModel(
        /**
         * status : 200
         * message : Success
         * data : [{"min_order":0,"onOffComm":1,"delivery_prior_time":30,"delivery_min_time":0,"delivery_max_time":0,"urgent_delivery_time":30,"total_reviews":0,"rating":0,"supplier_branch_id":1,"name":"Fashion Planet","ic_splash":"http://45.232.252.46:8082/clikat-buckettest/13347012_1032394443514805_8217217467431659662_n-2mEetEk.png","id":15,"status":0,"start_time":"15:10:00","end_time":"15:10:00","payment_method":0,"commission_package":0},{"min_order":0,"onOffComm":1,"delivery_prior_time":1470,"delivery_min_time":120,"delivery_max_time":1440,"urgent_delivery_time":30,"total_reviews":7,"rating":5,"supplier_branch_id":2,"name":"Metro Shoes Ltd","ic_splash":"http://45.232.252.46:8082/clikat-buckettest/metroaNrdb6.jpg","id":16,"status":0,"start_time":"00:00:00","end_time":"23:55:00","payment_method":0,"commission_package":0},{"min_order":0,"onOffComm":1,"delivery_prior_time":1530,"delivery_min_time":15,"delivery_max_time":15,"urgent_delivery_time":60,"total_reviews":14,"rating":4.2,"supplier_branch_id":3,"name":"Kapsoons","ic_splash":"http://45.232.252.46:8082/clikat-buckettest/150600301972Xq30.jpg","id":17,"status":1,"start_time":"12:46:00","end_time":"12:46:00","payment_method":0,"commission_package":0},{"min_order":0,"onOffComm":1,"delivery_prior_time":1470,"delivery_min_time":420,"delivery_max_time":300,"urgent_delivery_time":30,"total_reviews":0,"rating":0,"supplier_branch_id":4,"name":"Intas Pharmaceuticals Pvt Ltd","ic_splash":"http://45.232.252.46:8082/clikat-buckettest/productN0RbDs.gif","id":18,"status":1,"start_time":"00:00:00","end_time":"23:55:00","payment_method":0,"commission_package":0}]
         */
        var status: Int = 0,
        var message: Any? = null,
        var data: List<SupplierDataBean>? = null
)
data class HomeSupplierPaginationModel(
        /**
         * status : 200
         * message : Success
         * data : [{"min_order":0,"onOffComm":1,"delivery_prior_time":30,"delivery_min_time":0,"delivery_max_time":0,"urgent_delivery_time":30,"total_reviews":0,"rating":0,"supplier_branch_id":1,"name":"Fashion Planet","ic_splash":"http://45.232.252.46:8082/clikat-buckettest/13347012_1032394443514805_8217217467431659662_n-2mEetEk.png","id":15,"status":0,"start_time":"15:10:00","end_time":"15:10:00","payment_method":0,"commission_package":0},{"min_order":0,"onOffComm":1,"delivery_prior_time":1470,"delivery_min_time":120,"delivery_max_time":1440,"urgent_delivery_time":30,"total_reviews":7,"rating":5,"supplier_branch_id":2,"name":"Metro Shoes Ltd","ic_splash":"http://45.232.252.46:8082/clikat-buckettest/metroaNrdb6.jpg","id":16,"status":0,"start_time":"00:00:00","end_time":"23:55:00","payment_method":0,"commission_package":0},{"min_order":0,"onOffComm":1,"delivery_prior_time":1530,"delivery_min_time":15,"delivery_max_time":15,"urgent_delivery_time":60,"total_reviews":14,"rating":4.2,"supplier_branch_id":3,"name":"Kapsoons","ic_splash":"http://45.232.252.46:8082/clikat-buckettest/150600301972Xq30.jpg","id":17,"status":1,"start_time":"12:46:00","end_time":"12:46:00","payment_method":0,"commission_package":0},{"min_order":0,"onOffComm":1,"delivery_prior_time":1470,"delivery_min_time":420,"delivery_max_time":300,"urgent_delivery_time":30,"total_reviews":0,"rating":0,"supplier_branch_id":4,"name":"Intas Pharmaceuticals Pvt Ltd","ic_splash":"http://45.232.252.46:8082/clikat-buckettest/productN0RbDs.gif","id":18,"status":1,"start_time":"00:00:00","end_time":"23:55:00","payment_method":0,"commission_package":0}]
         */
        var status: Int = 0,
        var message: Any? = null,
        var data: SupplierItem? = null
)

data class SupplierItem(
        var count:Int?=null,
        var list: List<SupplierDataBean>? = null
)
@Parcelize
data class SupplierDataBean(
        var min_order: Int = 0,
        var is_scheduled:Int=0,
        var onOffComm: Int = 0,
        var is_on_discount:Int=0,
        var is_free_delivery:Int=0,
        var image_path:String?=null,
        var delivery_prior_time: Int = 0,
        var delivery_min_time: Int = 0,
        var delivery_max_time: Int = 0,
        var latitude:Double?=0.0,
        val longitude:Double?=0.0,
        var is_dine_in: Int? = null,
        var urgent_delivery_time: Int = 0,
        var total_reviews: Float = 0.0f,
        var rating: Double = 0.0,
        var supplier_branch_id: Int = 0,
        var having_products:Int=0,
        var count_supplier_branch:Int=0,
        var name: String? = null,
        var logo: String? = null,
        var id: Int = 0,
        var category_id: Int = 0,
        var status: Int = 0,
        var isOpen: Boolean = false,
        var start_time: String? = null,
        var end_time: String? = null,
        var address: String? = null,
        var payment_method: Int = 0,
        var commission_package: Int = 0,
        var viewType: String = "",
        var is_open_close:Int=0,
        var dynamicSectionName :String? =null,
        var supplier_image: String? = null,
        var itemModel: HomeItemModel? = null,
        val timings: String? = null,
        var timing: List<TimeDataBean>? = null,

        val categories:String? = null,
        val category: List<Category>? = null,
        val delivery_radius: Int? = null,
        val description: String? = null,
        val distance: Double? = null,
        val self_pickup: Int? = null,
        val supplier_id: Int? = null,
        var is_multi_branch: Int? = null,
        val terms_and_conditions: String? = null,
        val uniqueness: String? = null,
       // var supplier_tags: ArrayList<SupplierTags>? = null,
        var branch_list: List<Branch>? = null,
        val name_arabic: String?=null,
        val branch_name_arabic: String?=null,

        var Favourite:Int?=null) : Parcelable


@Parcelize
data class SupplierTags(
        val icon: String? = null,
        var id: Int? = null,
        val tag_image: String? = null,
        var name: String? = null
) : Parcelable {
    override fun toString(): String {
        return name ?: ""
    }
}
@Parcelize
data class Branch(
        val address: String,
        val category: List<Category>,
        val category_id: Int,
        val category_name: String,
        val commission_package: Int,
        val delivery_max_time: Int,
        val delivery_min_time: Int,
        val delivery_prior_time: Int,
        val delivery_radius: Int,
        val end_time: String,
        val id: Int,
        val is_deactivated: Int,
        val logo: String,
        val name: String,
        val name_arabic: String,
        val onOffComm: Int,
        val payment_method: Int,
        val rating: Int,
        val start_time: String,
        val status: Int,
        val supplier_branch_id: Int,
        val supplier_branch_name: String,
        val supplier_image: String,
        val total_reviews: Int,
        val urgent_delivery_time: Int
) : Parcelable

@Parcelize
data class Category(
        val category_flow: String,
        val category_id: Int,
        val category_name: String,
        val description: String,
        val image: String,
        val order: Int,
        val supplier_placement_level: Int
) : Parcelable
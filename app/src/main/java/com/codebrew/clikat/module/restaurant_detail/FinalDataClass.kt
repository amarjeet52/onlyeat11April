package com.codebrew.clikat.module.restaurant_detail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Final {
    @SerializedName("supplier_detail")
    @Expose
     val supplierDetail: SupplierDetail? = null

    @SerializedName("free_delivery")
    @Expose
     val freeDelivery: Int? = null

    @SerializedName("is_combo")
    @Expose
     val isCombo: Int? = null

    @SerializedName("is_discount")
    @Expose
     val isDiscount: Int? = null

    @SerializedName("buy_one_get_one")
    @Expose
     val buyOneGetOne: Int? = null
}


class SupplierDetail {
    @SerializedName("id")
    @Expose
     val id: Int? = null

    @SerializedName("supplier_branch_name")
    @Expose
     val supplierBranchName: String? = null

    @SerializedName("supplier_id")
    @Expose
     val supplierId: Int? = null

    @SerializedName("branch_name")
    @Expose
     val branchName: String? = null

    @SerializedName("address")
    @Expose
     val address: String? = null

    @SerializedName("logo")
    @Expose
     val logo: String? = null

    @SerializedName("name")
    @Expose
     val name: String? = null

    @SerializedName("supplier_image")
    @Expose
     val supplierImage: String? = null
}
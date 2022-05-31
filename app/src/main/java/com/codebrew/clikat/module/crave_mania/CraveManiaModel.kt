package com.codebrew.clikat.module.crave_mania

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class CraveManiaModel(
    val `data`: CraveManiaData,
    val message: String,
    val status: Int
)

data class CraveManiaData(
        val products: MutableList<SupplierDetailX>

)

data class SupplierDetailX(
        val address: String,
        val branch_name_arabic:String,
        val branch_name: String,
        val is_free_delivery:Int,
        val is_on_discount:Int,
        val id: Int,
        val rating:Double = 0.0,
        val logo: String,
        val name: String,
        val supplier_branch_name: String,
        val supplier_id: Int,
        val supplier_image: Any
)
data class SupplierDetail(
        val address: String,
        val branch_name: String,
        val id: Int,
        val logo: String,
        val name: String,
        val supplier_branch_name: String,
        val supplier_id: Int,
        val supplier_image: Any
)
data class FdArray(
        val buy_one_get_one: Int,
        val free_delivery: Int,
        val is_combo: Int,
        val is_discount: Int,
        val supplier_detail: SupplierDetailX
)

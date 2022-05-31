package com.codebrew.clikat.modal.other

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SubCategoryListModel(val data: SubCatData? = null,
                                val message: String? = null,
                                val status: Int? = null) : Parcelable

@Parcelize
data class SubCatData(
        val brand: List<Brand>? = null,
        val offer: List<ProductDataBean>? = null,
        val sub_category_data: ArrayList<SubCategoryData>? = null,
        val supplier_with_subcategory: ArrayList<SuppliersList>? = null
) : Parcelable

@Parcelize
data class SuppliersList(
        val sub_category_name: String? = null,
        val suppliers: ArrayList<SupplierInArabicBean>? = null
) : Parcelable

data class SubCatList(
        val name: String? = null,

        val datalist: List<Any?>? = null,
        val isSuppliers:Boolean?=false
)

@Parcelize
data class SubCategoryData(
        val description: String? = null,
        val icon: String? = null,
        val image: String? = null,
        var is_question: Int? = null,
        var status: Boolean? = null,
        @SerializedName("is_subcategory", alternate = ["is_cub_category"])
        val is_cub_category: Int? = null,
        @SerializedName("name", alternate = ["category_name"])
        val name: String? = null,
        val supplier_placement_level: Int? = null,
        @SerializedName("category_id", alternate = ["id", "sub_category_id"])
        val category_id: Int? = null,
        val category_flow: String? = null,
        val menu_type: Int? = null
) : Parcelable


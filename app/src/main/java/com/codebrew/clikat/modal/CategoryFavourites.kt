package com.codebrew.clikat.modal

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class CategoryFavourites (
     val image: String? = null,
     val supplier_placement_level: Int? = null,
     val category_id: Int? = null,
     @SerializedName("is_subcategory",alternate = ["is_cub_category"])
     val is_subcategory: Int? = null,
     val category_name: String? = null,
     val description: String? = null,
     val order: Int? = null,
     val name: String?=null,
     val sub_category_id: Int?=null,
     var status: Boolean?=null,
     val category_flow: String? = null
):Parcelable

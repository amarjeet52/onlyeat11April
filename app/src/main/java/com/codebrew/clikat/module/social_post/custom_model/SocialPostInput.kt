package com.codebrew.clikat.module.social_post.custom_model

import android.os.Parcelable
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.CommentBean
import com.codebrew.clikat.data.model.others.ImageListModel
import com.codebrew.clikat.data.model.others.PopUpItem
import com.codebrew.clikat.modal.agent.CblUserBean
import com.codebrew.clikat.modal.other.ProductBean
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SupplierDataBean
import com.codebrew.clikat.modal.other.SupplierDetailBean
import kotlinx.android.parcel.Parcelize
import org.apache.commons.lang3.mutable.Mutable

@Parcelize
data class SocialPostInput(
        var post_head: String? = null,
        var post_desc: String? = null,
        var supplier_data: SupplierDataBean? = null,
        var suplierList: MutableList<SupplierDataBean>? = null,
        var product_data: ProductDataBean? = null,
        var prodList: MutableList<ProductDataBean>? = null,
        var imageList: MutableList<ImageListModel?>? = null,
        var actualImageList: MutableList<ImageListModel?>? = null,
        var isEdit: Boolean? = null,
        var id: Int? = null
) : Parcelable


data class SocialDataItem(val socialType: Int, val productList: ProductDataBean? = null,
                          val supplierList: SupplierDataBean? = null, val commentBean: CommentBean? = null,
                          val likeBean: CommentBean? = null)

@Parcelize
data class BottomDataItem(val prodList: ArrayList<ProductDataBean>? = null,
                          val supList: ArrayList<SupplierDataBean>? = null,
                          val commentList: ArrayList<CommentBean>? = null,
                          val likeList: ArrayList<CommentBean>? = null) : Parcelable

data class SocialSupplierBean(var supplierData: SupplierDetailBean? = null,
                            var prodList: MutableList<ProductDataBean>? = null)

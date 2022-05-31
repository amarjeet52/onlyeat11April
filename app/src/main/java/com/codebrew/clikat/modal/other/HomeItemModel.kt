package com.codebrew.clikat.modal.other

import android.os.Parcelable
import com.codebrew.clikat.data.model.api.orderDetail.OrderHistory
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HomeItemModel(

        var specialOffers: MutableList<ProductDataBean>? = null,
       var offersSuppliers: MutableList<ProductDataBean>? = null,
        var sponserList: List<SupplierInArabicBean>? = null,
        var categoryList: MutableList<English>? = null,
        var tagsList: MutableList<Brand>? = null,
        var brandsList: List<Brand>? = null,
        var vendorProdList: ProductBean? = null,
        var popularProdList: MutableList<ProductDataBean>? = null,
        var recentProdList: MutableList<ProductDataBean>? = null,
        var recentViewHistory: List<ProductDataBean>? = null,
        var bannerList: List<TopBanner>? = null,
        var suppliersList: List<SupplierDataBean>? = null,
        var highestRatingSuppliersList: List<SupplierDataBean>? = null,
        var newSuppliersList: List<SupplierDataBean>? = null,
        var categoryWiseSuppliers: ArrayList<CategoryWiseSuppliers>? = null,
        var mSpecialType: Int? = null,
        var mSpecialOfferName: String? = null,
        var screenType: Int = 0,
        var isSingleVendor: Int = 0,
        var bannerWidth: Int = 0,
        var supplierCount: Int = 0,
        var userAddress: String? = null,
        var nearBySupplierView: Int=1,
        var recentOrdersList:MutableList<OrderHistory>?=null
) : Parcelable


package com.codebrew.clikat.module.subcategory.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.databinding.ItemSubcategoryParentBinding
import com.codebrew.clikat.modal.SearchProductListItem
import com.codebrew.clikat.modal.other.*
import com.codebrew.clikat.module.home_screen.adapter.*
import com.codebrew.clikat.module.product.product_listing.adapter.ProductListingAdapter
import com.codebrew.clikat.module.subcategory.SubCategory
import com.codebrew.clikat.utils.configurations.Configurations
import com.codebrew.clikat.utils.configurations.TextConfig
import kotlinx.android.synthetic.main.item_subcategory_parent.view.*

class SubCatAdapter(private val mChecklist: List<SubCatList>,
                    private val appUtils: AppUtils,
                    private val show_ecom_v2_theme: String?,
                    private val clientInform: SettingModel.DataBean.SettingData?,
                    private val textConfig: TextConfig?, private val selectedCurrency:Currency?) : RecyclerView.Adapter<SubCatAdapter.ViewHolder>() {

    private var mContext: Context? = null

    private var fragment: SubCategory? = null

    private lateinit var mCallback: SubCatCallback

    fun setFragCallback(fragment: SubCategory, mCallback: SubCatCallback) {
        this.fragment = fragment
        this.mCallback = mCallback
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        mContext = parent.context

        val binding = DataBindingUtil.inflate<ItemSubcategoryParentBinding>(LayoutInflater.from(parent.context),
                R.layout.item_subcategory_parent, parent, false)
        binding.color = Configurations.colors
        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return mChecklist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var type: String?=""
        var viewType =""

        when (mChecklist[holder.adapterPosition].name) {
            "subcategory" -> {
                type = mContext?.getString(R.string.shop_by_type) ?: ""
                viewType = if(BuildConfig.CLIENT_CODE == "dailyooz_0544"|| BuildConfig.CLIENT_CODE =="dailyoozz_0865"){
                    "horizontal"
                } else {
                    "grid"
                }

                val adapter = SubCategorySelectAdapter(mChecklist[holder.adapterPosition].datalist as MutableList<SubCategoryData>?,viewType)
                adapter.settingCallback(fragment)
                holder.rvList.adapter = adapter
            }
            "brand" -> {
                type = mContext?.getString(R.string.popular_brand) ?: ""
                viewType = "horizontal"

                val adapter = BrandsListAdapter(mChecklist[holder.adapterPosition].datalist as List<Brand>)
                fragment?.let { adapter.settingCallback(it) }
                holder.rvList.adapter = adapter

            }
            "offer" -> {
                type = mContext?.getString(R.string.discount_product, textConfig?.products) ?: ""
                viewType = "grid"

                val adapter = ProductListingAdapter(mContext,
                        mChecklist[holder.adapterPosition].datalist as MutableList<ProductDataBean>?,
                        appUtils,
                        show_ecom_v2_theme,selectedCurrency)
                adapter.settingCallback(fragment)
                holder.rvList.adapter = adapter

                mCallback.onProdItemUpdate(adapter)
            }

            "suppliers" ->{
                val dataHolder = mutableListOf<SupplierDataBean>()
                mChecklist[holder.adapterPosition].datalist?.forEach {item ->
                    if(item is SupplierDataBean) {
                        item.viewType = HomeItemAdapter.SUPL_TYPE
                    }
                    dataHolder.add(item as SupplierDataBean)
                }


                type = "Nearby restaurant"
                viewType = "vertical"

                val adapter = HomeItemAdapter(dataHolder ,1,
                        appUtils,clientInform,userAdrs = null,screenFlowBean = null, currency = selectedCurrency)
                holder.rvList.adapter = adapter
                adapter.settingCallback(object: HomeItemAdapter.SupplierListCallback {
                    override fun onSupplierDetail(supplierBean: SupplierDataBean?) {
                        mCallback.onSupplierDetail(supplierBean)
                    }

                    override fun viewAllNearBy()  {}

                    override fun onSpclView(specialListAdapter: SpecialListAdapter?) {}

                    override fun onFilterScreen() {}

                    override fun onSearchItem(text: String?) {}

                    override fun onHomeCategory(position: Int) {}

                    override fun onSortByClicked(tvSortBy: TextView) {}
                    override fun onViewMore(title: String?) {

                    }

                    override fun onViewAllCategories(list: List<English>) {}

                    override fun onPagerScroll(listAdapter: BannerListAdapter, rvBannerList: RecyclerView) {}

                    override fun onSearchClickedForV2Theme() {}
                    override fun supplierViewMoreCliked(data: SupplierDataBean?, listType: Int, title: String) {

                    }

                    override fun onBookNow(supplierData: SupplierDataBean) {

                    }

                    override fun onListViewChanges(adapterPosition: Int, isGrid: Boolean) {

                    }

                    override fun onFilterClicked(ivFilter: ImageView) {

                    }

                    override fun isLargeClicked(isLarge: Boolean) {

                    }
                })
            }


            "products" ->{
                type = "Products"
                viewType = "vertical"

               val adapter = ProductListingAdapter(holder.itemView.context, mChecklist[holder.adapterPosition].datalist as List<ProductDataBean>?,
                       appUtils, clientInform?.show_ecom_v2_theme,selectedCurrency)
                adapter.settingLayout(false)
                adapter.settingCallback(fragment as SubCategory)
                mCallback.onProdItemUpdate(adapter)
                holder.rvList.adapter = adapter
            }

            else -> {
                if (mChecklist[holder.adapterPosition].isSuppliers == true) {
                    type = mContext?.getString(R.string.recommed_supplier,mChecklist[holder.adapterPosition].name) ?: ""
                    viewType = "grid"

                    val adapter = SponsorListAdapter(mChecklist[position].datalist as MutableList<SupplierInArabicBean>,
                            AppConstants.APP_SUB_TYPE, clientInfom = clientInform, parentPos = position,appUtils =appUtils )
                    adapter.settingCallback(fragment as SubCategory)
                    holder.rvList.adapter = adapter
                }
            }
        }

        holder.tvName.text = type
        holder.rvList.layoutManager = lytManager(viewType)

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvName = itemView.tv_category
        var rvList = itemView.rv_subcategory

    }

    private fun lytManager(type: String): RecyclerView.LayoutManager {

        return when (type) {
            "horizontal" -> {
                LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false)
            }
            "grid" -> {
                GridLayoutManager(mContext, 2)
            }
            else -> LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        }
    }

    interface SubCatCallback {
        fun onProdItemUpdate(adpater: ProductListingAdapter)
        fun onSupplierDetail(supplierBean: SupplierDataBean?)
    }
}
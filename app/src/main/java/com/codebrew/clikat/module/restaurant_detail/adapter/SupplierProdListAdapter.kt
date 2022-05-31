package com.codebrew.clikat.module.restaurant_detail.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.ListPreloader.PreloadModelProvider
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.util.FixedPreloadSizeProvider
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.base.EmptyListListener
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.databinding.ItemTimeslotViewBinding
import com.codebrew.clikat.modal.other.ProductBean
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.restaurant_detail.RestaurantDetailFrag
import com.codebrew.clikat.module.restaurant_detail.RestaurantDetailFrag2
import com.codebrew.clikat.module.restaurant_detail.RestaurantDetailNewFragment
import com.codebrew.clikat.module.restaurant_detail.RestaurantSearchDialogFragment
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.item_timeslot_view.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


private val colors = Configurations.colors

class SupplierProdListAdapter(private var fragment: Fragment,
                              private var settingBean: SettingModel.DataBean.SettingData?,
                              private var appUtils: AppUtils, val selectedCurrency: Currency?, private var emptyListener: EmptyListListener, private var fragmentType: String) :
        ListAdapter<SupplierProdListAdapter.SupProdItem, RecyclerView.ViewHolder>(DgFlowListDiffCallback()), Filterable {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private var mFilteredList = mutableListOf<ProductBean>()
    private var mProductBeans = mutableListOf<ProductBean>()

    private var isOpen: Boolean = true


    fun settingList(lstUser: MutableList<ProductBean>) {
        mProductBeans = lstUser
    }

    fun checkResturantOpen(isOpen: Boolean) {
        this.isOpen = isOpen
        notifyDataSetChanged()
    }

    fun addItmSubmitList(list: List<ProductBean>?) {
        adapterScope.launch {
            val items = list?.map {
                SupProdItem.SPSuplier(it)
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProductViewHolder -> {
                val nightItem = getItem(position) as SupProdItem.SPSuplier
                holder.bind(nightItem.SupProdItem, settingBean, isOpen, fragment, appUtils, selectedCurrency, fragmentType)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProductViewHolder.from(parent)
    }

    class ProductViewHolder private constructor(val binding: ItemTimeslotViewBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(productBean: ProductBean, settingBean: SettingModel.DataBean.SettingData?, isOpen: Boolean, fragment: Fragment,
                 appUtils: AppUtils, selectedCurrency: Currency?, fragmentType: String) {
            binding.color = colors

            val mView = binding.root

            if (productBean.detailed_sub_category != null && settingBean?.zipTheme == null) {
                mView.tv_sub_title.visibility = View.VISIBLE
                mView.tv_sub_title.text = productBean.detailed_sub_category
            } else {
                mView.tv_sub_title.visibility = View.GONE
            }

            if (productBean.is_SubCat_visible == true) {
                mView.tv_title.visibility = View.VISIBLE
                mView.tv_title.text = productBean.sub_cat_name
            } else {
                mView.tv_title.visibility = View.GONE
            }

            settingBean?.zipTheme?.let {
                if (it == "1") {
                    mView.tv_title.setBackgroundColor(Color.parseColor("#f9f9f9"))
                }
            }


            mView.rv_timeperiod_slot.layoutManager = LinearLayoutManager(binding.root.context, RecyclerView.VERTICAL, false)
            val adapter = ProdListAdapter(settingBean, selectedCurrency)

            val imgHeight =
                    Utils.dpToPx(250)

            val imgWidth =
                    Utils.dpToPx(100)

            val sizeProvider: ListPreloader.PreloadSizeProvider<ProductDataBean> = FixedPreloadSizeProvider<ProductDataBean>(imgWidth.toInt(), imgHeight.toInt())


            val modelProvider = MyPreloadModelProvider(productBean.value, binding.root.context)
            val preloader = RecyclerViewPreloader(fragment, modelProvider, sizeProvider, 10)


            mView.rv_timeperiod_slot.adapter = adapter
            // mView.rv_timeperiod_slot.itemAnimator?.changeDuration = 0
            mView.rv_timeperiod_slot.itemAnimator = null

            mView.rv_timeperiod_slot.setItemViewCacheSize(0)
            mView.rv_timeperiod_slot.addOnScrollListener(preloader)

            adapter.updateRestTime(isOpen)
            adapter.updateParentPos(adapterPosition)

            fragment.activity?.runOnUiThread {
                adapter.addItmSubmitList(productBean.value
                        ?: mutableListOf())
            }

            when (fragment) {
                is RestaurantDetailNewFragment -> {
                    adapter.settingCallback(fragment as RestaurantDetailNewFragment, appUtils)
                }
                is RestaurantSearchDialogFragment -> {
                    adapter.settingCallback(fragment as RestaurantSearchDialogFragment, appUtils)
                }
                else -> {
                    ///Todo change pending
                    if (fragmentType == "old") {
                        adapter.settingCallback(fragment as RestaurantDetailFrag, appUtils)
                    } else {
                        adapter.settingCallback(fragment as RestaurantDetailFrag2, appUtils)
                    }
                }
            }

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ProductViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemTimeslotViewBinding.inflate(layoutInflater, parent, false)
                return ProductViewHolder(binding)
            }
        }
    }


    class DgFlowListDiffCallback : DiffUtil.ItemCallback<SupProdItem>() {
        override fun areItemsTheSame(oldItem: SupProdItem, newItem: SupProdItem): Boolean {
            return oldItem.socialData == newItem.socialData
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: SupProdItem, newItem: SupProdItem): Boolean {
            return oldItem == newItem
        }
    }

    sealed class SupProdItem {
        data class SPSuplier(val SupProdItem: ProductBean) : SupProdItem() {
            override val socialData = SupProdItem
        }

        abstract val socialData: ProductBean
    }

    class MyPreloadModelProvider(private val products: MutableList<ProductDataBean>?, private val context: Context) : PreloadModelProvider<ProductDataBean> {

        override fun getPreloadItems(position: Int): MutableList<ProductDataBean> {

            return if (!products.isNullOrEmpty() && products.size >= position) {
                val url = products[position].image_path.toString()

                if (TextUtils.isEmpty(url)) {
                    Collections.emptyList()
                } else
                    Collections.singletonList(products[position])
            } else
                Collections.emptyList()


        }

        override fun getPreloadRequestBuilder(item: ProductDataBean): RequestBuilder<*>? {


            val glide = Glide.with(context)


            val requestOptions = RequestOptions
                    .bitmapTransform(RoundedCornersTransformation(8, 0,
                            RoundedCornersTransformation.CornerType.ALL))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(if (AppConstants.APP_SUB_TYPE == AppDataType.Food.type) R.drawable.iv_placeholder else R.drawable.white_placeholder)
                    .error(if (AppConstants.APP_SUB_TYPE == AppDataType.Food.type) R.drawable.iv_placeholder else R.drawable.white_placeholder)


            return glide.load(item.image_path.toString())
                    .apply(requestOptions)


        }


    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {

                val charString = charSequence.toString()

                mFilteredList = if (charString.isEmpty()) {
                    mProductBeans.map { it.copy() }.toMutableList()
                } else {
                    val mProductList = mutableListOf<ProductBean>()

                    mProductBeans.map { it.copy() }.toMutableList().forEachIndexed { _, productBean ->
                        if (productBean.value?.any { it.name?.toLowerCase()?.contains(charString) == true } == true) {
                            productBean.value = productBean.value?.filter { it.name?.toLowerCase()?.contains(charString) == true }?.toMutableList()
                            mProductList.add(productBean.copy())
                        }
                    }
                    mProductList.toMutableList()
                }
                val filterResults = FilterResults()
                filterResults.values = mFilteredList.toMutableList()
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                emptyListener.onEmptyList((filterResults.values as MutableList<ProductBean>).filter { it.value?.isNotEmpty() == true }.count()
                        ?: 0)
                addItmSubmitList(filterResults.values as MutableList<ProductBean>)
            }
        }
    }
}


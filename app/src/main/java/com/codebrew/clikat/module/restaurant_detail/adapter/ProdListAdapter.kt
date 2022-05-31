package com.codebrew.clikat.module.restaurant_detail.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppToasty
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.setColorScale
import com.codebrew.clikat.app_utils.extension.setGreyScale
import com.codebrew.clikat.app_utils.extension.setSafeOnClickListener
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.databinding.ItemSupplierProductBinding
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.utils.StaticFunction.loadImage
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.ColorConfig
import com.codebrew.clikat.utils.configurations.Configurations
import com.codebrew.clikat.utils.configurations.TextConfig
import kotlinx.android.synthetic.main.item_supplier_product.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.text.DecimalFormat


class ProdListAdapter(private val settingBean: SettingModel.DataBean.SettingData?, private val selectedCurrency: Currency?) :
        ListAdapter<ProdListAdapter.ProdDataItem, RecyclerView.ViewHolder>(ProdListDiffCallback()), Filterable {

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    private var mCallback: ProdCallback? = null
    private var parentPosition = 0
    private var isOpen = false

    private var mFilteredList = mutableListOf<ProductDataBean>()
    private var mProductBeans = mutableListOf<ProductDataBean>()

    fun settingList(lstUser: MutableList<ProductDataBean>) {
        mProductBeans = lstUser
    }

    private var textConfig: TextConfig? = null

    val colorConfig by lazy { Configurations.colors }

    fun settingCallback(mCallback: ProdCallback?, appUtils: AppUtils) {
        this.mCallback = mCallback
        textConfig = appUtils.loadAppConfig(0).strings
    }

    fun updateParentPos(parentPosition: Int) {
        this.parentPosition = parentPosition
    }

    fun updateRestTime(isOpen: Boolean) {
        this.isOpen = isOpen
    }


    fun addItmSubmitList(list: MutableList<ProductDataBean>?) {
        // adapterScope.launch {

        val items = list?.map {
            ProdDataItem.SPSuplier(it)
        }
        // withContext(Dispatchers.Default) {
        submitList(items)
        // }
        //  }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProdViewHolder -> {
                val nightItem = getItem(position) as ProdDataItem.SPSuplier
                holder.onBind(nightItem.SupProdItem, isOpen, settingBean, mCallback, parentPosition, colorConfig, textConfig, selectedCurrency)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProdViewHolder.from(parent)
    }

    class ProdViewHolder private constructor(val binding: ItemSupplierProductBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun onBind(bean: ProductDataBean, isOpen: Boolean, settingBean: SettingModel.DataBean.SettingData?,
                   mCallback: ProdCallback?, parentPosition: Int, colorConfig: ColorConfig, textConfig: TextConfig?, selectedCurrency: Currency?) {
            val mView = binding.root

            binding.productItem = bean
            binding.color = Configurations.colors
            binding.strings = textConfig
            binding.theme = if (settingBean?.is_hunger_app == "1" || settingBean?.zipTheme == "1") 1 else 0
            binding.isRatingVisible = settingBean?.is_product_rating == "1"
            binding.isWeightVisible = settingBean?.is_product_weight == "1"

            itemView.setOnClickListener { mCallback?.onProdDetail(bean) }

            if (bean.image_path.toString().isEmpty() && settingBean?.zipTheme == "1")
                mView.iv_prod.visibility = View.GONE
            else {
                mView.iv_prod.visibility = View.VISIBLE
                //mView.iv_prod.loadImage(bean.image_path.toString(),300,250)
                loadImage(url = bean.image_path.toString(), roundedShape = true, imageView = mView.iv_prod, imageHeight = 100, imageWidth = 200)
            }

            if (settingBean?.yummyTheme == "1") {
                mView.iv_prod.setOnClickListener {
                    mCallback?.onProdDialog(bean)
                }
            }

            mView.tv_name.text = bean.name

            if (settingBean?.is_hunger_app == "1") {
                mView.viewBottom.visibility = View.VISIBLE
                itemView.tvViewDetail?.visibility = View.GONE
            } else {
                itemView.tvViewDetail?.visibility = View.GONE
                mView.viewBottom.visibility = View.GONE
            }

            settingBean?.zipDesc.let {

                if (it == "1") {
                    mView.tv_desc_product.visibility = View.VISIBLE
                    itemView.tvViewDetail.visibility = View.GONE
                }
            }

            mView.tv_desc_product.text = HtmlCompat.fromHtml(bean.product_desc.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)

            bean.fixed_price = Utils.getDiscountPrice(bean.fixed_price?.toFloatOrNull()
                    ?: 0.0f, bean.perProductLoyalityDiscount, settingBean).toString()
            mView.tv_total_prod.text = mView.context?.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL,
                    Utils.getPriceFormat(bean.fixed_price?.toFloatOrNull()
                            ?: 0.0f, settingBean,selectedCurrency))
            if (bean.fixed_price?.toFloatOrNull()?:0f != bean.display_price?.toFloatOrNull()?:0f) {
                itemView.tvActualPrice.visibility = View.VISIBLE
                itemView.tvActualPrice.paintFlags = mView.tv_total_prod.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                itemView.tvActualPrice.text = mView.context?.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL,
                        Utils.getPriceFormat(bean.display_price?.toFloatOrNull()
                                ?: 0.0f, settingBean,selectedCurrency))
            } else {
                itemView.tvActualPrice.visibility = View.GONE
            }

            val quantity = if (settingBean?.is_decimal_quantity_allowed == "1")
                bean.prod_quantity.toString()
            else
                (bean.prod_quantity?.toInt()).toString()
            mView.tv_quant.setText(quantity)

            if (bean.adds_on != null && bean.adds_on?.isNotEmpty() == true) {
                itemView.tv_type_custmize.visibility = View.VISIBLE
                itemView.iv_increment.visibility=View.GONE
            } else {
                itemView.tv_type_custmize.visibility = View.GONE
                itemView.iv_increment.visibility=View.VISIBLE
            }
            mView.tv_type_custmize.setOnClickListener {
                mCallback?.onProdAdded(bean, parentPosition, adapterPosition, !isOpen)
            }
            //Out of Stock
            if (bean.purchased_quantity ?: 0f >= bean.quantity ?: 0f || bean.quantity == 0f || bean.item_unavailable == "1") {
                mView.iv_prod.setGreyScale()
                mView.stock_label.visibility = View.VISIBLE
                mView.actionGroup.visibility = View.INVISIBLE
                mView.iv_increment.visibility = View.INVISIBLE
                itemView.tvViewDetail.setTextColor(Color.parseColor(colorConfig.textHead))
                itemView.tv_type_custmize.visibility = View.GONE
            } else {
                mView.iv_prod.setColorScale()
                mView.stock_label.visibility = View.GONE
                mView.iv_increment.visibility =if(bean.is_out_network==1 && bean.prod_quantity==1f) View.GONE else View.VISIBLE
                itemView.tvViewDetail.setTextColor(Color.parseColor(colorConfig.primaryColor))
                mView.actionGroup.visibility = if (bean.prod_quantity ?: 0f > 0f) View.VISIBLE else View.INVISIBLE
            }

            if (!isOpen) {
                mView.iv_increment.setGreyScale()
                mView.iv_decrement.setGreyScale()
            }

            mView.tvSubscribed.visibility = if (bean.is_subscription_required == 1) View.VISIBLE else View.GONE
            mView.tv_food_rating.text = bean.avg_rating.toString()

            mView.tv_desc.text = HtmlCompat.fromHtml(bean.product_desc.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
            mView.tv_quant.isEnabled = settingBean?.is_decimal_quantity_allowed == "1"


            mView.iv_increment.setOnClickListener {
                mCallback?.onProdAdded(bean, parentPosition, adapterPosition, !isOpen) }
            mView.iv_decrement.setOnClickListener {
                mCallback?.onProdDelete(bean, parentPosition, adapterPosition, !isOpen) }
            mView.tv_desc_product.setOnClickListener {
                mCallback?.onProdDesc(bean.product_desc ?: "")
            }
            mView.tv_desc.setOnClickListener {
                mCallback?.onDescExpand(mView.tv_desc, bean, parentPosition)
            }

            mView.tv_quant.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val decimalFormat = DecimalFormat("0.00")
                    val updatedQuantity = decimalFormat.format(mView.tv_quant.text.toString().trim().toFloat()).toFloat()
                    if (settingBean?.is_decimal_quantity_allowed == "1" && settingBean.is_decimal_fixed_interval == "1") {
                        val rem = (updatedQuantity.times(100).toInt()).rem(AppConstants.DECIMAL_INTERVAL.times(100).toInt())
                        if (rem == 0) {
                            mView.tv_quant.isCursorVisible = false
                            mCallback?.onEditQuantity(updatedQuantity, bean, parentPosition, adapterPosition, !isOpen)
                        } else
                            mView.context?.let { AppToasty.error(it, mView.context?.getString(R.string.quantity_shoul_be_multiple).toString()) }
                    } else {
                        mView.tv_quant.isCursorVisible = false
                        mCallback?.onEditQuantity(updatedQuantity, bean, parentPosition, adapterPosition, !isOpen)
                    }
                    true
                } else false
            }
        }

        companion object {
            fun from(parent: ViewGroup): ProdViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSupplierProductBinding.inflate(layoutInflater, parent, false)
                return ProdViewHolder(binding)
            }
        }
    }


    interface ProdCallback {
        fun onProdAdded(productBean: ProductDataBean?, parentPosition: Int, childPosition: Int, isOpen: Boolean){}
        fun onProdDelete(productBean: ProductDataBean?, parentPosition: Int, childPosition: Int, isOpen: Boolean){}
        fun onProdDetail(productBean: ProductDataBean?){}
        fun onDescExpand(tvDesc: TextView?, productBean: ProductDataBean?, childPosition: Int){}
        fun onProdDesc(productDesc: String){}
        fun onProdDialog(bean: ProductDataBean?){}
        fun onEditQuantity(quantity: Float, productBean: ProductDataBean?, parentPosition: Int, childPosition: Int, isOpen: Boolean){}
        fun onProdAllergiesClicked(bean: ProductDataBean){}
    }


    class ProdListDiffCallback : DiffUtil.ItemCallback<ProdDataItem>() {
        override fun areItemsTheSame(oldItem: ProdDataItem, newItem: ProdDataItem): Boolean {
            return oldItem.prodData.prod_quantity == newItem.prodData.prod_quantity
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: ProdDataItem, newItem: ProdDataItem): Boolean {
            return oldItem.prodData.prod_quantity == newItem.prodData.prod_quantity
        }
    }

    sealed class ProdDataItem {
        data class SPSuplier(val SupProdItem: ProductDataBean) : ProdDataItem() {
            override val prodData = SupProdItem
        }

        abstract val prodData: ProductDataBean
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {

                val charString = charSequence.toString()

                mFilteredList = if (charString.isEmpty()) {
                    mProductBeans.map { it.copy() }.toMutableList()
                } else {
                    val mProductList = mutableListOf<ProductDataBean>()
                    mProductBeans.map { it.copy() }.toMutableList().forEachIndexed { _, productBean ->
                        if (productBean.name?.toLowerCase()?.contains(charString) == true) {
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
                if (filterResults.values != null)
                    addItmSubmitList(filterResults.values as MutableList<ProductDataBean>?)
            }
        }
    }
}
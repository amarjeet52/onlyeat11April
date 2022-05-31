package com.codebrew.clikat.module.product.product_listing.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.Group
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.app_utils.extension.setColorScale
import com.codebrew.clikat.app_utils.extension.setGreyScale
import com.codebrew.clikat.app_utils.extension.setSafeOnClickListener
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.VendorAppType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.databinding.*
import com.codebrew.clikat.modal.AppGlobal
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.other.SettingModel.DataBean.BookingFlowBean
import com.codebrew.clikat.modal.other.SettingModel.DataBean.ScreenFlowBean
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.preferences.Prefs
import com.codebrew.clikat.utils.StaticFunction.loadImage
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.item_supplier_crave_new_layout.view.*
import java.util.*
import kotlin.collections.ArrayList

/*
 * Created by Ankit Jindal on 19/4/16.
 */
class ProductListingAdapter(private var mContext: Context?, private val varientList: List<ProductDataBean>?,
                            private val appUtils: AppUtils, private val show_ecom_v2_theme: String?,
                            private val selectedCurrency: Currency?) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    private var mCallback: ProductCallback? = null
    private var viewType = true
    private var varientFilteredList: List<ProductDataBean>

    private val cart_flow: Int
    private val bookingFlowBean: BookingFlowBean?
    private val screenFlowBean: ScreenFlowBean?
    private val settingsData: SettingModel.DataBean.SettingData?
    private val calendar = Calendar.getInstance()
    fun settingCallback(mCallback: ProductCallback?) {
        this.mCallback = mCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context
        val clientInform = Prefs.with(mContext).getObject(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        return if (show_ecom_v2_theme == "1") {
            val binding: ItemProductListV2Binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_product_list_v2, parent, false)
            binding.color = Configurations.colors
            binding.drawables = Configurations.drawables
            binding.isRatingVisible = settingsData?.is_product_rating == "1"
            binding.isWeightVisible = settingsData?.is_product_weight == "1"
            binding.strings = appUtils.loadAppConfig(0).strings
            binding.singleVndorType = screenFlowBean?.is_single_vendor == VendorAppType.Single.appType
            ViewHolder(binding)
        } else {
            if (this.viewType) {
                (if (clientInform?.is_carveQatar_home_theme == "1") {
                    val binding: ItemSupplierCraveNewLayout2Binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_supplier_crave_new_layout2, parent, false)
                    binding.color = Configurations.colors
                    binding.drawables = Configurations.drawables
                    binding.strings = appUtils.loadAppConfig(0).strings
                    binding.isRatingVisible = settingsData?.is_product_rating == "1"
                    binding.isWeightVisible = settingsData?.is_product_weight == "1"
                    binding.strings = appUtils.loadAppConfig(0).strings
                    binding.singleVndorType = screenFlowBean?.is_single_vendor == VendorAppType.Single.appType
                    GridViewHolder(binding)
                } else {
                    val binding: ItemProductGridBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_product_grid, parent, false)
                    binding.color = Configurations.colors
                    binding.drawables = Configurations.drawables
                    binding.isRatingVisible = settingsData?.is_product_rating == "1"
                    binding.isWeightVisible = settingsData?.is_product_weight == "1"
                    binding.strings = appUtils.loadAppConfig(0).strings
                    binding.singleVndorType = screenFlowBean?.is_single_vendor == VendorAppType.Single.appType
                    ViewHolder(binding)
                }) as RecyclerView.ViewHolder
            } else {
                (if (clientInform?.is_carveQatar_home_theme == "1") {
                    val binding: ItemHomeSupplierCrave2Binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_home_supplier_crave2, parent, false)
                    binding.color = Configurations.colors
                    binding.drawables = Configurations.drawables
                    binding.isRatingVisible = settingsData?.is_product_rating == "1"
                    binding.isWeightVisible = settingsData?.is_product_weight == "1"
                    binding.strings = appUtils.loadAppConfig(0).strings
                    binding.singleVndorType = screenFlowBean?.is_single_vendor == VendorAppType.Single.appType
                    ListViewHolder(binding)
                } else {
                    val binding: ItemProductListBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_product_list, parent, false)
                    binding.color = Configurations.colors
                    binding.drawables = Configurations.drawables
                    binding.isRatingVisible = settingsData?.is_product_rating == "1"
                    binding.isWeightVisible = settingsData?.is_product_weight == "1"
                    binding.strings = appUtils.loadAppConfig(0).strings
                    binding.singleVndorType = screenFlowBean?.is_single_vendor == VendorAppType.Single.appType
                    ViewHolder(binding)
                }) as RecyclerView.ViewHolder
            }
        }

    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val productData = varientFilteredList[position]
        val clientInform = Prefs.with(mContext).getObject(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
         if (viewHolder is GridViewHolder) {
             viewHolder.onBinData(productData)
             viewHolder.onClickListener()
        }else if(viewHolder is ListViewHolder){
             viewHolder.onBinData(productData)
             viewHolder.onClickListener()
        }else{
             val holder = viewHolder as ViewHolder

             holder.updateItem(productData)

             calendar.clear()
             var displayTime = ""
             if (!varientFilteredList[position].brand_name.isNullOrEmpty() && show_ecom_v2_theme == "1") {
                 holder.brandName?.visibility = View.VISIBLE
                 holder.brandName?.text = "${holder?.itemView.context.getString(R.string.brand_name)} ${varientFilteredList[position].brand_name} "
             } else {
                 holder.brandName?.visibility = View.GONE
             }

             holder.tvName.text = varientFilteredList[position].name

             holder.tvRating.visibility = View.GONE

             holder.tvSupplierName.text = mContext?.getString(R.string.supplier_tag, productData.supplier_name)
             when (cart_flow) {
                 0, 2 -> holder.groupCart.visibility = View.GONE
                 1, 3 -> holder.groupCart.visibility = View.VISIBLE
             }
             if (varientFilteredList[position].is_product == 0) {
                 holder.groupCart.visibility = View.VISIBLE
                 holder.tvAddCart.visibility = View.GONE
             } else {
                 holder.groupCart.visibility = if (varientFilteredList[position].is_agent == 1 && varientFilteredList[position].agent_list == 1) View.GONE else View.VISIBLE
                 holder.tvAddCart.visibility = if (varientFilteredList[position].is_agent == 1 && varientFilteredList[position].agent_list == 1) View.VISIBLE else View.GONE
             }
             if (varientFilteredList[position].is_variant == 1) {
                 holder.groupCart.visibility = View.INVISIBLE
                 holder.tvAddCart.visibility = View.GONE
             } else {
                 holder.groupCart.visibility = if (productData.is_quantity != 0) View.VISIBLE else View.INVISIBLE
                 holder.tvAddCart.visibility = if (productData.is_quantity == 0) View.VISIBLE else View.GONE
             }
             loadImage(varientFilteredList[position].image_path.toString(), holder.sdvImage, false)


             //product
             if (productData.is_product == 1) {
                 holder.tvActualPrice.visibility = View.GONE
                 //price_type 1 for hourly 0 for fixed price
                 if (productData.price_type == 1) {
                     calendar.add(Calendar.MINUTE, productData.duration ?: 0)
                     displayTime = (if (calendar[Calendar.HOUR] > 0) mContext?.getString(R.string.hour_tag, calendar[Calendar.HOUR]) else "") + " " +
                             if (calendar[Calendar.MINUTE] > 0) mContext?.getString(R.string.minute_tag, calendar[Calendar.MINUTE]) else ""
                     holder.tvPrice.text = mContext?.getString(R.string.discountprice_search_multiple, AppConstants.CURRENCY_SYMBOL,
                             Utils.getPriceFormat(productData.netPrice
                                     ?: 0.0f, settingsData, selectedCurrency), displayTime)
                 } else {
                     holder.tvPrice.text = mContext?.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL,
                             Utils.getPriceFormat(productData.fixed_price?.toFloatOrNull()
                                     ?: 0.0f, settingsData, selectedCurrency))
                 }
             } else { //service

                 calendar.add(Calendar.MINUTE, productData.duration ?: 0)
                 displayTime = (if (calendar[Calendar.HOUR] > 0) mContext?.getString(R.string.hour_tag, calendar[Calendar.HOUR]) else "") + " " +
                         if (calendar[Calendar.MINUTE] > 0) mContext?.getString(R.string.minute_tag, calendar[Calendar.MINUTE]) else ""

                 holder.tvPrice.text = if (productData.price_type == 0) {
                     mContext?.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(productData.netPrice
                             ?: 0.0f, settingsData, selectedCurrency))
                 } else {
                     mContext?.getString(R.string.discountprice_search_multiple, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(productData.netPrice
                             ?: 0.0f, settingsData, selectedCurrency), displayTime)
                 }

                 productData.fixed_price = productData.netPrice.toString()

                 if (productData.netPrice != productData.netDiscount && productData.netDiscount ?: 0.0f > 0) {
                     productData.display_price = productData.netDiscount.toString()
                 }

             }

             if (productData.fixed_price?.toFloatOrNull() != productData.display_price?.toFloatOrNull() && productData.netDiscount ?: 0.0f > 0) {
                 holder.tvActualPrice.visibility = View.VISIBLE
                 holder.tvActualPrice.paintFlags = holder.tvPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                 holder.tvActualPrice.text = mContext?.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(productData.display_price?.toFloatOrNull()
                         ?: 0.0f,
                         settingsData, selectedCurrency))
             } else {
                 holder.tvActualPrice.visibility = View.GONE
             }


             // if (productData.avg_rating != 0f) {

             if (settingsData?.is_product_rating == "1") {
                 holder.rbProdRating.visibility = View.VISIBLE

                 holder.rbProdRating.rating = productData.avg_rating ?: 0f
             }


             //  holder.tvRating.text = mContext?.getString(R.string.reviews_text, productData.avg_rating)

             //   }


             if (productData.is_product == 0 && productData.price_type == 1) {
                 calendar[Calendar.HOUR_OF_DAY] = 0
                 calendar[Calendar.MINUTE] = 0
                 calendar[Calendar.DAY_OF_MONTH] = 1
                 calendar.add(Calendar.MINUTE, (productData.serviceDuration?.times(productData.prod_quantity?.toInt()
                         ?: 0)) ?: 0)
                 displayTime = (if (calendar[Calendar.DAY_OF_MONTH] - 1 > 0) mContext?.getString(R.string.day_tag, calendar[Calendar.DAY_OF_MONTH] - 1) else "") + " " + (if (calendar[Calendar.HOUR_OF_DAY] > 0) mContext?.getString(R.string.hour_tag, calendar[Calendar.HOUR_OF_DAY]) else "") + " " +
                         if (calendar[Calendar.MINUTE] > 0) mContext?.getString(R.string.minute_tag, calendar[Calendar.MINUTE]) else ""
                 // if (productData.price_type == 1) {
                 holder.tvQuant.text = if (productData.prod_quantity == 0f) "00:00" else displayTime
                 /*  } else {
                       holder.tvQuant.text = mContext?.getString(R.string.hourly_tag, productData.prod_quantity)
                   }*/
             } else {
                 holder.tvQuant.text = Utils.getDecimalPointValue(settingsData, productData.prod_quantity)
             }



             if (screenFlowBean?.app_type == AppDataType.HomeServ.type) {
                 if (productData.is_quantity == 0 && productData.prod_quantity ?: 0f > 0f) {
                     holder.tvAddCart.text = mContext?.getString(R.string.remove_cart)
                 } else {
                     holder.tvAddCart.text = mContext?.getString(R.string.add_cart_tag)
                 }
             }

             holder.ivWishlist.isChecked = productData.is_favourite == 1
             holder.ivWishlist.visibility = View.GONE

             when (screenFlowBean?.app_type) {
                 AppDataType.Food.type -> {
                     holder.tvFoodReview.text = productData.avg_rating.toString()
                 }
                 AppDataType.Ecom.type -> {
                     if (settingsData?.is_product_wishlist == "1")
                         holder.ivWishlist.visibility = View.VISIBLE
                 }
                 else -> {
                     holder.groupRating.visibility = if (settingsData?.is_product_rating == "1") View.VISIBLE else View.GONE
                 }
             }

             holder.qtyText.text = if (productData.price_type == 1) mContext?.getString(R.string.per_hour) else mContext?.getString(R.string.qty)

             //holder.itemView.setEnabled(true);
             holder.itemView.setOnClickListener {
                 mCallback?.productDetail(varientFilteredList[holder.adapterPosition])
             }


             if (productData.purchased_quantity ?: 0f >= productData.quantity ?: 0f || productData.quantity == 0f || productData?.item_unavailable == "1") {
                 holder.sdvImage.setGreyScale()
                 holder.groupCart.visibility = View.GONE
                 holder.tvStock.visibility = View.VISIBLE
             } else {
                 holder.sdvImage.setColorScale()
                 holder.tvStock.visibility = View.GONE
                 holder.groupCart.visibility = View.VISIBLE
             }

             if (productData.adds_on?.isNotEmpty() == true) {
                 holder.tvcustmize.visibility = View.VISIBLE
             } else {
                 holder.tvcustmize.visibility = View.INVISIBLE
             }

             if (screenFlowBean?.app_type == AppDataType.Ecom.type) {
                 holder.groupCart.visibility = View.GONE
                 holder.tvcustmize.visibility = View.GONE
             }

             if (screenFlowBean?.app_type == AppDataType.HomeServ.type && settingsData?.enable_freelancer_flow == "1") {
                 holder.tvPrice.visibility = View.GONE

                 if (productData.is_appointment != "1")
                     holder.tvBookNow.visibility = View.VISIBLE

             } else {
                 holder.tvPrice.visibility = View.VISIBLE
                 holder.tvBookNow.visibility = View.GONE
             }

         }



    }

    override fun getItemCount(): Int {
        return varientFilteredList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                varientFilteredList = if (charString.isEmpty()) {
                    varientList ?: emptyList()
                } else {
                    val filteredList: MutableList<ProductDataBean> = ArrayList()
                    if (varientList != null) {
                        for (row in varientList) { // name match condition. this might differ depending on your requirement
                            // here we are looking for name  match
                            if (row.name?.toLowerCase(DateTimeUtils.timeLocale)?.contains(charString) == true) {
                                filteredList.add(row)
                            }
                        }
                    }
                    /*        if (filteredList.isEmpty())
                        varientFilteredList=varientList;
                    else*/filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = varientFilteredList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                varientFilteredList = filterResults.values as List<ProductDataBean>
                mCallback!!.publishResult(varientFilteredList.size)
                notifyDataSetChanged()
            }
        }
    }

    fun settingLayout(viewType: Boolean) {
        this.viewType = viewType
        //   notifyDataSetChanged();
    }

    inner class ViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root), OnClickListener {

        internal var tvPrice: TextView = itemView.findViewById(R.id.tv_total_prod)
        internal var tvActualPrice: TextView = itemView.findViewById(R.id.tvActualPrice)
        internal var tvName: TextView = itemView.findViewById(R.id.tvName)
        internal var sdvImage: ImageView = itemView.findViewById(R.id.sdvImage)
        internal var tvSupplierName: TextView = itemView.findViewById(R.id.tvSupplierName)
        internal var qtyText: TextView = itemView.findViewById(R.id.qty_text)
        private var tvMinus: ImageView = itemView.findViewById(R.id.tv_minus)
        internal var tvQuant: TextView = itemView.findViewById(R.id.tv_quant)
        private var tvPlus: ImageView = itemView.findViewById(R.id.tv_plus)
        internal var rbProdRating: RatingBar = itemView.findViewById(R.id.rb_prod_rating)
        internal var tvRating: TextView = itemView.findViewById(R.id.tv_rating)
        internal var groupCart: Group = itemView.findViewById(R.id.group_cart)
        internal var tvAddCart: MaterialButton = itemView.findViewById(R.id.tvAddCart)
        internal var tvcustmize: TextView = itemView.findViewById(R.id.tv_type_custmize)
        internal var tvFoodReview: TextView = itemView.findViewById(R.id.tv_food_rating)
        internal var ivWishlist: CheckBox = itemView.findViewById(R.id.iv_wishlist)
        internal var groupRating: Group = itemView.findViewById(R.id.rate_group)
        internal var tvStock: TextView = itemView.findViewById(R.id.stock_label)
        internal var tvBookNow: TextView = itemView.findViewById(R.id.tvBookNow)
        internal var brandName: TextView? =
                if (show_ecom_v2_theme == "1") itemView.findViewById(R.id.brandName) else null


        override fun onClick(view: View) {
            when (view.id) {
                R.id.tv_minus -> mCallback?.removeToCart(varientList?.indexOf(varientFilteredList[adapterPosition])
                        ?: -1)
                R.id.tv_plus -> {
                    //if both agent_type & is_agent ==1 then it's service
                    mCallback?.addToCart(varientList?.indexOf(varientFilteredList[adapterPosition])
                            ?: -1, varientFilteredList.any {
                        it.is_agent == 1 && it.agent_list == 1
                    })
                }
                R.id.tvAddCart -> if (tvAddCart.text.toString() == mContext?.getString(R.string.add_cart_tag) || tvAddCart.text.toString() == mContext?.getString(R.string.add_to_cart)) {
                    //if both agent_type & is_agent ==1 then it's service
                    mCallback?.addToCart(varientList?.indexOf(varientFilteredList[adapterPosition])
                            ?: -1, varientFilteredList.any {
                        it.is_agent == 1 && it.agent_list == 1
                    })
                } else {
                    mCallback?.removeToCart(varientList?.indexOf(varientFilteredList[adapterPosition])
                            ?: -1)
                }

                R.id.iv_wishlist -> {
                    if (screenFlowBean?.app_type == AppDataType.Ecom.type) {

                        ivWishlist.setOnCheckedChangeListener { checkBox, isChecked ->
                            if (isChecked) {
                                mCallback?.addtoWishList(adapterPosition, 1, varientFilteredList.get(adapterPosition).product_id)
                            } else {
                                mCallback?.addtoWishList(adapterPosition, 0, varientFilteredList.get(adapterPosition).product_id)
                            }
                        }
                    }
                }
                R.id.tvBookNow -> {
                    mCallback?.bookNow(adapterPosition, varientFilteredList[adapterPosition])
                }
            }
        }

        init {
            tvBookNow.setOnClickListener(this)
            tvMinus.setOnClickListener(this)
            tvPlus.setOnClickListener(this)
            ivWishlist.setOnClickListener(this)
            tvAddCart.setOnClickListener(this)

            tvPrice.typeface = AppGlobal.regular
            tvName.typeface = AppGlobal.regular
            tvSupplierName.typeface = AppGlobal.regular
            qtyText.typeface = AppGlobal.regular
            tvActualPrice.typeface = AppGlobal.semi_bold
            tvActualPrice.paintFlags = tvActualPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }

        fun updateItem(productItem: ProductDataBean) {

            when (binding) {
                is ItemProductListV2Binding -> {
                    binding.productItem = productItem
                }

                is ItemProductGridBinding -> {
                    binding.productItem = productItem
                }

                is ItemProductListBinding -> {
                    binding.productItem = productItem
                }
                is ItemSupplierCraveNewLayoutBinding -> {
                    binding.productItem = productItem
                }
            }
        }
    }

    inner class ListViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root), OnClickListener {
        internal var tvName: TextView = itemView.findViewById(R.id.tvName)
        internal var sdvImage: ImageView = itemView.findViewById(R.id.sdvImage)
//        internal var tvSupplierName: TextView = itemView.findViewById(R.id.tvSupplierName)
        internal var brandName: TextView? =
                if (show_ecom_v2_theme == "1") itemView.findViewById(R.id.brandName) else null


        override fun onClick(view: View) {
            when (view.id) {

            }
        }

        init {
//              tvBookNow.setOnClickListener(this)

            tvName.typeface = AppGlobal.regular
           // tvSupplierName.typeface = AppGlobal.regular
        }

        fun updateItem(productItem: ProductDataBean) {

            when (binding) {
                is ItemProductListV2Binding -> {
                    binding.productItem = productItem
                }

                is ItemProductGridBinding -> {
                    binding.productItem = productItem
                }

                is ItemProductListBinding -> {
                    binding.productItem = productItem
                }
            }
        }

        fun onBinData(productData: ProductDataBean) {
            tvName.text = productData.name
            loadImage(productData.image_path.toString(), sdvImage, false)
            itemView.tvSupplierloc.text = productData.category_name.toString()
            itemView.tv_supplier_inf.text = mContext?.getString(R.string.min_max_time_, productData?.delivery_min_time, productData?.delivery_max_time)
        }

        fun onClickListener() {

        }
    }

  internal  inner class GridViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root), OnClickListener {

        internal var tvName: TextView = itemView.findViewById(R.id.tvName)
        internal var sdvImage: ImageView = itemView.findViewById(R.id.sdvImage)
        internal var tvSupplierloc: TextView = itemView.findViewById(R.id.tvSupplierloc)
        internal var tv_supplier_inf: TextView = itemView.findViewById(R.id.tv_supplier_inf)
      //  internal var grp: Group = itemView.findViewById(R.id.group)


        override fun onClick(view: View) {
            when (view.id) {

            }
        }

      fun onBinData(productData: ProductDataBean) {
          tvName.text = productData.name
          loadImage(productData.image_path.toString(), sdvImage, false)
          tvSupplierloc.text = productData.category_name.toString()
          tv_supplier_inf.text = mContext?.getString(R.string.min_max_time_, productData?.delivery_min_time, productData?.delivery_max_time)
      }

      fun onClickListener() {
          itemView.setOnClickListener {
              mCallback?.productDetail(varientFilteredList[adapterPosition])
          }

      }

      init {
            tvName.typeface = AppGlobal.regular
        }
    }

    interface ProductCallback {
        fun addToCart(position: Int, agentType: Boolean)
        fun removeToCart(position: Int)
        fun productDetail(bean: ProductDataBean?)
        fun publishResult(count: Int)
        fun addtoWishList(adapterPosition: Int, status: Int?, productId: Int?)
        fun bookNow(adapterPosition: Int, bean: ProductDataBean)
    }

    init {
        /*      this.list.clear();
        this.list.addAll(list);*/
        varientFilteredList = varientList ?: emptyList()
        bookingFlowBean = appUtils.dataManager.getGsonValue(DataNames.BOOKING_FLOW, BookingFlowBean::class.java)
        screenFlowBean = appUtils.dataManager.getGsonValue(DataNames.SCREEN_FLOW, ScreenFlowBean::class.java)
        settingsData = appUtils.dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        cart_flow = bookingFlowBean?.cart_flow ?: 0
    }

}
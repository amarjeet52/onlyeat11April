package com.codebrew.clikat.module.home_screen.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.CommonUtils
import com.codebrew.clikat.app_utils.extension.loadImage
import com.codebrew.clikat.app_utils.extension.setColorScale
import com.codebrew.clikat.app_utils.extension.setGreyScale
import com.codebrew.clikat.app_utils.extension.setSafeOnClickListener
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.VendorAppType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.databinding.ItemSpecialOfferBinding
import com.codebrew.clikat.databinding.ItemSpecialOfferListBinding
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.home_screen.adapter.SpecialListAdapter.ViewHolder
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import com.codebrew.clikat.utils.customviews.ClikatTextView
import com.google.android.material.button.MaterialButton
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.item_special_offer.view.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.collections.ArrayList

const val SERVICE_OFFER = 1

class SpecialListAdapter internal constructor(private val offerList: List<ProductDataBean?>?,
                                              private val screenType: Int,
                                              private val singleVendor: Int,
                                              private val mSpecialType: Int,
                                              val clientInform: SettingModel.DataBean.SettingData?,
                                              val isCompareProducts: Boolean? = false,val currency:Currency?,var appUtils:AppUtils)
    : Adapter<ViewHolder>() {
    private var mContext: Context? = null
    private var isRecommendedType: Boolean? = false
    private var mCallback: OnProductDetail? = null
    private val calendar = Calendar.getInstance()

    var df: DecimalFormat = DecimalFormat("0.0", DecimalFormatSymbols.getInstance(Locale.ENGLISH))

    internal fun settingCllback(mCallback: OnProductDetail?) {
        this.mCallback = mCallback
    }

    fun isRecommended(value: Boolean){
        isRecommendedType=value
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        mContext = viewGroup.context
        df.roundingMode = RoundingMode.HALF_DOWN
        return if (viewType == SERVICE_OFFER) {
            val binding: ItemSpecialOfferBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.context),
                    R.layout.item_special_offer, viewGroup, false)
            binding.color = Configurations.colors
            binding.strings = Configurations.strings
            binding.singleVndorType = singleVendor == VendorAppType.Single.appType
            binding.theme = if (clientInform?.is_hunger_app == "1") 1 else 0
            binding.screenType = screenType
            binding.abdullahTheme=if(clientInform?.app_selected_theme=="3") 1 else 0
            binding.isWeightVisible = clientInform?.is_product_weight == "1"

            ViewHolder(binding, viewType)
        } else {
            val binding: ItemSpecialOfferListBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.context),
                    R.layout.item_special_offer_list, viewGroup, false)
            binding.color = Configurations.colors
            binding.strings = Configurations.strings
            binding.theme = if (clientInform?.is_hunger_app == "1") 1 else 0
            binding.singleVndorType = singleVendor == VendorAppType.Single.appType
            binding.isWeightVisible = clientInform?.is_product_weight == "1"
            binding.screenType = screenType
            ViewHolder(binding, viewType)
        }
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        calendar.clear()

        viewHolder.onBind(offerList?.get(viewHolder.adapterPosition))
    }

    override fun getItemViewType(position: Int): Int {
        return mSpecialType
    }

    override fun getItemCount(): Int {
        return offerList?.size ?: 0
    }

    interface OnProductDetail {
        fun onProductDetail(bean: ProductDataBean?)
        fun addToCart(position: Int, productBean: ProductDataBean?)
        fun removeToCart(position: Int, productBean: ProductDataBean?)
        fun addtoWishList(adapterPosition: Int, status: Int?, productId: Int?)
        fun onProdDesc(productDesc: String)
        fun onProdAllergies(bean: ProductDataBean?)
        fun onProdDialog(bean: ProductDataBean?)
        fun onBookNow(bean: ProductDataBean?)
    }

    inner class ViewHolder(private val binding: ViewDataBinding, viewType: Int) : RecyclerView.ViewHolder(binding.root), OnClickListener {
        internal var viewType: Int
        private var ivProduct: RoundedImageView = itemView.findViewById(R.id.iv_product)
        private var tvName: TextView = itemView.findViewById(R.id.tv_name)
        private var tvSupplierName: TextView = itemView.findViewById(R.id.tv_supplier_name)
        private var tvDescProduct: TextView = itemView.findViewById(R.id.tv_desc_product)
        private var tvDiscountPrice: TextView = itemView.findViewById(R.id.tv_discount_price)
        private var tvActualPrice: TextView = itemView.findViewById(R.id.tv_real_price)
        private var tvFoodReview: TextView = itemView.findViewById(R.id.tv_food_rating)
        private var ivPlus: ImageView = itemView.findViewById(R.id.tv_plus)
        private var ivMinus: ImageView = itemView.findViewById(R.id.tv_minus)
        private var ivWishlist: CheckBox = itemView.findViewById(R.id.iv_wishlist)
        private var tv_type_custmize: TextView = itemView.findViewById(R.id.tv_type_custmize)
        private var tv_percentage_price: TextView = itemView.findViewById(R.id.tv_product_prentage)
        private var tvSupplierTop:TextView=itemView.findViewById(R.id.tvSupplier)
        //internal var qty_text: TextView
        private var tvQuant: ClikatTextView = itemView.findViewById(R.id.tv_quant)
        private var rbRating: RatingBar = itemView.findViewById(R.id.rb_rating)
        private var groupRating: Group = itemView.findViewById(R.id.rate_group)
        private var groupCart: Group = itemView.findViewById(R.id.cart_group)
        private var groupPrice: Group = itemView.findViewById(R.id.price_group)
        private var itemLayout: ConstraintLayout = itemView.findViewById(R.id.itemLayout)
        private var tvStock: TextView = itemView.findViewById(R.id.stock_label)
        private var tv_brand_name: TextView = itemView.findViewById(R.id.tv_brand_name)
        private var tvDistance:TextView=itemLayout.findViewById(R.id.tvDistance)
        private var ivSelect: ImageView = itemView.findViewById(R.id.ivSelect)
        private var tvBookNow:MaterialButton=itemView.findViewById(R.id.tvBookNow)
        private var tvRating:TextView=itemView.findViewById(R.id.tvRating)
        private var tvSpecialOffer:TextView=itemView.findViewById(R.id.tvSpecialOffer)
        private var ivAllergies:ImageView=itemView.findViewById(R.id.ivAllergies)
        internal fun onBind(offerItem: ProductDataBean?) {

            updateItem(offerItem)

            var displayTime = ""

            /*   if (viewType == SERVICE_OFFER) {
                actionLayout.setVisibility(View.GONE);
            }*/
            val image=if(offerItem?.isSupplier==true) offerItem.supplier_image else offerItem?.image_path
            ivProduct.loadImage(image.toString())

            tvName.text = offerItem?.name
            if (offerItem?.brand_name.isNullOrEmpty() || clientInform?.app_selected_theme=="3") {
                tv_brand_name.visibility = View.GONE
            } else {
                tv_brand_name.text = "${itemView.context?.getString(R.string.brand_name)} ${offerItem?.brand_name}"
                tv_brand_name.visibility = View.VISIBLE
            }
            tvDistance.visibility=if(clientInform?.app_selected_theme=="3" && isRecommendedType==false) View.VISIBLE else View.GONE
            tvRating.visibility=if(clientInform?.app_selected_theme=="3" && isRecommendedType==true) View.VISIBLE else View.GONE
            tvRating.text= df.format(offerItem?.avg_rating ?: 0f)

            if (AppConstants.APP_SUB_TYPE > AppDataType.Custom.type) {
                val set = ConstraintSet()
                set.clone(itemLayout)
                if (clientInform?.clikat_theme == "1") {
                    itemView.iv_product?.cornerRadius = StaticFunction.pxFromDp(8, mContext).toFloat()
                    set.setDimensionRatio(ivProduct.id, "H,4:4")
                } else {
                    set.setDimensionRatio(ivProduct.id, "H,3.8:4")
                    itemView.iv_product?.cornerRadius = StaticFunction.pxFromDp(24, mContext).toFloat()
                }

                set.constrainWidth(ivProduct.id, StaticFunction.pxFromDp(184, mContext))
                set.applyTo(itemLayout)
            }

            if (clientInform?.yummyTheme == "1") {
                itemView.iv_product?.setOnClickListener {
                    mCallback?.onProdDialog(offerItem)
                }
            }

            tvSupplierName.visibility = if (screenType > AppDataType.Custom.type || clientInform?.app_selected_theme=="3") {
                View.GONE
            } else {
                View.VISIBLE
            }
            tvSupplierTop.visibility=if(clientInform?.app_selected_theme=="3" && isRecommendedType==false && offerItem?.isSupplier==false) View.VISIBLE else View.GONE
            tvSupplierTop.text=offerItem?.supplier_name
            tvSupplierName.text = mContext?.getString(R.string.supplier_tag, offerItem?.supplier_name)
            tvDescProduct.text = Utils.getHtmlData(offerItem?.product_desc ?: "")

            if (offerItem?.prod_quantity == 0f) {
                groupCart.visibility = View.INVISIBLE
            } else {
                groupCart.visibility = View.VISIBLE
            }

            tvQuant.text = offerItem?.prod_quantity.toString()


            if (offerItem?.is_product == 1) {

                tvActualPrice.visibility = View.GONE
                //price_type 1 for hourly 0 for fixed price
                if (offerItem.price_type == 1) {
                    offerItem.netPrice=Utils.getDiscountPrice(offerItem.netPrice?:0f,offerItem.perProductLoyalityDiscount,clientInform)

                    calendar.add(Calendar.MINUTE, offerItem.duration ?: 0)
                    displayTime = (if (calendar[Calendar.HOUR] > 0) mContext?.getString(R.string.hour_tag, calendar[Calendar.HOUR]) else "") + " " +
                            if (calendar[Calendar.MINUTE] > 0) mContext?.getString(R.string.minute_tag, calendar[Calendar.MINUTE]) else ""

                    tvDiscountPrice.text = mContext?.getString(R.string.discountprice_search_multiple, AppConstants.CURRENCY_SYMBOL,
                            Utils.getPriceFormat(offerItem.netPrice ?: 0.0f,clientInform,currency), displayTime)
                } else {
                    offerItem.fixed_price=Utils.getDiscountPrice(offerItem.fixed_price?.toFloatOrNull() ?: 0.0f,offerItem.perProductLoyalityDiscount,clientInform).toString()

                    tvDiscountPrice.text = mContext?.getString(R.string.discountprice_tag, AppConstants.CURRENCY_SYMBOL,
                            Utils.getPriceFormat(offerItem.fixed_price?.toFloatOrNull() ?: 0.0f,clientInform,currency))
                }
            } else { //service

                calendar.add(Calendar.MINUTE, offerItem?.duration ?: 0)
                displayTime = (if (calendar[Calendar.HOUR] > 0) mContext?.getString(R.string.hour_tag, calendar[Calendar.HOUR]) else "") + " " +
                        if (calendar[Calendar.MINUTE] > 0) mContext?.getString(R.string.minute_tag, calendar[Calendar.MINUTE]) else ""
                //if (productData.price_type == 1) {

                tvDiscountPrice.text = if (offerItem?.price_type == 0) {
                    mContext?.getString(R.string.discountprice_tag, AppConstants.CURRENCY_SYMBOL,
                            Utils.getPriceFormat(offerItem.netPrice ?: 0.0f,clientInform,currency))
                } else {
                    mContext?.getString(R.string.discountprice_search_multiple, AppConstants.CURRENCY_SYMBOL,
                            Utils.getPriceFormat(offerItem?.netPrice ?: 0.0f,clientInform,currency), displayTime)
                }

                offerItem?.fixed_price = offerItem?.netPrice.toString()

                if (offerItem?.netPrice != offerItem?.netDiscount && offerItem?.netDiscount ?: 0.0f > 0) {
                    offerItem?.display_price = offerItem?.netDiscount.toString()
                }
            }
            if (offerItem?.fixed_price != offerItem?.display_price && offerItem?.netDiscount ?: 0.0f > 0) {
                tvActualPrice.visibility = View.VISIBLE
                tvActualPrice.paintFlags = tvDiscountPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                tvActualPrice.text = mContext?.getString(R.string.discountprice_tag, AppConstants.CURRENCY_SYMBOL,
                        Utils.getPriceFormat(offerItem?.display_price?.toFloatOrNull()?:0f,clientInform,currency))
            } else {
                tvActualPrice.visibility = View.GONE
            }


            if (offerItem?.is_product == 0 && offerItem.price_type == 1) {
                calendar[Calendar.HOUR_OF_DAY] = 0
                calendar[Calendar.MINUTE] = 0
                calendar[Calendar.DAY_OF_MONTH] = 1
                calendar.add(Calendar.MINUTE, (offerItem.serviceDuration?.times(offerItem.prod_quantity?.toInt()
                        ?: 0)) ?: 0)
                displayTime = (if (calendar[Calendar.DAY_OF_MONTH] - 1 > 0) mContext?.getString(R.string.day_tag, calendar[Calendar.DAY_OF_MONTH] - 1) else "") + " " + (if (calendar[Calendar.HOUR_OF_DAY] > 0) mContext?.getString(R.string.hour_tag, calendar[Calendar.HOUR_OF_DAY]) else "") + " " +
                        if (calendar[Calendar.MINUTE] > 0) mContext?.getString(R.string.minute_tag, calendar[Calendar.MINUTE]) else ""
                // if (productData.price_type == 1) {
                tvQuant.text = if (offerItem.prod_quantity == 0f) "00:00" else displayTime
                /*  } else {
                      holder.tvQuant.text = mContext?.getString(R.string.hourly_tag, productData.prod_quantity)
                  }*/
            } else {
                tvQuant.text = Utils.getDecimalPointValue(clientInform, offerItem?.prod_quantity)
            }



            if (offerItem?.adds_on != null && offerItem.adds_on?.isNotEmpty() == true && clientInform?.app_selected_theme!="3") {
                tv_type_custmize.visibility = View.VISIBLE
            } else {
                tv_type_custmize.visibility = View.INVISIBLE
            }

            if (screenType > AppDataType.Custom.type) {
                groupPrice.visibility = View.GONE
                tv_percentage_price.visibility = View.VISIBLE

                //real=offerItem?.display_price
                // disc= offerItem?.netprice

                val perncentageAmt = (offerItem?.display_price?.toFloat()?.minus(offerItem.netPrice
                        ?: 0.0f)
                        ?.div(offerItem.display_price?.toFloat() ?: 0.0f))?.times(100f)

                tv_percentage_price.text = itemView.context.getString(R.string.percentage_tag, Utils.getPriceFormat(perncentageAmt
                        ?: 0.0f, clientInform,currency))

            }else if((isRecommendedType==true && clientInform?.app_selected_theme=="3") || offerItem?.isSupplier==true)
                groupPrice.visibility = View.GONE
            else {
                groupPrice.visibility = View.VISIBLE
                tv_percentage_price.visibility = View.GONE
            }


            clientInform?.zipDesc?.let {
                if (it == "1") {
                    tvDescProduct.visibility = View.VISIBLE
                }
            }




            ivWishlist.isChecked = offerItem?.is_favourite == 1


            //tvActualPrice.setVisibility(View.GONE);
            // if (offerItem?.avg_rating != 0f) {
            rbRating.rating = offerItem?.avg_rating ?: 0f
            //  }


            if (screenType == AppDataType.Ecom.type && offerItem?.is_variant == 1) {
                groupCart.visibility = View.INVISIBLE
                //  qty_text.visibility = View.INVISIBLE
            }

            when {
                offerItem?.isSupplier==true->{
                    groupCart.visibility = View.INVISIBLE
                    ivPlus.visibility = View.INVISIBLE
                    tvStock.visibility = View.INVISIBLE
                    tv_type_custmize.visibility = View.INVISIBLE
                }
                offerItem?.purchased_quantity ?: 0f >= offerItem?.quantity ?: 0f ||  offerItem?.item_unavailable=="1"-> {
                    ivProduct.setGreyScale()
                    groupCart.visibility = View.INVISIBLE
                    ivPlus.visibility = View.INVISIBLE
                    tvStock.visibility = View.VISIBLE
                    tv_type_custmize.visibility = View.INVISIBLE
                    // qty_text.visibility = View.INVISIBLE
                }
                clientInform?.app_selected_theme!="3" -> {
                    ivProduct.setColorScale()
                    tvStock.visibility = View.GONE
                    ivPlus.visibility = View.VISIBLE
                    groupCart.visibility = View.VISIBLE
                }
                else -> {
                    ivProduct.scaleType=ImageView.ScaleType.CENTER_CROP
                    tvStock.visibility = View.GONE
                    ivPlus.visibility = View.INVISIBLE
                    groupCart.visibility = View.INVISIBLE
                    //qty_text.visibility = View.VISIBLE
                }
            }

            ivWishlist.visibility = View.INVISIBLE

            when (screenType) {
                AppDataType.Food.type -> {
                    groupRating.visibility = View.GONE
                    tvFoodReview.text = df.format(offerItem?.avg_rating ?: 0f)
                }
                AppDataType.Ecom.type -> {
                    tvFoodReview.visibility = View.INVISIBLE
                    if (clientInform?.is_product_wishlist == "1")
                        ivWishlist.visibility = View.VISIBLE
                    tv_type_custmize.visibility = View.INVISIBLE
                    // qty_text.visibility = View.GONE
                    groupCart.visibility = View.INVISIBLE
                    ivPlus.visibility = View.INVISIBLE
                }
                AppDataType.HomeServ.type -> {
                    if (clientInform?.is_product_wishlist == "1")
                        ivWishlist.visibility = View.VISIBLE
                    groupRating.visibility = View.GONE
                    tvFoodReview.text = df.format(offerItem?.avg_rating ?: 0f)
                    groupPrice.visibility = if (clientInform?.enable_freelancer_flow == "1") View.GONE else View.VISIBLE
                }
                else -> {
                    groupRating.visibility = View.GONE
                    tvFoodReview.text = mContext?.getString(R.string.reviews_text, offerItem?.avg_rating)
                }
            }

            tvFoodReview.visibility = if (clientInform?.is_product_rating == "1" && screenType != AppDataType.Ecom.type) View.VISIBLE else View.GONE
            if (screenType != AppDataType.Food.type || clientInform?.app_selected_theme=="3" || clientInform?.app_selected_theme=="1") {
                itemView.setOnClickListener {
                    mCallback?.onProductDetail(offerItem)
                }
            }

            if (clientInform?.show_ecom_v2_theme == "1" && getItemViewType(adapterPosition) == SERVICE_OFFER) {
                val layoutParams = itemLayout.layoutParams as RecyclerView.LayoutParams
                layoutParams.width = RecyclerView.LayoutParams.MATCH_PARENT
                layoutParams.setMargins(StaticFunction.pxFromDp(8, itemView.context), 0, StaticFunction.pxFromDp(8, itemView.context), StaticFunction.pxFromDp(8, itemView.context))
                itemLayout.layoutParams = layoutParams


                val layoutParams2 = ivProduct.layoutParams as ConstraintLayout.LayoutParams
                layoutParams2.width = ConstraintLayout.LayoutParams.MATCH_PARENT
                ivProduct.layoutParams = layoutParams2

                groupCart.visibility = View.GONE
                ivPlus.visibility = View.GONE

            }

            ivSelect.visibility = if (clientInform?.is_compare_products == "1" && isCompareProducts == true) View.VISIBLE else View.GONE

            ivSelect.setImageResource(if (offerItem?.isSelected == true) {
                R.drawable.ic_radio_active
            } else
                R.drawable.ic_radio_unactive)

            tvSpecialOffer.visibility=if(offerItem?.isSupplier==true && (clientInform?.app_selected_theme=="3" || clientInform?.app_selected_theme=="1"))
                View.VISIBLE else View.GONE
            tvSpecialOffer.text=mContext?.getString(R.string.off,offerItem?.offerValue.toString())

            if(offerItem?.isSupplier==true && (clientInform?.app_selected_theme=="3" || clientInform?.app_selected_theme=="1")){
                if (clientInform.app_selected_theme == "3" && clientInform.is_table_booking == "1") {
                    if (offerItem.is_dine_in == 1)
                       tvBookNow.visibility = View.VISIBLE
                    else
                        tvBookNow.visibility = View.GONE

                    if (appUtils.getCurrentTableData() != null)
                        tvBookNow.visibility = View.GONE

                    tvBookNow.setOnClickListener {
                        mCallback?.onBookNow(offerItem)
                    }

                }
            }else  tvBookNow.visibility=View.GONE

            ivAllergies.visibility=if(clientInform?.enable_product_allergy=="1" && offerItem?.is_allergy_product=="1") View.VISIBLE else View.GONE
        }

        private fun updateItem(productItem: ProductDataBean?) {

            when (binding) {
                is ItemSpecialOfferBinding -> {
                    binding.productItem = productItem
                }

                is ItemSpecialOfferListBinding -> {
                    binding.productItem = productItem
                }

            }
        }

        override fun onClick(view: View) {
            when (view.id) {
                R.id.ivSelect -> {
                    offerList?.get(adapterPosition)?.isSelected = !(offerList?.get(adapterPosition)?.isSelected
                            ?: false)
                    notifyItemChanged(adapterPosition)
                }
                R.id.tv_minus -> {
                    //offerList?.get(adapterPosition)?.discount = 1
                    mCallback?.removeToCart(adapterPosition, offerList?.get(adapterPosition))
                }
                R.id.tv_plus -> {
                    offerList?.get(adapterPosition)?.discount = 1
                    mCallback?.addToCart(adapterPosition, offerList?.get(adapterPosition))
                }
                R.id.iv_wishlist -> {
                    if (clientInform?.is_product_wishlist == "1" && (screenType == AppDataType.HomeServ.type || screenType == AppDataType.Ecom.type)) {

                        ivWishlist.setOnCheckedChangeListener { checkBox, isChecked ->
                            if (isChecked) {
                                mCallback?.addtoWishList(adapterPosition, 1, offerList?.get(adapterPosition)?.product_id)
                            } else {
                                mCallback?.addtoWishList(adapterPosition, 0, offerList?.get(adapterPosition)?.product_id)
                            }
                        }
                    }
                }
                R.id.tvBookNow -> {
                    mCallback?.onBookNow(offerList?.get(adapterPosition))
                }
                R.id.tv_desc_product -> {
                    if(offerList?.get(adapterPosition)?.isSupplier!=true)
                      mCallback?.onProdDesc(offerList?.get(adapterPosition)?.product_desc ?: "")
                }
                R.id.ivAllergies->{
                    mCallback?.onProdAllergies(offerList?.get(adapterPosition))
                }

            }
        }

        init {
            //  qty_text = itemView.findViewById(R.id.qty_text)
            ivMinus.setOnClickListener(this)
            ivPlus.setOnClickListener(this)
            ivWishlist.setOnClickListener(this)
            tvDescProduct.setOnClickListener(this)
            tvBookNow.setOnClickListener(this)
            ivSelect.setOnClickListener(this)
            ivAllergies.setOnClickListener(this)
            this.viewType = viewType
        }
    }

    fun getSelectedItems(): ArrayList<ProductDataBean?> {
        return ArrayList(offerList?.filter { it?.isSelected == true } ?: emptyList())
    }

    private fun checkFavouriteImage(tbFavourite: ImageView): Boolean {
        return CommonUtils.areDrawablesIdentical(tbFavourite.drawable, mContext?.let { ContextCompat.getDrawable(it, R.drawable.ic_unfavorite) }!!)

    }

}
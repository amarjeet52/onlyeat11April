package com.codebrew.clikat.module.cart.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppToasty
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.setColorScale
import com.codebrew.clikat.app_utils.extension.setGreyScale
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.VendorAppType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.AppConstants.Companion.CURRENCY_SYMBOL
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.databinding.ItemCartNewBinding
import com.codebrew.clikat.databinding.ItemCartsBinding
import com.codebrew.clikat.databinding.ItemCartsV2Binding
import com.codebrew.clikat.modal.CartInfo
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.StaticFunction.loadImage
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.item_carts.view.*
import kotlinx.android.synthetic.main.item_carts.view.tv_desc_product
import kotlinx.android.synthetic.main.item_carts.view.tv_food_rating
import kotlinx.android.synthetic.main.item_carts.view.tv_total_prod
import kotlinx.android.synthetic.main.item_supplier_product.view.*
import java.text.DecimalFormat
import java.util.*

/*
 * Created by Ankit Jindal on 19/4/16.
 */
class CartAdapter(private val mContext: Context, private val list: MutableList<CartInfo>,
                  private val screenFlow: SettingModel.DataBean.ScreenFlowBean?,
                  private val appUtils: AppUtils,
                  private val settingData: SettingModel.DataBean.SettingData?,
                  private val selectedCurrency: Currency?) : Adapter<ViewHolder>() {
    var edAdditionalRemarks: EditText? = null
    private var mCallback: CartCallback? = null

    //    float charges = 0;
    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }
    private val calendar = Calendar.getInstance()
    private var displayTime: String? = null
    fun settingCallback(mCallback: CartCallback?) {
        this.mCallback = mCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
                if (settingData?.show_ecom_v2_theme == "1") {
                    DataBindingUtil.inflate(LayoutInflater.from(
                            parent.context), R.layout.item_carts_v2, parent, false) as ItemCartsV2Binding
                } else {
                    DataBindingUtil.inflate(LayoutInflater.from(
                            parent.context), R.layout.item_carts, parent, false) as ItemCartsBinding
                }

        when (binding) {
            is ItemCartNewBinding -> {
                binding.color = Configurations.colors
                binding.drawables = Configurations.drawables
                binding.strings = appUtils.loadAppConfig(0).strings
                binding.isWeightVisible = settingData?.is_product_weight == "1"
                binding.singleVndorType = screenFlow?.is_single_vendor == VendorAppType.Single.appType
            }
            is ItemCartsBinding -> {
                binding.color = Configurations.colors
                binding.drawables = Configurations.drawables
                binding.strings = appUtils.loadAppConfig(0).strings
                binding.isWeightVisible = settingData?.is_product_weight == "1"
                binding.singleVndorType = screenFlow?.is_single_vendor == VendorAppType.Single.appType
            }

            is ItemCartsV2Binding -> {
                binding.color = Configurations.colors
                binding.drawables = Configurations.drawables
                binding.strings = appUtils.loadAppConfig(0).strings
                binding.isWeightVisible = settingData?.is_product_weight == "1"
                binding.singleVndorType = screenFlow?.is_single_vendor == VendorAppType.Single.appType
            }
        }

        return HeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (holder is HeaderViewHolder) {

            calendar.clear()
            val mProduct = list[position]

            displayTime = ""

            holder.tvName.text = mProduct.productName

            holder.updateItem(mProduct)

            holder.tvProcductDesc.text = Utils.getHtmlData(mProduct.productDesc ?: "")
            //  holder.tvMeasurementQuantity.setText(list.get(position).getMeasuringUnit());
            if (mProduct.serviceType == 0) {
                mProduct.serviceDurationSum = mProduct.serviceDuration * mProduct.quantity
            }

            holder.tvSupplierName.text = mContext.getString(R.string.supplier_tag, mProduct.supplierName)

            if (mProduct.serviceType == 0) {
                holder.cartAction.visibility = View.VISIBLE
            }
            else {
                holder.cartAction.visibility = if (mProduct.agentType == 1) View.INVISIBLE else View.VISIBLE
            }



                holder.tvSpecialInstruction.visibility = View.GONE

//                holder.tvSpecialInstruction.pushDownClickListener {
//                    mCallback?.onViewProductSpecialInstruction(position)
//                }

                if (mProduct.productSpecialInstructions.isNullOrEmpty()) {
                    holder.tvSpecialInstruction.text = mContext.getString(R.string.add_instructions_)
                } else
                    holder.tvSpecialInstruction.text = mContext.getString(R.string.edit_instructions)


            holder.cartAction.visibility = if (mProduct.isQuant == 1) View.VISIBLE else View.GONE
            holder.cartAction.visibility = if (mProduct.isQuant == 1)
                View.VISIBLE
            else
                View.GONE

            if (mProduct.imagePath.toString().isEmpty())
                holder.sdvImage.visibility = View.GONE
            else {
                holder.sdvImage.visibility = View.VISIBLE
                loadImage(mProduct.imagePath, holder.sdvImage, false)
            }
            holder.tvAgentType.visibility = if (mProduct.agentType != 0) View.VISIBLE else View.GONE
            holder.tvAgentType.text = if (mProduct.agentType == 0) mContext.getString(R.string.agent_not_available, textConfig?.agent) else
                mContext.getString(R.string.agent_available, textConfig?.agent)
            holder.tvCount.isEnabled = settingData?.is_decimal_quantity_allowed == "1"

            if (mProduct.serviceType == 0) {
                //price_type 1 for hourly 0 for fixed price
                if (mProduct.hourlyPrice.isNotEmpty()) {
                    calendar.add(Calendar.MINUTE, mProduct.serviceDuration)
                    displayTime = (if (calendar[Calendar.HOUR] > 0) mContext.getString(R.string.hour_tag, calendar[Calendar.HOUR]) else "") + " " +
                            if (calendar[Calendar.MINUTE] > 0) mContext.getString(R.string.minute_tag, calendar[Calendar.MINUTE]) else ""
                    if (mProduct.discount != 0) {
                        holder.tvDiscountPrice.visibility=View.VISIBLE
                        holder.tvDiscountPrice.paintFlags =    holder.tvDiscountPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        holder.tvDiscountPrice.text = mContext.getString(R.string.discountprice_search_multiple, CURRENCY_SYMBOL, Utils.getPriceFormat(mProduct.display_price, settingData, selectedCurrency), displayTime)

                        holder.tvPrice.text = mContext.getString(R.string.discountprice_search_multiple, CURRENCY_SYMBOL, Utils.getPriceFormat(mProduct.price, settingData, selectedCurrency), displayTime)

                    } else{
                        holder.tvDiscountPrice.visibility=View.GONE
                        holder.tvPrice.text = mContext.getString(R.string.discountprice_search_multiple, CURRENCY_SYMBOL, Utils.getPriceFormat(mProduct.price, settingData, selectedCurrency), displayTime)
                }
                } else {
                    if (mProduct.discount !=0) {
                        holder.tvDiscountPrice.visibility = View.VISIBLE
                        holder.tvDiscountPrice.paintFlags = holder.tvDiscountPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        holder.tvDiscountPrice.text = mContext.getString(R.string.discountprice_tag, CURRENCY_SYMBOL, Utils.getPriceFormat(mProduct.display_price, settingData, selectedCurrency))

                        holder.tvPrice.text = mContext.getString(R.string.discountprice_tag, CURRENCY_SYMBOL, Utils.getPriceFormat(mProduct.price, settingData, selectedCurrency))

                    } else {
                        holder.tvDiscountPrice.visibility = View.GONE
                        holder.tvPrice.text = mContext.getString(R.string.discountprice_tag, CURRENCY_SYMBOL, Utils.getPriceFormat(mProduct.price, settingData, selectedCurrency))
                    }
                }
            } else { //service

                calendar.add(Calendar.MINUTE, mProduct.serviceDuration)
                displayTime = (if (calendar[Calendar.HOUR] > 0) mContext.getString(R.string.hour_tag, calendar[Calendar.HOUR]) else "") + " " +
                        if (calendar[Calendar.MINUTE] > 0) mContext.getString(R.string.minute_tag, calendar[Calendar.MINUTE]) else ""
                if (mProduct.discount !=0) {
                    holder.tvDiscountPrice.visibility = View.VISIBLE
                    holder.tvDiscountPrice.paintFlags = holder.tvDiscountPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                    holder.tvDiscountPrice.text = if (displayTime?.trim()?.isEmpty() == true) mContext.getString(R.string.discountprice_tag, CURRENCY_SYMBOL,
                            Utils.getPriceFormat(mProduct.display_price, settingData, selectedCurrency))
                    else mContext.getString(R.string.discountprice_search_multiple, CURRENCY_SYMBOL,
                            Utils.getPriceFormat(mProduct.display_price, settingData, selectedCurrency), displayTime)

                    holder.tvPrice.text = if (displayTime?.trim()?.isEmpty() == true) mContext.getString(R.string.discountprice_tag, CURRENCY_SYMBOL,
                            Utils.getPriceFormat(mProduct.price, settingData, selectedCurrency))
                    else mContext.getString(R.string.discountprice_search_multiple, CURRENCY_SYMBOL,
                            Utils.getPriceFormat(mProduct.price, settingData, selectedCurrency), displayTime)

                } else {
                    holder.tvDiscountPrice.visibility = View.GONE
                    holder.tvPrice.text = if (displayTime?.trim()?.isEmpty() == true) mContext.getString(R.string.discountprice_tag, CURRENCY_SYMBOL,
                            Utils.getPriceFormat(mProduct.price, settingData, selectedCurrency))
                    else mContext.getString(R.string.discountprice_search_multiple, CURRENCY_SYMBOL,
                            Utils.getPriceFormat(mProduct.price, settingData, selectedCurrency), displayTime)
                }
            }

            if (mProduct.serviceType == 0 && mProduct.priceType == 1) {
                calendar[Calendar.HOUR_OF_DAY] = 0
                calendar[Calendar.MINUTE] = 0
                calendar[Calendar.DAY_OF_MONTH] = 1
                calendar.add(Calendar.MINUTE, mProduct.serviceDurationSum.toInt())
                displayTime = (if (calendar[Calendar.DAY_OF_MONTH] - 1 > 0) mContext.getString(R.string.day_tag, calendar[Calendar.DAY_OF_MONTH] - 1) else "") + " " + (if (calendar[Calendar.HOUR_OF_DAY] > 0) mContext.getString(R.string.hour_tag, calendar[Calendar.HOUR_OF_DAY]) else "") + " " +
                        if (calendar[Calendar.MINUTE] > 0) mContext.getString(R.string.minute_tag, calendar[Calendar.MINUTE]) else ""
                // if (mProduct?.hourlyPrice.isNotEmpty()) {
                holder.tvCount.setText(if (mProduct.quantity == 0f) "00:00" else displayTime)
                /* } else {
                     holder.tvCount.text = mContext?.getString(R.string.hourly_tag, mProduct.quantity)
                 }*/
            } else {

                val quantity = if (settingData?.is_decimal_quantity_allowed == "1")
                    mProduct.quantity.toString()
                else (mProduct.quantity.toInt()).toString()

                holder.tvCount.setText(quantity)

            }
            holder.rbRating.rating = mProduct.avgRating ?: 0f

            if (mProduct.add_ons != null && mProduct.add_ons?.isNotEmpty() == true) {
                holder.addonName.text = mContext.getString(R.string.addon_name_tag, mProduct.add_on_name)
                holder.addonName.visibility = View.VISIBLE
            } else {
                holder.addonName.visibility = View.GONE
            }

             holder.foodRating.text = mProduct.avgRating.toString()

            if (screenFlow?.app_type == AppDataType.Food.type) {
                holder.reviewGroup.visibility = View.GONE
                //  holder.foodRating.visibility = View.VISIBLE
            } else {
                holder.reviewGroup.visibility = View.GONE
                //  holder.foodRating.visibility = View.GONE
            }

            //Out of Stock
            if (mProduct.isStock == true) {
                holder.sdvImage.setColorScale()
                /* no need to show  plus minus in case of out network products*/
                holder.cartAction.visibility = if (mProduct.quantity == 1f && !mProduct.product_owner_name.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
            } else {
                holder.sdvImage.setGreyScale()
                holder.cartAction.visibility = View.INVISIBLE
            }

            if (mProduct.varients?.isNotEmpty() == true) {
                val adapter = VarientItemAdapter()

                holder.rvVarientlist.adapter = adapter
                adapter.submitItemList(mProduct.varients)
            }

            holder.groupOutNetwork.visibility = if (!mProduct.product_owner_name.isNullOrEmpty()) View.VISIBLE else View.GONE
            holder.tvOwnerName.text = mContext.getString(R.string.owner_name, mProduct.product_owner_name)
            holder.tvReferenceId.text = mContext.getString(R.string.reference_id_show, mProduct.product_reference_id)
            holder.tvDimensions.text = mContext.getString(R.string.dimensions_show, mProduct.product_dimensions)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class HeaderViewHolder(private val binding: ViewDataBinding) : ViewHolder(binding.root) {

        fun updateItem(productItem: CartInfo) {

            when (binding) {
                is ItemCartsBinding -> {
                    binding.cartItem = productItem
                }

                is ItemCartsV2Binding -> {
                    binding.cartItem = productItem
                }
            }
        }

        internal var tvName: TextView = itemView.tvName
        internal var tvProcductDesc: TextView = itemView.tv_desc_product
        internal var tvCount: TextView = itemView.tvCounts
        internal var sdvImage: RoundedImageView = itemView.sdvImage
        internal var ivPlus: ImageView = itemView.ivPlus
        internal var ivMinus: ImageView = itemView.ivMinus
        internal var tvPrice: TextView = itemView.tv_total_prod
        internal var tvDiscountPrice=itemView.tv_price_discount
        internal var tvSupplierName: TextView = itemView.tv_supplier_name
        internal var tvSpecialInstruction: TextView = itemView.tvSpecialInstruction
        internal var ivDelete: TextView = itemView.iv_delete
        internal var tvAgentType: TextView = itemView.tv_agentType
        internal var cartAction: Group = itemView.cart_action
        internal var reviewGroup: Group = itemView.group_review
        internal var addonName: TextView = itemView.tv_addon_name
        internal var foodRating: TextView = itemView.tv_food_rating
        internal var rvVarientlist: RecyclerView = itemView.rv_varient_list
        internal var rbRating: RatingBar = itemView.rb_rating
        internal var groupOutNetwork: Group = itemView.groupOutNetwork
        internal var tvDimensions: TextView = itemView.tvDimensions
        internal var tvOwnerName: TextView = itemView.tvOwnerName
        internal var tvReferenceId: TextView = itemView.tvReferenceId
        internal var tvViewReceipt: TextView = itemView.tvViewReceipt

        init {
            ivPlus.setOnClickListener { v: View? -> mCallback?.addItem(adapterPosition) }
            ivMinus.setOnClickListener { v: View? -> mCallback?.removeItem(adapterPosition) }
            ivDelete.setOnClickListener { view: View? -> mCallback?.onDeleteCart(adapterPosition) }
            tvProcductDesc.setOnClickListener { v: View? -> mCallback?.onClickProdDesc(adapterPosition) }
            tvCount.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val decimalFormat = DecimalFormat("0.00")
                    val updatedQuantity = decimalFormat.format(tvCount.text.toString().trim().toFloat()).toFloat()
                    if (settingData?.is_decimal_quantity_allowed == "1" && settingData.is_decimal_fixed_interval == "1") {
                        val rem = (updatedQuantity.times(100).toInt()).rem(AppConstants.DECIMAL_INTERVAL.times(100).toInt())
                        if (updatedQuantity > AppConstants.DECIMAL_INTERVAL && rem == 0) {
                            mCallback?.onEditQuantity(updatedQuantity, adapterPosition)
                        } else
                            AppToasty.error(mContext, mContext.getString(R.string.quantity_shoul_be_multiple))
                    } else {
                        mCallback?.onEditQuantity(updatedQuantity, adapterPosition)
                    }
                    true
                } else false
            }
            tvViewReceipt.setOnClickListener {
                if (!list[adapterPosition].product_upload_reciept.isNullOrEmpty())
                    StaticFunction.openCustomChrome(mContext, list[adapterPosition].product_upload_reciept
                            ?: "")
            }
        }
    }

    fun getList(): MutableList<CartInfo> {
        return list
    }

    interface CartCallback {
        fun onDeleteCart(position: Int)
        fun onViewProductSpecialInstruction(position: Int)
        fun addItem(position: Int)
        fun removeItem(position: Int)
        fun onClickProdDesc(position: Int)
        fun onEditQuantity(updatedQuantity: Float?, position: Int)
    }
}
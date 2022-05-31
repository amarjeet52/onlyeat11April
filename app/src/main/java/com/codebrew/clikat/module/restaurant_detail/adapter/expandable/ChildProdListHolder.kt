package com.codebrew.clikat.module.restaurant_detail.adapter.expandable

import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.text.HtmlCompat
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppToasty
import com.codebrew.clikat.app_utils.extension.setColorScale
import com.codebrew.clikat.app_utils.extension.setGreyScale
import com.codebrew.clikat.app_utils.extension.setSafeOnClickListener
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.databinding.ItemSupplierProductBinding
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.restaurant_detail.adapter.ProdListAdapter
import com.codebrew.clikat.utils.StaticFunction.loadImage
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.ColorConfig
import com.codebrew.clikat.utils.configurations.Configurations
import com.codebrew.clikat.utils.configurations.TextConfig
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import kotlinx.android.synthetic.main.item_supplier_product.view.*
import java.text.DecimalFormat


class ChildProdListHolder  constructor(val binding: ItemSupplierProductBinding) :
        ChildViewHolder(binding.root) {

    fun setArtistName(bean: ProductDataBean, isOpen: Boolean?=null, settingBean: SettingModel.DataBean.SettingData?=null,
               mCallback: ProdListAdapter.ProdCallback?=null, parentPosition: Int?=null, colorConfig: ColorConfig?=null, textConfig: TextConfig?=null, selectedCurrency: Currency?=null) {
        val mView = binding.root

        binding.productItem = bean
        binding.color = Configurations.colors
        binding.strings = textConfig
        binding.theme = if (settingBean?.is_hunger_app == "1" || settingBean?.zipTheme == "1") 1 else 0
        binding.isRatingVisible = settingBean?.is_product_rating == "1"
        binding.isWeightVisible = settingBean?.is_product_weight == "1"

        mView.iv_prod.setOnClickListener { mCallback?.onProdDetail(bean) }

        if (bean.image_path.toString().isEmpty() && settingBean?.zipTheme == "1")
            mView.iv_prod.visibility = View.GONE
        else {
            mView.iv_prod.visibility = View.VISIBLE
            //mView.iv_prod.loadImage(bean.image_path.toString(),300,250)
            loadImage(url = bean.image_path.toString(), roundedShape = true, imageView = mView.iv_prod, imageHeight = 100, imageWidth = 200)
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
                        ?: 0.0f, settingBean, selectedCurrency))
        if (bean.fixed_price?.toFloatOrNull() ?: 0f != bean.display_price?.toFloatOrNull() ?: 0f) {
            itemView.tvActualPrice.visibility = View.VISIBLE
            itemView.tvActualPrice.paintFlags = mView.tv_total_prod.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            itemView.tvActualPrice.text = mView.context?.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL,
                    Utils.getPriceFormat(bean.display_price?.toFloatOrNull()
                            ?: 0.0f, settingBean, selectedCurrency))
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
        } else {
            itemView.tv_type_custmize.visibility = View.GONE
        }
        mView.tv_type_custmize.setOnClickListener {
            mCallback?.onProdAdded(bean, parentPosition?:0, adapterPosition, !(isOpen ?:false))
        }
        //Out of Stock
        if (bean.purchased_quantity ?: 0f >= bean.quantity ?: 0f || bean.quantity == 0f || bean.item_unavailable == "1") {
            mView.iv_prod.setGreyScale()
            mView.stock_label.visibility = View.VISIBLE
            mView.actionGroup.visibility = View.INVISIBLE
            mView.iv_increment.visibility = View.INVISIBLE
            itemView.tvViewDetail.setTextColor(Color.parseColor(colorConfig?.textHead))
            itemView.tv_type_custmize.visibility = View.GONE
        } else {
            mView.iv_prod.setColorScale()
            mView.stock_label.visibility = View.GONE
            mView.iv_increment.visibility = if (bean.is_out_network == 1 && bean.prod_quantity == 1f) View.GONE else View.VISIBLE
           // itemView.tvViewDetail.setTextColor(Color.parseColor(colorConfig?.primaryColor))
            mView.actionGroup.visibility = if (bean.prod_quantity ?: 0f > 0f) View.VISIBLE else View.INVISIBLE
        }

       /* if (isOpen != true) {
            mView.iv_increment.setGreyScale()
            mView.iv_decrement.setGreyScale()
        }*/

        mView.tvSubscribed.visibility = if (bean.is_subscription_required == 1) View.VISIBLE else View.GONE
        mView.tv_food_rating.text = bean.avg_rating.toString()

        mView.tv_desc.text = HtmlCompat.fromHtml(bean.product_desc.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        mView.tv_quant.isEnabled = settingBean?.is_decimal_quantity_allowed == "1"


        mView.iv_increment.setOnClickListener {
            mCallback?.onProdAdded(bean, parentPosition?:0, adapterPosition, !(isOpen?:false))
        }
        mView.iv_decrement.setOnClickListener { mCallback?.onProdDelete(bean, parentPosition?:0, adapterPosition, !(isOpen?:false)) }
        mView.tv_desc_product.setOnClickListener {
            mCallback?.onProdDesc(bean.product_desc ?: "")
        }
        mView.tv_desc.setOnClickListener {
            mCallback?.onDescExpand(mView.tv_desc, bean, parentPosition?:0)
        }

        mView.tv_quant.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val decimalFormat = DecimalFormat("0.00")
                val updatedQuantity = decimalFormat.format(mView.tv_quant.text.toString().trim().toFloat()).toFloat()
                if (settingBean?.is_decimal_quantity_allowed == "1" && settingBean.is_decimal_fixed_interval == "1") {
                    val rem = (updatedQuantity.times(100).toInt()).rem(AppConstants.DECIMAL_INTERVAL.times(100).toInt())
                    if (rem == 0) {
                        mView.tv_quant.isCursorVisible = false
                        mCallback?.onEditQuantity(updatedQuantity, bean, parentPosition?:0, adapterPosition, !(isOpen?:false))
                    } else
                        mView.context?.let { AppToasty.error(it, mView.context?.getString(R.string.quantity_shoul_be_multiple).toString()) }
                } else {
                    mView.tv_quant.isCursorVisible = false
                    mCallback?.onEditQuantity(updatedQuantity, bean, parentPosition?:0, adapterPosition, !(isOpen?:false))
                }
                true
            } else false
        }
    }


}
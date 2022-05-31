package com.codebrew.clikat.module.product_detail.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.codebrew.clikat.R
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.databinding.ItemCompareProductsBinding
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.product_detail.adapter.CompareProductsAdapter.ViewHolder
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.material.button.MaterialButton
import java.util.*

class CompareProductsAdapter(private val products: ArrayList<ProductDataBean?>?, private val settingBean: SettingModel.DataBean.SettingData?,
                             private val selectedCurrency:Currency?) : Adapter<ViewHolder>() {
    private var context: Context? = null
    private var mCallback: OnProductDetail? = null
    internal fun settingCallback(mCallback: OnProductDetail?) {
        this.mCallback = mCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding: ItemCompareProductsBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_compare_products, parent, false)
        binding.color = Configurations.colors
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(products?.get(position))
    }

    override fun getItemCount(): Int {
        return products?.size ?: 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val calendar = Calendar.getInstance()
        private var tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        private var tvBrand: TextView = itemView.findViewById(R.id.tvBrand)
        var tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private var tvSoldBy: TextView = itemView.findViewById(R.id.tvSoldBy)
        var tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private var tvSpecifications: TextView = itemView.findViewById(R.id.tvSpecifications)
        private var ivProduct: ImageView = itemView.findViewById(R.id.ivProduct)
        val clMain: ConstraintLayout = itemView.findViewById(R.id.clMain)
        private val tvDiscountPrice: TextView = itemView.findViewById(R.id.tvDiscountPrice)
        private val btnViewDetail: MaterialButton = itemView.findViewById(R.id.btnViewDetail)

        fun onBind(productDataBean: ProductDataBean?) {
            if (adapterPosition != 0) {
                btnViewDetail.visibility=View.VISIBLE
                val displayTime: String
                clMain.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.white))
                tvProductName.text = productDataBean?.name
                tvBrand.text = if (!productDataBean?.brand_name.isNullOrEmpty()) productDataBean?.brand_name else "-"
                tvSoldBy.text = productDataBean?.supplier_name
                tvSpecifications.text = Utils.getHtmlData(productDataBean?.product_desc ?: "")
                tvCategory.text = productDataBean?.category_name
                StaticFunction.loadImage(productDataBean?.image_path.toString(), ivProduct, false)

                if (productDataBean?.is_product == 1) {
                    //price_type 1 for hourly 0 for fixed price
                    if (productDataBean.price_type == 1) {
                        calendar.add(Calendar.MINUTE, productDataBean.duration ?: 0)
                        displayTime = (if (calendar[Calendar.HOUR] > 0) context?.getString(R.string.hour_tag, calendar[Calendar.HOUR]) else "") + " " +
                                if (calendar[Calendar.MINUTE] > 0) context?.getString(R.string.minute_tag, calendar[Calendar.MINUTE]) else ""

                        tvDiscountPrice.text = context?.getString(R.string.discountprice_search_multiple, AppConstants.CURRENCY_SYMBOL,
                                Utils.getPriceFormat(productDataBean.netPrice ?: 0.0f,settingBean,selectedCurrency), displayTime)
                    } else {
                        tvDiscountPrice.text = context?.getString(R.string.discountprice_tag, AppConstants.CURRENCY_SYMBOL,
                                Utils.getPriceFormat(productDataBean.fixed_price?.toFloatOrNull()
                                        ?: 0.0f,settingBean,selectedCurrency))
                    }
                } else { //service

                    calendar.add(Calendar.MINUTE, productDataBean?.duration ?: 0)
                    displayTime = (if (calendar[Calendar.HOUR] > 0) context?.getString(R.string.hour_tag, calendar[Calendar.HOUR]) else "") + " " +
                            if (calendar[Calendar.MINUTE] > 0) context?.getString(R.string.minute_tag, calendar[Calendar.MINUTE]) else ""
                    //if (productData.price_type == 1) {

                    tvDiscountPrice.text = if (productDataBean?.price_type == 0) {
                        context?.getString(R.string.discountprice_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(productDataBean.netPrice
                                ?: 0.0f,settingBean,selectedCurrency))
                    } else {
                        context?.getString(R.string.discountprice_search_multiple, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(productDataBean?.netPrice
                                ?: 0.0f,settingBean,selectedCurrency), displayTime)
                    }


                    productDataBean?.fixed_price = productDataBean?.netPrice.toString()

                    if (productDataBean?.netPrice != productDataBean?.netDiscount && productDataBean?.netDiscount ?: 0.0f > 0) {
                        productDataBean?.display_price = productDataBean?.netDiscount.toString()
                    }

                }

                if (productDataBean?.fixed_price != productDataBean?.display_price && productDataBean?.netDiscount ?: 0.0f > 0) {
                    tvPrice.visibility = View.VISIBLE
                    tvPrice.paintFlags = tvDiscountPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    tvPrice.text = context?.getString(R.string.discountprice_tag, AppConstants.CURRENCY_SYMBOL,
                            Utils.getPriceFormat(productDataBean?.display_price?.toFloatOrNull()
                                    ?: 0.0f,settingBean,selectedCurrency))
                } else {
                    tvPrice.visibility = View.GONE
                }


            } else {
                btnViewDetail.visibility=View.GONE
                clMain.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.blackF7))
                tvProductName.text = context?.getString(R.string.product_name)
                tvBrand.text = context?.getString(R.string.brand_)
                tvSoldBy.text = context?.getString(R.string.sold_by)
                tvSpecifications.text = context?.getString(R.string.specifications)
                tvCategory.text = context?.getString(R.string.category)
                Glide.with(itemView.context).load("").into(ivProduct)
                tvDiscountPrice.text = context?.getString(R.string.price)
                tvPrice.visibility = View.GONE
                btnViewDetail.visibility=View.INVISIBLE
            }

            btnViewDetail.setOnClickListener {
                mCallback?.onProductDetailCompareProduct(products?.get(adapterPosition))
            }
        }
    }

    interface OnProductDetail{
        fun onProductDetailCompareProduct(bean: ProductDataBean?)
    }
}
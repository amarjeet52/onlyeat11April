package com.codebrew.clikat.module.wishlist_prod.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.extension.loadImage
import com.codebrew.clikat.app_utils.extension.setColorScale
import com.codebrew.clikat.app_utils.extension.setGreyScale
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.databinding.ItemProductGridBinding
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.item_product_grid.view.*
import java.util.*

class WishlistAdapter(private val bookingFlowBean: SettingModel.DataBean.BookingFlowBean?,
                      private val screenFlowBean: SettingModel.DataBean.ScreenFlowBean?,
                        private val clientInfo: SettingModel.DataBean.SettingData?,private val selectedCurrency:Currency?) : RecyclerView.Adapter<ViewHolder>() {

    private var mContext: Context? = null

    private var items: MutableList<ProductDataBean>? = mutableListOf()

    private val calendar = Calendar.getInstance()

    private var mCallback: WishCallback? = null

    fun settingCallback(mCallback: WishCallback) {
        this.mCallback = mCallback
    }

    fun updateList(itemList: MutableList<ProductDataBean>?) {
        items?.clear()
        items?.addAll(itemList ?: emptyList())
        notifyDataSetChanged()
    }

    fun removedProduct(position: Int?) {
        position?.let { items?.removeAt(it) }
        notifyDataSetChanged()
    }

    fun getList(): MutableList<ProductDataBean>? {
        return items
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context


        val binding = DataBindingUtil.inflate<ItemProductGridBinding>(LayoutInflater.from(mContext), R.layout.item_product_grid, parent, false)
        binding.color = Configurations.colors
        binding.drawables = Configurations.drawables
        binding.strings = Configurations.strings
        return ViewHolder(binding.root)
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //onBind(items[adapterPosition])

        val data = items?.get(holder.adapterPosition)


        calendar.clear()

        var displayTime: String

        holder.tvName.text = data?.name

        holder.tvSupplierName.text = mContext?.getString(R.string.supplier_tag, data?.supplier_name)

        if (data?.is_product == 0) {
            holder.groupCart.visibility = View.VISIBLE
            holder.tvAddCart.visibility = View.GONE
        } else {
            holder.groupCart.visibility = if (data?.is_agent == 1 && data.agent_list == 1) View.GONE else View.VISIBLE
            holder.tvAddCart.visibility = if (data?.is_agent == 1 && data.agent_list == 1) View.VISIBLE else View.GONE
        }


        if (data?.is_variant == 1) {
            holder.groupCart.visibility = View.INVISIBLE
            holder.tvAddCart.visibility = View.INVISIBLE
        }

        holder.sdvImage.loadImage(data?.image_path as String)


        //product
        holder.tvPrice.text = mContext?.getString(R.string.discountprice_tag, AppConstants.CURRENCY_SYMBOL,
                Utils.getPriceFormat(data.fixed_price?.toFloatOrNull()?:0.0f,clientInfo,selectedCurrency))
        if (data.fixed_price != data.display_price) {
            holder.tvActualPrice.visibility = View.VISIBLE
            holder.tvActualPrice.paintFlags = holder.tvPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.tvActualPrice.text = mContext?.getString(R.string.discountprice_tag, AppConstants.CURRENCY_SYMBOL,
                    Utils.getPriceFormat(data.display_price?.toFloatOrNull()?:0.0f, clientInfo,selectedCurrency))
        } else {
            holder.tvActualPrice.visibility = View.GONE
        }



        holder.groupCart.visibility = View.GONE
        holder.tvAddCart.visibility = View.GONE
        holder.qtyText.visibility = View.GONE
        holder.tvCustomize.visibility = View.GONE

        if (screenFlowBean?.app_type == AppDataType.Food.type) {
            holder.rbProdRating.visibility = View.GONE
            //holder.tvRating.visibility = View.GONE
            holder.tvFoodReview.visibility = View.VISIBLE
            holder.tvFoodReview.text = data.avg_rating.toString()
        } else {
            holder.rbProdRating.visibility = View.VISIBLE
            // holder.tvRating.visibility = View.VISIBLE
            holder.tvFoodReview.visibility = View.GONE
        }

        holder.ivWishlist.isChecked=data.is_favourite == 1


        if (data.avg_rating?:0f > 0) {
            holder.rbProdRating.rating = data.avg_rating?:0f
            holder.tvRating.text = mContext?.getString(R.string.reviews_text, data.avg_rating)
        }

        if (data.purchased_quantity ?: 0f >= data.quantity ?: 0f || data.quantity == 0f||data.item_unavailable=="1") {
            holder.sdvImage.setGreyScale()
            holder.stockLabel.visibility = View.VISIBLE
        } else {
            holder.sdvImage.setColorScale()
            holder.stockLabel.visibility = View.GONE
        }



        holder.itemView.setOnClickListener { v ->
            mCallback?.productDetail(data)
        }

        holder.ivWishlist.setOnClickListener {
            mCallback?.removeProduct(data.product_id, 0, position)

        }

    }

    interface WishCallback {
        fun productDetail(bean: ProductDataBean?)
        fun removeProduct(productId: Int?, favStatus: Int?, position: Int?)

    }


}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    var tvPrice = view.tv_total_prod
    var tvActualPrice = view.tvActualPrice
    var tvName = view.tvName
    var sdvImage = view.sdvImage
    var tvSupplierName = view.tvSupplierName
    var tvCustomize = view.tv_type_custmize
    var qtyText = view.qty_text
    var tvMinus = view.tv_minus
    var tvQuant = view.tv_quant
    var tvPlus = view.tv_plus
    var ivWishlist = view.iv_wishlist
    var rbProdRating = view.rb_prod_rating
    var tvRating = view.tv_rating
    var groupCart = view.group_cart
    var tvAddCart = view.tvAddCart
    var tvFoodReview = view.tv_food_rating
    var stockLabel = view.stock_label
}





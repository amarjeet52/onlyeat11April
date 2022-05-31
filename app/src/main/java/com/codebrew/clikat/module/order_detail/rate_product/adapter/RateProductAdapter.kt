package com.codebrew.clikat.module.order_detail.rate_product.adapter

import android.content.Context
import com.codebrew.clikat.utils.StaticFunction.loadImage
import com.codebrew.clikat.data.model.others.RateProductListModel
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.module.order_detail.rate_product.adapter.RateProductAdapter.RateCallback
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.codebrew.clikat.utils.configurations.Configurations
import com.codebrew.clikat.utils.StaticFunction
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.RatingBar
import com.codebrew.clikat.utils.customviews.ClikatTextView
import android.widget.EditText
import android.widget.ImageView
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.extension.loadImage
import com.codebrew.clikat.app_utils.extension.loadUserImage
import com.codebrew.clikat.databinding.ItemRateProductBinding

class RateProductAdapter(private val rateProductList: List<RateProductListModel>) : RecyclerView.Adapter<RateProductAdapter.ViewHolder>() {
    private var mCallback: RateCallback? = null
    private var context: Context? = null
    private var isType: String? = null
    fun settingCallback(mCallback: RateCallback?, isType: String?) {
        this.mCallback = mCallback
        this.isType = isType
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        context = viewGroup.context
        val binding: ItemRateProductBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.context),
                R.layout.item_rate_product, viewGroup, false)
        binding.color = Configurations.colors
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val pos = viewHolder.adapterPosition
        viewHolder.tvProdName.text = rateProductList[pos].prodName
        viewHolder.tvProdSupplier.text = context!!.getString(R.string.supplier_tag, rateProductList[pos].supplierName)

        if(isType!="Prod")
        {
            viewHolder.ivProdImage.loadUserImage(rateProductList[pos].prodImage?:"")
        }else
        {
            viewHolder.ivProdImage.loadImage(rateProductList[pos].prodImage?:"")
        }

        // Glide.with(context).load(rateProductList.get(pos).getProdImage()).into(viewHolder.ivProdImage);
        viewHolder.rbReview.rating = 1.0f
        viewHolder.rbReview.onRatingBarChangeListener = OnRatingBarChangeListener { ratingBar: RatingBar, rating: Float, fromUser: Boolean ->
            if (rating < 1.0f) {
                ratingBar.rating = 1.0f
            } else {
                ratingBar.rating = rating
            }
        }
    }

    override fun getItemCount(): Int {
        return rateProductList.size
    }

    interface RateCallback {
        fun rateProduct(position: Int, rateProduct: RateProductListModel?)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivProdImage: ImageView
        var tvProdName: ClikatTextView
        var tvProdSupplier: ClikatTextView
        var rbReview: RatingBar
        var edHead: EditText
        var edDescription: EditText
        var btnRateProd: Button

        init {
            ivProdImage = itemView.findViewById(R.id.iv_prod_image)
            tvProdName = itemView.findViewById(R.id.tv_prod_name)
            tvProdSupplier = itemView.findViewById(R.id.tv_prod_supplier)
            rbReview = itemView.findViewById(R.id.rb_review)
            edHead = itemView.findViewById(R.id.ed_head)
            edDescription = itemView.findViewById(R.id.ed_description)
            btnRateProd = itemView.findViewById(R.id.btn_rate_prod)
            btnRateProd.setOnClickListener { view: View? ->
                rateProductList[adapterPosition].title = edHead.text.toString()
                rateProductList[adapterPosition].reviews = edDescription.text.toString()
                rateProductList[adapterPosition].value = rbReview.rating.toString()
                mCallback?.rateProduct(adapterPosition, rateProductList[adapterPosition])
            }
        }
    }
}
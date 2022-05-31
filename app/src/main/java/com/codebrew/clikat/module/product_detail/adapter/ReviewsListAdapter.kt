package com.codebrew.clikat.module.product_detail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.codebrew.clikat.R
import com.codebrew.clikat.databinding.ItemProductReviewBinding
import com.codebrew.clikat.modal.other.RatingBean
import com.codebrew.clikat.module.product_detail.adapter.ReviewsListAdapter.ViewHolder
import com.codebrew.clikat.utils.StaticFunction.loadImage
import com.codebrew.clikat.utils.configurations.Configurations
import com.codebrew.clikat.utils.customviews.ClikatImageView
import de.hdodenhof.circleimageview.CircleImageView

class ReviewsListAdapter(private val ratingBeans: MutableList<RatingBean>?) : Adapter<ViewHolder>() {
    private var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding: ItemProductReviewBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_product_review, parent, false)
        binding.color = Configurations.colors
        binding.drawables = Configurations.drawables
        binding.strings = Configurations.strings
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pos: Int = holder.adapterPosition
        holder.rbRating.rating = (ratingBeans?.get(pos)?.value?:0).toFloat()
        holder.tvRateTitle.visibility = if (ratingBeans?.get(pos)?.title.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }

        holder.tvRateDesc.visibility = if (ratingBeans?.get(pos)?.reviews.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }

        holder.tvRateTitle.text = ratingBeans?.get(pos)?.title ?: ""
        holder.tvRateDesc.text = ratingBeans?.get(pos)?.reviews ?: ""
        loadImage(ratingBeans?.get(pos)?.user_image, holder.ivUserImage, false)
        //  Glide.with(context).load(ratingBeans.get(pos).getUser_image()).into(holder.ivUserImage);
        holder.tvUserName.text = ratingBeans?.get(pos)?.firstname
        holder.tvRateDate.text = ratingBeans?.get(pos)?.created_on
    }

    override fun getItemCount(): Int {
        return ratingBeans?.size ?: 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var rbRating: RatingBar
        internal var tvRateTitle: TextView
        internal var tvRateDate: TextView
        internal var ivRateOption: ClikatImageView
        internal var tvRateDesc: TextView
        internal var ivUserImage: CircleImageView
        internal var tvUserName: TextView
        internal var cnstReview: ConstraintLayout

        init {
            rbRating = itemView.findViewById(R.id.rb_rating)
            tvRateTitle = itemView.findViewById(R.id.tv_rate_title)
            tvRateDate = itemView.findViewById(R.id.tv_rate_date)
            ivRateOption = itemView.findViewById(R.id.iv_rate_option)
            tvRateDesc = itemView.findViewById(R.id.tv_rate_desc)
            ivUserImage = itemView.findViewById(R.id.iv_userImage)
            tvUserName = itemView.findViewById(R.id.tv_userName)
            cnstReview = itemView.findViewById(R.id.cnst_review)
        }
    }

}
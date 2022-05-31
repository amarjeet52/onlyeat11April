package com.codebrew.clikat.module.all_categories.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.loadImage
import com.codebrew.clikat.databinding.ItemAllCategoryBinding
import com.codebrew.clikat.modal.other.English
import com.codebrew.clikat.utils.configurations.Configurations
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_all_category.view.*

class CategoryListAdapter(
        private val mValues: MutableList<English>?,
        private val mListener: OnCategoryListener?,
        private val appUtils: AppUtils)
    : RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as English
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onCategoryItem(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = DataBindingUtil.inflate<ItemAllCategoryBinding>(LayoutInflater.from(parent.context),
                R.layout.item_all_category, parent, false)
        binding.color = Configurations.colors
        binding.string = appUtils.loadAppConfig(0).strings
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues?.get(position)
        holder.mCatImage.loadImage(item?.image ?: "")
        holder.mContentView.text = item?.name

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues?.size ?: 0

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mCatImage: CircleImageView = mView.iv_category
        val mContentView: TextView = mView.tv_category

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }

    interface OnCategoryListener {
        fun onCategoryItem(item: English?)
    }
}

package com.codebrew.clikat.module.home_screen.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.databinding.*
import com.codebrew.clikat.modal.other.English
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.home_screen.adapter.CategoryListAdapter.CategoryViewHolder
import com.codebrew.clikat.utils.StaticFunction.loadImage
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.material.card.MaterialCardView
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.activity_splash.*

class CategoryListAdapter internal constructor(private val beanList: List<English>,
                                               private val screenType: Int,
                                               private val clientInform: SettingModel.DataBean.SettingData?) : RecyclerView.Adapter<CategoryViewHolder>() {
    private var mCallback: CategoryDetail? = null

    private val colorConfig by lazy { Configurations.colors }

    private var mContext: Context? = null
    private var isSkipLarge: Boolean? = false

    fun settingCallback(mCallback: CategoryDetail?) {
        this.mCallback = mCallback
    }

    fun isSkipViewAllUi(isSkipLarge: Boolean) {
        this.isSkipLarge = isSkipLarge
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {

        mContext = parent.context

        return when (viewType) {
            TYPE_CRAVE_CATEGORY -> {
                val binding: ItemCategoryCraveBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_category_crave, parent, false)
                binding.color = colorConfig
                CategoryViewHolder(binding.root, viewType)
            }

            else -> {
                val binding: ItemCategoryHorizontalBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_category_horizontal, parent, false)
                binding.color = colorConfig
                CategoryViewHolder(binding.root, viewType)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: CategoryViewHolder, i: Int) {
        viewHolder.onBind()
    }

    fun getCategoriesList(): List<English> {
        return beanList
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            clientInform?.is_carveQatar_home_theme == "1" -> {
                TYPE_CRAVE_CATEGORY
            }

            screenType == AppDataType.Ecom.type -> {
                if (clientInform?.is_lubanah_theme == "1")
                    ROUND_CATEGORY
                else MULTIPLE_CATEGORY
            }
            screenType == AppDataType.HomeServ.type -> {
                SERVICE_CATEGORY
            }

            else -> {
                TWO_CATEGORY
            }
        }
    }

    override fun getItemCount(): Int {
        return beanList.size
    }

    interface CategoryDetail {
        fun onCategoryDetail(bean: English?)
    }

    inner class CategoryViewHolder(itemView: View, var viewType: Int) : RecyclerView.ViewHolder(itemView) {
        var ivCategory: RoundedImageView = itemView.findViewById(R.id.iv_userImage)
        var tvCategory: TextView = itemView.findViewById(R.id.category_text)
        val categoryCenterText: TextView? =
                if (clientInform?.show_ecom_v2_theme == "1") {
                    itemView.findViewById(R.id.category_center_text)
                } else null
        val clMain: MaterialCardView? = if (clientInform?.is_skip_theme == "1") itemView.findViewById(R.id.cvMain) else null

        @RequiresApi(Build.VERSION_CODES.M)
        fun onBind() {
            tvCategory.text = beanList[adapterPosition].name
            categoryCenterText?.text = beanList[adapterPosition].name
            itemView.isEnabled = true
            itemView.setOnClickListener { v: View? ->
                mCallback?.onCategoryDetail(beanList[adapterPosition])
            }

            if (clientInform?.show_ecom_v2_theme == "1" && adapterPosition == itemCount - 1) {
                categoryCenterText?.visibility = View.VISIBLE
                ivCategory.background = null
                ivCategory.setBackgroundColor(Color.parseColor(colorConfig.primaryColor_trans))
                tvCategory.visibility = View.INVISIBLE
            } else {
                if (clientInform?.show_tags_for_suppliers == "1")
                    loadImage(beanList[adapterPosition].tag_image, ivCategory, true)
                else {
                    if (clientInform?.is_carveQatar_home_theme == "1" && adapterPosition == 0)
                        Glide.with(mContext!!).load(R.drawable.ic_all).into(ivCategory)
                    else
                        loadImage(if (screenType == AppDataType.HomeServ.type || screenType == AppDataType.Beauty.type) beanList[adapterPosition].icon else beanList[adapterPosition].icon, ivCategory, true)
                }
            }


        }
    }

    companion object {
        private const val MULTIPLE_CATEGORY = 1
        private const val TWO_CATEGORY = 2
        private const val SERVICE_CATEGORY = 3
        private const val ROUND_CATEGORY = 8
        private const val TYPE_CRAVE_CATEGORY = 11
    }

}
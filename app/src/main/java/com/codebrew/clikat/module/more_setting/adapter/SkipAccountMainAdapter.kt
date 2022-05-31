package com.codebrew.clikat.module.more_setting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.data.SkipAppScreens
import com.codebrew.clikat.databinding.ItemAboutAppBinding
import com.codebrew.clikat.databinding.ItemAccountBinding
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.other.more_list.MoreListModel
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.configurations.Configurations
import com.codebrew.clikat.utils.configurations.TextConfig

class SkipAccountMainAdapter(private val context: Context, private val textConfig: TextConfig?,
                             val appUtils: AppUtils, private val screenType: String) :
        RecyclerView.Adapter<SkipAccountMainAdapter.ViewHolder>() {

    private var itemsCount = mutableListOf<MoreListModel>()
    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null
    private var settingFlowBean: SettingModel.DataBean.SettingData? = null
    private var featureBean: ArrayList<SettingModel.DataBean.FeatureData>? = null

    private lateinit var clickListener: TagListener

    fun settingCallback(clickListener: TagListener) {
        this.clickListener = clickListener
    }


    fun loadData(loginStatus: Boolean, screenFlowBean: SettingModel.DataBean.ScreenFlowBean?,
                 settingFlowBean: SettingModel.DataBean.SettingData?,
                 featureBean: ArrayList<SettingModel.DataBean.FeatureData>?) {
        this.settingFlowBean = settingFlowBean
        this.screenFlowBean = screenFlowBean
        this.featureBean = featureBean
        itemsCount = populateData(loginStatus)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            MAIN , PROFILE-> {
                val binding = ItemAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                binding.color = Configurations.colors
                ViewHolder(binding)
            }
            else -> {
                val binding = ItemAboutAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                binding.color = Configurations.colors
                ViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int {
        return itemsCount.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.updateData(itemsCount[holder.adapterPosition])

        holder.itemView.setOnClickListener {
            clickListener.onClick(itemsCount[holder.adapterPosition].name)
        }
    }

    inner class ViewHolder(private var binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        fun updateData(model: MoreListModel) {
            when (binding) {
                is ItemAccountBinding -> {
                    (binding as ItemAccountBinding).tvTitle.text = model.name
                    (binding as ItemAccountBinding).ivImage.setImageResource(model.image)
                    (binding as ItemAccountBinding).ivImageEnd.setImageResource(model.large_image
                            ?: 0)
                    if (screenType == SkipAppScreens.PROFILE.type) {
                        (binding as ItemAccountBinding).tvTitle.setPadding(StaticFunction.pxFromDp(12, context), StaticFunction.pxFromDp(24, context),
                                StaticFunction.pxFromDp(12, context), StaticFunction.pxFromDp(24, context))
                    }
                }
                is ItemAboutAppBinding -> {
                    (binding as ItemAboutAppBinding).tvTitle.text = model.name
                    (binding as ItemAboutAppBinding).ivImage.setImageResource(model.image)
                    (binding as ItemAboutAppBinding).ivImageEnd.setImageResource(model.large_image
                            ?: 0)
                    if (screenType == SkipAppScreens.GET_HELP.type ) {
                        (binding as ItemAboutAppBinding).tvTitle.setTextColor(ContextCompat.getColor(context, R.color.white))
                        (binding as ItemAboutAppBinding).clMain.background = ContextCompat.getDrawable(context, R.drawable.back_rec_theme_color_solid)
                    }
                }
            }
        }

    }


    fun populateData(loginStatus: Boolean): MutableList<MoreListModel> {

        val itemList = mutableListOf<MoreListModel>()
        when (screenType) {
            SkipAppScreens.MAIN.type -> {
                val items = context.resources.getStringArray(R.array.main_settings_array)
                val itemImages = arrayOf(
                        R.drawable.ic_profile, R.drawable.ic_info, R.drawable.ic_globe, R.drawable.ic_info)
                val itemImagesLarge = arrayOf(
                        R.drawable.ic_user_big, R.drawable.ic_help_big, R.drawable.ic_language_big, R.drawable.ic_about_big)

                for (i in items.indices) {
                    itemList.add(MoreListModel(items[i], itemImages[i], itemImagesLarge[i]))
                }
            }
            SkipAppScreens.ABOUT_APP.type -> {
                val items = context.resources.getStringArray(R.array.about_app)
                val itemImages = arrayOf(
                        R.drawable.ic_faq_big, R.drawable.ic_contact_big, R.drawable.ic_feedback_big, R.drawable.ic_privacy_big, R.drawable.ic_terms_big)
                val itemImagesLarge = arrayOf(
                        R.drawable.ic_faq_big, R.drawable.ic_contact_big, R.drawable.ic_feedback_big, R.drawable.ic_privacy_big, R.drawable.ic_terms_big)
                for (i in items.indices) {
                    itemList.add(MoreListModel(items[i], itemImages[i], itemImagesLarge[i]))
                }
            }

            SkipAppScreens.ACCOUNT.type -> {
                val items = context.resources.getStringArray(R.array.account)
                val itemImages = arrayOf(
                        R.drawable.ic_profile, R.drawable.ic_lock)
                val itemImagesLarge = arrayOf(
                        R.drawable.ic_profile, R.drawable.ic_lock)
                for (i in items.indices) {
                    itemList.add(MoreListModel(items[i], itemImages[i], itemImagesLarge[i]))
                }
            }
            SkipAppScreens.PROFILE.type -> {
                val items = context.resources.getStringArray(R.array.my_profile)
                val itemImages = arrayOf(
                        R.drawable.ic_notification, R.drawable.ic_car_1, R.drawable.ic_shopping,
                        R.drawable.ic_tag,R.drawable.ic_heart, R.drawable.ic_recover)

                val itemImagesLarge = arrayOf(
                        R.drawable.ic_notification_big, R.drawable.ic_car_big, R.drawable.ic_order_big, R.drawable.ic_offer_big,R.drawable.ic_favourite_big,
                        R.drawable.ic_regulars_big)

                for (i in items.indices) {
                    itemList.add(MoreListModel(items[i], itemImages[i], itemImagesLarge[i]))
                }
            }
            SkipAppScreens.GET_HELP.type -> {
                val items = context.resources.getStringArray(R.array.get_help)
                val itemImages = arrayOf(
                        R.drawable.ic_mail_white, R.drawable.ic_call_white)

                val itemImagesLarge = arrayOf(
                        R.drawable.ic_notification_big, R.drawable.ic_car_big)

                for (i in items.indices) {
                    itemList.add(MoreListModel(items[i], itemImages[i], itemImagesLarge[i]))
                }
            }
        }

        return itemList
    }

    class TagListener(val clickListener: (type: String) -> Unit) {
        fun onClick(type: String) = clickListener(type)
    }

    override fun getItemViewType(position: Int): Int {
        return when (screenType) {
            SkipAppScreens.MAIN.type -> MAIN
            SkipAppScreens.ABOUT_APP.type -> APP_INFO
            SkipAppScreens.PROFILE.type -> PROFILE
            SkipAppScreens.GET_HELP.type -> GET_HELP
            SkipAppScreens.ACCOUNT.type -> ACCOUNT

            else -> MAIN
        }
    }

    companion object {
        const val MAIN = 1
        const val APP_INFO = 2
        const val PROFILE = 3
        const val GET_HELP = 4
        const val ACCOUNT = 5
    }
}
package com.codebrew.clikat.module.more_setting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginStart
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.BuildConfig.CLIENT_CODE
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.databinding.ItemMoreTagBinding
import com.codebrew.clikat.databinding.PopupRestaurantMenuBinding
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.other.more_list.MoreListModel
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.configurations.Configurations
import com.codebrew.clikat.utils.configurations.TextConfig
import kotlinx.android.synthetic.main.item_more_tag.view.*

class MoreTagAdapter(private val context: Context, private val textConfig: TextConfig?, val appUtils: AppUtils,internal var self_pickup:String) : RecyclerView.Adapter<MoreTagAdapter.ViewHolder>() {

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

    fun loadCardData() {
        this.settingFlowBean = null
        this.screenFlowBean = null
        this.featureBean = null
        itemsCount = populateCardData()
    }

    private fun populateCardData(): MutableList<MoreListModel> {
        val itemList = mutableListOf<MoreListModel>()

        itemList.add(MoreListModel(context.getString(R.string.debitCreditCard),
                R.drawable.ic_credit_card))


        return itemList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMoreTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.color = Configurations.colors
        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return itemsCount.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(settingFlowBean?.is_lubanah_theme=="1"){
            holder.itemView.clTop?.setBackgroundResource(R.drawable.background_grey_stroke_4dp)
            val eightDp=StaticFunction.pxFromDp(8,context)
            val sixteenDp=StaticFunction.pxFromDp(16,context)
            holder.itemView.clTop.setPadding(eightDp,sixteenDp,sixteenDp,sixteenDp)
            holder.itemView.iv_forward?.visibility=View.VISIBLE
        }else
            holder.itemView.iv_forward?.visibility=View.GONE

        holder.updateData(itemsCount[holder.adapterPosition])

        holder.itemView.setOnClickListener {
            clickListener.onClick(itemsCount[holder.adapterPosition].name)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun updateData(model: MoreListModel) {

            itemView.tv_more_name.text = model.name
            itemView.iv_more.setImageResource(model.image)
        }

    }


    fun populateData(loginStatus: Boolean): MutableList<MoreListModel> {

        val itemList = mutableListOf<MoreListModel>()


        if(settingFlowBean?.is_juman_flow_enable!="1") {
            val items = context.resources.getStringArray(R.array.more_array)

            val itemImages = arrayOf(
                    R.drawable.ic_more_share_app, R.drawable.ic_more_terms, R.drawable.ic_more_info, R.drawable.ic_more_policy)


            for (i in items.indices) {
                itemList.add(MoreListModel(items[i], itemImages[i]))
            }
        }
        if (settingFlowBean?.show_ecom_v2_theme == "1" && loginStatus) {
            itemList.add(MoreListModel(context.getString(R.string.orders),
                    R.drawable.ic_assignment))
        }


        settingFlowBean?.secondary_language?.let {
            if (it != "0") {
                itemList.add(MoreListModel(context.getString(R.string.change_language), R.drawable.ic_more_language))
            }
        }

        if (BuildConfig.CLIENT_CODE == "roadsideassistance_0649"){
            itemList.add(MoreListModel(context.getString(R.string.user_license_agree), R.drawable.ic_more_terms))
        }

        if (loginStatus) {
            if (settingFlowBean?.is_sos_enable == "1")
                itemList.add(MoreListModel(context.getString(R.string.sos), R.drawable.ic_more_info))

            if (settingFlowBean?.is_product_wishlist == "1" || settingFlowBean?.is_supplier_wishlist == "1") {
                itemList.add(MoreListModel(textConfig?.wishlist
                        ?: "", R.drawable.ic_more_favourite))
            }

            if (!settingFlowBean?.referral_feature.isNullOrEmpty() && settingFlowBean?.referral_feature == "1") {
                itemList.add(MoreListModel(context.getString(R.string.referral), R.drawable.ic_more_referral))
            }

            if (!settingFlowBean?.show_prescription_requests.isNullOrEmpty()
                    && settingFlowBean?.show_prescription_requests == "1") {
                itemList.add(MoreListModel(context.getString(R.string.requests, textConfig?.order), R.drawable.ic_more_terms))
            }

            featureBean?.find { it1 -> it1.name == "Tawk" }?.key_value_front?.firstOrNull()?.value.let {
                if (it?.isNotEmpty()==true) {
                    itemList.add(MoreListModel(context.getString(R.string.customer_support), R.drawable.ic_more_support))
                }
            }

//            settingFlowBean?.is_user_subscription?.let {
//                if (it == "1") {
////                    itemList.add(MoreListModel(textConfig?.my_subscription
////                            ?: context.getString(R.string.subscriptions), R.drawable.ic_more_subscription))
////                }
//            }

            if (settingFlowBean?.wallet_module == "1") {
//                itemList.add(MoreListModel(textConfig?.wallet
//                        ?: context.getString(R.string.wallet), R.drawable.ic_more_wallet))
                if (self_pickup.equals("9")) {
                    itemList.add(MoreListModel(context.getString(R.string.your_vehicle), R.drawable.ic_vehicle))
                }else{

                }
                itemList.add(MoreListModel(context.getString(R.string.add_new), R.drawable.pinyellow))
            }

            if (settingFlowBean?.admin_to_user_chat == "1")
                itemList.add(MoreListModel(context.getString(R.string.chat_with_admin), R.drawable.ic_more_chat_admin))

            if (settingFlowBean?.is_loyality_enable == "1")
                itemList.add(MoreListModel(textConfig?.loyalty_points
                        ?: context.getString(R.string.loyality_points), R.drawable.ic_more_loyality_points))

            settingFlowBean?.is_table_booking?.let {
                if (it == "1") {
                    if (CLIENT_CODE == "betternoms_0820") {
                        itemList.add(0, MoreListModel(context.getString(R.string.table_booking), R.drawable.ic_table))
                    } else {
                        itemList.add(MoreListModel(context.getString(R.string.table_booking), R.drawable.ic_table))
                    }
                }
            }

              if ( settingFlowBean?.is_social_ecommerce=="1" && screenFlowBean?.app_type == AppDataType.Ecom.type) {
                  itemList.add(MoreListModel(context.getString(R.string.social_post), R.drawable.ic_post_active))
              }
        }

        settingFlowBean?.is_feedback_form_enabled?.let {
            if (it == "1") {
                itemList.add(MoreListModel(context.getString(R.string.feedback), R.drawable.ic_more_feedback_form))
            }
        }
        if (settingFlowBean?.enable_whatsapp_contact_us == "1")
            itemList.add(MoreListModel(context.getString(R.string.contact_us), R.drawable.ic_whatsapp))

        if (!settingFlowBean?.fackbook_link.isNullOrEmpty())
            itemList.add(MoreListModel(context.getString(R.string.facebook), R.drawable.ic_fb))
        if (!settingFlowBean?.twitter_link.isNullOrEmpty())
            itemList.add(MoreListModel(context.getString(R.string.twitter), R.drawable.ic_twitter))
        if (!settingFlowBean?.linkedin_link.isNullOrEmpty())
            itemList.add(MoreListModel(context.getString(R.string.linkein), R.drawable.ic_lonkedin))
        if (!settingFlowBean?.google_link.isNullOrEmpty())
            itemList.add(MoreListModel(context.getString(R.string.google_plus), R.drawable.ic_g_plus))

        if (settingFlowBean?.extra_instructions == "1") {
            itemList.add(MoreListModel(context.getString(R.string.faq), R.drawable.ic_more_faq))
        }

        itemList.add(MoreListModel(context.getString(R.string.support_email), R.drawable.ic_more_terms))
        itemList.add(MoreListModel(context.getString(R.string.support_number), R.drawable.ic_more_terms))

        if (settingFlowBean?.extra_functionality == "1") {
            itemList.add(MoreListModel(context.getString(R.string.become_care_giver), R.drawable.ic_more_caregiver1))
        }

        if (loginStatus)
            itemList.add(MoreListModel(context.getString(R.string.logout), R.drawable.ic_more_logout))

        return itemList

    }

    class TagListener(val clickListener: (type: String) -> Unit) {
        fun onClick(type: String) = clickListener(type)
    }

}
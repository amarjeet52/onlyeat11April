package com.codebrew.clikat.module.home_screen.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.loadImage
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.FoodAppType
import com.codebrew.clikat.data.NearBySupplierType
import com.codebrew.clikat.databinding.ItemHomeSupplierBinding
import com.codebrew.clikat.databinding.ItemSponsorBinding
import com.codebrew.clikat.databinding.ItemSupplierLayoutHorizontalBinding
import com.codebrew.clikat.databinding.ItemSupplierLayoutNewBinding
import com.codebrew.clikat.modal.other.SettingModel.DataBean.SettingData
import com.codebrew.clikat.modal.other.SupplierDataBean
import com.codebrew.clikat.modal.other.SupplierTags
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.StaticFunction.loadImage
import com.codebrew.clikat.utils.configurations.Configurations
import com.codebrew.clikat.utils.customviews.ClikatTextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.fragment_restaurant_detail_new.*

class NearBySuppliersAdapter internal constructor(private val sponsorList: List<SupplierDataBean>,
                                                  private val mScreenType: Int,
                                                  private val clientInfom: SettingData?,
                                                  private val parentPos: Int,
                                                  private val tableBooked: Boolean,
                                                  private val appUtils: AppUtils?,private var deliveryType: Int?=null

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mCallback: SponsorDetail? = null
    private var mContext: Context? = null
    private val textConfig by lazy { appUtils?.loadAppConfig(0)?.strings }
    private var sectionType:Int?=NearBySupplierType.GRID.type
    private var isVerticalList:Boolean?=false
    fun settingCallback(mCallback: SponsorDetail?) {
        this.mCallback = mCallback
    }

    fun setSectionType(type:Int,isVerticalList:Boolean){
        this.isVerticalList=isVerticalList
        sectionType=type
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = viewGroup.context
        return if (viewType== TYPE_VERT) {
            val binding: ViewDataBinding = if (sectionType==NearBySupplierType.GRID.type)
                DataBindingUtil.inflate(LayoutInflater.from(viewGroup.context), R.layout.item_supplier_layout_new, viewGroup, false)
            else
                DataBindingUtil.inflate(LayoutInflater.from(viewGroup.context), R.layout.item_home_supplier, viewGroup, false)
            SupplierViewHolder(binding)

        } else {
            val bindingHor: ItemSponsorBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.context), R.layout.item_sponsor, viewGroup, false)
            bindingHor.color = Configurations.colors
            return HorizonatlViewHolder(bindingHor)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val pos = holder.adapterPosition
        if (holder is HorizonatlViewHolder) {
            val horViewHolder = holder as HorizonatlViewHolder

            horViewHolder.ivProduct.loadImage(sponsorList[pos].logo ?: "")
            horViewHolder.ivImage.loadImage(sponsorList[pos].logo ?: "")

            // StaticFunction.INSTANCE.loadImage(sponsorList.get(pos).getLogo(), horViewHolder.ivImage, false);

            if (mScreenType == AppDataType.Ecom.type || mScreenType == 5) {
                horViewHolder.rbSponsor.rating = java.lang.Float.valueOf(sponsorList[pos].rating.toFloat())
                horViewHolder.tvRating.text = mContext!!.getString(R.string.reviews_text, sponsorList[pos].total_reviews)
                horViewHolder.tvSponsorLocation.text = sponsorList[pos].address
                loadImage(sponsorList[pos].logo, horViewHolder.ivProduct, false)
            }
            if (mScreenType != AppDataType.Food.type && clientInfom != null &&
                    clientInfom.is_supplier_wishlist == "1") {
                horViewHolder.ivWishlist.visibility = View.VISIBLE
            } else {
                horViewHolder.ivWishlist.visibility = View.GONE
            }

            horViewHolder.ivWishlist.setOnCheckedChangeListener(null)
            horViewHolder.ivWishlist.isChecked = sponsorList[pos].Favourite == 1

            horViewHolder.ivWishlist.setOnCheckedChangeListener { checkBox, isChecked ->
                //  mCallback?.onSponsorWishList(sponsorList[pos], parentPos,isChecked)
            }

            horViewHolder.tvSponsorName.text = sponsorList[pos].name
            horViewHolder.tvBottomSpsrName.text = sponsorList[pos].name

            // viewHolder.itemView.setEnabled(true);
            horViewHolder.itemView.setOnClickListener {
                //  viewHolder.itemView.setEnabled(false);
                mCallback?.onSponsorDetail(sponsorList[pos])
            }


            if (mScreenType > AppDataType.Custom.type || clientInfom?.app_selected_theme == "3" || clientInfom?.app_selected_theme == "1") {
                if (clientInfom?.clikat_theme == "1")
                    horViewHolder.ivImage.cornerRadius = 8f
                else
                    horViewHolder.ivImage.cornerRadius = 24f

                horViewHolder.groupClikat.visibility = View.VISIBLE
                horViewHolder.clMain.visibility = View.GONE

                if (clientInfom?.app_selected_theme == "3" || clientInfom?.app_selected_theme == "1") {
                    horViewHolder.tvRatingNew.visibility = View.VISIBLE
                    horViewHolder.tvBottomSpsrName.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                    horViewHolder.tvRatingNew.text = if (sponsorList[pos].rating > 0f) sponsorList[pos].rating.toString() else mContext?.getString(R.string.new_tag)

                } else {
                    horViewHolder.tvRatingNew.visibility = View.GONE
                    horViewHolder.tvBottomSpsrName.textAlignment = View.TEXT_ALIGNMENT_CENTER
                }
            } else {

                horViewHolder.clMain.visibility = View.VISIBLE
                horViewHolder.groupClikat.visibility = View.GONE
            }

            if (clientInfom?.show_ecom_v2_theme == "1") {
                val param = horViewHolder.itemSponsorContainer.layoutParams as RecyclerView.LayoutParams
                param.width = RecyclerView.LayoutParams.MATCH_PARENT
                horViewHolder.itemSponsorContainer.layoutParams = param

                val param2 = horViewHolder.clMain.layoutParams as ConstraintLayout.LayoutParams
                param2.width = ConstraintLayout.LayoutParams.MATCH_PARENT
                horViewHolder.clMain.layoutParams = param2

                val param3 = horViewHolder.ivProduct.layoutParams as ConstraintLayout.LayoutParams
                param3.dimensionRatio = "4:2"
                param3.width = ConstraintLayout.LayoutParams.MATCH_PARENT
                horViewHolder.ivProduct.layoutParams = param3


                val param4 = horViewHolder.itemLayout.layoutParams as FrameLayout.LayoutParams
                param4.width = FrameLayout.LayoutParams.MATCH_PARENT
                horViewHolder.itemLayout.layoutParams = param4

                horViewHolder.grpSupplier.visibility = View.GONE
            } else {
                horViewHolder.grpSupplier.visibility = if (mScreenType == AppDataType.Ecom.type || mScreenType == 5) View.VISIBLE else View.GONE
            }

            /* horViewHolder.btnBookTable.visibility = if (sponsorList[pos].is == 1 && settingBean?.is_table_booking == "1") {
             View.VISIBLE
         } else {
             View.GONE
         }*/

            if (clientInfom?.app_selected_theme == "3" && clientInfom.is_table_booking == "1") {
                if (sponsorList[pos].is_dine_in == 1)
                    horViewHolder.btnBookTable.visibility = View.VISIBLE
                else
                    horViewHolder.btnBookTable.visibility = View.INVISIBLE

                if (tableBooked)
                    horViewHolder.btnBookTable.visibility = View.INVISIBLE

                horViewHolder.btnBookTable.setOnClickListener {
                    mCallback?.onBookNow(sponsorList[pos])
                }
            }
        }else if(holder is SupplierViewHolder){
            holder.onBinData(sponsorList[pos])
            holder.onClickListener()
        }

    }


    override fun getItemCount(): Int {
        return sponsorList.size
    }

    interface SponsorDetail {
        fun onSponsorDetail(supplier: SupplierDataBean?)
        fun onBookNow(supplierData: SupplierDataBean)
    }


    inner class HorizonatlViewHolder(private val bindingHor: ItemSponsorBinding) : RecyclerView.ViewHolder(bindingHor.root) {
        var ivImage: RoundedImageView = itemView.findViewById(R.id.ivSupplierCustom)
        var ivProduct: ImageView = itemView.findViewById(R.id.iv_supplier)
        var tvSponsorName: TextView = itemView.findViewById(R.id.tv_sponsor_name)
        var tvRating: TextView = itemView.findViewById(R.id.tv_rating)
        var rbSponsor: RatingBar = itemView.findViewById(R.id.rb_rating)
        var tvSponsorLocation: TextView = itemView.findViewById(R.id.tv_sponsor_adrs)
        var grpSupplier: Group = itemView.findViewById(R.id.gp_suplr)
        var tvBottomSpsrName: TextView = itemView.findViewById(R.id.tvBottomSpsrName)
        var ivWishlist: CheckBox = itemView.findViewById(R.id.iv_wishlist)
        var clMain: CardView = itemView.findViewById(R.id.clMain)
        var itemSponsorContainer: ConstraintLayout = itemView.findViewById(R.id.itemSponsorContainer)
        val itemLayout: ConstraintLayout = itemView.findViewById(R.id.itemLayout)
        val groupClikat: Group = itemView.findViewById(R.id.groupClikat)
        var tvRatingNew: MaterialTextView = itemView.findViewById(R.id.tvRating)
        var btnBookTable: MaterialButton = itemView.findViewById(R.id.btnBookTable)
    }

    inner class SupplierViewHolder(private var binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        var sdvImage: RoundedImageView
        var tvName: ClikatTextView
        var tvSupplierInf: TextView
        var tvSupplierloc: ClikatTextView
        var tvRating: TextView
        var tvTrackOption: TextView
        var ivStatus: TextView
        fun onBinData(dataBean: SupplierDataBean?) {
            dataBean?.isOpen = appUtils?.checkResturntTiming(dataBean?.timing)?:false
            when (binding) {
                is ItemSupplierLayoutNewBinding -> {
                    (binding as ItemSupplierLayoutNewBinding).color = Configurations.colors
                    (binding as ItemSupplierLayoutNewBinding).strings = textConfig
                    (binding as ItemSupplierLayoutNewBinding).supplierData = dataBean
                    (binding as ItemSupplierLayoutNewBinding).settingData = clientInfom
                    (binding as ItemSupplierLayoutNewBinding).isHungerApp = clientInfom?.is_hunger_app == "1"
                    (binding as ItemSupplierLayoutNewBinding).ratingBar.rating = dataBean?.rating?.toFloat()
                            ?: 0f

                    (binding as ItemSupplierLayoutNewBinding).groupDeliveryTime.visibility=if(clientInfom?.is_table_booking == "1"
                            && deliveryType !=null && deliveryType== FoodAppType.DineIn.foodType) View.GONE else View.VISIBLE

                    if (clientInfom?.app_selected_theme == "3"  && clientInfom.is_table_booking == "1") {
                        if (dataBean?.is_dine_in == 1)
                            (binding as ItemSupplierLayoutNewBinding).tvBookNow.visibility = View.VISIBLE
                        else
                            (binding as ItemSupplierLayoutNewBinding).tvBookNow.visibility = View.GONE

                        if(appUtils?.getCurrentTableData() != null)
                            (binding as ItemSupplierLayoutNewBinding).tvBookNow.visibility = View.GONE

                        (binding as ItemSupplierLayoutNewBinding).tvBookNow.setOnClickListener {
                            mCallback?.onBookNow(dataBean?: SupplierDataBean())
                        }
                    }
                }
                is ItemHomeSupplierBinding -> {
                    (binding as ItemHomeSupplierBinding).color =  Configurations.colors
                    (binding as ItemHomeSupplierBinding).strings = textConfig
                    (binding as ItemHomeSupplierBinding).supplierData = dataBean
                    (binding as ItemHomeSupplierBinding).settingData = clientInfom
                    (binding as ItemHomeSupplierBinding).isHungerApp = clientInfom?.is_hunger_app == "1"
                    (binding as ItemHomeSupplierBinding).groupTags.visibility=View.GONE
                  /*  (binding as ItemHomeSupplierBinding).groupTags.visibility = if (!dataBean?.supplier_tags.isNullOrEmpty() && clientInfom?.show_tags_for_suppliers == "1")
                        View.VISIBLE else View.GONE

                    val tags = if (!dataBean?.supplier_tags.isNullOrEmpty()) {
                        dataBean?.supplier_tags?.joinToString(",")
                    } else ""
                    (binding as ItemHomeSupplierBinding).tvTags.text = tags*/


                    if (clientInfom?.app_selected_theme == "3"  && clientInfom.is_table_booking == "1") {
                        if (dataBean?.is_dine_in == 1)
                            (binding as ItemHomeSupplierBinding).tvBookNow.visibility = View.VISIBLE
                        else
                            (binding as ItemHomeSupplierBinding).tvBookNow.visibility = View.GONE

                        if(appUtils?.getCurrentTableData() != null)
                            (binding as ItemHomeSupplierBinding).tvBookNow.visibility = View.GONE

                        (binding as ItemHomeSupplierBinding).tvBookNow.setOnClickListener {
                            mCallback?.onBookNow(dataBean?: SupplierDataBean())
                        }
                    }
                }
            }

            if (BuildConfig.CLIENT_CODE == "muraa_0322") {
                tvTrackOption.visibility = View.INVISIBLE
                ivStatus.visibility = View.INVISIBLE
                tvSupplierInf.visibility = View.INVISIBLE
            }
            mContext?.let {
                sdvImage.loadImage(dataBean?.logo ?: "")
            }
            //tvRating.setVisibility(dataBean.getRating() > 0 ? View.VISIBLE : View.GONE);
            // 0 for delivery 1 for pickup
            //  if (dataBean?.rating ?: 0.0 > 0) tvRating.text = dataBean?.rating.toString() else tvRating.text = "NEW"
            tvSupplierInf.text = if (clientInfom?.full_view_supplier_theme == "1") {
                mContext?.getString(R.string.min_max_time_, dataBean?.delivery_min_time, dataBean?.delivery_max_time)
            } else
                mContext?.getString(R.string.min_max_time, dataBean?.delivery_min_time, dataBean?.delivery_max_time)

            tvSupplierInf.visibility = if (dataBean?.delivery_max_time != null && StaticFunction.checkVisibility(clientInfom?.show_supplier_delivery_timing,
                            clientInfom?.show_supplier_info_settings) &&  deliveryType != FoodAppType.DineIn.foodType)
                View.VISIBLE else View.INVISIBLE

        }


        fun onClickListener() {
            itemView.setOnClickListener {
                mCallback?.onSponsorDetail(sponsorList[adapterPosition])
            }
        }

        init {
            val view = binding.root

            ivStatus = view.findViewById(R.id.ivStatus)
            sdvImage = view.findViewById(R.id.sdvImage)
            tvName = view.findViewById(R.id.tvName)
            tvSupplierInf = view.findViewById(R.id.tv_supplier_inf)
            tvSupplierloc = view.findViewById(R.id.tvSupplierloc)
            tvRating = view.findViewById(R.id.tv_rating)
            tvTrackOption = view.findViewById(R.id.tv_live_track)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (clientInfom?.dynamic_home_screen_sections == "1" && isVerticalList==true) {
            TYPE_VERT
        } else {
            TYPE_HOR
        }
    }
    companion object {
        private const val TYPE_HOR = 1
        private const val TYPE_VERT = 2
    }

}


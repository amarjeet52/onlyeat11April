package com.codebrew.clikat.module.home_screen.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.loadImage
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.databinding.*
import com.codebrew.clikat.modal.other.SettingModel.DataBean.SettingData
import com.codebrew.clikat.modal.other.SupplierDataBean
import com.codebrew.clikat.modal.other.SupplierInArabicBean
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.StaticFunction.loadImage
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.material.textview.MaterialTextView
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.item_sponsor.view.*
import kotlinx.android.synthetic.main.item_sponsor.view.ic_call
import kotlinx.android.synthetic.main.item_sponsor.view.iv_wishlist
import kotlinx.android.synthetic.main.item_sponsor.view.tv_rating
import kotlinx.android.synthetic.main.item_sponsor_vertical.view.*

class SponsorListAdapter internal constructor(private val sponsorList: List<SupplierInArabicBean>,
                                              private val mScreenType: Int,
                                              private val clientInfom: SettingData?,
                                              private val parentPos: Int,private val appUtils:AppUtils
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mCallback: SponsorDetail? = null
    private var mContext: Context? = null
    private var isSuppliersDetailView: Boolean? = false

    private var isViewAllUi:Boolean?=false

    fun setIsSupplierDetail(isDetailView: Boolean) {
        isSuppliersDetailView = isDetailView
    }

    fun setShowOnlyVertical(isViewAllUi:Boolean){
        this.isViewAllUi=isViewAllUi
    }

    fun settingCallback(mCallback: SponsorDetail?) {
        this.mCallback = mCallback
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = viewGroup.context
        return if (viewType == TYPE_HOR) {
            if ((clientInfom?.app_selected_theme == "3" || clientInfom?.app_selected_theme == "1") && isSuppliersDetailView == true ) {
                val bindingHor: ItemSupplierLayoutHorizontalBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.context), R.layout.item_supplier_layout_horizontal, viewGroup, false)
                SupplierViewHolder(bindingHor)
            } else {
                val bindingHor: ItemSponsorBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.context), R.layout.item_sponsor, viewGroup, false)
                bindingHor.color = Configurations.colors
                HorizonatlViewHolder(bindingHor)
            }
        } else if (viewType == TYPE_SKIP_THEME) {
            val bindingHor: ItemSkipRecomendedBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.context), R.layout.item_skip_recomended, viewGroup, false)
            SkipRecommendedViewHolder(bindingHor)
        } else {
            val bindingVert: ItemSponsorVerticalBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.context), R.layout.item_sponsor_vertical, viewGroup, false)
            bindingVert.color = Configurations.colors
            VerticalViewHolder(bindingVert)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val pos = holder.adapterPosition
        if (holder is SupplierViewHolder) {
            holder.onBindHor(sponsorList[holder.getAdapterPosition()])

            holder.itemView.setOnClickListener {
                mCallback?.onSponsorDetail(sponsorList[pos])
            }
        }
        else if (holder is SkipRecommendedViewHolder) {
            holder.onBindHor(sponsorList[holder.getAdapterPosition()])

            holder.itemView.setOnClickListener {
                mCallback?.onSponsorDetail(sponsorList[pos])
            }
        } else if (holder is VerticalViewHolder) {
            val vertViewHolder = holder
            vertViewHolder.onBindVert(sponsorList[holder.getAdapterPosition()])
            if (clientInfom != null &&
                    clientInfom.is_supplier_wishlist == "1") {
                vertViewHolder.ivWishlist.visibility = View.VISIBLE
            } else {
                vertViewHolder.ivWishlist.visibility = View.GONE
            }

            vertViewHolder.ivWishlist.setOnCheckedChangeListener(null)
            vertViewHolder.ivWishlist.isChecked = sponsorList[pos].Favourite == 1

            vertViewHolder.ivWishlist.setOnCheckedChangeListener { checkBox, isChecked ->
                mCallback?.onSponsorWishList(sponsorList[pos], parentPos,isChecked)
            }

            //  StaticFunction.INSTANCE.loadImage(sponsorList.get(pos).getLogo(), vertViewHolder.sdvImage, false);
            vertViewHolder.itemView.setOnClickListener {
                //  viewHolder.itemView.setEnabled(false);
                mCallback?.onSponsorDetail(sponsorList[pos])
            }

            vertViewHolder.ivCall.visibility = if (sponsorList[pos].supplierPhoneNumber?.isNotEmpty() == true && clientInfom?.isCallSupplier ?: "" == "1") {
                View.VISIBLE
            } else {
                View.GONE
            }
            vertViewHolder.tvSponsored.visibility=if(sponsorList[pos].is_subscribed=="1") View.VISIBLE else View.GONE
            vertViewHolder.ivCall.setOnClickListener {
                mCallback?.onSupplierCall(sponsorList[pos])
            }

        } else {
            val horViewHolder = holder as HorizonatlViewHolder

            horViewHolder.ivProduct.loadImage(sponsorList[pos].logo ?: "")
            horViewHolder.ivImage.loadImage(sponsorList[pos].logo ?: "")

            // StaticFunction.INSTANCE.loadImage(sponsorList.get(pos).getLogo(), horViewHolder.ivImage, false);

            if (mScreenType == AppDataType.Ecom.type || mScreenType == 5) {
                horViewHolder.rbSponsor.rating = java.lang.Float.valueOf(sponsorList[pos].rating!!)
                horViewHolder.tvRating.text = mContext!!.getString(R.string.reviews_text, sponsorList[pos].total_reviews)
                horViewHolder.tvSponsorLocation.text = sponsorList[pos].address
                loadImage(sponsorList[pos].logo, horViewHolder.ivProduct, false)
            }
            if (clientInfom != null &&
                    clientInfom.is_supplier_wishlist == "1") {
                horViewHolder.ivWishlist.visibility = View.VISIBLE
            } else {
                horViewHolder.ivWishlist.visibility = View.GONE
            }

            horViewHolder.ivWishlist.setOnCheckedChangeListener(null)
            horViewHolder.ivWishlist.isChecked = sponsorList[pos].Favourite == 1

            horViewHolder.ivWishlist.setOnCheckedChangeListener { checkBox, isChecked ->
                mCallback?.onSponsorWishList(sponsorList[pos], parentPos,isChecked)
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
                    horViewHolder.tvRatingNew.text = if (sponsorList[pos].rating ?: 0f > 0f) sponsorList[pos].rating.toString() else mContext?.getString(R.string.new_tag)

                } else {
                    horViewHolder.tvRatingNew.visibility = View.GONE
                    if (BuildConfig.CLIENT_CODE=="dailyooz_0544") {
                        horViewHolder.tvBottomSpsrName.textAlignment = View.TEXT_ALIGNMENT_TEXT_START

                    } else{
                        horViewHolder.tvBottomSpsrName.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    }

                }
            } else {
                horViewHolder.tvRatingNew.visibility = View.GONE
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

        }
    }

    override fun getItemCount(): Int {
        return sponsorList.size
    }

    interface SponsorDetail {
        fun onSponsorDetail(supplier: SupplierInArabicBean?)
        fun onSupplierCall(supplier: SupplierInArabicBean?)
        fun onSponsorWishList(supplier: SupplierInArabicBean?, parentPos: Int?, isChecked: Boolean)
        fun onBookNow(supplierData: SupplierInArabicBean)
    }

    override fun getItemViewType(position: Int): Int {
        return if (clientInfom?.is_skip_theme == "1" && isViewAllUi==false) {
            TYPE_SKIP_THEME
        } else if (mScreenType == AppDataType.HomeServ.type || mScreenType == AppDataType.Beauty.type ||  isViewAllUi==true ){
            TYPE_VERT
        } else {
            TYPE_HOR
        }
    }

    inner class HorizonatlViewHolder(private val bindingHor: ItemSponsorBinding) : RecyclerView.ViewHolder(bindingHor.root) {
        var ivImage: RoundedImageView  = itemView.ivSupplierCustom
        var ivProduct: ImageView = itemView.iv_supplier
        var tvSponsorName: TextView = itemView.tv_sponsor_name
        var tvRating: TextView = itemView.tv_rating
        var rbSponsor: RatingBar = itemView.rb_rating
        var tvSponsorLocation: TextView = itemView.tv_sponsor_adrs
        var grpSupplier: Group = itemView.gp_suplr
        var tvBottomSpsrName: TextView = itemView.tvBottomSpsrName
        var ivWishlist: CheckBox = itemView.iv_wishlist
        var clMain: CardView = itemView.clMain
        var itemSponsorContainer: ConstraintLayout = itemView.itemSponsorContainer
        val itemLayout: ConstraintLayout = itemView.itemLayout
        val groupClikat: Group = itemView.groupClikat
        var tvRatingNew: MaterialTextView = itemView.tvRating
    }


    inner class SkipRecommendedViewHolder(private val bindingVer: ItemSkipRecomendedBinding) : RecyclerView.ViewHolder(bindingVer.root) {
        fun onBindHor(supplierData: SupplierInArabicBean) {
            bindingVer.color = Configurations.colors
            bindingVer.ivSupplierImage.loadImage(supplierData.logo ?: "")
            bindingVer.tvSponsorName.text = supplierData.name
            bindingVer.tvDistance.text=mContext?.getString(R.string.km,supplierData.distance)
        }
    }

    inner class SupplierViewHolder(private val bindingHor: ItemSupplierLayoutHorizontalBinding) : RecyclerView.ViewHolder(bindingHor.root) {
        fun onBindHor(supplierData: SupplierInArabicBean) {
            bindingHor.tvSponsored.visibility=if(supplierData.is_subscribed=="1") View.VISIBLE else View.GONE
            bindingHor.color = Configurations.colors
            bindingHor.settingData = clientInfom
            bindingHor.supplierData = supplierData

            bindingHor.tvSupplierloc.text=if(clientInfom?.table_book_mac_theme=="1" && clientInfom.dynamic_home_screen_sections=="1"
                    && !supplierData.category.isNullOrEmpty() &&
                    StaticFunction.checkVisibility(clientInfom.show_supplier_categories, clientInfom.show_supplier_info_settings)){
                supplierData.category?.joinToString(","){ it.name?:"" }

            }else supplierData.address
            Log.e("categoor","........")
            if (clientInfom?.app_selected_theme == "3"  && clientInfom.is_table_booking == "1") {
                if (supplierData.is_dine_in == 1)
                    bindingHor.tvBookNow.visibility = View.VISIBLE
                else
                    bindingHor.tvBookNow.visibility = View.GONE

                if(appUtils.getCurrentTableData() != null)
                    bindingHor.tvBookNow.visibility = View.GONE

                bindingHor.tvBookNow.setOnClickListener {
                    mCallback?.onBookNow(supplierData)
                }

            }
        }
    }

    inner class VerticalViewHolder(private val bindingVert: ItemSponsorVerticalBinding) : RecyclerView.ViewHolder(bindingVert.root) {
        var sdvImage: ImageView = itemView.sdvImage
        var name: TextView = itemView.tvName
        var location: TextView = itemView.tvSupplierloc
        var rating: TextView = itemView.tv_rating
        var ivWishlist: CheckBox = itemView.iv_wishlist
        var ivCall: ImageView = itemView.ic_call
        var tvSponsored:TextView=itemView.tvSponsored

        fun onBindVert(list: SupplierInArabicBean) {
            bindingVert.sponsorlist = list
        }
    }

    companion object {
        private const val TYPE_HOR = 1
        private const val TYPE_VERT = 2
        private const val TYPE_SKIP_THEME = 3
    }

}
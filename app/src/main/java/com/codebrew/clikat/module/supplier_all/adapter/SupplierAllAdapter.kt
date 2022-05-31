package com.codebrew.clikat.module.supplier_all.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.base.EmptyListListener
import com.codebrew.clikat.data.constants.AppConstants.Companion.CURRENCY_SYMBOL
import com.codebrew.clikat.databinding.*
import com.codebrew.clikat.modal.AppGlobal
import com.codebrew.clikat.modal.SupplierList
import com.codebrew.clikat.modal.other.Category
import com.codebrew.clikat.modal.other.SettingModel.DataBean.SettingData
import com.codebrew.clikat.modal.other.SupplierDataBean
import com.codebrew.clikat.modal.other.SupplierInArabicBean
import com.codebrew.clikat.modal.other.TimeDataBean
import com.codebrew.clikat.utils.ClikatConstants
import com.codebrew.clikat.utils.GeneralFunctions
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.StaticFunction.getLanguage
import com.codebrew.clikat.utils.StaticFunction.loadImage
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SupplierAllAdapter(private val updatedList: MutableList<SupplierList>, private val appUtils: AppUtils, private val listener: EmptyListListener,internal var langCode:String?=null) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private var supplierList: MutableList<SupplierList>
    private val category_builder = StringBuilder()
    private var category_name=""
    private var mContext: Context? = null
    private var mCallback: SupplierListCallback? = null
    private var clientInform: SettingData? = null
    private var type: String? = null
    fun settingCallback(mCallback: SupplierListCallback?) {
        this.mCallback = mCallback
    }

    fun setIsSkipTheme(type: String) {
        this.type = type
    }

    fun settingClientInf(clientInform: SettingData?) {
        this.clientInform = clientInform
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        mContext = parent.context
        return when (viewType) {
            BRANCHES_LIST -> {
                when (type) {
                    "GRID" -> {
                        val bindingHor: ItemSupplierBranchesBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_supplier_branches,
                                parent, false)
                        SupplierSkipViewHolder(bindingHor)
                    }
                    "VERTICAL" -> {
                        val bindingHor: ItemSupplierVerticalBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_supplier_vertical, parent, false)
                        SupplierSkipVerticalViewHolder(bindingHor)
                    }
                    else -> {
                        val binding: ItemSupplierBranchBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                                R.layout.item_supplier_branch, parent, false)
                        binding.color = Configurations.colors
                        BranchesViewHolder(binding)
                    }
                }
            }
            else -> {

                if (clientInform?.is_carveQatar_home_theme == "1") {
                    val binding: ItemAllSupplierCraveBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                            R.layout.item_all_supplier_crave, parent, false)
                    binding.color = Configurations.colors
                    binding.drawables = Configurations.drawables
                    binding.isSupplierRating = clientInform?.is_supplier_rating == "1"
                    binding.strings = Configurations.strings
                    binding.settingData = clientInform
                    ViewHolder_row_crave(binding)
                } else {
                    val binding: ItemAllSupplierBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                            R.layout.item_all_supplier, parent, false)
                    binding.color = Configurations.colors
                    binding.drawables = Configurations.drawables
                    binding.isSupplierRating = clientInform?.is_supplier_rating == "1"
                    binding.strings = Configurations.strings
                    ViewHolder_row(binding)
                }
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ViewHolder_row) {
            val holder_row = holder
            val data = supplierList[position]

            holder_row.updateSupplierData(data)

            holder_row.gpPrice.visibility = View.GONE
            if (data.supplierBranchName == null || data.supplierBranchName!!.isEmpty()) {
                if(langCode.equals("14"))
                {
                    holder_row.tvStoreName.text=data.name
                }else{
                    holder_row.tvStoreName.text = data.name//branch_list?.get(position)?.name_arabic
                }

            } else {
                if(langCode.equals("14")) {
                    holder_row.tvStoreName.text = data.supplierBranchName
                }else{
                    holder_row.tvStoreName.text = data.supplierBranchName//branch_list?.get(position)?.name_arabic
                }
            }

            holder_row.tvReviewCount.text = "(" + data.totalReviews + " " + mContext?.resources?.getString(R.string.reviews) + ")"
            holder_row.tvMinOrderValue.text = CURRENCY_SYMBOL + " " + data.minOrder + ""
            holder_row.tvDeliveryTimeValue.text = GeneralFunctions.getFormattedTime(data.deliveryMinTime!!, mContext) + " - " + GeneralFunctions.getFormattedTime(data.deliveryMaxTime!!, mContext)
            holder_row.ratingBar.text = "" + data.rating
            loadImage(data.logo, holder_row.image, false)
            when (data.paymentMethod) {
                0 -> {
                    holder_row.ivPaymentCash.visibility = View.VISIBLE
                    holder_row.ivPaymentCard.visibility = View.GONE
                }
                1 -> {
                    holder_row.ivPaymentCash.visibility = View.GONE
                    holder_row.ivPaymentCard.visibility = View.VISIBLE
                }
                2 -> {
                    holder_row.ivPaymentCash.visibility = View.VISIBLE
                    holder_row.ivPaymentCard.visibility = View.VISIBLE
                }
            }

            if (data.commissionPackage == null) data.commissionPackage = 0
            when (data.commissionPackage) {
                0 -> {
                    holder_row.ivBadge.visibility = View.VISIBLE
                    holder_row.ivBadge.setImageResource(R.drawable.ic_badge_mini_silver)
                }
                1 -> {
                    holder_row.ivBadge.visibility = View.VISIBLE
                    holder_row.ivBadge.setImageResource(R.drawable.ic_badge_mini_bronze)
                }
                2 -> {
                    holder_row.ivBadge.visibility = View.VISIBLE
                    holder_row.ivBadge.setImageResource(R.drawable.ic_badge_mini_gold)
                }
                4 -> {
                    holder_row.ivBadge.visibility = View.VISIBLE
                    holder_row.ivBadge.setImageResource(R.drawable.ic_np)
                }
                else -> holder_row.ivBadge.visibility = View.GONE
            }
            if (data.isSponsor == 1) {
                holder_row.ivSponser.visibility = View.VISIBLE
                if (getLanguage(mContext) == ClikatConstants.LANGUAGE_OTHER) {
                    holder_row.ivSponser.rotation = -270f
                }
                holder_row.itemView.background = ContextCompat.getDrawable(mContext!!, R.drawable.card_sponser)
            } else {
                holder_row.ivSponser.visibility = View.GONE
                holder_row.itemView.background = ContextCompat.getDrawable(mContext!!, R.color.white)
            }

            holder_row.groupDeliveryTime.visibility = if (data.deliveryMinTime != null && data.deliveryMaxTime != null &&
                    StaticFunction.checkVisibility(clientInform?.show_supplier_delivery_timing, clientInform?.show_supplier_info_settings))
                View.VISIBLE else View.GONE

            holder_row.callSupplier.visibility = if (data.supplierPhoneNumber?.isNotEmpty() == true && clientInform?.isCallSupplier ?: "" == "1") {
                View.VISIBLE
            } else {
                View.GONE
            }

            holder_row.callSupplier.setOnClickListener {
                mCallback?.onCallSupplier(data)
            }
        } else if (holder is ViewHolder_row_crave) {
              val holder_row = holder
            val data = supplierList[position]

            holder_row.updateSupplierData(data)
            if(data.rating.toString().equals("null")||data.rating.toString().equals("")||data.rating.toString().equals("0")||data.rating.toString()==null)
            {
                holder_row.tv_rating.text = "0.0"
            }else{
                holder_row.tv_rating.text = data.rating.toString()
            }

            if (data!!.is_on_discount == 0) {
                holder.ivPercentage.visibility = View.GONE
            } else {
                holder.ivPercentage.visibility = View.VISIBLE
            }
            if (data!!.is_free_delivery == 0) {
                holder.ivCar.visibility = View.GONE
            } else {
                holder.ivCar.visibility = View.VISIBLE
            }
            category_builder.setLength(0)
            val type = object : TypeToken<List<Category>>() {}.type
            val listOfMap: List<Category> = Gson().fromJson(data!!.categories, type)
            for (i in 0 until listOfMap.size) {
                category_builder.append(listOfMap.get(i)?.category_name + ",")
                category_name = method(category_builder.toString()!!).toString()
                holder_row.tvSupplierCatType.text = category_name

            }


            holder_row.gpPrice.visibility = View.GONE
            if (data.supplierBranchName == null || data.supplierBranchName!!.isEmpty()) {
                if(langCode.equals("14"))
                {
                    holder_row.tvStoreName.text=data.name
                }else{
                    holder_row.tvStoreName.text = data.name_arabic//branch_list?.get(position)?.name_arabic
                }
            } else {
                if(langCode.equals("14")) {
                    holder_row.tvStoreName.text = data.supplierBranchName
                }else{
                    holder_row.tvStoreName.text = data.name_arabic//branch_list?.get(position)?.name_arabic
                }
            }
            holder_row.tvReviewCount.text = "(" + data.totalReviews + " " + mContext?.resources?.getString(R.string.reviews) + ")"
            holder_row.tvMinOrderValue.text = CURRENCY_SYMBOL + " " + data.minOrder + ""
            holder_row.tvDeliveryTimeValue.text = GeneralFunctions.getFormattedTime(data.deliveryMinTime!!, mContext) + " - " + GeneralFunctions.getFormattedTime(data.deliveryMaxTime!!, mContext)
//            holder_row.tvSupplier.text = GeneralFunctions.getFormattedTime(data.deliveryMinTime!!, mContext) + " - " + GeneralFunctions.getFormattedTime(data.deliveryMaxTime!!, mContext)
            holder_row.ratingBar.text = "" + data.rating
            loadImage(data.logo, holder_row.image, false)
            when (data.paymentMethod) {
                0 -> {
                    holder_row.ivPaymentCash.visibility = View.VISIBLE
                    holder_row.ivPaymentCard.visibility = View.GONE
                }
                1 -> {
                    holder_row.ivPaymentCash.visibility = View.GONE
                    holder_row.ivPaymentCard.visibility = View.VISIBLE
                }
                2 -> {
                    holder_row.ivPaymentCash.visibility = View.VISIBLE
                    holder_row.ivPaymentCard.visibility = View.VISIBLE
                }
            }

            if (data.commissionPackage == null) data.commissionPackage = 0
            when (data.commissionPackage) {
                0 -> {
                    holder_row.ivBadge.visibility = View.VISIBLE
                    holder_row.ivBadge.setImageResource(R.drawable.ic_badge_mini_silver)
                }
                1 -> {
                    holder_row.ivBadge.visibility = View.VISIBLE
                    holder_row.ivBadge.setImageResource(R.drawable.ic_badge_mini_bronze)
                }
                2 -> {
                    holder_row.ivBadge.visibility = View.VISIBLE
                    holder_row.ivBadge.setImageResource(R.drawable.ic_badge_mini_gold)
                }
                4 -> {
                    holder_row.ivBadge.visibility = View.VISIBLE
                    holder_row.ivBadge.setImageResource(R.drawable.ic_np)
                }
                else -> holder_row.ivBadge.visibility = View.GONE
            }
            if (data.isSponsor == 1) {
                holder_row.ivSponser.visibility = View.VISIBLE
                if (getLanguage(mContext) == ClikatConstants.LANGUAGE_OTHER) {
                    holder_row.ivSponser.rotation = -270f
                }
                holder_row.itemView.background = ContextCompat.getDrawable(mContext!!, R.drawable.card_sponser)
            } else {
                holder_row.ivSponser.visibility = View.GONE
                holder_row.itemView.background = ContextCompat.getDrawable(mContext!!, R.color.white)
            }

            holder_row.groupDeliveryTime.visibility = if (data.deliveryMinTime != null && data.deliveryMaxTime != null &&
                    StaticFunction.checkVisibility(clientInform?.show_supplier_delivery_timing, clientInform?.show_supplier_info_settings))
                View.VISIBLE else View.GONE

            holder_row.callSupplier.visibility = if (data.supplierPhoneNumber?.isNotEmpty() == true && clientInform?.isCallSupplier ?: "" == "1") {
                View.VISIBLE
            } else {
                View.GONE
            }

            holder_row.callSupplier.setOnClickListener {
                mCallback?.onCallSupplier(data)
            }
        }else if (holder is BranchesViewHolder) {

            holder.updateSupplierData(supplierList[position])
            holder.itemView.setOnClickListener {
                mCallback?.onSupplierListDetail(supplierList[position])
            }
        } else if (holder is SupplierSkipViewHolder) {
            holder.onBindHor(supplierList[position])

            holder.itemView.setOnClickListener {
                mCallback?.onSupplierListDetail(supplierList[position])
            }

        } else if (holder is SupplierSkipVerticalViewHolder) {
            holder.onBindHor(supplierList[position])

            holder.itemView.setOnClickListener {
                mCallback?.onSupplierListDetail(supplierList[position])
            }
        }
    }
    fun method(str: String?): String? {
        var str = str
        if (str != null && str.length > 0 && str[str.length - 1] == ',') {
            str = str.substring(0, str.length - 1)
        }
        return str
    }
    inner class SupplierSkipViewHolder(private val bindingHor: ItemSupplierBranchesBinding) : RecyclerView.ViewHolder(bindingHor.root) {
        init {
            bindingHor.ivWishList.setOnClickListener {
                mCallback?.onSupplierWishList(supplierList[adapterPosition], adapterPosition)
            }
        }

        fun onBindHor(supplierData: SupplierList) {
            bindingHor.color = Configurations.colors
            if (supplierData.supplierBranchName == null || supplierData.supplierBranchName?.isEmpty() == true) {
                bindingHor.tvName.text = supplierData.name
            } else {
                bindingHor.tvName.text = supplierData.supplierBranchName
            }
            bindingHor.tvDistance.text = itemView.context?.getString(R.string.km, supplierData.distance)
            bindingHor.tvTime.text = itemView.context?.getString(R.string.deliver_time_min, supplierData.deliveryMinTime.toString())
            bindingHor.tvRating.text = (supplierData.rating ?: 0f).toString()
            if (supplierData.Favourite == 1) {
                bindingHor.ivWishList.setImageResource(R.drawable.ic_favourite)
            } else {
                bindingHor.ivWishList.setImageResource(R.drawable.ic_unfavorite)
            }
            loadImage(supplierData.logo, bindingHor.ivImage, false)
        }
    }

    inner class SupplierSkipVerticalViewHolder(private val bindingVer: ItemSupplierVerticalBinding) : RecyclerView.ViewHolder(bindingVer.root) {
        init {
            bindingVer.ivWishlist.setOnClickListener {
                mCallback?.onSupplierWishList(supplierList[adapterPosition], adapterPosition)
            }

        }

        fun onBindHor(supplierData: SupplierList) {
            bindingVer.color = Configurations.colors
            if (supplierData.supplierBranchName == null || supplierData.supplierBranchName?.isEmpty() == true) {
                bindingVer.tvName.text = supplierData.name
            } else {
                bindingVer.tvName.text = supplierData.supplierBranchName
            }
            bindingVer.tvDistance.text = itemView.context?.getString(R.string.km, supplierData.distance)
            bindingVer.tvRating.text = supplierData.rating.toString()
            bindingVer.tvTime.text = itemView.context?.getString(R.string.deliver_time_min, supplierData.deliveryMinTime.toString())
            if (supplierData.Favourite == 1) {
                bindingVer.ivWishlist.setImageResource(R.drawable.ic_favourite)
            } else {
                bindingVer.ivWishlist.setImageResource(R.drawable.ic_unfavorite)
            }
            loadImage(supplierData.logo, bindingVer.ivImage, false)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    supplierList = updatedList
                } else {
                    val filteredList = mutableListOf<SupplierList>()
                    for (supplierBean in updatedList) {
                        if (supplierBean.name?.toLowerCase(DateTimeUtils.timeLocale)?.contains(charSequence) == true) {
                            filteredList.add(supplierBean)
                        }
                    }
                    supplierList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = supplierList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                supplierList = (filterResults.values as? MutableList<SupplierList>)
                        ?: mutableListOf()

                listener.onEmptyList(supplierList?.count() ?: 0)
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return supplierList.size
    }

    interface SupplierListCallback {
        fun onSupplierListDetail(supplierBean: SupplierList?)
        fun onCallSupplier(supplierBean: SupplierList?)
        fun onBookNow(supplierBean: SupplierList?)
        fun onSupplierWishList(supplier: SupplierList?, pos: Int?)
    }

    inner class ViewHolder_row_crave(private val binding: ItemAllSupplierCraveBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        var tvSupplierCatType: TextView = binding.tvSupplierCatType
        var tvSupplier: TextView = binding.tvSupplier
        var ivCar: TextView = binding.ivCar
        var ivPercentage: ImageView = binding.ivPercentage
        var tvStoreName: TextView = binding.tvStoreName
        var tvReviewCount: TextView = binding.tvReviewCount
        var tvMinOrder: TextView = binding.tvMinOrder
        var tvDeliveryTime: TextView = binding.tvDeliveryTime
        var tvMinOrderValue: TextView = binding.tvMinOrderValue
        var tvDeliveryTimeValue: TextView = binding.tvDeliveryTimeValue
        var tvPaymentOptions: TextView = binding.tvPaymentOptions
        var image: ImageView = binding.image
        var ivPaymentCard: ImageView = binding.ivPaymentCard
        var ivPaymentCash: ImageView = binding.ivPaymentCash
        var ivStatus: TextView = binding.ivStatus
        var ratingBar: TextView = binding.ratingBar
        var ivBadge: ImageView = binding.ivBadge
        var ivSponser: ImageView = binding.ivSponser
        var gpPrice: Group = binding.gpPrice
        var callSupplier: ImageView = binding.icCall
        var groupDeliveryTime: Group = binding.groupDeliveryTime
        var tv_rating:TextView=binding.tvRating
        override fun onClick(v: View) {
            mCallback?.onSupplierListDetail(supplierList[adapterPosition])
        }

        fun updateSupplierData(data: SupplierList) {
            val type = object : TypeToken<List<TimeDataBean>>() {}.type
            val listOfMap: List<TimeDataBean> = Gson().fromJson(data?.timings, type)
            data.isOpen = this@SupplierAllAdapter.appUtils.checkResturntTiming(listOfMap)
            binding.supplierData = data
            if(!data!!.isOpen&&data!!.is_scheduled==0)
            {
                (binding as ItemAllSupplierCraveBinding).tvPreClose.visibility=View.VISIBLE
            }else{
                (binding as ItemAllSupplierCraveBinding).tvPreClose.visibility=View.GONE
            }

            if (clientInform?.app_selected_theme == "3" && clientInform?.is_table_booking == "1") {
                if (data.is_dine_in == 1)
                    binding.tvBookNow.visibility = View.VISIBLE
                else
                    binding.tvBookNow.visibility = View.GONE

                if (appUtils.getCurrentTableData() != null)
                    binding.tvBookNow.visibility = View.GONE

                binding.tvBookNow.setOnClickListener {
                    mCallback?.onBookNow(data)
                }

            }
        }

        init {

            binding.root.setOnClickListener(this)
            tvDeliveryTime.typeface = AppGlobal.regular
            tvMinOrder.typeface = AppGlobal.regular
            tvStoreName.typeface = AppGlobal.regular
            tvReviewCount.typeface = AppGlobal.regular
            tvPaymentOptions.typeface = AppGlobal.regular
            tvMinOrderValue.typeface = AppGlobal.regular
            tvDeliveryTimeValue.typeface = AppGlobal.regular
            ratingBar.typeface = AppGlobal.regular
            ivStatus.typeface = AppGlobal.semi_bold
        }
    }

    inner class ViewHolder_row(private val binding: ItemAllSupplierBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        var tvStoreName: TextView = binding.tvStoreName
        var tvReviewCount: TextView = binding.tvReviewCount
        var tvMinOrder: TextView = binding.tvMinOrder
        var tvDeliveryTime: TextView = binding.tvDeliveryTime
        var tvMinOrderValue: TextView = binding.tvMinOrderValue
        var tvDeliveryTimeValue: TextView = binding.tvDeliveryTimeValue
        var tvPaymentOptions: TextView = binding.tvPaymentOptions
        var image: ImageView = binding.image
        var ivPaymentCard: ImageView = binding.ivPaymentCard
        var ivPaymentCash: ImageView = binding.ivPaymentCash
        var ivStatus: TextView = binding.ivStatus
        var ratingBar: TextView = binding.ratingBar
        var ivBadge: ImageView = binding.ivBadge
        var ivSponser: ImageView = binding.ivSponser
        var gpPrice: Group = binding.gpPrice
        var callSupplier: ImageView = binding.icCall
        var groupDeliveryTime: Group = binding.groupDeliveryTime

        override fun onClick(v: View) {
            mCallback?.onSupplierListDetail(supplierList[adapterPosition])
        }

        fun updateSupplierData(data: SupplierList) {
            val type = object : TypeToken<List<TimeDataBean>>() {}.type
            val listOfMap: List<TimeDataBean> = Gson().fromJson(data?.timings, type)
            data.isOpen = this@SupplierAllAdapter.appUtils.checkResturntTiming(listOfMap)
            binding.supplierData = data

            if (clientInform?.app_selected_theme == "3" && clientInform?.is_table_booking == "1") {
                if (data.is_dine_in == 1)
                    binding.tvBookNow.visibility = View.VISIBLE
                else
                    binding.tvBookNow.visibility = View.GONE

                if (appUtils.getCurrentTableData() != null)
                    binding.tvBookNow.visibility = View.GONE

                binding.tvBookNow.setOnClickListener {
                    mCallback?.onBookNow(data)
                }

            }
        }

        init {

            binding.root.setOnClickListener(this)
            tvDeliveryTime.typeface = AppGlobal.regular
            tvMinOrder.typeface = AppGlobal.regular
            tvStoreName.typeface = AppGlobal.regular
            tvReviewCount.typeface = AppGlobal.regular
            tvPaymentOptions.typeface = AppGlobal.regular
            tvMinOrderValue.typeface = AppGlobal.regular
            tvDeliveryTimeValue.typeface = AppGlobal.regular
            ratingBar.typeface = AppGlobal.regular
            ivStatus.typeface = AppGlobal.semi_bold
        }
    }

    inner class BranchesViewHolder(private val binding: ItemSupplierBranchBinding) : RecyclerView.ViewHolder(binding.root) {

        fun updateSupplierData(data: SupplierList) {
            if (data.supplierBranchName == null || data.supplierBranchName?.isEmpty() == true) {
                binding.tvName.text = data.name
            } else {
                binding.tvName.text = data.supplierBranchName
            }
            binding.tvDistance.text = itemView.context?.getString(R.string.km, data.distance)
            binding.tvTime.text = itemView.context?.getString(R.string.deliver_time_min, data.deliveryMinTime.toString())
            binding.tvRating.text = (data.rating ?: 0f).toString()
            loadImage(data.logo, binding.ivImage, false)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (clientInform?.is_skip_theme == "1") {
            BRANCHES_LIST
        } else SUPPLIERS_LIST
    }

    init {
        supplierList = updatedList
    }

    companion object {
        const val SUPPLIERS_LIST = 1
        const val BRANCHES_LIST = 2
    }

}
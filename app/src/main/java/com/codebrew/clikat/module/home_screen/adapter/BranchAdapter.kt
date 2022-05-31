package com.codebrew.clikat.module.home_screen.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.base.EmptyListListener
import com.codebrew.clikat.data.FoodAppType
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

class BranchAdapter(private val updatedList: MutableList<SupplierDataBean>,private var deliveryType: Int, private val appUtils: AppUtils, private val listener: EmptyListListener, internal var langCode: String? = null) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private var supplierList: MutableList<SupplierDataBean>
    private val category_builder = StringBuilder()
    private var category_name = ""
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
    fun setDeliveryType(type: Int) {
        deliveryType = type
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        mContext = parent.context
        return when (viewType) {
            BRANCHES_LIST -> {
                val binding: ItemSupplierCraveNewLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                        R.layout.item_supplier_crave_new_layout, parent, false)
                binding.color = Configurations.colors
                binding.drawables = Configurations.drawables

                binding.strings = Configurations.strings
                binding.settingData = clientInform
                ViewHolder_row_crave(binding)
            }
            else -> {
                val binding: ItemSupplierCraveNewLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                        R.layout.item_supplier_crave_new_layout, parent, false)
                binding.color = Configurations.colors
                binding.drawables = Configurations.drawables

                binding.strings = Configurations.strings
                binding.settingData = clientInform
                ViewHolder_row_crave(binding)

            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ViewHolder_row_crave) {
            val holder_row = holder
            val data = supplierList[position]

            holder_row.updateSupplierData(data)


holder.tvSupplier.text=data.address?:""
            if (langCode.equals("14")) {
                holder_row.tvStoreName.text = data.name
            } else {
                holder_row.tvStoreName.text = data.branch_name_arabic//branch_list?.get(position)?.name_arabic
            }
            var imageurl=data.supplier_image.toString()
        var    imagePathCon=imageurl.replace(" ", "%20");

            Glide.with(mContext!!).load(imagePathCon).into(holder_row.sdvImage)

            //set category
//            category_builder.setLength(0)
//            val type = object : TypeToken<List<Category>>() {}.type
//            val listOfMap: List<Category> = Gson().fromJson(data!!.categories, type)
//            for (i in 0 until listOfMap.size) {
//                category_builder.append(listOfMap.get(i)?.category_name + ",")
//                category_name = method(category_builder.toString()!!).toString()
//                holder_row.tvSupplierCatType.text = category_name
//
//            }
//            val type1 = object : TypeToken<List<TimeDataBean>>() {}.type
//            val listOfMap1: List<TimeDataBean> = Gson().fromJson(data?.timings, type1)
//            if(data?.is_scheduled==0)
//            {
//
//                data?.isOpen= appUtils.checkResturntTiming(listOfMap1)
//            }
//
//
//            if(!data!!.isOpen&&data!!.is_scheduled==0)
//            {
//                if (langCode.toString().equals("15")) {
//
//                    holder_row.rl_close.visibility=View.VISIBLE
//                    holder_row.rl_closeE.visibility=View.GONE
//                } else {
//                    holder_row.rl_close.visibility=View.GONE
//                    holder_row.rl_closeE.visibility=View.VISIBLE
//                }
//
//            }else{
                holder_row.rl_close.visibility=View.GONE
                holder_row.rl_closeE.visibility=View.GONE
//            }



            holder_row.tvSupplierInf.visibility = if (data?.delivery_max_time != null && StaticFunction.checkVisibility(clientInform?.show_supplier_delivery_timing,
                            clientInform?.show_supplier_info_settings) && deliveryType != FoodAppType.Pickup.foodType)
                View.GONE else View.GONE

            holder_row .ratingBar.rating = data?.rating?.toFloat() ?: 0f


        }
    }

    fun method(str: String?): String? {
        var str = str
        if (str != null && str.length > 0 && str[str.length - 1] == ',') {
            str = str.substring(0, str.length - 1)
        }
        return str
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    supplierList = updatedList
                } else {
                    val filteredList = mutableListOf<SupplierDataBean>()
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
                supplierList = (filterResults.values as? MutableList<SupplierDataBean>)
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
        fun onSupplierListDetail(supplierBean: SupplierDataBean?)
    }

    inner class ViewHolder_row_crave(private val binding: ItemSupplierCraveNewLayoutBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        var tvSupplierCatType: TextView = binding.tvSupplierloc
        var tvSupplier: TextView = binding.tvSupplierloc
        var tvStoreName: TextView = binding.tvName
        var sdvImage: ImageView = binding.sdvImage
var rl_close:RelativeLayout=binding.rlClose
        var rl_closeE:RelativeLayout=binding.rlCloseE
        var tvSupplierInf:TextView=binding.tvSupplierInf
        var ratingBar:RatingBar=binding.ratingBar


        override fun onClick(v: View) {
            mCallback?.onSupplierListDetail(supplierList[adapterPosition])
        }

        fun updateSupplierData(data: SupplierDataBean) {
//            val type = object : TypeToken<List<TimeDataBean>>() {}.type
//            val listOfMap: List<TimeDataBean> = Gson().fromJson(data?.timings, type)
//            data.isOpen = this@BranchAdapter.appUtils.checkResturntTiming(listOfMap)
            binding.supplierData = data

            if (clientInform?.app_selected_theme == "3" && clientInform?.is_table_booking == "1") {
                if (data.is_dine_in == 1)
                    binding.tvBookNow.visibility = View.GONE
                else
                    binding.tvBookNow.visibility = View.GONE

                if (appUtils.getCurrentTableData() != null)
                    binding.tvBookNow.visibility = View.GONE



            }
        }

        init {

            binding.root.setOnClickListener(this)

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
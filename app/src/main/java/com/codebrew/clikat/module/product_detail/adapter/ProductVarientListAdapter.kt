package com.codebrew.clikat.module.product_detail.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.databinding.ItemFilterCheckcolorBinding
import com.codebrew.clikat.databinding.ItemFilterChecksizeBinding
import com.codebrew.clikat.modal.other.VariantValuesBean
import com.codebrew.clikat.module.product_detail.ProductDetails.Companion.filterData
import com.codebrew.clikat.utils.StaticFunction.varientColor
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.material.button.MaterialButton
import java.util.*

class ProductVarientListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    private val SIZE = 1
    private val COLOR = 2
    private var viewType: String? = null
    private val varientList: MutableList<VariantValuesBean> = ArrayList()
    private val varientFilteredList: MutableList<VariantValuesBean> = ArrayList()
    private var mVarientCallback: FilterVarientCallback? = null
    private var mposition = 0
    private var filterStatus = false
    fun settingCallback(mVarientCallback: FilterVarientCallback?, viewType: String?, mChecklist: List<VariantValuesBean>?, mposition: Int) {
        varientFilteredList.clear()
        varientList.clear()
        varientList.addAll(mChecklist!!)
        this.mVarientCallback = mVarientCallback
        this.viewType = viewType
        varientFilteredList.addAll(varientList)
        this.mposition = mposition
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        return if (i == SIZE) {
            val binding: ItemFilterChecksizeBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.context),
                    R.layout.item_filter_checksize, viewGroup, false)
            binding.color = Configurations.colors
            ViewHolder(binding.root)
        } else {
            val binding: ItemFilterCheckcolorBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.context),
                    R.layout.item_filter_checkcolor, viewGroup, false)
            binding.color = Configurations.colors
            ColorViewHolder(binding.root)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        val pos = viewHolder.adapterPosition
        if (viewHolder is ViewHolder) {
            val holder = viewHolder
            holder.tvName.text = varientFilteredList[pos].variant_value
            if (filterData[mposition] == varientFilteredList[pos].unid) {
                holder.tvName.setBackgroundColor(Color.parseColor(Configurations.colors.primaryColor))
                holder.tvName.setTextColor(Color.parseColor(Configurations.colors.appBackground))
                //checked
            } else {
                holder.tvName.setBackgroundColor(Color.parseColor(Configurations.colors.appBackground))
                //  holder.tvName.setStrokeWidth(2);
                holder.tvName.strokeColor = ColorStateList.valueOf(Color.parseColor(Configurations.colors.primaryColor))
                holder.tvName.setTextColor(Color.parseColor(Configurations.colors.textHead))
                //unchecked
            }
        } else {
            val colorViewHolder = viewHolder as ColorViewHolder
            colorViewHolder.tvShape.background = varientColor(varientFilteredList[pos].variant_value!!, Configurations.colors.primaryColor!!, GradientDrawable.RADIAL_GRADIENT)
            if (filterData[mposition] == varientFilteredList[pos].unid) {
                colorViewHolder.itemLayout.background = varientColor(Configurations.colors.appBackground, Configurations.colors.primaryColor!!, GradientDrawable.RECTANGLE)
                //checked
            } else {
                colorViewHolder.itemLayout.background = varientColor(Configurations.colors.appBackground, Configurations.colors.textSubhead, GradientDrawable.RECTANGLE)
                //unchecked
            }
            if (filterStatus && varientFilteredList[pos].isVisiblity_status!!) {
                colorViewHolder.itemLayout.visibility = View.VISIBLE
            } else {
                colorViewHolder.itemLayout.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return varientFilteredList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (viewType == "color") COLOR else SIZE
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val filteredListNew: MutableList<VariantValuesBean> = ArrayList()
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    filteredListNew.clear()
                    filteredListNew.addAll(varientList)
                } else {
                    val filteredList: MutableList<VariantValuesBean> = ArrayList()
                    for (row in varientList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.filterstatus != 1 && charString.contains(row.product_id.toString())) {
                            row.filter_product_id = row.product_id.toString()
                            row.isVisiblity_status = charString.contains(row.product_id.toString())
                            filteredList.add(row)
                        }
                    }
                    filteredListNew.clear()
                    if (filteredList.isEmpty()) {
                        filteredListNew.addAll(varientList)
                    } else filteredListNew.addAll(filteredList)
                }
                val filterResults = FilterResults()
                filterResults.values = filteredListNew
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                varientFilteredList.clear()
                varientFilteredList.addAll((filterResults.values as List<VariantValuesBean>))
                filterStatus = true
                notifyDataSetChanged()
            }
        }
    }

    interface FilterVarientCallback {
        fun onFilterVarient(variantValuesBean: VariantValuesBean?, varientId: String?, adpaterPosition: Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: MaterialButton

        init {
            tvName = itemView.findViewById(R.id.tv_name)
            tvName.setOnClickListener { v: View? ->
                if (adapterPosition != -1) {
                    filterData.put(mposition, varientFilteredList[adapterPosition].unid!!)
                    if (varientFilteredList[adapterPosition].filter_product_id != null) mVarientCallback!!.onFilterVarient(varientFilteredList[adapterPosition], varientFilteredList[adapterPosition].filter_product_id.toString(), mposition) else mVarientCallback!!.onFilterVarient(varientFilteredList[adapterPosition], varientFilteredList[adapterPosition].product_id.toString(), mposition)
                }
            }
        }
    }

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvShape: TextView
        var itemLayout: ConstraintLayout

        init {
            tvShape = itemView.findViewById(R.id.tv_shape)
            itemLayout = itemView.findViewById(R.id.itemLayout)
            itemLayout.setOnClickListener { v: View? ->
                filterData.put(mposition, varientFilteredList[adapterPosition].unid!!)
                if (varientFilteredList[adapterPosition].filter_product_id != null) mVarientCallback!!.onFilterVarient(varientFilteredList[adapterPosition], varientFilteredList[adapterPosition].filter_product_id.toString(), mposition) else mVarientCallback!!.onFilterVarient(varientFilteredList[adapterPosition], varientFilteredList[adapterPosition].product_id.toString(), mposition)
            }
        }
    }
}
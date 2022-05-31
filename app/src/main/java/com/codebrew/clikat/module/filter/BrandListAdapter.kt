package com.codebrew.clikat.module.filter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.databinding.ItemFilterChecksizeBinding
import com.codebrew.clikat.modal.other.FilterVarientCatModel
import com.codebrew.clikat.module.filter.BottomSheetFragment.Companion.varientData
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.item_filter_checksize.view.*

class BrandListAdapter(private val mChecklist: MutableList<FilterVarientCatModel.BrandsBean>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val checkedPosition = -1

    private val colorWhite: String by lazy { Configurations.colors.appBackground!! }
    private val appColor: String by lazy { Configurations.colors.primaryColor ?: "" }
    private val greyColor: String by lazy { Configurations.colors.textSubhead }

    private val textGrey: Int by lazy { Color.parseColor(Configurations.colors.textSubhead) }
    private val textAppcolor: Int by lazy { Color.parseColor(Configurations.colors.primaryColor) }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {

        val binding = DataBindingUtil.inflate<ItemFilterChecksizeBinding>(LayoutInflater.from(viewGroup.context),
                R.layout.item_filter_checksize, viewGroup, false)
        binding.color = Configurations.colors
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {

        val pos = viewHolder.adapterPosition

        val holder = viewHolder as ViewHolder

        holder.tvName!!.text = mChecklist[pos].name


        if (varientData.brandId.size > 0) {
            if (varientData.brandId.contains(mChecklist[pos].id)) {

                viewHolder.tvName.setTextColor(Color.parseColor(colorWhite))
                viewHolder.tvName.setBackgroundColor(Color.parseColor(Configurations.colors.primaryColor))
                //checked
            } else {
                viewHolder.tvName.setBackgroundColor(Color.parseColor(Configurations.colors.appBackground))
                viewHolder.tvName.strokeWidth = 2
                viewHolder.tvName.strokeColor = ColorStateList.valueOf(Color.parseColor(Configurations.colors.primaryColor))
                viewHolder.tvName.setTextColor(Color.parseColor(Configurations.colors.textHead))
                //unchecked
            }
        } else {
            if (checkedPosition == i) {
                varientData.brandId.add(mChecklist[pos].id)
                mChecklist[pos].isStatus = true
                viewHolder.tvName.setTextColor(Color.parseColor(colorWhite))
                viewHolder.tvName.setBackgroundColor(Color.parseColor(Configurations.colors.primaryColor))
                //checked
            } else {
                viewHolder.tvName.setBackgroundColor(Color.parseColor(Configurations.colors.appBackground))
                viewHolder.tvName.strokeWidth = 2
                viewHolder.tvName.strokeColor = ColorStateList.valueOf(Color.parseColor(Configurations.colors.primaryColor))
                viewHolder.tvName.setTextColor(Color.parseColor(Configurations.colors.textHead))
                //unchecked
            }
        }

        holder.onBindClick()


    }

    override fun getItemCount(): Int {
        return mChecklist.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvName = itemView.tv_name


        fun onBindClick() {

            tvName.setOnClickListener {

                if (mChecklist[adapterPosition].isStatus) {
                    varientData.brandId.remove(mChecklist[adapterPosition].id)
                    mChecklist[adapterPosition].isStatus = false
                } else {
                    varientData.brandId.add(mChecklist[adapterPosition].id)
                    mChecklist[adapterPosition].isStatus = true
                }
                notifyDataSetChanged()
            }
        }
    }

}

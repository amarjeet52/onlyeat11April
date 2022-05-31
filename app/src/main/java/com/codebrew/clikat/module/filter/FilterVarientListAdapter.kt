package com.codebrew.clikat.module.filter

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.databinding.ItemFilterCheckcolorBinding
import com.codebrew.clikat.databinding.ItemFilterChecksizeBinding
import com.codebrew.clikat.modal.other.FilterVarientCatModel
import com.codebrew.clikat.module.filter.BottomSheetFragment.Companion.varientData
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.item_filter_checkcolor.view.*
import kotlinx.android.synthetic.main.item_filter_checksize.view.*

class FilterVarientListAdapter(private val mChecklist: List<FilterVarientCatModel.DataBean.VariantValuesBean>, private val viewType: String, private val listPosition: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val checkedPosition = -1

    private val SIZE = 1

    private val COLOR = 2

    private val colorWhite: String by lazy { Configurations.colors.appBackground }
    private val appColor: String by lazy { Configurations.colors.primaryColor ?: "" }
    private val greyColor: String by lazy { Configurations.colors.textSubhead }

    private val textGrey: Int by lazy { Color.parseColor(Configurations.colors.textSubhead) }
    private val textAppcolor: Int by lazy { Color.parseColor(Configurations.colors.primaryColor) }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {

        return if (i == SIZE) {
            val binding = DataBindingUtil.inflate<ItemFilterChecksizeBinding>(LayoutInflater.from(viewGroup.context),
                    R.layout.item_filter_checksize, viewGroup, false)
            binding.color = Configurations.colors
            ViewHolder(binding.root)
        } else {
            val binding = DataBindingUtil.inflate<ItemFilterCheckcolorBinding>(LayoutInflater.from(viewGroup.context),
                    R.layout.item_filter_checkcolor, viewGroup, false)
            binding.color = Configurations.colors
            ColorViewHolder(binding.root)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {

        val pos = viewHolder.adapterPosition


        if (viewHolder is ViewHolder) {

            viewHolder.tvName.text = mChecklist[pos].value


            if (varientData.varientID.size > 0) {
                if (varientData.varientID.contains(mChecklist[pos].id)) {
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
                    varientData.varientID.add(mChecklist[pos].id)
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

            viewHolder.tvName.setOnClickListener {

                if (mChecklist[pos].isStatus) {
                    varientData.varientID.remove(mChecklist[pos].id)
                    mChecklist[pos].isStatus = false
                } else {
                    varientData.varientID.add(mChecklist[pos].id)
                    mChecklist[pos].isStatus = true
                }
                notifyDataSetChanged()
            }


        } else {
            val colorViewHolder = viewHolder as ColorViewHolder

            //colorViewHolder.tvShape?.background = StaticFunction.varientColor(mChecklist[pos].value, appColor, GradientDrawable.RADIAL_GRADIENT)

            try {
                colorViewHolder.tvShape?.setBackgroundColor(Color.parseColor(mChecklist[pos].value))
            }catch (ex:IllegalArgumentException){
                ex.printStackTrace()
            }

            colorViewHolder.tvShape?.text =""


            if (varientData.varientID.size > 0) {
                if (varientData.varientID.contains(mChecklist[pos].id)) {
                    colorViewHolder.itemLayout?.background = StaticFunction.varientColor(colorWhite, appColor, GradientDrawable.RECTANGLE)
                    //checked
                } else {
                    colorViewHolder.itemLayout?.background = StaticFunction.varientColor(colorWhite, greyColor, GradientDrawable.RECTANGLE)
                    //unchecked
                }
            } else {
                if (checkedPosition == i) {
                    varientData.varientID.add(mChecklist[pos].id)
                    mChecklist[pos].isStatus = true
                    colorViewHolder.itemLayout?.background = StaticFunction.varientColor(colorWhite, appColor, GradientDrawable.RECTANGLE)
                    //checked
                } else {
                    colorViewHolder.itemLayout?.background = StaticFunction.varientColor(colorWhite, greyColor, GradientDrawable.RECTANGLE)
                    //unchecked
                }
            }

            colorViewHolder.itemLayout.setOnClickListener {

                if (mChecklist[pos].isStatus) {
                    varientData.varientID.remove(mChecklist[pos].id)
                    mChecklist[pos].isStatus = false
                } else {
                    varientData.varientID.add(mChecklist[pos].id)
                    mChecklist[pos].isStatus = true
                }
                notifyDataSetChanged()
            }

        }


    }

    override fun getItemCount(): Int {
        return mChecklist.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (viewType == "color")
            COLOR
        else
            SIZE
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvName = itemView.tv_name
    }

    inner class ColorViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvShape = itemView.tv_shape
        var itemLayout = itemView.itemLayout
    }
}

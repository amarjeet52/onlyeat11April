package com.codebrew.clikat.module.questions.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.model.api.AnswersData
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.utils.Utils

class AnswersAdapter(var activity: Activity, var listData: ArrayList<AnswersData>, var isMultipleSelection: Boolean,
                     private val settingData: SettingModel.DataBean.SettingData?,val currency:Currency?)
    : RecyclerView.Adapter<AnswersAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.answers_adapter_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listData.size
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var answerData = listData[position]

        holder.cbAnswer.text = answerData.optionLabel
        holder.rbAnswer.text = answerData.optionLabel


        val priceTag = if (answerData.flatValue > 0) {
            holder.itemView.context.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(answerData.flatValue,settingData,currency))
        } else {
            "${answerData.percentageValue} % of service"
        }

        holder.tvRbPrice.text = priceTag
        holder.tvCbPrice.text = priceTag

        if (isMultipleSelection) {
            holder.grpCheck.visibility = View.VISIBLE
            holder.grpRadio.visibility = View.GONE
        } else {
            holder.grpRadio.visibility = View.VISIBLE
            holder.grpCheck.visibility = View.GONE
        }

        holder.rbAnswer.isChecked = answerData.isChecked
        holder.cbAnswer.isChecked = answerData.isChecked


        holder.itemView.setOnClickListener {
            val data = answerData
            if (isMultipleSelection) {
                data.isChecked = !data.isChecked
            } else {
                listData.map {
                    it.isChecked = false
                }
                data.isChecked = true
            }

            answerData = data
            notifyDataSetChanged()
        }

        holder.cbAnswer.setOnClickListener {
            holder.itemView.callOnClick()
        }

        holder.rbAnswer.setOnClickListener {
            holder.itemView.callOnClick()
        }
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var rbAnswer: RadioButton = view.findViewById(R.id.rb_answer) as RadioButton
        var cbAnswer: CheckBox = view.findViewById(R.id.cb_answer) as CheckBox
        var tvRbPrice: TextView = view.findViewById(R.id.tv_price_rb) as TextView
        var tvCbPrice: TextView = view.findViewById(R.id.tv_price_cb) as TextView
        var grpRadio: Group = view.findViewById(R.id.group_radio) as Group
        var grpCheck: Group = view.findViewById(R.id.group_check) as Group

    }

}
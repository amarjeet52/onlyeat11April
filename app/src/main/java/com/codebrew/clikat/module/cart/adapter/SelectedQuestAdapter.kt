package com.codebrew.clikat.module.cart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.QuestionList
import com.codebrew.clikat.modal.other.SettingModel
import kotlinx.android.synthetic.main.item_select_quest.view.*

class SelectedQuestAdapter(private val mContext: Context, private val list: List<QuestionList?>,private val settingData: SettingModel.DataBean.SettingData?,
private val selectedCurrency:Currency?) : RecyclerView.Adapter<SelectedQuestAdapter.SelectQuestViewHolder>() {

    var selectAnsAdapter: SelectAnsAdapter? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectQuestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_select_quest, parent, false)
        return SelectQuestViewHolder(view)
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: SelectQuestViewHolder, position: Int) {

        //  val mQuest=list[position]
        holder.tvQuest.text = "${position + 1}. ${list[position]?.question}"

        selectAnsAdapter = SelectAnsAdapter(mContext, list[position]?.optionsList
                ?: mutableListOf(),settingData,selectedCurrency)
        holder.rView.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        holder.rView.adapter = selectAnsAdapter
    }


    inner class SelectQuestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var tvQuest: TextView = itemView.tvQuestion
        internal var rView: RecyclerView = itemView.findViewById(R.id.rvSelectAns)

    }


}
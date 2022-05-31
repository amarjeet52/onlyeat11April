package com.codebrew.clikat.module.help

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.data.ReceiverType
import com.codebrew.clikat.data.model.api.orderDetail.Agent
import com.codebrew.clikat.databinding.LayoutHelpBindingBinding
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.module.all_categories.adapter.CategoryListAdapter
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.user_chat.UserChatActivity
import kotlinx.android.synthetic.main.item_service_list.view.*
import kotlinx.android.synthetic.main.layout_help_binding.view.*


class HelpAdapter(private val mValues: List<HelpModel>,
                  var activity: Activity,
                  private val mListener: HelpListener?,)
: RecyclerView.Adapter<HelpAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding: LayoutHelpBindingBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.layout_help_binding, parent, false)

        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]

            holder.tvItem.text = item.name
holder.mView.setOnClickListener {
    mListener?.onClickListen()
}
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val tvItem: TextView = mView.tvItem


    }
    interface  HelpListener
    {
        fun onClickListen()
    }

}
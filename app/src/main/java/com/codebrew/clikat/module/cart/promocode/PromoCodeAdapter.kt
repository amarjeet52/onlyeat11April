package com.codebrew.clikat.module.cart.promocode


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.data.model.others.PromoCodeItem
import com.codebrew.clikat.databinding.ItemNotificationBinding
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.item_notification.view.*

class PromoCodeAdapter(
        private val callback: OnPromoCodeClicked?,val dateTime:DateTimeUtils,private val showApply:Boolean)
    : RecyclerView.Adapter<PromoCodeAdapter.ViewHolder>() {

    private var list = ArrayList<PromoCodeItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = DataBindingUtil.inflate<ItemNotificationBinding>(LayoutInflater.from(parent.context),
                R.layout.item_notification, parent, false)
        binding.color = Configurations.colors
        binding.strings = Configurations.strings
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.onBind(item)
        with(holder.mView) {
            tag = item
        }
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {

        init {
            mView.tvApply.setOnClickListener {
                callback?.onItemClicked(list[adapterPosition])
            }
        }

        fun onBind(item: PromoCodeItem) {

            mView.tvApply.visibility = if(showApply) View.VISIBLE else View.GONE
            mView.ivIcon.visibility = View.GONE
            mView.tvValidTill.visibility=View.VISIBLE
            mView.tvDescription?.text = itemView.context.getString(R.string.code_,item.promoCode)
            mView.tvTime?.text = item.promoDesc
            val date= dateTime.convertDateOneToAnother(
                    item.endDate ?: "",
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                    "dd MMM yyyy"
            ) ?: ""
            mView.tvValidTill.text=itemView.context.getString(R.string.valid_till,date)
        }
    }

    fun addList(isFirstPage: Boolean?, list: ArrayList<PromoCodeItem>) {
        if (isFirstPage == true)
            this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    interface OnPromoCodeClicked {
        fun onItemClicked(item: PromoCodeItem?)
    }
}

package com.codebrew.clikat.module.notification.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.extension.loadImage
import com.codebrew.clikat.databinding.ItemNotificationBinding
import com.codebrew.clikat.modal.Notification
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.StaticFunction.convertFromUtcFormat
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.item_notification.view.*


class NotificationListAdapter(
        private val callback: OnNotificationClicked?, val clientInform: SettingModel.DataBean.SettingData?)
    : RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {

    private var list = mutableListOf<Notification>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = DataBindingUtil.inflate<ItemNotificationBinding>(LayoutInflater.from(parent.context),
                R.layout.item_notification, parent, false)
        binding.color = Configurations.colors
        binding.strings = Configurations.strings
        binding.isSkipTheme = if (clientInform?.is_skip_theme == "1") 1 else 0
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
            mView.setOnClickListener {
                callback?.onItemClicked(list[adapterPosition])
            }
        }

        fun onBind(item: Notification) {
            if (clientInform?.is_skip_theme == "1") {
                mView.cvTop?.cardElevation = 4f
                mView.cvTop?.radius = 8f
                mView.viewBottom?.visibility = View.INVISIBLE
                mView.viewLayout?.setPadding(StaticFunction.pxFromDp(8,mView.context), 0, StaticFunction.pxFromDp(8,mView.context), 0)
            }
            mView.ivIcon.loadImage(item.logo ?: "")
            mView.tvDescription?.text = item.notification_message ?: ""
            /*mView.tvTime?.text = GetTimeAgo.getTimeAgo(convertFromUtcFormat(item.created_on), mView.context)*/
            mView.tvTime?.text = convertFromUtcFormat("dd MMM, yyyy hh:mm a", item.created_on ?: "")
        }
    }

    fun addList(isFirstPage: Boolean?, list: ArrayList<Notification>) {
        if (isFirstPage == true)
            this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    interface OnNotificationClicked {
        fun onItemClicked(item: Notification?)
    }
}

package com.codebrew.clikat.dialog_flow.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.data.DgFlow
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.others.DialogDataItem
import com.codebrew.clikat.databinding.DgflowChatLeftBinding
import com.codebrew.clikat.databinding.DgflowChatRightBinding
import com.codebrew.clikat.databinding.DgflowItemListBinding
import com.codebrew.clikat.databinding.DgflowShowDialogBinding
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private const val TEXT_LEFT_VIEW_TYPE = 1
private const val TEXT_RIGHT_VIEW_TYPE = 2
private const val LIST_LEFT_VIEW_TYPE = 3
private const val DIALOG_LEFT_VIEW_TYPE = 4


private val colors = Configurations.colors

private  val mProdAdapter = DialogProdAdapter()
private  val mAdrsAdapter = DialogAddressAdapter()

class DialogChatAdapter(val clientInf: SettingModel.DataBean.SettingData?,val currency:Currency?,val clickListener: ChatListener) :
        ListAdapter<DialogChatAdapter.DgFlowItem, RecyclerView.ViewHolder>(DgFlowListDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)


    fun addItmSubmitList(list: List<DialogDataItem>?) {
        adapterScope.launch {
            val items = list?.map {

                when (it.dialogType) {
                    DgFlow.TextLeftType.dgFlowType -> {
                        DgFlowItem.DgFlowLeftChat(it)
                    }
                    DgFlow.ListLeftType.dgFlowType -> {
                        DgFlowItem.DgFlowList(it)
                    }
                    DgFlow.PopUpLeftType.dgFlowType -> {
                        DgFlowItem.DgFlowPopup(it)
                    }
                    else -> {
                        DgFlowItem.DgFlowRightChat(it)
                    }
                }
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TextLeftViewHolder -> {
                val nightItem = getItem(position) as DgFlowItem.DgFlowLeftChat
                holder.bind(nightItem.chatData)
            }

            is TextRightViewHolder -> {
                val nightItem = getItem(position) as DgFlowItem.DgFlowRightChat
                holder.bind(nightItem.chatData, clickListener)
            }

            is ListLeftViewHolder -> {
                val headItem = getItem(position) as DgFlowItem.DgFlowList
                holder.bind(headItem.chatData, clickListener,clientInf,currency)
            }

            is DialogLeftViewHolder -> {
                val headItem = getItem(position) as DgFlowItem.DgFlowPopup
                holder.bind(headItem.chatData, clickListener)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TEXT_LEFT_VIEW_TYPE -> TextLeftViewHolder.from(parent)
            TEXT_RIGHT_VIEW_TYPE -> TextRightViewHolder.from(parent)
            LIST_LEFT_VIEW_TYPE -> ListLeftViewHolder.from(parent)
            DIALOG_LEFT_VIEW_TYPE -> DialogLeftViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DgFlowItem.DgFlowLeftChat -> TEXT_LEFT_VIEW_TYPE
            is DgFlowItem.DgFlowRightChat -> TEXT_RIGHT_VIEW_TYPE
            is DgFlowItem.DgFlowList -> LIST_LEFT_VIEW_TYPE
            is DgFlowItem.DgFlowPopup -> DIALOG_LEFT_VIEW_TYPE
            else -> -1
        }
    }


    class TextLeftViewHolder private constructor(val binding: DgflowChatLeftBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DialogDataItem) {
            binding.msg = item.message
            binding.color = colors
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): TextLeftViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DgflowChatLeftBinding.inflate(layoutInflater, parent, false)
                return TextLeftViewHolder(binding)
            }
        }
    }


    class ListLeftViewHolder private constructor(val binding: DgflowItemListBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DialogDataItem, listener: ChatListener, clientInf: SettingModel.DataBean.SettingData?,currency:Currency?) {

            binding.color = colors

            if(item.items.isNotEmpty())
            {
                mProdAdapter.settingCallback(clientInf,currency,ProdListener {
                    listener.onProdClick(it)
                })
                binding.rvDgflowItem.adapter = mProdAdapter
                mProdAdapter.submitItemList(item.items)
            }else
            {
                mAdrsAdapter.settingCallback(AddressListener {
                    listener.onAdrsClick(it)
                })
                binding.rvDgflowItem.adapter = mAdrsAdapter
                mAdrsAdapter.submitItemList(item.address)
            }

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ListLeftViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DgflowItemListBinding.inflate(layoutInflater, parent, false)
                return ListLeftViewHolder(binding)
            }
        }
    }

    class TextRightViewHolder private constructor(val binding: DgflowChatRightBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DialogDataItem, listener: ChatListener) {
            binding.msg = item.message
            binding.color = colors
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): TextRightViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DgflowChatRightBinding.inflate(layoutInflater, parent, false)
                return TextRightViewHolder(binding)
            }
        }
    }

    class DialogLeftViewHolder private constructor(val binding: DgflowShowDialogBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DialogDataItem, listener: ChatListener) {
            binding.currency = AppConstants.CURRENCY_SYMBOL
            binding.listener = listener
            binding.dialogItem = item.popUpItem
            binding.color = colors
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): DialogLeftViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DgflowShowDialogBinding.inflate(layoutInflater, parent, false)
                return DialogLeftViewHolder(binding)
            }
        }
    }


    class DgFlowListDiffCallback : DiffUtil.ItemCallback<DgFlowItem>() {
        override fun areItemsTheSame(oldItem: DgFlowItem, newItem: DgFlowItem): Boolean {
            return oldItem.chatData.dialogType == newItem.chatData.dialogType
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: DgFlowItem, newItem: DgFlowItem): Boolean {
            return oldItem == newItem
        }
    }


    class ChatListener(val clickListener: (model: DialogDataItem) -> Unit, val prodListener: (model: ProductDataBean) -> Unit,
                       val adrsListener: (model: AddressBean) -> Unit, val poplistener: (yes:String,no:String,type:Int,inputType:String) -> Unit) {
        fun onClick(chatBean: DialogDataItem) = clickListener(chatBean)
        fun onProdClick(prodBean: ProductDataBean) = prodListener(prodBean)
        fun onAdrsClick(adrsBean: AddressBean) = adrsListener(adrsBean)
        fun onPopClick(yes: String,no: String,type: Int,input:String)=poplistener(yes, no,type,input)
    }

    sealed class DgFlowItem {


        data class DgFlowLeftChat(val messageData: DialogDataItem) : DgFlowItem() {
            override val chatData = messageData
        }

        data class DgFlowRightChat(val messageData: DialogDataItem) : DgFlowItem() {
            override val chatData = messageData
        }

        data class DgFlowPopup(val messageData: DialogDataItem) : DgFlowItem() {
            override val chatData = messageData
        }

        data class DgFlowList(val messageData: DialogDataItem) : DgFlowItem() {
            override val chatData = messageData
        }

        abstract val chatData: DialogDataItem
    }
}


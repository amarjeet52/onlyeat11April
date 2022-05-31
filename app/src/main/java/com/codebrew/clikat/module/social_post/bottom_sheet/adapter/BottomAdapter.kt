package com.codebrew.clikat.module.social_post.bottom_sheet.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.data.SPType
import com.codebrew.clikat.data.model.api.CommentBean
import com.codebrew.clikat.databinding.*
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SupplierDataBean
import com.codebrew.clikat.module.social_post.custom_model.SocialDataItem
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


private const val SUPPLIER_VIEW_TYPE = 1
private const val PRODUCT_VIEW_TYPE = 2
private const val COMMENT_VIEW_TYPE = 3
private const val LIKE_VIEW_TYPE = 4


private val colors = Configurations.colors

class BottomAdapter(val clickListener: SPListener) :
        ListAdapter<BottomAdapter.SocialItem, RecyclerView.ViewHolder>(DgFlowListDiffCallback()), Filterable {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private var mFilteredList: List<SocialDataItem>? = null
    private var mLstUser: List<SocialDataItem>? = null

    fun updateList(lstUser: MutableList<SocialDataItem>) {
        mLstUser = lstUser
        mFilteredList = lstUser
    }

    fun addItmSubmitList(list: List<SocialDataItem>?) {
        adapterScope.launch {
            val items = list?.map {

                when (it.socialType) {
                    SPType.SupplierType.type -> {
                        SocialItem.SPSuplier(it)
                    }
                    SPType.LikeType.type -> {
                        SocialItem.SPLike(it)
                    }
                    SPType.CommentType.type -> {
                        SocialItem.SPComment(it)
                    }
                    else -> {
                        SocialItem.SPProduct(it)
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
            is SupplierViewHolder -> {
                val nightItem = getItem(position) as SocialItem.SPSuplier
                holder.bind(nightItem.socialItem.supplierList, clickListener)
            }

            is ProductViewHolder -> {
                val nightItem = getItem(position) as SocialItem.SPProduct
                holder.bind(nightItem.socialItem.productList, clickListener)
            }

            is CommentViewHolder -> {
                val nightItem = getItem(position) as SocialItem.SPComment
                holder.bind(nightItem.socialItem.commentBean)
            }

            is LikeViewHolder -> {
                val nightItem = getItem(position) as SocialItem.SPLike
                holder.bind(nightItem.socialItem.likeBean)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SUPPLIER_VIEW_TYPE -> SupplierViewHolder.from(parent)
            PRODUCT_VIEW_TYPE -> ProductViewHolder.from(parent)
            COMMENT_VIEW_TYPE -> CommentViewHolder.from(parent)
            LIKE_VIEW_TYPE -> LikeViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SocialItem.SPProduct -> PRODUCT_VIEW_TYPE
            is SocialItem.SPSuplier -> SUPPLIER_VIEW_TYPE
            is SocialItem.SPComment -> COMMENT_VIEW_TYPE
            is SocialItem.SPLike -> LIKE_VIEW_TYPE
            else -> -1
        }
    }


    class SupplierViewHolder private constructor(val binding: ItemChooseSupplierBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SupplierDataBean?, listener: SPListener) {
            binding.supplierData = item
            binding.color = colors
            binding.sociallistener = listener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): SupplierViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemChooseSupplierBinding.inflate(layoutInflater, parent, false)
                return SupplierViewHolder(binding)
            }
        }
    }

    class CommentViewHolder private constructor(val binding: ItemSocialCommentBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CommentBean?) {
            binding.dataItem = item
            binding.color = colors
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): CommentViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSocialCommentBinding.inflate(layoutInflater, parent, false)
                return CommentViewHolder(binding)
            }
        }
    }

    class LikeViewHolder private constructor(val binding: ItemSocialLikesBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CommentBean?) {
            binding.dataItem = item
            binding.color = colors
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): LikeViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSocialLikesBinding.inflate(layoutInflater, parent, false)
                return LikeViewHolder(binding)
            }
        }
    }


    class ProductViewHolder private constructor(val binding: DgflowItemProductBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ProductDataBean?, listener: SPListener) {
            binding.color = colors
            binding.productItem = item
            binding.sociallistener = listener
            binding.isSocialLyt = true
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ProductViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DgflowItemProductBinding.inflate(layoutInflater, parent, false)
                return ProductViewHolder(binding)
            }
        }
    }


    class DgFlowListDiffCallback : DiffUtil.ItemCallback<SocialItem>() {
        override fun areItemsTheSame(oldItem: SocialItem, newItem: SocialItem): Boolean {
            return oldItem.socialData == newItem.socialData
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: SocialItem, newItem: SocialItem): Boolean {
            return oldItem == newItem
        }
    }


    class SPListener(val productListener: (model: ProductDataBean) -> Unit, val supplierListener: (model: SupplierDataBean) -> Unit) {
        fun onProductClick(chatBean: ProductDataBean) = productListener(chatBean)
        fun onSupplierClick(chatBean: SupplierDataBean) = supplierListener(chatBean)
    }

    sealed class SocialItem {


        data class SPSuplier(val socialItem: SocialDataItem) : SocialItem() {
            override val socialData = socialItem
        }

        data class SPProduct(val socialItem: SocialDataItem) : SocialItem() {
            override val socialData = socialItem
        }

        data class SPComment(val socialItem: SocialDataItem) : SocialItem() {
            override val socialData = socialItem
        }

        data class SPLike(val socialItem: SocialDataItem) : SocialItem() {
            override val socialData = socialItem
        }

        abstract val socialData: SocialDataItem
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {

                val charString = charSequence.toString()

                mFilteredList = if (charString.isEmpty()) {
                    mLstUser
                } else {
                     mLstUser?.filter {
                                when (it.socialType) {
                                    SPType.SupplierType.type -> {
                                        it.supplierList?.name?.toLowerCase(Locale.getDefault())?.contains(charString) == true
                                    }
                                    SPType.CommentType.type -> {
                                        it.commentBean?.user_name?.toLowerCase(Locale.getDefault())?.contains(charString) == true
                                    }
                                    SPType.LikeType.type -> {
                                        it.likeBean?.user_name?.toLowerCase(Locale.getDefault())?.contains(charString) == true
                                    }
                                    else -> {
                                        it.productList?.name?.toLowerCase(Locale.getDefault())?.contains(charString) == true
                                    }
                                }
                            }
                            ?.toMutableList()
                }

                val filterResults = FilterResults()
                filterResults.values = mFilteredList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                addItmSubmitList(filterResults.values as MutableList<SocialDataItem>)
            }
        }
    }
}


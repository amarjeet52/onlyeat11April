package com.codebrew.clikat.module.social_post.home.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.extension.setSafeOnClickListener
import com.codebrew.clikat.data.model.api.PostItem
import com.codebrew.clikat.data.model.others.ImageListModel
import com.codebrew.clikat.databinding.PostsRecyclerItemBinding
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.module.social_post.interfaces.RecyclerActionListener
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.posts_recycler_item.view.*

class PostsRecyclerAdapter : RecyclerView.Adapter<PostsRecyclerAdapter.ViewHolder>() {
    private lateinit var actionListener: RecyclerActionListener
    private lateinit var collection: ArrayList<PostItem?>
    private var isUserLoggedIn = false

    private var signup: PojoSignUp?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: PostsRecyclerItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.posts_recycler_item,
                parent,
                false
        )

        binding.color = Configurations.colors
        binding.isUserLoggedIn = isUserLoggedIn
        return ViewHolder(binding)
    }


    override fun getItemCount(): Int = collection.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            holder.onBind(collection[position])

    inner class ViewHolder(val binding: PostsRecyclerItemBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            itemView.tvLikes?.setOnClickListener(this)
            itemView.favImage?.setOnClickListener(this)
            itemView.ivComments?.setOnClickListener(this)
            itemView.tvComments?.setOnClickListener(this)
            itemView.ivShare?.setOnClickListener(this)
            itemView.tvSharePost?.setOnClickListener(this)
            itemView.OrderNow?.setOnClickListener(this)
            itemView.post_comment?.setOnClickListener(this)
            itemView.ivPostOptions?.setOnClickListener(this)
            itemView.tvSupplierName?.setOnClickListener(this)

        }

        fun onBind(dataItem: PostItem?) = with(itemView) {
            binding.dataItem = dataItem
//            val formatter = DateTimeFormatter.ofPattern(
//                    "yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
//            val x = OffsetDateTime.parse(dataItem?.createdAt, formatter)
//                    .toInstant()
//                    .toEpochMilli()
//            binding.tvPostDate.text = GetTimeAgo.getTimeAgo(x, itemView.context)

            StaticFunction.loadUserImage(dataItem?.userImage ?: "", itemView.postUserImage, true)
            StaticFunction.loadUserImage(signup?.data?.user_image?: "", itemView.currentUSerImage, true)

            if (dataItem?.postImages?.isNotEmpty() == true) {
                val imagesAdapter = PostsImagesRecyclerAdapter()
                imagesAdapter.setOnActionListener(actionListener)
                imagesAdapter.setListData(dataItem.postImages as ArrayList<ImageListModel?>)
                imagesRecyclerVIew?.adapter = imagesAdapter
            }
        }

        override fun onClick(p0: View?) {
            val selectedDataHolder = collection[adapterPosition]
            when (p0) {
                 itemView.favImage -> {
                     if(selectedDataHolder?.totalLikes?:0>=0) {
                         actionListener.onLikePostClicked(selectedDataHolder, adapterPosition)
                     }
                }

                itemView.tvLikes->{
                    actionListener.onShowPostLikes(selectedDataHolder, adapterPosition)
                }
                itemView.ivPostOptions->{
                    actionListener.onMoreOption(itemView.ivPostOptions,selectedDataHolder, adapterPosition)
                }

                itemView.tvSupplierName->{
                    actionListener.onSupplierDetail(selectedDataHolder, adapterPosition)
                }

                itemView.ivComments, itemView.tvComments -> actionListener.onShowPostCommentsClicked(selectedDataHolder, adapterPosition)
                itemView.ivShare, itemView.tvSharePost -> actionListener.onSharePostClicked(selectedDataHolder)
                itemView.OrderNow -> actionListener.onOrderNowClicked(selectedDataHolder,adapterPosition)
                itemView.post_comment -> {
                    actionListener.onAddComment(selectedDataHolder, adapterPosition, itemView.ed_add_comment.text.trim().toString())
                    itemView.ed_add_comment.setText("")
                }

            }
        }
    }

    fun setOnActionListener(actionListener: RecyclerActionListener) {
        this.actionListener = actionListener
    }

    fun setListData(collection: ArrayList<PostItem?>) {
        this.collection = collection
    }

    fun isUserLoggedIn(isUserLoggedIn: Boolean) {
        this.isUserLoggedIn = isUserLoggedIn
    }

    fun updateUser(signup: PojoSignUp?) {
        this.signup=signup
    }
}
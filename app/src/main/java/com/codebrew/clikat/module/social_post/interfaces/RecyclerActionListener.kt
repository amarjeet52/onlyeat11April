package com.codebrew.clikat.module.social_post.interfaces

import android.view.View
import android.widget.ImageView
import com.codebrew.clikat.data.model.api.PostItem

interface RecyclerActionListener {
    fun onLikePostClicked(postItem: PostItem?,position:Int)
    fun onShowPostCommentsClicked(postItem: PostItem?,position:Int)
    fun onShowPostLikes(postItem: PostItem?,position:Int)
    fun onSharePostClicked(postItem: PostItem?)
    fun onOrderNowClicked(postItem: PostItem?, position: Int)
    fun onAddComment(postItem: PostItem?, position: Int, comment: String)
    fun onMoreOption(view: View?, postItem: PostItem?, position: Int)
    fun onSupplierDetail(postItem: PostItem?, position: Int)
    fun onProdItemUpdate(image: String, postImage: ImageView)
}
package com.codebrew.clikat.module.social_post.interfaces

import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.data.model.api.CommentBean
import com.codebrew.clikat.data.model.api.PostItem
import com.codebrew.clikat.module.social_post.custom_model.SocialPostInput

interface SocialPostNavigator:BaseInterface {

    fun onSocialPost(socialPostInput: SocialPostInput){}

    fun onPostCreated(){}

    fun onCommentReponse(commentList: MutableList<CommentBean>, postItem: PostItem?){}

    fun onLikeReponse(likeList: MutableList<CommentBean>, postItem: PostItem?){}

    fun onPostComment(){}

    fun onPostLike(likeStatus:Int){}

    fun onReportRequest(head: String, desc: String) {}

    fun onReportResponse(message: String) {}

    fun onBlockUserResp(message: String) {}

    fun onPostDetail(postItems: List<PostItem>) {}

    fun onUploadPic(message: String, image: String) {}
}
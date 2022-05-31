package com.codebrew.clikat.module.social_post

import android.content.Context
import android.widget.EditText
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.CommonUtils
import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.*
import com.codebrew.clikat.data.model.api.SuccessModel
import com.codebrew.clikat.data.model.others.CreatePostInput
import com.codebrew.clikat.data.network.ApiResponse
import com.codebrew.clikat.modal.ExampleCommon
import com.codebrew.clikat.modal.other.*
import com.codebrew.clikat.module.social_post.custom_model.SocialPostInput
import com.codebrew.clikat.module.social_post.custom_model.SocialSupplierBean
import com.codebrew.clikat.module.social_post.interfaces.SocialPostNavigator
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File

class SocialPostViewModel(dataManager: DataManager) : BaseViewModel<SocialPostNavigator>(dataManager) {

    val socialResponseObserver = MutableLiveData<PagingResult<ResponseData>>()

    val supplierCount = ObservableInt(0)

    val productCount = ObservableInt(0)

    val supplierLiveData by lazy { SingleLiveEvent<List<SupplierDataBean>>() }

    val productLiveData by lazy { SingleLiveEvent<SocialSupplierBean>() }

    val imageLiveData: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val isLikeLoading = ObservableBoolean(false)

    var skip: Int? = 0
    private var isLastReceived = false

    fun validForPaging(): Boolean = !isLoading.get() && !isLastReceived


    fun setLikeLoading(isLikeLoad: Boolean) {
        this.isLikeLoading.set(isLikeLoad)
    }

    fun getUserPosts(requestsHolder: HashMap<String, String>, isFirstPage: Boolean) {

        if (isFirstPage) {
            skip = 0
            isLastReceived = false
        }

        requestsHolder["limit"] = AppConstants.LIMIT.toString()
        requestsHolder["offset"] = (skip ?: 0).toString()

        setIsLoading(true)
        dataManager.getUserPostsList(requestsHolder)
                .observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.handlePostsResponse(it, isFirstPage) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    private fun handlePostsResponse(it: PostsResponseModel?, firstPage: Boolean) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                val receivedGroups = it.data
                if ((receivedGroups?.list?.size ?: 0) < AppConstants.LIMIT) {
                    Timber.i("Last group is received")
                    isLastReceived = true
                } else {
                    Timber.i("Next page for topic groups is available")
                    skip = skip?.plus(AppConstants.LIMIT)
                }
                socialResponseObserver.value = PagingResult(firstPage, it.data)
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
            }
        }



        setIsLoading(false)

        when (it?.status) {
            NetworkConstants.SUCCESS -> {

            }
            else -> {
                it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
            }
        }
    }


    fun markPostFavUnfav(postId: Int, likeStatus: Int) {
        setLikeLoading(true)

        val callApi: Observable<SuccessModel>? = if (likeStatus == 1) {
            dataManager.removeLike(dataManager.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING).toString(), postId.toString())
        } else {
            dataManager.addLike(postId.toString())
        }

        callApi?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.handleLikeResponse(it, likeStatus) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    private fun handleLikeResponse(it: SuccessModel?, likeStatus: Int) {
        setLikeLoading(false)
        when (it?.statusCode) {
            NetworkConstants.SUCCESS -> {
                navigator.onPostLike(likeStatus)
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
            }
        }
    }


    fun getSupplierList() {
        setIsLoading(true)

        val param = dataManager.updateUserInf()

        compositeDisposable.add(dataManager.getSupplierList(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.handleSuppliers(it) }, { this.handleError(it) })
        )
    }

    private fun handleSuppliers(it: HomeSupplierModel?) {
        setIsLoading(false)
        setIsList(it?.data?.size ?: 0)
        setSupplierCount(it?.data?.size ?: 0)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                supplierLiveData.value = it.data
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it?.message?.let { it1 -> navigator.onErrorOccur(it1.toString()) }
            }
        }
    }


    fun getProductList(supplierId: String) {

        setIsLoading(true)

        val param = dataManager.updateUserInf()
        param["supplier_id"] = supplierId
        compositeDisposable.add(dataManager.getProductLst(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.handleProductList(it) }, {
                    this.handleError(it)
                })
        )
    }

    private fun handleProductList(it: SuplierProdListModel?) {
        setIsLoading(false)
        setProductCount(0)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                productLiveData.value = SocialSupplierBean(it.data?.supplier_detail,it.data?.product?.flatMap { product ->
                    mutableListOf<ProductDataBean>().also {
                        it.addAll(product.value ?: mutableListOf())
                    }
                }?.toMutableList())

                setProductCount(productLiveData.value?.prodList?.size ?: 0)
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it?.message?.let { it1 -> navigator.onErrorOccur(it1.toString()?:"") }
            }
        }
    }

    fun uploadImage(image: String) {
        setIsLoading(true)

        val file = File(image)
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val partImage = MultipartBody.Part.createFormData("file", file.name, requestBody)

        compositeDisposable.add(dataManager.uploadFile(partImage)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.imageResponse(it) }, { this.handleError(it) })
        )
    }

    private fun imageResponse(it: ApiResponse<Any>?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                imageLiveData.value = it.data.toString()
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it?.msg?.let { it1 -> navigator.onErrorOccur(it1.toString()) }
            }
        }
    }

    fun createUserPost(socialInput: SocialPostInput) {
        setIsLoading(true)

        val model = CreatePostInput()

        with(model)
        {
            description = socialInput.post_desc
            heading = socialInput.post_head

            socialInput.supplier_data?.let {
                if (it.id == 0) return@let
                supplier_id = it.id

                if (it.supplier_branch_id != 0) {
                    branch_id = it.supplier_branch_id
                }
            }

            if (socialInput.id != null && socialInput.isEdit == true) {
                id = socialInput.id
            } else {
                if (dataManager.getCurrentUserLoggedIn()) {
                    user_id = dataManager.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING).toString().toInt()
                }
            }


            socialInput.product_data?.let {
                product_id = it.product_id
            }

            socialInput.imageList?.let {
                if (it.size ?: 0 > 0) {
                    post_images = it.map { it?.image ?: "" }
                }
            }


        }


        val callApi = if (socialInput.isEdit == true) {
            dataManager.updatePost(model)
        } else {
            dataManager.createPost(model)
        }

        callApi.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.handleCreatePostResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    private fun handleCreatePostResponse(it: SuccessModel?) {
        setIsLoading(false)

        when (it?.statusCode) {
            NetworkConstants.SUCCESS -> {
                navigator.onPostCreated()
            }
            else -> {
                it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
            }
        }
    }


    fun getUserCommentLike(postItem: PostItem?, type: String) {
        setIsLoading(true)

        val hashMap = hashMapOf<String, String>("post_id" to postItem?.id.toString())

        var callApi: Observable<SocialCommentLikes>? = null

        callApi = if (type == "comment") {
            dataManager.getSocialComments(hashMap)
        } else {
            dataManager.getSocialLikes(hashMap)
        }

        compositeDisposable.add(callApi
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.handleComments(it, type, postItem) }, { this.handleError(it) })
        )
    }

    private fun handleComments(it: SocialCommentLikes?, type: String, postItem: PostItem?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                if (type == "comment") {
                    navigator.onCommentReponse(it.data.list, postItem)
                } else {
                    navigator.onLikeReponse(it.data.list, postItem)
                }
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
            }
        }
    }


    fun addComment(postId: Int, comment: String) {
        setIsLoading(true)

        compositeDisposable.add(dataManager.addComment(postId.toString(), comment)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.handleCommentResponse(it) }, { this.handleError(it) })
        )
    }


    private fun handleCommentResponse(it: SuccessModel?) {
        setIsLoading(false)
        when (it?.statusCode) {
            NetworkConstants.SUCCESS -> {
                navigator.onPostComment()
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
            }
        }
    }

    fun reportPost(param: HashMap<String, String>) {
        setIsLoading(true)

        param["user_id"] = dataManager.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING).toString()

        compositeDisposable.add(dataManager.postReport(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.handleReportPost(it) }, { this.handleError(it) })
        )
    }

    private fun handleReportPost(it: SuccessModel) {
        setIsLoading(false)
        when (it.statusCode) {
            NetworkConstants.SUCCESS -> {
                navigator.onReportResponse(it.message)
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it.message.let { it1 -> navigator.onErrorOccur(it1) }
            }
        }
    }


    fun blockUser(param: HashMap<String, String>) {
        setIsLoading(true)

        //{"blocked_by_user_id":302,"blocked_user_id":282,"is_block":1}

        param["blocked_by_user_id"] = dataManager.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING).toString()
        param["is_block"] = "1"

        compositeDisposable.add(dataManager.blockUser(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.handleBlockUser(it) }, { this.handleError(it) })
        )
    }

    private fun handleBlockUser(it: SuccessModel) {
        setIsLoading(false)
        when (it.statusCode) {
            NetworkConstants.SUCCESS -> {
                navigator.onBlockUserResp(it.message)
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it.message.let { it1 -> navigator.onErrorOccur(it1) }
            }
        }
    }


    fun deletePost(post_id: String) {
        setIsLoading(true)

        compositeDisposable.add(dataManager.deletePost(post_id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.handleBlockUser(it) }, { this.handleError(it) })
        )
    }


    fun postDetails(postId: String) {
        setIsLoading(true)


        compositeDisposable.add(dataManager.postDetails(postId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.handlePostItem(it) }, { this.handleError(it) })
        )
    }

    private fun handlePostItem(it: ApiResponse<List<PostItem>>) {
        setIsLoading(false)
        when (it.status) {
            NetworkConstants.SUCCESS -> {
                navigator.onPostDetail(it.data ?: emptyList())
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it.msg.let { it1 -> navigator.onErrorOccur(it1.toString()) }
            }
        }
    }

    fun uploadProfImage(image: String) {
        setImageLoading(true)

        val file = File(image)
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val partImage = MultipartBody.Part.createFormData("profilePic", file.name, requestBody)

        dataManager.uploadSingleImage(CommonUtils.convrtReqBdy(dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString())
                , partImage)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.changePicResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }

    }

    private fun changePicResponse(it: ExampleCommon?) {
        setImageLoading(false)

        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                navigator.onUploadPic(it.message, it.data.image)
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
            }
        }
    }

    private fun handleError(e: Throwable) {
        CommonUtils.changebaseUrl(dataManager.getRetrofitUtl(), BuildConfig.BASE_URL)
        setIsLoading(false)
        handleErrorMsg(e).let {
            if (it == NetworkConstants.AUTH_MSG) {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            } else {
                navigator.onErrorOccur(it)
            }
        }
    }

    fun setSupplierCount(isList: Int) {
        this.supplierCount.set(isList)
    }

    fun setProductCount(isList: Int) {
        this.productCount.set(isList)
    }


    fun validateSocialPostDesc(context: Context, head: EditText, desc: EditText, socialInput: SocialPostInput?) {
        when {
            head.text.isEmpty() -> {
                navigator.onErrorOccur(context.getString(R.string.enter_head))
            }
            desc.text.isEmpty() -> {
                navigator.onErrorOccur(context.getString(R.string.enter_description))
            }
            else -> {

                socialInput?.post_head = head.text.toString().trim()
                socialInput?.post_desc = desc.text.toString().trim()

                socialInput?.let {
                    navigator.onSocialPost(it)
                }

            }
        }
    }

    fun validateReportPost(context: Context, head: EditText, desc: EditText) {
        when {
            head.text.isEmpty() -> {
                navigator.onErrorOccur(context.getString(R.string.enter_head))
            }
            desc.text.isEmpty() -> {
                navigator.onErrorOccur(context.getString(R.string.enter_description))
            }
            else -> {
                navigator.onReportRequest(head.text.toString().trim(), desc.text.toString().trim())
            }
        }
    }

}
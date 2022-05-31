package com.codebrew.clikat.module.social_post.home

import android.app.Activity
import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateUtils
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.BR
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.activities.ImageSHow
import com.codebrew.clikat.app_utils.*
import com.codebrew.clikat.app_utils.dialogintrface.ImageDialgFragment
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.CommentBean
import com.codebrew.clikat.data.model.api.PostItem
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragSocialPostHomeBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.other.SupplierDataBean
import com.codebrew.clikat.module.product.product_listing.DialogListener
import com.codebrew.clikat.module.social_post.SocialPostViewModel
import com.codebrew.clikat.module.social_post.bottom_sheet.BottomSocialSheetFrag
import com.codebrew.clikat.module.social_post.custom_model.BottomDataItem
import com.codebrew.clikat.module.social_post.custom_model.SocialPostInput
import com.codebrew.clikat.module.social_post.custom_model.SocialSupplierBean
import com.codebrew.clikat.module.social_post.home.adapters.PostsRecyclerAdapter
import com.codebrew.clikat.module.social_post.interfaces.BottomActionListener
import com.codebrew.clikat.module.social_post.interfaces.RecyclerActionListener
import com.codebrew.clikat.module.social_post.interfaces.SocialPostNavigator
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.GeneralFunctions
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.gson.Gson
import com.codebrew.clikat.intrface.ImageCallback
import kotlinx.android.synthetic.main.activity_order_details.*
import kotlinx.android.synthetic.main.frag_social_post_home.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.IOException
import javax.inject.Inject

const val CREATE_POST_REQUEST = 145
const val ASK_QUES_REQUEST = 146
const val COMMENT_LIST_REQUEST = 147
const val LIKES_LIST_REQUEST = 148
const val FAV_POST_REQUEST = 149
const val POST_ACTION_REQUEST = 150

class PostsHomeFragment : BaseFragment<FragSocialPostHomeBinding, SocialPostViewModel>(), RecyclerActionListener,
        SocialPostNavigator, BottomActionListener, DialogListener, ImageCallback,EasyPermissions.PermissionCallbacks {

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var mDataManager: PreferenceHelper

    @Inject
    lateinit var mDialogsUtil: DialogsUtil

    @Inject
    lateinit var mDateUtils: DateTimeUtils

    @Inject
    lateinit var imageUtils: ImageUtility

    @Inject
    lateinit var permissionFile: PermissionFile

    private var photoFile: File? = null

    private val imageDialog by lazy { ImageDialgFragment() }

    private var recyclerAdapter = PostsRecyclerAdapter()
    private var mViewModel: SocialPostViewModel? = null
    private var mBinding: FragSocialPostHomeBinding? = null
    private val collection = ArrayList<PostItem?>()

    private var currentPost: PostItem? = null
    private var currentPosition: Int? = null
    private var signup: PojoSignUp? = null
    private var socialPostInput: SocialPostInput? = null
    private var isTrending = false
    private var isBlockFeature = false
    private var isCartDelete = false
    private var settingBean: SettingModel.DataBean.SettingData? = null
    private var bookingFlowBean: SettingModel.DataBean.BookingFlowBean? = null
    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null
    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }

    private var mDialog: Dialog? = null

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.frag_social_post_home

    override fun getViewModel(): SocialPostViewModel {
        mViewModel = ViewModelProviders.of(this, factory).get(SocialPostViewModel::class.java)
        return mViewModel as SocialPostViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this

        productListObserver()
        screenFlowBean = mDataManager.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        bookingFlowBean = mDataManager.getGsonValue(DataNames.BOOKING_FLOW, SettingModel.DataBean.BookingFlowBean::class.java)
        settingBean = mDataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        signup = mDataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding
        mBinding?.color = Configurations.colors
        mBinding?.strings = textConfig
        setRecyclerAdapter()
        initializeResponseObserver()
        loadPostsDataFromApi(isTrending, true)

        updateUserData()

        onRecyclerViewScrolled()

        btn_create_post.setOnClickListener {
            if (mDataManager.getCurrentUserLoggedIn()) {
                val action = PostsHomeFragmentDirections.actionSocialPostToSocialPostDescFrag(socialPostInput)
                navController(this@PostsHomeFragment).navigate(action)
            } else {
                appUtils.checkLoginFlow(requireActivity(), CREATE_POST_REQUEST)
            }
        }

        btn_ask_ques.setOnClickListener {
            if (mDataManager.getCurrentUserLoggedIn()) {
                val action = PostsHomeFragmentDirections.actionSocialPostToSocialEnterQuesFrag(socialPostInput)
                navController(this@PostsHomeFragment).navigate(action)
            } else {
                appUtils.checkLoginFlow(requireActivity(), ASK_QUES_REQUEST)
            }
        }

        trend_post_view.setOnClickListener {
            isTrending = !isTrending
            loadPostsDataFromApi(isTrending, true)
        }

        invite_view.setOnClickListener {
            val shareMsg = if (settingBean?.is_app_sharing_message?.isEmpty() == false && settingBean?.is_app_sharing_message == "1") {
                settingBean?.app_sharing_message
            } else {
                getString(R.string.share_body, BuildConfig.APPLICATION_ID)
            }

            GeneralFunctions.shareApp(activity, shareMsg)
        }

        iv_userImage.setOnClickListener {
            if(!mDataManager.getCurrentUserLoggedIn()) return@setOnClickListener

            if (permissionFile.hasCameraPermissions(activity ?: requireActivity())) {
                if (isNetworkConnected) {
                    showImagePicker()
                }

            } else {
                permissionFile.cameraAndGalleryTask(this)
            }
        }

    }

    private fun showImagePicker() {
        imageDialog.settingCallback(this)
        imageDialog.show(
                childFragmentManager,
                "image_picker"
        )
    }

    private fun changeTrendPost(isTrending: Boolean) {

        tv_post_name.text = if (isTrending) {
            getString(R.string.social_all_posts)
        } else {
            getString(R.string.social_all_trending)
        }

    }

    private fun productListObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<SocialSupplierBean> { resource ->
            hideLoading()
            updateProduct(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.productLiveData.observe(this, catObserver)
    }

    private fun updateProduct(resource: SocialSupplierBean?) {

        if (resource?.prodList?.isEmpty() == true) return

        val productDataBean = resource?.prodList?.find { it.product_id == currentPost?.productId }

        if (!appUtils.checkResturntTiming(resource?.supplierData?.timing)) {
            mDialogsUtil.openAlertDialog(activity
                    ?: requireContext(), getString(R.string.offline_supplier_tag, textConfig?.supplier), getString(R.string.ok), "", this)
            return
        }

        if (productDataBean != null) {
            if (bookingFlowBean?.vendor_status == 0 && appUtils.checkVendorStatus(productDataBean.supplier_id, vendorBranchId = productDataBean.supplier_branch_id, branchFlow = settingBean?.branch_flow)) {
                isCartDelete=true
                mDialogsUtil.openAlertDialog(activity
                        ?: requireContext(), getString(R.string.clearCart, textConfig?.supplier
                        ?: "",textConfig?.proceed ?: ""), "Yes", "No", this)
            } else {
                if (appUtils.checkBookingFlow(requireContext(), productDataBean.product_id, this)) {

                    if (productDataBean.prod_quantity == 0f) {

                        productDataBean.prod_quantity = 1f

                        productDataBean.self_pickup = 0

                        productDataBean.type = screenFlowBean?.app_type

                        productDataBean.netPrice = if (productDataBean.fixed_price?.toFloatOrNull() ?: 0.0f > 0) productDataBean.fixed_price?.toFloatOrNull() else 0f

                        if (AppConstants.APP_SUB_TYPE > AppDataType.Custom.type) {
                            productDataBean.payment_after_confirmation = settingBean?.payment_after_confirmation?.toInt()
                                    ?: 0
                        }
                        appUtils.addProductDb(activity ?: requireContext(), screenFlowBean?.app_type
                                ?: 0, productDataBean)

                        AppToasty.success(activity ?: requireContext(), "Added to Cart")

                        val action = PostsHomeFragmentDirections.actionSocialPostToCart()
                        navController(this@PostsHomeFragment).navigate(action)

                    }
                }
            }
        }else{
            onErrorOccur("Sorry ,Current ${textConfig?.product} not available.")
        }
    }


    private fun updateUserData() {
        tv_user_name.text = signup?.data?.firstname ?: "Guest User"
        StaticFunction.loadUserImage(signup?.data?.user_image ?: "", iv_userImage, true)
    }

    private fun initializeResponseObserver() {
        mViewModel?.socialResponseObserver?.observe(requireActivity(), Observer { it ->

            if (it.isFirstPage) {
                collection.clear()
            }

            it.result?.list?.let { it1 -> collection.addAll(it1) }

            collection.map {
                it?.formattedDate = DateUtils.getRelativeTimeSpanString(mDateUtils.convertDateToTimeStamp(it?.createdAt
                        ?: "", "yyyy-MM-dd'T'HH:mm:ss"),
                        mDateUtils.timeOffset, DateUtils.SECOND_IN_MILLIS).toString()
            }

            recyclerAdapter.notifyDataSetChanged()
        })
    }

    private fun loadPostsDataFromApi(isTrending: Boolean, isFirstPage: Boolean) {

        changeTrendPost(isTrending)

        val requestsHolder = hashMapOf(
                "is_trending" to if (isTrending) "1" else "0"
        )
        if (mDataManager.getCurrentUserLoggedIn()) {
            requestsHolder["user_id"] = mDataManager.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING).toString()
        }

        viewModel.getUserPosts(requestsHolder, isFirstPage)
    }

    private fun setRecyclerAdapter() {
        recyclerAdapter.setOnActionListener(this)
        recyclerAdapter.setListData(collection)
        recyclerAdapter.isUserLoggedIn(mDataManager.getCurrentUserLoggedIn())

        recyclerAdapter.updateUser(signup)
        postsRecyclerView?.adapter = recyclerAdapter
    }

    override fun onErrorOccur(message: String) {

        hideLoading()

        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onLikePostClicked(postItem: PostItem?, position: Int) {
        currentPost = postItem
        currentPosition = position

        if (isNetworkConnected) {
            if (mDataManager.getCurrentUserLoggedIn()) {
                if(viewModel.isLikeLoading.get()) return
                viewModel.markPostFavUnfav(postItem?.id ?: 0, postItem?.alreadyLike ?: 0)
            } else {
                appUtils.checkLoginFlow(requireActivity(), FAV_POST_REQUEST)
            }
        }
    }

    override fun onShowPostCommentsClicked(postItem: PostItem?, position: Int) {
        currentPost = postItem
        currentPosition = position

        if (isNetworkConnected) {

            if (mDataManager.getCurrentUserLoggedIn()) {
                viewModel.getUserCommentLike(postItem, "comment")
            } else {
                appUtils.checkLoginFlow(requireActivity(), COMMENT_LIST_REQUEST)
            }
        }
    }

    override fun onShowPostLikes(postItem: PostItem?, position: Int) {
        currentPost = postItem
        currentPosition = position

        if (isNetworkConnected) {
            if (mDataManager.getCurrentUserLoggedIn()) {
                viewModel.getUserCommentLike(postItem, "like")
            } else {
                appUtils.checkLoginFlow(requireActivity(), LIKES_LIST_REQUEST)
            }
        }
    }

    override fun onSharePostClicked(postItem: PostItem?) {
        invite_view.callOnClick()
    }

    override fun onOrderNowClicked(postItem: PostItem?, position: Int) {
        currentPost = postItem
        currentPosition = position

        viewModel.getProductList(postItem?.supplierId?.toString() ?: "")
    }

    override fun onAddComment(postItem: PostItem?, position: Int, comment: String) {

        currentPost = postItem
        currentPosition = position

        if (isNetworkConnected) {
            viewModel.addComment(postItem?.id
                    ?: 0, comment)
        }
    }

    override fun onMoreOption(view: View?, postItem: PostItem?, position: Int) {

        if (!mDataManager.getCurrentUserLoggedIn()) {
            appUtils.checkLoginFlow(requireActivity(), POST_ACTION_REQUEST)
            return
        }

        currentPost = postItem
        currentPosition = position

        val popup = PopupMenu(activity, view)
        //inflating menu from xml resource

        if (postItem?.userId == mDataManager.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING).toString()) {
            popup.inflate(R.menu.popup_user_post_action)

        } else {
            popup.inflate(R.menu.popup_report_action)
        }
        //adding click listener

        popup.setOnMenuItemClickListener { menuItem ->

            when (menuItem.itemId) {
                R.id.menu_report -> {
                    mDialog = mDialogsUtil.showReportPost(activity ?: requireContext(), viewModel)
                }

                R.id.menu_block_user -> {
                    isBlockFeature = true
                    mDialogsUtil.openAlertDialog(activity
                            ?: requireContext(), "Are you sure you want to block user?", "Yes", "Cancel", this)
                }

                R.id.menu_delete -> {
                    if (isNetworkConnected) {
                        viewModel.deletePost(currentPost?.id.toString())
                    }
                }

                R.id.menu_edit -> {
                    if (isNetworkConnected) {
                        viewModel.postDetails(currentPost?.id.toString())
                    }
                }
            }
            true
        }

        //displaying the popup
        popup.show()
    }

    override fun onSupplierDetail(postItem: PostItem?, position: Int) {
        currentPost = postItem
        currentPosition = position

        val bundle = bundleOf("supplierId" to postItem?.supplierId)
        if (settingBean?.app_selected_template != null && settingBean?.app_selected_template == "1")
            Navigation.findNavController(requireView()).navigate(R.id.action_social_post_to_restaurantDetailFragNew, bundle)
        else
            Navigation.findNavController(requireView()).navigate(R.id.action_social_post_to_restaurantDetailFrag, bundle)
    }


    override fun onCommentReponse(commentList: MutableList<CommentBean>, postItem: PostItem?) {

        commentList.map {
            it.post_id = postItem?.id
        }

        val mBottomData = BottomDataItem(commentList = ArrayList(commentList))

        BottomSocialSheetFrag.newInstance(mBottomData).show(childFragmentManager, "dialog")
    }

    override fun onLikeReponse(likeList: MutableList<CommentBean>, postItem: PostItem?) {
        val mBottomData = BottomDataItem(likeList = ArrayList(likeList))

        BottomSocialSheetFrag.newInstance(mBottomData).show(childFragmentManager, "dialog")
    }

    override fun onUpdateComment(product: CommentBean?) {
        currentPost?.totalComments = currentPost?.totalComments?.inc()
        collection[currentPosition ?: 0] = currentPost

        recyclerAdapter.notifyDataSetChanged()
    }

    override fun onPostComment() {
        onUpdateComment(null)
    }

    override fun onReportRequest(head: String, desc: String) {

        if (mDialog != null && mDialog?.isShowing == true) {
            mDialog?.dismiss()
        }

        if (isNetworkConnected) {
            val hashMap = hashMapOf("heading" to head, "description" to desc, "post_id" to currentPost?.id.toString())

            viewModel.reportPost(hashMap)
        }
    }

    override fun onReportResponse(message: String) {
        AppToasty.success(activity ?: requireContext(), message)
    }

    override fun onPostLike(likeStatus: Int) {

        with(currentPost)
        {
            if (likeStatus == 1) {
                this?.alreadyLike = 0
                this?.totalLikes = this?.totalLikes?.dec()
            } else {
                this?.alreadyLike = 1
                this?.totalLikes = this?.totalLikes?.inc()
            }

            collection[currentPosition ?: 0] = this
            recyclerAdapter.notifyDataSetChanged()
        }
    }

    override fun onPostDetail(postItems: List<PostItem>) {

        val postItem = postItems.firstOrNull()

        if (postItem?.postImages?.isNotEmpty() == true) {
            postItem.postImages.map {
                it?.isSelected = true
                it?.is_imageLoad = true
                it?.isDeleteImage = true
            }
        }



        socialPostInput = SocialPostInput(post_head = postItem?.heading,
                post_desc = postItem?.description, supplier_data = SupplierDataBean(name = postItem?.supplierName, id = postItem?.supplierId
                ?: 0,
                supplier_branch_id = postItem?.branchId ?: 0),
                product_data = ProductDataBean(image_path = "", name = postItem?.productName, supplier_name = postItem?.supplierName,
                        product_id = postItem?.productId),
                imageList = postItem?.postImages, actualImageList = postItem?.postImages, isEdit = true, id = postItem?.id
        )

        if (postItem?.productId == null) {
            btn_ask_ques.callOnClick()
        } else {
            btn_create_post.callOnClick()
        }
    }

    override fun onUploadPic(message: String, image: String) {

        StaticFunction.loadUserImage(image, iv_userImage, true)

        signup?.data?.user_image = image
        mDataManager.addGsonValue(DataNames.USER_DATA, Gson().toJson(signup))

        recyclerAdapter.updateUser(signup)

        loadPostsDataFromApi(isTrending, true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (mDataManager.getCurrentUserLoggedIn()) {
            signup = mDataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
            updateUserData()
        }

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            onErrorOccur(getString(R.string.returned_from_app_settings_to_activity))
        }

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CREATE_POST_REQUEST -> {
                    btn_create_post.callOnClick()
                }
                ASK_QUES_REQUEST -> {
                    btn_ask_ques.callOnClick()
                }
                COMMENT_LIST_REQUEST -> {
                    viewModel.getUserCommentLike(currentPost, "comment")
                }
                LIKES_LIST_REQUEST -> {
                    viewModel.getUserCommentLike(currentPost, "like")
                }
                FAV_POST_REQUEST -> {
                    viewModel.markPostFavUnfav(currentPost?.id ?: 0, currentPost?.alreadyLike ?: 0)
                }

                AppConstants.CameraPicker -> {
                    if (isNetworkConnected) {
                        if (photoFile?.isRooted == true) {
                            viewModel.uploadProfImage(imageUtils.compressImage(photoFile?.absolutePath
                                    ?: ""))
                        }
                    }
                }

                AppConstants.GalleyPicker -> {
                    if (data != null) {
                        if (isNetworkConnected) {
                            //data.getData return the content URI for the selected Image
                            val selectedImage = data.data
                            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                            // Get the cursor
                            val cursor = activity?.contentResolver?.query(selectedImage!!, filePathColumn, null, null, null)
                            // Move to first row
                            cursor?.moveToFirst()
                            //Get the column index of MediaStore.Images.Media.DATA
                            val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
                            //Gets the String value in the column
                            val imgDecodableString = cursor?.getString(columnIndex ?: 0)
                            cursor?.close()

                            if (imgDecodableString?.isNotEmpty() == true) {
                                viewModel.uploadProfImage(imageUtils.compressImage(imgDecodableString))
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onSucessListner() {

        if (isNetworkConnected && isBlockFeature) {
            val hashMap = hashMapOf("blocked_user_id" to currentPost?.userId.toString())
            viewModel.blockUser(hashMap)
        }

        if(isCartDelete)
        {
            isCartDelete=false
            appUtils.clearCart().run {
               updateProduct(viewModel.productLiveData.value)
            }
        }
    }

    override fun onBlockUserResp(message: String) {
        //call api
        isBlockFeature = false
        AppToasty.success(activity ?: requireContext(), message)

        if (isNetworkConnected) {
            loadPostsDataFromApi(isTrending, true)
        }
    }

    override fun onErrorListener() {
        isCartDelete=false
    }

    private fun onRecyclerViewScrolled() {
        postsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val isPagingActive = viewModel.validForPaging()
                if (!recyclerView.canScrollVertically(1) && isPagingActive) {
                    loadPostsDataFromApi(isTrending, false)
                }
            }
        })
    }

    override fun onPdf() {

    }

    override fun onGallery() {
        val pickIntent = Intent(Intent.ACTION_PICK)
        pickIntent.type = "image/*"
        startActivityForResult(pickIntent, AppConstants.GalleyPicker)
    }

    override fun onCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity?.packageManager!!)?.also {
                // Create the File where the photo should go
                photoFile = try {
                    ImageUtility.filename(imageUtils)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            activity ?: requireContext(),
                            activity?.packageName ?: "",
                            it)

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, AppConstants.CameraPicker)
                }
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }


    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == AppConstants.CameraGalleryPicker) {

            if (isNetworkConnected) {
                showImagePicker()
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, context)
    }

    override fun onProdItemUpdate(image: String, postImage: ImageView) {
        val options = ActivityOptions.makeSceneTransitionAnimation(requireActivity(), postImage, "postImage")
        val intent = Intent(activity, ImageSHow::class.java)
        intent.putExtra("image", image)
        activity?.startActivity(intent, options.toBundle())
    }
}
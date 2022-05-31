package com.codebrew.clikat.module.social_post.other

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.ImageUtility
import com.codebrew.clikat.app_utils.PermissionFile
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.model.others.ImageListModel
import com.codebrew.clikat.databinding.FragSocialSelectImagesBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.module.cart.adapter.ImageListAdapter
import com.codebrew.clikat.module.social_post.SocialPostViewModel
import com.codebrew.clikat.module.social_post.interfaces.SocialPostNavigator
import com.codebrew.clikat.module.social_post.other.adapter.ImageListener
import com.codebrew.clikat.module.social_post.other.adapter.SelectedImageAdapter
import com.codebrew.clikat.utils.configurations.Configurations
import com.codebrew.clikat.intrface.ImageCallback
import com.codebrew.clikat.app_utils.dialogintrface.ImageDialgFragment
import kotlinx.android.synthetic.main.frag_social_select_images.*
import kotlinx.android.synthetic.main.toolbar_subscription.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.IOException
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 * Use the [SocialSelectImagesFrag.newInstance] factory method to
 * create an instance of this fragment.
 */
class SocialSelectImagesFrag : BaseFragment<FragSocialSelectImagesBinding, SocialPostViewModel>(), SocialPostNavigator, ImageCallback, EasyPermissions.PermissionCallbacks {

    private lateinit var viewModel: SocialPostViewModel

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var imageUtils: ImageUtility

    @Inject
    lateinit var permissionFile: PermissionFile

    private var mBinding: FragSocialSelectImagesBinding? = null
    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }
    private val argument: SocialSelectImagesFragArgs by navArgs()

    private var photoFile: File? = null
    private val imageDialog by lazy { ImageDialgFragment() }
    private var mAdapter: ImageListAdapter? = null
    private var mSelectedAdapter: SelectedImageAdapter? = null
    private var actualImageList = mutableListOf<ImageListModel?>()
    private var mSelectedImages = mutableListOf<ImageListModel?>()

    private var isSkip = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigator = this

        imageObserver()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding
        mBinding?.color = Configurations.colors
        mBinding?.strings = textConfig

        tb_name.text = getString(R.string.select_images)

        tb_back.setOnClickListener {
            navController(this@SocialSelectImagesFrag).popBackStack()
        }

        btnContinue.setOnClickListener {
            if (mSelectedImages.isNotEmpty() && mSelectedImages.any { it?.isSelected == true } || isSkip) {
                argument.postDescData?.imageList = mSelectedImages
                argument.postDescData?.actualImageList = actualImageList

                val action = SocialSelectImagesFragDirections.actionSocialSelectImagesFragToSocialConfirmPostFrag(argument.postDescData)
                navController(this@SocialSelectImagesFrag).navigate(action)
            }else
            {
                onErrorOccur("Please select images")
            }
        }

        btnSkip.setOnClickListener {
            isSkip = true
            mSelectedImages.clear()
            actualImageList.clear()

/*            actualImageList.add(ImageListModel(is_imageLoad = true, image = argument.postDescData?.product_data?.image_path.toString(),
                    _id = System.currentTimeMillis().toString(), isSelected = true, isDeleteImage = true))*/

            //actualImageList = mSelectedImages

            btnContinue.callOnClick()
        }

        initializeLayout()
    }

    private fun initializeLayout() {


        if(actualImageList.isEmpty() && mSelectedImages.isEmpty())
        {
            with(argument.postDescData)
            {
                if (this?.actualImageList != null && this.actualImageList?.isNotEmpty() == true) {
                    this@SocialSelectImagesFrag.actualImageList.addAll(this.actualImageList
                            ?: mutableListOf())
                } else {
                    if (argument.postDescData?.product_data?.image_path.toString().isNotEmpty()) {
                        actualImageList.add(ImageListModel(is_imageLoad = true, image = argument.postDescData?.product_data?.image_path.toString(),
                                _id = System.currentTimeMillis().toString(), isSelected = true, isDeleteImage = true))
                    }
                }

                if (this?.imageList != null && this.imageList?.isNotEmpty() == true) {
                    mSelectedImages.addAll(this.imageList ?: mutableListOf())
                }else
                {
                    mSelectedImages.addAll(actualImageList.filter { it?.isSelected == true }.toMutableList())
                }
            }
        }

        mAdapter = ImageListAdapter(ImageListAdapter.UserChatListener({

            if (it.is_imageLoad == false) {
                uploadImage()
            }
        }, { it1 ->
            if (it1 < 0) return@UserChatListener
            actualImageList.removeAt(it1)
            mAdapter?.submitMessageList(actualImageList, "social_images")
        }, { model, pos ->
            model.isSelected = !model.isSelected!!
            model.isDeleteImage = !model.isDeleteImage!!

            mSelectedImages.clear()

            mSelectedImages.addAll(actualImageList.filter { it?.isSelected == true }.toMutableList())
            mSelectedImages.map {
                it?.isDeleteImage = true
            }

            mSelectedAdapter?.submitItemList(mSelectedImages)



            mAdapter?.notifyDataSetChanged()
        }), true)

        rv_images.layoutManager = GridLayoutManager(activity, 4)
        rv_images.adapter = mAdapter

        mAdapter?.submitMessageList(actualImageList, "social_images")


        mSelectedAdapter = SelectedImageAdapter(ImageListener { model, position ->

            actualImageList[position]?.isSelected = false
            actualImageList[position]?.isDeleteImage = false
            mAdapter?.notifyDataSetChanged()

            mSelectedImages.removeAt(position)
            mSelectedAdapter?.submitItemList(mSelectedImages)

        }, true)
        rv_selected_images.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        rv_selected_images.adapter = mSelectedAdapter

        mSelectedAdapter?.submitItemList(mSelectedImages)

    }

    private fun imageObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<String> { resource ->

            actualImageList.add(ImageListModel(is_imageLoad = true, image = resource, _id = System.currentTimeMillis().toString(), isSelected = false, isDeleteImage = false))
            mAdapter?.submitMessageList(actualImageList, "social_images")
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.imageLiveData.observe(this, catObserver)
    }

    private fun uploadImage() {
        if (permissionFile.hasCameraPermissions(activity ?: requireContext())) {
            if (isNetworkConnected) {
                showImagePicker()
            }
        } else {
            permissionFile.cameraAndGalleryTask(this)
        }
    }

    private fun showImagePicker() {
        imageDialog.settingCallback(this)
        imageDialog.show(
                childFragmentManager,
                "image_picker"
        )
    }

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    /**
     * @return layout resource id
     */
    override fun getLayoutId(): Int {
        return R.layout.frag_social_select_images
    }

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): SocialPostViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(SocialPostViewModel::class.java)
        return viewModel
    }

    override fun onErrorOccur(message: String) {
        hideKeyboard()
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
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
                            requireContext(),
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
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            mBinding?.root?.onSnackbar(getString(R.string.returned_from_app_settings_to_activity))
        } else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == AppConstants.CameraPicker) {
            if (isNetworkConnected) {
                if (photoFile?.isRooted == true) {
                    viewModel.uploadImage(imageUtils.compressImage(photoFile?.absolutePath
                            ?: ""))
                }
            }
        } else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == AppConstants.GalleyPicker) {
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
                        viewModel.uploadImage(imageUtils.compressImage(imgDecodableString))
                    }
                }
            }
        }
    }


}
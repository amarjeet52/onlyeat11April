package com.codebrew.clikat.module.cart.addproduct


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.ImageUtility
import com.codebrew.clikat.app_utils.PermissionFile
import com.codebrew.clikat.app_utils.dialogintrface.ImageDialgFragment
import com.codebrew.clikat.app_utils.extension.loadImage
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseDialogFragment
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.databinding.DialogAddProductBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.cart.CartNavigator
import com.codebrew.clikat.module.cart.CartViewModel
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.configurations.Configurations
import com.codebrew.clikat.intrface.ImageCallback
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.dialog_add_product.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */

private const val ARG_PARAM1 = "parentPosition"
private const val ARG_PARAM2 = "childPosition"
private const val ARG_PARAM3 = "open"
private const val ARG_PARAM4="productData"

class AddProductDialog : BaseDialogFragment(), EasyPermissions.PermissionCallbacks, CartNavigator, ImageCallback {

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var appUtils: AppUtils

    private var mViewModel: CartViewModel? = null

    private var mBinding: DialogAddProductBinding? = null

    private var settingData: SettingModel.DataBean.SettingData? = null

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var imageUtils: ImageUtility

    @Inject
    lateinit var permissionFile: PermissionFile
    private var photoFile: File? = null
    private var productBean:ProductDataBean?=null
    private val imageDialog by lazy { ImageDialgFragment() }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_add_product, container, false)
        return mBinding?.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        settingData = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        )

        mViewModel = ViewModelProviders.of(this, factory).get(CartViewModel::class.java)
        mViewModel?.navigator = this
        mBinding?.color = Configurations.colors
        initialise()
        setListeners()
        imageObserver()
    }

    private fun initialise() {
        arguments?.let {
            productBean=it.getParcelable(ARG_PARAM4)
        }
    }

    private fun setListeners() {
        ivCross.setOnClickListener {
            dismiss()
        }
        tvUpload?.setOnClickListener {
            uploadFile()
        }

        btnAddProduct?.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        hideKeyboard()
        val name = etName?.text.toString().trim()
        val referenceId = etReferenceId?.text.toString().trim()
        val dimensions = etDimensions?.text.toString().trim()
        when {
            name.isEmpty() -> {
                mBinding?.root?.onSnackbar(getString(R.string.enter_name))
            }
            referenceId.isEmpty() -> mBinding?.root?.onSnackbar(getString(R.string.enter_reference_id))
            dimensions.isEmpty() -> {
                mBinding?.root?.onSnackbar(getString(R.string.enter_dimensions))
            }
            photoFile == null -> {
                mBinding?.root?.onSnackbar(getString(R.string.upload_receipt))
            }
            else -> {
                productBean?.product_owner_name=name
                productBean?.product_reference_id = referenceId
                productBean?.product_dimensions = dimensions
                productBean?.is_out_network=1
                productBean?.product_upload_reciept = mViewModel?.imageLiveData?.value
                val parentPos = arguments?.getInt(ARG_PARAM1)
                val childPos = arguments?.getInt(ARG_PARAM2)
                val isOpen = arguments?.getBoolean(ARG_PARAM3)
                dismiss()
                onAddProductListener?.onProductAdded(productBean, parentPos?:0, childPos?:0, isOpen
                        ?: true)
            }
        }
    }


    private fun uploadFile() {
        if (permissionFile.hasCameraPermissions(requireContext())) {
            if (isNetworkConnected) {
                showImagePicker()
            }
        } else {
            permissionFile.cameraAndGalleryActivity(requireContext())
        }
    }



    private fun showImagePicker() {
        imageDialog.setPdfUpload(true)
        imageDialog.settingCallback(this)
        imageDialog.show(
                childFragmentManager,
                "image_picker"
        )
    }

    override fun onPdf() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Pdf"),
                AppConstants.REQUEST_CODE_PDF)
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.REQUEST_CODE_PDF && resultCode == Activity.RESULT_OK) {
            val dir = File(Environment.getExternalStorageDirectory(), "/app")
            if (!dir.exists()) dir.mkdirs()
            val uri = data?.data
            val uriString = uri?.toString()
            val pdfFile: File?
            val myFile = File(uriString ?: "")
            if (uriString?.startsWith("content://") == true) {
                try {
                    val v = myFile.name.replace("[^a-zA-Z]+".toRegex(), "_") + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()) + ".pdf"
                    pdfFile = File(dir, v)
                    val fos = FileOutputStream(pdfFile)
                    val out = BufferedOutputStream(fos)
                    val inputStream = context?.contentResolver?.openInputStream(uri)
                    try {
                        val buffer = ByteArray(8192)
                        var len: Int
                        while (inputStream?.read(buffer).also { len = it ?: 0 } ?: 0 >= 0) {
                            out.write(buffer, 0, len)
                        }
                        out.flush()
                    } finally {
                        fos.fd.sync()
                        out.close()
                        inputStream?.close()
                    }
                    photoFile = pdfFile
                    if (photoFile?.isRooted == true) {
                        progressBar?.show()
                        tvUpload?.setImageResource(R.drawable.pdf)
                        mViewModel?.uploadImage(photoFile?.absolutePath ?: "")
                    }
                } catch (e: Exception) {
                }
            } else if (uriString?.startsWith("file://") == true) {
                photoFile = myFile
                if (photoFile?.isRooted == true) {
                    progressBar?.show()
                    tvUpload?.setImageResource(R.drawable.pdf)
                    mViewModel?.uploadImage(photoFile?.absolutePath ?: "")
                }
            }
        }else if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            mBinding?.root?.onSnackbar(getString(R.string.returned_from_app_settings_to_activity))
        } else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == AppConstants.CameraPicker) {
            if (isNetworkConnected) {
                if (photoFile?.isRooted == true) {
                    progressBar?.show()
                    val image=imageUtils.compressImage(photoFile?.absolutePath ?: "")
                    mViewModel?.uploadImage(image)
                    tvUpload?.loadImage(image)
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
                        val image=imageUtils.compressImage(imgDecodableString)
                        progressBar?.show()
                        tvUpload?.loadImage(image)
                        mViewModel?.uploadImage(image)
                    }
                }
            }
        }
    }

    private fun imageObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<String> { resource ->
            progressBar?.hide()
            photoFile = File(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mViewModel?.imageLiveData?.observe(this, catObserver)
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

    private var onAddProductListener: OnAddProductListener? = null
    fun setListeners(onAddProductListener: OnAddProductListener) {
        this.onAddProductListener = onAddProductListener
    }

    interface OnAddProductListener {
        fun onProductAdded(productBean: ProductDataBean?, parentPosition: Int, childPosition: Int, isOpen: Boolean)
    }

    companion object {
        @JvmStatic
        fun newInstance(productBean: ProductDataBean?, parentPosition: Int, childPosition: Int, open: Boolean) =
                AddProductDialog().apply {
                    arguments = bundleOf(ARG_PARAM1 to parentPosition, ARG_PARAM2 to childPosition, ARG_PARAM3 to open,
                    ARG_PARAM4 to productBean)
                }
    }

    override fun onProfileUpdate() {

    }

    override fun onErrorOccur(message: String) {
        progressBar?.hide()
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        progressBar?.hide()
        openActivityOnTokenExpire()
    }


}

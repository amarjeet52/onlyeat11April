package com.codebrew.clikat.module.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.codebrew.clikat.BR
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.*
import com.codebrew.clikat.app_utils.extension.onCheckChanged
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragemntSettingAnimBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.Dialogs.ChangePasswordDilaog
import com.codebrew.clikat.utils.Dialogs.ChangePasswordDilaog.OnOkClickListener
import com.codebrew.clikat.utils.GeneralFunctions
import com.codebrew.clikat.utils.StaticFunction.loadUserImage
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.gson.Gson
import com.codebrew.clikat.intrface.ImageCallback
import com.codebrew.clikat.app_utils.dialogintrface.ImageDialgFragment
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.module.dialog_adress.AddressDialogFragment
import com.codebrew.clikat.module.dialog_adress.interfaces.AddressDialogListener
import com.codebrew.clikat.module.dialog_adress.v2.AddressDialogFragmentV2
import com.codebrew.clikat.utils.StaticFunction.focus
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.fragemnt_setting_anim.*
import kotlinx.android.synthetic.main.toolbar_app.*
import okhttp3.RequestBody
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.IOException
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

/*
 * Created by cbl80 on 16/5/16.
 */
class SettingFragment : BaseFragment<FragemntSettingAnimBinding, SettingViewModel>(),
        SettingNavigator, ImageCallback, EasyPermissions.PermissionCallbacks, AdapterView.OnItemSelectedListener, AddressDialogListener {

    private var settingBean: SettingModel.DataBean.SettingData? = null
    private var signUp: PojoSignUp? = null

    @Inject
    lateinit var prefHelper: PreferenceHelper

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var imageUtils: ImageUtility


    @Inject
    lateinit var permissionFile: PermissionFile

    @Inject
    lateinit var appUtils: AppUtils


    private var languageParam: String? = null

    private var photoFile: File? = null


    private lateinit var viewModel: SettingViewModel

    private var mBinding: FragemntSettingAnimBinding? = null

    private val imageDialog by lazy { ImageDialgFragment() }

    private var mLanguageId = 0
    private var click = 0

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragemnt_setting_anim
    }

    override fun getViewModel(): SettingViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(SettingViewModel::class.java)
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        viewDataBinding?.colors = Configurations.colors
        viewDataBinding?.drawables = Configurations.drawables
        viewDataBinding?.strings = appUtils.loadAppConfig(0).strings

        intialize()
        settingToolbar()

    }

    private fun settingToolbar() {
        tb_title.setText(R.string.settings)
        tb_back.setOnClickListener { Navigation.findNavController(requireView()).popBackStack() }
    }


    private fun intialize() {
        settingBean = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        ic_edit.setOnClickListener {
            if (permissionFile.hasCameraPermissions(activity ?: requireActivity())) {
                if (isNetworkConnected) {
                    showImagePicker()
                }

            } else {
                permissionFile.cameraAndGalleryTask(this)
            }
        }

        tvChangePassword.visibility = takeIf { prefHelper.getCurrentUserLoggedIn() }
                ?.let { View.VISIBLE } ?: View.GONE


        takeIf { prefHelper.getCurrentUserLoggedIn() }?.let {

            signUp = prefHelper.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)

            loadUserImage(signUp?.data?.user_image ?: "", ivBadge, true)

            tvChangePassword.visibility = takeIf { signUp?.data?.google_access_token.isNullOrEmpty() && signUp?.data?.fbId.isNullOrEmpty() }
                    ?.let { View.VISIBLE } ?: View.GONE

            Glide.with(activity ?: requireActivity()).load(signUp?.data?.user_image)
                    .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 2)))
                    .placeholder(R.drawable.ic_user_placeholder)
                    .into(ivParent)

            tvUserName.text = signUp?.data?.firstname ?: ""
            tvPhone.text = signUp?.data?.mobile_no ?: ""

            tvPhone.visibility = if (signUp?.data?.mobile_no.isNullOrBlank()) View.GONE else View.VISIBLE
            tvUserName.visibility = if (signUp?.data?.firstname.isNullOrBlank()) View.GONE else View.VISIBLE

            tvNoti.isChecked = signUp?.data?.notification_status == 1

            gp_password.visibility = if (signUp?.data?.fbId?.isBlank() == true) View.VISIBLE else View.GONE

            if (settingBean?.is_abn_business == "1") {
                gpAbnNumber?.visibility = View.VISIBLE
                gpBusinessName?.visibility = View.VISIBLE
                tvAbnNumber?.setText(signUp?.data?.abn_number)
                tvBusinessName?.setText(signUp?.data?.business_name)
            } else {
                gpAbnNumber?.visibility = View.GONE
                gpBusinessName?.visibility = View.GONE
            }
            groupInvoiceId?.visibility=if(settingBean?.enable_id_for_invoice_in_profile=="1") View.VISIBLE else View.GONE
            val idForInvoice=prefHelper.getKeyValue(PrefenceConstants.ID_FOR_INVOICE, PrefenceConstants.TYPE_STRING)?.toString()
            etIdForInvoice?.setText(idForInvoice)
        }

        takeUnless { prefHelper.getCurrentUserLoggedIn() }?.let {
            loadUserImage("", ivBadge, true)

            Glide.with(activity ?: requireActivity()).load(signUp?.data?.user_image)
                    .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 1)))
                    .placeholder(R.drawable.ic_user_placeholder)
                    .into(ivParent)
        }


        tvNoti.onCheckChanged { status ->
            notiStatusApi(takeIf { status }?.let { 1 } ?: 0)
        }

        tvChangePassword.setOnClickListener {
            ChangePasswordDilaog(activity, false, OnOkClickListener { }).show()
        }

        tv_savedAddress.visibility = if (BuildConfig.CLIENT_CODE == "foodydoo_0590") {
            View.VISIBLE
        } else {
            View.GONE
        }

        tv_savedAddress.setOnClickListener {
            if (settingBean?.show_ecom_v2_theme == "1") {
                AddressDialogFragmentV2.newInstance(0).show(childFragmentManager, "dialog")
            } else {
                AddressDialogFragment.newInstance(0,0).show(childFragmentManager, "dialog")
            }
        }

        tvEditAbnNumber?.setOnClickListener {
            when {
                tvEditAbnNumber?.text.toString().trim() == getString(R.string.edit) -> {
                    tvEditAbnNumber?.text = getString(R.string.done)
                    tvAbnNumber?.isEnabled = true
                    tvAbnNumber?.requestFocus()
                }
                tvAbnNumber?.text.toString().isEmpty() -> {
                    onErrorOccur(getString(R.string.pls_enter_abn_number))
                }
                else -> {
                    editProfile()
                }
            }

        }
        tvEditId?.setOnClickListener {
            hideKeyboard()
            when {
                tvEditId?.text.toString().trim() == getString(R.string.edit) -> {
                    tvEditId?.text = getString(R.string.done)
                    etIdForInvoice?.isEnabled = true
                    etIdForInvoice?.focus()

                }
                etIdForInvoice?.text.toString().isEmpty() -> {
                    onErrorOccur(getString(R.string.please_enter_id_for_invoice))
                }
                else -> {
                    editProfile()
                }
            }

        }
        tvEditName?.setOnClickListener {
            if (tvEditName?.text.toString().trim() == getString(R.string.edit)) {
                tvEditName?.text = getString(R.string.done)
                tvBusinessName?.isEnabled = true
                tvBusinessName?.requestFocus()
            } else if (tvBusinessName?.text.toString().isEmpty()) {
                onErrorOccur(getString(R.string.pls_enter_business_name))
            } else {
                editProfile()
            }

        }
        ArrayAdapter.createFromResource(
                activity ?: requireContext(),
                R.array.app_language,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            togglelanguage.adapter = adapter
        }
        togglelanguage.onItemSelectedListener = this

    }


    override fun onDestroyView() {
        super.onDestroyView()
        AppConstants.isOnlyAuth = false
        CommonUtils.changebaseUrl(dataManager.getRetrofitUtl(), BuildConfig.BASE_URL)
    }

    private fun editProfile() {
        val hashMap = HashMap<String, RequestBody>()
        hashMap["accessToken"] = CommonUtils.convrtReqBdy(prefHelper.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString())
        if (tvAbnNumber?.text.toString().trim().isNotEmpty())
            hashMap["abn_number"] = CommonUtils.convrtReqBdy(tvAbnNumber?.text.toString().trim())
        if (tvBusinessName?.text.toString().trim().isNotEmpty())
            hashMap["business_name"] = CommonUtils.convrtReqBdy(tvBusinessName?.text.toString().trim())

        if (etIdForInvoice?.text.toString().trim().isNotEmpty())
            hashMap["id_for_invoice"] = CommonUtils.convrtReqBdy(etIdForInvoice?.text.toString().trim())

        hashMap["name"] = CommonUtils.convrtReqBdy(tvUserName?.text.toString().trim())
        if (isNetworkConnected)
            viewModel.editProfile(hashMap)
    }

    private fun showImagePicker() {
        imageDialog.settingCallback(this)
        imageDialog.show(
                childFragmentManager,
                "image_picker"
        )
    }

    override fun onVehicleSelect(id:String,model: String, color: String, plate_no: String) {
        TODO("Not yet implemented")
    }
    private fun languageApi(languageId: Int, languageParam: String) {

        if (isNetworkConnected) {
            mLanguageId = languageId
            viewModel.changeNotiLang(languageId, languageParam)
        }
    }

    private fun notiStatusApi(status: Int) {
        if (isNetworkConnected) {
            viewModel.changeNotification(status)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            mBinding?.root?.onSnackbar(getString(R.string.returned_from_app_settings_to_activity))
        }

        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == AppConstants.CameraPicker) {

            if (isNetworkConnected) {
                if (photoFile?.isRooted == true) {
                    viewModel.uploadProfImage(imageUtils.compressImage(photoFile?.absolutePath
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
                        viewModel.uploadProfImage(imageUtils.compressImage(imgDecodableString))
                    }
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


    override fun onNotifiChange(message: String) {
        val status = if (tvNoti.isChecked) 1 else 0
        signUp?.data?.notification_status = status
        prefHelper.addGsonValue(DataNames.USER_DATA, Gson().toJson(signUp))

        mBinding?.root?.onSnackbar(message)
    }

    override fun pandaDocSuccess() {
        AppToasty.success(requireContext(), getString(R.string.upload_documents))
    }

    override fun onNotiLangChange(message: String, langCode: String) {
        mBinding?.root?.onSnackbar(message)

        prefHelper.setkeyValue(DataNames.SELECTED_LANGUAGE, languageParam ?: "")
        when (languageParam) {
            "english", "en" -> {
                GeneralFunctions.force_layout_to_LTR(baseActivity)
            }
            "spanish", "es" -> {
                GeneralFunctions.force_layout_to_LTR(baseActivity)
            }
            "arabic", "ar" -> {
                GeneralFunctions.force_layout_to_RTL(baseActivity)
            }
        }

    }

    override fun onProfileUpdate() {
        tvEditName?.text = getString(R.string.edit)
        tvEditAbnNumber?.text = getString(R.string.edit)
        tvBusinessName?.isEnabled = false
        tvAbnNumber?.isEnabled = false

        tvEditId?.text = getString(R.string.edit)
        etIdForInvoice?.isEnabled = false

        etIdForInvoice?.clearFocus()
        tvBusinessName?.clearFocus()
        tvAbnNumber?.clearFocus()
        AppToasty.success(requireContext(), getString(R.string.profile_edited_success))
    }

    override fun onUploadPic(message: String, image: String) {
        mBinding?.root?.onSnackbar(message)

        Glide.with(activity ?: requireActivity()).load(image)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 1)))
                .placeholder(R.drawable.ic_user_placeholder)
                .into(ivParent)

        loadUserImage(image, ivBadge, true)

        signUp?.data?.user_image = image
        prefHelper.addGsonValue(DataNames.USER_DATA, Gson().toJson(signUp))

    }

    override fun onErrorOccur(message: String) {
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
                            activity ?: requireContext(),
                            activity?.packageName ?: "",
                            it)

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, AppConstants.CameraPicker)
                }
            }
        }
    }


    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
        click += click.inc()
        if (click > 1) {
            languageParam = parent?.getItemAtPosition(pos).toString().toLowerCase(Locale.ENGLISH)

            languageParam = when (languageParam) {
                "arabic" -> {
                    "ar"
                }
                "spanish", "español" -> {
                    "es"
                }
                else -> {
                    "en"
                }
            }

            languageApi(appUtils.getLangCode(languageParam),languageParam?:"")
        }
    }

    override fun onAddressSelect(adrsBean: AddressBean) {
        adrsBean.let {
            appUtils.setUserLocale(it)

            prefHelper.addGsonValue(PrefenceConstants.ADRS_DATA, Gson().toJson(it))
        }
    }

    override fun onDestroyDialog() {

    }


}
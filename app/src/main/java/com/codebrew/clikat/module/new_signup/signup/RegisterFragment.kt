package com.codebrew.clikat.module.new_signup.signup

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.os.ConfigurationCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.codebrew.clikat.BR
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.*
import com.codebrew.clikat.app_utils.dialogintrface.ImageDialgFragment
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.AppConstants.Companion.ADMIN_SUPPLIER_DETAIL
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.CblCustomerDomain
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.RegisterFragmentBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.bottom_navigation.MainScreenActivity
import com.codebrew.clikat.module.login.LoginActivityNew
import com.codebrew.clikat.module.signup.DocumentsListAdapter
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.retrofit.RetrofitUtils
import com.codebrew.clikat.utils.GeneralFunctions
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.*
import com.codebrew.clikat.intrface.ImageCallback
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.register_fragment.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.IOException
import javax.inject.Inject


class RegisterFragment : BaseFragment<RegisterFragmentBinding, RegisterViewModel>(), RegisterNavigator, Validator.ValidationListener,
        DocumentsListAdapter.OnItemClicked, ImageCallback, EasyPermissions.PermissionCallbacks {

    companion object {
        fun newInstance() = RegisterFragment()
    }

    private lateinit var viewModel: RegisterViewModel

    private var mBinding: RegisterFragmentBinding? = null

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var prefHelper: PreferenceHelper

    // Validationbutton_login
    private val validator = Validator(this)

    var settingData: SettingModel.DataBean.SettingData? = null

    @NotEmpty(message = "Please enter First Name")
    @Order(1)
    private lateinit var firstNameEditText: EditText

    @NotEmpty(message = "Please enter Last Name")
    @Order(2)
    private lateinit var lastNameEditText: EditText

    @NotEmpty(message = "Please enter Mobile no.")
    @Order(3)
    private lateinit var mobileEditText: EditText

    @NotEmpty(message = "Please enter Email")
    @Email
    @Order(4)
    private lateinit var emailEditText: EditText

    @NotEmpty
    @Password(scheme = Password.Scheme.ANY)
    @Order(5)
    @Length(min = 4, message = "Password must be between 4 and 12 characters")
    private lateinit var passwordEditText: EditText

    @ConfirmPassword
    private lateinit var confirmPswrdEditText: EditText
    private var documentAdapter: DocumentsListAdapter? = null
    private var validNumber = false

    private val imageDialog by lazy { ImageDialgFragment() }

    @Inject
    lateinit var imageUtils: ImageUtility

    @Inject
    lateinit var permissionFile: PermissionFile

    private var imageFile: File? = null

    private var clientData: CblCustomerDomain? = null

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        clientData = prefHelper.getGsonValue(PrefenceConstants.DB_INFORMATION, CblCustomerDomain::class.java)
        settingData = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        // This callback will only be called when MyFragment is at least Started.
        // This callback will only be called when MyFragment is at least Started.
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
               navController(this@RegisterFragment).popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding
        mBinding?.color = Configurations.colors
        mBinding?.strings = textConfig
        mBinding?.settingData = settingData
        Glide.with(this).asGif().load(R.raw.loading_gif).into(mBinding!!.imageLoader);
        viewModel.navigator = this

        // Validator
        validator.validationMode = Validator.Mode.BURST
        validator.setValidationListener(this)
        firstNameEditText = view.findViewById(R.id.edFirstName)
        lastNameEditText = view.findViewById(R.id.edLastName)
        mobileEditText = view.findViewById(R.id.edPhoneNumber)
        emailEditText = view.findViewById(R.id.edEmail)
        passwordEditText = view.findViewById(R.id.etPassword)
        confirmPswrdEditText = view.findViewById(R.id.etConfirmPassword)


        updateToken()
        initialise()
        setAdapter()

        btn_signup.setOnClickListener {
            if (settingData?.signup_declaration == "1") {
                showDeclationDialog()
            } else
                validator.validate()
        }

        tvLoginCrave.setOnClickListener {
            activity?.launchActivity<LoginActivityNew>(AppConstants.REQUEST_USER_PROFILE)
        }

        back.setOnClickListener {
            findNavController().popBackStack()
        }

        tvText.setOnClickListener {
            navController(this@RegisterFragment).navigate(R.id.action_registerFragment_to_loginFragment)
        }

        tv_vendor_regis.setOnClickListener {

            if (BuildConfig.CLIENT_CODE == "bodyformula_0497" && !clientData?.supplier_domain.isNullOrEmpty()) {
                Utils.openWebView(activity
                        ?: requireContext(), clientData?.supplier_domain.plus(ADMIN_SUPPLIER_DETAIL))
            } else if (!clientData?.admin_domain.isNullOrEmpty()) {
                Utils.openWebView(activity
                        ?: requireContext(), clientData?.admin_domain.plus(ADMIN_SUPPLIER_DETAIL))
            }
        }


        ccp.registerCarrierNumberEditText(mobileEditText)
        ccp.setNumberAutoFormattingEnabled(false)
        settingData?.cutom_country_code?.let {
            if (it == "1") {
                ccp.setDefaultCountryUsingNameCode("VE")
                ccp.setCustomMasterCountries("VE,US")
                ccp.resetToDefaultCountry()
            } else {
                val locale = ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]
                ccp.setDefaultCountryUsingNameCode(locale.country)
            }
        }
        ccp.setPhoneNumberValidityChangeListener { isValidNumber: Boolean -> validNumber = isValidNumber }
    }

    private fun showDeclationDialog() {

    }

    private fun updateToken() {

        if (prefHelper.getKeyValue(DataNames.REGISTRATION_ID, PrefenceConstants.TYPE_STRING).toString().isEmpty()) {

            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if (it.isSuccessful) {
                    val token  = it.result
                    prefHelper.setkeyValue(DataNames.REGISTRATION_ID, token.toString())
                }

            }
        }
    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.register_fragment
    }

    override fun getViewModel(): RegisterViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(RegisterViewModel::class.java)
        return viewModel
    }

    override fun onRegisterSuccess(accessToken: String,message: String) {

/*        if (settingData?.bypass_otp == "1") {
            if (isNetworkConnected) {

                val hashMap = hashMapOf("accessToken" to accessToken,
                        "otp" to "12345",
                        "languageId" to prefHelper.getLangCode())

                viewModel.validateOtp(hashMap)
            }
        } else {*/
        if(accessToken.equals(""))
        {
            mBinding?.root?.onSnackbar(message)
        }else{
            val action = RegisterFragmentDirections.actionRegisterFragmentToOtpVerifyFragment(accessToken)
            navController(this@RegisterFragment).navigate(action)
        }
    }


    override fun onOtpVerify() {
        val isGuest = settingData?.login_template.isNullOrEmpty() || settingData?.login_template == "0"

        if (isGuest) {
            activity?.setResult(Activity.RESULT_OK)
        } else {
            activity?.launchActivity<MainScreenActivity>()
        }

        activity?.finish()
    }


    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onValidationFailed(errors: MutableList<ValidationError>?) {
        val error = errors?.get(0)
        val message = error?.getCollatedErrorMessage(activity)
        val editText = error?.view as EditText
        editText.error = message
        editText.requestFocus()
    }

    override fun onValidationSucceeded() {
        hideKeyboard()
        if (isNetworkConnected) {

            if (prefHelper.getKeyValue(DataNames.REGISTRATION_ID, PrefenceConstants.TYPE_STRING).toString().isEmpty()) {
                updateToken()
            } else if (validNumber) {
                val body: ArrayList<MultipartBody.Part>? = ArrayList()
                val mLocUser = prefHelper.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)
                val hashMap = HashMap<String, RequestBody>()
                hashMap["first_name"] = CommonUtils.convrtReqBdy(firstNameEditText.text.toString().trim())
                hashMap["last_name"] = CommonUtils.convrtReqBdy(lastNameEditText.text.toString().trim())
                hashMap["mobileNumber"] = CommonUtils.convrtReqBdy(mobileEditText.text.toString().trim())
                hashMap["countryCode"] = CommonUtils.convrtReqBdy(ccp.selectedCountryCodeWithPlus)
                hashMap["email"] = CommonUtils.convrtReqBdy(emailEditText.text.toString().trim())
                hashMap["deviceType"] = CommonUtils.convrtReqBdy("0")
                hashMap["deviceToken"] = CommonUtils.convrtReqBdy(prefHelper.getKeyValue(DataNames.REGISTRATION_ID, PrefenceConstants.TYPE_STRING).toString())
                hashMap["password"] = CommonUtils.convrtReqBdy(confirmPswrdEditText.text.toString().trim())
                hashMap["languageId"] = CommonUtils.convrtReqBdy(prefHelper.getLangCode())
                hashMap["longitude"] = CommonUtils.convrtReqBdy(mLocUser?.longitude ?: "")
                hashMap["latitude"] = CommonUtils.convrtReqBdy(mLocUser?.latitude ?: "")
                if (etBusinessName?.text.toString().trim().isNotEmpty())
                    hashMap["business_name"] = CommonUtils.convrtReqBdy(etBusinessName?.text.toString().trim())

                if (etAbnNumber?.text.toString().trim().isNotEmpty())
                    hashMap["abn_number"] = CommonUtils.convrtReqBdy(etAbnNumber?.text.toString().trim())
                /* val registerParam = RegisterParamModel(firstNameEditText.text.toString().trim(), lastNameEditText.text.toString().trim(),
                         mobileEditText.text.toString().trim().toLong(), ccp.selectedCountryCodeWithPlus,
                         emailEditText.text.toString().trim(), "0", prefHelper.getKeyValue(DataNames.REGISTRATION_ID, PrefenceConstants.TYPE_STRING).toString(),
                         confirmPswrdEditText.text.toString().trim(), prefHelper.getLangCode(),
                         mLocUser?.latitude?.toDouble() ?: 0.0,
                         mLocUser?.longitude?.toDouble() ?: 0.0)*/
                if (settingData?.user_id_proof != null && settingData?.user_id_proof == "1") {
                    val list = documentAdapter?.getList()
                    if ((list?.size ?: 0) > 1) {
                        if (list?.isNotEmpty() == true && list.size > 1) {
                            for (i in 0 until (list.size - 1)) {
                                // MultipartBody.Part is used to send also the actual file name
                                body?.add(MultipartBody.Part.createFormData("documents", File(list[i]).name, RetrofitUtils.textToRequestBody(list[i])))
                            }
                        }
                        viewModel.validateSignup(hashMap, body)
                    } else
                        AppToasty.error(requireActivity(), getString(R.string.please_upload_id_proof))
                } else {
                    viewModel.validateSignup(hashMap, body)
                }
            } else {
                mobileEditText.error = getString(R.string.enter_valid_number)
            }
        }
    }

    private fun initialise() {
        if (settingData?.user_id_proof != null && settingData?.user_id_proof == "1") {
            tvuploadDocs?.visibility = View.VISIBLE
            rvUploadDocument?.visibility = View.VISIBLE
        } else {
            tvuploadDocs?.visibility = View.GONE
            rvUploadDocument?.visibility = View.GONE
        }

        if (settingData?.is_abn_business != null && settingData?.is_abn_business == "1") {
            tlAbnNumber?.visibility = View.VISIBLE
            tlBusinessName?.visibility = View.VISIBLE
        } else {
            tlBusinessName?.visibility = View.GONE
            tlAbnNumber?.visibility = View.GONE
        }

        settingData?.is_vendor_registration?.let {
            if (it == "1") {
                tv_vendor_regis.text = getString(R.string.register_service_provider, textConfig?.supplier)

                tv_vendor_regis.visibility = View.VISIBLE
            }
        }
    }

    private fun setAdapter() {
        documentAdapter = DocumentsListAdapter(this)
        rvUploadDocument?.adapter = documentAdapter
        documentAdapter?.addImage("")
    }

    private fun showImagePicker() {
        imageDialog.settingCallback(this)
        imageDialog.show(
                childFragmentManager,
                "image_picker"
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            GeneralFunctions.showSnackBar(view, getString(R.string.returned_from_app_settings_to_activity), activity)
        }

        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == AppConstants.CameraGalleryPicker) {
            showImagePicker()
        }


        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == AppConstants.CameraPicker) {

            if (isNetworkConnected) {
                if (imageFile?.isRooted == true) {
                    documentAdapter?.addImage(imageUtils.compressImage(imageFile?.absolutePath
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
                        documentAdapter?.addImage(imageUtils.compressImage(imgDecodableString))
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

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, context)
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
                imageFile = try {
                    ImageUtility.filename(imageUtils)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                imageFile?.also {
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

    override fun onItemClicked() {
        if (permissionFile.hasCameraPermissions(activity ?: requireActivity())) {
            if (isNetworkConnected) {
                showImagePicker()
            }
        } else {
            permissionFile.cameraAndGalleryTask(this)
        }
    }

    override fun onItemRemoved(adapterPosition: Int) {
        documentAdapter?.removeItem(adapterPosition)
    }
}

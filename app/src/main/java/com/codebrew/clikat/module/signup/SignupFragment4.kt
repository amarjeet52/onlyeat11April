package com.codebrew.clikat.module.signup

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.*
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.app_utils.extension.loadImage
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentSignup4Binding
import com.codebrew.clikat.modal.AppGlobal
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.bottom_navigation.MainScreenActivity
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.preferences.Prefs
import com.codebrew.clikat.retrofit.RestClient
import com.codebrew.clikat.retrofit.RetrofitUtils
import com.codebrew.clikat.utils.ClikatConstants
import com.codebrew.clikat.utils.ConnectionDetector
import com.codebrew.clikat.utils.GeneralFunctions
import com.codebrew.clikat.utils.ProgressBarDialog
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.firebase.analytics.FirebaseAnalytics
import com.codebrew.clikat.intrface.ImageCallback
import com.codebrew.clikat.app_utils.dialogintrface.ImageDialgFragment
import com.codebrew.clikat.app_utils.extension.afterTextChanged
import com.segment.analytics.Analytics
import com.segment.analytics.Properties
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_signup_4.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/*
 * Created by cbl80 on 25/4/16.
 */
class SignupFragment4 : Fragment(), View.OnClickListener, ImageCallback, EasyPermissions.PermissionCallbacks, DocumentsListAdapter.OnItemClicked {

    private var settingsData: SettingModel.DataBean.SettingData? = null
    private var documentAdapter: DocumentsListAdapter? = null

    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    private var userImage: File? = null
    private var imageFile: File? = null
    private var cd: ConnectionDetector? = null

    @Inject
    lateinit var imageUtils: ImageUtility

    @Inject
    lateinit var prefHelper: PreferenceHelper

    @Inject
    lateinit var appUtils: AppUtils

    private val imageDialog by lazy { ImageDialgFragment() }

    @Inject
    lateinit var permissionFile: PermissionFile

    private lateinit var pojoSignUp1: PojoSignUp
    private var calender :Calendar?= null
    private var forImage = AppConstants.PROFILE
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentSignup4Binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_4, container, false)
        binding.color = Configurations.colors
        binding.drawables = Configurations.drawables
        binding.strings = Configurations.strings
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireContext());
        setypeface()
        cd = ConnectionDetector(activity)

        iv_back.setOnClickListener(this)
        ivImage.setOnClickListener(this)
        tvFinish.setOnClickListener(this)
        etDob?.setOnClickListener(this)

        pojoSignUp1 = Prefs.with(activity).getObject(DataNames.USER_DATA, PojoSignUp::class.java)
        settingsData = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        etFullname.setText(pojoSignUp1.data.firstname)
        Glide.with(requireContext()).load(pojoSignUp1.data.user_image).into(ivImage)
        initialise()
        setAdapter()
    }

    private fun initialise() {
        calender=Calendar.getInstance()
        if (settingsData?.user_id_proof != null && settingsData?.user_id_proof == "1") {
            tvuploadDocs?.visibility = View.VISIBLE
            rvUploadDocument?.visibility = View.VISIBLE
        } else {
            tvuploadDocs?.visibility = View.GONE
            rvUploadDocument?.visibility = View.GONE
        }

        if (settingsData?.is_abn_business == "1") {
            inputAbnNo?.visibility = View.VISIBLE
            inputBusinessNameLayout?.visibility = View.VISIBLE
        } else {
            inputAbnNo?.visibility = View.GONE
            inputBusinessNameLayout?.visibility = View.GONE
        }

        if (BuildConfig.CLIENT_CODE == "roadsideassistance_0649") {
            groupCdcFields.visibility = View.VISIBLE
            inputLayout.visibility = View.GONE
        }
        inputDob?.visibility=if(settingsData?.enable_date_of_birth=="1") View.VISIBLE else View.GONE
        etDob?.keyListener=null

        etFullname?.afterTextChanged {
            inputLayout?.error=null
        }
    }

    private fun setypeface() {
        etFullname.typeface = AppGlobal.regular
        tvText.typeface = AppGlobal.semi_bold
        tvFinish.typeface = AppGlobal.semi_bold
    }

    private fun setAdapter() {
        documentAdapter = DocumentsListAdapter(this)
        rvUploadDocument?.adapter = documentAdapter
        documentAdapter?.addImage("")
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.iv_back -> findNavController().popBackStack()
            R.id.ivImage ->
                if (permissionFile.hasCameraPermissions(activity ?: requireActivity())) {
                    if (cd?.isConnectingToInternet == true) {
                        forImage = AppConstants.PROFILE
                        showImagePicker()
                    }

                } else {
                    permissionFile.cameraAndGalleryTask(this)
                }

            R.id.tvFinish -> validate_values()
            R.id.etDob->{
                showCalender()
            }
        }
    }

    private fun showCalender() {
        val year = calender?.get(Calendar.YEAR)
        val month = calender?.get(Calendar.MONTH)
        val day = calender?.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, yearCal, monthOfYear, dayOfMonth ->

            // Display Selected date in textbox
            etDob.setText(getString(R.string.dob_text,dayOfMonth.toString(),monthOfYear.toString(),yearCal.toString()))

        }, year?:0, month?:0, day?:0)

        dpd.datePicker.maxDate=calender?.timeInMillis?:0L
        dpd.show()
    }

    private fun showImagePicker() {
        imageDialog.settingCallback(this)
        imageDialog.show(
                childFragmentManager,
                "image_picker"
        )
    }


    private fun validate_values() {
        if (!(BuildConfig.CLIENT_CODE == "roadsideassistance_0649") && etFullname.text.toString().trim().isEmpty()) {
            inputLayout.requestFocus()
            inputLayout.error = getString(R.string.empty_fn)
        } else if (BuildConfig.CLIENT_CODE == "roadsideassistance_0649"
                && (etFirstName.text.toString().trim().isEmpty() || etLastName.text.toString().trim().isEmpty() || etCompanyId.text.toString().trim().isEmpty() || etAssignedVehicle.text.toString().trim().isEmpty())) {
            if (etFirstName.text.toString().trim().isEmpty()) {
                inputLayoutFirstName.requestFocus()
                inputLayoutFirstName.error = getString(R.string.empty_firstName)
            } else if (etLastName.text.toString().trim().isEmpty()) {
                inputLayoutLastName.requestFocus()
                inputLayoutLastName.error = getString(R.string.empty_last_name)
            } else if (etCompanyId.text.toString().trim().isEmpty()) {
                inputLayoutCompanyId.requestFocus()
                inputLayoutCompanyId.error = getString(R.string.empty_company_id)
            } else if (etAssignedVehicle.text.toString().trim().isEmpty()) {
                inputLayoutAssignedVehicle.requestFocus()
                inputLayoutAssignedVehicle.error = getString(R.string.empty_assigned_veh)
            }
        }
        else {
            inputLayout.error = null
            if (cd?.isConnectingToInternet == true) {
                if (settingsData?.user_id_proof != null && settingsData?.user_id_proof == "1") {
                    /*list size ==1 is to add images*/
                    if ((documentAdapter?.getList()?.size ?: 0) > 1)
                        hitApiUploadDocument()
                    else
                        AppToasty.error(requireActivity(), getString(R.string.please_upload_id_proof))
                } else
                    api_hit()

            } else {
                cd?.showNoInternetDialog()
            }
        }
    }

    private fun hitApiUploadDocument() {
        val barDialog = ProgressBarDialog(activity)
        barDialog.show()
        val body: ArrayList<MultipartBody.Part>? = ArrayList()
        val list = documentAdapter?.getList()
        if (list?.isNotEmpty() == true && list.size > 1) {
            for (i in 0 until (list.size - 1)) {
                // MultipartBody.Part is used to send also the actual file name
                body?.add(MultipartBody.Part.createFormData("documents", File(list[i]).name, RetrofitUtils.imageToRequestBody(File(list[i]))))
            }
        }

        RestClient.getModalApiService(activity).uploadDocuments(body).enqueue(object : Callback<PojoSignUp?> {
            override fun onResponse(call: Call<PojoSignUp?>?, response: Response<PojoSignUp?>?) {
                barDialog.dismiss()
                if (response!!.code() == 200) {
                    val pojoSignUp = response.body()
                    if (pojoSignUp?.status == ClikatConstants.STATUS_SUCCESS) {
                        api_hit()
                    } else if (pojoSignUp?.status == ClikatConstants.STATUS_INVALID_TOKEN) {
                        //  cd!!.loginExpiredDialog()
                    } else {
                        GeneralFunctions.showSnackBar(view, pojoSignUp?.message, activity)
                    }
                }
            }

            override fun onFailure(call: Call<PojoSignUp?>?, t: Throwable?) {
                barDialog.dismiss()
            }
        })
    }

    private fun api_hit() {
        val barDialog = ProgressBarDialog(activity)
        barDialog.show()
        //   val pojoSignUp1 = Prefs.with(activity).getObject(DataNames.USER_DATA, PojoSignUp::class.java)
        val hashMap = HashMap<String?, RequestBody?>()
        hashMap["accessToken"] = CommonUtils.convrtReqBdy(pojoSignUp1.data?.access_token ?: "")
        hashMap["name"] = CommonUtils.convrtReqBdy(etFullname?.text.toString().trim())
        if (userImage?.isFile == true) {
            val requestBody = (userImage
                    ?: File("")).asRequestBody("image/*".toMediaTypeOrNull())
            hashMap["profilePic"] = requestBody
        }

        if (etAbnNo?.text.toString().trim().isNotEmpty())
            hashMap["abn_number"] = CommonUtils.convrtReqBdy(etAbnNo?.text.toString().trim())

        if (etBusinessName?.text.toString().trim().isNotEmpty())
            hashMap["business_name"] = CommonUtils.convrtReqBdy(etBusinessName?.text.toString().trim())

        if (Prefs.with(activity).getString(PrefenceConstants.USER_TEMP_PASSWORD, "").isNotEmpty()) {
            hashMap["password"] = CommonUtils.convrtReqBdy(Prefs.with(activity).getString(PrefenceConstants.USER_TEMP_PASSWORD, ""))
        }
        if(settingsData?.enable_date_of_birth=="1"){
            hashMap["dateOfBirth"]=CommonUtils.convrtReqBdy(DateTimeUtils().getDateFromTimestamp(calender?.timeInMillis.toString(),"YYYY-MM-dd"))
        }

        RestClient.getModalApiService(activity).signUpFinish(hashMap).enqueue(object : Callback<PojoSignUp?> {
            override fun onResponse(call: Call<PojoSignUp?>?, response: Response<PojoSignUp?>?) {
                barDialog.dismiss()
                if (response?.code() == 200) {
                    val pojoSignUp = response.body()
                    if (pojoSignUp?.status == ClikatConstants.STATUS_SUCCESS) {

                        //     etFullname.setText(pojoSignUp.data.firstname)
                        //   Glide.with(requireContext()).load(pojoSignUp.data.user_image).into(ivImage)

                        pojoSignUp.data.firstname = etFullname?.text.toString().trim()
                        pojoSignUp.data.user_image = ClikatConstants.LOCAL_FILE_PREFIX +
                                userImage
                        Prefs.with(activity).save(PrefenceConstants.USER_ID, pojoSignUp.data.id.toString())
                        Prefs.with(activity).save(PrefenceConstants.ACCESS_TOKEN, pojoSignUp.data.access_token)

                        Prefs.with(activity).save(PrefenceConstants.USER_LOGGED_IN, true)

                        Prefs.with(activity).save(DataNames.USER_DATA, pojoSignUp)
                        Prefs.with(activity).save(PrefenceConstants.CUSTOMER_PAYMENT_ID, pojoSignUp.data.customer_payment_id)


                        pojoSignUp.data.user_created_id?.let {
                            Prefs.with(activity).save(PrefenceConstants.USER_CHAT_ID, it)
                        }
                        pojoSignUp.data.id_for_invoice?.let {
                            Prefs.with(activity).save(PrefenceConstants.ID_FOR_INVOICE, it)
                        }
                        pojoSignUp.data.referral_id?.let {
                            Prefs.with(activity).save(PrefenceConstants.USER_REFERRAL_ID, it)
                        }


                        val settingBean = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
                        val isGuest = settingBean?.login_template.isNullOrEmpty() || settingBean?.login_template == "0"

                        //    findNavController().navigate(SignupFragment4Directions.actionSignupFragment4ToQuestionsFragment())
                        if (BuildConfig.CLIENT_CODE == "yummy_0122" || BuildConfig.CLIENT_CODE == "readychef_0309") {
                            getAnalytic(pojoSignUp.data.id.toString(), pojoSignUp.data.email ?: "")

                            Analytics.with(context).track("New SignUp", Properties().putValue("Full Name", etFullname?.text.toString().trim())
                                    .putValue("Email", pojoSignUp.data.email ?: ""))
                        }
                        appUtils.triggerEvent("login")

                        if (isGuest) {
                            activity?.setResult(Activity.RESULT_OK)
                        } else {
                            activity?.launchActivity<MainScreenActivity>()
                        }

                        activity?.finish()
                    } else if (pojoSignUp?.status == ClikatConstants.STATUS_INVALID_TOKEN) {
                        //  cd!!.loginExpiredDialog()
                    } else {
                        GeneralFunctions.showSnackBar(view, pojoSignUp?.message, activity)
                    }
                }
            }

            override fun onFailure(call: Call<PojoSignUp?>?, t: Throwable?) {
                barDialog.dismiss()
            }
        })
    }

    private fun getAnalytic(id: String, email: String) {
        val bundle = Bundle()
        bundle.putString("UserId", id)
        bundle.putString("Email", email)
        mFirebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
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

            if (cd?.isConnectingToInternet == true) {
                if (imageFile?.isRooted == true) {
                    if (forImage == AppConstants.UPLOAD_DOCUMENTS) {
                        documentAdapter?.addImage(imageUtils.compressImage(imageFile?.absolutePath
                                ?: ""))
                    } else
                        loadUserImage(imageUtils.compressImage(imageFile?.absolutePath ?: ""))
                }
            }
        } else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == AppConstants.GalleyPicker) {
            if (data != null) {
                if (cd?.isConnectingToInternet == true) {
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
                        if (forImage == AppConstants.UPLOAD_DOCUMENTS) {
                            documentAdapter?.addImage(imageUtils.compressImage(imgDecodableString))
                        } else
                            loadUserImage(imageUtils.compressImage(imgDecodableString))
                    }
                }
            }
        }

    }

    private fun loadUserImage(compressImage: String) {
        userImage = File(compressImage)

        ivImage.loadImage(compressImage)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == AppConstants.CameraGalleryPicker) {

            if (cd?.isConnectingToInternet == true) {
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
        forImage = AppConstants.UPLOAD_DOCUMENTS

        if (permissionFile.hasCameraPermissions(activity ?: requireActivity())) {
            if (cd?.isConnectingToInternet == true) {
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
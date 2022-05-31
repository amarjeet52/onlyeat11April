package com.codebrew.clikat.module.more_setting


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.*
import com.codebrew.clikat.app_utils.SocketManager.Companion.destroy
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.SkipAppScreens
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentSkipSettingsBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.change_language.SkipChangeLanguage
import com.codebrew.clikat.module.location.LocationActivity
import com.codebrew.clikat.module.more_setting.adapter.SkipAccountMainAdapter
import com.codebrew.clikat.module.product.product_listing.DialogListener
import com.codebrew.clikat.module.webview.WebViewActivity
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.Dialogs.ChangePasswordDilaog
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.StaticFunction.sendEmail
import com.codebrew.clikat.utils.configurations.Configurations
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_skip_settings.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.lang.reflect.Type
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
class SkipMoreSettingFragment : BaseFragment<FragmentSkipSettingsBinding, MoreSettingViewModel>(), DialogListener, MoreSettingNavigator,
        EasyPermissions.PermissionCallbacks {

    @Inject
    lateinit var prefHelper: PreferenceHelper

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var dialogsUtil: DialogsUtil

    private lateinit var viewModel: MoreSettingViewModel

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var googleHelper: GoogleLoginHelper

    @Inject
    lateinit var permissionUtil: PermissionFile

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }


    private var adapter: SkipAccountMainAdapter? = null

    private var settingBean: SettingModel.DataBean.SettingData? = null
    private var featureBean: ArrayList<SettingModel.DataBean.FeatureData>? = null
    private var mBinding: FragmentSkipSettingsBinding? = null
    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null
    private var terminologyBean: SettingModel.DataBean.Terminology? = null
    private var userDataModel: PojoSignUp? = null
    private val colorConfig by lazy { Configurations.colors }
    private var screenType: String? = null


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_skip_settings
    }

    override fun getViewModel(): MoreSettingViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(MoreSettingViewModel::class.java)
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding
        viewDataBinding.color = colorConfig

        initialise()
        listeners()

        prefHelper.getKeyValue(DataNames.FEATURE_DATA, PrefenceConstants.TYPE_STRING).toString().let {
            val listType: Type = object : TypeToken<ArrayList<SettingModel.DataBean.FeatureData?>?>() {}.type
            featureBean = Gson().fromJson(it, listType)
        }
        screenFlowBean = prefHelper.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        terminologyBean = prefHelper.getGsonValue(PrefenceConstants.APP_TERMINOLOGY, SettingModel.DataBean.Terminology::class.java)
        adapter = SkipAccountMainAdapter(this.activity
                ?: requireContext(), textConfig, appUtils, screenType ?: "MAIN")

        adapter?.loadData(prefHelper.getCurrentUserLoggedIn(), screenFlowBean, settingBean, featureBean)

        val termsCondition = prefHelper.getGsonValue(PrefenceConstants.TERMS_CONDITION, SettingModel.DataBean.TermCondition::class.java)
        val adminData = prefHelper.getGsonValue(PrefenceConstants.ADMIN_DETAILS, SettingModel.DataBean.AdminDetail::class.java)

        adapter?.settingCallback(SkipAccountMainAdapter.TagListener {
            when (it) {

                getString(R.string.about_the_app) -> {
                    val bundle = bundleOf("screenType" to SkipAppScreens.ABOUT_APP.type)
                    navController(this@SkipMoreSettingFragment).navigate(R.id.action_other_to_self, bundle)
                }
                getString(R.string.account) -> {
                    val bundle = bundleOf("screenType" to SkipAppScreens.ACCOUNT.type)
                    navController(this@SkipMoreSettingFragment).navigate(R.id.action_other_to_self, bundle)
                }
                getString(R.string.account_info) -> {
                    if (prefHelper.getCurrentUserLoggedIn()) {
                        val bundle = bundleOf("screenType" to SkipAppScreens.PROFILE.type)
                        navController(this@SkipMoreSettingFragment).navigate(R.id.action_other_to_self, bundle)
                    } else {
                        appUtils.checkLoginFlow(requireActivity(), AppConstants.REQUEST_USER_PROFILE)
                    }
                }

                getString(R.string.get_help) -> {
                    val bundle = bundleOf("screenType" to SkipAppScreens.GET_HELP.type)
                    navController(this@SkipMoreSettingFragment).navigate(R.id.action_other_to_self, bundle)
                }

                getString(R.string.email_us) -> {
                    requireContext().sendEmail(adminData?.email ?: "")
                }
                getString(R.string.call_us) -> {
                    if (permissionUtil.hasCallPermissions(requireContext())) {
                        callPhone(adminData?.phone_number ?: "")
                    } else {
                        permissionUtil.phoneCallTask(requireContext())
                    }
                }
                getString(R.string.terms_of_use) -> {
                    if (termsCondition?.termsAndConditions == 0) return@TagListener
                    activity?.launchActivity<WebViewActivity> {
                        putExtra("terms", 0)
                    }
                }
                getString(R.string.privacy_policy) -> {
                    activity?.launchActivity<WebViewActivity> {
                        putExtra("terms", 3)
                    }
                }
                getString(R.string.about_feedback) -> {
                    if (settingBean?.is_new_feedback_theme == "1")
                        navController(this@SkipMoreSettingFragment).navigate(R.id.action_feedback_fragment_new)
                    else
                        navController(this@SkipMoreSettingFragment).navigate(R.id.action_feedback_fragment)
                }

                getString(R.string.faq) -> {
                    activity?.launchActivity<WebViewActivity> {
                        putExtra("terms", 4)
                    }
                }

                getString(R.string.my_favorites) -> {
                    if (prefHelper.getCurrentUserLoggedIn()) {
                        navController(this@SkipMoreSettingFragment).navigate(R.id.action_other_to_wishListFrag)
                    } else {
                        appUtils.checkLoginFlow(requireActivity(), AppConstants.REQUEST_WISH_LIST)
                    }
                }

                getString(R.string.referral) -> {
                    navController(this@SkipMoreSettingFragment).navigate(R.id.action_other_to_manageReferralFrag)
                }

                getString(R.string.languages) -> {
                    SkipChangeLanguage.newInstance().show(childFragmentManager, "change_language")
                }


                getString(R.string.linkein) -> {
                    StaticFunction.openCustomChrome(requireActivity(), settingBean?.linkedin_link
                            ?: "")
                }
                getString(R.string.twitter) -> {
                    StaticFunction.openCustomChrome(requireActivity(), settingBean?.twitter_link
                            ?: "")
                }
                getString(R.string.google_plus) -> {
                    StaticFunction.openCustomChrome(requireActivity(), settingBean?.google_link
                            ?: "")
                }
                getString(R.string.about_us) -> {
                    if (termsCondition?.privacyPolicy == 0) return@TagListener
                    activity?.launchActivity<WebViewActivity> {
                        putExtra("terms", 1)
                    }
                    //navController(this@MoreSettingFragment).navigate(R.id.action_other_to_questionFrag)
                }

                getString(R.string.requests, textConfig?.order) -> {
                    navController(this@SkipMoreSettingFragment).navigate(R.id.action_other_to_RequestsFrag)
                }

                getString(R.string.contact_us) -> {
                    activity?.launchActivity<WebViewActivity> {
                        putExtra("terms", 5)
                    }
                }

                getString(R.string.notification) -> {
                    navController(this@SkipMoreSettingFragment).navigate(R.id.action_other_to_notifications)
                }

                getString(R.string.my_offers) -> {
                    navController(this@SkipMoreSettingFragment).navigate(R.id.action_other_to_banners)
                }
                getString(R.string.change_password) -> {
                    ChangePasswordDilaog(activity, false, ChangePasswordDilaog.OnOkClickListener { }).show()
                }
                getString(R.string.my_orders) -> {
                    navController(this@SkipMoreSettingFragment).navigate(R.id.action_other_to_OrdersFrag)
                }


            }
        })

        rv_more_tag.adapter = adapter


        lyt_profile.setOnClickListener {
            if (settingBean?.is_pickup_primary == "1") {
                if (!prefHelper.getCurrentUserLoggedIn()) {
                    appUtils.checkLoginFlow(requireActivity(), AppConstants.REQUEST_USER_PROFILE)
                }
                return@setOnClickListener
            }


            if (prefHelper.getCurrentUserLoggedIn()) {
                if (settingBean?.show_ecom_v2_theme == "1") {
                    navController(this@SkipMoreSettingFragment).navigate(R.id.action_other_to_settingFragment2V2)
                } else {
                    navController(this@SkipMoreSettingFragment).navigate(R.id.action_other_to_settingFragment2)
                }
            } else {
                appUtils.checkLoginFlow(requireActivity(), AppConstants.REQUEST_USER_PROFILE)
            }

        }


    }

    private fun initialise() {
        settingBean = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        activity?.let { context ->
            appUtils.checkGoogleLogin(context).let {
                if (it.isNotEmpty()) {
                    googleHelper.initialize(context, it)
                }
            }
        }
        screenType = arguments.let {
            it?.getString("screenType", "MAIN")
        }

        if (screenType != null && screenType != SkipAppScreens.MAIN.type && screenType != SkipAppScreens.PROFILE.type) {
            if (screenType == SkipAppScreens.ABOUT_APP.type || screenType == SkipAppScreens.ACCOUNT.type) {
                clMain?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            tvTitle?.text = when (screenType) {
                SkipAppScreens.GET_HELP.type -> getString(R.string.get_help)
                SkipAppScreens.ACCOUNT.type -> getString(R.string.account)
                else -> getString(R.string.about_app)
            }
            groupTitle?.visibility = View.VISIBLE
            ivCross?.visibility = View.GONE
        } else {
            groupTitle?.visibility = View.GONE
            ivCross?.visibility = View.VISIBLE
        }
        tvLogout?.visibility=if(screenType==SkipAppScreens.PROFILE.type) View.VISIBLE else View.GONE
        groupSocialLinks?.visibility=if(screenType==SkipAppScreens.ABOUT_APP.type) View.VISIBLE else View.GONE
        lyt_profile?.visibility = if (screenType == SkipAppScreens.PROFILE.type) View.VISIBLE else View.GONE
    }

    private fun listeners() {
        ivBack?.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }
        ivCross?.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }
        tvLogout?.setOnClickListener {
            if (prefHelper.getCurrentUserLoggedIn()) {
                dialogsUtil.openAlertDialog(activity
                        ?: requireContext(), getString(R.string.log_out_msg), getString(R.string.ok), getString(R.string.cancel), this)
            }
        }
        ivFacebook?.setOnClickListener {
            if(!settingBean?.fackbook_link.isNullOrEmpty())
            StaticFunction.openCustomChrome(requireActivity(), settingBean?.fackbook_link
                    ?: "")
        }
        ivInstagram?.setOnClickListener {
            if(!settingBean?.instagram_link.isNullOrEmpty())
            StaticFunction.openCustomChrome(requireActivity(), settingBean?.instagram_link
                    ?: "")
        }
        ivTwitter?.setOnClickListener {
            if(!settingBean?.twitter_link.isNullOrEmpty())
            StaticFunction.openCustomChrome(requireActivity(), settingBean?.twitter_link
                    ?: "")
        }
    }

    private fun callPhone(number: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null))
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Manifest.permission.CALL_PHONE
            startActivity(intent)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 1)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
    }

    private fun loadProfile() {
        if (screenType == SkipAppScreens.PROFILE.type && prefHelper.getCurrentUserLoggedIn()) {
            userDataModel = prefHelper.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
            StaticFunction.loadUserImage(userDataModel?.data?.user_image ?: "", iv_profile, true)
            tv_name.text = userDataModel?.data?.firstname ?: ""
            lyt_profile?.visibility = View.VISIBLE
            adapter?.loadData(prefHelper.getCurrentUserLoggedIn(), screenFlowBean, settingBean, featureBean)
            rv_more_tag.adapter?.notifyDataSetChanged()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AppConstants.REQUEST_USER_PROFILE && resultCode == Activity.RESULT_OK) {
            val pojoLoginData = StaticFunction.isLoginProperly(activity)
            loadProfile()
            if (pojoLoginData.data != null) {
                adapter?.loadData(prefHelper.getCurrentUserLoggedIn(), screenFlowBean, settingBean, featureBean)
                rv_more_tag.adapter?.notifyDataSetChanged()
                //  navController(this@MoreSettingFragment).navigate(R.id.action_other_to_settingFragment2)
            }
        } else if (requestCode == AppConstants.REQUEST_WISH_LIST && resultCode == Activity.RESULT_OK) {
            navController(this@SkipMoreSettingFragment).navigate(R.id.action_other_to_wishListFrag)
        }
    }

    override fun onSucessListner() {
        prefHelper.logout()

        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut()
        }
        googleHelper.logoutGoogle {
            destroy()
            StaticFunction.clearCart(activity)
            dataManager.removeValue(DataNames.DISCOUNT_AMOUNT)
            StaticFunction.clearCart(activity)
            activity?.launchActivity<LocationActivity>()
            activity?.finishAffinity()
        }
    }

    override fun onErrorListener() {

    }

    override fun onSosSuccess() {
        AppToasty.success(requireContext(), getString(R.string.request_submitted))
    }

    override fun onLogoutSuccess() {

    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }


}

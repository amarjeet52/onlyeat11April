package com.codebrew.clikat.module.more_setting


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.codebrew.clikat.BR
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.*
import com.codebrew.clikat.app_utils.SocketManager.Companion.destroy
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.ReceiverType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.orderDetail.Agent
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentMoreSettingBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.bottom_navigation.OnNavigationMenuClicked
import com.codebrew.clikat.module.cart.AddVehicleActivity
import com.codebrew.clikat.module.cart.VehicleListingActivity
import com.codebrew.clikat.module.cart.v2.DEtailVehicleActivity
import com.codebrew.clikat.module.change_language.ChangeLanguage
import com.codebrew.clikat.module.dialog_adress.AddressDialogActivity
import com.codebrew.clikat.module.dialog_adress.SelectlocActivity
import com.codebrew.clikat.module.dialog_adress.VEHICLE_PARAM
import com.codebrew.clikat.module.location.LocationActivity
import com.codebrew.clikat.module.more_setting.adapter.MoreTagAdapter
import com.codebrew.clikat.module.new_signup.signup.SignInActivityNew
import com.codebrew.clikat.module.product.product_listing.DialogListener
import com.codebrew.clikat.module.signup.SignupActivity
import com.codebrew.clikat.module.webview.WebViewActivity
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.user_chat.UserChatActivity
import com.codebrew.clikat.utils.GeneralFunctions
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.StaticFunction.openWhatsApp
import com.codebrew.clikat.utils.StaticFunction.sendEmail
import com.codebrew.clikat.utils.configurations.Configurations
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_more_setting.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.lang.reflect.Type
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
class MoreSettingFragment : BaseFragment<FragmentMoreSettingBinding, MoreSettingViewModel>(), DialogListener,
        MoreSettingNavigator, EasyPermissions.PermissionCallbacks {


    @Inject
    lateinit var permissionUtil: PermissionFile

    private var adminData: SettingModel.DataBean.AdminDetail? = null
var User_id=""
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

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }

    private var userDataModel: PojoSignUp? = null

    private var adapter: MoreTagAdapter? = null
var self_pickup=""
    private var settingBean: SettingModel.DataBean.SettingData? = null
    private var featureBean: ArrayList<SettingModel.DataBean.FeatureData>? = null
    private var mBinding: FragmentMoreSettingBinding? = null
    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null
    private var terminologyBean: SettingModel.DataBean.Terminology? = null
    private var navigationListeners: OnNavigationMenuClicked? = null
    private val colorConfig by lazy { Configurations.colors }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_more_setting
    }

    override fun getViewModel(): MoreSettingViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(MoreSettingViewModel::class.java)
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this
        User_id = prefHelper.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING)?.toString()!!

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNavigationMenuClicked)
            navigationListeners = context
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding
        viewDataBinding.color = colorConfig
        self_pickup = if (prefHelper.getKeyValue(AppConstants.ROAD_MAP_TYPE, PrefenceConstants.TYPE_STRING)!!.equals("Delivery")) {
            "8"
        } else {
            "9"
        }
        activity?.let { context ->
            appUtils.checkGoogleLogin(context).let {
                if (it.isNotEmpty()) {

                    googleHelper.initialize(context, it)
                }
            }
        }
        adminData = prefHelper.getGsonValue(PrefenceConstants.ADMIN_DETAILS, SettingModel.DataBean.AdminDetail::class.java)
        settingBean = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        prefHelper.getKeyValue(DataNames.FEATURE_DATA, PrefenceConstants.TYPE_STRING).toString().let {
            val listType: Type = object : TypeToken<ArrayList<SettingModel.DataBean.FeatureData?>?>() {}.type
            featureBean = Gson().fromJson(it, listType)
        }
        screenFlowBean = prefHelper.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        terminologyBean = prefHelper.getGsonValue(PrefenceConstants.APP_TERMINOLOGY, SettingModel.DataBean.Terminology::class.java)


        adapter = MoreTagAdapter(this.activity ?: requireContext(), textConfig, appUtils,self_pickup)

        adapter?.loadData(prefHelper.getCurrentUserLoggedIn(), screenFlowBean, settingBean, featureBean)
        val termsCondition = prefHelper.getGsonValue(PrefenceConstants.TERMS_CONDITION, SettingModel.DataBean.TermCondition::class.java)

        adapter?.settingCallback(MoreTagAdapter.TagListener {

            when (it) {
                getString(R.string.share_app) -> {
                    val shareMsg = if (settingBean?.app_sharing_message?.isEmpty() == false) {
                        "${settingBean?.app_sharing_message} ${getString(R.string.app_link_share, getString(R.string.share_body, BuildConfig.APPLICATION_ID))}"
                    } else {
                        getString(R.string.share_body, BuildConfig.APPLICATION_ID)
                    }

                    GeneralFunctions.shareApp(activity, shareMsg)
                }

                getString(R.string.sos) -> {
                    if (isNetworkConnected)
                        viewModel.apiSos()

                }
                getString(R.string.terms) -> {
//                    if (termsCondition?.termsAndConditions == 0) return@TagListener
                    activity?.launchActivity<WebViewActivity> {
                        putExtra("terms", 0)
                    }
                }
                getString(R.string.your_vehicle) -> {
//                    if (termsCondition?.termsAndConditions == 0) return@TagListener
                    val intent = Intent(activity, VehicleListingActivity::class.java)
                    intent.putExtra("user_id",User_id)
                    startActivity(intent)
                }

                getString(R.string.privacy_policy) -> {
                    activity?.launchActivity<WebViewActivity> {
                        putExtra("terms", 3)
                    }
                }

                getString(R.string.referral) -> {
                    navController(this@MoreSettingFragment).navigate(R.id.action_other_to_manageReferralFrag)
                }

                getString(R.string.change_language) -> {
                    ChangeLanguage.newInstance().show(childFragmentManager, "change_language")
                }

                getString(R.string.facebook) -> {
                    StaticFunction.openCustomChrome(requireActivity(), settingBean?.fackbook_link
                            ?: "")
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
                textConfig?.wishlist ?: getString(R.string.wallet) -> {
                    if (prefHelper.getCurrentUserLoggedIn()) {
                        navController(this@MoreSettingFragment).navigate(R.id.action_other_to_wishListFrag)
                    } else {

                        appUtils.checkLoginFlow(requireActivity(), AppConstants.REQUEST_WISH_LIST)
                    }
                }
                textConfig?.loyalty_points ?: getString(R.string.loyality_points) -> {
                    navController(this@MoreSettingFragment).navigate(R.id.action_other_to_loyalty_Frag)
                }
                getString(R.string.add_new)->{
                    if (dataManager.getCurrentUserLoggedIn()) {
                        val intent = Intent(requireActivity(), SelectlocActivity::class.java)
                        intent.putExtra("is_new", 1)
                        intent.putExtra("type", "add")
                       startActivity(intent)
                                 } else {
                        appUtils.checkLoginFlow(requireActivity(), AppConstants.REQUEST_ADDRESS_ADD)
                    }
                }
                getString(R.string.contact_us) -> {
                    val whatsAppNumber = prefHelper.getKeyValue(PrefenceConstants.WHATSAPP_PHONE_NUMBER, PrefenceConstants.TYPE_STRING)?.toString()
                    if (!whatsAppNumber.isNullOrEmpty())
                        requireContext().openWhatsApp(whatsAppNumber)
                }

                getString(R.string.feedback) -> {
                    if (settingBean?.is_new_feedback_theme == "1")
                        navController(this@MoreSettingFragment).navigate(R.id.action_feedback_fragment_new)
                    else
                        navController(this@MoreSettingFragment).navigate(R.id.action_feedback_fragment)
                }

                getString(R.string.requests, textConfig?.order) -> {
                    navController(this@MoreSettingFragment).navigate(R.id.action_other_to_RequestsFrag)
                }

                getString(R.string.customer_support) -> {
                    activity?.launchActivity<WebViewActivity> {
                        putExtra("terms", 5)
                    }
                }

                textConfig?.wallet ?: getString(R.string.wallet) -> {
                    navController(this@MoreSettingFragment).navigate(R.id.action_other_wallet)
                }

                getString(R.string.logout) -> {
                    if (prefHelper.getCurrentUserLoggedIn()) {
                        dialogsUtil.openAlertDialog(activity
                                ?: requireContext(), getString(R.string.log_out_msg), getString(R.string.ok), getString(R.string.cancel), this)
                    }
                }

                getString(R.string.faq) -> {
                    activity?.launchActivity<WebViewActivity> {
                        putExtra("terms", 4)
                    }
                }

                getString(R.string.become_care_giver) -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Tobacco-106835007601737/"))
                    startActivity(intent)
                }

                textConfig?.my_subscription ?: getString(R.string.subscriptions) -> {
                    navController(this@MoreSettingFragment).navigate(R.id.action_other_to_subscriptionFrag)
                }

                getString(R.string.orders) -> {
                    navController(this@MoreSettingFragment).navigate(R.id.action_other_to_OrdersFrag)
                }

                getString(R.string.chat_with_admin) -> {
                    val userInfo = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
                    val data = Agent()
                    data.message_id = userInfo?.data?.message_id
                    data.name = getString(R.string.admin)
                    activity?.launchActivity<UserChatActivity> {
                        putExtra("userType", ReceiverType.ADMIN.type)
                        putExtra("userData", data)
                    }
                }
                getString(R.string.table_booking) -> {
                    navController(this@MoreSettingFragment).navigate(R.id.action_other_to_booked_tables)
                }

                getString(R.string.your_orders) -> {
                    navController(this@MoreSettingFragment).navigate(R.id.action_other_to_Order_details)
                }
                getString(R.string.social_post) -> {
                    navController(this@MoreSettingFragment).navigate(R.id.action_other_to_PostsHomeFragment)
                }
                getString(R.string.support_email) -> {
                    if (!adminData?.email.isNullOrEmpty())
                        requireContext().sendEmail("customerservice@craveqatar.com")
                }
                getString(R.string.support_number) -> {
                    if (permissionUtil.hasCallPermissions(requireContext())) {
                        callPhone("44072888")
                    } else {
                        permissionUtil.phoneCallTask(requireContext())
                    }
                }
            }
        })

        rv_more_tag.adapter = adapter

        if (settingBean?.show_platform_versions == "1") {
            tv_app_version.visibility = View.VISIBLE
            tv_app_version.text = getString(R.string.version_tag, BuildConfig.VERSION_NAME)
        } else {
            tv_app_version.visibility = View.GONE
        }

        tvLogin?.setOnClickListener {
            appUtils.checkLoginFlow(requireActivity(), AppConstants.REQUEST_USER_PROFILE)
        }

        tvRegister?.setOnClickListener {
            requireActivity().launchActivity<SignupActivity> {
                putExtra(DataNames.PHONE_VERIFIED, "0")
            }
        }

        lyt_profile.setOnClickListener {
            if (settingBean?.is_pickup_primary == "1") {

                if (!prefHelper.getCurrentUserLoggedIn()) {
                    appUtils.checkLoginFlow(requireActivity(), AppConstants.REQUEST_USER_PROFILE)
                }
                return@setOnClickListener
            }


            if (prefHelper.getCurrentUserLoggedIn()) {

                if (settingBean?.show_ecom_v2_theme == "1") {
                    navController(this@MoreSettingFragment).navigate(R.id.action_other_to_settingFragment2V2)
                } else {
                    navController(this@MoreSettingFragment).navigate(R.id.action_other_to_settingFragment2)
                }
            } else {
                appUtils.checkLoginFlow(requireActivity(), AppConstants.REQUEST_USER_PROFILE)
            }

        }

        if (settingBean?.show_ecom_v2_theme == "1") {
            tb_back?.visibility = View.VISIBLE
            tb_back?.setOnClickListener {
                Navigation.findNavController(requireView()).popBackStack()
            }
            toolbar?.elevation = 0f
            toolbar.background = ContextCompat.getDrawable(requireActivity(), R.drawable.background_toolbar_bottom_radius)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                toolbar.background.colorFilter = BlendModeColorFilter(Color.parseColor(colorConfig.toolbarColor), BlendMode.SRC_ATOP)
            }

            toolbar_layout.visibility = View.VISIBLE
            tb_title.text = textConfig?.otherTab
        } else if (settingBean?.is_juman_flow_enable == "1") {
            tb_title?.text = textConfig?.otherTab
            tb_title?.setTextColor(Color.parseColor(colorConfig.toolbarText))
            toolbar_layout.visibility = View.VISIBLE
            tb_back?.visibility = View.GONE
            ivNavigation?.visibility = View.VISIBLE
            tb_icon?.visibility = View.GONE
            ivNavigation?.setOnClickListener {
                navigationListeners?.onNavigationMenuChanged()
            }
        } else {
            toolbar.setTitleTextColor(Color.parseColor(colorConfig.toolbarText))
            toolbar.title = textConfig?.otherTab
            toolbar_layout.visibility = View.GONE
        }

        if (settingBean?.is_pickup_primary == "1") {
            val tempTabTitles = mutableListOf(getString(R.string.user_detail), getString(R.string.notifications), getString(R.string.payments))
            val tempTabImages = mutableListOf(R.drawable.ic_menu_user, R.drawable.ic_bell_profile, R.drawable.ic_credit_card)

            tabLayoutProfile?.visibility = View.VISIBLE
            more_lineView?.visibility = View.VISIBLE
            tempTabImages.forEachIndexed { index, i ->
                val tab = tabLayoutProfile.newTab()
                tab.text = tempTabTitles[index]
                tab.icon = ContextCompat.getDrawable(requireActivity(), i)
                tabLayoutProfile.addTab(tab)

                tabLayoutProfile.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        rv_more_tag?.visibility = View.VISIBLE
                        when (tab?.position) {
                            0 -> {
                                adapter?.loadData(prefHelper.getCurrentUserLoggedIn(), screenFlowBean, settingBean, featureBean)
                                rv_more_tag.adapter = adapter
                            }
                            1 -> {
                                rv_more_tag?.visibility = View.GONE
                            }
                            2 -> {
                                showCardSectionListItem()
                            }
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {
                        //do nothing
                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {
                        //do nothing
                    }

                })
            }
        }
    }

    private fun showCardSectionListItem() {
        adapter?.loadCardData()
        rv_more_tag.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
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

    private fun loadProfile() {
        if (prefHelper.getCurrentUserLoggedIn()) {
            userDataModel = prefHelper.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
            StaticFunction.loadUserImage(userDataModel?.data?.user_image ?: "", iv_profile, true)

            tv_name.text = userDataModel?.data?.firstname ?: ""

            login_signup_text.visibility = View.INVISIBLE
            grpRegisterLogin.visibility = View.GONE
            gp_profile.visibility = View.VISIBLE

            if (userDataModel?.data?.email.isNullOrEmpty())
                tv_email?.visibility = View.GONE
            else {
                tv_email?.visibility = View.VISIBLE
                tv_email.text = userDataModel?.data?.email ?: ""
            }
            lyt_profile?.visibility = View.VISIBLE
            adapter?.loadData(prefHelper.getCurrentUserLoggedIn(), screenFlowBean, settingBean, featureBean)
            rv_more_tag.adapter?.notifyDataSetChanged()
        } else {
            gp_profile.visibility = View.GONE
            if (settingBean?.is_lubanah_theme == "1") {
                grpRegisterLogin.visibility = View.VISIBLE
                login_signup_text.visibility = View.INVISIBLE
                lyt_profile?.visibility = View.GONE
            } else {
                lyt_profile?.visibility = View.VISIBLE
                grpRegisterLogin.visibility = View.GONE
                login_signup_text.visibility = View.VISIBLE
            }
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
            navController(this@MoreSettingFragment).navigate(R.id.action_other_to_wishListFrag)
        }
    }

    override fun onSucessListner() {

        val headers = hashMapOf(
                "accessToken" to dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()

        )
        viewModel.logOut()
    }

    override fun onErrorListener() {

    }

    override fun onSosSuccess() {
        AppToasty.success(requireContext(), getString(R.string.request_submitted))
    }

    override fun onLogoutSuccess() {

      //  prefHelper.logout()

        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut()
        }
        googleHelper.logoutGoogle {
            destroy()
            StaticFunction.clearCart(activity)
            dataManager.removeValue(DataNames.DISCOUNT_AMOUNT)
            dataManager.removeValue(PrefenceConstants.ID_FOR_INVOICE)
            StaticFunction.clearCart(activity)
            activity?.launchActivity<SignInActivityNew>()
            activity?.finishAffinity()
        }


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

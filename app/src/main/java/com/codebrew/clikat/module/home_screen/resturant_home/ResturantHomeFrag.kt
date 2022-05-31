package com.codebrew.clikat.module.home_screen.resturant_home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppToasty
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.CommonUtils.Companion.toArrayList
import com.codebrew.clikat.app_utils.extension.loadImage
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.DeliveryType
import com.codebrew.clikat.data.FoodAppType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.ListItem
import com.codebrew.clikat.data.model.api.LoyaltyData
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentResturantHomeBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.cart.SCHEDULE_REQUEST
import com.codebrew.clikat.module.dialog_adress.AddressDialogFragment
import com.codebrew.clikat.module.dialog_adress.interfaces.AddressDialogListener
import com.codebrew.clikat.module.dialog_adress.v2.AddressDialogFragmentV2
import com.codebrew.clikat.module.home_screen.HomeFragment
import com.codebrew.clikat.module.home_screen.resturant_home.dineIn.DineInResturantFrag
import com.codebrew.clikat.module.home_screen.resturant_home.pickup.PickupResturantFrag
import com.codebrew.clikat.module.loyaltyPoints.DataLoyality
import com.codebrew.clikat.module.loyaltyPoints.LoyaltyPointsNavigator
import com.codebrew.clikat.module.loyaltyPoints.LoyaltyPointsViewModel
import com.codebrew.clikat.module.roadmap.RoadMapActivity
import com.codebrew.clikat.module.signup.declaration.DeclarationDialogListener
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_resturant_home.*
import kotlinx.android.synthetic.main.toolbar_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import ru.nikartm.support.BadgePosition
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
const val REFERAL_REQUEST = 595
const val WALLET_REQUEST = 596

class ResturantHomeFrag : Fragment(), TabLayout.OnTabSelectedListener, AddressDialogListener, LoyaltyPointsNavigator {


    private var tabNames = emptyArray<String>()

    @Inject
    lateinit var prefHelper: PreferenceHelper

    private lateinit var viewModel: LoyaltyPointsViewModel

    @Inject
    lateinit var factory: ViewModelProviderFactory

    private var self_Pickup = ""

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var appUtils: AppUtils

    private var clientInform: SettingModel.DataBean.SettingData? = null
    private var bookingFlowBean: SettingModel.DataBean.BookingFlowBean? = null


    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null

    private var mAdapter: PagerAdapter? = null

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }
    private var currentPos: Int? = null

    private val decimalFormat: DecimalFormat = DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH))



    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        viewModel = ViewModelProviders.of(this, factory).get(LoyaltyPointsViewModel::class.java)
        screenFlowBean = prefHelper.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        clientInform = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        bookingFlowBean = prefHelper.getGsonValue(DataNames.BOOKING_FLOW, SettingModel.DataBean.BookingFlowBean::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment

        val binding = DataBindingUtil.inflate<FragmentResturantHomeBinding>(inflater, R.layout.fragment_resturant_home, container, false)
        binding.color = Configurations.colors
        binding.drawables = Configurations.drawables
        binding.strings = textConfig




        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefHelper.removeValue(PrefenceConstants.FIRST_HOME_SECTION)
        viewModel.navigator = this
        viewModel.apiGetLoyaltyPoints()
        if(prefHelper.getKeyValue(AppConstants.ROAD_MAP_TYPE,PrefenceConstants.TYPE_STRING).toString().equals(""))
        {
//            var intent=Intent(requireActivity(),RoadMapActivity::class.java)
//            startActivity(intent)
            activity?.finish()
        }
       else  if (prefHelper.getKeyValue(AppConstants.ROAD_MAP_TYPE, PrefenceConstants.TYPE_STRING) == AppConstants.ROAD_MAP_TYPE_CRAVE_MANIA)
            {

                navController(this@ResturantHomeFrag).navigate(R.id.action_resturantHomeFrag_to_crave_mania_new)
            }
            else{


            val mlistFrag: List<Fragment>?
            when {
                clientInform?.dynamic_order_type_client_wise == "1" -> {
                    val fragList: MutableList<Fragment>? = mutableListOf()
                    val tabTitle: MutableList<String>? = mutableListOf()

                    if (clientInform?.dynamic_order_type_client_wise_delivery == "1") {
                        fragList?.add(HomeFragment.newInstance())
                        tabTitle?.add(textConfig?.delivery_tab.toString())
                    }
                    if (clientInform?.dynamic_order_type_client_wise_pickup == "1") {
                        fragList?.add(PickupResturantFrag.newInstance())
                        tabTitle?.add(textConfig?.pickup_tab.toString())
                    }
                    if (clientInform?.is_table_booking == "1") {
                        fragList?.add(DineInResturantFrag.newInstance())
                        tabTitle?.add(getString(R.string.dine_in_tag))
                    }

                    mlistFrag = fragList

                    if ((mlistFrag?.size ?: 0) > 1) {
                        tabNames = tabTitle?.toTypedArray() ?: emptyArray()
                        if (!tabNames.contains(textConfig?.delivery_tab))
                            tl_home?.removeTabAt(0)
                        if (!tabNames.contains(textConfig?.pickup_tab))
                            tl_home?.removeTabAt(1)
                        if (!tabNames.contains(getString(R.string.dine_in_tag)))
                            tl_home?.removeTabAt(2)

                        tl_home.visibility = View.VISIBLE
                        tl_home.addOnTabSelectedListener(this)
                    } else
                        tl_home.visibility = View.GONE

                    if (clientInform?.is_order_types_screen_dynamic == "1" && !tabNames.isNullOrEmpty() && !fragList.isNullOrEmpty()) {
                        val tempTabList = ArrayList<String>()
                        tempTabList.addAll(tabNames)
                        val sectionsList = dataManager.getGsonValue(DataNames.ORDER_TYPE_DYNAMIC_SECTIONS, SettingModel.DataBean.DynamicSectionsData::class.java)

                        sectionsList?.let { sectionData ->
                            sectionData.list?.sort()

                            val list: ArrayList<SettingModel.DataBean.DynamicScreenSections> = if (tabNames.size != sectionData.list?.size) {
                                sectionData.list?.subList(0, tabNames.size)?.toArrayList()
                                        ?: arrayListOf()
                            } else
                                sectionData.list ?: arrayListOf()

                            list.forEachIndexed { index, section ->
                                var fragment: Fragment? = null
                                when {
                                    section.code == "delivery" && tabNames.contains(textConfig?.delivery_tab) -> {
                                        fragment = if (section.section_place == 1) HomeFragment.newInstance() else DineInResturantFrag.newInstance(true)
                                    }

                                    section.code == "pickup" && tabNames.contains(textConfig?.pickup_tab) -> {
                                        fragment = PickupResturantFrag.newInstance()
                                    }

                                    section.code == "dinein" && tabNames.contains(getString(R.string.dine_in_tag)) -> {
                                        fragment = DineInResturantFrag.newInstance()
                                    }

                                }

                                tl_home?.getTabAt(index)?.setIcon(StaticFunction.getTabIcon(section.code
                                        ?: ""))

                                tempTabList[(section.section_place ?: 1) - 1] = section.section_name
                                        ?: ""

                                fragList[(section.section_place ?: 1) - 1] = fragment
                                        ?: HomeFragment.newInstance()

                            }
                            tabNames = tempTabList.toTypedArray()
                            if (!fragList.isNullOrEmpty()) {
                                fragList[0] = HomeFragment.newInstance()
                                prefHelper.addGsonValue(PrefenceConstants.FIRST_HOME_SECTION, Gson().toJson(list.firstOrNull()))
                            }
                        }
                    }

                }


                clientInform?.is_skip_theme == "1" -> {
                    tl_home.visibility = View.GONE
                    iv_search?.visibility = View.GONE
                    iv_notification?.visibility = View.GONE
                    iv_logo?.visibility = View.VISIBLE
                    iv_logo.loadImage(clientInform?.logo_url ?: "")
                    mlistFrag = listOf(HomeFragment.newInstance())
                    ivCart?.visibility = View.VISIBLE
                }
                bookingFlowBean?.is_pickup_order == FoodAppType.Both.foodType -> {
                    tl_home.visibility = View.VISIBLE
                    settingTabLayout()
                    tl_home.addOnTabSelectedListener(this)
                    mlistFrag = if (clientInform?.is_table_booking == "1") {
                        listOf(HomeFragment.newInstance(), PickupResturantFrag.newInstance(), DineInResturantFrag.newInstance())
                    } else {
                        tl_home?.removeTabAt(2)
                        listOf(HomeFragment.newInstance(), PickupResturantFrag.newInstance())
                    }
                }
                else -> {
                    tl_home.visibility = View.GONE
                    mlistFrag = listOf(PickupResturantFrag.newInstance())
                }
            }

            location_txt?.text = textConfig?.location_text

            if (clientInform?.is_order_types_screen_dynamic == "1" && tabNames.size == tl_home?.tabCount) {
                tabNames.forEachIndexed { index, title ->
                    tl_home.getTabAt(index)?.text = title
                }
            } else {

                if (tabNames.isNotEmpty()) {
                    if (tabNames.contains(textConfig?.delivery_tab))
                        tl_home.getTabAt(0)?.text = textConfig?.delivery_tab

                    val pickupIndex = tabNames.indexOfFirst { it == textConfig?.pickup_tab }
                    if (pickupIndex != -1)
                        tl_home.getTabAt(pickupIndex)?.text = textConfig?.pickup_tab
                }
            }




            mAdapter = PagerAdapter(this@ResturantHomeFrag, mlistFrag ?: emptyList())
            //viewPager.setPagingEnabled(false)
            viewPager.offscreenPageLimit = 2
            viewPager.adapter = mAdapter

            viewPager.isUserInputEnabled = false

            viewPager.isSaveFromParentEnabled = true

            tvTitle?.visibility = if (clientInform?.is_skip_theme == "1") View.VISIBLE else View.GONE

            if (bookingFlowBean?.is_pickup_order == FoodAppType.Both.foodType && !tabNames.isNullOrEmpty() && clientInform?.dynamic_order_type_client_wise != "1") {

                when (AppConstants.DELIVERY_OPTIONS) {
                    DeliveryType.PickupOrder.type -> {
                        tabSetting(tabNames[1])
                    }
                    DeliveryType.DeliveryOrder.type -> {
                        tabSetting(tabNames[0])
                    }
                    DeliveryType.DineIn.type -> {
                        tabSetting(tabNames[2])
                    }
                }
            }
            if (AppConstants.APP_SUB_TYPE > AppDataType.Custom.type || clientInform?.show_food_groc == "1") {
                iv_supplier_logo.setImageResource(R.drawable.ic_back_home)


            } else {
                if (clientInform?.app_selected_template == null || clientInform?.app_selected_template == "0" && clientInform?.is_skip_theme != "1") {
                    iv_supplier_logo.visibility = View.VISIBLE
                    if (BuildConfig.CLIENT_CODE == "foodydoo_0590") {
                        iv_supplier_logo.setImageDrawable(ContextCompat.getDrawable(activity
                                ?: requireContext(), R.drawable.fast_food))
                    } else {
                        iv_supplier_logo.loadImage(clientInform?.logo_url ?: "")
                    }
                } else {
                    iv_supplier_logo.visibility = View.GONE
                }
            }

            ivCart?.setOnClickListener {
                navController(this@ResturantHomeFrag).navigate(R.id.action_resturantHomeFrag_to_cart)
            }

            iv_rewards.setOnClickListener {

                if (prefHelper.getCurrentUserLoggedIn()) {
                    navController(this@ResturantHomeFrag).navigate(R.id.action_resturantHomeFrag_to_recordinFragment)
                } else {
                    appUtils.checkLoginFlow(requireActivity(), REFERAL_REQUEST)
                }

//                if (prefHelper.getCurrentUserLoggedIn()) {
//                    navController(this@ResturantHomeFrag).navigate(R.id.action_resturantHomeFrag_to_manageReferralFrag)
//                } else {
//                    appUtils.checkLoginFlow(requireActivity(), REFERAL_REQUEST)
//                }
            }

            iv_rewards2.setOnClickListener {
                if (prefHelper.getCurrentUserLoggedIn()) {
                    navController(this@ResturantHomeFrag).navigate(R.id.action_resturantHomeFrag_to_recordinFragment)
                } else {
                    appUtils.checkLoginFlow(requireActivity(), REFERAL_REQUEST)
                }
//                if (prefHelper.getCurrentUserLoggedIn()) {
//                    navController(this@ResturantHomeFrag).navigate(R.id.action_resturantHomeFrag_to_manageReferralFrag)
//                } else {
//                    appUtils.checkLoginFlow(requireActivity(), REFERAL_REQUEST)
//                }
            }

            iv_logo?.setOnClickListener {
                navController(this@ResturantHomeFrag).navigate(R.id.action_resturantHomeFrag_to_skipOther)
            }

            if (clientInform?.referral_feature == "1" && clientInform?.yummyTheme == "1") {
                iv_rewards.visibility = View.VISIBLE
            } else {
                iv_rewards.visibility = View.GONE
            }

            /*    TabLayoutMediator(tl_home, viewPager) { tab, position ->
                tab.text = "OBJECT ${(position + 1)}"
            }.attach()*/

            updateLyt()

            if (currentPos != null) {
                tabSetting(tabNames[currentPos ?: 0])
            } else
                currentPos = tl_home?.selectedTabPosition



            if (clientInform?.is_carveQatar_home_theme == "1") {
                setupUiForCrave()
            }

            if (prefHelper.getKeyValue(AppConstants.ROAD_MAP_TYPE, PrefenceConstants.TYPE_STRING) == AppConstants.ROAD_MAP_TYPE_SIGNATURE) {
                vpHome.displayedChild = 1
            } else
                vpHome.displayedChild = 0
        }
    }

    fun setupUiForCrave() {
        layoutMainToolbar.visibility = View.GONE
        layoutCrave.visibility = View.VISIBLE

        iv_supplier_logo2.setImageResource(R.drawable.fast_food)

        iv_supplier_logo2.setOnClickListener {
          var intent=Intent(requireActivity(),RoadMapActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
        icAddress2.setImageResource(R.drawable.ic_add)
        tl_home.visibility = View.GONE
    }
    override fun onVehicleSelect(id:String,model: String, color: String, plate_no: String) {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()

        AppConstants.DELIVERY_OPTIONS = DeliveryType.DeliveryOrder.type
        EventBus.getDefault().unregister(this)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun updateLyt() {


        prefHelper.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)?.let {
            tvArea.text = appUtils.getAddressFormat(it)
            tvArea2.text = appUtils.getAddressFormat(it)

            Log.e("update_here","ghfdhgfhgfg")
        }

        val isUserLoggedIn = prefHelper.getCurrentUserLoggedIn()
        tvArea.setOnClickListener {
            if (clientInform?.show_ecom_v2_theme == "1") {
                AddressDialogFragmentV2.newInstance(0).show(childFragmentManager, "dialog")
            } else {
                AddressDialogFragment.newInstance(0,0).show(childFragmentManager, "dialog")
            }

        }

        tvArea2.setOnClickListener {
            if (clientInform?.show_ecom_v2_theme == "1") {
                AddressDialogFragmentV2.newInstance(0).show(childFragmentManager, "dialog")
            } else {
                AddressDialogFragment.newInstance(0,0).show(childFragmentManager, "dialog")
            }

        }

        layChefBoutique.setOnClickListener {
            val bundle = bundleOf("type" to "2")
            navController(this@ResturantHomeFrag).navigate(R.id.action_resturantHomeFrag_to_signatureHome,bundle)
        }

        laySignaturePlates.setOnClickListener {
            val bundle = bundleOf("type" to "3")
            navController(this@ResturantHomeFrag).navigate(R.id.action_resturantHomeFrag_to_signatureHome,bundle)
        }

        laySignatureCatering.setOnClickListener {
            val bundle = bundleOf("type" to "")
            navController(this@ResturantHomeFrag).navigate(R.id.action_resturantHomeFrag_to_signatureHome,bundle)
        }

        iv_search.setOnClickListener {
            if (clientInform?.is_unify_search == "1")
                navController(this@ResturantHomeFrag).navigate(R.id.action_resturantHomeFrag_to_unify_SearchFragment)
            else
                navController(this@ResturantHomeFrag).navigate(R.id.action_resturantHomeFrag_to_searchFragment)
        }
        if (prefHelper.getKeyValue(AppConstants.ROAD_MAP_TYPE, PrefenceConstants.TYPE_STRING)!!.equals("Delivery")) {
            tvType.setText(getString(R.string.delivery_txt))
        } else {
            tvType.text=getString(R.string.grab_n_go)
        }
        iv_search2.setOnClickListener {
            if (clientInform?.is_unify_search == "1")
                navController(this@ResturantHomeFrag).navigate(R.id.action_resturantHomeFrag_to_unify_SearchFragment)
            else
                navController(this@ResturantHomeFrag).navigate(R.id.action_resturantHomeFrag_to_searchFragment)
        }

        iv_notification.setOnClickListener {
            if (isUserLoggedIn)
                navController(this@ResturantHomeFrag).navigate(R.id.action_home_to_notificationFrag)
            else
                AppToasty.error(requireContext(), getString(R.string.login_first))
        }

        iv_notification.setBadgeValue(prefHelper.getKeyValue(PrefenceConstants.BADGE_COUNT, PrefenceConstants.TYPE_STRING).toString().toIntOrNull()
                ?: 0)
                .setBadgeOvalAfterFirst(true)
                .setBadgeTextSize(16f)
                .setMaxBadgeValue(999)
                .setBadgeBackground(ContextCompat.getDrawable(activity
                        ?: requireContext(), R.drawable.rectangle_rounded))
                .setBadgePosition(BadgePosition.TOP_RIGHT)
                .setBadgeTextStyle(Typeface.NORMAL)
                .setShowCounter(true)
                .setBadgePadding(4)
    }


    private fun settingTabLayout() {
        tabNames =
                if (clientInform?.is_table_booking == "1") {
                    arrayOf(textConfig?.delivery_tab.toString(), textConfig?.pickup_tab.toString(), getString(R.string.dine_in_tag))
                } else {
                    arrayOf(textConfig?.delivery_tab.toString(), textConfig?.pickup_tab.toString())
                }
    }


    override fun onTabSelected(tab: TabLayout.Tab) {
        currentPos = tab.position
        tabSetting(tabNames[tab.position])
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {
        //   tabSetting(tabNames[tab.position])
        // Log.e("onTabUnselected",tabNames[tab.position])
    }

    override fun onTabReselected(tab: TabLayout.Tab) {
        //  Log.e("onTabReselected",tabNames[tab.position])
    }

    override fun onAddressSelect(adrsBean: AddressBean) {

        adrsBean.let {
            appUtils.setUserLocale(it)
            prefHelper.addGsonValue(PrefenceConstants.ADRS_DATA, Gson().toJson(it))
            tvArea.text = appUtils.getAddressFormat(it)
            tvArea2.text = appUtils.getAddressFormat(it)
        }


        val fragments = mAdapter?.getItem(viewPager.currentItem)
        if (fragments is HomeFragment) {
            if (fragments.clearViewModelApi()) {
                viewPager.adapter = mAdapter
            }
        }


        //viewPager.adapter = null
        //
    }

    override fun onDestroyDialog() {

    }


    private fun tabSetting(tabName: String) {
        val pos = tabNames.indexOfFirst { it == tabName }
        if (pos == -1) return

        val frag = mAdapter?.getItem(pos)
        when (tabName) {
            getString(R.string.delivery_txt) -> {
                AppConstants.DELIVERY_OPTIONS = DeliveryType.DeliveryOrder.type
                viewPager.currentItem = pos
                tl_home.getTabAt(pos)?.select()

                if (frag is DineInResturantFrag)
                    frag.changeToDelivery(true)

                mAdapter?.notifyItemChanged(pos)
            }
            getString(R.string.dine_in_tag) -> {
                AppConstants.DELIVERY_OPTIONS = DeliveryType.DineIn.type
                viewPager.currentItem = pos
                tl_home.getTabAt(pos)?.select()
                if (frag is DineInResturantFrag)
                    frag.changeToDelivery(false)
                mAdapter?.notifyItemChanged(pos)
            }
            else -> {
                AppConstants.DELIVERY_OPTIONS = DeliveryType.PickupOrder.type
                viewPager.currentItem = pos
                tl_home.getTabAt(pos)?.select()
                mAdapter?.notifyItemChanged(pos)
            }
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onResultReceived(result: ListItem?) {
        Log.e("event", "....")
//        val currentPos = viewPager?.currentItem
//        Log.e("currentpos",currentPos.toString())
//        if (currentPos != null && currentPos != -1) {
//            val fragments = mAdapter?.getItem(currentPos)
//            if (fragments is HomeFragment)
//                fragments.onResultReceived(result)
//            else if(fragments is DineInResturantFrag)
//                fragments.onResultReceived(result)
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REFERAL_REQUEST && resultCode == Activity.RESULT_OK) {
            iv_rewards.performClick()
//            iv_rewards2.performClick()
        } else if ((requestCode == SCHEDULE_REQUEST || requestCode == AppConstants.REQUEST_PAYMENT_OPTION) && resultCode == Activity.RESULT_OK) {
            if (currentPos != null && currentPos != -1) {
                val fragments = mAdapter?.getItem(currentPos ?: 0)
                if (fragments is HomeFragment)
                    fragments.onActivityResult(requestCode, resultCode, data)
                else if (fragments is DineInResturantFrag)
                    fragments.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun loyaltyPointsSuccess(data: LoyaltyData?) {
        tvRewardText?.text = decimalFormat.format(data?.totalEarningPoint)
    }
    override fun loyaltyPointsSuccessNew(data: List<DataLoyality?>) {

    }
    override fun onErrorOccur(message: String) {
    }

    override fun onSessionExpire() {
    }


}// Required empty public constructor

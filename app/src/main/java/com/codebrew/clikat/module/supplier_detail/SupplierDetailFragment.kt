package com.codebrew.clikat.module.supplier_detail


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.adapters.PagerImageAdapter
import com.codebrew.clikat.app_utils.AppToasty
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.FoodAppType
import com.codebrew.clikat.data.ReceiverType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.orderDetail.Agent
import com.codebrew.clikat.data.model.api.supplier_detail.DataSupplierDetail
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.SupplierDetailBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.LocationUser
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.other.SubCatData
import com.codebrew.clikat.modal.other.SupplierServiceModel
import com.codebrew.clikat.module.login.LoginActivity
import com.codebrew.clikat.module.supplier_detail.adapter.SupplierDetailAdapter
import com.codebrew.clikat.module.supplier_detail.adapter.SupplierServiceAdapter
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.user_chat.UserChatActivity
import com.codebrew.clikat.utils.DialogIntrface
import com.codebrew.clikat.utils.GeneralFunctions
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.gson.Gson
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import kotlinx.android.synthetic.main.fragment_restaurant_detail_new.*
import kotlinx.android.synthetic.main.supplier_detail.*
import kotlinx.android.synthetic.main.supplier_detail.ivChat
import kotlinx.android.synthetic.main.supplier_detail.ivFacebook
import kotlinx.android.synthetic.main.supplier_detail.ivInstagram
import kotlinx.android.synthetic.main.supplier_detail.ivSupplierBanner
import kotlinx.android.synthetic.main.supplier_detail.ivSupplierIcon
import kotlinx.android.synthetic.main.supplier_detail.llContainer
import java.util.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class SupplierDetailFragment : BaseFragment<SupplierDetailBinding, SupplierDetailViewModel>(), SupplierDetailNavigator, View.OnClickListener, DialogIntrface {


    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var prefHelper: PreferenceHelper


    @Inject
    lateinit var appUtils: AppUtils


    @Inject
    lateinit var dataManager: DataManager


    private var mSupplierViewModel: SupplierDetailViewModel? = null


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.supplier_detail
    }

    override fun getViewModel(): SupplierDetailViewModel {
        mSupplierViewModel = ViewModelProviders.of(this, factory).get(SupplierDetailViewModel::class.java)
        return mSupplierViewModel as SupplierDetailViewModel
    }


    private val tabTitles = arrayOfNulls<String>(3)


    private var supplierId: Int = 0
    private var branchId: Int = 0
    private var categoryId: Int = 0
    private var mDeliveryType: Int = 0


    private var title = ""

    private var serviceAdapter: SupplierServiceAdapter? = null

    private var serviceList: MutableList<SupplierServiceModel>? = null

    private val colorConfig = Configurations.colors

    private val textConfig = Configurations.strings

    private var supplerDetail: DataSupplierDetail? = null


    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null

    var settingBean: SettingModel.DataBean.SettingData? = null


    private var fbLink: String? = null
    private var instaLink: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this

        settingBean = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewDataBinding.color = Configurations.colors
        viewDataBinding.strings = Configurations.strings

        tabTitles[0] = getString(R.string.desc, textConfig.supplier)
        tabTitles[1] = getString(R.string.about)
        tabTitles[2] = getString(R.string.review)

        Initialization()

        val bundle = arguments

        if (bundle != null) {
            categoryId = bundle.getInt("categoryId", 0)
            branchId = bundle.getInt("branchId", 0)
            supplierId = bundle.getInt("supplierId", 0)

            mDeliveryType = when (bundle.getString("deliveryType")) {
                "dineIn" -> FoodAppType.DineIn.foodType
                "pickup" -> FoodAppType.Pickup.foodType
                "both" -> FoodAppType.Both.foodType
                else -> FoodAppType.Delivery.foodType
            }
        }

        btnMakeOrder.setOnClickListener(this)

        supplierDetailObserver()

        subCatObserver()

        fetchSupplierInf(supplierId.toString(), branchId.toString())

        settingToolbar(view)

    }

    private fun settingToolbar(view: View) {

        tb_back.setOnClickListener {
            Navigation.findNavController(view).popBackStack()
        }


    }

    private fun fetchSupplierInf(supplierId: String, branchId: String) {
        if (isNetworkConnected) {
            if (viewModel.supllierLiveData.value == null) {
                viewModel.fetchSupplierInf(supplierId, branchId, categoryId.toString())
            } else {
                setdata(viewModel.supllierLiveData.value)
                btnMakeOrder?.visibility = View.VISIBLE
            }


        }
    }

    private fun supplierDetailObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<DataSupplierDetail> { resource ->
            // Update the UI, in this case, a TextView.

            supplerDetail = resource

            setdata(resource)

            btnMakeOrder?.visibility = View.VISIBLE
            // showDetailButton()

        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.supllierLiveData.observe(viewLifecycleOwner, catObserver)

    }

    private fun subCatObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<SubCatData> { resource ->

            hideLoading()

            val bundle = Bundle()
            bundle.putInt("categoryId", categoryId)
            bundle.putString("title", supplerDetail?.name)
            bundle.putInt("supplierId", supplierId)
            bundle.putInt("deliveryType", mDeliveryType)
            if (resource?.sub_category_data?.isEmpty() == true) {

                navController(this@SupplierDetailFragment)
                        .navigate(R.id.action_supplierDetail_to_productTabListing, bundle)
            } else {

                if (settingBean?.is_expactor != "1") {
                    bundle.putBoolean("is_supplier", true)
                }

                bundle.putBoolean("has_subcat", true)
                navController(this@SupplierDetailFragment)
                        .navigate(R.id.actionSubCatList, bundle)
            }
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.subCatLiveData.observe(this, catObserver)
    }

    private fun showDetailButton() {
        btnMakeOrder.animate()
                .translationY(0f)
                .alpha(0.9f)
                .setDuration(1000)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        btnMakeOrder?.visibility = View.VISIBLE
                    }
                })
    }


    private fun setdata(data: DataSupplierDetail?) {

        tvTitleMain.text = data?.name ?: ""

        tb_favourite?.visibility = if (settingBean?.is_supplier_wishlist == "1") View.VISIBLE else View.GONE

        tb_favourite.setOnCheckedChangeListener(null)

        tb_favourite.isChecked = data?.favourite == 1

        tb_favourite.setOnCheckedChangeListener { checkBox, isChecked ->
            if (dataManager.getCurrentUserLoggedIn()) {
                if (isNetworkConnected) {
                    if (isChecked) {
                        viewModel.markFavouriteSupplier(supplierId.toString())
                    } else {
                        viewModel.unFavouriteSupplier(supplierId.toString())
                    }
                }
            } else {
                appUtils.checkLoginFlow(requireActivity(), -1)
            }
        }

        if (mDeliveryType == FoodAppType.Pickup.foodType || mDeliveryType == FoodAppType.DineIn.foodType) {
            val mRestUser = LocationUser()
            mRestUser.address = "${data?.name ?: ""} , ${data?.address}"
            dataManager.addGsonValue(PrefenceConstants.RESTAURANT_INF, Gson().toJson(mRestUser))
        }

        fbLink = data?.facebook_link
        instaLink = data?.linkedin_link

        screenFlowBean = prefHelper.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)

        btnMakeOrder.text = if (screenFlowBean?.app_type == AppDataType.Food.type) getString(R.string.view_menu) else getString(R.string.view_category)

        tvLocation.text = data?.address ?: ""

        val paymentType = when {
            data?.paymentMethod == DataNames.DELIVERY_CARD -> getString(R.string.payment_card)
            data?.paymentMethod ?: 0 == DataNames.DELIVERY_CASH -> textConfig.cod
            else -> getString(R.string.payment_both)
        }

        // 0 : cash on delivery , 1: card , 2: both

        val businessType: String

        /* businessType = try {
             GeneralFunctions.getYear(data.businessStartDate)
         } catch (e: ParseException) {
             e.printStackTrace()
             ""
         }*/

        serviceList?.clear()
        //setting service data
        serviceList?.add(SupplierServiceModel(R.drawable.ic_min_delivery_time, getString(R.string.min_order_time), GeneralFunctions.getFormattedTime(data?.deliveryMinTime
                ?: 0, activity)))
        serviceList?.add(SupplierServiceModel(R.drawable.ic_min_order, getString(R.string.minimum_order, textConfig.order), AppConstants.CURRENCY_SYMBOL + " " + data?.minOrder))
        //  serviceList?.add(SupplierServiceModel(R.drawable.ic_delivery_charges, getString(R.string.delivery_charges), StaticFunction.getCurrency(activity) + " " + data?.deliveryCharges + ""))
        serviceList?.add(SupplierServiceModel(R.drawable.ic_payment_option, getString(R.string.txtPaymentOptions), paymentType))
        serviceList?.add(SupplierServiceModel(R.drawable.ic_orders_so_far, getString(R.string.txtOrderDone, textConfig.order), data?.totalOrder.toString()))
        // serviceList?.add(SupplierServiceModel(R.drawable.ic_in_bussiness, getString(R.string.txtInbusiness), data?.businessStartDate?:""))

        serviceAdapter?.notifyDataSetChanged()


        tvName.text = data?.name
        resturant_delivery_desc.text = getString(R.string.restaurant_order_type, data?.name, textConfig.supplier)

        //tvOrderDoneValue.setText("" + data.getTotalOrder());

        tv_total_rating.text = (data?.rating ?: 0).toString()
        tvReviewCount.text = (data?.totalReviews
                ?: 0).toString() + " " + resources.getString(R.string.reviews)
        if (data?.open_time == null) {
            tvOpenTime.visibility = View.GONE
        } else {
            tvOpenTime.text = data.open_time + " - " + data.closeTime
            tvOpenTime.visibility = View.VISIBLE
        }

        llContainer.visibility = View.VISIBLE
        // 1: available, 2:busy , 0:closed
        if (data?.status?.toInt() == DataNames.AVAILABLE) {
            tvClosed.text = getString(R.string.open)
            tvClosed.setTextColor(Color.parseColor(colorConfig.open_now))
        } else if (data?.status?.toInt() == DataNames.BUSY) {
            tvClosed.text = getString(R.string.busy)
        } else {
            tvClosed.text = getString(R.string.close)
            tvClosed.setTextColor(colorConfig.rejectColor)
        }


        Glide.with(requireActivity())
                .load(data?.logo)
                .into(ivSupplierIcon)


/*        Prefs.with(activity).save(DataNames.SUPPLIER_LOGO, data?.ic_splash)
        Prefs.with(activity).save(DataNames.SUPPLIER_LOGO_ID, "" + supplierId)
        Prefs.with(activity).save(DataNames.SUPPLIER_LOGO_BRANCH_ID, "" + branchId)
        Prefs.with(activity).save(DataNames.SUPPLIER_LOGO_NAME, "" + title)*/

        /*     val effect = JazzyViewPager.TransitionEffect.valueOf(getString(R.string.tablet))
             ivSupplierBanner.setTransitionEffect(effect)*/


        val adapter = PagerImageAdapter(activity, data?.supplier_image
                ?: emptyList(), ivSupplierBanner)

        ivSupplierBanner.adapter = adapter

        cpiIndicator.setViewPager(ivSupplierBanner)

        // Attach the view pagerBanner to the tab strip


        val tabTitles = arrayOfNulls<String>(1)

        // tabTitles[0] = getString(R.string.desc, Configurations.strings.supplier)
        // tabTitles[1] = getString(R.string.about)
        tabTitles[0] = getString(R.string.review)


        val mAdapter = SupplierDetailAdapter(childFragmentManager, supplerDetail, tabTitles)

        viewPagerProductListing.adapter = mAdapter

        pager_tab_strip.setupWithViewPager(viewPagerProductListing)

        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)
    }


    private fun Initialization() {

        if (settingBean?.show_social_links != "1") {
            ivFacebook?.visibility = View.GONE
            ivInstagram?.visibility = View.GONE
        }

        if (settingBean?.supplier_to_user_chat == "1")
            ivChat?.visibility = View.VISIBLE
        else
            ivChat?.visibility = View.GONE


        ivFacebook?.setOnClickListener {
            if (!fbLink.isNullOrEmpty()) {
                StaticFunction.openCustomChrome(requireActivity(), fbLink ?: "")
            }
        }
        ivInstagram?.setOnClickListener {
            if (!instaLink.isNullOrEmpty()) {
                StaticFunction.openCustomChrome(requireActivity(), instaLink ?: "")
            }
        }

        ivChat?.setOnClickListener {
            if (dataManager.getCurrentUserLoggedIn()) {
                val data = Agent()
                data.image = supplerDetail?.supplier_image?.firstOrNull()
                data.name = supplerDetail?.name
                data.message_id = supplerDetail?.message_id
                data.user_created_id = supplerDetail?.user_created_id
                data.agent_created_id = supplerDetail?.id.toString()
                activity?.launchActivity<UserChatActivity> {
                    putExtra("userType", ReceiverType.SUPPLIER.type)
                    putExtra("userData", data)
                }
            } else {
                activity?.launchActivity<LoginActivity>()
            }
        }


        serviceList = ArrayList()

        serviceAdapter = SupplierServiceAdapter(serviceList)
        providerServices.layoutManager = GridLayoutManager(activity, 2)

        val animationAdapter = AlphaInAnimationAdapter(serviceAdapter)
        animationAdapter.setDuration(1000)

        providerServices.adapter = animationAdapter


    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnMakeOrder -> {
                when (supplerDetail?.status) {
                    DataNames.AVAILABLE -> makeOrder()
                    DataNames.BUSY -> StaticFunction.dialogue(activity, supplerDetail?.name + " " + getString(R.string.supplierBusy1), getString(R.string.Alert), true, this)
                    else -> StaticFunction.dialogue(activity, supplerDetail?.name + " " + getString(R.string.supplierClosed), getString(R.string.Alert), true, this)
                }
            }
        }
    }


    fun makeOrder() {

        if (isNetworkConnected) {
            val hashMap = dataManager.updateUserInf()
            if (supplierId != 0) {
                hashMap["supplier_id"] = "" + supplierId
            }
            hashMap["category_id"] = categoryId.toString()
            if (isNetworkConnected) {
                showLoading()
                viewModel.getSubCategory(hashMap)
            }
        }
    }


    override fun onErrorOccur(message: String) {

        cnst_supplier_lyt.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onSuccessListener() {
        makeOrder()
    }

    override fun onErrorListener() {

    }

    override fun onFavouriteStatus(message: String) {
        AppToasty.success(activity ?: requireContext(), message)
    }
}

package com.codebrew.clikat.module.supplier_detail.v2


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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.adapters.PagerImageAdapter
import com.codebrew.clikat.app_utils.AppToasty
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.FoodAppType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.supplier_detail.DataSupplierDetail
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.SupplierDetailV2Binding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.LocationUser
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.other.SupplierServiceModel
import com.codebrew.clikat.module.supplier_detail.SupplierDetailNavigator
import com.codebrew.clikat.module.supplier_detail.SupplierDetailViewModel
import com.codebrew.clikat.module.supplier_detail.adapter.SupplierServiceAdapter
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.DialogIntrface
import com.codebrew.clikat.utils.GeneralFunctions
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.gson.Gson
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import kotlinx.android.synthetic.main.supplier_detail.*
import kotlinx.android.synthetic.main.supplier_detail_v2.*
import kotlinx.android.synthetic.main.supplier_detail_v2.btnMakeOrder
import kotlinx.android.synthetic.main.supplier_detail_v2.cnst_supplier_lyt
import kotlinx.android.synthetic.main.supplier_detail_v2.cpiIndicator
import kotlinx.android.synthetic.main.supplier_detail_v2.ivSupplierBanner
import kotlinx.android.synthetic.main.supplier_detail_v2.llContainer
import kotlinx.android.synthetic.main.supplier_detail_v2.pager_tab_strip
import kotlinx.android.synthetic.main.supplier_detail_v2.providerServices
import kotlinx.android.synthetic.main.supplier_detail_v2.resturant_delivery_desc
import kotlinx.android.synthetic.main.supplier_detail_v2.tb_back
import kotlinx.android.synthetic.main.supplier_detail_v2.tb_favourite
import kotlinx.android.synthetic.main.supplier_detail_v2.tvClosed
import kotlinx.android.synthetic.main.supplier_detail_v2.tvLocation
import kotlinx.android.synthetic.main.supplier_detail_v2.tvName
import kotlinx.android.synthetic.main.supplier_detail_v2.tvOpenTime
import kotlinx.android.synthetic.main.supplier_detail_v2.tvReviewCount
import kotlinx.android.synthetic.main.supplier_detail_v2.tvTitleMain
import kotlinx.android.synthetic.main.supplier_detail_v2.viewPagerProductListing
import java.util.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class SupplierDetailFragmentV2 : BaseFragment<SupplierDetailV2Binding, SupplierDetailViewModel>()
        , SupplierDetailNavigator, View.OnClickListener, DialogIntrface {


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
        return R.layout.supplier_detail_v2
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

    private var categoryFlow = ArrayList<String>()

    private var supplerDetail: DataSupplierDetail? = null


    private lateinit var screenFlowBean: SettingModel.DataBean.ScreenFlowBean


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this
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
        }

        btnMakeOrder.setOnClickListener(this)

        supplierDetailObserver()

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
                btnMakeOrder!!.visibility = View.VISIBLE
            }


        }
    }

    private fun supplierDetailObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<DataSupplierDetail> { resource ->
            // Update the UI, in this case, a TextView.

            supplerDetail = resource

            setdata(resource)

            btnMakeOrder!!.visibility = View.VISIBLE
            // showDetailButton()

        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.supllierLiveData.observe(requireActivity(), catObserver)

    }

    private fun showDetailButton() {
        btnMakeOrder.animate()
                .translationY(0f)
                .alpha(0.9f)
                .setDuration(1000)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        btnMakeOrder!!.visibility = View.VISIBLE
                    }
                })
    }


    private fun setdata(data: DataSupplierDetail?) {

        tvTitleMain.visibility = View.GONE

        tb_favourite.setOnCheckedChangeListener(null)

        tb_favourite.isChecked=data?.favourite==1

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
                appUtils.checkLoginFlow(requireActivity(),-1)
            }
        }

        if (mDeliveryType == FoodAppType.Pickup.foodType || mDeliveryType == FoodAppType.DineIn.foodType) {
            val mRestUser = LocationUser()
            mRestUser.address = "${data?.name?: ""} , ${data?.address}"
            dataManager.addGsonValue(PrefenceConstants.RESTAURANT_INF, Gson().toJson(mRestUser))
        }

        screenFlowBean = prefHelper.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)!!

        btnMakeOrder.text = if (screenFlowBean.app_type == AppDataType.Food.type) getString(R.string.view_menu) else getString(R.string.view_category)

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


        //setting service data
        serviceList?.add(SupplierServiceModel(R.drawable.ic_min_delivery_time, getString(R.string.min_order_time), GeneralFunctions.getFormattedTime(data?.deliveryMaxTime!!, activity)))
        serviceList?.add(SupplierServiceModel(R.drawable.ic_min_order, getString(R.string.minimum_order, textConfig.order), AppConstants.CURRENCY_SYMBOL + " " + data?.minOrder))
        serviceList?.add(SupplierServiceModel(R.drawable.ic_delivery_charges, getString(R.string.delivery_charges), AppConstants.CURRENCY_SYMBOL + " " + data?.deliveryCharges + ""))
        serviceList?.add(SupplierServiceModel(R.drawable.ic_payment_option, getString(R.string.txtPaymentOptions), paymentType))
        serviceList?.add(SupplierServiceModel(R.drawable.ic_orders_so_far, getString(R.string.txtOrderDone, textConfig.order), data?.totalOrder.toString()))
        serviceList?.add(SupplierServiceModel(R.drawable.ic_in_bussiness, getString(R.string.txtInbusiness), ""))

        serviceAdapter?.notifyDataSetChanged()


        tvName.text = data?.name
        resturant_delivery_desc.text = getString(R.string.restaurant_order_type, data?.name, textConfig.supplier)

        //tvOrderDoneValue.setText("" + data.getTotalOrder());

        tvReviewCount.text = data?.rating.toString()

        tvReviewCount.text = data?.totalReviews.toString() + " " + resources.getString(R.string.reviews)
        if (data?.open_time == null) {
            tvOpenTime.visibility = View.GONE
        } else {
            tvOpenTime.text = data.open_time + " - " + data.closeTime
            tvOpenTime.visibility = View.VISIBLE
        }

        llContainer.visibility = View.VISIBLE
        // 1: available, 2:busy , 0:closed
        when {
            data?.status?.toInt() == DataNames.AVAILABLE -> {
                tvClosed.text = getString(R.string.open)
                tvClosed.setTextColor(Color.parseColor(colorConfig.open_now))
            }
            data?.status?.toInt() == DataNames.BUSY -> {
                tvClosed.text = getString(R.string.busy)
            }
            else -> {
                tvClosed.text = getString(R.string.close)
                tvClosed.setTextColor(colorConfig.rejectColor)
            }
        }



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


        val tabTitles = arrayOfNulls<String>(3)

        tabTitles[0] = getString(R.string.desc, Configurations.strings.supplier)
        tabTitles[1] = getString(R.string.about)
        tabTitles[2] = getString(R.string.review)


        pager_tab_strip.visibility =  View.GONE
        viewPagerProductListing.visibility =  View.GONE

        // DrawableCompat.setTint(((MainActivity)getActivity()).tbFavourite.getDrawable(), Color.parseColor(colorConfig.appBackground));

        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)
    }


    private fun Initialization() {


        serviceList = ArrayList()

        serviceAdapter = SupplierServiceAdapter(serviceList)
        providerServices.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        val animationAdapter = AlphaInAnimationAdapter(serviceAdapter)
        animationAdapter.setDuration(1000)

        providerServices.adapter = animationAdapter


    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnMakeOrder -> {
                when {
                    supplerDetail?.status!!.toInt() == DataNames.AVAILABLE -> makeOrder()
                    supplerDetail?.status!!.toInt() == DataNames.BUSY -> StaticFunction.dialogue(activity, supplerDetail?.name + " " + getString(R.string.supplierBusy1), getString(R.string.Alert), true, this)
                    else -> StaticFunction.dialogue(activity, supplerDetail?.name + " " + getString(R.string.supplierClosed), getString(R.string.Alert), true, this)
                }
            }
        }
    }


    fun makeOrder() {


        if (arguments != null && arguments?.containsKey("categoryFlow") == true) {
            categoryFlow.clear()
            categoryFlow.addAll(arguments?.getStringArrayList("categoryFlow") ?: emptyList())
            categoryFlow.removeAt(0)
        }


        when {

            categoryFlow.contains("SubCategory") -> {
                val bundle = Bundle()
                bundle.putInt("categoryId", categoryId)
                bundle.putInt("supplierId", supplierId)
                bundle.putStringArrayList("categoryFlow", categoryFlow)
                // bundle.putInt("placement", DataNames.YES);

                navController(this@SupplierDetailFragmentV2)
                        .navigate(R.id.actionSubCatList, bundle)

            }
            else -> {
                val bundle = Bundle()
                bundle.putInt("categoryId", categoryId)
                bundle.putInt("supplierId", supplierId)
                // bundle.putInt("placement", DataNames.YES);
                bundle.putStringArrayList("categoryFlow", categoryFlow)
                navController(this@SupplierDetailFragmentV2)
                        .navigate(R.id.actionSubCatList, bundle)
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

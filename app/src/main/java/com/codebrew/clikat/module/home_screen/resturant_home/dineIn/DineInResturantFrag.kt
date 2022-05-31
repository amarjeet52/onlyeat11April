package com.codebrew.clikat.module.home_screen.resturant_home.dineIn

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codebrew.clikat.BR
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.*
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.ListItem
import com.codebrew.clikat.data.model.api.supplier_detail.DataSupplierDetail
import com.codebrew.clikat.data.model.others.CustomPayModel
import com.codebrew.clikat.databinding.FragmentPickupResturantBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.*
import com.codebrew.clikat.modal.other.SettingModel.DataBean.ScreenFlowBean
import com.codebrew.clikat.modal.slots.SupplierSlots
import com.codebrew.clikat.module.cart.SCHEDULE_REQUEST
import com.codebrew.clikat.module.cart.schedule_order.ScheduleOrder
import com.codebrew.clikat.module.home_screen.HomeNavigator
import com.codebrew.clikat.module.home_screen.HomeViewModel
import com.codebrew.clikat.module.home_screen.adapter.BannerListAdapter
import com.codebrew.clikat.module.home_screen.adapter.HomeItemAdapter
import com.codebrew.clikat.module.home_screen.adapter.HomeItemAdapter.SupplierListCallback
import com.codebrew.clikat.module.home_screen.adapter.SpecialListAdapter
import com.codebrew.clikat.module.home_screen.resturant_home.WALLET_REQUEST
import com.codebrew.clikat.module.payment_gateway.PaymentListActivity
import com.codebrew.clikat.module.restaurant_detail.OnTableCapacityListener
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.DialogIntrface
import com.codebrew.clikat.utils.ProgressBarDialog
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_ecommerce.*
import kotlinx.android.synthetic.main.fragment_pickup_resturant.*
import kotlinx.android.synthetic.main.fragment_pickup_resturant.bottom_cart
import kotlinx.android.synthetic.main.fragment_pickup_resturant.swiprRefresh
import kotlinx.android.synthetic.main.layout_bottom_cart.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.reflect.Type
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class DineInResturantFrag : BaseFragment<FragmentPickupResturantBinding, HomeViewModel>(), SupplierListCallback, HomeNavigator, SwipeRefreshLayout.OnRefreshListener, DialogIntrface, OnTableCapacityListener {


    private var deliveryType: Int=FoodAppType.DineIn.foodType
    private var selectedCurrency: Currency?=null
    private var barDialog: ProgressBarDialog? = null

    @Inject
    lateinit var dataManager: AppDataManager

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var appUtils: AppUtils

    private lateinit var viewModel: HomeViewModel

    private var mBinding: FragmentPickupResturantBinding? = null

    private var homeItemAdapter: HomeItemAdapter? = null
    private var mSupplierList: MutableList<SupplierDataBean>? = null
    private var screenFlowBean: ScreenFlowBean? = null
    private var bookingFlowBean: SettingModel.DataBean.BookingFlowBean? = null
    private var settingData: SettingModel.DataBean.SettingData? = null
    private lateinit var pickupImage: Array<String?>
    private lateinit var bannerImage: IntArray
    //private var changeToDelivery:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this
        supplierObserver()
      //  tableCapacityObserver()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding

        mBinding?.color = Configurations.colors
        mBinding?.drawables = Configurations.drawables
        mBinding?.strings = appUtils.loadAppConfig(0).strings
        selectedCurrency = dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
        screenFlowBean = dataManager.getGsonValue(DataNames.SCREEN_FLOW, ScreenFlowBean::class.java)
        bookingFlowBean = dataManager.getGsonValue(DataNames.BOOKING_FLOW, SettingModel.DataBean.BookingFlowBean::class.java)
        settingData = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)

        HomeItemAdapter.SORT_POPUP = getString(R.string.sort_by_distance)

//        if(arguments!=null && arguments?.containsKey("changeToDelivery")==true )
//        {
//            changeToDelivery=arguments?.getBoolean("changeToDelivery")?:false
//            Log.e("changesss",changeToDelivery.toString())
//            Log.e("deliveryOptionssss",AppConstants.DELIVERY_OPTIONS.toString())
//        }
        values()

    }

    override fun onResume() {
        super.onResume()

        showBottomCart()
        callApi(StaticFunction.getSortByValue(HomeItemAdapter.SORT_POPUP),true)
    }

    //  homeItemAdapter.settingPickupBanner();
    private fun values() {

        mSupplierList = ArrayList()
        barDialog = ProgressBarDialog(activity)
        val mLocUser = dataManager.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)


        homeItemAdapter = HomeItemAdapter(mSupplierList, deliveryType, appUtils, settingData, mLocUser,screenFlowBean,selectedCurrency)
        rv_resturant_list.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        rv_resturant_list.adapter = homeItemAdapter
        homeItemAdapter!!.settingCallback(this)
        swiprRefresh.setOnRefreshListener(this)

        if (settingData?.enable_rest_pagination_category_wise == "1")
            onRecyclerViewScrolled()

        //  homeItemAdapter.settingPickupBanner();
    }// setViewType(HomeItemAdapter.SEARCH_TYPE, 0);

    //  rvSupplier.setVisibility(response.body().getData().size() > 0 ? View.VISIBLE : View.GONE);

    private fun setViewType(viewType: String, supplierCount: Int) {
        val itemModel = HomeItemModel()
        val dataBean = SupplierDataBean()
        dataBean.viewType = viewType
        if (viewType == HomeItemAdapter.BANNER_TYPE) {
            val bannerList: MutableList<TopBanner> = ArrayList()
            if (settingData?.pickup_url_one?.isEmpty() == false && settingData?.pickup_url_two?.isEmpty() == false
                    && settingData?.pickup_url_three?.isEmpty() == false) {
                pickupImage = arrayOf(settingData?.pickup_url_one, settingData?.pickup_url_two, settingData?.pickup_url_three)
                var bannerBean: TopBanner
                for (i1 in pickupImage) {
                    bannerBean = TopBanner()
                    bannerBean.pickupImage = i1
                    bannerBean.isEnabled = false
                    bannerList.add(bannerBean)
                }

            } else {
                bannerImage = intArrayOf(R.drawable.ic_pickup_banner_2, R.drawable.ic_pickup_banner_3, R.drawable.ic_pickup_banner_1)
                //    val bannerImage = intArrayOf(R.drawable.ic_phar_1, R.drawable.ic_phar_2, R.drawable.ic_phar_3)
                //  val bannerImage = intArrayOf(R.drawable.ic_phar_1, R.drawable.ic_groc_2, R.drawable.ic_pickup_banner_1)
                // }

                var bannerBean: TopBanner
                for (i1 in bannerImage) {
                    bannerBean = TopBanner()
                    bannerBean.bannerImage = i1
                    bannerBean.isEnabled = false
                    bannerList.add(bannerBean)
                }

            }



            if (bannerList.size > 0) {
                itemModel.bannerWidth = 1
                itemModel.bannerList = ArrayList()
                itemModel.bannerList = bannerList
            }
        } else if (viewType == HomeItemAdapter.FILTER_TYPE) {
            itemModel.supplierCount = viewModel.supplierListCount.get()

        }
        itemModel.screenType = screenFlowBean!!.app_type
        dataBean.itemModel = itemModel
        //mSupplierList?.add(dataBean)
    }

    override fun onSortByClicked(tvSortBy: TextView) {
        showPopup(tvSortBy)
    }

    override fun onViewAllCategories(list: List<English>) {
        //do nothing
    }

    private fun showPopup(view: TextView) {
        val popup = PopupMenu(requireContext(), view)
        popup.inflate(R.menu.popup_sort_by)

        popup.setOnMenuItemClickListener { item: MenuItem? ->

            when (item?.itemId) {
                R.id.sort_by_new -> {
                    mSupplierList?.clear()
                    callApi(null,true)
                }
                R.id.sort_by_rating -> {
                    mSupplierList?.clear()
                    callApi(SortBy.SortByRating.sortBy.toString(),true)
                }
                R.id.sort_by_distance -> {
                    mSupplierList?.clear()
                    callApi(SortBy.SortByDistance.sortBy.toString(),true)
                }

                R.id.sort_by_atz -> {
                    mSupplierList?.clear()
                    callApi(SortBy.SortByATZ.sortBy.toString(),true)
                }
                /*  R.id.sort_by_zta -> {
                      mSupplierList?.clear()
                      fetchSupplierList(SortBy.SortByZTA.sortBy.toString())
                  }*/
                R.id.sort_by_open -> {
                    mSupplierList?.clear()
                    callApi(SortBy.SortByOpen.sortBy.toString(),true)
                }
                R.id.sort_by_close -> {
                    mSupplierList?.clear()
                    callApi(SortBy.SortByClose.sortBy.toString(),true)

                }
            }

            HomeItemAdapter.SORT_POPUP = item?.title.toString()

            true
        }

        popup.show()
    }

    fun changeToDelivery(value:Boolean){
     //   changeToDelivery=value

    }
    private fun callApi(sortBy:String?, isFirstPage: Boolean){
        val selfPickup=if(AppConstants.DELIVERY_OPTIONS==DeliveryType.DeliveryOrder.type)  "0" else "3"
        if(isNetworkConnected)
           viewModel.getSupplierList(selfPickup, sortBy, settingData, isFirstPage)
    }

    override fun viewAllNearBy() {
        //do nothing
    }

    private fun supplierObserver() {
        // Create the observer which updates the UI.
        val supplierObserver = Observer<PagingResult<List<SupplierDataBean>>> { resource ->

            deliveryType=if(AppConstants.DELIVERY_OPTIONS==DeliveryType.DineIn.type) FoodAppType.DineIn.foodType else FoodAppType.Delivery.foodType
            homeItemAdapter?.setDeliveryType(deliveryType)

            if (resource?.isFirstPage == true)
                updateSupplierData(resource.result)
            else {
                if (resource?.result != null && !resource.result.isNullOrEmpty()) {
                    val posStart = mSupplierList?.size
                    for (i in resource.result.indices) {
                        resource.result[i].viewType = HomeItemAdapter.SUPL_TYPE
                        mSupplierList?.add(resource.result[i])
                    }
                    homeItemAdapter?.notifyItemRangeInserted(posStart ?: 0, resource.result.size)
                }
            }
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.supplierLiveData.observe(this, supplierObserver)

    }

    private fun updateSupplierData(resource: List<SupplierDataBean>?) {

        if (resource?.isNotEmpty() == true) {
            mSupplierList?.clear()
            setViewType(HomeItemAdapter.BANNER_TYPE, 0)
            // setViewType(HomeItemAdapter.SEARCH_TYPE, 0);
            setViewType(HomeItemAdapter.FILTER_TYPE, resource.size)
            for (i in resource.indices) {
                resource[i].viewType = HomeItemAdapter.SUPL_TYPE
                mSupplierList?.add(resource[i])
            }
            homeItemAdapter?.notifyDataSetChanged()
        } else {
            setViewType(HomeItemAdapter.BANNER_TYPE, 0)
            setViewType(HomeItemAdapter.FILTER_TYPE, 0)
        }
    }



    override fun unFavSupplierResponse(data: SupplierInArabicBean?) {

    }

    override fun favSupplierResponse(supplierId: SupplierInArabicBean?) {
    }

    private fun showBottomCart() {

        val appCartModel = appUtils.getCartData(settingData,selectedCurrency)

        if (appCartModel.cartAvail) {

            bottom_cart.visibility = View.VISIBLE

            tv_total_price.text = String.format("%s %s %s", getString(R.string.total), AppConstants.CURRENCY_SYMBOL, appCartModel.totalPrice)

            tv_total_product.text = getString(R.string.total_item_tag, Utils.getDecimalPointValue(settingData, appCartModel.totalCount))


            if (appCartModel.supplierName.isNotEmpty() && screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {
                tv_supplier_name.visibility = View.VISIBLE
                tv_supplier_name.text = getString(R.string.supplier_tag, appCartModel.supplierName)
            } else {
                tv_supplier_name.visibility = View.GONE
            }

            bottom_cart.setOnClickListener { v ->
                val navOptions: NavOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.pickupResturantFrag, false)
                        .build()


                if (bookingFlowBean?.is_pickup_order == FoodAppType.Both.foodType) {
                    navController(this@DineInResturantFrag).navigate(R.id.action_resturantHomeFrag_to_cart, null, navOptions)
                } else {
                    navController(this@DineInResturantFrag).navigate(R.id.action_pickupResturantFrag_to_cart, null, navOptions)
                }

            }
        } else {
            bottom_cart.visibility = View.GONE
        }
    }


    override fun onSupplierDetail(supplierBean: SupplierDataBean?) {
        val bundle = bundleOf("supplierId" to supplierBean?.id,
                "branchId" to supplierBean?.supplier_branch_id,
                "categoryId" to supplierBean?.category_id,
                "title" to supplierBean?.name)
        if(AppConstants.DELIVERY_OPTIONS==DeliveryType.DineIn.type)
            bundle.putString("deliveryType", "dineIn")

        if (settingData?.show_supplier_detail != null && settingData?.show_supplier_detail == "1") {
            navController(this@DineInResturantFrag).navigate(R.id.action_supplierDetail, bundle)
        }
        else if (settingData?.app_selected_template != null
                && settingData?.app_selected_template == "1")
            Navigation.findNavController(requireView()).navigate(R.id.restaurantDetailFragNew, bundle)
        else
            Navigation.findNavController(requireView()).navigate(R.id.restaurantDetailFrag, bundle)
    }

    override fun onSpclView(specialListAdapter: SpecialListAdapter?) {}
    override fun onFilterScreen() {
        Navigation.findNavController(requireView()).navigate(R.id.action_pickupResturantFrag_to_bottomSheetFragment)
    }

    override fun onSearchItem(text: String?) {

    }

    override fun onHomeCategory(position: Int) {

    }

    override fun onViewMore(title :String?) {
        Navigation.findNavController(requireView()).navigate(R.id.action_pickupResturantFrag_to_offerProductListingFragment)
    }

    override fun onPagerScroll(listAdapter: BannerListAdapter, rvBannerList: RecyclerView) {
        val NUM_PAGES = mSupplierList?.find { it.viewType == HomeItemAdapter.BANNER_TYPE }?.itemModel?.bannerList?.count()
                ?: 0
        var currentPage = 0
        // Auto start of viewpager
        val handler = Handler()
        val Update = Runnable {
            if (currentPage == NUM_PAGES) {
                currentPage = 0
            }
            rvBannerList.scrollToPosition(currentPage++)
        }
        val swipeTimer = Timer()
        swipeTimer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(Update)
            }
        }, 3000, 3000)
    }

    override fun onSearchClickedForV2Theme() {

    }

    override fun supplierViewMoreCliked(data: SupplierDataBean?, listType: Int, title: String) {
    }


    companion object {
        fun newInstance(changeToDelivery:Boolean?=false) = DineInResturantFrag().apply {
             bundleOf("changeToDelivery" to changeToDelivery)
        }
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_pickup_resturant
    }

    override fun getViewModel(): HomeViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(HomeViewModel::class.java)
        return viewModel
    }

    override fun onFavStatus() {

    }


    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onRefresh() {
        swiprRefresh.isRefreshing = false
        mSupplierList?.clear()
        callApi(null, true)
    }


    private fun onRecyclerViewScrolled() {
        rv_resturant_list?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (settingData?.enable_rest_pagination_category_wise == "1") {
                    val isSuppliersPagingActive = viewModel.validForSuppliersPaging()
                    if (!recyclerView.canScrollVertically(1) && isSuppliersPagingActive) {
                        callApi(StaticFunction.getSortByValue(HomeItemAdapter.SORT_POPUP), false)
                    }
                }
            }
        })
    }

    override fun supplierDetailSuccess(data: DataSupplierDetail) {

    }

    override fun onTableSuccessfullyBooked() {
        selectedDateTimeForScheduling=null
        StaticFunction.sweetDialogueSuccess11(activity, getString(R.string.table_book_message_title),
                getString(R.string.table_book_message),
                false, 1001, this)
    }
    private var selectedDateTimeForScheduling: SupplierSlots? = null
    private var supplierId:Int?=null
    private var supplierBranchId:Int?=null

    override fun onBookNow(supplierData: SupplierDataBean) {
        if (dataManager.getCurrentUserLoggedIn()) {
            supplierId = supplierData.id
            supplierBranchId = supplierData.supplier_branch_id
            selectedDateTimeForScheduling = null

            if (supplierData.is_dine_in == 1 && settingData?.is_table_booking == "1" && settingData?.table_book_mac_theme == "1") {
              //  getTableCapacityApi(supplierData.id,supplierData.supplier_branch_id)
                StaticFunction.tableCapacityDialog(requireContext(), this)
            } else {
                activity?.launchActivity<ScheduleOrder>(SCHEDULE_REQUEST) {
                    putExtra("supplierId", supplierId.toString())
                    putExtra("dineIn", true)
                }
            }
        } else {
            appUtils.checkLoginFlow(requireActivity(), DataNames.REQUEST_RESTAURANT_LOGIN)
        }
    }
    private fun tableCapacityObserver() {
        // Create the observer which updates the UI.
        val observer = Observer<ArrayList<kotlin.String>> { resource ->

        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.tableCapacityLiveData.observe(this, observer)
    }

    private fun getTableCapacityApi(supplierId:Int,branchId:Int) {
        if (isNetworkConnected)
            viewModel.getTableCapacity(supplierId.toString(), branchId.toString())
    }

    override fun onTableCapacitySelected(capacity: Int) {
        activity?.launchActivity<ScheduleOrder>(SCHEDULE_REQUEST) {
            putExtra("supplierId", supplierId.toString())
            putExtra("dineIn", true)
            putExtra("seating_capacity", capacity)
            putExtra("supplierBranchId",supplierBranchId)
        }
    }
    override fun onListViewChanges(adapterPosition: Int,  isGrid: Boolean) {
        //do nothing
    }

    override fun onFilterClicked(ivFilter: ImageView) {

    }

    override fun isLargeClicked(isLarge: Boolean) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SCHEDULE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedDateTimeForScheduling = data?.getParcelableExtra("slotDetail")
            if (settingData?.table_book_mac_theme == "1" && selectedDateTimeForScheduling?.selectedTable != null) {
                AlertDialog.Builder(requireContext()).setMessage(getString(R.string.select_items_to_continue))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.yes)) { _, _->
                            dataManager.setkeyValue(DataNames.SAVED_TABLE_DATA, Gson().toJson(selectedDateTimeForScheduling))
                            showSupplierDetailScreen()
                        }
                        .setNegativeButton(getString(R.string.no)) { _, _->
                            onPaymentSelection()
                        }
                        .show()
            } else {
                if (settingData?.by_pass_tables_selection == "1") {
                    bookTableWithSchedule("0", supplierId)
                } else {
                    getListOfRestaurantsAccordingToSlot()
                }
            }
        } else if(requestCode==AppConstants.REQUEST_PAYMENT_OPTION && resultCode==Activity.RESULT_OK)
        {
            val payItem = data?.getParcelableExtra<CustomPayModel>("payItem")
            bookTableWithSchedule(selectedDateTimeForScheduling?.selectedTable?.id?.toString(), supplierId)
        }
    }

    private fun onPaymentSelection() {
        dataManager.getKeyValue(DataNames.FEATURE_DATA, PrefenceConstants.TYPE_STRING).toString().let {
            val listType: Type = object : TypeToken<ArrayList<SettingModel.DataBean.FeatureData?>?>() {}.type
            val featureList: ArrayList<SettingModel.DataBean.FeatureData> = Gson().fromJson(it, listType)

            activity?.launchActivity<PaymentListActivity>(AppConstants.REQUEST_PAYMENT_OPTION) {
                putParcelableArrayListExtra("feature_data", featureList)
                putExtra("enablePayment", true)
                putExtra("totalAmount",selectedDateTimeForScheduling?.selectedTable?.table_booking_price)
            }
        }
    }
    private fun showSupplierDetailScreen() {
        val bundle = Bundle()
        bundle.putInt("supplierId", supplierId ?: 0)
        bundle.putInt("branchId", supplierBranchId ?: 0)
        bundle.putInt("categoryId", 0)

        if (screenFlowBean?.app_type == AppDataType.Food.type) {
            if (bookingFlowBean?.is_pickup_order == FoodAppType.Pickup.foodType || settingData?.is_skip_theme == "1") {
                bundle.putString("deliveryType", "pickup")
            }
            if (settingData?.show_supplier_detail != null && settingData?.show_supplier_detail == "1") {
                navController(this@DineInResturantFrag).navigate(R.id.action_supplierDetail, bundle)
            } else if (settingData?.app_selected_template != null
                    && settingData?.app_selected_template == "1")
                Navigation.findNavController(requireView()).navigate(R.id.restaurantDetailFragNew, bundle)
            else
                Navigation.findNavController(requireView()).navigate(R.id.restaurantDetailFrag, bundle)
        }
    }

    private fun bookTableWithSchedule(tableId: kotlin.String?, supplierId: Int?) {

        val tempRequestHolder = hashMapOf(
                "user_id" to dataManager.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING).toString(),
                "table_id" to tableId,
                "slot_id" to selectedDateTimeForScheduling?.supplierTimings?.get(0)?.id.toString(),
                "schedule_date" to selectedDateTimeForScheduling?.startDateTime,
                "schedule_end_date" to selectedDateTimeForScheduling?.endDateTime,
                "supplier_id" to supplierId.toString(),
                "branch_id" to supplierBranchId.toString()
        )

        viewModel.makeBookingAccordingToSlot(tempRequestHolder)
    }

    private fun getListOfRestaurantsAccordingToSlot() {
        val bundle = bundleOf("slot_id" to selectedDateTimeForScheduling?.supplierTimings?.get(0)?.id.toString(),
                "supplierId" to supplierId,
                "branchId" to supplierBranchId,
                "requestFromCart" to "0"
        )
        if(activity!=null)
        navController(this@DineInResturantFrag)
                .navigate(R.id.action_home_to_tableSelection, bundle)

    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onResultReceived(result: ListItem?) {
        if (result?.requestedFrom == "1") {
            return
        }
        bookTableWithSchedule(result?.id.toString(), supplierId)
    }

    override fun onSuccessListener() {

    }

    override fun onErrorListener() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
      //  EventBus.getDefault().unregister(this)
    }
}
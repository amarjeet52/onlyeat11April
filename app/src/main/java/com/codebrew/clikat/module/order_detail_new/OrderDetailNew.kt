package com.codebrew.clikat.module.order_detail_new

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppToasty
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.OrderUtils
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.data.model.api.AddCardResponseData
import com.codebrew.clikat.data.model.api.GeofenceData
import com.codebrew.clikat.data.model.api.TrackDhl
import com.codebrew.clikat.data.model.api.orderDetail.Data
import com.codebrew.clikat.data.model.api.orderDetail.OrderDetailModel
import com.codebrew.clikat.data.model.api.orderDetail.OrderHistory
import com.codebrew.clikat.databinding.ActivityOrderDetailNewBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.AppGlobal
import com.codebrew.clikat.modal.DataZoom
import com.codebrew.clikat.modal.other.AddtoCartModel
import com.codebrew.clikat.module.order_detail.OrderDetailNavigator
import com.codebrew.clikat.module.order_detail.OrderDetailViewModel
import com.codebrew.clikat.module.order_detail_new.adapter.OrderPagerAdapter
import com.codebrew.clikat.module.order_detail_new.adapter.OrderPagerDataAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.activity_order_detail_new.*
import kotlinx.android.synthetic.main.activity_order_details.*
import kotlinx.android.synthetic.main.toolbar_app.*
import java.io.InputStreamReader
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import javax.inject.Inject


class OrderDetailNew : BaseActivity<ActivityOrderDetailNewBinding, OrderDetailViewModel>(), OrderDetailNavigator, OrderShowListener, HasAndroidInjector {

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var orderUtils: OrderUtils

    private var mAdapter: OrderPagerAdapter?=null

    private lateinit var mBinding: ActivityOrderDetailNewBinding
    private var mViewModel: OrderDetailViewModel? = null
    private var mOrderList= mutableListOf<OrderHistory>()

    private var mOderPagerList= mutableListOf<OrderPagerFragment>()

    private val decimalFormat: DecimalFormat = DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = viewDataBinding


        settingLayout()


        tb_back.setOnClickListener {
            finish()
        }

        tb_title.text = "Track Order"

        if (isNetworkConnected) {
            //viewModel.getOrderDetail()
            callOrderData()
        }

    }

    private fun settingLayout() {
        mAdapter=OrderPagerAdapter(this,mOderPagerList)
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager.adapter=mAdapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)

            }
        })
    }

    private fun callOrderData() {

        try {
            val assetManager = getAssets();
            val ims = assetManager.open("order_detail.txt");
            val gson = Gson()
            val reader = InputStreamReader(ims);
            val gsonObj = gson.fromJson(reader, OrderDetailModel::class.java)

            handleOrderData(gsonObj.data)

        } catch (e: Exception) {

        }
    }

    private fun handleOrderData(data: Data?) {

        data?.orderHistory?.map {
            it.type=1
        }

        val bottomSheetDialogFragment: BottomSheetDialogFragment = OrderStatusDialog.newInstance(data?.orderHistory?.firstOrNull())
        bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)


        mOrderList.clear()
        mOrderList.addAll(data?.orderHistory?.toMutableList()?: mutableListOf())

        mOrderList.forEachIndexed { index, orderHistory ->
            mOderPagerList.add(OrderPagerFragment.newInstance(orderHistory))
        }


        mAdapter?.notifyDataSetChanged()

    }

    override fun getBindingVariable(): Int {
            return BR.viewModel
        }

        override fun getLayoutId(): Int {
            return R.layout.activity_order_detail_new
        }

        override fun getViewModel(): OrderDetailViewModel {
            mViewModel = ViewModelProviders.of(this, factory).get(OrderDetailViewModel::class.java)
            return mViewModel as OrderDetailViewModel
        }

        override fun onBackPressed() {

        }

        override fun onCartAdded(cartdata: AddtoCartModel.CartdataBean?) {
            TODO("Not yet implemented")
        }

        override fun onCancelOrder() {
            TODO("Not yet implemented")
        }

        override fun editOrderResponse(data: Data?) {
            TODO("Not yet implemented")
        }

        override fun onCompletePayment() {
            TODO("Not yet implemented")
        }

        override fun onTrackDhl(data: TrackDhl?) {
            TODO("Not yet implemented")
        }

        override fun onTrackShipRocket(data: TrackDhl?) {
            TODO("Not yet implemented")
        }

        override fun zoomAuth(data: TrackDhl?) {
            TODO("Not yet implemented")
        }

        override fun zoomCallLink(data: DataZoom?) {
            TODO("Not yet implemented")
        }

        override fun onChangeStatus(status: Double?) {
            TODO("Not yet implemented")
        }

        override fun onGeofencePayment(data: GeofenceData?, geofenceTax: Boolean) {
            TODO("Not yet implemented")
        }

        override fun getSaddedPaymentSuccess(data: AddCardResponseData?) {
            TODO("Not yet implemented")
        }

        override fun getMyFatoorahPaymentSuccess(data: AddCardResponseData?) {
            TODO("Not yet implemented")
        }

        override fun onSosSuccess() {
            TODO("Not yet implemented")
        }

        override fun onErrorOccur(message: String) {
            TODO("Not yet implemented")
        }

        override fun onSessionExpire() {
            TODO("Not yet implemented")
        }

        override fun onShowDialog() {
            AppToasty.success(this, "ShowDialog")
        }

        override fun onDismissDialog() {
            AppToasty.success(this, "Dismiss")
        }

    override fun onCallSupplier(phoneNumber: String) {
        TODO("Not yet implemented")
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { AppGlobal.localeManager?.setLocale(it) })
    }
}
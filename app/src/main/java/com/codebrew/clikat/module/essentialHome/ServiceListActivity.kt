package com.codebrew.clikat.module.essentialHome

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentServiceListBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.Data
import com.codebrew.clikat.modal.other.English
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.other.TopBanner
import com.codebrew.clikat.module.all_categories.adapter.CategoryListAdapter
import com.codebrew.clikat.module.bottom_navigation.MainScreenActivity
import com.codebrew.clikat.module.dialog_adress.AddressDialogFragment
import com.codebrew.clikat.module.dialog_adress.interfaces.AddressDialogListener
import com.codebrew.clikat.module.home_screen.adapter.BannerListAdapter
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.gson.Gson
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.fragment_service_list.*
import java.util.*
import javax.inject.Inject


class ServiceListActivity : BaseActivity<FragmentServiceListBinding, ServiceViewModel>(), BaseInterface,
        ServiceHeaderViewAdapter.OnListFragmentInteractionListener, AddressDialogListener, HasAndroidInjector,
        CategoryListAdapter.OnCategoryListener, BannerListAdapter.BannerCallback, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var viewModel: ServiceViewModel

    private var mBinding: FragmentServiceListBinding? = null

    @Inject
    lateinit var prefHelper: PreferenceHelper

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var appUtils: AppUtils

    private var mAdapter: ServiceHeaderViewAdapter? = null

    private val mValues: MutableList<English>? = mutableListOf()
    private var mValuesHeader: HashMap<String, List<English>>? = null

    private var clientInform: SettingModel.DataBean.SettingData? = null

    private var mCategoryName = ""

    private val appBackground = Color.parseColor(Configurations.colors.appBackground)

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigator = this

        StaticFunction.setStatusBarColor(this@ServiceListActivity, getColor(R.color.dark_blue))

//        EventBus.getDefault().register(this)


        mBinding = viewDataBinding
        viewDataBinding.color = Configurations.colors
        viewDataBinding.strings = Configurations.strings

        clientInform = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)

        categoryObserver()


        swiprRefresh.setOnRefreshListener(this)


        mValues?.clear()

        if (isNetworkConnected) {
            if (viewModel.homeDataLiveData.value == null) {
                viewModel.getCategories()
            } else {
                loadCategories(viewModel.homeDataLiveData.value)
            }
        }

        tvArea.setOnClickListener {
            AddressDialogFragment.newInstance(0,0).show(supportFragmentManager, "dialog")
        }
    }

    override fun onResume() {
        super.onResume()

        settingToolbar()
    }

    private fun getHeaderMap(resource: Data?) {
        val results: HashMap<String, List<English>> = hashMapOf()

        val essentials = mutableListOf<English>()

        mValues?.let {
            for (i in 0 until it.size) {
                if (it[i].id == 93 || it[i].id == 94) {
                    essentials.add(it[i])
                }
            }
        }

        results["Essentials, delivered at your door steps."] = essentials

        mValuesHeader = results

        setServiceLIstAdapter(resource)
    }

    private fun setServiceLIstAdapter(resource: Data?) {

            grp_banner.visibility = View.GONE
            ic_bg_1.visibility = View.VISIBLE
            ic_bg_2.visibility = View.VISIBLE

            mAdapter = ServiceHeaderViewAdapter(mValuesHeader ?: hashMapOf(), this)
            rv_categories_header.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            rv_categories_header.adapter = mAdapter

    }


    private fun settingToolbar() {

        prefHelper.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)?.let {

            tvArea.text = appUtils.getAddressFormat(it)
        }
        // iv_marker.loadImage(clientInform?.logo_url ?: "")
        //  iv_marker.visibility = View.VISIBLE
    }


    private fun categoryObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<Data> { resource ->

            loadCategories(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.homeDataLiveData.observe(this, catObserver)

    }

    private fun loadCategories(resource: Data?) {
        if (resource?.english?.isNotEmpty() == true) {
            mValues?.clear()
            mValues?.addAll(resource.english)

        }
        getHeaderMap(resource)
    }

    private fun saveCategoryInf(bean: English?) {
        clientInform?.payment_after_confirmation = (bean?.payment_after_confirmation
                ?: 0).toString()
        clientInform?.order_instructions = (bean?.order_instructions ?: 0).toString()
        clientInform?.cart_image_upload = (bean?.cart_image_upload ?: 0).toString()

        prefHelper.setkeyValue(DataNames.SETTING_DATA, Gson().toJson(clientInform))


    }


    companion object {
        @JvmStatic
        fun newInstance() =
                ServiceListActivity()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_service_list
    }

    override fun getViewModel(): ServiceViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(ServiceViewModel::class.java)
        return viewModel
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onListFragmentInteraction(item: English?) {


        if (item?.categoryName.isNullOrBlank()) {

            AppConstants.APP_SAVED_SUB_TYPE = item?.type ?: 0
            AppConstants.APP_SUB_TYPE = item?.type ?: 0


            saveCategoryInf(item)

            // prefHelper.setkeyValue(DataNames.SELECTED_LANGUAGE, ClikatConstants.LANGUAGE_ENGLISH)

            StaticFunction.clearCart(this)
            prefHelper.onCartClear()
            prefHelper.setkeyValue(PrefenceConstants.CATEGORY_ID, item?.id.toString())

            launchActivity<MainScreenActivity> {
                putExtra("cat_id", item?.id)
                if (!item?.screenType.isNullOrEmpty() && item?.screenType == "Banner") {
                    putExtra("screenType", item.screenType)
                    putExtra("bannerItem", item)
                }
            }

        }
    }


    fun catId(categoryName: String): Int {
        return when (categoryName) {
            "Cab Booking" -> 7
            "Ambulance" -> 10
            else -> 4
        }
    }

    override fun onAddressSelect(adrsBean: AddressBean) {

        adrsBean.let {
            appUtils.setUserLocale(it)

            prefHelper.addGsonValue(PrefenceConstants.ADRS_DATA, Gson().toJson(it))
            tvArea.text = appUtils.getAddressFormat(it)
        }


    }

    override fun onDestroyDialog() {

    }
    override fun onVehicleSelect(id:String,model: String, color: String, plate_no: String) {
        TODO("Not yet implemented")
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    override fun onCategoryItem(item: English?) {
        onListFragmentInteraction(item)
    }

    override fun onBannerDetail(bannerBean: TopBanner?) {

        val bannerItem = English()
        bannerItem.is_question = bannerBean?.is_question
        bannerItem.is_assign = bannerBean?.is_assign
        bannerItem.payment_after_confirmation = bannerBean?.payment_after_confirmation
        bannerItem.terminology = bannerBean?.terminology
        bannerItem.cart_image_upload = bannerBean?.cart_image_upload
        bannerItem.order_instructions = bannerBean?.order_instructions
        bannerItem.supplierName = bannerBean?.supplier_name
        bannerItem.id = bannerBean?.category_id
        bannerItem.supplier_id = bannerBean?.supplier_id
        bannerItem.supplier_branch_id = bannerBean?.branch_id
        bannerItem.type = bannerBean?.type
        bannerItem.screenType = "Banner"


        onListFragmentInteraction(bannerItem)
    }

    override fun onRefresh() {

        swiprRefresh.isRefreshing = false

        if (isNetworkConnected) {
            viewModel.getCategories()
        }
    }

}

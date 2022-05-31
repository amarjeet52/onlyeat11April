package com.codebrew.clikat.module.crave_mania

import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.app_utils.extension.afterTextChanged
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.base.EmptyListListener
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.LoyaltyData
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentCraveManiaAllBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.crave_mania.adapter.CraveManiaAllItemAdapter
import com.codebrew.clikat.module.dialog_adress.AddressDialogFragment
import com.codebrew.clikat.module.dialog_adress.interfaces.AddressDialogListener
import com.codebrew.clikat.module.dialog_adress.v2.AddressDialogFragmentV2
import com.codebrew.clikat.module.loyaltyPoints.DataLoyality
import com.codebrew.clikat.module.loyaltyPoints.LoyaltyPointsNavigator
import com.codebrew.clikat.module.loyaltyPoints.LoyaltyPointsViewModel
import com.codebrew.clikat.module.roadmap.RoadMapActivity
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_all_supplier.*
import kotlinx.android.synthetic.main.fragment_crave_mania_all.*
import kotlinx.android.synthetic.main.frgment_crave_mania.*
import kotlinx.android.synthetic.main.frgment_crave_mania.rvChief
import kotlinx.android.synthetic.main.toolbar_app.*
import kotlinx.android.synthetic.main.toolbar_home.*
import kotlinx.android.synthetic.main.toolbar_supplier.*
import kotlinx.android.synthetic.main.toolbar_supplier.tb_back
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class FragmentCraveManiaAll1 : BaseFragment<FragmentCraveManiaAllBinding, CraveManiaViewModelNew>(), AddressDialogListener
        , CraveManiaNavigator, CraveManiaAllItemAdapter.CraveListCallback, EmptyListListener, LoyaltyPointsNavigator {
    private lateinit var viewModel: CraveManiaViewModelNew
    lateinit var craveManiaAdapter: CraveManiaAllItemAdapter
    private var supplierBundle: Bundle? = null
    var allSampleData: ArrayList<SectionDataModel>? = ArrayList<SectionDataModel>()
    var type = ""
    private val colorConfig by lazy { Configurations.colors }
    private var clientInform: SettingModel.DataBean.SettingData? = null
    @Inject
    lateinit var appUtils: AppUtils
    private val decimalFormat: DecimalFormat = DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH))
    @Inject
    lateinit var dataManager: DataManager
    @Inject
    lateinit var factory: ViewModelProviderFactory
    private var mBinding: FragmentCraveManiaAllBinding? = null
    private lateinit var viewModel1: LoyaltyPointsViewModel
    var singleItem: List<FdArray> = ArrayList<FdArray>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding
        viewModel1 = ViewModelProviders.of(this, factory).get(LoyaltyPointsViewModel::class.java)
        viewModel1.navigator = this
        viewModel1.apiGetLoyaltyPoints()

        clientInform = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        setupUiForCrave()
        updateLyt()
        Glide.with(this).asGif().load(R.raw.loading_gif).into(mBinding!!.imageLoader);

        type = arguments?.getString("type", "") ?: ""
        dataObserver()
        val linearLayoutManager =
                LinearLayoutManager(requireActivity())
        rvChief.layoutManager = linearLayoutManager
        viewModel.getSupplierListAll(type)

        searchView.afterTextChanged {
            if (it.isEmpty()) {
                hideKeyboard()
            }
            craveManiaAdapter?.filter?.filter(it.toLowerCase(DateTimeUtils.timeLocale))
        }
        tb_back.visibility=View.GONE
        tb_back.setOnClickListener {
            hideKeyboard()
            Navigation.findNavController(view).popBackStack()
        }

        if (clientInform?.show_ecom_v2_theme == "1") {
            searchView?.background = ContextCompat.getDrawable(requireActivity(), R.drawable.search_home_radius_background)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                searchView.background.colorFilter = BlendModeColorFilter(Color.parseColor(colorConfig.search_background), BlendMode.SRC_ATOP)
            }
            searchView.setHint(getString(R.string.search_)+" "+getString(R.string.restaurant))
            searchView.setTextColor(Color.parseColor(colorConfig.search_textcolor))
            searchView.setHintTextColor(Color.parseColor(colorConfig.search_textcolor))

//            toolbar_layout?.elevation = 0f
//            toolbar_layout?.background = ContextCompat.getDrawable(requireActivity(), R.drawable.background_toolbar_bottom_radius)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                toolbar_layout?.background?.colorFilter = BlendModeColorFilter(Color.parseColor(colorConfig.toolbarColor), BlendMode.SRC_ATOP)
//            }
        } else {
            searchView.setHint(getString(R.string.search_)+" "+getString(R.string.restaurant))
            searchView?.background = ContextCompat.getDrawable(requireActivity(), R.drawable.shape_supplier_search)
        }

    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_crave_mania_all
    }

    override fun getViewModel(): CraveManiaViewModelNew {
        let {
            viewModel = ViewModelProviders.of(it, factory).get(CraveManiaViewModelNew::class.java)
        }

        return viewModel
    }

    private fun dataObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<CraveManiaData> { data ->
            Log.e("data", "data")


            if (data?.products?.size != 0) {
                craveManiaAdapter = CraveManiaAllItemAdapter(requireActivity(), data?.products!!, this,dataManager.getLangCode())
                rvChief.visibility=View.VISIBLE
                tv_no_data.visibility=View.GONE
            }else{
                rvChief.visibility=View.GONE
                tv_no_data.visibility=View.VISIBLE
            }

            craveManiaAdapter?.settingCallback(this)
            rvChief.adapter = craveManiaAdapter

        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.homeDataLiveData.observe(this, catObserver)
    }


    override fun onSupplierList(data: CraveManiaData) {


    }

    override fun onBannerList(data: List<BannerData>) {
        TODO("Not yet implemented")
    }

    override fun onErrorOccur(message: String) {
//        try {
//            mainmenu.onSnackbar(message)
//        } catch (e: Exception) {
//
//        }
    }

    override fun onSessionExpire() {
        TODO("Not yet implemented")
    }


    override fun onCraveListDetail(item_list: List<SupplierDetailX>, pos: Int) {
        supplierBundle = bundleOf("supplierId" to item_list?.get(pos).supplier_id)
        supplierBundle?.putInt("branchId", item_list?.get(pos).id?:0)
        supplierBundle?.putInt("categoryId", 0)
        try {
            if (item_list?.get(pos).supplier_image.toString() == null || item_list?.get(pos).supplier_image.toString().equals("")) {
                supplierBundle?.putString("supplierLogo", "")
            } else {
                supplierBundle?.putString("supplierBannerImage", item_list?.get(pos).supplier_image.toString()?:"")
            }
        }catch (e:Exception)
        {

        }

        try {
            if (item_list?.get(pos).logo.toString() == null || item_list?.get(pos).logo.toString().equals("")) {
                supplierBundle?.putString("supplierLogo", "")
            } else {
                supplierBundle?.putString("supplierLogo", item_list?.get(pos).logo.toString() ?: "")
            }
        }catch (e:Exception)
        {

        }

        supplierBundle?.putString("supplierRating", "")

        supplierBundle?.putString("supplierName", item_list?.get(pos).name?:"")
        supplierBundle?.putString("supplierTime", "")
        supplierBundle?.putString("is_crave", "1")
        navController(this@FragmentCraveManiaAll1).navigate(R.id.action_mania_all_to_restaurant_detail, supplierBundle)

    }

    override fun onEmptyList(count: Int) {

    }
    fun setupUiForCrave() {
        layoutMainToolbar.visibility = View.GONE
        layoutCrave.visibility = View.VISIBLE

        iv_supplier_logo2.setImageResource(R.drawable.ic_c)

        iv_supplier_logo2.setOnClickListener {
            var intent= Intent(requireActivity(), RoadMapActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
        icAddress2.setImageResource(R.drawable.ic_add)

    }

    private fun updateLyt() {


        dataManager.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)?.let {
            tvArea.text = appUtils.getAddressFormat(it)
            tvArea2.text = appUtils.getAddressFormat(it)

            Log.e("update_here", "ghfdhgfhgfg")
        }

        val isUserLoggedIn = dataManager.getCurrentUserLoggedIn()
        tvArea.setOnClickListener {
            if (clientInform?.show_ecom_v2_theme == "1") {
                AddressDialogFragmentV2.newInstance(0).show(childFragmentManager, "dialog")
            } else {
                AddressDialogFragment.newInstance(0, 0).show(childFragmentManager, "dialog")
            }

        }

        tvArea2.setOnClickListener {
            if (clientInform?.show_ecom_v2_theme == "1") {
                AddressDialogFragmentV2.newInstance(0).show(childFragmentManager, "dialog")
            } else {
                AddressDialogFragment.newInstance(0, 0).show(childFragmentManager, "dialog")
            }

        }


        iv_search.setOnClickListener {
            if (clientInform?.is_unify_search == "1")
                navController(this@FragmentCraveManiaAll1).navigate(R.id.action_craveManiaAllFrag_to_unify_SearchFragment)
            else
                navController(this@FragmentCraveManiaAll1).navigate(R.id.action_craveManiaAllFrag_to_searchFragment)
        }
               tvType.setText(getString(R.string.crave_mania))

        iv_search2.setOnClickListener {
            if (clientInform?.is_unify_search == "1")
                navController(this@FragmentCraveManiaAll1).navigate(R.id.action_craveManiaAllFrag_to_unify_SearchFragment)
            else
                navController(this@FragmentCraveManiaAll1).navigate(R.id.action_craveManiaAllFrag_to_searchFragment)
        }

    }
    override fun onAddressSelect(adrsBean: AddressBean) {

        adrsBean.let {
            appUtils.setUserLocale(it)
            Log.e("update_here","hjkkllllll")
            dataManager.addGsonValue(PrefenceConstants.ADRS_DATA, Gson().toJson(it))
            tvArea.text = appUtils.getAddressFormat(it)
            tvArea2.text = appUtils.getAddressFormat(it)
            viewModel.getSupplierListAll(type)

        }

    }

    override fun onDestroyDialog() {
        TODO("Not yet implemented")
    }

    override fun onVehicleSelect(id: String, model: String, color: String, plate_no: String) {
        TODO("Not yet implemented")
    }

    override fun loyaltyPointsSuccess(data: LoyaltyData?) {
        tvRewardText?.text = decimalFormat.format(data?.totalEarningPoint)
    }

    override fun loyaltyPointsSuccessNew(data: List<DataLoyality?>) {

    }

}
package com.codebrew.clikat.module.crave_mania

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppToasty
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.LoyaltyData
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentAllSupplierBinding
import com.codebrew.clikat.databinding.FrgmentCraveManiaBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.other.SupplierDataBean
import com.codebrew.clikat.module.crave_mania.adapter.CraveManiaAdapterNew
import com.codebrew.clikat.module.dialog_adress.AddressDialogFragment
import com.codebrew.clikat.module.dialog_adress.interfaces.AddressDialogListener
import com.codebrew.clikat.module.dialog_adress.v2.AddressDialogFragmentV2
import com.codebrew.clikat.module.home_screen.HomeFragment
import com.codebrew.clikat.module.home_screen.adapter.BranchAdapter
import com.codebrew.clikat.module.loyaltyPoints.DataLoyality
import com.codebrew.clikat.module.loyaltyPoints.LoyaltyPointsNavigator
import com.codebrew.clikat.module.loyaltyPoints.LoyaltyPointsViewModel
import com.codebrew.clikat.module.roadmap.RoadMapActivity
import com.codebrew.clikat.preferences.DataNames
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_resturant_home.*
import kotlinx.android.synthetic.main.frgment_crave_mania.*
import kotlinx.android.synthetic.main.toolbar_home.*
import ru.nikartm.support.BadgePosition
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class FragmentCraveMania() : BaseFragment<FrgmentCraveManiaBinding, CraveManiaViewModelNew>(), AddressDialogListener,
        CraveManiaNavigator, CraveManiaAdapterNew.CraveListCallback, Parcelable , LoyaltyPointsNavigator {
    private lateinit var viewModel: CraveManiaViewModelNew
    lateinit var craveManiaAdapter: CraveManiaAdapterNew
    private val decimalFormat: DecimalFormat = DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH))
    private var supplierBundle: Bundle? = null
    @Inject
    lateinit var appUtils: AppUtils
    @Inject
    lateinit var mDataManager: PreferenceHelper
    var allSampleData: ArrayList<SectionDataModel>? = ArrayList<SectionDataModel>()
    private var mBinding: FrgmentCraveManiaBinding? = null
    private lateinit var viewModel1: LoyaltyPointsViewModel
    @Inject
    lateinit var factory: ViewModelProviderFactory
    private var specialOffers: MutableList<ProductDataBean>? = null
    var singleItem: List<BannerData> = ArrayList<BannerData>()
    private var clientInform: SettingModel.DataBean.SettingData? = null
    constructor(parcel: Parcel) : this() {
        supplierBundle = parcel.readBundle(Bundle::class.java.classLoader)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding

        clientInform = mDataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        viewModel1 = ViewModelProviders.of(this, factory).get(LoyaltyPointsViewModel::class.java)
        viewModel1.navigator = this
        viewModel1.apiGetLoyaltyPoints()
        Glide.with(this).asGif().load(R.raw.loading_gif).into(mBinding!!.imageLoader);
        setupUiForCrave()
        updateLyt()
        dataObserver()
        val linearLayoutManager =
                LinearLayoutManager(requireActivity())
        mBinding?.rvChief?.layoutManager = linearLayoutManager
        viewModel.getSupplierList()
    }

     fun onBackPressed() {

        mDataManager.setkeyValue(AppConstants.ROAD_MAP_TYPE, "")
    }
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.frgment_crave_mania
    }

    override fun getViewModel(): CraveManiaViewModelNew {
        let {
            viewModel = ViewModelProviders.of(it, factory).get(CraveManiaViewModelNew::class.java)
        }

        return viewModel
    }

    private fun dataObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<List<BannerData>> { data ->

            allSampleData?.clear()


                var dm = SectionDataModel()
            if (mDataManager.getLangCode().equals("14")) {
                dm.setHeaderTitle("Buy 1 Get 1")
            } else {
                dm.setHeaderTitle("إشتري ١ واحصل على ١ مجانا")
            }

                dm.setImage(R.drawable.buy_one)
                singleItem = data!!
                dm.setAllItemsInSection(singleItem)
                allSampleData?.add(dm)
            dm = SectionDataModel()
            if(  mDataManager.getLangCode().equals("14"))
            {
                dm.setHeaderTitle("Discount")
            }else{
                dm.setHeaderTitle("خصم")
            }

            dm.setImage(R.drawable.discount)
            singleItem = data!!
            dm.setAllItemsInSection(singleItem)
            allSampleData?.add( dm)

               dm = SectionDataModel()
            if(  mDataManager.getLangCode().equals("14"))
            {
                dm.setHeaderTitle("Combo")
            }else{
                dm.setHeaderTitle("كومبو")
            }
                dm.setImage(R.drawable.combo)
                singleItem = data!!
                dm.setAllItemsInSection(singleItem)
                allSampleData?.add(dm)

                 dm = SectionDataModel()
            if(  mDataManager.getLangCode().equals("14"))
            {
                dm.setHeaderTitle("Free Delivery")
            }else{
                dm.setHeaderTitle("توصيل مجاني")
            }
                dm.setImage(R.drawable.free_deluvery)
                singleItem = data!!
                dm.setAllItemsInSection(singleItem)
                allSampleData?.add(dm)



            craveManiaAdapter = CraveManiaAdapterNew(requireActivity(), allSampleData!!)
            craveManiaAdapter.settingCallback(this)
            mBinding?.rvChief?.adapter = craveManiaAdapter
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.homeDataAllLiveData.observe(this, catObserver)
    }


    override fun onSupplierList(data: CraveManiaData) {


    }

    override fun onBannerList(data: List<BannerData>) {
        TODO("Not yet implemented")
    }

    override fun loyaltyPointsSuccess(data: LoyaltyData?) {
        tvRewardText?.text = decimalFormat.format(data?.totalEarningPoint)
    }

    override fun loyaltyPointsSuccessNew(data: List<DataLoyality?>) {
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

    override fun onCraveDetail(item_list: List<FdArray>, pos: Int) {
        supplierBundle = bundleOf("supplierId" to item_list?.get(pos).supplier_detail.supplier_id)
        supplierBundle?.putInt("branchId", item_list?.get(pos).supplier_detail.id)
        supplierBundle?.putInt("categoryId", 0)
        supplierBundle?.putString("supplierBannerImage", item_list?.get(pos).supplier_detail.logo)
        supplierBundle?.putString("supplierLogo", item_list?.get(pos).supplier_detail.logo.toString())
        supplierBundle?.putString("supplierRating", "")

        supplierBundle?.putString("supplierName", item_list?.get(pos).supplier_detail.name)
        supplierBundle?.putString("supplierTime", "")
        supplierBundle?.putString("is_crave", "1")
        navController(this@FragmentCraveMania).navigate(R.id.action_mania_to_restaurant_detail, supplierBundle)

    }

    override fun onViewAllClick(type:String)
    {
        val bundle = bundleOf(
                "type" to  type
        )
        navController(this@FragmentCraveMania).navigate(R.id.craveManiaAll, bundle)

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeBundle(supplierBundle)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FragmentCraveMania> {
        override fun createFromParcel(parcel: Parcel): FragmentCraveMania {
            return FragmentCraveMania(parcel)
        }

        override fun newArray(size: Int): Array<FragmentCraveMania?> {
            return arrayOfNulls(size)
        }
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


        mDataManager.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)?.let {
            tvArea.text = appUtils.getAddressFormat(it)
            tvArea2.text = appUtils.getAddressFormat(it)

            Log.e("update_here", "ghfdhgfhgfg")
        }

        val isUserLoggedIn = mDataManager.getCurrentUserLoggedIn()
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
                navController(this@FragmentCraveMania).navigate(R.id.action_craveManiaFrag_to_unify_SearchFragment)
            else
                navController(this@FragmentCraveMania).navigate(R.id.action_craveManiaFrag_to_searchFragment)
        }
             tvType.setText(getString(R.string.crave_mania))

        iv_search2.setOnClickListener {
            if (clientInform?.is_unify_search == "1")
                navController(this@FragmentCraveMania).navigate(R.id.action_craveManiaFrag_to_unify_SearchFragment)
            else
                navController(this@FragmentCraveMania).navigate(R.id.action_craveManiaFrag_to_searchFragment)
        }

    }
    override fun onAddressSelect(adrsBean: AddressBean) {

        adrsBean.let {
            appUtils.setUserLocale(it)
            Log.e("update_here","hjkkllllll")
            mDataManager.addGsonValue(PrefenceConstants.ADRS_DATA, Gson().toJson(it))
            tvArea.text = appUtils.getAddressFormat(it)
            tvArea2.text = appUtils.getAddressFormat(it)
        }

    }

    override fun onDestroyDialog() {
        TODO("Not yet implemented")
    }

    override fun onVehicleSelect(id: String, model: String, color: String, plate_no: String) {
        TODO("Not yet implemented")
    }

}
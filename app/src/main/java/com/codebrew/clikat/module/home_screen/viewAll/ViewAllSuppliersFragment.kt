package com.codebrew.clikat.module.home_screen.viewAll

import android.os.Bundle
import android.os.Parcelable
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.databinding.FragmentSupplierListBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.SupplierList
import com.codebrew.clikat.modal.other.OfferDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.other.SupplierInArabicBean
import com.codebrew.clikat.module.home_screen.adapter.HomeItemAdapter
import com.codebrew.clikat.module.home_screen.adapter.SponsorListAdapter
import com.codebrew.clikat.module.supplier_all.SupplierListNavigator
import com.codebrew.clikat.module.supplier_all.SupplierListViewModel
import com.codebrew.clikat.preferences.DataNames
import kotlinx.android.synthetic.main.fragment_supplier_list.*
import kotlinx.android.synthetic.main.nothing_found.*
import javax.inject.Inject


class ViewAllSuppliersFragment : BaseFragment<FragmentSupplierListBinding, SupplierListViewModel>(),
        SupplierListNavigator, SponsorListAdapter.SponsorDetail {


    private var mAdapter: SponsorListAdapter? = null
    private var clientInform: SettingModel.DataBean.SettingData? = null

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var factory: ViewModelProviderFactory

    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null

    private lateinit var viewModel: SupplierListViewModel

    private var mBinding: FragmentSupplierListBinding? = null
    private var sponsorList: MutableList<SupplierInArabicBean>? = mutableListOf()
    private var catId = 0
    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }

    private var listType: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigator = this

        screenFlowBean = dataManager.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        clientInform = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)

        suppplierListObserver()

    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_supplier_list
    }

    override fun getViewModel(): SupplierListViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(SupplierListViewModel::class.java)
        return viewModel
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding

        initialise()
        listeners()
        setAdapter()
        hitApi()

    }

    private fun hitApi() {
        if (viewModel.offersLiveData.value == null) {

            val orderBy = if (clientInform?.dynamic_home_screen_sections == "1") {
                "1"
            } else null

            if (isNetworkConnected)
                viewModel.getOfferList(catId, clientInform?.app_selected_theme, orderBy)
        } else {
            updateList(viewModel.offersLiveData.value)
        }
    }

    private fun initialise() {
        tvTitleCenter?.visibility = View.VISIBLE
        tvTitle?.visibility = View.GONE
        bottom_cart?.visibility = View.GONE
        searchView?.visibility = View.GONE

        catId = if (dataManager.getKeyValue(PrefenceConstants.CATEGORY_ID, PrefenceConstants.TYPE_STRING).toString().isNotEmpty()) {
            dataManager.getKeyValue(PrefenceConstants.CATEGORY_ID, PrefenceConstants.TYPE_STRING).toString().toInt()
        } else {
            0
        }
        arguments?.let {
            listType = it.getInt("listType")

            if (!it.getString("title").isNullOrEmpty())
                tvTitleCenter?.text = it.getString("title")
            else {
                tvTitleCenter?.text = when (listType) {
                    HomeItemAdapter.TYPE_HORIZONTAL_SUPPLIERS -> getString(R.string.restaurant_trending_sellers, textConfig?.suppliers)
                    HomeItemAdapter.TYPE_BEST_SELLERS_SUPLR -> getString(R.string.restaurant_best_sellers, textConfig?.suppliers)
                    HomeItemAdapter.TYPE_FASTEST_DELIVERY -> getString(R.string.restaurant_fastest_delivery, textConfig?.suppliers)
                    else -> getString(R.string.recommed_supplier, textConfig?.suppliers)
                }
            }

        }

        if (clientInform?.is_skip_theme == "1") {
            tvTitleOoPs?.visibility = View.VISIBLE
            ivPlaceholder?.setImageResource(R.drawable.ic_graphic)
            tvText?.text = getString(R.string.nothing_found_try_again)
            tvText?.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            tvText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        }


    }

    private fun listeners() {
        ivBack?.setOnClickListener {
            hideKeyboard()
            navController(this).popBackStack()
        }
    }

    private fun setAdapter() {
        mAdapter = SponsorListAdapter(sponsorList ?: listOf(), screenFlowBean?.app_type
                ?: -1, clientInform, 0,appUtils)

        recyclerView?.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recyclerView?.adapter = mAdapter
        mAdapter?.setShowOnlyVertical(true)
        mAdapter?.settingCallback(this)
    }


    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    private fun suppplierListObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<OfferDataBean> { resource ->
            updateList(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.offersLiveData.observe(this, catObserver)
    }

    private fun updateList(resource: OfferDataBean?) {
        val list = when (listType) {
            HomeItemAdapter.TYPE_BEST_SELLERS_SUPLR, HomeItemAdapter.TYPE_HORIZONTAL_SUPPLIERS -> resource?.bestSellersSuppliers
            HomeItemAdapter.TYPE_FASTEST_DELIVERY -> resource?.fastestDeliverySuppliers
            else -> resource?.supplierInArabic
        }
        viewModel.setSupplierList(list?.size ?: 0)
        sponsorList?.clear()
        sponsorList?.addAll(list ?: emptyList())
        mAdapter?.notifyDataSetChanged()
    }

    override fun favSupplierResponse(supplierId: SupplierList?) {
        supplierId?.Favourite = 1
        if (supplierId?.position != null && supplierId.position != -1)
            mAdapter?.notifyItemChanged(supplierId.position ?: 0)
    }

    override fun unFavSupplierResponse(data: SupplierList?) {
        data?.Favourite = 0
        if (data?.position != null && data.position != -1)
            mAdapter?.notifyItemChanged(data.position ?: 0)
    }

    override fun onBranchList(supplierList: MutableList<SupplierList>) {

    }

    override fun onSponsorDetail(supplier: SupplierInArabicBean?) {
        if (clientInform?.show_supplier_detail != null && clientInform?.show_supplier_detail == "1") {
            val bundle = Bundle()
            bundle.putInt("supplierId", supplier?.id ?: 0)
            bundle.putInt("branchId", supplier?.supplier_branch_id ?: 0)
            bundle.putString("title", supplier?.name ?: "")
            bundle.putInt("categoryId", 0)

            navController(this@ViewAllSuppliersFragment)
                    .navigate(R.id.action_supplierDetail, bundle)
        } else {
            val bundle = bundleOf("supplierId" to supplier?.id,
                    "title" to supplier?.name,
                    "branchId" to supplier?.supplier_branch_id)

            if (screenFlowBean?.app_type == AppDataType.Food.type) {

                if (clientInform?.app_selected_template != null && clientInform?.app_selected_template == "1") {
                    navController(this@ViewAllSuppliersFragment).navigate(R.id.action_restaurantDetailNew, bundle)
                } else {
                    navController(this@ViewAllSuppliersFragment).navigate(R.id.action_restaurantDetail, bundle)
                }
            } else {
                if (!supplier?.category.isNullOrEmpty()) {
                    bundle.putBoolean("is_supplier", true)
                    bundle.putParcelable("supplierData", supplier)
                    bundle.putParcelableArrayList("subcategory", ArrayList<Parcelable>(supplier?.category
                            ?: mutableListOf()))


                    if (clientInform?.is_supplier_detail == "1") {
                        if (clientInform?.show_ecom_v2_theme == "1") {
                            navController(this@ViewAllSuppliersFragment)
                                    .navigate(R.id.action_supplierDetail_v2, bundle)
                        } else {
                            navController(this@ViewAllSuppliersFragment)
                                    .navigate(R.id.action_supplierDetail, bundle)
                        }
                    } else {
                        navController(this@ViewAllSuppliersFragment)
                                .navigate(R.id.actionSubCategory, bundle)
                    }

                } else {
                    navController(this@ViewAllSuppliersFragment).navigate(R.id.action_productListing, bundle)
                }

            }
        }
    }

    override fun onSupplierCall(supplier: SupplierInArabicBean?) {
    }

    override fun onSponsorWishList(supplier: SupplierInArabicBean?, parentPos: Int?, isChecked: Boolean) {
        if (dataManager.getCurrentUserLoggedIn()) {
            if (isNetworkConnected) {
                val data = SupplierList()
                data.position = parentPos
                if (supplier?.Favourite == null || supplier.Favourite == 0)
                    viewModel.markFavSupplier(data)
                else
                    viewModel.unFavSupplier(data)
            }
        }
    }

    override fun onBookNow(supplierData: SupplierInArabicBean) {

    }


}
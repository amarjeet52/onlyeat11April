package com.codebrew.clikat.module.jumaHome

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.*
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.AppDataManager
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.databinding.FragmentJumaProductsBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.*
import com.codebrew.clikat.modal.other.SettingModel.DataBean.ScreenFlowBean
import com.codebrew.clikat.module.jumaHome.adapter.JumaProductsMainAdapter
import com.codebrew.clikat.module.product.product_listing.DialogListener
import com.codebrew.clikat.module.restaurant_detail.RestDetailNavigator
import com.codebrew.clikat.module.restaurant_detail.RestDetailViewModel
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.fragment_juma_products.*
import kotlinx.android.synthetic.main.toolbar_app.*
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
class JumaProductsFragment : BaseFragment<FragmentJumaProductsBinding, RestDetailViewModel>(), RestDetailNavigator, JumaProductsMainAdapter.OnProceedClicked, DialogListener {

    private var adapter: JumaProductsMainAdapter? = null


    @Inject
    lateinit var dataManager: AppDataManager

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var appUtils: AppUtils

    private lateinit var viewModel: RestDetailViewModel

    @Inject
    lateinit var permissionUtil: PermissionFile

    @Inject
    lateinit var prodUtils: ProdUtils

    private var mBinding: FragmentJumaProductsBinding? = null
    private var screenFlowBean: ScreenFlowBean? = null
    private var bookingFlowBean: SettingModel.DataBean.BookingFlowBean? = null
    private var settingData: SettingModel.DataBean.SettingData? = null
    private var categoryDetail: English? = null
    private var productBeans = mutableListOf<ProductBean>()
    private var supplierId: String? = null
    private var mDeliveyType: Int = 0

    @Inject
    lateinit var mDialogsUtil: DialogsUtil

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this
        restDetailObserver()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding

        mBinding?.color = Configurations.colors
        mBinding?.drawables = Configurations.drawables

        screenFlowBean = dataManager.getGsonValue(DataNames.SCREEN_FLOW, ScreenFlowBean::class.java)
        bookingFlowBean = dataManager.getGsonValue(DataNames.BOOKING_FLOW, SettingModel.DataBean.BookingFlowBean::class.java)
        settingData = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        supplierId = dataManager.getKeyValue(PrefenceConstants.GENRIC_SUPPLIERID, PrefenceConstants.TYPE_INT).toString()
        listeners()
        initialise()
        setAdapter()
        apiProductsList(true)
    }

    private fun initialise() {
        if (arguments != null && arguments?.containsKey("categoryDetail") == true) {
            categoryDetail = arguments?.getParcelable("categoryDetail")
            tb_title?.text = categoryDetail?.name
        }
    }

    private fun listeners() {
        tb_back?.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }

        swipeRefresh?.setOnRefreshListener {
            swipeRefresh?.isRefreshing = false
            apiProductsList(true)
        }
    }

    private fun setAdapter() {
        adapter = JumaProductsMainAdapter(this)
        rvProducts?.adapter = adapter
    }


    private fun apiProductsList(isFirstPage: Boolean) {
        if (isNetworkConnected)
            viewModel.getProductList(supplierId.toString(), isFirstPage, settingData, categoryDetail?.id.toString())
    }

    private fun restDetailObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<PagingResult<SuplierProdListModel>> { resource ->
            refreshAdapter(resource?.result?.data)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.supplierLiveData.observe(this, catObserver)
    }


    private fun refreshAdapter(data: DataBean?) {

        var subCatName: String? = null

        if (viewModel.skip == 0)
            productBeans.clear()

        if (data?.product?.isNotEmpty() == true) {


            // productBeans.addAll(data.product ?: listOf())

            data.product?.map { prod ->

                prod.is_SubCat_visible = true

                changeProductList(prod.value)

                if (prod.detailed_category_name?.count() ?: 0 > 0) {
                    prod.detailed_category_name?.distinctBy { it.detailed_sub_category_id }?.forEach { detailProd ->
                        val prodBean = prod.copy()
                        prodBean.detailed_sub_category = detailProd.name
                        prod.value?.map {
                            it.detailed_sub_name = detailProd.name
                        }

                        prodBean.value = prod.value?.filter { it.detailed_sub_category_id == detailProd.detailed_sub_category_id }?.toMutableList()

                        if (prodBean.value?.isEmpty() == true) return@forEach

                        if (subCatName == prodBean.sub_cat_name) {
                            prodBean.is_SubCat_visible = false
                        }
                        subCatName = prodBean.sub_cat_name


                        if (prodBean.value?.isNotEmpty() == true) {
                            productBeans.add(prodBean.copy())
                        }
                    }
                } else {
                    if (productBeans.any { it.sub_cat_name == prod.sub_cat_name }) {
                        prod.is_SubCat_visible = false
                        productBeans.add(prod.copy())
                    } else {
                        productBeans.add(prod.copy())
                    }
                }
            }

            adapter?.addList(productBeans)
        }
    }

    private fun changeProductList(product: MutableList<ProductDataBean>?) {
        for (j in product?.indices!!) {
            val dataProduct = product[j]
            val productId = dataProduct.product_id

            dataProduct.prod_quantity = StaticFunction.getCartQuantity(activity, productId)
            dataProduct.isSelected= dataProduct.prod_quantity?:0f>0f
            dataProduct.self_pickup = mDeliveyType

            //for fixed price
            dataProduct.let {
                prodUtils.changeProductList(true, dataProduct)
            }
        }
    }

    companion object {
        fun newInstance() = JumaProductsFragment()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_juma_products
    }


    override fun getViewModel(): RestDetailViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(RestDetailViewModel::class.java)
        return viewModel
    }

    override fun favResponse() {

    }

    override fun unFavResponse() {
    }


    override fun onTableSuccessfullyBooked() {
    }


    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onProceedButtonClicked(list: List<ProductDataBean>?) {
        val productList: ArrayList<ProductDataBean>? = arrayListOf()
        productList?.addAll(list ?: emptyList())
        appUtils.clearCart()

        for (i in productList?.indices!!) {
            val dataProduct = productList?.get(i)
            // if vendor status ==0 single vendor && vendor status==1 multiple vendor
            if (bookingFlowBean?.vendor_status == 0 && appUtils.checkVendorStatus(dataProduct?.supplier_id,
                            vendorBranchId = dataProduct?.supplier_branch_id, branchFlow = settingData?.branch_flow)) {

                mDialogsUtil.openAlertDialog(activity
                        ?: requireContext(), getString(R.string.clearCart, textConfig?.supplier
                        ?: "", textConfig?.proceed ?: ""), "Yes", "No", this)
            } else {
                addNewData(dataProduct)
            }
        }

        if (settingData?.show_ecom_v2_theme == "1") {
            navController(this@JumaProductsFragment).navigate(R.id.action_fragment_to_cartV2)
        } else {
            navController(this@JumaProductsFragment).navigate(R.id.action_fragment_to_cart)
        }
    }

    override fun onProductClicked(index: Int) {

    }

    private fun addNewData(productData: ProductDataBean?) {

        if (appUtils.checkBookingFlow(requireContext(), productData?.product_id
                        ?: 0, this)) {
            val addInDb = settingData?.enable_freelancer_flow != "1"


            productData?.type = screenFlowBean?.app_type
            prodUtils.addItemToCart(productData, addInDb)
        }
    }


    override fun onSucessListner() {
        appUtils.clearCart()
        productBeans.map {
            it.value?.map { it1 ->
                it1.prod_quantity = 0f
                it1.isSelected=false
            }

        }

        adapter?.notifyDataSetChanged()


    }

    override fun onErrorListener() {
    }


}
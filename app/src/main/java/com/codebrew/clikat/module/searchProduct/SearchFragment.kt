package com.codebrew.clikat.module.searchProduct

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.bumptech.glide.Glide
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.DialogsUtil
import com.codebrew.clikat.app_utils.extension.afterTextChanged
import com.codebrew.clikat.app_utils.extension.afterTextChangedDelayed
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.*
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.others.FilterInputModel
import com.codebrew.clikat.databinding.FragmentCompareProdutcsBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.database.SearchCatListModel
import com.codebrew.clikat.modal.database.SearchCategoryModel
import com.codebrew.clikat.modal.other.*
import com.codebrew.clikat.modal.other.SettingModel.DataBean.BookingFlowBean
import com.codebrew.clikat.modal.other.SettingModel.DataBean.ScreenFlowBean
import com.codebrew.clikat.module.filter.CraveFilterBottomSheet
import com.codebrew.clikat.module.home_screen.listeners.OnSortByListenerClicked
import com.codebrew.clikat.module.product.product_listing.DialogListener
import com.codebrew.clikat.module.product.product_listing.adapter.ProductListingAdapter.ProductCallback
import com.codebrew.clikat.module.product_addon.AddonFragment
import com.codebrew.clikat.module.product_detail.ProductDetails
import com.codebrew.clikat.module.searchProduct.adapter.ResturantListAdapter
import com.codebrew.clikat.module.searchProduct.adapter.ResturantListener
import com.codebrew.clikat.module.searchProduct.adapter.SuggestionListAdapter
import com.codebrew.clikat.module.searchProduct.adapter.SuggestionListAdapter.Searchcallback
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.preferences.Prefs
import com.codebrew.clikat.utils.StaticFunction.getLanguage
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import io.realm.OrderedRealmCollection
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_compare_produtcs.*
import kotlinx.android.synthetic.main.layout_bottom_cart.*
import kotlinx.android.synthetic.main.nothing_found.*
import kotlinx.android.synthetic.main.toolbar_app.*
import javax.inject.Inject

/*
 * Created by cbl80 on 8/7/16.
 */
class SearchFragment : BaseFragment<FragmentCompareProdutcsBinding, SearchViewModel>(),
        Searchcallback, ProductCallback, DialogListener, View.OnClickListener,
        SearchNavigator, AddonFragment.AddonCallback, AdapterView.OnItemSelectedListener, OnSortByListenerClicked {


    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var appUtils: AppUtils

    private lateinit var mBinding: FragmentCompareProdutcsBinding

    var self_Pickup=""

    private lateinit var viewModel: SearchViewModel
    private var selectedCurrency: Currency? = null

    private var resturantListAdapter: ResturantListAdapter? = null
    private var suggestionListAdapter: SuggestionListAdapter? = null
    private var varientData: FilterVarientData? = null
    private val selectedColor by lazy { Color.parseColor(Configurations.colors.tabSelected) }
    private val unselectedColor by lazy { Color.parseColor(Configurations.colors.tabUnSelected) }
    private var realm: Realm? = null
    private var bookingFlowBean: BookingFlowBean? = null
    private var screenFlowBean: ScreenFlowBean? = null
    private var clientInform: SettingModel.DataBean.SettingData? = null
    private var catId = 0
    private var mSearchType = ""

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var mDialogsUtil: DialogsUtil
    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }

    private val colorConfig by lazy { Configurations.colors }
    private var inputModel: FilterInputModel = FilterInputModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        viewModel.navigator = this

        supplierObserver()
        self_Pickup = if (dataManager.getKeyValue(AppConstants.ROAD_MAP_TYPE, PrefenceConstants.TYPE_STRING)!!.equals("Delivery")) {
            "8"
        } else {
            "9"
        }
        catId = if (dataManager.getKeyValue(PrefenceConstants.CATEGORY_ID, PrefenceConstants.TYPE_STRING).toString().isNotEmpty()) {
            dataManager.getKeyValue(PrefenceConstants.CATEGORY_ID, PrefenceConstants.TYPE_STRING).toString().toInt()
        } else {
            0
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding
        mBinding.color = colorConfig
        mBinding.drawables = Configurations.drawables
        mBinding.strings = textConfig

        Glide.with(this).asGif().load(R.raw.loading_gif).into(mBinding!!.imageLoader);
        screenFlowBean = dataManager.getGsonValue(DataNames.SCREEN_FLOW, ScreenFlowBean::class.java)
        bookingFlowBean = dataManager.getGsonValue(DataNames.BOOKING_FLOW, BookingFlowBean::class.java)
        clientInform = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        selectedCurrency = dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)

        spinner_view.visibility = if (clientInform?.search_by == "2") {
            // View.VISIBLE
            View.GONE
        } else {
            View.GONE
        }
        searchView?.hint = textConfig?.what_are_u_looking_for

        searchView?.setTextColor(ContextCompat.getColor(requireActivity(), android.R.color.darker_gray))
        searchView?.setHintTextColor(ContextCompat.getColor(requireActivity(), android.R.color.darker_gray))


        setData()
        clickListner()

        searchView.afterTextChanged {
            if (it.trim().isNotEmpty()) {
                iv_search.isEnabled = true
                viewModel.getSupplierList(searchView.text.toString().trim(), categoryId = catId.toString(), self_pickup = self_Pickup)

                searchView.afterTextChangedDelayed {
                    realm?.executeTransaction { realm: Realm? -> SearchCategoryModel.create(realm,it.trim()) }
                }
            } else {
                varientData = null
                iv_search.isEnabled = false
                fetchpreviousResult()
            }
        }



        searchView.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // Your piece of code on keyboard search click
                if (searchView.text.trim().isNotEmpty())
                    iv_search.callOnClick()
                true
            } else false
        }


        tb_title.text = getString(R.string.search, appUtils.loadAppConfig(0).strings?.product)
        tb_back.setOnClickListener {
            hideKeyboard()
            Navigation.findNavController(requireView()).popBackStack()
        }

        showBottomCart()

        onRecyclerViewScrolled()

        if (arguments != null && arguments?.containsKey("searchText") == true) {
            searchView?.setText(arguments?.getString("searchText"))
            if (searchView?.text.toString().trim().isNotEmpty())
                iv_search?.callOnClick()
        }


        if(viewModel.supplierLiveData.value!=null)
        {
            updateSupplierData(viewModel.supplierLiveData.value)
        }
    }


    private fun supplierObserver() {
        // Create the observer which updates the UI.
        val supplierObserver = Observer<List<SupplierDataBean>> { resource ->
            resource.let {
                updateSupplierData(it)
            }
        }
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.supplierLiveData.observe(this, supplierObserver)
    }

    private fun updateSupplierData(resource: List<SupplierDataBean>?) {
        mBinding.searchHistory = resource?.count() ?: 0 == 0

        checkAdapter(resturantListAdapter, false)

        resturantListAdapter?.notifyDataSetChanged()
        viewModel.setIsSearchHist(true)
        mBinding.executePendingBindings()
        AppConstants.SEARCH_OPTION = SearchType.TYPE_RESTU.type
        resturantListAdapter?.submitMessageList(resource)
    }


    private fun clickListner() {
        iv_search.setOnClickListener(this)
        iv_grid_view.setOnClickListener(this)
        tv_filter.setOnClickListener(this)
        iv_list_view.setOnClickListener(this)

        if (clientInform?.show_ecom_v2_theme == "1") {
            iv_grid_view.visibility = View.INVISIBLE
            iv_list_view.visibility = View.INVISIBLE
            tv_view_product.visibility = View.INVISIBLE
        }
        if (clientInform?.is_skip_theme == "1") {
            tvTitleOoPs?.visibility = View.VISIBLE
            ivPlaceholder?.setImageResource(R.drawable.ic_graphic)
            tvText?.text = getString(R.string.nothing_found_try_again)
            tvText?.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        }
    }

    private fun fetchpreviousResult() {
        viewModel.setIsSearchHist(true)
        AppConstants.SEARCH_OPTION = SearchType.TYPE_PROD.type
        viewModel.setIsList(realm?.where(SearchCatListModel::class.java)?.findFirst()?.itemList?.size?.plus(1)
                ?: 1)
        if (suggestionListAdapter == null || recyclerview?.adapter !== suggestionListAdapter) {

            suggestionListAdapter = SuggestionListAdapter(realm?.where(SearchCatListModel::class.java)?.findFirst()?.itemList)
            suggestionListAdapter?.settingcallback(this)
            checkAdapter(suggestionListAdapter, false)
        } else suggestionListAdapter?.notifyDataSetChanged()
        hideKeyboard()
    }



    private fun setData() {
        val screenFlowBean = Prefs.with(activity).getObject(DataNames.SCREEN_FLOW, ScreenFlowBean::class.java)
        iv_list_view.setColorFilter(unselectedColor, PorterDuff.Mode.MULTIPLY)
        iv_grid_view.setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY)
        // Create the Realm instance
        realm = Realm.getDefaultInstance()

        resturantListAdapter = ResturantListAdapter(ResturantListener {

            val bundle = bundleOf("supplierId" to it.id,
                    "branchId" to it.supplier_branch_id,
                    "deliveryType" to Utils.getDeliveryType(it.self_pickup ?: 0),
                    "categoryId" to it.category_id,
                "supplierTime" to "0-" + it?.delivery_max_time.toString() + getString(R.string.min),
                    "supplierBannerImage" to it?.supplier_image,
            "supplierLogo" to it?.logo,
                    "supplierName" to it.name)
if(self_Pickup.equals("8")) {
    if (clientInform?.show_supplier_detail != null && clientInform?.show_supplier_detail == "1") {

        navController(this@SearchFragment).navigate(R.id.action_supplierDetail, bundle)
    } else if (clientInform?.app_selected_template != null && clientInform?.app_selected_template == "1") {
        navController(this@SearchFragment)
                .navigate(R.id.action_searchFragment_to_restaurantDetailNewFrag, bundle)
    } else {
        navController(this@SearchFragment)
                .navigate(R.id.action_searchFragment_to_restaurantDetailFrag, bundle)
    }
}else{
    val bundle = bundleOf("title" to it?.name,
            "supplierId" to (it?.id ?: 0),
            "subCategoryId" to 0)
    navController(this@SearchFragment).navigate(R.id.branchListFragment, bundle)

}
        }, appUtils,dataManager.getLangCode())

        mBinding.searchHistory = (viewModel.productCount.get() == 0 && viewModel.supplierCount.get() == 0)

        if (suggestionListAdapter == null) {
            fetchpreviousResult()
        } else {
            viewModel.setIsSearchHist(false)
            // AppConstants.SEARCH_OPTION = SearchType.TYPE_PROD.type
            recyclerview.layoutManager = setLayoutManager(clientInform?.show_ecom_v2_theme != "1")
        }


        val mList = arrayListOf<String>()

        mList.add(appUtils.loadAppConfig(0).strings?.supplier ?: "")


        ArrayAdapter(
                activity ?: requireContext(), android.R.layout.simple_spinner_item,
                mList
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_search.adapter = adapter
        }

        spinner_search.onItemSelectedListener = this

    }

    override fun onDestroyView() {
        super.onDestroyView()
        realm?.close()
    }

    override fun getSearch(keyword: String) {
        searchView?.setText(keyword)

        if (!isNetworkConnected) return
        viewModel.getSupplierList(searchView.text.toString().trim(), categoryId = catId.toString(), self_pickup = self_Pickup)

    }

    override fun clearHistory() {
        realm?.executeTransactionAsync { realm: Realm ->
            // realm.deleteAll();
            val data: OrderedRealmCollection<SearchCategoryModel>? = realm.where(SearchCatListModel::class.java).findFirst()!!.itemList
            // Otherwise it has been deleted already.
            data?.deleteAllFromRealm()
        }
        fetchpreviousResult()
    }


    private fun setLayoutManager(type: Boolean): LayoutManager {
        val clientInform = Prefs.with(activity).getObject(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        return if (clientInform?.is_carveQatar_home_theme == "1") {
            if (type) GridLayoutManager(activity, 1) else LinearLayoutManager(activity)
        } else {
            if (type) GridLayoutManager(activity, 2) else LinearLayoutManager(activity)
        }
    }


    override fun addToCart(position: Int, agentType: Boolean) {


    }


    override fun removeToCart(position: Int) {

    }

    override fun productDetail(bean: ProductDataBean?) {

    }


    override fun publishResult(count: Int) {

        viewModel.setIsList(count)
    }

    override fun addtoWishList(adapterPosition: Int, status: Int?, productId: Int?) {


    }

    override fun bookNow(adapterPosition: Int, bean: ProductDataBean) {
        //do nothing
    }

    override fun onSucessListner() {

    }

    override fun onErrorListener() {}

    companion object {
        private const val FILTERDATA = 788
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.iv_search -> {
                if (searchView.text.toString().isEmpty()) return
                //  tvTitle.text = searchView.text.toString()
                hideKeyboard()
                getSearch(searchView.text.toString().trim())
                realm?.executeTransaction { realm: Realm? -> SearchCategoryModel.create(realm, searchView?.text.toString().trim()) }

            }


            R.id.tv_filter -> {
                ///  productAdapter.settingLayout(false);
                //  recyclerview.setAdapter(productAdapter);
                if (mSearchType == "resturant") {
                    if (searchView.text.toString().isEmpty()) return

                    hideKeyboard()
                    openPrepTimeDialog()
                } else {
                    val bottomSheetFragment = CraveFilterBottomSheet()
                    /*  val bundle = Bundle()
                      if (varientData != null) {
                          bundle.putParcelable("varientData", varientData)
                      }
                      bundle.putString("product_name", searchView!!.text.toString())
                      bottomSheetFragment.arguments = bundle*/
                    bottomSheetFragment.show(childFragmentManager, "bottom_sheet")
                }
            }
        }
    }

    private fun openPrepTimeDialog() {
        appUtils.showPrepTimeDialog(requireContext(), this)
    }

    override fun onPrepTimeSelected(filters: FiltersSupplierList) {
        if (isNetworkConnected)
            viewModel.getSupplierList(searchView.text.toString().trim(),
                    categoryId = catId.toString(), self_pickup = self_Pickup)
    }


    private fun showBottomCart() {

        val appCartModel = appUtils.getCartData(clientInform, selectedCurrency)

        bottom_cart.visibility = View.VISIBLE

        if (appCartModel.cartAvail) {

            tv_total_product.text = getString(R.string.total_item_tag, Utils.getDecimalPointValue(clientInform, appCartModel.totalCount))
            tv_total_price.text = getString(R.string.total).plus(" ").plus(AppConstants.CURRENCY_SYMBOL).plus(" ").plus(appCartModel.totalPrice)


            if (appCartModel.supplierName.isNotEmpty() && screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {
                tv_supplier_name.visibility = View.VISIBLE
                tv_supplier_name.text = getString(R.string.supplier_tag, appCartModel.supplierName)
            } else {
                tv_supplier_name.visibility = View.GONE
            }

            bottom_cart.setOnClickListener {
                if (clientInform?.show_ecom_v2_theme == "1") {
                    navController(this@SearchFragment).navigate(R.id.action_searchFragment_to_cartV2)
                } else {
                    navController(this@SearchFragment).navigate(R.id.action_searchFragment_to_cart)
                }
            }
        } else {
            bottom_cart.visibility = View.GONE
        }

    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_compare_produtcs
    }

    override fun getViewModel(): SearchViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(SearchViewModel::class.java)
        return viewModel
    }

    override fun onFavStatus() {

    }

    override fun onErrorOccur(message: String) {
        mBinding.root.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onAddonAdded(productModel: ProductDataBean) {

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {


    }

    private fun checkAdapter(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>?, viewType: Boolean, isAdapterRefresh: Boolean = false) {
        if (recyclerview.adapter !== adapter || isAdapterRefresh) {
            recyclerview.layoutManager = setLayoutManager(viewType)
            recyclerview.adapter = adapter
        }
    }


    private fun onRecyclerViewScrolled() {
    }

    override fun onItemSelected(type: Int) {

    }


}
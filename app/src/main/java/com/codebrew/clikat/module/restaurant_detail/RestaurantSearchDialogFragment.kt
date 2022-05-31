package com.codebrew.clikat.module.restaurant_detail

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.*
import com.codebrew.clikat.app_utils.extension.afterTextChanged
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.base.EmptyListListener
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.FoodAppType
import com.codebrew.clikat.data.VendorAppType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.orderDetail.OrderHistory
import com.codebrew.clikat.data.model.others.EditProductsItem
import com.codebrew.clikat.databinding.FragmentRestaurantDetailNewBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.CartList
import com.codebrew.clikat.modal.LocationUser
import com.codebrew.clikat.modal.other.*
import com.codebrew.clikat.module.addon_quant.SavedAddon
import com.codebrew.clikat.module.product.product_listing.DialogListener
import com.codebrew.clikat.module.product_addon.AddonFragment
import com.codebrew.clikat.module.product_detail.ProductDetails
import com.codebrew.clikat.module.restaurant_detail.adapter.ProdListAdapter
import com.codebrew.clikat.module.restaurant_detail.adapter.SupplierProdListAdapter
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.DialogIntrface
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_restaurant_detail_new.*
import kotlinx.android.synthetic.main.fragment_restaurant_detail_new.bottom_cart
import kotlinx.android.synthetic.main.fragment_restaurant_detail_new.mainmenu
import kotlinx.android.synthetic.main.fragment_restaurant_detail_new.noData
import kotlinx.android.synthetic.main.fragment_restaurant_detail_new.rvproductList
import kotlinx.android.synthetic.main.layout_bottom_cart.*
import kotlinx.android.synthetic.main.search_dialog_fragment.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class RestaurantSearchDialogFragment : BaseFragment<FragmentRestaurantDetailNewBinding, RestDetailViewModel>(),
        ProdListAdapter.ProdCallback, DialogListener,
        RestDetailNavigator, AddonFragment.AddonCallback, DialogIntrface,EmptyListListener {


    private var selectedCurrency: Currency?=null

    private var parentPos: Int = 0
    private var childPos: Int = 0
    private var adapterSearch: SupplierProdListAdapter? = null

    private var productBeans = mutableListOf<ProductBean>()
    private var bookingFlowBean: SettingModel.DataBean.BookingFlowBean? = null
    private var prodlytManager: LinearLayoutManager? = null
    private var deliveryType = 0
    private var supplierId = 0
    private var supplierBranchId=0
    private var isResutantOpen: Boolean = false

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var mDialogsUtil: DialogsUtil

    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null

    private var mBinding: FragmentRestaurantDetailNewBinding? = null
    var settingBean: SettingModel.DataBean.SettingData? = null

    private lateinit var viewModel: RestDetailViewModel
    private var selectedProductsList: ArrayList<EditProductsItem>? = arrayListOf()
    private var orderDetail: OrderHistory? = null
    private val decimalFormat: DecimalFormat = DecimalFormat("0.00")
    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_restaurant_detail_new
    }

    override fun getViewModel(): RestDetailViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(RestDetailViewModel::class.java)
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this
        settingBean = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        screenFlowBean = dataManager.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        selectedCurrency = dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
        restDetailObserver()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding

        viewDataBinding.color = Configurations.colors
        viewDataBinding.drawables = Configurations.drawables
        viewDataBinding.strings = Configurations.strings

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mainmenu?.foreground?.alpha = 0
        }
        initialise()
        getValues()
        settingLayout()
        setclikListener()

        if (orderDetail == null || orderDetail?.editOrder == false)
            showBottomCart()
        else
            bottom_cart.visibility = View.GONE


        onRecyclerViewScrolled()
    }

    private fun initialise() {
        etSearch?.hint=textConfig?.what_are_u_looking_for
        layoutSearchView?.visibility = View.VISIBLE

    }

    private fun settingLayout() {

        productBeans.clear()

        adapterSearch = SupplierProdListAdapter(this@RestaurantSearchDialogFragment, settingBean, appUtils, selectedCurrency, this, "old")
        rvSearch?.adapter = adapterSearch

        if (arguments != null) {
            if (arguments?.containsKey("deliveryType") == true) {
                deliveryType = arguments?.getInt("deliveryType") ?: 0
            }

            if (arguments?.containsKey("supplierId") == true) {
                supplierId = arguments?.getInt("supplierId") ?: 0
                if (arguments?.containsKey("branchId") == true) {
                    supplierBranchId = arguments?.getInt("branchId") ?: 0
                }
                getProductList(supplierId,true,supplierBranchId)
            }
            if (arguments?.containsKey("selectedList") == true)
                selectedProductsList = arguments?.getParcelableArrayList("selectedList")

            if (arguments?.containsKey("orderItem") == true) {
                orderDetail = arguments?.getParcelable("orderItem")
                btnEdit?.visibility = View.VISIBLE
            }
        }

    }

    private fun getProductList(supplierId: Int, isFirstPage: Boolean,supplierBranchId:Int) {
        if (isNetworkConnected)
            viewModel.getProductList(supplierId.toString(), isFirstPage, settingBean,null)
    }

    private fun setclikListener() {

        iv_close?.setOnClickListener {
            hideKeyboard()
            navController(this@RestaurantSearchDialogFragment).popBackStack()
        }

        etSearch?.afterTextChanged {
            if (it.isNotEmpty()) {
                adapterSearch?.filter?.filter(it.toLowerCase())
            } else {
                adapterSearch?.filter?.filter("")
            }
        }
    }

    private fun restDetailObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<PagingResult<SuplierProdListModel>> { resource ->
            /*productBeans.clear()*/
            isResutantOpen = checkResturntTiming(resource?.result?.data?.supplier_detail?.timing)
            refreshAdapter(resource?.result?.data)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.supplierLiveData.observe(this, catObserver)
    }

    private fun refreshAdapter(data: DataBean?) {
        viewDataBinding.supplierModel = data?.supplier_detail

        val previousCount=productBeans.count()

        noDataVisibility(data?.product?.count()==0)

        if (data?.product?.isNotEmpty() == true) {
         //   productBeans.clear()
            productBeans.addAll(data.product ?: emptyList())
            for (i in productBeans.indices) {
                if (orderDetail?.editOrder == true) {
                    productBeans[i].value = changeProductList(productBeans[i].value
                            ?: mutableListOf())
                } else
                    changeProductList(productBeans[i].value!!)
            }
            adapterSearch?.settingList(productBeans)
            adapterSearch?.filter?.filter("")
        }
    }

    private fun changeProductList(product: List<ProductDataBean>) {

        for (j in product.indices) {
            val dataProduct = product[j]
            val productId = dataProduct.product_id

            product[j].prod_quantity = StaticFunction.getCartQuantity(requireActivity(), productId)

            dataProduct.netPrice = if (dataProduct.fixed_price?.toFloatOrNull() ?: 0.0f > 0) dataProduct.fixed_price?.toFloatOrNull() else 0f
        }
    }

    private fun changeProductList(product: MutableList<ProductDataBean>): ArrayList<ProductDataBean> {
        val values = ArrayList<ProductDataBean>()
        for (j in 0 until product.size) {
            val dataProduct = product[j]
            dataProduct.netPrice =
                    if (dataProduct.fixed_price?.toFloatOrNull() ?: 0.0f > 0) dataProduct.fixed_price?.toFloatOrNull() else 0f

            val index = orderDetail?.product?.indexOfFirst {
                it?.product_id == dataProduct.product_id
            }

            if (index == null || index == -1) {
                val selectedItemPos = selectedProductsList?.indexOfFirst { it.productId == dataProduct.product_id }
                if (selectedItemPos != null && selectedItemPos != -1)
                    dataProduct.prod_quantity = selectedProductsList?.get(selectedItemPos)?.quantity

                values.add(dataProduct)
            }

        }
        return values
    }

    private fun showBottomCart() {

        val appCartModel = appUtils.getCartData(settingBean,selectedCurrency)

        if (screenFlowBean?.app_type == AppDataType.Ecom.type) {
            bottom_cart?.visibility = View.GONE
        } else {
            bottom_cart?.visibility = View.VISIBLE
        }

        if (appCartModel.cartAvail) {

            tv_total_price.text = getString(R.string.total).plus(" ").plus(AppConstants.CURRENCY_SYMBOL).plus(" ").plus(appCartModel.totalPrice)
            tv_total_product.text =getString(R.string.total_item_tag, Utils.getDecimalPointValue(settingBean, appCartModel.totalCount))


            if (appCartModel.supplierName.isNotEmpty() && screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {
                tv_supplier_name.visibility = View.VISIBLE
                tv_supplier_name.text = getString(R.string.supplier_tag, appCartModel.supplierName)
            } else {
                tv_supplier_name.visibility = View.GONE
            }

            bottom_cart.setOnClickListener {
                val navOptions: NavOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.restaurantDetailFragNew, false)
                        .build()

                navController(this@RestaurantSearchDialogFragment).navigate(R.id.action_restaurantDetailFrag_to_cart, null, navOptions)
            }
        } else {
            bottom_cart.visibility = View.GONE
        }

    }

    private fun getValues() {
        bookingFlowBean = dataManager.getGsonValue(DataNames.BOOKING_FLOW, SettingModel.DataBean.BookingFlowBean::class.java)
    }

    override fun onEditQuantity(quantity: Float, productBean: ProductDataBean?, parentPosition: Int, childPosition: Int, isOpen: Boolean) {
        hideKeyboard()
        if (settingBean?.is_decimal_quantity_allowed == "1") {
            checkEditQuantity(quantity, productBean, parentPosition, childPosition, isOpen)
        }
    }

    override fun onProdAllergiesClicked(bean: ProductDataBean) {

    }

    private fun checkEditQuantity(updatedQuantity: Float?, productBean: ProductDataBean?, parentPosition: Int, childPosition: Int, isOpen: Boolean) {
        if (isOpen) {
            mDialogsUtil.openAlertDialog(activity
                    ?: requireContext(), getString(R.string.offline_supplier_tag, Configurations.strings.suppliers), getString(R.string.ok), "", this)
            return
        }

        this.parentPos = parentPosition
        this.childPos = childPosition
        if (orderDetail != null && orderDetail?.editOrder == true) {
            productBean?.prod_quantity = productBean?.prod_quantity?.plus(1)
            val index =
                    selectedProductsList?.indexOfFirst { it.productId == productBean?.product_id }
            if (index != null && index != -1)
                selectedProductsList?.get(index)?.quantity = productBean?.prod_quantity
            else
                addInSelectedList(productBean)

            adapterSearch?.notifyItemChanged(parentPosition)

        } else {
            if (deliveryType == FoodAppType.Pickup.foodType) {
                if (viewModel.supplierLiveData.value == null) return

                val mRestUser = LocationUser()
                mRestUser.address = "${viewModel.supplierLiveData.value?.result?.data?.supplier_detail?.name} , ${viewModel.supplierLiveData.value?.result?.data?.supplier_detail?.address}"
                dataManager.addGsonValue(PrefenceConstants.RESTAURANT_INF, Gson().toJson(mRestUser))
            }

            if (screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {
                val cartList: CartList? = dataManager.getGsonValue(DataNames.CART, CartList::class.java)

                if (cartList != null && cartList.cartInfos?.isNotEmpty() == true) {
                    if (cartList.cartInfos?.any { it.deliveryType != deliveryType } == true) {
                        appUtils.clearCart()
                        refreshAdapter(viewModel.supplierLiveData.value?.result?.data)
                    }
                }
            }

            if (productBean?.adds_on?.isNotEmpty() == true) {
                val cartList: CartList? = dataManager.getGsonValue(DataNames.CART, CartList::class.java)

                if (appUtils.checkProdExistance(productBean.product_id)) {
                    val savedProduct = cartList?.cartInfos?.filter {
                        it.supplierId == supplierId && it.productId == productBean.product_id
                    } ?: emptyList()

                    SavedAddon.newInstance(productBean, deliveryType, savedProduct, this).show(childFragmentManager, "savedAddon")
                } else {
                    AddonFragment.newInstance(productBean, deliveryType, this).show(childFragmentManager, "addOn")
                }

            } else {
                if (bookingFlowBean?.vendor_status == 0 && appUtils.checkVendorStatus(productBean?.supplier_id
                                , vendorBranchId = productBean?.supplier_branch_id, branchFlow = settingBean?.branch_flow)) {
                    mDialogsUtil.openAlertDialog(activity
                            ?: requireContext(), getString(R.string.clearCart, Configurations.strings.supplier
                            ?: "",Configurations.strings?.proceed ?: ""), "Yes", "No", this)
                } else {
                    if (appUtils.checkBookingFlow(requireContext(), productBean?.product_id, this)) {
                        addCart(updatedQuantity, productBean)
                    }
                }
                showBottomCart()
            }
        }
    }

    override fun onProdAdded(productBean: ProductDataBean?, parentPosition: Int, childPosition: Int, isOpen: Boolean) {
        checkEditQuantity(null, productBean, parentPosition, childPosition, isOpen)
    }

    private fun addInSelectedList(productDataBean: ProductDataBean?) {
        val item = EditProductsItem()
        item.productId = productDataBean?.product_id
        item.branchId = productDataBean?.supplier_branch_id
        item.imagePath = productDataBean?.image_path.toString()
        item.productDesc = productDataBean?.product_desc
        item.quantity = productDataBean?.prod_quantity
        item.price = productDataBean?.price?.toFloat()
        item.productName = productDataBean?.name
        item.handling_admin = productDataBean?.handling_admin?.toDouble()
        selectedProductsList?.add(item)
    }

    override fun onProdDelete(productBean: ProductDataBean?, parentPosition: Int, childPosition: Int, isOpen: Boolean) {
        this.parentPos = parentPosition
        this.childPos = childPosition

        if (parentPosition == -1 && childPosition == -1  && productBean?.prod_quantity ?: 0f<0) return

        if (orderDetail != null && orderDetail?.editOrder == true) {

            productBean?.prod_quantity = productBean?.prod_quantity?.minus(1)
            val index =
                    selectedProductsList?.indexOfFirst { it.productId == productBean?.product_id }
            if (index != null && index != -1)
                removeFromSelectedList(productBean, index)

            adapterSearch?.notifyItemChanged(parentPosition)

        } else {
            if (productBean?.adds_on?.isNotEmpty() == true) {
                val cartList: CartList? = dataManager.getGsonValue(DataNames.CART, CartList::class.java)

                val savedProduct = cartList?.cartInfos?.filter {
                    it.supplierId == supplierId && it.productId == productBean.product_id
                } ?: emptyList()

                SavedAddon.newInstance(productBean, deliveryType, savedProduct, this).show(childFragmentManager, "savedAddon")

            } else {

                var quantity = productBean?.prod_quantity ?: 0f

                if (quantity > 0f) {
                    quantity--
                    productBean?.prod_quantity = quantity

                    if (quantity == 0f) {
                        StaticFunction.removeFromCart(activity, productBean?.product_id, 0)

                    } else {
                        StaticFunction.updateCart(activity, productBean?.product_id, quantity, productBean?.price?.toFloat()
                                ?: 0.0f)
                    }
                    productBean?.let { productBeans[parentPosition].value?.set(childPosition, it) }
                    if (adapterSearch != null) {
                        adapterSearch?.notifyDataSetChanged()
                    }

                }

                showBottomCart()
            }
        }

    }

    private fun removeFromSelectedList(
            productBean: ProductDataBean?,
            index: Int
    ) {
        if (productBean?.prod_quantity != 0f)
            selectedProductsList?.get(index)?.quantity = productBean?.prod_quantity
        else
            selectedProductsList?.removeAt(index)
    }

    override fun onProdDetail(productBean: ProductDataBean?) {

        if (settingBean?.isProductDetailCheck != null && settingBean?.isProductDetailCheck == "1") return

        navController(this).navigate(R.id.action_search_to_product_details,
                ProductDetails.newInstance(productBean, 0, false, isResutantOpen, orderDetail?.editOrder
                        ?: false))
    }

    override fun onDescExpand(tvDesc: TextView?, productBean: ProductDataBean?, childPosition: Int) {
        CommonUtils.expandTextView(tvDesc)
    }

    override fun onProdDesc(productDesc: String) {
        StaticFunction.sweetDialogueSuccess11(activity, getString(R.string.description), productDesc, false, 1002
                , this)
    }

    override fun onProdDialog(bean: ProductDataBean?) {
        mDialogsUtil.showProductDialog(requireContext(), bean)
    }


    private fun addCart(updatedQuantity: Float?, productModel: ProductDataBean?) {

        if (screenFlowBean?.app_type == AppDataType.Food.type) {
            val cartList: CartList? = dataManager.getGsonValue(DataNames.CART, CartList::class.java)

            if (cartList != null && cartList.cartInfos?.size!! > 0) {
                if (cartList.cartInfos?.any { cartList.cartInfos?.get(0)?.deliveryType != it.deliveryType } == true) {
                    appUtils.clearCart()

                    mBinding?.root?.onSnackbar("Cart Clear Successfully")
                }
            }
        }

        if (productModel?.prod_quantity == 0f) {
            if (settingBean?.is_decimal_quantity_allowed == "1")
                productModel.prod_quantity = AppConstants.DECIMAL_INTERVAL
            else
                productModel.prod_quantity = 1f

            productModel.self_pickup = deliveryType

            appUtils.addProductDb(activity ?: requireContext(), screenFlowBean?.app_type
                    ?: 0, productModel)


        } else {
            var quantity: Float
            if (updatedQuantity == null) {
                quantity = productModel?.prod_quantity ?: 0f

                if (settingBean?.is_decimal_quantity_allowed == "1")
                    quantity = decimalFormat.format((quantity + AppConstants.DECIMAL_INTERVAL)).toFloat()
                else
                    quantity++
            } else
                quantity = updatedQuantity


            val remaingProd = productModel?.quantity?.minus(productModel.purchased_quantity ?: 0f)
                    ?: 0f

            if (quantity <= remaingProd) {
                productModel?.prod_quantity = quantity

                StaticFunction.updateCart(activity, productModel?.product_id, quantity, productModel?.netPrice
                        ?: 0.0f)
            } else {
                mBinding?.root?.onSnackbar(getString(R.string.maximum_limit_cart))
            }
        }


        productBeans.mapIndexed { index, productBean ->

            if (productBean.sub_cat_name == productModel?.sub_category_name) {
                productModel?.let {
                    productBean.value?.set(productBean.value?.indexOf(productModel) ?: 0, it)
                }
            }

        }




        if (adapterSearch != null) {
            adapterSearch?.notifyDataSetChanged()
        }

    }

    override fun onSuccessListener() {

    }


    override fun onErrorListener() {

    }

    private fun clearCart() {

        productBeans.map {
            it.value?.map {
                it.prod_quantity = 0f
            }
        }

        /*     for (i in productBeans.indices) {

                 productBeans[i].value?.mapIndexed { index, valueBean ->
                     productBeans[i].value?.get(index)?.prod_quantity = 0
                 }
             }*/


        if (adapterSearch != null)
            adapterSearch?.notifyDataSetChanged()

        showBottomCart()
    }

    override fun onSucessListner() {

        if (isResutantOpen) {
            appUtils.clearCart()
            clearCart()
        }
    }

    override fun onAddonAdded(productModel: ProductDataBean) {

        if (productModel.adds_on?.isNullOrEmpty() == false && appUtils.checkProdExistance(productModel.product_id)) {
            val cartList: CartList? = appUtils.getCartList()
            productModel.prod_quantity = (cartList?.cartInfos?.filter { productModel.product_id == it.productId }?.sumByDouble { it.quantity.toDouble() }
                    ?: 0.0).toFloat()
        }

        showBottomCart()


        productBeans.mapIndexed { index, productBean ->

            if (productBean.sub_cat_name == productModel.sub_category_name) {
                productModel.let {
                    productBean.value?.set(productBean.value?.indexOf(productModel) ?: 0, it)
                }
            }

        }

        if (adapterSearch != null) {
            adapterSearch?.notifyDataSetChanged()
        }
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

    private fun checkResturntTiming(timing: List<TimeDataBean>?): Boolean {
        // val calendar = Calendar.getInstance()
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        val currentDate = Calendar.getInstance()

        var isCheckStatus = false

        val sdf = SimpleDateFormat("HH:mm:ss", DateTimeUtils.timeLocale)
        timing?.forEachIndexed { index, timeDataBean ->
            if (timeDataBean.week_id == (appUtils.getDayId(currentDate.get(Calendar.DAY_OF_WEEK))
                            ?: "-1").toInt()) {
                isCheckStatus = timeDataBean.is_open == 1

                startDate.time = sdf.parse(timeDataBean.start_time ?: "")!!
                startDate.set(Calendar.YEAR, currentDate.get(Calendar.YEAR))
                startDate.set(Calendar.MONTH, currentDate.get(Calendar.MONTH))
                startDate.set(Calendar.DAY_OF_MONTH, currentDate.get(Calendar.DAY_OF_MONTH))

                endDate.time = sdf.parse(timeDataBean.end_time ?: "")!!
                endDate.set(Calendar.YEAR, currentDate.get(Calendar.YEAR))
                endDate.set(Calendar.MONTH, currentDate.get(Calendar.MONTH))
                endDate.set(Calendar.DAY_OF_MONTH, currentDate.get(Calendar.DAY_OF_MONTH))
            }
        }

        return if (isCheckStatus) {
            currentDate.time.after(startDate.time) && currentDate.time.before(endDate.time)
        } else {
            isCheckStatus
        }
    }

    private fun onRecyclerViewScrolled() {

        rvSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val isPagingActive = viewModel.validForPaging()
                //lytManager
                // val position = (recyclerView.layoutManager as LinearLayoutManager?)?.findFirstVisibleItemPosition()
                // Log.e("ss", "" + position)
                if (!recyclerView.canScrollVertically(1) && isPagingActive) {
                   // getProductList(supplierId, false)
                }
            }
        })
    }

    override fun onEmptyList(count: Int) {
        noDataVisibility(count == 0)
    }

    private fun noDataVisibility(isNoData: Boolean) {
        if (isNoData) {
            noData.visibility = View.VISIBLE
            rvproductList.visibility = View.GONE
        } else {
            noData.visibility = View.GONE
            rvproductList.visibility = View.VISIBLE
        }
    }

}

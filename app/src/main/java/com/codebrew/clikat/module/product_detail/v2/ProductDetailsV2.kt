package com.codebrew.clikat.module.product_detail.v2


import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.util.SparseIntArray
import android.view.View
import android.webkit.WebSettings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.adapters.PagerImageAdapter
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.DialogsUtil
import com.codebrew.clikat.app_utils.ProdUtils
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.extension.setColorScale
import com.codebrew.clikat.app_utils.extension.setGreyScale
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.VendorAppType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.QuestionList
import com.codebrew.clikat.data.model.others.RateProductListModel
import com.codebrew.clikat.databinding.FragmentProdctDetailV2Binding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.CartInfoServer
import com.codebrew.clikat.modal.CartInfoServerArray
import com.codebrew.clikat.modal.CartList
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.modal.other.*
import com.codebrew.clikat.module.addon_quant.SavedAddon
import com.codebrew.clikat.module.order_detail.rate_product.RateProductActivity
import com.codebrew.clikat.module.product.product_listing.DialogListener
import com.codebrew.clikat.module.product_addon.AddonFragment
import com.codebrew.clikat.module.product_detail.ProdDetailNavigator
import com.codebrew.clikat.module.product_detail.ProdDetailViewModel
import com.codebrew.clikat.module.product_detail.adapter.ProductListAdapter
import com.codebrew.clikat.module.product_detail.adapter.ProductVarientListAdapter
import com.codebrew.clikat.module.product_detail.adapter.ReviewsListAdapter
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.preferences.Prefs
import com.codebrew.clikat.utils.ProgressBarDialog
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.fragment_prodct_detail.*
import kotlinx.android.synthetic.main.fragment_prodct_detail_v2.bottom_cart
import kotlinx.android.synthetic.main.fragment_prodct_detail_v2.btnAddtoCart
import kotlinx.android.synthetic.main.fragment_prodct_detail_v2.cart_action
import kotlinx.android.synthetic.main.fragment_prodct_detail_v2.cpiIndicator
import kotlinx.android.synthetic.main.fragment_prodct_detail_v2.group_supplier
import kotlinx.android.synthetic.main.fragment_prodct_detail_v2.ivMinus
import kotlinx.android.synthetic.main.fragment_prodct_detail_v2.ivPlus
import kotlinx.android.synthetic.main.fragment_prodct_detail_v2.ivSupplierIcon
import kotlinx.android.synthetic.main.fragment_prodct_detail_v2.iv_wishlist
import kotlinx.android.synthetic.main.fragment_prodct_detail_v2.jazzyViewPager
import kotlinx.android.synthetic.main.fragment_prodct_detail_v2.rate_prod_tv
import kotlinx.android.synthetic.main.fragment_prodct_detail_v2.rating_group
import kotlinx.android.synthetic.main.fragment_prodct_detail_v2.rb_rating
import kotlinx.android.synthetic.main.fragment_prodct_detail_v2.rv_reviews_list
import kotlinx.android.synthetic.main.fragment_prodct_detail_v2.rv_variation_list
import kotlinx.android.synthetic.main.fragment_prodct_detail_v2.stock_label
import kotlinx.android.synthetic.main.fragment_prodct_detail_v2.toolbar_layout
import kotlinx.android.synthetic.main.fragment_prodct_detail_v2.tvBrandName
import kotlinx.android.synthetic.main.fragment_prodct_detail_v2.tvBrandNameTxt
import kotlinx.android.synthetic.main.fragment_prodct_detail_v2.tvCounts
import kotlinx.android.synthetic.main.fragment_prodct_detail_v2.tvName
import kotlinx.android.synthetic.main.fragment_prodct_detail_v2.tvSupplierName
import kotlinx.android.synthetic.main.fragment_prodct_detail_v2.tv_discount_price
import kotlinx.android.synthetic.main.fragment_prodct_detail_v2.tv_no_rating
import kotlinx.android.synthetic.main.fragment_prodct_detail_v2.tv_priceType
import kotlinx.android.synthetic.main.fragment_prodct_detail_v2.tv_prod_price
import kotlinx.android.synthetic.main.fragment_prodct_detail_v2.tv_total_rating
import kotlinx.android.synthetic.main.fragment_prodct_detail_v2.webView_prod_desc
import kotlinx.android.synthetic.main.layout_bottom_cart.*
import kotlinx.android.synthetic.main.toolbar_app.*
import java.util.ArrayList
import java.util.Calendar
import java.util.HashMap
import javax.inject.Inject
import kotlin.Comparator


/*
 * Created by Harman Sandhu on 20/4/16.
 */
private const val ARG_PARAM1 = "prodData"
private const val ARG_PARAM2 = "offerType"
private const val ARG_PARAM3 = "isCategory"
private const val ARG_PARAM4 = "supplierDetail"

class ProductDetailsV2 : BaseFragment<FragmentProdctDetailV2Binding, ProdDetailViewModel>(), DialogListener,
        ProductVarientListAdapter.FilterVarientCallback, Runnable, AddonFragment.AddonCallback, ProdDetailNavigator {

    private var selectedCurrency: Currency?=null

    //private var productId: Int = 0
    private var exampleAllSupplier: ProductDataBean? = null
    private var productBean: ProductDataBean? = null

    //  private var categoryId: Int = 0
    // private var supplier_branch_id: Int = 0

    private var adapter: ProductListAdapter? = null

    private var modelList: MutableList<VariantsBean>? = null

    private var cart_flow: Int = 0

    private var bookingFlowBean: SettingModel.DataBean.BookingFlowBean? = null

    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null

    private var handler: Handler? = null

    private var varientId: String? = null
    private var offerType: Int = 0
    private var isCategory = false
    private var supplierDetail: SupplierDetailBean? = null

    //private var deliveryType: Int = 0

    private var ratingBeans: MutableList<RatingBean>? = null

    private var reviewAdapter: ReviewsListAdapter? = null
    private var mBinding: FragmentProdctDetailV2Binding? = null


    private var agentType: Boolean = false

    private val calendar = Calendar.getInstance()


    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var prodUtils: ProdUtils

    @Inject
    lateinit var mDialogsUtil: DialogsUtil


    @Inject
    lateinit var factory: ViewModelProviderFactory


    private lateinit var viewModel: ProdDetailViewModel

    private var mQuestionList = mutableListOf<QuestionList>()

    private var isQuestion = false

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }

    private var settingBean: SettingModel.DataBean.SettingData? = null
    private var isRestaurantOpen: Boolean? = true
    private var fromEditOrder: Boolean? = false

    var prod_variants = mutableListOf<VariantValuesBean>()

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_prodct_detail_v2
    }

    override fun getViewModel(): ProdDetailViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(ProdDetailViewModel::class.java)
        return viewModel
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this

        handler = Handler()

        filterData.clear()

        arguments?.let {
            productBean = it.getParcelable(ARG_PARAM1)

            exampleAllSupplier = productBean

            offerType = it.getInt(ARG_PARAM2)

            isCategory = it.getBoolean(ARG_PARAM3, false)
            isRestaurantOpen = it.getBoolean("isRestaurantOpen")
            fromEditOrder = it.getBoolean("fromEditOrder")
        }

        mQuestionList = exampleAllSupplier?.selectQuestAns ?: mutableListOf()

        isQuestion = exampleAllSupplier?.is_question == 1


        // quantity = StaticFunction.getCartQuantity(activity, exampleAllSupplier?.product_id)

        bookingFlowBean = dataManager.getGsonValue(DataNames.BOOKING_FLOW, SettingModel.DataBean.BookingFlowBean::class.java)

        screenFlowBean = dataManager.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)

        settingBean = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        selectedCurrency = dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)

        cart_flow = bookingFlowBean?.cart_flow ?: 0



        productDetailObserver()
        webView_prod_desc?.getSettings()?.setRenderPriority(WebSettings.RenderPriority.HIGH)
    }

    private fun productDetailObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<ProductDataBean> { resource ->
            prodDetail(resource)
        }
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.productLiveData.observe(this, catObserver)
    }

    private fun prodDetail(resource: ProductDataBean?) {
        exampleAllSupplier = resource

        exampleAllSupplier?.self_pickup = productBean?.self_pickup
        exampleAllSupplier?.type = productBean?.type
        exampleAllSupplier?.payment_after_confirmation = productBean?.payment_after_confirmation
        exampleAllSupplier?.latitude = productBean?.latitude
        exampleAllSupplier?.longitude = productBean?.longitude
        exampleAllSupplier?.radius_price = productBean?.radius_price
        exampleAllSupplier?.distance_value = productBean?.distance_value
        exampleAllSupplier?.supplier_name = productBean?.supplier_name

        if (exampleAllSupplier?.name != null && exampleAllSupplier?.price != null) {
            setdata(exampleAllSupplier)
            btnAddtoCart.visibility = View.GONE
        } else {
            showWarning(getString(R.string.no_detail_found), false)
        }

        viewModel.setIsLoading(false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding
        viewDataBinding.color = Configurations.colors
        viewDataBinding.drawables = Configurations.drawables
        viewDataBinding.strings = textConfig

        if (fromEditOrder == true)
            bottom_cart?.visibility = View.GONE
        else
            setPriceLayout()
        settingToolbar()
        settingLayout()

        clickListener()


/*        if (viewModel.productLiveData.value != null) {
            setdata(viewModel.productLiveData.value)
        } else {*/
        if (isNetworkConnected) {
            apiProductDetail(exampleAllSupplier?.product_id.toString())
        } else {
            StaticFunction.showNoInternetDialog(activity)
        }
        // }


        //to visibility of layout
        when (cart_flow) {
            //to choose single fixed_quantity for products
            0, 2 -> {
                cart_action.visibility = View.GONE
                btnAddtoCart.visibility = View.VISIBLE
            }

            // to choose multiple fixed_quantity for products
            1, 3 -> {
                cart_action.visibility = View.VISIBLE
                btnAddtoCart.visibility = View.GONE
            }
        }
    }


    private fun settingToolbar() {
        when (screenFlowBean?.app_type) {
            AppDataType.HomeServ.type -> tb_title.text = getString(R.string.service_detail)
            AppDataType.Food.type -> tb_title.text = getString(R.string.food_item_detail)
            else -> tb_title.text = getString(R.string.product_detail)
        }
        tb_back.setOnClickListener { v -> Navigation.findNavController(v).popBackStack() }
        /*  tb_back.setOnClickListener {
              requireActivity().onBackPressed()

          }*/
    }


    private fun apiProductDetail(productId: String) {

        val hashMap = hashMapOf("productId" to productId,
                "supplierBranchId" to exampleAllSupplier?.supplier_branch_id.toString(),
                "offer" to "")

        if (dataManager.getCurrentUserLoggedIn()) {
            hashMap["accessToken"] = dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()
        }

        if (isNetworkConnected) {
            viewModel.getProductDetail(hashMap)

            if(dataManager.getCurrentUserLoggedIn())
            {
                viewModel.viewProduct(productId.toInt())
            }

        }
    }


    private fun setdata(productBean: ProductDataBean?) {
        if (productBean != null) {
            //data = exampleAllSupplier

            tvCustomizable?.visibility=if(!productBean.adds_on.isNullOrEmpty()&& fromEditOrder==false) View.VISIBLE else View.GONE
            productBean.product_id = productBean.id

            StaticFunction.loadImage(productBean.supplier_image, ivSupplierIcon!!, false)

            if (productBean.product_desc != null)
                webView_prod_desc?.loadData(productBean.product_desc.toString(), "text/html; charset=UTF-8", null)

            tvName.text = productBean.name

            rate_prod_tv.visibility = View.GONE



            if (productBean.brand_name != null && productBean.brand_name?.isNotEmpty() == true) {
                tvBrandName.visibility = View.VISIBLE
                tvBrandName.text = productBean.brand_name ?: ""
                tvBrandNameTxt.visibility = View.VISIBLE
            } else {
                tvBrandNameTxt.visibility = View.GONE
                tvBrandName.visibility = View.GONE
            }

            if (!productBean.supplier_name.isNullOrEmpty()) {
                group_supplier?.visibility = View.VISIBLE
                tvSupplierName.text = productBean.supplier_name
            }

            rb_rating.rating = productBean.avg_rating?:0f

            //   tv_rating.text = getString(R.string.reviews_text, productBean.total_reviews)


            if (productBean.price_type == 1) {
                tv_priceType.visibility = View.VISIBLE
                tv_priceType.text = if (productBean.is_product == 1) getString(R.string.per_hour) else getString(R.string.duration)
            } else
                tv_priceType.visibility = View.GONE


            settingPriceData(productBean)

            rating_group.visibility = if (productBean.rating?.isNullOrEmpty() == false) View.VISIBLE else View.GONE

            tv_no_rating.visibility = if (productBean.rating?.isNullOrEmpty() == false) View.GONE else View.VISIBLE

            //  tv_total_reviews.text = getString(R.string.reviews_text, productBean.total_reviews)

            tv_total_rating.text = productBean.avg_rating.toString()

            if (productBean.rating?.isNullOrEmpty() == false) {
                ratingBeans?.addAll(productBean.rating ?: listOf())
                reviewAdapter?.notifyDataSetChanged()
            }

            if (productBean.is_product ?: 0 == 1) {
                cart_action.visibility = if (productBean.agent_list == 1 && productBean.is_agent == 1) View.GONE else View.VISIBLE
                btnAddtoCart.visibility = if (productBean.agent_list == 1 && productBean.is_agent == 1) View.VISIBLE else View.GONE
            } else {
                cart_action.visibility = View.VISIBLE
                btnAddtoCart.visibility = View.GONE
            }

            if (settingBean?.is_product_wishlist != "1") {
                iv_wishlist.visibility = View.GONE
            }

            if (productBean.is_favourite == 1) {
                iv_wishlist.setImageResource(R.drawable.ic_favourite)
            } else {
                iv_wishlist.setImageResource(R.drawable.ic_unfavorite)
            }

            //Setting Variant Product
            if (productBean.variants?.isNotEmpty() == true) {

                modelList?.clear()

                modelList?.addAll(productBean.variants ?: emptyList())


                val variantsBean = VariantsBean()

                variantsBean.variant_name = modelList?.get(0)?.variant_name

                val hashMap = HashMap<String, VariantValuesBean>()


                val filterbeanList = modelList?.get(0)?.variant_values

                for (i in filterbeanList?.indices!!) {
                    if (hashMap.containsKey(filterbeanList[i].variant_value)) {
                        val filterVarient = hashMap[filterbeanList.get(i).variant_value]
                        filterbeanList[i].filter_product_id = filterVarient!!.filter_product_id + "," + filterbeanList.get(i).product_id
                        filterbeanList[i].filterstatus = 1

                        hashMap[filterbeanList[i].variant_value ?: ""] = filterbeanList[i]
                    } else {
                        filterbeanList[i].filter_product_id = filterbeanList[i].product_id.toString()
                        filterbeanList[i].filterstatus = 1

                        hashMap[filterbeanList[i].variant_value ?: ""] = filterbeanList[i]
                    }
                }


                val valuesBeans = ArrayList<VariantValuesBean>()

                for ((_, value) in hashMap) {

                    valuesBeans.add(value)
                }

                varientId = valuesBeans[0].filter_product_id

                variantsBean.variant_values = valuesBeans
                valuesBeans.sortWith(Comparator { v1, v2 -> v1.variant_value!!.compareTo(v2.variant_value!!) })
                modelList!![0] = variantsBean

                adapter?.notifyDataSetChanged()

                rv_variation_list?.visibility = View.VISIBLE
                cart_action?.visibility = View.GONE


                handler?.postDelayed(this, 500)
            } else {
                cart_action?.visibility = View.VISIBLE
                btnAddtoCart?.visibility = View.VISIBLE

                // rv_variation_list.setVisibility(View.GONE);


                if (productBean.purchased_quantity ?: 0f >= productBean.quantity ?: 0f || productBean.quantity == 0f ||productBean.item_unavailable=="1") {
                    stock_label.visibility = View.VISIBLE
                    disableQuant(false)
                } else {
                    stock_label.visibility = View.GONE
                    disableQuant(true)
                }


            }


            /*   if (data?.is_quantity == 0) {
                   btnAddtoCart!!.visibility = View.VISIBLE
                   cart_action!!.visibility = View.GONE
               }*/

            agentType = productBean.agent_list == 1 && productBean.is_agent == 1


            if (productBean.netPrice == 0f)
                productBean.netPrice = 0f

            jazzyViewPager.adapter = PagerImageAdapter(activity, productBean.image_path as MutableList<String>?, jazzyViewPager)
            cpiIndicator.setViewPager(jazzyViewPager)
            cpiIndicator.setSnap(false)
            cpiIndicator.setFillColor(ContextCompat.getColor(activity
                    ?: requireContext(), R.color.white))
            cpiIndicator.setUnFillColor(ContextCompat.getColor(activity
                    ?: requireContext(), R.color.black))

        }

    }

    private fun disableQuant(status: Boolean) {
        ivMinus.isEnabled = status
        ivPlus.isEnabled = status

        if (status) {
            ivMinus.setColorScale()
            ivPlus.setColorScale()
        } else {
            ivMinus.setGreyScale()
            ivPlus.setGreyScale()
        }

    }


    private fun settingPriceData(data: ProductDataBean?) {

        //  val decimalFormat = DecimalFormat("###.##")

        data?.prod_quantity = StaticFunction.getCartQuantity(activity, exampleAllSupplier?.product_id)

        calendar.clear()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        var displayTime = ""


        data.let {
            prodUtils.changeProductList(false,data)
        }

        //product
        if (data?.is_product == 1) {
            tvCounts.text = data.prod_quantity.toString()
        } else {
            //service

            if (data?.price_type == 0) {
                // calendar.add(Calendar.MINUTE, data.duration?.times(data.prod_quantity?:0)?:0)

                tvCounts.text = data.prod_quantity.toString()

            } else {
                calendar.add(Calendar.MINUTE, data?.serviceDuration ?: 0)

                displayTime = (if (calendar.get(Calendar.DAY_OF_MONTH) - 1 > 0) getString(R.string.day_tag, calendar.get(Calendar.DAY_OF_MONTH) - 1) else "") + " " + (if (calendar.get(Calendar.HOUR) > 0) getString(R.string.hour_tag, calendar.get(Calendar.HOUR)) else "") + " " +
                        if (calendar.get(Calendar.MINUTE) > 0) getString(R.string.minute_tag, calendar.get(Calendar.MINUTE)) else ""

                tvCounts.text = if (data?.prod_quantity == 0f) "00:00" else displayTime
            }

        }

        if (data?.netDiscount ?: 0.0f > 0 && data?.fixed_price != data?.display_price) {
            tv_discount_price.visibility = View.VISIBLE
            tv_discount_price.paintFlags = tv_prod_price?.paintFlags?.or(Paint.STRIKE_THRU_TEXT_FLAG)
                    ?: 0
            tv_discount_price.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(data?.netDiscount
                    ?: 0.0f,settingBean,selectedCurrency))
            tv_prod_price.text = if (data?.price_type == 0) {
                getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(data.netPrice
                        ?: 0.0f,settingBean,selectedCurrency))
            } else {
                getString(R.string.discountprice_search_multiple, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(data?.netPrice
                        ?: 0.0f,settingBean,selectedCurrency), displayTime)
            }
        } else {
            tv_discount_price.visibility = View.GONE

            tv_prod_price.text = if (data?.price_type == 0) {
                getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(data.netPrice
                        ?: 0.0f,settingBean,selectedCurrency))
            } else {
                getString(R.string.discountprice_search_multiple, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(data?.netPrice
                        ?: 0.0f,settingBean,selectedCurrency), displayTime)
            }
        }




        if (screenFlowBean?.app_type == AppDataType.HomeServ.type && data?.is_quantity == 0) {

            btnAddtoCart.visibility = View.VISIBLE
            cart_action.visibility = View.GONE

            if (data.prod_quantity ?: 0f > 0f) {
                btnAddtoCart.setText(R.string.remove_cart)
            } else {
                btnAddtoCart.setText(R.string.add_cart_tag)
            }
        } else {
            btnAddtoCart.text = getString(R.string.buy_now)
        }

    }


    private fun settingLayout() {

        modelList = ArrayList()
        ratingBeans = ArrayList()

        val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        rv_variation_list?.layoutManager = layoutManager
        adapter = ProductListAdapter(modelList)
        adapter?.settingCallback(this)
        rv_variation_list?.adapter = adapter

        val reviewLayoutMnager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        rv_reviews_list?.layoutManager = reviewLayoutMnager
        reviewAdapter = ReviewsListAdapter(ratingBeans)
        rv_reviews_list?.adapter = reviewAdapter

        //tvDeliveryLocation.setText(locationUser.getArea(StaticFunction.getLanguage(getActivity())));
        tb_title?.text = ""
        toolbar_layout?.background = ContextCompat.getDrawable(requireActivity(), R.drawable.tool_shadow_gradient)
//            toolbar_layout?.setBackgroundColor(ContextCompat.getColor(requireActivity(),android.R.color.transparent))

    }

    private fun clickListener() {
        btnAddtoCart.setOnClickListener {
            if (btnAddtoCart!!.text.toString() == getString(R.string.buy_now)) {
                if (StaticFunction.isInternetConnected(activity)) {
                    val dataLogin = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
                    if (dataLogin?.data != null && dataLogin.data.access_token != null && dataLogin?.data.access_token?.trim()?.isNotEmpty()==true) {

                        requestBuyNow()
                        //
                    } else {

                        startActivityForResult(Intent(activity, appUtils.checkLoginActivity()), DataNames.REQUEST_BUY_NOW)
                    }
                } else {
                    StaticFunction.showNoInternetDialog(activity)
                }
            } else if (btnAddtoCart!!.text.toString() == getString(R.string.remove_cart)) {
                removeCart()
            } else {
                addCartItem()
            }
        }

        ivPlus.setOnClickListener {
            addProduct()
        }


        ivMinus.setOnClickListener {
            removeCart()
            setPriceLayout()
        }


        rate_prod_tv.setOnClickListener {
            val pojoLoginData = StaticFunction.isLoginProperly(activity)

            if (pojoLoginData.data != null) {
                val intent = Intent(activity, RateProductActivity::class.java)
                intent.putExtra("rateProducts", calculateRateProduct(exampleAllSupplier))
                requireActivity().startActivity(intent)
            } else {
                startActivityForResult(Intent(activity, appUtils.checkLoginActivity()), DataNames.REQUEST_HOME_SCREEN)
            }
        }

        iv_wishlist.setOnClickListener {
            markFavourite()
        }
    }

    private fun addProduct() {
        if (isRestaurantOpen == false) {
            mDialogsUtil.openAlertDialog(activity
                    ?: requireContext(), getString(R.string.offline_supplier_tag, Configurations.strings.supplier), getString(R.string.ok), "", this)
            return
        }

        if (exampleAllSupplier?.adds_on?.isNotEmpty() == true) {
            val cartList: CartList? = dataManager.getGsonValue(DataNames.CART, CartList::class.java)

            if (appUtils.checkProdExistance(exampleAllSupplier?.id ?: -1)) {
                val savedProduct = cartList?.cartInfos?.filter {
                    it.supplierId == exampleAllSupplier?.supplier_id && it.productId == exampleAllSupplier?.id
                } ?: emptyList()

                SavedAddon.newInstance(exampleAllSupplier, 0, savedProduct, this).show(childFragmentManager, "savedAddon")
            } else {
                AddonFragment.newInstance(exampleAllSupplier!!, 0, this).show(childFragmentManager, "addOn")
            }

        } else {
            if (bookingFlowBean?.vendor_status == 0 && appUtils.checkVendorStatus(exampleAllSupplier?.supplier_id
                            ?: -1, vendorBranchId = exampleAllSupplier?.supplier_branch_id, branchFlow = settingBean?.branch_flow)) {
                mDialogsUtil.openAlertDialog(activity
                        ?: requireContext(), getString(R.string.clearCart, textConfig?.supplier
                        ?: "",textConfig?.proceed ?: ""), "Yes", "No", this)
            } else {
                // if (appUtils.checkBookingFlow(exampleAllSupplier?.id ?: -1, this)) {
                addCartItem()
                //}
            }
            setPriceLayout()
        }
    }

    private fun markFavourite() {
        if (dataManager.getCurrentUserLoggedIn()) {
            viewModel.markFavProduct(exampleAllSupplier?.product_id, if (exampleAllSupplier?.is_favourite == 0) 1 else 0)
        } else {
            appUtils.checkLoginFlow(requireActivity(),AppConstants.REQUEST_PRODUCT_FAVOURITE)
        }
    }

    private fun requestBuyNow() {

        val productList = ArrayList<CartInfoServer>()
        val cartInfoServer = CartInfoServer()
        cartInfoServer.quantity = 1f
        cartInfoServer.pricetype = exampleAllSupplier!!.price_type ?: 0
        cartInfoServer.productId = exampleAllSupplier!!.id.toString()
        cartInfoServer.category_id = exampleAllSupplier!!.category_id ?: 0
        cartInfoServer.handlingAdmin = exampleAllSupplier!!.handling_admin
        cartInfoServer.supplier_branch_id = exampleAllSupplier!!.supplier_branch_id ?: 0
        cartInfoServer.handlingSupplier = exampleAllSupplier!!.handling_supplier
        cartInfoServer.supplier_id = exampleAllSupplier!!.supplier_id ?: 0
        cartInfoServer.agent_type = if (exampleAllSupplier!!.is_agent == 1 && exampleAllSupplier!!.is_product == 0) 1 else 0
        cartInfoServer.agent_list = exampleAllSupplier!!.agent_list ?: 0
        cartInfoServer.deliveryCharges = exampleAllSupplier!!.delivery_charges?.toFloat() ?: 0.0f
        cartInfoServer.name = exampleAllSupplier!!.name
        cartInfoServer.price = exampleAllSupplier!!.netPrice ?: 0.0f
        // cartInfoServer.setSubCatName(exampleAllSupplier.getData().getSu);
        productList.add(cartInfoServer)

        apiAddToCart(exampleAllSupplier!!.supplier_branch_id ?: 0, productList)
    }

    private fun addCartItem() {

        var mServiceStatus = false

        var mServiceCheck = false

        if (exampleAllSupplier!!.is_product == 1 && screenFlowBean?.app_type == AppDataType.HomeServ.type)
            mServiceStatus = exampleAllSupplier!!.is_agent == 1 && exampleAllSupplier!!.agent_list == 1

        if (mServiceStatus) {
            if (StaticFunction.covertCartToArray(activity).isNotEmpty()) {
                mServiceCheck = checkMultipleService(StaticFunction.covertCartToArray(activity), exampleAllSupplier?.product_id
                        ?: 0)
            }
        }


        // if vendor status ==0 single vendor && vendor status==1 multiple vendor
        if (bookingFlowBean?.vendor_status == 0 && appUtils.checkVendorStatus(exampleAllSupplier?.supplier_id
                        , vendorBranchId = exampleAllSupplier?.supplier_branch_id, branchFlow = settingBean?.branch_flow) || mServiceCheck) {

            mDialogsUtil.openAlertDialog(activity
                    ?: requireContext(), getString(R.string.clearCart, textConfig?.supplier
                    ?: "",textConfig?.proceed ?: ""), "Yes", "No", this)

        } else {

            if (appUtils.checkBookingFlow(requireContext(), exampleAllSupplier?.product_id, this)) {


                if (isQuestion && prodUtils.addItemToCart(exampleAllSupplier) != null && isCategory == false) {
                    exampleAllSupplier?.prod_quantity = 0f
                    val bundle = bundleOf("productBean" to exampleAllSupplier, "is_Category" to false
                            , "categoryId" to exampleAllSupplier?.sub_category_id)
                    navController(this).navigate(R.id.action_productDetailsV2_to_questionFrag, bundle)
                } else {
                    addCart()
                }
            }
        }
    }


    private fun calculateRateProduct(product: ProductDataBean?): ArrayList<RateProductListModel> {

        val rateProductListModels = ArrayList<RateProductListModel>()

        rateProductListModels.add(RateProductListModel(product?.name, product?.supplier_name, (exampleAllSupplier?.image_path as MutableList<String>)[0], product?.id.toString()))

        return rateProductListModels
    }

    private fun removeCart() {

        var displayTime: String

        calendar.clear()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        exampleAllSupplier.let {
            prodUtils.removeItemToCart(exampleAllSupplier)
        }


        //service
        if (exampleAllSupplier?.is_product == 0 && exampleAllSupplier?.price_type == 1) {

            calendar.clear()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.DAY_OF_MONTH, 1)

            calendar.set(Calendar.MINUTE, exampleAllSupplier?.serviceDuration?.times(exampleAllSupplier?.prod_quantity?.toInt()
                    ?: 0) ?: 0)

            displayTime = (if (calendar.get(Calendar.DAY_OF_MONTH) - 1 > 0) getString(R.string.day_tag, calendar.get(Calendar.DAY_OF_MONTH) - 1) else "") + " " + (if (calendar.get(Calendar.HOUR_OF_DAY) > 0) getString(R.string.hour_tag, calendar.get(Calendar.HOUR_OF_DAY)) else "") + " " +
                    if (calendar.get(Calendar.MINUTE) > 0) getString(R.string.minute_tag, calendar.get(Calendar.MINUTE)) else ""


            tvCounts.text = if (exampleAllSupplier?.prod_quantity == 0f) "00:00" else displayTime
            //  if (exampleAllSupplier?.price_type == 1) {
            //   tv_discount_price.visibility = View.GONE
            //   tv_prod_price.visibility = View.VISIBLE

            displayTime = if (exampleAllSupplier?.duration ?: 0 > 0) exampleAllSupplier?.duration?.div(bookingFlowBean?.interval
                    ?: 0).toString() else bookingFlowBean?.interval?.div(60).toString()
            tv_discount_price.text = getString(R.string.currency_tag,
                    AppConstants.CURRENCY_SYMBOL,
                    Utils.getPriceFormat(exampleAllSupplier?.netDiscount?: 0f,settingBean,selectedCurrency))


            // }

        } else {

            tvCounts.text = exampleAllSupplier?.prod_quantity.toString()
        }

        if (screenFlowBean?.app_type == AppDataType.HomeServ.type && exampleAllSupplier?.is_quantity == 0) {
            if (exampleAllSupplier?.prod_quantity ?: 0f > 0f) {
                btnAddtoCart.setText(R.string.remove_cart)
            } else {
                btnAddtoCart.setText(R.string.add_cart_tag)
            }
        }
    }


    private fun addCart() {

        val dataBean = exampleAllSupplier

        dataBean?.product_id = dataBean?.id


        if (dataBean?.image_path is MutableList<*>) {
            dataBean.image_path = (dataBean.image_path as MutableList<*>?)?.get(0)
        }

        dataBean?.prod_variants = prod_variants

        if (dataBean?.fixed_price == dataBean?.display_price) {
            dataBean?.discount = 0
        } else {
            dataBean?.discount = 1
        }
        val displayTime: String

        calendar.clear()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        if (isQuestion) {
            dataBean?.selectQuestAns = mQuestionList
        }

        dataBean?.type = screenFlowBean?.app_type

        dataBean?.supplier_branch_id = productBean?.supplier_branch_id

        dataBean.let {
            prodUtils.addItemToCart(it)
        }


        if (screenFlowBean?.app_type == AppDataType.HomeServ.type) {
            if (dataBean?.prod_quantity ?: 0f > 0f) {
                btnAddtoCart.setText(R.string.remove_cart)
            } else {
                btnAddtoCart.setText(R.string.add_cart_tag)
            }
        }


        //service
        if (dataBean?.is_product == 0 && dataBean.price_type == 1) {

            calendar.add(Calendar.MINUTE, dataBean.serviceDuration?.times(dataBean.prod_quantity?.toInt()
                    ?: 0) ?: 0)
            displayTime = (if (calendar.get(Calendar.DAY_OF_MONTH) - 1 > 0) getString(R.string.day_tag, calendar.get(Calendar.DAY_OF_MONTH) - 1) else "") + " " + (if (calendar.get(Calendar.HOUR_OF_DAY) > 0) getString(R.string.hour_tag, calendar.get(Calendar.HOUR_OF_DAY)) else "") + " " +
                    if (calendar.get(Calendar.MINUTE) > 0) getString(R.string.minute_tag, calendar.get(Calendar.MINUTE)) else ""

            tvCounts.text = if (dataBean.prod_quantity ?: 0 == 0) "00:00" else displayTime
            if (dataBean.price_type == 1) {
                // tv_discount_price.visibility = View.GONE
                //  tv_prod_price.visibility = View.VISIBLE


                tv_prod_price.text = getString(R.string.discountprice_search_multiple,
                        AppConstants.CURRENCY_SYMBOL,
                        dataBean.netPrice.toString(),
                        displayTime)
            }


        } else {
            tvCounts.text = (dataBean?.prod_quantity ?: 0).toString()
        }


    }


    private fun checkMultipleService(covertCartToArray: List<CartInfoServer>, product_id: Int): Boolean {

        for (i in covertCartToArray.indices) {

            if (covertCartToArray[i].productId == product_id.toString()) {
                return true
            }

        }
        return false
    }


    override fun onSucessListner() {
        exampleAllSupplier?.prod_quantity = 0f
        tvCounts.text = exampleAllSupplier?.prod_quantity.toString()
        appUtils.clearCart()
        if (isRestaurantOpen == true)
            addCartItem()
        setPriceLayout()
    }

    override fun onErrorListener() {

    }

    override fun onFilterVarient(variantValuesBean: VariantValuesBean?, varientId: String?, adpaterPosition: Int) {
        this.varientId = varientId

        //rv_variation_list.clearAnimation();

        if (modelList?.size == adpaterPosition + 1) {
            adapter?.notifyDataSetChanged()

            if (!(varientId?:"").contains(",")) {
                prod_variants.clear()
                variantValuesBean?.let { prod_variants.add(it) }

                apiProductDetail(varientId?:"")
            }

        } else {
            rv_variation_list.post { adapter?.settingQuery(varientId, adpaterPosition + 1) }
        }
    }

    override fun run() {
        adapter?.settingQuery(varientId, 1)
        adapter?.notifyDataSetChanged()
        handler?.removeCallbacks(this)
    }


    private fun showWarning(message: String, status: Boolean) {
        val sweetAlertDialog = AlertDialog.Builder(requireActivity())
        sweetAlertDialog.setTitle(getString(R.string.alert))
        sweetAlertDialog.setMessage(message)
        sweetAlertDialog.setPositiveButton(getString(R.string.ok)) { dialog, which ->
            dialog.dismiss()
        }
        sweetAlertDialog.show()
    }


    private fun apiAddToCart(supplierBranchId: Int, productList: ArrayList<CartInfoServer>) {
        val barDialog = ProgressBarDialog(activity)
        barDialog.show()

        //CartList cartList = StaticFunction.allCart(getActivity(), Prefs.with(getActivity()).getInt(DataNames.FLOW_STROE, 0));

        val pojoSignUp = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
        val cartInfoServerArray = CartInfoServerArray()
        cartInfoServerArray.productList = productList
        cartInfoServerArray.accessToken = pojoSignUp!!.data.access_token

        cartInfoServerArray.remarks = "0"
        cartInfoServerArray.cartId = "0"
        cartInfoServerArray.supplierBranchId = supplierBranchId

        if (Prefs.with(activity).getInt(DataNames.FLOW_STROE, 0) == DataNames.PROMOTION)
            cartInfoServerArray.promoationType = "1"
        else
            cartInfoServerArray.promoationType = "0"


        if (isNetworkConnected) {
            viewModel.addCart(cartInfoServerArray, productList)
        }

    }


    private fun setPriceLayout() {


        val mCartData = appUtils.getCartData(settingBean,selectedCurrency)

        bottom_cart.visibility = View.VISIBLE


        if (mCartData.cartAvail) {
            tv_total_price.text = getString(R.string.total).plus(" ").plus(AppConstants.CURRENCY_SYMBOL).plus(" ").plus(mCartData.totalPrice)

            tv_total_product.text = getString(R.string.total_item_tag, Utils.getDecimalPointValue(settingBean, mCartData.totalCount))


            if (mCartData.supplierName.isNotEmpty() && screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {
                tv_supplier_name.visibility = View.VISIBLE
                tv_supplier_name.text = getString(R.string.supplier_tag, mCartData.supplierName)
            } else {
                tv_supplier_name.visibility = View.GONE
            }

            bottom_cart.setOnClickListener { v ->

                val navOptions: NavOptions

                navOptions = if (screenFlowBean!!.app_type == AppDataType.Food.type) {
                    NavOptions.Builder()
                            .setPopUpTo(R.id.resturantHomeFrag, false)
                            .build()
                } else {
                    NavOptions.Builder()
                            .setPopUpTo(R.id.homeFragment, false)
                            .build()
                }

                Navigation.findNavController(requireView()).navigate(R.id.action_productDetailsV2_to_cartV2, null, navOptions)

            }
        } else {
            bottom_cart.visibility = View.GONE
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DataNames.REQUEST_BUY_NOW && resultCode == AppCompatActivity.RESULT_OK) {
            requestBuyNow()
        }

    }


    override fun onAddonAdded(productModel: ProductDataBean) {

        if (productModel.adds_on?.isNullOrEmpty() == false && appUtils.checkProdExistance(productModel.product_id)) {
            val cartList: CartList? = appUtils.getCartList()
            productModel.prod_quantity = (cartList?.cartInfos?.filter { productModel.product_id == it.productId }?.sumByDouble { it.quantity.toDouble() }
                    ?: 0.0).toFloat()
        }
        tvCounts.text = productModel.prod_quantity.toString()
        setPriceLayout()
    }

    override fun onCartAdded(cartdata: AddtoCartModel.CartdataBean?, productList: ArrayList<CartInfoServer>, message: String) {
        if (cartdata != null) {
            Prefs.with(activity).save(DataNames.CART_ID, cartdata.cartId)

            val productIds = ArrayList<Int>()

            for (i in productList.indices) {

                productIds.add(productList[i].productId?.toInt() ?: 0)
            }

        } else {

            mBinding?.root?.onSnackbar(message)
        }
    }

    override fun onFavStatus(prodStatus: Int?) {

        exampleAllSupplier?.is_favourite = prodStatus

        if (prodStatus == 1) {
            mBinding?.root?.onSnackbar(getString(R.string.success_wishlist, textConfig?.product, textConfig?.wishlist))
            iv_wishlist.setImageResource(R.drawable.ic_favourite)
        } else {
            mBinding?.root?.onSnackbar(getString(R.string.removed_wishlist, textConfig?.product, textConfig?.wishlist))
            iv_wishlist.setImageResource(R.drawable.ic_unfavorite)
        }
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    companion object {

        var filterData = SparseIntArray()

        @JvmStatic
        fun newInstance(param1: ProductDataBean?, param2: Int, param3: Boolean, isRestaurantOpen: Boolean = true) =
                bundleOf( ARG_PARAM1 to param1,
                         ARG_PARAM2 to param2,
                        ARG_PARAM3 to param3,
                        "isRestaurantOpen" to isRestaurantOpen)
    }


}

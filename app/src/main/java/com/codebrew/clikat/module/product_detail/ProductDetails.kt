package com.codebrew.clikat.module.product_detail


import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.util.SparseIntArray
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.Toast
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
import com.clevertap.android.sdk.CleverTapAPI
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.adapters.PagerImageAdapter
import com.codebrew.clikat.app_utils.*
import com.codebrew.clikat.app_utils.extension.*
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.VendorAppType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.QuestionList
import com.codebrew.clikat.data.model.others.AgentCustomParam
import com.codebrew.clikat.data.model.others.RateProductListModel
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentProdctDetailBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.*
import com.codebrew.clikat.modal.other.*
import com.codebrew.clikat.module.addon_quant.SavedAddon
import com.codebrew.clikat.module.cart.addproduct.AddProductDialog
import com.codebrew.clikat.module.home_screen.adapter.SpecialListAdapter
import com.codebrew.clikat.module.order_detail.rate_product.RateProductActivity
import com.codebrew.clikat.module.product.product_listing.DialogListener
import com.codebrew.clikat.module.product_addon.AddonFragment
import com.codebrew.clikat.module.product_detail.adapter.CompareProductsAdapter
import com.codebrew.clikat.module.product_detail.adapter.ProductListAdapter
import com.codebrew.clikat.module.product_detail.adapter.ProductVarientListAdapter
import com.codebrew.clikat.module.product_detail.adapter.ReviewsListAdapter
import com.codebrew.clikat.module.service_selection.ServSelectionActivity
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.preferences.Prefs
import com.codebrew.clikat.utils.DialogIntrface
import com.codebrew.clikat.utils.ProgressBarDialog
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_prodct_detail.*
import kotlinx.android.synthetic.main.layout_bottom_cart.*
import kotlinx.android.synthetic.main.toolbar_app.*
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject
import kotlin.Comparator
import kotlin.collections.ArrayList


/*
 * Created by Harman Sandhu on 20/4/16.
 */
private const val ARG_PARAM1 = "prodData"
private const val ARG_PARAM2 = "offerType"
private const val ARG_PARAM3 = "isCategory"
private const val ARG_PARAM4 = "supplierDetail"

class ProductDetails : BaseFragment<FragmentProdctDetailBinding, ProdDetailViewModel>(), DialogListener,
        ProductVarientListAdapter.FilterVarientCallback, Runnable, AddonFragment.AddonCallback, ProdDetailNavigator,
        SpecialListAdapter.OnProductDetail, DialogIntrface, CompareProductsAdapter.OnProductDetail, AddProductDialog.OnAddProductListener {

    private var selectedCurrency: Currency?=null
    private var similarProductAdapter: SpecialListAdapter? = null
    private var compareProductsAdapter: CompareProductsAdapter? = null
    var cleverTapDefaultInstance: CleverTapAPI? = null
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

    //private var deliveryType: Int = 0

    private var ratingBeans: MutableList<RatingBean>? = null

    private var reviewAdapter: ReviewsListAdapter? = null
    private var mBinding: FragmentProdctDetailBinding? = null


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
    lateinit var prefHelper: PreferenceHelper


    @Inject
    lateinit var factory: ViewModelProviderFactory


    private lateinit var viewModel: ProdDetailViewModel

    private var mQuestionList = mutableListOf<QuestionList>()

    private var isQuestion = false

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }

    private var settingBean: SettingModel.DataBean.SettingData? = null
    private var isRestaurantOpen: Boolean? = true

    var prod_variants = mutableListOf<VariantValuesBean>()
    private val decimalFormat: DecimalFormat = DecimalFormat("0.00")
    private var fromEditOrder: Boolean? = false
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_prodct_detail
    }

    override fun getViewModel(): ProdDetailViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(ProdDetailViewModel::class.java)
        return viewModel
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this
        cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(requireActivity())
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
        val prodViewedAction = mapOf(
                "Product Name" to resource?.name,
                "Product ID" to resource?.id,
                "Category" to resource?.cate_name,
                "Selling Price" to resource?.display_price,
                "Stock Availability" to resource?.availability)

        cleverTapDefaultInstance?.pushEvent("Product_detail_event", prodViewedAction)
        exampleAllSupplier = resource

        val selfPickup = if (screenFlowBean?.app_type == AppDataType.Ecom.type) 0 else productBean?.self_pickup
        exampleAllSupplier?.self_pickup = selfPickup
        exampleAllSupplier?.type = productBean?.type
        exampleAllSupplier?.payment_after_confirmation = productBean?.payment_after_confirmation
        exampleAllSupplier?.latitude = productBean?.latitude
        exampleAllSupplier?.longitude = productBean?.longitude
        exampleAllSupplier?.radius_price = productBean?.radius_price
        exampleAllSupplier?.distance_value = productBean?.distance_value
        exampleAllSupplier?.supplier_name = productBean?.supplier_name
        exampleAllSupplier?.is_out_network = productBean?.is_out_network

        if (settingBean?.is_compare_products == "1") {

            handleSimilarProd(ArrayList(resource?.similarProduct ?: mutableListOf()))
        } else
            btnCompare?.visibility = View.GONE


        if (exampleAllSupplier?.name != null && exampleAllSupplier?.price != null) {
            setdata(exampleAllSupplier)
            btnAddtoCart.visibility = View.GONE
        } else {
            showWarning(getString(R.string.no_detail_found), false)
        }

        btnBook?.visibility = if (settingBean?.enable_freelancer_flow == "1" && !isQuestion && resource?.is_appointment != "1") View.VISIBLE else View.GONE
        ivAllergies?.visibility = if (settingBean?.enable_product_allergy == "1" && exampleAllSupplier?.is_allergy_product == "1") View.VISIBLE else View.GONE
        viewModel.setIsLoading(false)
    }

    private fun handleSimilarProd(similarProduct: ArrayList<ProductDataBean?>?) {

        if (similarProduct?.isNullOrEmpty() == true) return

        similar_prod_grp.visibility = View.VISIBLE
        btnCompare?.visibility = View.VISIBLE
        tv_category.text = "Similar " + textConfig?.product

        rv_similar_prod.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        similarProductAdapter = SpecialListAdapter(if (similarProduct.count() > 5) similarProduct.subList(0, 5) else similarProduct,
                screenFlowBean?.app_type ?: -1,
                screenFlowBean?.is_single_vendor ?: -1,
                0, settingBean, true, currency = selectedCurrency, appUtils = appUtils)

        similarProductAdapter?.settingCllback(this)

        rv_similar_prod.adapter = similarProductAdapter

        tv_viewmore.visibility = if (similarProduct.count() > 5) {
            View.VISIBLE
        } else {
            View.GONE
        }

        tv_viewmore.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelableArrayList("similar_list", similarProduct)
            navController(this@ProductDetails).navigate(R.id.action_productDetails_to_offerProductListingFragment, bundle)
        }


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
            if (exampleAllSupplier?.product_id != null)
                apiProductDetail(exampleAllSupplier?.product_id.toString())
        } else {
            StaticFunction.showNoInternetDialog(activity)
        }
        // }

       mBinding?.etInstruction?.setText(StaticFunction.getCartSpecialInstructions(requireActivity(),exampleAllSupplier?.product_id))

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

        mBinding?.etInstruction?.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
               updateInstructions()
                return@OnKeyListener true
            }
            false
        })
    }

    private fun updateInstructions() {
        if (mBinding?.etInstruction?.text.toString().trim().isNotEmpty())
            StaticFunction.updateCartInstructions(requireContext(), exampleAllSupplier?.product_id, mBinding?.etInstruction?.text.toString().trim())

    }


    private fun settingToolbar() {
        tb_title.text = getString(R.string.product_detail, textConfig?.product ?: "")
        tvCounts?.isEnabled = settingBean?.is_decimal_quantity_allowed == "1"

        tb_back.setOnClickListener { v -> Navigation.findNavController(v).popBackStack() }
        /*  tb_back.setOnClickListener {
              requireActivity().onBackPressed()

          }*/

        tvCounts.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val updatedQuantity = decimalFormat.format((if (tvCounts.text.toString().trim().isEmpty()) "0.0" else tvCounts.text.toString().trim()).toFloat()).toFloat()
                if (settingBean?.is_decimal_quantity_allowed == "1" && settingBean?.is_decimal_fixed_interval == "1") {
                    val rem = (updatedQuantity.times(100).toInt()).rem(AppConstants.DECIMAL_INTERVAL.times(100).toInt())
                    if (rem == 0) {
                        hideKeyboard()
                        addProduct(updatedQuantity)
                    } else
                        AppToasty.error(requireContext(), getString(R.string.quantity_shoul_be_multiple))
                } else {
                    hideKeyboard()
                    addProduct(updatedQuantity)
                }
                true
            } else false
        }

        tvSizeChart?.setOnClickListener {
            showSizeDialog()
        }
        btnBook?.setOnClickListener {
            bookNow(exampleAllSupplier)
        }
    }

    private fun bookNow(bean: ProductDataBean?) {
        if (bean?.prod_quantity ?: 0f > 0f) {
            if (dataManager.getCurrentUserLoggedIn()) {
                val productIds = ArrayList<String>()
                productIds.add(bean?.product_id.toString())
                activity?.launchActivity<ServSelectionActivity>(AppConstants.REQUEST_AGENT_DETAIL)
                {
                    putExtra(DataNames.SUPPLIER_BRANCH_ID, exampleAllSupplier?.supplier_branch_id)
                    putExtra("screenType", "productList")
                    putExtra("serviceData", bean)
                    putExtra("productIds", productIds.toTypedArray())
                }
            } else {
                appUtils.checkLoginFlow(requireActivity(), AppConstants.REQUEST_CART_LOGIN_BOOKING)
            }
        } else {
            mBinding?.root?.onSnackbar(getString(R.string.add_quantity))
        }
    }

    private fun showSizeDialog() {
        val sizeDialog = Dialog(requireContext())
        sizeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sizeDialog.setContentView(R.layout.dialog_size_chart)
        sizeDialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )
        sizeDialog.setCancelable(false)
        val ivCross = sizeDialog.findViewById(R.id.ivCross) as ImageView
        val imageView = sizeDialog.findViewById<ImageView>(R.id.ivSizeChart)

        imageView.loadImage(exampleAllSupplier?.size_chart_url ?: "")

        ivCross.setOnClickListener {
            sizeDialog.dismiss()
        }

        sizeDialog.show()
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
            if (dataManager.getCurrentUserLoggedIn()) {
                viewModel.viewProduct(productId.toInt())
            }
        }
    }


    private fun setdata(productBean: ProductDataBean?) {

        if (productBean != null) {
            //data = exampleAllSupplier

            viewDataBinding.productItem = productBean
            viewDataBinding.isWeightVisible = settingBean?.is_product_weight == "1"

            tvCustomizable?.visibility = if (!productBean.adds_on.isNullOrEmpty() && fromEditOrder == false) View.VISIBLE else View.GONE
            productBean.product_id = productBean.id

            StaticFunction.loadImage(productBean.supplier_image, ivSupplierIcon!!, false)

            if (productBean.product_desc != null)
                webView_prod_desc?.loadData(productBean.product_desc.toString(), "text/html; charset=UTF-8", null)

            tvName.text = productBean.name

            rate_prod_tv.visibility = View.GONE

            if (settingBean?.enable_country_of_origin_in_product == "1" && !productBean.country_of_origin.isNullOrEmpty()) {
                groupOrigin?.visibility = View.VISIBLE
                tvOriginName?.text = productBean.country_of_origin
            } else
                groupOrigin?.visibility = View.GONE

            tvSizeChart?.visibility = if (settingBean?.enable_size_chart_in_product == "1" && !productBean.size_chart_url.isNullOrEmpty())
                View.VISIBLE else View.GONE

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

            if (settingBean?.is_product_rating == "1") {
                rating_text.visibility = View.VISIBLE
                rb_rating.visibility = View.VISIBLE
                rb_rating.rating = productBean.avg_rating ?: 0f
            }


            //   tv_rating.text = getString(R.string.reviews_text, productBean.total_reviews)


            if (productBean.price_type == 1) {
                tv_priceType.visibility = View.VISIBLE
                tv_priceType.text = if (productBean.is_product == 1) getString(R.string.per_hour) else getString(R.string.duration)
            } else
                tv_priceType.visibility = View.GONE


            settingPriceData(productBean)

            rating_group.visibility = if (productBean.rating?.isNullOrEmpty() == false && settingBean?.is_product_rating == "1") View.VISIBLE else View.GONE

            tv_no_rating.visibility = if (productBean.rating?.isNullOrEmpty() == false || settingBean?.is_product_rating != "1") View.GONE else View.VISIBLE

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
                cart_action?.visibility = if (screenFlowBean?.app_type == AppDataType.Food.type) View.VISIBLE else View.GONE
                btnAddCartBottom?.visibility = View.GONE

                handler?.postDelayed(this, 500)
            } else {
                checkAddCartButton(productBean)
                // rv_variation_list.setVisibility(View.GONE);


                if (productBean.purchased_quantity ?: 0f >= productBean.quantity ?: 0f || productBean.quantity == 0f || productBean?.item_unavailable=="1") {
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
        if (fromEditOrder == true)
            cart_action?.visibility = View.GONE

        if (productBean?.is_out_network == 1 && productBean.prod_quantity ?: 0f > 0f)
            ivPlus?.visibility = View.GONE

    }

    private fun checkAddCartButton(productBean: ProductDataBean?) {
        if (settingBean?.is_lubanah_theme == "1") {
            btnAddtoCart.visibility = View.GONE
            cart_action.visibility = View.GONE
            btnAddCartBottom.isEnabled = !(productBean?.purchased_quantity ?: 0f >= productBean?.quantity ?: 0f || productBean?.quantity == 0f || productBean?.item_unavailable=="1")

            btnAddCartBottom.visibility = if (productBean?.prod_quantity ?: 0f > 0f) {
                View.GONE
            } else
                View.VISIBLE

        } else {
            btnAddCartBottom?.visibility = View.GONE
            cart_action?.visibility = View.VISIBLE
            btnAddtoCart?.visibility = View.VISIBLE
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
            prodUtils.changeProductList(false, data)
        }

        //product
        if (data?.is_product == 1) {
            if(data.prod_quantity==0.0f)
            {

            }else{
                tvCounts.setText(Utils.getDecimalPointValue(settingBean, data.prod_quantity))
            }

        } else {
            //service

            if (data?.price_type == 0) {
                // calendar.add(Calendar.MINUTE, data.duration?.times(data.prod_quantity?:0)?:0)
                if(data.prod_quantity==0.0f)
                {

                }else{
                    tvCounts.setText(Utils.getDecimalPointValue(settingBean, data.prod_quantity))
                }


            } else {
                calendar.add(Calendar.MINUTE, data?.serviceDuration ?: 0)

                displayTime = (if (calendar.get(Calendar.DAY_OF_MONTH) - 1 > 0) getString(R.string.day_tag, calendar.get(Calendar.DAY_OF_MONTH) - 1) else "") + " " + (if (calendar.get(Calendar.HOUR) > 0) getString(R.string.hour_tag, calendar.get(Calendar.HOUR)) else "") + " " +
                        if (calendar.get(Calendar.MINUTE) > 0) getString(R.string.minute_tag, calendar.get(Calendar.MINUTE)) else ""
                if (data!!.prod_quantity == 0.0f) {

                } else {
                    tvCounts.setText(if (data?.prod_quantity == 0f) "00:00" else displayTime)
                }
            }
        }
        data?.netPrice=Utils.getDiscountPrice(data?.netPrice
                ?: 0f, data?.perProductLoyalityDiscount, settingBean)
        if (data?.netDiscount ?: 0.0f > 0 && data?.fixed_price != data?.display_price) {
            tv_discount_price.visibility = View.VISIBLE
            tv_discount_price.paintFlags = tv_prod_price?.paintFlags?.or(Paint.STRIKE_THRU_TEXT_FLAG)
                    ?: 0
            tv_discount_price.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL,
                    Utils.getPriceFormat(data?.netDiscount ?: 0.0f, settingBean, selectedCurrency))

            tv_prod_price.text = if (data?.price_type == 0) {
                getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(data.netPrice
                        ?: 0.0f, settingBean, selectedCurrency))
            } else {
                getString(R.string.discountprice_search_multiple, AppConstants.CURRENCY_SYMBOL,
                        Utils.getPriceFormat(data?.netPrice
                                ?: 0.0f, settingBean, selectedCurrency), displayTime)
            }
        } else {
            tv_discount_price.visibility = View.GONE

            tv_prod_price.text = if (data?.price_type == 0) {
                getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL,
                        Utils.getPriceFormat(data.netPrice ?: 0.0f, settingBean, selectedCurrency))
            } else {
                getString(R.string.discountprice_search_multiple, AppConstants.CURRENCY_SYMBOL,
                        Utils.getPriceFormat(data?.netPrice
                                ?: 0.0f, settingBean, selectedCurrency), displayTime)
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
        if (settingBean?.show_ecom_v2_theme == "1") {
            tb_title?.text = ""
            toolbar_layout?.background = ContextCompat.getDrawable(requireActivity(), R.drawable.tool_shadow_gradient)
        }
    }

    private fun clickListener() {
        btnAddtoCart.setOnClickListener {
            if (btnAddtoCart?.text.toString() == getString(R.string.buy_now)) {
                if (StaticFunction.isInternetConnected(activity)) {
                    val dataLogin = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
                    if (dataLogin?.data != null && dataLogin.data.access_token != null && dataLogin.data.access_token?.trim()?.isNotEmpty() == true) {
                        val prodViewedAction = mapOf(
                                "Product Name" to exampleAllSupplier?.name,
                                "Product ID" to exampleAllSupplier?.id,
                                "Category" to exampleAllSupplier?.category_id,
                                "Product Price" to exampleAllSupplier?.price,
                                "Quantity" to exampleAllSupplier?.prod_quantity,
                                "Stock Availability" to exampleAllSupplier?.availability)

                        cleverTapDefaultInstance?.pushEvent("card_added_event", prodViewedAction)
                        requestBuyNow()
                        //
                    } else {

                        startActivityForResult(Intent(activity, appUtils.checkLoginActivity()), DataNames.REQUEST_BUY_NOW)
                    }
                } else {
                    StaticFunction.showNoInternetDialog(activity)
                }
            } else if (btnAddtoCart?.text.toString() == getString(R.string.remove_cart)) {
                removeCart()
            } else {
                val prodViewedAction = mapOf(
                        "Product Name" to exampleAllSupplier?.name,
                        "Product ID" to exampleAllSupplier?.id,
                        "Category" to exampleAllSupplier?.category_id,
                        "Product Price" to exampleAllSupplier?.price,
                        "Quantity" to exampleAllSupplier?.prod_quantity,
                        "Stock Availability" to exampleAllSupplier?.availability)

                cleverTapDefaultInstance?.pushEvent("card_added_event", prodViewedAction)
                addCartItem(null)
            }
        }
        btnAddCartBottom.setOnClickListener {
            ivPlus?.callOnClick()
            btnAddCartBottom?.visibility = View.GONE

        }

        ivPlus.setOnClickListener {
            addProduct(null)
        }

        ivAllergies?.setOnClickListener {
            appUtils.showAllergiesDialog(requireContext(), exampleAllSupplier?.allergy_description
                    ?: "")
        }
        ivMinus.setOnClickListener {
            if (exampleAllSupplier?.prod_quantity ?: 0f < 0f) return@setOnClickListener

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

        btnCompare?.setOnClickListener {
            val compareList = similarProductAdapter?.getSelectedItems() ?: arrayListOf()

            when {
                compareList.isEmpty() -> mBinding?.root?.onSnackbar(getString(R.string.select_products_to_compare))
                compareList.size > 2 -> mBinding?.root?.onSnackbar(getString(R.string.product_size_less_than))
                else -> {
                    groupCompare?.visibility = View.VISIBLE
                    compareList.add(0, productBean)
                    compareList.add(0, ProductDataBean())
                    compareProductsAdapter = CompareProductsAdapter(compareList, settingBean, selectedCurrency)
                    compareProductsAdapter?.settingCallback(this)
                    rvCompareProducts?.adapter = compareProductsAdapter
                }
            }
        }
    }


    private fun addProduct(updatedQuantity: Float?) {
        val prodViewedAction = mapOf(
                "Product Name" to exampleAllSupplier?.name,
                "Product ID" to exampleAllSupplier?.id,
                "Category" to exampleAllSupplier?.category_id,
                "Product Price" to exampleAllSupplier?.price,
                "Quantity" to exampleAllSupplier?.prod_quantity,
                "Stock Availability" to exampleAllSupplier?.availability)

        cleverTapDefaultInstance?.pushEvent("card_added_event", prodViewedAction)
        if (isRestaurantOpen == false) {
            mDialogsUtil.openAlertDialog(activity
                    ?: requireContext(), getString(R.string.offline_supplier_tag, Configurations.strings.supplier), getString(R.string.ok), "", this)
            return
        }
        val cartList: CartList? = dataManager.getGsonValue(DataNames.CART, CartList::class.java)
        val userData = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)

        if (productBean?.is_subscription_required == 1 && userData?.data?.is_subscribed != 1) {
            mBinding?.root?.onSnackbar(getString(R.string.subcription_required))
            return
        } else if ((cartList?.cartInfos?.any { !(it.product_owner_name.isNullOrEmpty()) } == true || productBean?.is_out_network == 1)
                && cartList?.cartInfos?.size ?: 0 >= 4 && cartList?.cartInfos?.any { it.productId == productBean?.product_id } == false) {
            /*only 4 max products with out network product are allowed*/
            mBinding?.root?.onSnackbar(getString(R.string.max_products_allowed_with_out))
            return
        }

        if (exampleAllSupplier?.adds_on?.isNotEmpty() == true) {

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
                        ?: "", textConfig?.proceed ?: ""), "Yes", "No", this)
            } else if (productBean?.is_out_network == 1) {
                val dialog = AddProductDialog.newInstance(productBean, 0, 0, isRestaurantOpen
                        ?: false)
                dialog.setListeners(this)
                dialog.show(childFragmentManager, "dialog")
            } else {
                // if (appUtils.checkBookingFlow(exampleAllSupplier?.id ?: -1, this)) {
                addCartItem(updatedQuantity)
                //}
            }
            setPriceLayout()
        }
    }

    override fun onProductAdded(productBean: ProductDataBean?, parentPosition: Int, childPosition: Int, isOpen: Boolean) {
        ivPlus?.visibility = View.GONE
        addCartItem(null)
    }

    private fun markFavourite() {
        if (dataManager.getCurrentUserLoggedIn()) {
            viewModel.markFavProduct(exampleAllSupplier?.product_id, if (exampleAllSupplier?.is_favourite == 0) 1 else 0)
        } else {
            appUtils.checkLoginFlow(requireActivity(), AppConstants.REQUEST_PRODUCT_FAVOURITE)
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
        cartInfoServer.deliveryCharges = exampleAllSupplier!!.delivery_charges ?: 0.0f
        cartInfoServer.name = exampleAllSupplier!!.name
        cartInfoServer.price = exampleAllSupplier!!.netPrice ?: 0.0f
        // cartInfoServer.setSubCatName(exampleAllSupplier.getData().getSu);
        productList.add(cartInfoServer)

        apiAddToCart(exampleAllSupplier!!.supplier_branch_id ?: 0, productList)
    }

    private fun addCartItem(updatedQuantity: Float?) {

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

        val cartList: CartList? = appUtils.getCartList()

        val appointmentAdded = cartList?.cartInfos?.any { it.is_appointment == "1" }!!

        // if vendor status ==0 single vendor && vendor status==1 multiple vendor
        if (bookingFlowBean?.vendor_status == 0 && appUtils.checkVendorStatus(exampleAllSupplier?.supplier_id, vendorBranchId = exampleAllSupplier?.supplier_branch_id, branchFlow = settingBean?.branch_flow) || mServiceCheck) {

            mDialogsUtil.openAlertDialog(activity
                    ?: requireContext(), getString(R.string.clearCart, textConfig?.supplier
                    ?: "", textConfig?.proceed ?: ""), "Yes", "No", this)

        } else if (appointmentAdded)
            appUtils.mDialogsUtil.openAlertDialog(activity
                    ?: requireContext(), getString(R.string.appointment_multiple_service_message),
                    "Okay", "", null)
        else if (!cartList.cartInfos.isNullOrEmpty() && productBean?.is_appointment == "1")
            appUtils.mDialogsUtil.openAlertDialog(activity
                    ?: requireContext(), getString(R.string.appointment_multiple_service_message),
                    "Okay", "", null)
        else {

            if (appUtils.checkBookingFlow(requireContext(), exampleAllSupplier?.product_id, this)) {
                if (isQuestion && !isCategory) {
                    exampleAllSupplier?.prod_quantity = 0f
                    val bundle = bundleOf("productBean" to exampleAllSupplier, "is_Category" to false, "subCategoryId" to exampleAllSupplier?.sub_category_id)
                    navController(this@ProductDetails).navigate(R.id.action_productDetails_to_questionFrag, bundle)
                } else {

                    // FOR SINGLE TYPE ADDRESS IS ALREADY SAVED IN SPLASH SCREEN
                    if(screenFlowBean?.is_single_vendor != VendorAppType.Single.appType){
                        if(exampleAllSupplier?.is_appointment =="1"){
                            val mRestUser = LocationUser()
                            mRestUser.address = "${exampleAllSupplier?.supplier_name} , ${
                                exampleAllSupplier?.supplier_address
                                        ?: ""
                            }"
                            prefHelper.addGsonValue(PrefenceConstants.RESTAURANT_INF, Gson().toJson(mRestUser))
                        }
                    }



                    addCart(updatedQuantity)
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

        if (productBean?.is_out_network == 1)
            ivPlus?.visibility = View.VISIBLE


        //service
        if (exampleAllSupplier?.is_product == 0 && exampleAllSupplier?.price_type == 1) {

            calendar.clear()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.DAY_OF_MONTH, 1)

            calendar.set(Calendar.MINUTE, exampleAllSupplier?.serviceDuration?.times((exampleAllSupplier?.prod_quantity)?.toInt()
                    ?: 0) ?: 0)

            displayTime = (if (calendar.get(Calendar.DAY_OF_MONTH) - 1 > 0) getString(R.string.day_tag, calendar.get(Calendar.DAY_OF_MONTH) - 1) else "") + " " + (if (calendar.get(Calendar.HOUR_OF_DAY) > 0) getString(R.string.hour_tag, calendar.get(Calendar.HOUR_OF_DAY)) else "") + " " +
                    if (calendar.get(Calendar.MINUTE) > 0) getString(R.string.minute_tag, calendar.get(Calendar.MINUTE)) else ""

        if (exampleAllSupplier?.prod_quantity == 0.0f) {

                } else {
            tvCounts.setText(if (exampleAllSupplier?.prod_quantity == 0f) "00:00" else displayTime)
        }
            //  if (exampleAllSupplier?.price_type == 1) {
            //   tv_discount_price.visibility = View.GONE
            //   tv_prod_price.visibility = View.VISIBLE

            displayTime = if (exampleAllSupplier?.duration ?: 0 > 0) exampleAllSupplier?.duration?.div(bookingFlowBean?.interval
                    ?: 0).toString() else bookingFlowBean?.interval?.div(60).toString()
            tv_discount_price.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL,
                    Utils.getPriceFormat(exampleAllSupplier?.netDiscount
                            ?: 0f, settingBean, selectedCurrency))

            // }

        } else {
 if (exampleAllSupplier?.prod_quantity == 0.0f) {

                } else {
     tvCounts.setText(Utils.getDecimalPointValue(settingBean, exampleAllSupplier?.prod_quantity))
 }
        }

        if (screenFlowBean?.app_type == AppDataType.HomeServ.type && exampleAllSupplier?.is_quantity == 0) {
            if (exampleAllSupplier?.prod_quantity ?: 0f > 0f) {
                btnAddtoCart.setText(R.string.remove_cart)
            } else {
                btnAddtoCart.setText(R.string.add_cart_tag)
            }
        }
    }


    private fun addCart(updatedQuantity: Float?) {

        val dataBean = exampleAllSupplier

        dataBean?.product_id = dataBean?.id


        if (dataBean?.image_path is MutableList<*>) {
            dataBean.image_path = (dataBean.image_path as MutableList<*>?)?.get(0)
        }


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

        dataBean?.prod_variants = prod_variants

        dataBean?.type = screenFlowBean?.app_type

        dataBean?.supplier_branch_id = productBean?.supplier_branch_id

        //   val addInDb = settingBean?.enable_freelancer_flow != "1"

        val addInDb: Boolean = !(settingBean?.enable_freelancer_flow == "1" && exampleAllSupplier?.is_appointment != "1")

        dataBean.let {
            prodUtils.addItemToCart(it, quantityFromEditText = updatedQuantity, isAddInDb = addInDb)
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

            calendar.add(Calendar.MINUTE, dataBean.serviceDuration?.times((dataBean.prod_quantity)?.toInt()
                    ?: 0) ?: 0)

            displayTime = (if (calendar.get(Calendar.DAY_OF_MONTH) - 1 > 0) getString(R.string.day_tag, calendar.get(Calendar.DAY_OF_MONTH) - 1) else "") + " " + (if (calendar.get(Calendar.HOUR_OF_DAY) > 0) getString(R.string.hour_tag, calendar.get(Calendar.HOUR_OF_DAY)) else "") + " " +
                    if (calendar.get(Calendar.MINUTE) > 0) getString(R.string.minute_tag, calendar.get(Calendar.MINUTE)) else ""
 if (dataBean?.prod_quantity == 0.0f) {

                } else {
     tvCounts.setText(if (dataBean.prod_quantity ?: 0 == 0) "00:00" else displayTime)
 }
            if (dataBean.price_type == 1) {
                // tv_discount_price.visibility = View.GONE
                //  tv_prod_price.visibility = View.VISIBLE


                tv_prod_price.text = getString(R.string.discountprice_search_multiple, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(dataBean.netPrice
                        ?: 0f, settingBean, selectedCurrency), displayTime)
            }
        } else {
            if (dataBean?.prod_quantity == 0.0f) {

                } else {
                tvCounts.setText(Utils.getDecimalPointValue(settingBean, dataBean?.prod_quantity))
            }
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
        if (exampleAllSupplier?.prod_quantity == 0.0f) {

                } else {
            tvCounts.setText(Utils.getDecimalPointValue(settingBean, exampleAllSupplier?.prod_quantity))
        }
        appUtils.clearCart()
        if (isRestaurantOpen == true)
            addCartItem(null)
        setPriceLayout()
    }

    override fun onSuccessListener() {

    }

    override fun onErrorListener() {

    }

    override fun onFilterVarient(variantValuesBean: VariantValuesBean?, varientId: String?, adpaterPosition: Int) {
        this.varientId = varientId

        //rv_variation_list.clearAnimation();

        if (modelList?.size == adpaterPosition + 1) {
            adapter?.notifyDataSetChanged()

            if (!(varientId ?: "").contains(",")) {
                prod_variants.clear()
                variantValuesBean?.let { prod_variants.add(it) }

                apiProductDetail(varientId ?: "")
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


        val mCartData = appUtils.getCartData(settingBean, selectedCurrency)

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

                Navigation.findNavController(requireView()).navigate(R.id.action_productDetails_to_cart, null, navOptions)

            }
        } else {
            bottom_cart.visibility = View.GONE
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DataNames.REQUEST_BUY_NOW && resultCode == AppCompatActivity.RESULT_OK) {
            requestBuyNow()
        } else if (requestCode == AppConstants.REQUEST_AGENT_DETAIL && resultCode == Activity.RESULT_OK) {
            val dataAgent = data?.getParcelableExtra<AgentCustomParam>("agentData")

            val productBean = data?.getParcelableExtra<ProductDataBean>("serviceData")

            if (productBean != null) {
                productBean.agentDetail = dataAgent
                if (productBean.agentBufferPrice != null)
                    productBean.netPrice = (productBean.netPrice
                            ?: 0.0f).plus(productBean.agentBufferPrice?.toFloat() ?: 0f)
                if (!appUtils.checkProdExistance(productBean.product_id))
                    appUtils.addProductDb(requireContext(), screenFlowBean?.app_type
                            ?: 0, productBean)
                else
                    StaticFunction.updateCart(requireContext(), productBean.product_id, productBean.prod_quantity,
                            productBean.netPrice ?: 0f)


                navController(this@ProductDetails).navigate(R.id.action_productDetails_to_cart)

            }

        }
    }


    override fun onAddonAdded(productModel: ProductDataBean) {

        if (productModel.adds_on?.isNullOrEmpty() == false && appUtils.checkProdExistance(productModel.product_id)) {
            val cartList: CartList? = appUtils.getCartList()
            productModel.prod_quantity = (cartList?.cartInfos?.filter { productModel.product_id == it.productId }?.sumByDouble { it.quantity.toDouble() }
                    ?: 0.0).toFloat()
        }
              if (productModel?.prod_quantity == 0.0f) {

                } else {
                  tvCounts.setText(Utils.getDecimalPointValue(settingBean, productModel.prod_quantity))
              }
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
        fun newInstance(param1: ProductDataBean?, param2: Int, param3: Boolean, isRestaurantOpen: Boolean = true, fromEditOrder: Boolean = false) =
                bundleOf(ARG_PARAM1 to param1, ARG_PARAM2 to param2, ARG_PARAM3 to param3, "isRestaurantOpen" to isRestaurantOpen,
                        "fromEditOrder" to fromEditOrder)
    }

    override fun onProductDetail(bean: ProductDataBean?) {
        if (settingBean?.show_ecom_v2_theme == "1") {
            navController(this).navigate(R.id.action_productDetailsV2_self,
                    newInstance(bean, 0, false))
        } else {
            navController(this).navigate(R.id.action_productDetails_self,
                    newInstance(bean, 0, false))
        }
    }


    override fun onProductDetailCompareProduct(bean: ProductDataBean?) {
        val navOptions = NavOptions.Builder().setLaunchSingleTop(false)
        if (settingBean?.show_ecom_v2_theme == "1") {
            navController(this).navigate(R.id.action_productDetailsV2_self, newInstance(bean, 0, false), navOptions.build())
        } else {
            navController(this).navigate(R.id.action_productDetails_self,
                    newInstance(bean, 0, false), navOptions.build())
        }
    }

    override fun addToCart(position: Int, productBean: ProductDataBean?) {

    }

    override fun removeToCart(position: Int, productBean: ProductDataBean?) {

    }

    override fun addtoWishList(adapterPosition: Int, status: Int?, productId: Int?) {
        if (dataManager.getCurrentUserLoggedIn()) {
            if (isNetworkConnected) {
                viewModel.markFavProduct(productId, status)
            }
        } else {
            startActivityForResult(Intent(activity, appUtils.checkLoginActivity()), DataNames.REQUEST_HOME_SCREEN)
        }
    }

    override fun onProdDesc(productDesc: String) {
        StaticFunction.sweetDialogueSuccess11(activity, getString(R.string.description), productDesc, false, 1002, this)
    }

    override fun onProdAllergies(bean: ProductDataBean?) {

    }

    override fun onProdDialog(bean: ProductDataBean?) {
        appUtils.mDialogsUtil.showProductDialog(requireContext(), bean)
    }

    override fun onBookNow(bean: ProductDataBean?) {

    }


}

package com.codebrew.clikat.module.order_detail.rate_product

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.others.RateInputModel
import com.codebrew.clikat.data.model.others.RateProductListModel
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.ActivityRateProductBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.module.order_detail.rate_product.adapter.RateProductAdapter
import com.codebrew.clikat.module.order_detail.rate_product.adapter.RateProductAdapter.RateCallback
import com.codebrew.clikat.utils.StaticFunction.setStatusBarColor
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.activity_rate_product.*
import kotlinx.android.synthetic.main.layout_filter_toolbar.*
import javax.inject.Inject


private const val IS_AGENT = "Agent"
private const val IS_PROD = "Prod"
private const val IS_SUPPLIER = "Supplier"

class RateProductActivity : BaseActivity<ActivityRateProductBinding, RateViewModel>(), BaseInterface, RateCallback {

    private var adapter: RateProductAdapter? = null
    private var rateProductItem: MutableList<RateProductListModel>? = null
    private var currentPosition = 0
    private val appBackground = Color.parseColor(Configurations.colors.appBackground)

    private var isType = ""


    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    private var mViewModel: RateViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_rate_product
    }

    override fun getViewModel(): RateViewModel {
        mViewModel = ViewModelProviders.of(this, factory).get(RateViewModel::class.java)
        return mViewModel as RateViewModel
    }

    override fun onErrorOccur(message: String) {
        showMsg(message)
    }

    private fun showMsg(message: String) {
        window.decorView.findViewById<View>(android.R.id.content)?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigator = this

        viewDataBinding?.color = Configurations.colors


        rateProductObserver()

        setStatusBarColor(this, getColor(R.color.dark_blue))
        settingToolbar()
        settingLayout()
    }


    private fun settingLayout() {
        rateProductItem = intent.getParcelableArrayListExtra("rateProducts") ?: mutableListOf()

        isType = intent.getStringExtra("type") ?: ""

        rv_reviewsList?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        adapter = RateProductAdapter(rateProductItem?: mutableListOf())
        adapter?.settingCallback(this,isType)
        rv_reviewsList?.adapter = adapter
        if (rateProductItem?.count() ?: 0 > 0) adapter?.notifyDataSetChanged()
    }

    private fun settingToolbar() {
        iv_cross?.visibility = View.VISIBLE
        btn_done?.visibility = View.GONE
        tv_title?.text = getString(R.string.rating_review)

        iv_cross.setOnClickListener {
            finish()
        }
    }


    override fun rateProduct(position: Int, rateProduct: RateProductListModel?) {
        if (validateRating(rateProduct)) {
            currentPosition = position

            hideKeyboard()

            when (isType) {
                IS_AGENT -> {
                    rateAgentApi(rateProduct)
                }

                IS_PROD -> {
                    rateProductApi(rateProduct)
                }

                IS_SUPPLIER -> {
                    rateSupplierApi(rateProduct)
                }
            }


        }
    }

    private fun validateRating(rateProduct: RateProductListModel?): Boolean {
        return when {
            rateProduct?.value?.isEmpty() == true -> {
                showMsg(getString(R.string.provide_prod_rating))
                false
            }
            rateProduct?.title?.isEmpty() == true -> {
                showMsg(getString(R.string.provide_rate_title))
                false
            }
            rateProduct?.reviews?.isEmpty() == true -> {
                showMsg(getString(R.string.provide_rate_comment))
                false
            }
            else -> {
                true
            }
        }
    }

    private fun rateProductObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<String> { resource ->

            when (isType) {
                IS_AGENT -> {
                    showMsg(getString(R.string.rate_product_msg, appUtils.loadAppConfig(0).strings?.agent))
                }

                IS_PROD -> {
                    showMsg(getString(R.string.rate_product_msg, appUtils.loadAppConfig(0).strings?.product))
                }

                IS_SUPPLIER -> {
                    showMsg(getString(R.string.rate_product_msg, appUtils.loadAppConfig(0).strings?.supplier))
                }
            }

            rateProductItem?.removeAt(currentPosition)
            adapter?.notifyDataSetChanged()
            if (adapter?.itemCount == 0) {
                finish()
            }
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.rateLiveData.observe(this, catObserver)
    }


    private fun rateProductApi(rateProduct: RateProductListModel?) {

        if (isNetworkConnected) {
            val hashMap = hashMapOf<String, String>("value" to rateProduct?.value.toString(),
                    "product_id" to rateProduct?.product_id.toString(), "reviews" to rateProduct?.reviews
                    .toString(), "title" to rateProduct?.title.toString(),
                    "order_id" to rateProduct?.order_id.toString())

            mViewModel?.validateRateApi(hashMap)
        }
    }

    private fun rateSupplierApi(rateProduct: RateProductListModel?) {

        if (isNetworkConnected) {
            val rateModel = RateInputModel(accessToken = preferenceHelper.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString(),
                    supplierId = rateProduct?.supplier_id.toString(), rating = (rateProduct?.value?.toFloat() ?: 0f).toInt(),
                    comment = rateProduct?.reviews.toString(), orderId = rateProduct?.order_id.toString())

            mViewModel?.supplierRateApi(rateModel)
        }
    }


    private fun rateAgentApi(rateProduct: RateProductListModel?) {


        if (isNetworkConnected) {
            val hashMap = hashMapOf<String, String>("accessToken" to preferenceHelper.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString(),
                    "rating" to rateProduct?.value.toString(),
                    "orderId" to rateProduct?.order_id.toString(),
                    "comment" to rateProduct?.reviews.toString(),
                    "languageId" to preferenceHelper.getLangCode())

            mViewModel?.agentRateApi(hashMap)
        }
    }

}
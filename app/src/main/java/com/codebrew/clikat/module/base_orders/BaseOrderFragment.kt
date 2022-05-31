package com.codebrew.clikat.module.base_orders

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.OrderUtils
import com.codebrew.clikat.app_utils.extension.loadPlaceHolder
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentBaseOrderBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.AppGlobal
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.fragment_base_order.*
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.nothing_found.*
import javax.inject.Inject

abstract class BaseOrderFragment : BaseFragment<FragmentBaseOrderBinding, BaseOrderViewModel>(), BaseOrderNavigator
        , SwipeRefreshLayout.OnRefreshListener {


    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var prefHelper: PreferenceHelper

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var orderUtils: OrderUtils

    val textConfig by lazy { appUtils.loadAppConfig(0).strings }

    private lateinit var viewModel: BaseOrderViewModel

    var settingData: SettingModel.DataBean.SettingData? = null

    var mBinding: FragmentBaseOrderBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingData = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
    }

    @SuppressLint("StringFormatInvalid")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding

        mBinding?.color = Configurations.colors
        mBinding?.strings = Configurations.strings
        mBinding?.drawables = Configurations.drawables
        Glide.with(this).asGif().load(R.raw.loading_gif).into(mBinding!!.imageLoader);
        swiprRefresh.setOnRefreshListener(this)

        rvOrders.layoutManager = LinearLayoutManager(activity)
        tvText.typeface = AppGlobal.semi_bold
        tvText.text = getString(R.string.no_order_found, textConfig?.orders ?: "")

        Utils.loadAppPlaceholder(settingData?.order_history_listing ?: "")?.let {
            if (it.app?.isNotEmpty() == true) {
                ivPlaceholder.loadPlaceHolder(it.app)
            }
            if (it.message?.isNotEmpty() == true) {
                tvText.text = it.message
            }
        }
    }

    protected fun setAdapter(adapter: Adapter<*>?, title: String, string: String?) {

        rvOrders.adapter = adapter

       // tvText.text = string
        /*if (title == getString(R.string.favorites)) {
            tvText.text = getString(R.string.no_fav_found)
        }*/
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_base_order
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }


    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun getViewModel(): BaseOrderViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(BaseOrderViewModel::class.java)
        return viewModel
    }

    override fun onRefresh() {

        //    viewModel.pendingLiveData.value=null
        //   viewModel.historyLiveData.value=null

        swiprRefresh.isRefreshing = false
        refreshData()
    }

     fun calculateProdAddon(productList: List<ProductDataBean?>?): List<ProductDataBean?>? {

        val prodList = arrayListOf<ProductDataBean?>()

        productList?.mapIndexed { index, product ->

            if (product?.adds_on.isNullOrEmpty()) {
                product?.prod_quantity = product?.quantity
                prodList += product?.copy()
            } else {
                product?.adds_on?.groupBy {
                    it?.serial_number
                }?.mapValues {
                    //product.adds_on = it.value
                    product.add_on_name = it.value.map { "${it?.adds_on_type_name} * ${it?.adds_on_type_quantity}" }.joinToString()
                    product.prod_quantity = it.value[0]?.quantity ?: 0f
                    product.fixed_price = product.price?.toFloatOrNull()?.plus(it.value.sumByDouble {
                        (it?.price ?: 0.0f).toDouble().times((it?.adds_on_type_quantity
                                ?: "0").toInt())
                    }.toFloat()).toString()
                    prodList += product.copy()
                }
            }
        }

        return prodList.takeIf { it.isNotEmpty() } ?: productList
    }


}
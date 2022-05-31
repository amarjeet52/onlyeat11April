package com.codebrew.clikat.module.social_post.other

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.DialogsUtil
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.databinding.FragSocialSelctSupplierBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SupplierDataBean
import com.codebrew.clikat.module.product.product_listing.DialogListener
import com.codebrew.clikat.module.social_post.SocialPostViewModel
import com.codebrew.clikat.module.social_post.bottom_sheet.BottomSocialSheetFrag
import com.codebrew.clikat.module.social_post.custom_model.BottomDataItem
import com.codebrew.clikat.module.social_post.custom_model.SocialSupplierBean
import com.codebrew.clikat.module.social_post.interfaces.BottomActionListener
import com.codebrew.clikat.module.social_post.interfaces.SocialPostNavigator
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.frag_social_selct_supplier.*
import kotlinx.android.synthetic.main.toolbar_subscription.*
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 * Use the [SocialSelectSuplierFrag.newInstance] factory method to
 * create an instance of this fragment.
 */
class SocialSelectSuplierFrag : BaseFragment<FragSocialSelctSupplierBinding, SocialPostViewModel>(), SocialPostNavigator
        , BottomActionListener, DialogListener {
    private lateinit var viewModel: SocialPostViewModel

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var mDialogsUtil: DialogsUtil

    private var mBinding: FragSocialSelctSupplierBinding? = null
    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }

    private var supplierData: SupplierDataBean? = null
    private var productData: ProductDataBean? = null
    private val argument: SocialSelectSuplierFragArgs by navArgs()
    private var mSupplierList = mutableListOf<SupplierDataBean>()
    private var mProdList = mutableListOf<ProductDataBean>()
    private var mSelctSuplrTag: String? = null
    private var mBottomData: BottomDataItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigator = this

        supplierListObserver()
        productListObserver()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding
        mBinding?.color = Configurations.colors
        mBinding?.strings = textConfig

        mSelctSuplrTag = getString(R.string.select_supplier, textConfig?.supplier)

        tb_name.text = mSelctSuplrTag

        tb_back.setOnClickListener{
            navController(this).popBackStack()
        }

        if(argument.postDescData?.supplier_data?.id!=null)
        {
            supplierData = argument.postDescData?.supplier_data
            txt_supplier.text = supplierData?.name

            mSupplierList.clear()
            mSupplierList.addAll(argument.postDescData?.suplierList?: mutableListOf())
        }else
        {
            if (isNetworkConnected) {
                viewModel.getSupplierList()
            }
        }


        if(argument.postDescData?.product_data?.product_id!=null)
        {
            lyt_prod.visibility = View.VISIBLE
            productData = argument.postDescData?.product_data
            mBinding?.productItem =productData

            mProdList.clear()
            mProdList.addAll(argument.postDescData?.prodList?: mutableListOf())
        }


        txt_supplier.setOnClickListener {
            if (mSupplierList.isEmpty()) return@setOnClickListener

            mBottomData = BottomDataItem(supList = ArrayList(mSupplierList))
            BottomSocialSheetFrag.newInstance(mBottomData).show(childFragmentManager, "dialog")
        }

        lyt_prod.setOnClickListener {
            if (mProdList.isEmpty()) return@setOnClickListener

            mBottomData = BottomDataItem(prodList = ArrayList(mProdList))
            BottomSocialSheetFrag.newInstance(mBottomData).show(childFragmentManager, "dialog")
        }

        btnPost.setOnClickListener {

            when {
                supplierData == null -> {
                    onErrorOccur(mSelctSuplrTag ?: "")
                }
                productData == null -> {
                    onErrorOccur(getString(R.string.select_supplier, textConfig?.product) ?: "")
                }
                else -> {
                    argument.postDescData?.product_data = productData
                    argument.postDescData?.supplier_data = supplierData
                    argument.postDescData?.suplierList = mSupplierList
                    argument.postDescData?.prodList = mProdList

                    val action = SocialSelectSuplierFragDirections.actionSocialSelectSuplierFragToSocialSelectImagesFrag(argument.postDescData)
                    navController(this@SocialSelectSuplierFrag).navigate(action)
                }
            }
        }
    }

    private fun supplierListObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<List<SupplierDataBean>> { resource ->

            updateSupplierList(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.supplierLiveData.observe(this, catObserver)
    }

    private fun productListObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<SocialSupplierBean> { resource ->

            updateProduct(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.productLiveData.observe(this, catObserver)
    }

    private fun updateProduct(resource: SocialSupplierBean) {

        if(!appUtils.checkResturntTiming(resource.supplierData?.timing))
        {
            mDialogsUtil.openAlertDialog(activity
                    ?: requireContext(), getString(R.string.offline_supplier_tag, textConfig?.supplier), getString(R.string.ok), "", this)
            return
        }


        if (resource.prodList?.isNotEmpty() == true) {
            mProdList.clear()
            mProdList.addAll(resource.prodList?: mutableListOf())

            lyt_prod.visibility = View.VISIBLE

            resource.prodList?.firstOrNull()?.let {
                mBinding?.productItem =it
                productData=it
            }
        }

        lyt_prod.callOnClick()
    }


    private fun updateSupplierList(resource: List<SupplierDataBean>?) {

        if (resource?.isNotEmpty() == true) {
            mSupplierList.clear()

            mSupplierList.addAll(resource)
        }

        txt_supplier.callOnClick()
    }


    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    /**
     * @return layout resource id
     */
    override fun getLayoutId(): Int {
        return R.layout.frag_social_selct_supplier
    }

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): SocialPostViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(SocialPostViewModel::class.java)
        return viewModel
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onSupplierSelect(suppplier: SupplierDataBean) {
        supplierData = suppplier
        txt_supplier.text = supplierData?.name

        if (isNetworkConnected) {
            viewModel.getProductList(supplierData?.id.toString())
        }
    }

    override fun onProductSelect(product: ProductDataBean) {
        productData = product
        mBinding?.productItem = product
    }

    override fun onSucessListner() {

    }

    override fun onErrorListener() {

    }


}
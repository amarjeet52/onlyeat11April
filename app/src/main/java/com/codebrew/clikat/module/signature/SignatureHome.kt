package com.codebrew.clikat.module.signature

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.*
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.databinding.FrgmentSignatureHomeBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.SupplierDataBean
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.frgment_signature_home.*
import javax.inject.Inject

class SignatureHome : BaseFragment<FrgmentSignatureHomeBinding, SignatureViewModel>(), SignatureNavigator, SignatureAdapter.OnItemClick {

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }
    private val colorConfig by lazy { Configurations.colors }
    private lateinit var mViewModel: SignatureViewModel
    private var mBinding: FrgmentSignatureHomeBinding? = null


    val supplierList = mutableListOf<SupplierDataBean>()
    var type = ""

    @Inject
    lateinit var permissionUtil: PermissionFile

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

    var signatureAdapter: SignatureAdapter? = null


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.frgment_signature_home
    }

    override fun getViewModel(): SignatureViewModel {
        mViewModel = ViewModelProviders.of(this, factory).get(SignatureViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding
        mBinding?.color = colorConfig
        mBinding?.strings = textConfig
        setUpToolbar()
        if (arguments != null) {
            if (arguments?.containsKey("type") == true) {
                if (!arguments?.getString("type").isNullOrEmpty()) {
                    type = arguments?.getString("type").toString()
                }
            }
        }
        tvChief.layoutManager = LinearLayoutManager(requireContext())
        signatureAdapter = SignatureAdapter(supplierList, type)
        tvChief.adapter = signatureAdapter
        signatureAdapter?.settingCallback(this)
        if (type == "2") {
            tv_name.text = "Chef's Boutique"
            //need original image
/*            ivSupplierIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.chef_icon))
            ivSigntureTheme.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.chefs_screen_bg))*/

        } else if (type == "3") {
            tv_name.text = "Signature Plates"
            ivSupplierIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.plate))
            ivSigntureTheme.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.signature_plate_bg))

        } else {
            tv_name.text = "Signature Catering"
            //need original image
/*            ivSupplierIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.sign_catering))
            ivSigntureTheme.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.catering_bg))*/

        }
        //delete this when original image chnage
        ivSupplierIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.plate))
        ivSigntureTheme.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.signature_plate_bg))

        viewModel.getSupplierList("0", type)
    }

    private fun setUpToolbar() {
        //  layoutCrave.visibility = View.VISIBLE
        //   iv_supplier_logo2.setImageResource(R.drawable.ic_c)
        //   icAddress2.setImageResource(R.drawable.ic_add)
    }

    override fun onSupplierList(data: List<SupplierDataBean>?) {
        if (data != null) {
            supplierList.clear()
            supplierList.addAll(data ?: mutableListOf())
            tvChief.adapter?.notifyDataSetChanged()
        }
    }

    override fun onErrorOccur(message: String) {
        try {
            mainmenu.onSnackbar(message)
        } catch (e: Exception) {

        }
    }

    override fun onSessionExpire() {

    }

    override fun onClick(supplierDataBean: SupplierDataBean) {

        val navOptions: NavOptions = NavOptions.Builder()
                .setPopUpTo(R.id.caveSignatureDetailFrag, false)
                .build()
        val bundle = bundleOf("supplierName" to supplierDataBean.name, "supplierLogo" to supplierDataBean.logo, "supplierBannerImage" to supplierDataBean.supplier_image,
                "supplierId" to supplierDataBean.id, "branchId" to supplierDataBean.supplier_branch_id, "type" to type)
        navController(this@SignatureHome).navigate(R.id.action_signature_to_signature_detail, bundle, navOptions)

    }

}
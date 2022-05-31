package com.codebrew.clikat.module.home_screen.viewAll

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
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
import com.codebrew.clikat.data.VendorAppType
import com.codebrew.clikat.databinding.FragmentSupplierListBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.SupplierList
import com.codebrew.clikat.modal.other.English
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.home_screen.adapter.CategoryListAdapter
import com.codebrew.clikat.module.supplier_all.SupplierListNavigator
import com.codebrew.clikat.module.supplier_all.SupplierListViewModel
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.GridSpacingItemDecoration
import com.codebrew.clikat.utils.StaticFunction
import kotlinx.android.synthetic.main.fragment_supplier_list.*
import kotlinx.android.synthetic.main.nothing_found.*
import javax.inject.Inject


class ViewAllCategoriesFragment : BaseFragment<FragmentSupplierListBinding, SupplierListViewModel>(),
        SupplierListNavigator, CategoryListAdapter.CategoryDetail {


    private var mAdapter: CategoryListAdapter? = null
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
    private var categoryList: MutableList<English>? = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigator = this

        screenFlowBean = dataManager.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        clientInform = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)


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
    }


    private fun initialise() {
        tvTitleCenter?.visibility = View.GONE
        tvTitle?.visibility = View.VISIBLE
        bottom_cart?.visibility = View.GONE
        searchView?.visibility = View.GONE
        tvTitle?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        tvTitle?.textAlignment = View.TEXT_ALIGNMENT_CENTER
        tvTitle?.text=getString(R.string.choose_your_fav_category)
        tvTitle?.setTextSize(TypedValue.COMPLEX_UNIT_SP,22f)
        tvTitle?.setPadding(StaticFunction.pxFromDp(16,requireContext()),
                StaticFunction.pxFromDp(4,requireContext()),
                StaticFunction.pxFromDp(16,requireContext()),
                StaticFunction.pxFromDp(0,requireContext()))
        arguments?.let {
            categoryList = it.getParcelableArrayList("categoryList")
            viewModel.setSupplierList(categoryList?.size?:0)
        }
    }

    private fun listeners() {
        ivBack?.setOnClickListener {
            hideKeyboard()
            navController(this).popBackStack()
        }
    }

    private fun setAdapter() {
        mAdapter = CategoryListAdapter(categoryList ?: mutableListOf(), screenFlowBean?.app_type
                ?: -1, clientInform)
        recyclerView?.layoutManager = GridLayoutManager(requireContext(),2)
        recyclerView?.addItemDecoration(GridSpacingItemDecoration(false,2,StaticFunction.pxFromDp(12,requireContext()),true))
        mAdapter?.isSkipViewAllUi(true)
        mAdapter?.settingCallback(this)

        recyclerView?.adapter = mAdapter
    }


    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }


    override fun favSupplierResponse(supplierId: SupplierList?) {
        //do nothing
    }

    override fun unFavSupplierResponse(data: SupplierList?) {
        //do nothing

    }

    override fun onBranchList(supplierList: MutableList<SupplierList>) {
        //do nothing
    }


    override fun onCategoryDetail(bean: English?) {
        if (clientInform?.show_tags_for_suppliers == "1") {
            val bundle = bundleOf("tag_id" to bean?.id.toString())
            if (clientInform?.is_skip_theme == "1") {
                navController(this@ViewAllCategoriesFragment).navigate(R.id.action_frag_to_supplierListFragment, bundle)
            } else
                navController(this@ViewAllCategoriesFragment)
                        .navigate(R.id.action_supplierAll, bundle)
        } else {
            val bundle = bundleOf("title" to bean?.name,
                    "categoryId" to bean?.id,
                    "subCategoryId" to 0)
            if (clientInform?.show_ecom_v2_theme == "1" && bean?.id == null) {
                navController(this@ViewAllCategoriesFragment)
                        .navigate(R.id.action_homeFrag_to_categories)
                return
            }
            if (screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {
                if (bean?.sub_category?.count() ?: 0 > 0 && screenFlowBean?.app_type != AppDataType.Food.type) {
                    navController(this@ViewAllCategoriesFragment).navigate(R.id.actionSubCategory, bundle)
                } else if (clientInform?.is_skip_theme == "1") {
                    navController(this@ViewAllCategoriesFragment).navigate(R.id.action_frag_to_supplierListFragment, bundle)
                } else {
                    navController(this@ViewAllCategoriesFragment)
                            .navigate(R.id.action_supplierAll, bundle)
                }
            } else {
                if (screenFlowBean?.app_type != AppDataType.Food.type) {
                    if (bean?.sub_category?.count() ?: 0 > 0) {
                        navController(this@ViewAllCategoriesFragment).navigate(R.id.actionSubCategory, bundle)
                    } else {
                        bundle.putBoolean("has_subcat", true)
                        bundle.putInt("supplierId", bean?.supplier_branch_id ?: 0)
                        navController(this@ViewAllCategoriesFragment).navigate(R.id.action_productListing, bundle)
                    }
                } else {
                    bundle.putInt("supplierId", bean?.id ?: 0)
                    if (clientInform?.app_selected_template != null && clientInform?.app_selected_template == "1") {
                        navController(this@ViewAllCategoriesFragment)
                                .navigate(R.id.action_restaurantDetailNew, bundle)
                    } else {
                        navController(this@ViewAllCategoriesFragment)
                                .navigate(R.id.action_restaurantDetail, bundle)
                    }
                }
            }
        }
    }


}
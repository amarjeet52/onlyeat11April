package com.codebrew.clikat.module.all_categories

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentAllCategoryBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.English
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.all_categories.adapter.CategoryListAdapter
import com.codebrew.clikat.module.bottom_navigation.OnNavigationMenuClicked
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.configurations.Configurations

import kotlinx.android.synthetic.main.fragment_all_category.*
import kotlinx.android.synthetic.main.toolbar_app.*
import javax.inject.Inject

class CategoryFragment : BaseFragment<FragmentAllCategoryBinding, CategoryViewModel>(),
        CategoryListAdapter.OnCategoryListener, CategoryNavigator {


    private var mBinding: FragmentAllCategoryBinding? = null

    private lateinit var viewModel: CategoryViewModel

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var appUtils: AppUtils


    @Inject
    lateinit var prefHelper: PreferenceHelper

    private var settingsData: SettingModel.DataBean.SettingData? = null


    private var categoryList: MutableList<English>? = mutableListOf()

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings}
    private var navigationListeners: OnNavigationMenuClicked? = null
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_all_category
    }

    override fun getViewModel(): CategoryViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(CategoryViewModel::class.java)
        return viewModel
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this
        settingsData = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        categoryObserver()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewDataBinding.color = Configurations.colors
        viewDataBinding.string = textConfig

        with(list)
        {
            layoutManager = GridLayoutManager(context, 3)
            adapter = CategoryListAdapter(categoryList, this@CategoryFragment, appUtils)
        }


        if (viewModel.categoryLiveData.value != null) {
            refreshAdapter(viewModel.categoryLiveData.value)
        } else {
            if (isNetworkConnected) {
                viewModel.getCategories()
            }
        }

        if(settingsData?.show_ecom_v2_theme == "1"){
            category_text?.visibility =View.GONE
            toolbar?.visibility =View.VISIBLE
            toolbar?.background = ContextCompat.getDrawable(requireActivity(), R.drawable.background_toolbar_bottom_radius)
            toolbar.elevation = 0f
            tb_title?.text = textConfig?.categories
            divider?.visibility = View.GONE
            tb_back?.setOnClickListener { v: View? -> Navigation.findNavController(requireView()).popBackStack() }
        }
        else if(settingsData?.is_juman_flow_enable=="1"){
            ivNavigation?.visibility=View.VISIBLE
            ivNavigation?.setOnClickListener {
                navigationListeners?.onNavigationMenuChanged()
            }
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNavigationMenuClicked)
            navigationListeners = context
    }
    private fun categoryObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<MutableList<English>> { resource ->
            refreshAdapter(resource)
        }
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.categoryLiveData.observe(this, catObserver)
    }

    private fun refreshAdapter(resource: MutableList<English>?) {

        categoryList?.clear()
        categoryList?.addAll(resource ?: mutableListOf())

        list.adapter?.notifyDataSetChanged()
    }


    override fun onCategoryItem(item: English?) {

        val bundle = Bundle()

        if(settingsData?.is_juman_flow_enable=="1"){
            bundle.putParcelable("categoryDetail",item)
            navController(this@CategoryFragment).navigate(R.id.action_categories_to_jumaCategoies, bundle)
        }
      else{  if (item?.sub_category?.isEmpty() == true) {
            bundle.putInt("categoryId", item.id ?: 0)
            bundle.putBoolean("has_subcat", true)
            bundle.putString("title", item.name)
            navController(this@CategoryFragment).navigate(R.id.action_categories_to_productListing, bundle)
        } else {
            val items = item?.category_flow?.split(">".toRegex())?.dropLastWhile { it.isEmpty() }?.toMutableList()

            items?.removeAt(0)
            when {
                items?.get(0)?.contains("SubCategory") == true -> {

                    bundle.putString("title", item.name)
                    bundle.putStringArrayList("categoryFlow", items as ArrayList<String>?)
                    bundle.putInt("categoryId", item.id ?: 0)
                    navController(this@CategoryFragment).navigate(R.id.action_categories_to_SubCategory, bundle)
                }
                items?.get(0)?.contains("Suppliers") == true -> {

                    bundle.putString("title", item.name)
                    bundle.putInt("subCategoryId", 0)
                    // 1 for yes 0 for no
                    bundle.putInt("categoryId", item.id ?: 0)
                    bundle.putStringArrayList("categoryFlow", items as ArrayList<String>?)
                    navController(this@CategoryFragment)
                            .navigate(R.id.action_categories_to_supplierAll, bundle)
                }
                items?.get(0)?.contains("Pl") == true -> {
                    bundle.putInt("categoryId", item.id ?: 0)
                    bundle.putBoolean("has_subcat", true)
                    bundle.putString("title", item.name)
                    navController(this@CategoryFragment).navigate(R.id.action_categories_to_productListing, bundle)
                }
            }
        }}

    }


    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }
}

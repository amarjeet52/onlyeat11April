package com.codebrew.clikat.module.filter

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.view.View
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.extension.refreshFragment
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.databinding.FragmentFilterBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.*
import com.codebrew.clikat.module.filter.BottomSheetFragment.Companion.varientData
import com.codebrew.clikat.module.filter.adapter.FilterListAdapter
import com.codebrew.clikat.module.filter.category_selection.CategoryFragment
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.configurations.Configurations
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_filter.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

private const val ARG_PARAM1 = "position"
private const val ARG_PARAM2 = "categoryList"

class FilterFragment : BaseFragment<FragmentFilterBinding, FilterViewModel>(), FilterListAdapter.VarientListCallback, FilterNavigator {


    private val CATEGORY_TAG = "category"
    private val SUBCATEGORY_TAG = "subcategory"
    private val SUBCAT_CATEGORY_TAG = "subcat_category"

    private var fragName = ""

    private var tagId = 1

    private var adapter: FilterListAdapter? = null

    private lateinit var modelList: MutableList<FilterVarientCatModel.DataBean>

    private lateinit var categoryList: ArrayList<English>

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var factory: ViewModelProviderFactory


    @Inject
    lateinit var appUtils: AppUtils


    private lateinit var mFilterViewModel: FilterViewModel


    private var mBinding: FragmentFilterBinding? = null

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }
    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        screenFlowBean = dataManager.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        mFilterViewModel.navigator = this

        varientObserver()

        categoryList = ArrayList()
        modelList = ArrayList()

        adapter = FilterListAdapter(modelList)

        adapter?.settingCallback(this)

        if (arguments != null && arguments?.containsKey(ARG_PARAM2) == true) {
            categoryList.addAll(arguments?.getParcelableArrayList(ARG_PARAM2) ?: arrayListOf())
        }

        EventBus.getDefault().register(this)

    }

    private fun varientObserver() {

        val catObserver = Observer<FilterVarientCatModel> { resource ->


            if (resource.data.isNotEmpty() || resource.brands.isNotEmpty()) {
                if (!(parentFragment as BottomSheetFragment).adapter?.isCatgyExit("Others")!!)
                    (parentFragment as BottomSheetFragment).adapter?.addCategory("Others")
            } else {
                if ((parentFragment as BottomSheetFragment).adapter?.isCatgyExit("Others") == true) {
                    (parentFragment as BottomSheetFragment).adapter?.removeCategory("Others")
                }
            }

            modelList.clear()


            if (resource.data.size > 0 && screenFlowBean?.app_type==AppDataType.Food.type) {
                //Toast.makeText(FilterScreenActivity.this, "Data",  st.LENGTH_SHORT).show();
                modelList.addAll(resource.data)
            }

            if (resource.brands.size > 0) {

                val dataBean = FilterVarientCatModel.DataBean()

                dataBean.variant_name = "brands"
                dataBean.brands_values = resource.brands

                modelList.add(dataBean)
                /*             grpBrand.setVisibility(View.VISIBLE)
                             brandsBeans.addAll(catModel.brands)
                             brandListAdapter.notifyDataSetChanged()*/
            }

        }

        mFilterViewModel.varientCatLiveData.observe(this, catObserver)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding

        viewDataBinding.color = Configurations.colors
        viewDataBinding.strings = textConfig


        if (varientData.isFilterApply) {
            settingFilterData()
        } else {
            val supplierIds= varientData.supplierId
            varientData = FilterVarientData()
            varientData.supplierId=supplierIds
            clearFilterData()
        }

        settingLayout()

        if (categoryList.isNotEmpty()) {
            gp_category?.visibility = View.VISIBLE
            addFragment(CategoryFragment.newInstance(categoryList), CATEGORY_TAG, true)
        }


        /* updateLayout()

         settingLayout()*/
    }

    private fun settingLayout() {

        sort_radioGroup.setOnCheckedChangeListener { group, checkedId ->

            val checkedRadioButton = group.findViewById(checkedId) as RadioButton

            val isChecked = checkedRadioButton.isChecked


            if (isChecked)
                varientData.sortBy = checkedRadioButton.text.toString()


        }


        disc_radioGroup.setOnCheckedChangeListener { group, checkedId ->

            val checkedRadioButton = group.findViewById(checkedId) as RadioButton

            val isChecked = checkedRadioButton.isChecked


            varientData.isDiscount = isChecked && checkedRadioButton.text.toString().toLowerCase().contains("yes")

        }

        avail_radioGroup.setOnCheckedChangeListener { group, checkedId ->

            val checkedRadioButton = group.findViewById(checkedId) as RadioButton

            val isChecked = checkedRadioButton.isChecked


            varientData.isAvailability = isChecked && checkedRadioButton.text.toString().toLowerCase().contains("yes")

        }

        rangeSeekbar.setOnRangeSeekbarChangeListener { minValue, maxValue ->

                varientData.minPrice = minValue.toInt()

            if (maxValue.toInt() != 50000)
                varientData.maxPrice = maxValue.toInt()

            tv_price_range.text = getString(R.string.filter_price_tag, AppConstants.CURRENCY_SYMBOL, minValue.toInt(), maxValue.toInt())
        }


        val disc_no = SpannableString(getString(R.string.disc_yes_text,textConfig?.products?:""))
        disc_no.setSpan(RelativeSizeSpan(0.7f), 4, disc_no.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val disc_yes = SpannableString(getString(R.string.disc_no_text,textConfig?.products?:""))
        disc_yes.setSpan(RelativeSizeSpan(0.7f), 3, disc_yes.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        rb_disc_yes.text = disc_no
        rb_disc_no.text = disc_yes


        val avail_yes = SpannableString(getString(R.string.avail_yes_text,textConfig?.products?:""))
        avail_yes.setSpan(RelativeSizeSpan(0.7f), 4, avail_yes.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val avail_no = SpannableString(getString(R.string.avail_no_text,textConfig?.products?:""))
        avail_no.setSpan(RelativeSizeSpan(0.7f), 3, avail_no.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        rb_avail_yes.text = avail_yes
        rb_avail_no.text = avail_no
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)

    }


    private fun clearFilterData() {

        varientData.isFilterApply=false
        varientData.isAvailability = true
        varientData.sortBy = "Price: Low to High"
        varientData.isDiscount = false
        varientData.maxPrice = 50000
        varientData.minPrice = 0
        varientData.catId = 0
        varientData.catNames.clear()
        varientData.varientID.clear()
        varientData.subCatId.clear()
        varientData.brandId.clear()

        if ((parentFragment as BottomSheetFragment).adapter?.isCatgyExit("Others")!!) {
            (parentFragment as BottomSheetFragment).adapter?.removeCategory("Others")
        }

        val manager = childFragmentManager

        if (manager.backStackEntryCount > 1) {
            for (i in 1..manager.backStackEntryCount) {
                manager.popBackStack()
            }
        }

        settingFilterData()
    }

    private fun settingFilterData() {

        when (varientData.sortBy) {
            "Price: Low to High" -> rb_lh.isChecked = true

            "Price: High to Low" -> rb_hl.isChecked = true

            "Popularity" -> rb_popular.isChecked = true
        }

        rangeSeekbar.setMinStartValue(varientData.minPrice.toFloat()).setMaxStartValue(varientData.maxPrice.toFloat()).apply()

        if (varientData.catId != 0) {
            //rvFilterList.setVisibility(View.VISIBLE)
            getVarientByCategory(varientData.catId)
        } else {
            modelList.clear()
            adapter?.notifyDataSetChanged()
        }


        if (varientData.isAvailability)
            rb_avail_yes.isChecked = true
        else
            rb_avail_no.isChecked = true

        if (varientData.isDiscount)
            rb_disc_yes.isChecked = true
        else
            rb_disc_no.isChecked = true

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFragEvent(fragEvent: UpdateFilterEvent) {
        updateLayout(fragEvent)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: FilterEvent) {

        when (event.type) {
            AppConstants.CATEGORY_SELECT -> {
                if (event.catId != varientData.catId) {
                    varientData.catId = 0
                    varientData.catNames.clear()
                    varientData.subCatId.clear()
                    varientData.varientID.clear()
                    varientData.brandId.clear()
                }

                this.fragName = event.name
                varientData.catId = event.catId
                //

                if (!varientData.catNames.contains(event.name))
                    varientData.catNames.add(event.position, event.name)

                /*  if (getCategryRefreshListener() != null) {
                  getCategryRefreshListener().onCatgryRefresh()
              }
  */
                if (event.subcatList?.isNotEmpty() == true) {
                    subcategory(event.catId, true, SUBCATEGORY_TAG, event.position, event.subcatList)
                }else
                {
                    varientData.subCatId.add(event.catId)
                }
                getVarientByCategory(event.catId)
            }
            AppConstants.SUBCATEGORY_DETAIL -> {
                fragName = event.name

                varientData.subCatId.add(event.catId)

                if (!varientData.catNames.contains(event.name))
                    varientData.catNames.add(event.position, event.name)

                subcategory(event.catId, false, SUBCAT_CATEGORY_TAG, event.position, event.subcatList)
            }
            AppConstants.SUBCATEGORY_REMOVE -> {
                varientData.subCatId.remove(event.catId)

                if (!varientData.catNames.contains(event.name))
                    varientData.catNames.remove(event.name)
            }
            AppConstants.SUBCATEGORY_ADD -> {
                varientData.subCatId.add(event.catId)

                if (!varientData.catNames.contains(event.name))
                    varientData.catNames.add(event.name)
            }
            AppConstants.SUBCATEGORY_BACKPRESS -> checkBackStack()
            AppConstants.SUBCATEGORY_CATEGORY -> {
                fragName = event.name

                varientData.subCatId.add(event.catId)

                if (!varientData.catNames.contains(event.name))
                    varientData.catNames.add(event.name)

                subcategory(event.catId, false, SUBCAT_CATEGORY_TAG + tagId, event.position + tagId, event.subcatList)
                tagId += 1
            }


            // Toast.makeText(getActivity(), event.message, Toast.LENGTH_SHORT).show();
        }


        // Toast.makeText(getActivity(), event.message, Toast.LENGTH_SHORT).show();
    }

    private fun checkBackStack() {

        val manager = childFragmentManager

        if (manager.backStackEntryCount > 1) {
            manager.popBackStackImmediate()
        } else {

            /*  if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                  drawerLayout.closeDrawer(GravityCompat.END)
              } else {
                  val returnIntent = Intent()

                  returnIntent.putExtra("varientData", varientData)
                  setResult(AppCompatActivity.RESULT_CANCELED, returnIntent)
                  finish()
              }*/
        }
    }

    private fun updateDisLayout() {

        if (varientData.isDiscount) {
            rb_disc_yes.isChecked = true
        } else {
            rb_disc_no.isChecked = true
        }
    }


    private fun updateLayout(fragEvent: UpdateFilterEvent) {

        gp_sort?.visibility = View.GONE
        gp_price?.visibility = View.GONE
        gp_disc?.visibility = View.GONE
        gp_avail?.visibility = View.GONE
        gp_category?.visibility = View.GONE
        gp_other?.visibility = View.GONE



        if (fragEvent.filterPos != -1) {
            when (fragEvent.filterPos) {
                4 -> {
                    gp_avail?.visibility = View.VISIBLE
                    updateAvailLayout()
                }
                1 -> {
                    gp_sort?.visibility = View.VISIBLE
                    updateSortView()
                }
                2 -> {
                    gp_price?.visibility = View.VISIBLE
                    updatePriceLyt()
                }
                3 -> {
                    gp_disc.visibility = View.VISIBLE
                    updateDisLayout()
                }
                0 -> {
                    gp_category?.visibility = View.VISIBLE
                    refreshCategoryList()
                }
                5 -> {
                    gp_other?.visibility = View.VISIBLE

                    updateVarientLyt()
                }
            }
        } else {
            gp_category?.visibility = View.VISIBLE
            refreshCategoryList()
        }

        if (fragEvent.clearType) {
            clearFilterData()
        }
    }

    private fun refreshCategoryList() {
        val fragment = childFragmentManager.findFragmentByTag(CATEGORY_TAG)

        if (fragment == null) {
            addFragment(CategoryFragment.newInstance(categoryList), CATEGORY_TAG, true)
        } else {
            val bundle = Bundle()
            bundle.putParcelableArrayList("category", categoryList)
            fragment.arguments = bundle
            childFragmentManager.refreshFragment(fragment)
        }

    }

    private fun updatePriceLyt() {
        rangeSeekbar.setMinStartValue(varientData.minPrice.toFloat()).setMaxStartValue(varientData.maxPrice.toFloat()).apply()
    }

    private fun updateSortView() {
        when (varientData.sortBy) {
            "Price: Low to High" -> rb_lh.isChecked = true

            "Price: High to Low" -> rb_hl.isChecked = true

            "Popularity" -> rb_popular.isChecked = true
        }
    }

    private fun updateVarientLyt() {
        if(!modelList.isNullOrEmpty())
         rv_varient_list.visibility = View.VISIBLE
        rv_varient_list.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        rv_varient_list.adapter = adapter
    }

    private fun updateAvailLayout() {

        if (varientData.isAvailability) {
            rb_avail_yes.isChecked = true
        } else {
            rb_avail_no.isChecked = true
        }
    }

    private fun addFragment(fragment: Fragment, TAG: String, enterStatus: Boolean) {

        // if (!isAdded) return
        val manager = childFragmentManager
        val ft = manager.beginTransaction()

        if (enterStatus) {
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
        } else {
            ft.setCustomAnimations(R.anim.slide_out_left, R.anim.slide_in_right)
        }

        ft.add(R.id.stub_category, fragment, TAG).addToBackStack(TAG)
        ft.commit()
    }

    private fun subcategory(categoryId: Int, catStatus: Boolean, TagName: String, position: Int, subCatList: List<SubCategory>?) {

        if (catStatus)
            addFragment(SubCategoryFragment.newInstance(subCatList as ArrayList<SubCategory>?, fragName), TagName, true)
        else
            addFragment(SubCat_CategoryFragment.newInstance(subCatList as ArrayList<SubCategory>?, fragName, position), TagName, true)
    }


    private fun getVarientByCategory(catId: Int) {
        if (isNetworkConnected) {
            viewModel.getVarientByCategory(catId.toString())
        }
    }

    override fun addVarientId(id: Int, listPosition: Int) {

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int, catList: ArrayList<English>) =
                FilterFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_PARAM1, param1)
                        putParcelableArrayList(ARG_PARAM2, catList)
                    }
                }
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_filter
    }

    override fun getViewModel(): FilterViewModel {
        mFilterViewModel = ViewModelProviders.of(this, factory).get(FilterViewModel::class.java)
        return mFilterViewModel
    }
}

package com.codebrew.clikat.module.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseDialog
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.ProductData
import com.codebrew.clikat.data.model.others.FilterInputModel
import com.codebrew.clikat.databinding.LayoutFilterNewBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.English
import com.codebrew.clikat.modal.other.FilterResponseEvent
import com.codebrew.clikat.module.filter.adapter.CraveCategoryAdaptor
import com.codebrew.clikat.module.filter.adapter.CraveFilterSortAdaptor
import com.codebrew.clikat.module.filter.adapter.SelectedCategoryAdaptor
import com.codebrew.clikat.module.searchProduct.CraveSortByModel
import com.codebrew.clikat.utils.StaticFunction
import dagger.android.support.AndroidSupportInjection
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class CraveFilterBottomSheet : BaseDialog(), CraveCategoryAdaptor.CategorySelectListener, FilterNavigator, View.OnClickListener {

    lateinit var binding: LayoutFilterNewBinding

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var appUtils: AppUtils

    private var sortAdaptor: CraveFilterSortAdaptor? = null
    private var mFilterViewModel: FilterViewModel? = null
    private var mCategoryAdaptor: CraveCategoryAdaptor? = null
    private var mSelectedCategoryAdaptor: SelectedCategoryAdaptor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        // Inflate the layout for this fragment
        AndroidSupportInjection.inject(this)
        binding = DataBindingUtil.inflate(inflater, R.layout.layout_filter_new, container, false)
        mFilterViewModel = ViewModelProviders.of(this, factory).get(FilterViewModel::class.java)
        mFilterViewModel?.navigator = this
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryObserver()
        productListObserver()

        val items = arrayListOf(CraveSortByModel(type = "1", name = "Rating"), CraveSortByModel(type = "2", name = "Free delivery"),
                CraveSortByModel(type = "3", name = "Nearest"), CraveSortByModel(type = "4", name = "Fast delivery"))

        binding.rvSortBy.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvCategory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSelectedItens.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.rvCategory.isNestedScrollingEnabled = false
        binding.rvSortBy.isNestedScrollingEnabled = false

        mSelectedCategoryAdaptor = SelectedCategoryAdaptor(arrayListOf())
        binding.rvSelectedItens.adapter = mSelectedCategoryAdaptor
        sortAdaptor = CraveFilterSortAdaptor(items)
        binding.rvSortBy.adapter = sortAdaptor

        if (isNetworkConnected) {
            binding.progressBar.visibility = View.VISIBLE
            mFilterViewModel?.getCategories()
        }

        binding.tvSubmit.setOnClickListener {


            binding.progressBar.visibility = View.VISIBLE

            var low_to_high = 0

            var popularity = 0

            when (BottomSheetFragment.varientData.sortBy) {
                "Price: Low to High" -> low_to_high = 1

                "Price: High to Low" -> low_to_high = 0

                "Popularity" -> popularity = 1
            }


            val categories = mSelectedCategoryAdaptor?.getSelectedCategories()

            val ids = ArrayList<Int>()
            categories?.forEach {
                ids.add(it.id ?: 0)
            }

            val inputModel = FilterInputModel()
            inputModel.languageId = StaticFunction.getLanguage(activity).toString()
            inputModel.subCategoryId?.addAll(ids)

            val mLocUser = dataManager.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)

            if (mLocUser != null) {
                inputModel.latitude = mLocUser.latitude ?: ""
                inputModel.longitude = mLocUser.longitude ?: ""
            }
            inputModel.low_to_high = low_to_high.toString()
            inputModel.is_popularity = popularity
            if (arguments?.containsKey("product_name") == true) {
                inputModel.product_name = arguments?.getString("product_name")
            }
            inputModel.is_availability = 1.toString()
            inputModel.is_discount = 1.toString()
            inputModel.max_price_range = 100000.toString()
            inputModel.min_price_range = 0.toString()
            inputModel.variant_ids?.addAll(BottomSheetFragment.varientData.varientID)
            inputModel.supplier_ids?.addAll(BottomSheetFragment.varientData.supplierId)
            inputModel.brand_ids?.addAll(BottomSheetFragment.varientData.brandId)


            if (isNetworkConnected) {
                mFilterViewModel?.getProductList(inputModel)
            }

        }

    }

    private fun productListObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<ProductData> { resource ->

            binding.progressBar.visibility = View.GONE

            val responseModel = FilterResponseEvent()
            responseModel.status = "success"

            if (resource?.product?.isNotEmpty() == true) {

                responseModel.productlist = resource.product ?: arrayListOf()
            }

            EventBus.getDefault().post(responseModel)

            dismiss()
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mFilterViewModel?.productLiveData?.observe(this, catObserver)
    }


    private fun categoryObserver() {

        val catObserver = Observer<MutableList<English>> { resource ->

            binding.progressBar.visibility = View.GONE
            if (resource.isNotEmpty()) {
                mCategoryAdaptor = CraveCategoryAdaptor(resource.toList() as ArrayList<English>)
                mCategoryAdaptor?.settingCallback(this)
                binding.rvCategory.adapter = mCategoryAdaptor
            }
        }

        mFilterViewModel?.categoryLiveData?.observe(this, catObserver)
    }


    override fun onErrorOccur(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.root.onSnackbar(message)

    }

    override fun onSessionExpire() {
        binding.progressBar.visibility = View.GONE
        openActivityOnTokenExpire()
    }

    override fun onClick(v: View?) {

    }

    override fun onCategorySelected(english: English) {
        mSelectedCategoryAdaptor?.addCategory(english)
    }
}
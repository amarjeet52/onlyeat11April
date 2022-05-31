package com.codebrew.clikat.module.filter


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.addFragment
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseDialog
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.ProductData
import com.codebrew.clikat.data.model.others.FilterInputModel
import com.codebrew.clikat.databinding.FragmentBottomSheetBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.English
import com.codebrew.clikat.modal.other.FilterResponseEvent
import com.codebrew.clikat.modal.other.FilterVarientData
import com.codebrew.clikat.modal.other.UpdateFilterEvent
import com.codebrew.clikat.utils.GeneralFunctions
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.configurations.Configurations
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*
import kotlinx.android.synthetic.main.toolbar_filter.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
class BottomSheetFragment : BaseDialog(), FilterCatAdapter.FilterCallback, FilterNavigator, View.OnClickListener {


    var selectedPos = 0

    var adapter: FilterCatAdapter? = null

    lateinit var binding: FragmentBottomSheetBinding

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var appUtils: AppUtils

    private var mFilterViewModel: FilterViewModel? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        AndroidSupportInjection.inject(this)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_sheet, container, false)
        binding.color = Configurations.colors
        binding.strings = appUtils.loadAppConfig(0).strings
        mFilterViewModel = ViewModelProviders.of(this, factory).get(FilterViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFilterViewModel?.navigator = this
        productListObserver()
        categoryObserver()

        adapter = FilterCatAdapter(activity)
        adapter?.settingCallback(this)
        rvfilter_list.adapter = adapter

        varientData = if (arguments?.containsKey("varientData") == true) {
            arguments?.getParcelable("varientData")!!
        } else {
            FilterVarientData()
        }


        clickListner()

        if (isNetworkConnected) {
            progress_bar?.visibility = View.VISIBLE
            mFilterViewModel?.getCategories()
        }

    }

    private fun clickListner() {
        btn_apply.setOnClickListener(this)
        img_back.setOnClickListener(this)
        tv_reset.setOnClickListener(this)
    }

    override fun onfilterSelect(position: Int) {

        if (selectedPos != position) {
            selectedPos = position
            /*       val fragment = childFragmentManager.findFragmentByTag("filter")

                   val bundle=Bundle()
                   bundle.putInt("position",position)
                   fragment?.arguments=bundle

                   childFragmentManager.refreshFragment(fragment)*/

            val updateFilter = UpdateFilterEvent()

            updateFilter.clearType = false
            updateFilter.filterPos = position

            EventBus.getDefault().post(updateFilter)
        }


    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.btn_apply -> {

                if (validateFilterData()) {
                    setFilterData()
                }
            }
            R.id.img_back -> {

                /*val responseModel= FilterResponseEvent()

                 responseModel.filterModel=varientData

                 responseModel.status="cancel"

                 EventBus.getDefault().post(responseModel)*/

                dismiss()
            }
            R.id.tv_reset -> {

                val updateFilter = UpdateFilterEvent()

                updateFilter.clearType = true
                updateFilter.filterPos = -1

                selectedPos = 0
                adapter?.refreshAdapter()

                EventBus.getDefault().post(updateFilter)

            }
        }
    }

    private fun setFilterData() {

        var low_to_high = 0

        var popularity = 0

        when (varientData.sortBy) {
            "Price: Low to High" -> low_to_high = 1

            "Price: High to Low" -> low_to_high = 0

            "Popularity" -> popularity = 1
        }

        val inputModel = FilterInputModel()
        inputModel.languageId = StaticFunction.getLanguage(activity).toString()
        inputModel.subCategoryId?.addAll(varientData.subCatId)

        val mLocUser = dataManager.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)

        if (mLocUser != null) {
            inputModel.latitude = mLocUser.latitude ?: ""
            inputModel.longitude = mLocUser.longitude ?: ""
        }
        inputModel.low_to_high = low_to_high.toString()
        inputModel.is_popularity = popularity
        inputModel.is_availability = (if (varientData.isAvailability) 1 else 0).toString()
        inputModel.is_discount = (if (varientData.isDiscount) 1 else 0).toString()
        if (arguments?.containsKey("product_name") == true) {
            inputModel.product_name = arguments?.getString("product_name")
        }
        inputModel.max_price_range = varientData.maxPrice.toString()
        inputModel.min_price_range = varientData.minPrice.toString()
        inputModel.variant_ids?.addAll(varientData.varientID)
        inputModel.supplier_ids?.addAll(varientData.supplierId)
        inputModel.brand_ids?.addAll(varientData.brandId)


        if (isNetworkConnected) {
            mFilterViewModel?.getProductList(inputModel)
        }
    }


    private fun productListObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<ProductData> { resource ->

            varientData.isFilterApply=true

            val responseModel = FilterResponseEvent()
            responseModel.filterModel = varientData
            responseModel.status = "success"

            if (resource?.product?.isNotEmpty() == true) {

                responseModel.productlist = resource.product ?: arrayListOf()
            }

            EventBus.getDefault().post(responseModel)

            dismiss()
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mFilterViewModel?.productLiveData?.observe(viewLifecycleOwner, catObserver)
    }

    private fun categoryObserver() {
        val catObserver = Observer<MutableList<English>> { resource ->

           // val filterList = resource.filter { it.id == varientData.catId }
            progress_bar?.visibility = View.GONE

            childFragmentManager.addFragment(R.id.frameLayout, FilterFragment.newInstance(0,  (resource as ArrayList<English>) ?: arrayListOf()), "filter")
        }

        mFilterViewModel?.categoryLiveData?.observe(this, catObserver)
    }


    private fun validateFilterData(): Boolean {
        return when {
            varientData.maxPrice <= 0 -> {

                GeneralFunctions.showSnackBar(binding.root, getString(R.string.select_maximum_price_alert), activity)
                false
            }
            /*  varientData.catNames.size <= 0 -> {
                  GeneralFunctions.showSnackBar(binding.root, getString(R.string.select_category_alert), activity)
                  false
              }*/
            else -> true
        }

    }

    companion object {
        var varientData: FilterVarientData = FilterVarientData()
    }

    override fun onErrorOccur(message: String) {
        binding.root.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }


}


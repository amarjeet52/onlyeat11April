package com.codebrew.clikat.module.pre_delivery

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.app_utils.extension.loadImage
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.LayoutBookTableBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.slots.SupplierSlots
import com.codebrew.clikat.module.agent_time_slot.AgentViewModel
import com.codebrew.clikat.module.cart.SCHEDULE_REQUEST_DINE_IN
import com.codebrew.clikat.module.cart.schedule_order.BookTableSlotsActivity
import com.codebrew.clikat.preferences.DataNames
import com.google.gson.Gson
import javax.inject.Inject


class BookTableDetailFragment : BaseFragment<LayoutBookTableBinding, AgentViewModel>() {

    @Inject
    lateinit var factory: ViewModelProviderFactory

    private lateinit var viewModel: AgentViewModel
    private lateinit var mBinding: LayoutBookTableBinding
    var supplierBranchId = 0
    var supplierLogo=""
    @Inject
    lateinit var mDataManager: PreferenceHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding

        supplierBranchId = arguments?.getInt("branchId", 0)!!

        mBinding.tvName.text = arguments?.getString("supplierName", "")!!

         supplierLogo = arguments?.getString("supplierLogo", "")!!
        val supplierBanner = arguments?.getString("supplierBanner", "")!!
        mBinding.ivSupplierIcon.loadImage(supplierLogo)



        mBinding.tvButton.setOnClickListener {

            requireActivity().launchActivity<BookTableSlotsActivity>(SCHEDULE_REQUEST_DINE_IN) {
                putExtra("supplierId", arguments?.getInt("supplierId")?.toString())
                putExtra("dineIn", true)
                putExtra("supplierBranchId", supplierBranchId)
                putExtra("latitude", "30.70067")
                putExtra("longitude", "76.76414")

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SCHEDULE_REQUEST_DINE_IN && resultCode == Activity.RESULT_OK) {

            val selectedDateTimeForScheduling: SupplierSlots? = data?.getParcelableExtra("slotDetail")

            mDataManager.setkeyValue(DataNames.SAVED_TABLE_DATA, Gson().toJson(selectedDateTimeForScheduling))

            val bundle = bundleOf("supplierId" to data?.getStringExtra("supplier_id")?.toInt(),
                    "supplierName" to mBinding.tvName.text.toString(),
                    "supplierLogo" to supplierLogo,
                    "branchId" to supplierBranchId)


            findNavController().navigate(R.id.action_book_table_detail_to_restuarant_details, bundle)

        }
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.layout_book_table
    }

    override fun getViewModel(): AgentViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(AgentViewModel::class.java)
        return viewModel
    }

}
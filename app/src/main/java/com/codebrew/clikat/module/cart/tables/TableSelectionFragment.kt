package com.codebrew.clikat.module.cart.tables

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.app_utils.extension.loadPlaceHolder
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.ListItem
import com.codebrew.clikat.databinding.ItemTimeslotViewBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.other.SupplierItem
import com.codebrew.clikat.module.roadmap.RoadMapActivity
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.Utils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.dialog_table_booked.*
import kotlinx.android.synthetic.main.item_timeslot_view.*
import kotlinx.android.synthetic.main.nothing_found.*
import org.greenrobot.eventbus.EventBus
import zendesk.belvedere.BelvedereUi.showDialog
import javax.inject.Inject

class TableSelectionFragment : BaseFragment<ItemTimeslotViewBinding, TablesViewModel>(), TablesListNavigator {

    val adapter = RestaurantsRecyclerAdapter()
    val list = ArrayList<ListItem?>()
    private var requestedFromCart = "1"
    private lateinit var viewModel: TablesViewModel

    private var mBinding: ItemTimeslotViewBinding? = null
    var hashMap: java.util.HashMap<*, *> ?=null
    var settingData: SettingModel.DataBean.SettingData? = null

    var startTime = ""
    var endTime = ""

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var dataManager: DataManager

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.item_timeslot_view

    override fun getViewModel(): TablesViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(TablesViewModel::class.java)
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this
        settingData = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding
        tv_title?.text = getString(R.string.select_table)
        tv_sub_title?.visibility = View.INVISIBLE
        tvSelect?.visibility = View.VISIBLE

        adapter.setListData(list)
        val layoutManager = GridLayoutManager(context, 2)
        rv_timeperiod_slot?.layoutManager = layoutManager
        rv_timeperiod_slot?.adapter = adapter



        if (arguments != null) {

            if(arguments?.containsKey("map2")==true){
              val  supplierItem:HashMap<*,*> = arguments?.getSerializable("map2") as HashMap<*, *>
                startTime = supplierItem["startTime"].toString()
                endTime = supplierItem["endTime"].toString()
            }

            if (arguments?.containsKey("map") == true) {
                val b = this.arguments
                if (b?.getSerializable("map") != null) {
                    hashMap = b.getSerializable("map") as HashMap<*, *>
                    loadAvailableTableListNew(true, hashMap!!)
                }
            }
            loadAvailableTableList(true)

            requestedFromCart = arguments?.getString("requestFromCart").toString()
        }

        tvSelect?.setOnClickListener {
            if (adapter.selectedPosition < 0) {
                Toasty.error(requireActivity(), getString(R.string.table_selection_error_message))
                return@setOnClickListener
            }
            val selectedModel = list[adapter.selectedPosition]
            selectedModel?.requestedFrom = requestedFromCart
//            EventBus.getDefault().post(selectedModel ?: ListItem())
//            requireActivity().finishAffinity()

            bookTableWithSchedule(selectedModel)
        }

        onRecyclerViewScrolled()

        Utils.loadAppPlaceholder(settingData?.table_selection ?: "")?.let {
            if (it.app?.isNotEmpty() == true)
                ivPlaceholder.loadPlaceHolder(it.app)

            if (it.message?.isNotEmpty() == true) {
                tvText.text = it.message
            }
        }
    }


    private fun bookTableWithSchedule(item: ListItem?) {

        val tempRequestHolder:HashMap<String,String?> = hashMapOf(
                "user_id" to dataManager.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING).toString(),
                "table_id" to item?.id.toString(),
                "slot_id" to (hashMap?.get("slot_id")).toString(),
                "schedule_date" to startTime,
                "schedule_end_date" to endTime,
                "supplier_id" to item?.supplierId.toString(),
                "branch_id" to (hashMap?.get("branch_id").toString())
        )

        viewModel.makeBookingAccordingToSlot(tempRequestHolder)
    }

    private fun loadAvailableTableList(loader: Boolean) {
        val tempRequestHolder = hashMapOf(
                "slot_id" to hashMap!!["slot_id"],
                "offset" to list.size,
                "limit" to 20,
                "supplier_id" to hashMap!!["supplier_id"],
                "branch_id" to hashMap!!["branch_id"]
        )
        viewModel.getListOfTablesAccordingToSlot(tempRequestHolder, loader)
    }

    private fun loadAvailableTableListNew(loader: Boolean, hashMap: java.util.HashMap<*, *>) {
        val tempRequestHolder = hashMapOf(
                "slot_id" to hashMap["slot_id"],
                "offset" to hashMap["offset"],
                "limit" to hashMap["limit"],
                "supplier_id" to hashMap["supplier_id"],
                "branch_id" to hashMap["branch_id"]
        )
        viewModel.getListOfTablesAccordingToSlot(tempRequestHolder, loader)
    }

    override fun onTableListReceived(list: List<ListItem?>?) {
        list?.let { this.list.addAll(it) }
        adapter.notifyDataSetChanged()
    }

    override fun onTableSuccessfullyBooked() {
        showBookTableDialog()
        Toast.makeText(requireContext(),"Success",Toast.LENGTH_LONG).show()
    }

    private fun showBookTableDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_table_booked)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.ivTick.setOnClickListener {
            dialog.dismiss()
            requireActivity().launchActivity<RoadMapActivity>()
            requireActivity().finishAffinity()
        }
        dialog.show()
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    private fun onRecyclerViewScrolled() {
        rv_timeperiod_slot?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val isPagingActive = list.size % 20 == 0
                if (!recyclerView.canScrollVertically(1) && isPagingActive) {
                    loadAvailableTableList(false)
                }
            }
        })
    }

}
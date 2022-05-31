package com.codebrew.clikat.module.home_screen


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.codebrew.clikat.R
import com.codebrew.clikat.base.BaseDialog
import com.codebrew.clikat.data.SortBy
import com.codebrew.clikat.databinding.DialogSuppliersSortByBinding
import com.codebrew.clikat.modal.other.FiltersSupplierList
import com.codebrew.clikat.modal.other.SortByData
import com.codebrew.clikat.module.home_screen.adapter.SortByListAdapter
import com.codebrew.clikat.module.home_screen.listeners.OnSortByListenerClicked
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.dialog_suppliers_sort_by.*

class SortByFilterDialog : BaseDialog(), OnSortByListenerClicked {


    private lateinit var adapter: SortByListAdapter
    private var mListener: OnSortByListenerClicked? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = DataBindingUtil.inflate<DialogSuppliersSortByBinding>(inflater, R.layout.dialog_suppliers_sort_by, container, false)
        binding.color = Configurations.colors
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listeners()
        setAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    private fun setAdapter() {
        adapter = SortByListAdapter()
        adapter.settingCallback(this)
        recyclerView?.adapter = adapter
        val list = ArrayList<SortByData>()
        list.add(SortByData(filterName = getString(R.string.sort_by_distance), icon = R.drawable.ic_location, type = SortBy.SortByDistance.sortBy))
        list.add(SortByData(filterName = getString(R.string.sort_by_atz),icon = R.drawable.ic_pin, type = SortBy.SortByATZ.sortBy))
        list.add(SortByData(filterName = getString(R.string.sort_by_rating), icon = R.drawable.ic_star, type = SortBy.SortByRating.sortBy))
        adapter.addList(list)
    }

    private fun listeners() {
        ivCross?.setOnClickListener {
            dialog?.dismiss()
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        mListener = if (parent != null) {
            parent as OnSortByListenerClicked
        } else {
            context as OnSortByListenerClicked
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }


    companion object {
        fun newInstance() = SortByFilterDialog()

    }


    override fun onItemSelected(type: Int) {
        dialog?.dismiss()
        mListener?.onItemSelected(type)
    }

    override fun onPrepTimeSelected(filters: FiltersSupplierList) {

    }



}

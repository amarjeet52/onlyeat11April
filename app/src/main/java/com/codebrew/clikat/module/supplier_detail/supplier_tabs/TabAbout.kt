package com.codebrew.clikat.module.supplier_detail.supplier_tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.codebrew.clikat.R
import com.codebrew.clikat.data.model.api.supplier_detail.DataSupplierDetail
import com.codebrew.clikat.modal.AppGlobal


private const val ARG_PARAM1 = "supplier_data"

class TabAbout : Fragment() {


    private var param1: DataSupplierDetail? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getParcelable(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_tab_desc_or_about,
                container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvAbout = view.findViewById<WebView>(R.id.tvDesc)
        val noData = view.findViewById<TextView>(R.id.noData)
        noData.typeface = AppGlobal.semi_bold
        //        ((MainActivity) getActi/*    DataSupplierDetail data = Prefs.with(getActivity()).getObject(DataNames.SUPPLIER_DETAIL, DataSupplierDetail.class);
        //


        tvAbout.loadDataWithBaseURL(null, param1?.about.toString(), "text/html", "utf-8", null)
        if (param1?.about == null || param1?.about!!.isEmpty()) {
            noData.visibility = View.VISIBLE;tvAbout.visibility = View.GONE
        } else {
            noData.visibility = View.GONE
            tvAbout.visibility = View.VISIBLE
        }

    }


    companion object {

        @JvmStatic
        fun newInstance(supplierDetail: DataSupplierDetail?) =
                TabAbout().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_PARAM1, supplierDetail)
                    }
                }
    }

}

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

class TabDescription : Fragment() {


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
        // ButterKnife.bind(getActivity(),view);
        val tvDesc = view.findViewById<WebView>(R.id.tvDesc)
        val noData = view.findViewById<TextView>(R.id.noData)
        noData.typeface = AppGlobal.semi_bold

        val dummy = param1?.description + param1?.termsAndConditions
        tvDesc.loadDataWithBaseURL(null, dummy, "text/html", "utf-8", null)

        //        ((MainActivity)getActivity()).setSupplierImage(false,"",0,"",0,0);
        if (dummy == null || dummy.isEmpty()) {
            noData.visibility = View.VISIBLE
            tvDesc.visibility = View.GONE
        } else {
            noData.visibility = View.GONE
            tvDesc.visibility = View.VISIBLE
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(supplierDetail: DataSupplierDetail?) =
                TabDescription().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_PARAM1, supplierDetail)
                    }
                }
    }


}

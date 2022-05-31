package com.codebrew.clikat.module.filter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.databinding.FragmentFilterSubcategoryBinding
import com.codebrew.clikat.modal.other.FilterEvent
import com.codebrew.clikat.modal.other.SubCategory
import com.codebrew.clikat.module.filter.BottomSheetFragment.Companion.varientData
import com.codebrew.clikat.module.filter.sub_category_selection.SubCategoryAdapter
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.fragment_filter_subcategory.*
import org.greenrobot.eventbus.EventBus

class SubCategoryFragment : Fragment() {

    private val dataBeans = arrayListOf<SubCategory>()
    private var name: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null && arguments!!.containsKey(ARG_PARAM1)) {
            dataBeans.addAll(arguments?.getParcelableArrayList(ARG_PARAM1) ?: mutableListOf())
        }
        if (arguments != null && arguments!!.containsKey(ARG_PARAM2)) {
            name = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? { //fragment_filter_subcategory
        val binding: FragmentFilterSubcategoryBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_filter_subcategory, container, false)
        binding.color = Configurations.colors
        binding.drawables = Configurations.drawables
        binding.strings = Configurations.strings
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_title!!.text = name
        val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        rv_subcategoryList!!.layoutManager = layoutManager
        val all_cat = true
        for (dataBean in dataBeans) {
            for (subCatId in varientData.subCatId) {
                if (dataBean.id == subCatId) {
                    dataBean.status = true
                    dataBeans[dataBeans.indexOf(dataBean)] = dataBean
                }
            }
            /* if (dataBean.getIs_cub_category() == 1) {
                all_cat = false;
            }*/
        }
        rv_subcategoryList!!.adapter = SubCategoryAdapter(dataBeans)

        iv_back.setOnClickListener {
            EventBus.getDefault().post(FilterEvent(AppConstants.SUBCATEGORY_BACKPRESS, 0, "", 0))
        }
    }


    /* @OnClick({R.id.btn_done})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.btn_done) {
            // mListener.onSubmit();
        }
    }*/
    interface OnSubCategoryListener {
        fun onSubCategoryDetail(categoryId: Int, name: String?, position: Int)
        fun onSubCategoryRemove(categoryId: Int, name: String?, position: Int)
        fun onSubCategoryAdd(categoryId: Int, name: String?, position: Int)
        fun onSubmit()
        fun onbackpressed()
    }

    companion object {
        private const val ARG_PARAM1 = "subcategory"
        private const val ARG_PARAM2 = "name"
        fun newInstance(dataBeans: ArrayList<SubCategory>?, name: String?): SubCategoryFragment {
            val fragment = SubCategoryFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_PARAM1, dataBeans)
            args.putString(ARG_PARAM2, name)
            fragment.arguments = args
            return fragment
        }
    }
}
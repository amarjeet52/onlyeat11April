package com.codebrew.clikat.module.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.codebrew.clikat.R
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.databinding.FragmentFilterSubcatCategoryBinding
import com.codebrew.clikat.modal.other.FilterEvent
import com.codebrew.clikat.modal.other.SubCategory
import com.codebrew.clikat.module.filter.BottomSheetFragment.Companion.varientData
import com.codebrew.clikat.module.filter.SubCat_CategoryFragment.OnSubcatCategoryListener
import com.codebrew.clikat.module.filter.subcat_category_selection.SubCatCategoryAdapter
import com.codebrew.clikat.module.filter.subcat_category_selection.SubCatCategoryAdapter.SubCatCallback
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.fragment_filter_subcat_category.*
import org.greenrobot.eventbus.EventBus


/**
 * A fragment representing a list of Items.
 *
 *
 * Activities containing this fragment MUST implement the [OnSubcatCategoryListener]
 * interface.
 */
class SubCat_CategoryFragment : Fragment(), SubCatCallback {

    private var name: String? = null
    private var subcatIdList: MutableList<Int>? = null
    private val dataBeans = ArrayList<SubCategory>()
    private var position = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null && arguments?.containsKey(ARG_PARAM1) == true) {
            dataBeans.addAll(arguments?.getParcelableArrayList(ARG_PARAM1) ?: arrayListOf())
        }
        if (arguments != null && arguments?.containsKey(ARG_PARAM2) == true) {
            name = arguments?.getString(ARG_PARAM2)
        }
        if (arguments != null && arguments?.containsKey(ARG_PARAM3) == true) {
            position = arguments?.getInt(ARG_PARAM3) ?: 0
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? { //fragment_filter_subcat_category
        val binding: FragmentFilterSubcatCategoryBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_filter_subcat_category, container, false)
        binding.color = Configurations.colors
        val view = binding.root
        // btnDone.setVisibility(View.GONE);
        subcatIdList = ArrayList()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_title!!.text = name
        var all_cat = true
        if (varientData.subCatId != null) {
            for (dataBean in dataBeans) {
                for (subCatId in varientData.subCatId) {
                    if (dataBean.id == subCatId) {
                        dataBean.status = true
                        dataBeans[dataBeans.indexOf(dataBean)] = dataBean
                        subcatIdList!!.add(dataBean.id)
                    }
                }
                if (dataBean.sub_category != null) {
                    all_cat = false
                }
            }
        }
        // btnDone.setVisibility(subcatIdList.size() > 0 ? View.VISIBLE : View.GONE);
        rv_subcat_categoryList!!.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        val adapter = SubCatCategoryAdapter(dataBeans, position)
        adapter.settingCallback(this)
        rv_subcat_categoryList!!.adapter = adapter

        val itemDecoration: ItemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        rv_subcat_categoryList.addItemDecoration(itemDecoration)

        iv_back.setOnClickListener {
            EventBus.getDefault().post(FilterEvent(AppConstants.SUBCATEGORY_BACKPRESS, 0, "", 0))
        }
    }


    override fun onSubCat(id: Int) {
        if (!subcatIdList!!.contains(id)) subcatIdList!!.add(id) else subcatIdList!!.removeAt(subcatIdList!!.indexOf(id))
        // btnDone.setVisibility(subcatIdList.size() > 0 ? View.VISIBLE : View.GONE);
    }

    interface OnSubcatCategoryListener {
        fun onSubcatCategoryInteraction(catId: Int, name: String?, position: Int)
        fun onSubCategoryRemove(categoryId: Int, name: String?, position: Int)
        fun onSubCategoryAdd(categoryId: Int, name: String?, position: Int)
        fun onSubmit()
        fun onbackpressed()
    }

    companion object {
        /*    @BindView(R.id.btn_done)
    ClikatTextView btnDone;*/
// private OnSubcatCategoryListener mListener;
        private const val ARG_PARAM1 = "subcat_category"
        private const val ARG_PARAM2 = "name"
        private const val ARG_PARAM3 = "frag_position"
        fun newInstance(data: ArrayList<SubCategory>?, name: String?, position: Int): SubCat_CategoryFragment {
            val fragment = SubCat_CategoryFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_PARAM1, data)
            args.putString(ARG_PARAM2, name)
            args.putInt(ARG_PARAM3, position)
            fragment.arguments = args
            return fragment
        }
    }
}
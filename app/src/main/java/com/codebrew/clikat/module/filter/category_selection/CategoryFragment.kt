package com.codebrew.clikat.module.filter.category_selection

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
import com.codebrew.clikat.databinding.FragmentFilterCategoryBinding
import com.codebrew.clikat.modal.other.English
import com.codebrew.clikat.module.filter.CategoryAdapter
import com.codebrew.clikat.module.filter.category_selection.CategoryFragment.OnListFragmentInteractionListener
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.fragment_filter_category.*

/**
 * A fragment representing a list of Items.
 *
 *
 * Activities containing this fragment MUST implement the [OnListFragmentInteractionListener]
 * interface.
 */
class CategoryFragment : Fragment() {

    private val categoryList = ArrayList<English>()
    private var mListener: OnListFragmentInteractionListener? = null
    private var adapter: CategoryAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null && arguments!!.containsKey(ARG_PARAM1)) {
            categoryList.addAll(arguments?.getParcelableArrayList(ARG_PARAM1) ?: arrayListOf())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentFilterCategoryBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_filter_category, container, false)
        binding.color = Configurations.colors
        binding.strings = Configurations.strings

        // ((FilterScreenActivity)getActivity()).setCateRefreshListener(() -> adapter.notifyDataSetChanged());
// tvTitle.setText("Select Category");
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //   btnDone.setVisibility(View.GONE);
        val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        rv_categoryList!!.layoutManager = layoutManager
        adapter = CategoryAdapter(mListener, categoryList)
        rv_categoryList!!.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        if (adapter == null) return

        if (categoryList.isNotEmpty()) {
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            mListener = parentFragment as OnListFragmentInteractionListener?
        } else { // throw new RuntimeException(context.toString()
//       + " must implement OnListFragmentInteractionListener");
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /*    @OnClick(R.id.btn_done)
    public void onViewClicked() {
        mListener.onSubmit();
    }*/
    interface OnListFragmentInteractionListener {
        fun onCategoryInteraction(catId: Int, catName: String?, position: Int)
        fun onSubmit()
    }

    companion object {
        private const val ARG_PARAM1 = "category"
        fun newInstance(categoryList: ArrayList<English>): CategoryFragment {
            val fragment = CategoryFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_PARAM1, categoryList)
            fragment.arguments = args
            return fragment
        }
    }
}
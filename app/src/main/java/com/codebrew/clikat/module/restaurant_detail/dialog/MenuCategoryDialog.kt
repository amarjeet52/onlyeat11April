package com.codebrew.clikat.module.restaurant_detail.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.codebrew.clikat.R
import com.codebrew.clikat.base.BaseDialog
import com.codebrew.clikat.databinding.DialogMenuCategoriesBinding
import com.codebrew.clikat.modal.other.ProductBean
import com.codebrew.clikat.module.restaurant_detail.OnMenuCategoryListener
import com.codebrew.clikat.module.restaurant_detail.adapter.MenuCategoryAdapter
import com.codebrew.clikat.module.restaurant_detail.adapter.NewMenuCategoryListAdapter
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.dialog_menu_categories.*

class MenuCategoryDialog : BaseDialog(), MenuCategoryAdapter.MenuCategoryCallback {


    private lateinit var adapter: NewMenuCategoryListAdapter
    private var mListener: OnMenuCategoryListener? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = DataBindingUtil.inflate<DialogMenuCategoriesBinding>(inflater, R.layout.dialog_menu_categories, container, false)
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
        adapter = NewMenuCategoryListAdapter()
        adapter.settingCallback(this)
        rvCategories?.adapter=adapter
        val list = if (arguments?.containsKey("categoryList") == true)
            arguments?.getParcelableArrayList<ProductBean>("categoryList") else arrayListOf()
        adapter.addList(list ?: arrayListOf())
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
            parent as OnMenuCategoryListener
        } else {
            context as OnMenuCategoryListener
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }


    companion object {
        fun newInstance(productBeans: ArrayList<ProductBean>) =
                MenuCategoryDialog().apply {
                    arguments = Bundle().apply {
                        putParcelableArrayList("categoryList", productBeans)
                    }
                }
    }

    override fun getMenuCategory(position: Int) {
        dialog?.dismiss()
        mListener?.onMenuSelected(position)
    }


}

package com.codebrew.clikat.module.supplier_detail.adapter

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.codebrew.clikat.data.model.api.supplier_detail.DataSupplierDetail
import com.codebrew.clikat.module.supplier_detail.supplier_tabs.TabAbout
import com.codebrew.clikat.module.supplier_detail.supplier_tabs.TabDescription
import com.codebrew.clikat.module.supplier_detail.supplier_tabs.TabReviews


@SuppressLint("WrongConstant")
class SupplierDetailAdapter(fm: FragmentManager, supplierDetail: DataSupplierDetail?, private val tabTitles: Array<String?>) :
        FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {


    private val mFragments = listOf(/*TabDescription.newInstance(supplierDetail),
            TabAbout.newInstance(supplierDetail),*/
            TabReviews.newInstance(supplierDetail))


    override fun getCount(): Int {
        return mFragments.size
    }

    override fun getItem(position: Int): Fragment {

        return mFragments[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position]
    }
}


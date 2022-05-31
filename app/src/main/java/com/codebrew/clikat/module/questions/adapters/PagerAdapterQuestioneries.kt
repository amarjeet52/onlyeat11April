package com.codebrew.clikat.module.questions.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapterQuestioneries(fragment: Fragment?,
                                val mContext: Context?,
                                var array: List<Fragment>) : FragmentStateAdapter(fragment!!) {

    override fun getItemCount(): Int {
        return array.size

    }

    override fun createFragment(position: Int): Fragment {
        return array[position]

    }
}
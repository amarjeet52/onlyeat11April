package com.codebrew.clikat.module.home_screen.resturant_home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter


class PagerAdapter(fm: Fragment, private var mFragments: List<Fragment>) :
        FragmentStateAdapter(fm) {

    override fun getItemCount(): Int {
        return mFragments.size
    }

    override fun createFragment(position: Int): Fragment = mFragments[position]



    fun getItem(position: Int): Fragment? {
        return mFragments[position]
    }
}
package com.codebrew.clikat.module.order_detail_new.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.codebrew.clikat.module.order_detail_new.OrderPagerFragment

class OrderPagerAdapter(fa: FragmentActivity, private val mOrderFrag: MutableList<OrderPagerFragment>) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = mOrderFrag.count()

    override fun createFragment(position: Int): Fragment = mOrderFrag[position]
}
package com.codebrew.clikat.module.manage_referral

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.databinding.FragmentManageReferralBinding
import com.codebrew.clikat.module.manage_referral.adapter.ReferralPageAdapter
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_manage_referral.*
import kotlinx.android.synthetic.main.toolbar_app.*


class ManageReferralFrag : Fragment(), TabLayout.OnTabSelectedListener {

    private var mAdapter: ReferralPageAdapter? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding: FragmentManageReferralBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage_referral, container, false)
        binding.color = Configurations.colors
        binding.strings = Configurations.strings
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        tb_title.text = getString(R.string.referral)

        tb_back.setOnClickListener {
            navController(this@ManageReferralFrag).popBackStack()
        }

        mAdapter = ReferralPageAdapter(childFragmentManager)


        referral_container.setPagingEnabled(false)

        referral_container.offscreenPageLimit = 0

        referral_container.adapter = mAdapter

        tabLayout_referral.addOnTabSelectedListener(this)

        tabLayout_referral.addTab(tabLayout_referral.newTab().setText(getString(R.string.refer_a_friend)))
        tabLayout_referral.addTab(tabLayout_referral.newTab().setText(getString(R.string.my_referal)))
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabSelected(tab: TabLayout.Tab?) {

        tab?.position?.let { referral_container.currentItem = it }
    }

}

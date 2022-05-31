package com.codebrew.clikat.module.wishlist_prod

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentWishListMainBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.home_screen.resturant_home.PagerAdapter
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_wish_list_main.*
import kotlinx.android.synthetic.main.toolbar_app.*
import javax.inject.Inject


class WishListMainFragment : BaseFragment<FragmentWishListMainBinding, WishListViewModel>() {

    private lateinit var viewModel: WishListViewModel

    @Inject
    lateinit var prefHelper: PreferenceHelper

    @Inject
    lateinit var factory: ViewModelProviderFactory

    private var mBinding: FragmentWishListMainBinding? = null

    private var adapter: PagerAdapter? = null
    private var terminologyBean: SettingModel.DataBean.Terminology? = null
    var settingBean: SettingModel.DataBean.SettingData? = null
    private val textConfig = Configurations.strings
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_wish_list_main
    }

    override fun getViewModel(): WishListViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(WishListViewModel::class.java)
        return viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding
        mBinding?.color = Configurations.colors
        mBinding?.strings = Configurations.strings

        settingLayout()
        setViewPager()
    }

    private fun settingLayout() {
        terminologyBean = prefHelper.getGsonValue(PrefenceConstants.APP_TERMINOLOGY, SettingModel.DataBean.Terminology::class.java)
        settingBean = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        settingToolbar()
    }

    private fun settingToolbar() {
        tb_title.text = mBinding?.strings?.wishlist

        tb_back.setOnClickListener {
            mBinding?.root?.let { it1 -> Navigation.findNavController(it1).popBackStack() }
        }
        ivBack?.setOnClickListener {
            mBinding?.root?.let { it1 -> Navigation.findNavController(it1).popBackStack() }
        }
        if(settingBean?.is_skip_theme=="1"){
            groupSkip?.visibility=View.VISIBLE
            toolbar?.visibility=View.GONE
        }else{
            groupSkip?.visibility=View.GONE
            toolbar?.visibility=View.VISIBLE
        }
    }

    private fun setViewPager() {
        val fragments = ArrayList<Fragment>()
        val title = ArrayList<String>()

        if (settingBean?.is_supplier_wishlist == "1" || settingBean?.is_skip_theme=="1") {
            fragments.add(WishlistSuppliersFrag.newInstance())
            title.add(textConfig.suppliers ?: "")
        }

        if (settingBean?.is_product_wishlist == "1") {
            fragments.add(WishListFrag.newInstance())
            title.add(textConfig.products ?: "")
        }

        adapter = PagerAdapter(this@WishListMainFragment, fragments)
        viewPager?.adapter = adapter

        if (title.size > 1) {
            tabLayout.visibility = View.VISIBLE
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = title[position]
                viewPager.setCurrentItem(tab.position, true)
            }.attach()
        } else
            tabLayout.visibility = View.GONE
    }

}

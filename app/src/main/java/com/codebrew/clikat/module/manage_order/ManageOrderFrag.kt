package com.codebrew.clikat.module.manage_order


import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentManageOrderBinding
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.bottom_navigation.OnNavigationMenuClicked
import com.codebrew.clikat.module.manage_order.adapter.PagerAdapter
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.material.tabs.TabLayout
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_manage_order.*
import kotlinx.android.synthetic.main.toolbar_app.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class ManageOrderFrag : Fragment(), TabLayout.OnTabSelectedListener {


    @Inject
    lateinit var prefHelper: PreferenceHelper
    var settingBean: SettingModel.DataBean.SettingData? = null

    private var mAdapter: PagerAdapter? = null

    @Inject
    lateinit var appUtils: AppUtils

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }

    private val colorConfig by lazy { Configurations.colors}
    private var navigationListeners: OnNavigationMenuClicked? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)

        settingBean = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding: FragmentManageOrderBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage_order, container, false)

        if (settingBean?.show_ecom_v2_theme == "1") {
            binding.color?.toolbarTabIndicatorColor = "#00000000"
        }

        binding.color = colorConfig
        binding.strings = textConfig
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mAdapter = PagerAdapter(childFragmentManager)


        order_container.setPagingEnabled(false)

        order_container.offscreenPageLimit = 0

        order_container.adapter = mAdapter

        tabLayout_order.addOnTabSelectedListener(this)

        tabLayout_order.addTab(tabLayout_order.newTab().setText(textConfig?.pending_orders))
        tabLayout_order.addTab(tabLayout_order.newTab().setText(textConfig?.completed_orders))
        when {
            settingBean?.show_ecom_v2_theme == "1" -> {
                tabLayout_order.background = ContextCompat.getDrawable(requireActivity(), R.drawable.background_toolbar_bottom_radius)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    tabLayout_order.background.colorFilter = BlendModeColorFilter(Color.parseColor(colorConfig.toolbarColor), BlendMode.SRC_ATOP)
                }
                tabLayout_order.setSelectedTabIndicatorHeight(0)
                toolbar?.visibility = View.VISIBLE
                toolbar?.elevation = 0f
                tabLayout_order?.setSelectedTabIndicator(ContextCompat.getDrawable(requireActivity(), R.drawable.tab_color_selector_v2))
            }
            settingBean?.is_juman_flow_enable == "1" -> {
                toolbar?.visibility = View.GONE
                ivNavigation?.visibility=View.VISIBLE
                groupSkip?.visibility=View.VISIBLE
                ivBack?.visibility=View.INVISIBLE

                ivNavigation?.setOnClickListener {
                    navigationListeners?.onNavigationMenuChanged()
                }
            }
            settingBean?.is_skip_theme == "1" -> {
                clMain?.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.greyED))
                toolbar?.visibility = View.GONE
                groupSkip?.visibility=View.VISIBLE
            }
            else -> {
                tabLayout_order.setBackgroundColor(Color.parseColor(colorConfig.toolbarColor))
                tabLayout_order?.setSelectedTabIndicator(null)
                tabLayout_order?.setSelectedTabIndicatorColor(Color.parseColor(colorConfig.toolbarText))
            }
        }


        tb_back?.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }
        ivBack?.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }
        tb_title?.text = getString(R.string.orders)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNavigationMenuClicked)
            navigationListeners = context
    }
    private fun loadFragment(it: Int) {
        order_container.currentItem = it
    }

    override fun onTabReselected(p0: TabLayout.Tab?) {

    }

    override fun onTabUnselected(p0: TabLayout.Tab?) {

    }

    override fun onTabSelected(p0: TabLayout.Tab?) {

        p0?.position?.let { loadFragment(it) }

    }

}

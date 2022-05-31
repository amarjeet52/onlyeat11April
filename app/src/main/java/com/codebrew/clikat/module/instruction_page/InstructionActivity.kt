package com.codebrew.clikat.module.instruction_page

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager2.widget.ViewPager2
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.OrderStatus
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.ActivityInstructionScreenBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.other.SettingModel.DataBean.ScreenFlowBean

import com.codebrew.clikat.module.instruction_page.adapter.NewInstructionAdapter
import com.codebrew.clikat.module.location.LocationActivity
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.StaticFunction.setStatusBarColor
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_instruction_screen.*
import kotlinx.android.synthetic.main.activity_order_details.*
import javax.inject.Inject

class InstructionActivity : BaseActivity<ActivityInstructionScreenBinding, InstructionViewModel>(), BaseInterface, NewInstructionAdapter.InstructionCallback {

    @Inject
    lateinit var prefHelper: PreferenceHelper

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var dataManager: DataManager
var first=0
    @Inject
    lateinit var appUtils: AppUtils
    private var clientInform: SettingModel.DataBean.SettingData? = null
    private var tutorialScreens = ArrayList<InstructionModel>()//SettingModel.DataBean.TutorialItem>()
    private var mViewModel: InstructionViewModel? = null

    private val appBackground = Color.parseColor(Configurations.colors.appBackground)
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instruction_screen)
        setStatusBarColor(this@InstructionActivity, getColor(R.color.yellow_new))
        window.setNavigationBarColor(getColor(R.color.yellow_new))
        clientInform = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        viewModel.navigator = this
        settingLayout()

    }


    private fun scheduleScreen() {
        Handler().postDelayed(
                {
                    // After the screen duration, route to the right activities
                    onActivityFinish()
                },
                3000
        )
    }

    private fun settingLayout() {
        if (!clientInform?.tutorial_screens.isNullOrEmpty()) {
            val tutorialScreens = Gson().fromJson(clientInform?.tutorial_screens, Array<SettingModel.DataBean.TutorialItem>::class.java)
            //this.tutorialScreens.addAll(tutorialScreens)

            var ob1:InstructionModel=  InstructionModel(R.drawable.variety_vector,"From Arabic to Asian to Italian you name it. Enjoy the huge variety of cuisines we have.","Wide Food Selection")
            this.tutorialScreens.add(ob1)
             ob1=InstructionModel(R.drawable.monthly_giveaway_vector,"Win surprising gifts every month! Just by placing an order, you’re automatically part of the monthly giveaway. Multiple entries available means more chance of winning. What are you waiting for?","Monthly Giveaway")
            this.tutorialScreens.add(ob1)
             ob1=InstructionModel(R.drawable.monthly_giveaway_vector,"Yehey! Free Meals! You heard it right. With Crave reward club, gain points and redeem them at your favorite restaurants","Reward Club")
            this.tutorialScreens.add(ob1)
             ob1=InstructionModel(R.drawable.offers_vector,"Who doesn't love discounts? Enjoy our exclusive and unbelievable offers specially made for you.","Exclusive Offers")
            this.tutorialScreens.add(ob1)
        }

        val adapter = NewInstructionAdapter(
                appUtils, tutorialScreens)
        adapter.settingCallback(this)

        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager?.adapter = adapter
        indicator?.setViewPager(viewPager)

        tvContinue?.setOnClickListener {

            if (tutorialScreens.count() - 1 == viewPager.currentItem) {
                if(first==0)
                {
                    scheduleScreen()
                    first=1
                }

            } else {
                viewPager?.currentItem = (viewPager?.currentItem ?: 0) + 1
            }
        }


        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)

                if (position % 2 == 0) {
                    setStatusBarColor(this@InstructionActivity, getColor(R.color.yellow_new1))
                    window.setNavigationBarColor(getColor(R.color.yellow_new2))
                      lytMain.setBackgroundResource(R.drawable.tutorial_bg)
                    tvContinue.backgroundTintList=null
                    tvContinue.background= ContextCompat.getDrawable(this@InstructionActivity, R.drawable.gradient_blue_button)
                } else {
                    setStatusBarColor(this@InstructionActivity, getColor(R.color.blue_new1))
                    window.setNavigationBarColor(getColor(R.color.blue_new2))

                    lytMain.setBackgroundResource(R.drawable.tutorial_two)
                    tvContinue.backgroundTintList=null
                    tvContinue.background= ContextCompat.getDrawable(this@InstructionActivity, R.drawable.button_bg)
                }

            }
        })
    }

    override fun onNextButton(position: Int) {
        onActivityFinish()
    }

    private fun onActivityFinish() {
        prefHelper.setkeyValue(DataNames.FIRST_TIME, true)
        if (prefHelper.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java) != null) {
            onAdrressValidate()
        } else {
            launchActivity<LocationActivity>()
        }
        finish()
    }

    private fun onAdrressValidate() {
        val settingBean = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        val isGuest = settingBean?.login_template.isNullOrEmpty() || settingBean?.login_template == "0"
        if (isGuest) {
            appUtils.checkHomeActivity(this, intent.extras ?: Bundle.EMPTY)
        } else {
            appUtils.checkLoginFlow(this@InstructionActivity, -1)
        }
    }

    override fun getBindingVariable(): Int = BR.viewModel


    override fun getLayoutId(): Int = R.layout.activity_instruction_screen


    override fun getViewModel(): InstructionViewModel {
        mViewModel = ViewModelProviders.of(this, factory).get(InstructionViewModel::class.java)
        return mViewModel as InstructionViewModel
    }

    override fun onErrorOccur(message: String) = viewDataBinding.root.onSnackbar(message)


    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }
}
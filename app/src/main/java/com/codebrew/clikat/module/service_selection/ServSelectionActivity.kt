package com.codebrew.clikat.module.service_selection

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.databinding.ActivityServSelectionBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.preferences.DataNames
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class ServSelectionActivity : BaseActivity<ActivityServSelectionBinding, SerSelectionViewModel>(), BaseInterface, HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var dataManager: DataManager

    private var mViewModel: SerSelectionViewModel? = null

    var settingData: SettingModel.DataBean.SettingData? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_serv_selection
    }

    override fun getViewModel(): SerSelectionViewModel {
        mViewModel = ViewModelProviders.of(this, factory).get(SerSelectionViewModel::class.java)
        return mViewModel as SerSelectionViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingData = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        val navController = findNavController(R.id.nav_container)

        if (intent.extras != null) {
            if (settingData?.hideAgentList!=null && settingData?.hideAgentList=="1") {
                navController.graph.startDestination = R.id.timeSlotFragment
                navController.setGraph(navController.graph, intent.extras)
            } else
                navController.setGraph(navController.graph, intent.extras)
        }
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    override fun onErrorOccur(message: String) {

    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }


}

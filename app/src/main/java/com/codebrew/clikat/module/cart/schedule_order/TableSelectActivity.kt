package com.codebrew.clikat.module.cart.schedule_order

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.databinding.ActivityMainScreenBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.slots.SupplierTimingsItem
import com.codebrew.clikat.module.cart.tables.TableSelectionFragment
import com.codebrew.clikat.module.cart.tables.TablesViewModel
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class TableSelectActivity : BaseActivity<ActivityMainScreenBinding, TablesViewModel>(), BaseInterface,
        HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var factory: ViewModelProviderFactory

    private lateinit var viewModel: TablesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table_select)
        val hashMap = intent.getSerializableExtra("map") as HashMap<String, String>?
        val supplierTimingsItem = intent.getSerializableExtra("map2") as  HashMap<String, String>?
        val manager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        val bundle = Bundle()
        bundle.putSerializable("map", hashMap)
        bundle.putSerializable("map2", supplierTimingsItem)
        val tableSelectionFragment = TableSelectionFragment()
        tableSelectionFragment.arguments = bundle

        transaction.add(R.id.container, tableSelectionFragment, "")
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun getBindingVariable(): Int {
      return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_table_select
    }

    override fun getViewModel(): TablesViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(TablesViewModel::class.java)
        return viewModel
    }

    override fun onErrorOccur(message: String) {
    }

    override fun onSessionExpire() {

    }

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    override fun onBackPressed() {
        super.onBackPressed()
       finish()
    }
}
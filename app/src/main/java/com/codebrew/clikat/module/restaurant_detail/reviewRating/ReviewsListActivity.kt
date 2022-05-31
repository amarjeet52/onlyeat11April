package com.codebrew.clikat.module.restaurant_detail.reviewRating

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.databinding.ActivityReviewsBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.RatingBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.other.SupplierDetailBean
import com.codebrew.clikat.module.product_detail.adapter.ReviewsListAdapter
import com.codebrew.clikat.module.restaurant_detail.RestDetailNavigator
import com.codebrew.clikat.module.restaurant_detail.RestDetailViewModel
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.activity_reviews.*
import kotlinx.android.synthetic.main.toolbar_app.*
import javax.inject.Inject

class ReviewsListActivity : BaseActivity<ActivityReviewsBinding, RestDetailViewModel>(),
        RestDetailNavigator {

    private lateinit var adapter: ReviewsListAdapter
    private lateinit var viewModel: RestDetailViewModel
    private lateinit var mBinding: ActivityReviewsBinding
    private var ratingBeans: MutableList<RatingBean>? = mutableListOf()

    @Inject
    lateinit var factory: ViewModelProviderFactory


    @Inject
    lateinit var dataManager: DataManager

    private var settingData: SettingModel.DataBean.SettingData? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_reviews
    }

    override fun getViewModel(): RestDetailViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(RestDetailViewModel::class.java)
        return viewModel
    }

    override fun favResponse() {
        //do nothing
    }

    override fun unFavResponse() {
        //do nothing
    }

    override fun onTableSuccessfullyBooked() {
        //do nothing
    }

    override fun onErrorOccur(message: String) {
        mBinding.root.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this

        settingData = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)

        setAdapter()
        initialise()
    }




    private fun initialise() {
        mBinding = viewDataBinding
        mBinding.color = Configurations.colors
        mBinding.drawables = Configurations.drawables
        mBinding.strings = Configurations.strings

        tb_title.text = getString(R.string.rating_review)
        tb_back.setOnClickListener { v: View? -> onBackPressed() }
        ratingBeans?.clear()

        if (intent != null && intent?.hasExtra("supplierDetail") == true) {
            val supplierDetail = intent?.getParcelableExtra<SupplierDetailBean>("supplierDetail")
            ratingBeans?.clear()
            ratingBeans?.addAll(supplierDetail?.ratingAndReviews ?: mutableListOf())
            adapter.notifyDataSetChanged()
        }
    }

    private fun setAdapter() {
        adapter = ReviewsListAdapter(ratingBeans)
        rvNotification?.adapter = adapter
    }


}
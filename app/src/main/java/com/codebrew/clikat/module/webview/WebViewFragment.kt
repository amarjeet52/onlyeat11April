package com.codebrew.clikat.module.webview


import android.content.Context
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.CblCustomerDomain
import com.codebrew.clikat.data.model.api.Data
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentWebViewBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.bottom_navigation.OnNavigationMenuClicked
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.ClikatConstants
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_web_view.*
import kotlinx.android.synthetic.main.fragment_web_view.tvTitle
import kotlinx.android.synthetic.main.fragment_web_view.webView
import kotlinx.android.synthetic.main.web_view.*
import java.lang.reflect.Type
import javax.inject.Inject


/*
 * Created by cbl80 on 5/7/16.
 */
class WebViewFragment : BaseFragment<FragmentWebViewBinding, WebViewModel>(), BaseInterface {

    private var mViewModel: WebViewModel? = null

    @Inject
    lateinit var factory: ViewModelProviderFactory


    @Inject
    lateinit var prefHelper: PreferenceHelper

    @Inject
    lateinit var appUtils: AppUtils

    var mTermsParam = 0

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }
    private val colorConfig by lazy { Configurations.colors }
    private var navigationListeners: OnNavigationMenuClicked? = null
    private var clientData: CblCustomerDomain? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigator = this

        viewDataBinding?.color = colorConfig
        viewDataBinding?.drawables = Configurations.drawables
        viewDataBinding?.strings = textConfig
        termObserver()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNavigationMenuClicked)
            navigationListeners = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivNavigation?.visibility = View.VISIBLE
        webView.settings.javaScriptEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webView.settings.builtInZoomControls = true

        // dataManager.addGsonValue(PrefenceConstants.DB_INFORMATION, Gson().toJson(it.data.data[0].cbl_customer_domains[0]))

        clientData = prefHelper.getGsonValue(PrefenceConstants.DB_INFORMATION, CblCustomerDomain::class.java)

        mTermsParam = arguments?.getInt("terms", 0) ?: 0

        tvTitle.text = when (mTermsParam) {
            0 -> {
                getString(R.string.terms)
            }
            1 -> {
                getString(R.string.about_us)
            }
            4 -> {
                getString(R.string.faq)
            }
            5 -> {
                getString(R.string.customer_support)
            }
            else -> {
                getString(R.string.privacy_policy)
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                //showLoading()
                view.loadUrl(url)
                return true // then it is not handled by default action
            }

        }

        if (tvTitle.text.toString() == getString(R.string.customer_support)) {

            prefHelper.getKeyValue(DataNames.FEATURE_DATA, PrefenceConstants.TYPE_STRING).toString().let {
                val listType: Type = object : TypeToken<ArrayList<SettingModel.DataBean.FeatureData?>?>() {}.type
                val featureList: ArrayList<SettingModel.DataBean.FeatureData> = Gson().fromJson(it, listType)

                webView.loadUrl("https://tawk.to/chat/${featureList.find { it1 -> it1.name == "Tawk" }?.key_value_front?.firstOrNull()?.value ?: ""}/default")
            }
        } else {
            if (isNetworkConnected) {
                viewModel.getTermsCondition()
            }
        }

        ivNavigation?.setOnClickListener {
            navigationListeners?.onNavigationMenuChanged()
        }

    }

    private fun termObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<List<Data?>?> { resource ->

            updateTermData(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.webListData.observe(this, catObserver)
    }

    private fun updateTermData(resource: List<Data?>?) {

        val data = if (prefHelper.getLangCode().toInt() == ClikatConstants.LANGUAGE_ENGLISH)
            resource?.first() else resource?.get(1)

        val unencodedHtml = when (mTermsParam) {
            0 -> data?.terms_and_conditions ?: ""
            1 -> data?.about_us ?: ""
            4 -> data?.faqs ?: ""
            else -> data?.faq ?: ""
        }
        val encodedHtml = Base64.encodeToString(unencodedHtml.toByteArray(), Base64.NO_PADDING)
        webView.loadData(encodedHtml, "text/html", "base64")

    }


    private fun destroyWebView() {
        // Make sure you remove the WebView from its parent view before doing anything.
        // mWebContainer.removeAllViews()
        webView.clearHistory()
        // NOTE: clears RAM cache, if you pass true, it will also clear the disk cache.
        // Probably not a great idea to pass true if you have other WebViews still alive.
        webView.clearCache(true)
        // Loading a blank page is optional, but will ensure that the WebView isn't doing anything when you destroy it.
        webView.loadUrl("about:blank")
        webView.onPause()
        webView.removeAllViews()
        //  webView.destroyDrawingCache()
        // NOTE: This pauses JavaScript execution for ALL WebViews,
        // do not use if you have other WebViews still alive.
        // If you create another WebView after calling this,
        // make sure to call mWebView.resumeTimers().
        webView.pauseTimers()
        // NOTE: This can occasionally cause a segfault below API 17 (4.2)
        webView.destroy()
        // Null out the reference so that you don't end up re-using it.
        // mWebView = null
    }


    override fun onDestroyView() {
        super.onDestroyView()
        destroyWebView()
    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_web_view
    }

    override fun getViewModel(): WebViewModel {
        mViewModel = ViewModelProviders.of(this, factory).get(WebViewModel::class.java)
        return mViewModel as WebViewModel
    }

    override fun onErrorOccur(message: String) {
        viewDataBinding.root.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }
}